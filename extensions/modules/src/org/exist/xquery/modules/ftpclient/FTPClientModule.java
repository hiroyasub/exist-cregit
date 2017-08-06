begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ftpclient
package|;
end_package

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
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|net
operator|.
name|ftp
operator|.
name|FTPClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|AbstractInternalModule
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
name|FunctionDef
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
name|XQueryContext
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
name|modules
operator|.
name|ModuleUtils
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
name|modules
operator|.
name|ModuleUtils
operator|.
name|ContextMapEntryModifier
import|;
end_import

begin_comment
comment|/**  *  * @author WStarcev  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|FTPClientModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/ftpclient"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"ftpclient"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2011-03-24"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.2"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONNECTIONS_CONTEXTVAR
init|=
literal|"_eXist_ftp_connections"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|FTPClientModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|GetConnectionFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetDirListFunction
operator|.
name|signature
argument_list|,
name|GetDirListFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SendFileFunction
operator|.
name|signature
argument_list|,
name|SendFileFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetFileFunction
operator|.
name|signature
argument_list|,
name|GetFileFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FTPClientModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for performing FTP requests as a client"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
comment|/**      * Stores a Connection in the Context of an XQuery.      *      * @param   context  The Context of the XQuery to store the Connection in      * @param   ftp      The connection to store      *      * @return  A unique ID representing the connection      */
specifier|public
specifier|static
specifier|synchronized
name|long
name|storeConnection
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FTPClient
name|ftp
parameter_list|)
block|{
return|return
name|ModuleUtils
operator|.
name|storeObjectInContextMap
argument_list|(
name|context
argument_list|,
name|FTPClientModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
name|ftp
argument_list|)
return|;
block|}
comment|/**      * Retrieves a previously stored Connection from the Context of an XQuery.      *      * @param   context        The Context of the XQuery containing the Connection      * @param   connectionUID  The UID of the Connection to retrieve from the Context of the XQuery      *      * @return  DOCUMENT ME!      */
specifier|public
specifier|static
name|FTPClient
name|retrieveConnection
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|long
name|connectionUID
parameter_list|)
block|{
return|return
name|ModuleUtils
operator|.
name|retrieveObjectFromContextMap
argument_list|(
name|context
argument_list|,
name|FTPClientModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
name|connectionUID
argument_list|)
return|;
block|}
comment|/**      * Resets the Module Context and closes any FTP connections for the XQueryContext.      *      * @param  xqueryContext  The XQueryContext      */
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|,
name|boolean
name|keepGlobals
parameter_list|)
block|{
comment|// reset the module context
name|super
operator|.
name|reset
argument_list|(
name|xqueryContext
argument_list|,
name|keepGlobals
argument_list|)
expr_stmt|;
comment|// close any open Connections
name|closeAllConnections
argument_list|(
name|xqueryContext
argument_list|)
expr_stmt|;
block|}
comment|/**      * Closes all the open DB Connections for the specified XQueryContext.      *      * @param  xqueryContext  The context to close JDBC Connections for      */
specifier|private
specifier|static
name|void
name|closeAllConnections
parameter_list|(
name|XQueryContext
name|xqueryContext
parameter_list|)
block|{
name|ModuleUtils
operator|.
name|modifyContextMap
argument_list|(
name|xqueryContext
argument_list|,
name|FTPClientModule
operator|.
name|CONNECTIONS_CONTEXTVAR
argument_list|,
operator|new
name|ContextMapEntryModifier
argument_list|<
name|FTPClient
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Map
argument_list|<
name|Long
argument_list|,
name|FTPClient
argument_list|>
name|map
parameter_list|)
block|{
name|super
operator|.
name|modify
argument_list|(
name|map
argument_list|)
expr_stmt|;
comment|//empty the map
name|map
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|modify
parameter_list|(
name|Entry
argument_list|<
name|Long
argument_list|,
name|FTPClient
argument_list|>
name|entry
parameter_list|)
block|{
specifier|final
name|FTPClient
name|con
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
try|try
block|{
comment|// close the Connection
name|con
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|con
operator|.
name|isConnected
argument_list|()
condition|)
block|{
try|try
block|{
name|con
operator|.
name|disconnect
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
name|log
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
argument_list|)
expr_stmt|;
comment|// update the context
comment|//ModuleUtils.storeContextMap(xqueryContext, FTPClientModule.CONNECTIONS_CONTEXTVAR, connections);
block|}
block|}
end_class

end_unit

