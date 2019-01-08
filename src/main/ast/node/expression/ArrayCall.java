package ast.node.expression;

import ast.Visitor;

public class ArrayCall extends Expression {
    private Expression instance;
    private Expression index;

    public ArrayCall(Expression instance, Expression index) {
        this.instance = instance;
        this.index = index;
    }

    public Expression getInstance() {
        return instance;
    }

    public void setInstance(Expression instance) {
        this.instance = instance;
    }

    public Expression getIndex() {
        return index;
    }

    public void setIndex(Expression index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "ArrayCall";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getGeneratedCode() {
        String code = instance.getGeneratedCode();
        code += index.getGeneratedCode();
        code += "iaload";
        return code;
    }
}
