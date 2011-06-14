begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-08 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
package|;
end_package

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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
operator|.
name|Entry
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
name|Source
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
name|SAXSource
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
name|SAXAdapter
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
comment|/**  * Utility Functions for XQuery Extension Modules  *   * @author Adam Retter<adam@exist-db.org>  * @serial 200805202059  * @version 1.1  */
end_comment

begin_class
specifier|public
class|class
name|ModuleUtils
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ModuleUtils
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** 	 * Takes a String of XML and Creates an XML Node from it using SAX in the 	 * context of the query 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param str 	 *            The String of XML 	 *  	 * @return The NodeValue of XML 	 */
specifier|public
specifier|static
name|NodeValue
name|stringToXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|str
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|Reader
name|reader
init|=
operator|new
name|StringReader
argument_list|(
name|str
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|inputSourceToXML
argument_list|(
name|context
argument_list|,
operator|new
name|InputSource
argument_list|(
name|reader
argument_list|)
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|/** 	 * Takes an InputStream of XML and Creates an XML Node from it using SAX in the 	 * context of the query 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param is 	 *            The InputStream of XML 	 *  	 * @return The NodeValue of XML 	 */
specifier|public
specifier|static
name|NodeValue
name|streamToXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|InputStream
name|is
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
return|return
name|inputSourceToXML
argument_list|(
name|context
argument_list|,
operator|new
name|InputSource
argument_list|(
name|is
argument_list|)
argument_list|)
return|;
block|}
comment|/** 	 * Takes a Source of XML and Creates an XML Node from it using SAX in the 	 * context of the query 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param src 	 *            The Source of XML 	 *  	 * @return The NodeValue of XML 	 */
specifier|public
specifier|static
name|NodeValue
name|sourceToXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Source
name|src
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|InputSource
name|inputSource
init|=
name|SAXSource
operator|.
name|sourceToInputSource
argument_list|(
name|src
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|src
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is unsupported."
argument_list|)
throw|;
block|}
return|return
name|inputSourceToXML
argument_list|(
name|context
argument_list|,
name|inputSource
argument_list|)
return|;
block|}
comment|/** 	 * Takes a InputSource of XML and Creates an XML Node from it using SAX in the 	 * context of the query 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param xml 	 *            The InputSource of XML 	 *  	 * @return The NodeValue of XML 	 */
specifier|public
specifier|static
name|NodeValue
name|inputSourceToXML
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|InputSource
name|inputSource
parameter_list|)
throws|throws
name|SAXException
throws|,
name|IOException
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// try and construct xml document from input stream, we use eXist's
comment|// in-memory DOM implementation
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Parsing XML response ..."
argument_list|)
expr_stmt|;
comment|// TODO : we should be able to cope with context.getBaseURI()
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
name|inputSource
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
comment|// return (NodeValue)doc.getDocumentElement();
return|return
operator|(
operator|(
name|NodeValue
operator|)
name|doc
operator|)
return|;
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
block|}
comment|/** 	 * Takes a HTML InputSource and creates an XML representation of the HTML by 	 * tidying it (uses NekoHTML) 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param srcHtml 	 *            The Source for the HTML          * @param parserFeatures          *            The features to set on the Parser          * @param parserProperties          *            The properties to set on the Parser 	 *  	 * @return An in-memory Document representing the XML'ised HTML 	 */
specifier|public
specifier|static
name|DocumentImpl
name|htmlToXHtml
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|url
parameter_list|,
name|Source
name|srcHtml
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserFeatures
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parserProperties
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
name|InputSource
name|inputSource
init|=
name|SAXSource
operator|.
name|sourceToInputSource
argument_list|(
name|srcHtml
argument_list|)
decl_stmt|;
if|if
condition|(
name|inputSource
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|srcHtml
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|" is unsupported."
argument_list|)
throw|;
block|}
return|return
name|htmlToXHtml
argument_list|(
name|context
argument_list|,
name|url
argument_list|,
name|inputSource
argument_list|,
name|parserFeatures
argument_list|,
name|parserProperties
argument_list|)
return|;
block|}
comment|/** 	 * Takes a HTML InputSource and creates an XML representation of the HTML by 	 * tidying it (uses NekoHTML) 	 *  	 * @param context 	 *            The Context of the calling XQuery 	 * @param srcHtml 	 *            The InputSource for the HTML          * @param parserFeatures          *            The features to set on the Parser          * @param parserProperties          *            The properties to set on the Parser 	 *  	 * @return An in-memory Document representing the XML'ised HTML 	 */
specifier|public
specifier|static
name|DocumentImpl
name|htmlToXHtml
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|url
parameter_list|,
name|InputSource
name|srcHtml
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserFeatures
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parserProperties
parameter_list|)
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|// we use eXist's in-memory DOM implementation
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|DocumentImpl
name|memtreeDoc
init|=
literal|null
decl_stmt|;
comment|// use Neko to parse the HTML content to XML
name|XMLReader
name|reader
init|=
literal|null
decl_stmt|;
try|try
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Converting HTML to XML using NekoHTML parser for: "
operator|+
name|url
argument_list|)
expr_stmt|;
name|reader
operator|=
operator|(
name|XMLReader
operator|)
name|Class
operator|.
name|forName
argument_list|(
literal|"org.cyberneko.html.parsers.SAXParser"
argument_list|)
operator|.
name|newInstance
argument_list|()
expr_stmt|;
if|if
condition|(
name|parserFeatures
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserFeature
range|:
name|parserFeatures
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reader
operator|.
name|setFeature
argument_list|(
name|parserFeature
operator|.
name|getKey
argument_list|()
argument_list|,
name|parserFeature
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|parserProperties
operator|==
literal|null
condition|)
block|{
comment|//default: do not modify the case of elements and attributes
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://cyberneko.org/html/properties/names/elems"
argument_list|,
literal|"match"
argument_list|)
expr_stmt|;
name|reader
operator|.
name|setProperty
argument_list|(
literal|"http://cyberneko.org/html/properties/names/attrs"
argument_list|,
literal|"no-change"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|parserProperty
range|:
name|parserProperties
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|reader
operator|.
name|setProperty
argument_list|(
name|parserProperty
operator|.
name|getKey
argument_list|()
argument_list|,
name|parserProperty
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|String
name|errorMsg
init|=
literal|"Error while invoking NekoHTML parser. ("
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|"). If you want to parse non-wellformed HTML files, put "
operator|+
literal|"nekohtml.jar into directory 'lib/user'."
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errorMsg
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|errorMsg
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|SAXAdapter
name|adapter
init|=
operator|new
name|SAXAdapter
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|adapter
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
name|srcHtml
argument_list|)
expr_stmt|;
name|Document
name|doc
init|=
name|adapter
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|memtreeDoc
operator|=
operator|(
name|DocumentImpl
operator|)
name|doc
expr_stmt|;
name|memtreeDoc
operator|.
name|setContext
argument_list|(
name|context
argument_list|)
expr_stmt|;
return|return
name|memtreeDoc
return|;
block|}
comment|/** 	 * Parses a structure like<parameters><param name="a" value="1"/><param 	 * name="b" value="2"/></parameters> into a set of Properties 	 *  	 * @param nParameters 	 *            The parameters Node 	 * @return a set of name value properties for representing the XML 	 *         parameters 	 */
specifier|public
specifier|static
name|Properties
name|parseParameters
parameter_list|(
name|Node
name|nParameters
parameter_list|)
block|{
return|return
name|parseProperties
argument_list|(
name|nParameters
argument_list|,
literal|"param"
argument_list|)
return|;
block|}
comment|/** 	 * Parses a structure like<properties><property name="a" value="1"/><property 	 * name="b" value="2"/></properties> into a set of Properties 	 *  	 * @param nProperties 	 *            The properties Node 	 * @return a set of name value properties for representing the XML 	 *         properties 	 */
specifier|public
specifier|static
name|Properties
name|parseProperties
parameter_list|(
name|Node
name|nProperties
parameter_list|)
block|{
return|return
name|parseProperties
argument_list|(
name|nProperties
argument_list|,
literal|"property"
argument_list|)
return|;
block|}
comment|/** 	 * Parses a structure like<properties><property name="a" value="1"/><property 	 * name="b" value="2"/></properties> into a set of Properties 	 *  	 * @param container 	 *            The container of the properties 	 * @param elementName 	 *            The name of the property element 	 * @return a set of name value properties for representing the XML 	 *         properties 	 */
specifier|private
specifier|static
name|Properties
name|parseProperties
parameter_list|(
name|Node
name|container
parameter_list|,
name|String
name|elementName
parameter_list|)
block|{
name|Properties
name|properties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|container
operator|!=
literal|null
operator|&&
name|container
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|NodeList
name|params
init|=
operator|(
operator|(
name|Element
operator|)
name|container
operator|)
operator|.
name|getElementsByTagName
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|params
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|param
init|=
operator|(
operator|(
name|Element
operator|)
name|params
operator|.
name|item
argument_list|(
name|i
argument_list|)
operator|)
decl_stmt|;
name|String
name|name
init|=
name|param
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|param
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|value
operator|!=
literal|null
condition|)
block|{
name|properties
operator|.
name|setProperty
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Name or value attribute missing for "
operator|+
name|elementName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
return|return
name|properties
return|;
block|}
block|}
end_class

end_unit

