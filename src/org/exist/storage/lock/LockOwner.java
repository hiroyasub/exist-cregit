begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_comment
comment|/**  * Used to track acquired locks, mainly for debugging.  */
end_comment

begin_class
specifier|public
class|class
name|LockOwner
block|{
comment|/**      * Global flag: set to true to receive debugging output, in particular,      * to see where a lock was acquired. Note: it adds some considerable      * processing overhead.      */
specifier|public
specifier|static
name|boolean
name|DEBUG
init|=
literal|false
decl_stmt|;
specifier|private
specifier|final
name|Thread
name|owner
decl_stmt|;
specifier|private
name|Throwable
name|stack
init|=
literal|null
decl_stmt|;
specifier|public
name|LockOwner
parameter_list|(
name|Thread
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
if|if
condition|(
name|DEBUG
condition|)
block|{
name|this
operator|.
name|stack
operator|=
operator|new
name|Throwable
argument_list|()
operator|.
name|fillInStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
name|Thread
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
specifier|public
specifier|final
name|Throwable
name|getStack
parameter_list|()
block|{
return|return
name|stack
return|;
block|}
block|}
end_class

end_unit

