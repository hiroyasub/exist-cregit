begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|lucene
operator|.
name|facet
operator|.
name|DrillDownQuery
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
name|facet
operator|.
name|FacetsConfig
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParser
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
name|queryparser
operator|.
name|classic
operator|.
name|QueryParserBase
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
name|queryparser
operator|.
name|flexible
operator|.
name|standard
operator|.
name|CommonQueryParserConfiguration
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
name|search
operator|.
name|MultiTermQuery
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
name|search
operator|.
name|Query
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|stax
operator|.
name|ExtendedXMLStreamReader
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
name|functions
operator|.
name|array
operator|.
name|ArrayType
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
name|functions
operator|.
name|map
operator|.
name|AbstractMapType
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
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamReader
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
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|lucene
operator|.
name|QueryOptions
operator|.
name|DefaultOperator
operator|.
name|OR
import|;
end_import

begin_class
specifier|public
class|class
name|QueryOptions
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_DEFAULT_OPERATOR
init|=
literal|"default-operator"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_PHRASE_SLOP
init|=
literal|"phrase-slop"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_LEADING_WILDCARD
init|=
literal|"leading-wildcard"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_FILTER_REWRITE
init|=
literal|"filter-rewrite"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|DEFAULT_OPERATOR_OR
init|=
literal|"or"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_LOWERCASE_EXPANDED_TERMS
init|=
literal|"lowercase-expanded-terms"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_FACETS
init|=
literal|"facets"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_FIELDS
init|=
literal|"fields"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|OPTION_QUERY_ANALYZER_ID
init|=
literal|"query-analyzer-id"
decl_stmt|;
specifier|protected
enum|enum
name|DefaultOperator
block|{
name|OR
block|,
name|AND
block|}
specifier|protected
name|String
name|queryAnalyzerId
init|=
literal|null
decl_stmt|;
specifier|protected
name|DefaultOperator
name|defaultOperator
init|=
name|DefaultOperator
operator|.
name|AND
decl_stmt|;
specifier|protected
name|boolean
name|allowLeadingWildcard
init|=
literal|false
decl_stmt|;
specifier|protected
name|Optional
argument_list|<
name|Integer
argument_list|>
name|phraseSlop
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|protected
name|boolean
name|filterRewrite
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|lowercaseExpandedTerms
init|=
literal|false
decl_stmt|;
specifier|protected
name|Optional
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|FacetQuery
argument_list|>
argument_list|>
name|facets
init|=
name|Optional
operator|.
name|empty
argument_list|()
decl_stmt|;
specifier|protected
name|Set
argument_list|<
name|String
argument_list|>
name|fields
init|=
literal|null
decl_stmt|;
specifier|public
name|QueryOptions
parameter_list|()
block|{
comment|// default options
block|}
specifier|public
name|QueryOptions
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|NodeValue
name|root
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|int
name|thisLevel
init|=
name|root
operator|.
name|getNodeId
argument_list|()
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
specifier|final
name|XMLStreamReader
name|reader
init|=
name|context
operator|.
name|getXMLStreamReader
argument_list|(
name|root
argument_list|)
decl_stmt|;
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
name|reader
operator|.
name|next
argument_list|()
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|int
name|status
init|=
name|reader
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|START_ELEMENT
condition|)
block|{
name|set
argument_list|(
name|reader
operator|.
name|getLocalName
argument_list|()
argument_list|,
name|reader
operator|.
name|getElementText
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|status
operator|==
name|XMLStreamReader
operator|.
name|END_ELEMENT
condition|)
block|{
specifier|final
name|NodeId
name|otherId
init|=
operator|(
name|NodeId
operator|)
name|reader
operator|.
name|getProperty
argument_list|(
name|ExtendedXMLStreamReader
operator|.
name|PROPERTY_NODE_ID
argument_list|)
decl_stmt|;
specifier|final
name|int
name|otherLevel
init|=
name|otherId
operator|.
name|getTreeLevel
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherLevel
operator|==
name|thisLevel
condition|)
block|{
comment|// finished `optRoot` element...
break|break;
comment|// exit-while
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|XMLStreamException
decl||
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|LuceneModule
operator|.
name|EXXQDYFT0004
argument_list|,
literal|"Error while parsing options to ft:query: "
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
name|QueryOptions
parameter_list|(
name|AbstractMapType
name|map
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|map
control|)
block|{
specifier|final
name|String
name|key
init|=
name|entry
operator|.
name|getKey
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|OPTION_FIELDS
argument_list|)
operator|&&
operator|!
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|fields
operator|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|fields
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|key
operator|.
name|equals
argument_list|(
name|OPTION_FACETS
argument_list|)
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|hasOne
argument_list|()
operator|&&
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|MAP
condition|)
block|{
comment|// map to hold the facet values for each dimension
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|FacetQuery
argument_list|>
name|tf
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
comment|// iterate over each dimension and collect its values into a FacetQuery
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|facet
range|:
operator|(
name|AbstractMapType
operator|)
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
control|)
block|{
specifier|final
name|Sequence
name|value
init|=
name|facet
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|FacetQuery
name|values
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|hasOne
argument_list|()
operator|&&
name|value
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
name|values
operator|=
operator|new
name|FacetQuery
argument_list|(
operator|(
name|ArrayType
operator|)
name|facet
operator|.
name|getValue
argument_list|()
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|values
operator|=
operator|new
name|FacetQuery
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
name|tf
operator|.
name|put
argument_list|(
name|facet
operator|.
name|getKey
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
name|facets
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|tf
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|set
argument_list|(
name|key
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Holds the values of a facet for drill down. To support      * multiple query values for a hierarchical facet, values are      * kept in a two-dimensional list.      */
specifier|public
specifier|static
class|class
name|FacetQuery
block|{
specifier|final
name|List
argument_list|<
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|values
decl_stmt|;
comment|/**          * Create a single query value from a flat sequence.          *          * @param input input sequence          * @throws XPathException in case of conversion errors          */
specifier|public
name|FacetQuery
parameter_list|(
specifier|final
name|Sequence
name|input
parameter_list|)
throws|throws
name|XPathException
block|{
name|values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|subValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|input
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|si
init|=
name|input
operator|.
name|unorderedIterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|value
init|=
name|si
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|subValues
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|values
operator|.
name|add
argument_list|(
name|subValues
argument_list|)
expr_stmt|;
block|}
comment|/**          * Create a multi-valued query from an XQuery array.          *          * @param input an XQuery array          * @throws XPathException in case of conversion errors          */
specifier|public
name|FacetQuery
parameter_list|(
specifier|final
name|ArrayType
name|input
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|items
index|[]
init|=
name|input
operator|.
name|toArray
argument_list|()
decl_stmt|;
name|values
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|items
operator|.
name|length
argument_list|)
expr_stmt|;
for|for
control|(
name|Sequence
name|seq
range|:
name|items
control|)
block|{
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|subValues
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|si
init|=
name|seq
operator|.
name|unorderedIterator
argument_list|()
init|;
name|si
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|String
name|value
init|=
name|si
operator|.
name|nextItem
argument_list|()
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|subValues
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|values
operator|.
name|add
argument_list|(
name|subValues
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**          * Add the values for the facet dimension to the drill down query.          *          * @param dimension the facet dimension          * @param query the lucene drill down query          * @param hierarchical true if the facet is hierarchical          */
specifier|public
name|void
name|toQuery
parameter_list|(
specifier|final
name|String
name|dimension
parameter_list|,
specifier|final
name|DrillDownQuery
name|query
parameter_list|,
specifier|final
name|boolean
name|hierarchical
parameter_list|)
block|{
for|for
control|(
name|List
argument_list|<
name|String
argument_list|>
name|subValues
range|:
name|values
control|)
block|{
if|if
condition|(
name|hierarchical
condition|)
block|{
specifier|final
name|String
index|[]
name|result
init|=
operator|new
name|String
index|[
name|subValues
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|subValues
operator|.
name|toArray
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|query
operator|.
name|add
argument_list|(
name|dimension
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|String
name|value
range|:
name|subValues
control|)
block|{
name|query
operator|.
name|add
argument_list|(
name|dimension
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
specifier|public
name|Optional
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|FacetQuery
argument_list|>
argument_list|>
name|getFacets
parameter_list|()
block|{
return|return
name|facets
return|;
block|}
specifier|public
annotation|@
name|Nullable
name|Set
argument_list|<
name|String
argument_list|>
name|getFields
parameter_list|()
block|{
return|return
name|fields
return|;
block|}
specifier|public
name|boolean
name|filterRewrite
parameter_list|()
block|{
return|return
name|filterRewrite
return|;
block|}
specifier|private
name|void
name|set
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|key
condition|)
block|{
case|case
name|OPTION_DEFAULT_OPERATOR
case|:
if|if
condition|(
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
name|DEFAULT_OPERATOR_OR
argument_list|)
condition|)
block|{
name|defaultOperator
operator|=
name|OR
expr_stmt|;
block|}
break|break;
case|case
name|OPTION_LEADING_WILDCARD
case|:
name|allowLeadingWildcard
operator|=
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPTION_PHRASE_SLOP
case|:
try|try
block|{
name|phraseSlop
operator|=
name|Optional
operator|.
name|of
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
argument_list|)
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
name|XPathException
argument_list|(
name|LuceneModule
operator|.
name|EXXQDYFT0004
argument_list|,
literal|"Option "
operator|+
name|OPTION_PHRASE_SLOP
operator|+
literal|" must be an integer"
argument_list|)
throw|;
block|}
break|break;
case|case
name|OPTION_FILTER_REWRITE
case|:
name|filterRewrite
operator|=
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPTION_LOWERCASE_EXPANDED_TERMS
case|:
name|lowercaseExpandedTerms
operator|=
name|value
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
break|break;
case|case
name|OPTION_QUERY_ANALYZER_ID
case|:
name|queryAnalyzerId
operator|=
name|value
expr_stmt|;
default|default:
comment|// unknown option, ignore
break|break;
block|}
block|}
specifier|public
name|void
name|configureParser
parameter_list|(
name|CommonQueryParserConfiguration
name|parser
parameter_list|)
block|{
if|if
condition|(
name|parser
operator|instanceof
name|QueryParserBase
condition|)
block|{
switch|switch
condition|(
name|defaultOperator
condition|)
block|{
case|case
name|OR
case|:
operator|(
operator|(
name|QueryParserBase
operator|)
name|parser
operator|)
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|OR_OPERATOR
argument_list|)
expr_stmt|;
break|break;
default|default:
operator|(
operator|(
name|QueryParserBase
operator|)
name|parser
operator|)
operator|.
name|setDefaultOperator
argument_list|(
name|QueryParser
operator|.
name|AND_OPERATOR
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|allowLeadingWildcard
condition|)
name|parser
operator|.
name|setAllowLeadingWildcard
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|if
condition|(
name|phraseSlop
operator|.
name|isPresent
argument_list|()
condition|)
block|{
name|parser
operator|.
name|setPhraseSlop
argument_list|(
name|phraseSlop
operator|.
name|get
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filterRewrite
condition|)
name|parser
operator|.
name|setMultiTermRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_FILTER_REWRITE
argument_list|)
expr_stmt|;
else|else
name|parser
operator|.
name|setMultiTermRewriteMethod
argument_list|(
name|MultiTermQuery
operator|.
name|CONSTANT_SCORE_BOOLEAN_QUERY_REWRITE
argument_list|)
expr_stmt|;
if|if
condition|(
name|lowercaseExpandedTerms
condition|)
block|{
name|parser
operator|.
name|setLowercaseExpandedTerms
argument_list|(
name|lowercaseExpandedTerms
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getQueryAnalyzerId
parameter_list|()
block|{
return|return
name|queryAnalyzerId
return|;
block|}
block|}
end_class

end_unit

