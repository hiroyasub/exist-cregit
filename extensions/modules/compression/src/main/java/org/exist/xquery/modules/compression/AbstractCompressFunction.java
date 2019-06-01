begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007-2013 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|compression
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
name|collections
operator|.
name|Collection
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
name|persistent
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
name|lock
operator|.
name|Lock
operator|.
name|LockMode
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
name|lock
operator|.
name|LockManager
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
name|lock
operator|.
name|ManagedDocumentLock
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
name|serializers
operator|.
name|Serializer
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
name|Base64Decoder
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
name|LockException
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|UnsupportedCharsetException
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
name|Iterator
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
name|CRC32
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
name|DeflaterOutputStream
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
name|ZipEntry
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
name|ZipOutputStream
import|;
end_import

begin_comment
comment|/**  * Compresses a sequence of resources and/or collections  *   * @author Adam Retter<adam@exist-db.org>  * @author Leif-JÃ¶ran Olsson<ljo@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCompressFunction
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
name|AbstractCompressFunction
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|SOURCES_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sources"
argument_list|,
name|Type
operator|.
name|ANY_TYPE
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The sequence of URI's and/or Entrys. If an URI points to a collection then the collection, its resources and sub-collections are zipped recursively. "
operator|+
literal|"If URI points to file (available only to the DBA role.) then file or directory are zipped. "
operator|+
literal|"An Entry takes the format<entry name=\"filename.ext\" type=\"collection|uri|binary|xml|text\" method=\"deflate|store\">data</entry>. The method attribute is only effective for the compression:zip function."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|COLLECTION_HIERARCHY_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"use-collection-hierarchy"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Indicates whether the Collection hierarchy (if any) should be preserved in the zip file."
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|STRIP_PREFIX_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"strip-prefix"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"This prefix is stripped from the Entrys name"
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|ENCODING_PARAM
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"encoding"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"This encoding to be used for filenames inside the compressed file"
argument_list|)
decl_stmt|;
specifier|public
name|AbstractCompressFunction
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
specifier|private
name|String
name|removeLeadingOffset
parameter_list|(
name|String
name|uri
parameter_list|,
name|String
name|stripOffset
parameter_list|)
block|{
comment|// remove leading offset
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
name|stripOffset
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
name|stripOffset
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// remove leading /
if|if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|uri
operator|=
name|uri
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
return|return
name|uri
return|;
block|}
annotation|@
name|Override
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
comment|// are there some uri's to tar?
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|// use a hierarchy in the tar file?
name|boolean
name|useHierarchy
init|=
name|args
index|[
literal|1
index|]
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|// Get offset
name|String
name|stripOffset
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|3
condition|)
block|{
name|stripOffset
operator|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
comment|// Get encoding
try|try
block|{
specifier|final
name|Charset
name|encoding
decl_stmt|;
if|if
condition|(
operator|(
name|args
operator|.
name|length
operator|>=
literal|4
operator|)
operator|&&
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|encoding
operator|=
name|Charset
operator|.
name|forName
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|encoding
operator|=
name|StandardCharsets
operator|.
name|UTF_8
expr_stmt|;
block|}
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|;
name|OutputStream
name|os
init|=
name|stream
argument_list|(
name|baos
argument_list|,
name|encoding
argument_list|)
init|)
block|{
comment|// iterate through the argument sequence
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|Element
condition|)
block|{
name|Element
name|element
init|=
operator|(
name|Element
operator|)
name|item
decl_stmt|;
name|compressElement
argument_list|(
name|os
argument_list|,
name|element
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|compressFromUri
argument_list|(
name|os
argument_list|,
operator|(
operator|(
name|AnyURIValue
operator|)
name|item
operator|)
operator|.
name|toURI
argument_list|()
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|os
operator|.
name|flush
argument_list|()
expr_stmt|;
if|if
condition|(
name|os
operator|instanceof
name|DeflaterOutputStream
condition|)
block|{
operator|(
operator|(
name|DeflaterOutputStream
operator|)
name|os
operator|)
operator|.
name|finish
argument_list|()
expr_stmt|;
block|}
return|return
name|BinaryValueFromInputStream
operator|.
name|getInstance
argument_list|(
name|context
argument_list|,
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
operator|new
name|FastByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|UnsupportedCharsetException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|void
name|compressFromUri
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|URI
name|uri
parameter_list|,
name|boolean
name|useHierarchy
parameter_list|,
name|String
name|stripOffset
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|resourceName
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
literal|"file"
operator|.
name|equals
argument_list|(
name|uri
operator|.
name|getScheme
argument_list|()
argument_list|)
condition|)
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
comment|// got a file
name|Path
name|file
init|=
name|Paths
operator|.
name|get
argument_list|(
name|uri
operator|.
name|getPath
argument_list|()
argument_list|)
decl_stmt|;
name|compressFile
argument_list|(
name|os
argument_list|,
name|file
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
name|method
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|XmldbURI
name|xmldburi
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|// try for a collection
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|openCollection
argument_list|(
name|xmldburi
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|compressCollection
argument_list|(
name|os
argument_list|,
name|collection
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|LockException
decl||
name|SAXException
decl||
name|IOException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
comment|// otherwise, try for a doc
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|openCollection
argument_list|(
name|xmldburi
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|collection
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
literal|"Invalid URI: "
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
try|try
init|(
specifier|final
name|LockedDocument
name|doc
init|=
name|collection
operator|.
name|getDocumentWithLock
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|xmldburi
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
comment|// NOTE: early release of Collection lock inline with Asymmetrical Locking scheme
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
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
literal|"Invalid URI: "
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|compressResource
argument_list|(
name|os
argument_list|,
name|doc
operator|.
name|getDocument
argument_list|()
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
name|method
argument_list|,
name|resourceName
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|LockException
decl||
name|SAXException
decl||
name|IOException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Adds a element to a archive      *      * @param os      *            The Output Stream to add the element to      * @param file      *            The file to add to the archive      * @param useHierarchy      *            Whether to use a folder hierarchy in the archive file that      *            reflects the collection hierarchy      */
specifier|private
name|void
name|compressFile
parameter_list|(
specifier|final
name|OutputStream
name|os
parameter_list|,
specifier|final
name|Path
name|file
parameter_list|,
name|boolean
name|useHierarchy
parameter_list|,
name|String
name|stripOffset
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
operator|!
name|Files
operator|.
name|isDirectory
argument_list|(
name|file
argument_list|)
condition|)
block|{
comment|// create an entry in the Tar for the document
specifier|final
name|Object
name|entry
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|useHierarchy
condition|)
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|removeLeadingOffset
argument_list|(
name|file
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
name|stripOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|file
operator|.
name|getFileName
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|value
init|=
name|Files
operator|.
name|readAllBytes
argument_list|(
name|file
argument_list|)
decl_stmt|;
comment|// close the entry
specifier|final
name|CRC32
name|chksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|instanceof
name|ZipEntry
operator|&&
literal|"store"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setMethod
argument_list|(
name|ZipOutputStream
operator|.
name|STORED
argument_list|)
expr_stmt|;
name|chksum
operator|.
name|update
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setCrc
argument_list|(
name|chksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setSize
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|putEntry
argument_list|(
name|os
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|closeEntry
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
specifier|final
name|Path
name|child
range|:
name|FileUtils
operator|.
name|list
argument_list|(
name|file
argument_list|)
control|)
block|{
name|compressFile
argument_list|(
name|os
argument_list|,
name|file
operator|.
name|resolve
argument_list|(
name|child
argument_list|)
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
name|method
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Adds a element to a archive 	 *  	 * @param os 	 *            The Output Stream to add the element to 	 * @param element 	 *            The element to add to the archive 	 * @param useHierarchy 	 *            Whether to use a folder hierarchy in the archive file that 	 *            reflects the collection hierarchy 	 */
specifier|private
name|void
name|compressElement
parameter_list|(
specifier|final
name|OutputStream
name|os
parameter_list|,
specifier|final
name|Element
name|element
parameter_list|,
specifier|final
name|boolean
name|useHierarchy
parameter_list|,
specifier|final
name|String
name|stripOffset
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|ns
init|=
name|element
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|element
operator|.
name|getNodeName
argument_list|()
operator|.
name|equals
argument_list|(
literal|"entry"
argument_list|)
operator|||
operator|(
name|ns
operator|!=
literal|null
operator|&&
name|ns
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Item must be type of xs:anyURI or element entry."
argument_list|)
throw|;
block|}
if|if
condition|(
name|element
operator|.
name|getChildNodes
argument_list|()
operator|.
name|getLength
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Entry content is not valid XML fragment."
argument_list|)
throw|;
block|}
name|String
name|name
init|=
name|element
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
comment|//            if(name == null)
comment|//                throw new XPathException(this, "Entry must have name attribute.");
specifier|final
name|String
name|type
init|=
name|element
operator|.
name|getAttribute
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"uri"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|compressFromUri
argument_list|(
name|os
argument_list|,
name|URI
operator|.
name|create
argument_list|(
name|element
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
name|element
operator|.
name|getAttribute
argument_list|(
literal|"method"
argument_list|)
argument_list|,
name|name
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|useHierarchy
condition|)
block|{
name|name
operator|=
name|removeLeadingOffset
argument_list|(
name|name
argument_list|,
name|stripOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
name|name
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"collection"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|name
operator|+=
literal|"/"
expr_stmt|;
block|}
name|Object
name|entry
init|=
literal|null
decl_stmt|;
try|try
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
literal|"collection"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
name|byte
index|[]
name|value
decl_stmt|;
specifier|final
name|CRC32
name|chksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
specifier|final
name|Node
name|content
init|=
name|element
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
if|if
condition|(
name|content
operator|==
literal|null
condition|)
block|{
name|value
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|content
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
condition|)
block|{
name|String
name|text
init|=
name|content
operator|.
name|getNodeValue
argument_list|()
decl_stmt|;
name|Base64Decoder
name|dec
init|=
operator|new
name|Base64Decoder
argument_list|()
decl_stmt|;
if|if
condition|(
literal|"binary"
operator|.
name|equals
argument_list|(
name|type
argument_list|)
condition|)
block|{
comment|//base64 binary
name|dec
operator|.
name|translate
argument_list|(
name|text
argument_list|)
expr_stmt|;
name|value
operator|=
name|dec
operator|.
name|getByteArray
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|//text
name|value
operator|=
name|text
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
comment|//xml
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setUser
argument_list|(
name|context
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperty
argument_list|(
literal|"omit-xml-declaration"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|getDynamicSerializerOptions
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|value
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|NodeValue
operator|)
name|content
argument_list|)
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|entry
operator|instanceof
name|ZipEntry
operator|&&
literal|"store"
operator|.
name|equals
argument_list|(
name|element
operator|.
name|getAttribute
argument_list|(
literal|"method"
argument_list|)
argument_list|)
condition|)
block|{
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setMethod
argument_list|(
name|ZipOutputStream
operator|.
name|STORED
argument_list|)
expr_stmt|;
name|chksum
operator|.
name|update
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setCrc
argument_list|(
name|chksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setSize
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|putEntry
argument_list|(
name|os
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|SAXException
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
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|closeEntry
argument_list|(
name|os
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|getDynamicSerializerOptions
parameter_list|(
name|Serializer
name|serializer
parameter_list|)
throws|throws
name|SAXException
block|{
specifier|final
name|Option
name|option
init|=
name|context
operator|.
name|getOption
argument_list|(
name|Option
operator|.
name|SERIALIZE_QNAME
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
index|[]
name|params
init|=
name|option
operator|.
name|tokenizeContents
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|param
range|:
name|params
control|)
block|{
comment|// OutputKeys.INDENT
specifier|final
name|String
index|[]
name|kvp
init|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|param
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|setProperty
argument_list|(
name|kvp
index|[
literal|0
index|]
argument_list|,
name|kvp
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/** 	 * Adds a document to a archive 	 *  	 * @param os 	 *            The Output Stream to add the document to 	 * @param doc 	 *            The document to add to the archive 	 * @param useHierarchy 	 *            Whether to use a folder hierarchy in the archive file that 	 *            reflects the collection hierarchy 	 */
specifier|private
name|void
name|compressResource
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|useHierarchy
parameter_list|,
name|String
name|stripOffset
parameter_list|,
name|String
name|method
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|// create an entry in the Tar for the document
specifier|final
name|Object
name|entry
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|useHierarchy
condition|)
block|{
name|String
name|docCollection
init|=
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getURI
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|XmldbURI
name|collection
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|removeLeadingOffset
argument_list|(
name|docCollection
argument_list|,
name|stripOffset
argument_list|)
argument_list|)
decl_stmt|;
name|entry
operator|=
name|newEntry
argument_list|(
name|collection
operator|.
name|append
argument_list|(
name|doc
operator|.
name|getFileURI
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|=
name|newEntry
argument_list|(
name|doc
operator|.
name|getFileURI
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|value
decl_stmt|;
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
comment|// xml file
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setUser
argument_list|(
name|context
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperty
argument_list|(
literal|"omit-xml-declaration"
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|getDynamicSerializerOptions
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
name|String
name|strDoc
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|value
operator|=
name|strDoc
operator|.
name|getBytes
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|BINARY_FILE
condition|)
block|{
comment|// binary file
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|doc
argument_list|)
init|;
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
name|doc
operator|.
name|getContentLength
argument_list|()
operator|==
operator|-
literal|1
condition|?
literal|1024
else|:
operator|(
name|int
operator|)
name|doc
operator|.
name|getContentLength
argument_list|()
argument_list|)
init|)
block|{
name|baos
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|value
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
block|}
else|else
block|{
name|value
operator|=
operator|new
name|byte
index|[
literal|0
index|]
expr_stmt|;
block|}
comment|// close the entry
specifier|final
name|CRC32
name|chksum
init|=
operator|new
name|CRC32
argument_list|()
decl_stmt|;
if|if
condition|(
name|entry
operator|instanceof
name|ZipEntry
operator|&&
literal|"store"
operator|.
name|equals
argument_list|(
name|method
argument_list|)
condition|)
block|{
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setMethod
argument_list|(
name|ZipOutputStream
operator|.
name|STORED
argument_list|)
expr_stmt|;
name|chksum
operator|.
name|update
argument_list|(
name|value
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setCrc
argument_list|(
name|chksum
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
operator|(
operator|(
name|ZipEntry
operator|)
name|entry
operator|)
operator|.
name|setSize
argument_list|(
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|putEntry
argument_list|(
name|os
argument_list|,
name|entry
argument_list|)
expr_stmt|;
name|os
operator|.
name|write
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|closeEntry
argument_list|(
name|os
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Adds a Collection and its child collections and resources recursively to 	 * a archive 	 *  	 * @param os 	 *            The Output Stream to add the document to 	 * @param col 	 *            The Collection to add to the archive 	 * @param useHierarchy 	 *            Whether to use a folder hierarchy in the archive file that 	 *            reflects the collection hierarchy 	 */
specifier|private
name|void
name|compressCollection
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Collection
name|col
parameter_list|,
name|boolean
name|useHierarchy
parameter_list|,
name|String
name|stripOffset
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|PermissionDeniedException
block|{
comment|// iterate over child documents
specifier|final
name|DBBroker
name|broker
init|=
name|context
operator|.
name|getBroker
argument_list|()
decl_stmt|;
specifier|final
name|LockManager
name|lockManager
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getLockManager
argument_list|()
decl_stmt|;
specifier|final
name|MutableDocumentSet
name|childDocs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|col
operator|.
name|getDocuments
argument_list|(
name|broker
argument_list|,
name|childDocs
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|itChildDocs
init|=
name|childDocs
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|itChildDocs
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|childDoc
init|=
name|itChildDocs
operator|.
name|next
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|ManagedDocumentLock
name|updateLock
init|=
name|lockManager
operator|.
name|acquireDocumentReadLock
argument_list|(
name|childDoc
operator|.
name|getURI
argument_list|()
argument_list|)
init|)
block|{
name|compressResource
argument_list|(
name|os
argument_list|,
name|childDoc
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|,
literal|""
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
comment|// iterate over child collections
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|itChildCols
init|=
name|col
operator|.
name|collectionIterator
argument_list|(
name|broker
argument_list|)
init|;
name|itChildCols
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
comment|// get the child collection
name|XmldbURI
name|childColURI
init|=
name|itChildCols
operator|.
name|next
argument_list|()
decl_stmt|;
name|Collection
name|childCol
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|childColURI
argument_list|)
argument_list|)
decl_stmt|;
comment|// recurse
name|compressCollection
argument_list|(
name|os
argument_list|,
name|childCol
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
specifier|abstract
name|OutputStream
name|stream
parameter_list|(
name|FastByteArrayOutputStream
name|baos
parameter_list|,
name|Charset
name|encoding
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|Object
name|newEntry
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|void
name|putEntry
parameter_list|(
name|Object
name|os
parameter_list|,
name|Object
name|entry
parameter_list|)
throws|throws
name|IOException
function_decl|;
specifier|protected
specifier|abstract
name|void
name|closeEntry
parameter_list|(
name|Object
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_class

end_unit
