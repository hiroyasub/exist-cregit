begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
operator|.
name|methods
operator|.
name|Copy
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
name|webdav
operator|.
name|methods
operator|.
name|Delete
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
name|webdav
operator|.
name|methods
operator|.
name|Get
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
name|webdav
operator|.
name|methods
operator|.
name|Head
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
name|webdav
operator|.
name|methods
operator|.
name|Mkcol
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
name|webdav
operator|.
name|methods
operator|.
name|Move
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
name|webdav
operator|.
name|methods
operator|.
name|Options
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
name|webdav
operator|.
name|methods
operator|.
name|Propfind
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
name|webdav
operator|.
name|methods
operator|.
name|Put
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

begin_comment
comment|/**  * Create a {@link WebDAVMethod} for the method specified in the  * HTTP request.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|WebDAVMethodFactory
block|{
specifier|public
specifier|final
specifier|static
name|WebDAVMethod
name|create
parameter_list|(
name|String
name|method
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
block|{
if|if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"OPTIONS"
argument_list|)
condition|)
return|return
operator|new
name|Options
argument_list|()
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"GET"
argument_list|)
condition|)
return|return
operator|new
name|Get
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"HEAD"
argument_list|)
condition|)
return|return
operator|new
name|Head
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"PUT"
argument_list|)
condition|)
return|return
operator|new
name|Put
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"DELETE"
argument_list|)
condition|)
return|return
operator|new
name|Delete
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"MKCOL"
argument_list|)
condition|)
return|return
operator|new
name|Mkcol
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"PROPFIND"
argument_list|)
condition|)
return|return
operator|new
name|Propfind
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"MOVE"
argument_list|)
condition|)
return|return
operator|new
name|Move
argument_list|(
name|pool
argument_list|)
return|;
if|else if
condition|(
name|method
operator|.
name|equals
argument_list|(
literal|"COPY"
argument_list|)
condition|)
return|return
operator|new
name|Copy
argument_list|(
name|pool
argument_list|)
return|;
else|else
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

