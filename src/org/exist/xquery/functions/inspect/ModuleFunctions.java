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
name|inspect
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
name|security
operator|.
name|xacml
operator|.
name|AccessContext
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
name|functions
operator|.
name|fn
operator|.
name|FunOnFunctions
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
name|parser
operator|.
name|XQueryAST
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
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|ModuleFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|static
specifier|final
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
literal|"module-functions"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence of function items pointing to each public function in the current module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{}
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
name|ZERO_OR_MORE
argument_list|,
literal|"Sequence of function items containing all public functions in the current module or the empty sequence "
operator|+
literal|"if the module is not known in the current context."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module-functions"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence of function items pointing to each public function in the specified module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"location"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location URI of the module to be loaded."
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
name|ZERO_OR_MORE
argument_list|,
literal|"Sequence of function items containing all public functions in the module or the empty sequence "
operator|+
literal|"if the module is not known in the current context."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"module-functions-by-uri"
argument_list|,
name|InspectionModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|InspectionModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns a sequence of function items pointing to each public function in the specified module."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The URI of the module to be loaded."
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
name|ZERO_OR_MORE
argument_list|,
literal|"Sequence of function items containing all public functions in the module or the empty sequence "
operator|+
literal|"if the module is not known in the current context."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|ModuleFunctions
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
name|ValueSequence
name|list
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|XQueryContext
name|tempContext
init|=
operator|new
name|XQueryContext
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|XMLDB
argument_list|)
decl_stmt|;
name|tempContext
operator|.
name|setModuleLoadPath
argument_list|(
name|context
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|)
expr_stmt|;
name|Module
name|module
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"module-functions-by-uri"
argument_list|)
condition|)
name|module
operator|=
name|tempContext
operator|.
name|importModule
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
else|else
name|module
operator|=
name|tempContext
operator|.
name|importModule
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
name|addFunctionRefsFromModule
argument_list|(
name|tempContext
argument_list|,
name|list
argument_list|,
name|module
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|addFunctionRefsFromContext
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
specifier|private
name|void
name|addFunctionRefsFromModule
parameter_list|(
name|XQueryContext
name|tempContext
parameter_list|,
name|ValueSequence
name|resultSeq
parameter_list|,
name|Module
name|module
parameter_list|)
throws|throws
name|XPathException
block|{
name|FunctionSignature
name|signatures
index|[]
init|=
name|module
operator|.
name|listFunctions
argument_list|()
decl_stmt|;
for|for
control|(
name|FunctionSignature
name|signature
range|:
name|signatures
control|)
block|{
if|if
condition|(
operator|!
name|signature
operator|.
name|isPrivate
argument_list|()
condition|)
block|{
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
name|int
name|arity
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|isOverloaded
argument_list|()
condition|)
name|arity
operator|=
name|signature
operator|.
name|getArgumentTypes
argument_list|()
operator|.
name|length
expr_stmt|;
else|else
name|arity
operator|=
name|signature
operator|.
name|getArgumentCount
argument_list|()
expr_stmt|;
name|FunctionDef
name|def
init|=
operator|(
operator|(
name|InternalModule
operator|)
name|module
operator|)
operator|.
name|getFunctionDef
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|,
name|arity
argument_list|)
decl_stmt|;
name|XQueryAST
name|ast
init|=
operator|new
name|XQueryAST
argument_list|()
decl_stmt|;
name|ast
operator|.
name|setLine
argument_list|(
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|ast
operator|.
name|setColumn
argument_list|(
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|args
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|(
name|arity
argument_list|)
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
name|arity
condition|;
name|i
operator|++
control|)
block|{
name|args
operator|.
name|add
argument_list|(
operator|new
name|Function
operator|.
name|Placeholder
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Function
name|fn
init|=
name|Function
operator|.
name|createFunction
argument_list|(
name|tempContext
argument_list|,
name|ast
argument_list|,
name|def
argument_list|)
decl_stmt|;
name|fn
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|InternalFunctionCall
name|call
init|=
operator|new
name|InternalFunctionCall
argument_list|(
name|fn
argument_list|)
decl_stmt|;
name|FunctionCall
name|ref
init|=
name|FunctionFactory
operator|.
name|wrap
argument_list|(
name|tempContext
argument_list|,
name|call
argument_list|)
decl_stmt|;
name|resultSeq
operator|.
name|addAll
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|ref
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|UserDefinedFunction
name|func
init|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|,
name|signature
operator|.
name|getArgumentCount
argument_list|()
argument_list|,
name|tempContext
argument_list|)
decl_stmt|;
comment|// could be null if private function
if|if
condition|(
name|func
operator|!=
literal|null
condition|)
block|{
comment|// create function reference
name|FunctionCall
name|funcCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|tempContext
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|funcCall
operator|.
name|setLocation
argument_list|(
name|getLine
argument_list|()
argument_list|,
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|funcCall
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|void
name|addFunctionRefsFromContext
parameter_list|(
name|ValueSequence
name|resultSeq
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|UserDefinedFunction
argument_list|>
name|i
init|=
name|context
operator|.
name|localFunctions
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|UserDefinedFunction
name|f
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|FunctionCall
name|call
init|=
name|FunOnFunctions
operator|.
name|lookupFunction
argument_list|(
name|this
argument_list|,
name|f
operator|.
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|f
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
condition|)
block|{
name|resultSeq
operator|.
name|add
argument_list|(
operator|new
name|FunctionReference
argument_list|(
name|call
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

