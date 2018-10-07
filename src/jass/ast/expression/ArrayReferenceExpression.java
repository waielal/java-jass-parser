package jass.ast.expression;

import jass.ast.*;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;

import java.util.List;

public class ArrayReferenceExpression implements Expression {
    public final String variableArrayId;
    public final Expression indexExpr;
    public Variable variable;

    public ArrayReferenceExpression(String variableArrayId, Expression indexExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        variable = instance.getVariable(variableArrayId);

        indexExpr.checkRequirement(instance);

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
