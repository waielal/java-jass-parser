package jass.ast.statement;

import jass.ast.declaration.FunctionRef;
import jass.ast.expression.ConstantExpression;
import jass.ast.expression.Expression;

public class ReturnStatement extends Statement {
    public final Expression returnExpression;
    public final String functionId;
    public FunctionRef function;

    public ReturnStatement(Expression returnExpression, String functionId) {
        if (returnExpression == null)
            this.returnExpression = ConstantExpression.constNull();
        else
            this.returnExpression = returnExpression;
        this.functionId = functionId;
    }
}
