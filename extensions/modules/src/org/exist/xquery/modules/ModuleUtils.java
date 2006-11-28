begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
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
name|IOException
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
name|value
operator|.
name|NodeValue
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
comment|/**  * Utility Functions for XQuery Extension Modules  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-11-28  * @version 1.0  */
end_comment

begin_class
specifier|public
class|class
name|ModuleUtils
block|{
comment|/** 	 * Takes a String of XML and Creates an XML Node from it using SAX in the context of the query 	 *  	 * @param xml	The String of XML 	 *  	 * @return	The NodeValue of XML  	 * */
specifier|public
specifier|static
name|NodeValue
name|stringToXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|xml
parameter_list|)
throws|throws
name|XPathException
block|{
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
name|xml
operator|.
name|getBytes
argument_list|()
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
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
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
name|e
operator|.
name|getMessage
argument_list|()
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

