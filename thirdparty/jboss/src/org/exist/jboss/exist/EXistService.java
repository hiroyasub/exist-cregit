begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|jboss
operator|.
name|exist
package|;
end_package

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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jboss
operator|.
name|system
operator|.
name|ServiceMBeanSupport
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
name|Category
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * This service handles the lifecycle of the eXist XML database  *  * @author Per Nyfelt  */
end_comment

begin_class
specifier|public
class|class
name|EXistService
extends|extends
name|ServiceMBeanSupport
implements|implements
name|EXistServiceMBean
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|EXistService
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|confFile
decl_stmt|;
specifier|protected
name|Configuration
name|configuration
decl_stmt|;
specifier|protected
name|String
name|eXistHome
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_CONFIG
init|=
literal|"<?xml version='1.0'?> "
operator|+
literal|"<exist> "
operator|+
literal|"<db-connection database='native' files='data' "
operator|+
literal|"buffers='512' words_buffers='8192' "
operator|+
literal|"elements_buffers='1024' free_mem_min='2000000' "
operator|+
literal|"grow='32' compress='false'/> "
operator|+
literal|"<indexer batchLoad='true' tmpDir='tmp' "
operator|+
literal|"stemming='false' controls='ctl' caseSensitive='false' "
operator|+
literal|"suppress-whitespace='both'> "
operator|+
literal|"<stopwords file='stopword'/> "
operator|+
literal|"</indexer> "
operator|+
literal|"</exist>"
decl_stmt|;
specifier|public
name|String
name|getEXistHome
parameter_list|()
block|{
return|return
name|eXistHome
return|;
block|}
specifier|public
name|void
name|setEXistHome
parameter_list|(
name|String
name|existHome
parameter_list|)
block|{
name|this
operator|.
name|eXistHome
operator|=
name|existHome
expr_stmt|;
block|}
specifier|protected
name|void
name|startService
parameter_list|()
throws|throws
name|Exception
block|{
comment|// get path to the deploy target directory
name|String
name|jbossServerDir
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"jboss.server.home.dir"
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"jbossServerDir is "
operator|+
name|jbossServerDir
operator|+
literal|", eXistHome is "
operator|+
name|eXistHome
argument_list|)
expr_stmt|;
name|File
name|eXistHomeDir
init|=
operator|new
name|File
argument_list|(
name|jbossServerDir
argument_list|,
name|eXistHome
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"eXistHomeDir set to "
operator|+
name|eXistHomeDir
argument_list|)
expr_stmt|;
name|eXistHome
operator|=
name|eXistHomeDir
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"eXistHome set to "
operator|+
name|eXistHome
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|eXistHomeDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"exist home directory not found at "
operator|+
name|eXistHome
operator|+
literal|", creating new directory"
argument_list|)
expr_stmt|;
name|eXistHomeDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.home"
argument_list|,
name|eXistHome
argument_list|)
expr_stmt|;
name|File
name|dataDir
init|=
operator|new
name|File
argument_list|(
name|eXistHomeDir
argument_list|,
literal|"data"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dataDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"creating data dir in eXist home"
argument_list|)
expr_stmt|;
name|dataDir
operator|.
name|mkdir
argument_list|()
expr_stmt|;
block|}
name|confFile
operator|=
operator|new
name|File
argument_list|(
name|eXistHome
argument_list|,
literal|"conf.xml"
argument_list|)
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"confFile set to "
operator|+
name|confFile
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|confFile
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Config file does not exist, creating default configuration..."
argument_list|)
expr_stmt|;
name|createDefaultConfigFile
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"configuration file "
operator|+
name|confFile
operator|+
literal|" is not readable"
argument_list|)
throw|;
block|}
name|configuration
operator|=
operator|new
name|Configuration
argument_list|(
name|confFile
argument_list|,
name|eXistHome
argument_list|)
expr_stmt|;
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|Exception
argument_list|(
literal|"Failed to create configuration for database"
argument_list|)
throw|;
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Configuring database"
argument_list|)
expr_stmt|;
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
block|}
block|}
specifier|private
name|void
name|createDefaultConfigFile
parameter_list|(
name|File
name|file
parameter_list|)
throws|throws
name|IOException
block|{
name|FileWriter
name|writer
init|=
operator|new
name|FileWriter
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|DEFAULT_CONFIG
argument_list|)
expr_stmt|;
name|writer
operator|.
name|flush
argument_list|()
expr_stmt|;
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|void
name|stopService
parameter_list|()
throws|throws
name|Exception
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
name|stop
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * This is for the JMX HTML adapter to display som status info about the server      * @return an HTML string containing the db stauts      */
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
name|String
name|output
init|=
literal|""
decl_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|()
condition|)
name|output
operator|+=
literal|"<p>Server is not running ...</p>"
expr_stmt|;
else|else
block|{
name|output
operator|+=
literal|"<p>The database server is running ...</p>"
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|Configuration
name|conf
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|output
operator|+=
literal|"<table  width=\"80%\"><tr><th colspan=\"2\" align=\"left\" bgcolor=\"#0086b2\"><b>Status</b></th></tr>"
expr_stmt|;
name|output
operator|+=
literal|"<tr><td>Configuration:</td><td>"
operator|+
name|conf
operator|.
name|getPath
argument_list|()
operator|+
literal|"</td></tr>"
expr_stmt|;
name|output
operator|+=
literal|"<tr><td>Data directory:</td><td>"
operator|+
operator|(
name|String
operator|)
name|conf
operator|.
name|getProperty
argument_list|(
literal|"db-connection.data-dir"
argument_list|)
operator|+
literal|"</td></tr>"
expr_stmt|;
name|output
operator|+=
literal|"<tr><td>Active instances:</td><td>"
operator|+
name|pool
operator|.
name|active
argument_list|()
operator|+
literal|"</td></tr>"
expr_stmt|;
name|output
operator|+=
literal|"<tr><td>Available instances:</td><td>"
operator|+
name|pool
operator|.
name|available
argument_list|()
operator|+
literal|"</td></tr>"
expr_stmt|;
name|output
operator|+=
literal|"</table>"
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|output
operator|+=
name|e
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|output
return|;
block|}
block|}
end_class

end_unit

