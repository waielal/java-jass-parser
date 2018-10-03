package jass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A Lexer splits the source file into an array of elemental tokens
 */
public class JassLexer {
    public String[] tokens;
    private int pos = 0;
    private int cnt_peeks = 0;
    private int last_pos = 0;

    public JassLexer(String input) {
        input = JassLexer.remove_comments(input);

        String r = "" +
                //numbers
                "[1-9][0-9]*"+
                "|(\\$|0[xX])[0-9a-fA-F]+" +
                "|[0-9]+\\.[0-9]*|\\.[0-9]+" +
                "|0[0-7]*"+
                "|\'.{4}\'" +

                //punctuations
                "|[.,;]" +

                //logical operators
                "|[+\\-*/]|[><]=?|==|=|!=" +

                //brackets, parenthesis
                "|[\\[\\]()]" +

                //empty quotes
                "|\"\"(?!\"\")" +

                //quoted stuff
                "|\"[^\"\\\\]*(?:\\\\.[^\"\\\\]*)*\"" +

                //words
                "|[a-zA-Z]([a-zA-Z0-9_]*[a-zA-Z0-9])?";


        Pattern p = Pattern.compile(r);
        Matcher m = p.matcher(input);

        List<String> local = new ArrayList<>();

        while (m.find())
            local.add(m.group(0));


        this.tokens = new String[local.size()];
        local.toArray(this.tokens);
    }

    private static String remove_comments(String input) {
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

    String peek() {
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

    String next() {
        return this.tokens[this.pos++];
    }

    boolean hasMore() {
        return this.pos < this.tokens.length;
    }

    /**
     * Checks if next token is token, and if so EATS IT!!
     */
    boolean next_is(String token) {
        if (this.peek().equals(token)) {
            this.next();
            return true;
        }
        return false;
    }

    /**
     * Checks if next token is in the array, and if so EATS IT and returns it!!
     */
    String next_in(String... array) {
        if (Arrays.asList(array).contains(this.peek())) {
            return this.next();
        }
        return null;
    }

    boolean peek_in(String... array) {
        String peek = this.peek();
        return Arrays.stream(array).anyMatch(s -> s.equals(peek));
    }

    boolean match(String regex) {
        return this.peek().matches("^(" + regex + ")$");
    }

    void expect(String token) {
        if (!this.peek().equals(token)) {
            throw new RuntimeException("'" + token + "' expected, '" + this.peek() + "' found");
        } else {
            this.next();
        }
    }
}