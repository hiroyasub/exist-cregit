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
name|File
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
name|org
operator|.
name|apache
operator|.
name|cocoon
operator|.
name|components
operator|.
name|parser
operator|.
name|Parser
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|XMLUtil
import|;
end_import

begin_class
specifier|public
class|class
name|XMLResourceImpl
implements|implements
name|XMLResource
block|{
comment|/** 	 *  if this class is used from Cocoon, use the Cocoon parser component 	 *  instead of JAXP 	 */
specifier|private
name|Parser
name|cocoonParser
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|encoding
init|=
literal|"UTF-8"
decl_stmt|;
specifier|protected
name|String
name|id
decl_stmt|,
name|documentName
decl_stmt|,
name|path
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|indent
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|CollectionImpl
name|parent
decl_stmt|;
specifier|protected
name|boolean
name|saxDocEvents
init|=
literal|true
decl_stmt|;
specifier|protected
name|String
name|content
init|=
literal|null
decl_stmt|;
specifier|public
name|XMLResourceImpl
parameter_list|(
name|CollectionImpl
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
name|docId
argument_list|,
name|id
argument_list|,
literal|1
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|XMLResourceImpl
parameter_list|(
name|CollectionImpl
name|parent
parameter_list|,
name|String
name|docId
parameter_list|,
name|String
name|id
parameter_list|,
name|int
name|indent
parameter_list|,
name|String
name|encoding
parameter_list|)
throws|throws
name|XMLDBException
block|{
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
name|this
operator|.
name|indent
operator|=
name|indent
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
return|return
name|content
return|;
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
name|encoding
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|indent
argument_list|)
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
name|path
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
name|indent
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|encoding
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
name|encoding
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
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
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
return|return
operator|(
name|id
operator|==
literal|null
operator|)
condition|?
name|documentName
else|:
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
name|Parser
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
try|try
block|{
name|content
operator|=
name|XMLUtil
operator|.
name|readFile
argument_list|(
operator|(
name|File
operator|)
name|value
argument_list|)
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"could not retrieve document contents: "
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
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|(
literal|"xml"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|XMLSerializer
name|xmlout
init|=
operator|new
name|XMLSerializer
argument_list|(
name|sout
argument_list|,
name|format
argument_list|)
decl_stmt|;
try|try
block|{
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
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|(
literal|"xml"
argument_list|,
literal|"UTF-8"
argument_list|,
literal|false
argument_list|)
decl_stmt|;
return|return
operator|new
name|InternalXMLSerializer
argument_list|(
name|format
argument_list|)
return|;
block|}
specifier|protected
name|void
name|setEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
name|this
operator|.
name|encoding
operator|=
name|encoding
expr_stmt|;
block|}
specifier|protected
name|void
name|setSAXDocEvents
parameter_list|(
name|boolean
name|generate
parameter_list|)
block|{
name|this
operator|.
name|saxDocEvents
operator|=
name|generate
expr_stmt|;
block|}
specifier|private
class|class
name|InternalXMLSerializer
extends|extends
name|XMLSerializer
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
parameter_list|(
name|OutputFormat
name|format
parameter_list|)
block|{
name|super
argument_list|(
name|format
argument_list|)
expr_stmt|;
name|setOutputCharStream
argument_list|(
name|writer
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
block|}
end_class

end_unit

