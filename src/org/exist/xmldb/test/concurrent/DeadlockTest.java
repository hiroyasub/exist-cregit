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
name|xmldb
operator|.
name|test
operator|.
name|concurrent
package|;
end_package

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
name|xmldb
operator|.
name|DatabaseInstanceManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_class
specifier|public
class|class
name|DeadlockTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
specifier|final
name|String
name|DOCUMENT_CONTENT
init|=
literal|"<document>\n"
operator|+
literal|"<element1>value1</element1>\n"
operator|+
literal|"<element2>value2</element2>\n"
operator|+
literal|"<element3>value3</element3>\n"
operator|+
literal|"<element4>value4</element4>\n"
operator|+
literal|"</document>\n"
decl_stmt|;
specifier|private
name|String
name|rootCollection
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
name|Collection
name|root
decl_stmt|;
specifier|public
name|void
name|testDeadlock
parameter_list|()
block|{
name|int
name|threads
init|=
literal|20
decl_stmt|;
name|int
name|resources
init|=
literal|200
decl_stmt|;
try|try
block|{
name|Thread
index|[]
name|writerThreads
init|=
operator|new
name|Thread
index|[
name|threads
index|]
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
name|threads
condition|;
name|i
operator|++
control|)
block|{
name|writerThreads
index|[
name|i
index|]
operator|=
operator|new
name|WriterThread
argument_list|(
name|rootCollection
argument_list|,
name|resources
argument_list|)
expr_stmt|;
name|writerThreads
index|[
name|i
index|]
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|threads
condition|;
name|i
operator|++
control|)
block|{
name|writerThreads
index|[
name|i
index|]
operator|.
name|join
argument_list|()
expr_stmt|;
block|}
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
name|setUp
parameter_list|()
block|{
try|try
block|{
name|String
name|driver
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|driver
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|root
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
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|DatabaseInstanceManager
name|manager
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|manager
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
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
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|DeadlockTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
class|class
name|WriterThread
extends|extends
name|Thread
block|{
specifier|protected
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|resources
init|=
literal|0
decl_stmt|;
specifier|public
name|WriterThread
parameter_list|(
name|String
name|collectionURI
parameter_list|,
name|int
name|resources
parameter_list|)
throws|throws
name|Exception
block|{
name|this
operator|.
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionURI
argument_list|)
expr_stmt|;
name|this
operator|.
name|resources
operator|=
name|resources
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|resources
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|document
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"#"
operator|+
name|i
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|document
operator|.
name|setContent
argument_list|(
name|DOCUMENT_CONTENT
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"storing document "
operator|+
name|document
operator|.
name|getId
argument_list|()
operator|+
literal|"\n"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|document
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Writer "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" failed: "
operator|+
name|e
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

