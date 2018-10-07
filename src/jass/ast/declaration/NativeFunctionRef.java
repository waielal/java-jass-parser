package jass.ast.declaration;

import jass.ast.JassInstance;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class NativeFunctionRef {
    public final String name;
    public final Variable[] argumentVars;
    public final Type returnType;
    public final boolean isConst;


    public NativeFunctionRef(String name, Variable[] argumentVars, String returnTypeId, boolean isConst) {
        this.name = name;
        this.argumentVars = argumentVars;
        this.returnType = Type.getType(returnTypeId);
        this.isConst = isConst;
    }

    public void checkRequirement(JassInstance instance) {
        for (Variable a : argumentVars) {
            a.checkRequirement(instance);
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
}
