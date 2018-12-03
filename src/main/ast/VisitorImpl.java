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
import symbolTable.*;

import java.util.HashMap;

public class VisitorImpl implements Visitor {

    private Pass pass;
    private boolean hasError;
    private HashMap<String, SymbolTable> classSymbolTable;
    private HashMap<String, ClassDeclaration> classDecMap;

    public VisitorImpl() {
        pass = Pass.First;
        hasError = false;
        classSymbolTable = new HashMap<>();
        classDecMap = new HashMap<>();
    }

    public void setPass(Pass newPass) {
        pass = newPass;
    }

    public boolean hasError() {
        return hasError;
    }

    @Override
    public void visit(Program program) {
        if (pass == Pass.PrintOrder)
            System.out.println(program.toString());
        if (pass == Pass.First) {
            SymbolTable symbolTable = new SymbolTable();
            SymbolTable.push(symbolTable);
        }
        program.getMainClass().accept(this);
        for (ClassDeclaration classDec : program.getClasses()) {
            classDec.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if (pass == Pass.PrintOrder)
            System.out.println(classDeclaration.toString());

        if (pass == Pass.First) {
            SymbolTableClassItem symbolTableClassItem = new SymbolTableClassItem(classDeclaration.getName().getName());
            try {
                SymbolTable.top.put(symbolTableClassItem);
            } catch (ItemAlreadyExistsException e) {
                ErrorLogger.log("Redefinition of class "+classDeclaration.getName().getName(), classDeclaration);
                hasError = true;
            }
            classDecMap.put(classDeclaration.getName().getName(), classDeclaration);
        }

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
        SymbolTable.push(symbolTable);

        if (pass == Pass.Second && classDeclaration.getParentName() != null && classDeclaration.getParentName().getName() != null) {
            String parName = classDeclaration.getParentName().getName();
            ClassDeclaration x = classDecMap.get(parName);
            while (x != null) {
                SymbolTable s = classSymbolTable.get(x.getName().getName());
                for (SymbolTableItem symbolTableItem : s.getItems().values()) {
                    try {
                        SymbolTable.top.put(symbolTableItem);
                    } catch (ItemAlreadyExistsException e) {
                        hasError = true;
                    }
                }
                Identifier parIdentifier = x.getParentName();
                if (parIdentifier == null) {
                    break;
                }
                parName = x.getParentName().getName();
                x = classDecMap.get(parName);
            }
        }

        classDeclaration.getName().accept(this);
        if (classDeclaration.getParentName() != null && classDeclaration.getParentName().getName() != null) {
            classDeclaration.getParentName().accept(this);
        }
        for (VarDeclaration varDec : classDeclaration.getVarDeclarations()) {
            varDec.accept(this);
        }
        for (MethodDeclaration methodDec : classDeclaration.getMethodDeclarations()) {
            methodDec.accept(this);
        }

        SymbolTable.pop();
        if (pass == Pass.First) {
            classSymbolTable.put(classDeclaration.getName().getName(), symbolTable);
        }
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration) {
        if (pass == Pass.PrintOrder)
            System.out.println(methodDeclaration.toString());

        String methodName = methodDeclaration.getName().getName();
        SymbolTableMethodItem symbolTableMethodItem = new SymbolTableMethodItem(methodName, null);
        try {
            SymbolTable.top.put(symbolTableMethodItem);
        } catch (ItemAlreadyExistsException e) {
            if (pass == Pass.Second) {
                ErrorLogger.log("Redefinition of method "+methodName, methodDeclaration);
            }
            hasError = true;
        }

        if (pass != Pass.First) {
            SymbolTable symbolTable = new SymbolTable(SymbolTable.top);
            SymbolTable.push(symbolTable);

            methodDeclaration.getName().accept(this);
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
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if (pass == Pass.PrintOrder)
            System.out.println(varDeclaration.toString());
        varDeclaration.getIdentifier().accept(this);

        String varName = varDeclaration.getIdentifier().getName();
        SymbolTableVariableItem symbolTableVariableItem = new SymbolTableVariableItem(varName, varDeclaration.getType());
        try {
            SymbolTable.top.put(symbolTableVariableItem);
        } catch (ItemAlreadyExistsException e) {
            if (pass == Pass.Second) {
                ErrorLogger.log("Redefinition of variable " + varName, varDeclaration);
            }
            hasError = true;
        }
    }

    @Override
    public void visit(ArrayCall arrayCall) {
        if (pass == Pass.PrintOrder)
            System.out.println(arrayCall.toString());
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        if (pass == Pass.PrintOrder)
            System.out.println(binaryExpression.toString());
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);
    }

    @Override
    public void visit(Identifier identifier) {
        if (pass == Pass.PrintOrder)
            System.out.println(identifier.toString());
    }

    @Override
    public void visit(Length length) {
        if (pass == Pass.PrintOrder)
            System.out.println(length.toString());
        length.getExpression().accept(this);
    }

    @Override
    public void visit(MethodCall methodCall) {
        if (pass == Pass.PrintOrder)
            System.out.println(methodCall.toString());
        methodCall.getInstance().accept(this);
        methodCall.getMethodName().accept(this);
        for (Expression arg : methodCall.getArgs()) {
            arg.accept(this);
        }
    }

    @Override
    public void visit(NewArray newArray) {
        if (pass == Pass.PrintOrder)
            System.out.println(newArray.toString());
        newArray.getExpression().accept(this);

        IntValue intValue = (IntValue) newArray.getExpression();
        if (intValue.getConstant() == 0) {
            if (pass == Pass.Second) {
                ErrorLogger.log("Array length should not be zero or negative", newArray);
            }
            hasError = true;
        }
    }

    @Override
    public void visit(NewClass newClass) {
        if (pass == Pass.PrintOrder)
            System.out.println(newClass.toString());
        newClass.getClassName().accept(this);
    }

    @Override
    public void visit(This instance) {
        if (pass == Pass.PrintOrder)
            System.out.println(instance.toString());
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if (pass == Pass.PrintOrder)
            System.out.println(unaryExpression.toString());
        unaryExpression.getValue().accept(this);
    }

    @Override
    public void visit(BooleanValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
    }

    @Override
    public void visit(IntValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
    }

    @Override
    public void visit(StringValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
    }

    @Override
    public void visit(Assign assign) {
        if (pass == Pass.PrintOrder)
            System.out.println(assign.toString());

        if (assign.getlValue() != null) {
            assign.getlValue().accept(this);
        }
        if (assign.getrValue() != null) {
            assign.getrValue().accept(this);
        }
    }

    @Override
    public void visit(Block block) {
        if (pass == Pass.PrintOrder)
            System.out.println(block.toString());
        for (Statement statement : block.getBody()) {
            statement.accept(this);
        }
    }

    @Override
    public void visit(Conditional conditional) {
        if (pass == Pass.PrintOrder)
            System.out.println(conditional.toString());
        conditional.getExpression().accept(this);
        conditional.getConsequenceBody().accept(this);
        if (conditional.getAlternativeBody() != null) {
            conditional.getAlternativeBody().accept(this);
        }
    }

    @Override
    public void visit(MethodCallInMain methodCallInMain) {
        //TODO: implement appropriate visit functionality
    }

    @Override
    public void visit(While loop) {
        if (pass == Pass.PrintOrder)
            System.out.println(loop.toString());
        loop.getCondition().accept(this);
        loop.getBody().accept(this);
    }

    @Override
    public void visit(Write write) {
        if (pass == Pass.PrintOrder)
            System.out.println(write.toString());
        write.getArg().accept(this);
    }
}
