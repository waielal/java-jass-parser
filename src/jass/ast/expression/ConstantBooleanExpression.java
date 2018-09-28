package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ConstantBooleanExpression extends Expression {
    public final boolean value;

    public ConstantBooleanExpression(boolean value) {
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
        return Type.BOOLEAN;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
