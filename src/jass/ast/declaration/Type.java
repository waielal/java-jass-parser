package jass.ast.declaration;

import jass.JassPrinter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Type {
    public static final List<Type> types = new ArrayList<>();

    public static final Type NOTHING = new Type("nothing");
    public static final Type BOOLEAN = new Type("boolean");
    public static final Type INTEGER = new Type("integer");
    public static final Type REAL = new Type("real");
    public static final Type STRING = new Type("string");
    public static final Type HANDLE = new Type("handle");
    public static final Type CODE = new Type("code");

    public static void addType(String name, String parentId) {
        new Type(name, parentId);
    }

    public static Type getType(String typeId) {
        return types.stream().filter(type -> type.name.equals(typeId)).findFirst().orElse(null);
    }

    ////////////////////////////////////////////////////////////////////////////////
    public final String name;
    public final Set<Type> child = new HashSet<>();
    public final Type parent;

    private Type(String name) {
        this(name, null);
    }

    private Type(String name, String parentId) {
        this.name = name;

        if(parentId != null) {
            this.parent = types.stream().filter(type -> type.name.equals(parentId)).findFirst().orElse(null);

            if(this.parent == null)
                throw new RuntimeException("Parent type '" + parentId + "' of type '" + name + "' not declared!");

            Type parent = this;
            do (parent = parent.parent).child.add(this);
            while (parent != HANDLE);
        } else {
            this.parent = null;
        }

        Type.types.add(this);
    }

    public boolean isParentOf(Type type) {
        return child.contains(type);
    }

    public boolean equals(Object obj) {
        return (obj instanceof Type) && name.equals(((Type) obj).name);
    }

    public String toString() {
        return JassPrinter.print(this);
    }
}
