package ast;

import ast.node.Program;
import ast.node.declaration.ClassDeclaration;
import ast.node.declaration.MethodDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.Value.BooleanValue;
import ast.node.expression.Value.IntValue;
import ast.node.expression.Value.StringValue;
import ast.node.statement.*;

public class VisitorImpl implements Visitor {

    @Override
    public void visit(Program program) {
        System.out.println(program.toString());
        program.getMainClass().accept(this);
        for (ClassDeclaration classDec: program.getClasses()) {
            classDec.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        System.out.println(classDeclaration.toString());

        classDeclaration.getName().accept(this);

        for (VarDeclaration varDec: classDeclaration.getVarDeclarations()) {
            varDec.accept(this);
        }
        for (MethodDeclaration methodDec: classDeclaration.getMethodDeclarations()) {
            methodDec.accept(this);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        System.out.println(methodDeclaration.toString());

        methodDeclaration.getName().accept(this);

        for (VarDeclaration arg: methodDeclaration.getArgs()) {
            arg.accept(this);
        }
        for (VarDeclaration localVar: methodDeclaration.getLocalVars()) {
            localVar.accept(this);
        }
        for (Statement statement: methodDeclaration.getBody()) {
            statement.accept(this);
        }

        methodDeclaration.getReturnValue().accept(this);
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        System.out.println(varDeclaration.toString());
        varDeclaration.getIdentifier().accept(this);
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        System.out.println(arrayCall.toString());
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        System.out.println(binaryExpression.toString());
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
    }

    @Override
    public void visit(Identifier identifier) {
        System.out.println(identifier.toString());
    }

    @Override
    public void visit(Length length) {
        System.out.println(length.toString());
        length.getExpression().accept(this);
    }

    @Override
    public void visit(MethodCall methodCall) {
        System.out.println(methodCall.toString());
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg: methodCall.getArgs()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(NewArray newArray) {
        System.out.println(newArray.toString());
        newArray.getExpression().accept(this);
    }

    @Override
    public void visit(NewClass newClass) {
        System.out.println(newClass.toString());
        newClass.getClassName().accept(this);
    }

    @Override
    public void visit(This instance) {
        System.out.println(instance.toString());
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        System.out.println(unaryExpression.toString());
        unaryExpression.getValue().accept(this);
    }

    @Override
    public void visit(BooleanValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(IntValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(StringValue value) {
        System.out.println(value.toString());
    }

    @Override
    public void visit(Assign assign) {
        System.out.println(assign.toString());
        assign.getlValue().accept(this);
        assign.getrValue().accept(this);
    }

    @Override
    public void visit(Block block) {
        System.out.println(block.toString());
        for (Statement statement: block.getBody()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(Conditional conditional) {
        System.out.println(conditional.toString());
        conditional.getExpression().accept(this);
        conditional.getConsequenceBody().accept(this);
        if (conditional.getAlternativeBody() != null) {
            conditional.getAlternativeBody().accept(this);
        }
    }

    @Override
    public void visit(While loop) {
        System.out.println(loop.toString());
        loop.getCondition().accept(this);
        loop.getBody().accept(this);
    }

    @Override
    public void visit(Write write) {
        System.out.println(write.toString());
        write.getArg().accept(this);
    }
}
