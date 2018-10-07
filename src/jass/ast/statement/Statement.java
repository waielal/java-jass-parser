package jass.ast.statement;

import jass.JassPrinter;

public abstract class Statement {
    public String toString() {
        return JassPrinter.print(this);
    }
}
