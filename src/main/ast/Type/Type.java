package ast.Type;

public abstract class Type {
    public abstract String toString();
    public abstract boolean subtype(Type t);
    public abstract String getTypeCode();
}
