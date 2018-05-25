begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on 20 juil. 2004 $Id$  */
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
name|util
operator|.
name|io
operator|.
name|FastByteArrayInputStream
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
name|util
operator|.
name|URIUtils
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
name|Ignore
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
name|Service
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|Files
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
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardCopyOption
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
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
comment|/** A test case for accessing collections remotely  * @author jmv  * @author Pierrick Brihaye<pierrick.brihaye@free.fr>  */
end_comment

begin_class
specifier|public
class|class
name|RemoteCollectionTest
extends|extends
name|RemoteDBTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML_CONTENT
init|=
literal|"<xml/>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|BINARY_CONTENT
init|=
literal|"TEXT"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|XMLDBException
throws|,
name|IllegalAccessException
block|{
name|setUpRemoteDatabase
argument_list|()
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
block|{
name|removeCollection
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|indexQueryService
parameter_list|()
block|{
comment|// TODO .............
block|}
annotation|@
name|Test
specifier|public
name|void
name|getServices
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Service
index|[]
name|services
init|=
name|getCollection
argument_list|()
operator|.
name|getServices
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|services
operator|.
name|length
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteXPathQueryService
operator|.
name|class
argument_list|,
name|services
index|[
literal|0
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteCollectionManagementService
operator|.
name|class
argument_list|,
name|services
index|[
literal|1
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteUserManagementService
operator|.
name|class
argument_list|,
name|services
index|[
literal|2
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteDatabaseInstanceManager
operator|.
name|class
argument_list|,
name|services
index|[
literal|3
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteIndexQueryService
operator|.
name|class
argument_list|,
name|services
index|[
literal|4
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|RemoteXUpdateQueryService
operator|.
name|class
argument_list|,
name|services
index|[
literal|5
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|isRemoteCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertTrue
argument_list|(
name|getCollection
argument_list|()
operator|.
name|isRemoteCollection
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|getPath
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertEquals
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|getTestCollectionName
argument_list|()
argument_list|,
name|URIUtils
operator|.
name|urlDecodeUtf8
argument_list|(
name|getCollection
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|createStringResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|collection
init|=
name|getCollection
argument_list|()
decl_stmt|;
block|{
comment|// XML resource:
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
literal|"testresource"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collection
argument_list|,
name|resource
operator|.
name|getParentCollection
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"<?xml version='1.0'?><xml/>"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|{
comment|// binary resource:
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
literal|"testresource"
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collection
argument_list|,
name|resource
operator|.
name|getParentCollection
argument_list|()
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
literal|"some random binary data here :-)"
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
comment|/* issue 1874 */
specifier|public
name|void
name|createXMLFileResource
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
name|Collection
name|collection
init|=
name|getCollection
argument_list|()
decl_stmt|;
specifier|final
name|Resource
name|resource
init|=
name|collection
operator|.
name|createResource
argument_list|(
literal|"testresource"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|collection
argument_list|,
name|resource
operator|.
name|getParentCollection
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|sometxt
init|=
literal|"aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
decl_stmt|;
specifier|final
name|Path
name|path
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"tmp.xml"
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"<?xml version='1.0'?><xml>"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|5000
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"<element>"
argument_list|)
operator|.
name|append
argument_list|(
name|sometxt
argument_list|)
operator|.
name|append
argument_list|(
literal|"</element>"
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|"</xml>"
argument_list|)
expr_stmt|;
name|Files
operator|.
name|copy
argument_list|(
operator|new
name|FastByteArrayInputStream
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
argument_list|)
argument_list|,
name|path
argument_list|,
name|StandardCopyOption
operator|.
name|REPLACE_EXISTING
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|Files
operator|.
name|delete
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|getNonExistentResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|collection
init|=
name|getCollection
argument_list|()
decl_stmt|;
name|Resource
name|resource
init|=
name|collection
operator|.
name|getResource
argument_list|(
literal|"unknown.xml"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|listResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|xmlNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|xmlNames
operator|.
name|add
argument_list|(
literal|"xml1"
argument_list|)
expr_stmt|;
name|xmlNames
operator|.
name|add
argument_list|(
literal|"xml2"
argument_list|)
expr_stmt|;
name|xmlNames
operator|.
name|add
argument_list|(
literal|"xml3"
argument_list|)
expr_stmt|;
name|createResources
argument_list|(
name|xmlNames
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|ArrayList
argument_list|<
name|String
argument_list|>
name|binaryNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|binaryNames
operator|.
name|add
argument_list|(
literal|"b1"
argument_list|)
expr_stmt|;
name|binaryNames
operator|.
name|add
argument_list|(
literal|"b2"
argument_list|)
expr_stmt|;
name|createResources
argument_list|(
name|binaryNames
argument_list|,
literal|"BinaryResource"
argument_list|)
expr_stmt|;
name|String
index|[]
name|actualContents
init|=
name|getCollection
argument_list|()
operator|.
name|listResources
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
name|actualContents
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|xmlNames
operator|.
name|remove
argument_list|(
name|actualContents
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|binaryNames
operator|.
name|remove
argument_list|(
name|actualContents
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|xmlNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|binaryNames
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Trying to access a collection where the parent collection does 	 * not exist caused NullPointerException on DatabaseManager.getCollection() method. 	 */
annotation|@
name|Test
specifier|public
name|void
name|parent
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|c
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|getUri
argument_list|()
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|c
operator|.
name|getChildCollection
argument_list|(
literal|"b"
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|parentName
init|=
name|c
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|colName
init|=
name|parentName
operator|+
literal|"/a"
decl_stmt|;
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|getUri
argument_list|()
operator|+
name|parentName
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
comment|// following fails for XmlDb 20051203
name|c
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|getUri
argument_list|()
operator|+
name|colName
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|assertNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|createResources
parameter_list|(
name|ArrayList
argument_list|<
name|String
argument_list|>
name|names
parameter_list|,
name|String
name|type
parameter_list|)
throws|throws
name|XMLDBException
block|{
for|for
control|(
name|String
name|name
range|:
name|names
control|)
block|{
name|Resource
name|res
init|=
name|getCollection
argument_list|()
operator|.
name|createResource
argument_list|(
name|name
argument_list|,
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|.
name|equals
argument_list|(
literal|"XMLResource"
argument_list|)
condition|)
block|{
name|res
operator|.
name|setContent
argument_list|(
name|XML_CONTENT
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|res
operator|.
name|setContent
argument_list|(
name|BINARY_CONTENT
argument_list|)
expr_stmt|;
block|}
name|getCollection
argument_list|()
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

