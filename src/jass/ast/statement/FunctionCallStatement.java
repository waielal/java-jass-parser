package jass.ast.statement;

import jass.ast.*;
import jass.ast.expression.FunctionCallExpression;

public class FunctionCallStatement extends Statement {
    public final FunctionCallExpression expr;

    public FunctionCallStatement(FunctionCallExpression expr) {
        this.expr = expr;
    }

    @Override
    public void checkRequirement() {
        expr.checkRequirement();
    }

    @Override
    public void eval() {
        expr.eval();
    }

    @Override
    public String toString() {
        return "call " + expr;
    }
}
