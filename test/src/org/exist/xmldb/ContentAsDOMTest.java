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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
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
name|Permission
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
name|Account
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|dom
operator|.
name|DOMSource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamResult
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
name|Test
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|XMLResource
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
name|XQueryService
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbLocalTests
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  * Tests XMLResource.getContentAsDOM() for resources retrieved from  * an XQuery.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ContentAsDOMTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<root><test>ABCDEF</test></root>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"let $t := /root/test "
operator|+
literal|"return ("
operator|+
literal|"<!-- Comment -->,"
operator|+
literal|"<output>{$t}</output>)"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testContentAsDOM"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|getContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|TransformerConfigurationException
throws|,
name|TransformerException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
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
name|XQUERY
argument_list|)
decl_stmt|;
for|for
control|(
name|long
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|XMLResource
name|r
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|r
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|Transformer
name|t
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|t
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|t
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|StreamResult
name|output
init|=
operator|new
name|StreamResult
argument_list|(
name|System
operator|.
name|out
argument_list|)
decl_stmt|;
name|t
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|output
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
comment|// initialize driver
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
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
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// change ownership to guest
name|Account
name|guest
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|GUEST_UID
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|guest
argument_list|,
name|guest
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
name|Permission
operator|.
name|DEFAULT_COLLECTION_PERM
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|XML
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|resource
argument_list|,
name|guest
argument_list|,
name|GUEST_UID
argument_list|)
expr_stmt|;
comment|//change resource ownership to guest
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
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
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
comment|//shutdownDB the db
name|DatabaseInstanceManager
name|dim
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
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

