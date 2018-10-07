package jass.ast.statement;

import jass.ast.*;
import jass.ast.declaration.FunctionRef;
import jass.ast.expression.ConstantExpression.ConstantNullExpression;
import jass.ast.expression.Expression;

public class ReturnStatement implements Statement {
    public final Expression returnExpression;
    public final String functionId;
    public FunctionRef function;

    public ReturnStatement(Expression returnExpression, String functionId) {
        if (returnExpression == null)
            this.returnExpression = new ConstantNullExpression();
        else
            this.returnExpression = returnExpression;
        this.functionId = functionId;
    }

    @Override
    public void checkRequirement(JassInstance instance) {
        returnExpression.checkRequirement(instance);

        if (functionId == null) {
            throw new RuntimeException("Function identifier not set!");
        }

        function = (FunctionRef) instance.getFunction(functionId);

        if (function.returnType() != returnExpression.evalType()) {
            throw new RuntimeException("Wrong type returned. Expected: " +
                    function.returnType() + ", got: " + returnExpression.evalType());
        }
    }

    @Override
    public void run() {
        function.setReturnValue(returnExpression.eval());
    }

    @Override
    public String toString() {
        return "return " + returnExpression;
    }
}
