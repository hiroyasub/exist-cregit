begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
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
name|CollectionImpl
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
name|BeforeClass
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggerTest
block|{
specifier|static
name|Collection
name|test
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testDebugger
parameter_list|()
block|{
name|assertNotNull
argument_list|(
literal|"Database wasn't initilised."
argument_list|,
name|database
argument_list|)
expr_stmt|;
name|Debugger
name|debugger
decl_stmt|;
try|try
block|{
name|debugger
operator|=
operator|new
name|DebuggerImpl
argument_list|()
expr_stmt|;
name|DebuggingSource
name|source
init|=
name|debugger
operator|.
name|init
argument_list|(
literal|"http://127.0.0.1:8080/exist/admin/admin.xql"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
literal|"Debugging source can't be NULL."
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|source
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"IO exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
name|assertNotNull
argument_list|(
literal|"Timeout exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
name|database
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
parameter_list|()
block|{
comment|//        // initialize XML:DB driver
comment|//        try {
comment|//            Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");
comment|//            Database database = (Database) cl.newInstance();
comment|//            database.setProperty("create-database", "true");
comment|//            DatabaseManager.registerDatabase(database);
comment|//
comment|//            org.xmldb.api.base.Collection root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);
comment|//            CollectionManagementService mgmt = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
comment|//            test = mgmt.createCollection("test");
comment|//
comment|//        } catch (Exception e) {
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
name|database
operator|=
operator|new
name|org
operator|.
name|exist
operator|.
name|start
operator|.
name|Main
argument_list|(
literal|"jetty"
argument_list|)
expr_stmt|;
name|database
operator|.
name|run
argument_list|(
operator|new
name|String
index|[]
block|{
literal|"jetty"
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|closeDB
parameter_list|()
block|{
comment|//        try {
comment|//            Collection root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION, "admin", null);
comment|//            CollectionManagementService cmgr = (CollectionManagementService) root.getService("CollectionManagementService", "1.0");
comment|//            cmgr.removeCollection("test");
comment|//
comment|//            DatabaseInstanceManager mgr = (DatabaseInstanceManager) root.getService("DatabaseInstanceManager", "1.0");
comment|//            mgr.shutdown();
comment|//        } catch (XMLDBException e) {
comment|//            e.printStackTrace();
comment|//            fail(e.getMessage());
comment|//        }
name|database
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

