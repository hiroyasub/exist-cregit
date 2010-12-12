begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Apache FOP Transformation Extension  *  Copyright (C) 2007 Craig Goodyer at the University of the West of England  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xslfo
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
name|util
operator|.
name|Enumeration
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|Transformer
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|sax
operator|.
name|TransformerHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|Configuration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|SAXConfigurationHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fop
operator|.
name|apps
operator|.
name|FOUserAgent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fop
operator|.
name|apps
operator|.
name|Fop
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|fop
operator|.
name|apps
operator|.
name|FopFactory
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
name|storage
operator|.
name|DBBroker
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
name|modules
operator|.
name|ModuleUtils
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
name|Base64BinaryValueType
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
name|BinaryValueFromInputStream
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
name|FunctionParameterSequenceType
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
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|TransformerFactoryAllocator
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
name|helpers
operator|.
name|DefaultHandler
import|;
end_import

begin_comment
comment|/**  * @author Craig Goodyer<craiggoodyer@gmail.com>  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|RenderFunction
extends|extends
name|BasicFunction
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|private
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|RenderFunction
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"render"
argument_list|,
name|XSLFOModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XSLFOModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Renders a given XSL-FO document. "
operator|+
literal|"Returns an xs:base64binary of the result. "
operator|+
literal|"Parameters are specified with the structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/>"
operator|+
literal|"</parameters>. "
operator|+
literal|"Recognised rendering parameters are: author, title, keywords and dpi."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"document"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"XSL-FO document"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mime-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"parameters for the transform"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"render"
argument_list|,
name|XSLFOModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|XSLFOModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Renders a given XSL-FO document. "
operator|+
literal|"Returns an xs:base64binary of the result. "
operator|+
literal|"Parameters are specified with the structure: "
operator|+
literal|"<parameters><param name=\"param-name1\" value=\"param-value1\"/>"
operator|+
literal|"</parameters>. "
operator|+
literal|"Recognised rendering parameters are: author, title, keywords and dpi."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"document"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"XSL-FO document"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"mime-type"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|""
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"parameters for the transform"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"config-file"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Apache FOP Configuration file"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|BASE64_BINARY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"result"
argument_list|)
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * Constructor for RenderFunction, which returns a new instance of this 	 * class. 	 *  	 * @param context 	 * @param signature 	 */
specifier|public
name|RenderFunction
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
comment|/* 	 * Actual implementation of the rendering process. When a function in this 	 * module is called, this method is executed with the given inputs. @param 	 * Sequence[] args (XSL-FO, mime-type, parameters) @param Sequence 	 * contextSequence (default sequence) 	 *  	 * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], 	 *      org.exist.xquery.value.Sequence) 	 */
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
comment|// gather input XSL-FO document
comment|// if no input document (empty), return empty result as we need data to
comment|// process
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|Item
name|inputNode
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
comment|// get mime-type
name|String
name|mimeType
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
comment|// get parameters
name|Properties
name|parameters
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|parameters
operator|=
name|ModuleUtils
operator|.
name|parseParameters
argument_list|(
operator|(
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNode
argument_list|()
argument_list|)
expr_stmt|;
block|}
try|try
block|{
comment|// setup a transformer handler
name|TransformerHandler
name|handler
init|=
name|TransformerFactoryAllocator
operator|.
name|getTransformerFactory
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
argument_list|)
operator|.
name|newTransformerHandler
argument_list|()
decl_stmt|;
name|Transformer
name|transformer
init|=
name|handler
operator|.
name|getTransformer
argument_list|()
decl_stmt|;
comment|// set the parameters if any
if|if
condition|(
name|parameters
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Enumeration
name|keys
init|=
name|parameters
operator|.
name|keys
argument_list|()
decl_stmt|;
while|while
condition|(
name|keys
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|String
name|name
init|=
operator|(
name|String
operator|)
name|keys
operator|.
name|nextElement
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|parameters
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|setParameter
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
comment|// setup the FopFactory
name|FopFactory
name|fopFactory
init|=
name|FopFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|4
operator|&&
name|args
index|[
literal|3
index|]
operator|!=
literal|null
operator|&&
operator|!
name|args
index|[
literal|3
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|FopConfigurationBuilder
name|cfgBuilder
init|=
operator|new
name|FopConfigurationBuilder
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|)
decl_stmt|;
name|Configuration
name|cfg
init|=
name|cfgBuilder
operator|.
name|buildFromItem
argument_list|(
name|args
index|[
literal|3
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
name|fopFactory
operator|.
name|setUserConfig
argument_list|(
name|cfg
argument_list|)
expr_stmt|;
block|}
comment|// setup the foUserAgent, using given parameters held in the
comment|// transformer handler
name|FOUserAgent
name|foUserAgent
init|=
name|setupFOUserAgent
argument_list|(
name|fopFactory
operator|.
name|newFOUserAgent
argument_list|()
argument_list|,
name|parameters
argument_list|,
name|transformer
argument_list|)
decl_stmt|;
comment|// create new instance of FOP using the mimetype, the created user
comment|// agent, and the output stream
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|Fop
name|fop
init|=
name|fopFactory
operator|.
name|newFop
argument_list|(
name|mimeType
argument_list|,
name|foUserAgent
argument_list|,
name|baos
argument_list|)
decl_stmt|;
comment|// Obtain FOP's DefaultHandler
name|DefaultHandler
name|dh
init|=
name|fop
operator|.
name|getDefaultHandler
argument_list|()
decl_stmt|;
comment|// process the XSL-FO
name|dh
operator|.
name|startDocument
argument_list|()
expr_stmt|;
name|inputNode
operator|.
name|toSAX
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|dh
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
name|dh
operator|.
name|endDocument
argument_list|()
expr_stmt|;
comment|// return the result
return|return
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
operator|new
name|ByteArrayInputStream
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|TransformerException
name|te
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|te
operator|.
name|getMessageAndLocation
argument_list|()
argument_list|,
name|te
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SAXException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|se
operator|.
name|getMessage
argument_list|()
argument_list|,
name|se
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Setup the UserAgent for FOP, from given parameters * 	 *  	 * @param transformer 	 *            Created based on the XSLT, so containing any parameters to the 	 *            XSL-FO specified in the XQuery 	 * @param parameters 	 *            any user defined parameters to the XSL-FO process 	 * @return FOUserAgent The generated FOUserAgent to include any parameters 	 *         passed in 	 */
specifier|private
name|FOUserAgent
name|setupFOUserAgent
parameter_list|(
name|FOUserAgent
name|foUserAgent
parameter_list|,
name|Properties
name|parameters
parameter_list|,
name|Transformer
name|transformer
parameter_list|)
throws|throws
name|TransformerException
block|{
comment|// setup the foUserAgent as per the parameters given
name|foUserAgent
operator|.
name|setProducer
argument_list|(
literal|"eXist with Apache FOP"
argument_list|)
expr_stmt|;
if|if
condition|(
name|transformer
operator|.
name|getParameter
argument_list|(
literal|"FOPauthor"
argument_list|)
operator|!=
literal|null
condition|)
name|foUserAgent
operator|.
name|setAuthor
argument_list|(
name|parameters
operator|.
name|getProperty
argument_list|(
literal|"author"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transformer
operator|.
name|getParameter
argument_list|(
literal|"FOPtitle"
argument_list|)
operator|!=
literal|null
condition|)
name|foUserAgent
operator|.
name|setTitle
argument_list|(
name|parameters
operator|.
name|getProperty
argument_list|(
literal|"title"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transformer
operator|.
name|getParameter
argument_list|(
literal|"FOPkeywords"
argument_list|)
operator|!=
literal|null
condition|)
name|foUserAgent
operator|.
name|setTitle
argument_list|(
name|parameters
operator|.
name|getProperty
argument_list|(
literal|"keywords"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|transformer
operator|.
name|getParameter
argument_list|(
literal|"FOPdpi"
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|String
name|dpiStr
init|=
operator|(
name|String
operator|)
name|transformer
operator|.
name|getParameter
argument_list|(
literal|"dpi"
argument_list|)
decl_stmt|;
try|try
block|{
name|foUserAgent
operator|.
name|setTargetResolution
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|dpiStr
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
throw|throw
operator|new
name|TransformerException
argument_list|(
literal|"Cannot parse value of \"dpi\" - "
operator|+
name|dpiStr
operator|+
literal|" to configure FOUserAgent"
argument_list|)
throw|;
block|}
block|}
return|return
name|foUserAgent
return|;
block|}
comment|/** 	 * Extension of the Apache Avalon DefaultConfigurationBuilder Allows better 	 * integration with Nodes passed in from eXist as Configuration files 	 */
specifier|private
class|class
name|FopConfigurationBuilder
extends|extends
name|org
operator|.
name|apache
operator|.
name|avalon
operator|.
name|framework
operator|.
name|configuration
operator|.
name|DefaultConfigurationBuilder
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
specifier|public
name|FopConfigurationBuilder
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|public
name|FopConfigurationBuilder
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|boolean
name|enableNamespaces
parameter_list|)
block|{
name|super
argument_list|(
name|enableNamespaces
argument_list|)
expr_stmt|;
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|Configuration
name|buildFromItem
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|SAXException
block|{
name|SAXConfigurationHandler
name|handler
init|=
name|getHandler
argument_list|()
decl_stmt|;
name|handler
operator|.
name|clear
argument_list|()
expr_stmt|;
name|item
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|handler
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|handler
operator|.
name|getConfiguration
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

