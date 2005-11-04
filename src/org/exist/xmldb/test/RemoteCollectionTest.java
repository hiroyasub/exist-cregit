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
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
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
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|StandaloneServer
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
name|validation
operator|.
name|service
operator|.
name|RemoteValidationService
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
name|RemoteCollectionManagementService
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
name|RemoteDatabaseInstanceManager
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
name|RemoteIndexQueryService
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
name|RemoteUserManagementService
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
name|RemoteXPathQueryService
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
name|RemoteXUpdateQueryService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|util
operator|.
name|MultiException
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
specifier|static
name|StandaloneServer
name|server
init|=
literal|null
decl_stmt|;
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
specifier|public
name|RemoteCollectionTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|//Don't worry about closing the server : the shutdown hook will do the job
name|initServer
argument_list|()
expr_stmt|;
name|setUpRemoteDatabase
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|removeCollection
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initServer
parameter_list|()
throws|throws
name|Exception
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|StandaloneServer
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{}
decl_stmt|;
name|server
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MultiException
name|e
parameter_list|)
block|{
name|boolean
name|rethrow
init|=
literal|true
decl_stmt|;
name|Iterator
name|i
init|=
name|e
operator|.
name|getExceptions
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Exception
name|e0
init|=
operator|(
name|Exception
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e0
operator|instanceof
name|BindException
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A server is running already !"
argument_list|)
expr_stmt|;
name|rethrow
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|rethrow
condition|)
throw|throw
name|e
throw|;
block|}
block|}
block|}
block|}
specifier|public
name|void
name|testIndexQueryService
parameter_list|()
block|{
comment|// TODO .............
block|}
specifier|public
name|void
name|testGetServices
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
literal|7
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
name|assertEquals
argument_list|(
name|RemoteValidationService
operator|.
name|class
argument_list|,
name|services
index|[
literal|6
index|]
operator|.
name|getClass
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testIsRemoteCollection
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
specifier|public
name|void
name|testGetPath
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|assertEquals
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/"
operator|+
name|getTestCollectionName
argument_list|()
argument_list|,
name|getCollection
argument_list|()
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testCreateResource
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
specifier|public
name|void
name|testGetNonExistentResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Retrieving non-existing resource"
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|testListResources
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|ArrayList
name|xmlNames
init|=
operator|new
name|ArrayList
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
name|binaryNames
init|=
operator|new
name|ArrayList
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Resources found: "
operator|+
name|actualContents
operator|.
name|length
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
specifier|private
name|void
name|createResources
parameter_list|(
name|ArrayList
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
name|Iterator
name|i
init|=
name|names
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
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
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
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
name|res
operator|.
name|setContent
argument_list|(
name|XML_CONTENT
argument_list|)
expr_stmt|;
else|else
name|res
operator|.
name|setContent
argument_list|(
name|BINARY_CONTENT
argument_list|)
expr_stmt|;
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
name|RemoteCollectionTest
operator|.
name|class
argument_list|)
expr_stmt|;
comment|//Explicit shutdown for the shutdown hook
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

