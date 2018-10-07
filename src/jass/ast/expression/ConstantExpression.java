package jass.ast.expression;

import jass.ast.declaration.Type;

public class ConstantExpression<T> extends Expression {
    public final T value;

    private ConstantExpression(T value, Type type) {
        this.value = value;
        this.type = type;
    }

    public static ConstantExpression<Void> constNull() {
        return new ConstantExpression<>(null, Type.NOTHING);
    }

    public static ConstantExpression<Boolean> constBool(Boolean value) {
        return new ConstantExpression<>(value, Type.BOOLEAN);
    }

    public static ConstantExpression<Integer> constInt(Integer value) {
        return new ConstantExpression<>(value, Type.INTEGER);
    }

    public static ConstantExpression<Double> constReal(Double value) {
        return new ConstantExpression<>(value, Type.REAL);
    }

    public static ConstantExpression<String> constString(String value) {
        return new ConstantExpression<>(value, Type.STRING);
    }
}
