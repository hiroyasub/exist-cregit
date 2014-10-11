begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|update
package|;
end_package

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
name|XQueryService
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|UpdateValueTest
extends|extends
name|AbstractTestUpdate
block|{
annotation|@
name|Test
specifier|public
name|void
name|updateNamespacedAttribute
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|docName
init|=
literal|"pathNs.xml"
decl_stmt|;
name|XQueryService
name|service
init|=
name|storeXMLStringAndGetQueryService
argument_list|(
name|docName
argument_list|,
literal|"<test><t xml:id=\"id1\"/></test>"
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//t[@xml:id eq 'id1']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"update value //t/@xml:id with 'id2'"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"//t[@xml:id eq 'id2']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
name|docName
argument_list|,
literal|"id('id2', /test)"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

