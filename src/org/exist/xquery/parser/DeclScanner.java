begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// $ANTLR 2.7.7 (2006-11-01): "DeclScanner.g" -> "DeclScanner.java"$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|parser
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenBuffer
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamIOException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|ANTLRException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|LLkParser
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|Token
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStream
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|RecognitionException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|NoViableAltException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|MismatchedTokenException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|SemanticException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|ParserSharedInputState
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|impl
operator|.
name|BitSet
import|;
end_import

begin_comment
comment|/**  * Try to read the XQuery declaration. The purpose of this class is to determine  * the content encoding of an XQuery. It just reads until it finds an XQuery declaration  * and throws an XPathException afterwards. It also throws a RecognitionException  * if something else than a comment, a pragma or an XQuery declaration is found.  *   * The declared encoding can then be retrieved from getEncoding().  */
end_comment

begin_class
specifier|public
class|class
name|DeclScanner
extends|extends
name|antlr
operator|.
name|LLkParser
implements|implements
name|DeclScannerTokenTypes
block|{
specifier|private
name|String
name|encoding
init|=
literal|null
decl_stmt|;
specifier|public
name|String
name|getEncoding
parameter_list|()
block|{
return|return
name|encoding
return|;
block|}
specifier|protected
name|DeclScanner
parameter_list|(
name|TokenBuffer
name|tokenBuf
parameter_list|,
name|int
name|k
parameter_list|)
block|{
name|super
argument_list|(
name|tokenBuf
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|tokenNames
operator|=
name|_tokenNames
expr_stmt|;
block|}
specifier|public
name|DeclScanner
parameter_list|(
name|TokenBuffer
name|tokenBuf
parameter_list|)
block|{
name|this
argument_list|(
name|tokenBuf
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|DeclScanner
parameter_list|(
name|TokenStream
name|lexer
parameter_list|,
name|int
name|k
parameter_list|)
block|{
name|super
argument_list|(
name|lexer
argument_list|,
name|k
argument_list|)
expr_stmt|;
name|tokenNames
operator|=
name|_tokenNames
expr_stmt|;
block|}
specifier|public
name|DeclScanner
parameter_list|(
name|TokenStream
name|lexer
parameter_list|)
block|{
name|this
argument_list|(
name|lexer
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DeclScanner
parameter_list|(
name|ParserSharedInputState
name|state
parameter_list|)
block|{
name|super
argument_list|(
name|state
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|tokenNames
operator|=
name|_tokenNames
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|versionDecl
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
throws|,
name|XPathException
block|{
name|Token
name|v
init|=
literal|null
decl_stmt|;
name|Token
name|enc
init|=
literal|null
decl_stmt|;
name|match
argument_list|(
name|LITERAL_xquery
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|LITERAL_version
argument_list|)
expr_stmt|;
name|v
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|STRING_LITERAL
argument_list|)
expr_stmt|;
block|{
switch|switch
condition|(
name|LA
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
name|LITERAL_encoding
case|:
block|{
name|match
argument_list|(
name|LITERAL_encoding
argument_list|)
expr_stmt|;
name|enc
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|STRING_LITERAL
argument_list|)
expr_stmt|;
name|encoding
operator|=
name|enc
operator|.
name|getText
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|EOF
case|:
block|{
break|break;
block|}
default|default:
block|{
throw|throw
operator|new
name|NoViableAltException
argument_list|(
name|LT
argument_list|(
literal|1
argument_list|)
argument_list|,
name|getFilename
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Processing stopped"
argument_list|)
throw|;
block|}
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|_tokenNames
init|=
block|{
literal|"<0>"
block|,
literal|"EOF"
block|,
literal|"<2>"
block|,
literal|"NULL_TREE_LOOKAHEAD"
block|,
literal|"QNAME"
block|,
literal|"PREDICATE"
block|,
literal|"FLWOR"
block|,
literal|"PARENTHESIZED"
block|,
literal|"ABSOLUTE_SLASH"
block|,
literal|"ABSOLUTE_DSLASH"
block|,
literal|"WILDCARD"
block|,
literal|"PREFIX_WILDCARD"
block|,
literal|"FUNCTION"
block|,
literal|"UNARY_MINUS"
block|,
literal|"UNARY_PLUS"
block|,
literal|"XPOINTER"
block|,
literal|"XPOINTER_ID"
block|,
literal|"VARIABLE_REF"
block|,
literal|"VARIABLE_BINDING"
block|,
literal|"ELEMENT"
block|,
literal|"ATTRIBUTE"
block|,
literal|"ATTRIBUTE_CONTENT"
block|,
literal|"TEXT"
block|,
literal|"VERSION_DECL"
block|,
literal|"NAMESPACE_DECL"
block|,
literal|"DEF_NAMESPACE_DECL"
block|,
literal|"DEF_COLLATION_DECL"
block|,
literal|"DEF_FUNCTION_NS_DECL"
block|,
literal|"GLOBAL_VAR"
block|,
literal|"FUNCTION_DECL"
block|,
literal|"PROLOG"
block|,
literal|"OPTION"
block|,
literal|"ATOMIC_TYPE"
block|,
literal|"MODULE"
block|,
literal|"ORDER_BY"
block|,
literal|"GROUP_BY"
block|,
literal|"POSITIONAL_VAR"
block|,
literal|"MODULE_DECL"
block|,
literal|"MODULE_IMPORT"
block|,
literal|"SCHEMA_IMPORT"
block|,
literal|"ATTRIBUTE_TEST"
block|,
literal|"COMP_ELEM_CONSTRUCTOR"
block|,
literal|"COMP_ATTR_CONSTRUCTOR"
block|,
literal|"COMP_TEXT_CONSTRUCTOR"
block|,
literal|"COMP_COMMENT_CONSTRUCTOR"
block|,
literal|"COMP_PI_CONSTRUCTOR"
block|,
literal|"COMP_NS_CONSTRUCTOR"
block|,
literal|"COMP_DOC_CONSTRUCTOR"
block|,
literal|"PRAGMA"
block|,
literal|"\"xpointer\""
block|,
literal|"opening parenthesis '('"
block|,
literal|"closing parenthesis ')'"
block|,
literal|"name"
block|,
literal|"\"xquery\""
block|,
literal|"\"version\""
block|,
literal|"semicolon ';'"
block|,
literal|"\"module\""
block|,
literal|"\"namespace\""
block|,
literal|"="
block|,
literal|"string literal"
block|,
literal|"\"declare\""
block|,
literal|"\"default\""
block|,
literal|"\"boundary-space\""
block|,
literal|"\"ordering\""
block|,
literal|"\"construction\""
block|,
literal|"\"base-uri\""
block|,
literal|"\"copy-namespaces\""
block|,
literal|"\"option\""
block|,
literal|"\"function\""
block|,
literal|"\"variable\""
block|,
literal|"\"import\""
block|,
literal|"\"encoding\""
block|,
literal|"\"collation\""
block|,
literal|"\"element\""
block|,
literal|"\"order\""
block|,
literal|"\"empty\""
block|,
literal|"\"greatest\""
block|,
literal|"\"least\""
block|,
literal|"\"preserve\""
block|,
literal|"\"strip\""
block|,
literal|"\"ordered\""
block|,
literal|"\"unordered\""
block|,
literal|"COMMA"
block|,
literal|"\"no-preserve\""
block|,
literal|"\"inherit\""
block|,
literal|"\"no-inherit\""
block|,
literal|"dollar sign '$'"
block|,
literal|"opening curly brace '{'"
block|,
literal|"closing curly brace '{'"
block|,
literal|"COLON"
block|,
literal|"\"external\""
block|,
literal|"\"schema\""
block|,
literal|"\"as\""
block|,
literal|"\"at\""
block|,
literal|"\"empty-sequence\""
block|,
literal|"question mark '?'"
block|,
literal|"wildcard '*'"
block|,
literal|"+"
block|,
literal|"\"item\""
block|,
literal|"\"for\""
block|,
literal|"\"let\""
block|,
literal|"\"some\""
block|,
literal|"\"every\""
block|,
literal|"\"if\""
block|,
literal|"\"typeswitch\""
block|,
literal|"\"update\""
block|,
literal|"\"replace\""
block|,
literal|"\"value\""
block|,
literal|"\"insert\""
block|,
literal|"\"delete\""
block|,
literal|"\"rename\""
block|,
literal|"\"with\""
block|,
literal|"\"into\""
block|,
literal|"\"preceding\""
block|,
literal|"\"following\""
block|,
literal|"\"where\""
block|,
literal|"\"return\""
block|,
literal|"\"in\""
block|,
literal|"\"by\""
block|,
literal|"\"stable\""
block|,
literal|"\"ascending\""
block|,
literal|"\"descending\""
block|,
literal|"\"group\""
block|,
literal|"\"satisfies\""
block|,
literal|"\"case\""
block|,
literal|"\"then\""
block|,
literal|"\"else\""
block|,
literal|"\"or\""
block|,
literal|"\"and\""
block|,
literal|"\"instance\""
block|,
literal|"\"of\""
block|,
literal|"\"treat\""
block|,
literal|"\"castable\""
block|,
literal|"\"cast\""
block|,
literal|"BEFORE"
block|,
literal|"AFTER"
block|,
literal|"\"eq\""
block|,
literal|"\"ne\""
block|,
literal|"\"lt\""
block|,
literal|"\"le\""
block|,
literal|"\"gt\""
block|,
literal|"\"ge\""
block|,
literal|"!="
block|,
literal|">"
block|,
literal|">="
block|,
literal|"<"
block|,
literal|"<="
block|,
literal|"\"is\""
block|,
literal|"\"isnot\""
block|,
literal|"fulltext operator '&='"
block|,
literal|"fulltext operator '|='"
block|,
literal|"\"to\""
block|,
literal|"-"
block|,
literal|"\"div\""
block|,
literal|"\"idiv\""
block|,
literal|"\"mod\""
block|,
literal|"PRAGMA_START"
block|,
literal|"pragma expression"
block|,
literal|"\"union\""
block|,
literal|"union"
block|,
literal|"\"intersect\""
block|,
literal|"\"except\""
block|,
literal|"single slash '/'"
block|,
literal|"double slash '//'"
block|,
literal|"\"text\""
block|,
literal|"\"node\""
block|,
literal|"\"attribute\""
block|,
literal|"\"comment\""
block|,
literal|"\"processing-instruction\""
block|,
literal|"\"document-node\""
block|,
literal|"\"document\""
block|,
literal|"."
block|,
literal|"XML comment"
block|,
literal|"processing instruction"
block|,
literal|"opening brace '['"
block|,
literal|"closing brace ']'"
block|,
literal|"@ char"
block|,
literal|".."
block|,
literal|"\"child\""
block|,
literal|"\"self\""
block|,
literal|"\"descendant\""
block|,
literal|"\"descendant-or-self\""
block|,
literal|"\"following-sibling\""
block|,
literal|"\"parent\""
block|,
literal|"\"ancestor\""
block|,
literal|"\"ancestor-or-self\""
block|,
literal|"\"preceding-sibling\""
block|,
literal|"DOUBLE_LITERAL"
block|,
literal|"DECIMAL_LITERAL"
block|,
literal|"INTEGER_LITERAL"
block|,
literal|"\"schema-element\""
block|,
literal|"XML end tag"
block|,
literal|"double quote '\\\"'"
block|,
literal|"single quote '"
block|,
literal|"QUOT_ATTRIBUTE_CONTENT"
block|,
literal|"ESCAPE_QUOT"
block|,
literal|"APOS_ATTRIBUTE_CONTENT"
block|,
literal|"ESCAPE_APOS"
block|,
literal|"ELEMENT_CONTENT"
block|,
literal|"end of XML comment"
block|,
literal|"end of processing instruction"
block|,
literal|"CDATA section"
block|,
literal|"\"collection\""
block|,
literal|"\"validate\""
block|,
literal|"start of processing instruction"
block|,
literal|"CDATA section start"
block|,
literal|"end of CDATA section"
block|,
literal|"LETTER"
block|,
literal|"DIGITS"
block|,
literal|"HEX_DIGITS"
block|,
literal|"NMSTART"
block|,
literal|"NMCHAR"
block|,
literal|"WS"
block|,
literal|"XQuery comment"
block|,
literal|"PREDEFINED_ENTITY_REF"
block|,
literal|"CHAR_REF"
block|,
literal|"S"
block|,
literal|"NEXT_TOKEN"
block|,
literal|"CHAR"
block|,
literal|"BASECHAR"
block|,
literal|"IDEOGRAPHIC"
block|,
literal|"COMBINING_CHAR"
block|,
literal|"DIGIT"
block|,
literal|"EXTENDER"
block|}
decl_stmt|;
block|}
end_class

end_unit

