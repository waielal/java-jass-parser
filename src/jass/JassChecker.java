package jass;

import jass.ast.JassInstance;
import jass.ast.declaration.FunctionRef;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.Type;
import jass.ast.declaration.Variable;
import jass.ast.expression.*;
import jass.ast.statement.*;

public class JassChecker {
    private static JassInstance instance;
    private static FunctionRef activeFunction;

    static void check(JassInstance instance) {
        JassChecker.instance = instance;
        for (NativeFunctionRef ref : instance.functions.values()) {
            checkRequirement(ref);
        }
        JassChecker.instance = null;
    }

    static void check(JassInstance instance, NativeFunctionRef ref) {
        JassChecker.instance = instance;
        checkRequirement(ref);
        JassChecker.instance = null;
    }

    static void check(JassInstance instance, Variable variable) {
        JassChecker.instance = instance;
        checkRequirement(variable);
        JassChecker.instance = null;
    }

    private static void checkRequirement(NativeFunctionRef ref) {
        for (Variable a : ref.argumentVars) {
            checkRequirement(a);
        }

        if (ref instanceof FunctionRef)
            checkRequirement((FunctionRef) ref);
    }

    private static void checkRequirement(FunctionRef ref) {
        activeFunction = ref;

        for (Variable v : ref.localVariables) {
            checkRequirement(v);
        }

        for (Statement s : ref.statements) {
            checkRequirement(s);
        }

        activeFunction = null;
    }

    private static void checkRequirement(Variable variable) {
        if (variable.assignExpr != null) {
            checkRequirement(variable.assignExpr);

            if (variable.type != variable.assignExpr.type) {
                throw new RuntimeException("Type mismatch. Expected: " + variable.type + ", got: " + variable.assignExpr.type);
            }

            if (variable.isArray) {
                throw new RuntimeException("Can not assign value to an array declaration");
            }
        } else if (variable.isConst) {
            throw new RuntimeException("Constant variable not initialized!");
        }
    }


