package jass;

import jass.ast.*;
import jass.ast.NativeFunctionRef.FunctionDef;
import jass.ast.expression.*;
import jass.ast.statement.*;
import jass.ast.statement.IfThenElseStatement.Branch;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("SpellCheckingInspection")
public class JassParser {
    private static final String ID_REGEX = "[a-zA-Z_][a-zA-Z_0-9]*";

    private static final String DEC_REGEX = "[1-9][0-9]*";
    private static final String OCT_REGEX = "0[0-9]*";
    private static final String HEX_REGEX = "(\\$[0-9a-fA-F]+|0[xX][0-9a-fA-F]+)";
    private static final String FOURCC_REGEX = "'.{4}'";

    private static final String REAL_REGEX = "([0-9]+\\.[0-9]*|\\.[0-9]+)";

    private static final String[] reserved_words = new String[]{"function", "takes", "returns", "return", "nothing",
            "endfunction", "if", "else", "elseif", "endif", "loop",
            "exitwhen", "globals", "endglobals", "local", "set",
            "call", "constant", "type", "extends", "native", "array"};

    private JassInstance instance;
    private Identifier activeFunctionId;

    @SuppressWarnings("unused")
    public JassInstance parse(JassLexer lexer) {
        instance = new JassInstance();
        this.file(lexer);
        instance.init();
        return instance;
    }

    private void file(JassLexer lexer) {
        //noinspection StatementWithEmptyBody
        while (this.declaration(lexer)) ;

        //noinspection StatementWithEmptyBody
        while (this.func(lexer)) ;
    }

    private boolean func(JassLexer lexer) {
        if (!lexer.hasMore()) {
            return false;
        }
        boolean is_const = lexer.next_is("constant");
        lexer.expect("function");
        FunctionDef data = this.func_declr(lexer);
        activeFunctionId = data.name;
        Variable[] locals = this.local_var_list(lexer);
        List<Statement> statements = new ArrayList<>();

        while (!lexer.next_is("endfunction")) {
            statements.add(this.statement(lexer));
        }

        Statement[] s = new Statement[statements.size()];
        instance.functions.put(data.name.id, new FunctionRef(data, is_const, locals, statements.toArray(s)));
        activeFunctionId = null;
        return true;
    }

    private FunctionDef func_declr(JassLexer lexer) {
        Identifier id = this.id(lexer);
        lexer.expect("takes");
        Variable[] args = this.args_declaration(lexer);
        lexer.expect("returns");
        Identifier typeId;
        if (lexer.next_is("nothing")) {
            typeId = new Identifier("nothing");
        } else {
            typeId = this.id(lexer);
        }
        return new FunctionDef(id, args, typeId);
    }

    private Variable[] local_var_list(JassLexer lexer) {
        List<Variable> result = new ArrayList<>();
        while (lexer.next_is("local")) {
            result.add(this.var_declr(lexer, VariableScope.Local));
        }
        Variable[] v = new Variable[result.size()];
        return result.toArray(v);
    }

    private Statement statement(JassLexer lexer) {
        Statement ret;
        if ((ret = this.set(lexer)) != null) {
            return ret;
        }
        if ((ret = this.call(lexer)) != null) {
            return ret;
        }
        if ((ret = this.ifthenelse(lexer)) != null) {
            return ret;
        }
        if ((ret = this.loop(lexer)) != null) {
            return ret;
        }
        if ((ret = this.exitwhen(lexer)) != null) {
            return ret;
        }
        if ((ret = this.ret(lexer)) != null) {
            return ret;
        }
        if ((ret = this.debug(lexer)) != null) {
            return ret;
        }
        return null;
    }

    private Statement set(JassLexer lexer) {
        if (!lexer.next_is("set")) {
            return null;
        }
        Identifier id = this.id(lexer);
        Expression index = null;
        if (lexer.next_is("[")) {
            index = this.expr(lexer);
            lexer.expect("]");
        }
        lexer.expect("=");
        Expression val = this.expr(lexer);

        if (index == null) {
            return new SetStatement(id, val);
        } else {
            return new SetArrayStatement(id, index, val);
        }
    }

