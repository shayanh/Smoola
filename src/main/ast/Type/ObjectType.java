package ast.Type;

public class ObjectType extends Type {
    @Override
    public String toString() { return "Object"; }

    @Override
    public boolean subtype(Type t) {
        return t instanceof ObjectType;
    }
}
