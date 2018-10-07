package jass.ast.statement;

import jass.ast.JassInstance;

public class LoopStatement implements Statement {
    public final Statement[] statements;

    public LoopStatement(Statement[] statements) {
        this.statements = statements;
    }


    @Override
    public void checkRequirement(JassInstance instance) {
        for (Statement statement : statements) {
            statement.checkRequirement(instance);
        }
    }

    @Override
    public void run() {
        OuterLoop:
        while (true) {
            for (Statement statement : statements) {
                statement.run();

                if (statement instanceof ExitWhenStatement && ((ExitWhenStatement) statement).shouldBreak())
                    break OuterLoop;

                if (statement instanceof ReturnStatement)
                    break OuterLoop;
            }
        }
    }

    @Override
    public String toString() {
        String s = "loop\n";
        for (Statement statement : statements) {
            s += statement + "\n";
        }
        return s + "endloop";
    }
}
