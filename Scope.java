package oop.ex6.main;


import java.io.BufferedReader;
import java.util.LinkedList;

class Scope {

    // Constants
    String endOfScopeLine = "\\s*}\\s*";

    // Data members
    BufferedReader bufferedReader;
    String currentLine;
    private Scope parentScope;
    LinkedList<Variable> variables;

    // Constructor
    Scope(Scope parentScope, BufferedReader bufferedReader, String currentLine) {
        this.parentScope = parentScope;
        this.bufferedReader = bufferedReader;
        this.currentLine = currentLine;
        this.variables = new LinkedList<>();
    }

    // Constructor for the global scope.
    Scope() {
        this.parentScope = null;
        this.variables = new LinkedList<>();
    }

    void addNewVariables(String line) throws IllegalCodeException {
        String[] lineParts = line.trim().split("\\s+");
        String type;
        String[] variablesDeclarations = line.split("(char|int|boolean|double|String)")[1]
                .trim().replaceAll(";", "").split("\\s*,\\s*");
        if (lineParts[0].equals("final")) {
            type = lineParts[1];
            addNewFinalVariables(type, variablesDeclarations);
        } else {
            type = lineParts[0];
            addNewNotFinalVariables(type, variablesDeclarations);
        }
    }

    private void addNewFinalVariables(String newVariableType, String[] variablesDeclarations)
            throws IllegalCodeException {
        for (String assignment : variablesDeclarations) {
            initializeVariable(true, assignment, newVariableType);
        }
    }

    private void addNewNotFinalVariables(String newVariableType, String[] variablesDeclarations)
            throws IllegalCodeException {
        for (String assignment : variablesDeclarations) {
            if (assignment.contains("=")) {
                initializeVariable(false, assignment, newVariableType);
            } else {
                declareVariable(assignment, newVariableType);
            }
        }
    }

    private void declareVariable(String assignment, String type) throws IllegalCodeException {
        String variableName = assignment.trim();
        makeSureNameIsLegalAndNotTaken(variableName);
        Variable newVariable = new Variable(this, false, type, variableName);
        variables.add(newVariable);
    }

    private void initializeVariable(Boolean finallyTyped, String assignment, String type)
            throws IllegalCodeException {
        if (finallyTyped) {
            if (!assignment.contains("=")) {
                throw new IllegalCodeException
                        ("Finally typed variables must get an assignment in declaration");
            } else {
                int middle = assignment.indexOf("=");
                String variableName = assignment.substring(0, middle).trim();
                String valueOrVariable =
                        assignment.substring(middle + 1, assignment.length()).trim();
                makeSureNameIsLegalAndNotTaken(variableName);
                Variable newVariable = new Variable(this, true, type, variableName);
                makeAssignmentToVariable(newVariable, valueOrVariable);
                variables.add(newVariable);
            }
        } else {
            if (!assignment.contains("=")) {
                declareVariable(assignment, type);
            } else {
                int middle = assignment.indexOf("=");
                String variableName = assignment.substring(0, middle).trim();
                String valueOrVariable =
                        assignment.substring(middle + 1, assignment.length()).trim();
                makeSureNameIsLegalAndNotTaken(variableName);
                Variable newVariable = new Variable(this, finallyTyped, type, variableName);
                makeAssignmentToVariable(newVariable, valueOrVariable);
                variables.add(newVariable);
            }
        }

    }

    private void makeAssignmentToVariable(Variable receiver, String assignedVariableOrValue)
            throws IllegalCodeException {
        if (Variable.isLegalVariableName(assignedVariableOrValue)
                && Variable.getType(assignedVariableOrValue) == null) {
            Variable giver = getVariable(assignedVariableOrValue);
            if (giver == null) {
                if (getParentScope() == null) {
                    throw new IllegalCodeException
                            ("Illegal to initialize variable with an uninitialized variable in global scope");
                } else {
                    Variable[] assignment =
                            {receiver, new Variable(null, null, assignedVariableOrValue)};
                    Sjavac.assignmentsToCheck.add(assignment);
                    receiver.assignValueForNow();
                }
            } else {
                if (giver.isGlobal() && !giver.getHaveValue()) {
                    Variable[] assignment =
                            {receiver, giver};
                    Sjavac.assignmentsToCheck.add(assignment);
                    receiver.assignValueForNow();
                } else {
                    receiver.assignValue(giver);
                }
            }
        } else {
            if (!receiver.assignValue(assignedVariableOrValue)) {
                throw new IllegalCodeException("Illegal type assignment");
            }
        }
    }

