package ast.node.expression.Value;

import ast.Type.Type;
import ast.Visitor;

import java.util.ArrayList;

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
    public ArrayList<String> getGeneratedCode() {
        return null;
    }
}
