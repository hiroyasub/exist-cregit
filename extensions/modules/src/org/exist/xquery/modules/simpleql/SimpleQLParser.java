begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// $ANTLR 2.7.7 (2006-11-01): "SimpleQLParser.g" -> "SimpleQLParser.java"$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|simpleql
package|;
end_package

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

begin_class
specifier|public
class|class
name|SimpleQLParser
extends|extends
name|antlr
operator|.
name|LLkParser
implements|implements
name|SimpleQLParserTokenTypes
block|{
specifier|protected
name|SimpleQLParser
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
name|SimpleQLParser
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
name|SimpleQLParser
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
name|SimpleQLParser
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
name|SimpleQLParser
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
name|String
name|expr
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|str
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|s1
decl_stmt|,
name|s2
decl_stmt|;
name|s1
operator|=
name|orExpr
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s1
argument_list|)
expr_stmt|;
block|{
name|_loop3
label|:
do|do
block|{
if|if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_AND
operator|||
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_UND
operator|)
condition|)
block|{
name|and
argument_list|()
expr_stmt|;
name|s2
operator|=
name|orExpr
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" and "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break
name|_loop3
break|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
name|str
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
specifier|public
specifier|final
name|String
name|orExpr
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|str
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|s1
decl_stmt|,
name|s2
decl_stmt|;
name|s1
operator|=
name|notExpr
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s1
argument_list|)
expr_stmt|;
block|{
name|_loop6
label|:
do|do
block|{
if|if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_OR
operator|||
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_ODER
operator|)
condition|)
block|{
name|or
argument_list|()
expr_stmt|;
name|s2
operator|=
name|notExpr
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" or "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s2
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break
name|_loop6
break|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
name|str
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
specifier|public
specifier|final
name|void
name|and
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
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
name|LITERAL_AND
case|:
block|{
name|match
argument_list|(
name|LITERAL_AND
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|LITERAL_UND
case|:
block|{
name|match
argument_list|(
name|LITERAL_UND
argument_list|)
expr_stmt|;
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
specifier|public
specifier|final
name|String
name|notExpr
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|str
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|String
name|s
decl_stmt|;
name|s
operator|=
name|queryArg
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|{
name|_loop9
label|:
do|do
block|{
if|if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_NOT
operator|||
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|LITERAL_NICHT
operator|)
condition|)
block|{
name|not
argument_list|()
expr_stmt|;
name|s
operator|=
name|queryArg
argument_list|()
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" and not("
argument_list|)
operator|.
name|append
argument_list|(
name|s
argument_list|)
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break
name|_loop9
break|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
name|str
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
specifier|public
specifier|final
name|void
name|or
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
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
name|LITERAL_OR
case|:
block|{
name|match
argument_list|(
name|LITERAL_OR
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|LITERAL_ODER
case|:
block|{
name|match
argument_list|(
name|LITERAL_ODER
argument_list|)
expr_stmt|;
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
specifier|public
specifier|final
name|String
name|queryArg
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|arg
decl_stmt|;
name|Token
name|l
init|=
literal|null
decl_stmt|;
name|Token
name|r
init|=
literal|null
decl_stmt|;
name|Token
name|w2
init|=
literal|null
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|LA
argument_list|(
literal|1
argument_list|)
condition|)
block|{
case|case
name|STRING_LITERAL
case|:
block|{
name|l
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
name|buf
operator|.
name|append
argument_list|(
literal|"near(., \""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|l
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
expr_stmt|;
name|arg
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|REGEXP
case|:
block|{
name|r
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|REGEXP
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"match-all(., \""
argument_list|)
operator|.
name|append
argument_list|(
name|r
operator|.
name|getText
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\")"
argument_list|)
expr_stmt|;
name|arg
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|EOF
case|:
case|case
name|WORD
case|:
case|case
name|LITERAL_AND
case|:
case|case
name|LITERAL_UND
case|:
case|case
name|LITERAL_OR
case|:
case|case
name|LITERAL_ODER
case|:
case|case
name|LITERAL_NOT
case|:
case|case
name|LITERAL_NICHT
case|:
block|{
block|{
name|_loop12
label|:
do|do
block|{
if|if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|WORD
operator|)
condition|)
block|{
name|w2
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|WORD
argument_list|)
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|w2
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
break|break
name|_loop12
break|;
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
name|buf
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
literal|".&= \""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
name|arg
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
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
return|return
name|arg
return|;
block|}
specifier|public
specifier|final
name|void
name|not
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
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
name|LITERAL_NOT
case|:
block|{
name|match
argument_list|(
name|LITERAL_NOT
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|LITERAL_NICHT
case|:
block|{
name|match
argument_list|(
name|LITERAL_NICHT
argument_list|)
expr_stmt|;
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
literal|"string literal"
block|,
literal|"regular expression"
block|,
literal|"WORD"
block|,
literal|"\"AND\""
block|,
literal|"\"UND\""
block|,
literal|"\"OR\""
block|,
literal|"\"ODER\""
block|,
literal|"\"NOT\""
block|,
literal|"\"NICHT\""
block|,
literal|"WS"
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

