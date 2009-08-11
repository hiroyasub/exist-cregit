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
name|IOException
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
name|TransformerConfigurationException
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
name|TransformerFactory
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
name|StreamResult
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
name|xerces
operator|.
name|xni
operator|.
name|parser
operator|.
name|XMLEntityResolver
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
name|storage
operator|.
name|BrokerPool
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
name|io
operator|.
name|ExistIOException
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
name|XMLReaderObjectFactory
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
name|GrammarPool
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
name|ValidationContentHandler
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
name|resolver
operator|.
name|SearchResourceResolver
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
name|resolver
operator|.
name|eXistXMLCatalogResolver
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
name|value
operator|.
name|BooleanValue
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
name|FunctionReturnSequenceType
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
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
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
comment|/**  *   xQuery function for validation of XML instance documents  * using grammars like XSDs and DTDs.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Jaxp
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|simpleFunctionTxt
init|=
literal|"Validate document by parsing $instance. Optionally "
operator|+
literal|"grammar caching can be enabled. Supported grammars types "
operator|+
literal|"are '.xsd' and '.dtd'."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|extendedFunctionTxt
init|=
literal|"Validate document by parsing $instance. Optionally "
operator|+
literal|"grammar caching can be enabled and "
operator|+
literal|"an XML catalog can be specified. Supported grammars types "
operator|+
literal|"are '.xsd' and '.dtd'."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|documentTxt
init|=
literal|"The document referenced as xs:anyURI or a node (element or result of fn:doc())."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|catalogTxt
init|=
literal|"The catalogs referenced as xs:anyURI's."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|cacheTxt
init|=
literal|"Set the flag to true() to enable grammar caching."
decl_stmt|;
specifier|private
specifier|final
name|BrokerPool
name|brokerPool
decl_stmt|;
comment|// Setup function signature
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
literal|"jaxp"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|simpleFunctionTxt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|documentTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cache-grammars"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|cacheTxt
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|simplereportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxp"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|documentTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"cache-grammars"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|cacheTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"catalogs"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|catalogTxt
argument_list|)
block|,                 }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|simplereportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxp-report"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|simpleFunctionTxt
operator|+
literal|" An XML report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|documentTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"enable-grammar-cache"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|cacheTxt
argument_list|)
block|,                 }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|xmlreportText
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"jaxp-report"
argument_list|,
name|ValidationModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ValidationModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|extendedFunctionTxt
operator|+
literal|" An XML report is returned."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"instance"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|documentTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"enable-grammar-cache"
argument_list|,
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|cacheTxt
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"catalogs"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|catalogTxt
argument_list|)
block|,                 }
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|Shared
operator|.
name|xmlreportText
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Jaxp
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
name|brokerPool
operator|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
expr_stmt|;
block|}
comment|/**      * @throws org.exist.xquery.XPathException       * @see BasicFunction#eval(Sequence[], Sequence)      */
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
name|XMLEntityResolver
name|entityResolver
init|=
literal|null
decl_stmt|;
name|GrammarPool
name|grammarPool
init|=
literal|null
decl_stmt|;
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
name|ValidationContentHandler
name|contenthandler
init|=
operator|new
name|ValidationContentHandler
argument_list|()
decl_stmt|;
try|try
block|{
name|report
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Get initialized parser
name|XMLReader
name|xmlReader
init|=
name|getXMLReader
argument_list|()
decl_stmt|;
comment|// Setup validation reporting
name|xmlReader
operator|.
name|setContentHandler
argument_list|(
name|contenthandler
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setErrorHandler
argument_list|(
name|report
argument_list|)
expr_stmt|;
comment|// Get inputstream for instance document
name|InputSource
name|instance
init|=
name|Shared
operator|.
name|getInputSource
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|context
argument_list|)
decl_stmt|;
comment|// Handle catalog
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"No Catalog specified"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|args
index|[
literal|2
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|// Use system catalog
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using system catalog."
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|entityResolver
operator|=
operator|(
name|eXistXMLCatalogResolver
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|CATALOG_RESOLVER
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Get URL for catalog
name|String
name|catalogUrls
index|[]
init|=
name|Shared
operator|.
name|getUrls
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|)
decl_stmt|;
name|String
name|singleUrl
init|=
name|catalogUrls
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|singleUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Search grammar in collection specified by URL. Just one collection is used.
name|LOG
operator|.
name|debug
argument_list|(
literal|"Search for grammar in "
operator|+
name|singleUrl
argument_list|)
expr_stmt|;
name|entityResolver
operator|=
operator|new
name|SearchResourceResolver
argument_list|(
name|catalogUrls
index|[
literal|0
index|]
argument_list|,
name|brokerPool
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_ENTITYRESOLVER
argument_list|,
name|entityResolver
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|singleUrl
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Using catalogs "
operator|+
name|getStrings
argument_list|(
name|catalogUrls
argument_list|)
argument_list|)
expr_stmt|;
name|entityResolver
operator|=
operator|new
name|eXistXMLCatalogResolver
argument_list|()
expr_stmt|;
operator|(
operator|(
name|eXistXMLCatalogResolver
operator|)
name|entityResolver
operator|)
operator|.
name|setCatalogList
argument_list|(
name|catalogUrls
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_ENTITYRESOLVER
argument_list|,
name|entityResolver
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Catalog URLs should end on / or .xml"
argument_list|)
expr_stmt|;
block|}
block|}
name|boolean
name|useCache
init|=
operator|(
operator|(
name|BooleanValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
comment|// Use grammarpool
if|if
condition|(
name|useCache
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Grammar caching enabled."
argument_list|)
expr_stmt|;
name|Configuration
name|config
init|=
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|grammarPool
operator|=
operator|(
name|GrammarPool
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|GRAMMER_POOL
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_INTERNAL_GRAMMARPOOL
argument_list|,
name|grammarPool
argument_list|)
expr_stmt|;
block|}
comment|// Jaxp document
name|LOG
operator|.
name|debug
argument_list|(
literal|"Start parsing document"
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|parse
argument_list|(
name|instance
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Stopped parsing document"
argument_list|)
expr_stmt|;
comment|// Distill namespace from document
name|report
operator|.
name|setNamespaceUri
argument_list|(
name|contenthandler
operator|.
name|getNamespaceUri
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExistIOException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setException
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|report
operator|.
name|stop
argument_list|()
expr_stmt|;
block|}
comment|// Create response
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"parse"
argument_list|)
condition|)
block|{
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|BooleanValue
argument_list|(
name|report
operator|.
name|isValid
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
else|else
comment|/* isCalledAs("parse-report") */
block|{
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
name|NodeImpl
name|result
init|=
name|Shared
operator|.
name|writeReport
argument_list|(
name|report
argument_list|,
name|builder
argument_list|)
decl_stmt|;
return|return
name|result
return|;
block|}
block|}
comment|// ####################################
specifier|private
name|XMLReader
name|getXMLReader
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
block|{
comment|// setup sax factory ; be sure just one instance!
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
comment|// Enable validation stuff
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// Create xml reader
name|SAXParser
name|saxParser
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|xmlReader
init|=
name|saxParser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|Namespaces
operator|.
name|SAX_VALIDATION
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|Namespaces
operator|.
name|SAX_VALIDATION_DYNAMIC
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_FEATURES_VALIDATION_SCHEMA
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_LOAD_EXT_DTD
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|Namespaces
operator|.
name|SAX_NAMESPACES_PREFIXES
argument_list|,
literal|true
argument_list|)
expr_stmt|;
return|return
name|xmlReader
return|;
block|}
comment|// No-go ...processor is in validating mode
specifier|private
name|File
name|preparseDTD
parameter_list|(
name|StreamSource
name|instance
parameter_list|,
name|String
name|systemId
parameter_list|)
throws|throws
name|IOException
throws|,
name|TransformerConfigurationException
throws|,
name|TransformerException
block|{
comment|// prepare output tmp storage
name|File
name|tmp
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"DTDvalidation"
argument_list|,
literal|"tmp"
argument_list|)
decl_stmt|;
name|tmp
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|tmp
argument_list|)
decl_stmt|;
name|TransformerFactory
name|tf
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|Transformer
name|transformer
init|=
name|tf
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|DOCTYPE_SYSTEM
argument_list|,
name|systemId
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
name|instance
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|tmp
return|;
block|}
specifier|private
specifier|static
name|String
name|getStrings
parameter_list|(
name|String
index|[]
name|data
parameter_list|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|field
range|:
name|data
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
literal|" "
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
comment|/*      *           // Prepare grammar ; does not work             /*             if (args[1].hasOne()) {                 // Get URL for grammar                 grammarUrl = Shared.getUrl(args[1].itemAt(0));                  // Special case for DTD, the document needs to be rewritten.                 if (grammarUrl.endsWith(".dtd")) {                     StreamSource newInstance = Shared.getStreamSource(instance);                     tmpFile = preparseDTD(newInstance, grammarUrl);                     instance = new InputSource(new FileInputStream(tmpFile));                  } else if (grammarUrl.endsWith(".xsd")) {                     xmlReader.setProperty(XMLReaderObjectFactory.APACHE_PROPERTIES_NONAMESPACESCHEMALOCATION, grammarUrl);                  } else {                     throw new XPathException("Grammar type not supported.");                 }             }             */
block|}
end_class

end_unit

