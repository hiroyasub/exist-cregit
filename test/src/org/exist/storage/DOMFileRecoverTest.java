begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Writer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeIdFactory
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
name|btree
operator|.
name|IndexQuery
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
name|btree
operator|.
name|Value
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
name|dom
operator|.
name|DOMFile
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
name|TransactionManager
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
name|util
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Tests transaction management  and basic recovery for the DOMFile class.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|DOMFileRecoverTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|DOMFileRecoverTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|void
name|testAdd
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
specifier|final
name|NodeIdFactory
name|idFact
init|=
name|pool
operator|.
name|getNodeFactory
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
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Add some random data and force db corruption ...\n"
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
specifier|final
name|DOMFile
name|domDb
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDOMFile
argument_list|()
decl_stmt|;
name|domDb
operator|.
name|setOwnerObject
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|TransactionManager
name|mgr
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|long
name|firstToRemove
init|=
operator|-
literal|1
decl_stmt|;
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
comment|// put 1000 values into the btree
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|(
literal|"Value"
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|NodeId
name|id
init|=
name|idFact
operator|.
name|createInstance
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|long
name|addr
init|=
name|domDb
operator|.
name|put
argument_list|(
name|txn
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|500
argument_list|,
name|id
argument_list|)
argument_list|,
name|data
argument_list|)
decl_stmt|;
comment|//              TODO : test addr ?
if|if
condition|(
name|i
operator|==
literal|1
condition|)
name|firstToRemove
operator|=
name|addr
expr_stmt|;
block|}
name|domDb
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
comment|// remove all
name|NativeBroker
operator|.
name|NodeRef
name|ref
init|=
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|500
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|ref
argument_list|)
expr_stmt|;
name|IndexQuery
name|idx
init|=
operator|new
name|IndexQuery
argument_list|(
name|IndexQuery
operator|.
name|TRUNC_RIGHT
argument_list|,
name|ref
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|idx
argument_list|)
expr_stmt|;
name|domDb
operator|.
name|remove
argument_list|(
name|txn
argument_list|,
name|idx
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|domDb
operator|.
name|removeAll
argument_list|(
name|txn
argument_list|,
name|firstToRemove
argument_list|)
expr_stmt|;
comment|// put some more
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|10000
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|(
literal|"Value"
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|long
name|addr
init|=
name|domDb
operator|.
name|put
argument_list|(
name|txn
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|500
argument_list|,
name|idFact
operator|.
name|createInstance
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|)
decl_stmt|;
comment|//              TODO : test addr ?
block|}
name|domDb
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction commited ..."
argument_list|)
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
comment|// put 1000 new values into the btree
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|1000
condition|;
name|i
operator|++
control|)
block|{
name|byte
index|[]
name|data
init|=
operator|(
literal|"Value"
operator|+
name|i
operator|)
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|long
name|addr
init|=
name|domDb
operator|.
name|put
argument_list|(
name|txn
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|501
argument_list|,
name|idFact
operator|.
name|createInstance
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|,
name|data
argument_list|)
decl_stmt|;
comment|//              TODO : test addr ?
if|if
condition|(
name|i
operator|==
literal|1
condition|)
name|firstToRemove
operator|=
name|addr
expr_stmt|;
block|}
name|domDb
operator|.
name|closeDocument
argument_list|()
expr_stmt|;
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction commited ..."
argument_list|)
expr_stmt|;
block|}
comment|// the following transaction is not committed and will be rolled back during recovery
try|try
init|(
specifier|final
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<=
literal|200
condition|;
name|i
operator|++
control|)
block|{
name|domDb
operator|.
name|remove
argument_list|(
name|txn
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|500
argument_list|,
name|idFact
operator|.
name|createInstance
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|IndexQuery
name|idx
init|=
operator|new
name|IndexQuery
argument_list|(
name|IndexQuery
operator|.
name|TRUNC_RIGHT
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|501
argument_list|)
argument_list|)
decl_stmt|;
name|domDb
operator|.
name|remove
argument_list|(
name|txn
argument_list|,
name|idx
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|domDb
operator|.
name|removeAll
argument_list|(
name|txn
argument_list|,
name|firstToRemove
argument_list|)
expr_stmt|;
comment|// Don't commit...
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
name|mgr
operator|.
name|getJournal
argument_list|()
operator|.
name|flushToLog
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction interrupted ..."
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|domDb
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testGet
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Recover and read the data ...\n"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|TransactionManager
name|mgr
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|mgr
argument_list|)
expr_stmt|;
name|DOMFile
name|domDb
init|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDOMFile
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|domDb
argument_list|)
expr_stmt|;
name|domDb
operator|.
name|setOwnerObject
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|IndexQuery
name|query
init|=
operator|new
name|IndexQuery
argument_list|(
name|IndexQuery
operator|.
name|GT
argument_list|,
operator|new
name|NativeBroker
operator|.
name|NodeRef
argument_list|(
literal|500
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|?
argument_list|>
name|keys
init|=
name|domDb
operator|.
name|findKeys
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|keys
argument_list|)
expr_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|?
argument_list|>
name|i
init|=
name|keys
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|Value
name|key
init|=
operator|(
name|Value
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|key
argument_list|)
expr_stmt|;
name|Value
name|value
init|=
name|domDb
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|value
operator|.
name|data
argument_list|()
argument_list|,
name|value
operator|.
name|start
argument_list|()
argument_list|,
name|value
operator|.
name|getLength
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Values read: "
operator|+
name|count
argument_list|)
expr_stmt|;
name|Writer
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|domDb
operator|.
name|dump
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

