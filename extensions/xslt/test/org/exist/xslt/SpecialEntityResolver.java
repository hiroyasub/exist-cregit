begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
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
name|FileInputStream
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
name|net
operator|.
name|URL
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SpecialEntityResolver
implements|implements
name|EntityResolver2
block|{
specifier|private
name|String
name|rootURL
decl_stmt|;
specifier|public
name|SpecialEntityResolver
parameter_list|(
name|String
name|rootURL
parameter_list|)
block|{
name|this
operator|.
name|rootURL
operator|=
name|rootURL
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.EntityResolver2#getExternalSubset(java.lang.String, java.lang.String) 	 */
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
if|if
condition|(
name|baseURI
operator|!=
literal|null
condition|)
return|return
name|resolveInputSource
argument_list|(
name|baseURI
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.ext.EntityResolver2#resolveEntity(java.lang.String, java.lang.String, java.lang.String, java.lang.String) 	 */
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
return|return
name|resolveInputSource
argument_list|(
name|systemId
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.xml.sax.EntityResolver#resolveEntity(java.lang.String, java.lang.String) 	 */
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
return|return
name|resolveInputSource
argument_list|(
name|systemId
argument_list|)
return|;
block|}
specifier|private
name|InputSource
name|resolveInputSource
parameter_list|(
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
name|InputSource
name|inputsource
init|=
operator|new
name|InputSource
argument_list|()
decl_stmt|;
name|URI
name|url
init|=
operator|new
name|URI
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|InputStream
name|is
decl_stmt|;
if|if
condition|(
name|url
operator|.
name|isAbsolute
argument_list|()
condition|)
name|is
operator|=
operator|new
name|URL
argument_list|(
name|path
argument_list|)
operator|.
name|openStream
argument_list|()
expr_stmt|;
else|else
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|rootURL
operator|+
name|path
argument_list|)
decl_stmt|;
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
expr_stmt|;
block|}
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
return|return
name|inputsource
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

