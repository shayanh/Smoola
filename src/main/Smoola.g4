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

    program returns [Program prog]:
        {
            $prog = new Program();
        }
        main = mainClass { $prog.setMainClass($main.mainClassDec); }
        (classDec = classDeclaration { $prog.addClass($classDec.classDec); } )* EOF
    ;

    mainClass returns [ClassDeclaration mainClassDec]:
        // name should be checked later
        'class' className = ID {
            $mainClassDec = new ClassDeclaration(new Identifier($className.text), null);
            $mainClassDec.setLine($className.getLine());
        }
        '{' 'def' methodName = ID '(' ')' ':' 'int' '{'  stmnts = statements 'return' returnExpr = expression ';' '}' '}'
        {
            MethodDeclaration methodDec = new MethodDeclaration(new Identifier($methodName.text));
            methodDec.setLine($methodName.getLine());
            for (Statement stmnt : $stmnts.stmnts)
                methodDec.addStatement(stmnt);
            methodDec.setReturnType(new IntType());
            methodDec.setReturnValue($returnExpr.expr);
            $mainClassDec.addMethodDeclaration(methodDec);
        }
    ;

    classDeclaration returns [ClassDeclaration classDec]:
        'class' className = ID ('extends' parentName = ID)?
        {
            $classDec = new ClassDeclaration(new Identifier($className.text), new Identifier($parentName.text));
            $classDec.setLine($className.getLine());
        }
        '{' (varDec = varDeclaration { $classDec.addVarDeclaration($varDec.varDec); } )*
        (methodDec = methodDeclaration { $classDec.addMethodDeclaration($methodDec.methodDec); } )* '}'
    ;

    varDeclaration returns [VarDeclaration varDec]:
        'var' name = ID ':' varType = type ';'
        {
            $varDec = new VarDeclaration(new Identifier($name.text), $varType.synType);
            $varDec.setLine($name.getLine());
        }
    ;

    methodDeclaration returns [MethodDeclaration methodDec]:
        'def' name = ID {
            $methodDec = new MethodDeclaration(new Identifier($name.text));
            $methodDec.setLine($name.getLine());
        }
        ('(' ')'
        | ('(' argName1 = ID ':' argType1 = type {
            VarDeclaration var1 = new VarDeclaration(new Identifier($argName1.text), $argType1.synType);
            var1.setLine($argName1.getLine());
            $methodDec.addArg(var1);
        }
        (',' argName2 = ID ':' argType2 = type {
            VarDeclaration var2 = new VarDeclaration(new Identifier($argName2.text), $argType2.synType);
            var2.setLine($argName2.getLine());
            $methodDec.addArg(var2);
        })* ')'))
        ':' returnType = type { $methodDec.setReturnType($returnType.synType); }
        '{' (varDec = varDeclaration { $methodDec.addLocalVar($varDec.varDec); })*
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
        (stmnt = statement { $stmnts.add($stmnt.stmnt); } )*
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
            $cond.setAlternativeBody($alt.stmnt);
        }
    ;

    statementLoop returns [While loop]:
        'while' '(' condExpr = expression ')' body = statement
        {
            $loop = new While($condExpr.expr, $body.stmnt);
        }
    ;

    statementWrite returns [Write write]:
        'writeln(' arg = expression ')' ';'
        {
            $write = new Write($arg.expr);
        }
    ;

    statementAssignment returns [Assign assign]:
        expr = expression ';'
        {
            $assign = new Assign($expr.assignExpr.getLeft(), $expr.assignExpr.getRight());
        }
    ;

    expression returns [Expression expr, BinaryExpression assignExpr]:
		expr1 = expressionAssignment
		{
    	    $expr = $expr1.expr;
    	    $assignExpr = $expr1.assignExpr;
		}
	;

    expressionAssignment returns [Expression expr, BinaryExpression assignExpr]:
		leftExpr = expressionOr '=' rightExpr = expressionAssignment
		{
		    $assignExpr = new BinaryExpression($leftExpr.expr, $rightExpr.expr, BinaryOperator.assign);
		}
	    |	expr1 = expressionOr
	    {
	        $expr = $expr1.expr;
	    }
	;

    expressionOr returns [Expression expr]:
		andExpr = expressionAnd orTempExpr = expressionOrTemp
		{
		    if ($orTempExpr.expr == null) {
		        $expr = $andExpr.expr;
		    } else {
		        $expr = new BinaryExpression($andExpr.expr, $orTempExpr.expr, BinaryOperator.or);
		    }
		}
	;

    expressionOrTemp returns [Expression expr]:
		'||' andExpr = expressionAnd orTempExpr = expressionOrTemp
		{
		    if ($orTempExpr.expr == null) {
		        $expr = $andExpr.expr;
		    } else {
		        $expr = new BinaryExpression($andExpr.expr, $orTempExpr.expr, BinaryOperator.or);
		    }
		}
	    |
	    {
	        $expr = null;
	    }
	;

    expressionAnd returns [Expression expr]:
		eqExpr = expressionEq andTempExpr = expressionAndTemp
		{
		    if ($andTempExpr.expr == null) {
		        $expr = $eqExpr.expr;
		    } else {
		        $expr = new BinaryExpression($eqExpr.expr, $andTempExpr.expr, BinaryOperator.and);
		    }
		}
	;

    expressionAndTemp returns [Expression expr]:
		'&&' eqExpr = expressionEq andTempExpr = expressionAndTemp
		{
		    if ($andTempExpr.expr == null) {
		        $expr = $eqExpr.expr;
		    } else {
		        $expr = new BinaryExpression($eqExpr.expr, $andTempExpr.expr, BinaryOperator.and);
		    }
		}
	    |
	    {
	        $expr = null;
	    }
	;

    expressionEq returns [Expression expr]:
		cmpExpr = expressionCmp eqTempExpr = expressionEqTemp
		{
		    if ($eqTempExpr.expr == null) {
		        $expr = $cmpExpr.expr;
		    } else {
		        $expr = new BinaryExpression($cmpExpr.expr, $eqTempExpr.expr, $eqTempExpr.synOp);
		    }
		}
	;

    expressionEqTemp returns [Expression expr, BinaryOperator synOp]:
		op = ('==' | '<>') cmpExpr = expressionCmp eqTempExpr = expressionEqTemp
		{
		    if ($op.text.equals("<>"))
		        $synOp = BinaryOperator.neq;
            else
                $synOp = BinaryOperator.eq;
		    if ($eqTempExpr.expr == null) {
		        $expr = $cmpExpr.expr;
		    } else {
		        $expr = new BinaryExpression($cmpExpr.expr, $eqTempExpr.expr, $eqTempExpr.synOp);
		    }
		}
	    |
	    {
	        $expr = null;
	        $synOp = null;
	    }
	;

    expressionCmp returns [Expression expr]:
		addExpr = expressionAdd cmpTempExpr = expressionCmpTemp
		{
		    if ($cmpTempExpr.expr == null) {
		        $expr = $addExpr.expr;
		    } else {
		        $expr = new BinaryExpression($addExpr.expr, $cmpTempExpr.expr, $cmpTempExpr.synOp);
		    }
		}
	;

    expressionCmpTemp returns [Expression expr, BinaryOperator synOp]:
		op = ('<' | '>') addExpr = expressionAdd cmpTempExpr = expressionCmpTemp
		{
			if ($op.text.equals("<"))
			    $synOp = BinaryOperator.lt;
			else
			    $synOp = BinaryOperator.gt;
		    if ($cmpTempExpr.expr == null) {
		        $expr = $addExpr.expr;
		    } else {
		        $expr = new BinaryExpression($addExpr.expr, $cmpTempExpr.expr, $cmpTempExpr.synOp);
		    }
		}
	    |
	    {
	        $expr = null;
	        $synOp = null;
	    }
	;

    expressionAdd returns [Expression expr]:
		mulExpr = expressionMult addTempExpr = expressionAddTemp
		{
		    if ($addTempExpr.expr == null) {
		        $expr = $mulExpr.expr;
		    } else {
		        $expr = new BinaryExpression($mulExpr.expr, $addTempExpr.expr, $addTempExpr.synOp);
		    }
		}
	;

    expressionAddTemp returns [Expression expr, BinaryOperator synOp]:
		op = ('+' | '-') mulExpr = expressionMult addTempExpr = expressionAddTemp
		{
		    if ($op.text.equals("+"))
		        $synOp = BinaryOperator.add;
            else
                $synOp = BinaryOperator.sub;
		    if ($addTempExpr.expr == null) {
		        $expr = $mulExpr.expr;
		    } else {
		        $expr = new BinaryExpression($mulExpr.expr, $addTempExpr.expr, $addTempExpr.synOp);
		    }
		}
	    |
	    {
	        $expr = null;
	        $synOp = null;
	    }
	;

    expressionMult returns [Expression expr]:
		unaryExpr = expressionUnary mulTempExpr = expressionMultTemp
		{
		    if ($mulTempExpr.expr == null) {
		        $expr = $unaryExpr.expr;
		    } else {
		        $expr = new BinaryExpression($unaryExpr.expr, $mulTempExpr.expr, $mulTempExpr.synOp);
		    }
		}
	;

    expressionMultTemp returns [Expression expr, BinaryOperator synOp]:
		op = ('*' | '/') unaryExpr = expressionUnary mulTempExpr = expressionMultTemp
		{
		    if ($op.text.equals("*"))
		        $synOp = BinaryOperator.mult;
		    else
		        $synOp = BinaryOperator.div;
		    if ($mulTempExpr.expr == null) {
		        $expr = $unaryExpr.expr;
		    } else {
		        $expr = new BinaryExpression($unaryExpr.expr, $mulTempExpr.expr, $mulTempExpr.synOp);
		    }
		}
	    |
	    {
	        $expr = null;
	        $synOp = null;
	    }
	;

    expressionUnary returns [Expression expr]:
		op = ('!' | '-') unaryExpr = expressionUnary
		{
		    UnaryOperator unaryOp;
		    if ($op.text.equals("!"))
		        unaryOp = UnaryOperator.not;
		    else
		        unaryOp = UnaryOperator.minus;
		    $expr = new UnaryExpression(unaryOp, $unaryExpr.expr);
		}
	    |	memExpr = expressionMem
	    {
	        $expr = $memExpr.expr;
	    }
	;

    expressionMem returns [Expression expr]:
		methodsExpr = expressionMethods memTempExpr = expressionMemTemp
		{
		    if ($memTempExpr.expr == null) {
		        $expr = $methodsExpr.expr;
		    } else {
		        $expr = new ArrayCall($methodsExpr.expr, $memTempExpr.expr);
		    }
		}
	;

    expressionMemTemp returns [Expression expr]:
		'[' expr1 = expression ']'
		{
		    $expr = $expr1.expr;
		}
	    |
	    {
	        $expr = null;
	    }
	;
	expressionMethods returns [Expression expr]:
	    otherExpr = expressionOther methodsTempExpr = expressionMethodsTemp [$otherExpr.expr]
	    {
	        if ($methodsTempExpr.expr == null) {
	            $expr = $otherExpr.expr;
	        } else{
	            $expr = $methodsTempExpr.expr;
	        }
	    }
	;

	expressionMethodsTemp [Expression inhInstanceName] returns [Expression expr]:
	    '.' (
	        methodName = ID '(' ')' { $expr = new MethodCall($inhInstanceName, new Identifier($methodName.text)); }
	        | methodName = ID { MethodCall tmp = new MethodCall($inhInstanceName, new Identifier($methodName.text)); }
	        '(' (arg1 = expression { tmp.addArg($arg1.expr); } (',' arg2 = expression { tmp.addArg($arg2.expr); } )*) ')'
	        { $expr = tmp; }
	        | 'length' { $expr = new Length($inhInstanceName); }
	    ) expressionMethodsTemp [$expr]
	    |
	    {
	        $expr = null;
	    }
	;

    expressionOther returns [Expression expr]:
		num = CONST_NUM { $expr = new IntValue($num.int, new IntType()); }
        |	str = CONST_STR { $expr = new StringValue($str.text, new StringType()); }
        |   'new ' 'int' '[' size = CONST_NUM ']'
            {
                NewArray tmp = new NewArray();
                tmp.setExpression(new IntValue($size.int, new IntType()));
                $expr = tmp;
                $expr.setLine($size.getLine());
            }
        |   'new ' className = ID '(' ')' { $expr = new NewClass(new Identifier($className.text)); }
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
	        UserDefinedType tmp = new UserDefinedType();
	        tmp.setName(new Identifier($identifier.text));
	        $synType = tmp;
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