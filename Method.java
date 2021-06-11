package oop.ex6.main;


import java.util.LinkedList;

/**
 * Class representation for Method.
 */
public class Method {
    // Data members
    private LinkedList<Variable> parameters;
    private String name;
    private LinkedList<String> parametersTypes;

    // Constructor
    Method(String name) {
        this.name = name;
        this.parameters = new LinkedList<>();
        this.parametersTypes = new LinkedList<>();
    }

    // Methods
    String getName() {
        return name;
    }

    /**
     * @param globalScope global scope object.
     * @param methodCall  a given method call
     * @return true if the given call matches the required method and false otherwise
     */
    public Boolean isValidCall(Scope globalScope, Method methodCall) {
        // suspects to be global variables calls saved only with name field initialized.
        LinkedList<Variable> methodCallVariables = methodCall.getParameters();
        int i = 0;
        if (methodCallVariables.size() != parameters.size()) return false;
        for (String parameterType : getParametersTypes()) {
            if (methodCallVariables.get(i).getType() == null) {
                Variable globalVariable = globalScope.getVariable(methodCallVariables.get(i).getName());
                if (globalVariable == null) {
                    return false;
                } else {
                    if (Variable.checkUsage(parameterType, globalVariable.getType())) i++;
                    else return false;
                }
            } else {
                if (Variable.checkUsage(parameterType, methodCallVariables.get(i).getType())) i++;
                else return false;
            }
        }
        return true;
    }

    /**
     * @param variable a given variable to add as a parameter to the method
     */
    public void addParameter(Variable variable) {
        parameters.add(variable);
        variable.assignValueForNow();
        parametersTypes.add(variable.getType());
    }

    private LinkedList<Variable> getParameters() {
        return parameters;
    }

    private LinkedList<String> getParametersTypes() {
        return parametersTypes;
    }
}
