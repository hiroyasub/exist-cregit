begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
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
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|HttpServletRequest
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
name|HttpServletResponse
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|DatabaseConfigurationException
import|;
end_import

begin_comment
comment|// created    17. Mai 2002
end_comment

begin_comment
comment|/**  *  Servlet to configure eXist. Use this servlet in a web   * application to launch the database at startup.  *   *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
annotation|@
name|Deprecated
specifier|public
class|class
name|DatabaseAdminServlet
extends|extends
name|AbstractExistHttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|866427121174932091L
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|DatabaseAdminServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|doGet
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
specifier|final
name|PrintStream
name|output
init|=
operator|new
name|PrintStream
argument_list|(
name|response
operator|.
name|getOutputStream
argument_list|()
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/html"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<h1>eXist Database Server Status</h1>"
argument_list|)
expr_stmt|;
specifier|final
name|String
name|action
init|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"action"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|action
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|action
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"start"
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
argument_list|)
condition|)
block|{
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<p>Server has been started...</p>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|println
argument_list|(
literal|"<p>Server is already running.</p>"
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|action
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"shutdown"
argument_list|)
condition|)
block|{
if|if
condition|(
name|BrokerPool
operator|.
name|isConfigured
argument_list|()
condition|)
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<p>Server has been shut down...</p>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|println
argument_list|(
literal|"<p>Server is not running ...</p>"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|()
condition|)
block|{
name|output
operator|.
name|println
argument_list|(
literal|"<p>Server is not running ...</p>"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|output
operator|.
name|println
argument_list|(
literal|"<p>The database server is running ...</p>"
argument_list|)
expr_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
specifier|final
name|Configuration
name|conf
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<table  width=\"80%\"><tr>"
operator|+
literal|"<th colspan=\"2\" align=\"left\" bgcolor=\"#0086b2\"><b>Status</b></th></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<tr><td>Address:</td><td>"
operator|+
name|request
operator|.
name|getRequestURI
argument_list|()
operator|+
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<tr><td>Configuration:</td><td>"
operator|+
name|conf
operator|.
name|getConfigFilePath
argument_list|()
operator|+
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<tr><td>Data directory:</td><td>"
operator|+
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|+
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<tr><td>Active instances:</td><td>"
operator|+
name|pool
operator|.
name|countActiveBrokers
argument_list|()
operator|+
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"<tr><td>Available instances:</td><td>"
operator|+
name|pool
operator|.
name|available
argument_list|()
operator|+
literal|"</td></tr>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"</table>"
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|print
argument_list|(
literal|"<p><form action=\""
argument_list|)
expr_stmt|;
name|output
operator|.
name|print
argument_list|(
name|response
operator|.
name|encodeURL
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"\" method=\"GET\">"
argument_list|)
expr_stmt|;
name|output
operator|.
name|print
argument_list|(
literal|"<input type=\"submit\" name=\"action\" value=\"start\">"
argument_list|)
expr_stmt|;
name|output
operator|.
name|print
argument_list|(
literal|"<input type=\"submit\" name=\"action\" value=\"shutdown\">"
argument_list|)
expr_stmt|;
name|output
operator|.
name|println
argument_list|(
literal|"</form></p>"
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
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
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
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
annotation|@
name|Override
specifier|public
name|Logger
name|getLog
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
block|}
end_class

end_unit

