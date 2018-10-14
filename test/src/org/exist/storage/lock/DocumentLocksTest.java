begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|*
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
name|atomic
operator|.
name|AtomicReference
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
name|Supplier
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_comment
comment|/**  * Tests for Document Locks  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|DocumentLocksTest
block|{
specifier|private
specifier|static
specifier|final
name|int
name|CONCURRENCY_LEVEL
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
operator|.
name|availableProcessors
argument_list|()
operator|*
literal|3
decl_stmt|;
specifier|private
specifier|final
name|String
name|instanceId
init|=
literal|"test.document-locks-test"
decl_stmt|;
specifier|private
specifier|final
name|ThreadGroup
name|threadGroup
init|=
operator|new
name|ThreadGroup
argument_list|(
literal|"document-locks-test"
argument_list|)
decl_stmt|;
comment|/**      * This test makes sure that there can be multiple reader locks      * held by different threads on the same Document at the same time      *      * A {@link CountDownLatch} is used to ensure that all threads hold      * a read lock at the same time      */
annotation|@
name|Test
specifier|public
name|void
name|multipleReaders
parameter_list|()
throws|throws
name|LockException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|int
name|numberOfThreads
init|=
name|CONCURRENCY_LEVEL
decl_stmt|;
specifier|final
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/x/y/z/1.xml"
argument_list|)
decl_stmt|;
specifier|final
name|LockManager
name|lockManager
init|=
operator|new
name|LockManager
argument_list|(
name|instanceId
argument_list|,
name|threadGroup
argument_list|,
name|CONCURRENCY_LEVEL
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|continueLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
name|numberOfThreads
argument_list|)
decl_stmt|;
comment|// thread definition
specifier|final
name|Supplier
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|readDocumentFn
init|=
parameter_list|()
lambda|->
parameter_list|()
lambda|->
block|{
block_content|try(final ManagedDocumentLock documentLock
init|=
name|lockManager
operator|.
name|acquireDocumentReadLock
argument_list|(
name|docUri
argument_list|)
init|)
block|{
name|continueLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|continueLatch
operator|.
name|await
argument_list|()
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
empty_stmt|;
comment|// create threads
specifier|final
name|List
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|callables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|numberOfThreads
condition|;
name|i
operator|++
control|)
block|{
name|callables
operator|.
name|add
argument_list|(
name|readDocumentFn
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// execute threads
specifier|final
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numberOfThreads
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
name|executorService
operator|.
name|invokeAll
argument_list|(
name|callables
argument_list|)
decl_stmt|;
comment|// await all threads to finish
for|for
control|(
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
parameter_list|()
constructor_decl|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|continueLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_class

begin_comment
comment|/**      * This test makes sure that there can be only a single writer lock      * held by any one thread on the same Document      * at the same time      *      * A {@link CountDownLatch} is used to ensure that the first thread      * holds the write lock when the second thread attempts to acquire it      */
end_comment

begin_function
annotation|@
name|Test
specifier|public
name|void
name|singleWriter
parameter_list|()
throws|throws
name|LockException
throws|,
name|InterruptedException
throws|,
name|ExecutionException
block|{
specifier|final
name|int
name|numberOfThreads
init|=
name|CONCURRENCY_LEVEL
decl_stmt|;
specifier|final
name|XmldbURI
name|docUri
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/x/y/z/1.xml"
argument_list|)
decl_stmt|;
specifier|final
name|LockManager
name|lockManager
init|=
operator|new
name|LockManager
argument_list|(
name|instanceId
argument_list|,
name|threadGroup
argument_list|,
name|CONCURRENCY_LEVEL
argument_list|)
decl_stmt|;
specifier|final
name|CountDownLatch
name|thread2StartLatch
init|=
operator|new
name|CountDownLatch
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|final
name|AtomicReference
name|firstWriteHolder
init|=
operator|new
name|AtomicReference
argument_list|()
decl_stmt|;
specifier|final
name|AtomicReference
name|lastWriteHolder
init|=
operator|new
name|AtomicReference
argument_list|()
decl_stmt|;
specifier|final
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable1
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|ManagedDocumentLock
name|documentLock
init|=
name|lockManager
operator|.
name|acquireDocumentWriteLock
argument_list|(
name|docUri
argument_list|)
init|)
block|{
name|thread2StartLatch
operator|.
name|countDown
argument_list|()
expr_stmt|;
name|firstWriteHolder
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|this
argument_list|)
expr_stmt|;
comment|// make sure the second thread is waiting for the write lock before we continue
while|while
condition|(
operator|!
name|lockManager
operator|.
name|getDocumentLock
argument_list|(
name|docUri
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|hasQueuedThreads
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|10
argument_list|)
expr_stmt|;
block|}
name|lastWriteHolder
operator|.
name|set
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable2
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
name|thread2StartLatch
operator|.
name|await
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|ManagedDocumentLock
name|documentLock
init|=
name|lockManager
operator|.
name|acquireDocumentWriteLock
argument_list|(
name|docUri
argument_list|)
init|)
block|{
name|firstWriteHolder
operator|.
name|compareAndSet
argument_list|(
literal|null
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|lastWriteHolder
operator|.
name|set
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
comment|// create threads
specifier|final
name|List
argument_list|<
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|>
name|callables
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|callable2
argument_list|,
name|callable1
argument_list|)
decl_stmt|;
comment|// execute threads
specifier|final
name|ExecutorService
name|executorService
init|=
name|Executors
operator|.
name|newFixedThreadPool
argument_list|(
name|numberOfThreads
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Future
argument_list|<
name|Void
argument_list|>
argument_list|>
name|futures
init|=
name|executorService
operator|.
name|invokeAll
argument_list|(
name|callables
argument_list|)
decl_stmt|;
comment|// await all threads to finish
for|for
control|(
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|future
range|:
name|futures
control|)
block|{
name|future
operator|.
name|get
argument_list|()
expr_stmt|;
block|}
name|executorService
operator|.
name|shutdown
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|thread2StartLatch
operator|.
name|getCount
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|callable1
argument_list|,
name|firstWriteHolder
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|callable2
argument_list|,
name|lastWriteHolder
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
end_function

unit|}
end_unit

