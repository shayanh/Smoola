package ast.node.declaration;

import ast.Type.PrimitiveType.BooleanType;
import ast.Type.PrimitiveType.IntType;
import ast.Type.PrimitiveType.StringType;
import ast.Type.Type;
import ast.Visitor;
import ast.node.expression.Identifier;

import java.util.ArrayList;

public class VarDeclaration extends Declaration {
    private Identifier identifier;
    private Type type;

    public VarDeclaration(Identifier identifier, Type type) {
        this.identifier = identifier;
        this.type = type;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
    }

    public Type getType() {
        return type;
    }
    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VarDeclaration";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ArrayList<String> getGeneratedCode() {
        ArrayList<String> code = new ArrayList<>();
        if (type.subtype(new IntType()))
            code.add(".field protected " + identifier.getName() + " " + type.getTypeCode() + " = 0");
        else if (type.subtype(new StringType()))
            code.add(".field protected " + identifier.getName() + " " + type.getTypeCode() + " = \"\"");
        else if (type.subtype(new BooleanType()))
            code.add(".field protected " + identifier.getName() + " " + type.getTypeCode() + " = 0");
        else
            code.add(".field protected " + identifier.getName() + " " + type.getTypeCode());
        return code;
    }
}
