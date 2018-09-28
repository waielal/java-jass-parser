package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public abstract class UnaryTermExpression extends Expression {
    public static UnaryTermExpression not(Expression expr) {
        return new UnaryTermExpression(expr) {
            public void checkRequirement() {
                expr.checkRequirement();
                if (expr.evalType() != Type.BOOLEAN)
                    throw new RuntimeException("Expression is not from type Boolean!");
            }

            public Object eval() {
                return !(boolean) expr.eval();
            }

            public Type evalType() {
                return Type.BOOLEAN;
            }

            public String toString() {
                return "(NOT " + expr + ")";
            }
        };
    }

    public static UnaryTermExpression pos(Expression expr) {
        return new UnaryTermExpression(expr) {
            public void checkRequirement() {
                expr.checkRequirement();
                Type type = expr.evalType();
                if (type != Type.INTEGER || type != Type.REAL)
                    throw new RuntimeException("Expression is not a Number!");
            }

            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return expr.eval();
                else
                    return expr.eval();
            }

            public Type evalType() {
                return expr.evalType();
            }

            public String toString() {
                return "(+ " + expr + ")";
            }
        };
    }

    public static UnaryTermExpression neg(Expression expr) {
        return new UnaryTermExpression(expr) {
            public void checkRequirement() {
                expr.checkRequirement();
                Type type = expr.evalType();
                if (type != Type.INTEGER && type != Type.REAL)
                    throw new RuntimeException("Expression is not a Number! ");
            }

            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return -(int) expr.eval();
                else
                    return -(double) expr.eval();
            }

            public Type evalType() {
                return expr.evalType();
            }

            public String toString() {
                return "(- " + expr + ")";
            }
        };
    }

    public final Expression expr;

    private UnaryTermExpression(Expression expr) {
        this.expr = expr;
    }
}