    private Statement call(JassLexer lexer) {
        if (!lexer.next_is("call")) {
            return null;
        }
        Identifier id = this.id(lexer);
        Expression[] args = this.args(lexer);

        return new FunctionCallStatement(id, args);
    }

    private IfThenElseStatement ifthenelse(JassLexer lexer) {
        if (!lexer.next_is("if")) {
            return null;
        }

        List<Branch> branches = new ArrayList<>();
        Expression expr = this.expr(lexer);
        lexer.expect("then");
        List<Statement> statements = new ArrayList<>();
        String[] expected = new String[]{"else", "elseif", "endif"};

        while (true) {
            String next;
            while ((next = lexer.next_in(expected)) == null) {
                statements.add(this.statement(lexer));
            }
            Statement[] s = new Statement[statements.size()];
            branches.add(new Branch(expr, statements.toArray(s)));
            statements.clear();

            //noinspection IfCanBeSwitch
            if (next.equals("endif")) {
                break;
            } else if (next.equals("elseif")) {
                expr = this.expr(lexer);
                lexer.expect("then");
            } else if (next.equals("else")) {
                expr = new ConstantBooleanExpression(true);
                expected = new String[]{"endif"};
            }
        }

        Branch[] b = new Branch[branches.size()];
        return new IfThenElseStatement(branches.toArray(b));
    }

    private LoopStatement loop(JassLexer lexer) {
        if (!lexer.next_is("loop")) {
            return null;
        }
        List<Statement> statements = new ArrayList<>();

        while (!lexer.next_is("endloop")) {
            statements.add(this.statement(lexer));
        }
        Statement[] s = new Statement[statements.size()];
        return new LoopStatement(statements.toArray(s));
    }

    private ExitWhenStatement exitwhen(JassLexer lexer) {
        if (!lexer.next_is("exitwhen")) {
            return null;
        }

        return new ExitWhenStatement(this.expr(lexer));
    }

    private ReturnStatement ret(JassLexer lexer) {
        if (!lexer.next_is("return")) {
            return null;
        }
        return new ReturnStatement(this.expr(lexer), activeFunctionId);
    }

    private DebugStatement debug(JassLexer lexer) {
        if (!lexer.next_is("debug")) {
            return null;
        }
        Statement ret;

        boolean success = (ret = this.set(lexer)) != null
                || (ret = this.call(lexer)) != null
                || (ret = this.ifthenelse(lexer)) != null
                || (ret = this.loop(lexer)) != null;

        if (success) {
            return new DebugStatement(ret);
        }
        return null;
    }

    private Variable[] args_declaration(JassLexer lexer) {
        if (lexer.next_is("nothing")) {
            return new Variable[0];
        }
        List<Variable> result = new ArrayList<>();
        do {
            Identifier type = this.id(lexer);
            Identifier id = this.id(lexer);
            result.add(Variable.createArgument(id, type));
        } while (lexer.next_is(","));

        Variable[] v = new Variable[result.size()];
        return result.toArray(v);
    }

    private boolean declaration(JassLexer lexer) {
        return lexer.hasMore() &&
                (this.typedef(lexer) || this.globals(lexer) || this.native_(lexer));
    }

    private boolean native_(JassLexer lexer) {
        lexer.push();
        try {
            boolean constant = lexer.next_is("constant");
            lexer.expect("native");
            FunctionDef declr = this.func_declr(lexer);

            instance.natives.put(declr.name.id, new NativeFunctionRef(declr, constant));
            lexer.pop();
            return true;
        } catch (Exception e) {
            lexer.rollback();
            return false;
        }
    }

    private boolean typedef(JassLexer lexer) {
        if (!lexer.next_is("type")) {
            return false;
        }
        Identifier type_name = this.id(lexer);
        lexer.expect("extends");
        Identifier base_name = this.id(lexer);

        //noinspection ConstantConditions
        instance.types.put(type_name.id, new Type(type_name, base_name));
        return true;
    }

