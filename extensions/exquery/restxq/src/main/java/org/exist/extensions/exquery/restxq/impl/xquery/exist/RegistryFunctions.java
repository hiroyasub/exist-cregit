begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2013, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
operator|.
name|xquery
operator|.
name|exist
package|;
end_package

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
name|Collections
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|impl
operator|.
name|ExistXqueryRegistry
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
name|RestXqServiceRegistryManager
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|NodeValue
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

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|ExQueryException
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
name|RestXqService
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
name|RestXqServiceRegistry
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
class|class
name|RegistryFunctions
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnFindResourceFunctions
init|=
operator|new
name|QName
argument_list|(
literal|"find-resource-functions"
argument_list|,
name|ExistRestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExistRestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnRegisterModule
init|=
operator|new
name|QName
argument_list|(
literal|"register-module"
argument_list|,
name|ExistRestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExistRestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnDeregisterModule
init|=
operator|new
name|QName
argument_list|(
literal|"deregister-module"
argument_list|,
name|ExistRestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExistRestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnRegisterResourceFunction
init|=
operator|new
name|QName
argument_list|(
literal|"register-resource-function"
argument_list|,
name|ExistRestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExistRestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnDeregisterResourceFunction
init|=
operator|new
name|QName
argument_list|(
literal|"deregister-resource-function"
argument_list|,
name|ExistRestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ExistRestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|SequenceType
name|PARAM_MODULE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"module"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A URI pointing to an XQuery module."
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|SequenceType
name|PARAM_RESOURCE_FUNCTION
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function-signature"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A signature identifying a resource function. Takes the format {namespace}local-name#arity e.g. {http://somenamespace}some-function#2"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REGISTER_MODULE
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRegisterModule
argument_list|,
literal|"Registers all resource functions identified in the XQuery Module with the RestXQ Registry."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_MODULE
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The list of newly registered resource functions."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_DEREGISTER_MODULE
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnDeregisterModule
argument_list|,
literal|"Deregisters all resource functions identified in the XQuery Module from the RestXQ Registry."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_MODULE
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The list of deregistered resource functions."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FIND_RESOURCE_FUNCTIONS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnFindResourceFunctions
argument_list|,
literal|"Compiles the XQuery Module and examines it, producing a list of all the declared resource functions."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_MODULE
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DOCUMENT
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The list of newly registered resource functions."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REGISTER_RESOURCE_FUNCTION
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRegisterResourceFunction
argument_list|,
literal|"Registers a resource function from the XQuery Module with the RestXQ Registry."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_MODULE
block|,
name|PARAM_RESOURCE_FUNCTION
block|,         }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"true if the function was registered, false otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_DEREGISTER_RESOURCE_FUNCTION
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnDeregisterResourceFunction
argument_list|,
literal|"Deregisters a resource function from the RestXQ Registry."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_MODULE
block|,
name|PARAM_RESOURCE_FUNCTION
block|,         }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"true if the function was deregistered, false otherwise."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|RegistryFunctions
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
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|XmldbURI
name|moduleUri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|toJavaObject
argument_list|(
name|XmldbURI
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
name|ExistXqueryRegistry
name|xqueryRegistry
init|=
name|ExistXqueryRegistry
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|RestXqServiceRegistry
name|registry
init|=
name|RestXqServiceRegistryManager
operator|.
name|getRegistry
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
decl_stmt|;
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
try|try
block|{
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnRegisterModule
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|DocumentImpl
name|module
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getResource
argument_list|(
name|moduleUri
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|xqueryRegistry
operator|.
name|isXquery
argument_list|(
name|module
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|resourceFunctions
init|=
name|xqueryRegistry
operator|.
name|findServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|module
argument_list|)
decl_stmt|;
name|xqueryRegistry
operator|.
name|registerServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|resourceFunctions
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
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
name|xquery
operator|.
name|RegistryFunctions
operator|.
name|serializeRestXqServices
argument_list|(
name|context
operator|.
name|getDocumentBuilder
argument_list|()
argument_list|,
name|resourceFunctions
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnDeregisterModule
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|DocumentImpl
name|module
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getResource
argument_list|(
name|moduleUri
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|xqueryRegistry
operator|.
name|isXquery
argument_list|(
name|module
argument_list|)
condition|)
block|{
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|deregisteringServices
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|RestXqService
name|service
range|:
name|registry
control|)
block|{
if|if
condition|(
name|XmldbURI
operator|.
name|create
argument_list|(
name|service
operator|.
name|getResourceFunction
argument_list|()
operator|.
name|getXQueryLocation
argument_list|()
argument_list|)
operator|.
name|equals
argument_list|(
name|moduleUri
argument_list|)
condition|)
block|{
name|deregisteringServices
operator|.
name|add
argument_list|(
name|service
argument_list|)
expr_stmt|;
block|}
block|}
name|xqueryRegistry
operator|.
name|deregisterServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|moduleUri
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
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
name|xquery
operator|.
name|RegistryFunctions
operator|.
name|serializeRestXqServices
argument_list|(
name|context
operator|.
name|getDocumentBuilder
argument_list|()
argument_list|,
name|deregisteringServices
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnFindResourceFunctions
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|DocumentImpl
name|module
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getResource
argument_list|(
name|moduleUri
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|xqueryRegistry
operator|.
name|isXquery
argument_list|(
name|module
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|List
argument_list|<
name|RestXqService
argument_list|>
name|resourceFunctions
init|=
name|xqueryRegistry
operator|.
name|findServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|module
argument_list|)
decl_stmt|;
name|xqueryRegistry
operator|.
name|deregisterServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|moduleUri
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
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
name|xquery
operator|.
name|RegistryFunctions
operator|.
name|serializeRestXqServices
argument_list|(
name|context
operator|.
name|getDocumentBuilder
argument_list|()
argument_list|,
name|resourceFunctions
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnRegisterResourceFunction
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|resourceFunctionIdentifier
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|DocumentImpl
name|module
init|=
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
operator|.
name|getResource
argument_list|(
name|moduleUri
argument_list|,
name|Permission
operator|.
name|READ
argument_list|)
decl_stmt|;
if|if
condition|(
name|xqueryRegistry
operator|.
name|isXquery
argument_list|(
name|module
argument_list|)
condition|)
block|{
specifier|final
name|SignatureDetail
name|signatureDetail
init|=
name|extractSignatureDetail
argument_list|(
name|resourceFunctionIdentifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|signatureDetail
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|RestXqService
name|serviceToRegister
init|=
name|findService
argument_list|(
name|xqueryRegistry
operator|.
name|findServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getBroker
argument_list|()
argument_list|,
name|module
argument_list|)
operator|.
name|iterator
argument_list|()
argument_list|,
name|signatureDetail
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceToRegister
operator|!=
literal|null
condition|)
block|{
name|xqueryRegistry
operator|.
name|registerServices
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|serviceToRegister
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|=
name|BooleanValue
operator|.
name|TRUE
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|ExQueryException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnDeregisterResourceFunction
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
comment|//TODO
specifier|final
name|String
name|resourceFunctionIdentifier
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|SignatureDetail
name|signatureDetail
init|=
name|extractSignatureDetail
argument_list|(
name|resourceFunctionIdentifier
argument_list|)
decl_stmt|;
if|if
condition|(
name|signatureDetail
operator|!=
literal|null
condition|)
block|{
specifier|final
name|RestXqService
name|serviceToDeregister
init|=
name|findService
argument_list|(
name|xqueryRegistry
operator|.
name|registered
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
argument_list|,
name|signatureDetail
argument_list|)
decl_stmt|;
if|if
condition|(
name|serviceToDeregister
operator|!=
literal|null
condition|)
block|{
name|xqueryRegistry
operator|.
name|deregisterService
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|serviceToDeregister
argument_list|)
expr_stmt|;
name|result
operator|=
name|BooleanValue
operator|.
name|TRUE
expr_stmt|;
block|}
else|else
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pde
argument_list|)
throw|;
block|}
block|}
specifier|private
name|RestXqService
name|findService
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|RestXqService
argument_list|>
name|services
parameter_list|,
specifier|final
name|SignatureDetail
name|signatureDetail
parameter_list|)
block|{
name|RestXqService
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|services
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|services
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|RestXqService
name|service
init|=
name|services
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|FunctionSignature
name|signature
init|=
name|service
operator|.
name|getResourceFunction
argument_list|()
operator|.
name|getFunctionSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|signatureDetail
operator|.
name|name
argument_list|)
operator|&&
name|signature
operator|.
name|getArgumentCount
argument_list|()
operator|==
name|signatureDetail
operator|.
name|arity
condition|)
block|{
name|result
operator|=
name|service
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|SignatureDetail
name|extractSignatureDetail
parameter_list|(
specifier|final
name|String
name|resourceFunctionIdentifier
parameter_list|)
block|{
name|SignatureDetail
name|result
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourceFunctionIdentifier
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
operator|>
operator|-
literal|1
condition|)
block|{
specifier|final
name|int
name|arity
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|resourceFunctionIdentifier
operator|.
name|substring
argument_list|(
name|resourceFunctionIdentifier
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
operator|+
literal|1
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|name
decl_stmt|;
if|if
condition|(
name|resourceFunctionIdentifier
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|name
operator|=
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|resourceFunctionIdentifier
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|resourceFunctionIdentifier
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
operator|new
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
argument_list|(
name|resourceFunctionIdentifier
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|resourceFunctionIdentifier
operator|.
name|indexOf
argument_list|(
literal|'#'
argument_list|)
argument_list|)
argument_list|,
name|resourceFunctionIdentifier
operator|.
name|substring
argument_list|(
name|resourceFunctionIdentifier
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|result
operator|=
operator|new
name|SignatureDetail
argument_list|(
name|name
argument_list|,
name|arity
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
class|class
name|SignatureDetail
block|{
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|name
decl_stmt|;
specifier|final
name|int
name|arity
decl_stmt|;
specifier|public
name|SignatureDetail
parameter_list|(
specifier|final
name|javax
operator|.
name|xml
operator|.
name|namespace
operator|.
name|QName
name|name
parameter_list|,
specifier|final
name|int
name|arity
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|arity
operator|=
name|arity
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

