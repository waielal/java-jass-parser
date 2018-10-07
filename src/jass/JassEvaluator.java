package jass;

import jass.ast.declaration.FunctionRef;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.NativeFunctionRef.Argument;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;
import jass.ast.declaration.Variable.VariableScope;
import jass.ast.expression.*;
import jass.ast.statement.*;
import jass.ast.statement.ConditionalStatement.Branch;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.util.List;

public class JassEvaluator {
    private static final boolean DEBUG = true;

    public static Object run(NativeFunctionRef ref, Argument... arguments) {
        if (ref instanceof FunctionRef)
            return run((FunctionRef) ref, arguments);

        throw new NotImplementedException();
    }

    private static Object run(FunctionRef ref, Argument... arguments) {
        for (int i = 0; i < arguments.length; i++) {
            ref.argumentVars[i].value = arguments[i].value;
        }

        for (Variable var : ref.localVariables) {
            if (var.assignExpr != null) {
                var.value = eval(var.assignExpr);
            }
        }

        for (Statement statement : ref.statements) {
            eval(statement);

            if (ref.returnValue != null)
                break;

            if (statement instanceof ReturnStatement)
                break;
        }

        for (Variable var : ref.localVariables) {
            if (var.isArray) {
                //noinspection unchecked
                ((List<Object>) var.value).clear();
            } else {
                var.value = null;
            }
        }

        return ref.returnValue;
    }

    private static void eval(Statement statement) {
        if (statement instanceof SetStatement)
            eval((SetStatement) statement);
        else if (statement instanceof SetArrayStatement)
            eval((SetArrayStatement) statement);
        else if (statement instanceof ReturnStatement)
            eval((ReturnStatement) statement);
        else if (statement instanceof DebugStatement)
            eval((DebugStatement) statement);
        else if (statement instanceof ConditionalStatement)
            eval((ConditionalStatement) statement);
        else if (statement instanceof LoopStatement)
            eval((LoopStatement) statement);
        else if (statement instanceof ExitWhenStatement)
            eval((ExitWhenStatement) statement);
        else if (statement instanceof FunctionCallStatement)
            eval((FunctionCallStatement) statement);
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static void eval(SetStatement statement) {
        statement.variable.value = eval(statement.expr);
    }

    private static void eval(SetArrayStatement statement) {
        //noinspection unchecked
        ((List<Object>) statement.variable.value).set(((Number) eval(statement.indexExpr)).intValue(), eval(statement.assignExpr));
    }

    private static void eval(ReturnStatement statement) {
        statement.function.setReturnValue(eval(statement.returnExpression));
    }

    private static void eval(DebugStatement statement) {
        if (DEBUG) {
            eval(statement.statement);
        }
    }

    private static void eval(ConditionalStatement statement) {
        for (Branch branch : statement.branches) {
            if ((boolean) eval(branch.expr)) {
                eval(branch);
                break;
            }
        }
    }

    private static void eval(Branch branch) {
        for (Statement statement : branch.statements) {
            eval(statement);

            if (statement instanceof ReturnStatement)
                break;
        }
    }

    private static void eval(LoopStatement statement) {
        OuterLoop:
        while (true) {
            for (Statement s : statement.statements) {
                eval(s);

                if (s instanceof ExitWhenStatement && ((ExitWhenStatement) s).shouldBreak)
                    break OuterLoop;

                if (s instanceof ReturnStatement)
                    break OuterLoop;
            }
        }
    }

    private static void eval(ExitWhenStatement statement) {
        statement.shouldBreak = (boolean) eval(statement.expr);
    }

    private static void eval(FunctionCallStatement statement) {
        eval(statement.expr);
    }


    private static Object eval(Expression expression) {
        if (expression instanceof VariableExpression)
            return eval((VariableExpression) expression);
        else if (expression instanceof ArrayReferenceExpression)
            return eval((ArrayReferenceExpression) expression);
        else if (expression instanceof FunctionReferenceExpression)
            return eval((FunctionReferenceExpression) expression);
        else if (expression instanceof FunctionCallExpression)
            return eval((FunctionCallExpression) expression);
        else if (expression instanceof OperationTermExpression)
            return eval((OperationTermExpression) expression);
        else if (expression instanceof ConstantExpression)
            return eval((ConstantExpression) expression);
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static Object eval(VariableExpression expression) {
        Variable variable = expression.variable;

        if (variable.scope == VariableScope.Global && variable.assignExpr != null) {
            variable.value = eval(variable.assignExpr);
            variable.assignExpr = null;
        }
        return variable.value;
    }

    private static Object eval(ArrayReferenceExpression expression) {
        //noinspection unchecked
        return ((List<Object>) expression.variable.value).get((Integer) eval(expression.indexExpr));
    }

    private static Object eval(FunctionReferenceExpression expression) {
        return expression.function;
    }

    private static Object eval(FunctionCallExpression expression) {
        Argument[] values = new Argument[expression.arguments.length];

        for (int i = 0; i < expression.arguments.length; i++) {
            values[i] = new Argument(expression.arguments[i].type, eval(expression.arguments[i]));
        }

        return run(expression.function, values);
    }

    private static Object eval(OperationTermExpression expression) {
        switch (expression.operator) {
            case "AND":
                return (boolean) eval(expression.expr1) && (boolean) eval(expression.expr2);
            case "OR":
                return (boolean) eval(expression.expr1) || (boolean) eval(expression.expr2);
            case "NOT":
                return !(boolean) eval(expression.expr1);

            case "==":
                return eval(expression.expr1).equals(eval(expression.expr2));
            case ">=":
                return ((Number) eval(expression.expr1)).doubleValue() >=
                        ((Number) eval(expression.expr2)).doubleValue();
            case ">":
                return ((Number) eval(expression.expr1)).doubleValue() >
                        ((Number) eval(expression.expr2)).doubleValue();

            case "+":
                if (expression.type == Type.STRING)
                    return (String) eval(expression.expr1) + eval(expression.expr2);
                else if (expression.type == Type.INTEGER)
                    return ((Number) eval(expression.expr1)).intValue() +
                            ((Number) eval(expression.expr2)).intValue();
                return ((Number) eval(expression.expr1)).doubleValue() +
                        ((Number) eval(expression.expr2)).doubleValue();
            case "*":
                if (expression.type == Type.INTEGER)
                    return ((Number) eval(expression.expr1)).intValue() *
                            ((Number) eval(expression.expr2)).intValue();
                return ((Number) eval(expression.expr1)).doubleValue() *
                        ((Number) eval(expression.expr2)).doubleValue();
            case "/":
                if (expression.type == Type.INTEGER)
                    return ((Number) eval(expression.expr1)).intValue() /
                            ((Number) eval(expression.expr2)).intValue();
                return ((Number) eval(expression.expr1)).doubleValue() /
                        ((Number) eval(expression.expr2)).doubleValue();

            default:
                throw new RuntimeException("Unknown operator '" + expression.operator + "'");
        }
    }

    private static Object eval(ConstantExpression<?> expression) {
        return expression.value;
    }
}
