package jass.ast.statement;

import jass.ast.expression.Expression;

public class ConditionalStatement extends Statement {
    public Expression expr;
    public Statement thenStatements;
    public Statement elseStatements;
}
