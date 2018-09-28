package jass.ast.statement;

import jass.ast.*;

import java.util.List;

public class SetArrayStatement extends Statement {
    public final Identifier variableArrayId;
    final Expression indexExpr, assignExpr;

    private Variable variable;

    public SetArrayStatement(Identifier variableArrayId, Expression indexExpr, Expression assignExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
        this.assignExpr = assignExpr;
    }

    @Override
    public void checkRequirement() {
        variable = JassHelper.getVariable(variableArrayId);

        indexExpr.checkRequirement();
        assignExpr.checkRequirement();

        if (!variable.isArray)
            throw new RuntimeException(variable.name + " is not an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (indexExpr.evalType() != Type.INTEGER)
            throw new RuntimeException("Index hat to be from type Integer");

        if (variable.type() != assignExpr.evalType())
            throw new RuntimeException("Can not assign '" + assignExpr.evalType() + "' to '" + variable.type() + "'");
    }

    @Override
    public void eval() {
        List<Object> array = variable.getValue();
        array.set((int) indexExpr.eval(), assignExpr.eval());
    }

    @Override
    public String toString() {
        return "set " + variableArrayId + "[" + indexExpr + "] = " + assignExpr;
    }
}
