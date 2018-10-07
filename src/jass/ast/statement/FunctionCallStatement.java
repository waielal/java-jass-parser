package jass.ast.statement;

import jass.ast.expression.FunctionCallExpression;

public class FunctionCallStatement extends Statement {
    public final FunctionCallExpression expr;

    public FunctionCallStatement(FunctionCallExpression expr) {
        this.expr = expr;
    }
}
