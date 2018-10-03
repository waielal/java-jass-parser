package jass.ast;

import java.util.LinkedHashMap;
import java.util.Map;

public class JassInstance {
    public final Map<String, Type> types = new LinkedHashMap<>();
    public final Map<String, Variable> globals = new LinkedHashMap<>();
    public final Map<String, Variable> global_const = new LinkedHashMap<>();
    public final Map<String, FunctionRef> functions = new LinkedHashMap<>();
    public final Map<String, NativeFunctionRef> natives = new LinkedHashMap<>();

    public void init() {
        JassHelper.instance = this;

        types.put(Type.NOTHING.name.id, Type.NOTHING);
        types.put(Type.INTEGER.name.id, Type.INTEGER);
        types.put(Type.REAL.name.id, Type.REAL);
        types.put(Type.BOOLEAN.name.id, Type.BOOLEAN);
        types.put(Type.STRING.name.id, Type.STRING);
        types.put(Type.HANDLE.name.id, Type.HANDLE);
        types.put(Type.CODE.name.id, Type.CODE);

        for (Type type : types.values())
            type.preloadTypeReference();

        for (Variable var : global_const.values())
            var.preloadTypeReference();

        for (Variable var : globals.values())
            var.preloadTypeReference();

        for (NativeFunctionRef ref : natives.values())
            ref.preloadTypeReference();


        for (FunctionRef ref : functions.values()) {
            ref.preloadTypeReference();
            ref.checkRequirement();
        }

        JassHelper.instance = null;
    }
}
