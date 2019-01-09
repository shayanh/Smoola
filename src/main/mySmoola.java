import java.io.IOException;

import ast.GeneratorVisitorImpl;
import ast.Pass;
import ast.Visitor;
import ast.VisitorImpl;
import ast.node.Program;
import org.antlr.v4.runtime.*;
import symbolTable.SymbolTable;

public class mySmoola {
    public static void main(String[] args) throws IOException {
        CharStream reader = CharStreams.fromFileName("tests/phase4/test1.sml");
//        CharStream reader = CharStreams.fromFileName(args[0]);
        SmoolaLexer lexer = new SmoolaLexer(reader);   // SmoolaLexer in your project
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        SmoolaParser parser = new SmoolaParser(tokens);   // SmoolaParser in your project
        Program prog = parser.program().prog; // program is the name of the start rule

        VisitorImpl v = new VisitorImpl();
        prog.accept(v);
        v.setPass(Pass.Second);
        prog.accept(v);
        System.out.println(SymbolTable.getStackSize());
        if (!v.hasError()) {
            v.setPass(Pass.Third);
            prog.accept(v);
        }
//        Visitor codeGenerator = new GeneratorVisitorImpl();
//        ((GeneratorVisitorImpl) codeGenerator).setClassSymbolTable(v.getClassSymbolTable());
//        ((GeneratorVisitorImpl) codeGenerator).setClassDecMap(v.getClassDecMap());
//        prog.accept(codeGenerator);
    }
}
