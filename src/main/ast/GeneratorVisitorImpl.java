package ast;

import ast.Type.PrimitiveType.BooleanType;
import ast.Type.PrimitiveType.IntType;
import ast.Type.PrimitiveType.StringType;
import ast.node.Program;
import ast.node.declaration.ClassDeclaration;
import ast.node.declaration.MethodDeclaration;
import ast.node.declaration.VarDeclaration;
import ast.node.expression.*;
import ast.node.expression.Value.BooleanValue;
import ast.node.expression.Value.IntValue;
import ast.node.expression.Value.ObjectValue;
import ast.node.expression.Value.StringValue;
import ast.node.statement.*;
import symbolTable.ItemAlreadyExistsException;
import symbolTable.SymbolTable;
import symbolTable.SymbolTableMethodItem;
import symbolTable.SymbolTableVariableItem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

public class GeneratorVisitorImpl implements Visitor {

    private HashMap<String, SymbolTable> classSymbolTable;
    private ArrayList<String> generatedCode = new ArrayList<>();
    private boolean classVar = false;

    public void writeToFile(String name) {
        try {
            Path file = Paths.get("./generated/" + name + ".j");
            Files.write(file, generatedCode);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void visit(Program program) {
        String mainClass = program.getMainClass().getName().getName();
        starterClassCodeGenerator(mainClass);
        writeToFile("GeneratedMainClass");

        generatedCode = new ArrayList<>();
        program.getMainClass().accept(this);
        writeToFile(mainClass);

        for (ClassDeclaration classDec : program.getClasses()) {
            generatedCode = new ArrayList<>();
            classDec.accept(this);
            classDec.accept(this);
            writeToFile(classDec.getName().getName());
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        SymbolTable.push(symbolTable);

        generatedCode.add(classDeclaration.getGeneratedCode());

        classVar = true;
        for (VarDeclaration varDec : classDeclaration.getVarDeclarations()) {
            varDec.accept(this);
        }
        classVar = false;

        for (MethodDeclaration methodDec : classDeclaration.getMethodDeclarations()) {
            methodDec.accept(this);
        }

        SymbolTable.pop();
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        String methodName = methodDeclaration.getName().getName();
        SymbolTableMethodItem symbolTableMethodItem = new SymbolTableMethodItem(methodName, null);

        try {
            SymbolTable.top.put(symbolTableMethodItem);
        } catch (ItemAlreadyExistsException e) {
            e.printStackTrace();
        }

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        SymbolTable.push(symbolTable);

        for (VarDeclaration arg : methodDeclaration.getArgs()) {
            arg.accept(this);
        }
        for (VarDeclaration localVar : methodDeclaration.getLocalVars()) {
            localVar.accept(this);
        }
        for (Statement statement : methodDeclaration.getBody()) {
            statement.accept(this);
        }
        methodDeclaration.getReturnValue().accept(this);

        SymbolTable.pop();
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        SymbolTableVariableItem symbolTableVariableItem = new SymbolTableVariableItem(varDeclaration.getIdentifier().getName(), varDeclaration.getType());
        try {
            SymbolTable.top.put(symbolTableVariableItem);
        } catch (ItemAlreadyExistsException e) {
            e.printStackTrace();
        }

        if (!classVar) {
            if (varDeclaration.getType().subtype(new BooleanType())) {
                generatedCode.add("iconst_0\n" +
                        "istore " + String.valueOf(symbolTableVariableItem.getIndex()) + "\n");
            }
            else if (varDeclaration.getType().subtype(new IntType())) {
                generatedCode.add("iconst_0\n" +
                        "istore " + String.valueOf(symbolTableVariableItem.getIndex()) + "\n");
            }
            else if (varDeclaration.getType().subtype(new StringType())) {
                generatedCode.add("ldc \"\"\n" +
                        "astore " + String.valueOf(symbolTableVariableItem.getIndex()) + "\n");
            }
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
    }

    @Override
    public void visit(Identifier identifier) {

    }

    @Override
    public void visit(Length length) {
        length.getExpression().accept(this);
    }

    @Override
    public void visit(MethodCall methodCall) {
        methodCall.getInstance().accept(this);

        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(NewArray newArray) {
        newArray.getExpression().accept(this);
    }

    @Override
    public void visit(NewClass newClass) {

    }

    @Override
    public void visit(This instance) {

    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        unaryExpression.getValue().accept(this);
    }

    @Override
    public void visit(BooleanValue value) {

    }

    @Override
    public void visit(IntValue value) {

    }

    @Override
    public void visit(StringValue value) {

    }

    @Override
    public void visit(ObjectValue value) {

    }

    @Override
    public void visit(Assign assign) {

    }

    @Override
    public void visit(Block block) {
        for (Statement statement : block.getBody()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(Conditional conditional) {
        conditional.getExpression().accept(this);
        conditional.getConsequenceBody().accept(this);
        if (conditional.getAlternativeBody() != null) {
            conditional.getAlternativeBody().accept(this);
        }
    }

    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        methodCallInMain.getInstance().accept(this);
    }

    @Override
    public void visit(While loop) {
        loop.getCondition().accept(this);
        loop.getBody().accept(this);
    }

    @Override
    public void visit(Write write) {
        write.getArg().accept(this);
    }

    public void starterClassCodeGenerator(String mainClass) {
        String code = ".class public JavaMain\n" +
                ".super java/lang/Object\n" +
                ".method public <init>()V\n" +
                "aload_0 ; push this\n" +
                "invokespecial java/lang/Object/<init>()V ; call super\n" +
                "return\n" +
                ".end method\n" +
                ".method public static main([Ljava/lang/String;)V\n" +
                ".limit stack 2\n" +
                "new " + mainClass + " \n" +
                "dup\n" +
                "invokespecial " + mainClass + "/<init>()V\n" +
                "invokevirtual " + mainClass + "/main()I\n" +
                "return\n" +
                ".end method\n";
        generatedCode.add(code);
    }
}
