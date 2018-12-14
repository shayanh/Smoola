package ast.Type.PrimitiveType;

import ast.Type.ObjectType;
import ast.Type.Type;

public class IntType extends Type {
    @Override
    public String toString() {
        return "int";
    }

    @Override
    public boolean subtype(Type t) {
        return (t instanceof ObjectType) || (t instanceof IntType);
    }
}
