package ast.node;

import ast.Visitor;

import java.util.ArrayList;

public abstract class Node {
    private int line;

    public int getLine() { return line; }
    public void setLine(int l) { line = l; }

    public void accept(Visitor visitor) {}
    public abstract ArrayList<String> getGeneratedCode();
}
