begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * ServerShutdown.java - Jul 20, 2003  *   * @author wolf  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|jetty
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
name|ConfigurationHelper
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
name|DatabaseInstanceManager
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
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
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
name|Database
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
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|Argument
import|;
end_import

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|ArgumentException
import|;
end_import

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|CommandLineParser
import|;
end_import

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|ParsedArguments
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Files
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ArgumentUtil
operator|.
name|getOpt
import|;
end_import

begin_import
import|import static
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|Arguments
operator|.
name|helpArgument
import|;
end_import

begin_import
import|import static
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|Arguments
operator|.
name|stringArgument
import|;
end_import

begin_comment
comment|/**  * Call the main method of this class to shut down a running database instance.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ServerShutdown
block|{
comment|/* general arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|?
argument_list|>
name|helpArg
init|=
name|helpArgument
argument_list|(
literal|"-h"
argument_list|,
literal|"--help"
argument_list|)
decl_stmt|;
comment|/* database connection arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|userArg
init|=
name|stringArgument
argument_list|(
literal|"-u"
argument_list|,
literal|"--user"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify username (has to be a member of group dba)."
argument_list|)
operator|.
name|required
argument_list|()
operator|.
name|defaultValue
argument_list|(
literal|"admin"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|passwordArg
init|=
name|stringArgument
argument_list|(
literal|"-p"
argument_list|,
literal|"--password"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify password for the user."
argument_list|)
operator|.
name|required
argument_list|()
operator|.
name|defaultValue
argument_list|(
literal|""
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|uriArg
init|=
name|stringArgument
argument_list|(
literal|"-u"
argument_list|,
literal|"--uri"
argument_list|)
operator|.
name|description
argument_list|(
literal|"the XML:DB URI of the database instance to be shut down."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
name|void
name|main
parameter_list|(
specifier|final
name|String
index|[]
name|args
parameter_list|)
block|{
try|try
block|{
specifier|final
name|ParsedArguments
name|arguments
init|=
name|CommandLineParser
operator|.
name|withArguments
argument_list|(
name|userArg
argument_list|,
name|passwordArg
argument_list|,
name|uriArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|helpArg
argument_list|)
operator|.
name|parse
argument_list|(
name|args
argument_list|)
decl_stmt|;
name|process
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ArgumentException
name|e
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|e
operator|.
name|getMessageAndUsage
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|2
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|process
parameter_list|(
specifier|final
name|ParsedArguments
name|arguments
parameter_list|)
block|{
specifier|final
name|Properties
name|properties
init|=
name|loadProperties
argument_list|()
decl_stmt|;
specifier|final
name|String
name|user
init|=
name|arguments
operator|.
name|get
argument_list|(
name|userArg
argument_list|)
decl_stmt|;
specifier|final
name|String
name|passwd
init|=
name|arguments
operator|.
name|get
argument_list|(
name|passwordArg
argument_list|)
decl_stmt|;
name|String
name|uri
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|uriArg
argument_list|)
operator|.
name|orElseGet
argument_list|(
parameter_list|()
lambda|->
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://localhost:8080/exist/xmlrpc"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
comment|// initialize database drivers
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
comment|// create the default database
specifier|final
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|uri
operator|.
name|endsWith
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
expr_stmt|;
block|}
specifier|final
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|,
name|user
argument_list|,
name|passwd
argument_list|)
decl_stmt|;
specifier|final
name|DatabaseInstanceManager
name|manager
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shutting down database instance at "
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|'\t'
operator|+
name|uri
argument_list|)
expr_stmt|;
name|manager
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|!=
literal|null
operator|&&
name|t
operator|instanceof
name|XmlRpcException
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"CAUSE: "
operator|+
name|t
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Properties
name|loadProperties
parameter_list|()
block|{
specifier|final
name|Path
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"client.properties"
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|propFile
argument_list|)
condition|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|Files
operator|.
name|newInputStream
argument_list|(
name|propFile
argument_list|)
init|)
block|{
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
try|try
init|(
specifier|final
name|InputStream
name|pin
init|=
name|ServerShutdown
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"client.properties"
argument_list|)
init|)
block|{
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"WARN - Unable to load properties from: "
operator|+
name|propFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
block|}
block|}
end_class

end_unit

