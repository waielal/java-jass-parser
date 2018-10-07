package jass;

import jass.ast.JassInstance;
import jass.ast.declaration.*;
import jass.ast.declaration.Variable.VariableScope;
import jass.ast.expression.*;
import jass.ast.statement.*;
import jass.ast.statement.ConditionalStatement.Branch;

import java.util.ArrayList;
import java.util.List;

public class JassParser {
    private static final String ID_REGEX = "[a-zA-Z_][a-zA-Z_0-9]*";

    private static final String DEC_REGEX = "[1-9][0-9]*";
    private static final String OCT_REGEX = "0[0-9]*";
    private static final String HEX_REGEX = "(\\$[0-9a-fA-F]+|0[xX][0-9a-fA-F]+)";
    private static final String FOURCC_REGEX = "'.{4}'";

    private static final String REAL_REGEX = "([0-9]+\\.[0-9]*|\\.[0-9]+)";

    private static final String[] reserved_words = new String[]{
            "function", "takes", "returns", "return", "nothing", "endfunction",
            "if", "else", "elseif", "endif", "then", "loop", "endloop",
            "exitwhen", "globals", "endglobals", "local", "set",
            "call", "constant", "type", "extends", "native", "array"
    };

    private JassInstance instance;
    private NativeFunctionRef activeFunctionId;

    public JassInstance parse(JassLexer lexer) {
        instance = new JassInstance();
        this.file(lexer);
        return instance;
    }

    private void file(JassLexer lexer) {
        while (this.declaration(lexer)) ;
    }


    private boolean declaration(JassLexer lexer) {
        return lexer.hasMore() && (
                this.typedef(lexer) ||
                        this.globals(lexer) ||
                        this.func(lexer)
        );
    }

    private boolean typedef(JassLexer lexer) {
        if (!lexer.next_is("type")) {
            return false;
        }
        String type_name = this.id(lexer);
        lexer.expect("extends");
        String base_name = this.id(lexer);

        Type.addType(type_name, base_name);
        return true;
    }

    private boolean globals(JassLexer lexer) {
        if (!lexer.next_is("globals")) {
            return false;
        }
        while (!lexer.next_is("endglobals")) {
            if (lexer.next_is("constant")) {
                String type = this.id(lexer);
                String id = this.id(lexer);
                lexer.expect("=");
                Expression assignExpr = this.expr(lexer);

                Variable var = Variable.createGlobalConst(id, type);
                var.assignExpr = assignExpr;
                instance.globals.put(var.name, var);
                JassChecker.check(instance, var);
            } else {
                Variable var = this.var_declaration(lexer, VariableScope.Global);
                instance.globals.put(var.name, var);
                JassChecker.check(instance, var);
            }
        }
        return true;
    }

    private Variable var_declaration(JassLexer lexer, VariableScope scope) {
        String type = this.id(lexer);

        boolean is_array = lexer.next_is("array");
        String id = this.id(lexer);

        Variable ret;
        if (!is_array) {
            ret = Variable.createVariable(id, type, scope);

            if (lexer.next_is("=")) {
                ret.assignExpr = this.expr(lexer);
            }
        } else {
            ret = Variable.createArray(id, type, scope);
        }
        return ret;
    }

    private boolean func(JassLexer lexer) {
        if (!lexer.hasMore())
            return false;

        boolean is_const = lexer.next_is("constant");

        String funcType = lexer.next_in("native", "function");

        String id = this.id(lexer);
        lexer.expect("takes");
        Variable[] args = this.args_declaration(lexer);
        lexer.expect("returns");
        String typeId = lexer.next_is("nothing") ? "nothing" : this.id(lexer);

        if (funcType.equals("native")) {
            NativeFunctionRef ref = new NativeFunctionRef(id, args, typeId, is_const);
            instance.functions.put(id, ref);
            JassChecker.check(instance, ref);
        } else {
            activeFunctionId = new NativeFunctionRef(id, args, typeId, is_const);
            Variable[] locals = this.local_var_list(lexer);
            List<Statement> statements = new ArrayList<>();

            while (!lexer.next_is("endfunction")) {
                statements.add(this.statement(lexer));
            }

            Statement[] s = new Statement[statements.size()];
            FunctionRef ref = new FunctionRef(id, args, typeId, is_const, locals, statements.toArray(s));
            instance.functions.put(id, ref);
            JassChecker.check(instance, ref);
            activeFunctionId = null;
        }
        return true;
    }

    private Variable[] local_var_list(JassLexer lexer) {
        List<Variable> result = new ArrayList<>();
        while (lexer.next_is("local")) {
            result.add(this.var_declaration(lexer, VariableScope.Local));
        }
        Variable[] v = new Variable[result.size()];
        return result.toArray(v);
    }

