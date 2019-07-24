begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|Consumer2E
import|;
end_import

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|ConsumerE
import|;
end_import

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|ThreadSafe
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|lock
operator|.
name|ManagedLock
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
name|locks
operator|.
name|ReadWriteLock
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
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Consumer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * A wrapper which allows read or modify operations  * in a concurrent and thread-safe manner  * to an underlying value.  *  * @param<T> The type of the underlying value  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|ConcurrentValueWrapper
parameter_list|<
name|T
parameter_list|>
block|{
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|T
name|value
decl_stmt|;
specifier|protected
name|ConcurrentValueWrapper
parameter_list|(
specifier|final
name|T
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Read from the value.      *      * @param<U> the return type.      *      * @param readFn A function which reads the value      *     and returns a result.      *      * @return the result of the {@code readFn}.      */
specifier|public
parameter_list|<
name|U
parameter_list|>
name|U
name|read
parameter_list|(
specifier|final
name|Function
argument_list|<
name|T
argument_list|,
name|U
argument_list|>
name|readFn
parameter_list|)
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|readLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
return|return
name|readFn
operator|.
name|apply
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
comment|/**      * Write to the value.      *      * @param writeFn A function which writes to the value.      */
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|Consumer
argument_list|<
name|T
argument_list|>
name|writeFn
parameter_list|)
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|writeFn
operator|.
name|accept
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write to the value and return a result.      *      * @param<U> the return type.      *      * @param writeFn A function which writes to the value      *     and returns a result.      *      * @return the result of the write function.      */
specifier|public
parameter_list|<
name|U
parameter_list|>
name|U
name|writeAndReturn
parameter_list|(
specifier|final
name|Function
argument_list|<
name|T
argument_list|,
name|U
argument_list|>
name|writeFn
parameter_list|)
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
return|return
name|writeFn
operator|.
name|apply
argument_list|(
name|value
argument_list|)
return|;
block|}
block|}
comment|/**      * Write to the value.      *      * @param writeFn A function which writes to the value.      *      * @param<E> An exception which may be thrown by the {@code writeFn}.      *      * @throws E if an exception is thrown by the {@code writeFn}.      */
specifier|public
specifier|final
parameter_list|<
name|E
extends|extends
name|Throwable
parameter_list|>
name|void
name|writeE
parameter_list|(
specifier|final
name|ConsumerE
argument_list|<
name|T
argument_list|,
name|E
argument_list|>
name|writeFn
parameter_list|)
throws|throws
name|E
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|writeFn
operator|.
name|accept
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Write to the value.      *      * @param writeFn A function which writes to the value.      *      * @param<E1> An exception which may be thrown by the {@code writeFn}.      * @param<E2> An exception which may be thrown by the {@code writeFn}.      *      * @throws E1 if an exception is thrown by the {@code writeFn}.      * @throws E2 if an exception is thrown by the {@code writeFn}.      */
specifier|public
specifier|final
parameter_list|<
name|E1
extends|extends
name|Exception
parameter_list|,
name|E2
extends|extends
name|Exception
parameter_list|>
name|void
name|write2E
parameter_list|(
specifier|final
name|Consumer2E
argument_list|<
name|T
argument_list|,
name|E1
argument_list|,
name|E2
argument_list|>
name|writeFn
parameter_list|)
throws|throws
name|E1
throws|,
name|E2
block|{
try|try
init|(
specifier|final
name|ManagedLock
argument_list|<
name|ReadWriteLock
argument_list|>
name|writeLock
init|=
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
name|writeFn
operator|.
name|accept
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

