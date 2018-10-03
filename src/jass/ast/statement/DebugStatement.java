package jass.ast.statement;

import jass.ast.JassHelper;
import jass.ast.Statement;

public class DebugStatement extends Statement {
    public final Statement statement;

    public DebugStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public void checkRequirement() {
        statement.checkRequirement();

        if (!(statement instanceof SetStatement) && !(statement instanceof SetArrayStatement) &&
                !(statement instanceof FunctionCallStatement) && !(statement instanceof ConditionalStatement) &&
                !(statement instanceof LoopStatement)) {
            throw new RuntimeException(statement.getClass().getName() + " can not be debugged!");
        }
    }

    @Override
    public void eval() {
        if (JassHelper.isDebug()) {
            statement.eval();
        }
    }

    @Override
    public String toString() {
        return "debug " + statement;
    }
}
