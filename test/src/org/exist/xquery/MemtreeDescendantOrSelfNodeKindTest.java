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
name|XQueryService
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|MemtreeDescendantOrSelfNodeKindTest
extends|extends
name|AbstractDescendantOrSelfNodeKindTest
block|{
specifier|private
name|String
name|getInMemoryQuery
parameter_list|(
specifier|final
name|String
name|queryPostfix
parameter_list|)
block|{
return|return
literal|"let $doc := document {\n"
operator|+
name|TEST_DOCUMENT
operator|+
literal|"\n}\n"
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
specifier|final
name|String
name|query
init|=
name|getInMemoryQuery
argument_list|(
name|docQuery
argument_list|)
decl_stmt|;
specifier|final
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
return|;
block|}
block|}
end_class

end_unit

