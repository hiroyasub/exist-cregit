begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2003-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
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
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
name|DocumentTypeImpl
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
name|DOMSerializer
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
name|w3c
operator|.
name|dom
operator|.
name|DocumentFragment
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
name|DocumentType
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
name|SAXNotRecognizedException
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
name|SAXNotSupportedException
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
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
name|ErrorCodes
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

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
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
name|DocumentBuilderFactory
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|TransformerException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|BufferedOutputStream
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
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|UnsupportedEncodingException
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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteXMLResource
extends|extends
name|AbstractRemoteResource
implements|implements
name|XMLResource
block|{
specifier|private
specifier|final
specifier|static
name|Properties
name|emptyProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
comment|/**      *  Use external XMLReader to parse XML.      */
specifier|private
name|XMLReader
name|xmlReader
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|id
decl_stmt|;
specifier|protected
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|int
name|pos
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|protected
name|Properties
name|outputProperties
init|=
literal|null
decl_stmt|;
specifier|protected
name|LexicalHandler
name|lexicalHandler
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RemoteXMLResource
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|RemoteXMLResource
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|XmldbURI
name|docId
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|this
argument_list|(
name|parent
argument_list|,
operator|-
literal|1
argument_list|,
operator|-
literal|1
argument_list|,
name|docId
argument_list|,
name|id
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RemoteXMLResource
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|int
name|handle
parameter_list|,
name|int
name|pos
parameter_list|,
name|XmldbURI
name|docId
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|super
argument_list|(
name|parent
argument_list|,
name|docId
argument_list|)
expr_stmt|;
name|this
operator|.
name|handle
operator|=
name|handle
expr_stmt|;
name|this
operator|.
name|pos
operator|=
name|pos
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|mimeType
operator|=
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Object
name|getContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|content
argument_list|)
operator|.
name|getStringValue
argument_list|(
literal|true
argument_list|)
return|;
block|}
name|Object
name|res
init|=
name|super
operator|.
name|getContent
argument_list|()
decl_stmt|;
if|if
condition|(
name|res
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|res
operator|instanceof
name|byte
index|[]
condition|)
block|{
try|try
block|{
return|return
operator|new
name|String
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|res
argument_list|,
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|uee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|uee
argument_list|)
throw|;
block|}
block|}
else|else
block|{
return|return
name|res
return|;
block|}
block|}
return|return
literal|null
return|;
comment|// Backward compatible code (perhaps it is not needed?)
comment|/*         if (properties.getProperty(EXistOutputKeys.COMPRESS_OUTPUT, "no").equals("yes")) {             try {                 data = Compressor.uncompress(data);             } catch (IOException e) {                              }         }                  try {             content = new String(data, properties.getProperty(OutputKeys.ENCODING, "UTF-8"));             // fixme! - this should probably be earlier in the chain before serialisation. /ljo             content = new StringValue(content).getStringValue(true);         } catch (UnsupportedEncodingException ue) {             LOG.warn(ue);             content = new String(data);             content = new StringValue(content).getStringValue(true);         }         return content; 	*/
block|}
specifier|public
name|Node
name|getContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|InputSource
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
operator|new
name|InputSource
argument_list|(
name|getStreamContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|DocumentBuilderFactory
name|factory
init|=
name|DocumentBuilderFactory
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
name|factory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|doc
init|=
name|builder
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
comment|//<frederic.glorieux@ajlsm.com> return a full DOM doc, with root PI and comments
return|return
name|doc
return|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
catch|catch
parameter_list|(
name|ParserConfigurationException
name|pce
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|pce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pce
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
specifier|public
name|void
name|getContentAsSAX
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|InputSource
name|is
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|content
operator|!=
literal|null
condition|)
block|{
name|is
operator|=
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|=
operator|new
name|InputSource
argument_list|(
name|getStreamContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|xmlReader
operator|==
literal|null
condition|)
block|{
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
try|try
block|{
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|reader
operator|=
name|sax
operator|.
name|getXMLReader
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParserConfigurationException
name|pce
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|pce
operator|.
name|getMessage
argument_list|()
argument_list|,
name|pce
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|saxe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
block|}
else|else
block|{
name|reader
operator|=
name|xmlReader
expr_stmt|;
block|}
try|try
block|{
name|reader
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
if|if
condition|(
name|lexicalHandler
operator|!=
literal|null
condition|)
block|{
name|reader
operator|.
name|setProperty
argument_list|(
name|Namespaces
operator|.
name|SAX_LEXICAL_HANDLER
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
block|}
name|reader
operator|.
name|parse
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|saxe
parameter_list|)
block|{
name|saxe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|id
operator|==
literal|null
condition|?
literal|"1"
else|:
name|id
return|;
block|}
specifier|public
name|String
name|getDocumentId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|path
operator|.
name|lastSegment
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|id
operator|==
literal|null
operator|||
name|id
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|)
return|return
name|getDocumentId
argument_list|()
return|;
return|return
name|getDocumentId
argument_list|()
operator|+
literal|'_'
operator|+
name|id
return|;
block|}
specifier|public
name|String
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XMLResource"
return|;
block|}
comment|/**      * Sets the external XMLReader to use.      *      * @param xmlReader the XMLReader      */
specifier|public
name|void
name|setXMLReader
parameter_list|(
name|XMLReader
name|xmlReader
parameter_list|)
block|{
name|this
operator|.
name|xmlReader
operator|=
name|xmlReader
expr_stmt|;
block|}
specifier|public
name|void
name|setContent
parameter_list|(
name|Object
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|content
operator|=
literal|null
expr_stmt|;
if|if
condition|(
operator|!
name|super
operator|.
name|setContentInternal
argument_list|(
name|value
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|instanceof
name|String
condition|)
block|{
name|content
operator|=
operator|new
name|String
argument_list|(
operator|(
name|String
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
try|try
block|{
name|content
operator|=
operator|new
name|String
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|uee
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|uee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|uee
argument_list|)
throw|;
block|}
block|}
else|else
block|{
name|content
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|setContentAsDOM
parameter_list|(
name|Node
name|root
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
name|File
name|tmpfile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"eXistRXR"
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
name|tmpfile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|bos
argument_list|,
literal|"UTF-8"
argument_list|)
decl_stmt|;
name|DOMSerializer
name|xmlout
init|=
operator|new
name|DOMSerializer
argument_list|(
name|osw
argument_list|,
name|getProperties
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
switch|switch
condition|(
name|root
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
name|xmlout
operator|.
name|serialize
argument_list|(
operator|(
name|Element
operator|)
name|root
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
case|:
name|xmlout
operator|.
name|serialize
argument_list|(
operator|(
name|DocumentFragment
operator|)
name|root
argument_list|)
expr_stmt|;
break|break;
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
name|xmlout
operator|.
name|serialize
argument_list|(
operator|(
name|Document
operator|)
name|root
argument_list|)
expr_stmt|;
break|break;
default|default :
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"invalid node type"
argument_list|)
throw|;
block|}
name|setContent
argument_list|(
name|tmpfile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
finally|finally
block|{
try|try
block|{
name|osw
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
comment|// IgnoreIT(R)
block|}
try|try
block|{
name|bos
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
comment|// IgnoreIT(R)
block|}
try|try
block|{
name|fos
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
comment|// IgnoreIT(R)
block|}
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
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
specifier|public
name|ContentHandler
name|setContentAsSAX
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|freeLocalResources
argument_list|()
expr_stmt|;
name|content
operator|=
literal|null
expr_stmt|;
return|return
operator|new
name|InternalXMLSerializer
argument_list|()
return|;
block|}
specifier|private
class|class
name|InternalXMLSerializer
extends|extends
name|SAXSerializer
block|{
name|File
name|tmpfile
init|=
literal|null
decl_stmt|;
name|OutputStreamWriter
name|writer
init|=
literal|null
decl_stmt|;
specifier|public
name|InternalXMLSerializer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
name|File
name|tmpfile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"eXistRXR"
argument_list|,
literal|".xml"
argument_list|)
decl_stmt|;
name|tmpfile
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|FileOutputStream
name|fos
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tmpfile
argument_list|)
decl_stmt|;
name|BufferedOutputStream
name|bos
init|=
operator|new
name|BufferedOutputStream
argument_list|(
name|fos
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|bos
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|setOutput
argument_list|(
name|writer
argument_list|,
name|emptyProperties
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
name|SAXException
argument_list|(
literal|"Unable to create temp file for serialization data"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
name|super
operator|.
name|startDocument
argument_list|()
expr_stmt|;
block|}
comment|/** 		 * @see org.xml.sax.DocumentHandler#endDocument() 		 */
specifier|public
name|void
name|endDocument
parameter_list|()
throws|throws
name|SAXException
block|{
name|super
operator|.
name|endDocument
argument_list|()
expr_stmt|;
try|try
block|{
name|setContent
argument_list|(
name|tmpfile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|xe
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Unable to close temp file containing serialized data"
argument_list|,
name|xe
argument_list|)
throw|;
block|}
try|try
block|{
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Unable to close temp file containing serialized data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
comment|/* (non-Javadoc)      * @see org.xmldb.api.modules.XMLResource#getSAXFeature(java.lang.String)      */
specifier|public
name|boolean
name|getSAXFeature
parameter_list|(
name|String
name|arg0
parameter_list|)
throws|throws
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc)      * @see org.xmldb.api.modules.XMLResource#setSAXFeature(java.lang.String, boolean)      */
specifier|public
name|void
name|setSAXFeature
parameter_list|(
name|String
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
throws|throws
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
block|}
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|lexicalHandler
operator|=
name|handler
expr_stmt|;
block|}
specifier|protected
name|void
name|setProperties
parameter_list|(
name|Properties
name|properties
parameter_list|)
block|{
name|this
operator|.
name|outputProperties
operator|=
name|properties
expr_stmt|;
block|}
specifier|protected
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|outputProperties
operator|==
literal|null
condition|?
name|parent
operator|.
name|properties
else|:
name|outputProperties
return|;
block|}
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|DocumentType
name|result
init|=
literal|null
decl_stmt|;
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Object
index|[]
name|request
init|=
literal|null
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|request
operator|=
operator|(
name|Object
index|[]
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getDocType"
argument_list|,
name|params
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|request
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|DocumentTypeImpl
argument_list|(
operator|(
name|String
operator|)
name|request
index|[
literal|0
index|]
argument_list|,
operator|(
name|String
operator|)
name|request
index|[
literal|1
index|]
argument_list|,
operator|(
name|String
operator|)
name|request
index|[
literal|2
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
specifier|public
name|void
name|setDocType
parameter_list|(
name|DocumentType
name|doctype
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|doctype
operator|!=
literal|null
condition|)
block|{
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|4
argument_list|)
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|doctype
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|doctype
operator|.
name|getPublicId
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|doctype
operator|.
name|getPublicId
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|doctype
operator|.
name|getSystemId
argument_list|()
operator|==
literal|null
condition|?
literal|""
else|:
name|doctype
operator|.
name|getSystemId
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"setDocType"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
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
block|}
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|getContentIntoAStreamInternal
argument_list|(
name|os
argument_list|,
name|content
argument_list|,
name|id
operator|!=
literal|null
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Object
name|getExtendedContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getExtendedContentInternal
argument_list|(
name|content
argument_list|,
name|id
operator|!=
literal|null
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|InputStream
name|getStreamContent
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamContentInternal
argument_list|(
name|content
argument_list|,
name|id
operator|!=
literal|null
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
return|;
block|}
specifier|public
name|long
name|getStreamLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|getStreamLengthInternal
argument_list|(
name|content
argument_list|)
return|;
block|}
block|}
end_class

end_unit

