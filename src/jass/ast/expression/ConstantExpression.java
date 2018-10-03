package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public abstract class ConstantExpression<T> extends Expression {
    public final T value;

    private ConstantExpression(T value) {
        this.value = value;
    }

    public void checkRequirement() {
    }

    public Object eval() {
        return value;
    }

    public String toString() {
        return String.valueOf(value);
    }

    public static class ConstantNullExpression extends ConstantExpression<Void> {
        public ConstantNullExpression() {
            super(null);
        }
        public Type evalType() {
            return Type.NOTHING;
        }
    }

    public static class ConstantBooleanExpression extends ConstantExpression<Boolean> {
        public ConstantBooleanExpression(Boolean value) {
            super(value);
        }
        public Type evalType() {
            return Type.BOOLEAN;
        }
    }

    public static class ConstantIntegerExpression extends ConstantExpression<Integer> {
        public ConstantIntegerExpression(Integer value) {
            super(value);
        }
        public Type evalType() {
            return Type.INTEGER;
        }
    }

    public static class ConstantRealExpression extends ConstantExpression<Double> {
        public ConstantRealExpression(Double value) {
            super(value);
        }
        public Type evalType() {
            return Type.REAL;
        }
    }

    public static class ConstantStringExpression extends ConstantExpression<String> {
        public ConstantStringExpression(String value) {
            super(value);
        }
        public Type evalType() {
            return Type.STRING;
        }
    }
}
