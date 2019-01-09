package ast.node.statement;

import ast.Visitor;
import ast.node.Node;

import java.util.ArrayList;

public class Statement extends Node {

    @Override
    public String toString() {
        return "Statement";
    }

    @Override
    public void accept(Visitor visitor) {}

    @Override
    public ArrayList<String> getGeneratedCode() {
        return null;
    }
}
