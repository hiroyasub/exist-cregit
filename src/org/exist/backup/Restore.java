begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2005-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|Properties
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Stack
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
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
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
name|SAXParserFactory
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
name|RestoreHandler
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
name|repo
operator|.
name|RepoBackup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Account
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|SecurityManager
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
name|EXistInputSource
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
name|UserManagementService
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
name|xml
operator|.
name|sax
operator|.
name|XMLReader
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
name|ErrorCodes
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
name|BinaryResource
import|;
end_import

begin_comment
comment|/**  * Restore.java.  *  * @author Adam Retter<adam@exist-db.org>  * @author  Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|Restore
block|{
comment|//    private final static Logger LOG = LogManager.getLogger(Restore.class);
specifier|public
name|void
name|restore
parameter_list|(
name|RestoreListener
name|listener
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|newAdminPass
parameter_list|,
name|File
name|f
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|FileNotFoundException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|ParserConfigurationException
throws|,
name|URISyntaxException
block|{
comment|//set the admin password
if|if
condition|(
name|newAdminPass
operator|!=
literal|null
condition|)
block|{
name|password
operator|=
name|setAdminCredentials
argument_list|(
name|uri
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|newAdminPass
argument_list|)
expr_stmt|;
block|}
comment|//get the backup descriptors, can be more than one if it was an incremental backup
specifier|final
name|Stack
argument_list|<
name|BackupDescriptor
argument_list|>
name|descriptors
init|=
name|getBackupDescriptors
argument_list|(
name|f
argument_list|)
decl_stmt|;
specifier|final
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
specifier|final
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
try|try
block|{
name|listener
operator|.
name|restoreStarting
argument_list|()
expr_stmt|;
while|while
condition|(
operator|!
name|descriptors
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
specifier|final
name|BackupDescriptor
name|descriptor
init|=
name|descriptors
operator|.
name|pop
argument_list|()
decl_stmt|;
specifier|final
name|EXistInputSource
name|is
init|=
name|descriptor
operator|.
name|getInputSource
argument_list|()
decl_stmt|;
name|is
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
specifier|final
name|RestoreHandler
name|handler
init|=
operator|new
name|RestoreHandler
argument_list|(
name|listener
argument_list|,
name|uri
argument_list|,
name|username
argument_list|,
name|password
argument_list|,
name|descriptor
argument_list|)
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|listener
operator|.
name|restoreFinished
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Stack
argument_list|<
name|BackupDescriptor
argument_list|>
name|getBackupDescriptors
parameter_list|(
name|File
name|contents
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
specifier|final
name|Stack
argument_list|<
name|BackupDescriptor
argument_list|>
name|descriptors
init|=
operator|new
name|Stack
argument_list|<
name|BackupDescriptor
argument_list|>
argument_list|()
decl_stmt|;
do|do
block|{
specifier|final
name|BackupDescriptor
name|bd
init|=
name|getBackupDescriptor
argument_list|(
name|contents
argument_list|)
decl_stmt|;
name|descriptors
operator|.
name|push
argument_list|(
name|bd
argument_list|)
expr_stmt|;
comment|// check if the system collection is in the backup. This should be processed first
comment|//TODO : find a way to make a corespondance with DBRoker's named constants
specifier|final
name|BackupDescriptor
name|sysDescriptor
init|=
name|bd
operator|.
name|getChildBackupDescriptor
argument_list|(
literal|"system"
argument_list|)
decl_stmt|;
comment|// check if the system/security collection is in the backup, this must be the first system collection processed
if|if
condition|(
name|sysDescriptor
operator|!=
literal|null
condition|)
block|{
name|descriptors
operator|.
name|push
argument_list|(
name|sysDescriptor
argument_list|)
expr_stmt|;
specifier|final
name|BackupDescriptor
name|secDescriptor
init|=
name|sysDescriptor
operator|.
name|getChildBackupDescriptor
argument_list|(
literal|"security"
argument_list|)
decl_stmt|;
if|if
condition|(
name|secDescriptor
operator|!=
literal|null
condition|)
block|{
name|descriptors
operator|.
name|push
argument_list|(
name|secDescriptor
argument_list|)
expr_stmt|;
block|}
block|}
name|contents
operator|=
literal|null
expr_stmt|;
specifier|final
name|Properties
name|properties
init|=
name|bd
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|properties
operator|!=
literal|null
operator|)
operator|&&
literal|"yes"
operator|.
name|equals
argument_list|(
name|properties
operator|.
name|getProperty
argument_list|(
literal|"incremental"
argument_list|,
literal|"no"
argument_list|)
argument_list|)
condition|)
block|{
specifier|final
name|String
name|previous
init|=
name|properties
operator|.
name|getProperty
argument_list|(
literal|"previous"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|contents
operator|=
operator|new
name|File
argument_list|(
name|bd
operator|.
name|getParentDir
argument_list|()
argument_list|,
name|previous
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|contents
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Required part of incremental backup not found: "
operator|+
name|contents
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
block|}
do|while
condition|(
name|contents
operator|!=
literal|null
condition|)
do|;
return|return
name|descriptors
return|;
block|}
specifier|private
name|BackupDescriptor
name|getBackupDescriptor
parameter_list|(
name|File
name|f
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|BackupDescriptor
name|bd
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
name|bd
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|f
argument_list|,
literal|"db"
argument_list|)
argument_list|,
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|f
operator|.
name|getName
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
name|bd
operator|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|bd
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|bd
return|;
block|}
specifier|private
name|String
name|setAdminCredentials
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|adminPassword
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|URISyntaxException
block|{
specifier|final
name|XmldbURI
name|dbUri
decl_stmt|;
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
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dbUri
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|uri
argument_list|)
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
name|dbUri
operator|.
name|toString
argument_list|()
argument_list|,
name|username
argument_list|,
name|password
argument_list|)
decl_stmt|;
specifier|final
name|UserManagementService
name|mgmt
init|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Account
name|dba
init|=
name|mgmt
operator|.
name|getAccount
argument_list|(
name|SecurityManager
operator|.
name|DBA_USER
argument_list|)
decl_stmt|;
if|if
condition|(
name|dba
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"'"
operator|+
name|SecurityManager
operator|.
name|DBA_USER
operator|+
literal|"' account can't be found."
argument_list|)
throw|;
block|}
name|dba
operator|.
name|setPassword
argument_list|(
name|adminPassword
argument_list|)
expr_stmt|;
name|mgmt
operator|.
name|updateAccount
argument_list|(
name|dba
argument_list|)
expr_stmt|;
return|return
name|adminPassword
return|;
block|}
block|}
end_class

end_unit

