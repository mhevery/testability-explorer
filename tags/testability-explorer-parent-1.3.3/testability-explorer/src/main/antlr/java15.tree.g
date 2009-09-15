/* Java 1.5 AST Recognizer Grammar
 *
 * Author: (see java.g preamble)
 *
 * This grammar is in the PUBLIC DOMAIN
 */

header {
package com.google.test.metric.javasrc;

import java.util.List;
import java.util.ArrayList;

import com.google.test.metric.Type;
import com.google.test.metric.JavaType;

}

class JavaTreeParser extends TreeParser;

options {
	importVocab = Java;
}

{
	CompilationUnitBuilder builder;
	Type ignore;

	private String s(AST ast) {
	   return ast.getText();
	}
}

compilationUnit
	:	(packageDefinition)?
		(importDefinition)*
		(typeDefinition)*
	;

packageDefinition
	:	#( PACKAGE_DEF annotations pkgIdent:identifier )
	;

importDefinition
	:	#( IMPORT identifierStar)
	|	#( STATIC_IMPORT identifierStar )
	;

typeDefinition
{
    Type extendsType;
    List<Type> impls;
}
	:	#(CLASS_DEF m:modifiers i:IDENT
	    (ignore=typeParameters)? extendsType=extendsClause
	    impls=implementsClause {
	       builder.startType(i.getLine(), s(i), extendsType, impls);
	    }
	    objBlock ) {builder.endType();}
	|	#(INTERFACE_DEF modifiers IDENT (ignore=typeParameters)? extendsType=extendsClause interfaceBlock )
	|	#(ENUM_DEF modifiers IDENT impls=implementsClause enumBlock )
	|	#(ANNOTATION_DEF modifiers IDENT annotationBlock )
	;

typeParameters returns [Type type = null;]
	:	#(TYPE_PARAMETERS (type=typeParameter)+)
	;

typeParameter returns [Type type = null;]
	:	#(TYPE_PARAMETER IDENT (type=typeUpperBounds)?)
	;

typeUpperBounds returns [Type type = null;]
	:	#(TYPE_UPPER_BOUNDS (type=classOrInterfaceType)+)
	;

typeSpec returns [Type type = null;]
	:	#(TYPE type=typeSpecArray)
	;

typeSpecArray returns [Type type = null;]
	:	#( ARRAY_DECLARATOR type=typeSpecArray )
	|	type=type
	;

type returns [Type type = null;]
	:	type=classOrInterfaceType
	|	type=builtInType
	;

classOrInterfaceType returns [Type info = null;]
{info = builder.toType(classOrInterfaceType_AST_in);}
	:	IDENT (ignore=typeArguments)?
	|	#( DOT ignore=classOrInterfaceType )
	;

typeArguments returns [Type type = null;]
/*  :   #(TYPE_ARGUMENTS (type=type ignore=typeArgument)+) this seams to be wrong */
    :   #(TYPE_ARGUMENTS (ignore=typeArgument)+)
	;

typeArgument returns [Type type = null;]
	:	#(	TYPE_ARGUMENT
			(	type=typeSpec
			|	type=wildcardType
			)
		)
	;

wildcardType returns [Type type = null;]
	:	#(WILDCARD_TYPE (type=typeArgumentBounds)?)
	;

typeArgumentBounds returns [Type type = null;]
	:	#(TYPE_UPPER_BOUNDS (type=classOrInterfaceType)+)
	|	#(TYPE_LOWER_BOUNDS (type=classOrInterfaceType)+)
	;

builtInType returns [Type type = null;]
	:	"void" {type = JavaType.VOID;}
	|	"boolean" {type = JavaType.BOOLEAN;}
	|	"byte" {type = JavaType.BYTE;}
	|	"char" {type = JavaType.CHAR;}
	|	"short" {type = JavaType.SHORT;}
	|	"int" {type = JavaType.INT;}
	|	"float" {type = JavaType.FLOAT;}
	|	"long" {type = JavaType.LONG;}
	|	"double" {type = JavaType.DOUBLE;}
	;

modifiers
	:	#( MODIFIERS (modifier)* )
	;

modifier
	:	"private"
	|	"public"
	|	"protected"
	|	"static"
	|	"transient"
	|	"final"
	|	"abstract"
	|	"native"
	|	"threadsafe"
	|	"synchronized"
	|	"const"
	|	"volatile"
	|	"strictfp"
	|	annotation
	;

