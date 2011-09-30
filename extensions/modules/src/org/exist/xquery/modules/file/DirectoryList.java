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
name|File
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
name|DirectoryScanner
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
name|SequenceIterator
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
comment|/**  * eXist File Module Extension DirectoryList  *   * Enumerate a list of files, including their size and modification time, found in a specified directory, using a pattern  *   * @author Andrzej Taramina<andrzej@chaeron.com>  * @author ljo  * @serial 2009-08-09  * @version 1.2  *  * @see org.exist.xquery.BasicFunction#BasicFunction(org.exist.xquery.XQueryContext, org.exist.xquery.FunctionSignature)  */
end_comment

begin_class
specifier|public
class|class
name|DirectoryList
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DirectoryList
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
literal|"directory-list"
argument_list|,
name|NAMESPACE_URI
argument_list|,
name|PREFIX
argument_list|)
argument_list|,
literal|"List all files, including their file size and modification time, "
operator|+
literal|"found in or below a directory, $directory. Files are located in the server's "
operator|+
literal|"file system, using filename patterns, $pattern.  File pattern matching is based "
operator|+
literal|"on code from Apache's Ant, thus following the same conventions. For example:\n\n"
operator|+
literal|"'*.xml' matches any file ending with .xml in the current directory,\n- '**/*.xml' matches files "
operator|+
literal|"in any directory below the specified directory.  This method is only available to the DBA role."
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
literal|"The base directory path or URI in the file system where the files are located."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"pattern"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The file name pattern"
argument_list|)
block|}
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
name|ZERO_OR_ONE
argument_list|,
literal|"a node fragment that shows all matching "
operator|+
literal|"filenames, including their file size and modification time, and "
operator|+
literal|"the subdirectory they were found in"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * DirectoryList Constructor 	 *  	 * @param context	The Context of the calling XQuery 	 */
specifier|public
name|DirectoryList
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
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
comment|/** 	 * evaluate the call to the XQuery execute() function, 	 * it is really the main entry point of this class 	 *  	 * @param args		arguments from the execute() function call 	 * @param contextSequence	the Context Sequence to operate on (not used here internally!) 	 * @return		A node representing the SQL result set 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
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
name|File
name|baseDir
init|=
name|FileModuleHelper
operator|.
name|getFile
argument_list|(
name|inputPath
argument_list|)
decl_stmt|;
name|Sequence
name|patterns
init|=
name|args
index|[
literal|1
index|]
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
name|baseDir
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|xmlResponse
init|=
literal|null
decl_stmt|;
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
literal|"directory"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|baseDir
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|patterns
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|pattern
init|=
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|File
index|[]
name|scannedFiles
init|=
name|DirectoryScanner
operator|.
name|scanDir
argument_list|(
name|baseDir
argument_list|,
name|pattern
argument_list|)
decl_stmt|;
name|String
name|relDir
init|=
literal|null
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
literal|"Found: "
operator|+
name|scannedFiles
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|File
name|file
range|:
name|scannedFiles
control|)
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
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|String
name|relPath
init|=
name|file
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
name|baseDir
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|relPath
operator|.
name|lastIndexOf
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>=
literal|0
condition|)
block|{
name|relDir
operator|=
name|relPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|relDir
operator|=
name|relDir
operator|.
name|replace
argument_list|(
name|File
operator|.
name|separatorChar
argument_list|,
literal|'/'
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|startElement
argument_list|(
operator|new
name|QName
argument_list|(
literal|"file"
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
name|file
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|Long
name|sizeLong
init|=
name|file
operator|.
name|length
argument_list|()
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
name|file
operator|.
name|lastModified
argument_list|()
argument_list|)
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|relDir
operator|!=
literal|null
operator|&&
name|relDir
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|builder
operator|.
name|addAttribute
argument_list|(
operator|new
name|QName
argument_list|(
literal|"subdir"
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
argument_list|,
name|relDir
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|xmlResponse
operator|=
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
expr_stmt|;
return|return
operator|(
name|xmlResponse
operator|)
return|;
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

