begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|Debuggable
import|;
end_import

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
extends|extends
name|Debuggable
block|{
comment|/**      * The modes of a {@link Lock}      */
enum|enum
name|LockMode
block|{
name|NO_LOCK
block|,
name|READ_LOCK
block|,
name|WRITE_LOCK
block|}
comment|/**      * The type of a {@link Lock}      */
enum|enum
name|LockType
block|{
annotation|@
name|Deprecated
name|LEGACY_COLLECTION
decl_stmt|,         @
name|Deprecated
name|LEGACY_DOCUMENT
decl_stmt|,
name|COLLECTION
decl_stmt|,
name|DOCUMENT
decl_stmt|,
name|COLLECTIONS_DBX
block|}
comment|/**      * Get the id of the lock      */
name|String
name|getId
parameter_list|()
function_decl|;
comment|/**      * Create a LockInfo entry for the given lock.      *      * @return the lock info      */
name|LockInfo
name|getLockInfo
parameter_list|()
function_decl|;
comment|/** 	 * Acquire a lock for read. 	 *  	 * @throws LockException      *      * @deprecated Use {@link #acquire(LockMode)} 	 */
annotation|@
name|Deprecated
name|boolean
name|acquire
parameter_list|()
throws|throws
name|LockException
function_decl|;
comment|/**      * Acquire a lock for read or write.      * mode is one of {@link LockMode#READ_LOCK} or      * {@link LockMode#WRITE_LOCK}.      *       * @param mode The mode of the lock to acquire      * @throws LockException      */
name|boolean
name|acquire
parameter_list|(
name|LockMode
name|mode
parameter_list|)
throws|throws
name|LockException
function_decl|;
comment|/** 	 * Attempt to acquire a lock for read or write. This method 	 * will fail immediately if the lock cannot be acquired. 	 *      * @param mode The mode of the lock to attempt to acquire 	 */
name|boolean
name|attempt
parameter_list|(
name|LockMode
name|mode
parameter_list|)
function_decl|;
comment|/**      * Release a lock of the specified type.      *      * @param mode The mode of the lock to release      */
name|void
name|release
parameter_list|(
name|LockMode
name|mode
parameter_list|)
function_decl|;
comment|/**      * Release a number of references of the specified lock mode      *      * @param mode The mode of the lock to release      * @param count The number of references to release      */
name|void
name|release
parameter_list|(
name|LockMode
name|mode
parameter_list|,
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * Returns true if there are active or pending      * write locks.      *      * @return true if the lock is locked for write      */
name|boolean
name|isLockedForWrite
parameter_list|()
function_decl|;
comment|/**      * Check if the specified thread does currently hold a read lock.      *      * @param owner the thread to search for      * @return true if the thread holds a read lock      */
name|boolean
name|isLockedForRead
parameter_list|(
name|Thread
name|owner
parameter_list|)
function_decl|;
comment|/**      * Check if the lock is currently locked by someone.      *      * @return true if there's an active read or write lock      */
name|boolean
name|hasLock
parameter_list|()
function_decl|;
comment|/**      * Check if the specified thread holds either a write or a read lock      * on the resource.      *      * @param owner the thread      * @return true if owner has a lock      */
name|boolean
name|hasLock
parameter_list|(
name|Thread
name|owner
parameter_list|)
function_decl|;
comment|/**      * Wake up waiting threads and recompute dependencies.      * Currently used to rerun deadlock detection.      */
name|void
name|wakeUp
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

