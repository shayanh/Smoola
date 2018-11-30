package ast.node;

import ast.Visitor;

public abstract class Node {
    private int line;

    public int getLine() { return line; }
    public void setLine(int l) { line = l; }

    public void accept(Visitor visitor) {}
}
