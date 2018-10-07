package jass.ast.statement;

import jass.ast.*;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;
import jass.ast.expression.Expression;

import java.util.List;

public class SetArrayStatement implements Statement {
    public final String variableArrayId;
    public final Expression indexExpr;
    public final Expression assignExpr;

    public Variable variable;

    public SetArrayStatement(String variableArrayId, Expression indexExpr, Expression assignExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
        this.assignExpr = assignExpr;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        variable = instance.getVariable(variableArrayId);

        indexExpr.checkRequirement(instance);
        assignExpr.checkRequirement(instance);

        if (!variable.isArray)
            throw new RuntimeException(variable.name + " is not an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (indexExpr.evalType() != Type.INTEGER && indexExpr.evalType() != Type.REAL)
            throw new RuntimeException("Index hat to be from type Integer");

        if (variable.type() != assignExpr.evalType())
            throw new RuntimeException("Can not assign '" + assignExpr.evalType() + "' to '" + variable.type() + "'");
    }

    @Override
    public void run() {
        List<Object> array = variable.getValue();
        array.set(((Number) indexExpr.eval()).intValue(), assignExpr.eval());
    }

    @Override
    public String toString() {
        return "set " + variableArrayId + "[" + indexExpr + "] = " + assignExpr;
    }
}
