package jass.ast.statement;

import java.util.Iterator;

public class BlockStatement extends Statement implements Iterable<Statement> {
    public final Statement[] statements;

    public BlockStatement(Statement... statements) {
        this.statements = statements;
    }

    @Override
    public Iterator<Statement> iterator() {

        return new Iterator<Statement>() {
            int index = 0;
            public boolean hasNext() {
                return statements != null && index < statements.length;
            }

            public Statement next() {
                return statements[index++];
            }
        };
    }
}
