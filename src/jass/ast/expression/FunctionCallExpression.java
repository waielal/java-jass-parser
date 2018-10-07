package jass.ast.expression;

import jass.ast.*;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.NativeFunctionRef.Argument;
import jass.ast.declaration.Type;

public class FunctionCallExpression implements Expression {
    public final String functionId;
    public final Expression[] arguments;
    public NativeFunctionRef function;

    public FunctionCallExpression(String functionId, Expression[] arguments) {
        this.functionId = functionId;
        this.arguments = arguments;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        function = instance.getFunction(functionId);

        if (arguments.length != function.argumentVars.length) {
            throw new RuntimeException("Argument length does not match. Expected: " +
                    function.argumentVars.length + ", got: " + arguments.length);
        }

        for (int i = 0; i < arguments.length; i++) {
            arguments[i].checkRequirement(instance);
            if ((arguments[i].evalType() != function.argumentVars[i].type())
                    && !(arguments[i].evalType() == Type.INTEGER && function.argumentVars[i].type() == Type.REAL)
                    && !(arguments[i].evalType() == Type.NOTHING && Type.HANDLE.isParentOf(function.argumentVars[i].type()))
                    ) {
                throw new RuntimeException("Argument type (" + i + ") does not match. " + functionId + " expected: " +
                        function.argumentVars[i].type() + ", got: " + arguments[i].evalType());
            }
        }
    }

    @Override
    public Object eval() {
        Argument[] values = new Argument[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            values[i] = new Argument(arguments[i].evalType(), arguments[i].eval());
        }

        return function.eval(values);
    }

    @Override
    public Type evalType() {
        return function.returnType();
    }

    @Override
    public String toString() {
        return functionId + "(...)"; //TODO
    }
}