    private boolean globals(JassLexer lexer) {
        if (!lexer.next_is("globals")) {
            return false;
        }
        while (!lexer.next_is("endglobals")) {
            if (lexer.next_is("constant")) {
                Identifier type = this.id(lexer);
                Identifier id = this.id(lexer);
                lexer.expect("=");
                Expression val = this.expr(lexer);

                Variable var = Variable.createGlobalConst(id, type);
                var.setAssignExpr(val);

                instance.global_const.put(var.name.id, var);
            } else {
                Variable d = this.var_declr(lexer, VariableScope.Global);

                //noinspection ConstantConditions
                instance.globals.put(d.name.id, d);
            }
        }
        return true;
    }

    private Variable var_declr(JassLexer lexer, VariableScope scope) {
        Identifier type;
        if ((type = this.id(lexer)) == null) {
            return null;
        }
        boolean is_array = lexer.next_is("array");
        Identifier id = this.id(lexer);

        Variable ret;
        if (!is_array) {
            if (scope == VariableScope.Local)
                ret = Variable.createLocal(id, type);
            else
                ret = Variable.createGlobal(id, type);

            if (lexer.next_is("=")) {
                ret.setAssignExpr(this.expr(lexer));
            }
        } else {
            if (scope == VariableScope.Local)
                ret = Variable.createLocalArray(id, type);
            else
                ret = Variable.createGlobalArray(id, type);
        }
        return ret;
    }

    private Expression expr(JassLexer lexer) {
        return this.or_term(lexer);
    }

    private Expression or_term(JassLexer lexer) {
        Expression term = this.and_term(lexer);

        while (lexer.match("or")) {
            if (lexer.next().equals("or")) {
                term = BooleanOpTermExpression.and(term, this.and_term(lexer));
            }
        }

        return term;
    }

    private Expression and_term(JassLexer lexer) {
        Expression term = this.eqivalent_term(lexer);

        while (lexer.match("and")) {
            if (lexer.next().equals("and")) {
                term = BooleanOpTermExpression.and(term, this.eqivalent_term(lexer));
            }
        }

        return term;
    }

    private Expression eqivalent_term(JassLexer lexer) {
        Expression term = this.relation_term(lexer);

        while (lexer.match("==|!=")) {
            switch (lexer.next()) {
                case "==":
                    term = BooleanOpTermExpression.eq(term, this.relation_term(lexer));
                    break;
                case "!=":
                    term = BooleanOpTermExpression.neq(term, this.relation_term(lexer));
                    break;
            }
        }

        return term;
    }

