begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id: Transform.java 13189 2010-11-12 11:05:05Z shabanovd $  */
end_comment

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
name|response
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
name|PrintWriter
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
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_class
specifier|public
class|class
name|Stream
extends|extends
name|StrictResponseFunction
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Stream
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"stream"
argument_list|,
name|ResponseModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ResponseModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Stream can only be used within a servlet context. It directly streams its input to the servlet's output stream. "
operator|+
literal|"It should thus be the last statement in the XQuery."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"content"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The source sequence"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"serialization-options"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The serialization options"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Stream
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
annotation|@
name|Nonnull
specifier|final
name|ResponseWrapper
name|response
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
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|Sequence
name|inputNode
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
specifier|final
name|Properties
name|serializeOptions
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|serOpts
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
name|String
index|[]
name|contents
init|=
name|Option
operator|.
name|tokenize
argument_list|(
name|serOpts
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
name|contents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
index|[]
name|pair
init|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|contents
index|[
name|i
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|pair
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Found invalid serialization option: "
operator|+
name|contents
index|[
name|i
index|]
argument_list|)
throw|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Setting serialization property: "
operator|+
name|pair
index|[
literal|0
index|]
operator|+
literal|" = "
operator|+
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
name|serializeOptions
operator|.
name|setProperty
argument_list|(
name|pair
index|[
literal|0
index|]
argument_list|,
name|pair
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
literal|"org.exist.http.servlets.HttpResponseWrapper"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
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
name|XPDY0002
argument_list|,
name|signature
operator|.
name|toString
argument_list|()
operator|+
literal|" can only be used within the EXistServlet or XQueryServlet"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|mediaType
init|=
name|serializeOptions
operator|.
name|getProperty
argument_list|(
literal|"media-type"
argument_list|,
literal|"application/xml"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|encoding
init|=
name|serializeOptions
operator|.
name|getProperty
argument_list|(
literal|"encoding"
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediaType
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|setContentType
argument_list|(
name|mediaType
operator|+
literal|"; charset="
operator|+
name|encoding
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|BrokerPool
name|db
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|getBroker
argument_list|()
init|;
specifier|final
name|PrintWriter
name|output
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|,
name|encoding
argument_list|)
argument_list|)
init|)
block|{
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
specifier|final
name|SerializerPool
name|serializerPool
init|=
name|SerializerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|SAXSerializer
name|sax
init|=
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
decl_stmt|;
try|try
block|{
name|sax
operator|.
name|setOutput
argument_list|(
name|output
argument_list|,
name|serializeOptions
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|serializeOptions
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
name|inputNode
argument_list|,
literal|1
argument_list|,
name|inputNode
operator|.
name|getItemCount
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|serializerPool
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|//commit the response
name|response
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"IO exception while streaming node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Exception while streaming node: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit
