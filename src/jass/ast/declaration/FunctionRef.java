package jass.ast.declaration;

import jass.ast.JassInstance;
import jass.ast.statement.ReturnStatement;
import jass.ast.statement.Statement;

import java.util.List;

public class FunctionRef extends NativeFunctionRef {
    public final Variable[] localVariables;
    public final Statement[] statements;

    public Object returnValue = null;

    public FunctionRef(String name, Variable[] argumentVars, String returnTypeId, boolean isConst, Variable[] localVariables, Statement[] statements) {
        super(name, argumentVars, returnTypeId, isConst);
        this.localVariables = localVariables;
        this.statements = statements;
    }

    public void checkRequirement(JassInstance instance) {
        super.checkRequirement(instance);

        instance.activeFunction = this;

        for (Variable v : localVariables) {
            v.checkRequirement(instance);
        }

        for (Statement s : statements) {
            s.checkRequirement(instance);
        }

        instance.activeFunction = null;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }

    @Override
    public Object eval(Argument... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            argumentVars[i].setValue(arguments[i].value);
        }

        for (Variable var : localVariables) {
            var.initializeValue();
        }

        for (Statement statement : statements) {
            statement.run();

            if (returnValue != null) {
                break;
            }

            if (statement instanceof ReturnStatement) {
                break;
            }
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
