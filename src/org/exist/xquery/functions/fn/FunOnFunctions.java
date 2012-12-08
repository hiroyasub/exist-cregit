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
name|FunctionParameterSequenceType
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
name|FunctionReference
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
name|FunctionReturnSequenceType
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
name|IntegerValue
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
name|QNameValue
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
name|Sequence
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
name|SequenceType
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
name|Type
import|;
end_import

begin_class
specifier|public
class|class
name|FunOnFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"function-lookup"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns a reference to the function having a given name and arity, if there is one,"
operator|+
literal|" the empty sequence otherwise"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Qualified name of the function"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arity"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The arity (number of arguments) of the function"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The function if found, empty sequence otherwise"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"function-name"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the name of the function identified by a function item."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function item"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|QNAME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The name of the function or the empty sequence if $function is an anonymous function."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"function-arity"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns the arity of the function identified by a function item."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function item"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The arity of the function."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunOnFunctions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
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
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|getContext
argument_list|()
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|30
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0003
argument_list|,
literal|"Function '"
operator|+
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' is only supported for xquery version \"3.0\" and later."
argument_list|)
throw|;
block|}
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
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"function-lookup"
argument_list|)
condition|)
block|{
name|QName
name|fname
init|=
operator|(
operator|(
name|QNameValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getQName
argument_list|()
decl_stmt|;
name|int
name|arity
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|FunctionCall
name|call
decl_stmt|;
try|try
block|{
name|call
operator|=
name|NamedFunctionReference
operator|.
name|lookupFunction
argument_list|(
name|this
argument_list|,
name|context
argument_list|,
name|fname
argument_list|,
name|arity
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getErrorCode
argument_list|()
operator|==
name|ErrorCodes
operator|.
name|XPST0017
condition|)
block|{
comment|// return empty sequence for all "function not found" related errors
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
throw|throw
name|e
throw|;
block|}
return|return
name|call
operator|==
literal|null
condition|?
name|Sequence
operator|.
name|EMPTY_SEQUENCE
else|:
operator|new
name|FunctionReference
argument_list|(
name|call
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"function-name"
argument_list|)
condition|)
block|{
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|QName
name|qname
init|=
name|ref
operator|.
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|qname
operator|==
literal|null
operator|||
name|qname
operator|==
name|InlineFunction
operator|.
name|INLINE_FUNCTION_QNAME
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
else|else
return|return
operator|new
name|QNameValue
argument_list|(
name|context
argument_list|,
name|qname
argument_list|)
return|;
block|}
else|else
block|{
comment|// isCalledAs("function-arity")
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntegerValue
argument_list|(
name|ref
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|XPathException
condition|)
throw|throw
operator|(
name|XPathException
operator|)
name|e
throw|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0017
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|FunctionCall
name|lookupFunction
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|arity
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// check if the function is from a module
name|Module
name|module
init|=
name|parent
operator|.
name|getContext
argument_list|()
operator|.
name|getModule
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|UserDefinedFunction
name|func
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
name|func
operator|=
name|parent
operator|.
name|getContext
argument_list|()
operator|.
name|resolveFunction
argument_list|(
name|qname
argument_list|,
name|arity
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|parent
argument_list|,
name|ErrorCodes
operator|.
name|XPST0017
argument_list|,
literal|"Cannot create a reference to an internal Java function"
argument_list|)
throw|;
block|}
name|func
operator|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|qname
argument_list|,
name|arity
argument_list|,
name|parent
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|func
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|FunctionCall
name|funcCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|parent
operator|.
name|getContext
argument_list|()
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|funcCall
operator|.
name|setLocation
argument_list|(
name|parent
operator|.
name|getLine
argument_list|()
argument_list|,
name|parent
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|funcCall
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// function not found: return empty sequence
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit

