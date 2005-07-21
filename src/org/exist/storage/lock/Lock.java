begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Lock
block|{
specifier|public
specifier|final
specifier|static
name|int
name|READ_LOCK
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|WRITE_LOCK
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|NO_LOCK
init|=
operator|-
literal|1
decl_stmt|;
comment|/** 	 * Acquire a lock for read. 	 *  	 * @return 	 * @throws LockException 	 */
specifier|public
name|boolean
name|acquire
parameter_list|( )
throws|throws
name|LockException
function_decl|;
comment|/**      * Acquire a lock for read or write.      * mode is one of {@link #READ_LOCK} or      * {@link #WRITE_LOCK}.      *       * @param mode      * @return      * @throws LockException      */
specifier|public
name|boolean
name|acquire
parameter_list|(
name|int
name|mode
parameter_list|)
throws|throws
name|LockException
function_decl|;
comment|/** 	 * Attempt to acquire a lock for read or write. This method 	 * will fail immediately if the lock cannot be acquired. 	 *   	 * @param mode 	 * @return 	 * @throws LockException 	 */
specifier|public
name|boolean
name|attempt
parameter_list|(
name|int
name|mode
parameter_list|)
function_decl|;
comment|/** 	 * Release a lock. This method assumes that the 	 * lock is a read lock. 	 */
specifier|public
name|void
name|release
parameter_list|( )
function_decl|;
comment|/**      * Release a lock of the specified type.      *       * @param mode      */
specifier|public
name|void
name|release
parameter_list|(
name|int
name|mode
parameter_list|)
function_decl|;
comment|/**      * Returns true if there are active or pending      * write locks.      *       * @return      */
specifier|public
name|boolean
name|isLockedForWrite
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

