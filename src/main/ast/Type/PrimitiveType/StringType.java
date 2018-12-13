package ast.Type.PrimitiveType;

import ast.Type.ObjectType;
import ast.Type.Type;

public class StringType extends Type {

    @Override
    public String toString() {
        return "string";
    }

    @Override
    public boolean subtype(Type t) {
        return t instanceof ObjectType;
    }
}
