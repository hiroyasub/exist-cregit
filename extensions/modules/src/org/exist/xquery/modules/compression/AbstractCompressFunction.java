begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2007-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|io
operator|.
name|OutputStream
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
name|output
operator|.
name|ByteArrayOutputStream
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
name|BinaryDocument
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
name|DefaultDocumentSet
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
name|DocumentImpl
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
name|MutableDocumentSet
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
name|lock
operator|.
name|Lock
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
name|LockException
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
name|AnyURIValue
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
name|Base64Binary
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
name|Item
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

begin_comment
comment|/**  * Compresses a sequence of resources and/or collections  *   * @author Adam Retter<adam@exist-db.org>  * @version 1.0  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCompressFunction
extends|extends
name|BasicFunction
block|{
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
literal|"The sequence of URI's and/or Entrys. If a URI points to a collection then the collection, its resources and sub-collections are zipped recursively. An Entry takes the format<entry name=\"filename.ext\" type=\"collection|uri|binary|xml|text\" method=\"deflate|store\">data</entry>. The method attribute is only effective for the compression:zip function."
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
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|OutputStream
name|os
init|=
name|stream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
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
name|toXmldbURI
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
try|try
block|{
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
argument_list|)
throw|;
block|}
return|return
operator|new
name|Base64Binary
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|void
name|compressFromUri
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|XmldbURI
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
comment|// try for a doc
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|doc
operator|==
literal|null
condition|)
block|{
comment|// no doc, try for a collection
name|Collection
name|col
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getCollection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|col
operator|!=
literal|null
condition|)
block|{
comment|// got a collection
name|compressCollection
argument_list|(
name|os
argument_list|,
name|col
argument_list|,
name|useHierarchy
argument_list|,
name|stripOffset
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// no doc or collection
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
block|}
else|else
block|{
comment|// got a doc
name|compressResource
argument_list|(
name|os
argument_list|,
name|doc
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
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
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
catch|catch
parameter_list|(
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
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|LockException
name|le
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|le
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Adds a element to a archive 	 *  	 * @param os 	 *            The Output Stream to add the element to 	 * @param nodeValue 	 *            The element to add to the archive 	 * @param useHierarchy 	 *            Whether to use a folder hierarchy in the archive file that 	 *            reflects the collection hierarchy 	 */
specifier|private
name|void
name|compressElement
parameter_list|(
name|OutputStream
name|os
parameter_list|,
name|Element
name|element
parameter_list|,
name|boolean
name|useHierarchy
parameter_list|,
name|String
name|stripOffset
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|element
operator|.
name|getNamespaceURI
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Item must be type of xs:anyURI or element enry."
argument_list|)
throw|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Entry content is not valid XML fragment."
argument_list|)
throw|;
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
if|if
condition|(
name|name
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Entry must have name attribute."
argument_list|)
throw|;
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
name|XmldbURI
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
name|name
operator|+=
literal|"/"
expr_stmt|;
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
name|putEntry
argument_list|(
name|os
argument_list|,
name|entry
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
name|getUser
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
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|saxe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|saxe
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
name|Object
name|entry
init|=
literal|null
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
name|putEntry
argument_list|(
name|os
argument_list|,
name|entry
argument_list|)
expr_stmt|;
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
name|getUser
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
name|os
operator|.
name|write
argument_list|(
name|strDoc
operator|.
name|getBytes
argument_list|()
argument_list|)
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
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|16384
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
operator|)
operator|>
literal|0
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
comment|// close the entry
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
block|{
comment|// iterate over child documents
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
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|childDocs
argument_list|,
literal|true
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
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
operator|(
name|DocumentImpl
operator|)
name|itChildDocs
operator|.
name|next
argument_list|()
decl_stmt|;
name|childDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
try|try
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
finally|finally
block|{
name|childDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
comment|// iterate over child collections
for|for
control|(
name|Iterator
name|itChildCols
init|=
name|col
operator|.
name|collectionIterator
argument_list|()
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
operator|(
name|XmldbURI
operator|)
name|itChildCols
operator|.
name|next
argument_list|()
decl_stmt|;
name|Collection
name|childCol
init|=
name|context
operator|.
name|getBroker
argument_list|()
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
name|ByteArrayOutputStream
name|baos
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

