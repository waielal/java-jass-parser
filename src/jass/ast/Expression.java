package jass.ast;

public abstract class Expression extends Base {
    public abstract Object eval();

    public abstract Type evalType();
}
