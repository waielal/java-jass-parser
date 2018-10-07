package jass.ast.expression;

import jass.ast.JassInstance;
import jass.ast.declaration.Type;

public abstract class OperationTermExpression implements Expression {
    public final Expression expr1;
    public final Expression expr2;
    public final String operator;

    OperationTermExpression(Expression expr1, Expression expr2, String operator) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.operator = operator;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        if (operator.equals("NOT")) {
            expr1.checkRequirement(instance);
            if (expr1.evalType() != Type.BOOLEAN)
                throw new RuntimeException("Expression is not from type Boolean!");
            return;
        }

        expr1.checkRequirement(instance);
        expr2.checkRequirement(instance);

        Type aType = expr1.evalType();
        Type bType = expr2.evalType();

        switch (operator) {
            case "AND":
            case "OR":
                if (aType != Type.BOOLEAN)
                    throw new RuntimeException("First parameter is not Boolean!");
                if (bType != Type.BOOLEAN)
                    throw new RuntimeException("Second parameter is not Boolean!");
                break;

            case ">=":
            case ">":
                if (aType != Type.INTEGER && aType != Type.REAL)
                    throw new RuntimeException("First parameter is not a Number!");
                if (bType != Type.INTEGER && bType != Type.REAL)
                    throw new RuntimeException("Second parameter is not a Number!");
                break;

            case "==":
                // TODO: check if this is really irrelevant?
                break;


            case "+":
                if (aType == Type.STRING && bType == Type.STRING)
                    break;
            case "*":
            case "/":
                if ((aType != Type.INTEGER && aType != Type.REAL) || (bType != Type.INTEGER && bType != Type.REAL))
                    if(operator.equals("+"))
                        throw new RuntimeException("One or both types are not numbers or String: " + expr1 + " " + aType + ", " + bType);
                    else
                        throw new RuntimeException("One or both types are not numbers: " + expr1 + " " + aType + ", " + bType);
                break;
            default:
                throw new RuntimeException("Unknown operator '" + operator + "'");
        }
    }

    @Override
    public Type evalType() {
        switch (operator) {
            case "AND":
            case "OR":
            case "NOT":
            case ">=":
            case ">":
            case "==":
                return Type.BOOLEAN;
        }

        Type aType = expr1.evalType();
        Type bType = expr2.evalType();

        switch (operator) {
            case "+":
                if (aType == Type.STRING && bType == Type.STRING)
                    return Type.STRING;
            case "*":
            case "/":
                if (aType == Type.INTEGER && bType == Type.INTEGER)
                    return Type.INTEGER;
                else
                    return Type.REAL;
            default:
                throw new RuntimeException("Unknown operator '" + operator + "'");
        }
    }

    @Override
    public String toString() {
        return "(" + expr1 + " " + operator + " " + expr2 + ")";
    }


    public static OperationTermExpression and(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "AND") {
            public Object eval() {
                return (boolean) expr1.eval() && (boolean) expr2.eval();
            }
        };
    }

    public static OperationTermExpression or(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "OR") {
            public Object eval() {
                return (boolean) expr1.eval() || (boolean) expr2.eval();
            }
        };
    }

    public static OperationTermExpression not(Expression expr) {
        return new OperationTermExpression(expr, null, "NOT") {
            public Object eval() {
                return !(boolean) expr1.eval();
            }

            public String toString() {
                return "(NOT " + expr1 + ")";
            }
        };
    }


    public static OperationTermExpression eq(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "==") {
            public Object eval() {
                return expr1.eval().equals(expr2.eval());
            }
        };
    }

    public static OperationTermExpression ge(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, ">=") {
            public Object eval() {
                return ((Number) expr1.eval()).doubleValue() >= ((Number) expr2.eval()).doubleValue();
            }
        };
    }

    public static OperationTermExpression gt(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, ">") {
            public Object eval() {
                return ((Number) expr1.eval()).doubleValue() > ((Number) expr2.eval()).doubleValue();
            }
        };
    }


    public static OperationTermExpression add(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "+") {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return ((Number) expr1.eval()).intValue() + ((Number) expr2.eval()).intValue();
                else if (evalType() == Type.STRING)
                    return (String) expr1.eval() + expr2.eval();

                return ((Number) expr1.eval()).doubleValue() + ((Number) expr2.eval()).doubleValue();
            }
        };
    }

    public static OperationTermExpression mul(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "*") {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return ((Number) expr1.eval()).intValue() * ((Number) expr2.eval()).intValue();
                return ((Number) expr1.eval()).doubleValue() * ((Number) expr2.eval()).doubleValue();
            }
        };
    }

    public static OperationTermExpression div(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "/") {
            public Object eval() {
                if (evalType() == Type.INTEGER)
                    return ((Number) expr1.eval()).intValue() / ((Number) expr2.eval()).intValue();
                return ((Number) expr1.eval()).doubleValue() / ((Number) expr2.eval()).doubleValue();
            }
        };
    }
}
