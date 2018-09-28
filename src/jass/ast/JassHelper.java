package jass.ast;

public class JassHelper {
    public static JassInstance instance;
    public static FunctionRef activeFunction;

    public static Variable getVariable(Identifier variableId) {
        Variable var = null;

        if (activeFunction != null)
            for (Variable localVariable : activeFunction.localVariables) {
                if (localVariable.name.equals(variableId)) {
                    var = localVariable;
                    break;
                }
            }

        if (var == null && activeFunction != null)
            for (Variable argument : activeFunction.arguments) {
                if (argument.name.equals(variableId)) {
                    var = argument;
                    break;
                }
            }

        if (var == null)
            var = instance.globals.get(variableId.id);

        if (var == null)
            var = instance.global_const.get(variableId.id);

        return var;
    }

    public static NativeFunctionRef getFunction(Identifier functionId) {
        NativeFunctionRef functionRef = instance.natives.get(functionId.id);

        if (functionRef == null)
            functionRef = instance.functions.get(functionId.id);

        return functionRef;
    }

    public static Type getType(Identifier typeId) {
        if (typeId == null)
            return Type.NOTHING;

        return instance.types.get(typeId.id);
    }

    public static boolean isDebug() {
        return true;
    }
}







































