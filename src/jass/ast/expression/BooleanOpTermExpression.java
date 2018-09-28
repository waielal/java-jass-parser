package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public abstract class BooleanOpTermExpression extends Expression {
    abstract static class BooleanOpTermExpression_CheckBool extends BooleanOpTermExpression {
        BooleanOpTermExpression_CheckBool(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        public void checkRequirement() {
            expr1.checkRequirement();
            expr2.checkRequirement();

            if (expr1.evalType() != Type.BOOLEAN)
                throw new RuntimeException("First parameter is not Boolean!");
            if (expr2.evalType() != Type.BOOLEAN)
                throw new RuntimeException(expr2 + " Second parameter is not Boolean!");
        }
    }

    abstract static class BooleanOpTermExpression_CheckNumber extends BooleanOpTermExpression {
        BooleanOpTermExpression_CheckNumber(Expression expr1, Expression expr2) {
            super(expr1, expr2);
        }

        public void checkRequirement() {
            expr1.checkRequirement();
            expr2.checkRequirement();

            Type aType = expr1.evalType();
            Type bType = expr2.evalType();

            if (aType != Type.INTEGER && aType != Type.REAL)
                throw new RuntimeException("First parameter is not a Number!");
            if (bType != Type.INTEGER && bType != Type.REAL)
                throw new RuntimeException("Second parameter is not a Number!");
        }
    }

    public static BooleanOpTermExpression and(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckBool(expr1, expr2) {
            public Object eval() {
                return (boolean) expr1.eval() && (boolean) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " AND " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression or(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckBool(expr1, expr2) {
            public Object eval() {
                return (boolean) expr1.eval() || (boolean) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " OR " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression eq(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression(expr1, expr2) {
            public Object eval() {
                return expr1.eval() == expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " == " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression neq(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression(expr1, expr2) {
            public Object eval() {
                return expr1.eval() != expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " != " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression ge(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckNumber(expr1, expr2) {
            public Object eval() {
                return (double) expr1.eval() >= (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " >= " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression le(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckNumber(expr1, expr2) {
            public Object eval() {
                return (double) expr1.eval() <= (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " <= " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression gt(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckNumber(expr1, expr2) {
            public Object eval() {
                return (double) expr1.eval() > (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " > " + expr2 + ")";
            }
        };
    }

    public static BooleanOpTermExpression lt(Expression expr1, Expression expr2) {
        return new BooleanOpTermExpression_CheckNumber(expr1, expr2) {
            public Object eval() {
                return (double) expr1.eval() < (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " < " + expr2 + ")";
            }
        };
    }

    final Expression expr1, expr2;

    BooleanOpTermExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public void checkRequirement() {
        expr1.checkRequirement();
        expr2.checkRequirement();
    }

    @Override
    public Type evalType() {
        return Type.BOOLEAN;
    }
}
