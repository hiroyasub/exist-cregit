begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|// $ANTLR 2.7.7 (2006-11-01): "XQDocParser.g" -> "XQDocParser.java"$
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|xqdoc
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
name|xqdoc
operator|.
name|XQDocHelper
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

begin_class
specifier|public
class|class
name|XQDocParser
extends|extends
name|antlr
operator|.
name|LLkParser
implements|implements
name|XQDocParserTokenTypes
block|{
specifier|protected
name|XQDocParser
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
name|XQDocParser
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
name|XQDocParser
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
name|XQDocParser
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
name|XQDocParser
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
name|xqdocComment
parameter_list|(
name|XQDocHelper
name|doc
parameter_list|)
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|c
decl_stmt|;
try|try
block|{
comment|// for error handling
name|match
argument_list|(
name|XQDOC_START
argument_list|)
expr_stmt|;
block|{
name|_loop5
label|:
do|do
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
name|TAG
case|:
block|{
name|taggedContents
argument_list|(
name|doc
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|TRIM
case|:
case|case
name|SIMPLE_COLON
case|:
case|case
name|CHARS
case|:
block|{
name|c
operator|=
name|contents
argument_list|()
expr_stmt|;
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|addDescription
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
default|default:
block|{
break|break
name|_loop5
break|;
block|}
block|}
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
name|match
argument_list|(
name|XQDOC_END
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|reportError
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|recover
argument_list|(
name|ex
argument_list|,
name|_tokenSet_0
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
specifier|public
specifier|final
name|void
name|taggedContents
parameter_list|(
name|XQDocHelper
name|doc
parameter_list|)
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|Token
name|t
init|=
literal|null
decl_stmt|;
name|String
name|c
decl_stmt|;
try|try
block|{
comment|// for error handling
name|t
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|TAG
argument_list|)
expr_stmt|;
name|c
operator|=
name|contents
argument_list|()
expr_stmt|;
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|doc
operator|.
name|setTag
argument_list|(
name|t
operator|.
name|getText
argument_list|()
argument_list|,
name|c
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|reportError
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|recover
argument_list|(
name|ex
argument_list|,
name|_tokenSet_1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
block|}
specifier|public
specifier|final
name|String
name|contents
parameter_list|()
throws|throws
name|RecognitionException
throws|,
name|TokenStreamException
block|{
name|String
name|content
decl_stmt|;
name|Token
name|c
init|=
literal|null
decl_stmt|;
name|content
operator|=
literal|null
expr_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
block|{
comment|// for error handling
block|{
name|int
name|_cnt9
init|=
literal|0
decl_stmt|;
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
name|TRIM
operator|)
condition|)
block|{
name|match
argument_list|(
name|TRIM
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|SIMPLE_COLON
operator|)
condition|)
block|{
name|match
argument_list|(
name|SIMPLE_COLON
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
if|if
condition|(
name|buf
operator|.
name|charAt
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|!=
literal|'\n'
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
operator|(
name|LA
argument_list|(
literal|1
argument_list|)
operator|==
name|CHARS
operator|)
condition|)
block|{
name|c
operator|=
name|LT
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|match
argument_list|(
name|CHARS
argument_list|)
expr_stmt|;
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|c
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|_cnt9
operator|>=
literal|1
condition|)
block|{
break|break
name|_loop9
break|;
block|}
else|else
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
name|_cnt9
operator|++
expr_stmt|;
block|}
do|while
condition|(
literal|true
condition|)
do|;
block|}
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|content
operator|=
name|buf
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|ex
parameter_list|)
block|{
if|if
condition|(
name|inputState
operator|.
name|guessing
operator|==
literal|0
condition|)
block|{
name|reportError
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|recover
argument_list|(
name|ex
argument_list|,
name|_tokenSet_1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|ex
throw|;
block|}
block|}
return|return
name|content
return|;
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
literal|"XQDOC_START"
block|,
literal|"TAG"
block|,
literal|"XQDOC_END"
block|,
literal|"TRIM"
block|,
literal|"SIMPLE_COLON"
block|,
literal|"CHARS"
block|,
literal|"AT"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
index|[]
name|mk_tokenSet_0
parameter_list|()
block|{
name|long
index|[]
name|data
init|=
block|{
literal|2L
block|,
literal|0L
block|}
decl_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
specifier|final
name|BitSet
name|_tokenSet_0
init|=
operator|new
name|BitSet
argument_list|(
name|mk_tokenSet_0
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|long
index|[]
name|mk_tokenSet_1
parameter_list|()
block|{
name|long
index|[]
name|data
init|=
block|{
literal|992L
block|,
literal|0L
block|}
decl_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
specifier|final
name|BitSet
name|_tokenSet_1
init|=
operator|new
name|BitSet
argument_list|(
name|mk_tokenSet_1
argument_list|()
argument_list|)
decl_stmt|;
block|}
end_class

end_unit
