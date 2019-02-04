begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|ZipArchiveBackupDescriptor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|ResponseWrapper
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
name|FileUtils
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
name|*
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
name|value
operator|.
name|FunctionParameterSequenceType
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
name|value
operator|.
name|Sequence
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
name|value
operator|.
name|SequenceType
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
name|value
operator|.
name|Type
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
name|OutputStream
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
name|Optional
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

begin_class
specifier|public
class|class
name|RetrieveBackup
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"retrieve"
argument_list|,
name|BackupModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|BackupModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Retrieves a zipped backup archive, $name, and directly streams it to the HTTP response. "
operator|+
literal|"For security reasons, the function will only read .zip files in the specified directory, $directory."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"directory"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The path to the directory where the backup file is located."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The name of the file to retrieve."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|RetrieveBackup
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
name|context
operator|.
name|getEffectiveUser
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"You must be a DBA to retrieve a backup"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|exportDir
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Path
name|dir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|exportDir
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|dir
operator|.
name|isAbsolute
argument_list|()
condition|)
block|{
name|dir
operator|=
operator|(
operator|(
name|Path
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|BrokerPool
operator|.
name|PROPERTY_DATA_DIR
argument_list|)
operator|)
operator|.
name|resolve
argument_list|(
name|exportDir
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|name
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|backupFile
init|=
name|dir
operator|.
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Files
operator|.
name|isReadable
argument_list|(
name|backupFile
argument_list|)
condition|)
block|{
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
if|if
condition|(
operator|!
name|name
operator|.
name|endsWith
argument_list|(
literal|".zip"
argument_list|)
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"for security reasons, the function only allows "
operator|+
literal|"reading zipped backup archives"
argument_list|)
operator|)
throw|;
block|}
try|try
block|{
specifier|final
name|ZipArchiveBackupDescriptor
name|descriptor
init|=
operator|new
name|ZipArchiveBackupDescriptor
argument_list|(
name|backupFile
argument_list|)
decl_stmt|;
specifier|final
name|Properties
name|properties
init|=
name|descriptor
operator|.
name|getProperties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
name|properties
operator|==
literal|null
operator|)
operator|||
operator|(
name|properties
operator|.
name|isEmpty
argument_list|()
operator|)
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"the file does not see to be a valid backup archive"
argument_list|)
operator|)
throw|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"the file does not see to be a valid backup archive"
argument_list|)
operator|)
throw|;
block|}
comment|// directly stream the backup contents to the HTTP response
specifier|final
name|Optional
argument_list|<
name|ResponseWrapper
argument_list|>
name|maybeResponse
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|context
operator|.
name|getHttpContext
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|XQueryContext
operator|.
name|HttpContext
operator|::
name|getResponse
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|maybeResponse
operator|.
name|isPresent
argument_list|()
condition|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"No response object found in the current XQuery context."
argument_list|)
operator|)
throw|;
block|}
specifier|final
name|ResponseWrapper
name|response
init|=
name|maybeResponse
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
literal|"org.exist.http.servlets.HttpResponseWrapper"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|signature
operator|.
name|toString
argument_list|()
operator|+
literal|" can only be used within the EXistServlet or XQueryServlet"
argument_list|)
throw|;
block|}
name|response
operator|.
name|setContentType
argument_list|(
literal|"application/zip"
argument_list|)
expr_stmt|;
name|response
operator|.
name|setHeader
argument_list|(
literal|"Content-Length"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|FileUtils
operator|.
name|sizeQuietly
argument_list|(
name|backupFile
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
try|try
init|(
specifier|final
name|OutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
init|)
block|{
name|Files
operator|.
name|copy
argument_list|(
name|backupFile
argument_list|,
name|os
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|flushBuffer
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|(
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An IO error occurred while reading the backup archive"
argument_list|)
operator|)
throw|;
block|}
return|return
operator|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
operator|)
return|;
block|}
block|}
end_class

end_unit

