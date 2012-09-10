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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|serializers
operator|.
name|Serializer
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
name|serializer
operator|.
name|SAXSerializer
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
name|serializer
operator|.
name|SerializerPool
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
name|exquery
operator|.
name|http
operator|.
name|HttpResponse
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
name|restxq
operator|.
name|impl
operator|.
name|serialization
operator|.
name|AbstractRestXqServiceSerializer
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
name|impl
operator|.
name|serialization
operator|.
name|SerializationProperty
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
operator|.
name|SupportedMethod
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RestXqServiceSerializerImpl
extends|extends
name|AbstractRestXqServiceSerializer
block|{
specifier|final
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|public
name|RestXqServiceSerializerImpl
parameter_list|(
specifier|final
name|BrokerPool
name|brokerPool
parameter_list|)
block|{
name|this
operator|.
name|brokerPool
operator|=
name|brokerPool
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
specifier|protected
name|void
name|serializeBinaryBody
parameter_list|(
specifier|final
name|Sequence
name|result
parameter_list|,
specifier|final
name|HttpResponse
name|response
parameter_list|)
throws|throws
name|RestXqServiceException
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"TODO adam needs to implement this yet!"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|serializeNodeBody
parameter_list|(
specifier|final
name|Sequence
name|result
parameter_list|,
specifier|final
name|HttpResponse
name|response
parameter_list|,
specifier|final
name|Map
argument_list|<
name|SerializationProperty
argument_list|,
name|String
argument_list|>
name|serializationProperties
parameter_list|)
throws|throws
name|RestXqServiceException
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|SAXSerializer
name|sax
init|=
literal|null
decl_stmt|;
specifier|final
name|SerializerPool
name|serializerPool
init|=
name|SerializerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getBrokerPool
argument_list|()
operator|.
name|get
argument_list|(
name|brokerPool
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|sax
operator|=
operator|(
name|SAXSerializer
operator|)
name|serializerPool
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
expr_stmt|;
specifier|final
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|serializationProperties
operator|.
name|get
argument_list|(
name|SerializationProperty
operator|.
name|ENCODING
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|outputProperties
init|=
name|serializationPropertiesToProperties
argument_list|(
name|serializationProperties
argument_list|)
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
operator|(
operator|(
name|SequenceAdapter
operator|)
name|result
operator|)
operator|.
name|getExistSequence
argument_list|()
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RestXqServiceException
argument_list|(
literal|"Error while serializing xml: "
operator|+
name|ioe
operator|.
name|toString
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
throw|throw
operator|new
name|RestXqServiceException
argument_list|(
literal|"Error while serializing xml: "
operator|+
name|ee
operator|.
name|toString
argument_list|()
argument_list|,
name|ee
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|RestXqServiceException
argument_list|(
literal|"Error while serializing xml: "
operator|+
name|se
operator|.
name|toString
argument_list|()
argument_list|,
name|se
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|sax
operator|!=
literal|null
condition|)
block|{
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|getBrokerPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Properties
name|serializationPropertiesToProperties
parameter_list|(
specifier|final
name|Map
argument_list|<
name|SerializationProperty
argument_list|,
name|String
argument_list|>
name|serializationProperties
parameter_list|)
block|{
specifier|final
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Entry
argument_list|<
name|SerializationProperty
argument_list|,
name|String
argument_list|>
name|serializationProperty
range|:
name|serializationProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
if|if
condition|(
name|serializationProperty
operator|.
name|getKey
argument_list|()
operator|==
name|SerializationProperty
operator|.
name|METHOD
operator|&&
name|serializationProperty
operator|.
name|getValue
argument_list|()
operator|.
name|equals
argument_list|(
name|SupportedMethod
operator|.
name|html
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
comment|//Map HTML -> HTML5 as eXist doesnt have a html serializer that isnt html5
name|props
operator|.
name|setProperty
argument_list|(
name|serializationProperty
operator|.
name|getKey
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|SupportedMethod
operator|.
name|html5
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|props
operator|.
name|setProperty
argument_list|(
name|serializationProperty
operator|.
name|getKey
argument_list|()
operator|.
name|name
argument_list|()
operator|.
name|toLowerCase
argument_list|()
argument_list|,
name|serializationProperty
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|props
return|;
block|}
block|}
end_class

end_unit

