/* eXist Open Source Native XML Database
 * Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Library General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Library General Public License for more details.
 *
 * You should have received a copy of the GNU Library General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * $Id$
 */
header {
	package org.exist.xquery.parser;

	import antlr.debug.misc.*;
	import java.io.StringReader;
	import java.io.BufferedReader;
	import java.io.InputStreamReader;
	import java.util.ArrayList;
	import java.util.List;
	import java.util.Iterator;
	import java.util.Stack;
	import org.exist.storage.BrokerPool;
	import org.exist.storage.DBBroker;
	import org.exist.storage.analysis.Tokenizer;
	import org.exist.EXistException;
	import org.exist.dom.DocumentSet;
	import org.exist.dom.DocumentImpl;
	import org.exist.dom.QName;
	import org.exist.security.PermissionDeniedException;
	import org.exist.security.User;
	import org.exist.xquery.*;
	import org.exist.xquery.value.*;
	import org.exist.xquery.functions.*;
}

/**
 * The XQuery parser. eXist uses two steps to parse an XQuery expression:
 * in the first step, the XQueryParser generates an abstract syntax tree (AST),
 * which is - in the second step - passed to {@link XQueryTreeParser} for
 * analysis. XQueryTreeParser finally creates an internal representation of
 * the query from the tree of AST nodes.
 */
class XQueryParser extends Parser;

options {
	defaultErrorHandler= false;
	k= 1;
	buildAST= true;
	ASTLabelType = org.exist.xquery.parser.XQueryAST;
	exportVocab=XQuery;
}

{
	protected ArrayList exceptions= new ArrayList(2);
	protected boolean foundError= false;
	protected Stack globalStack= new Stack();
	protected Stack elementStack= new Stack();
	protected XQueryLexer lexer;

	public XQueryParser(XQueryLexer lexer) {
		this((TokenStream)lexer);
		this.lexer= lexer;
		setASTNodeClass("org.exist.xquery.parser.XQueryAST");
	}

	public boolean foundErrors() {
		return foundError;
	}

	public String getErrorMessage() {
		StringBuffer buf= new StringBuffer();
		for (Iterator i= exceptions.iterator(); i.hasNext();) {
			buf.append(((Exception) i.next()).toString());
			buf.append('\n');
		}
		return buf.toString();
	}

	protected void handleException(Exception e) {
		foundError= true;
		exceptions.add(e);
	}
}

/* The following tokens are assigned by the parser (not the lexer)
 * and have to be exported (so the tree parser can see them).
 */
imaginaryTokenDefinitions
:
	QNAME
	PREDICATE 
	FLWOR 
	PARENTHESIZED 
	ABSOLUTE_SLASH 
	ABSOLUTE_DSLASH 
	WILDCARD 
	PREFIX_WILDCARD 
	FUNCTION 
	UNARY_MINUS
	UNARY_PLUS
	XPOINTER
	XPOINTER_ID
	VARIABLE_REF 
	VARIABLE_BINDING
	ELEMENT
	ATTRIBUTE 
	TEXT 
	VERSION_DECL 
	NAMESPACE_DECL 
	DEF_NAMESPACE_DECL 
	DEF_COLLATION_DECL
	DEF_FUNCTION_NS_DECL 
	GLOBAL_VAR 
	FUNCTION_DECL 
	PROLOG 
	ATOMIC_TYPE 
	MODULE 
	ORDER_BY 
	POSITIONAL_VAR 
	BEFORE 
	AFTER 
	MODULE_DECL 
	ATTRIBUTE_TEST 
	COMP_ELEM_CONSTRUCTOR
	COMP_ATTR_CONSTRUCTOR 
	COMP_TEXT_CONSTRUCTOR 
	COMP_COMMENT_CONSTRUCTOR
	COMP_PI_CONSTRUCTOR 
	COMP_NS_CONSTRUCTOR
	COMP_DOC_CONSTRUCTOR
	;

