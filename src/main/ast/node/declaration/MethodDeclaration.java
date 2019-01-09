package ast.node.declaration;

import ast.Type.PrimitiveType.BooleanType;
import ast.Type.PrimitiveType.IntType;
import ast.Type.Type;
import ast.Visitor;
import ast.node.expression.Expression;
import ast.node.expression.Identifier;
import ast.node.statement.Statement;

import java.util.ArrayList;

public class MethodDeclaration extends Declaration {
    private Expression returnValue;
    private Type returnType;
    private Identifier name;
    private ArrayList<VarDeclaration> args = new ArrayList<>();
    private ArrayList<VarDeclaration> localVars = new ArrayList<>();
    private ArrayList<Statement> body = new ArrayList<>();

    public MethodDeclaration(Identifier name) {
        this.name = name;
    }

    public Expression getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public Identifier getName() {
        return name;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public ArrayList<VarDeclaration> getArgs() {
        return args;
    }

    public void addArg(VarDeclaration arg) {
        this.args.add(arg);
    }

    public ArrayList<Statement> getBody() {
        return body;
    }

    public void addStatement(Statement statement) {
        this.body.add(statement);
    }

    public ArrayList<VarDeclaration> getLocalVars() {
        return localVars;
    }

    public void addLocalVar(VarDeclaration localVar) {
        this.localVars.add(localVar);
    }

    @Override
    public String toString() {
        return "MethodDeclaration";
    }
    @Override
    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public ArrayList<String> getGeneratedCode() {
        ArrayList<String> code = new ArrayList<>();
        StringBuilder dec = new StringBuilder();
        dec.append(".method public ").append(name.getName()).append("(");
        for (VarDeclaration arg : args) {
            dec.append(arg.getType().getTypeCode());
        }
        dec.append(")");
        dec.append(this.getReturnType().getTypeCode());
        code.add(dec.toString());
        code.add(".limit stack " + String.valueOf(localVars.size() + args.size() + 20));
        code.add(".limit locals " + String.valueOf(args.size() + localVars.size() + 1));

        return code;
    }

    public String getReturnCode() {
        String code = "";
        if (returnType.subtype(new IntType()) || returnType.subtype(new BooleanType()))
            code = "ireturn";
        else
            code = "areturn";
        return code;
    }

    public String getInvokationCode() {
        StringBuilder code = new StringBuilder(name.getName() + "(");
        for (VarDeclaration arg : args) {
            code.append(arg.getType().getTypeCode());
        }
        code.append(")");
        code.append(this.getReturnType().getTypeCode());

        return code.toString();
    }
}
