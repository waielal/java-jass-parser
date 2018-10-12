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
    public List<String> tokens = new ArrayList<>();
    public List<Location> locations = new ArrayList<>();

    private int pos = 0;
    private int cnt_peeks = 0;
    private int last_pos = 0;

    public JassLexer(String input) {
        input = JassLexer.remove_comments(input);

        String r = "" +
                //numbers
                "\\n|[1-9][0-9]*"+
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

        int row = 1;
        int col_offset = 0;
        while (m.find()) {
            String s = m.group(0);

            if(s.equals("\n")) {
                col_offset = m.end(0);
                row++;
                continue;
            }

            this.locations.add(new Location(row, m.start(0)-col_offset+1, m.end(0)-m.start(0)));
            this.tokens.add(s);
        }
    }

    private static String remove_comments(String input) {
        String[] a = input.split("\n");

        for (int i = 0; i < a.length; i++) {
            int index = a[i].indexOf("//");
            if (index > -1) {
                a[i] = a[i].substring(0, index) + "\n";
            }
        }

        return Arrays.stream(a).reduce((s, s2) -> s2.trim().equals("") ? s : (s + "\n" + s2)).orElse("");
    }

    String peek() {
        if (this.pos != this.last_pos) {
            this.last_pos = this.pos;
            this.cnt_peeks = 0;
        }
        if (++this.cnt_peeks > 100) {
            Location l = getLocation();
            throw new RuntimeException("stuck at line " +  l.row + ":" + l.start);
        }
        if (!this.hasMore()) {
            throw new RuntimeException("Requesting more tokens when eof reached");
        }
        return this.tokens.get(this.pos);
    }

    String next() {
        //Location l = getLocation();
        //System.out.println("'"+peek()+"' at line " +  l.row + ":" + l.start + " ("+l.length+")");
        return this.tokens.get(this.pos++);
    }

    boolean hasMore() {
        return this.pos < this.tokens.size();
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
            Location l = getLocation();
            throw new RuntimeException("'" + token + "' expected, '" + this.peek() + "' found at line " +  l.row + ":" + l.start);
        } else {
            this.next();
        }
    }

    Location getLocation() {
        return getLocation(0);
    }

    Location getLocation(int offset) {
        return locations.get(this.pos + offset);
    }

    public static class Location {
        public final int row, start, length;
        public Location(int row, int start, int length) {
            this.row = row;
            this.start = start;
            this.length = length;
        }
    }
}