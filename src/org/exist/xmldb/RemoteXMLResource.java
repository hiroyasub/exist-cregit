begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|StringReader
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
name|Date
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
import|import
name|java
operator|.
name|util
operator|.
name|Vector
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
name|OutputKeys
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
name|security
operator|.
name|Permission
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
name|DOMSerializerPool
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

begin_class
specifier|public
class|class
name|RemoteXMLResource
implements|implements
name|XMLResource
implements|,
name|EXistResource
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
comment|/** 	 *  if this class is used from Cocoon, use the Cocoon parser component 	 *  instead of JAXP 	 */
specifier|private
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParser
name|cocoonParser
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|id
decl_stmt|;
specifier|protected
name|String
name|documentName
decl_stmt|;
specifier|protected
name|String
name|path
init|=
literal|null
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
specifier|protected
name|RemoteCollection
name|parent
decl_stmt|;
specifier|protected
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|protected
name|File
name|file
init|=
literal|null
decl_stmt|;
specifier|protected
name|Permission
name|permissions
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|contentLen
init|=
literal|0
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
specifier|public
name|RemoteXMLResource
parameter_list|(
name|RemoteCollection
name|parent
parameter_list|,
name|String
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
name|String
name|docId
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|int
name|p
decl_stmt|;
if|if
condition|(
name|docId
operator|!=
literal|null
operator|&&
operator|(
name|p
operator|=
name|docId
operator|.
name|lastIndexOf
argument_list|(
literal|'/'
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|path
operator|=
name|docId
expr_stmt|;
name|documentName
operator|=
name|docId
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|parent
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|docId
expr_stmt|;
name|documentName
operator|=
name|docId
expr_stmt|;
block|}
block|}
specifier|public
name|Date
name|getCreationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Date
operator|)
operator|(
operator|(
name|Vector
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getTimestamps"
argument_list|,
name|params
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|0
argument_list|)
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
catch|catch
parameter_list|(
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
name|Date
name|getLastModificationTime
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|(
name|Date
operator|)
operator|(
operator|(
name|Vector
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getTimestamps"
argument_list|,
name|params
argument_list|)
operator|)
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
catch|catch
parameter_list|(
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
return|return
name|content
return|;
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
return|return
name|file
return|;
name|Properties
name|properties
init|=
name|parent
operator|.
name|getProperties
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|properties
argument_list|)
expr_stmt|;
comment|//params.addElement("UTF-8");
comment|//params.addElement(new Integer(1));
try|try
block|{
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"getDocument"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
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
else|else
block|{
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|handle
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|properties
argument_list|)
expr_stmt|;
try|try
block|{
name|data
operator|=
operator|(
name|byte
index|[]
operator|)
name|parent
operator|.
name|getClient
argument_list|()
operator|.
name|execute
argument_list|(
literal|"retrieve"
argument_list|,
name|params
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XmlRpcException
name|xre
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
name|xre
operator|.
name|getMessage
argument_list|()
argument_list|,
name|xre
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
try|try
block|{
name|content
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|properties
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|ue
parameter_list|)
block|{
name|content
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
return|return
name|content
return|;
block|}
specifier|public
name|Node
name|getContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|content
operator|==
literal|null
condition|)
name|getContent
argument_list|()
expr_stmt|;
comment|// content can be a file
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
name|getData
argument_list|()
expr_stmt|;
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
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
return|return
name|doc
operator|.
name|getDocumentElement
argument_list|()
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
if|if
condition|(
name|content
operator|==
literal|null
condition|)
name|getContent
argument_list|()
expr_stmt|;
comment|//		content can be a file
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
name|getData
argument_list|()
expr_stmt|;
if|if
condition|(
name|cocoonParser
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
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
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
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://xml.org/sax/properties/lexical-handler"
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
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
else|else
try|try
block|{
name|cocoonParser
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|content
argument_list|)
argument_list|)
argument_list|,
name|handler
argument_list|,
name|lexicalHandler
argument_list|)
expr_stmt|;
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
name|getDocumentId
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|documentName
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
name|documentName
return|;
return|return
name|documentName
operator|+
literal|'_'
operator|+
name|id
return|;
block|}
specifier|public
name|Collection
name|getParentCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|parent
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
comment|/** 	 *  Sets the cocoonParser to be used. 	 * 	 *@param  parser  The new cocoonParser value 	 */
specifier|public
name|void
name|setCocoonParser
parameter_list|(
name|org
operator|.
name|apache
operator|.
name|excalibur
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXParser
name|parser
parameter_list|)
block|{
name|this
operator|.
name|cocoonParser
operator|=
name|parser
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
if|if
condition|(
name|value
operator|instanceof
name|File
condition|)
block|{
name|file
operator|=
operator|(
name|File
operator|)
name|value
expr_stmt|;
block|}
else|else
name|content
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
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
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|DOMSerializer
name|xmlout
init|=
name|DOMSerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowDOMSerializer
argument_list|()
decl_stmt|;
name|xmlout
operator|.
name|reset
argument_list|()
expr_stmt|;
name|xmlout
operator|.
name|setOutputProperties
argument_list|(
name|getProperties
argument_list|()
argument_list|)
expr_stmt|;
name|xmlout
operator|.
name|setWriter
argument_list|(
name|sout
argument_list|)
expr_stmt|;
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
name|content
operator|=
name|sout
operator|.
name|toString
argument_list|()
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
name|DOMSerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnDOMSerializer
argument_list|(
name|xmlout
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|ContentHandler
name|setContentAsSAX
parameter_list|()
throws|throws
name|XMLDBException
block|{
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
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
specifier|public
name|InternalXMLSerializer
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|setWriter
argument_list|(
name|writer
argument_list|)
expr_stmt|;
name|setOutputProperties
argument_list|(
name|emptyProperties
argument_list|)
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
name|content
operator|=
name|writer
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.modules.XMLResource#getSAXFeature(java.lang.String) 	 */
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
comment|/* (non-Javadoc) 	 * @see org.xmldb.api.modules.XMLResource#setSAXFeature(java.lang.String, boolean) 	 */
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
comment|/** 	 * Force content to be loaded into mem 	 *  	 * @throws XMLDBException 	 */
specifier|protected
name|byte
index|[]
name|getData
parameter_list|()
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|file
operator|!=
literal|null
condition|)
block|{
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"failed to read resource content from file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
try|try
block|{
name|byte
index|[]
name|chunk
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|FileInputStream
name|in
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|int
name|l
decl_stmt|;
do|do
block|{
name|l
operator|=
name|in
operator|.
name|read
argument_list|(
name|chunk
argument_list|)
expr_stmt|;
if|if
condition|(
name|l
operator|>
literal|0
condition|)
name|out
operator|.
name|write
argument_list|(
name|chunk
argument_list|,
literal|0
argument_list|,
name|l
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|>
operator|-
literal|1
condition|)
do|;
name|in
operator|.
name|close
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|out
operator|.
name|toByteArray
argument_list|()
decl_stmt|;
name|content
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|file
operator|=
literal|null
expr_stmt|;
return|return
name|data
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"failed to read resource content from file "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
if|else if
condition|(
name|content
operator|!=
literal|null
condition|)
try|try
block|{
return|return
name|content
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setContentLength
parameter_list|(
name|int
name|len
parameter_list|)
block|{
name|this
operator|.
name|contentLen
operator|=
name|len
expr_stmt|;
block|}
specifier|public
name|int
name|getContentLength
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|contentLen
return|;
block|}
specifier|public
name|void
name|setPermissions
parameter_list|(
name|Permission
name|perms
parameter_list|)
block|{
name|permissions
operator|=
name|perms
expr_stmt|;
block|}
specifier|public
name|Permission
name|getPermissions
parameter_list|()
block|{
return|return
name|permissions
return|;
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
specifier|private
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
block|}
end_class

end_unit

