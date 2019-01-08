package ast.node.expression;

import ast.Visitor;

public class UnaryExpression extends Expression {

    private UnaryOperator unaryOperator;
    private Expression value;

    public UnaryExpression(UnaryOperator unaryOperator, Expression value) {
        this.unaryOperator = unaryOperator;
        this.value = value;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public UnaryOperator getUnaryOperator() {
        return unaryOperator;
    }

    public void setUnaryOperator(UnaryOperator unaryOperator) {
        this.unaryOperator = unaryOperator;
    }

    @Override
    public String toString() {
        return "UnaryExpression " + unaryOperator.name();
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getGeneratedCode() {
        String code = "";
        if (unaryOperator == UnaryOperator.minus) {
            code += value.getGeneratedCode();
            code += "ineg";
        }
        else {
            code += value.getGeneratedCode();
            code += "ifne 4\n" +
                    "iconst_1\n" +
                    "goto 2\n" +
                    "iconst_0";
        }
        return code;
    }
}

