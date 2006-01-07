begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|net
operator|.
name|URISyntaxException
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
name|util
operator|.
name|serializer
operator|.
name|SAXSerializer
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
name|JavaObjectValue
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
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
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
name|XMLResource
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLDBStore
extends|extends
name|XMLDBAbstractCollectionManipulator
block|{
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
operator|new
name|QName
argument_list|(
literal|"store"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Store a new resource into the database. The first "
operator|+
literal|"argument denotes the collection where the resource should be stored. "
operator|+
literal|"The collection can be either specified as a simple collection path, "
operator|+
literal|"an XMLDB URI, or a collection object as returned by the collection or "
operator|+
literal|"create-collection functions. The second argument is the name of the new "
operator|+
literal|"resource. The third argument is either a node, an xs:string, a Java file object or an xs:anyURI. "
operator|+
literal|"A node will be serialized to SAX. It becomes the root node of the new "
operator|+
literal|"document. If the argument is of type xs:anyURI, the resource is loaded "
operator|+
literal|"from that URI. The functions returns the path to the new document as an xs:string or "
operator|+
literal|" - if the document could not be stored - the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"store"
argument_list|,
name|XMLDBModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XMLDBModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Store a new resource into the database. The first "
operator|+
literal|"argument denotes the collection where the resource should be stored. "
operator|+
literal|"The collection can be either specified as a simple collection path, "
operator|+
literal|"an XMLDB URI, or a collection object as returned by the collection or "
operator|+
literal|"create-collection functions. The second argument is the name of the new "
operator|+
literal|"resource. The third argument is either a node, an xs:string, a Java file object or an xs:anyURI. "
operator|+
literal|"A node will be serialized to SAX. It becomes the root node of the new "
operator|+
literal|"document. If the argument is of type xs:anyURI, the resource is loaded "
operator|+
literal|"from that URI. The final argument $d is used to specify a mime-type.  If the mime-type "
operator|+
literal|"is something other than 'text/xml' or 'application/xml', the resource will be stored as "
operator|+
literal|"a binary resource. The functions returns the path to the new document as an xs:string or "
operator|+
literal|"- if the document could not be stored - the empty sequence."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 * @param signature 	 */
specifier|public
name|XMLDBStore
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
name|Sequence
name|evalWithCollection
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|Sequence
name|args
index|[]
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|docName
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|?
literal|null
else|:
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|docName
operator|!=
literal|null
operator|&&
name|docName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
name|docName
operator|=
literal|null
expr_stmt|;
name|String
name|mimeType
init|=
literal|"text/xml"
decl_stmt|;
name|boolean
name|binary
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
operator|==
literal|4
condition|)
block|{
name|mimeType
operator|=
name|args
index|[
literal|3
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|mimeType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
name|binary
operator|=
operator|!
name|mime
operator|.
name|isXMLType
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|docName
operator|!=
literal|null
condition|)
block|{
name|MimeType
name|mime
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentTypeFor
argument_list|(
name|docName
argument_list|)
decl_stmt|;
if|if
condition|(
name|mime
operator|!=
literal|null
condition|)
block|{
name|mimeType
operator|=
name|mime
operator|.
name|getName
argument_list|()
expr_stmt|;
name|binary
operator|=
operator|!
name|mime
operator|.
name|isXMLType
argument_list|()
expr_stmt|;
block|}
block|}
name|Item
name|item
init|=
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Resource
name|resource
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|JAVA_OBJECT
argument_list|)
condition|)
block|{
name|Object
name|obj
init|=
operator|(
operator|(
name|JavaObjectValue
operator|)
name|item
operator|)
operator|.
name|getObject
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|obj
operator|instanceof
name|File
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Passed java object should be a File"
argument_list|)
throw|;
name|resource
operator|=
name|loadFromFile
argument_list|(
name|collection
argument_list|,
operator|(
name|File
operator|)
name|obj
argument_list|,
name|docName
argument_list|,
name|binary
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|)
condition|)
block|{
try|try
block|{
name|URI
name|uri
init|=
operator|new
name|URI
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|resource
operator|=
name|loadFromURI
argument_list|(
name|collection
argument_list|,
name|uri
argument_list|,
name|docName
argument_list|,
name|binary
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Invalid URI: "
operator|+
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|binary
condition|)
block|{
name|resource
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|docName
argument_list|,
literal|"BinaryResource"
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
argument_list|)
expr_stmt|;
block|}
else|else
name|resource
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|docName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|resource
operator|.
name|setContent
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|binary
condition|)
block|{
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|item
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ContentHandler
name|handler
init|=
operator|(
operator|(
name|XMLResource
operator|)
name|resource
operator|)
operator|.
name|setContentAsSAX
argument_list|()
decl_stmt|;
name|handler
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|item
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|handler
argument_list|)
expr_stmt|;
name|handler
operator|.
name|endDocument
argument_list|()
expr_stmt|;
block|}
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Data should be either a node or a string"
argument_list|)
throw|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|context
operator|.
name|getRootExpression
argument_list|()
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XMLDB reported an exception while storing document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"SAX reported an exception while storing document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|resource
operator|==
literal|null
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
else|else
try|try
block|{
comment|//TODO : use dedicated function in XmldbURI
return|return
operator|new
name|StringValue
argument_list|(
name|collection
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
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XMLDB reported an exception while retrieving the "
operator|+
literal|"stored document"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Resource
name|loadFromURI
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|URI
name|uri
parameter_list|,
name|String
name|docName
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|XPathException
block|{
name|Resource
name|resource
decl_stmt|;
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
name|String
name|path
init|=
name|uri
operator|.
name|getPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Cannot read from URI: "
operator|+
name|uri
operator|.
name|toASCIIString
argument_list|()
argument_list|)
throw|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Cannot read path: "
operator|+
name|path
argument_list|)
throw|;
name|resource
operator|=
name|loadFromFile
argument_list|(
name|collection
argument_list|,
name|file
argument_list|,
name|docName
argument_list|,
name|binary
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|File
name|temp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"existDBS"
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
comment|// This is deleted later; is this necessary?
name|temp
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|temp
argument_list|)
decl_stmt|;
name|InputStream
name|is
init|=
name|uri
operator|.
name|toURL
argument_list|()
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|is
operator|.
name|read
argument_list|(
name|data
argument_list|)
operator|)
operator|>
operator|-
literal|1
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
name|read
argument_list|)
expr_stmt|;
block|}
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|resource
operator|=
name|loadFromFile
argument_list|(
name|collection
argument_list|,
name|temp
argument_list|,
name|docName
argument_list|,
name|binary
argument_list|,
name|mimeType
argument_list|)
expr_stmt|;
name|temp
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Malformed URL: "
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"IOException while reading from URL: "
operator|+
name|uri
operator|.
name|toString
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
return|return
name|resource
return|;
block|}
specifier|private
name|Resource
name|loadFromFile
parameter_list|(
name|Collection
name|collection
parameter_list|,
name|File
name|file
parameter_list|,
name|String
name|docName
parameter_list|,
name|boolean
name|binary
parameter_list|,
name|String
name|mimeType
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|file
operator|.
name|isFile
argument_list|()
condition|)
block|{
if|if
condition|(
name|docName
operator|==
literal|null
condition|)
name|docName
operator|=
name|file
operator|.
name|getName
argument_list|()
expr_stmt|;
try|try
block|{
name|Resource
name|resource
decl_stmt|;
if|if
condition|(
name|binary
condition|)
block|{
name|resource
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|docName
argument_list|,
literal|"BinaryResource"
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
argument_list|)
expr_stmt|;
block|}
else|else
name|resource
operator|=
name|collection
operator|.
name|createResource
argument_list|(
name|docName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|file
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
return|return
name|resource
return|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Could not store file "
operator|+
name|file
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
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|file
operator|.
name|getAbsolutePath
argument_list|()
operator|+
literal|" does not point to a file"
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

