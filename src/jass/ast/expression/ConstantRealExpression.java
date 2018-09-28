package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ConstantRealExpression extends Expression {
    public final double value;

    public ConstantRealExpression(double value) {
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
        return Type.REAL;
    }

    public String toString() {
        return String.valueOf(value);
    }
}
