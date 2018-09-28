package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ConstantIntegerExpression extends Expression {
    public final int value;

    public ConstantIntegerExpression(int value) {
        this.value = value;
    }

    @Override
    public void checkRequirement() {
    }

    @Override
    public Object eval() {
        return value;
    }

    @Override
    public Type evalType() {
        return Type.INTEGER;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
