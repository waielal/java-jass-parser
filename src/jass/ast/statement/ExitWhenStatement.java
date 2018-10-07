package jass.ast.statement;

import jass.ast.expression.Expression;

public class ExitWhenStatement extends Statement {
    public final Expression expr;
    public boolean shouldBreak = false;

    public ExitWhenStatement(Expression expr) {
        this.expr = expr;
    }
}
