package jass.ast.expression;

public class OperationTermExpression extends Expression {
    public final Expression expr1;
    public final Expression expr2;
    public final String operator;

    private OperationTermExpression(Expression expr1, Expression expr2, String operator) {
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.operator = operator;
    }


    public static OperationTermExpression and(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "AND");
    }

    public static OperationTermExpression or(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "OR");
    }

    public static OperationTermExpression not(Expression expr) {
        return new OperationTermExpression(expr, null, "NOT");
    }


    public static OperationTermExpression eq(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "==");
    }

    public static OperationTermExpression ge(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, ">=");
    }

    public static OperationTermExpression gt(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, ">");
    }


    public static OperationTermExpression add(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "+");
    }

    public static OperationTermExpression mul(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "*");
    }

    public static OperationTermExpression div(Expression expr1, Expression expr2) {
        return new OperationTermExpression(expr1, expr2, "/");
    }
}
