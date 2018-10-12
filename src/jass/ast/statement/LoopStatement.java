package jass.ast.statement;

public class LoopStatement extends Statement {
    public final BlockStatement statements;

    public LoopStatement(BlockStatement statements) {
        this.statements = statements;
    }
}
