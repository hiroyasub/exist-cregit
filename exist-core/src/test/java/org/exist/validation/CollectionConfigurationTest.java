begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: CollectionConfigurationValidationModeTest.java 6709 2007-10-12 20:58:52Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

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
name|junit
operator|.
name|ClassRule
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
name|XMLDBException
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
name|modules
operator|.
name|CollectionManagementService
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
name|*
import|;
end_import

begin_comment
comment|/**  *  Some tests regarding invalid collection.xconf documents.  *   * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|CollectionConfigurationTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
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
name|String
name|invalidConfig
init|=
literal|"<invalid/>"
decl_stmt|;
specifier|private
name|void
name|createCollection
parameter_list|(
name|String
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|cmservice
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|testCollection
init|=
name|cmservice
operator|.
name|createCollection
argument_list|(
name|collection
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|testCollection
operator|=
name|cmservice
operator|.
name|createCollection
argument_list|(
literal|"/db/system/config"
operator|+
name|collection
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeCollectionXconf
parameter_list|(
name|String
name|collection
parameter_list|,
name|String
name|document
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|result
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"xmldb:store(\""
operator|+
name|collection
operator|+
literal|"\", \"collection.xconf\", "
operator|+
name|document
operator|+
literal|")"
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Store xconf"
argument_list|,
name|collection
operator|+
literal|"/collection.xconf"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|insertInvalidCollectionXconf
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|createCollection
argument_list|(
literal|"/db/system/config/db/foobar"
argument_list|)
expr_stmt|;
name|storeCollectionXconf
argument_list|(
literal|"/db/system/config/db/foobar"
argument_list|,
name|invalidConfig
argument_list|)
expr_stmt|;
name|createCollection
argument_list|(
literal|"/db/system/config/db/foobar"
argument_list|)
expr_stmt|;
name|storeCollectionXconf
argument_list|(
literal|"/db/system/config/db/foobar"
argument_list|,
name|invalidConfig
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

