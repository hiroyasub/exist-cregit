begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|resolver
operator|.
name|unstable
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
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|logging
operator|.
name|Level
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|protocolhandler
operator|.
name|embedded
operator|.
name|EmbeddedInputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmldb
operator|.
name|XmldbURL
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|protocolhandler
operator|.
name|xmlrpc
operator|.
name|XmlrpcInputStream
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
name|ls
operator|.
name|LSInput
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
name|ls
operator|.
name|LSResourceResolver
import|;
end_import

begin_comment
comment|/**  * eXistLSResourceResolver provides a way for applications to redirect  * references to external resource.  *  * To be used by @see javax.xml.validation.Validator  *   * @author Dizzzz (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|eXistLSResourceResolver
implements|implements
name|LSResourceResolver
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|eXistLSResourceResolver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|LSInput
name|resolveResource
parameter_list|(
name|String
name|type
parameter_list|,
name|String
name|namespaceURI
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|,
name|String
name|baseURI
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"type="
operator|+
name|type
operator|+
literal|" namespaceURI="
operator|+
name|namespaceURI
operator|+
literal|" publicId="
operator|+
name|publicId
operator|+
literal|" systemId="
operator|+
name|systemId
operator|+
literal|" baseURI="
operator|+
name|baseURI
argument_list|)
expr_stmt|;
name|LSInput
name|lsInput
init|=
operator|new
name|eXistLSInput
argument_list|()
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|getInputStream
argument_list|(
name|systemId
argument_list|)
decl_stmt|;
name|lsInput
operator|.
name|setByteStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|lsInput
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|lsInput
return|;
block|}
specifier|private
name|InputStream
name|getInputStream
parameter_list|(
name|String
name|resourcePath
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|IOException
block|{
if|if
condition|(
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"/db"
argument_list|)
condition|)
block|{
name|resourcePath
operator|=
literal|"xmldb:exist://"
operator|+
name|resourcePath
expr_stmt|;
block|}
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|resourcePath
operator|.
name|startsWith
argument_list|(
literal|"xmldb:"
argument_list|)
condition|)
block|{
name|XmldbURL
name|xmldbURL
init|=
operator|new
name|XmldbURL
argument_list|(
name|resourcePath
argument_list|)
decl_stmt|;
if|if
condition|(
name|xmldbURL
operator|.
name|isEmbedded
argument_list|()
condition|)
block|{
name|is
operator|=
operator|new
name|EmbeddedInputStream
argument_list|(
name|xmldbURL
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
operator|new
name|XmlrpcInputStream
argument_list|(
name|xmldbURL
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|is
operator|=
operator|new
name|URL
argument_list|(
name|resourcePath
argument_list|)
operator|.
name|openStream
argument_list|()
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
block|}
end_class

end_unit

