begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|file
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
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
name|dom
operator|.
name|memtree
operator|.
name|MemTreeBuilder
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
name|BasicFunction
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
name|Cardinality
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
name|FunctionSignature
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
name|XPathException
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
name|XQueryContext
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
name|BooleanValue
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
name|DateTimeValue
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
name|FunctionReturnSequenceType
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
name|NodeValue
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

begin_comment
comment|/**  * eXist File Module Extension DirectoryList  *   * Enumerate a list of files and directories, including their size and modification time, found in  * a specified directory  *  * @author<a href="mailto:dannes@exist-db.org">Dannes Wessels</a>  * @author<a href="mailto:andrzej@chaeron.com">Andrzej Taramina</a>  * @author<a href="mailto:ljo@exist-db.org">Leif-JÃ¶ran Olsson</a>  * @serial 2010-05-12  * @version 1.2  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|Directory
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Directory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
name|FileModule
operator|.
name|NAMESPACE_URI
decl_stmt|;
specifier|final
specifier|static
name|String
name|PREFIX
init|=
name|FileModule
operator|.
name|PREFIX
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"list"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|"List all files and directories under the specified directory. "
operator|+
literal|"This method is only available to the DBA role."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"path"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The directory path or URI in the file system."
argument_list|)
block|,             }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"a node describing file and directory names and meta data."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Directory
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
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
annotation|@
name|Override
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
name|getSubject
argument_list|()
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
name|XPathException
name|xPathException
init|=
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission denied, calling user '"
operator|+
name|context
operator|.
name|getSubject
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"' must be a DBA to call this function."
argument_list|)
decl_stmt|;
name|logger
operator|.
name|error
argument_list|(
literal|"Invalid user"
argument_list|,
name|xPathException
argument_list|)
expr_stmt|;
throw|throw
name|xPathException
throw|;
block|}
specifier|final
name|String
name|inputPath
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Path
name|directoryPath
init|=
name|FileModuleHelper
operator|.
name|getFile
argument_list|(
name|inputPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Listing matching files in directory: "
operator|+
name|directoryPath
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|directoryPath
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"'"
operator|+
name|inputPath
operator|+
literal|"' does not point to a valid directory."
argument_list|)
throw|;
block|}
comment|// Get list of files, null if baseDir does not point to a directory
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|Stream
argument_list|<
name|Path
argument_list|>
name|scannedFiles
init|=
name|Files
operator|.
name|list
argument_list|(
name|directoryPath
argument_list|)
init|)
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|builder
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"list"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|scannedFiles
operator|.
name|forEach
argument_list|(
name|entry
lambda|->
block|{
if|if
condition|(
name|logger
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Found: "
operator|+
name|entry
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|entryType
init|=
literal|"unknown"
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|entry
argument_list|)
condition|)
block|{
name|entryType
operator|=
literal|"file"
expr_stmt|;
block|}
if|else if
condition|(
name|Files
operator|.
name|isDirectory
argument_list|(
name|entry
argument_list|)
condition|)
block|{
name|entryType
operator|=
literal|"directory"
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
name|entryType
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"name"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|entry
argument_list|)
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|isRegularFile
argument_list|(
name|entry
argument_list|)
condition|)
block|{
specifier|final
name|Long
name|sizeLong
init|=
name|Files
operator|.
name|size
argument_list|(
name|entry
argument_list|)
decl_stmt|;
name|String
name|sizeString
init|=
name|Long
operator|.
name|toString
argument_list|(
name|sizeLong
argument_list|)
decl_stmt|;
name|String
name|humanSize
init|=
name|getHumanSize
argument_list|(
name|sizeLong
argument_list|,
name|sizeString
argument_list|)
decl_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"size"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|sizeString
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"human-size"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|humanSize
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"modified"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|DateTimeValue
argument_list|(
operator|new
name|Date
argument_list|(
name|Files
operator|.
name|getLastModifiedTime
argument_list|(
name|entry
argument_list|)
operator|.
name|toMillis
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"hidden"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|BooleanValue
argument_list|(
name|Files
operator|.
name|isHidden
argument_list|(
name|entry
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"canRead"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|BooleanValue
argument_list|(
name|Files
operator|.
name|isReadable
argument_list|(
name|entry
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"canWrite"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
operator|new
name|BooleanValue
argument_list|(
name|Files
operator|.
name|isWritable
argument_list|(
name|entry
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XPathException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|ioe
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
return|return
operator|(
name|NodeValue
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|getHumanSize
parameter_list|(
specifier|final
name|Long
name|sizeLong
parameter_list|,
specifier|final
name|String
name|sizeString
parameter_list|)
block|{
name|String
name|humanSize
init|=
literal|"n/a"
decl_stmt|;
name|int
name|sizeDigits
init|=
name|sizeString
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|sizeDigits
operator|<
literal|4
condition|)
block|{
name|humanSize
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|sizeLong
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|sizeDigits
operator|>=
literal|4
operator|&&
name|sizeDigits
operator|<=
literal|6
condition|)
block|{
if|if
condition|(
name|sizeLong
operator|<
literal|1024
condition|)
block|{
comment|// We don't want 0KB fÃ¶r e.g. 1006 Bytes.
name|humanSize
operator|=
name|Long
operator|.
name|toString
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|sizeLong
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|humanSize
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|sizeLong
operator|/
literal|1024
argument_list|)
operator|+
literal|"KB"
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|sizeDigits
operator|>=
literal|7
operator|&&
name|sizeDigits
operator|<=
literal|9
condition|)
block|{
if|if
condition|(
name|sizeLong
operator|<
literal|1048576
condition|)
block|{
name|humanSize
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|sizeLong
operator|/
literal|1024
argument_list|)
operator|+
literal|"KB"
expr_stmt|;
block|}
else|else
block|{
name|humanSize
operator|=
name|Math
operator|.
name|abs
argument_list|(
name|sizeLong
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
argument_list|)
operator|+
literal|"MB"
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|sizeDigits
operator|>
literal|9
condition|)
block|{
if|if
condition|(
name|sizeLong
operator|<
literal|1073741824
condition|)
block|{
name|humanSize
operator|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|sizeLong
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|)
operator|)
argument_list|)
operator|+
literal|"MB"
expr_stmt|;
block|}
else|else
block|{
name|humanSize
operator|=
name|Math
operator|.
name|abs
argument_list|(
operator|(
name|sizeLong
operator|/
operator|(
literal|1024
operator|*
literal|1024
operator|*
literal|1024
operator|)
operator|)
argument_list|)
operator|+
literal|"GB"
expr_stmt|;
block|}
block|}
return|return
name|humanSize
return|;
block|}
block|}
end_class

end_unit

