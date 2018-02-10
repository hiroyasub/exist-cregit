begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|persistent
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
name|util
operator|.
name|VirtualTempFile
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
name|annotation
operator|.
name|Nullable
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

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Optional
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
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RemoteXMLResource
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * Use external XMLReader to parse XML.      */
specifier|private
name|XMLReader
name|xmlReader
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
name|id
decl_stmt|;
specifier|private
specifier|final
name|int
name|handle
decl_stmt|;
specifier|private
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
specifier|private
name|Properties
name|outputProperties
init|=
literal|null
decl_stmt|;
specifier|private
name|LexicalHandler
name|lexicalHandler
init|=
literal|null
decl_stmt|;
specifier|public
name|RemoteXMLResource
parameter_list|(
specifier|final
name|RemoteCollection
name|parent
parameter_list|,
specifier|final
name|XmldbURI
name|docId
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
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
specifier|final
name|RemoteCollection
name|parent
parameter_list|,
specifier|final
name|int
name|handle
parameter_list|,
specifier|final
name|int
name|pos
parameter_list|,
specifier|final
name|XmldbURI
name|docId
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|String
argument_list|>
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
argument_list|,
name|MimeType
operator|.
name|XML_TYPE
operator|.
name|getName
argument_list|()
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
block|}
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|id
operator|.
name|map
argument_list|(
name|x
lambda|->
name|x
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|?
name|getDocumentId
argument_list|()
else|:
name|getDocumentId
argument_list|()
operator|+
literal|'_'
operator|+
name|id
argument_list|)
operator|.
name|orElse
argument_list|(
name|getDocumentId
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getResourceType
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|XMLResource
operator|.
name|RESOURCE_TYPE
return|;
block|}
annotation|@
name|Override
annotation|@
name|Nullable
specifier|public
name|Properties
name|getProperties
parameter_list|()
block|{
return|return
name|outputProperties
operator|==
literal|null
condition|?
name|super
operator|.
name|getProperties
argument_list|()
else|:
name|outputProperties
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperties
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|String
name|getDocumentId
parameter_list|()
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
annotation|@
name|Override
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
specifier|final
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
name|UTF_8
argument_list|)
return|;
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
block|}
annotation|@
name|Override
specifier|public
name|Node
name|getContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|InputSource
name|is
decl_stmt|;
name|InputStream
name|cis
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|cis
operator|=
name|getStreamContent
argument_list|()
expr_stmt|;
name|is
operator|=
operator|new
name|InputSource
argument_list|(
name|cis
argument_list|)
expr_stmt|;
block|}
specifier|final
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
specifier|final
name|DocumentBuilder
name|builder
init|=
name|factory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
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
specifier|final
name|SAXException
decl||
name|IOException
decl||
name|ParserConfigurationException
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
if|if
condition|(
name|cis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cis
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
name|warn
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
annotation|@
name|Override
specifier|public
name|void
name|getContentAsSAX
parameter_list|(
specifier|final
name|ContentHandler
name|handler
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|InputSource
name|is
decl_stmt|;
name|InputStream
name|cis
init|=
literal|null
decl_stmt|;
try|try
block|{
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
name|cis
operator|=
name|getStreamContent
argument_list|()
expr_stmt|;
name|is
operator|=
operator|new
name|InputSource
argument_list|(
name|cis
argument_list|)
expr_stmt|;
block|}
name|XMLReader
name|reader
init|=
name|xmlReader
decl_stmt|;
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
specifier|final
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
specifier|final
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
specifier|final
name|ParserConfigurationException
decl||
name|SAXException
decl||
name|IOException
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
if|if
condition|(
name|cis
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|cis
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
name|warn
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
annotation|@
name|Override
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
name|idIsPresent
argument_list|()
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setContent
parameter_list|(
specifier|final
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
operator|(
name|String
operator|)
name|value
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
name|UTF_8
argument_list|)
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|void
name|setContentAsDOM
parameter_list|(
specifier|final
name|Node
name|root
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|VirtualTempFile
name|vtmpfile
init|=
operator|new
name|VirtualTempFile
argument_list|()
decl_stmt|;
name|vtmpfile
operator|.
name|setTempPrefix
argument_list|(
literal|"eXistRXR"
argument_list|)
expr_stmt|;
name|vtmpfile
operator|.
name|setTempPostfix
argument_list|(
literal|".xml"
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|OutputStreamWriter
name|osw
init|=
operator|new
name|OutputStreamWriter
argument_list|(
name|vtmpfile
argument_list|,
literal|"UTF-8"
argument_list|)
init|)
block|{
specifier|final
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
specifier|final
name|short
name|type
init|=
name|root
operator|.
name|getNodeType
argument_list|()
decl_stmt|;
if|if
condition|(
name|type
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|DOCUMENT_FRAGMENT_NODE
operator|||
name|type
operator|==
name|Node
operator|.
name|DOCUMENT_NODE
condition|)
block|{
name|xmlout
operator|.
name|serialize
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
else|else
block|{
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
block|}
finally|finally
block|{
try|try
block|{
name|vtmpfile
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
name|warn
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
name|setContent
argument_list|(
name|vtmpfile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TransformerException
decl||
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
annotation|@
name|Override
specifier|public
name|ContentHandler
name|setContentAsSAX
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|freeResources
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
specifier|public
name|boolean
name|idIsPresent
parameter_list|()
block|{
return|return
name|id
operator|.
name|isPresent
argument_list|()
return|;
block|}
specifier|public
name|String
name|getNodeId
parameter_list|()
block|{
return|return
name|id
operator|.
name|orElse
argument_list|(
literal|"1"
argument_list|)
return|;
block|}
comment|/**      * Sets the external XMLReader to use.      *      * @param xmlReader the XMLReader      */
specifier|public
name|void
name|setXMLReader
parameter_list|(
specifier|final
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
specifier|private
class|class
name|InternalXMLSerializer
extends|extends
name|SAXSerializer
block|{
name|VirtualTempFile
name|vtmpfile
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
annotation|@
name|Override
specifier|public
name|void
name|startDocument
parameter_list|()
throws|throws
name|SAXException
block|{
try|try
block|{
name|vtmpfile
operator|=
operator|new
name|VirtualTempFile
argument_list|()
expr_stmt|;
name|vtmpfile
operator|.
name|setTempPrefix
argument_list|(
literal|"eXistRXR"
argument_list|)
expr_stmt|;
name|vtmpfile
operator|.
name|setTempPostfix
argument_list|(
literal|".xml"
argument_list|)
expr_stmt|;
name|writer
operator|=
operator|new
name|OutputStreamWriter
argument_list|(
name|vtmpfile
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|setOutput
argument_list|(
name|writer
argument_list|,
operator|new
name|Properties
argument_list|()
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
annotation|@
name|Override
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
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|vtmpfile
operator|!=
literal|null
condition|)
block|{
name|vtmpfile
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|setContent
argument_list|(
name|vtmpfile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
decl||
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
literal|"Unable to set file content containing serialized data"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|getSAXFeature
parameter_list|(
specifier|final
name|String
name|name
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
annotation|@
name|Override
specifier|public
name|void
name|setSAXFeature
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|boolean
name|value
parameter_list|)
throws|throws
name|SAXNotRecognizedException
throws|,
name|SAXNotSupportedException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|setLexicalHandler
parameter_list|(
specifier|final
name|LexicalHandler
name|handler
parameter_list|)
block|{
name|this
operator|.
name|lexicalHandler
operator|=
name|handler
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentType
name|getDocType
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|List
name|params
init|=
operator|new
name|ArrayList
argument_list|(
literal|1
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
try|try
block|{
specifier|final
name|Object
index|[]
name|request
init|=
operator|(
name|Object
index|[]
operator|)
name|collection
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
decl_stmt|;
specifier|final
name|DocumentType
name|result
decl_stmt|;
if|if
condition|(
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|request
index|[
literal|0
index|]
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
else|else
block|{
name|result
operator|=
literal|null
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|setDocType
parameter_list|(
specifier|final
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
specifier|final
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
name|collection
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
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|getContentIntoAStream
parameter_list|(
specifier|final
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
name|idIsPresent
argument_list|()
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
name|idIsPresent
argument_list|()
argument_list|,
name|handle
argument_list|,
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
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

