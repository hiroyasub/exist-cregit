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
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|collections
operator|.
name|IndexInfo
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
name|DocumentSet
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
name|BrokerPool
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
name|DBBroker
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
name|NativeBroker
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
name|xupdate
operator|.
name|Modification
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xupdate
operator|.
name|XUpdateProcessor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_class
specifier|public
class|class
name|RenameTest
extends|extends
name|AbstractUpdateTest
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
name|RenameTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testUpdate
parameter_list|()
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
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
name|IndexInfo
name|info
init|=
name|init
argument_list|(
name|broker
argument_list|,
name|mgr
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|XUpdateProcessor
name|proc
init|=
operator|new
name|XUpdateProcessor
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|proc
argument_list|)
expr_stmt|;
name|Txn
name|transaction
init|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|String
name|xupdate
decl_stmt|;
name|Modification
name|modifications
index|[]
decl_stmt|;
comment|// append some new element to records
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
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:append select=\"/products\">"
operator|+
literal|"<product>"
operator|+
literal|"<xu:attribute name=\"id\"><xu:value-of select=\"count(/products/product) + 1\"/></xu:attribute>"
operator|+
literal|"<description>Product "
operator|+
name|i
operator|+
literal|"</description>"
operator|+
literal|"<price>"
operator|+
operator|(
name|i
operator|*
literal|2.5
operator|)
operator|+
literal|"</price>"
operator|+
literal|"<stock>"
operator|+
operator|(
name|i
operator|*
literal|10
operator|)
operator|+
literal|"</stock>"
operator|+
literal|"</product>"
operator|+
literal|"</xu:append>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|domDb
operator|.
name|debugPages
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|commit
argument_list|(
name|transaction
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
comment|// the following transaction will not be committed and thus undone during recovery
name|transaction
operator|=
name|mgr
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
comment|// rename elements
name|xupdate
operator|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xu:rename select=\"/products/product/description\">descript</xu:rename>"
operator|+
literal|"</xu:modifications>"
expr_stmt|;
name|proc
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|proc
operator|.
name|setDocumentSet
argument_list|(
name|docs
argument_list|)
expr_stmt|;
name|modifications
operator|=
name|proc
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xupdate
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|modifications
argument_list|)
expr_stmt|;
name|modifications
index|[
literal|0
index|]
operator|.
name|process
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
name|proc
operator|.
name|reset
argument_list|()
expr_stmt|;
comment|//          Don't commit...
name|pool
operator|.
name|getTransactionManager
argument_list|()
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
block|}
end_class

end_unit

