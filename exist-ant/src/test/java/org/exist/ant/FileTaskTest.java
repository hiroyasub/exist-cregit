begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|ant
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|tools
operator|.
name|ant
operator|.
name|Project
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
name|xmldb
operator|.
name|EXistResource
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|CoreMatchers
operator|.
name|containsString
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertThat
import|;
end_import

begin_class
specifier|public
class|class
name|FileTaskTest
extends|extends
name|AbstractTaskTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_COLLECTION_NAME
init|=
literal|"test"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_RESOURCE_NAME
init|=
literal|"test.xml"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ANT_TEST_DATA_TEST_COLLECTION
init|=
literal|"test.data.test.collection"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ANT_TEST_DATA_TEST_RESOURCE
init|=
literal|"test.data.test.resource"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ANT_TEST_DATA_USER
init|=
literal|"test.data.user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PROP_ANT_TEST_DATA_GROUP
init|=
literal|"test.data.group"
decl_stmt|;
annotation|@
name|Nullable
annotation|@
name|Override
specifier|protected
name|URL
name|getBuildFile
parameter_list|()
block|{
return|return
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"file.xml"
argument_list|)
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|fileSetup
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|col
init|=
name|existEmbeddedServer
operator|.
name|createCollection
argument_list|(
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
argument_list|,
name|TEST_COLLECTION_NAME
argument_list|)
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
name|col
operator|.
name|createResource
argument_list|(
name|TEST_RESOURCE_NAME
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
literal|"<test/>"
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|fileCleanup
parameter_list|()
throws|throws
name|XMLDBException
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
name|service
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|chmod
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Project
name|project
init|=
name|buildFileRule
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_TEST_COLLECTION
argument_list|,
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_TEST_RESOURCE
argument_list|,
name|TEST_RESOURCE_NAME
argument_list|)
expr_stmt|;
name|buildFileRule
operator|.
name|executeTarget
argument_list|(
literal|"chmod"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|project
operator|.
name|getProperty
argument_list|(
name|PROP_ANT_TEST_DATA_RESULT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|containsString
argument_list|(
name|TEST_RESOURCE_NAME
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|col
init|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildCollection
argument_list|(
name|TEST_COLLECTION_NAME
argument_list|)
decl_stmt|;
specifier|final
name|EXistResource
name|res
init|=
operator|(
name|EXistResource
operator|)
name|col
operator|.
name|getResource
argument_list|(
name|TEST_RESOURCE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"---rwxrwx"
argument_list|,
name|res
operator|.
name|getPermissions
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|chown
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Project
name|project
init|=
name|buildFileRule
operator|.
name|getProject
argument_list|()
decl_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_TEST_COLLECTION
argument_list|,
name|TEST_COLLECTION_NAME
argument_list|)
expr_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_TEST_RESOURCE
argument_list|,
name|TEST_RESOURCE_NAME
argument_list|)
expr_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_USER
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|)
expr_stmt|;
name|project
operator|.
name|setProperty
argument_list|(
name|PROP_ANT_TEST_DATA_GROUP
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|)
expr_stmt|;
name|buildFileRule
operator|.
name|executeTarget
argument_list|(
literal|"chown"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|project
operator|.
name|getProperty
argument_list|(
name|PROP_ANT_TEST_DATA_RESULT
argument_list|)
decl_stmt|;
name|assertThat
argument_list|(
name|result
argument_list|,
name|containsString
argument_list|(
name|TEST_RESOURCE_NAME
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|col
init|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildCollection
argument_list|(
name|TEST_COLLECTION_NAME
argument_list|)
decl_stmt|;
specifier|final
name|EXistResource
name|res
init|=
operator|(
name|EXistResource
operator|)
name|col
operator|.
name|getResource
argument_list|(
name|TEST_RESOURCE_NAME
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|,
name|res
operator|.
name|getPermissions
argument_list|()
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|,
name|res
operator|.
name|getPermissions
argument_list|()
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
argument_list|(
literal|"Would require implementing an UnlockResourceTask as well"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|lockResource
parameter_list|()
block|{
name|buildFileRule
operator|.
name|executeTarget
argument_list|(
literal|"lockResource"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

