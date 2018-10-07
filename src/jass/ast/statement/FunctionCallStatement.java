package jass.ast.statement;

import jass.ast.expression.FunctionCallExpression;

public class FunctionCallStatement extends FunctionCallExpression implements Statement {
    public FunctionCallStatement(FunctionCallExpression expr) {
        super(expr.functionId, expr.arguments);
    }

    @Override
    public void run() {
        eval();
    }

    @Override
    public String toString() {
        return "call " + super.toString();
    }
}
