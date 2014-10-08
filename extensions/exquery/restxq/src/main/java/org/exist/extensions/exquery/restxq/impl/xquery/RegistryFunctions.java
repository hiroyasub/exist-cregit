begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2014, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
name|RestXqServiceRegistryManager
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
name|MemTreeBuilder
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
name|Type
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

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|annotation
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|AbstractYesNoSerializationAnnotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|MethodAnnotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|SerializationAnnotation
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
name|Literal
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
name|Annotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RegistryFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|RESOURCE_FUNCTIONS
init|=
operator|new
name|QName
argument_list|(
literal|"resource-functions"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|RESOURCE_FUNCTION
init|=
operator|new
name|QName
argument_list|(
literal|"resource-function"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|ANNOTATIONS
init|=
operator|new
name|QName
argument_list|(
literal|"annotations"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|SEGMENT
init|=
operator|new
name|QName
argument_list|(
literal|"segment"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|INTERNET_MEDIA_TYPE
init|=
operator|new
name|QName
argument_list|(
literal|"internet-media-type"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XQUERY_URI
init|=
literal|"xquery-uri"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|QName
name|RESOURCE_FUNCTION_IDENTITY
init|=
operator|new
name|QName
argument_list|(
literal|"identity"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE
init|=
literal|"namespace"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|LOCAL_NAME
init|=
literal|"local-name"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ARITY
init|=
literal|"arity"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|VALUE
init|=
literal|"value"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ARGUMENT
init|=
literal|"argument"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_VALUE
init|=
literal|"default-value"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SPECIFICITY_METRIC
init|=
literal|"specificity-metric"
decl_stmt|;
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
name|RESOURCE_FUNCTIONS
operator|.
name|getLocalPart
argument_list|()
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Gets a list of all the registered resource functions."
argument_list|,
name|FunctionSignature
operator|.
name|NO_ARGS
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
literal|"The list of registered resource functions."
argument_list|)
argument_list|)
block|}
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
return|return
operator|(
name|NodeValue
operator|)
name|serializeRestXqServices
argument_list|(
name|getContext
argument_list|()
operator|.
name|getDocumentBuilder
argument_list|()
argument_list|,
name|registry
argument_list|)
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
comment|/**      * Serializes RESTXQ Services to an XML description      *      * @param builder The receiver for the serialization      * @param services The services to describe      *      * @return The XML Document constructed from serializing the      * services to the MemTreeBuilder      */
specifier|public
specifier|static
name|Document
name|serializeRestXqServices
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|Iterable
argument_list|<
name|RestXqService
argument_list|>
name|services
parameter_list|)
block|{
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|RESOURCE_FUNCTIONS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|RestXqService
name|service
range|:
name|services
control|)
block|{
specifier|final
name|ResourceFunction
name|resourceFn
init|=
name|service
operator|.
name|getResourceFunction
argument_list|()
decl_stmt|;
name|serializeResourceFunction
argument_list|(
name|builder
argument_list|,
name|resourceFn
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|builder
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|/**      * Serializes a RESTXQ Resource Function as an XML description      *      * @param builder The receiver for the serialization      * @param resourceFn The resource function to describe      */
specifier|static
name|void
name|serializeResourceFunction
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|ResourceFunction
name|resourceFn
parameter_list|)
block|{
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|XQUERY_URI
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|resourceFn
operator|.
name|getXQueryLocation
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|RESOURCE_FUNCTION
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
comment|//identity
name|attrs
operator|=
operator|new
name|AttributesImpl
argument_list|()
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|NAMESPACE
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|resourceFn
operator|.
name|getFunctionSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|LOCAL_NAME
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|resourceFn
operator|.
name|getFunctionSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|ARITY
argument_list|,
literal|""
argument_list|,
literal|"int"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|resourceFn
operator|.
name|getFunctionSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|RESOURCE_FUNCTION_IDENTITY
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|//rest annotations
name|builder
operator|.
name|startElement
argument_list|(
name|ANNOTATIONS
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|serializeAnnotations
argument_list|(
name|builder
argument_list|,
name|resourceFn
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|static
name|void
name|serializeAnnotations
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|ResourceFunction
name|resourceFn
parameter_list|)
block|{
specifier|final
name|List
argument_list|<
name|Annotation
argument_list|>
name|annotations
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|annotations
operator|.
name|addAll
argument_list|(
name|resourceFn
operator|.
name|getHttpMethodAnnotations
argument_list|()
argument_list|)
expr_stmt|;
name|annotations
operator|.
name|addAll
argument_list|(
name|resourceFn
operator|.
name|getConsumesAnnotations
argument_list|()
argument_list|)
expr_stmt|;
name|annotations
operator|.
name|addAll
argument_list|(
name|resourceFn
operator|.
name|getProducesAnnotations
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Annotation
name|annotation
range|:
name|annotations
control|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|annotation
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|Literal
name|literals
index|[]
init|=
name|annotation
operator|.
name|getLiterals
argument_list|()
decl_stmt|;
if|if
condition|(
name|literals
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Literal
name|literal
range|:
name|literals
control|)
block|{
if|if
condition|(
name|annotation
operator|instanceof
name|ConsumesAnnotation
operator|||
name|annotation
operator|instanceof
name|ProducesAnnotation
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|INTERNET_MEDIA_TYPE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|literal
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|//path annotation
if|if
condition|(
name|resourceFn
operator|.
name|getPathAnnotation
argument_list|()
operator|!=
literal|null
condition|)
block|{
specifier|final
name|PathAnnotation
name|pathAnnotation
init|=
name|resourceFn
operator|.
name|getPathAnnotation
argument_list|()
decl_stmt|;
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|SPECIFICITY_METRIC
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|Long
operator|.
name|toString
argument_list|(
name|pathAnnotation
operator|.
name|getPathSpecificityMetric
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|pathAnnotation
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|segments
init|=
name|pathAnnotation
operator|.
name|getLiterals
argument_list|()
index|[
literal|0
index|]
operator|.
name|getValue
argument_list|()
operator|.
name|split
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|segment
range|:
name|segments
control|)
block|{
if|if
condition|(
operator|!
name|segment
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|SEGMENT
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|segment
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|//parameter annotations
for|for
control|(
specifier|final
name|ParameterAnnotation
name|parameterAnnotation
range|:
name|resourceFn
operator|.
name|getParameterAnnotations
argument_list|()
control|)
block|{
specifier|final
name|Literal
index|[]
name|literals
init|=
name|parameterAnnotation
operator|.
name|getLiterals
argument_list|()
decl_stmt|;
specifier|final
name|AttributesImpl
name|attrs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|NAME
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|literals
index|[
literal|0
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|ARGUMENT
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|literals
index|[
literal|1
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|literals
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|attrs
operator|.
name|addAttribute
argument_list|(
literal|null
argument_list|,
name|DEFAULT_VALUE
argument_list|,
literal|""
argument_list|,
literal|"string"
argument_list|,
name|literals
index|[
literal|2
index|]
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|parameterAnnotation
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|attrs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|//serialization annotations
for|for
control|(
specifier|final
name|SerializationAnnotation
name|serializationAnnotation
range|:
name|resourceFn
operator|.
name|getSerializationAnnotations
argument_list|()
control|)
block|{
name|serializeSerializationAnnotation
argument_list|(
name|builder
argument_list|,
name|serializationAnnotation
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|void
name|serializeSerializationAnnotation
parameter_list|(
specifier|final
name|MemTreeBuilder
name|builder
parameter_list|,
specifier|final
name|SerializationAnnotation
name|serializationAnnotation
parameter_list|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
name|QName
operator|.
name|fromJavaQName
argument_list|(
name|serializationAnnotation
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|serializationAnnotation
operator|instanceof
name|AbstractYesNoSerializationAnnotation
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|AbstractYesNoSerializationAnnotation
operator|)
name|serializationAnnotation
operator|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|serializationAnnotation
operator|instanceof
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|MediaTypeAnnotation
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|MediaTypeAnnotation
operator|)
name|serializationAnnotation
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|serializationAnnotation
operator|instanceof
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|EncodingAnnotation
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|EncodingAnnotation
operator|)
name|serializationAnnotation
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|serializationAnnotation
operator|instanceof
name|MethodAnnotation
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
operator|(
operator|(
name|org
operator|.
name|exquery
operator|.
name|serialization
operator|.
name|annotation
operator|.
name|MethodAnnotation
operator|)
name|serializationAnnotation
operator|)
operator|.
name|getMethod
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO further output: annotations as they are implemented
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

