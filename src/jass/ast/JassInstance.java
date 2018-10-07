package jass.ast;

import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Variable;

import java.util.LinkedHashMap;
import java.util.Map;

public class JassInstance {
    public final Map<String, Variable> globals = new LinkedHashMap<>();
    public final Map<String, NativeFunctionRef> functions = new LinkedHashMap<>();
}
