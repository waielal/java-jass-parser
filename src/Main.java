
import jass.JassLexer;
import jass.JassParser;
import jass.ast.JassInstance;

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

    public static String loadFile(String file) throws IOException {
        String path = "C:/Path/To/Folder/";

        return Files.readAllLines(Paths.get(path + file)).stream().reduce((s, s2) -> s + "\n" + s2).get();
    }
}
