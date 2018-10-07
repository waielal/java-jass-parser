package jass.ast.declaration;

import jass.ast.JassInstance;
import jass.ast.expression.Expression;

public class Variable {
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

    public void setAssignExpr(Expression assignExpr) {
        this.assignExpr = assignExpr;
    }

    public Type type() {
        return type;
    }

    public void checkRequirement(JassInstance instance) {
        if (assignExpr != null) {
            assignExpr.checkRequirement(instance);

            if (type != assignExpr.evalType()) {
                throw new RuntimeException("Type mismatch. Expected: " + type + ", got: " + assignExpr.evalType());
            }

            if (isArray) {
                throw new RuntimeException("Can not assign value to an array declaration");
            }
        } else if (isConst) {
            throw new RuntimeException("Constant variable not initialized!");
        }
    }

    public void initializeValue() {
        if (assignExpr != null) {
            value = assignExpr.eval();
        }
    }

    public <T> T getValue() {
        if (scope == VariableScope.Global && assignExpr != null) {
            initializeValue();
            assignExpr = null;
        }

        //noinspection unchecked
        return (T) value;
    }

    public <T> void setValue(T newValue) {
        value = newValue;
    }

    @Override
    public String toString() {
        String s = "";
        if (isConst)
            s += "const ";

        s += type;
        if (isArray)
            s += "[]";

        s += " ";
        s += name;

        return s + " = " + value;
    }

    public enum VariableScope {
        Global, Argument, Local
    }
}
