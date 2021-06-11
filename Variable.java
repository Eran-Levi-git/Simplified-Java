package oop.ex6.main;

import java.util.regex.Pattern;

/**
 * Class representation for Variable.
 */
public class Variable {
    // Constants
    static final String INT = "int";
    static final String DOUBLE = "double";
    static final String BOOLEAN = "boolean";
    private static final String STRING = "String";
    private static final String CHAR = "char";
    private static final String intValue = "-?\\d+";
    private static final String doubleValue = "((-?\\d+[.]\\d+)|(-?\\d+))";
    private static final String stringValue = "\\\".*\\\"";
    private static final String charValue = "'.'";
    private static final String booleanValue = "(false|true|(-?\\d+[.]\\d+)|-?\\d+)";

    // Data members
    private String type;
    private String name;
    private Boolean isFinal;
    private Boolean haveValue;
    private Boolean isGlobal;

    // Constructors
    Variable(Boolean isFinal, String type, String name) {
        this.isGlobal = false;
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        this.haveValue = false;
    }

    // constructor for global variables
    Variable(Scope scope, Boolean isFinal, String type, String name) {
        isGlobal = scope.getParentScope() == null;
        this.isFinal = isFinal;
        this.type = type;
        this.name = name;
        this.haveValue = false;
    }

    // Methods
    Boolean isYourName(String name) {
        return this.name.equals(name);
    }

    String getType() {
        return type;
    }

    Boolean getHaveValue() {
        return haveValue;
    }


    Boolean isGlobal() {
        return isGlobal;
    }

    void assignValue(Variable giver) throws IllegalCodeException {
        if (!giver.haveValue) {
            throw new IllegalCodeException("Cant assign variable not initialized");
        }
        switch (this.type) {
            case INT:
                if (giver.getType().equals(INT)) {
                    this.haveValue = true;
                } else throw new IllegalCodeException("Wrong type assignment");
                return;
            case DOUBLE:
                if ((giver.getType().equals(INT)) || (giver.getType().equals(DOUBLE))) {
                    haveValue = true;
                } else throw new IllegalCodeException("Wrong type assignment");
                return;
            case CHAR:
                if ((giver.getType().equals(CHAR))) {
                    haveValue = true;
                } else throw new IllegalCodeException("Wrong type assignment");
                return;
            case STRING:
                if ((giver.getType().equals(STRING))) {
                    haveValue = true;
                } else throw new IllegalCodeException("Wrong type assignment");
                return;
            case BOOLEAN:
                if ((giver.getType().equals(BOOLEAN)) || (giver.getType().equals(INT)) ||
                        (giver.getType().equals(DOUBLE))) {
                    haveValue = true;
                } else throw new IllegalCodeException("Wrong type assignment");
        }
    }

    static Boolean checkUsage(String methodRequiredType, String type) {
        switch (methodRequiredType) {
            case INT:
                return type.equals(INT);
            case DOUBLE:
                return (type.equals(INT)) || (type.equals(DOUBLE));
            case CHAR:
                return (type.equals(CHAR));
            case STRING:
                return (type.equals(STRING));
            case BOOLEAN:
                return (type.equals(BOOLEAN)) || (type.equals(INT)) ||
                        (type.equals(DOUBLE));
            default:
                return false;
        }
    }

    static String getType(String value) {
        if (Pattern.compile(intValue).matcher(value).matches()) return INT;
        if (Pattern.compile(doubleValue).matcher(value).matches()) return DOUBLE;
        if (Pattern.compile(charValue).matcher(value).matches()) return CHAR;
        if (Pattern.compile(stringValue).matcher(value).matches()) return STRING;
        if (Pattern.compile(booleanValue).matcher(value).matches()) return BOOLEAN;
        else return null;
    }

    void assignValueForNow() {
        haveValue = true;
    }

    Boolean assignValue(String value) {
        switch (type) {
            case INT:
                if (Pattern.compile(intValue).matcher(value).matches()) haveValue = true;
                else return false;
                break;
            case DOUBLE:
                if (Pattern.compile(doubleValue).matcher(value).matches()) haveValue = true;
                else return false;
                break;
            case CHAR:
                if (Pattern.compile(charValue).matcher(value).matches()) haveValue = true;
                else return false;
                break;
            case STRING:
                if (Pattern.compile(stringValue).matcher(value).matches()) haveValue = true;
                else return false;
                break;
            case BOOLEAN:
                if (Pattern.compile(booleanValue).matcher(value).matches()) haveValue = true;
                else return false;
                break;
            default:
                return false;
        }
        return true;
    }

    static Boolean isLegalVariableName(String assignedVariableOrValue) {
        if (assignedVariableOrValue.equals("true") || assignedVariableOrValue.equals("false")) return false;
        else {
            String legalVariableName = "(([^\n\\d_ ]\\w*)|(_\\w+))";
            return (Pattern.compile(legalVariableName).matcher(assignedVariableOrValue).matches());
        }
    }

    Boolean getFinal() {
        return isFinal;
    }

    String getName() {
        return name;
    }
}
