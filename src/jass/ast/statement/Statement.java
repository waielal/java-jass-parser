package jass.ast.statement;

import jass.ast.JassInstance;

public interface Statement {
    void checkRequirement(JassInstance instance);

    void run();
}
