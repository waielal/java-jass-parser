package jass.ast.expression;

import jass.ast.declaration.NativeFunctionRef;

public class FunctionReferenceExpression extends Expression {
    public final String functionId;
    public NativeFunctionRef function;

    public FunctionReferenceExpression(String functionId) {
        this.functionId = functionId;
    }
}
