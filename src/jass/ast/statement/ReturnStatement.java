package jass.ast.statement;

import jass.ast.*;
import jass.ast.expression.ConstantNullExpression;

public class ReturnStatement extends Statement {
    public final Expression returnExpression;
    public final Identifier functionId;
    private FunctionRef function;

    public ReturnStatement(Expression returnExpression, Identifier functionId) {
        if (returnExpression == null)
            this.returnExpression = new ConstantNullExpression();
        else
            this.returnExpression = returnExpression;
        this.functionId = functionId;
    }

    @Override
    public void checkRequirement() {
        returnExpression.checkRequirement();

        if (functionId == null) {
            throw new RuntimeException("Function identifier not set!");
        }

        function = (FunctionRef) JassHelper.getFunction(functionId);

        if (function == null) {
            throw new RuntimeException("Could not retrieve function reference from identifier \"" + functionId + "\"!");
        }

        if (function.returnType() != returnExpression.evalType()) {
            throw new RuntimeException("Wrong type returned. Expected: " +
                    function.returnType() + ", got: " + returnExpression.evalType());
        }
    }

    @Override
    public void eval() {
        function.setReturnValue(returnExpression.eval());
    }

    @Override
    public String toString() {
        return "return " + returnExpression;
    }
}
