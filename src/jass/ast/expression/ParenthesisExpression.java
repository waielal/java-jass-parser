package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ParenthesisExpression extends Expression {
    public final Expression expr;

    public ParenthesisExpression(Expression expr) {
        this.expr = expr;
    }

    @Override
    public void checkRequirement() {
        expr.checkRequirement();
    }

    @Override
    public Object eval() {
        return expr.eval();
    }

    @Override
    public Type evalType() {
        return expr.evalType();
    }

    public String toString() {
        return "(" + expr + ")";
    }
}