    private static void checkRequirement(Statement statement) {
        if (statement instanceof SetStatement) {
            ((SetStatement) statement).variable = getVariable(((SetStatement) statement).variableId, activeFunction);
        } else if (statement instanceof SetArrayStatement) {
            ((SetArrayStatement) statement).variable = getVariable(((SetArrayStatement) statement).variableArrayId, activeFunction);
        } else if (statement instanceof ReturnStatement) {
            ((ReturnStatement) statement).function = (FunctionRef) getFunction(((ReturnStatement) statement).functionId);
        }

        if (statement instanceof SetStatement)
            checkRequirement((SetStatement) statement);
        else if (statement instanceof SetArrayStatement)
            checkRequirement((SetArrayStatement) statement);
        else if (statement instanceof ReturnStatement)
            checkRequirement((ReturnStatement) statement);
        else if (statement instanceof DebugStatement)
            checkRequirement((DebugStatement) statement);
        else if (statement instanceof ConditionalStatement)
            checkRequirement((ConditionalStatement) statement);
        else if (statement instanceof LoopStatement)
            checkRequirement((LoopStatement) statement);
        else if (statement instanceof ExitWhenStatement)
            checkRequirement((ExitWhenStatement) statement);
        else if (statement instanceof FunctionCallStatement)
            checkRequirement((FunctionCallStatement) statement);
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static void checkRequirement(SetStatement statement) {
        checkRequirement(statement.expr);

        Variable variable = statement.variable;

        if (variable.isArray)
            throw new RuntimeException(variable.name + " is an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (variable.type != statement.expr.type)
            throw new RuntimeException("Can not assign '" + statement.expr.type + "' to '" + variable.type + "'");
    }

    private static void checkRequirement(SetArrayStatement statement) {
        Variable variable = statement.variable;

        checkRequirement(statement.indexExpr);
        checkRequirement(statement.assignExpr);

        if (!variable.isArray)
            throw new RuntimeException(variable.name + " is not an array!");

        if (variable.isConst)
            throw new RuntimeException(variable.name + " is constant. You can not assign a value to constant variables!");

        if (statement.indexExpr.type != Type.INTEGER && statement.indexExpr.type != Type.REAL)
            throw new RuntimeException("Index hat to be from type Integer");

        if (variable.type != statement.assignExpr.type)
            throw new RuntimeException("Can not assign '" + statement.assignExpr.type + "' to '" + variable.type + "'");
    }

    private static void checkRequirement(ReturnStatement statement) {
        checkRequirement(statement.returnExpression);

        NativeFunctionRef function = statement.function;

        if (function.returnType != statement.returnExpression.type) {
            throw new RuntimeException("Wrong type returned. Expected: " +
                    function.returnType + ", got: " + statement.returnExpression.type);
        }
    }

    private static void checkRequirement(DebugStatement statement) {
        checkRequirement(statement.statement);

        if (!(statement.statement instanceof SetStatement) && !(statement.statement instanceof SetArrayStatement) &&
                !(statement.statement instanceof FunctionCallStatement) && !(statement.statement instanceof ConditionalStatement) &&
                !(statement.statement instanceof LoopStatement)) {
            throw new RuntimeException(statement.getClass().getName() + " can not be debugged!");
        }
    }

    private static void checkRequirement(ConditionalStatement statement) {
        for (ConditionalStatement.Branch branch : statement.branches) {
            checkRequirement(branch);
        }
    }

    private static void checkRequirement(ConditionalStatement.Branch branch) {
        checkRequirement(branch.expr);

        if (branch.expr.type != Type.BOOLEAN) {
            throw new RuntimeException("The provided expr doesn't return a Boolean");
        }

        for (Statement statement : branch.statements) {
            checkRequirement(statement);
        }
    }

    private static void checkRequirement(LoopStatement statement) {
        for (Statement s : statement.statements) {
            checkRequirement(s);
        }
    }

    private static void checkRequirement(ExitWhenStatement statement) {
        checkRequirement(statement.expr);

        if (statement.expr.type != Type.BOOLEAN)
            throw new RuntimeException("Exitwhen excepts that the expr returns a Boolean value!");
    }

    private static void checkRequirement(FunctionCallStatement statement) {
        checkRequirement((Expression) statement.expr);
    }


    private static void checkRequirement(Expression expression) {
        if (expression instanceof VariableExpression) {
            ((VariableExpression) expression).variable = getVariable(((VariableExpression) expression).variableId, activeFunction);
        } else if (expression instanceof FunctionReferenceExpression) {
            ((FunctionReferenceExpression) expression).function = getFunction(((FunctionReferenceExpression) expression).functionId);
        } else if (expression instanceof FunctionCallExpression) {
            ((FunctionCallExpression) expression).function = getFunction(((FunctionCallExpression) expression).functionId);
        } else if (expression instanceof ArrayReferenceExpression) {
            ((ArrayReferenceExpression) expression).variable = getVariable(((ArrayReferenceExpression) expression).variableArrayId, activeFunction);
        }

        expression.type = evalType(expression);

        if (expression instanceof VariableExpression)
            checkRequirement((VariableExpression) expression);
        else if (expression instanceof ArrayReferenceExpression)
            checkRequirement((ArrayReferenceExpression) expression);
        else if (expression instanceof FunctionCallExpression)
            checkRequirement((FunctionCallExpression) expression);
        else if (expression instanceof OperationTermExpression)
            checkRequirement((OperationTermExpression) expression);
        else if (expression instanceof FunctionReferenceExpression)
            ; // Nothing to check
        else if (expression instanceof ConstantExpression)
            ; // Nothing to check
        else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static void checkRequirement(VariableExpression expression) {
        Variable variable = expression.variable;

        if (variable.isArray)
            throw new RuntimeException(variable.name + " is an array!");
    }

    private static void checkRequirement(ArrayReferenceExpression expression) {
        Variable variable = expression.variable;

        checkRequirement(expression.indexExpr);

        if (!variable.isArray)
            throw new RuntimeException(variable.name + " is not an array!");

        if (expression.indexExpr.type != Type.INTEGER)
            throw new RuntimeException("Index must be an Integer");
    }

    private static void checkRequirement(FunctionCallExpression expression) {
        NativeFunctionRef function = expression.function;

        if (expression.arguments.length != function.argumentVars.length) {
            throw new RuntimeException("Argument length does not match. Expected: " +
                    function.argumentVars.length + ", got: " + expression.arguments.length);
        }

        for (int i = 0; i < expression.arguments.length; i++) {
            checkRequirement(expression.arguments[i]);
            if (expression.arguments[i].type != function.argumentVars[i].type
                    && !(expression.arguments[i].type == Type.INTEGER && function.argumentVars[i].type == Type.REAL)
                    && !(expression.arguments[i].type == Type.NOTHING && Type.HANDLE.isParentOf(function.argumentVars[i].type))
                    ) {
                throw new RuntimeException("Argument type (" + i + ") does not match. " + expression.functionId + " expected: " +
                        function.argumentVars[i].type + ", got: " + expression.arguments[i].type);
            }
        }
    }

    private static void checkRequirement(OperationTermExpression expression) {
        if (expression.operator.equals("NOT")) {
            checkRequirement(expression.expr1);
            if (expression.type != Type.BOOLEAN)
                throw new RuntimeException("Expression '" + expression + "'is from type '" + expression.type + "' not from type boolean!");
            return;
        }

        checkRequirement(expression.expr1);
        checkRequirement(expression.expr2);

        Type aType = expression.expr1.type;
        Type bType = expression.expr2.type;

        switch (expression.operator) {
            case "AND":
            case "OR":
                if (aType != Type.BOOLEAN)
                    throw new RuntimeException("First parameter is not Boolean!");
                if (bType != Type.BOOLEAN)
                    throw new RuntimeException("Second parameter is not Boolean!");
                break;

            case ">=":
            case ">":
                if (aType != Type.INTEGER && aType != Type.REAL)
                    throw new RuntimeException("First parameter is not a Number!");
                if (bType != Type.INTEGER && bType != Type.REAL)
                    throw new RuntimeException("Second parameter is not a Number!");
                break;

            case "==":
                // TODO: check if this is really irrelevant
                break;


            case "+":
                if (aType == Type.STRING && bType == Type.STRING)
                    break;
            case "*":
            case "/":
                if ((aType != Type.INTEGER && aType != Type.REAL) || (bType != Type.INTEGER && bType != Type.REAL))
                    if (expression.operator.equals("+"))
                        throw new RuntimeException("One or both types are not numbers or String: " + expression.expr1 + " " + aType + ", " + bType);
                    else
                        throw new RuntimeException("One or both types are not numbers: " + expression.expr1 + " " + aType + ", " + bType);
                break;
            default:
                throw new RuntimeException("Unknown operator '" + expression.operator + "'");
        }
    }


    private static Type evalType(Expression expression) {
        if (expression instanceof VariableExpression)
            return ((VariableExpression) expression).variable.type;
        else if (expression instanceof ArrayReferenceExpression)
            return ((ArrayReferenceExpression) expression).variable.type;
        else if (expression instanceof FunctionCallExpression)
            return ((FunctionCallExpression) expression).function.returnType;
        else if (expression instanceof FunctionReferenceExpression)
            return Type.CODE;
        else if (expression instanceof ConstantExpression)
            return ((ConstantExpression) expression).type;
        else if (expression instanceof OperationTermExpression) {
            OperationTermExpression expr = (OperationTermExpression) expression;
            switch (expr.operator) {
                case "AND":
                case "OR":
                case "NOT":
                case ">=":
                case ">":
                case "==":
                    return Type.BOOLEAN;
            }

            checkRequirement(expr.expr1);
            Type aType = evalType(expr.expr1);
            checkRequirement(expr.expr2);
            Type bType = evalType(expr.expr2);

            switch (expr.operator) {
                case "+":
                    if (aType == Type.STRING && bType == Type.STRING)
                        return Type.STRING;
                case "*":
                case "/":
                    if (aType == Type.INTEGER && bType == Type.INTEGER)
                        return Type.INTEGER;
                    else
                        return Type.REAL;
                default:
                    throw new RuntimeException("Unknown operator '" + expr.operator + "'");
            }
        } else
            throw new RuntimeException("Hmm okay... :confused:");
    }

    private static Variable getVariable(String variableId, FunctionRef activeFunction) {
        Variable var = null;

        if (activeFunction != null) {
            for (Variable localVariable : activeFunction.localVariables) {
                if (localVariable.name.equals(variableId)) {
                    var = localVariable;
                    break;
                }
            }
        }

        if (var == null && activeFunction != null) {
            for (Variable argumentVar : activeFunction.argumentVars) {
                if (argumentVar.name.equals(variableId)) {
                    var = argumentVar;
                    break;
                }
            }
        }

        if (var == null) {
            var = instance.globals.get(variableId);
        }

        if (var == null) {
            throw new RuntimeException("Variable '" + variableId + "' was not declared before this line!");
        }

        return var;
    }

    private static NativeFunctionRef getFunction(String functionId) {
        NativeFunctionRef ref = instance.functions.get(functionId);

        if (ref == null) {
            throw new RuntimeException("Function '" + functionId + "' was not declared before this line!");
        }

        return ref;
    }
}
