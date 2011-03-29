begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist EXPath  *  Copyright (C) 2011 Adam Retter<adam@existsolutions.com>  *  www.existsolutions.com  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|model
operator|.
name|exist
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
name|OutputStream
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|HttpClientException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|expath
operator|.
name|httpclient
operator|.
name|model
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
comment|/**  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|EXistSequence
implements|implements
name|Sequence
block|{
specifier|private
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
decl_stmt|;
specifier|private
specifier|final
name|SequenceIterator
name|sequenceIterator
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|EXistSequence
parameter_list|(
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
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
name|this
operator|.
name|sequenceIterator
operator|=
name|sequence
operator|.
name|iterate
argument_list|()
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
throws|throws
name|HttpClientException
block|{
return|return
name|sequence
operator|.
name|isEmpty
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|next
parameter_list|()
throws|throws
name|HttpClientException
block|{
try|try
block|{
return|return
operator|new
name|EXistSequence
argument_list|(
operator|(
name|NodeValue
operator|)
name|sequenceIterator
operator|.
name|nextItem
argument_list|()
argument_list|,
name|context
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
name|xpe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xpe
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|serialize
parameter_list|(
name|OutputStream
name|out
parameter_list|,
name|Properties
name|params
parameter_list|)
throws|throws
name|HttpClientException
block|{
name|SAXSerializer
name|sax
init|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
argument_list|)
decl_stmt|;
name|params
operator|.
name|setProperty
argument_list|(
name|Serializer
operator|.
name|GENERATE_DOC_EVENTS
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
try|try
block|{
name|SequenceIterator
name|itSequence
init|=
name|sequence
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|String
name|encoding
init|=
name|params
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|Writer
name|writer
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|out
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|params
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
name|sax
operator|.
name|startDocument
argument_list|()
expr_stmt|;
while|while
condition|(
name|itSequence
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|NodeValue
name|next
init|=
operator|(
name|NodeValue
operator|)
name|itSequence
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
name|sax
operator|.
name|endDocument
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
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"A problem occurred while serializing the node set: "
operator|+
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|saxe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"A problem occurred while serializing the node set: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|xpe
parameter_list|)
block|{
throw|throw
operator|new
name|HttpClientException
argument_list|(
literal|"A problem occurred while serializing the node set: "
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
finally|finally
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
block|}
block|}
end_class

end_unit
