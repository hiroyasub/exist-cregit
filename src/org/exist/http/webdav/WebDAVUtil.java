begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletInputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|Text
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
name|InputSource
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

begin_class
specifier|public
class|class
name|WebDAVUtil
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PARSE_ERR
init|=
literal|"Request content could not be parsed: "
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XML_CONFIGURATION_ERR
init|=
literal|"Failed to create XML parser: "
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|UNEXPECTED_ELEMENT_ERR
init|=
literal|"Unexpected element found: "
decl_stmt|;
specifier|public
specifier|static
name|Document
name|parseRequestContent
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|,
name|DocumentBuilder
name|docBuilder
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
if|if
condition|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
try|try
block|{
name|String
name|content
init|=
name|getRequestContent
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|content
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
name|docBuilder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|PARSE_ERR
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|static
name|String
name|getRequestContent
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|encoding
init|=
name|request
operator|.
name|getCharacterEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|encoding
operator|=
literal|"UTF-8"
expr_stmt|;
try|try
block|{
name|ServletInputStream
name|is
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|StringWriter
name|content
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|char
name|ch
index|[]
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|ch
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
name|content
operator|.
name|write
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Unsupported character encoding in request content: "
operator|+
name|encoding
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|Node
name|firstElementNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|node
operator|=
name|node
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|node
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|equals
argument_list|(
name|WebDAV
operator|.
name|DAV_NS
argument_list|)
condition|)
break|break;
block|}
return|return
name|node
return|;
block|}
specifier|public
specifier|static
name|String
name|getElementContent
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|StringBuffer
name|content
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
name|content
operator|.
name|append
argument_list|(
operator|(
operator|(
name|Text
operator|)
name|node
operator|)
operator|.
name|getData
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|content
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

