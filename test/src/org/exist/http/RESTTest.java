begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|HttpClient
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
name|ExistWebServer
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
name|XmldbURI
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

begin_class
specifier|public
specifier|abstract
class|class
name|RESTTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
name|String
name|getRestUrl
parameter_list|()
block|{
return|return
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
return|;
block|}
specifier|protected
specifier|static
name|String
name|getCollectionRootUri
parameter_list|()
block|{
return|return
name|getRestUrl
argument_list|()
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
return|;
block|}
specifier|protected
specifier|static
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
block|}
end_class

end_unit

