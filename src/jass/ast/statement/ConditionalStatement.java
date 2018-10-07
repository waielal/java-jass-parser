package jass.ast.statement;

import jass.ast.JassInstance;
import jass.ast.expression.Expression;
import jass.ast.declaration.Type;

public class ConditionalStatement implements Statement {
    public final Branch[] branches;

    public ConditionalStatement(Branch[] branches) {
        this.branches = branches;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        for (Branch branch : branches) {
            branch.checkRequirement(instance);
        }
    }

    @Override
    public void run() {
        for (Branch branch : branches) {
            if ((boolean) branch.expr.eval()) {
                branch.run();
                break;
            }
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < branches.length; i++) {
            Branch b = branches[i];

            if (i == 0) {
                s += "if " + b.expr + " then\n" + b;
            } else {
                s += "elseif " + b.expr + " then\n" + b;
            }
        }
        return s + "endif";
    }

    public static class Branch implements Statement {
        public final Expression expr;
        public final Statement[] statements;

        public Branch(Expression expr, Statement[] statements) {
            this.expr = expr;
            this.statements = statements;
        }

        @Override
        public void checkRequirement(JassInstance instance) {
            expr.checkRequirement(instance);

            if (expr.evalType() != Type.BOOLEAN) {
                throw new RuntimeException("The provided expression doesn't return a Boolean");
            }

            for (Statement statement : statements) {
                statement.checkRequirement(instance);
            }
        }

        @Override
        public void run() {
            for (Statement statement : statements) {
                statement.run();

                if (statement instanceof ReturnStatement)
                    break;
            }
        }

        @Override
        public String toString() {
            String s = "";
            for (Statement statement : statements) {
                s += statement + "\n";
            }
            return s;
        }
    }
}
