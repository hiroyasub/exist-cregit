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
name|concurrent
package|;
end_package

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
name|TestUtils
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
name|ExistXmldbEmbeddedServer
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
name|modules
operator|.
name|XMLResource
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
name|fail
import|;
end_import

begin_class
specifier|public
class|class
name|DeadlockTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|server
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DeadlockTest
operator|.
name|class
argument_list|)
decl_stmt|;
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
annotation|@
name|Test
specifier|public
name|void
name|deadlock
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|int
name|threads
init|=
literal|20
decl_stmt|;
specifier|final
name|int
name|resources
init|=
literal|200
decl_stmt|;
specifier|final
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
name|resources
argument_list|)
expr_stmt|;
name|writerThreads
index|[
name|i
index|]
operator|.
name|setName
argument_list|(
literal|"T"
operator|+
name|i
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
specifier|public
specifier|static
class|class
name|WriterThread
extends|extends
name|Thread
block|{
specifier|protected
name|int
name|resources
init|=
literal|0
decl_stmt|;
specifier|public
name|WriterThread
parameter_list|(
specifier|final
name|int
name|resources
parameter_list|)
block|{
name|this
operator|.
name|resources
operator|=
name|resources
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
try|try
block|{
specifier|final
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
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
name|resources
condition|;
name|i
operator|++
control|)
block|{
specifier|final
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
literal|"_"
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Storing document "
operator|+
name|document
operator|.
name|getId
argument_list|()
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
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
block|}
block|}
end_class

end_unit

