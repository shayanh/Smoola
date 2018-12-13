package ast.Type.PrimitiveType;

import ast.Type.ObjectType;
import ast.Type.Type;

public class BooleanType extends Type {

    @Override
    public String toString() {
        return "bool";
    }

    @Override
    public boolean subtype(Type t) {
        return t instanceof ObjectType;
    }
}
