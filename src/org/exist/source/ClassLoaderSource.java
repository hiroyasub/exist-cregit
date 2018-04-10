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
name|IOException
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

begin_comment
comment|/**  * A source loaded through the current context class loader.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ClassLoaderSource
extends|extends
name|URLSource
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PROTOCOL
init|=
literal|"resource:"
decl_stmt|;
specifier|private
specifier|final
name|String
name|source
decl_stmt|;
comment|/**      * @param source The resource name (e.g. url).      *      *<p> The name of a resource is a '<tt>/</tt>'-separated path name that      * identifies the resource. Preceding "/" and "resource:"" are removed.      */
specifier|public
name|ClassLoaderSource
parameter_list|(
name|String
name|source
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|source
operator|=
name|source
expr_stmt|;
if|if
condition|(
name|source
operator|.
name|startsWith
argument_list|(
name|PROTOCOL
argument_list|)
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|substring
argument_list|(
name|PROTOCOL
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|source
operator|=
name|source
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ClassLoader
name|cl
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
specifier|final
name|URL
name|url
init|=
name|cl
operator|.
name|getResource
argument_list|(
name|source
argument_list|)
decl_stmt|;
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Source not found: "
operator|+
name|source
argument_list|)
throw|;
block|}
name|setURL
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|type
parameter_list|()
block|{
specifier|final
name|String
name|protocol
init|=
name|url
operator|.
name|getProtocol
argument_list|()
decl_stmt|;
specifier|final
name|String
name|host
init|=
name|url
operator|.
name|getHost
argument_list|()
decl_stmt|;
if|if
condition|(
name|protocol
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
operator|&&
operator|(
name|host
operator|==
literal|null
operator|||
name|host
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
literal|"localhost"
operator|.
name|equals
argument_list|(
name|host
argument_list|)
operator|||
literal|"127.0.0.1"
operator|.
name|equals
argument_list|(
name|host
argument_list|)
operator|)
condition|)
block|{
return|return
literal|"File"
return|;
block|}
return|return
literal|"Classloader"
return|;
block|}
specifier|public
name|String
name|getSource
parameter_list|()
block|{
return|return
name|source
return|;
block|}
block|}
end_class

end_unit