    private Expression relation_term(JassLexer lexer) {
        Expression term = this.add_sub_term(lexer);

        while (lexer.match("[><]=?")) {
            switch (lexer.next()) {
                case ">=":
                    term = BooleanOpTermExpression.ge(term, this.add_sub_term(lexer));
                    break;
                case "<=":
                    term = BooleanOpTermExpression.le(term, this.add_sub_term(lexer));
                    break;
                case ">":
                    term = BooleanOpTermExpression.gt(term, this.add_sub_term(lexer));
                    break;
                case "<":
                    term = BooleanOpTermExpression.lt(term, this.add_sub_term(lexer));
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
                    term = NumberOpTermExpression.add(term, this.unary_term(lexer));
                    break;
                case "-":
                    term = NumberOpTermExpression.sub(term, this.unary_term(lexer));
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
                    term = UnaryTermExpression.not(this.mul_div_term(lexer));
                    break;
                case "+":
                    term = UnaryTermExpression.pos(this.mul_div_term(lexer));
                    break;
                case "-":
                default:
                    term = UnaryTermExpression.neg(this.mul_div_term(lexer));
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
                    term = NumberOpTermExpression.mul(term, this.factor(lexer));
                    break;
                case "/":
                    term = NumberOpTermExpression.div(term, this.factor(lexer));
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
        if ((ret = this.array_ref(lexer)) != null) {
            return ret;
        }
        if ((ret = this.func_ref(lexer)) != null) {
            return ret;
        }
        if ((ret = this.func_call(lexer)) != null) {
            return ret;
        }
        if ((ret = this.parenthesis(lexer)) != null) {
            return ret;
        }

        Identifier id;
        if ((id = this.id(lexer)) != null) {
            return new VariableExpression(id);
        }

        return null;
    }

    private Expression constant(JassLexer lexer) {
        if (lexer.next_is("null")) {
            return new ConstantNullExpression();
        }
        Expression ret;
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

    private ConstantIntegerExpression int_constant(JassLexer lexer) {
        String s;

        if (lexer.match(DEC_REGEX)) {
            s = lexer.next();
            return new ConstantIntegerExpression(Integer.parseInt(s));
        }
        if (lexer.match(OCT_REGEX)) {
            s = lexer.next();
            return new ConstantIntegerExpression(Integer.parseInt(s, 8));
        }
        if (lexer.match(HEX_REGEX)) {
            s = lexer.next();
            String r = s.replace("$", "").replace("0X", "").replace("0x", "");
            return new ConstantIntegerExpression(Integer.parseInt(r, 16));
        }
        if (lexer.match(FOURCC_REGEX)) {
            s = lexer.next();
            return new ConstantIntegerExpression(
                    s.charAt(1) | (s.charAt(2) << 8) | (s.charAt(3) << 16) | (s.charAt(4) << 24)
            );
        }

        return null;
    }

    private ConstantRealExpression real_constant(JassLexer lexer) {
        if (lexer.match(REAL_REGEX)) {
            return new ConstantRealExpression(Double.parseDouble(lexer.next()));
        }
        return null;
    }

    private ConstantBooleanExpression bool_constant(JassLexer lexer) {
        if ((lexer.peek().equals("true")) || (lexer.peek().equals("false"))) {
            return new ConstantBooleanExpression(lexer.next().equals("true"));
        }
        return null;
    }

    private ConstantStringExpression string_constant(JassLexer lexer) {
        if (lexer.peek().charAt(0) == '"') {
            String val = lexer.next();
            val = val.substring(1, val.length() - 2);

            return new ConstantStringExpression(val);
        }
        return null;
    }

    private ArrayReferenceExpression array_ref(JassLexer lexer) {
        lexer.push();
        try {
            Identifier id;
            if ((id = this.id(lexer)) == null) {
                return null;
            }
            lexer.expect("[");
            Expression index = this.expr(lexer);
            lexer.expect("]");
            lexer.pop();

            return new ArrayReferenceExpression(id, index);
        } catch (Exception e) {
            lexer.rollback();
            return null;
        }
    }

    private FunctionReferenceExpression func_ref(JassLexer lexer) {
        if (lexer.next_is("function")) {
            return new FunctionReferenceExpression(this.id(lexer));
        }
        return null;
    }

    private FunctionCallExpression func_call(JassLexer lexer) {
        lexer.push();
        try {
            Identifier id;
            if ((id = this.id(lexer)) == null) {
                return null;
            }
            Expression[] args = this.args(lexer);
            lexer.pop();
            return new FunctionCallExpression(id, args);
        } catch (Exception e) {
            lexer.rollback();
            return null;
        }
    }

    private Expression[] args(JassLexer lexer) {
        lexer.expect("(");
        ArrayList<Expression> result = new ArrayList<>();
        while (!lexer.next_is(")")) {
            if (result.size() > 0) {
                lexer.expect(",");
            }
            result.add(this.expr(lexer));
        }
        Expression[] ret = new Expression[result.size()];
        return result.toArray(ret);
    }

    private ParenthesisExpression parenthesis(JassLexer lexer) {
        if (lexer.next_is("(")) {
            Expression res = this.expr(lexer);
            lexer.expect(")");

            return new ParenthesisExpression(res);
        }
        return null;
    }

    private Identifier id(JassLexer lexer) {
        if (lexer.match(ID_REGEX)) {
            if (lexer.peek_in(reserved_words)) {
                return null;
            }

            return new Identifier(lexer.next());
        }
        return null;
    }
}

