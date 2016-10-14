begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|xmldb
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
name|ArrayList
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
name|util
operator|.
name|MimeTable
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
name|MimeType
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
name|EXistResource
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
name|StringValue
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
name|Resource
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBLoadFromPattern
extends|extends
name|XMLDBAbstractCollectionManipulator
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|XMLDBLoadFromPattern
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|QName
name|FUNCTION_NAME
init|=
operator|new
name|QName
argument_list|(
literal|"store-files-from-pattern"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|FUNCTION_DESCRIPTION
init|=
literal|"Stores new resources into the database. Resources are read from the server's "
operator|+
literal|"file system, using file patterns. "
operator|+
literal|"The function returns a sequence of all document paths added "
operator|+
literal|"to the db. These can be directly passed to fn:doc() to retrieve the document(s)."
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|PARAM_COLLECTION
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"collection-uri"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The collection-uri where resources should be stored. "
operator|+
name|XMLDBModule
operator|.
name|COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|PARAM_FS_DIRECTORY
init|=
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
literal|"The directory in the file system from where the files are read."
argument_list|)
decl_stmt|;
comment|// fixit! - security - we should say some words about sanity
comment|// DBA role should be required for anything short of chroot/jail
comment|// easily setup per installation/execution host for each function. /ljo
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|PARAM_FS_PATTERN
init|=
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
name|ONE_OR_MORE
argument_list|,
literal|"The file matching pattern. Based on code from Apache's Ant, thus following the same conventions. For example: *.xml matches any file ending with .xml in the current directory, **/*.xml matches files in any directory below the current one"
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|PARAM_MIME_TYPE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mime-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"If the mime-type is something other than 'text/xml' or 'application/xml', the resource will be stored as a binary resource."
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|SequenceType
name|PARAM_PRESERVE_STRUCTURE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"preserve-structure"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"If preserve-structure is true(), the filesystem directory structure will be mirrored in the collection. Otherwise all the matching resources, including the ones in sub-directories, will be stored in the collection given in the first argument flatly."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|PARAM_EXCLUDES
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"exclude"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A sequence of file patterns to exclude"
argument_list|)
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|RETURN_TYPE
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence of document paths"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
name|FUNCTION_NAME
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_COLLECTION
block|,
name|PARAM_FS_DIRECTORY
block|,
name|PARAM_FS_PATTERN
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|FUNCTION_NAME
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_COLLECTION
block|,
name|PARAM_FS_DIRECTORY
block|,
name|PARAM_FS_PATTERN
block|,
name|PARAM_MIME_TYPE
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|FUNCTION_NAME
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_COLLECTION
block|,
name|PARAM_FS_DIRECTORY
block|,
name|PARAM_FS_PATTERN
block|,
name|PARAM_MIME_TYPE
block|,
name|PARAM_PRESERVE_STRUCTURE
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
name|FUNCTION_NAME
argument_list|,
name|FUNCTION_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|PARAM_COLLECTION
block|,
name|PARAM_FS_DIRECTORY
block|,
name|PARAM_FS_PATTERN
block|,
name|PARAM_MIME_TYPE
block|,
name|PARAM_PRESERVE_STRUCTURE
block|,
name|PARAM_EXCLUDES
block|}
argument_list|,
name|RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
specifier|public
name|XMLDBLoadFromPattern
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.functions.xmldb.XMLDBAbstractCollectionManipulator#evalWithCollection(org.xmldb.api.base.Collection, org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
specifier|protected
name|Sequence
name|evalWithCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
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
specifier|final
name|File
name|baseDir
init|=
operator|new
name|File
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Loading files from directory: "
operator|+
name|baseDir
argument_list|)
expr_stmt|;
comment|//determine resource type - xml or binary?
name|MimeType
name|mimeTypeFromArgs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>
literal|3
operator|&&
name|args
index|[
literal|3
index|]
operator|.
name|hasOne
argument_list|()
condition|)
block|{
specifier|final
name|String
name|mimeTypeParam
init|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|mimeTypeFromArgs
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|mimeTypeParam
argument_list|)
expr_stmt|;
if|if
condition|(
name|mimeTypeFromArgs
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown mime type specified: "
operator|+
name|mimeTypeParam
argument_list|)
throw|;
block|}
block|}
comment|//keep the directory structure?
name|boolean
name|keepDirStructure
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|>=
literal|5
condition|)
block|{
name|keepDirStructure
operator|=
name|args
index|[
literal|4
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|excludes
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
name|getSignature
argument_list|()
operator|.
name|getArgumentCount
argument_list|()
operator|==
literal|6
condition|)
block|{
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|5
index|]
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
name|excludes
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|ValueSequence
name|stored
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
comment|//store according to each pattern
specifier|final
name|Sequence
name|patterns
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
for|for
control|(
specifier|final
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
comment|//get the files to store
specifier|final
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
specifier|final
name|File
index|[]
name|files
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
name|logger
operator|.
name|debug
argument_list|(
literal|"Found: "
operator|+
name|files
operator|.
name|length
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|collection
decl_stmt|;
name|String
name|relDir
decl_stmt|,
name|prevDir
init|=
literal|null
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
name|files
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
try|try
block|{
name|logger
operator|.
name|debug
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|relPath
init|=
name|files
index|[
name|j
index|]
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
argument_list|)
decl_stmt|;
specifier|final
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
name|checkExcludes
argument_list|(
name|excludes
argument_list|,
name|relPath
argument_list|)
condition|)
block|{
continue|continue;
block|}
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
else|else
block|{
name|relDir
operator|=
name|relPath
expr_stmt|;
block|}
if|if
condition|(
name|keepDirStructure
operator|&&
operator|(
name|prevDir
operator|==
literal|null
operator|||
operator|(
operator|!
name|relDir
operator|.
name|equals
argument_list|(
name|prevDir
argument_list|)
operator|)
operator|)
condition|)
block|{
name|col
operator|=
name|createCollectionPath
argument_list|(
name|collection
argument_list|,
name|relDir
argument_list|)
expr_stmt|;
name|prevDir
operator|=
name|relDir
expr_stmt|;
block|}
name|MimeType
name|mimeType
init|=
name|mimeTypeFromArgs
decl_stmt|;
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
name|mimeType
operator|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|mimeType
operator|==
literal|null
condition|)
block|{
name|mimeType
operator|=
name|MimeType
operator|.
name|BINARY_TYPE
expr_stmt|;
block|}
block|}
comment|//TODO  : these probably need to be encoded and checked for right mime type
specifier|final
name|Resource
name|resource
init|=
name|col
operator|.
name|createResource
argument_list|(
name|files
index|[
name|j
index|]
operator|.
name|getName
argument_list|()
argument_list|,
name|mimeType
operator|.
name|getXMLDBType
argument_list|()
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|files
index|[
name|j
index|]
argument_list|)
expr_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|resource
operator|)
operator|.
name|setMimeType
argument_list|(
name|mimeType
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|col
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
comment|//TODO : use dedicated function in XmldbURI
name|stored
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|col
operator|.
name|getName
argument_list|()
operator|+
literal|"/"
operator|+
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
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
name|logger
operator|.
name|error
argument_list|(
literal|"Could not store file "
operator|+
name|files
index|[
name|j
index|]
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|": "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|stored
return|;
block|}
comment|/**      * Check if path matches any of the exclude patterns.      */
specifier|private
specifier|static
name|boolean
name|checkExcludes
parameter_list|(
name|List
argument_list|<
name|String
argument_list|>
name|excludes
parameter_list|,
name|String
name|path
parameter_list|)
block|{
if|if
condition|(
name|excludes
operator|==
literal|null
operator|||
name|excludes
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|path
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
name|File
operator|.
name|separatorChar
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|boolean
name|skip
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|exclude
range|:
name|excludes
control|)
block|{
if|if
condition|(
name|DirectoryScanner
operator|.
name|match
argument_list|(
name|exclude
argument_list|,
name|path
argument_list|)
condition|)
block|{
name|skip
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
return|return
name|skip
return|;
block|}
block|}
end_class

end_unit

