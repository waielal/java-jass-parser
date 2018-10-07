package jass.ast;

import com.sun.istack.internal.NotNull;
import jass.ast.declaration.FunctionRef;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Variable;

import java.util.LinkedHashMap;
import java.util.Map;

public class JassInstance {
    public final Map<String, Variable> globals = new LinkedHashMap<>();
    public final Map<String, NativeFunctionRef> functions = new LinkedHashMap<>();

    public FunctionRef activeFunction;

    public static boolean isDebug() {
        return true;
    }


    @NotNull
    public Variable getVariable(String variableId) {
        return getVariable(variableId, activeFunction);
    }

    @NotNull
    public Variable getVariable(String variableId, FunctionRef activeFunction) {
        Variable var = null;

        if (activeFunction != null) {
            for (Variable localVariable : activeFunction.localVariables) {
                if (localVariable.name.equals(variableId)) {
                    var = localVariable;
                    break;
                }
            }
        }

        if (var == null && activeFunction != null) {
            for (Variable argumentVar : activeFunction.argumentVars) {
                if (argumentVar.name.equals(variableId)) {
                    var = argumentVar;
                    break;
                }
            }
        }

        if (var == null) {
            var = globals.get(variableId);
        }

        if (var == null) {
            throw new RuntimeException("Variable '" + variableId + "' was not declared before this line!");
        }

        return var;
    }

    @NotNull
    public NativeFunctionRef getFunction(String functionId) {
        NativeFunctionRef ref = functions.get(functionId);

        if (ref == null) {
            throw new RuntimeException("Function '" + functionId + "' was not declared before this line!");
        }

        return ref;
    }
}
