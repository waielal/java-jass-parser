package jass.ast.statement;

import jass.ast.*;

public class SetStatement extends Statement {
    public final Identifier variableId;
    public final Expression expr;

    private Variable variable;

    public SetStatement(Identifier variableId, Expression expr) {
        this.variableId = variableId;
        this.expr = expr;
    }

    @Override
    public void checkRequirement() {
        expr.checkRequirement();

        variable = JassHelper.getVariable(variableId);

        if (variable.isArray)
            throw new RuntimeException(variable.name + " is an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (variable.type() != expr.evalType())
            throw new RuntimeException("Can not assign '" + expr.evalType() + "' to '" + variable.type() + "'");
    }

    @Override
    public void eval() {
        variable.setValue(expr.eval());
    }

    @Override
    public String toString() {
        return "set " + variableId + " = " + expr;
    }
}
