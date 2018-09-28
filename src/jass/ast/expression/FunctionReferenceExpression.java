package jass.ast.expression;

import jass.ast.*;

public class FunctionReferenceExpression extends Expression {
    public final Identifier functionId;
    private NativeFunctionRef function;

    public FunctionReferenceExpression(Identifier functionId) {
        this.functionId = functionId;
    }


    @Override
    public void checkRequirement() {
        function = JassHelper.getFunction(functionId);
    }

    @Override
    public Object eval() {
        return function;
    }

    @Override
    public Type evalType() {
        return Type.CODE;
    }

    public String toString() {
        return "function " + functionId + ")";
    }
}
