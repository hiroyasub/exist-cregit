begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|File
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
name|URL
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
name|Source
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
name|TransformerException
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
name|URIResolver
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
name|stream
operator|.
name|StreamSource
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
name|storage
operator|.
name|BrokerPool
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

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|EntityResolver2
import|;
end_import

begin_comment
comment|/**  *  * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|ExistResolver
implements|implements
name|EntityResolver2
implements|,
name|URIResolver
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
name|ExistResolver
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|LOCALURI
init|=
literal|"xmldb:exist:///"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|SHORTLOCALURI
init|=
literal|"xmldb:///"
decl_stmt|;
specifier|public
name|ExistResolver
parameter_list|(
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
comment|/* ========================================== */
comment|/* SAX1: interface org.xml.sax.EntityResolver */
comment|/* ========================================== */
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|publicId
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"publicId="
operator|+
name|publicId
operator|+
literal|" systemId="
operator|+
name|systemId
argument_list|)
expr_stmt|;
return|return
name|resolveInputSource
argument_list|(
name|brokerPool
argument_list|,
name|systemId
argument_list|)
return|;
block|}
comment|/*  =============================================== */
comment|/*  SAX2: interface org.xml.sax.ext.EntityResolver2 */
comment|/*  =============================================== */
specifier|public
name|InputSource
name|getExternalSubset
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|baseURI
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"name="
operator|+
name|name
operator|+
literal|" baseURI="
operator|+
name|baseURI
argument_list|)
expr_stmt|;
return|return
name|resolveInputSource
argument_list|(
name|brokerPool
argument_list|,
name|baseURI
argument_list|)
return|;
block|}
specifier|public
name|InputSource
name|resolveEntity
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|publicId
parameter_list|,
name|String
name|baseURI
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"name="
operator|+
name|name
operator|+
literal|" publicId="
operator|+
name|publicId
operator|+
literal|" baseURI="
operator|+
name|baseURI
operator|+
literal|" systemId="
operator|+
name|systemId
argument_list|)
expr_stmt|;
return|return
name|resolveInputSource
argument_list|(
name|brokerPool
argument_list|,
name|systemId
argument_list|)
return|;
block|}
comment|/* ================================================ */
comment|/* JAXP : interface javax.xml.transform.URIResolver */
comment|/* ================================================ */
specifier|public
name|Source
name|resolve
parameter_list|(
name|String
name|href
parameter_list|,
name|String
name|base
parameter_list|)
throws|throws
name|TransformerException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"href="
operator|+
name|href
operator|+
literal|" base="
operator|+
name|base
argument_list|)
expr_stmt|;
if|if
condition|(
name|base
operator|!=
literal|null
condition|)
block|{
name|String
name|sep
init|=
literal|"/"
decl_stmt|;
if|if
condition|(
name|base
operator|.
name|startsWith
argument_list|(
literal|"file:"
argument_list|)
condition|)
block|{
name|sep
operator|=
name|File
operator|.
name|separator
expr_stmt|;
block|}
specifier|final
name|int
name|pos
init|=
name|base
operator|.
name|lastIndexOf
argument_list|(
name|sep
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|!=
operator|-
literal|1
condition|)
block|{
name|base
operator|=
name|base
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
expr_stmt|;
name|href
operator|=
name|base
operator|+
name|sep
operator|+
name|href
expr_stmt|;
block|}
block|}
return|return
name|resolveStreamSource
argument_list|(
name|brokerPool
argument_list|,
name|href
argument_list|)
return|;
block|}
comment|/* ============== */
comment|/* Helper methods */
comment|/* ============== */
specifier|private
name|InputSource
name|resolveInputSource
parameter_list|(
name|BrokerPool
name|bPool
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving "
operator|+
name|path
argument_list|)
expr_stmt|;
specifier|final
name|InputSource
name|inputsource
init|=
operator|new
name|InputSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|LOCALURI
argument_list|)
operator|||
name|path
operator|.
name|startsWith
argument_list|(
name|SHORTLOCALURI
argument_list|)
condition|)
block|{
specifier|final
name|XmldbURL
name|url
init|=
operator|new
name|XmldbURL
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|EmbeddedInputStream
name|eis
init|=
operator|new
name|EmbeddedInputStream
argument_list|(
name|bPool
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|inputsource
operator|.
name|setByteStream
argument_list|(
name|eis
argument_list|)
expr_stmt|;
name|inputsource
operator|.
name|setSystemId
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|InputStream
name|is
init|=
operator|new
name|URL
argument_list|(
name|path
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|inputsource
operator|.
name|setByteStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|inputsource
operator|.
name|setSystemId
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|inputsource
return|;
block|}
specifier|private
name|StreamSource
name|resolveStreamSource
parameter_list|(
name|BrokerPool
name|bPool
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|TransformerException
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Resolving "
operator|+
name|path
argument_list|)
expr_stmt|;
specifier|final
name|StreamSource
name|streamsource
init|=
operator|new
name|StreamSource
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|path
operator|.
name|startsWith
argument_list|(
name|LOCALURI
argument_list|)
operator|||
name|path
operator|.
name|startsWith
argument_list|(
name|SHORTLOCALURI
argument_list|)
condition|)
block|{
specifier|final
name|XmldbURL
name|url
init|=
operator|new
name|XmldbURL
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|EmbeddedInputStream
name|eis
init|=
operator|new
name|EmbeddedInputStream
argument_list|(
name|bPool
argument_list|,
name|url
argument_list|)
decl_stmt|;
name|streamsource
operator|.
name|setInputStream
argument_list|(
name|eis
argument_list|)
expr_stmt|;
name|streamsource
operator|.
name|setSystemId
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|InputStream
name|is
init|=
operator|new
name|URL
argument_list|(
name|path
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|streamsource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|streamsource
operator|.
name|setSystemId
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
name|ex
argument_list|)
throw|;
block|}
return|return
name|streamsource
return|;
block|}
block|}
end_class

end_unit

