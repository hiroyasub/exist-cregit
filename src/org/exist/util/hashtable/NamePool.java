begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|hashtable
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ConcurrentMap
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

begin_comment
comment|/**  * @author Pieter Deelen  */
end_comment

begin_class
specifier|public
class|class
name|NamePool
block|{
specifier|private
name|ConcurrentMap
name|pool
decl_stmt|;
specifier|public
name|NamePool
parameter_list|()
block|{
name|pool
operator|=
operator|new
name|ConcurrentHashMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|QName
name|getSharedName
parameter_list|(
name|QName
name|name
parameter_list|)
block|{
name|QName
name|sharedName
init|=
operator|(
name|QName
operator|)
name|pool
operator|.
name|putIfAbsent
argument_list|(
name|name
argument_list|,
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|sharedName
operator|==
literal|null
condition|)
block|{
comment|// The name was not in the pool, return the name just added.
return|return
name|name
return|;
block|}
else|else
block|{
comment|// The name was in the pool, return the shared name.
return|return
name|sharedName
return|;
block|}
block|}
block|}
end_class

end_unit

