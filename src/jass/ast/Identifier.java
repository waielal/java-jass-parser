package jass.ast;

public class Identifier {
    public final String id;

    public Identifier(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        return (obj instanceof Identifier) && id.equals(((Identifier) obj).id);
    }

    @Override
    public String toString() {
        return id;
    }
}
