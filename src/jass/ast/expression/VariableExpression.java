package jass.ast.expression;

import jass.ast.declaration.Variable;

public class VariableExpression extends Expression {
    public final String variableId;
    public Variable variable;

    public VariableExpression(String variableId) {
        this.variableId = variableId;
    }
}
