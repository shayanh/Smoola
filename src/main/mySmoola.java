import java.io.IOException;

import ast.VisitorImpl;
import ast.node.Program;
import org.antlr.v4.runtime.*;

public class mySmoola {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName("doc-sample4.sml");
        SmoolaLexer lexer = new SmoolaLexer(reader);   // SmoolaLexer in your project
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SmoolaParser parser = new SmoolaParser(tokens);   // SmoolaParser in your project
        Program prog = parser.program().prog; // program is the name of the start rule

        VisitorImpl v = new VisitorImpl();
        prog.accept(v);
    }
}