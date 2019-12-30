begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|input
operator|.
name|CloseShieldInputStream
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
name|Namespaces
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
name|dom
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
name|Configuration
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
name|io
operator|.
name|CachingFilterInputStream
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
name|util
operator|.
name|io
operator|.
name|FilterInputStreamCache
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
name|FilterInputStreamCacheFactory
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
name|FilterInputStreamCacheFactory
operator|.
name|FilterInputStreamCacheConfiguration
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author<a href="mailto:adam@exist-db.org">Adam retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|GetData
extends|extends
name|StrictRequestFunction
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
name|GetData
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"Returns the content of a POST request. "
operator|+
literal|"If the HTTP Content-Type header in the request identifies it as a binary document, then xs:base64Binary is returned. "
operator|+
literal|"If its not a binary document, we attempt to parse it as XML and return a document-node(). "
operator|+
literal|"If its not a binary or XML document, any other data type is returned as an xs:string representation or "
operator|+
literal|"an empty sequence if there is no data to be read."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"the content of a POST request"
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|GetData
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
annotation|@
name|Nonnull
specifier|final
name|RequestWrapper
name|request
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//if the content length is unknown or 0, return
if|if
condition|(
name|request
operator|.
name|getContentLength
argument_list|()
operator|<=
literal|0
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|InputStream
name|isRequest
init|=
literal|null
decl_stmt|;
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
try|try
block|{
name|isRequest
operator|=
name|request
operator|.
name|getInputStream
argument_list|()
expr_stmt|;
comment|//was there any POST content?
comment|/**              * There is a bug in HttpInput.available() in Jetty 7.2.2.v20101205              * This has been filed as Bug 333415 -              * https://bugs.eclipse.org/bugs/show_bug.cgi?id=333415 It is              * expected to be fixed in the Jetty 7.3.0 release              */
comment|//TODO reinstate call to .available() when Jetty 7.3.0 is released, use of .getContentLength() is not reliable because of http mechanics
comment|//if(is != null&& is.available()> 0) {
if|if
condition|(
name|isRequest
operator|!=
literal|null
operator|&&
name|request
operator|.
name|getContentLength
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// 1) determine if exists mime database considers this binary data
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
block|{
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
block|}
specifier|final
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
operator|&&
operator|!
name|mimeType
operator|.
name|isXMLType
argument_list|()
condition|)
block|{
comment|//binary data
name|result
operator|=
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
name|isRequest
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|result
operator|==
name|Sequence
operator|.
name|EMPTY_SEQUENCE
condition|)
block|{
comment|//2) not binary, try and parse as an XML documemnt, otherwise 3) return a string representation
comment|//parsing will consume the stream so we must cache!
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
name|FilterInputStreamCache
name|cache
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//we have to cache the input stream, so we can reread it, as we may use it twice (once for xml attempt and once for string attempt)
name|cache
operator|=
name|FilterInputStreamCacheFactory
operator|.
name|getCacheInstance
argument_list|(
operator|new
name|FilterInputStreamCacheConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getCacheClass
parameter_list|()
block|{
return|return
operator|(
name|String
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
name|Configuration
operator|.
name|BINARY_CACHE_CLASS_PROPERTY
argument_list|)
return|;
block|}
block|}
argument_list|,
name|isRequest
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|CachingFilterInputStream
argument_list|(
name|cache
argument_list|)
expr_stmt|;
comment|//mark the start of the stream
name|is
operator|.
name|mark
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
comment|//2) try and  parse as XML
name|result
operator|=
name|parseAsXml
argument_list|(
name|is
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|==
name|Sequence
operator|.
name|EMPTY_SEQUENCE
condition|)
block|{
comment|// 3) not a valid XML document, return a string representation of the document
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
comment|//reset the stream, as we need to reuse for string parsing after the XML parsing happened
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
name|result
operator|=
name|parseAsString
argument_list|(
name|is
argument_list|,
name|encoding
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
literal|"An IO exception occurred: "
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
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cache
operator|.
name|invalidate
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
comment|//NOTE we do not close isRequest, because it may be needed further by the caching input stream wrapper
block|}
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
literal|"An IO exception occurred: "
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
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|parseAsXml
parameter_list|(
name|InputStream
name|is
parameter_list|)
block|{
name|Sequence
name|result
init|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
decl_stmt|;
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
comment|//try and construct xml document from input stream, we use eXist's in-memory DOM implementation
comment|//we have to use CloseShieldInputStream otherwise the parser closes the stream and we cant later reread
specifier|final
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|CloseShieldInputStream
argument_list|(
name|is
argument_list|)
argument_list|)
decl_stmt|;
name|reader
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
operator|.
name|borrowXMLReader
argument_list|()
expr_stmt|;
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
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
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
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
specifier|final
name|Document
name|doc
init|=
name|receiver
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|NodeValue
operator|)
name|doc
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
decl||
name|IOException
name|saxe
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
if|if
condition|(
name|reader
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getParserPool
argument_list|()
operator|.
name|returnXMLReader
argument_list|(
name|reader
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|parseAsString
parameter_list|(
name|InputStream
name|is
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|bos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|bos
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|bos
operator|.
name|toString
argument_list|(
name|encoding
argument_list|)
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

