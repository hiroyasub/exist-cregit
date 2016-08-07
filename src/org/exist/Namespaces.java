begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2005-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_comment
comment|/**  * Global namespace declarations.  *   * @author wolf  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Namespaces
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DTD_NS
init|=
name|XMLConstants
operator|.
name|XML_DTD_NS_URI
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_NS
init|=
name|XMLConstants
operator|.
name|W3C_XML_SCHEMA_NS_URI
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_DATATYPES_NS
init|=
literal|"http://www.w3.org/2001/XMLSchema-datatypes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SCHEMA_INSTANCE_NS
init|=
name|XMLConstants
operator|.
name|W3C_XML_SCHEMA_INSTANCE_NS_URI
decl_stmt|;
comment|// Move this here from Function.BUILTIN_FUNCTION_NS? /ljo
specifier|public
specifier|final
specifier|static
name|String
name|XPATH_FUNCTIONS_NS
init|=
literal|"http://www.w3.org/2005/xpath-functions"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XQUERY_LOCAL_NS
init|=
literal|"http://www.w3.org/2005/xquery-local-functions"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XPATH_DATATYPES_NS
init|=
literal|"http://www.w3.org/2003/05/xpath-datatypes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XPATH_FUNCTIONS_MATH_NS
init|=
literal|"http://www.w3.org/2005/xpath-functions/math"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XQUERY_OPTIONS_NS
init|=
literal|"http://www.w3.org/2011/xquery-options"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XSLT_XQUERY_SERIALIZATION_NS
init|=
literal|"http://www.w3.org/2010/xslt-xquery-serialization"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|W3C_XQUERY_XPATH_ERROR_NS
init|=
literal|"http://www.w3.org/2005/xqt-errors"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|W3C_XQUERY_XPATH_ERROR_PREFIX
init|=
literal|"err"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XSL_NS
init|=
literal|"http://www.w3.org/1999/XSL/Transform"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_XQUERY_XPATH_ERROR_NS
init|=
literal|"http://www.exist-db.org/xqt-errors/"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_XQUERY_XPATH_ERROR_PREFIX
init|=
literal|"exerr"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_NS
init|=
literal|"http://exist.sourceforge.net/NS/exist"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXIST_NS_PREFIX
init|=
literal|"exist"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RDF_NS
init|=
literal|"http://www.w3.org/1999/02/22-rdf-syntax-ns#"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DC_NS
init|=
literal|"http://purl.org/dc/elements/1.1/"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XML_NS
init|=
name|XMLConstants
operator|.
name|XML_NS_URI
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XMLNS_NS
init|=
name|XMLConstants
operator|.
name|XMLNS_ATTRIBUTE_NS_URI
decl_stmt|;
comment|/** QName representing xml:id */
specifier|public
specifier|final
specifier|static
name|QName
name|XML_ID_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"id"
argument_list|,
name|XML_NS
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
comment|/** QName representing xml:space */
specifier|public
specifier|final
specifier|static
name|QName
name|XML_SPACE_QNAME
init|=
operator|new
name|QName
argument_list|(
literal|"space"
argument_list|,
name|XML_NS
argument_list|,
literal|"xml"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SOAP_ENVELOPE
init|=
literal|"http://schemas.xmlsoap.org/soap/envelope/"
decl_stmt|;
comment|//SAXfeatures / properties : move toadedicated package
specifier|public
specifier|final
specifier|static
name|String
name|SAX_LEXICAL_HANDLER
init|=
literal|"http://xml.org/sax/properties/lexical-handler"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SAX_NAMESPACES
init|=
literal|"http://xml.org/sax/features/namespaces"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SAX_NAMESPACES_PREFIXES
init|=
literal|"http://xml.org/sax/features/namespace-prefixes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SAX_VALIDATION
init|=
literal|"http://xml.org/sax/features/validation"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|SAX_VALIDATION_DYNAMIC
init|=
literal|"http://apache.org/xml/features/validation/dynamic"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XHTML_NS
init|=
literal|"http://www.w3.org/1999/xhtml"
decl_stmt|;
block|}
end_interface

end_unit

