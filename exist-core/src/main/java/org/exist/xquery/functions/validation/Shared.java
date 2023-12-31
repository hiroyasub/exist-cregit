begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|validation
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
name|FileInputStream
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
name|URL
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
name|Iterator
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|stream
operator|.
name|StreamSource
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
name|persistent
operator|.
name|NodeProxy
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
name|dom
operator|.
name|memtree
operator|.
name|NodeImpl
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
name|validation
operator|.
name|ValidationReport
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|ValidationReportItem
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|internal
operator|.
name|node
operator|.
name|NodeInputStream
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
name|Base64BinaryDocument
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
name|BinaryValue
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
name|helpers
operator|.
name|AttributesImpl
import|;
end_import

begin_comment
comment|/**  *  Shared methods for validation functions.  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|Shared
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Shared
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|simplereportText
init|=
literal|"true() if the "
operator|+
literal|"document is valid and no single problem occured, false() for "
operator|+
literal|"all other conditions. For detailed validation information "
operator|+
literal|"use the corresponding -report() function."
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|xmlreportText
init|=
literal|"a validation report."
decl_stmt|;
comment|/**      *  Get input stream for specified resource      * @param s The item      * @param context Xquery context      * @return Inputstream containing the item      * @throws XPathException An error occurred.      * @throws IOException An I/O error occurred.      */
specifier|public
specifier|static
name|InputStream
name|getInputStream
parameter_list|(
name|Item
name|s
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|StreamSource
name|streamSource
init|=
name|getStreamSource
argument_list|(
name|s
argument_list|,
name|context
argument_list|)
decl_stmt|;
return|return
name|streamSource
operator|.
name|getInputStream
argument_list|()
return|;
block|}
comment|/**      *  Get stream source for specified resource, containing InputStream and      * location. Used by @see Jaxv.      * @param s The sequence      * @param context xquery context      * @return Streamsources      * @throws XPathException An error occurred.      * @throws IOException An I/O error occurred.      */
specifier|public
specifier|static
name|StreamSource
index|[]
name|getStreamSource
parameter_list|(
name|Sequence
name|s
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|ArrayList
argument_list|<
name|StreamSource
argument_list|>
name|sources
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|i
init|=
name|s
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|StreamSource
name|streamsource
init|=
name|getStreamSource
argument_list|(
name|next
argument_list|,
name|context
argument_list|)
decl_stmt|;
name|sources
operator|.
name|add
argument_list|(
name|streamsource
argument_list|)
expr_stmt|;
block|}
name|StreamSource
name|returnSources
index|[]
init|=
operator|new
name|StreamSource
index|[
name|sources
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|returnSources
operator|=
name|sources
operator|.
name|toArray
argument_list|(
name|returnSources
argument_list|)
expr_stmt|;
return|return
name|returnSources
return|;
block|}
specifier|public
specifier|static
name|StreamSource
name|getStreamSource
parameter_list|(
name|Item
name|item
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|StreamSource
name|streamSource
init|=
operator|new
name|StreamSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|JAVA_OBJECT
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Streaming Java object"
argument_list|)
expr_stmt|;
specifier|final
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Passed java object should be a File"
argument_list|)
throw|;
block|}
specifier|final
name|File
name|inputFile
init|=
operator|(
name|File
operator|)
name|obj
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FileInputStream
argument_list|(
name|inputFile
argument_list|)
decl_stmt|;
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|streamSource
operator|.
name|setSystemId
argument_list|(
name|inputFile
operator|.
name|toURI
argument_list|()
operator|.
name|toURL
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_URI
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Streaming xs:anyURI"
argument_list|)
expr_stmt|;
comment|// anyURI provided
name|String
name|url
init|=
name|item
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// Fix URL
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|=
literal|"xmldb:exist://"
operator|+
name|url
expr_stmt|;
block|}
specifier|final
name|InputStream
name|is
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
operator|.
name|openStream
argument_list|()
decl_stmt|;
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|streamSource
operator|.
name|setSystemId
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ELEMENT
operator|||
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Streaming element or document node"
argument_list|)
expr_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
specifier|final
name|NodeProxy
name|np
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
specifier|final
name|String
name|url
init|=
literal|"xmldb:exist://"
operator|+
name|np
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getBaseURI
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document detected, adding URL "
operator|+
name|url
argument_list|)
expr_stmt|;
name|streamSource
operator|.
name|setSystemId
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
comment|// Node provided
specifier|final
name|Serializer
name|serializer
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|newSerializer
argument_list|()
decl_stmt|;
specifier|final
name|NodeValue
name|node
init|=
operator|(
name|NodeValue
operator|)
name|item
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
operator|new
name|NodeInputStream
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|,
name|serializer
argument_list|,
name|node
argument_list|)
decl_stmt|;
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BASE64_BINARY
operator|||
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|HEX_BINARY
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Streaming base64 binary"
argument_list|)
expr_stmt|;
specifier|final
name|BinaryValue
name|binary
init|=
operator|(
name|BinaryValue
operator|)
name|item
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|binary
operator|.
name|toJavaObject
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
decl_stmt|;
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|data
argument_list|)
decl_stmt|;
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
comment|//TODO consider using BinaryValue.getInputStream()
if|if
condition|(
name|item
operator|instanceof
name|Base64BinaryDocument
condition|)
block|{
specifier|final
name|Base64BinaryDocument
name|b64doc
init|=
operator|(
name|Base64BinaryDocument
operator|)
name|item
decl_stmt|;
specifier|final
name|String
name|url
init|=
literal|"xmldb:exist://"
operator|+
name|b64doc
operator|.
name|getUrl
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Base64BinaryDocument detected, adding URL "
operator|+
name|url
argument_list|)
expr_stmt|;
name|streamSource
operator|.
name|setSystemId
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Wrong item type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"wrong item type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|streamSource
return|;
block|}
comment|/**      *  Get input source for item. Used by @see Jing.      *      *  @param s The item      *  @param context xquery context      *  @return Inputsource      *  @throws XPathException An error occurred.      *  @throws IOException An I/O error occurred.      */
specifier|public
specifier|static
name|InputSource
name|getInputSource
parameter_list|(
name|Item
name|s
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|StreamSource
name|streamSource
init|=
name|getStreamSource
argument_list|(
name|s
argument_list|,
name|context
argument_list|)
decl_stmt|;
specifier|final
name|InputSource
name|inputSource
init|=
operator|new
name|InputSource
argument_list|()
decl_stmt|;
name|inputSource
operator|.
name|setByteStream
argument_list|(
name|streamSource
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|inputSource
operator|.
name|setSystemId
argument_list|(
name|streamSource
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|inputSource
return|;
block|}
specifier|public
specifier|static
name|StreamSource
name|getStreamSource
parameter_list|(
name|InputSource
name|in
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
specifier|final
name|StreamSource
name|streamSource
init|=
operator|new
name|StreamSource
argument_list|()
decl_stmt|;
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|in
operator|.
name|getByteStream
argument_list|()
argument_list|)
expr_stmt|;
name|streamSource
operator|.
name|setSystemId
argument_list|(
name|in
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|streamSource
return|;
block|}
comment|/**      *  Get URL value of item.      * @param item Item      * @return URL of item      * @throws XPathException Item has no URL.      */
specifier|public
specifier|static
name|String
name|getUrl
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|url
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ANY_URI
comment|/*|| item.getType() != Type.STRING */
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converting anyURI"
argument_list|)
expr_stmt|;
name|url
operator|=
name|item
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOCUMENT
operator|||
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Retreiving URL from (document) node"
argument_list|)
expr_stmt|;
if|if
condition|(
name|item
operator|instanceof
name|NodeProxy
condition|)
block|{
specifier|final
name|NodeProxy
name|np
init|=
operator|(
name|NodeProxy
operator|)
name|item
decl_stmt|;
name|url
operator|=
name|np
operator|.
name|getOwnerDocument
argument_list|()
operator|.
name|getBaseURI
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Document detected, adding URL "
operator|+
name|url
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|url
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Parameter should be of type xs:anyURI or document."
argument_list|)
throw|;
block|}
if|if
condition|(
name|url
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|url
operator|=
literal|"xmldb:exist://"
operator|+
name|url
expr_stmt|;
block|}
return|return
name|url
return|;
block|}
comment|/**      * Get URL values of sequence items.      *      * @param s Sequence      * @return URLs of items in sequence      * @throws XPathException Thrown when an item does not have an associated URL.      */
specifier|public
specifier|static
name|String
index|[]
name|getUrls
parameter_list|(
name|Sequence
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|ArrayList
argument_list|<
name|String
argument_list|>
name|urls
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|final
name|SequenceIterator
name|i
init|=
name|s
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
specifier|final
name|String
name|url
init|=
name|getUrl
argument_list|(
name|next
argument_list|)
decl_stmt|;
name|urls
operator|.
name|add
argument_list|(
name|url
argument_list|)
expr_stmt|;
block|}
name|String
name|returnUrls
index|[]
init|=
operator|new
name|String
index|[
name|urls
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|returnUrls
operator|=
name|urls
operator|.
name|toArray
argument_list|(
name|returnUrls
argument_list|)
expr_stmt|;
return|return
name|returnUrls
return|;
block|}
comment|/**      * Create validation report.      * @param report The validation report data.      * @param builder Helperclass to create in memory XML.      * @return Validation report as node.      */
specifier|static
specifier|public
name|NodeImpl
name|writeReport
parameter_list|(
name|ValidationReport
name|report
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
block|{
comment|// start root element
specifier|final
name|int
name|nodeNr
init|=
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"report"
argument_list|,
literal|"report"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
comment|// validation status: valid or invalid
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"status"
argument_list|,
literal|"status"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|report
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|builder
operator|.
name|characters
argument_list|(
literal|"valid"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|builder
operator|.
name|characters
argument_list|(
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// namespace when available
if|if
condition|(
name|report
operator|.
name|getNamespaceUri
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"namespace"
argument_list|,
literal|"namespace"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|report
operator|.
name|getNamespaceUri
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|// validation duration
specifier|final
name|AttributesImpl
name|durationAttribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
name|durationAttribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"unit"
argument_list|,
literal|"unit"
argument_list|,
literal|"CDATA"
argument_list|,
literal|"msec"
argument_list|)
expr_stmt|;
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"duration"
argument_list|,
literal|"duration"
argument_list|,
name|durationAttribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
literal|""
operator|+
name|report
operator|.
name|getValidationDuration
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// print exceptions if any
if|if
condition|(
name|report
operator|.
name|getThrowable
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"exception"
argument_list|,
literal|"exception"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
specifier|final
name|String
name|className
init|=
name|report
operator|.
name|getThrowable
argument_list|()
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"class"
argument_list|,
literal|"class"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|className
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|message
init|=
name|report
operator|.
name|getThrowable
argument_list|()
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"message"
argument_list|,
literal|"message"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|stacktrace
init|=
name|report
operator|.
name|getStackTrace
argument_list|()
decl_stmt|;
if|if
condition|(
name|stacktrace
operator|!=
literal|null
condition|)
block|{
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"stacktrace"
argument_list|,
literal|"stacktrace"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|stacktrace
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|// reusable attributes
specifier|final
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
comment|// iterate validation report items, write message
for|for
control|(
specifier|final
name|ValidationReportItem
name|vri
range|:
name|report
operator|.
name|getValidationReportItemList
argument_list|()
control|)
block|{
comment|// construct attributes
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"level"
argument_list|,
literal|"level"
argument_list|,
literal|"CDATA"
argument_list|,
name|vri
operator|.
name|getTypeText
argument_list|()
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"line"
argument_list|,
literal|"line"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|vri
operator|.
name|getLineNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"column"
argument_list|,
literal|"column"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|vri
operator|.
name|getColumnNumber
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|vri
operator|.
name|getRepeat
argument_list|()
operator|>
literal|1
condition|)
block|{
name|attribs
operator|.
name|addAttribute
argument_list|(
literal|""
argument_list|,
literal|"repeat"
argument_list|,
literal|"repeat"
argument_list|,
literal|"CDATA"
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|vri
operator|.
name|getRepeat
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|// write message
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"message"
argument_list|,
literal|"message"
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
name|builder
operator|.
name|characters
argument_list|(
name|vri
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// Reuse attributes
name|attribs
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
comment|// finish root element
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
comment|// return result
return|return
name|builder
operator|.
name|getDocument
argument_list|()
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
comment|/**      * Safely close the input source and underlying inputstream.      * @param source The inputsource.      */
specifier|public
specifier|static
name|void
name|closeInputSource
parameter_list|(
name|InputSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|InputStream
name|is
init|=
name|source
operator|.
name|getByteStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem while closing inputstream. ("
operator|+
name|getDetails
argument_list|(
name|source
argument_list|)
operator|+
literal|") "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Safely close the stream source and underlying inputstream.      * @param source The stream source.      */
specifier|public
specifier|static
name|void
name|closeStreamSource
parameter_list|(
name|StreamSource
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
condition|)
block|{
return|return;
block|}
specifier|final
name|InputStream
name|is
init|=
name|source
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
return|return;
block|}
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
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem while closing inputstream. ("
operator|+
name|getDetails
argument_list|(
name|source
argument_list|)
operator|+
literal|") "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Safely close the stream sources and underlying inputstreams.      * @param sources Streamsources.      */
specifier|public
specifier|static
name|void
name|closeStreamSources
parameter_list|(
name|StreamSource
name|sources
index|[]
parameter_list|)
block|{
if|if
condition|(
name|sources
operator|==
literal|null
condition|)
block|{
return|return;
block|}
for|for
control|(
specifier|final
name|StreamSource
name|source
range|:
name|sources
control|)
block|{
name|closeStreamSource
argument_list|(
name|source
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|String
name|getDetails
parameter_list|(
name|InputSource
name|source
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"PublicId='"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'  "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|.
name|getSystemId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"SystemId='"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'  "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|.
name|getEncoding
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"Encoding='"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source
operator|.
name|getEncoding
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'  "
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|String
name|getDetails
parameter_list|(
name|StreamSource
name|source
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|source
operator|.
name|getPublicId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"PublicId='"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'  "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|source
operator|.
name|getSystemId
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"SystemId='"
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|source
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|"'  "
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

