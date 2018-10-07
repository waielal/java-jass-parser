package jass.ast.statement;

public class DebugStatement extends Statement {
    public final Statement statement;

    public DebugStatement(Statement statement) {
        this.statement = statement;
    }
}
