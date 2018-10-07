package jass.ast.statement;

import jass.ast.declaration.Variable;
import jass.ast.expression.Expression;

public class SetStatement extends Statement {
    public final String variableId;
    public final Expression expr;
    public Variable variable;

    public SetStatement(String variableId, Expression expr) {
        this.variableId = variableId;
        this.expr = expr;
    }
}
