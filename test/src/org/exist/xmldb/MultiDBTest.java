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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileOutputStream
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
name|util
operator|.
name|SingleInstanceConfiguration
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
name|MimeTable
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
name|MimeType
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
name|base
operator|.
name|ResourceIterator
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
name|ResourceSet
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
name|XMLDBException
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
name|CollectionManagementService
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|MultiDBTest
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
name|MultiDBTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|int
name|INSTANCE_COUNT
init|=
literal|5
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CONFIG
init|=
literal|"<exist>"
operator|+
literal|"<db-connection database=\"native\" files=\".\" cacheSize=\"32M\">"
operator|+
literal|"<pool min=\"1\" max=\"5\" sync-period=\"120000\"/>"
operator|+
literal|"</db-connection>"
operator|+
literal|"</exist>"
decl_stmt|;
specifier|public
name|void
name|testStore
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
name|INSTANCE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:test"
operator|+
name|i
operator|+
literal|"://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
decl_stmt|;
name|Collection
name|test
init|=
name|root
operator|.
name|getChildCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
name|test
operator|==
literal|null
condition|)
block|{
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|test
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
block|}
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|File
name|samples
init|=
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/shakespeare"
argument_list|)
decl_stmt|;
name|File
index|[]
name|files
init|=
name|samples
operator|.
name|listFiles
argument_list|()
decl_stmt|;
name|MimeTable
name|mimeTab
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
name|MimeType
name|mime
init|=
name|mimeTab
operator|.
name|getContentTypeFor
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
operator|&&
name|mime
operator|.
name|isXMLType
argument_list|()
condition|)
name|loadFile
argument_list|(
name|test
argument_list|,
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|doQuery
argument_list|(
name|test
argument_list|,
literal|"//SPEECH[SPEAKER='HAMLET']"
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
specifier|static
name|void
name|loadFile
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|path
parameter_list|)
block|{
try|try
block|{
comment|// create new XMLResource; an id will be assigned to the new resource
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
name|path
operator|.
name|substring
argument_list|(
name|path
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
argument_list|)
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|document
operator|.
name|setContent
argument_list|(
operator|new
name|File
argument_list|(
name|path
argument_list|)
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
specifier|static
name|void
name|doQuery
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|String
name|query
parameter_list|)
block|{
try|try
block|{
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Found "
operator|+
name|result
operator|.
name|getSize
argument_list|()
operator|+
literal|" results."
argument_list|)
expr_stmt|;
for|for
control|(
name|ResourceIterator
name|i
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|String
name|content
init|=
name|i
operator|.
name|nextResource
argument_list|()
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|String
name|homeDir
init|=
name|SingleInstanceConfiguration
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|homeDir
operator|==
literal|null
condition|)
name|homeDir
operator|=
literal|"."
expr_stmt|;
else|else
name|homeDir
operator|=
operator|(
operator|new
name|File
argument_list|(
name|homeDir
argument_list|)
operator|)
operator|.
name|getParent
argument_list|()
expr_stmt|;
name|File
name|testDir
init|=
operator|new
name|File
argument_list|(
name|homeDir
operator|+
name|File
operator|.
name|separatorChar
operator|+
literal|"test"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|testDir
operator|.
name|canWrite
argument_list|()
condition|)
name|testDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
try|try
block|{
comment|// initialize database drivers
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
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
name|INSTANCE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|testDir
argument_list|,
literal|"db"
operator|+
name|i
argument_list|)
decl_stmt|;
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
name|File
name|conf
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
literal|"conf.xml"
argument_list|)
decl_stmt|;
name|FileOutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|conf
argument_list|)
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|CONFIG
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
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
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"configuration"
argument_list|,
name|conf
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"database-id"
argument_list|,
literal|"test"
operator|+
name|i
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
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
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|Runtime
name|rt
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
name|long
name|free
init|=
name|rt
operator|.
name|freeMemory
argument_list|()
operator|/
literal|1024
decl_stmt|;
name|long
name|total
init|=
name|rt
operator|.
name|totalMemory
argument_list|()
operator|/
literal|1024
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
name|INSTANCE_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:test"
operator|+
name|i
operator|+
literal|"://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|removeCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|mgr
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
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Mem total: "
operator|+
name|total
operator|+
literal|"K"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Mem free: "
operator|+
name|free
operator|+
literal|"K"
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
block|}
end_class

end_unit