xpointer throws XPathException
:
	"xpointer"^ LPAREN! ex:expr RPAREN!
	{ #xpointer= #(#[XPOINTER, "xpointer"], #ex); }
	|
	nc:NCNAME
	{ #xpointer= #(#[XPOINTER_ID, "id"], #nc); }
	;

xpath throws XPathException
:
	( module )? EOF
	;
//	exception catch [RecognitionException e]
//	{ handleException(e); }

module throws XPathException: 
	( ( "xquery" "version" ) => v:versionDecl SEMICOLON! )?
	(
		( "module" "namespace" ) => libraryModule 
		| 
		mainModule
	)
	;

mainModule throws XPathException: 
	prolog queryBody ;

libraryModule throws XPathException: 
	moduleDecl prolog;

moduleDecl throws XPathException: 
	"module"! "namespace"! prefix:NCNAME EQ! uri:STRING_LITERAL SEMICOLON!
	{
		#moduleDecl = #(#[MODULE_DECL, prefix.getText()], uri);
	}
	;
	
prolog throws XPathException
{ boolean inSetters = true; }
:
	(
		(
			( "import" "module" ) => moduleImport
			|
			( "declare" ( "default" | "xmlspace" ) ) =>
			setter
			{
				if(!inSetters)
					throw new TokenStreamException("Default declarations have to come first");
			}
			|
			( "declare" "namespace" )
			=> namespaceDecl { inSetters = false; }
			|
			( "declare" "function" )
			=> functionDecl { inSetters = false; }
			|
			( "declare" "variable" )
			=> varDecl { inSetters = false; }
		)
		SEMICOLON!
	)*
	;

versionDecl
:
	"xquery" "version" v:STRING_LITERAL { #versionDecl = #(#[VERSION_DECL, v.getText()]); }
	;

setter:
	(
		( "declare" "default" ) =>
		"declare"! "default"!
		(
			"collation"! defc:STRING_LITERAL
			{ #setter = #(#[DEF_COLLATION_DECL, "defaultCollationDecl"], defc); }
			|
			"element"! "namespace"! defu:STRING_LITERAL
			{ #setter= #(#[DEF_NAMESPACE_DECL, "defaultNamespaceDecl"], defu); }
			|
			"function"! "namespace"! deff:STRING_LITERAL
			{ #setter= #(#[DEF_FUNCTION_NS_DECL, "defaultFunctionNSDecl"], deff); }
		)
		|
		( "declare" "xmlspace" ) =>
		"declare"! "xmlspace"^ ( "preserve" | "strip" )
	)
	;
	
namespaceDecl
{ String prefix = null; }
:
	"declare" "namespace" prefix=ncnameOrKeyword EQ! uri:STRING_LITERAL
	{ #namespaceDecl= #(#[NAMESPACE_DECL, prefix], uri); }
	;

varDecl throws XPathException
{ String varName= null; }
:
	decl:"declare"! "variable"! DOLLAR! varName=qName! ( typeDeclaration )?
	LCURLY! ex:expr RCURLY!
	{ 
        #varDecl= #(#[GLOBAL_VAR, varName], #varDecl);
        #varDecl.copyLexInfo(#decl);
    }
	;
	
moduleImport
:
	"import"^ "module"! ( "namespace"! NCNAME EQ! )? STRING_LITERAL ( "at"! STRING_LITERAL )?
	;
	
functionDecl throws XPathException
{ String name= null; }
:
	"declare"! "function"! name=qName! lp:LPAREN! ( paramList )?
	RPAREN! ( returnType )?
	functionBody
	{ 
		#functionDecl= #(#[FUNCTION_DECL, name], #functionDecl); 
		#functionDecl.copyLexInfo(#lp);
	}
	;

functionBody throws XPathException: 
	LCURLY^ e:expr RCURLY! ;

returnType throws XPathException: 
	"as"^ sequenceType ;

paramList throws XPathException
:
	param ( COMMA! p1:param )*
	;

param throws XPathException
{ String varName= null; }
:
	DOLLAR! varName=qName ( t:typeDeclaration )?
	{ #param= #(#[VARIABLE_BINDING, varName], #t); }
	;

typeDeclaration throws XPathException: 
	"as"^ sequenceType ;

sequenceType throws XPathException
:
	( "empty" LPAREN ) => "empty"^ LPAREN! RPAREN! | itemType ( occurrenceIndicator )?
	;

occurrenceIndicator
:
	QUESTION | STAR | PLUS
	;

itemType throws XPathException
:
	( "item" LPAREN ) => "item"^ LPAREN! RPAREN! | ( . LPAREN ) => kindTest | atomicType
	;

singleType throws XPathException
:
	atomicType ( QUESTION )?
	;

atomicType throws XPathException
{ String name= null; }
:
	name=qName
	{ #atomicType= #[ATOMIC_TYPE, name]; }
	;

queryBody throws XPathException: expr ;

expr throws XPathException
:
	exprSingle ( COMMA^ exprSingle )*
	;

exprSingle throws XPathException
:
	( ( "for" | "let" ) DOLLAR ) => flworExpr
	| ( ( "some" | "every" ) DOLLAR ) => quantifiedExpr
	| ( "if" LPAREN ) => ifExpr 
	| orExpr
	;

flworExpr throws XPathException
:
	( forClause | letClause )+ ( "where" expr )? ( orderByClause )? "return"^ exprSingle
	;

forClause throws XPathException
:
	"for"^ inVarBinding ( COMMA! inVarBinding )*
	;

letClause throws XPathException
:
	"let"^ letVarBinding ( COMMA! letVarBinding )*
	;

inVarBinding throws XPathException
{ String varName; }
:
	DOLLAR! varName=qName! ( typeDeclaration )?
	( positionalVar )?
	"in"! exprSingle
	{ #inVarBinding= #(#[VARIABLE_BINDING, varName], #inVarBinding); }
	;

positionalVar
{ String varName; }
:
	"at" DOLLAR! varName=qName
	{ #positionalVar= #[POSITIONAL_VAR, varName]; }
	;

letVarBinding throws XPathException
{ String varName; }
:
	DOLLAR! varName=qName! ( typeDeclaration )?
	COLON! EQ! exprSingle
	{ #letVarBinding= #(#[VARIABLE_BINDING, varName], #letVarBinding); }
	;

orderByClause throws XPathException
:
	"order"! "by"! orderSpecList
	{ #orderByClause= #([ORDER_BY, "order by"], #orderByClause); }
	;

orderSpecList throws XPathException
:
	orderSpec ( COMMA! orderSpec )*
	;

orderSpec throws XPathException: exprSingle orderModifier ;

orderModifier
:
	( "ascending" | "descending" )? ( "empty" ( "greatest" | "least" ) )? ( "collation" STRING_LITERAL )?
	;

quantifiedExpr throws XPathException:
	( "some"^ | "every"^ ) quantifiedInVarBinding ( COMMA! quantifiedInVarBinding )*
	"satisfies"! exprSingle
	;

quantifiedInVarBinding throws XPathException
{ String varName; }:
	DOLLAR! varName=qName! ( typeDeclaration )? "in"! exprSingle
	{ #quantifiedInVarBinding = #(#[VARIABLE_BINDING, varName], #quantifiedInVarBinding); }
	;
	
ifExpr throws XPathException: "if"^ LPAREN! expr RPAREN! "then"! exprSingle "else"! exprSingle ;

orExpr throws XPathException
:
	andExpr ( "or"^ andExpr )*
	;

andExpr throws XPathException
:
	instanceofExpr ( "and"^ instanceofExpr )*
	;

instanceofExpr throws XPathException
:
	castableExpr ( "instance"^ "of"! sequenceType )?
	;

castableExpr throws XPathException
:
	castExpr ( "castable"^ "as"! singleType )?
	;
	
castExpr throws XPathException
:
	comparisonExpr ( "cast"^ "as"! singleType )?
	;

comparisonExpr throws XPathException
:
	rangeExpr (
		( LT LT ) => LT! LT! rangeExpr 
			{
				#comparisonExpr = #(#[BEFORE, "<<"], #comparisonExpr);
			}
		|
		( GT GT ) => GT! GT! rangeExpr
			{
				#comparisonExpr = #(#[AFTER, ">>"], #comparisonExpr);
			}
		| ( ( "eq"^ | "ne"^ | "lt"^ | "le"^ | "gt"^ | "ge"^ ) rangeExpr )
		| ( ( EQ^ | NEQ^ | GT^ | GTEQ^ | LT^ | LTEQ^ ) rangeExpr )
		| ( ( "is"^ | "isnot"^ ) rangeExpr )
		| ( ( ANDEQ^ | OREQ^ ) rangeExpr )
	)?
	;

rangeExpr throws XPathException
:
	additiveExpr ( "to"^ additiveExpr )?
	;

additiveExpr throws XPathException
:
	multiplicativeExpr ( ( PLUS^ | MINUS^ ) multiplicativeExpr )*
	;

multiplicativeExpr throws XPathException
:
	unaryExpr ( ( STAR^ | "div"^ | "idiv"^ | "mod"^ ) unaryExpr )*
	;

unaryExpr throws XPathException
:
	// TODO: XPath 2.0 allows an arbitrary number of +/-, 
	// we restrict it to one
	m:MINUS expr:unionExpr
	{ 
        #unaryExpr= #(#[UNARY_MINUS, "-"], #expr);
        #unaryExpr.copyLexInfo(#m);
    }
	|
	p:PLUS expr2:unionExpr
	{ 
        #unaryExpr= #(#[UNARY_PLUS, "+"], #expr2);
        #unaryExpr.copyLexInfo(#p);
    }
	|
	unionExpr
	;

unionExpr throws XPathException
:
	intersectExceptExpr
	(
		( "union"! | UNION! ) unionExpr
		{
			#unionExpr = #(#[UNION, "union"], #unionExpr);
		}
	)?
	;

intersectExceptExpr throws XPathException
:
	pathExpr
	(
		( "intersect"^ | "except"^ ) pathExpr
	)*
	;
	
pathExpr throws XPathException
:
	relativePathExpr
	|
	( SLASH relativePathExpr )
	=> SLASH relPath:relativePathExpr
	{ #pathExpr= #(#[ABSOLUTE_SLASH, "AbsoluteSlash"], #relPath); }
	// lone slash
	|
	SLASH
	{ #pathExpr= #[ABSOLUTE_SLASH, "AbsoluteSlash"]; }
	|
	DSLASH relPath2:relativePathExpr
	{ #pathExpr= #(#[ABSOLUTE_DSLASH, "AbsoluteSlashSlash"], #relPath2); }
	;

relativePathExpr throws XPathException
:
	stepExpr ( ( SLASH^ | DSLASH^ ) stepExpr )*
	;

stepExpr throws XPathException
:
	( ( "text" | "node" | "element" | "attribute" | "comment" | "processing-instruction" | "document-node" ) LPAREN )
	=> axisStep
	|
	( ( "element" | "attribute" | "text" | "document" | "processing-instruction" | 
	"comment" ) LCURLY ) => 
	filterStep
	|
	( ( "element" | "attribute" | "processing-instruction" | "namespace" ) qName LCURLY ) => filterStep
	|
	( DOLLAR | ( qName LPAREN ) | SELF | LPAREN | literal | XML_COMMENT | LT |
	  XML_PI )
	=> filterStep
	|
	axisStep
	;

axisStep throws XPathException
:
	( forwardOrReverseStep ) predicates
	;

predicates throws XPathException
:
	( predicate )*
	;

predicate throws XPathException
:
	LPPAREN! predExpr:expr RPPAREN!
	{ #predicate= #(#[PREDICATE, "Pred"], #predExpr); }
	;

forwardOrReverseStep throws XPathException
:
	( forwardAxisSpecifier COLON )
	=> forwardAxis nodeTest
	|
	( reverseAxisSpecifier COLON )
	=> reverseAxis nodeTest
	|
	abbrevStep
	;

abbrevStep throws XPathException
:
	( AT )? nodeTest | PARENT
	;

forwardAxis : forwardAxisSpecifier COLON! COLON! ;

forwardAxisSpecifier
:
	"child" | "self" | "attribute" | "descendant" | "descendant-or-self" 
    | "following-sibling" | "following"
	;

reverseAxis : reverseAxisSpecifier COLON! COLON! ;

reverseAxisSpecifier
:
	"parent" | "ancestor" | "ancestor-or-self" | "preceding-sibling"
	;

nodeTest throws XPathException
:
	( . LPAREN ) => kindTest | nameTest
	;

nameTest throws XPathException
{ String name= null; }
:
	( ( ncnameOrKeyword COLON STAR ) | STAR )
	=> wildcard
	|
	name=qName
	{ #nameTest= #[QNAME, name]; }
	;

wildcard
{ String name= null; }
:
	// *:localname
	( STAR COLON )
	=> STAR! COLON! name=ncnameOrKeyword
	{ #wildcard= #(#[PREFIX_WILDCARD, "*"], #[NCNAME, name]); }
	// prefix:*
	|
	name=ncnameOrKeyword COLON! STAR!
	{ #wildcard= #(#[NCNAME, name], #[WILDCARD, "*"]); }
	// *
	|
	STAR
	{
		// make this distinct from multiplication
		#wildcard= #[WILDCARD, "*"];
	}
	;

filterStep throws XPathException: 
	primaryExpr predicates ;

primaryExpr throws XPathException
{ String varName= null; }
:
	( ( "element" | "attribute" | "text" | "document" | "processing-instruction" | 
	"comment" ) LCURLY ) => 
	computedConstructor
	|
	( ( "element" | "attribute" | "processing-instruction" | "namespace" ) qName LCURLY ) => computedConstructor
	|
	constructor
	|
	functionCall
	|
	contextItemExpr
	|
	parenthesizedExpr
	|
	DOLLAR! varName=v:qName
	{ 
        #primaryExpr= #[VARIABLE_REF, varName];
        #primaryExpr.copyLexInfo(#v);
    }
	|
	literal
	;

literal
:
	STRING_LITERAL^ | numericLiteral
	;

numericLiteral
:
	DOUBLE_LITERAL^ | DECIMAL_LITERAL^ | INTEGER_LITERAL^
	;

parenthesizedExpr throws XPathException
:
	LPAREN! ( e:expr )?
	RPAREN!
	{ #parenthesizedExpr= #(#[PARENTHESIZED, "Parenthesized"], #e); }
	;

functionCall throws XPathException
{ String fnName= null; }
:
	fnName=q:qName l:LPAREN!
	{ 
        #functionCall = #[FUNCTION, fnName];
    }
	(
		params:functionParameters
		{ #functionCall= #(#[FUNCTION, fnName], #params); }
	)?
    { #functionCall.copyLexInfo(#q); }
	RPAREN!
	;

functionParameters throws XPathException
:
	exprSingle ( COMMA! exprSingle )*
	;

contextItemExpr : SELF^ ;

kindTest
:
	textTest | anyKindTest | elementTest | attributeTest | commentTest | piTest | documentTest
	;

textTest : "text"^ LPAREN! RPAREN! ;

anyKindTest : "node"^ LPAREN! RPAREN! ;

elementTest : "element"^ LPAREN! ( elementNameOrWildcard )? RPAREN! ;

elementNameOrWildcard 
{ String qn = null; }:
	STAR { #elementNameOrWildcard = #[WILDCARD, "*"]; }
	|
	qn=qName { #elementNameOrWildcard = #[QNAME, qn]; }
	;

attributeTest : "attribute"! LPAREN! ( attributeNameOrWildcard ) ? RPAREN! 
	{ #attributeTest= #(#[ATTRIBUTE_TEST, "attribute()"], #attributeTest); }
	;

attributeNameOrWildcard
{ String qn = null; }:
	STAR { #attributeNameOrWildcard = #[WILDCARD, "*"]; }
	|
	qn=qName { #attributeNameOrWildcard = #[QNAME, qn]; }
	;
	
commentTest : "comment"^ LPAREN! RPAREN! ;

piTest : "processing-instruction"^ LPAREN! RPAREN! ;

documentTest : "document-node"^ LPAREN! RPAREN! ;

qName returns [String name]
{
	name= null;
	String name2;
}
:
	( ncnameOrKeyword COLON ncnameOrKeyword )
	=> name=nc1:ncnameOrKeyword COLON name2=ncnameOrKeyword
	{ 
		name= name + ':' + name2;
		#qName.copyLexInfo(#nc1);
	}
	|
	name=ncnameOrKeyword
	;

constructor throws XPathException
:
	elementConstructor
	| 
	xmlComment 
	| 
	xmlPI
	;

computedConstructor throws XPathException
:
	compElemConstructor
	|
	compAttrConstructor
	|
	compTextConstructor
	|
	compDocumentConstructor
	|
	compXmlPI
	|
	compXmlComment
	;

compElemConstructor throws XPathException
{
	String qn;
}
:
	( "element" LCURLY ) =>
	"element"! LCURLY! expr RCURLY! LCURLY! compElemBody RCURLY!
	{ #compElemConstructor = #(#[COMP_ELEM_CONSTRUCTOR], #compElemConstructor); }
	|
	"element"! qn=qName LCURLY! e3:compElemBody RCURLY!
	{ #compElemConstructor = #(#[COMP_ELEM_CONSTRUCTOR, qn], #[STRING_LITERAL, qn], #e3); }
	;

compElemBody throws XPathException
:
	( 
		( "namespace" ncnameOrKeyword LCURLY ) => localNamespaceDecl 
		| 
		exprSingle 
	)
	( COMMA! (
		( "namespace" ncnameOrKeyword LCURLY ) => localNamespaceDecl 
		| 
		exprSingle ) 
	)*
	;
	
compAttrConstructor throws XPathException
{
	String qn;
}
:
	( "attribute" LCURLY ) =>
	"attribute"! LCURLY! e1:expr RCURLY! LCURLY! e2:expr RCURLY!
	{ #compAttrConstructor = #(#[COMP_ATTR_CONSTRUCTOR], #compAttrConstructor); }
	|
	"attribute"! qn=qName LCURLY! e3:expr RCURLY!
	{ #compAttrConstructor = #(#[COMP_ATTR_CONSTRUCTOR, qn], #[STRING_LITERAL, qn], #e3); }
	;

compTextConstructor throws XPathException
:
	"text" LCURLY! e:expr RCURLY!
	{ #compTextConstructor = #(#[COMP_TEXT_CONSTRUCTOR, "text"], #e); }
	;

compDocumentConstructor throws XPathException
:
	"document" LCURLY! e:expr RCURLY!
	{ #compDocumentConstructor = #(#[COMP_DOC_CONSTRUCTOR, "document"], #e); }
	;

compXmlPI throws XPathException
{
	String qn;
}
:
	( "processing-instruction" LCURLY ) =>
	"processing-instruction"! LCURLY! e1:expr RCURLY! LCURLY! e2:expr RCURLY!
	{ #compXmlPI = #(#[COMP_PI_CONSTRUCTOR], #compXmlPI); }
	|
	"processing-instruction"! qn=qName LCURLY! e3:expr RCURLY!
	{ #compXmlPI = #(#[COMP_PI_CONSTRUCTOR, qn], #[STRING_LITERAL, qn], #e3); }
	;

compXmlComment throws XPathException
:
	"comment" LCURLY! e:expr RCURLY!
	{ #compXmlComment = #(#[COMP_COMMENT_CONSTRUCTOR, "comment"], #e); }
	;

localNamespaceDecl
{
	String nc = null;
}
:
	"namespace"! nc=ncnameOrKeyword LCURLY! l:STRING_LITERAL RCURLY!
	{ #localNamespaceDecl = #(#[COMP_NS_CONSTRUCTOR, nc], #l); }
	;

elementConstructor throws XPathException
{
	String name= null;
	//lexer.wsExplicit= false;
}
:
	( LT qName ~( GT | SLASH ) ) => elementWithAttributes | elementWithoutAttributes
	;

elementWithoutAttributes throws XPathException
{ String name= null; }
:
	LT name=q:qName
	(
		(
			SLASH! GT!
			{
				//lexer.wsExplicit= false;
				if (!elementStack.isEmpty())
					lexer.inElementContent= true;
				#elementWithoutAttributes= #[ELEMENT, name];
			}
		)
		|
		(
			GT!
			{
				elementStack.push(name);
				lexer.inElementContent= true;
			}
			content:mixedElementContent END_TAG_START! name=qn:qName! GT!
			{
				if (elementStack.isEmpty())
					throw new XPathException(#qn, "found wrong closing tag: " + name);
				String prev= (String) elementStack.pop();
				if (!prev.equals(name))
					throw new XPathException(#qn, "found closing tag: " + name + "; expected: " + prev);
				#elementWithoutAttributes= #(#[ELEMENT, name], #content);
				if (!elementStack.isEmpty()) {
					lexer.inElementContent= true;
					//lexer.wsExplicit= false;
				}
			}
		)
	)
    { #elementWithoutAttributes.copyLexInfo(#q); }
	;

elementWithAttributes throws XPathException
{ String name= null; }
:
	LT! name=q:qName attrs:attributeList
	(
		(
			SLASH! GT!
			{
				if (!elementStack.isEmpty())
					lexer.inElementContent= true;
				//lexer.wsExplicit= false;
				#elementWithAttributes= #(#[ELEMENT, name], #attrs);
			}
		)
		|
		(
			GT!
			{
				elementStack.push(name);
				lexer.inElementContent= true;
				//lexer.wsExplicit= false;
			}
			content:mixedElementContent END_TAG_START! name=qn:qName! GT!
			{
				if (elementStack.isEmpty())
					throw new XPathException(#qn, "found closing tag without opening tag: " + name);
				String prev= (String) elementStack.pop();
				if (!prev.equals(name))
					throw new XPathException(#qn, "found closing tag: " + name + "; expected: " + prev);
				#elementWithAttributes= #(#[ELEMENT, name], #attrs);
				if (!elementStack.isEmpty()) {
					lexer.inElementContent= true;
					//lexer.wsExplicit= false;
				}
			}
		)
	)
    { #elementWithAttributes.copyLexInfo(#q); }
	;

attributeList throws XPathException
:
	( attributeDef )+
	;

attributeDef throws XPathException
{
	String name= null;
	lexer.parseStringLiterals= false;
}
:
	name=q:qName! EQ! 
	( 
		QUOT!
		{ lexer.inAttributeContent= true; }
		attributeValue { lexer.inAttributeContent= false; }
		QUOT!
		{ 
			lexer.parseStringLiterals= true;
			lexer.inAttributeContent= false;
		}
		| 
		APOS! { lexer.inAttributeContent= true; }
		attributeValue
		APOS! 
		{ 
			lexer.parseStringLiterals= true;
			lexer.inAttributeContent= false;
		}
	)
	{ 
		#attributeDef= #(#[ATTRIBUTE, name], #attributeDef);
		#attributeDef.copyLexInfo(#q);
	}
	;

attributeValue throws XPathException
:
	( ATTRIBUTE_CONTENT | attributeEnclosedExpr )+
	;

mixedElementContent throws XPathException
:
	( elementContent )*
	;

elementContent throws XPathException
:
	elementConstructor
	|
	content:ELEMENT_CONTENT
	{ #elementContent= #[TEXT, content.getText()]; }
	|
	xmlComment
	|
	xmlPI
	|
	enclosedExpr
	;

xmlComment : XML_COMMENT XML_COMMENT_END! ;

xmlPI : XML_PI XML_PI_END! ;

enclosedExpr throws XPathException
:
	LCURLY^
	{
		globalStack.push(elementStack);
		elementStack= new Stack();
		lexer.inElementContent= false;
		//lexer.wsExplicit= false;
	}
	expr RCURLY!
	{
		elementStack= (Stack) globalStack.pop();
		lexer.inElementContent= true;
		//lexer.wsExplicit= true;
	}
	;

attributeEnclosedExpr throws XPathException
:
	LCURLY^
	{
		lexer.inAttributeContent= false;
		lexer.parseStringLiterals = true;
		//lexer.wsExplicit= false;
	}
	expr RCURLY!
	{
		lexer.inAttributeContent= true;
		lexer.parseStringLiterals = false;
		//lexer.wsExplicit= true;
	}
	;

/* All of the literals used in this grammar can also be
 * part of a valid QName. We thus have to test for each
 * of them below.
 */
ncnameOrKeyword returns [String name]
{ name= null; }
:
	n1:NCNAME { name= n1.getText(); }
	|
	name=reservedKeywords
	;

reservedKeywords returns [String name]
{ name= null; }
:
	"element" { name = "element"; }
	|
	"to" { name = "to"; }
	|
	"div" { name= "div"; }
	|
	"mod" { name= "mod"; }
	|
	"text" { name= "text"; }
	|
	"node" { name= "node"; }
	|
	"or" { name= "or"; }
	|
	"and" { name= "and"; }
	|
	"child" { name= "child"; }
	|
	"parent" { name= "parent"; }
	|
	"self" { name= "self"; }
	|
	"attribute" { name= "attribute"; }
	|
	"comment" { name= "comment"; }
	|
	"document" { name= "document"; }
	|
	"document-node" { name= "document-node"; }
	|
	"collection" { name= "collection"; }
	|
	"ancestor" { name= "ancestor"; }
	|
	"descendant" { name= "descendant"; }
	|
	"descendant-or-self" { name= "descendant-or-self"; }
	|
	"ancestor-or-self" { name= "ancestor-or-self"; }
	|
	"preceding-sibling" { name= "preceding-sibling"; }
	|
	"following-sibling" { name= "following-sibling"; }
	|
	"following" { name = "following"; }
	|
	"preceding" { name = "preceding"; }
	|
	"item" { name= "item"; }
	|
	"empty" { name= "empty"; }
	|
	"version" { name= "version"; }
	|
	"xquery" { name= "xquery"; }
	|
	"variable" { name= "variable"; }
	|
	"namespace" { name= "namespace"; }
	|
	"if" { name= "if"; }
	|
	"then" { name= "then"; }
	|
	"else" { name= "else"; }
	|
	"for" { name= "for"; }
	|
	"let" { name= "let"; }
	|
	"default" { name= "default"; }
	|
	"function" { name= "function"; }
	|
	"as" { name = "as"; }
	|
	"union" { name = "union"; }
	|
	"intersect" { name = "intersect"; }
	|
	"except" { name = "except"; }
	|
	"order" { name = "order"; }
	|
	"by" { name = "by"; }
	|
	"some" { name = "some"; }
	|
	"every" { name = "every"; }
	|
	"is" { name = "is"; }
	|
	"isnot" { name = "isnot"; }
	|
	"module" { name = "module"; }
	|
	"import" { name = "import"; }
	|
	"at" { name = "at"; }
	|
	"cast" { name = "cast"; }
	|
	"return" { name = "return"; }
	|
	"instance" { name = "instance"; }
	|
	"of" { name = "of"; }
	|
	"declare" { name = "declare"; }
	|
	"collation" { name = "collation"; }
	|
	"xmlspace" { name = "xmlspace"; }
	|
	"preserve" { name = "preserve"; }
	|
	"strip" { name = "strip"; }
	;

/**
 * The XQuery/XPath lexical analyzer.
 */
class XQueryLexer extends Lexer;

options {
	k = 4;
	testLiterals = false;
	charVocabulary = '\u0003'..'\uFFFE';
	codeGenBitsetTestThreshold = 20;
	exportVocab=XQuery;
}

{
	protected boolean wsExplicit= false;
	protected boolean parseStringLiterals= true;
	protected boolean inElementContent= false;
	protected boolean inAttributeContent= false;
	protected boolean inComment= false;
	
	protected XQueryContext context;
	
	public XQueryLexer(XQueryContext context, Reader in) {
		this(in);
		this.context = context;
	}
}

protected SLASH : '/' ;
protected DSLASH : '/' '/' ;
protected COLON : ':' ;
protected COMMA : ',' ;
protected SEMICOLON : ';' ;
protected STAR : '*' ;
protected QUESTION : '?' ;
protected PLUS : '+' ;
protected MINUS : '-' ;
protected LPPAREN : '[' ;
protected RPPAREN : ']' ;
protected LPAREN options { paraphrase="'('"; } : '(' ;
protected RPAREN options { paraphrase="')'"; } : ')' ;
protected SELF : '.' ;
protected PARENT : ".." ;
protected UNION : '|' ;
protected AT : '@' ;
protected DOLLAR : '$' ;
protected ANDEQ : "&=" ;
protected OREQ : "|=" ;
protected EQ : '=' ;
protected NEQ : "!=" ;
protected GT : '>' ;
protected GTEQ : ">=" ;
protected QUOT : '"' ;
protected APOS : "'";
protected LTEQ : "<=" ;

protected LT : '<' ;
protected END_TAG_START : "</" ;

protected LCURLY : '{' ;
protected RCURLY : '}' ;

protected XML_COMMENT_END : "-->" ;
protected XML_PI_START : "<?" ;
protected XML_PI_END : "?>" ;

protected LETTER
:
	( BASECHAR | IDEOGRAPHIC )
	;

protected DIGITS
:
	( DIGIT )+
	;

protected HEX_DIGITS
:
	( '0'..'9' | 'a'..'f' | 'A'..'F' )+
	;

protected NMSTART
:
	( LETTER | '_' )
	;

protected NMCHAR
:
	( LETTER | DIGIT | '.' | '-' | '_' | COMBINING_CHAR | EXTENDER )
	;

protected NCNAME
options {
	testLiterals=true;
}
:
	NMSTART ( NMCHAR )*
	;

protected WS
:
	(
		' '
		|
		'\t'
		|
		'\n' { newline(); }
		|
		'\r'
	)+
	;

protected EXPR_COMMENT
options {
	testLiterals=false;
}
:
	"(:" ( CHAR | ( ':' ~( ')' ) ) => ':' )* ":)"
	;

protected PRAGMA
options {
	testLiterals=false;
}
{ String content = null; }:
	"(::" "pragma"
	WS qn:PRAGMA_QNAME WS 
	( c:PRAGMA_CONTENT { content = c.getText(); } )? ':' ':' ')'
	{
		try {
			context.addPragma(qn.getText(), content);
		} catch(XPathException e) {
			throw new RecognitionException(e.getMessage());
		}
	}
	;

protected PRAGMA_CONTENT
:
	( ~( ' ' | '\t' | '\n' | '\r' ) ) 
	( CHAR | (':' ~( ':' )) => ':' | (':' ':' ~(')') ) => ':' ':' )+
	;

protected PRAGMA_QNAME
:
	NCNAME ( ':' NCNAME )?
	;
	
protected INTEGER_LITERAL : 
	{ !(inElementContent || inAttributeContent) }? DIGITS ;

protected DOUBLE_LITERAL
:
	{ !(inElementContent || inAttributeContent) }?
	( ( '.' DIGITS ) | ( DIGITS ( '.' ( DIGIT )* )? ) ) ( 'e' | 'E' ) ( '+' | '-' )? DIGITS
	;

protected DECIMAL_LITERAL
:
	{ !(inElementContent || inAttributeContent) }?
	( '.' DIGITS ) | ( DIGITS ( '.' ( DIGIT )* )? )
	;

protected PREDEFINED_ENTITY_REF
:
	'&' ( "lt" | "gt" | "amp" | "quot" | "apos" ) ';'
	;

protected CHAR_REF
:
	'&' '#' ( DIGITS | ( 'x' HEX_DIGITS ) ) ';'
	;

protected STRING_LITERAL
options {
	testLiterals = false;
}
:
	'"'! ( PREDEFINED_ENTITY_REF | CHAR_REF | ( '"'! '"' ) | ~ ( '"' | '&' ) )*
	'"'!
	|
	'\''! ( PREDEFINED_ENTITY_REF | CHAR_REF | ( '\''! '\'' ) | ~ ( '\'' | '&' ) )*
	'\''!
	;

protected ATTRIBUTE_CONTENT
options {
	testLiterals=false;
}
:
	(
		'\t'
		|
		'\r'
		|
		'\n' { newline(); }
		|
		'\u0020'
		|
		'\u0021'
		|
		'\u0023'..'\u003b'
		|
		'\u003d'..'\u007a'
		|
		'\u007c'
		|
		'\u007e'..'\uFFFD'
	)+
	;

protected ELEMENT_CONTENT
options {
	testLiterals=false;
}
:
	( '\t' | '\r' | '\n' { newline(); } | '\u0020'..'\u003b' | '\u003d'..'\u007a' | '\u007c' | '\u007e'..'\uFFFD' )+
	;

protected XML_COMMENT
options {
	testLiterals=false;
}
:
	"<!--"! ( ~ ( '-' ) | ( '-' ~ ( '-' ) ) => '-' )+
	;

protected XML_PI
options {
	testLiterals=false;
}
:
	XML_PI_START! NCNAME ' ' ( ~ ( '?' ) | ( '?' ~ ( '>' ) ) => '?' )+
	;

/**
 * Main method that decides which token to return next.
 * We need this as many things depend on the current
 * context.
 */
NEXT_TOKEN
options {
	testLiterals = false;
}
:
	XML_COMMENT { $setType(XML_COMMENT); }
	|
	( XML_PI_START )
	=> XML_PI { $setType(XML_PI); }
	|
	END_TAG_START
	{
		inElementContent= false;
		wsExplicit= false;
		$setType(END_TAG_START);
	}
	|
	LT
	{
		inElementContent= false;
		$setType(LT);
	}
	|
	LTEQ { $setType(LTEQ); }
	|
	LCURLY
	{
		inElementContent= false;
		$setType(LCURLY);
	}
	|
	RCURLY { $setType(RCURLY); }
	|
	{ inAttributeContent }?
	ATTRIBUTE_CONTENT
	{ $setType(ATTRIBUTE_CONTENT); }
	|
	{ !(parseStringLiterals || inElementContent) }?
	QUOT
	{ $setType(QUOT); }
	|
	{ inElementContent }?
	ELEMENT_CONTENT
	{ $setType(ELEMENT_CONTENT); }
	|
	WS
	{
		if (wsExplicit) {
			$setType(WS);
			$setText("WS");
		} else
			$setType(Token.SKIP);
	}
	|
	( "(::" ) => PRAGMA
	{ $setType(Token.SKIP); }
	|
	EXPR_COMMENT
	{ $setType(Token.SKIP); }
	|
	ncname:NCNAME { $setType(ncname.getType()); }
	|
	{ parseStringLiterals }?
	STRING_LITERAL { $setType(STRING_LITERAL); }
	|
	( '.' '.' ) =>
	{ !(inAttributeContent || inElementContent) }?
	PARENT { $setType(PARENT); }
	|
    ( '.' INTEGER_LITERAL ( 'e' | 'E' ) )
	=> DECIMAL_LITERAL { $setType(DECIMAL_LITERAL); }
    |
	( '.' INTEGER_LITERAL )
	=> DECIMAL_LITERAL { $setType(DECIMAL_LITERAL); }
	|
	( '.' )
	=> SELF { $setType(SELF); }
	|
	( INTEGER_LITERAL ( '.' ( INTEGER_LITERAL )? )? ( 'e' | 'E' ) )
	=> DOUBLE_LITERAL
	{ $setType(DOUBLE_LITERAL); }
	|
	( INTEGER_LITERAL '.' )
	=> DECIMAL_LITERAL
	{ $setType(DECIMAL_LITERAL); }
	|
	INTEGER_LITERAL { $setType(INTEGER_LITERAL); }
	|
	SLASH { $setType(SLASH); }
	|
	{ !(inAttributeContent || inElementContent) }?
	DSLASH { $setType(DSLASH); }
	|
	COLON { $setType(COLON); }
	|
	COMMA { $setType(COMMA); }
	|
	SEMICOLON { $setType(SEMICOLON); }
	|
	STAR { $setType(STAR); }
	|
	QUESTION { $setType(QUESTION); }
	|
	PLUS { $setType(PLUS); }
	|
	MINUS { $setType(MINUS); }
	|
	LPPAREN { $setType(LPPAREN); }
	|
	RPPAREN { $setType(RPPAREN); }
	|
	LPAREN { $setType(LPAREN); }
	|
	RPAREN { $setType(RPAREN); }
	|
	UNION { $setType(UNION); }
	|
	AT { $setType(AT); }
	|
	DOLLAR { $setType(DOLLAR); }
	|
	{ !(inAttributeContent || inElementContent) }?
	OREQ { $setType(OREQ); }
	|
	{ !(inAttributeContent || inElementContent) }?
	ANDEQ { $setType(ANDEQ); }
	|
	EQ { $setType(EQ); }
	|
	{ !(inAttributeContent || inElementContent) }?
	NEQ { $setType(NEQ); }
	|
	XML_COMMENT_END { $setType(XML_COMMENT_END); }
	|
	GT { $setType(GT); }
	|
	{ !(inAttributeContent || inElementContent) }?
	GTEQ { $setType(GTEQ); }
	|
	XML_PI_END { $setType(XML_PI_END); }
	;

protected CHAR
:
	( '\t' | '\n' { newline(); } | '\r' | '\u0020'..'\u0039' | '\u003B'..'\uD7FF' | '\uE000'..'\uFFFD' )
	;

protected BASECHAR
:
	(
		'\u0041'..'\u005a'
		|
		'\u0061'..'\u007a'
		|
		'\u00c0'..'\u00d6'
		|
		'\u00d8'..'\u00f6'
		|
		'\u00f8'..'\u00ff'
		|
		'\u0100'..'\u0131'
		|
		'\u0134'..'\u013e'
		|
		'\u0141'..'\u0148'
		|
		'\u014a'..'\u017e'
		|
		'\u0180'..'\u01c3'
		|
		'\u01cd'..'\u01f0'
		|
		'\u01f4'..'\u01f5'
		|
		'\u01fa'..'\u0217'
		|
		'\u0250'..'\u02a8'
		|
		'\u02bb'..'\u02c1'
		|
		'\u0386'
		|
		'\u0388'..'\u038a'
		|
		'\u038c'
		|
		'\u038e'..'\u03a1'
		|
		'\u03a3'..'\u03ce'
		|
		'\u03d0'..'\u03d6'
		|
		'\u03da'
		|
		'\u03dc'
		|
		'\u03de'
		|
		'\u03e0'
		|
		'\u03e2'..'\u03f3'
		|
		'\u0401'..'\u040c'
		|
		'\u040e'..'\u044f'
		|
		'\u0451'..'\u045c'
		|
		'\u045e'..'\u0481'
		|
		'\u0490'..'\u04c4'
		|
		'\u04c7'..'\u04c8'
		|
		'\u04cb'..'\u04cc'
		|
		'\u04d0'..'\u04eb'
		|
		'\u04ee'..'\u04f5'
		|
		'\u04f8'..'\u04f9'
		|
		'\u0531'..'\u0556'
		|
		'\u0559'
		|
		'\u0561'..'\u0586'
		|
		'\u05d0'..'\u05ea'
		|
		'\u05f0'..'\u05f2'
		|
		'\u0621'..'\u063a'
		|
		'\u0641'..'\u064a'
		|
		'\u0671'..'\u06b7'
		|
		'\u06ba'..'\u06be'
		|
		'\u06c0'..'\u06ce'
		|
		'\u06d0'..'\u06d3'
		|
		'\u06d5'
		|
		'\u06e5'..'\u06e6'
		|
		'\u0905'..'\u0939'
		|
		'\u093d'
		|
		'\u0958'..'\u0961'
		|
		'\u0985'..'\u098c'
		|
		'\u098f'..'\u0990'
		|
		'\u0993'..'\u09a8'
		|
		'\u09aa'..'\u09b0'
		|
		'\u09b2'
		|
		'\u09b6'..'\u09b9'
		|
		'\u09dc'..'\u09dd'
		|
		'\u09df'..'\u09e1'
		|
		'\u09f0'..'\u09f1'
		|
		'\u0a05'..'\u0a0a'
		|
		'\u0a0f'..'\u0a10'
		|
		'\u0a13'..'\u0a28'
		|
		'\u0a2a'..'\u0a30'
		|
		'\u0a32'..'\u0a33'
		|
		'\u0a35'..'\u0a36'
		|
		'\u0a38'..'\u0a39'
		|
		'\u0a59'..'\u0a5c'
		|
		'\u0a5e'
		|
		'\u0a72'..'\u0a74'
		|
		'\u0a85'..'\u0a8b'
		|
		'\u0a8d'
		|
		'\u0a8f'..'\u0a91'
		|
		'\u0a93'..'\u0aa8'
		|
		'\u0aaa'..'\u0ab0'
		|
		'\u0ab2'..'\u0ab3'
		|
		'\u0ab5'..'\u0ab9'
		|
		'\u0abd'
		|
		'\u0ae0'
		|
		'\u0b05'..'\u0b0c'
		|
		'\u0b0f'..'\u0b10'
		|
		'\u0b13'..'\u0b28'
		|
		'\u0b2a'..'\u0b30'
		|
		'\u0b32'..'\u0b33'
		|
		'\u0b36'..'\u0b39'
		|
		'\u0b3d'
		|
		'\u0b5c'..'\u0b5d'
		|
		'\u0b5f'..'\u0b61'
		|
		'\u0b85'..'\u0b8a'
		|
		'\u0b8e'..'\u0b90'
		|
		'\u0b92'..'\u0b95'
		|
		'\u0b99'..'\u0b9a'
		|
		'\u0b9c'
		|
		'\u0b9e'..'\u0b9f'
		|
		'\u0ba3'..'\u0ba4'
		|
		'\u0ba8'..'\u0baa'
		|
		'\u0bae'..'\u0bb5'
		|
		'\u0bb7'..'\u0bb9'
		|
		'\u0c05'..'\u0c0c'
		|
		'\u0c0e'..'\u0c10'
		|
		'\u0c12'..'\u0c28'
		|
		'\u0c2a'..'\u0c33'
		|
		'\u0c35'..'\u0c39'
		|
		'\u0c60'..'\u0c61'
		|
		'\u0c85'..'\u0c8c'
		|
		'\u0c8e'..'\u0c90'
		|
		'\u0c92'..'\u0ca8'
		|
		'\u0caa'..'\u0cb3'
		|
		'\u0cb5'..'\u0cb9'
		|
		'\u0cde'
		|
		'\u0ce0'..'\u0ce1'
		|
		'\u0d05'..'\u0d0c'
		|
		'\u0d0e'..'\u0d10'
		|
		'\u0d12'..'\u0d28'
		|
		'\u0d2a'..'\u0d39'
		|
		'\u0d60'..'\u0d61'
		|
		'\u0e01'..'\u0e2e'
		|
		'\u0e30'
		|
		'\u0e32'..'\u0e33'
		|
		'\u0e40'..'\u0e45'
		|
		'\u0e81'..'\u0e82'
		|
		'\u0e84'
		|
		'\u0e87'..'\u0e88'
		|
		'\u0e8a'
		|
		'\u0e8d'
		|
		'\u0e94'..'\u0e97'
		|
		'\u0e99'..'\u0e9f'
		|
		'\u0ea1'..'\u0ea3'
		|
		'\u0ea5'
		|
		'\u0ea7'
		|
		'\u0eaa'..'\u0eab'
		|
		'\u0ead'..'\u0eae'
		|
		'\u0eb0'
		|
		'\u0eb2'..'\u0eb3'
		|
		'\u0ebd'
		|
		'\u0ec0'..'\u0ec4'
		|
		'\u0f40'..'\u0f47'
		|
		'\u0f49'..'\u0f69'
		|
		'\u10a0'..'\u10c5'
		|
		'\u10d0'..'\u10f6'
		|
		'\u1100'
		|
		'\u1102'..'\u1103'
		|
		'\u1105'..'\u1107'
		|
		'\u1109'
		|
		'\u110b'..'\u110c'
		|
		'\u110e'..'\u1112'
		|
		'\u113c'
		|
		'\u113e'
		|
		'\u1140'
		|
		'\u114c'
		|
		'\u114e'
		|
		'\u1150'
		|
		'\u1154'..'\u1155'
		|
		'\u1159'
		|
		'\u115f'..'\u1161'
		|
		'\u1163'
		|
		'\u1165'
		|
		'\u1167'
		|
		'\u1169'
		|
		'\u116d'..'\u116e'
		|
		'\u1172'..'\u1173'
		|
		'\u1175'
		|
		'\u119e'
		|
		'\u11a8'
		|
		'\u11ab'
		|
		'\u11ae'..'\u11af'
		|
		'\u11b7'..'\u11b8'
		|
		'\u11ba'
		|
		'\u11bc'..'\u11c2'
		|
		'\u11eb'
		|
		'\u11f0'
		|
		'\u11f9'
		|
		'\u1e00'..'\u1e9b'
		|
		'\u1ea0'..'\u1ef9'
		|
		'\u1f00'..'\u1f15'
		|
		'\u1f18'..'\u1f1d'
		|
		'\u1f20'..'\u1f45'
		|
		'\u1f48'..'\u1f4d'
		|
		'\u1f50'..'\u1f57'
		|
		'\u1f59'
		|
		'\u1f5b'
		|
		'\u1f5d'
		|
		'\u1f5f'..'\u1f7d'
		|
		'\u1f80'..'\u1fb4'
		|
		'\u1fb6'..'\u1fbc'
		|
		'\u1fbe'
		|
		'\u1fc2'..'\u1fc4'
		|
		'\u1fc6'..'\u1fcc'
		|
		'\u1fd0'..'\u1fd3'
		|
		'\u1fd6'..'\u1fdb'
		|
		'\u1fe0'..'\u1fec'
		|
		'\u1ff2'..'\u1ff4'
		|
		'\u1ff6'..'\u1ffc'
		|
		'\u2126'
		|
		'\u212a'..'\u212b'
		|
		'\u212e'
		|
		'\u2180'..'\u2182'
		|
		'\u3041'..'\u3094'
		|
		'\u30a1'..'\u30fa'
		|
		'\u3105'..'\u312c'
		|
		'\uac00'..'\ud7a3'
	)
	;

protected IDEOGRAPHIC
:
	( '\u4e00'..'\u9fa5' | '\u3007' | '\u3021'..'\u3029' )
	;

protected COMBINING_CHAR
:
	(
		'\u0300'..'\u0345'
		|
		'\u0360'..'\u0361'
		|
		'\u0483'..'\u0486'
		|
		'\u0591'..'\u05a1'
		|
		'\u05a3'..'\u05b9'
		|
		'\u05bb'..'\u05bd'
		|
		'\u05bf'
		|
		'\u05c1'..'\u05c2'
		|
		'\u05c4'
		|
		'\u064b'..'\u0652'
		|
		'\u0670'
		|
		'\u06d6'..'\u06dc'
		|
		'\u06dd'..'\u06df'
		|
		'\u06e0'..'\u06e4'
		|
		'\u06e7'..'\u06e8'
		|
		'\u06ea'..'\u06ed'
		|
		'\u0901'..'\u0903'
		|
		'\u093c'
		|
		'\u093e'..'\u094c'
		|
		'\u094d'
		|
		'\u0951'..'\u0954'
		|
		'\u0962'..'\u0963'
		|
		'\u0981'..'\u0983'
		|
		'\u09bc'
		|
		'\u09be'
		|
		'\u09bf'
		|
		'\u09c0'..'\u09c4'
		|
		'\u09c7'..'\u09c8'
		|
		'\u09cb'..'\u09cd'
		|
		'\u09d7'
		|
		'\u09e2'..'\u09e3'
		|
		'\u0a02'
		|
		'\u0a3c'
		|
		'\u0a3e'
		|
		'\u0a3f'
		|
		'\u0a40'..'\u0a42'
		|
		'\u0a47'..'\u0a48'
		|
		'\u0a4b'..'\u0a4d'
		|
		'\u0a70'..'\u0a71'
		|
		'\u0a81'..'\u0a83'
		|
		'\u0abc'
		|
		'\u0abe'..'\u0ac5'
		|
		'\u0ac7'..'\u0ac9'
		|
		'\u0acb'..'\u0acd'
		|
		'\u0b01'..'\u0b03'
		|
		'\u0b3c'
		|
		'\u0b3e'..'\u0b43'
		|
		'\u0b47'..'\u0b48'
		|
		'\u0b4b'..'\u0b4d'
		|
		'\u0b56'..'\u0b57'
		|
		'\u0b82'..'\u0b83'
		|
		'\u0bbe'..'\u0bc2'
		|
		'\u0bc6'..'\u0bc8'
		|
		'\u0bca'..'\u0bcd'
		|
		'\u0bd7'
		|
		'\u0c01'..'\u0c03'
		|
		'\u0c3e'..'\u0c44'
		|
		'\u0c46'..'\u0c48'
		|
		'\u0c4a'..'\u0c4d'
		|
		'\u0c55'..'\u0c56'
		|
		'\u0c82'..'\u0c83'
		|
		'\u0cbe'..'\u0cc4'
		|
		'\u0cc6'..'\u0cc8'
		|
		'\u0cca'..'\u0ccd'
		|
		'\u0cd5'..'\u0cd6'
		|
		'\u0d02'..'\u0d03'
		|
		'\u0d3e'..'\u0d43'
		|
		'\u0d46'..'\u0d48'
		|
		'\u0d4a'..'\u0d4d'
		|
		'\u0d57'
		|
		'\u0e31'
		|
		'\u0e34'..'\u0e3a'
		|
		'\u0e47'..'\u0e4e'
		|
		'\u0eb1'
		|
		'\u0eb4'..'\u0eb9'
		|
		'\u0ebb'..'\u0ebc'
		|
		'\u0ec8'..'\u0ecd'
		|
		'\u0f18'..'\u0f19'
		|
		'\u0f35'
		|
		'\u0f37'
		|
		'\u0f39'
		|
		'\u0f3e'
		|
		'\u0f3f'
		|
		'\u0f71'..'\u0f84'
		|
		'\u0f86'..'\u0f8b'
		|
		'\u0f90'..'\u0f95'
		|
		'\u0f97'
		|
		'\u0f99'..'\u0fad'
		|
		'\u0fb1'..'\u0fb7'
		|
		'\u0fb9'
		|
		'\u20d0'..'\u20dc'
		|
		'\u20e1'
		|
		'\u302a'..'\u302f'
		|
		'\u3099'
		|
		'\u309a'
	)
	;

protected DIGIT
:
	(
		'\u0030'..'\u0039'
		|
		'\u0660'..'\u0669'
		|
		'\u06f0'..'\u06f9'
		|
		'\u0966'..'\u096f'
		|
		'\u09e6'..'\u09ef'
		|
		'\u0a66'..'\u0a6f'
		|
		'\u0ae6'..'\u0aef'
		|
		'\u0b66'..'\u0b6f'
		|
		'\u0be7'..'\u0bef'
		|
		'\u0c66'..'\u0c6f'
		|
		'\u0ce6'..'\u0cef'
		|
		'\u0d66'..'\u0d6f'
		|
		'\u0e50'..'\u0e59'
		|
		'\u0ed0'..'\u0ed9'
		|
		'\u0f20'..'\u0f29'
	)
	;

protected EXTENDER
:
	(
		'\u00b7'
		|
		'\u02d0'
		|
		'\u02d1'
		|
		'\u0387'
		|
		'\u0640'
		|
		'\u0e46'
		|
		'\u0ec6'
		|
		'\u3005'
		|
		'\u3031'..'\u3035'
		|
		'\u309d'..'\u309e'
		|
		'\u30fc'..'\u30fe'
	)
	;

