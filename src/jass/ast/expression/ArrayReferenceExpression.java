package jass.ast.expression;

import jass.ast.declaration.Variable;

public class ArrayReferenceExpression extends Expression {
    public final String variableArrayId;
    public final Expression indexExpr;
    public Variable variable;

    public ArrayReferenceExpression(String variableArrayId, Expression indexExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
    }
}
