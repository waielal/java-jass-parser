package jass.ast;

import jass.ast.statement.ReturnStatement;

import java.util.List;

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
    public Object eval(Argument... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            this.arguments[i].setValue(arguments[i].value);
        }

        JassHelper.activeFunction = this;
        for (Variable var : localVariables) {
            var.initializeValue();
        }
        JassHelper.activeFunction = null;

        for (Statement statement : statements) {
            statement.eval();

            if (returnValue != null)
                break;

            if (statement instanceof ReturnStatement)
                break;
        }

        for (Variable var : localVariables) {
            if (var.isArray) {
                List<Object> array = var.getValue();
                array.clear();
            } else {
                var.setValue(null);
            }
        }

        return returnValue;
    }
}
