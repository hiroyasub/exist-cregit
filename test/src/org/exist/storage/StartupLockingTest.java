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
name|tuple
operator|.
name|Tuple2
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
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
name|LockEventJsonListener
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
name|LockEventXmlListener
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
name|LockTable
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
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistEmbeddedServer
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
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|ClassRule
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
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|AtomicBoolean
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Simple test that Starts the database and checks that no Collection Locks are still held  * once the database is at rest  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|StartupLockingTest
block|{
specifier|private
specifier|static
name|LockCountListener
name|lockCountListener
init|=
operator|new
name|LockCountListener
argument_list|()
decl_stmt|;
comment|/**      * Very useful for debugging lock life cycles      */
comment|//    private static LockEventJsonListener lockEventJsonListener = new LockEventJsonListener(Paths.get("/tmp/startupLockingTest" + System.currentTimeMillis() + ".json"), true);
comment|//    private static LockEventXmlListener lockEventXmlListener = new LockEventXmlListener(Paths.get("/tmp/startupLockingTest" + System.currentTimeMillis() + ".xml"), true);
specifier|private
specifier|static
name|LockTable
name|lockTable
init|=
name|LockTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
static|static
block|{
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
comment|//        lockTable.registerListener(lockEventJsonListener);
comment|//        lockTable.registerListener(lockEventXmlListener);
block|}
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|removeListener
parameter_list|()
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
comment|//        lockTable.deregisterListener(lockEventJsonListener);
comment|//        lockTable.deregisterListener(lockEventXmlListener);
while|while
condition|(
name|lockCountListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
comment|//        while(lockEventJsonListener.isRegistered()) {}
comment|//        while(lockEventXmlListener.isRegistered()) {}
block|}
comment|/**      * Checks that there are no dangling Collection locks      * once eXist has finished starting up      *      * A failure of this test indicates either:      *   1) Locks have been acquired but not released      *   2) A bug has been introduced in {@link org.exist.storage.lock.LockManager}      */
annotation|@
name|Test
specifier|public
name|void
name|noCollectionLocksAfterStartup
parameter_list|()
throws|throws
name|InterruptedException
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockCountListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
comment|//check all locks are zero!
specifier|final
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|lockCount
init|=
name|lockCountListener
operator|.
name|getlockCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|0l
argument_list|,
name|lockCount
operator|.
name|_1
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0l
argument_list|,
name|lockCount
operator|.
name|_2
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/**      * Checks that no locks are leaked by {@link NativeBroker#getOrCreateCollectionExplicit(Txn, XmldbURI)}      *      * That is to say the number of read and write locks held after a call      * to {@link NativeBroker#getOrCreateCollectionExplicit(Txn, XmldbURI)} should be the same      * as before the call was made      */
annotation|@
name|Test
specifier|public
name|void
name|getOrCreateCollectionDoesNotGainLocks
parameter_list|()
throws|throws
name|InterruptedException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockCountListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
specifier|final
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|preLockCount
init|=
name|lockCountListener
operator|.
name|getlockCount
argument_list|()
decl_stmt|;
name|lockTable
operator|.
name|registerListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/a"
argument_list|)
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|lockTable
operator|.
name|deregisterListener
argument_list|(
name|lockCountListener
argument_list|)
expr_stmt|;
block|}
comment|// wait for the listener to be deregistered
while|while
condition|(
name|lockCountListener
operator|.
name|isRegistered
argument_list|()
condition|)
block|{
block|}
comment|//check that we haven't gained any locks
specifier|final
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|postLockCount
init|=
name|lockCountListener
operator|.
name|getlockCount
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|preLockCount
operator|.
name|_1
operator|.
name|longValue
argument_list|()
argument_list|,
name|postLockCount
operator|.
name|_1
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|preLockCount
operator|.
name|_2
operator|.
name|longValue
argument_list|()
argument_list|,
name|postLockCount
operator|.
name|_2
operator|.
name|longValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|ThreadSafe
specifier|private
specifier|static
class|class
name|LockCountListener
implements|implements
name|LockTable
operator|.
name|LockEventListener
block|{
comment|// holds a Map of all locks whose read or write count is greater than zero
comment|//<lockId, Tuple2<readCount, writeCount>>
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
argument_list|>
name|lockReadWriteCount
init|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|AtomicBoolean
name|registered
init|=
operator|new
name|AtomicBoolean
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|registered
parameter_list|()
block|{
name|registered
operator|.
name|compareAndSet
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unregistered
parameter_list|()
block|{
name|registered
operator|.
name|compareAndSet
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isRegistered
parameter_list|()
block|{
return|return
name|registered
operator|.
name|get
argument_list|()
return|;
block|}
comment|/**          * @return Tuple2<readCount, writeCount>          */
specifier|public
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|getlockCount
parameter_list|()
block|{
synchronized|synchronized
init|(
name|lockReadWriteCount
init|)
block|{
name|long
name|readCount
init|=
literal|0
decl_stmt|;
name|long
name|writeCount
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|value
range|:
name|lockReadWriteCount
operator|.
name|values
argument_list|()
control|)
block|{
name|readCount
operator|+=
name|value
operator|.
name|_1
expr_stmt|;
name|writeCount
operator|+=
name|value
operator|.
name|_2
expr_stmt|;
block|}
return|return
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|readCount
argument_list|,
name|writeCount
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|LockTable
operator|.
name|LockAction
name|lockAction
parameter_list|)
block|{
synchronized|synchronized
init|(
name|lockReadWriteCount
init|)
block|{
specifier|final
name|long
name|change
decl_stmt|;
switch|switch
condition|(
name|lockAction
operator|.
name|action
condition|)
block|{
case|case
name|Acquired
case|:
name|change
operator|=
literal|1
expr_stmt|;
break|break;
case|case
name|Released
case|:
name|change
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
default|default:
name|change
operator|=
literal|0
expr_stmt|;
block|}
name|Tuple2
argument_list|<
name|Long
argument_list|,
name|Long
argument_list|>
name|value
init|=
name|lockReadWriteCount
operator|.
name|get
argument_list|(
name|lockAction
operator|.
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|Tuple2
argument_list|<>
argument_list|(
literal|0l
argument_list|,
literal|0l
argument_list|)
expr_stmt|;
block|}
switch|switch
condition|(
name|lockAction
operator|.
name|mode
condition|)
block|{
case|case
name|READ_LOCK
case|:
name|value
operator|=
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|value
operator|.
name|_1
operator|+
name|change
argument_list|,
name|value
operator|.
name|_2
argument_list|)
expr_stmt|;
break|break;
case|case
name|WRITE_LOCK
case|:
name|value
operator|=
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|value
operator|.
name|_1
argument_list|,
name|value
operator|.
name|_2
operator|+
name|change
argument_list|)
expr_stmt|;
break|break;
block|}
if|if
condition|(
name|value
operator|.
name|_1
operator|<
literal|0
operator|||
name|value
operator|.
name|_2
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Cannot have less than zero locks!"
argument_list|)
throw|;
block|}
if|if
condition|(
name|value
operator|.
name|_1
operator|==
literal|0
operator|&&
name|value
operator|.
name|_2
operator|==
literal|0
condition|)
block|{
name|lockReadWriteCount
operator|.
name|remove
argument_list|(
name|lockAction
operator|.
name|id
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|lockReadWriteCount
operator|.
name|put
argument_list|(
name|lockAction
operator|.
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

