package jass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Lexer splits the source file into an array of elemental tokens
 */
public class JassLexer {
    public String[] tokens;
    private int pos = 0;
    private Stack<Integer> stack = new Stack<>();

    public static String remove_comments(String input) {
        String[] a = input.split("\n");

        for (int i = 0; i < a.length; i++) {
            int index = a[i].indexOf("//");
            if (index > -1) {
                a[i] = a[i].substring(0, index);
            }
        }

        //noinspection ConstantConditions
        return Arrays.stream(a).reduce((s, s2) -> s2.trim().equals("") ? s : (s + "\n" + s2)).get().trim();
    }

    @SuppressWarnings("EmptyAlternationBranch")
    public JassLexer(String input) {
        input = JassLexer.remove_comments(input);

        //numbers
        String r = "[0-9]+\\.[0-9]*|\\.[0-9]+";
        r += "|[.,;]";

        //fourcc
        r += "|\'.{4}\'";

        //logical operators
        r += "|(?:<>|<=>|>=|<=|==|=|!=|!|<<|>>|<|>|\\|\\||\\||&&|&|-|\\+|\\*(?!/)|/(?!\\*)|\\\\%|~|\\^|\\?)";

        //brackets, parenthesis
        r += "|[\\[\\]()]";

        //empty quotes
        r += "|''(?!')";
        r += "|\"\"(?!\"\")";

        //quoted stuff
        r += "|\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"";


        //words
        r += "|(?:[\\w:@]+(?:(?:\\w+)?)*)";


        Pattern p = Pattern.compile(r);
        Matcher m = p.matcher(input);

        List<String> local = new ArrayList<>();

        while (m.find())
            local.add(m.group(0));


        this.tokens = new String[local.size()];
        local.toArray(this.tokens);
    }

    private int cnt_peeks = 0;
    private int last_pos = 0;

    public String peek() {
        if (this.pos != this.last_pos) {
            this.last_pos = this.pos;
            this.cnt_peeks = 0;
        }
        if (++this.cnt_peeks > 100) {
            throw new RuntimeException("stuck");
        }
        if (!this.hasMore()) {
            throw new RuntimeException("Requesting more tokens when eof reached");
        }
        return this.tokens[this.pos];
    }

    public String next() {
        return this.tokens[this.pos++];
    }

    public boolean hasMore() {
        return this.pos < this.tokens.length;
    }

    /**
     * Checks if next token is token, and if so EATS IT!!
     */
    public boolean next_is(String token) {
        if (this.peek().equals(token)) {
            this.next();
            return true;
        }
        return false;
    }

    public String next_in(String[] array) {
        if (Arrays.asList(array).contains(this.peek())) {
            return this.next();
        }
        return null;
    }

    public boolean peek_in(String[] array) {
        String peek = this.peek();
        return Arrays.stream(array).anyMatch(s -> s.equals(peek));
    }

    public boolean match(String regex) {
        return this.peek().matches("^(" + regex + ")$");
    }

    public void expect(String token) {
        if (!this.peek().equals(token)) {
            throw new RuntimeException(token + " expected, '" + this.peek() + "' found");
        } else {
            this.next();
        }
    }

    public void push() {
        this.stack.push(this.pos);
    }

    public void pop() {
        this.stack.pop();
    }

    public void rollback() {
        this.pos = this.stack.pop();
    }
}