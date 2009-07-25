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
name|Logger
operator|.
name|getLogger
argument_list|(
name|Shared
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      *  Get input stream for specified resource.      */
specifier|public
specifier|static
name|InputStream
name|getInputStream
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
name|MalformedURLException
throws|,
name|IOException
block|{
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
comment|/**      *  Get stream source for specified resource, containing InputStream and       * location. Used by @see Jaxv.      */
specifier|public
specifier|static
name|StreamSource
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
name|MalformedURLException
throws|,
name|IOException
block|{
name|StreamSource
name|streamSource
init|=
operator|new
name|StreamSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|.
name|getItemType
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
name|Item
name|item
init|=
name|s
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
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
name|File
name|inputFile
init|=
operator|(
name|File
operator|)
name|obj
decl_stmt|;
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
name|s
operator|.
name|getItemType
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
name|s
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
name|s
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|ELEMENT
operator|||
name|s
operator|.
name|getItemType
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
comment|// Node provided
name|InputStream
name|is
init|=
operator|new
name|NodeInputStream
argument_list|(
name|context
argument_list|,
name|s
operator|.
name|iterate
argument_list|()
argument_list|)
decl_stmt|;
comment|// new NodeInputStream()
name|streamSource
operator|.
name|setInputStream
argument_list|(
name|is
argument_list|)
expr_stmt|;
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
name|s
operator|.
name|getItemType
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
name|s
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
return|return
name|streamSource
return|;
block|}
comment|/**      *  Get input source for specified resource, containing inputStream and       * location. Used by @see Jing.      */
specifier|public
specifier|static
name|InputSource
name|getInputSource
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
name|MalformedURLException
throws|,
name|IOException
block|{
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
comment|/**      *  Get URL value of parameter.      */
specifier|public
specifier|static
name|String
name|getUrl
parameter_list|(
name|Sequence
name|s
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|s
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|ANY_URI
operator|&&
name|s
operator|.
name|getItemType
argument_list|()
operator|!=
name|Type
operator|.
name|STRING
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Parameter should be of type xs:anyURI"
operator|+
literal|" or string"
argument_list|)
throw|;
block|}
name|String
name|url
init|=
name|s
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
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
comment|/**      * Create validation report.      */
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
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"time"
argument_list|,
literal|"time"
argument_list|,
literal|null
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
name|builder
operator|.
name|startElement
argument_list|(
literal|""
argument_list|,
literal|"message"
argument_list|,
literal|"exception"
argument_list|,
literal|null
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
name|getThrowable
argument_list|()
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
literal|""
operator|+
name|report
operator|.
name|getStackTrace
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
name|builder
operator|.
name|endElement
argument_list|()
expr_stmt|;
block|}
comment|// reusable attributes
name|AttributesImpl
name|attribs
init|=
operator|new
name|AttributesImpl
argument_list|()
decl_stmt|;
comment|// iterate validation report items, write message
name|List
name|cr
init|=
name|report
operator|.
name|getValidationReportItemList
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|cr
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|ValidationReportItem
name|vri
init|=
operator|(
name|ValidationReportItem
operator|)
name|iter
operator|.
name|next
argument_list|()
decl_stmt|;
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
operator|(
operator|(
name|DocumentImpl
operator|)
name|builder
operator|.
name|getDocument
argument_list|()
operator|)
operator|.
name|getNode
argument_list|(
name|nodeNr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

