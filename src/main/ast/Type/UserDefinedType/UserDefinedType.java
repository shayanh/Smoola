package ast.Type.UserDefinedType;

import ast.Type.ObjectType;
import ast.Type.Type;
import ast.node.declaration.ClassDeclaration;
import ast.node.expression.Identifier;

public class UserDefinedType extends Type {
    private ClassDeclaration classDeclaration;

    public ClassDeclaration getClassDeclaration() {
        return classDeclaration;
    }

    public void setClassDeclaration(ClassDeclaration classDeclaration) {
        this.classDeclaration = classDeclaration;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    private Identifier name;

    @Override
    public String toString() {
        return classDeclaration.getName().getName();
    }

    @Override
    public boolean subtype(Type t) {
        if (t instanceof ObjectType) {
            return true;
        }
        if (!(t instanceof UserDefinedType)) {
            return false;
        }

        ClassDeclaration c = classDeclaration;
        while (c != null) {
            if (c.getName() == ((UserDefinedType) t).getClassDeclaration().getName()) {
                return true;
            }
            c = c.getParentClass();
        }
        return true;
    }
}
