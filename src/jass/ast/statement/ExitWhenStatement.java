package jass.ast.statement;

import jass.ast.Expression;
import jass.ast.Statement;
import jass.ast.Type;

public class ExitWhenStatement extends Statement {
    public final Expression expr;
    private boolean lastResult = false;

    public ExitWhenStatement(Expression expr) {
        this.expr = expr;
    }

    @Override
    public void checkRequirement() {
        expr.checkRequirement();

        if (expr.evalType() != Type.BOOLEAN)
            throw new RuntimeException("Exitwhen excepts that the expression returns a Boolean value!");
    }

    @Override
    public void eval() {
        lastResult = (boolean) expr.eval();
    }

    public boolean shouldBreak() {
        return lastResult;
    }

    @Override
    public String toString() {
        return "exitwhen " + expr;
    }
}
