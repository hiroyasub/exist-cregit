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
name|compiler
package|;
end_package

begin_comment
comment|/**  * @author shabanovd  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Names
block|{
specifier|public
specifier|static
specifier|final
name|String
name|XMLNS
init|=
literal|"xmlns"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXTENSION_ELEMENT_PREFIXES
init|=
literal|"extension-element-prefixes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|EXCLUDE_RESULT_PREFIXES
init|=
literal|"exclude-result-prefixes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VERSION
init|=
literal|"version"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|XPATH_DEFAULT_NAMESPACE
init|=
literal|"xpath-default-namespace"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_VALIDATION
init|=
literal|"default-validation"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_COLLATION
init|=
literal|"default-collation"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INPUT_TYPE_ANNOTATIONS
init|=
literal|"input-type-annotations"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRESERVE
init|=
literal|"preserve"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STRIP
init|=
literal|"strip"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|UNSPECIFIED
init|=
literal|"unspecified"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SELECT
init|=
literal|"select"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SEPARATOR
init|=
literal|"separator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DISABLE_OUTPUT_ESCAPING
init|=
literal|"disable-output-escaping"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|YES
init|=
literal|"yes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NO
init|=
literal|"no"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH
init|=
literal|"match"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PRIORITY
init|=
literal|"priority"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MODE
init|=
literal|"mode"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|AS
init|=
literal|"as"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|HREF
init|=
literal|"href"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USE_ATTRIBUTE_SETS
init|=
literal|"use-attribute-sets"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USE_CHARACTER_MAPS
init|=
literal|"use-character-maps"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DECIMAL_SEPARATOR
init|=
literal|"decimal_separator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUPING_SEPARATOR
init|=
literal|"grouping-separator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INFINITY
init|=
literal|"infinity"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MINUS_SIGN
init|=
literal|"minus-sign"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAN
init|=
literal|"NaN"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PERCENT
init|=
literal|"percent"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PER_MILLE
init|=
literal|"per-mille"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ZERO_DIGIT
init|=
literal|"zero-digit"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DIGIT
init|=
literal|"digit"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PATTERN_SEPARATOR
init|=
literal|"pattern-separator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OVERRIDE
init|=
literal|"override"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE
init|=
literal|"namespace"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|SCHEMA_LOCATION
init|=
literal|"schema-location"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|USE
init|=
literal|"use"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COLLATION
init|=
literal|"collation"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STYLESHEET_PREFIX
init|=
literal|"stylesheet-prefix"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|RESULT_PREFIX
init|=
literal|"result-prefix"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|METHOD
init|=
literal|"method"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|BYTE_ORDER_MARK
init|=
literal|"byte-order-mark"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CDATA_SECTION_ELEMENTS
init|=
literal|"cdata-section-elements"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DOCTYPE_PUBLIC
init|=
literal|"doctype-public"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DOCTYPE_SYSTEM
init|=
literal|"doctype-system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ENCODING
init|=
literal|"encoding"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ESCAPE_URI_ATTRIBUTES
init|=
literal|"escape-uri-attributes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INCLUDE_CONTENT_TYPE
init|=
literal|"include-content-type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDENT
init|=
literal|"indent"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MEDIA_TYPE
init|=
literal|"media-type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|NORMALIZATION_FORM
init|=
literal|"normalization-form"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OMIT_XML_DECLARATION
init|=
literal|"omit-xml-declaration"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STANDALONE
init|=
literal|"standalone"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|UNDECLARE_PREFIXES
init|=
literal|"undeclare-prefixes"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REQUIRED
init|=
literal|"required"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TUNNEL
init|=
literal|"tunnel"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ELEMENTS
init|=
literal|"elements"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VALIDATION
init|=
literal|"validation"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COPY_NAMESPACES
init|=
literal|"copy-namespaces"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INHERIT_NAMESPACES
init|=
literal|"inherit-namespaces"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FORMAT
init|=
literal|"format"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OUTPUT_VERSION
init|=
literal|"output-version"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TERMINATE
init|=
literal|"terminate"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|REGEX
init|=
literal|"regex"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FLAGS
init|=
literal|"flags"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TEST
init|=
literal|"test"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_BY
init|=
literal|"group-by"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_ADJACENT
init|=
literal|"group-adjacent"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_STARTING_WITH
init|=
literal|"group-starting-with"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUP_ENDING_WITH
init|=
literal|"group-ending-with"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|VALUE
init|=
literal|"value"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LEVEL
init|=
literal|"level"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|COUNT
init|=
literal|"count"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FROM
init|=
literal|"from"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LANG
init|=
literal|"lang"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|LETTER_VALUE
init|=
literal|"letter-value"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ORDINAL
init|=
literal|"ordinal"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|GROUPING_SIZE
init|=
literal|"grouping-size"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CHARACTER
init|=
literal|"character"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STRING
init|=
literal|"string"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|ORDER
init|=
literal|"order"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|STABLE
init|=
literal|"stable"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CASE_ORDER
init|=
literal|"case-order"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DATA_TYPE
init|=
literal|"data-type"
decl_stmt|;
comment|//	public static final String AA = "aa";
block|}
end_interface

end_unit

