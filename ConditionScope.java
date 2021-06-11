package oop.ex6.main;

import java.io.BufferedReader;
import java.io.IOException;

class ConditionScope extends Scope {

    // Constructor
    ConditionScope(Scope parentScope, BufferedReader bufferedReader, String line)
            throws IllegalCodeException, IOException {
        super(parentScope, bufferedReader, line);
        checkCondition();
        handleInnerConditionScope();
    }

    // Methods
    private void handleInnerConditionScope() throws IllegalCodeException, IOException {
        while ((currentLine = bufferedReader.readLine()) != null) {
            if (Sjavac.shouldBeIgnored(currentLine)) continue;
            // Legal line in Condition scope can only be:
            // variable declaration, variable assignment, condition scope opening,
            // method call or ending of scope line.
            if (Patterns.getLegalVariableDeclarationPattern().matcher(currentLine).matches()) {
                addNewVariables(currentLine);
            } else if (Patterns.getLegalVariableAssignmentPattern().matcher(currentLine).matches()) {
                makeAssignmentToVariable(currentLine);
            } else if (Patterns.getLegalConditionFirstLinePattern().matcher(currentLine).matches()) {
                new ConditionScope(this, bufferedReader, currentLine);
            } else if (Patterns.getLegalMethodCallPattern().matcher(currentLine).matches()) {
                saveMethodCall();
            } else if (!currentLine.matches("\\s*return\\s*;\\s*")) {
                if (currentLine.matches(endOfScopeLine)) {
                    break;
                } else {
                    throw new IllegalCodeException("Illegal line in condition");
                }
            }
        }
    }

    private void checkCondition() throws IllegalCodeException {
        int conditionStart = currentLine.indexOf("(");
        int conditionEnd = currentLine.indexOf(")");
        String condition = currentLine.substring(conditionStart + 1, conditionEnd);
        condition = condition.replaceAll(" ", "");
        String[] variables = condition.split("([|&])\\1");
        for (String variableNameOrValue : variables) {
            variableNameOrValue = variableNameOrValue.trim();
            if (Variable.isLegalVariableName(variableNameOrValue) && !variableNameOrValue.equals("true")
                    && Variable.getType(variableNameOrValue) == null && !variableNameOrValue.equals("false")) {
                Variable variable = getVariable(variableNameOrValue);
                if (variable == null) {
                    Sjavac.globalVariableConditionUsageToCheck.add(variableNameOrValue);
                } else {
                    if (verifyConditionType(variable.getType())) {
                        if (variable.getHaveValue()) return;
                        else throw new IllegalCodeException("Uninitialized condition variable");
                    } else throw new IllegalCodeException("Illegal condition value");
                }
            } else {
                String valueType = Variable.getType(variableNameOrValue);
                if (valueType == null) throw new IllegalCodeException("Illegal condition value");
                else {
                    if (verifyConditionType(valueType)) {
                        return;
                    } else throw new IllegalCodeException("Illegal condition value");
                }
            }
        }
    }

    private Boolean verifyConditionType(String type) {
        return type.equals(Variable.INT) || type.equals(Variable.DOUBLE) || type.equals(Variable.BOOLEAN);
    }
}
