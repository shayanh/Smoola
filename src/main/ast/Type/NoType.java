package ast.Type;

public class NoType extends Type {
    @Override
    public String toString() {
        return "NoType";
    }

    @Override
    public boolean subtype(Type t) {
        return true;
    }

    @Override
    public String getTypeCode() {
        return null;
    }
}
