begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestDataGenerator
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
name|EXistXPathQueryService
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
name|IndexQueryService
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
name|util
operator|.
name|Random
import|;
end_import

begin_class
specifier|public
class|class
name|ProtectedModeTest
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
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"<create path=\"//section/@id\" type=\"xs:string\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|COLLECTION_COUNT
init|=
literal|20
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|DOCUMENT_COUNT
init|=
literal|20
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|generateXQ
init|=
literal|"<book id=\"{$filename}\" n=\"{$count}\">"
operator|+
literal|"<chapter>"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"       {"
operator|+
literal|"           for $section in 1 to 8 return"
operator|+
literal|"<section id=\"sect{$section}\">"
operator|+
literal|"<title>{pt:random-text(7)}</title>"
operator|+
literal|"                   {"
operator|+
literal|"                       for $para in 1 to 10 return"
operator|+
literal|"<para>{pt:random-text(40)}</para>"
operator|+
literal|"                   }"
operator|+
literal|"</section>"
operator|+
literal|"       }"
operator|+
literal|"</chapter>"
operator|+
literal|"</book>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|queryCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/protected"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|EXistXPathQueryService
name|service
init|=
operator|(
name|EXistXPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
try|try
block|{
name|service
operator|.
name|beginProtected
argument_list|()
expr_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"collection('/db/protected/test5')//book"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
name|DOCUMENT_COUNT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|endProtected
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|queryRoot
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/protected"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|EXistXPathQueryService
name|service
init|=
operator|(
name|EXistXPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
try|try
block|{
name|service
operator|.
name|beginProtected
argument_list|()
expr_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"//book"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
name|COLLECTION_COUNT
operator|*
name|DOCUMENT_COUNT
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|endProtected
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|queryDocs
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/protected"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
specifier|final
name|EXistXPathQueryService
name|service
init|=
operator|(
name|EXistXPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Random
name|random
init|=
operator|new
name|Random
argument_list|()
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
name|COLLECTION_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|String
name|docURI
init|=
literal|"doc('/db/protected/test"
operator|+
name|i
operator|+
literal|"/xdb"
operator|+
name|random
operator|.
name|nextInt
argument_list|(
name|DOCUMENT_COUNT
argument_list|)
operator|+
literal|".xml')"
decl_stmt|;
try|try
block|{
name|service
operator|.
name|beginProtected
argument_list|()
expr_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|docURI
operator|+
literal|"//book"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|service
operator|.
name|endProtected
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setupDb
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
block|{
name|CollectionManagementService
name|mgmt
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
specifier|final
name|Collection
name|collection
init|=
name|mgmt
operator|.
name|createCollection
argument_list|(
literal|"protected"
argument_list|)
decl_stmt|;
specifier|final
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
specifier|final
name|XMLResource
name|hamlet
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
literal|"hamlet.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|hamlet
operator|.
name|setContent
argument_list|(
name|TestUtils
operator|.
name|resolveShakespeareSample
argument_list|(
literal|"hamlet.xml"
argument_list|)
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|hamlet
argument_list|)
expr_stmt|;
name|mgmt
operator|=
operator|(
name|CollectionManagementService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
specifier|final
name|TestDataGenerator
name|generator
init|=
operator|new
name|TestDataGenerator
argument_list|(
literal|"xdb"
argument_list|,
name|DOCUMENT_COUNT
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
name|COLLECTION_COUNT
condition|;
name|i
operator|++
control|)
block|{
name|Collection
name|currentColl
init|=
name|mgmt
operator|.
name|createCollection
argument_list|(
literal|"test"
operator|+
name|i
argument_list|)
decl_stmt|;
specifier|final
name|Path
index|[]
name|files
init|=
name|generator
operator|.
name|generate
argument_list|(
name|currentColl
argument_list|,
name|generateXQ
argument_list|)
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
specifier|final
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|currentColl
operator|.
name|createResource
argument_list|(
literal|"xdb"
operator|+
name|j
operator|+
literal|".xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|toFile
argument_list|()
argument_list|)
expr_stmt|;
name|currentColl
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanupDb
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|CollectionManagementService
name|cmgr
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
name|cmgr
operator|.
name|removeCollection
argument_list|(
literal|"protected"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

