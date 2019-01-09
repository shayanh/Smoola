package ast.node.expression;

import ast.Type.ArrayType.ArrayType;
import ast.Visitor;
import ast.node.expression.Value.IntValue;

import java.util.ArrayList;

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
    public ArrayList<String> getGeneratedCode() {
        ArrayList<String> code = new ArrayList<>();
        code.add("arraylength");
        return code;
    }
}
