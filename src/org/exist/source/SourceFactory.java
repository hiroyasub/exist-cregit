begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|source
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
name|FileNotFoundException
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Constants
import|;
end_import

begin_comment
comment|/**  * Factory to create a {@link org.exist.source.Source} object for a given  * URL.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|SourceFactory
block|{
comment|/**      * Create a {@link Source} object for the given URL.      *       * As a special case, if the URL starts with "resource:", the resource      * will be read from the current context class loader.      *       * @param contextPath      * @param location      * @return      * @throws MalformedURLException      * @throws IOException      */
specifier|public
specifier|static
specifier|final
name|Source
name|getSource
parameter_list|(
name|String
name|contextPath
parameter_list|,
name|String
name|location
parameter_list|,
name|boolean
name|checkXQEncoding
parameter_list|)
throws|throws
name|MalformedURLException
throws|,
name|IOException
block|{
name|Source
name|source
init|=
literal|null
decl_stmt|;
comment|//TODO: Xmldb source for xmldbURIs?
if|if
condition|(
name|location
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
operator|==
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|contextPath
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|location
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|File
argument_list|(
name|location
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|FileNotFoundException
argument_list|(
literal|"cannot read module source from file at "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
name|location
operator|=
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
expr_stmt|;
name|source
operator|=
operator|new
name|FileSource
argument_list|(
name|f
argument_list|,
literal|"UTF-8"
argument_list|,
name|checkXQEncoding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|location
operator|.
name|startsWith
argument_list|(
name|ClassLoaderSource
operator|.
name|PROTOCOL
argument_list|)
condition|)
block|{
name|source
operator|=
operator|new
name|ClassLoaderSource
argument_list|(
name|location
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|location
argument_list|)
decl_stmt|;
name|source
operator|=
operator|new
name|URLSource
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

