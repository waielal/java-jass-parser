package jass.ast.expression;

import jass.JassPrinter;
import jass.ast.declaration.Type;

public abstract class Expression {
    public Type type;

    public String toString() {
        return JassPrinter.print(this);
    }
}