annotations
	:	#(ANNOTATIONS (annotation)* )
	;

annotation
	:	#(ANNOTATION identifier (annotationMemberValueInitializer | (anntotationMemberValuePair)+)? )
	;

annotationMemberValueInitializer
	:	conditionalExpr | annotation | annotationMemberArrayInitializer
	;

anntotationMemberValuePair
	:	#(ANNOTATION_MEMBER_VALUE_PAIR IDENT annotationMemberValueInitializer)
	;

annotationMemberArrayInitializer
	:	#(ANNOTATION_ARRAY_INIT (annotationMemberArrayValueInitializer)* )
	;

annotationMemberArrayValueInitializer
	:	conditionalExpr | annotation
	;

extendsClause returns [Type type=null]
	:	#(EXTENDS_CLAUSE (type=classOrInterfaceType)* )
	;

implementsClause returns [List<Type> impls=new ArrayList<Type>();]
{Type c;}
	:	#(IMPLEMENTS_CLAUSE (c=classOrInterfaceType{impls.add(c);})* )
	;


interfaceBlock
	:	#(	OBJBLOCK
			(	methodDecl
			|	variableDef
			|	typeDefinition
			)*
		)
	;

objBlock
	:	#(	OBJBLOCK
			(	ctorDef
			|	methodDef
			|	variableDef
			|	typeDefinition
			|	#(STATIC_INIT slist)
			|	#(INSTANCE_INIT slist)
			)*
		)
	;

annotationBlock
	:	#(	OBJBLOCK
			(	annotationFieldDecl
			|	variableDef
			|	typeDefinition
			)*
		)
	;

enumBlock
	:	#(	OBJBLOCK
			(
				enumConstantDef
			)*
			(	ctorDef
			|	methodDef
			|	variableDef
			|	typeDefinition
			|	#(STATIC_INIT slist)
			|	#(INSTANCE_INIT slist)
			)*
		)
	;

ctorDef
	:	#(CTOR_DEF modifiers (ignore=typeParameters)? methodHead (slist)?)
	;

methodDecl
	:	#(METHOD_DEF modifiers (ignore=typeParameters)? ignore=typeSpec methodHead)
	;

methodDef
	:	#(METHOD_DEF modifiers (ignore=typeParameters)? ignore=typeSpec methodHead (slist)?)
	;

variableDef
{Type type;}
	:	#(VARIABLE_DEF m:modifiers type=typeSpec v:variableDeclarator i:varInitializer)
	    {builder.type.addField(s(v), type, builder.visibility(m), builder.isStatic(m), builder.isFinal(m));}
	;

parameterDef
	:	#(PARAMETER_DEF modifiers ignore=typeSpec IDENT )
	;

variableLengthParameterDef
	:	#(VARIABLE_PARAMETER_DEF modifiers ignore=typeSpec IDENT )
	;

annotationFieldDecl
	:	#(ANNOTATION_FIELD_DEF modifiers ignore=typeSpec IDENT (annotationMemberValueInitializer)?)
	;

enumConstantDef
	:	#(ENUM_CONSTANT_DEF annotations IDENT (elist)? (enumConstantBlock)?)
	;

enumConstantBlock
	:	#(	OBJBLOCK
			(	methodDef
			|	variableDef
			|	typeDefinition
			|	#(INSTANCE_INIT slist)
			)*
		)
	;

objectinitializer
	:	#(INSTANCE_INIT slist)
	;

variableDeclarator
	:	IDENT
	|	LBRACK variableDeclarator
	;

varInitializer
	:	#(ASSIGN initializer)
	|
	;

initializer
	:	expression
	|	arrayInitializer
	;

arrayInitializer
	:	#(ARRAY_INIT (initializer)*)
	;

methodHead
	:	IDENT #( PARAMETERS (parameterDef)* ) (throwsClause)?
	;

throwsClause
	:	#( "throws" (ignore=classOrInterfaceType)* )
	;

identifier
	:	IDENT
	|	#( DOT identifier IDENT )
	;

identifierStar
	:	IDENT
	|	#( DOT identifier (STAR|IDENT) )
	;

slist
	:	#( SLIST (stat)* )
	;

