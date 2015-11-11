begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2012, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
package|;
end_package

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
name|net
operator|.
name|URISyntaxException
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
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceCompiledXQueryCache
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|adapters
operator|.
name|SequenceAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|adapters
operator|.
name|TypeAdapter
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
name|memtree
operator|.
name|DocumentImpl
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
name|Permission
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
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|ProcessMonitor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|AbstractExpression
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
name|AnalyzeContextInfo
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
name|CompiledXQuery
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
name|Expression
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
name|FunctionCall
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
name|UserDefinedFunction
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
name|VariableDeclaration
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
name|XPathException
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
name|XQueryContext
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
name|util
operator|.
name|ExpressionDumper
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
name|AnyURIValue
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
name|AtomicValue
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
name|Base64BinaryValueType
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
name|BinaryValueFromInputStream
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
name|BooleanValue
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
name|DateTimeValue
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
name|DateValue
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
name|DecimalValue
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
name|DoubleValue
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
name|FloatValue
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
name|Item
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
name|StringValue
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
name|TimeValue
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
name|ValueSequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|Namespace
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|ResourceFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|ResourceFunctionExecuter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|TypedArgumentValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|TypedValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|FunctionSignature
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|ResourceFunctionExecutorImpl
implements|implements
name|ResourceFunctionExecuter
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ResourceFunctionExecutorImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|XQ_VAR_BASE_URI
init|=
operator|new
name|QName
argument_list|(
literal|"base-uri"
argument_list|,
name|Namespace
operator|.
name|ANNOTATION_NS
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|XQ_VAR_URI
init|=
operator|new
name|QName
argument_list|(
literal|"uri"
argument_list|,
name|Namespace
operator|.
name|ANNOTATION_NS
argument_list|)
decl_stmt|;
comment|//TODO generalise with RequestModule
specifier|private
specifier|final
specifier|static
name|String
name|EXQ_REQUEST_ATTR
init|=
literal|"exquery-request"
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|private
specifier|final
name|String
name|uri
decl_stmt|;
specifier|private
specifier|final
name|String
name|baseUri
decl_stmt|;
specifier|public
name|ResourceFunctionExecutorImpl
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|,
specifier|final
name|String
name|baseUri
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|brokerPool
operator|=
name|brokerPool
expr_stmt|;
name|this
operator|.
name|baseUri
operator|=
name|baseUri
expr_stmt|;
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|private
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|brokerPool
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|execute
parameter_list|(
specifier|final
name|ResourceFunction
name|resourceFunction
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|TypedArgumentValue
argument_list|>
name|arguments
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|)
throws|throws
name|RestXqServiceException
block|{
specifier|final
name|RestXqServiceCompiledXQueryCache
name|cache
init|=
name|RestXqServiceCompiledXQueryCacheImpl
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|xquery
init|=
literal|null
decl_stmt|;
name|ProcessMonitor
name|processMonitor
init|=
literal|null
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getBrokerPool
argument_list|()
operator|.
name|getBroker
argument_list|()
init|)
block|{
comment|//ensure we can execute the function before going any further
name|checkSecurity
argument_list|(
name|broker
argument_list|,
name|resourceFunction
operator|.
name|getXQueryLocation
argument_list|()
argument_list|)
expr_stmt|;
comment|//get a compiled query service from the cache
name|xquery
operator|=
name|cache
operator|.
name|getCompiledQuery
argument_list|(
name|broker
argument_list|,
name|resourceFunction
operator|.
name|getXQueryLocation
argument_list|()
argument_list|)
expr_stmt|;
comment|//find the function that we will execute
specifier|final
name|UserDefinedFunction
name|fn
init|=
name|findFunction
argument_list|(
name|xquery
argument_list|,
name|resourceFunction
operator|.
name|getFunctionSignature
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|XQueryContext
name|xqueryContext
init|=
name|xquery
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//set the request object - can later be used by the EXQuery Request Module
name|xqueryContext
operator|.
name|setAttribute
argument_list|(
name|EXQ_REQUEST_ATTR
argument_list|,
name|request
argument_list|)
expr_stmt|;
comment|//TODO this is a workaround?
name|declareVariables
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
comment|//START workaround: evaluate global variables in modules, as they are reset by XQueryContext.reset()
specifier|final
name|Expression
name|rootExpr
init|=
name|xqueryContext
operator|.
name|getRootExpression
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
name|rootExpr
operator|.
name|getSubExpressionCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Expression
name|subExpr
init|=
name|rootExpr
operator|.
name|getSubExpression
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|subExpr
operator|instanceof
name|VariableDeclaration
condition|)
block|{
name|subExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|//END workaround
comment|//setup monitoring
name|processMonitor
operator|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getProcessMonitor
argument_list|()
expr_stmt|;
name|xqueryContext
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceQueryStart
argument_list|()
expr_stmt|;
name|processMonitor
operator|.
name|queryStarted
argument_list|(
name|xqueryContext
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
comment|//create a function call
specifier|final
name|FunctionReference
name|fnRef
init|=
operator|new
name|FunctionReference
argument_list|(
operator|new
name|FunctionCall
argument_list|(
name|xqueryContext
argument_list|,
name|fn
argument_list|)
argument_list|)
decl_stmt|;
comment|//convert the arguments
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
index|[]
name|fnArgs
init|=
name|convertToExistFunctionArguments
argument_list|(
name|xqueryContext
argument_list|,
name|fn
argument_list|,
name|arguments
argument_list|)
decl_stmt|;
comment|//execute the function call
name|fnRef
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|result
init|=
name|fnRef
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|fnArgs
argument_list|)
decl_stmt|;
return|return
operator|new
name|SequenceAdapter
argument_list|(
name|result
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
decl||
name|EXistException
decl||
name|XPathException
decl||
name|PermissionDeniedException
name|use
parameter_list|)
block|{
throw|throw
operator|new
name|RestXqServiceException
argument_list|(
name|use
operator|.
name|getMessage
argument_list|()
argument_list|,
name|use
argument_list|)
throw|;
block|}
finally|finally
block|{
comment|//clear down monitoring
if|if
condition|(
name|processMonitor
operator|!=
literal|null
condition|)
block|{
name|xquery
operator|.
name|getContext
argument_list|()
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceQueryEnd
argument_list|(
name|xquery
operator|.
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|processMonitor
operator|.
name|queryCompleted
argument_list|(
name|xquery
operator|.
name|getContext
argument_list|()
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|xquery
operator|!=
literal|null
condition|)
block|{
comment|//return the compiled query to the pool
name|cache
operator|.
name|returnCompiledQuery
argument_list|(
name|resourceFunction
operator|.
name|getXQueryLocation
argument_list|()
argument_list|,
name|xquery
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|declareVariables
parameter_list|(
specifier|final
name|XQueryContext
name|xqueryContext
parameter_list|)
throws|throws
name|XPathException
block|{
name|xqueryContext
operator|.
name|declareVariable
argument_list|(
name|XQ_VAR_BASE_URI
argument_list|,
name|baseUri
argument_list|)
expr_stmt|;
name|xqueryContext
operator|.
name|declareVariable
argument_list|(
name|XQ_VAR_URI
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
comment|/**      * Ensures that the xqueryLocation has READ and EXECUTE access      *       * @param broker The current broker      * @param xqueryLocation The xquery to check permissions for      *       * @throws URISyntaxException if the xqueryLocation cannot be parsed      * @throws PermissionDeniedException if there is not READ and EXECUTE access on the xqueryLocation for the current user      */
specifier|private
name|void
name|checkSecurity
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|URI
name|xqueryLocation
parameter_list|)
throws|throws
name|URISyntaxException
throws|,
name|PermissionDeniedException
block|{
name|broker
operator|.
name|getResource
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|xqueryLocation
argument_list|)
argument_list|,
name|Permission
operator|.
name|READ
operator||
name|Permission
operator|.
name|EXECUTE
argument_list|)
expr_stmt|;
block|}
comment|/**      * Lookup a Function in an XQuery given a Function Signature      *       * @param xquery The XQuery to interrogate      * @param functionSignature The Function Signature to use to match a Function      *       * @return The Function from the XQuery matching the Function Signature      */
specifier|private
name|UserDefinedFunction
name|findFunction
parameter_list|(
specifier|final
name|CompiledXQuery
name|xquery
parameter_list|,
specifier|final
name|FunctionSignature
name|functionSignature
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|QName
name|fnName
init|=
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|functionSignature
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|int
name|arity
init|=
name|functionSignature
operator|.
name|getArgumentCount
argument_list|()
decl_stmt|;
return|return
name|xquery
operator|.
name|getContext
argument_list|()
operator|.
name|resolveFunction
argument_list|(
name|fnName
argument_list|,
name|arity
argument_list|)
return|;
block|}
comment|/**      * Creates converts function arguments from EXQuery to eXist-db types      *       * @param xqueryContext The XQuery Context of the XQuery containing the Function Call      * @param fn The Function in the XQuery to create a Function Call for      * @param arguments The arguments to be passed to the Function when its invoked      *       * @return The arguments ready to pass to the Function Call when it is invoked      */
specifier|private
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
index|[]
name|convertToExistFunctionArguments
parameter_list|(
specifier|final
name|XQueryContext
name|xqueryContext
parameter_list|,
specifier|final
name|UserDefinedFunction
name|fn
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|TypedArgumentValue
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
throws|,
name|RestXqServiceException
block|{
specifier|final
name|List
argument_list|<
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
argument_list|>
name|fnArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceType
name|argumentType
range|:
name|fn
operator|.
name|getSignature
argument_list|()
operator|.
name|getArgumentTypes
argument_list|()
control|)
block|{
specifier|final
name|FunctionParameterSequenceType
name|fnParameter
init|=
operator|(
name|FunctionParameterSequenceType
operator|)
name|argumentType
decl_stmt|;
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|fnArg
init|=
literal|null
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|TypedArgumentValue
name|argument
range|:
name|arguments
control|)
block|{
specifier|final
name|String
name|argumentName
init|=
name|argument
operator|.
name|getArgumentName
argument_list|()
decl_stmt|;
if|if
condition|(
name|argumentName
operator|!=
literal|null
operator|&&
name|argumentName
operator|.
name|equals
argument_list|(
name|fnParameter
operator|.
name|getAttributeName
argument_list|()
argument_list|)
condition|)
block|{
name|fnArg
operator|=
name|convertToExistSequence
argument_list|(
name|xqueryContext
argument_list|,
name|argument
argument_list|,
name|fnParameter
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
comment|//value is not always provided, e.g. by PathAnnotation, so use empty sequence
comment|//TODO do we need to check the cardinality of the receiving arg to make sure it permits ZERO?
comment|//argumentType.getCardinality();
comment|//create the empty sequence
name|fnArg
operator|=
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
name|fnArgs
operator|.
name|add
argument_list|(
name|fnArg
argument_list|)
expr_stmt|;
block|}
return|return
name|fnArgs
operator|.
name|toArray
argument_list|(
operator|new
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
index|[
name|fnArgs
operator|.
name|size
argument_list|()
index|]
argument_list|)
return|;
block|}
comment|//TODO this needs to be abstracted into EXQuery library / or not, see the TODOs below
specifier|private
parameter_list|<
name|X
parameter_list|>
name|TypedValue
argument_list|<
name|X
argument_list|>
name|convertToType
parameter_list|(
specifier|final
name|XQueryContext
name|xqueryContext
parameter_list|,
specifier|final
name|String
name|argumentName
parameter_list|,
specifier|final
name|TypedValue
name|typedValue
parameter_list|,
specifier|final
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Type
name|destinationType
parameter_list|,
specifier|final
name|Class
argument_list|<
name|X
argument_list|>
name|underlyingDestinationClass
parameter_list|)
throws|throws
name|RestXqServiceException
block|{
comment|//TODO consider changing Types that can be used as<T> to TypedValue to a set of interfaces for XDM types that
comment|//require absolute minimal implementation, and we provide some default or abstract implementations if possible
specifier|final
name|Item
name|convertedValue
decl_stmt|;
try|try
block|{
specifier|final
name|int
name|existDestinationType
init|=
name|TypeAdapter
operator|.
name|toExistType
argument_list|(
name|destinationType
argument_list|)
decl_stmt|;
specifier|final
name|Item
name|value
decl_stmt|;
comment|//TODO This type system is a complete mess:
comment|//EXQuery XDM should not have any concrete types, just interfaces
comment|//some of the abstract code in EXQuery needs to be able to instantiate types.
comment|//Consider a factory or java.util.ServiceLoader pattern
if|if
condition|(
name|typedValue
operator|instanceof
name|org
operator|.
name|exquery
operator|.
name|xdm
operator|.
name|type
operator|.
name|StringTypedValue
condition|)
block|{
name|value
operator|=
operator|new
name|StringValue
argument_list|(
operator|(
operator|(
name|org
operator|.
name|exquery
operator|.
name|xdm
operator|.
name|type
operator|.
name|StringTypedValue
operator|)
name|typedValue
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|typedValue
operator|instanceof
name|org
operator|.
name|exquery
operator|.
name|xdm
operator|.
name|type
operator|.
name|Base64BinaryTypedValue
condition|)
block|{
name|value
operator|=
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|xqueryContext
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|(
operator|(
name|org
operator|.
name|exquery
operator|.
name|xdm
operator|.
name|type
operator|.
name|Base64BinaryTypedValue
operator|)
name|typedValue
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|value
operator|=
operator|(
name|Item
operator|)
name|typedValue
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|existDestinationType
operator|==
name|value
operator|.
name|getType
argument_list|()
condition|)
block|{
name|convertedValue
operator|=
name|value
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|AtomicValue
condition|)
block|{
name|convertedValue
operator|=
name|value
operator|.
name|convertTo
argument_list|(
name|existDestinationType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not convert parameter '"
operator|+
name|argumentName
operator|+
literal|"' from '"
operator|+
name|typedValue
operator|.
name|getType
argument_list|()
operator|.
name|name
argument_list|()
operator|+
literal|"' to '"
operator|+
name|destinationType
operator|.
name|name
argument_list|()
operator|+
literal|"'."
argument_list|)
expr_stmt|;
name|convertedValue
operator|=
name|value
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|xpe
parameter_list|)
block|{
comment|//TODO define an ErrorCode
throw|throw
operator|new
name|RestXqServiceException
argument_list|(
literal|"TODO need to implement error code for problem with parameter conversion!: "
operator|+
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
return|return
operator|new
name|TypedValue
argument_list|<
name|X
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Type
name|getType
parameter_list|()
block|{
comment|//return destinationType;
return|return
name|TypeAdapter
operator|.
name|toExQueryType
argument_list|(
name|convertedValue
operator|.
name|getType
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|X
name|getValue
parameter_list|()
block|{
return|return
operator|(
name|X
operator|)
name|convertedValue
return|;
block|}
block|}
return|;
block|}
specifier|private
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|convertToExistSequence
parameter_list|(
specifier|final
name|XQueryContext
name|xqueryContext
parameter_list|,
specifier|final
name|TypedArgumentValue
name|argument
parameter_list|,
specifier|final
name|int
name|fnParameterType
parameter_list|)
throws|throws
name|RestXqServiceException
throws|,
name|XPathException
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|sequence
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|TypedValue
name|value
range|:
operator|(
name|Sequence
argument_list|<
name|Object
argument_list|>
operator|)
name|argument
operator|.
name|getTypedValue
argument_list|()
control|)
block|{
specifier|final
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|Type
name|destinationType
init|=
name|TypeAdapter
operator|.
name|toExQueryType
argument_list|(
name|fnParameterType
argument_list|)
decl_stmt|;
specifier|final
name|Class
name|destinationClass
decl_stmt|;
switch|switch
condition|(
name|fnParameterType
condition|)
block|{
case|case
name|Type
operator|.
name|ITEM
case|:
name|destinationClass
operator|=
name|Item
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DOCUMENT
case|:
name|destinationClass
operator|=
name|DocumentImpl
operator|.
name|class
expr_stmt|;
comment|//TODO test this
break|break;
case|case
name|Type
operator|.
name|STRING
case|:
name|destinationClass
operator|=
name|StringValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|INT
case|:
case|case
name|Type
operator|.
name|INTEGER
case|:
name|destinationClass
operator|=
name|IntegerValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|FLOAT
case|:
name|destinationClass
operator|=
name|FloatValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DOUBLE
case|:
name|destinationClass
operator|=
name|DoubleValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DECIMAL
case|:
name|destinationClass
operator|=
name|DecimalValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DATE
case|:
name|destinationClass
operator|=
name|DateValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
name|destinationClass
operator|=
name|DateTimeValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|TIME
case|:
name|destinationClass
operator|=
name|TimeValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|QNAME
case|:
name|destinationClass
operator|=
name|QNameValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|ANY_URI
case|:
name|destinationClass
operator|=
name|AnyURIValue
operator|.
name|class
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
name|destinationClass
operator|=
name|BooleanValue
operator|.
name|class
expr_stmt|;
break|break;
default|default:
name|destinationClass
operator|=
name|Item
operator|.
name|class
expr_stmt|;
block|}
specifier|final
name|TypedValue
argument_list|<
name|?
extends|extends
name|Item
argument_list|>
name|val
init|=
name|convertToType
argument_list|(
name|xqueryContext
argument_list|,
name|argument
operator|.
name|getArgumentName
argument_list|()
argument_list|,
name|value
argument_list|,
name|destinationType
argument_list|,
name|destinationClass
argument_list|)
decl_stmt|;
name|sequence
operator|.
name|add
argument_list|(
name|val
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|sequence
return|;
block|}
specifier|public
class|class
name|DocumentImplExpressionAdapter
extends|extends
name|AbstractExpression
block|{
specifier|private
specifier|final
name|DocumentImpl
name|doc
decl_stmt|;
specifier|public
name|DocumentImplExpressionAdapter
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|eval
parameter_list|(
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|DOCUMENT
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
block|}
block|}
block|}
end_class

end_unit

