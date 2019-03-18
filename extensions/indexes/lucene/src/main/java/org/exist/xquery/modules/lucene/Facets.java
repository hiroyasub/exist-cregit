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
name|FacetResult
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
name|FacetsCollector
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
name|Match
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
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndex
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexWorker
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
name|*
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
name|MapType
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_class
specifier|public
class|class
name|Facets
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
index|[]
name|signatures
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"facets"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Return a map of facet labels and counts for the result of a Lucene query. Facets and facet counts apply "
operator|+
literal|"to the entire sequence returned by ft:query, so the same map will be returned for all nodes in the sequence. "
operator|+
literal|"It is thus sufficient to specify one node from the sequence as first argument to this function."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A single node resulting from a call to ft:query for which facet information should be retrieved. "
operator|+
literal|"If the node has no facet information attached, an empty sequence will be returned."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dimension"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The facet dimension. This should correspond to a dimension defined in the index configuration"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"A map having the facet label as key and the facet count as value"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"facets"
argument_list|,
name|LuceneModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LuceneModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Return a map of facet labels and counts for the result of a Lucene query. Facets and facet counts apply "
operator|+
literal|"to the entire sequence returned by ft:query, so the same map will be returned for all nodes in the sequence. "
operator|+
literal|"It is thus sufficient to specify one node from the sequence as first argument to this function."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"node"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A single node resulting from a call to ft:query for which facet information should be retrieved. "
operator|+
literal|"If the node has no facet information attached, an empty sequence will be returned."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"dimension"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The facet dimension. This should correspond to a dimension defined in the index configuration"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"count"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The number of facet labels to be returned. Facets with more occurrences in the result will be returned "
operator|+
literal|"first."
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"A map having the facet label as key and the facet count as value"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|Facets
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
block|}
annotation|@
name|Override
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
specifier|final
name|NodeValue
name|nv
init|=
operator|(
name|NodeValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|nv
operator|.
name|getImplementationType
argument_list|()
operator|==
name|NodeValue
operator|.
name|IN_MEMORY_NODE
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
name|int
name|count
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
name|count
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|2
index|]
operator|)
operator|.
name|getInt
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|dimension
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|NodeProxy
name|proxy
init|=
operator|(
name|NodeProxy
operator|)
name|nv
decl_stmt|;
try|try
block|{
name|Match
name|match
init|=
name|proxy
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|match
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|match
operator|.
name|getIndexId
argument_list|()
operator|.
name|equals
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
condition|)
block|{
specifier|final
name|FacetsCollector
name|collector
init|=
operator|(
operator|(
name|LuceneIndexWorker
operator|.
name|LuceneMatch
operator|)
name|match
operator|)
operator|.
name|getFacetsCollector
argument_list|()
decl_stmt|;
specifier|final
name|LuceneIndexWorker
name|index
init|=
operator|(
name|LuceneIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexId
argument_list|(
name|LuceneIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
specifier|final
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|Facets
name|facets
init|=
name|index
operator|.
name|getFacets
argument_list|(
name|collector
argument_list|)
decl_stmt|;
specifier|final
name|FacetResult
name|result
init|=
name|facets
operator|.
name|getTopChildren
argument_list|(
name|count
argument_list|,
name|dimension
argument_list|)
decl_stmt|;
specifier|final
name|MapType
name|map
init|=
operator|new
name|MapType
argument_list|(
name|context
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|labelValues
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|String
name|label
init|=
name|result
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|label
decl_stmt|;
specifier|final
name|Number
name|value
init|=
name|result
operator|.
name|labelValues
index|[
name|i
index|]
operator|.
name|value
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|label
argument_list|)
argument_list|,
operator|new
name|IntegerValue
argument_list|(
name|value
operator|.
name|longValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
name|match
operator|=
name|match
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|LuceneModule
operator|.
name|EXXQDYFT0002
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

