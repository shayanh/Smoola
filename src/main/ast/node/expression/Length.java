package ast.node.expression;

import ast.Visitor;
import ast.node.expression.Value.IntValue;

public class Length extends Expression {
    private Expression expression;

    public Length(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "Length";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getGeneratedCode() {
        IntValue value = (IntValue) expression;
        return "ldc " + String.valueOf(value.getConstant());
    }
}
