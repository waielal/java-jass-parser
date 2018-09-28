package jass.ast.expression;

import jass.ast.*;

public class VariableExpression extends Expression {
    public final Identifier variableId;
    private Variable variable;

    public VariableExpression(Identifier variableId) {
        this.variableId = variableId;
    }

    @Override
    public void checkRequirement() {
        variable = JassHelper.getVariable(variableId);

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
