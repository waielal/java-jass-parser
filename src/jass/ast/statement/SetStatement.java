package jass.ast.statement;

import jass.ast.*;
import jass.ast.declaration.Variable;
import jass.ast.expression.Expression;

public class SetStatement implements Statement {
    public final String variableId;
    public final Expression expr;

    public Variable variable;

    public SetStatement(String variableId, Expression expr) {
        this.variableId = variableId;
        this.expr = expr;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        expr.checkRequirement(instance);

        variable = instance.getVariable(variableId);

        if (variable.isArray)
            throw new RuntimeException(variable.name + " is an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (variable.type() != expr.evalType())
            throw new RuntimeException("Can not assign '" + expr.evalType() + "' to '" + variable.type() + "'");
    }

    @Override
    public void run() {
        variable.setValue(expr.eval());
    }

    @Override
    public String toString() {
        return "set " + variableId + " = " + expr;
    }
}
