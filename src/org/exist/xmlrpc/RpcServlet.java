begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
import|;
end_import

begin_class
specifier|public
class|class
name|RpcServlet
extends|extends
name|HttpServlet
block|{
specifier|protected
name|XmlRpcServer
name|xmlrpc
decl_stmt|;
comment|/** id of the database registred against the BrokerPool */
specifier|protected
name|String
name|databaseid
init|=
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
decl_stmt|;
comment|/**      *  Handle XML-RPC requests      *      *@param  request               Description of the Parameter      *@param  response              Description of the Parameter      *@exception  ServletException  Description of the Exception      *@exception  IOException       Description of the Exception      */
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|String
name|user
init|=
literal|"admin"
decl_stmt|;
name|String
name|password
init|=
literal|null
decl_stmt|;
name|String
name|auth
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authorization"
argument_list|)
decl_stmt|;
if|if
condition|(
name|auth
operator|!=
literal|null
condition|)
block|{
name|byte
index|[]
name|c
init|=
name|Base64
operator|.
name|decode
argument_list|(
name|auth
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|user
operator|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|password
operator|=
name|s
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
name|byte
index|[]
name|result
init|=
name|xmlrpc
operator|.
name|execute
argument_list|(
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/xml"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setContentLength
argument_list|(
name|result
operator|.
name|length
argument_list|)
expr_stmt|;
name|OutputStream
name|output
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|output
operator|.
name|write
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|/**      *  Create XML-RPC handler      *      *@param  config                Description of the Parameter      *@exception  ServletException  Description of the Exception      */
specifier|public
name|void
name|init
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
comment|//<frederic.glorieux@ajlsm.com> to allow multi-instance xmlrpc server, use a databaseid everywhere
name|String
name|id
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"database-id"
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
name|this
operator|.
name|databaseid
operator|=
name|id
expr_stmt|;
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|databaseid
argument_list|)
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"database is not running"
argument_list|)
throw|;
name|boolean
name|enableDebug
init|=
literal|false
decl_stmt|;
name|String
name|param
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"debug"
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
condition|)
name|enableDebug
operator|=
name|param
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
try|try
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|databaseid
argument_list|)
decl_stmt|;
name|Configuration
name|conf
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|xmlrpc
operator|=
operator|new
name|XmlRpcServer
argument_list|()
expr_stmt|;
name|AuthenticatedHandler
name|rpcserv
init|=
operator|new
name|AuthenticatedHandler
argument_list|(
name|conf
argument_list|,
name|databaseid
argument_list|)
decl_stmt|;
comment|//RpcServer rpcserv = new RpcServer( conf );
name|xmlrpc
operator|.
name|addHandler
argument_list|(
literal|"$default"
argument_list|,
name|rpcserv
argument_list|)
expr_stmt|;
name|XmlRpc
operator|.
name|setDebug
argument_list|(
name|enableDebug
argument_list|)
expr_stmt|;
name|XmlRpc
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

