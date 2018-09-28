package jass.ast.statement;

import jass.ast.Expression;
import jass.ast.Statement;
import jass.ast.Type;

public class IfThenElseStatement extends Statement {
    public final Branch[] branches;

    public IfThenElseStatement(Branch[] branches) {
        this.branches = branches;
    }

    @Override
    public void checkRequirement() {
        for (Branch branch : branches) {
            branch.checkRequirement();
        }
    }

    @Override
    public void eval() {
        for (Branch branch : branches) {
            if ((boolean) branch.expr.eval()) {
                branch.eval();
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

    public static class Branch extends Statement {

        public final Expression expr;
        public final Statement[] statements;

        public Branch(Expression expr, Statement[] statements) {
            this.expr = expr;
            this.statements = statements;
        }

        @Override
        public void checkRequirement() {
            expr.checkRequirement();

            if (expr.evalType() != Type.BOOLEAN) {
                throw new RuntimeException("The provided expression doesn't return a Boolean");
            }

            for (Statement statement : statements) {
                statement.checkRequirement();
            }
        }

        @Override
        public void eval() {
            for (Statement statement : statements) {
                statement.eval();

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
