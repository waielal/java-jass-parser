package jass.ast.expression;

import jass.ast.Expression;
import jass.ast.Type;

public class ConstantNullExpression extends Expression {
    @Override
    public void checkRequirement() {
    }

    @Override
    public Object eval() {
        return null;
    }

    @Override
    public Type evalType() {
        return Type.NOTHING;
    }

    public String toString() {
        return "null";
    }
}
