package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public abstract class NumberOpTermExpression extends Expression {
    public static NumberOpTermExpression add(Expression expr1, Expression expr2) {
        return new NumberOpTermExpression(expr1, expr2) {
            @Override
            public void checkRequirement() {
                expr1.checkRequirement();
                expr2.checkRequirement();

                Type aType = expr1.evalType();
                Type bType = expr2.evalType();

                if (aType == Type.STRING && bType == Type.STRING)
                    return;

                if ((aType != Type.INTEGER && aType != Type.REAL) || (bType != Type.INTEGER && bType != Type.REAL))
                    throw new RuntimeException("One or both types are not numbers or String: " + expr1 + " " + aType + ", " + bType);
            }

            @Override
            public Type evalType() {
                Type aType = expr1.evalType();
                Type bType = expr2.evalType();

                if (aType == Type.INTEGER && bType == Type.INTEGER)
                    return Type.INTEGER;
                else if (aType == Type.STRING && bType == Type.STRING)
                    return Type.STRING;

                return Type.REAL;
            }

            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return (int) expr1.eval() + (int) expr2.eval();
                else if (evalType() == Type.STRING)
                    return (String) expr1.eval() + expr2.eval();

                return (double) expr1.eval() + (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " + " + expr2 + ")";
            }
        };
    }

    public static NumberOpTermExpression sub(Expression expr1, Expression expr2) {
        return new NumberOpTermExpression(expr1, expr2) {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return (int) expr1.eval() - (int) expr2.eval();
                return (double) expr1.eval() - (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " - " + expr2 + ")";
            }
        };
    }

    public static NumberOpTermExpression mul(Expression expr1, Expression expr2) {
        return new NumberOpTermExpression(expr1, expr2) {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return (int) expr1.eval() * (int) expr2.eval();
                return (double) expr1.eval() * (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " * " + expr2 + ")";
            }
        };
    }

    public static NumberOpTermExpression div(Expression expr1, Expression expr2) {
        return new NumberOpTermExpression(expr1, expr2) {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return (int) expr1.eval() / (int) expr2.eval();
                return (double) expr1.eval() / (double) expr2.eval();
            }

            public String toString() {
                return "(" + expr1 + " / " + expr2 + ")";
            }
        };
    }

    final Expression expr1, expr2;

    NumberOpTermExpression(Expression expr1, Expression expr2) {
        this.expr1 = expr1;
        this.expr2 = expr2;
    }

    @Override
    public void checkRequirement() {
        expr1.checkRequirement();
        expr2.checkRequirement();

        Type aType = expr1.evalType();
        Type bType = expr2.evalType();

        if ((aType != Type.INTEGER && aType != Type.REAL) || (bType != Type.INTEGER && bType != Type.REAL))
            throw new RuntimeException("One or both types are not numbers: " + expr1 + " " + aType + ", " + bType);
    }

    @Override
    public Type evalType() {
        Type aType = expr1.evalType();
        Type bType = expr2.evalType();

        if (aType == Type.INTEGER && bType == Type.INTEGER)
            return Type.INTEGER;
        else
            return Type.REAL;
    }
}
