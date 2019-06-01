begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|serializers
package|;
end_package

begin_class
specifier|public
class|class
name|EXistOutputKeys
block|{
comment|/**      * Parameter "item-separator" from the XQuery serialization spec 3.1      */
specifier|public
specifier|final
specifier|static
name|String
name|ITEM_SEPARATOR
init|=
literal|"item-separator"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|OUTPUT_DOCTYPE
init|=
literal|"output-doctype"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|EXPAND_XINCLUDES
init|=
literal|"expand-xincludes"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROCESS_XSL_PI
init|=
literal|"process-xsl-pi"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|HIGHLIGHT_MATCHES
init|=
literal|"highlight-matches"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INDENT_SPACES
init|=
literal|"indent-spaces"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STYLESHEET
init|=
literal|"stylesheet"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|STYLESHEET_PARAM
init|=
literal|"stylesheet-param"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|COMPRESS_OUTPUT
init|=
literal|"compress-output"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|ADD_EXIST_ID
init|=
literal|"add-exist-id"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XINCLUDE_PATH
init|=
literal|"xinclude-path"
decl_stmt|;
comment|/**      * Enforce XHTML namespace on elements with no namespace      */
specifier|public
specifier|final
specifier|static
name|String
name|ENFORCE_XHTML
init|=
literal|"enforce-xhtml"
decl_stmt|;
comment|/**      * Applies to JSON serialization only: preserve namespace prefixes in JSON properties      * by replacing ":" with "_", so element foo:bar becomes "foo_bar".      */
specifier|public
specifier|final
specifier|static
name|String
name|JSON_OUTPUT_NS_PREFIX
init|=
literal|"preserve-prefix"
decl_stmt|;
comment|/**      * Applies to JSON serialization only: sets the jsonp callback function      */
specifier|public
specifier|final
specifier|static
name|String
name|JSONP
init|=
literal|"jsonp"
decl_stmt|;
comment|/**      * JSON serialization: prefix XML attributes with a '@' when serializing      * them as JSON properties      */
specifier|public
specifier|final
specifier|static
name|String
name|JSON_PREFIX_ATTRIBUTES
init|=
literal|"prefix-attributes"
decl_stmt|;
comment|/**      * JSON serialization: if text nodes are encountered which consist solely of whitespace then they      * will be ignored by the serializer      */
specifier|public
specifier|final
specifier|static
name|String
name|JSON_IGNORE_WHITESPACE_TEXT_NODES
init|=
literal|"json-ignore-whitespace-text-nodes"
decl_stmt|;
comment|/**      * Defines the output method to be used for serializing nodes within json output.      */
specifier|public
specifier|final
specifier|static
name|String
name|JSON_NODE_OUTPUT_METHOD
init|=
literal|"json-node-output-method"
decl_stmt|;
comment|/**      * Defines the output for JSON serializing to array even if only one item.      */
specifier|public
specifier|final
specifier|static
name|String
name|JSON_ARRAY_OUTPUT
init|=
literal|"json-array-output"
decl_stmt|;
comment|/**      * Determines whether the presence of multiple keys in a map item with the same string value      * will or will not raise serialization error err:SERE0022.      */
specifier|public
specifier|final
specifier|static
name|String
name|ALLOW_DUPLICATE_NAMES
init|=
literal|"allow-duplicate-names"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|HTML_VERSION
init|=
literal|"html-version"
decl_stmt|;
comment|/**      * When serializing an XDM this should be used      * to enforce XDM serialization rules.      *      * XDM has different serialization rules      * compared to retrieving and serializing resources from the database.      *      * Set to "yes" to enable xdm-serialization rules, false otherwise.      */
specifier|public
specifier|final
specifier|static
name|String
name|XDM_SERIALIZATION
init|=
literal|"xdm-serialization"
decl_stmt|;
block|}
end_class

end_unit
