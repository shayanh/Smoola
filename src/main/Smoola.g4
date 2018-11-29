grammar Smoola;

    @header{
        import ast.*;
        import ast.node.*;
        import ast.node.declaration.*;
        import ast.node.expression.*;
        import ast.node.expression.Value.*;
        import ast.node.statement.*;
        import ast.Type.*;
        import ast.Type.ArrayType.*;
        import ast.Type.PrimitiveType.*;
        import ast.Type.UserDefinedType.*;
    }

    program:
        { Program program = new Program(); }
        main = mainClass { program.setMainClass($main.mainClassDec); }
        (classDec = classDeclaration { program.addClass($classDec.classDec); } )* EOF
        {
            Visitor visitor = new VisitorImpl();
            program.accept(visitor);
        }
    ;

    mainClass returns [ClassDeclaration mainClassDec]:
        // name should be checked later
        'class' className = ID { $mainClassDec = new ClassDeclaration(new Identifier($className.text), null); }
        '{' 'def' methodName = ID '(' ')' ':' 'int' '{'  stmnts = statements 'return' returnExpr = expression ';' '}' '}'
        {
            MethodDeclaration methodDec = new MethodDeclaration(new Identifier($methodName.text));
            for (Statement stmnt : $stmnts.stmnts)
                methodDeclaration.addStatement(stmnt);
            methodDeclaration.setReturnType(new ReturnType(new IntType()));
            methodDeclaration.setReturnValue($returnExpr.expr);
            $mainClassDec.addMethodDeclaration(methodDeclaration);
        }
    ;

    classDeclaration returns [ClassDeclaration classDec]:
        'class' className = ID ('extends' parentName = ID)?
        {
            $classDec = new ClassDeclaration(new Identifier($className.text), new Identifier($parentName.text));
        }
        '{' (varDec = varDeclaration { $classDec.addVarDeclaration($varDec.varDec); } )*
        (methodDec = methodDeclaration { $classDec.addMethodDeclaration($methodDec.methodDec); } )* '}'
    ;

    varDeclaration returns [VarDeclaration varDec]:
        'var' name = ID ':' varType = type ';'
        { $varDec = new VarDeclaration(new Identifier($name.text), $varType.synType); }
    ;

    methodDeclaration returns [MethodDeclaration methodDec]:
        'def' name = ID { $methodDec = new MethodDeclaration(new Identifier($name.text)); }
        ('(' ')'
        | ('(' argName1 = ID ':' argType1 = type { $methodDec.addArg(new VarDeclaration(new Identifier($argName1.text), $argType1.synType); }
        (',' argName2 = ID ':' argType2 = type { $methodDec.addArg(new VarDeclaration(new Identifier($argName2.text), $argType2.synType); })* ')'))
        ':' returnType = type { $methodDec.setReturnType($returnType.synType); }
        '{' (varDec = varDeclaration { $methodDec.addVarDeclaration($varDec.varDec); })*
        stmnts = statements {
            for (Statement stmnt : $stmnts.stmnts)
                $methodDec.addStatement(stmnt);
        }
        'return' returnValue = expression ';' '}' { $methodDec.setReturnValue($returnValue.expr); }
    ;

    statements returns [ArrayList<Statement> stmnts]:
        {
            $stmnts = new ArrayList<>();
        }
        (stmnt = statement { $stmnts.add(stmnt); } )*
    ;

    statement returns [Statement stmnt]:
        {
            $stmnt = new Statement();
        }
        block = statementBlock { $stmnt = $block.block; } |
        cond = statementCondition { $stmnt = $cond.cond; } |
        loop = statementLoop { $stmnt = $loop.loop; } |
        write = statementWrite { $stmnt = $write.write; } |
        assign = statementAssignment { $stmnt = $assign.assign; }
    ;

    statementBlock returns [Block block]:
        '{' stmnts = statements '}'
        {
            $block = new Block();
            for (Statement stmnt : $stmnts.stmnts)
                $block.addStatement(stmnt);
        }
    ;

    statementCondition returns [Conditional cond]:
        'if' '(' expr = expression ')' 'then' cons = statement ('else' alt = statement)?
        {
            $cond = new Conditional($expr.expr, $cons.stmnt);
            $cond.addAlternativeBody($alt.stmnt);
        }
    ;

    statementLoop returns [While loop]:
        'while' '(' cond = expression ')' body = statement
        {
            $loop = new While(cond.expr, $body.stmnt);
        }
    ;

    statementWrite returns [Write write]:
        'writeln(' arg = expression ')' ';'
        {
            $write = new Write($arg.expr);
        }
    ;

    statementAssignment returns [Assign assign]:
        expression ';'
    ;

    expression returns [Experssion expr]:
		expressionAssignment
		{
		}
	;

    expressionAssignment:
		expressionOr '=' expressionAssignment
	    |	expressionOr
	;

    expressionOr:
		expressionAnd expressionOrTemp
	;

    expressionOrTemp:
		'||' expressionAnd expressionOrTemp
	    |
	;

    expressionAnd:
		expressionEq expressionAndTemp
	;

    expressionAndTemp:
		'&&' expressionEq expressionAndTemp
	    |
	;

    expressionEq:
		expressionCmp expressionEqTemp
	;

    expressionEqTemp:
		('==' | '<>') expressionCmp expressionEqTemp
	    |
	;

    expressionCmp:
		expressionAdd expressionCmpTemp
	;

    expressionCmpTemp:
		('<' | '>') expressionAdd expressionCmpTemp
	    |
	;

    expressionAdd:
		expressionMult expressionAddTemp
	;

    expressionAddTemp:
		('+' | '-') expressionMult expressionAddTemp
	    |
	;

        expressionMult:
		expressionUnary expressionMultTemp
	;

    expressionMultTemp:
		('*' | '/') expressionUnary expressionMultTemp
	    |
	;

    expressionUnary:
		('!' | '-') expressionUnary
	    |	expressionMem
	;

    expressionMem:
		expressionMethods expressionMemTemp
	;

    expressionMemTemp:
		'[' expression ']'
	    |
	;
	expressionMethods:
	    expressionOther expressionMethodsTemp
	;

	expressionMethodsTemp:
	    '.' (ID '(' ')' | ID '(' (expression (',' expression)*) ')' | 'length') expressionMethodsTemp
	    |
	;

    expressionOther returns [Expression expr]:
		num = CONST_NUM { $expr = new IntValue($num.int, new IntType()); }
        |	str = CONST_STR { $expr = new StringValue($str.text, new StringType()); }
        |   'new ' 'int' '[' size = CONST_NUM ']'
            {
                $expr = new NewArray();
                $expr.setExpression(new IntValue($size.int));
            }
        |   'new ' className = ID '(' ')' { $expr = new NewClass($className.text); }
        |   'this' { $expr = new This(); }
        |   'true' { $expr = new BooleanValue(true, new BooleanType()); }
        |   'false' { $expr = new BooleanValue(false, new BooleanType()); }
        |	name = ID { $expr = new Identifier($name.text); }
        |   name = ID '[' index = expression ']' { $expr = new ArrayCall(new Identifier($name.text), $index.expr); }
        |	'(' ex = expression ')' { $expr = $ex.expr; }
	;

	type returns [Type synType]:
	    'int'
	    {
	        $synType = new IntType();
        }
        |
	    'boolean'
	    {
	        $synType = new BooleanType();
        }
	    |
	    'string'
	    {
	        $synType = new StringType();
        }
	    |
	    'int' '[' ']'
	    {
	        $synType = new ArrayType();
        }
	    |
	    identifier = ID
	    {
	        $synType = new Identifier($identifier.text);
        }
	;

    CONST_NUM:
		[0-9]+
	;

    CONST_STR:
		'"' ~('\r' | '\n' | '"')* '"'
	;
    NL:
		'\r'? '\n' -> skip
	;

    ID:
		[a-zA-Z_][a-zA-Z0-9_]*
	;

    COMMENT:
		'#'(~[\r\n])* -> skip
	;

    WS:
    	[ \t] -> skip
    ;