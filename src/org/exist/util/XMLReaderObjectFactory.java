begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

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
name|commons
operator|.
name|pool
operator|.
name|BasePoolableObjectFactory
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

begin_comment
comment|/**  * Factory to create new XMLReader objects on demand. The factory is used  * by {@link org.exist.util.XMLReaderPool}.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XMLReaderObjectFactory
extends|extends
name|BasePoolableObjectFactory
block|{
specifier|public
specifier|final
specifier|static
name|int
name|VALIDATION_UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|VALIDATION_ENABLED
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|VALIDATION_AUTO
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|VALIDATION_DISABLED
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONFIGURATION_ENTITY_RESOLVER_ELEMENT_NAME
init|=
literal|"entity-resolver"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONFIGURATION_CATALOG_ELEMENT_NAME
init|=
literal|"catalog"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"validation"
decl_stmt|;
comment|//TOO : move elsewhere ?
specifier|public
specifier|final
specifier|static
name|String
name|VALIDATION_MODE_ATTRIBUTE
init|=
literal|"mode"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_VALIDATION_MODE
init|=
literal|"validation.mode"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CATALOG_RESOLVER
init|=
literal|"validation.resolver"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CATALOG_URIS
init|=
literal|"validation.catalog_uris"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|GRAMMER_POOL
init|=
literal|"validation.grammar_pool"
decl_stmt|;
comment|// Xerces feature and property names
specifier|public
specifier|final
specifier|static
name|String
name|FEATURES_VALIDATION_SCHEMA
init|=
literal|"http://apache.org/xml/features/validation/schema"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTIES_INTERNAL_GRAMMARPOOL
init|=
literal|"http://apache.org/xml/properties/internal/grammar-pool"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTIES_LOAD_EXT_DTD
init|=
literal|"http://apache.org/xml/features/nonvalidating/load-external-dtd"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTIES_ENTITYRESOLVER
init|=
literal|"http://apache.org/xml/properties/internal/entity-resolver"
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/**      *      */
specifier|public
name|XMLReaderObjectFactory
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
comment|/**      * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()      */
specifier|public
name|Object
name|makeObject
parameter_list|()
throws|throws
name|Exception
block|{
name|Configuration
name|config
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
comment|// Get validation settings
name|String
name|option
init|=
operator|(
name|String
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|PROPERTY_VALIDATION_MODE
argument_list|)
decl_stmt|;
name|int
name|validation
init|=
name|convertValidationMode
argument_list|(
name|option
argument_list|)
decl_stmt|;
name|GrammarPool
name|grammarPool
init|=
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
decl_stmt|;
name|eXistXMLCatalogResolver
name|resolver
init|=
operator|(
name|eXistXMLCatalogResolver
operator|)
name|config
operator|.
name|getProperty
argument_list|(
name|CATALOG_RESOLVER
argument_list|)
decl_stmt|;
name|XMLReader
name|xmlReader
init|=
name|createXmlReader
argument_list|(
name|validation
argument_list|,
name|grammarPool
argument_list|,
name|resolver
argument_list|)
decl_stmt|;
name|setReaderValidationMode
argument_list|(
name|validation
argument_list|,
name|xmlReader
argument_list|)
expr_stmt|;
return|return
name|xmlReader
return|;
block|}
comment|/**      * Create Xmlreader and setup validation      */
specifier|public
specifier|static
name|XMLReader
name|createXmlReader
parameter_list|(
name|int
name|validation
parameter_list|,
name|GrammarPool
name|grammarPool
parameter_list|,
name|eXistXMLCatalogResolver
name|resolver
parameter_list|)
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
block|{
comment|// Create a xmlreader
name|SAXParserFactory
name|saxFactory
init|=
name|SAXParserFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
if|if
condition|(
name|validation
operator|==
name|VALIDATION_AUTO
operator|||
name|validation
operator|==
name|VALIDATION_ENABLED
condition|)
block|{
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
comment|// Setup grammar cache
if|if
condition|(
name|grammarPool
operator|!=
literal|null
condition|)
block|{
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|PROPERTIES_INTERNAL_GRAMMARPOOL
argument_list|,
name|grammarPool
argument_list|)
expr_stmt|;
block|}
comment|// Setup xml catalog resolver
if|if
condition|(
name|resolver
operator|!=
literal|null
condition|)
block|{
name|xmlReader
operator|.
name|setProperty
argument_list|(
name|PROPERTIES_ENTITYRESOLVER
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
block|}
return|return
name|xmlReader
return|;
block|}
comment|/**      * Convert configuration text (yes,no,true,false,auto) into a magic number.        */
specifier|public
specifier|static
name|int
name|convertValidationMode
parameter_list|(
name|String
name|option
parameter_list|)
block|{
name|int
name|validation
init|=
name|VALIDATION_AUTO
decl_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|option
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
operator|||
name|option
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
name|validation
operator|=
name|VALIDATION_ENABLED
expr_stmt|;
block|}
if|else if
condition|(
name|option
operator|.
name|equals
argument_list|(
literal|"auto"
argument_list|)
condition|)
block|{
name|validation
operator|=
name|VALIDATION_AUTO
expr_stmt|;
block|}
else|else
block|{
name|validation
operator|=
name|VALIDATION_DISABLED
expr_stmt|;
block|}
block|}
return|return
name|validation
return|;
block|}
comment|/**      * Setup validation mode of xml reader.      */
specifier|public
specifier|static
name|void
name|setReaderValidationMode
parameter_list|(
name|int
name|validation
parameter_list|,
name|XMLReader
name|xmlReader
parameter_list|)
block|{
if|if
condition|(
name|validation
operator|==
name|VALIDATION_UNKNOWN
condition|)
block|{
return|return;
block|}
comment|// Configure xmlreader see http://xerces.apache.org/xerces2-j/features.html
try|try
block|{
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
name|setFeature
argument_list|(
name|Namespaces
operator|.
name|SAX_VALIDATION
argument_list|,
name|validation
operator|==
name|VALIDATION_AUTO
operator|||
name|validation
operator|==
name|VALIDATION_ENABLED
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
name|validation
operator|==
name|VALIDATION_AUTO
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|FEATURES_VALIDATION_SCHEMA
argument_list|,
name|validation
operator|==
name|VALIDATION_AUTO
operator|||
name|validation
operator|==
name|VALIDATION_ENABLED
argument_list|)
expr_stmt|;
name|xmlReader
operator|.
name|setFeature
argument_list|(
name|PROPERTIES_LOAD_EXT_DTD
argument_list|,
name|validation
operator|==
name|VALIDATION_AUTO
operator|||
name|validation
operator|==
name|VALIDATION_ENABLED
argument_list|)
expr_stmt|;
comment|// Attempt to make validation function equal to insert mode
comment|//saxFactory.setFeature(Namespaces.SAX_NAMESPACES_PREFIXES, true);
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
name|e1
parameter_list|)
block|{
comment|// Ignore: feature only recognized by xerces
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
name|e1
parameter_list|)
block|{
comment|// Ignore: feature only recognized by xerces
block|}
block|}
block|}
end_class

end_unit

