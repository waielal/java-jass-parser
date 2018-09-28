package jass.ast;

import jass.ast.statement.ReturnStatement;

public class FunctionRef extends NativeFunctionRef {
    public final Variable[] localVariables;
    public final Statement[] statements;
    private Object returnValue = null;

    public FunctionRef(FunctionDef def, boolean isConst, Variable[] localVariables, Statement[] statements) {
        super(def, isConst);
        this.localVariables = localVariables;
        this.statements = statements;
    }

    @Override
    public void preloadTypeReference() {
        super.preloadTypeReference();

        JassHelper.activeFunction = this;

        for (Variable argument : arguments) {
            argument.preloadTypeReference();
        }

        for (Variable localVariable : localVariables) {
            localVariable.preloadTypeReference();
        }

        JassHelper.activeFunction = null;
    }

    public void checkRequirement() {
        JassHelper.activeFunction = this;

        for (Statement s : statements) {
            s.checkRequirement();
        }

        JassHelper.activeFunction = null;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object eval(Argument[] arguments) {
        for (Variable var : localVariables) {
            var.initializeValue();
        }

        for (Statement statement : statements) {
            statement.eval();

            if (returnType != null)
                break;

            if (statement instanceof ReturnStatement)
                break;
        }

        return returnValue;
    }
}
