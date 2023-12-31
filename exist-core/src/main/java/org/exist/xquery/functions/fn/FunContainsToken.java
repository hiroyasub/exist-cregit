begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|com
operator|.
name|ibm
operator|.
name|icu
operator|.
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Collations
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * fn:contains-token($input as xs:string*, $token as xs:string) as xs:boolean  * fn:contains-token($input as xs:string*, $token as xs:string, $collation as xs:string) as xs:boolean  *   * @author tuurma  * @see<a href="https://www.w3.org/TR/xpath-functions-31/#func-contains-token">https://www.w3.org/TR/xpath-functions-31/#func-contains-token</a>  */
end_comment

begin_class
specifier|public
class|class
name|FunContainsToken
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|QName
name|FS_CONTAINS_TOKEN_NAME
init|=
operator|new
name|QName
argument_list|(
literal|"contains-token"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|FS_INPUT
init|=
name|optManyParam
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"The input string"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|FS_TOKEN
init|=
name|param
argument_list|(
literal|"token"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"The token to be searched for"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionParameterSequenceType
name|FS_COLLATION
init|=
name|optParam
argument_list|(
literal|"pattern"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
literal|"Collation to use"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FS_CONTAINS_TOKEN
index|[]
init|=
name|functionSignatures
argument_list|(
name|FS_CONTAINS_TOKEN_NAME
argument_list|,
literal|"Determines whether or not any of the supplied strings, when tokenized at whitespace boundaries, "
operator|+
literal|"contains the supplied token, under the rules of the supplied collation."
argument_list|,
name|returns
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
literal|"The function returns true if and only if there is string in $input which, "
operator|+
literal|"after tokenizing at whitespace boundaries, contains a token that is equal to the trimmed value of $token "
operator|+
literal|"under the rules of the selected collation."
argument_list|)
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|(
name|FS_INPUT
argument_list|,
name|FS_TOKEN
argument_list|)
argument_list|,
name|arity
argument_list|(
name|FS_INPUT
argument_list|,
name|FS_TOKEN
argument_list|,
name|FS_COLLATION
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FunContainsToken
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
comment|/* for all further processing trimmed value of the token is used */
name|String
name|token
init|=
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|token
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
comment|/* tokenize all input on whitespace*/
name|ArrayList
argument_list|<
name|String
argument_list|>
name|fragments
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|args
index|[
literal|0
index|]
operator|.
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|String
index|[]
name|chunks
init|=
name|Option
operator|.
name|tokenize
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|chunk
range|:
name|chunks
control|)
block|{
name|fragments
operator|.
name|add
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
block|}
block|}
name|Collator
name|collator
init|=
name|context
operator|.
name|getDefaultCollator
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|>
literal|2
operator|&&
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|collator
operator|=
name|context
operator|.
name|getCollator
argument_list|(
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* return true only if some fragment matches the trimmed token under current collation */
for|for
control|(
name|String
name|fragment
range|:
name|fragments
control|)
block|{
if|if
condition|(
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|fragment
argument_list|,
name|token
argument_list|)
operator|==
name|Constants
operator|.
name|EQUAL
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
block|}
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
block|}
end_class

end_unit

