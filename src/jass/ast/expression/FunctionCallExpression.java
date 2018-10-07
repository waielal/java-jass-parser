package jass.ast.expression;

import jass.ast.declaration.NativeFunctionRef;

public class FunctionCallExpression extends Expression {
    public final String functionId;
    public final Expression[] arguments;
    public NativeFunctionRef function;

    public FunctionCallExpression(String functionId, Expression[] arguments) {
        this.functionId = functionId;
        this.arguments = arguments;
    }
}
