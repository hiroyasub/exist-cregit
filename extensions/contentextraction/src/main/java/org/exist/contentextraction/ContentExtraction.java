begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|contentextraction
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
name|InputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|exception
operator|.
name|TikaException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|metadata
operator|.
name|Metadata
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|AutoDetectParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|ParseContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tika
operator|.
name|parser
operator|.
name|Parser
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
name|Receiver
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
name|SAXToReceiver
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
name|BinaryValue
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
name|ContentHandler
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
comment|/**  * @author<a href="mailto:dulip.withanage@gmail.com">Dulip Withanage</a>  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|ContentExtraction
block|{
specifier|final
name|Parser
name|parser
init|=
operator|new
name|AutoDetectParser
argument_list|()
decl_stmt|;
specifier|final
name|ParseContext
name|parseContext
init|=
operator|new
name|ParseContext
argument_list|()
decl_stmt|;
specifier|public
name|ContentExtraction
parameter_list|()
block|{
name|parseContext
operator|.
name|set
argument_list|(
name|Parser
operator|.
name|class
argument_list|,
name|parser
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Metadata
name|extractContentAndMetadata
parameter_list|(
specifier|final
name|BinaryValue
name|binaryValue
parameter_list|,
specifier|final
name|ContentHandler
name|contentHandler
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ContentExtractionException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|binaryValue
operator|.
name|getInputStream
argument_list|()
init|)
block|{
specifier|final
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
name|contentHandler
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContentExtractionException
argument_list|(
literal|"Problem with content extraction library: "
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
block|}
specifier|public
name|void
name|extractContentAndMetadata
parameter_list|(
name|BinaryValue
name|binaryValue
parameter_list|,
name|Receiver
name|receiver
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ContentExtractionException
block|{
name|extractContentAndMetadata
argument_list|(
name|binaryValue
argument_list|,
operator|new
name|SAXToReceiver
argument_list|(
name|receiver
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Metadata
name|extractMetadata
parameter_list|(
specifier|final
name|BinaryValue
name|binaryValue
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|ContentExtractionException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|binaryValue
operator|.
name|getInputStream
argument_list|()
init|)
block|{
specifier|final
name|Metadata
name|metadata
init|=
operator|new
name|Metadata
argument_list|()
decl_stmt|;
name|parser
operator|.
name|parse
argument_list|(
name|is
argument_list|,
literal|null
argument_list|,
name|metadata
argument_list|,
name|parseContext
argument_list|)
expr_stmt|;
return|return
name|metadata
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TikaException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ContentExtractionException
argument_list|(
literal|"Problem with content extraction library: "
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
block|}
block|}
end_class

end_unit

