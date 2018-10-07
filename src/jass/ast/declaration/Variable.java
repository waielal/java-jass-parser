package jass.ast.declaration;

import jass.ast.expression.Expression;

public class Variable {
    public enum VariableScope {
        Global, Argument, Local
    }

    public final VariableScope scope;
    public final String name;
    public final boolean isConst;
    public final boolean isArray;
    public Type type;

    public Expression assignExpr;
    public Object value;

    private Variable(VariableScope scope, String name, String typeId, boolean isConst, boolean isArray) {
        this.scope = scope;
        this.name = name;
        this.isConst = isConst;
        this.isArray = isArray;
        this.type = Type.getType(typeId);
    }



    public static Variable createVariable(String name, String typeId, VariableScope scope) {
        return new Variable(scope, name, typeId, false, false);
    }

    public static Variable createArray(String name, String typeId, VariableScope scope) {
        return new Variable(scope, name, typeId, false, true);
    }

    public static Variable createArgument(String name, String typeId) {
        return new Variable(VariableScope.Argument, name, typeId, false, false);
    }

    public static Variable createGlobalConst(String name, String typeId) {
        return new Variable(VariableScope.Global, name, typeId, true, false);
    }
}
