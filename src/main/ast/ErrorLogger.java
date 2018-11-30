package ast;

import ast.node.Node;

public class ErrorLogger {
    public static void log(String msg, Node node) {
        System.out.println("Line:" + String.valueOf(node.getLine()) + ":" + msg);
    }
}
