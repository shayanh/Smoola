package ast.node.declaration;

import ast.Type.NoType;
import ast.Type.Type;
import ast.Visitor;
import ast.node.expression.Identifier;

import java.util.ArrayList;

public class ClassDeclaration extends Declaration{
    private Identifier name;
    private Identifier parentName;
    private ClassDeclaration parentClass;
    private ArrayList<VarDeclaration> varDeclarations = new ArrayList<>();
    private ArrayList<MethodDeclaration> methodDeclarations = new ArrayList<>();

    public ClassDeclaration(Identifier name, Identifier parentName) {
        this.name = name;
        this.parentName = parentName;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public Identifier getParentName() {
        return parentName;
    }

    public void setParentName(Identifier parentName) {
        this.parentName = parentName;
    }

    public ArrayList<VarDeclaration> getVarDeclarations() {
        return varDeclarations;
    }

    public void addVarDeclaration(VarDeclaration varDeclaration) {
        this.varDeclarations.add(varDeclaration);
    }

    public ArrayList<MethodDeclaration> getMethodDeclarations() {
        return methodDeclarations;
    }

    public void addMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclarations.add(methodDeclaration);
    }

    public boolean hasParent() {
        return this.getParentName() != null && this.getParentName().getName() != null;
    }

    @Override
    public String toString() {
        return "ClassDeclaration";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ArrayList<String> getGeneratedCode() {
        ArrayList<String> code = new ArrayList<>();
        code.add(".class public " + name.getName());

        if (!this.hasParent()) {
            code.add(".super java/lang/Object");
        }
        else {
            code.add(".super " + parentName.getName());
        }

        return code;
    }

    public ArrayList<String> getInitMethodDecCode(ArrayList<String> initCode) {
        String par;
        ArrayList<String> code = new ArrayList<>();

        if (!this.hasParent()) {
            par = "java/lang/Object";
        }
        else {
            par = parentName.getName();
        }

        code.add(".method public <init>()V");
        code.add(".limit stack 2");
        code.add("aload_0");
        code.add("invokespecial " + par + "/<init>()V");
        code.addAll(initCode);
        code.add("return");
        code.add(".end method");

        return code;
    }

    public String getInitMethod() {
        return "invokespecial " + name.getName() + "/<init>()V";
    }

    public ClassDeclaration getParentClass() {
        return parentClass;
    }

    public void setParentClass(ClassDeclaration parentClass) {
        this.parentClass = parentClass;
    }

    public boolean containsMethod(Identifier methodName) {
        for (MethodDeclaration method : methodDeclarations) {
            if (method.getName().getName().equals(methodName.getName())) {
                return true;
            }
        }

        return parentClass != null && parentClass.containsMethod(methodName);

    }

    public Type getMethodType(Identifier methodName) {
        for (MethodDeclaration method : methodDeclarations) {
            if (method.getName().getName().equals(methodName.getName())) {
                return method.getReturnType();
            }
        }

        if (parentClass != null)
            return parentClass.getMethodType(methodName);

        return new NoType();
    }

    public MethodDeclaration getMethodDeclaration(Identifier methodName) {
        for (MethodDeclaration method : methodDeclarations) {
            if (method.getName().getName().equals(methodName.getName())) {
                return method;
            }
        }

        if (parentClass != null)
            return parentClass.getMethodDeclaration(methodName);

        return null;
    }

}
