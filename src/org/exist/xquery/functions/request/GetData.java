begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: GetRequestData.java 2436 2006-01-07 21:47:15 +0000 (Sat, 07 Jan 2006) brihaye $  */
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
name|request
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
name|ByteArrayOutputStream
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
name|RequestWrapper
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
name|DocumentBuilderReceiver
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
name|Variable
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|InputSource
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

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|GetData
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
literal|"get-data"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the content of a POST request.If its a binary document xs:base64Binary is returned or if its an XML document a node() is returned. All other data is returned as an xs:string representaion. Returns an empty sequence if there is no data."
argument_list|,
literal|null
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
name|ZERO_OR_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|deprecated
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-request-data"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Returns the content of a POST request. If its a binary document xs:base64Binary is returned or if its an XML document a node() is returned. All other data is returned as an xs:string representaion. Returns an empty sequence if there is no data."
argument_list|,
literal|null
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
name|ZERO_OR_ONE
argument_list|)
argument_list|,
literal|"Renamed to get-data."
argument_list|)
decl_stmt|;
specifier|public
name|GetData
parameter_list|(
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence) 	 */
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
name|RequestModule
name|myModule
init|=
operator|(
name|RequestModule
operator|)
name|context
operator|.
name|getModule
argument_list|(
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|// request object is read from global variable $request
name|Variable
name|var
init|=
name|myModule
operator|.
name|resolveVariable
argument_list|(
name|RequestModule
operator|.
name|REQUEST_VAR
argument_list|)
decl_stmt|;
if|if
condition|(
name|var
operator|==
literal|null
operator|||
name|var
operator|.
name|getValue
argument_list|()
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
literal|"No request object found in the current XQuery context."
argument_list|)
throw|;
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|JAVA_OBJECT
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Variable $request is not bound to an Java object."
argument_list|)
throw|;
name|JavaObjectValue
name|value
init|=
operator|(
name|JavaObjectValue
operator|)
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getObject
argument_list|()
operator|instanceof
name|RequestWrapper
condition|)
block|{
name|RequestWrapper
name|request
init|=
operator|(
name|RequestWrapper
operator|)
name|value
operator|.
name|getObject
argument_list|()
decl_stmt|;
comment|//if the content length is unknown, return
if|if
condition|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
comment|//first, get the content of the request
name|byte
index|[]
name|bufRequestData
init|=
literal|null
decl_stmt|;
try|try
block|{
name|InputStream
name|is
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|ByteArrayOutputStream
name|bos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|(
name|request
operator|.
name|getContentLength
argument_list|()
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|256
index|]
decl_stmt|;
name|int
name|l
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|l
operator|=
name|is
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
name|bufRequestData
operator|=
name|bos
operator|.
name|toByteArray
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
name|getASTNode
argument_list|()
argument_list|,
literal|"An IO exception ocurred: "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
comment|//was there any POST content
if|if
condition|(
name|bufRequestData
operator|!=
literal|null
condition|)
block|{
comment|//determine if exists mime database considers this binary data
name|String
name|contentType
init|=
name|request
operator|.
name|getContentType
argument_list|()
decl_stmt|;
if|if
condition|(
name|contentType
operator|!=
literal|null
condition|)
block|{
comment|//strip off any charset encoding info
if|if
condition|(
name|contentType
operator|.
name|indexOf
argument_list|(
literal|";"
argument_list|)
operator|>
operator|-
literal|1
condition|)
name|contentType
operator|=
name|contentType
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|contentType
operator|.
name|indexOf
argument_list|(
literal|";"
argument_list|)
argument_list|)
expr_stmt|;
name|MimeType
name|mimeType
init|=
name|MimeTable
operator|.
name|getInstance
argument_list|()
operator|.
name|getContentType
argument_list|(
name|contentType
argument_list|)
decl_stmt|;
if|if
condition|(
name|mimeType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|mimeType
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
comment|//binary data
return|return
operator|new
name|Base64Binary
argument_list|(
name|bufRequestData
argument_list|)
return|;
block|}
block|}
block|}
comment|//try and parse as an XML documemnt, otherwise fallback to returning the data as a string
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
comment|//try and construct xml document from input stream, we use eXist's in-memory DOM implementation
name|SAXParserFactory
name|factory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|factory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//TODO : we should be able to cope with context.getBaseURI()
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|ByteArrayInputStream
argument_list|(
name|bufRequestData
argument_list|)
argument_list|)
decl_stmt|;
name|SAXParser
name|parser
init|=
name|factory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|parser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|receiver
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|receiver
operator|.
name|getDocument
argument_list|()
decl_stmt|;
return|return
operator|(
name|NodeValue
operator|)
name|doc
operator|.
name|getDocumentElement
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
comment|//do nothing, we will default to trying to return a string below
block|}
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
comment|//do nothing, we will default to trying to return a string below
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//do nothing, we will default to trying to return a string below
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
comment|//not a valid XML document, return a string representation of the document
name|String
name|encoding
init|=
name|request
operator|.
name|getCharacterEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
block|{
name|encoding
operator|=
literal|"UTF-8"
expr_stmt|;
block|}
try|try
block|{
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|bufRequestData
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|s
argument_list|)
return|;
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
literal|"An IO exception ocurred: "
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
block|{
comment|//no post data
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Variable $request is not bound to a Request object."
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

