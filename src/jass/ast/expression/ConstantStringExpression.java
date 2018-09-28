package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ConstantStringExpression extends Expression {
    public final String value;

    public ConstantStringExpression(String value) {
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
        return Type.STRING;
    }

    public String toString() {
        return "\"" + value + "\"";
    }
}
