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
        main = mainClass { program.setMainClass(main); }
         (classDec = classDeclaration { program.addClass(classDec); } )* EOF
    ;

    mainClass returns [classDeclaration main_class]:
        // name should be checked later
        'class' className = ID { ClassDeclaration classDec = new ClassDeclaration(new Identifier(className), null); }
        '{' 'def' methodName = ID '(' ')' ':' 'int' '{'  stmnts = statements 'return' returnExpr = expression ';' '}' '}'
        {
            MethodDeclaration methodDec = new MethodDeclaration(new Identifier(methodName));
            for (Statement stmnt : stmnts)
                methodDeclaration.addStatement(stmnt);
            methodDeclaration.setReturnType(new ReturnType(new IntType()));
            methodDeclaration.setReturnValue(returnExpr);
            classDec.addMethodDeclaration(methodDeclaration);
        }
    ;

    classDeclaration returns [classDeclaration class_dec]:
        'class' className = ID ('extends' parentName = ID)?
        {
            ClassDeclaration classDec = new ClassDeclaration(new Identifier(className), new Identifier(parentName));
        }
        '{' (varDec = varDeclaration { classDec.addVarDeclaration(varDec); } )*
        (methodDec = methodDeclaration { classDec.addMethodDeclaration(methodDec); } )* '}'
    ;

    varDeclaration returns [VarDeclaration varDec]:
        'var' name = ID ':' varType = type ';'
        { varDec = new VarDeclaration(new Identifier(name), varType); }
    ;

    methodDeclaration returns [MethodDeclaration methodDec]:
        'def' ID ('(' ')' | ('(' ID ':' type (',' ID ':' type)* ')')) ':' type '{'  varDeclaration* statements 'return' expression ';' '}'
    ;

    statements:
        (statement)*
    ;

    statement returns [Statement stmnt]:
        statementBlock |
        statementCondition |
        statementLoop |
        statementWrite |
        statementAssignment
    ;

    statementBlock returns [Block block]:
        '{'  statements '}'
    ;

    statementCondition returns [Conditional cond]:
        'if' '('expression')' 'then' statement ('else' statement)?
    ;

    statementLoop returns [While loop]:
        'while' '(' expression ')' statement
    ;

    statementWrite returns [Write write]:
        'writeln(' expression ')' ';'
    ;

    statementAssignment returns [Assign assign]:
        expression ';'
    ;

    expression:
		expressionAssignment
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

    expressionOther:
		CONST_NUM
        |	CONST_STR
        |   'new ' 'int' '[' CONST_NUM ']'
        |   'new ' ID '(' ')'
        |   'this'
        |   'true'
        |   'false'
        |	ID
        |   ID '[' expression ']'
        |	'(' expression ')'
	;

	type returns [Type syn_type]:
	    'int'
	    {
	        $syn_type = new IntType();
        }
        |
	    'boolean'
	    {
	        $syn_type = new BooleanType();
        }
	    |
	    'string'
	    {
	        $syn_type = new StringType();
        }
	    |
	    'int' '[' ']'
	    {
	        $syn_type = new ArrayType();
        }
	    |
	    identifier = ID
	    {
	        $syn_type = new Identifier($identifier);
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