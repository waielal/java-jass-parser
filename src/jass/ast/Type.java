package jass.ast;

public class Type implements PreloadTypeReference {
    public static final Type NOTHING = new Type("nothing");
    public static final Type BOOLEAN = new Type("boolean");
    public static final Type INTEGER = new Type("integer");
    public static final Type REAL = new Type("real");
    public static final Type STRING = new Type("string");
    public static final Type HANDLE = new Type("handle");
    public static final Type CODE = new Type("code");

    public final Identifier name;
    public final Identifier parentId;

    private Type parent;

    private Type(String name) {
        this(new Identifier(name), null);
    }

    public Type(Identifier name, Identifier parentId) {
        this.name = name;
        this.parentId = parentId;
    }

    public Type parent() {
        return parent;
    }

    @Override
    public void preloadTypeReference() {
        parent = JassHelper.getType(parentId);
    }

    @Override
    public String toString() {
        if (parentId == null)
            return name.toString();

        return name + " extends " + parentId;
    }

    public Type root() {
        Type t = this;
        while (t.parent() != NOTHING)
            t = t.parent();
        return t;
    }
}
