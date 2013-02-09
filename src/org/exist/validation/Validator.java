begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *  *  This program stream free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program stream distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|util
operator|.
name|PropertyMapBuilder
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|SchemaReader
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|ValidateProperty
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|ValidationDriver
import|;
end_import

begin_import
import|import
name|com
operator|.
name|thaiopensource
operator|.
name|validate
operator|.
name|rng
operator|.
name|CompactSchemaReader
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
name|Namespaces
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
name|resolver
operator|.
name|AnyUriResolver
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
name|ErrorHandler
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
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  *  Validate XML documents with their grammars (DTD's and Schemas).  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Validator
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Validator
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|brokerPool
init|=
literal|null
decl_stmt|;
specifier|private
name|GrammarPool
name|grammarPool
init|=
literal|null
decl_stmt|;
specifier|private
name|Configuration
name|config
init|=
literal|null
decl_stmt|;
specifier|private
name|eXistXMLCatalogResolver
name|systemCatalogResolver
init|=
literal|null
decl_stmt|;
comment|/**      *  Setup Validator object with brokerpool as db connection.      *       * @param pool Brokerpool      */
specifier|public
name|Validator
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Initializing Validator."
argument_list|)
expr_stmt|;
if|if
condition|(
name|brokerPool
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|brokerPool
operator|=
name|pool
expr_stmt|;
block|}
comment|// Get configuration
name|config
operator|=
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
expr_stmt|;
comment|// Check xerces version
specifier|final
name|StringBuilder
name|xmlLibMessage
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|XmlLibraryChecker
operator|.
name|hasValidParser
argument_list|(
name|xmlLibMessage
argument_list|)
condition|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|xmlLibMessage
argument_list|)
expr_stmt|;
block|}
comment|// setup grammar brokerPool
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
comment|// setup system wide catalog resolver
name|systemCatalogResolver
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
comment|/**      *  Validate XML data using system catalog. XSD and DTD only.       *      * @param stream XML input.      * @return Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
return|return
name|validate
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      *  Validate XML data from reader using specified grammar.      *      * @param grammarUrl   User supplied path to grammar.      * @param stream       XML input.      * @return Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validate
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|grammarUrl
parameter_list|)
block|{
comment|// repair path to local resource
if|if
condition|(
name|grammarUrl
operator|!=
literal|null
operator|&&
name|grammarUrl
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|grammarUrl
operator|=
literal|"xmldb:exist://"
operator|+
name|grammarUrl
expr_stmt|;
block|}
if|if
condition|(
name|grammarUrl
operator|!=
literal|null
operator|&&
operator|(
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".rng"
argument_list|)
operator|||
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".rnc"
argument_list|)
operator|||
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".nvdl"
argument_list|)
operator|||
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".sch"
argument_list|)
operator|)
condition|)
block|{
comment|// Validate with Jing
return|return
name|validateJing
argument_list|(
name|stream
argument_list|,
name|grammarUrl
argument_list|)
return|;
block|}
else|else
block|{
comment|// Validate with Xerces
return|return
name|validateParse
argument_list|(
name|stream
argument_list|,
name|grammarUrl
argument_list|)
return|;
block|}
block|}
comment|/**      *  Validate XML data from reader using specified grammar with Jing.      *      * @param stream       XML input document.      * @param grammarUrl   User supplied path to grammar.      * @return Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validateJing
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|grammarUrl
parameter_list|)
block|{
specifier|final
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
try|try
block|{
name|report
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// Setup validation properties. see Jing interface
specifier|final
name|PropertyMapBuilder
name|properties
init|=
operator|new
name|PropertyMapBuilder
argument_list|()
decl_stmt|;
name|ValidateProperty
operator|.
name|ERROR_HANDLER
operator|.
name|put
argument_list|(
name|properties
argument_list|,
name|report
argument_list|)
expr_stmt|;
comment|// Copied from Jing code ; the Compact syntax seem to have a different
comment|// Schema reader. To be investigated. http://www.thaiopensource.com/relaxng/api/jing/index.html
specifier|final
name|SchemaReader
name|schemaReader
init|=
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".rnc"
argument_list|)
condition|?
name|CompactSchemaReader
operator|.
name|getInstance
argument_list|()
else|:
literal|null
decl_stmt|;
comment|// Setup driver
specifier|final
name|ValidationDriver
name|driver
init|=
operator|new
name|ValidationDriver
argument_list|(
name|properties
operator|.
name|toPropertyMap
argument_list|()
argument_list|,
name|schemaReader
argument_list|)
decl_stmt|;
comment|// Load schema
name|driver
operator|.
name|loadSchema
argument_list|(
operator|new
name|InputSource
argument_list|(
name|grammarUrl
argument_list|)
argument_list|)
expr_stmt|;
comment|// Validate XML instance
name|driver
operator|.
name|validate
argument_list|(
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setThrowable
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setThrowable
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
return|return
name|report
return|;
block|}
comment|/**      *  Validate XML data using system catalog. XSD and DTD only.      *      * @param stream XML input.      * @return Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validateParse
parameter_list|(
name|InputStream
name|stream
parameter_list|)
block|{
return|return
name|validateParse
argument_list|(
name|stream
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      *  Validate XML data from reader using specified grammar.      *      * @param grammarUrl   User supplied path to grammar.      * @param stream XML input.      * @return Validation report containing all validation info.      */
specifier|public
name|ValidationReport
name|validateParse
parameter_list|(
name|InputStream
name|stream
parameter_list|,
name|String
name|grammarUrl
parameter_list|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Start validation."
argument_list|)
expr_stmt|;
specifier|final
name|ValidationReport
name|report
init|=
operator|new
name|ValidationReport
argument_list|()
decl_stmt|;
specifier|final
name|ValidationContentHandler
name|contenthandler
init|=
operator|new
name|ValidationContentHandler
argument_list|()
decl_stmt|;
try|try
block|{
specifier|final
name|XMLReader
name|xmlReader
init|=
name|getXMLReader
argument_list|(
name|contenthandler
argument_list|,
name|report
argument_list|)
decl_stmt|;
if|if
condition|(
name|grammarUrl
operator|==
literal|null
condition|)
block|{
comment|// Scenario 1 : no params - use system catalog
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation using system catalog."
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
name|systemCatalogResolver
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|".xml"
argument_list|)
condition|)
block|{
comment|// Scenario 2 : path to catalog (xml)
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation using user specified catalog '"
operator|+
name|grammarUrl
operator|+
literal|"'."
argument_list|)
expr_stmt|;
specifier|final
name|eXistXMLCatalogResolver
name|resolver
init|=
operator|new
name|eXistXMLCatalogResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|setCatalogList
argument_list|(
operator|new
name|String
index|[]
block|{
name|grammarUrl
block|}
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
name|resolver
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|grammarUrl
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// Scenario 3 : path to collection ("/"): search.
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation using searched grammar, start from '"
operator|+
name|grammarUrl
operator|+
literal|"'."
argument_list|)
expr_stmt|;
specifier|final
name|SearchResourceResolver
name|resolver
init|=
operator|new
name|SearchResourceResolver
argument_list|(
name|grammarUrl
argument_list|,
name|brokerPool
argument_list|)
decl_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_ENTITYRESOLVER
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// Scenario 4 : path to grammar (xsd, dtd) specified.
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation using specified grammar '"
operator|+
name|grammarUrl
operator|+
literal|"'."
argument_list|)
expr_stmt|;
specifier|final
name|AnyUriResolver
name|resolver
init|=
operator|new
name|AnyUriResolver
argument_list|(
name|grammarUrl
argument_list|)
decl_stmt|;
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|APACHE_PROPERTIES_ENTITYRESOLVER
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
block|}
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation started."
argument_list|)
expr_stmt|;
name|report
operator|.
name|start
argument_list|()
expr_stmt|;
specifier|final
name|InputSource
name|source
init|=
operator|new
name|InputSource
argument_list|(
name|stream
argument_list|)
decl_stmt|;
name|xmlReader
operator|.
name|parse
argument_list|(
name|source
argument_list|)
expr_stmt|;
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation stopped."
argument_list|)
expr_stmt|;
name|report
operator|.
name|stop
argument_list|()
expr_stmt|;
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
if|if
condition|(
operator|!
name|report
operator|.
name|isValid
argument_list|()
condition|)
block|{
name|logger
operator|.
name|debug
argument_list|(
literal|"Document is not valid."
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setThrowable
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|ex
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|report
operator|.
name|setThrowable
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
name|logger
operator|.
name|debug
argument_list|(
literal|"Validation performed in "
operator|+
name|report
operator|.
name|getValidationDuration
argument_list|()
operator|+
literal|" msec."
argument_list|)
expr_stmt|;
block|}
return|return
name|report
return|;
block|}
specifier|private
name|XMLReader
name|getXMLReader
parameter_list|(
name|ContentHandler
name|contentHandler
parameter_list|,
name|ErrorHandler
name|errorHandler
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
block|{
comment|// setup sax factory ; be sure just one instance!
specifier|final
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
specifier|final
name|SAXParser
name|saxParser
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
specifier|final
name|XMLReader
name|xmlReader
init|=
name|saxParser
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
comment|// Setup xmlreader
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
name|xmlReader
operator|.
name|setContentHandler
argument_list|(
name|contentHandler
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setErrorHandler
argument_list|(
name|errorHandler
argument_list|)
expr_stmt|;
return|return
name|xmlReader
return|;
block|}
block|}
end_class

end_unit

