package jass.ast;

public class Variable implements PreloadTypeReference {
    public final VariableScope scope;
    public final Identifier name;
    public final Identifier typeId;
    public final boolean isConst;
    public final boolean isArray;
    private Type type;

    private Expression assignExpr;

    private Object value;

    private Variable(VariableScope scope, Identifier name, Identifier typeId, boolean isConst, boolean isArray) {
        this.scope = scope;
        this.name = name;
        this.typeId = typeId;
        this.isConst = isConst;
        this.isArray = isArray;
    }

    public static Variable createGlobal(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Global, name, typeId, false, false);
    }

    public static Variable createGlobalConst(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Global, name, typeId, true, false);
    }

    public static Variable createGlobalArray(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Global, name, typeId, false, true);
    }

    public static Variable createArgument(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Argument, name, typeId, false, false);
    }

    public static Variable createLocal(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Local, name, typeId, false, false);
    }

    public static Variable createLocalArray(Identifier name, Identifier typeId) {
        return new Variable(VariableScope.Local, name, typeId, false, true);
    }

    @Override
    public void preloadTypeReference() {
        type = JassHelper.getType(typeId);

        if (assignExpr != null) {
            if (isArray) {
                throw new RuntimeException("Can not assign value to an array declaration");
            }

        } else if (isConst) {
            throw new RuntimeException("Constant variable not initialized!");
        }
    }

    public void setAssignExpr(Expression assignExpr) {
        this.assignExpr = assignExpr;
    }

    public Type type() {
        return type;
    }

    void initializeValue() {
        if (assignExpr != null) {
            assignExpr.checkRequirement();

            if (type != assignExpr.evalType()) {
                throw new RuntimeException("Type mismatch. Expected: " + type + ", got: " + assignExpr.evalType());
            }

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
}
