begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
package|;
end_package

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
name|FileInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JFileChooser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|JOptionPane
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|plaf
operator|.
name|FileChooserUI
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLArgsParser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLOptionDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|excalibur
operator|.
name|cli
operator|.
name|CLUtil
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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

begin_comment
comment|/**  * Main.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Main
block|{
specifier|private
specifier|final
specifier|static
name|int
name|HELP_OPT
init|=
literal|'h'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|USER_OPT
init|=
literal|'u'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|PASS_OPT
init|=
literal|'p'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|BACKUP_OPT
init|=
literal|'b'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|BACKUP_DIR_OPT
init|=
literal|'d'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|RESTORE_OPT
init|=
literal|'r'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|OPTION_OPT
init|=
literal|'o'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|GUI_OPT
init|=
literal|'U'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|CLOptionDescriptor
name|OPTIONS
index|[]
init|=
operator|new
name|CLOptionDescriptor
index|[]
block|{
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"help"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|HELP_OPT
argument_list|,
literal|"print help on command line options and exit."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"gui"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|GUI_OPT
argument_list|,
literal|"start in GUI mode"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"user"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|USER_OPT
argument_list|,
literal|"set user."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"password"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|PASS_OPT
argument_list|,
literal|"set password."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"backup"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_OPTIONAL
argument_list|,
name|BACKUP_OPT
argument_list|,
literal|"backup the specified collection."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"dir"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|BACKUP_DIR_OPT
argument_list|,
literal|"specify the directory to use for backups."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"restore"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_OPTIONAL
argument_list|,
name|RESTORE_OPT
argument_list|,
literal|"read the specified restore file and restore the "
operator|+
literal|"resources described there."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"option"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENTS_REQUIRED_2
operator||
name|CLOptionDescriptor
operator|.
name|DUPLICATES_ALLOWED
argument_list|,
name|OPTION_OPT
argument_list|,
literal|"specify extra options: property=value. For available properties see "
operator|+
literal|"client.properties."
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * Constructor for Main. 	 */
specifier|public
specifier|static
name|void
name|process
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
comment|// read properties
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|home
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|propFile
decl_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|propFile
operator|=
operator|new
name|File
argument_list|(
literal|"backup.properties"
argument_list|)
expr_stmt|;
else|else
name|propFile
operator|=
operator|new
name|File
argument_list|(
name|home
operator|+
name|System
operator|.
name|getProperty
argument_list|(
literal|"file.separator"
argument_list|,
literal|"/"
argument_list|)
operator|+
literal|"backup.properties"
argument_list|)
expr_stmt|;
name|InputStream
name|pin
decl_stmt|;
if|if
condition|(
name|propFile
operator|.
name|canRead
argument_list|()
condition|)
name|pin
operator|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
expr_stmt|;
else|else
name|pin
operator|=
name|Main
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"backup.properties"
argument_list|)
expr_stmt|;
if|if
condition|(
name|pin
operator|!=
literal|null
condition|)
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
block|}
comment|// parse command-line options
specifier|final
name|CLArgsParser
name|optParser
init|=
operator|new
name|CLArgsParser
argument_list|(
name|args
argument_list|,
name|OPTIONS
argument_list|)
decl_stmt|;
if|if
condition|(
name|optParser
operator|.
name|getErrorString
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"ERROR: "
operator|+
name|optParser
operator|.
name|getErrorString
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
specifier|final
name|List
name|opt
init|=
name|optParser
operator|.
name|getArguments
argument_list|()
decl_stmt|;
specifier|final
name|int
name|size
init|=
name|opt
operator|.
name|size
argument_list|()
decl_stmt|;
name|CLOption
name|option
decl_stmt|;
name|String
name|optionBackup
init|=
literal|null
decl_stmt|;
name|String
name|optionRestore
init|=
literal|null
decl_stmt|;
name|String
name|optionPass
init|=
literal|null
decl_stmt|;
name|boolean
name|doBackup
init|=
literal|false
decl_stmt|;
name|boolean
name|doRestore
init|=
literal|false
decl_stmt|;
name|boolean
name|guiMode
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
name|size
condition|;
name|i
operator|++
control|)
block|{
name|option
operator|=
operator|(
name|CLOption
operator|)
name|opt
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|option
operator|.
name|getId
argument_list|()
condition|)
block|{
case|case
name|HELP_OPT
case|:
name|printUsage
argument_list|()
expr_stmt|;
return|return;
case|case
name|GUI_OPT
case|:
name|guiMode
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|OPTION_OPT
case|:
name|properties
operator|.
name|setProperty
argument_list|(
name|option
operator|.
name|getArgument
argument_list|(
literal|0
argument_list|)
argument_list|,
name|option
operator|.
name|getArgument
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|USER_OPT
case|:
name|properties
operator|.
name|setProperty
argument_list|(
literal|"user"
argument_list|,
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|PASS_OPT
case|:
name|optionPass
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
break|break;
case|case
name|BACKUP_OPT
case|:
if|if
condition|(
name|option
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
name|optionBackup
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
else|else
name|optionBackup
operator|=
literal|null
expr_stmt|;
name|doBackup
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|RESTORE_OPT
case|:
if|if
condition|(
name|option
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
name|optionRestore
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
name|doRestore
operator|=
literal|true
expr_stmt|;
break|break;
case|case
name|BACKUP_DIR_OPT
case|:
name|properties
operator|.
name|setProperty
argument_list|(
literal|"backup-dir"
argument_list|,
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
comment|// initialize driver
name|Database
name|database
decl_stmt|;
try|try
block|{
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"driver"
argument_list|,
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
if|if
condition|(
name|properties
operator|.
name|containsKey
argument_list|(
literal|"configuration"
argument_list|)
condition|)
name|database
operator|.
name|setProperty
argument_list|(
literal|"configuration"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"configuration"
argument_list|)
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// process
if|if
condition|(
name|doBackup
condition|)
block|{
if|if
condition|(
name|optionBackup
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|guiMode
condition|)
block|{
name|CreateBackupDialog
name|dialog
init|=
operator|new
name|CreateBackupDialog
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://"
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|optionPass
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"backup-dir"
argument_list|,
literal|"backup"
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|JOptionPane
operator|.
name|showOptionDialog
argument_list|(
literal|null
argument_list|,
name|dialog
argument_list|,
literal|"Create Backup"
argument_list|,
name|JOptionPane
operator|.
name|OK_CANCEL_OPTION
argument_list|,
name|JOptionPane
operator|.
name|QUESTION_MESSAGE
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
operator|==
name|JOptionPane
operator|.
name|YES_OPTION
condition|)
block|{
name|optionBackup
operator|=
name|dialog
operator|.
name|getCollection
argument_list|()
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
literal|"backup-dir"
argument_list|,
name|dialog
operator|.
name|getBackupDir
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
name|optionBackup
operator|=
literal|"/db"
expr_stmt|;
block|}
if|if
condition|(
name|optionBackup
operator|!=
literal|null
condition|)
block|{
name|Backup
name|backup
init|=
operator|new
name|Backup
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|optionPass
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"backup-dir"
argument_list|,
literal|"backup"
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://"
argument_list|)
operator|+
name|optionBackup
argument_list|)
decl_stmt|;
try|try
block|{
name|backup
operator|.
name|backup
argument_list|(
name|guiMode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"caused by "
argument_list|)
expr_stmt|;
name|e
operator|.
name|getException
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|doRestore
condition|)
block|{
if|if
condition|(
name|optionRestore
operator|==
literal|null
operator|&&
name|guiMode
condition|)
block|{
name|JFileChooser
name|chooser
init|=
operator|new
name|JFileChooser
argument_list|()
decl_stmt|;
name|chooser
operator|.
name|setMultiSelectionEnabled
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|chooser
operator|.
name|setFileSelectionMode
argument_list|(
name|JFileChooser
operator|.
name|FILES_ONLY
argument_list|)
expr_stmt|;
if|if
condition|(
name|chooser
operator|.
name|showDialog
argument_list|(
literal|null
argument_list|,
literal|"Select backup file for restore"
argument_list|)
operator|==
name|JFileChooser
operator|.
name|APPROVE_OPTION
condition|)
block|{
name|File
name|f
init|=
name|chooser
operator|.
name|getSelectedFile
argument_list|()
decl_stmt|;
name|optionRestore
operator|=
name|f
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|optionRestore
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|optionPass
argument_list|,
operator|new
name|File
argument_list|(
name|optionRestore
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://"
argument_list|)
argument_list|)
decl_stmt|;
name|restore
operator|.
name|restore
argument_list|(
name|guiMode
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|FileNotFoundException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
try|try
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://"
argument_list|)
operator|+
literal|"/db"
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
name|optionPass
argument_list|)
decl_stmt|;
name|shutdown
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e1
parameter_list|)
block|{
name|e1
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|exit
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|reportError
parameter_list|(
name|Throwable
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
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
if|if
condition|(
name|e
operator|.
name|getCause
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"caused by "
argument_list|)
expr_stmt|;
name|e
operator|.
name|getCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|void
name|printUsage
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Usage: java "
operator|+
name|Main
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|" [options]"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|CLUtil
operator|.
name|describeOptions
argument_list|(
name|OPTIONS
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|final
specifier|static
name|void
name|shutdown
parameter_list|(
name|Collection
name|root
parameter_list|)
block|{
try|try
block|{
name|DatabaseInstanceManager
name|mgr
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
if|if
condition|(
name|mgr
operator|==
literal|null
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"service is not available"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|mgr
operator|.
name|isLocalInstance
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"shutting down database..."
argument_list|)
expr_stmt|;
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
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
literal|"database shutdown failed: "
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
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
name|process
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

