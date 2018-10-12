package jass;

import jass.ast.declaration.FunctionRef;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;
import jass.ast.expression.*;
import jass.ast.statement.*;

import java.util.Arrays;

public class JassPrinter {

    public static String print(NativeFunctionRef ref) {
        if (ref instanceof FunctionRef)
            return print((FunctionRef) ref);

        return "native " + (ref.isConst ? "const " : "") + ref.name +
                " takes " +
                Arrays.stream(ref.argumentVars).map(v -> v.type.name + " " + v.name).reduce((a, b) -> a + ", " + b).orElse("nothing") +
                " returns " +
                ref.returnType.name;
    }

    private static String print(FunctionRef ref) {
        String ret = (ref.isConst ? "const " : "") + ref.name +
                " takes " +
                Arrays.stream(ref.argumentVars).map(v -> v.type.name + " " + v.name).reduce((a, b) -> a + ", " + b).orElse("nothing") +
                " returns " +
                ref.returnType.name + "\n";

        for (Variable var : ref.localVariables) {
            ret += print(var) + "\n";
        }

        ret += "\n";

        for (Statement s : ref.statements) {
            ret += print(s) + "\n";
        }

        return ret + "endfunction";
    }

    public static String print(Variable variable) {
        String s = "";
        if (variable.isConst)
            s += "const ";

        s += variable.type;
        if (variable.isArray)
            s += "[]";

        s += " ";
        s += variable.name;

        return s + " = " + variable.value;
    }

    public static String print(Type type) {
        if (type.parent == null)
            return type.name;
        return type.name + " extends " + type.parent.name;
    }

    public static String print(Statement statement) {
        if (statement instanceof BlockStatement)
            return print((BlockStatement) statement);
        else if (statement instanceof SetStatement)
            return print((SetStatement) statement);
        else if (statement instanceof SetArrayStatement)
            return print((SetArrayStatement) statement);
        else if (statement instanceof ReturnStatement)
            return print((ReturnStatement) statement);
        else if (statement instanceof DebugStatement)
            return print((DebugStatement) statement);
        else if (statement instanceof ConditionalStatement)
            return print((ConditionalStatement) statement);
        else if (statement instanceof LoopStatement)
            return print((LoopStatement) statement);
        else if (statement instanceof ExitWhenStatement)
            return print((ExitWhenStatement) statement);
        else if (statement instanceof FunctionCallStatement)
            return "call " + print(((FunctionCallStatement) statement).expr);
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static String print(BlockStatement statement) {
        StringBuilder ret = new StringBuilder();
        for (Statement s : statement) {
            ret.append("\n").append(print(s));
        }
        return ret.deleteCharAt(0).toString();
    }

    private static String print(SetStatement statement) {
        return "set " + statement.variableId + " = " + statement.expr;
    }

    private static String print(SetArrayStatement statement) {
        return "set " + statement.variableArrayId + "[" + statement.indexExpr + "] = " + statement.assignExpr;
    }

    private static String print(ReturnStatement statement) {
        return "return " + print(statement.returnExpression);
    }

    private static String print(DebugStatement statement) {
        return "debug " + print(statement.statement);
    }

    private static String print(ConditionalStatement statement) {
        String s = "if " + statement.expr + " then\n";

        s += print(statement.thenStatements) + "\n";
        s += "else\n";
        s += print(statement.thenStatements) + "\n";
        s += "endif";

        return s ;
    }

    private static String print(LoopStatement statement) {
        String ret = "loop\n";
        for (Statement s : statement.statements) {
            ret += print(s) + "\n";
        }
        return ret + "endloop";
    }

    private static String print(ExitWhenStatement statement) {
        return "exitwhen " + statement.expr;
    }


    public static String print(Expression expression) {
        if (expression instanceof VariableExpression)
            return print((VariableExpression) expression);
        else if (expression instanceof ArrayReferenceExpression)
            return print((ArrayReferenceExpression) expression);
        else if (expression instanceof FunctionReferenceExpression)
            return print((FunctionReferenceExpression) expression);
        else if (expression instanceof FunctionCallExpression)
            return print((FunctionCallExpression) expression);
        else if (expression instanceof OperationTermExpression)
            return print((OperationTermExpression) expression);
        else if (expression instanceof ConstantExpression)
            return print((ConstantExpression) expression);
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static String print(VariableExpression expression) {
        return expression.variableId;
    }

    private static String print(ArrayReferenceExpression expression) {
        return expression.variableArrayId + "[" + expression.indexExpr + "]";
    }

    private static String print(FunctionReferenceExpression expression) {
        return "function " + expression.functionId;
    }

    private static String print(FunctionCallExpression expression) {
        return expression.functionId + "(" + Arrays.stream(expression.arguments).map(Expression::toString).reduce((a, b) -> a + ", " + b).orElse("") + ")";
    }

    private static String print(OperationTermExpression expression) {
        if (expression.operator.equals("NOT"))
            return "(NOT " + expression.expr1 + ")";
        return "(" + expression.expr1 + " " + expression.operator + " " + expression.expr2 + ")";
    }

    private static String print(ConstantExpression<?> expression) {
        if (expression.type == Type.STRING)
            return "\"" + expression.value + "\"";
        return String.valueOf(expression.value);
    }
}
