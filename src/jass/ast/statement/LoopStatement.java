package jass.ast.statement;

public class LoopStatement extends Statement {
    public final Statement[] statements;

    public LoopStatement(Statement[] statements) {
        this.statements = statements;
    }
}
