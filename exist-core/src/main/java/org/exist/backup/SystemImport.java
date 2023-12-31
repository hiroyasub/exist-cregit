begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|Database
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
name|SystemImportHandler
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
name|config
operator|.
name|ConfigurationException
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
name|AuthenticationException
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
name|PermissionDeniedException
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
name|Subject
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
name|DBBroker
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
name|txn
operator|.
name|TransactionException
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
name|txn
operator|.
name|Txn
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
name|util
operator|.
name|FileUtils
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
name|XMLReaderPool
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|ArrayDeque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
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

begin_comment
comment|/**  * Restore   *  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|SystemImport
block|{
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|SystemImport
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Database
name|db
decl_stmt|;
specifier|public
name|SystemImport
parameter_list|(
specifier|final
name|Database
name|db
parameter_list|)
block|{
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
block|}
specifier|public
name|void
name|restore
parameter_list|(
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|Object
name|credentials
parameter_list|,
annotation|@
name|Nullable
specifier|final
name|String
name|newCredentials
parameter_list|,
specifier|final
name|Path
name|f
parameter_list|,
specifier|final
name|RestoreListener
name|listener
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|AuthenticationException
throws|,
name|ConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|TransactionException
block|{
comment|//login
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|db
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|credentials
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|broker
operator|.
name|continueOrBeginTransaction
argument_list|()
init|)
block|{
comment|//set the new password
if|if
condition|(
name|newCredentials
operator|!=
literal|null
condition|)
block|{
name|setAdminCredentials
argument_list|(
name|broker
argument_list|,
name|newCredentials
argument_list|)
expr_stmt|;
block|}
comment|//get the backup descriptors, can be more than one if it was an incremental backup
specifier|final
name|Deque
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
name|XMLReaderPool
name|parserPool
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|parserPool
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
name|listener
operator|.
name|started
argument_list|(
literal|0
argument_list|)
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
name|SystemImportHandler
name|handler
init|=
operator|new
name|SystemImportHandler
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|descriptor
argument_list|,
name|listener
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
name|finished
argument_list|()
expr_stmt|;
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|parserPool
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|Deque
argument_list|<
name|BackupDescriptor
argument_list|>
name|getBackupDescriptors
parameter_list|(
name|Path
name|contents
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Deque
argument_list|<
name|BackupDescriptor
argument_list|>
name|descriptors
init|=
operator|new
name|ArrayDeque
argument_list|<>
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
specifier|final
name|BackupDescriptor
name|sysDescriptor
init|=
name|bd
operator|.
name|getChildBackupDescriptor
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_NAME
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
operator|!
name|previous
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|contents
operator|=
name|bd
operator|.
name|getParentDir
argument_list|()
operator|.
name|resolve
argument_list|(
name|previous
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|contents
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Required part of incremental backup not found: "
operator|+
name|contents
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
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
specifier|final
name|Path
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
name|Files
operator|.
name|isDirectory
argument_list|(
name|f
argument_list|)
condition|)
block|{
name|bd
operator|=
operator|new
name|FileSystemBackupDescriptor
argument_list|(
name|f
argument_list|,
name|f
operator|.
name|resolve
argument_list|(
literal|"db"
argument_list|)
operator|.
name|resolve
argument_list|(
name|BackupDescriptor
operator|.
name|COLLECTION_DESCRIPTOR
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|FileUtils
operator|.
name|fileName
argument_list|(
name|f
argument_list|)
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
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
return|return
name|bd
return|;
block|}
specifier|private
name|void
name|setAdminCredentials
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|String
name|newCredentials
parameter_list|)
throws|throws
name|ConfigurationException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|Subject
name|subject
init|=
name|broker
operator|.
name|getCurrentSubject
argument_list|()
decl_stmt|;
name|subject
operator|.
name|setPassword
argument_list|(
name|newCredentials
argument_list|)
expr_stmt|;
name|subject
operator|.
name|save
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

