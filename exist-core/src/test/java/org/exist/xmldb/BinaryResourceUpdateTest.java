begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
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
name|BinaryResource
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
name|java
operator|.
name|net
operator|.
name|URISyntaxException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|Path
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
name|org
operator|.
name|junit
operator|.
name|Test
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

begin_class
specifier|public
class|class
name|BinaryResourceUpdateTest
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
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testBinaryResource"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|REPEAT
init|=
literal|10
decl_stmt|;
specifier|private
name|URL
name|binFile
decl_stmt|;
specifier|private
name|URL
name|xmlFile
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|updateBinary
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|URISyntaxException
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
name|REPEAT
condition|;
name|i
operator|++
control|)
block|{
name|BinaryResource
name|binaryResource
init|=
operator|(
name|BinaryResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test1.xml"
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|binaryResource
operator|.
name|setContent
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|binFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|binaryResource
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"test1.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XMLResource
name|xmlResource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test2.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|xmlResource
operator|.
name|setContent
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|xmlFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|xmlResource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"test2.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
comment|// with same docname test fails for windows
annotation|@
name|Test
specifier|public
name|void
name|updateBinary_windows
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|URISyntaxException
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
name|REPEAT
condition|;
name|i
operator|++
control|)
block|{
name|BinaryResource
name|binaryResource
init|=
operator|(
name|BinaryResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|binaryResource
operator|.
name|setContent
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|binFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|binaryResource
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XMLResource
name|xmlResource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|xmlResource
operator|.
name|setContent
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
name|xmlFile
operator|.
name|toURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|xmlResource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|CollectionManagementService
name|service
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
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|binFile
operator|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"org/exist/xmldb/test.bin"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|binFile
argument_list|)
expr_stmt|;
name|xmlFile
operator|=
name|getClass
argument_list|()
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"org/exist/xmldb/test.xml"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|xmlFile
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//delete the test collection
specifier|final
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|testCollection
operator|.
name|getParentCollection
argument_list|()
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
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|binFile
operator|=
literal|null
expr_stmt|;
name|xmlFile
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit
