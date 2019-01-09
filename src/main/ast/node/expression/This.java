package ast.node.expression;

import ast.Visitor;

import java.util.ArrayList;

public class This extends Expression {
    @Override
    public String toString() {
        return "This";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ArrayList<String> getGeneratedCode() {
        ArrayList<String> code = new ArrayList<>();
        code.add("aload_0");
        return code;
    }
}
