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
name|security
operator|.
name|SecurityManager
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
name|BTreeCallback
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|TerminatedException
import|;
end_import

begin_comment
comment|/**  * Tests transaction management and basic recovery for the BTree base class.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|BTreeRecoverTest
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
name|BTreeRecoverTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|public
name|void
name|testAdd
parameter_list|()
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
name|TransactionManager
name|mgr
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|NodeIdFactory
name|idFact
init|=
name|pool
operator|.
name|getNodeFactory
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|Txn
name|txn
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
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
name|domDb
operator|.
name|setOwnerObject
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
comment|// put 1000 values into the btree
name|NodeId
name|id
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|1001
condition|;
name|i
operator|++
control|)
block|{
name|id
operator|=
name|idFact
operator|.
name|createInstance
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|domDb
operator|.
name|addValue
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
name|i
argument_list|)
expr_stmt|;
block|}
name|IndexQuery
name|idx
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
argument_list|,
name|idFact
operator|.
name|createInstance
argument_list|(
literal|800
argument_list|)
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
name|mgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
comment|// start a dirty, uncommitted transaction. This will be rolled back by the recovery.
name|txn
operator|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|801
init|;
name|i
operator|<
literal|2001
condition|;
name|i
operator|++
control|)
block|{
name|domDb
operator|.
name|addValue
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
name|i
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|101
init|;
name|i
operator|<
literal|301
condition|;
name|i
operator|++
control|)
block|{
name|domDb
operator|.
name|addValue
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
name|i
operator|*
literal|3
argument_list|)
expr_stmt|;
block|}
name|idx
operator|=
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
argument_list|,
name|idFact
operator|.
name|createInstance
argument_list|(
literal|600
argument_list|)
argument_list|)
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
specifier|public
name|void
name|testGet
parameter_list|()
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
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|TransactionManager
name|mgr
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|NodeIdFactory
name|idFact
init|=
name|pool
operator|.
name|getNodeFactory
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|SecurityManager
operator|.
name|SYSTEM_USER
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
name|GEQ
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
literal|1
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|domDb
operator|.
name|query
argument_list|(
name|query
argument_list|,
operator|new
name|IndexCallback
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found: "
operator|+
name|count
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|count
argument_list|,
literal|800
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
try|try
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
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
block|}
specifier|private
specifier|final
class|class
name|IndexCallback
implements|implements
name|BTreeCallback
block|{
specifier|public
name|boolean
name|indexInfo
parameter_list|(
name|Value
name|value
parameter_list|,
name|long
name|pointer
parameter_list|)
throws|throws
name|TerminatedException
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|final
name|byte
index|[]
name|data
init|=
name|value
operator|.
name|data
argument_list|()
decl_stmt|;
comment|//        	NodeId id = pool.getNodeFactory().createFromData(data[value.start() + 4], data, value.start() + 5);
comment|//            System.out.println(id + " -> " + pointer);
name|count
operator|++
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
block|}
end_class

end_unit

