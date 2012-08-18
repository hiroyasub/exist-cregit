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
name|replication
operator|.
name|jms
operator|.
name|obsolete
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
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
name|FileOutputStream
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
name|util
operator|.
name|Enumeration
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|GZIPInputStream
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jms
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|FileUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|IOUtils
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
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|replication
operator|.
name|shared
operator|.
name|eXistMessage
import|;
end_import

begin_comment
comment|/**  * Listener for actual handling of JMS message.  *  * @author Dannes Wessels  *  */
end_comment

begin_class
specifier|public
class|class
name|FileSystemListener
implements|implements
name|MessageListener
block|{
specifier|private
specifier|static
name|File
name|baseDir
decl_stmt|;
specifier|private
name|eXistMessage
name|convertMessage
parameter_list|(
name|BytesMessage
name|bm
parameter_list|)
block|{
name|eXistMessage
name|em
init|=
operator|new
name|eXistMessage
argument_list|()
decl_stmt|;
try|try
block|{
name|String
name|value
init|=
name|bm
operator|.
name|getStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_RESOURCE_TYPE
argument_list|)
decl_stmt|;
name|eXistMessage
operator|.
name|ResourceType
name|resourceType
init|=
name|eXistMessage
operator|.
name|ResourceType
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|em
operator|.
name|setResourceType
argument_list|(
name|resourceType
argument_list|)
expr_stmt|;
name|value
operator|=
name|bm
operator|.
name|getStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_RESOURCE_OPERATION
argument_list|)
expr_stmt|;
name|eXistMessage
operator|.
name|ResourceOperation
name|changeType
init|=
name|eXistMessage
operator|.
name|ResourceOperation
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
decl_stmt|;
name|em
operator|.
name|setResourceOperation
argument_list|(
name|changeType
argument_list|)
expr_stmt|;
name|value
operator|=
name|bm
operator|.
name|getStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_SOURCE_PATH
argument_list|)
expr_stmt|;
name|em
operator|.
name|setResourcePath
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|value
operator|=
name|bm
operator|.
name|getStringProperty
argument_list|(
name|eXistMessage
operator|.
name|EXIST_DESTINATION_PATH
argument_list|)
expr_stmt|;
name|em
operator|.
name|setDestinationPath
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|long
name|size
init|=
name|bm
operator|.
name|getBodyLength
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"length="
operator|+
name|size
argument_list|)
expr_stmt|;
comment|// This is potentially memory intensive
name|byte
index|[]
name|payload
init|=
operator|new
name|byte
index|[
operator|(
name|int
operator|)
name|size
index|]
decl_stmt|;
name|bm
operator|.
name|readBytes
argument_list|(
name|payload
argument_list|)
expr_stmt|;
name|em
operator|.
name|setPayload
argument_list|(
name|payload
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
name|em
return|;
block|}
specifier|public
name|FileSystemListener
parameter_list|()
block|{
name|baseDir
operator|=
operator|new
name|File
argument_list|(
literal|"clusteringTest"
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|baseDir
operator|.
name|exists
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Creating "
operator|+
name|baseDir
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|baseDir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|FileSystemListener
operator|.
name|class
argument_list|)
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|onMessage
parameter_list|(
name|Message
name|message
parameter_list|)
block|{
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"JMSMessageID="
operator|+
name|message
operator|.
name|getJMSMessageID
argument_list|()
argument_list|)
expr_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
comment|// Write properties
name|Enumeration
name|names
init|=
name|message
operator|.
name|getPropertyNames
argument_list|()
decl_stmt|;
for|for
control|(
name|Enumeration
argument_list|<
name|?
argument_list|>
name|e
init|=
name|names
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'"
operator|+
name|key
operator|+
literal|"='"
operator|+
name|message
operator|.
name|getStringProperty
argument_list|(
name|key
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
name|sb
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Handle message
if|if
condition|(
name|message
operator|instanceof
name|TextMessage
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
operator|(
operator|(
name|TextMessage
operator|)
name|message
operator|)
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|message
operator|instanceof
name|BytesMessage
condition|)
block|{
name|BytesMessage
name|bm
init|=
operator|(
name|BytesMessage
operator|)
name|message
decl_stmt|;
name|eXistMessage
name|em
init|=
name|convertMessage
argument_list|(
name|bm
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|em
operator|.
name|getResourceType
argument_list|()
condition|)
block|{
case|case
name|DOCUMENT
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"document"
argument_list|)
expr_stmt|;
name|handleDocument
argument_list|(
name|em
argument_list|)
expr_stmt|;
break|break;
case|case
name|COLLECTION
case|:
name|LOG
operator|.
name|info
argument_list|(
literal|"collection"
argument_list|)
expr_stmt|;
name|handleCollection
argument_list|(
name|em
argument_list|)
expr_stmt|;
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown resource type"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|JMSException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|handleDocument
parameter_list|(
name|eXistMessage
name|em
parameter_list|)
block|{
comment|// Get original path
name|String
name|resourcePath
init|=
name|em
operator|.
name|getResourcePath
argument_list|()
decl_stmt|;
name|String
index|[]
name|srcSplitPath
init|=
name|splitPath
argument_list|(
name|resourcePath
argument_list|)
decl_stmt|;
name|String
name|srcDir
init|=
name|srcSplitPath
index|[
literal|0
index|]
decl_stmt|;
name|String
name|srcDoc
init|=
name|srcSplitPath
index|[
literal|1
index|]
decl_stmt|;
name|File
name|dir
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|srcDir
argument_list|)
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|dir
argument_list|,
name|srcDoc
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|em
operator|.
name|getResourceOperation
argument_list|()
condition|)
block|{
case|case
name|CREATE_UPDATE
case|:
comment|// Create dirs if not existent
name|dir
operator|.
name|mkdirs
argument_list|()
expr_stmt|;
comment|// Create file reference
name|LOG
operator|.
name|info
argument_list|(
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
comment|// Prepare streams
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|em
operator|.
name|getPayload
argument_list|()
argument_list|)
decl_stmt|;
name|GZIPInputStream
name|gis
init|=
operator|new
name|GZIPInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
comment|// Copy and unzip
name|IOUtils
operator|.
name|copy
argument_list|(
name|gis
argument_list|,
name|fos
argument_list|)
expr_stmt|;
comment|// Cleanup
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|fos
argument_list|)
expr_stmt|;
name|IOUtils
operator|.
name|closeQuietly
argument_list|(
name|gis
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|DELETE
case|:
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|file
argument_list|)
expr_stmt|;
break|break;
case|case
name|MOVE
case|:
name|File
name|mvFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|em
operator|.
name|getDestinationPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|moveFile
argument_list|(
name|file
argument_list|,
name|mvFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|COPY
case|:
name|File
name|cpFile
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|em
operator|.
name|getDestinationPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|copyFile
argument_list|(
name|file
argument_list|,
name|cpFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown change type"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
index|[]
name|splitPath
parameter_list|(
name|String
name|fullPath
parameter_list|)
block|{
name|String
name|directory
decl_stmt|,
name|documentname
decl_stmt|;
name|int
name|separator
init|=
name|fullPath
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|separator
operator|==
operator|-
literal|1
condition|)
block|{
name|directory
operator|=
literal|""
expr_stmt|;
name|documentname
operator|=
name|fullPath
expr_stmt|;
block|}
else|else
block|{
name|directory
operator|=
name|fullPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|separator
argument_list|)
expr_stmt|;
name|documentname
operator|=
name|fullPath
operator|.
name|substring
argument_list|(
name|separator
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|String
index|[]
block|{
name|directory
block|,
name|documentname
block|}
return|;
block|}
specifier|private
name|void
name|handleCollection
parameter_list|(
name|eXistMessage
name|em
parameter_list|)
block|{
name|File
name|src
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|em
operator|.
name|getResourcePath
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|em
operator|.
name|getResourceOperation
argument_list|()
condition|)
block|{
case|case
name|CREATE_UPDATE
case|:
try|try
block|{
comment|// Create dirs if not existent
name|FileUtils
operator|.
name|forceMkdir
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|DELETE
case|:
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|src
argument_list|)
expr_stmt|;
break|break;
case|case
name|MOVE
case|:
name|File
name|mvDest
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|em
operator|.
name|getDestinationPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|moveDirectoryToDirectory
argument_list|(
name|src
argument_list|,
name|mvDest
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
case|case
name|COPY
case|:
name|File
name|cpDest
init|=
operator|new
name|File
argument_list|(
name|baseDir
argument_list|,
name|em
operator|.
name|getDestinationPath
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|FileUtils
operator|.
name|copyDirectoryToDirectory
argument_list|(
name|src
argument_list|,
name|cpDest
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
name|LOG
operator|.
name|error
argument_list|(
literal|"Unknown change type"
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