stat:	typeDefinition
	|	variableDef
	|	expression
	|	#(LABELED_STAT IDENT stat)
	|	#("if" expression stat (stat)? )
	|	#(	"for"
			(
				#(FOR_INIT ((variableDef)+ | elist)?)
				#(FOR_CONDITION (expression)?)
				#(FOR_ITERATOR (elist)?)
			|
				#(FOR_EACH_CLAUSE parameterDef expression)
			)
			stat
		)
	|	#("while" expression stat)
	|	#("do" stat expression)
	|	#("break" (IDENT)? )
	|	#("continue" (IDENT)? )
	|	#("return" (expression)? )
	|	#("switch" expression (caseGroup)*)
	|	#("throw" expression)
	|	#("synchronized" expression stat)
	|	tryBlock
	|	slist // nested SLIST
	|	#("assert" expression (expression)?)
	|	EMPTY_STAT
	;

caseGroup
	:	#(CASE_GROUP (#("case" expression) | "default")+ slist)
	;

tryBlock
	:	#( "try" slist (handler)* (#("finally" slist))? )
	;

handler
	:	#( "catch" parameterDef slist )
	;

elist
	:	#( ELIST (expression)* )
	;

expression
	:	#(EXPR expr)
	;

expr
	:	conditionalExpr
	|	#(ASSIGN expr expr)			// binary operators...
	|	#(PLUS_ASSIGN expr expr)
	|	#(MINUS_ASSIGN expr expr)
	|	#(STAR_ASSIGN expr expr)
	|	#(DIV_ASSIGN expr expr)
	|	#(MOD_ASSIGN expr expr)
	|	#(SR_ASSIGN expr expr)
	|	#(BSR_ASSIGN expr expr)
	|	#(SL_ASSIGN expr expr)
	|	#(BAND_ASSIGN expr expr)
	|	#(BXOR_ASSIGN expr expr)
	|	#(BOR_ASSIGN expr expr)
	;

conditionalExpr
	:	#(QUESTION expr expr expr)	// trinary operator
	|	#(LOR expr expr)
	|	#(LAND expr expr)
	|	#(BOR expr expr)
	|	#(BXOR expr expr)
	|	#(BAND expr expr)
	|	#(NOT_EQUAL expr expr)
	|	#(EQUAL expr expr)
	|	#(LT expr expr)
	|	#(GT expr expr)
	|	#(LE expr expr)
	|	#(GE expr expr)
	|	#(SL expr expr)
	|	#(SR expr expr)
	|	#(BSR expr expr)
	|	#(PLUS expr expr)
	|	#(MINUS expr expr)
	|	#(DIV expr expr)
	|	#(MOD expr expr)
	|	#(STAR expr expr)
	|	#(INC expr)
	|	#(DEC expr)
	|	#(POST_INC expr)
	|	#(POST_DEC expr)
	|	#(BNOT expr)
	|	#(LNOT expr)
	|	#("instanceof" expr expr)
	|	#(UNARY_MINUS expr)
	|	#(UNARY_PLUS expr)
	|	primaryExpression
	;

primaryExpression
	:	IDENT
	|	#(	DOT
			(	expr
				(	IDENT
				|	arrayIndex
				|	"this"
				|	"class"
				|	newExpression
				|	"super"
				|	(ignore=typeArguments)? // for generic methods calls
				)
			|	#(ARRAY_DECLARATOR ignore=typeSpecArray)
			|	ignore=builtInType ("class")?
			)
		)
	|	arrayIndex
	|	#(METHOD_CALL primaryExpression (ignore=typeArguments)? elist)
	|	ctorCall
	|	#(TYPECAST ignore=typeSpec expr)
	|	newExpression
	|	constant
	|	"super"
	|	"true"
	|	"false"
	|	"this"
	|	"null"
	|	ignore=typeSpec // type name used with instanceof
	;

ctorCall
	:	#( CTOR_CALL elist )
	|	#( SUPER_CTOR_CALL
			(	elist
			|	primaryExpression elist
			)
		 )
	;

arrayIndex
	:	#(INDEX_OP expr expression)
	;

constant
	:	NUM_INT
	|	CHAR_LITERAL
	|	STRING_LITERAL
	|	NUM_FLOAT
	|	NUM_DOUBLE
	|	NUM_LONG
	;

newExpression
	:	#(	"new" (ignore=typeArguments)? ignore=type
			(	newArrayDeclarator (arrayInitializer)?
			|	elist (objBlock)?
			)
		)

	;

newArrayDeclarator
	:	#( ARRAY_DECLARATOR (newArrayDeclarator)? (expression)? )
	;
