package jass.ast.statement;

import jass.ast.declaration.Variable;
import jass.ast.expression.Expression;

public class SetArrayStatement extends Statement {
    public final String variableArrayId;
    public final Expression indexExpr;
    public final Expression assignExpr;
    public Variable variable;

    public SetArrayStatement(String variableArrayId, Expression indexExpr, Expression assignExpr) {
        this.variableArrayId = variableArrayId;
        this.indexExpr = indexExpr;
        this.assignExpr = assignExpr;
    }
}
