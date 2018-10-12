package jass.ast.declaration;

import jass.ast.statement.BlockStatement;

public class FunctionRef extends NativeFunctionRef {
    public final Variable[] localVariables;
    public final BlockStatement statements;

    public Object returnValue = null;

    public FunctionRef(String name, Variable[] argumentVars, String returnTypeId, boolean isConst, Variable[] localVariables, BlockStatement statements) {
        super(name, argumentVars, returnTypeId, isConst);
        this.localVariables = localVariables;
        this.statements = statements;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
