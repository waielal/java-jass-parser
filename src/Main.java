import jass.JassLexer;
import jass.JassParser;
import jass.ast.FunctionRef;
import jass.ast.JassInstance;
import jass.ast.NativeFunctionRef.Argument;
import jass.ast.Type;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) {
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
            instance.init();

            System.out.println(System.currentTimeMillis() - start);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void test() {
        String content =
            "function A takes integer a, real b returns real " +
                "local real c = a * a + b * b " +
                "local integer i " +
                "set i = 0 " +
                "loop set c = c + c " +
                    "set i = i + 1 " +
                    "exitwhen i > 1 " +
                "endloop " +
                "return c " +
            "endfunction";

        JassLexer lexer = new JassLexer(content);
        JassParser parser = new JassParser();
        JassInstance instance = parser.parse(lexer);

        FunctionRef refA = instance.functions.get("A");
        Object res = refA.eval(new Argument(Type.INTEGER, 1), new Argument(Type.INTEGER, 1));

        System.out.println("(" + refA.returnType() + ") " + res);
    }

    public static String loadFile(String file) throws IOException {
        String path = "C:/Path/To/Folder/";

        return Files.readAllLines(Paths.get(path + file)).stream().reduce((s, s2) -> s + "\n" + s2).get();
    }
}
