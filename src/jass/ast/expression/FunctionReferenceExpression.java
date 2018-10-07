package jass.ast.expression;

import jass.ast.*;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Type;

public class FunctionReferenceExpression implements Expression {
    public final String functionId;
    public NativeFunctionRef function;

    public FunctionReferenceExpression(String functionId) {
        this.functionId = functionId;
    }


    @Override
    public void checkRequirement(JassInstance instance) {
        function = instance.getFunction(functionId);
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
