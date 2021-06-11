package oop.ex6.main;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Class for verifying s-java code. should get one argument - path to a s-java code file.
 * Will print:
 * 0 - if the code is legal.
 * 1 - if the code is illegal.
 * 2 - in case of IO error.
 */
public class Sjavac {

    // Constants
    private static final String FILE_NOT_FOUND_MESSAGE = "Warning: Running on a non s-java file";
    private static final String CANNOT_READ_FILE = "Warning: Running on a non s-java file";
    private static final String ARGS_NOT_VALID_MESSAGE = "Warning: Running on a non s-java file";
    private static String FILE_PATH;

    // Data members
    /* assignmentsToCheck contains assignments involving global variable to check.
     * first place will have the left assignment variable. second place will have right side variable.
     * possible global variable will have only name field.
     * other variables constants will have initialized type field.
     */
    static LinkedList<Variable[]> assignmentsToCheck;
    // globalVariableConditionUsageToCheck will save all the names of global variables calls to check.
    static LinkedList<String> globalVariableConditionUsageToCheck;
    // methodCalls will save all the suspects to be method calls.
    // suspects to be global variables calls will be saved only with name.
    static LinkedList<Method> methodCalls;
    // methods will save real methods names after verifying legal definition of them.
    static LinkedList<Method> methods;
    // static boolean variable, turns true if illegal code found.
    private static Boolean illegalCodeError;
    private static Scope globalScope;


    // Constructor
    Sjavac() {
        assignmentsToCheck = new LinkedList<>();
        globalVariableConditionUsageToCheck = new LinkedList<>();
        methodCalls = new LinkedList<>();
        methods = new LinkedList<>();
        globalScope = new Scope();
        illegalCodeError = false;
    }

    // Methods
    public static void main(String[] args) {
        try {
            new Sjavac();
            if (args.length != 1) throw new IllegalArgumentException();
            else FILE_PATH = args[0];
            checkCode();
            if (lastChecks()) System.out.println(0);
            else System.out.println(1);
        } catch (FileNotFoundException e) {
            System.err.println(FILE_NOT_FOUND_MESSAGE);
            System.out.println(2);
        } catch (IOException e) {
            System.err.println(CANNOT_READ_FILE);
            System.out.println(2);
        } catch (IllegalArgumentException e) {
            System.err.println(ARGS_NOT_VALID_MESSAGE);
            System.out.println(2);
        } catch (IllegalCodeException e) {
            e.printMessage();
            System.out.println(1);
            illegalCodeError = true;
        }
    }

    private static Boolean lastChecks() throws IllegalCodeException {
        return assignmentsCheck() && methodCallsCheck() && globalVariableConditionUsageCheck();
    }

    private static Boolean assignmentsCheck() throws IllegalCodeException {
        for (Variable[] assignment : assignmentsToCheck) {
            if (assignment[0].getType() == null) {
                Variable globalScopeVariable = globalScope.getVariable(assignment[0].getName());
                if (globalScopeVariable != null) globalScopeVariable.assignValue(assignment[1]);
                else throw new IllegalCodeException
                        ("Some crazy try to use global variable which never declared");
            } else {
                Variable globalScopeVariable = globalScope.getVariable(assignment[1].getName());
                if (globalScopeVariable == null) {
                    throw new IllegalCodeException
                            ("Some crazy try to use global variable which never declared");
                } else {
                    assignment[0].assignValue(globalScopeVariable);
                }
            }
        }
        return true;
    }

    private static Boolean globalVariableConditionUsageCheck() {
        for (String name : globalVariableConditionUsageToCheck) {
            Variable globalVariable = globalScope.getVariable(name);
            if (globalVariable == null) return false;
        }
        return true;
    }

    private static Boolean methodCallsCheck() throws IllegalCodeException {
        Boolean check = false;
        for (Method methodCall : methodCalls) {
            for (Method method : methods) {
                if (method.getName().equals(methodCall.getName())) {
                    if (!method.isValidCall(globalScope, methodCall)) {
                        throw new IllegalCodeException("Illegal method call (type or number of arguments)");
                    } else {
                        check = true;
                        break;
                    }
                }
            }
            if (!check) {
                throw new IllegalCodeException("Illegal method call (type or number of arguments)");
            }
        }
        return true;
    }

    private static void checkCode() throws IOException, IllegalCodeException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(FILE_PATH));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            if (shouldBeIgnored(line)) continue;
            if (Patterns.getLegalMethodDefinitionPattern().matcher(line).matches()) {
                new MethodDefinitionScope(globalScope, bufferedReader, line);
            } else if (Patterns.getLegalVariableDeclarationPattern().matcher(line).matches()) {
                globalScope.addNewVariables(line);
            } else if (Patterns.getLegalVariableAssignmentPattern().matcher(line).matches()) {
                globalScope.makeAssignmentToVariable(line);
            } else {
                throw new IllegalCodeException("Illegal line in code");
            }
            if (illegalCodeError) { // Finish reading this code.
                finishReadingCode(bufferedReader);
            }
        }
    }

    static boolean shouldBeIgnored(String line) {
        return (line.startsWith("//")) || (line.matches("\\s*"));
    }

    private static void finishReadingCode(BufferedReader bufferedReader) throws IOException {
        while (true) {
            if (bufferedReader.readLine() == null) break;
        }
    }
}