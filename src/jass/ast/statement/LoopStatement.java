package jass.ast.statement;

import jass.ast.Statement;

public class LoopStatement extends Statement {
    public final Statement[] statements;

    public LoopStatement(Statement[] statements) {
        this.statements = statements;
    }


    @Override
    public void checkRequirement() {

//        boolean atLeastOneExitWhenStatement = false;
        for (Statement statement : statements) {
            statement.checkRequirement();

//            if(statement instanceof ExitWhenStatement)
//                atLeastOneExitWhenStatement = true;
        }

        /*
        if (!atLeastOneExitWhenStatement) {
            //throw new RuntimeException("Loop has to have at least one exitwhen statement!");
            System.err.println("Warning: Loop has to have at least one exitwhen statement!");
        }
        */
    }

    @Override
    public void eval() {
        OuterLoop:
        while (true) {
            for (Statement statement : statements) {
                statement.eval();

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
