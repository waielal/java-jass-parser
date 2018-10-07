package jass.ast.statement;

import jass.ast.JassInstance;
import jass.ast.expression.Expression;
import jass.ast.declaration.Type;

public class ExitWhenStatement implements Statement {
    public final Expression expr;
    public boolean lastResult = false;

    public ExitWhenStatement(Expression expr) {
        this.expr = expr;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        expr.checkRequirement(instance);

        if (expr.evalType() != Type.BOOLEAN)
            throw new RuntimeException("Exitwhen excepts that the expression returns a Boolean value!");
    }

    @Override
    public void run() {
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
