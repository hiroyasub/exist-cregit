begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|runner
operator|.
name|RunWith
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|PersistentDescendantOrSelfNodeKindTest
extends|extends
name|AbstractDescendantOrSelfNodeKindTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|TEST_DOCUMENT_NAME
init|=
literal|"PersistentDescendantOrSelfNodeKindTest.xml"
decl_stmt|;
specifier|private
name|String
name|getDbQuery
parameter_list|(
specifier|final
name|String
name|queryPostfix
parameter_list|)
block|{
return|return
literal|"let $doc := doc('/db/"
operator|+
name|TEST_DOCUMENT_NAME
operator|+
literal|"')\n"
operator|+
literal|"return\n"
operator|+
name|queryPostfix
return|;
block|}
annotation|@
name|Override
specifier|protected
name|ResourceSet
name|executeQueryOnDoc
parameter_list|(
specifier|final
name|String
name|docQuery
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|getDbQuery
argument_list|(
name|docQuery
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|storeTestDoc
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|root
operator|.
name|createResource
argument_list|(
name|TEST_DOCUMENT_NAME
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|TEST_DOCUMENT
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|removeTestDoc
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|Collection
name|root
init|=
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
specifier|final
name|Resource
name|res
init|=
name|root
operator|.
name|getResource
argument_list|(
name|TEST_DOCUMENT_NAME
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