    Scope getParentScope() {
        return parentScope;
    }

    void makeAssignmentToVariable(String line) throws IllegalCodeException {
        String variableName = line.split("\\s*=\\s*")[0].trim();
        String assignedVariableOrValue =
                line.split("\\s*=\\s*")[1].trim().replaceAll(";", "");
        Variable receiver = getVariable(variableName);
        if (receiver == null) {
            if (Variable.isLegalVariableName(assignedVariableOrValue)
                    && Variable.getType(assignedVariableOrValue) == null) {
                Variable giver = getVariable(assignedVariableOrValue);
                if (giver == null) {
                    throw new IllegalCodeException
                            ("Illegal to initialize global variable with an uninitialized global variable");
                } else {
                    String type = giver.getType();
                    Variable[] assignment =
                            {new Variable(this, null, null, variableName), giver};
                    Sjavac.assignmentsToCheck.add(assignment);
                    // in the meanwhile we will add this variable as a local initialized variable
                    // in order to prevent an awkward moment when somewhere in next lines this
                    // not-yet-initialized-global-variable will be used.
                    variables.add(new Variable(this, true, type, variableName));
                }
            } else {
                String type = Variable.getType(assignedVariableOrValue);
                Variable[] assignment =
                        {new Variable(this, null, null, variableName),
                                new Variable(this, null, type, null)};
                Sjavac.assignmentsToCheck.add(assignment);
                // in the meanwhile we will add this variable as a local initialized variable
                // in order to prevent an awkward moment when somewhere in next lines this
                // not-yet-initialized-global-variable will be used.
                variables.add(new Variable(this, true, type, variableName));
            }
        } else {
            if (receiver.getFinal()) {
                throw new IllegalCodeException("Finally typed variables cant get an assignment");
            } else {
                if (!receiver.assignValue(assignedVariableOrValue)) {
                    throw new IllegalCodeException("Illegal type assignment");
                }
            }
        }
    }

    void saveMethodCall() throws IllegalCodeException {
        Method methodCall = new Method(currentLine.trim().split("\\s*\\(")[0]);
        int parametersStart = currentLine.indexOf("(");
        int parametersEnd = currentLine.indexOf(")");
        String trimParameters = currentLine.substring(parametersStart + 1, parametersEnd).trim();
        if (!trimParameters.equals("")) {
            String[] finalNameOrValue = trimParameters.replaceAll
                    (",", " ").split("\\s+");
            for (int i = 0; i < finalNameOrValue.length; i++) {
                if (finalNameOrValue[i].equals("final")) {
                    String parameterName = finalNameOrValue[i + 1];
                    Variable parameter = getVariable(parameterName);
                    saveMethodCallHelper(methodCall, parameter, parameterName);
                } else {
                    String parameterName = finalNameOrValue[i];
                    if (Variable.isLegalVariableName(parameterName)
                            && Variable.getType(parameterName) == null) {
                        Variable parameter = getVariable(parameterName);
                        saveMethodCallHelper(methodCall, parameter, parameterName);
                    } else {
                        String type = Variable.getType(parameterName);
                        methodCall.addParameter(new Variable(this, null, type, null));
                    }
                }
            }
        }
        Sjavac.methodCalls.add(methodCall);
    }

    private void saveMethodCallHelper(Method methodCall, Variable parameter, String parameterName)
            throws IllegalCodeException {
        if (parameter == null) {
            methodCall.addParameter(
                    new Variable(this, null, null, parameterName));
        } else {
            if (parameter.getHaveValue()) {
                methodCall.addParameter(parameter);
            } else {
                throw new IllegalCodeException
                        ("Illegal to use Uninitialized variable in method call");
            }
        }
    }

    Variable getVariable(String variableName) {
        for (Variable variable : variables) {
            if (variable.isYourName(variableName)) return variable;
        }
        if (parentScope != null) {
            return parentScope.getVariable(variableName);
        } else {
            return null; // sign for global uninitialized variable
        }
    }

    void makeSureNameIsLegalAndNotTaken(String variableName) throws IllegalCodeException {
        if (!Variable.isLegalVariableName(variableName)) {
            throw new IllegalCodeException("Illegal variable name");
        }
        for (Variable variable : variables) {
            if (variable.isYourName(variableName)) {
                throw new IllegalCodeException
                        ("Illegal to have two local variables in the same scope with the same name.");
            }
        }
    }
}