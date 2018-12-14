package ast;

import ast.Type.ArrayType.ArrayType;
import ast.Type.NoType;
import ast.Type.PrimitiveType.BooleanType;
import ast.Type.PrimitiveType.IntType;
import ast.Type.PrimitiveType.StringType;
import ast.Type.Type;
import ast.Type.UserDefinedType.UserDefinedType;
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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class VisitorImpl implements Visitor {

    private Pass pass;
    private boolean hasError;
    private HashMap<String, SymbolTable> classSymbolTable;
    private HashMap<String, ClassDeclaration> classDecMap;

    private List<String> keywords = Arrays.asList("boolean", "string", "int", "class", "def", "then", "if", "writeln",
            "extends", "var", "this", "false", "true", "while", "else", "return", "new");

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
        if (pass == Pass.First) {
            SymbolTable symbolTable = new SymbolTable();
            SymbolTable.push(symbolTable);

            SymbolTableClassItem objectClassItem = new SymbolTableClassItem("Object");
            try {
                SymbolTable.top.put(objectClassItem);
            } catch (ItemAlreadyExistsException e) {
                e.printStackTrace();
            }
            ClassDeclaration objectClassDec = new ClassDeclaration(new Identifier("Object"), new Identifier(""));
            classDecMap.put("Object", objectClassDec);
            classSymbolTable.put("Object", new SymbolTable());
        }
        program.getMainClass().accept(this);
        for (ClassDeclaration classDec : program.getClasses()) {
            classDec.accept(this);
        }
    }

    @Override
    public void visit(ClassDeclaration classDeclaration) {
        if (pass == Pass.First) {
            SymbolTableClassItem symbolTableClassItem = new SymbolTableClassItem(classDeclaration.getName().getName());
            try {
                SymbolTable.top.put(symbolTableClassItem);
            } catch (ItemAlreadyExistsException e) {
                ErrorLogger.log("Redefinition of class "+ classDeclaration.getName().getName(), classDeclaration);
                hasError = true;
                classDeclaration.setName(new Identifier("Temporary_" + classDeclaration.getName().getName() + "_1"));
            }
            classDecMap.put(classDeclaration.getName().getName(), classDeclaration);
        }

        SymbolTable symbolTable = new SymbolTable(SymbolTable.top);

        UserDefinedType typ = new UserDefinedType();
        typ.setName(classDeclaration.getName());
        typ.setClassDeclaration(classDeclaration);
        SymbolTableVariableItem symbolTableVariableItem = new SymbolTableVariableItem("this", typ);
        try {
            symbolTable.put(symbolTableVariableItem);
        }
        catch (ItemAlreadyExistsException e) {
            System.out.println("Can't put this");
        }

        SymbolTable.push(symbolTable);

        if ((pass == Pass.Second || pass == Pass.Third) && classDeclaration.hasParent()) {
            String parName = classDeclaration.getParentName().getName();
            ClassDeclaration x = classDecMap.get(parName);
            classDeclaration.setParentClass(x);
            while (x != null) {
                SymbolTable s = classSymbolTable.get(x.getName().getName());
                for (SymbolTableItem symbolTableItem : s.getItems().values()) {
                    if (symbolTableItem.getKey().equals("this"))
                        continue;
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

        if (classDeclaration.hasParent() && pass == Pass.Third) {
            if (classDecMap.get(classDeclaration.getParentName().getName()) == null) {
                ErrorLogger.log("parent class " + classDeclaration.getParentName().getName() +
                        " is not defined", classDeclaration);
            }
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
        String methodName = methodDeclaration.getName().getName();
        SymbolTableMethodItem symbolTableMethodItem = new SymbolTableMethodItem(methodName, null);
        try {
            SymbolTable.top.put(symbolTableMethodItem);
        } catch (ItemAlreadyExistsException e) {
            if (pass == Pass.Second) {
                ErrorLogger.log("Redefinition of method "+ methodName, methodDeclaration);
            }
            hasError = true;
        }

        if (pass == Pass.First) {
            return;
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

        if (pass == Pass.Third) {
            if (methodDeclaration.getReturnType() instanceof UserDefinedType) {
                UserDefinedType typ = new UserDefinedType();
                Identifier className = ((UserDefinedType) methodDeclaration.getReturnType()).getName();
                ClassDeclaration classDec = classDecMap.get(className.getName());
                if (classDec == null) {
                    ErrorLogger.log("method return type " + className.getName() + " is not valid", methodDeclaration);
                    classDec = new ClassDeclaration(className, null);
                }
                typ.setName(className);
                typ.setClassDeclaration(classDec);
                methodDeclaration.setReturnType(typ);
            }
            if (!methodDeclaration.getReturnValue().getType().subtype(methodDeclaration.getReturnType())) {
                String msg = methodName + " return type must be " + methodDeclaration.getReturnType().toString();
                ErrorLogger.log(msg, methodDeclaration.getReturnValue());
                hasError = true;
            }
        }

        SymbolTable.pop();
    }

    @Override
    public void visit(VarDeclaration varDeclaration) {
        if (pass == Pass.Second || pass == Pass.Third) {
            if (varDeclaration.getType() instanceof UserDefinedType) {
                UserDefinedType typ = new UserDefinedType();
                Identifier className = ((UserDefinedType) varDeclaration.getType()).getName();
                ClassDeclaration classDec = classDecMap.get(className.getName());
                if (classDec != null) {
                    typ.setName(className);
                    typ.setClassDeclaration(classDec);
                    varDeclaration.setType(typ);
                } else {
                    if (pass == Pass.Third) {
                        ErrorLogger.log("variable type " + className.getName() + " is not valid", varDeclaration);
                        varDeclaration.setType(new NoType());
                    }
                }
            }
        }

        String varName = varDeclaration.getIdentifier().getName();

        if (pass == Pass.Third) {
            if (keywords.contains(varName)) {
                ErrorLogger.log("variable name cannot be a keyword", varDeclaration);
                varDeclaration.setType(new NoType());
                return;
            }
        }

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
        arrayCall.getInstance().accept(this);
        arrayCall.getIndex().accept(this);

        if (pass == Pass.Third) {
            if (!arrayCall.getInstance().getType().subtype(new ArrayType())) {
                ErrorLogger.log("instance must be an array", arrayCall);
                arrayCall.setType(new NoType());
            }
            else {
                if (!arrayCall.getIndex().getType().subtype(new IntType())) {
                    ErrorLogger.log("index must be an int", arrayCall);
                    arrayCall.setType(new NoType());
                }
                else {
                    arrayCall.setType(new IntType());
                }
            }
        }
    }

    @Override
    public void visit(BinaryExpression binaryExpression) {
        binaryExpression.getLeft().accept(this);
        binaryExpression.getRight().accept(this);

        if (pass == Pass.Third) {
            BinaryOperator op = binaryExpression.getBinaryOperator();
            if (op == BinaryOperator.mult || op == BinaryOperator.div || op == BinaryOperator.add ||
                    op == BinaryOperator.sub || op == BinaryOperator.gt || op == BinaryOperator.lt) {
                if (!binaryExpression.getLeft().getType().subtype(new IntType())) {
                    ErrorLogger.log("unsupported operand type for " + op.name(), binaryExpression.getLeft());
                    binaryExpression.setType(new NoType());
                }
                else if (!binaryExpression.getRight().getType().subtype(new IntType())) {
                    ErrorLogger.log("unsupported operand type for " +op.name(), binaryExpression.getRight());
                    binaryExpression.setType(new NoType());
                }
                else {
                    if (op == BinaryOperator.mult || op == BinaryOperator.div || op == BinaryOperator.add ||
                            op == BinaryOperator.sub)
                        binaryExpression.setType(new IntType());
                    else
                        binaryExpression.setType(new BooleanType());
                }
            }

            if (op == BinaryOperator.and || op == BinaryOperator.or) {
                if (!binaryExpression.getLeft().getType().subtype(new BooleanType())) {
                    ErrorLogger.log("unsupported operand type for " + op.name(), binaryExpression.getLeft());
                    binaryExpression.setType(new NoType());
                }
                else if (!binaryExpression.getRight().getType().subtype(new BooleanType())) {
                    ErrorLogger.log("unsupported operand type for " + op.name(), binaryExpression.getRight());
                    binaryExpression.setType(new NoType());
                }
                else {
                    binaryExpression.setType(new BooleanType());
                }
            }

            if (op == BinaryOperator.eq || op == BinaryOperator.neq) {
                Type right = binaryExpression.getRight().getType();
                Type left = binaryExpression.getLeft().getType();
                if (!(left.subtype(new IntType()) && right.subtype(new IntType())) &&
                        !(left.subtype(new StringType()) && right.subtype(new StringType())) &&
                        !(left.subtype(new ArrayType()) && right.subtype(new ArrayType())) &&
                        !(left.subtype(right) && right.subtype(left))) {
                    ErrorLogger.log("unsupported operand type for " + op.name(), binaryExpression);
                    binaryExpression.setType(new NoType());
                }
                else {
                    binaryExpression.setType(new BooleanType());
                }
            }

            if (op == BinaryOperator.assign) {
                if (!binaryExpression.getRight().getType().subtype(binaryExpression.getLeft().getType())) {
                    ErrorLogger.log("unsupported operand type for " + op.name(), binaryExpression);
                    binaryExpression.setType(new NoType());
                }
                else {
                    binaryExpression.setType(binaryExpression.getLeft().getType());
                }
            }
        }
    }

    @Override
    public void visit(Identifier identifier) {
        if (pass == Pass.Third) {
            try {
                //System.out.println(SymbolTable.top.getItems().keySet().toString());
                SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top.get(identifier.getName());
                identifier.setType(item.getType());
            }
            catch (ItemNotFoundException e) {
                ErrorLogger.log("variable " + identifier.getName() + " is not declared", identifier);
                identifier.setType(new NoType());
            }
        }
     }

    @Override
    public void visit(Length length) {
        if (pass == Pass.PrintOrder)
            System.out.println(length.toString());
        length.getExpression().accept(this);
        if (pass == Pass.Third) {
            if (!length.getExpression().getType().subtype(new ArrayType())) {
                ErrorLogger.log("Length argument must be an array", length);
                length.setType(new NoType());
            }
            else
                length.setType(new IntType());
        }
    }

    @Override
    public void visit(MethodCall methodCall) {
        if (pass == Pass.PrintOrder)
            System.out.println(methodCall.toString());
        methodCall.getInstance().accept(this);

        if (pass != Pass.Third)
            methodCall.getMethodName().accept(this);

        if (pass == Pass.Third) {
                String instanceType = methodCall.getInstance().getType().toString();
                ClassDeclaration classDec = classDecMap.get(instanceType);
                // TODO: What if instance is noType?
                if (!classDec.containsMethod(methodCall.getMethodName())) {
                    ErrorLogger.log("there is no method named " + methodCall.getMethodName().getName() +
                            " in class " + instanceType, methodCall);
                }
                methodCall.setType(classDec.getMethodType(methodCall.getMethodName()));
        }

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

        if (pass == Pass.Third) {
            newArray.setType(new IntType());
        }
    }

    @Override
    public void visit(NewClass newClass) {
        if (pass == Pass.Third) {
            try {
                SymbolTableClassItem item = (SymbolTableClassItem) SymbolTable.top.get(newClass.getClassName().getName());
                UserDefinedType typ = new UserDefinedType();
                typ.setName(newClass.getClassName());
                typ.setClassDeclaration(classDecMap.get(newClass.getClassName().getName()));
                newClass.setType(typ);
            } catch (ItemNotFoundException e) {
                ErrorLogger.log("class " + newClass.getClassName().getName() + " is not declared", newClass);
                newClass.setType(new NoType());
            }
        }
    }

    @Override
    public void visit(This instance) {
        if (pass == Pass.PrintOrder)
            System.out.println(instance.toString());
        if (pass == Pass.Third) {
            try {
                SymbolTableVariableItem item = (SymbolTableVariableItem) SymbolTable.top.get("this");
                instance.setType(item.getType());
            } catch (ItemNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void visit(UnaryExpression unaryExpression) {
        if (pass == Pass.PrintOrder)
            System.out.println(unaryExpression.toString());
        unaryExpression.getValue().accept(this);

        if (pass == Pass.Third) {
            if (unaryExpression.getUnaryOperator() == UnaryOperator.minus) {
                if (!unaryExpression.getValue().getType().subtype(new IntType())) {
                    ErrorLogger.log("unsupported operand type for " + unaryExpression.getUnaryOperator().name(),
                            unaryExpression);
                    unaryExpression.setType(new NoType());
                }
                else {
                    unaryExpression.setType(new IntType());
                }
            }

            if (unaryExpression.getUnaryOperator() == UnaryOperator.not) {
                if (!unaryExpression.getValue().getType().subtype(new BooleanType())) {
                    ErrorLogger.log("unsupported operand type for " + unaryExpression.getUnaryOperator().name(),
                            unaryExpression);
                    unaryExpression.setType(new NoType());
                }
                else {
                    unaryExpression.setType(new BooleanType());
                }
            }
        }
    }

    @Override
    public void visit(BooleanValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
        if (pass == Pass.Third)
            value.setType(new BooleanType());
    }

    @Override
    public void visit(IntValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
        if (pass == Pass.Third)
            value.setType(new IntType());
    }

    @Override
    public void visit(StringValue value) {
        if (pass == Pass.PrintOrder)
            System.out.println(value.toString());
        if (pass == Pass.Third)
            value.setType(new StringType());
    }

    @Override
    public void visit(Assign assign) {
        if (pass == Pass.PrintOrder)
            System.out.println(assign.toString());

        if (pass == Pass.Third) {
            boolean check = true;

            if (assign.getlValue() != null) {
                assign.getlValue().accept(this);
            }
            else {
                ErrorLogger.log("lvalue cannot be null", assign);
                check = false;
            }

            if (assign.getrValue() != null) {
                assign.getrValue().accept(this);
            }
            else {
                ErrorLogger.log("rvalue cannot be null", assign);
                check = false;
            }
            if (check && !assign.getrValue().getType().subtype(assign.getlValue().getType())) {
                ErrorLogger.log("unsupported operand type for " + BinaryOperator.assign, assign);
            }
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

        if (pass == Pass.Third) {
            if (!conditional.getExpression().getType().subtype(new BooleanType())) {
                ErrorLogger.log("condition type must be boolean", conditional);
            }
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

        if (pass == Pass.Third) {
            if (!loop.getCondition().getType().subtype(new BooleanType())) {
                ErrorLogger.log("condition type must be boolean", loop);
            }
        }
    }

    @Override
    public void visit(Write write) {
        if (pass == Pass.PrintOrder)
            System.out.println(write.toString());
        write.getArg().accept(this);

        if (pass == Pass.Third) {
            Type argType = write.getArg().getType();
            if (!argType.subtype(new IntType()) && !argType.subtype(new StringType()) && !argType.subtype(new ArrayType())) {
                ErrorLogger.log("unsupported type for writeln", write);
                write.getArg().setType(new NoType());
            }
        }
    }
}
