package jass.ast;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NativeFunctionRef implements PreloadTypeReference {
    public final boolean isConst;
    public final Identifier name;
    public final Identifier returnTypeId;
    public final Variable[] argumentVars;
    protected Type returnType;


    public NativeFunctionRef(FunctionDef def, boolean isConst) {
        this.argumentVars = def.argumentVars;
        this.returnTypeId = def.returnTypeId;
        this.name = def.name;
        this.isConst = isConst;
    }

    @Override
    public void preloadTypeReference() {
        returnType = JassHelper.getType(returnTypeId);

        for (Variable argument : argumentVars) {
            argument.preloadTypeReference();
        }
    }

    public Type returnType() {
        return returnType;
    }

    public Object eval(Argument... arguments) {
        throw new NotImplementedException();
    }

    public static class Argument {
        public final Type type;
        public final Object value;

        public Argument(Type type, Object value) {
            this.type = type;
            this.value = value;
        }
    }

    public static class FunctionDef {
        public final Identifier name;
        public final Variable[] argumentVars;
        public final Identifier returnTypeId;

        public FunctionDef(Identifier name, Variable[] argumentVars, Identifier returnTypeId) {
            this.name = name;
            this.argumentVars = argumentVars;
            this.returnTypeId = returnTypeId;
        }
    }
}
