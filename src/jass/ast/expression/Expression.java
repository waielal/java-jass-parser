package jass.ast.expression;

import jass.ast.JassInstance;
import jass.ast.declaration.Type;

public interface Expression {
    void checkRequirement(JassInstance instance);

    Object eval();

    Type evalType();
}
