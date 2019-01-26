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
name|io
operator|.
name|File
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|Optional
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
name|ExecutorService
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
name|NamedThreadFactory
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
name|SystemExitCodes
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
name|ConsoleRestoreListener
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

begin_import
import|import
name|se
operator|.
name|softhouse
operator|.
name|jargo
operator|.
name|*
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
name|getBool
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
name|*
import|;
end_import

begin_comment
comment|/**  * Main.java  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Main
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER_PROP
init|=
literal|"user"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PASSWORD_PROP
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|URI_PROP
init|=
literal|"uri"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CONFIGURATION_PROP
init|=
literal|"configuration"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DRIVER_PROP
init|=
literal|"driver"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|CREATE_DATABASE_PROP
init|=
literal|"create-database"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|BACKUP_DIR_PROP
init|=
literal|"backup-dir"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_USER
init|=
literal|"admin"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_PASSWORD
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_URI
init|=
literal|"xmldb:exist://"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DEFAULT_BACKUP_DIR
init|=
literal|"backup"
decl_stmt|;
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
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|Boolean
argument_list|>
name|guiArg
init|=
name|optionArgument
argument_list|(
literal|"-U"
argument_list|,
literal|"--gui"
argument_list|)
operator|.
name|description
argument_list|(
literal|"start in GUI mode"
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
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
name|Boolean
argument_list|>
name|quietArg
init|=
name|optionArgument
argument_list|(
literal|"-q"
argument_list|,
literal|"--quiet"
argument_list|)
operator|.
name|description
argument_list|(
literal|"be quiet. Just print errors."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
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
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|optionArg
init|=
name|stringArgument
argument_list|(
literal|"-o"
argument_list|,
literal|"--option"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify extra options: property=value. For available properties see client.properties."
argument_list|)
operator|.
name|asKeyValuesWithKeyParser
argument_list|(
name|StringParsers
operator|.
name|stringParser
argument_list|()
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* user/pass arguments */
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
literal|"set user."
argument_list|)
operator|.
name|defaultValue
argument_list|(
name|DEFAULT_USER
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
literal|"set the password for connecting to the database."
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
name|dbaPasswordArg
init|=
name|stringArgument
argument_list|(
literal|"-P"
argument_list|,
literal|"--dba-password"
argument_list|)
operator|.
name|description
argument_list|(
literal|"if the backup specifies a different password for the admin user, use this option to specify the new password. Otherwise you will get a permission denied"
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* backup arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|String
argument_list|>
name|backupCollectionArg
init|=
name|stringArgument
argument_list|(
literal|"-b"
argument_list|,
literal|"--backup"
argument_list|)
operator|.
name|description
argument_list|(
literal|"backup the specified collection."
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
name|File
argument_list|>
name|backupOutputDirArg
init|=
name|fileArgument
argument_list|(
literal|"-d"
argument_list|,
literal|"--dir"
argument_list|)
operator|.
name|description
argument_list|(
literal|"specify the directory to use for backups."
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
comment|/* restore arguments */
specifier|private
specifier|static
specifier|final
name|Argument
argument_list|<
name|File
argument_list|>
name|restoreArg
init|=
name|fileArgument
argument_list|(
literal|"-r"
argument_list|,
literal|"--restore"
argument_list|)
operator|.
name|description
argument_list|(
literal|"read the specified __contents__.xml file and restore the resources described there."
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
name|Boolean
argument_list|>
name|rebuildExpathRepoArg
init|=
name|optionArgument
argument_list|(
literal|"-R"
argument_list|,
literal|"--rebuild"
argument_list|)
operator|.
name|description
argument_list|(
literal|"rebuild the EXpath app repository after restore."
argument_list|)
operator|.
name|defaultValue
argument_list|(
literal|false
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|Properties
name|loadProperties
parameter_list|()
block|{
comment|// read properties
specifier|final
name|Path
name|propFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"backup.properties"
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
name|Main
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"backup.properties"
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
comment|/**      * Constructor for Main.      *      * @param arguments parsed command line arguments      */
specifier|public
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
specifier|final
name|boolean
name|guiMode
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|guiArg
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|quiet
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|quietArg
argument_list|)
decl_stmt|;
name|Optional
operator|.
name|ofNullable
argument_list|(
name|arguments
operator|.
name|get
argument_list|(
name|optionArg
argument_list|)
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|options
lambda|->
name|options
operator|.
name|forEach
argument_list|(
name|properties
operator|::
name|setProperty
argument_list|)
argument_list|)
expr_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|USER_PROP
argument_list|,
name|arguments
operator|.
name|get
argument_list|(
name|userArg
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|String
name|optionPass
init|=
name|arguments
operator|.
name|get
argument_list|(
name|passwordArg
argument_list|)
decl_stmt|;
name|properties
operator|.
name|setProperty
argument_list|(
name|PASSWORD_PROP
argument_list|,
name|optionPass
argument_list|)
expr_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|optionDbaPass
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|dbaPasswordArg
argument_list|)
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|backupCollection
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|backupCollectionArg
argument_list|)
decl_stmt|;
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|backupOutputDirArg
argument_list|)
operator|.
name|ifPresent
argument_list|(
name|backupOutputDir
lambda|->
name|properties
operator|.
name|setProperty
argument_list|(
name|BACKUP_DIR_PROP
argument_list|,
name|backupOutputDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|restorePath
init|=
name|getOpt
argument_list|(
name|arguments
argument_list|,
name|restoreArg
argument_list|)
operator|.
name|map
argument_list|(
name|File
operator|::
name|toPath
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|rebuildRepo
init|=
name|getBool
argument_list|(
name|arguments
argument_list|,
name|rebuildExpathRepoArg
argument_list|)
decl_stmt|;
comment|// initialize driver
specifier|final
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
name|DRIVER_PROP
argument_list|,
name|DEFAULT_DRIVER
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
name|CREATE_DATABASE_PROP
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
name|CONFIGURATION_PROP
argument_list|)
condition|)
block|{
name|database
operator|.
name|setProperty
argument_list|(
name|CONFIGURATION_PROP
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|CONFIGURATION_PROP
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
decl||
name|InstantiationException
decl||
name|XMLDBException
decl||
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
comment|// process
if|if
condition|(
name|backupCollection
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|String
name|collection
init|=
name|backupCollection
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|isEmpty
argument_list|()
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
name|URI_PROP
argument_list|,
name|DEFAULT_URI
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|USER_PROP
argument_list|,
name|DEFAULT_USER
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|PASSWORD_PROP
argument_list|,
name|DEFAULT_PASSWORD
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
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
name|collection
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
name|BACKUP_DIR_PROP
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
name|collection
operator|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|collection
operator|.
name|isEmpty
argument_list|()
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
name|USER_PROP
argument_list|,
name|DEFAULT_USER
argument_list|)
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|PASSWORD_PROP
argument_list|,
name|DEFAULT_PASSWORD
argument_list|)
argument_list|,
name|Paths
operator|.
name|get
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
name|BACKUP_DIR_PROP
argument_list|,
name|DEFAULT_BACKUP_DIR
argument_list|)
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
name|URI_PROP
argument_list|,
name|DEFAULT_URI
argument_list|)
operator|+
name|collection
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
name|restorePath
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|Path
name|path
init|=
name|restorePath
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
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
name|path
operator|=
name|chooser
operator|.
name|getSelectedFile
argument_list|()
operator|.
name|toPath
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|path
argument_list|)
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
name|USER_PROP
argument_list|,
name|DEFAULT_USER
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
name|URI_PROP
argument_list|,
name|DEFAULT_URI
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
name|path
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
name|path
argument_list|,
name|uri
argument_list|,
name|rebuildRepo
argument_list|,
name|quiet
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
name|URI_PROP
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
name|USER_PROP
argument_list|,
name|DEFAULT_USER
argument_list|)
argument_list|,
name|optionDbaPass
operator|.
name|orElse
argument_list|(
name|optionPass
argument_list|)
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
name|SystemExitCodes
operator|.
name|OK_EXIT_CODE
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
name|Optional
argument_list|<
name|String
argument_list|>
name|dbaPassword
parameter_list|,
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|boolean
name|rebuildRepo
parameter_list|,
specifier|final
name|boolean
name|quiet
parameter_list|)
block|{
specifier|final
name|RestoreListener
name|listener
init|=
operator|new
name|ConsoleRestoreListener
argument_list|(
name|quiet
argument_list|)
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
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
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
name|IOException
decl||
name|URISyntaxException
decl||
name|ParserConfigurationException
decl||
name|XMLDBException
decl||
name|SAXException
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
if|if
condition|(
name|rebuildRepo
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Rebuilding application repository ..."
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"URI: "
operator|+
name|uri
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|rootURI
init|=
name|uri
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|rootURI
operator|.
name|contains
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
operator|||
name|rootURI
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
name|rootURI
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
name|rootURI
argument_list|,
name|username
argument_list|,
name|dbaPassword
operator|.
name|orElse
argument_list|(
name|password
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|root
operator|!=
literal|null
condition|)
block|{
name|ClientFrame
operator|.
name|repairRepository
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Application repository rebuilt successfully."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Failed to retrieve root collection: "
operator|+
name|uri
argument_list|)
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
name|reportError
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Rebuilding application repository failed!"
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"\nIf you restored collections inside /db/apps, you may want\n"
operator|+
literal|"to rebuild the application repository. To do so, run the following query\n"
operator|+
literal|"as admin:\n\n"
operator|+
literal|"import module namespace repair=\"http://exist-db.org/xquery/repo/repair\"\n"
operator|+
literal|"at \"resource:org/exist/xquery/modules/expathrepo/repair.xql\";\n"
operator|+
literal|"repair:clean-all(),\n"
operator|+
literal|"repair:repair()\n"
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
name|Optional
argument_list|<
name|String
argument_list|>
name|dbaPassword
parameter_list|,
specifier|final
name|Path
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
parameter_list|()
lambda|->
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
operator|.
name|orElse
argument_list|(
literal|null
argument_list|)
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
if|if
condition|(
name|JOptionPane
operator|.
name|showConfirmDialog
argument_list|(
literal|null
argument_list|,
literal|"Would you like to rebuild the application repository?\nThis is only necessary if application packages were restored."
argument_list|,
literal|"Rebuild App Repository?"
argument_list|,
name|JOptionPane
operator|.
name|YES_NO_OPTION
argument_list|)
operator|==
name|JOptionPane
operator|.
name|YES_OPTION
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Rebuilding application repository ..."
argument_list|)
expr_stmt|;
try|try
block|{
name|String
name|rootURI
init|=
name|uri
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|rootURI
operator|.
name|contains
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
operator|||
name|rootURI
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
name|rootURI
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
name|rootURI
argument_list|,
name|username
argument_list|,
name|dbaPassword
operator|.
name|orElse
argument_list|(
name|password
argument_list|)
argument_list|)
decl_stmt|;
name|ClientFrame
operator|.
name|repairRepository
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Application repository rebuilt successfully."
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Rebuilding application repository failed!"
argument_list|)
expr_stmt|;
block|}
block|}
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
decl_stmt|;
specifier|final
name|ExecutorService
name|executor
init|=
name|Executors
operator|.
name|newSingleThreadExecutor
argument_list|(
operator|new
name|NamedThreadFactory
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|"backup.restore-with-gui"
argument_list|)
argument_list|)
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
decl||
name|TimeoutException
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
block|}
block|}
specifier|private
specifier|static
name|void
name|reportError
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
name|SystemExitCodes
operator|.
name|CATCH_ALL_GENERAL_ERROR_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|shutdown
parameter_list|(
specifier|final
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
name|dbaPasswordArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|backupCollectionArg
argument_list|,
name|backupOutputDirArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|restoreArg
argument_list|,
name|rebuildExpathRepoArg
argument_list|)
operator|.
name|andArguments
argument_list|(
name|helpArg
argument_list|,
name|guiArg
argument_list|,
name|quietArg
argument_list|,
name|optionArg
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
name|SystemExitCodes
operator|.
name|INVALID_ARGUMENT_EXIT_CODE
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
name|SystemExitCodes
operator|.
name|CATCH_ALL_GENERAL_ERROR_EXIT_CODE
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