    private Statement statement(JassLexer lexer) {
        if (lexer.next_is("set")) {
            return this.set(lexer);
        }
        if (lexer.next_is("call")) {
            return this.call(lexer);
        }
        if (lexer.peek().equals("if")) {
            return this.conditional(lexer);
        }
        if (lexer.next_is("loop")) {
            return this.loop(lexer);
        }

        if (lexer.next_is("exitwhen")) {
            return this.exitwhen(lexer);
        }
        if (lexer.next_is("return")) {
            return this.ret(lexer);
        }

        if (lexer.next_is("debug")) {
            if (lexer.peek_in("set", "call", "if", "loop")) {
                return new DebugStatement(this.statement(lexer));
            }
        }

        return null;
    }

    private Statement set(JassLexer lexer) {
        String id = this.id(lexer);
        Expression index = null;

        if (lexer.next_is("[")) {
            index = this.expr(lexer);
            lexer.expect("]");
        }

        lexer.expect("=");

        if (index == null) {
            return new SetStatement(id, this.expr(lexer));
        } else {
            return new SetArrayStatement(id, index, this.expr(lexer));
        }
    }

    private Statement call(JassLexer lexer) {
        return new FunctionCallStatement((FunctionCallExpression) this.variable(lexer));
    }

    private ConditionalStatement conditional(JassLexer lexer) {
        List<Branch> branches = new ArrayList<>();

        outerLoop:
        while (true) {
            String next = lexer.next();

            switch (next) {
                case "if":
                case "elseif": {
                    Expression expr = this.expr(lexer);
                    lexer.expect("then");
                    branches.add(new Branch(expr, this.statement_list(lexer, "else", "elseif", "endif")));
                    break;
                }
                case "else": {
                    Expression expr = ConstantExpression.constBool(true);
                    branches.add(new Branch(expr, this.statement_list(lexer, "endif")));
                    break;
                }
                case "endif":
                    break outerLoop;
            }
        }

        Branch[] b = new Branch[branches.size()];
        return new ConditionalStatement(branches.toArray(b));
    }

    private LoopStatement loop(JassLexer lexer) {
        Statement[] s = this.statement_list(lexer, "endloop");
        lexer.expect("endloop");
        return new LoopStatement(s);
    }

    private Statement[] statement_list(JassLexer lexer, String... until) {
        List<Statement> statements = new ArrayList<>();

        while (!lexer.peek_in(until)) {
            statements.add(this.statement(lexer));
        }

        Statement[] s = new Statement[statements.size()];
        return statements.toArray(s);
    }

    private ExitWhenStatement exitwhen(JassLexer lexer) {
        return new ExitWhenStatement(this.expr(lexer));
    }

    private ReturnStatement ret(JassLexer lexer) {
        if (activeFunctionId.returnType.equals(Type.NOTHING)) {
            return new ReturnStatement(null, activeFunctionId.name);
        } else {
            return new ReturnStatement(this.expr(lexer), activeFunctionId.name);
        }
    }

    private Variable[] args_declaration(JassLexer lexer) {
        if (lexer.next_is("nothing")) {
            return new Variable[0];
        }
        List<Variable> result = new ArrayList<>();
        do {
            String type = this.id(lexer);
            String id = this.id(lexer);
            result.add(Variable.createArgument(id, type));
        } while (lexer.next_is(","));

        Variable[] v = new Variable[result.size()];
        return result.toArray(v);
    }

    private Expression expr(JassLexer lexer) {
        Expression term = this.and_term(lexer);

        while (lexer.next_is("or")) {
            term = OperationTermExpression.or(term, this.and_term(lexer));
        }

        return term;
    }

    private Expression and_term(JassLexer lexer) {
        Expression term = this.equivalence_relation_term(lexer);

        while (lexer.next_is("and")) {
            term = OperationTermExpression.and(term, this.equivalence_relation_term(lexer));
        }

        return term;
    }

    private Expression equivalence_relation_term(JassLexer lexer) {
        Expression term = this.order_relation_term(lexer);

        while (lexer.match("==|!=")) {
            switch (lexer.next()) {
                case "==":
                    term = OperationTermExpression.eq(term, this.order_relation_term(lexer));
                    break;
                case "!=":
                    term = OperationTermExpression.not(OperationTermExpression.eq(term, this.order_relation_term(lexer)));
                    break;
            }
        }

        return term;
    }

    private Expression order_relation_term(JassLexer lexer) {
        Expression term = this.add_sub_term(lexer);

        while (lexer.match("[><]=?")) {
            switch (lexer.next()) {
                case ">=":
                    term = OperationTermExpression.ge(term, this.add_sub_term(lexer));
                    break;
                case "<=":
                    term = OperationTermExpression.ge(this.add_sub_term(lexer), term);
                    break;
                case ">":
                    term = OperationTermExpression.gt(term, this.add_sub_term(lexer));
                    break;
                case "<":
                    term = OperationTermExpression.gt(this.add_sub_term(lexer), term);
                    break;
            }
        }

        return term;
    }


    private Expression add_sub_term(JassLexer lexer) {
        Expression term = this.unary_term(lexer);

        while (lexer.match("\\+|\\-")) {
            switch (lexer.next()) {
                case "+":
                    term = OperationTermExpression.add(term, this.unary_term(lexer));
                    break;
                case "-":
                    term = OperationTermExpression.add(term, OperationTermExpression.mul(ConstantExpression.constInt(-1), this.unary_term(lexer)));
                    break;
            }
        }

        return term;
    }

