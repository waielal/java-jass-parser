import jass.JassEvaluator;
import jass.JassLexer;
import jass.JassParser;
import jass.ast.JassInstance;
import jass.ast.declaration.NativeFunctionRef;
import jass.ast.declaration.NativeFunctionRef.Argument;
import jass.ast.declaration.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
        test();
        try {
            long start = System.currentTimeMillis();

            String content = loadFile("common.j") + "\n" + loadFile("common.ai");

            System.out.println(System.currentTimeMillis() - start);
            start = System.currentTimeMillis();

            JassLexer lexer = new JassLexer(content);
            JassParser parser = new JassParser();

            System.out.println(System.currentTimeMillis() - start);
            start = System.currentTimeMillis();

            JassInstance instance = parser.parse(lexer);

            System.out.println(System.currentTimeMillis() - start);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        String content =
            "function B takes real a returns real\n" +
                "if true then\n"+
                    "return a * a\n" +
                "else\n"+
                    "return a\n" +
                "endif\n"+
            "endfunction\n"+

            "function A takes integer a, real b returns real\n" +
                "local real c = B(a) + B(b)\n" +
                "local integer i\n" +
                "set i = 0\n" +
                "loop set c = c + c\n" +
                    "set i = i + 1\n" +
                    "exitwhen i > 1\n" +
                "endloop\n" +
                "return c\n" +
            "endfunction";

        JassLexer lexer = new JassLexer(content);
        JassParser parser = new JassParser();
        JassInstance instance = parser.parse(lexer);

        NativeFunctionRef refA = instance.functions.get("A");
        Object res = JassEvaluator.run(refA, new Argument(Type.INTEGER, 1), new Argument(Type.INTEGER, 1));
        System.out.println("(" + refA.returnType + ") " + res);
    }

    public static String loadFile(String file) throws IOException {
        String path = "C:/Path/To/Folder/";

        return Files.readAllLines(Paths.get(path + file)).stream().reduce((s, s2) -> s + "\n" + s2).get();
    }
}
