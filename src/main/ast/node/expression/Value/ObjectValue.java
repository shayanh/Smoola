package ast.node.expression.Value;

import ast.Type.Type;
import ast.Visitor;

public class ObjectValue extends Value {
    public ObjectValue(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "ObjectValue";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public String getGeneratedCode() {
        return null;
    }
}
