begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2011 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

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
name|util
operator|.
name|concurrent
operator|.
name|ExecutorService
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
name|net
operator|.
name|URISyntaxException
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
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Callable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ExecutionException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Executors
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|Future
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeoutException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|prefs
operator|.
name|Preferences
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
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|DefaultRestoreListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|GuiRestoreListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|RestoreListener
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|ClientFrame
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
name|DBA_PASS_OPT
init|=
literal|'P'
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
name|int
name|QUIET_OPT
init|=
literal|'q'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|CLOptionDescriptor
index|[]
name|OPTIONS
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
literal|"set the password for connecting to the database."
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"dba-password"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
argument_list|,
name|DBA_PASS_OPT
argument_list|,
literal|"if the backup specifies a different password for the admin/dba user, use this option "
operator|+
literal|"to specify the new password. Otherwise you will get a permission denied"
argument_list|)
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"backup"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_REQUIRED
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
name|ARGUMENT_REQUIRED
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
block|,
operator|new
name|CLOptionDescriptor
argument_list|(
literal|"quiet"
argument_list|,
name|CLOptionDescriptor
operator|.
name|ARGUMENT_DISALLOWED
argument_list|,
name|QUIET_OPT
argument_list|,
literal|"be quiet. Just print errors."
argument_list|)
block|}
decl_stmt|;
comment|/**      * Constructor for Main.      *      * @param  args  DOCUMENT ME!      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
specifier|static
name|void
name|process
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
comment|// read properties
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
specifier|final
name|File
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"backup.properties"
argument_list|)
decl_stmt|;
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
block|{
name|pin
operator|=
operator|new
name|FileInputStream
argument_list|(
name|propFile
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
if|if
condition|(
name|pin
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|properties
operator|.
name|load
argument_list|(
name|pin
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pin
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
block|}
specifier|final
name|Preferences
name|preferences
init|=
name|Preferences
operator|.
name|userNodeForPackage
argument_list|(
name|Main
operator|.
name|class
argument_list|)
decl_stmt|;
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
argument_list|<
name|CLOption
argument_list|>
name|opts
init|=
name|optParser
operator|.
name|getArguments
argument_list|()
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
name|String
name|optionDbaPass
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
name|boolean
name|quiet
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|CLOption
name|option
range|:
name|opts
control|)
block|{
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
block|{
name|printUsage
argument_list|()
expr_stmt|;
return|return;
block|}
case|case
name|GUI_OPT
case|:
block|{
name|guiMode
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|QUIET_OPT
case|:
block|{
name|quiet
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|OPTION_OPT
case|:
block|{
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
block|}
case|case
name|USER_OPT
case|:
block|{
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
block|}
case|case
name|PASS_OPT
case|:
block|{
name|properties
operator|.
name|setProperty
argument_list|(
literal|"password"
argument_list|,
name|option
operator|.
name|getArgument
argument_list|()
argument_list|)
expr_stmt|;
name|optionPass
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
comment|//remove after change inside restore
break|break;
block|}
case|case
name|DBA_PASS_OPT
case|:
block|{
name|optionDbaPass
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
break|break;
block|}
case|case
name|BACKUP_OPT
case|:
block|{
if|if
condition|(
name|option
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|optionBackup
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|optionBackup
operator|=
literal|null
expr_stmt|;
block|}
name|doBackup
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|RESTORE_OPT
case|:
block|{
if|if
condition|(
name|option
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|1
condition|)
block|{
name|optionRestore
operator|=
name|option
operator|.
name|getArgument
argument_list|()
expr_stmt|;
block|}
name|doRestore
operator|=
literal|true
expr_stmt|;
break|break;
block|}
case|case
name|BACKUP_DIR_OPT
case|:
block|{
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
block|}
comment|// initialize driver
name|Database
name|database
decl_stmt|;
try|try
block|{
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
block|{
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
block|}
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
name|properties
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|,
literal|""
argument_list|)
argument_list|,
operator|new
name|File
argument_list|(
name|preferences
operator|.
name|get
argument_list|(
literal|"directory.backup"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
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
name|getBackupTarget
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|optionBackup
operator|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION
expr_stmt|;
block|}
block|}
if|if
condition|(
name|optionBackup
operator|!=
literal|null
condition|)
block|{
try|try
block|{
specifier|final
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
name|properties
operator|.
name|getProperty
argument_list|(
literal|"password"
argument_list|,
literal|""
argument_list|)
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
name|XmldbURI
operator|.
name|xmldbUriFor
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
name|optionBackup
argument_list|)
argument_list|,
name|properties
argument_list|)
decl_stmt|;
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
specifier|final
name|Exception
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
if|if
condition|(
name|doRestore
condition|)
block|{
if|if
condition|(
operator|(
name|optionRestore
operator|==
literal|null
operator|)
operator|&&
name|guiMode
condition|)
block|{
specifier|final
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
specifier|final
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
specifier|final
name|String
name|username
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
decl_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|optionRestore
argument_list|)
decl_stmt|;
specifier|final
name|String
name|uri
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
literal|"xmldb:exist://"
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|guiMode
condition|)
block|{
name|restoreWithGui
argument_list|(
name|username
argument_list|,
name|optionPass
argument_list|,
name|optionDbaPass
argument_list|,
name|f
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|restoreWithoutGui
argument_list|(
name|username
argument_list|,
name|optionPass
argument_list|,
name|optionDbaPass
argument_list|,
name|f
argument_list|,
name|uri
argument_list|)
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
name|String
name|uri
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"uri"
argument_list|,
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI_PREFIX
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|uri
operator|.
name|contains
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
operator|||
name|uri
operator|.
name|endsWith
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
operator|)
condition|)
block|{
name|uri
operator|+=
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
name|properties
operator|.
name|getProperty
argument_list|(
literal|"user"
argument_list|,
literal|"admin"
argument_list|)
argument_list|,
operator|(
name|optionDbaPass
operator|==
literal|null
operator|)
condition|?
name|optionPass
else|:
name|optionDbaPass
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
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|reportError
argument_list|(
name|e
argument_list|)
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
specifier|static
name|void
name|restoreWithoutGui
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|String
name|dbaPassword
parameter_list|,
specifier|final
name|File
name|f
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
block|{
specifier|final
name|RestoreListener
name|listener
init|=
operator|new
name|DefaultRestoreListener
argument_list|()
decl_stmt|;
specifier|final
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|()
decl_stmt|;
try|try
block|{
name|restore
operator|.
name|restore
argument_list|(
name|listener
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|dbaPassword
argument_list|,
name|f
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|FileNotFoundException
name|fnfe
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|fnfe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|saxe
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|xmldbe
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|xmldbe
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ParserConfigurationException
name|pce
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|pce
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|use
parameter_list|)
block|{
name|listener
operator|.
name|error
argument_list|(
name|use
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|listener
operator|.
name|hasProblems
argument_list|()
condition|)
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
name|listener
operator|.
name|warningsAndErrorsAsString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|restoreWithGui
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|String
name|dbaPassword
parameter_list|,
specifier|final
name|File
name|f
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|)
block|{
specifier|final
name|GuiRestoreListener
name|listener
init|=
operator|new
name|GuiRestoreListener
argument_list|()
decl_stmt|;
specifier|final
name|Callable
argument_list|<
name|Void
argument_list|>
name|callable
init|=
operator|new
name|Callable
argument_list|<
name|Void
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Void
name|call
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Restore
name|restore
init|=
operator|new
name|Restore
argument_list|()
decl_stmt|;
try|try
block|{
name|restore
operator|.
name|restore
argument_list|(
name|listener
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|dbaPassword
argument_list|,
name|f
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|listener
operator|.
name|hideDialog
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
comment|//$NON-NLS-1$
block|}
finally|finally
block|{
if|if
condition|(
name|listener
operator|.
name|hasProblems
argument_list|()
condition|)
block|{
name|ClientFrame
operator|.
name|showErrorMessage
argument_list|(
name|listener
operator|.
name|warningsAndErrorsAsString
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|()
decl_stmt|;
specifier|final
name|Future
argument_list|<
name|Void
argument_list|>
name|future
init|=
name|executor
operator|.
name|submit
argument_list|(
name|callable
argument_list|)
decl_stmt|;
while|while
condition|(
operator|!
name|future
operator|.
name|isDone
argument_list|()
operator|&&
operator|!
name|future
operator|.
name|isCancelled
argument_list|()
condition|)
block|{
try|try
block|{
name|future
operator|.
name|get
argument_list|(
literal|100
argument_list|,
name|TimeUnit
operator|.
name|MILLISECONDS
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|ie
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
specifier|final
name|ExecutionException
name|ee
parameter_list|)
block|{
break|break;
block|}
catch|catch
parameter_list|(
specifier|final
name|TimeoutException
name|te
parameter_list|)
block|{
block|}
block|}
block|}
specifier|private
specifier|static
name|void
name|reportError
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
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
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
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
specifier|final
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
try|try
block|{
name|process
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|System
operator|.
name|exit
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

