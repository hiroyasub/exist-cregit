begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|expression
package|;
end_package

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
name|util
operator|.
name|ExpressionDumper
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
name|Sequence
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
name|XSLContext
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
name|Attr
import|;
end_import

begin_comment
comment|/**  *<!-- Category: declaration -->  *<xsl:output  *   name? = qname  *   method? = "xml" | "html" | "xhtml" | "text" | qname-but-not-ncname  *   byte-order-mark? = "yes" | "no"  *   cdata-section-elements? = qnames  *   doctype-public? = string  *   doctype-system? = string  *   encoding? = string  *   escape-uri-attributes? = "yes" | "no"  *   include-content-type? = "yes" | "no"  *   indent? = "yes" | "no"  *   media-type? = string  *   normalization-form? = "NFC" | "NFD" | "NFKC" | "NFKD" | "fully-normalized" | "none" | nmtoken  *   omit-xml-declaration? = "yes" | "no"  *   standalone? = "yes" | "no" | "omit"  *   undeclare-prefixes? = "yes" | "no"  *   use-character-maps? = qnames  *   version? = nmtoken />  *   * @author shabanovd  *  */
end_comment

begin_class
specifier|public
class|class
name|Output
extends|extends
name|Declaration
block|{
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|method
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|byte_order_mark
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|cdata_section_elements
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|doctype_public
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|doctype_system
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|escape_uri_attributes
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|include_content_type
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|indent
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|media_type
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|normalization_form
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|omit_xml_declaration
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|standalone
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|undeclare_prefixes
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|use_character_maps
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|version
init|=
literal|null
decl_stmt|;
specifier|public
name|Output
parameter_list|(
name|XSLContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setToDefaults
parameter_list|()
block|{
name|name
operator|=
literal|null
expr_stmt|;
name|method
operator|=
literal|null
expr_stmt|;
name|byte_order_mark
operator|=
literal|null
expr_stmt|;
name|cdata_section_elements
operator|=
literal|null
expr_stmt|;
name|doctype_public
operator|=
literal|null
expr_stmt|;
name|doctype_system
operator|=
literal|null
expr_stmt|;
name|encoding
operator|=
literal|null
expr_stmt|;
name|escape_uri_attributes
operator|=
literal|null
expr_stmt|;
name|include_content_type
operator|=
literal|null
expr_stmt|;
name|indent
operator|=
literal|null
expr_stmt|;
name|media_type
operator|=
literal|null
expr_stmt|;
name|normalization_form
operator|=
literal|null
expr_stmt|;
name|omit_xml_declaration
operator|=
literal|null
expr_stmt|;
name|standalone
operator|=
literal|null
expr_stmt|;
name|undeclare_prefixes
operator|=
literal|null
expr_stmt|;
name|use_character_maps
operator|=
literal|null
expr_stmt|;
name|version
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|prepareAttribute
parameter_list|(
name|Attr
name|attr
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|attr_name
init|=
name|attr
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NAME
argument_list|)
condition|)
block|{
name|name
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|METHOD
argument_list|)
condition|)
block|{
name|method
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|BYTE_ORDER_MARK
argument_list|)
condition|)
block|{
name|byte_order_mark
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|CDATA_SECTION_ELEMENTS
argument_list|)
condition|)
block|{
name|cdata_section_elements
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DOCTYPE_PUBLIC
argument_list|)
condition|)
block|{
name|doctype_public
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|DOCTYPE_SYSTEM
argument_list|)
condition|)
block|{
name|doctype_system
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|ENCODING
argument_list|)
condition|)
block|{
name|encoding
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|ESCAPE_URI_ATTRIBUTES
argument_list|)
condition|)
block|{
name|escape_uri_attributes
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|INCLUDE_CONTENT_TYPE
argument_list|)
condition|)
block|{
name|include_content_type
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|INDENT
argument_list|)
condition|)
block|{
name|indent
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|MEDIA_TYPE
argument_list|)
condition|)
block|{
name|media_type
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|NORMALIZATION_FORM
argument_list|)
condition|)
block|{
name|normalization_form
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|OMIT_XML_DECLARATION
argument_list|)
condition|)
block|{
name|omit_xml_declaration
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|STANDALONE
argument_list|)
condition|)
block|{
name|standalone
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|UNDECLARE_PREFIXES
argument_list|)
condition|)
block|{
name|undeclare_prefixes
operator|=
name|getBoolean
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|USE_CHARACTER_MAPS
argument_list|)
condition|)
block|{
name|use_character_maps
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|attr_name
operator|.
name|equals
argument_list|(
name|VERSION
argument_list|)
condition|)
block|{
name|version
operator|=
name|attr
operator|.
name|getValue
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//TODO: output eval
comment|//throw new RuntimeException("eval(Sequence contextSequence, Item contextItem) at "+this.getClass());
comment|//	default output properties for the XML serialization
comment|//	    public final static Properties OUTPUT_PROPERTIES = new Properties();
comment|//	    static {
comment|//	        OUTPUT_PROPERTIES.setProperty(OutputKeys.INDENT, "yes");
comment|//	        OUTPUT_PROPERTIES.setProperty(OutputKeys.ENCODING, "UTF-8");
comment|//	        OUTPUT_PROPERTIES.setProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
comment|//	        OUTPUT_PROPERTIES.setProperty(EXistOutputKeys.EXPAND_XINCLUDES, "no");
comment|//	        OUTPUT_PROPERTIES.setProperty(EXistOutputKeys.PROCESS_XSL_PI, "no");
comment|//	    }
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"<xsl:output"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" name = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" method = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|method
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|byte_order_mark
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" byte_order_mark = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|byte_order_mark
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|cdata_section_elements
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" cdata_section_elements = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|cdata_section_elements
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doctype_public
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" doctype_public = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|doctype_public
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|doctype_system
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" doctype_system = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|doctype_system
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" encoding = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|encoding
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|escape_uri_attributes
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" escape_uri_attributes = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|escape_uri_attributes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|include_content_type
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" include_content_type = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|include_content_type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|indent
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" indent = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|indent
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|media_type
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" media_type = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|media_type
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|normalization_form
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" normalization_form = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|normalization_form
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|omit_xml_declaration
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" omit_xml_declaration = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|omit_xml_declaration
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|standalone
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" standalone = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|standalone
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|undeclare_prefixes
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" undeclare_prefixes = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|undeclare_prefixes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|use_character_maps
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" use_character_maps = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|use_character_maps
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" version = "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|version
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"</xsl:output>"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<xsl:output"
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" name = "
operator|+
name|name
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|method
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" method = "
operator|+
name|method
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|byte_order_mark
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" byte_order_mark = "
operator|+
name|byte_order_mark
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|cdata_section_elements
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" cdata_section_elements = "
operator|+
name|cdata_section_elements
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|doctype_public
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" doctype_public = "
operator|+
name|doctype_public
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|doctype_system
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" doctype_system = "
operator|+
name|doctype_system
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" encoding = "
operator|+
name|encoding
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|escape_uri_attributes
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" escape_uri_attributes = "
operator|+
name|escape_uri_attributes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|include_content_type
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" include_content_type = "
operator|+
name|include_content_type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|indent
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" indent = "
operator|+
name|indent
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|media_type
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" media_type = "
operator|+
name|media_type
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|normalization_form
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" normalization_form = "
operator|+
name|normalization_form
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|omit_xml_declaration
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" omit_xml_declaration = "
operator|+
name|omit_xml_declaration
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|standalone
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" standalone = "
operator|+
name|standalone
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|undeclare_prefixes
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" undeclare_prefixes = "
operator|+
name|undeclare_prefixes
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|use_character_maps
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" use_character_maps = "
operator|+
name|use_character_maps
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|version
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" version = "
operator|+
name|version
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"> "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|super
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"</xsl:output> "
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

