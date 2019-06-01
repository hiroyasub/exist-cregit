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
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|FileSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|SourceFactory
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
name|Module
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
name|functions
operator|.
name|fn
operator|.
name|LoadXQueryModule
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
name|javax
operator|.
name|xml
operator|.
name|xpath
operator|.
name|XPath
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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
specifier|final
specifier|static
name|FunctionSignature
name|FNS_MODULE_FUNCTIONS_CURRENT
init|=
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
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_MODULE_FUNCTIONS_OTHER
init|=
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
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_MODULE_FUNCTIONS_OTHER_URI
init|=
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
specifier|final
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
specifier|final
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
name|context
operator|.
name|getProfiler
argument_list|()
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
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"module-functions-by-uri"
argument_list|)
condition|)
block|{
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
block|}
else|else
block|{
specifier|final
name|URI
name|locationUri
init|=
operator|(
operator|(
name|AnyURIValue
operator|)
name|args
index|[
literal|0
index|]
operator|)
operator|.
name|toURI
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|Source
name|source
init|=
name|SourceFactory
operator|.
name|getSource
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|tempContext
operator|.
name|getModuleLoadPath
argument_list|()
argument_list|,
name|locationUri
operator|.
name|toString
argument_list|()
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|source
operator|!=
literal|null
condition|)
block|{
name|tempContext
operator|.
name|setSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
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
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Failed to import module: "
operator|+
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getErrorCode
argument_list|()
operator|.
name|equals
argument_list|(
name|ErrorCodes
operator|.
name|XPST0003
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
comment|// ensure variable declarations in the imported module are analyzed.
comment|// unlike when using a normal import statement, this is not done automatically
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|analyzeGlobalVars
argument_list|()
expr_stmt|;
block|}
name|LoadXQueryModule
operator|.
name|addFunctionRefsFromModule
argument_list|(
name|this
argument_list|,
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
specifier|final
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
specifier|final
name|UserDefinedFunction
name|f
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
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
