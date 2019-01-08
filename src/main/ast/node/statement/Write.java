package ast.node.statement;

import ast.Visitor;
import ast.node.expression.Expression;

public class Write extends Statement {
    private Expression arg;

    public Write(Expression arg) {
        this.arg = arg;
    }

    public Expression getArg() {
        return arg;
    }

    public void setArg(Expression arg) {
        this.arg = arg;
    }

    @Override
    public String toString() {
        return "Write";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    public String getInvokeCode() {
        return "invokevirtual java/io/PrintStream/println(I)V";
    }

    public String getPrintStream() {
        return "getstatic java/lang/System/out Ljava/io/PrintStream;";
    }

}
