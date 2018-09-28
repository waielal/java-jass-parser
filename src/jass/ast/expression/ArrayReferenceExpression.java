package jass.ast.expression;

import jass.ast.*;

import java.util.List;

public class ArrayReferenceExpression extends Expression {
    public final Identifier variableArrayId;
    public final Expression indexExpr;
    private Variable variable;

    public ArrayReferenceExpression(Identifier variableArrayId, Expression indexExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
    }

    @Override
    public void checkRequirement() {
        variable = JassHelper.getVariable(variableArrayId);

        indexExpr.checkRequirement();

        if (!variable.isArray)
            throw new RuntimeException(variable.name + " is not an array!");

        if (indexExpr.evalType() != Type.INTEGER)
            throw new RuntimeException("Index must be an Integer");
    }

    @Override
    public Object eval() {
        List<Object> array = variable.getValue();
        return array.get((Integer) indexExpr.eval());
    }

    @Override
    public Type evalType() {
        return variable.type();
    }

    public String toString() {
        return variableArrayId + "[" + indexExpr + "]";
    }
}
