begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
name|encoding
operator|.
name|DGapVInt8IntDecoder
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
name|params
operator|.
name|CategoryListParams
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
name|params
operator|.
name|FacetSearchParams
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
name|params
operator|.
name|CategoryListParams
operator|.
name|OrdinalPolicy
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
name|search
operator|.
name|CountingFacetsAggregator
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
name|search
operator|.
name|FacetArrays
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
name|search
operator|.
name|FacetRequest
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
name|search
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
name|search
operator|.
name|FacetResultNode
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
name|search
operator|.
name|FacetResultsHandler
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
name|search
operator|.
name|FacetsAggregator
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
name|search
operator|.
name|FastCountingFacetsAggregator
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
name|search
operator|.
name|FloatFacetResultsHandler
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
name|search
operator|.
name|IntFacetResultsHandler
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
name|search
operator|.
name|TopKFacetResultsHandler
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
name|search
operator|.
name|TopKInEachNodeHandler
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
name|search
operator|.
name|FacetRequest
operator|.
name|FacetArraysSource
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
name|search
operator|.
name|FacetRequest
operator|.
name|ResultMode
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
name|search
operator|.
name|FacetRequest
operator|.
name|SortOrder
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
name|search
operator|.
name|FacetsCollector
operator|.
name|MatchingDocs
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
name|taxonomy
operator|.
name|ParallelTaxonomyArrays
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
name|taxonomy
operator|.
name|TaxonomyReader
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
name|util
operator|.
name|PartitionsUtils
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
name|index
operator|.
name|AtomicReader
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
name|index
operator|.
name|AtomicReaderContext
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
name|index
operator|.
name|NumericDocValues
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
name|Collector
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
name|Scorer
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
name|util
operator|.
name|FixedBitSet
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
name|DefaultDocumentSet
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
name|DocumentSet
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|QueryFacetCollector
extends|extends
name|Collector
block|{
specifier|protected
name|Scorer
name|scorer
decl_stmt|;
specifier|protected
name|AtomicReaderContext
name|context
decl_stmt|;
specifier|protected
name|AtomicReader
name|reader
decl_stmt|;
specifier|protected
name|NumericDocValues
name|docIdValues
decl_stmt|;
specifier|protected
specifier|final
name|DocumentSet
name|docs
decl_stmt|;
specifier|protected
specifier|final
name|List
argument_list|<
name|MatchingDocs
argument_list|>
name|matchingDocs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
specifier|final
name|FacetArrays
name|facetArrays
decl_stmt|;
specifier|protected
specifier|final
name|TaxonomyReader
name|taxonomyReader
decl_stmt|;
specifier|protected
specifier|final
name|FacetSearchParams
name|searchParams
decl_stmt|;
specifier|protected
name|int
name|totalHits
decl_stmt|;
specifier|protected
name|FixedBitSet
name|bits
decl_stmt|;
specifier|protected
name|float
index|[]
name|scores
decl_stmt|;
specifier|protected
name|DefaultDocumentSet
name|docbits
decl_stmt|;
comment|//private FixedBitSet docbits;
specifier|protected
name|QueryFacetCollector
parameter_list|(
specifier|final
name|DocumentSet
name|docs
parameter_list|,
specifier|final
name|FacetSearchParams
name|searchParams
parameter_list|,
specifier|final
name|TaxonomyReader
name|taxonomyReader
parameter_list|)
block|{
name|this
operator|.
name|docs
operator|=
name|docs
expr_stmt|;
name|this
operator|.
name|searchParams
operator|=
name|searchParams
expr_stmt|;
name|this
operator|.
name|taxonomyReader
operator|=
name|taxonomyReader
expr_stmt|;
comment|//        this.facetArrays = new FacetArrays(taxonomyReader.getSize());
name|this
operator|.
name|facetArrays
operator|=
operator|new
name|FacetArrays
argument_list|(
name|PartitionsUtils
operator|.
name|partitionSize
argument_list|(
name|searchParams
operator|.
name|indexingParams
argument_list|,
name|taxonomyReader
argument_list|)
argument_list|)
expr_stmt|;
name|docbits
operator|=
operator|new
name|DefaultDocumentSet
argument_list|(
literal|1031
argument_list|)
expr_stmt|;
comment|//docs.getDocumentCount());
comment|//docbits = new FixedBitSet(docs.getDocumentCount());
block|}
annotation|@
name|Override
specifier|public
name|void
name|setScorer
parameter_list|(
name|Scorer
name|scorer
parameter_list|)
throws|throws
name|IOException
block|{
name|this
operator|.
name|scorer
operator|=
name|scorer
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNextReader
parameter_list|(
name|AtomicReaderContext
name|atomicReaderContext
parameter_list|)
throws|throws
name|IOException
block|{
name|reader
operator|=
name|atomicReaderContext
operator|.
name|reader
argument_list|()
expr_stmt|;
name|docIdValues
operator|=
name|reader
operator|.
name|getNumericDocValues
argument_list|(
name|LuceneUtil
operator|.
name|FIELD_DOC_ID
argument_list|)
expr_stmt|;
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|FixedBitSet
argument_list|(
name|reader
operator|.
name|maxDoc
argument_list|()
argument_list|)
expr_stmt|;
name|totalHits
operator|=
literal|0
expr_stmt|;
name|scores
operator|=
operator|new
name|float
index|[
literal|64
index|]
expr_stmt|;
comment|// some initial size
name|context
operator|=
name|atomicReaderContext
expr_stmt|;
block|}
specifier|protected
name|void
name|finish
parameter_list|()
block|{
if|if
condition|(
name|bits
operator|!=
literal|null
condition|)
block|{
name|matchingDocs
operator|.
name|add
argument_list|(
operator|new
name|MatchingDocs
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|bits
argument_list|,
name|totalHits
argument_list|,
name|scores
argument_list|)
argument_list|)
expr_stmt|;
name|bits
operator|=
literal|null
expr_stmt|;
name|scores
operator|=
literal|null
expr_stmt|;
name|context
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|acceptsDocsOutOfOrder
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|abstract
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
function_decl|;
specifier|private
name|boolean
name|verifySearchParams
parameter_list|(
name|FacetSearchParams
name|fsp
parameter_list|)
block|{
comment|// verify that all category lists were encoded with DGapVInt
for|for
control|(
name|FacetRequest
name|fr
range|:
name|fsp
operator|.
name|facetRequests
control|)
block|{
name|CategoryListParams
name|clp
init|=
name|fsp
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|clp
operator|.
name|createEncoder
argument_list|()
operator|.
name|createMatchingDecoder
argument_list|()
operator|.
name|getClass
argument_list|()
operator|!=
name|DGapVInt8IntDecoder
operator|.
name|class
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|FacetsAggregator
name|getAggregator
parameter_list|()
block|{
if|if
condition|(
name|verifySearchParams
argument_list|(
name|searchParams
argument_list|)
condition|)
block|{
return|return
operator|new
name|FastCountingFacetsAggregator
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|CountingFacetsAggregator
argument_list|()
return|;
block|}
block|}
specifier|private
name|Set
argument_list|<
name|CategoryListParams
argument_list|>
name|getCategoryLists
parameter_list|()
block|{
if|if
condition|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getAllCategoryListParams
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|Collections
operator|.
name|singleton
argument_list|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
literal|null
argument_list|)
argument_list|)
return|;
block|}
name|HashSet
argument_list|<
name|CategoryListParams
argument_list|>
name|clps
init|=
operator|new
name|HashSet
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|clps
operator|.
name|add
argument_list|(
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|clps
return|;
block|}
specifier|private
name|FacetResultsHandler
name|createFacetResultsHandler
parameter_list|(
name|FacetRequest
name|fr
parameter_list|)
block|{
if|if
condition|(
name|fr
operator|.
name|getDepth
argument_list|()
operator|==
literal|1
operator|&&
name|fr
operator|.
name|getSortOrder
argument_list|()
operator|==
name|SortOrder
operator|.
name|DESCENDING
condition|)
block|{
name|FacetArraysSource
name|fas
init|=
name|fr
operator|.
name|getFacetArraysSource
argument_list|()
decl_stmt|;
if|if
condition|(
name|fas
operator|==
name|FacetArraysSource
operator|.
name|INT
condition|)
block|{
return|return
operator|new
name|IntFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
if|if
condition|(
name|fas
operator|==
name|FacetArraysSource
operator|.
name|FLOAT
condition|)
block|{
return|return
operator|new
name|FloatFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
block|}
if|if
condition|(
name|fr
operator|.
name|getResultMode
argument_list|()
operator|==
name|ResultMode
operator|.
name|PER_NODE_IN_TREE
condition|)
block|{
return|return
operator|new
name|TopKInEachNodeHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
return|return
operator|new
name|TopKFacetResultsHandler
argument_list|(
name|taxonomyReader
argument_list|,
name|fr
argument_list|,
name|facetArrays
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|FacetResult
name|emptyResult
parameter_list|(
name|int
name|ordinal
parameter_list|,
name|FacetRequest
name|fr
parameter_list|)
block|{
name|FacetResultNode
name|root
init|=
operator|new
name|FacetResultNode
argument_list|(
name|ordinal
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|root
operator|.
name|label
operator|=
name|fr
operator|.
name|categoryPath
expr_stmt|;
return|return
operator|new
name|FacetResult
argument_list|(
name|fr
argument_list|,
name|root
argument_list|,
literal|0
argument_list|)
return|;
block|}
name|List
argument_list|<
name|FacetResult
argument_list|>
name|facetResults
init|=
literal|null
decl_stmt|;
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getFacetResults
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|facetResults
operator|==
literal|null
condition|)
block|{
name|finish
argument_list|()
expr_stmt|;
name|facetResults
operator|=
name|accumulate
argument_list|()
expr_stmt|;
block|}
return|return
name|facetResults
return|;
block|}
specifier|private
name|List
argument_list|<
name|FacetResult
argument_list|>
name|accumulate
parameter_list|()
throws|throws
name|IOException
block|{
comment|// aggregate facets per category list (usually only one category list)
name|FacetsAggregator
name|aggregator
init|=
name|getAggregator
argument_list|()
decl_stmt|;
for|for
control|(
name|CategoryListParams
name|clp
range|:
name|getCategoryLists
argument_list|()
control|)
block|{
for|for
control|(
name|MatchingDocs
name|md
range|:
name|matchingDocs
control|)
block|{
name|aggregator
operator|.
name|aggregate
argument_list|(
name|md
argument_list|,
name|clp
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
name|ParallelTaxonomyArrays
name|arrays
init|=
name|taxonomyReader
operator|.
name|getParallelTaxonomyArrays
argument_list|()
decl_stmt|;
comment|// compute top-K
specifier|final
name|int
index|[]
name|children
init|=
name|arrays
operator|.
name|children
argument_list|()
decl_stmt|;
specifier|final
name|int
index|[]
name|siblings
init|=
name|arrays
operator|.
name|siblings
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|FacetResult
argument_list|>
name|res
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
for|for
control|(
name|FacetRequest
name|fr
range|:
name|searchParams
operator|.
name|facetRequests
control|)
block|{
name|int
name|rootOrd
init|=
name|taxonomyReader
operator|.
name|getOrdinal
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
comment|// category does not exist
if|if
condition|(
name|rootOrd
operator|==
name|TaxonomyReader
operator|.
name|INVALID_ORDINAL
condition|)
block|{
comment|// Add empty FacetResult
name|res
operator|.
name|add
argument_list|(
name|emptyResult
argument_list|(
name|rootOrd
argument_list|,
name|fr
argument_list|)
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|CategoryListParams
name|clp
init|=
name|searchParams
operator|.
name|indexingParams
operator|.
name|getCategoryListParams
argument_list|(
name|fr
operator|.
name|categoryPath
argument_list|)
decl_stmt|;
comment|// someone might ask to aggregate ROOT category
if|if
condition|(
name|fr
operator|.
name|categoryPath
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|OrdinalPolicy
name|ordinalPolicy
init|=
name|clp
operator|.
name|getOrdinalPolicy
argument_list|(
name|fr
operator|.
name|categoryPath
operator|.
name|components
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
if|if
condition|(
name|ordinalPolicy
operator|==
name|OrdinalPolicy
operator|.
name|NO_PARENTS
condition|)
block|{
comment|// rollup values
name|aggregator
operator|.
name|rollupValues
argument_list|(
name|fr
argument_list|,
name|rootOrd
argument_list|,
name|children
argument_list|,
name|siblings
argument_list|,
name|facetArrays
argument_list|)
expr_stmt|;
block|}
block|}
name|FacetResultsHandler
name|frh
init|=
name|createFacetResultsHandler
argument_list|(
name|fr
argument_list|)
decl_stmt|;
name|res
operator|.
name|add
argument_list|(
name|frh
operator|.
name|compute
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|res
return|;
block|}
block|}
end_class

end_unit

