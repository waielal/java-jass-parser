package jass.ast.expression;

import jass.ast.*;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;

public class VariableExpression implements Expression {
    public final String variableId;
    public Variable variable;

    public VariableExpression(String variableId) {
        this.variableId = variableId;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        variable = instance.getVariable(variableId);

        if (variable.isArray)
            throw new RuntimeException(variable.name + " is an array!");
    }

    @Override
    public Object eval() {
        return variable.getValue();
    }

    @Override
    public Type evalType() {
        return variable.type();
    }

    public String toString() {
        return variableId.toString();
    }
}
