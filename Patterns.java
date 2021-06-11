package oop.ex6.main;

import java.util.regex.Pattern;

class Patterns {
    // Methods
    static Pattern getLegalVariableAssignmentPattern() {
        String variableName = "(([^\n\\d_ ]\\w*)|(_\\w+))";
        String intValue = "-?\\d+";
        String doubleValue = "((-?\\d+[.]\\d+)|(-?\\d+))";
        String stringValue = "\"[^\n\\\\',\"].*\"";
        String charValue = "'[^\n\\\\',\"]'";
        String booleanValue = "(false|true|(-?\\d+[.]\\d+)|-?\\d+)";
        String legalAssignment = "((" + intValue + ")|(" + doubleValue + ")|(" + stringValue + ")|" +
                "(" + charValue + ")|(" + booleanValue + ")|(" + variableName + "))";
        return Pattern.compile("\\s*" + variableName + "\\s*=" +
                "\\s*((" + variableName + ")|(" + legalAssignment + "))\\s*;\\s*");
    }

    static Pattern getLegalVariableDeclarationPattern() {
        String legalVariableName = "(([^\n\\d_ ]\\w*)|(_\\w+))";
        String intValue = "-?\\d+|"+legalVariableName;
        String singleIntDeclarationWithoutValue = "\\s*" + legalVariableName + "\\s*";
        String singleIntDeclarationWithValue =
                singleIntDeclarationWithoutValue + "=\\s*((" + intValue + ")|("+legalVariableName+"))\\s*";
        String intDeclarationWithValues = "int\\s+(" + singleIntDeclarationWithValue + ")+";
        String intDeclarationWithOrWithoutValues = "int\\s+" +
                "((" + singleIntDeclarationWithValue + ")|(" + singleIntDeclarationWithoutValue + "))"
                + "(,\\s*((" + singleIntDeclarationWithValue + ")|(" + singleIntDeclarationWithoutValue + ")))*";
        String doubleValue = "((-?\\d+[.]\\d+)|(-?\\d+))|"+legalVariableName;
        String singleDoubleDeclarationWithValue =
                legalVariableName + "\\s*=\\s*((" + doubleValue + ")|("+legalVariableName+"))\\s*";
        String doubleDeclarationWithValues = "double\\s+(" + singleDoubleDeclarationWithValue + ")+";
        String doubleDeclarationWithOrWithoutValues = "double[ ]+" +
                "((\\w+)|(" + singleDoubleDeclarationWithValue + "))" +
                "((\\s*,\\s*" + doubleValue + ")*|(\\s*,\\s*" + singleDoubleDeclarationWithValue + ")*)*";
        String stringValue = "\".*\"|"+legalVariableName;
        String singleStringDeclarationWithValue =
                legalVariableName + "\\s*=\\s*((" + stringValue+ ")|("+legalVariableName+"))";
        String stringDeclarationWithValues = "String\\s+(" + singleStringDeclarationWithValue + ")+";
        String stringDeclarationWithOrWithoutValues = "String\\s+" +
                "((" + legalVariableName + ")|(" + singleStringDeclarationWithValue + "))" +
                "((\\s*,\\s*" + stringValue + ")*|(\\s*,\\s*" + singleStringDeclarationWithValue + ")*)*";
        String charValue = "(('.')|"+legalVariableName+")";
        String singleCharDeclarationWithValue = "("+legalVariableName + "\\s*=\\s*" + charValue+")";
        String charDeclarationWithValues = "char\\s+(\\s*" + singleCharDeclarationWithValue + "\\s*)";
        String charDeclarationWithOrWithoutValues = "char\\s+" +
                "((\\s*" + legalVariableName + "\\s*)|(\\s*" + singleCharDeclarationWithValue + "\\s*))" +
                "(\\s*,\\s*((\\s* "+ charValue + "\\s*)|(\\s*" + singleCharDeclarationWithValue + "\\s*)))*";
        String booleanValue = "(((-?\\d+[.]\\d+)|-?\\d+)|"+legalVariableName+")";
        String singleBooleanDeclarationWithValue = legalVariableName + "\\s*=\\s*" + booleanValue+ "\\s*";
        String booleanDeclarationWithValues = "boolean\\s+(" + singleBooleanDeclarationWithValue + ")\\s*";
        String booleanDeclarationWithOrWithoutValues = "boolean\\s+" +
                "((" + legalVariableName + ")|(" + singleBooleanDeclarationWithValue + "))" +
                "((\\s*,\\s*" + booleanValue + ")*|(\\s*,\\s*" + singleBooleanDeclarationWithValue + ")*)*";
        String finalDeclaration = "\\s*(final\\s)\\s*(" +
                "(" + intDeclarationWithValues + ")|" +
                "(" + doubleDeclarationWithValues + ")|" +
                "(" + charDeclarationWithValues + ")|" +
                "(" + stringDeclarationWithValues + ")|" +
                "(" + booleanDeclarationWithValues + ")|" +
                ")\\s*;\\s*";
        String notFinalDeclaration = "\\s*(" +
                "(" + intDeclarationWithOrWithoutValues + ")|" +
                "(" + doubleDeclarationWithOrWithoutValues + ")|" +
                "(" + stringDeclarationWithOrWithoutValues + ")|" +
                "(" + charDeclarationWithOrWithoutValues + ")|" +
                "(" + booleanDeclarationWithOrWithoutValues + ")|" +
                ")\\s*;\\s*";
        return Pattern.compile("((" + finalDeclaration + ")|(" + notFinalDeclaration + "))");
    }

    static Pattern getLegalConditionFirstLinePattern() {
        String conditionStart = "\\s*(if|while)\\s*";
        String singleCondition = "\\s*(false|true|(-*\\d+[.]\\d+|-*\\d+)|(\\w+))\\s*";
        String multiCondition = "(" + singleCondition + "(&&|(\\|\\|))" + singleCondition + ")*";
        String conditionEnd = "\\s*\\{\\s*";
        return Pattern.compile(conditionStart + "\\((" + singleCondition + "|" + multiCondition + ")\\)" +
                "" + conditionEnd + "");
    }

    static Pattern getLegalMethodCallPattern() {
        String legalMethodName = "[^\n_\\d]\\w*";
        String singleLegalMethodParameter =
                "\\s*(([^\n\\d_ ]\\w*)|(_\\w+)|(-?\\d+[.]\\d+)|(-?\\d+)|\".*\")\\s*";
        String multipleMethodParameters =
                "(" + singleLegalMethodParameter + ")(,(" + singleLegalMethodParameter + "))+";
        return Pattern.compile("\\s*" + legalMethodName + "\\s*\\(" +
                "((\\s*)|(" + singleLegalMethodParameter + ")|" +
                "(" + multipleMethodParameters + "))\\)\\s*;\\s*");
    }

    static Pattern getLegalMethodDefinitionPattern() {
        String legalMethodName = "[^_\\d]\\w*";
        String legalParameterType = "(int|char|boolean|double|String)";
        String singleMethodParameter = "\\s*(final )?\\s*" + legalParameterType + "\\s+\\w+\\s*";
        String multipleMethodParameters =
                "((" + singleMethodParameter + ")(,(" + singleMethodParameter + "))+)";
        return Pattern.compile("\\s*void\\s+" + legalMethodName + "\\s*\\(" +
                "((\\s*)|(" + singleMethodParameter + ")|(" + multipleMethodParameters + "))\\)\\s*\\{\\s*");
    }
}
