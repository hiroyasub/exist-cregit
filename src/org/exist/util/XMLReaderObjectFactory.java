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
name|EXistException
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
specifier|private
specifier|final
specifier|static
name|int
name|VALIDATION_ENABLED
init|=
literal|0
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|VALIDATION_AUTO
init|=
literal|1
decl_stmt|;
specifier|private
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
name|PROPERTY_VALIDATION
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
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTIES_RESOLVER
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
comment|/** (non-Javadoc)      * @see org.apache.commons.pool.BasePoolableObjectFactory#makeObject()      */
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
comment|// get validation settings
name|int
name|validation
init|=
name|VALIDATION_AUTO
decl_stmt|;
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
name|PROPERTY_VALIDATION
argument_list|)
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
name|validation
operator|=
name|VALIDATION_ENABLED
expr_stmt|;
if|else if
condition|(
name|option
operator|.
name|equals
argument_list|(
literal|"auto"
argument_list|)
condition|)
name|validation
operator|=
name|VALIDATION_AUTO
expr_stmt|;
else|else
name|validation
operator|=
name|VALIDATION_DISABLED
expr_stmt|;
block|}
comment|// create a SAX parser
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
try|try
block|{
name|saxFactory
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
try|try
block|{
comment|// TODO check does this work?
comment|// http://xerces.apache.org/xerces2-j/features.html
name|saxFactory
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
name|saxFactory
operator|.
name|setFeature
argument_list|(
literal|"http://apache.org/xml/features/nonvalidating/load-external-dtd"
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
name|saxFactory
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
name|saxFactory
operator|.
name|setFeature
argument_list|(
literal|"http://apache.org/xml/features/validation/schema"
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
block|}
catch|catch
parameter_list|(
name|SAXNotRecognizedException
name|e1
parameter_list|)
block|{
comment|// ignore: feature only recognized by xerces
block|}
catch|catch
parameter_list|(
name|SAXNotSupportedException
name|e1
parameter_list|)
block|{
comment|// ignore: feature only recognized by xerces
block|}
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|parser
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
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
name|parser
operator|.
name|setProperty
argument_list|(
name|PROPERTIES_RESOLVER
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
return|return
name|parser
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
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

