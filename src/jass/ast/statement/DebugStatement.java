package jass.ast.statement;

import jass.ast.JassInstance;

public class DebugStatement implements Statement {
    public final Statement statement;

    public DebugStatement(Statement statement) {
        this.statement = statement;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        statement.checkRequirement(instance);

        if (!(statement instanceof SetStatement) && !(statement instanceof SetArrayStatement) &&
                !(statement instanceof FunctionCallStatement) && !(statement instanceof ConditionalStatement) &&
                !(statement instanceof LoopStatement)) {
            throw new RuntimeException(statement.getClass().getName() + " can not be debugged!");
        }
    }

    @Override
    public void run() {
        if (JassInstance.isDebug()) {
            statement.run();
        }
    }

    @Override
    public String toString() {
        return "debug " + statement;
    }
}
