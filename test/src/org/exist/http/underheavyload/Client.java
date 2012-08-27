begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *   */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|underheavyload
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
name|Header
import|;
end_import

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
name|apache
operator|.
name|commons
operator|.
name|httpclient
operator|.
name|URI
import|;
end_import

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
name|methods
operator|.
name|PostMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * @author dmitriy  *  */
end_comment

begin_class
specifier|public
class|class
name|Client
implements|implements
name|Runnable
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Client
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|running
decl_stmt|;
specifier|private
name|ClientsManager
name|manager
decl_stmt|;
specifier|public
name|Client
parameter_list|()
block|{
name|running
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|Client
parameter_list|(
name|ClientsManager
name|clients
parameter_list|)
block|{
name|this
operator|.
name|manager
operator|=
name|clients
expr_stmt|;
name|running
operator|=
literal|true
expr_stmt|;
block|}
comment|//	@Override
specifier|public
name|void
name|run
parameter_list|()
block|{
name|HttpClient
name|client
init|=
operator|new
name|HttpClient
argument_list|()
decl_stmt|;
comment|// connect to a login page to retrieve session ID
name|PostMethod
name|method
init|=
operator|new
name|PostMethod
argument_list|(
name|getURL
argument_list|()
argument_list|)
decl_stmt|;
comment|// post auth information with it
name|method
operator|.
name|setParameter
argument_list|(
literal|"username"
argument_list|,
literal|"admin"
argument_list|)
expr_stmt|;
name|method
operator|.
name|setParameter
argument_list|(
literal|"password"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
try|try
block|{
name|client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
name|String
name|redirectLocation
init|=
literal|null
decl_stmt|;
name|Header
name|locationHeader
init|=
name|method
operator|.
name|getResponseHeader
argument_list|(
literal|"location"
argument_list|)
decl_stmt|;
if|if
condition|(
name|locationHeader
operator|!=
literal|null
condition|)
block|{
name|redirectLocation
operator|=
name|locationHeader
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// The response is invalid and did not provide the new location for
comment|// the resource. Report an error or possibly handle the response
comment|// like a 404 Not Found error.
comment|//LOG.debug(method.getResponseBodyAsString());
block|}
name|method
operator|.
name|setURI
argument_list|(
operator|new
name|URI
argument_list|(
name|redirectLocation
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
name|client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
comment|// store the session info for the next call
name|Header
index|[]
name|headers
init|=
name|method
operator|.
name|getResponseHeaders
argument_list|()
decl_stmt|;
comment|//TODO: fetch links
while|while
condition|(
name|running
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
comment|// connect to a page you're interested...
comment|// jetty.port.jetty
name|PostMethod
name|getMethod
init|=
operator|new
name|PostMethod
argument_list|(
literal|"http://localhost:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
operator|+
literal|"/exist/admin/admin.xql?panel=xqueries"
argument_list|)
decl_stmt|;
comment|// ...using the session ID retrieved before
for|for
control|(
name|Header
name|header
range|:
name|headers
control|)
block|{
name|getMethod
operator|.
name|setRequestHeader
argument_list|(
name|header
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|executeMethod
argument_list|(
name|method
argument_list|)
expr_stmt|;
comment|// log the page source
comment|//LOG.info(method.getResponseBodyAsString());
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|shutdown
parameter_list|()
block|{
name|running
operator|=
literal|false
expr_stmt|;
block|}
specifier|private
name|String
name|getURL
parameter_list|()
block|{
if|if
condition|(
name|manager
operator|==
literal|null
condition|)
comment|// jetty.port.jetty
return|return
literal|"http://localhost:"
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"jetty.port"
argument_list|)
operator|+
literal|"/exist/admin"
return|;
else|else
return|return
name|manager
operator|.
name|getURL
argument_list|()
return|;
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
name|Client
name|client
init|=
operator|new
name|Client
argument_list|()
decl_stmt|;
name|client
operator|.
name|run
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

