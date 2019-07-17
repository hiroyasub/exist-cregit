begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2015 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|collections
operator|.
name|MultiMap
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
name|collections
operator|.
name|map
operator|.
name|MultiValueMap
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
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
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
name|apache
operator|.
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|dom
operator|.
name|persistent
operator|.
name|AttrImpl
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
name|ElementValue
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
name|NodePath
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
name|DatabaseConfigurationException
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
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneIndexConfig
block|{
specifier|private
specifier|final
specifier|static
name|String
name|N_INLINE
init|=
literal|"inline"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|N_IGNORE
init|=
literal|"ignore"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|IGNORE_ELEMENT
init|=
literal|"ignore"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|INLINE_ELEMENT
init|=
literal|"inline"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MATCH_ATTR_ELEMENT
init|=
literal|"match-attribute"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|HAS_ATTR_ELEMENT
init|=
literal|"has-attribute"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|MATCH_SIBLING_ATTR_ELEMENT
init|=
literal|"match-sibling-attribute"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|HAS_SIBLING_ATTR_ELEMENT
init|=
literal|"has-sibling-attribute"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FACET_ELEMENT
init|=
literal|"facet"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FIELD_ELEMENT
init|=
literal|"field"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|QNAME_ATTR
init|=
literal|"qname"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|MATCH_ATTR
init|=
literal|"match"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|FIELD_ATTR
init|=
literal|"field"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TYPE_ATTR
init|=
literal|"type"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|INDEX_ATTR
init|=
literal|"index"
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|NodePathPattern
name|path
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isQNameIndex
init|=
literal|false
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|String
argument_list|>
name|specialNodes
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|AbstractFieldConfig
argument_list|>
name|facetsAndFields
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|LuceneIndexConfig
name|nextConfig
init|=
literal|null
decl_stmt|;
specifier|private
name|FieldType
name|type
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|doIndex
init|=
literal|true
decl_stmt|;
comment|// This is for the @attr match boosting
comment|// and the intention is to do a proper predicate check instead in the future. /ljo
specifier|private
name|MultiMap
name|matchAttrs
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|LuceneIndexConfig
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|LuceneIndexConfig
parameter_list|(
name|LuceneConfig
name|parent
parameter_list|,
name|Element
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|AnalyzerConfig
name|analyzers
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|FieldType
argument_list|>
name|fieldTypes
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
condition|)
block|{
name|QName
name|qname
init|=
name|parseQName
argument_list|(
name|config
argument_list|,
name|namespaces
argument_list|)
decl_stmt|;
name|path
operator|=
operator|new
name|NodePathPattern
argument_list|(
name|qname
argument_list|)
expr_stmt|;
name|isQNameIndex
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|String
name|matchPath
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|MATCH_ATTR
argument_list|)
decl_stmt|;
try|try
block|{
name|path
operator|=
operator|new
name|NodePathPattern
argument_list|(
name|namespaces
argument_list|,
name|matchPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: Invalid match path in collection config: "
operator|+
name|matchPath
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: invalid qname in configuration: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|String
name|name
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|FIELD_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|!=
literal|null
operator|&&
name|name
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|setName
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|String
name|fieldType
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|TYPE_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldType
operator|!=
literal|null
operator|&&
name|fieldType
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|type
operator|=
name|fieldTypes
operator|.
name|get
argument_list|(
name|fieldType
argument_list|)
expr_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
name|type
operator|=
operator|new
name|FieldType
argument_list|(
name|config
argument_list|,
name|analyzers
argument_list|)
expr_stmt|;
name|String
name|indexParam
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|INDEX_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|indexParam
operator|!=
literal|null
operator|&&
name|indexParam
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|doIndex
operator|=
literal|"yes"
operator|.
name|equalsIgnoreCase
argument_list|(
name|indexParam
argument_list|)
operator|||
literal|"true"
operator|.
name|equalsIgnoreCase
argument_list|(
name|indexParam
argument_list|)
expr_stmt|;
block|}
name|parse
argument_list|(
name|parent
argument_list|,
name|config
argument_list|,
name|namespaces
argument_list|,
name|analyzers
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|parse
parameter_list|(
name|LuceneConfig
name|parent
parameter_list|,
name|Element
name|root
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|,
name|AnalyzerConfig
name|analyzers
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|Node
name|child
init|=
name|root
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|child
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|child
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
specifier|final
name|String
name|localName
init|=
name|child
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|localName
condition|)
block|{
name|Element
name|configElement
init|=
operator|(
name|Element
operator|)
name|child
decl_stmt|;
switch|switch
condition|(
name|localName
condition|)
block|{
case|case
name|FACET_ELEMENT
case|:
block|{
name|facetsAndFields
operator|.
name|add
argument_list|(
operator|new
name|LuceneFacetConfig
argument_list|(
name|parent
argument_list|,
name|configElement
argument_list|,
name|namespaces
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|FIELD_ELEMENT
case|:
block|{
specifier|final
name|LuceneFieldConfig
name|fieldConfig
init|=
operator|new
name|LuceneFieldConfig
argument_list|(
name|parent
argument_list|,
name|configElement
argument_list|,
name|namespaces
argument_list|,
name|analyzers
argument_list|)
decl_stmt|;
name|facetsAndFields
operator|.
name|add
argument_list|(
name|fieldConfig
argument_list|)
expr_stmt|;
if|if
condition|(
name|fieldConfig
operator|.
name|getAnalyzer
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|type
operator|.
name|addAnalzer
argument_list|(
name|fieldConfig
operator|.
name|getName
argument_list|()
argument_list|,
name|fieldConfig
operator|.
name|getAnalyzer
argument_list|()
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
name|IGNORE_ELEMENT
case|:
block|{
name|String
name|qnameAttr
init|=
name|configElement
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|qnameAttr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element 'ignore' needs an attribute 'qname'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|specialNodes
operator|==
literal|null
condition|)
block|{
name|specialNodes
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|specialNodes
operator|.
name|put
argument_list|(
name|parseQName
argument_list|(
name|qnameAttr
argument_list|,
name|namespaces
argument_list|)
argument_list|,
name|N_IGNORE
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|INLINE_ELEMENT
case|:
block|{
name|String
name|qnameAttr
init|=
name|configElement
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|qnameAttr
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element 'inline' needs an attribute 'qname'"
argument_list|)
throw|;
block|}
if|if
condition|(
name|specialNodes
operator|==
literal|null
condition|)
block|{
name|specialNodes
operator|=
operator|new
name|TreeMap
argument_list|<>
argument_list|()
expr_stmt|;
block|}
name|specialNodes
operator|.
name|put
argument_list|(
name|parseQName
argument_list|(
name|qnameAttr
argument_list|,
name|namespaces
argument_list|)
argument_list|,
name|N_INLINE
argument_list|)
expr_stmt|;
break|break;
block|}
case|case
name|MATCH_SIBLING_ATTR_ELEMENT
case|:
case|case
name|HAS_SIBLING_ATTR_ELEMENT
case|:
case|case
name|HAS_ATTR_ELEMENT
case|:
case|case
name|MATCH_ATTR_ELEMENT
case|:
block|{
specifier|final
name|boolean
name|doMatch
init|=
name|localName
operator|.
name|equals
argument_list|(
name|MATCH_ATTR_ELEMENT
argument_list|)
operator|||
name|localName
operator|.
name|equals
argument_list|(
name|MATCH_SIBLING_ATTR_ELEMENT
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|onSibling
init|=
name|localName
operator|.
name|equals
argument_list|(
name|HAS_SIBLING_ATTR_ELEMENT
argument_list|)
operator|||
name|localName
operator|.
name|equals
argument_list|(
name|MATCH_SIBLING_ATTR_ELEMENT
argument_list|)
decl_stmt|;
if|if
condition|(
name|onSibling
operator|&&
operator|!
name|isAttributeNode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: "
operator|+
name|localName
operator|+
literal|" can only be used on attribute"
argument_list|)
throw|;
block|}
if|else if
condition|(
operator|!
name|onSibling
operator|&&
name|isAttributeNode
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene module: "
operator|+
name|localName
operator|+
literal|" can not be used on attribute"
argument_list|)
throw|;
block|}
specifier|final
name|String
name|qname
init|=
name|configElement
operator|.
name|getAttribute
argument_list|(
literal|"qname"
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|qname
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element '"
operator|+
name|localName
operator|+
literal|" needs an attribute 'qname'"
argument_list|)
throw|;
block|}
name|float
name|boost
decl_stmt|;
specifier|final
name|String
name|boostStr
init|=
name|configElement
operator|.
name|getAttribute
argument_list|(
literal|"boost"
argument_list|)
decl_stmt|;
try|try
block|{
name|boost
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|boostStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Invalid value for attribute 'boost'. "
operator|+
literal|"Expected float, got: "
operator|+
name|boostStr
argument_list|)
throw|;
block|}
name|String
name|value
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|doMatch
condition|)
block|{
name|value
operator|=
name|configElement
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
expr_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|value
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene configuration element '"
operator|+
name|localName
operator|+
literal|" needs an attribute 'value'"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|matchAttrs
operator|==
literal|null
condition|)
name|matchAttrs
operator|=
operator|new
name|MultiValueMap
argument_list|()
expr_stmt|;
name|matchAttrs
operator|.
name|put
argument_list|(
name|qname
argument_list|,
operator|new
name|MatchAttrData
argument_list|(
name|qname
argument_list|,
name|value
argument_list|,
name|boost
argument_list|,
name|onSibling
argument_list|)
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
name|child
operator|=
name|child
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|boolean
name|hasFieldsOrFacets
parameter_list|()
block|{
return|return
operator|!
name|facetsAndFields
operator|.
name|isEmpty
argument_list|()
return|;
block|}
comment|// return saved Analyzer for use in LuceneMatchListener
specifier|public
name|Analyzer
name|getAnalyzer
parameter_list|()
block|{
return|return
name|type
operator|.
name|getAnalyzer
argument_list|()
return|;
block|}
specifier|public
name|String
name|getAnalyzerId
parameter_list|()
block|{
return|return
name|type
operator|.
name|getAnalyzerId
argument_list|()
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|path
operator|.
name|getLastComponent
argument_list|()
return|;
block|}
specifier|public
name|NodePathPattern
name|getNodePathPattern
parameter_list|()
block|{
return|return
name|path
return|;
block|}
specifier|public
name|boolean
name|doIndex
parameter_list|()
block|{
return|return
name|this
operator|.
name|doIndex
return|;
block|}
specifier|public
name|float
name|getBoost
parameter_list|()
block|{
return|return
name|type
operator|.
name|getBoost
argument_list|()
return|;
block|}
comment|/**      * Get boost by matching the config with given attributes      * (e.g. sibling or child atributes)      * if no match, the value from getBoost() is returned      *      * @param attributes the attributes      *      * @return the boost, or 0 if there is no boost      */
specifier|public
name|float
name|getAttrBoost
parameter_list|(
specifier|final
name|Collection
argument_list|<
name|AttrImpl
argument_list|>
name|attributes
parameter_list|)
block|{
name|float
name|boost
init|=
literal|0
decl_stmt|;
name|boolean
name|hasBoost
init|=
literal|false
decl_stmt|;
for|for
control|(
specifier|final
name|Attr
name|attr
range|:
name|attributes
control|)
block|{
name|Collection
argument_list|<
name|MatchAttrData
argument_list|>
name|matchAttrData
init|=
operator|(
name|Collection
argument_list|<
name|MatchAttrData
argument_list|>
operator|)
name|matchAttrs
operator|.
name|get
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|matchAttrData
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
for|for
control|(
name|MatchAttrData
name|matchAttrDatum
range|:
name|matchAttrData
control|)
block|{
comment|// if matchAttr value is null we don't care about the value
if|if
condition|(
name|matchAttrDatum
operator|.
name|value
operator|==
literal|null
operator|||
name|matchAttrDatum
operator|.
name|value
operator|.
name|equals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|)
condition|)
block|{
name|hasBoost
operator|=
literal|true
expr_stmt|;
name|boost
operator|+=
name|matchAttrDatum
operator|.
name|boost
expr_stmt|;
comment|// we matched the attribute already, but since we allow
comment|// further boost on the attribute, e g
comment|// both from "has-attribute" and "match-attribute"
comment|// there is no break here
block|}
block|}
block|}
if|if
condition|(
name|hasBoost
condition|)
block|{
return|return
name|boost
return|;
block|}
else|else
block|{
return|return
name|getBoost
argument_list|()
return|;
block|}
block|}
specifier|public
name|void
name|setName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|LuceneIndexConfig
name|config
parameter_list|)
block|{
if|if
condition|(
name|nextConfig
operator|==
literal|null
condition|)
name|nextConfig
operator|=
name|config
expr_stmt|;
else|else
name|nextConfig
operator|.
name|add
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneIndexConfig
name|getNext
parameter_list|()
block|{
return|return
name|nextConfig
return|;
block|}
specifier|private
name|boolean
name|isAttributeNode
parameter_list|()
block|{
return|return
name|path
operator|.
name|getLastComponent
argument_list|()
operator|.
name|getNameType
argument_list|()
operator|==
name|ElementValue
operator|.
name|ATTRIBUTE
return|;
block|}
comment|/**      * @return true if this index can be queried by name      */
specifier|public
name|boolean
name|isNamed
parameter_list|()
block|{
return|return
name|name
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|isIgnoredNode
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|specialNodes
operator|!=
literal|null
operator|&&
name|specialNodes
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|==
name|N_IGNORE
return|;
block|}
specifier|public
name|boolean
name|isInlineNode
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
return|return
name|specialNodes
operator|!=
literal|null
operator|&&
name|specialNodes
operator|.
name|get
argument_list|(
name|qname
argument_list|)
operator|==
name|N_INLINE
return|;
block|}
specifier|public
name|List
argument_list|<
name|AbstractFieldConfig
argument_list|>
name|getFacetsAndFields
parameter_list|()
block|{
return|return
name|facetsAndFields
return|;
block|}
specifier|public
specifier|static
name|QName
name|parseQName
parameter_list|(
name|Element
name|config
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|String
name|name
init|=
name|config
operator|.
name|getAttribute
argument_list|(
name|QNAME_ATTR
argument_list|)
decl_stmt|;
if|if
condition|(
name|StringUtils
operator|.
name|isEmpty
argument_list|(
name|name
argument_list|)
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index configuration error: element "
operator|+
name|config
operator|.
name|getNodeName
argument_list|()
operator|+
literal|" must have an attribute "
operator|+
name|QNAME_ATTR
argument_list|)
throw|;
return|return
name|parseQName
argument_list|(
name|name
argument_list|,
name|namespaces
argument_list|)
return|;
block|}
specifier|protected
specifier|static
name|QName
name|parseQName
parameter_list|(
name|String
name|name
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|boolean
name|isAttribute
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"@"
argument_list|)
condition|)
block|{
name|isAttribute
operator|=
literal|true
expr_stmt|;
name|name
operator|=
name|name
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|String
name|prefix
init|=
name|QName
operator|.
name|extractPrefix
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|localName
init|=
name|QName
operator|.
name|extractLocalName
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|String
name|namespaceURI
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|namespaceURI
operator|=
name|namespaces
operator|.
name|get
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
if|if
condition|(
name|namespaceURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"No namespace defined for prefix: "
operator|+
name|prefix
operator|+
literal|" in index definition"
argument_list|)
throw|;
block|}
block|}
specifier|final
name|QName
name|qname
decl_stmt|;
if|if
condition|(
name|isAttribute
condition|)
block|{
name|qname
operator|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|,
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qname
operator|=
operator|new
name|QName
argument_list|(
name|localName
argument_list|,
name|namespaceURI
argument_list|,
name|prefix
argument_list|)
expr_stmt|;
block|}
return|return
name|qname
return|;
block|}
catch|catch
parameter_list|(
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Lucene index configuration error: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|match
parameter_list|(
name|NodePath
name|other
parameter_list|)
block|{
if|if
condition|(
name|isQNameIndex
condition|)
block|{
specifier|final
name|QName
name|qn1
init|=
name|path
operator|.
name|getLastComponent
argument_list|()
decl_stmt|;
specifier|final
name|QName
name|qn2
init|=
name|other
operator|.
name|getLastComponent
argument_list|()
decl_stmt|;
return|return
name|qn1
operator|.
name|getNameType
argument_list|()
operator|==
name|qn2
operator|.
name|getNameType
argument_list|()
operator|&&
name|qn2
operator|.
name|equals
argument_list|(
name|qn1
argument_list|)
return|;
block|}
return|return
name|path
operator|.
name|match
argument_list|(
name|other
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|path
operator|.
name|toString
argument_list|()
return|;
block|}
name|boolean
name|shouldReindexOnAttributeChange
parameter_list|()
block|{
return|return
name|matchAttrs
operator|!=
literal|null
return|;
block|}
specifier|private
specifier|static
class|class
name|MatchAttrData
block|{
specifier|final
name|String
name|qname
decl_stmt|;
specifier|final
name|String
name|value
decl_stmt|;
specifier|final
name|float
name|boost
decl_stmt|;
specifier|final
name|boolean
name|onSibling
decl_stmt|;
name|MatchAttrData
parameter_list|(
name|String
name|qname
parameter_list|,
name|String
name|value
parameter_list|,
name|float
name|boost
parameter_list|,
name|boolean
name|onSibling
parameter_list|)
block|{
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|boost
operator|=
name|boost
expr_stmt|;
name|this
operator|.
name|onSibling
operator|=
name|onSibling
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

