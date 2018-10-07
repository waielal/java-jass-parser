package jass;

import jass.ast.declaration.FunctionRef;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;
import jass.ast.expression.*;
import jass.ast.statement.*;
import jass.ast.statement.ConditionalStatement.Branch;

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

    public static String print(FunctionRef ref) {
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
        if (statement instanceof SetStatement)
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

    public static String print(SetStatement statement) {
        return "set " + statement.variableId + " = " + statement.expr;
    }

    public static String print(SetArrayStatement statement) {
        return "set " + statement.variableArrayId + "[" + statement.indexExpr + "] = " + statement.assignExpr;
    }

    public static String print(ReturnStatement statement) {
        return "return " + print(statement.returnExpression);
    }

    public static String print(DebugStatement statement) {
        return "debug " + print(statement.statement);
    }

    public static String print(ConditionalStatement statement) {
        String s = "";
        for (int i = 0; i < statement.branches.length; i++) {
            Branch b = statement.branches[i];

            if (i == 0) {
                s += "if " + b.expr + " then\n" + b;
            } else {
                s += "elseif " + b.expr + " then\n" + b;
            }
        }
        return s + "endif";
    }

    public static String print(Branch branch) {
        String s = "";
        for (Statement statement : branch.statements) {
            s += statement + "\n";
        }
        return s;
    }

    public static String print(LoopStatement statement) {
        String ret = "loop\n";
        for (Statement s : statement.statements) {
            ret += print(s) + "\n";
        }
        return ret + "endloop";
    }

    public static String print(ExitWhenStatement statement) {
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

    public static String print(VariableExpression expression) {
        return expression.variableId;
    }

    public static String print(ArrayReferenceExpression expression) {
        return expression.variableArrayId + "[" + expression.indexExpr + "]";
    }

    public static String print(FunctionReferenceExpression expression) {
        return "function " + expression.functionId;
    }

    public static String print(FunctionCallExpression expression) {
        return expression.functionId + "(" + Arrays.stream(expression.arguments).map(Expression::toString).reduce((a, b) -> a + ", " + b).orElse("") + ")";
    }

    public static String print(OperationTermExpression expression) {
        if (expression.operator.equals("NOT"))
            return "(NOT " + expression.expr1 + ")";
        return "(" + expression.expr1 + " " + expression.operator + " " + expression.expr2 + ")";
    }

    public static String print(ConstantExpression<?> expression) {
        if (expression.type == Type.STRING)
            return "\"" + expression.value + "\"";
        return String.valueOf(expression.value);
    }
}
