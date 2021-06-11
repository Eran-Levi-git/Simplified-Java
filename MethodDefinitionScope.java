package oop.ex6.main;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

class MethodDefinitionScope extends Scope {
    // Constructor
    MethodDefinitionScope(Scope parentScope, BufferedReader bufferedReader, String currentLine)
            throws IOException, IllegalCodeException {
        super(parentScope, bufferedReader, currentLine);
        Method thisMethod = new Method(getDefinedMethodName());
        addParametersOfDefinedMethod(thisMethod);
        handleInnerMethodScope();
        Sjavac.methods.add(thisMethod);
    }

    private void handleInnerMethodScope() throws IOException, IllegalCodeException {
        Boolean lastLineWasReturn = false;
        Boolean legalExit = false;
        while ((currentLine = bufferedReader.readLine()) != null) {
            if (Sjavac.shouldBeIgnored(currentLine)) {
                lastLineWasReturn = false;
                continue;
            }
            if (Patterns.getLegalVariableDeclarationPattern().matcher(currentLine).matches()) {
                addNewVariables(currentLine);
                lastLineWasReturn = false;
            } else if (Patterns.getLegalVariableAssignmentPattern().matcher(currentLine).matches()) {
                makeAssignmentToVariable(currentLine);
                lastLineWasReturn = false;
            } else if (Patterns.getLegalConditionFirstLinePattern().matcher(currentLine).matches()) {
                new ConditionScope(this, bufferedReader, currentLine);
                lastLineWasReturn = false;
            } else if (Patterns.getLegalMethodCallPattern().matcher(currentLine).matches()) {
                saveMethodCall();
                lastLineWasReturn = false;
            } else if (currentLine.matches("\\s*return\\s*;\\s*")) {
                lastLineWasReturn = true;
            } else if (currentLine.matches(endOfScopeLine)) {
                if (lastLineWasReturn) {
                    legalExit = true;
                    break;
                } else throw new IllegalCodeException("No return line before closing line");
            } else {
                throw new IllegalCodeException("Illegal line in Method definition");
            }
        }
        if (!lastLineWasReturn || !legalExit) {
            throw new IllegalCodeException("No return line before closing line");
        }
    }

    private String getDefinedMethodName() {
        String newMethodName = null;
        String[] currentLineParts = currentLine.split("\\(")[0].split("\\s+");
        for (String part : currentLineParts) {
            if ((!part.equals("")) && (!part.equals("void"))) {
                newMethodName = part;
                break;
            }
        }
        assert (newMethodName != null);
        return newMethodName;
    }

    private void addParametersOfDefinedMethod(Method newMethod) throws IllegalCodeException {
        int conditionStart = currentLine.indexOf("(");
        int conditionEnd = currentLine.indexOf(")");
        String trimParameters = currentLine.substring(conditionStart + 1, conditionEnd).trim();
        if (Pattern.compile("\\s*").matcher(trimParameters).matches()) return;
        String[] finalTypeOrName = trimParameters.replaceAll(",", "").split("\\s+");
        String parameterType;
        String parameterName;
        for (int i = 0; i < finalTypeOrName.length; i++) {
            Variable parameter;
            if (finalTypeOrName[i].equals("final")) {
                i += 1;
                parameterType = finalTypeOrName[i];
                i += 1;
                parameterName = finalTypeOrName[i];
                makeSureNameIsLegalAndNotTaken(parameterName);
                parameter = new Variable(true, parameterType, parameterName);
                newMethod.addParameter(parameter);
            } else {
                parameterType = finalTypeOrName[i];
                i += 1;
                parameterName = finalTypeOrName[i];
                makeSureNameIsLegalAndNotTaken(parameterName);
                parameter = new Variable(false, parameterType, parameterName);
                newMethod.addParameter(parameter);
            }
            variables.add(parameter);
        }
    }
}