    private Expression unary_term(JassLexer lexer) {
        Expression term;

        if (lexer.match("not|\\-|\\+")) {
            switch (lexer.next()) {
                case "not":
                    term = OperationTermExpression.not(this.mul_div_term(lexer));
                    break;
                case "+":
                    term = this.mul_div_term(lexer);
                    break;
                case "-":
                default:
                    term = OperationTermExpression.mul(ConstantExpression.constInt(-1), this.mul_div_term(lexer));
                    break;
            }
        } else {
            term = this.mul_div_term(lexer);
        }

        return term;
    }

    private Expression mul_div_term(JassLexer lexer) {
        Expression term = this.factor(lexer);

        while (lexer.match("\\*|\\/")) {
            switch (lexer.next()) {
                case "*":
                    term = OperationTermExpression.mul(term, this.factor(lexer));
                    break;
                case "/":
                    term = OperationTermExpression.div(term, this.factor(lexer));
                    break;
            }
        }

        return term;
    }

    private Expression factor(JassLexer lexer) {
        Expression ret;
        if ((ret = this.constant(lexer)) != null) {
            return ret;
        }
        if ((ret = this.func_ref(lexer)) != null) {
            return ret;
        }
        if ((ret = this.parenthesis(lexer)) != null) {
            return ret;
        }
        if ((ret = this.variable(lexer)) != null) {
            return ret;
        }

        return null;
    }

    private ConstantExpression constant(JassLexer lexer) {
        ConstantExpression ret;

        if ((ret = this.null_constant(lexer)) != null) {
            return ret;
        }
        if ((ret = this.int_constant(lexer)) != null) {
            return ret;
        }
        if ((ret = this.real_constant(lexer)) != null) {
            return ret;
        }
        if ((ret = this.bool_constant(lexer)) != null) {
            return ret;
        }
        if ((ret = this.string_constant(lexer)) != null) {
            return ret;
        }

        return null;
    }

    private ConstantExpression null_constant(JassLexer lexer) {
        if (lexer.next_is("null")) {
            return ConstantExpression.constNull();
        }
        return null;
    }

    private ConstantExpression int_constant(JassLexer lexer) {
        String s;

        if (lexer.match(DEC_REGEX)) {
            s = lexer.next();
            return ConstantExpression.constInt(Integer.parseInt(s));
        }
        if (lexer.match(OCT_REGEX)) {
            s = lexer.next();
            return ConstantExpression.constInt(Integer.parseInt(s, 8));
        }
        if (lexer.match(HEX_REGEX)) {
            s = lexer.next();
            String r = s.replace("$", "").replace("0X", "").replace("0x", "");
            return ConstantExpression.constInt(Integer.parseInt(r, 16));
        }
        if (lexer.match(FOURCC_REGEX)) {
            s = lexer.next();
            return ConstantExpression.constInt(
                    s.charAt(1) | (s.charAt(2) << 8) | (s.charAt(3) << 16) | (s.charAt(4) << 24)
            );
        }

        return null;
    }

    private ConstantExpression real_constant(JassLexer lexer) {
        if (lexer.match(REAL_REGEX)) {
            return ConstantExpression.constReal(Double.parseDouble(lexer.next()));
        }
        return null;
    }

    private ConstantExpression bool_constant(JassLexer lexer) {
        if (lexer.match("true|false")) {
            return ConstantExpression.constBool(lexer.next().equals("true"));
        }
        return null;
    }

    private ConstantExpression string_constant(JassLexer lexer) {
        if (lexer.peek().charAt(0) == '"') {
            String val = lexer.next();
            val = val.substring(1, val.length() - 2);

            return ConstantExpression.constString(val);
        }
        return null;
    }

    private FunctionReferenceExpression func_ref(JassLexer lexer) {
        if (lexer.next_is("function")) {
            return new FunctionReferenceExpression(this.id(lexer));
        }
        return null;
    }

    private Expression parenthesis(JassLexer lexer) {
        if (lexer.next_is("(")) {
            Expression res = this.expr(lexer);
            lexer.expect(")");
            return res;
        }

        return null;
    }

    private Expression variable(JassLexer lexer) {
        String id = this.id(lexer);
        if (id == null)
            return null;

        if (lexer.next_is("(")) {
            ArrayList<Expression> result = new ArrayList<>();

            while (!lexer.next_is(")")) {
                if (result.size() > 0)
                    lexer.expect(",");

                result.add(this.expr(lexer));
            }

            Expression[] ret = new Expression[result.size()];
            return new FunctionCallExpression(id, result.toArray(ret));
        }

        if (lexer.next_is("[")) {
            Expression index = this.expr(lexer);
            lexer.expect("]");

            return new ArrayReferenceExpression(id, index);
        }

        return new VariableExpression(id);
    }

    private String id(JassLexer lexer) {
        if (lexer.match(ID_REGEX)) {
            if (lexer.peek_in(reserved_words)) {
                return null;
            }
            return lexer.next();
        }
        return null;
    }
}

