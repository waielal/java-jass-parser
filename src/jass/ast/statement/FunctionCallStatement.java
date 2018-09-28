package jass.ast.statement;

import jass.ast.*;
import jass.ast.NativeFunctionRef.Argument;

public class FunctionCallStatement extends Statement {
    public final Identifier functionId;
    public final Expression[] arguments;
    private NativeFunctionRef function;

    public FunctionCallStatement(Identifier functionId, Expression[] arguments) {
        this.functionId = functionId;
        this.arguments = arguments;
    }

    @Override
    public void checkRequirement() {
        function = JassHelper.getFunction(functionId);

        if (arguments.length != function.arguments.length)
            throw new RuntimeException("Argument length does not match. Expected: " +
                    function.arguments.length + ", got: " + arguments.length);

        for (int i = 0; i < arguments.length; i++) {
            arguments[i].checkRequirement();
            if ((arguments[i].evalType() != function.arguments[i].type())
                    && !(arguments[i].evalType() == Type.INTEGER && function.arguments[i].type() == Type.REAL)
                    && !(arguments[i].evalType() == Type.NOTHING && function.arguments[i].type().root() == Type.HANDLE)
                    ) {
                throw new RuntimeException("Argument type (" + i + ") does not match. " + functionId + " expected: " +
                        function.arguments[i].type() + ", got: " + arguments[i].evalType());
            }
        }
    }

    @Override
    public void eval() {
        Argument[] values = new Argument[arguments.length];

        for (int i = 0; i < arguments.length; i++) {
            values[i] = new Argument(arguments[i].evalType(), arguments[i].eval());
        }

        function.eval(values);
    }

    @Override
    public String toString() {
        return "call " + functionId + "(...)"; //TODO
    }
}
