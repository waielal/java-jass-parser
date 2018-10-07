package jass.ast.statement;

import jass.ast.expression.Expression;

public class ConditionalStatement extends Statement {
    public final Branch[] branches;

    public ConditionalStatement(Branch[] branches) {
        this.branches = branches;
    }

    public static class Branch extends Statement {
        public final Expression expr;
        public final Statement[] statements;

        public Branch(Expression expr, Statement[] statements) {
            this.expr = expr;
            this.statements = statements;
        }
    }
}
