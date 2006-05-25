begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|irc
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
name|text
operator|.
name|DateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|SimpleDateFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Calendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jibble
operator|.
name|pircbot
operator|.
name|IrcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jibble
operator|.
name|pircbot
operator|.
name|NickAlreadyInUseException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jibble
operator|.
name|pircbot
operator|.
name|PircBot
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|CollectionManagementService
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
name|XPathQueryService
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
name|XUpdateQueryService
import|;
end_import

begin_comment
comment|/**  * Implements a simple IRC drone, which logs IRC events to a collection in an eXist database.  * One log file is created every day. Messages are appended using XUpdate.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|XBot
extends|extends
name|PircBot
block|{
specifier|private
specifier|final
specifier|static
name|String
name|VERSION
init|=
literal|"0.1"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://localhost:8080/exist/xmlrpc/db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|COLLECTION
init|=
literal|"ircbot"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XUPDATE_START
init|=
literal|"<xu:modifications version=\"1.0\" xmlns:xu=\"http://www.xmldb.org/xupdate\">\n"
operator|+
literal|"<xu:variable name=\"now\" select=\"current-time()\"/>\n"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|URL_REGEX
init|=
literal|"((http|ftp)s{0,1}://[\\-\\.\\,/\\%\\~\\=\\@\\_\\&\\:\\?\\#a-zA-Z0-9]*[/\\=\\#a-zA-Z0-9])"
decl_stmt|;
comment|// these commands may be passed in a private message to the bot
specifier|private
specifier|final
name|Command
index|[]
name|commands
init|=
block|{
operator|new
name|HelpCommand
argument_list|()
block|,
operator|new
name|QuitCommand
argument_list|()
block|,
operator|new
name|FunctionLookup
argument_list|()
block|}
decl_stmt|;
comment|// the base collection
specifier|private
name|Collection
name|collection
decl_stmt|;
comment|// server and channel settings
specifier|private
name|String
name|channel
decl_stmt|;
specifier|private
name|String
name|password
decl_stmt|;
specifier|private
name|String
name|server
decl_stmt|;
specifier|private
name|DateFormat
name|dateFormat
init|=
operator|new
name|SimpleDateFormat
argument_list|(
literal|"yyyy-MM-dd"
argument_list|)
decl_stmt|;
specifier|private
name|Pattern
name|urlPattern
decl_stmt|;
specifier|private
name|Matcher
name|matcher
init|=
literal|null
decl_stmt|;
specifier|public
name|XBot
parameter_list|(
name|String
name|server
parameter_list|,
name|String
name|channel
parameter_list|,
name|String
name|nick
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|IrcException
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|server
operator|=
name|server
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|channel
expr_stmt|;
name|this
operator|.
name|password
operator|=
name|password
expr_stmt|;
name|this
operator|.
name|setName
argument_list|(
name|nick
argument_list|)
expr_stmt|;
name|this
operator|.
name|setVerbose
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|setupDb
argument_list|()
expr_stmt|;
name|urlPattern
operator|=
name|Pattern
operator|.
name|compile
argument_list|(
name|URL_REGEX
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Connect to the server. 	 *  	 * @throws IrcException 	 * @throws IOException 	 */
specifier|public
name|void
name|connect
parameter_list|()
throws|throws
name|IrcException
throws|,
name|IOException
block|{
name|log
argument_list|(
literal|"Connecting to "
operator|+
name|server
argument_list|)
expr_stmt|;
name|boolean
name|connected
init|=
literal|false
decl_stmt|;
while|while
condition|(
operator|!
name|connected
condition|)
block|{
try|try
block|{
name|connect
argument_list|(
name|server
argument_list|)
expr_stmt|;
name|connected
operator|=
literal|true
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NickAlreadyInUseException
name|e
parameter_list|)
block|{
name|this
operator|.
name|setName
argument_list|(
name|this
operator|.
name|getName
argument_list|()
operator|+
literal|'_'
argument_list|)
expr_stmt|;
block|}
block|}
name|log
argument_list|(
literal|"Join channel: "
operator|+
name|channel
argument_list|)
expr_stmt|;
name|joinChannel
argument_list|(
name|channel
argument_list|)
expr_stmt|;
name|sendMessage
argument_list|(
literal|"NickServ"
argument_list|,
literal|"IDENTIFY "
operator|+
name|password
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Callback method called after a user has joined the channel. 	 */
specifier|protected
name|void
name|onJoin
parameter_list|(
name|String
name|channel
parameter_list|,
name|String
name|sender
parameter_list|,
name|String
name|login
parameter_list|,
name|String
name|hostname
parameter_list|)
block|{
try|try
block|{
name|String
name|xupdate
init|=
literal|"<join nick=\""
operator|+
name|sender
operator|+
literal|"\" login=\""
operator|+
name|login
operator|+
literal|"\" host=\""
operator|+
name|hostname
operator|+
literal|"\">"
operator|+
literal|"<xu:attribute name=\"time\"><xu:value-of select=\"$now\"/></xu:attribute>"
operator|+
literal|"</join>"
decl_stmt|;
name|doUpdate
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Callback method: a user has parted. 	 */
specifier|protected
name|void
name|onPart
parameter_list|(
name|String
name|channel
parameter_list|,
name|String
name|sender
parameter_list|,
name|String
name|login
parameter_list|,
name|String
name|hostname
parameter_list|)
block|{
try|try
block|{
name|String
name|xupdate
init|=
literal|"<part nick=\""
operator|+
name|sender
operator|+
literal|"\" login=\""
operator|+
name|login
operator|+
literal|"\" host=\""
operator|+
name|hostname
operator|+
literal|"\">"
operator|+
literal|"<xu:attribute name=\"time\"><xu:value-of select=\"$now\"/></xu:attribute>"
operator|+
literal|"</part>"
decl_stmt|;
name|doUpdate
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Callback method: a user disconnected from the server. 	 */
specifier|protected
name|void
name|onQuit
parameter_list|(
name|String
name|sourceNick
parameter_list|,
name|String
name|sourceLogin
parameter_list|,
name|String
name|sourceHostname
parameter_list|,
name|String
name|reason
parameter_list|)
block|{
try|try
block|{
name|String
name|xupdate
init|=
literal|"<part nick=\""
operator|+
name|sourceNick
operator|+
literal|"\" login=\""
operator|+
name|sourceLogin
operator|+
literal|"\" host=\""
operator|+
name|sourceHostname
operator|+
literal|"\" reason=\""
operator|+
name|reason
operator|+
literal|"\">"
operator|+
literal|"<xu:attribute name=\"time\"><xu:value-of select=\"$now\"/></xu:attribute>"
operator|+
literal|"</part>"
decl_stmt|;
name|doUpdate
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Callback method: a message was sent. 	 */
specifier|protected
name|void
name|onMessage
parameter_list|(
name|String
name|channel
parameter_list|,
name|String
name|sender
parameter_list|,
name|String
name|login
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|message
parameter_list|)
block|{
try|try
block|{
name|String
name|xupdate
init|=
literal|"<message nick=\""
operator|+
name|sender
operator|+
literal|"\">"
operator|+
literal|"<xu:attribute name=\"time\"><xu:value-of select=\"$now\"/></xu:attribute>"
operator|+
literal|"<![CDATA["
operator|+
name|preprocessMessage
argument_list|(
name|message
argument_list|)
operator|+
literal|"]]>"
operator|+
literal|"</message>\n"
decl_stmt|;
name|doUpdate
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Callback method: a private message has been sent to the bot. Check if it contains a known 	 * command and execute it. 	 */
specifier|protected
name|void
name|onPrivateMessage
parameter_list|(
name|String
name|sender
parameter_list|,
name|String
name|login
parameter_list|,
name|String
name|hostname
parameter_list|,
name|String
name|message
parameter_list|)
block|{
name|String
name|args
index|[]
init|=
name|message
operator|.
name|split
argument_list|(
literal|"\\s+"
argument_list|)
decl_stmt|;
name|log
argument_list|(
literal|"Arguments: "
operator|+
name|args
operator|.
name|length
operator|+
literal|"; command: "
operator|+
name|args
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
name|boolean
name|recognized
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|commands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|equalsIgnoreCase
argument_list|(
name|commands
index|[
name|i
index|]
operator|.
name|name
argument_list|)
condition|)
block|{
comment|// executing command
try|try
block|{
name|commands
index|[
name|i
index|]
operator|.
name|execute
argument_list|(
name|sender
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IrcException
name|e
parameter_list|)
block|{
name|log
argument_list|(
literal|"An exception occurred while executing command '"
operator|+
name|message
operator|+
literal|"': "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|recognized
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|recognized
condition|)
block|{
name|sendMessage
argument_list|(
name|sender
argument_list|,
literal|"Don't know what to respond. Send me a message 'HELP' to see a list of "
operator|+
literal|"commands I understand."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Helper method to xupdate the log file. 	 *  	 * @param content 	 * @throws XMLDBException 	 */
specifier|private
name|void
name|doUpdate
parameter_list|(
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|xupdate
init|=
name|XUPDATE_START
operator|+
literal|"<xu:append select=\"doc('"
operator|+
name|getLogPath
argument_list|()
operator|+
literal|"')/xlog\">\n"
operator|+
name|content
operator|+
literal|"</xu:append>\n"
operator|+
literal|"</xu:modifications>"
decl_stmt|;
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|update
argument_list|(
name|xupdate
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Parse a message. Tries to detect URLs in the message and transforms them 	 * into an HTML link. 	 *  	 * @param message 	 * @return 	 */
specifier|private
name|String
name|preprocessMessage
parameter_list|(
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
name|matcher
operator|==
literal|null
condition|)
name|matcher
operator|=
name|urlPattern
operator|.
name|matcher
argument_list|(
name|message
argument_list|)
expr_stmt|;
else|else
name|matcher
operator|.
name|reset
argument_list|(
name|message
argument_list|)
expr_stmt|;
return|return
name|matcher
operator|.
name|replaceAll
argument_list|(
literal|"<a href=\"$1\">$1</a>"
argument_list|)
return|;
block|}
comment|/** 	 * Initialize the database. 	 *  	 * @throws IrcException 	 */
specifier|private
name|void
name|setupDb
parameter_list|()
throws|throws
name|IrcException
block|{
try|try
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
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
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/ircbot"
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
expr_stmt|;
if|if
condition|(
name|collection
operator|==
literal|null
condition|)
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"guest"
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|mgr
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|collection
operator|=
name|mgr
operator|.
name|createCollection
argument_list|(
name|COLLECTION
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IrcException
argument_list|(
literal|"Failed to initialize the database: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Returns the full database path to the current log document. 	 *  	 * @return 	 * @throws XMLDBException 	 */
specifier|private
name|String
name|getLogPath
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"/db/"
operator|+
name|COLLECTION
operator|+
literal|'/'
operator|+
name|getCurrentLog
argument_list|()
return|;
block|}
comment|/** 	 * Returns the name of the current log document. If no document 	 * has been created for today yet, create a new, empty one. 	 *  	 * @return 	 * @throws XMLDBException 	 */
specifier|private
name|String
name|getCurrentLog
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Calendar
name|cal
init|=
name|Calendar
operator|.
name|getInstance
argument_list|(
name|TimeZone
operator|.
name|getTimeZone
argument_list|(
literal|"GMT"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|date
init|=
name|dateFormat
operator|.
name|format
argument_list|(
name|cal
operator|.
name|getTime
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|resourceName
init|=
name|date
operator|+
literal|".xlog"
decl_stmt|;
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|getResource
argument_list|(
name|resourceName
argument_list|)
decl_stmt|;
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
comment|// create a new log for today's date
name|String
name|xml
init|=
literal|"<xlog server=\""
operator|+
name|server
operator|+
literal|"\" channel=\""
operator|+
name|channel
operator|+
literal|"\" date=\""
operator|+
name|date
operator|+
literal|"\"/>"
decl_stmt|;
name|res
operator|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|resourceName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|xml
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
return|return
name|resourceName
return|;
block|}
comment|/** 	 * Base class for all commands that can be send in a private message. 	 *  	 * @author wolf 	 * 	 */
specifier|private
specifier|abstract
class|class
name|Command
block|{
name|String
name|name
decl_stmt|;
name|String
name|description
decl_stmt|;
specifier|public
name|Command
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|description
operator|=
name|description
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|execute
parameter_list|(
name|String
name|target
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IrcException
function_decl|;
block|}
specifier|private
class|class
name|HelpCommand
extends|extends
name|Command
block|{
specifier|public
name|HelpCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"help"
argument_list|,
literal|"List available commands."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|String
name|target
parameter_list|,
name|String
name|args
index|[]
parameter_list|)
throws|throws
name|IrcException
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"XBot "
operator|+
name|VERSION
operator|+
literal|" - Available commands:"
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|commands
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
name|commands
index|[
name|i
index|]
operator|.
name|name
operator|+
literal|"\t\t"
operator|+
name|commands
index|[
name|i
index|]
operator|.
name|description
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|QuitCommand
extends|extends
name|Command
block|{
specifier|public
name|QuitCommand
parameter_list|()
block|{
name|super
argument_list|(
literal|"quit"
argument_list|,
literal|"Disconnect from the server."
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|String
name|target
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IrcException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"Usage: QUIT password"
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"Wrong password specified!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|quitServer
argument_list|(
literal|"Even a bot needs to rest sometimes..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
class|class
name|FunctionLookup
extends|extends
name|Command
block|{
specifier|public
name|FunctionLookup
parameter_list|()
block|{
name|super
argument_list|(
literal|"lookup"
argument_list|,
literal|"Lookup a function definition"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|execute
parameter_list|(
name|String
name|target
parameter_list|,
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|IrcException
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"util:describe-function('"
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|"')"
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getSize
argument_list|()
operator|==
literal|0
condition|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"Function "
operator|+
name|args
index|[
literal|1
index|]
operator|+
literal|" is unknown!"
argument_list|)
expr_stmt|;
return|return;
block|}
name|Node
name|node
init|=
operator|(
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
name|node
operator|=
operator|(
operator|(
name|Document
operator|)
name|node
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
name|NodeList
name|children
init|=
operator|(
operator|(
name|Element
operator|)
name|node
operator|)
operator|.
name|getElementsByTagName
argument_list|(
literal|"prototype"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|NodeList
name|nl
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|nl
operator|.
name|getLength
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|node
operator|=
name|nl
operator|.
name|item
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
if|if
condition|(
literal|"signature"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"[signature] "
operator|+
name|getNodeValue
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"description"
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"[description] "
operator|+
name|getNodeValue
argument_list|(
name|node
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|sendMessage
argument_list|(
name|target
argument_list|,
literal|"An exception occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getNodeValue
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/** 	 * @param args 	 * @throws IrcException  	 */
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|Exception
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|<
literal|2
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Usage: org.exist.irc.XBot nickname password"
argument_list|)
expr_stmt|;
block|}
name|XBot
name|bot
init|=
operator|new
name|XBot
argument_list|(
literal|"irc.freenode.net"
argument_list|,
literal|"#existdb"
argument_list|,
name|args
index|[
literal|0
index|]
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
decl_stmt|;
name|bot
operator|.
name|connect
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

