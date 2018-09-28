package jass.ast;

import java.util.Arrays;
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

    public String generateTypeClasses() {
        StringBuilder s = new StringBuilder();
        for (Type type : types.values()) {
            s.append("class ").append(type.name).append(" extends ").append(type.parentId).append(" {\n");

            for (NativeFunctionRef ref : natives.values()) {
                if (ref.returnType == type || (ref.arguments.length > 0 && ref.arguments[0].type() == type))
                    s.append("\t").append(fun2str(ref)).append("\n");
            }

            s.append("}\n\n");
        }
        return s.toString();
    }

    public String generateTypes() {
        StringBuilder s = new StringBuilder();
        for (Type type : types.values()) {
            s.append("class ").append(type.name).append(" extends ").append(type.parentId).append(" {");

            s.append("}\n");
        }
        return s.toString();
    }

    public String generateNativeInterface() {
        StringBuilder s = new StringBuilder("public interface JassNativeInterface {\n");

        for (NativeFunctionRef ref : natives.values()) {
            s.append("\t").append(fun2str(ref)).append("\n");
        }

        return s + "}";
    }

    private static String fun2str(NativeFunctionRef ref) {
        StringBuilder s = new StringBuilder();

        s.append(typeToJavaType(ref.returnType));
        s.append(" ");
        s.append(ref.name);
        s.append("(");

        Arrays.stream(ref.arguments)
                .map(a -> typeToJavaType(a.type()) + " " + a.name)
                .reduce((a, b) -> a + ", " + b)
                .ifPresent(s::append);

        s.append(");");

        return s.toString();
    }

    private static String typeToJavaType(Type t) {
        if (t == Type.NOTHING) {
            return "void";
        } else if (t == Type.INTEGER) {
            return "int";
        } else if (t == Type.REAL) {
            return "double";
        } else if (t == Type.BOOLEAN) {
            return "boolean";
        } else if (t == Type.STRING) {
            return "String";
        } else if (t == Type.CODE) {
            return "Callback";
        }

        return "Pointer<" + t.name + ">";
    }
}
