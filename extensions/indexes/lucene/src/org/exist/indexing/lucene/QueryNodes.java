begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
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
name|BinaryDocValues
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
name|ParseException
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
name|search
operator|.
name|IndexSearcher
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
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|ArrayUtil
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
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
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
name|DocumentImpl
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
name|indexing
operator|.
name|lucene
operator|.
name|LuceneIndexWorker
operator|.
name|LuceneMatch
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
name|storage
operator|.
name|DBBroker
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
name|util
operator|.
name|ByteConversion
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
name|TerminatedException
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|QueryNodes
block|{
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|query
parameter_list|(
name|LuceneIndexWorker
name|worker
parameter_list|,
name|QName
name|qname
parameter_list|,
name|int
name|contextId
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|Query
name|query
parameter_list|,
name|FacetSearchParams
name|searchParams
parameter_list|,
name|SearchCallback
argument_list|<
name|NodeProxy
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
throws|,
name|XPathException
block|{
specifier|final
name|LuceneIndex
name|index
init|=
name|worker
operator|.
name|index
decl_stmt|;
specifier|final
name|Database
name|db
init|=
name|index
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
return|return
name|index
operator|.
name|withSearcher
argument_list|(
name|searcher
lambda|->
block|{
specifier|final
name|TaxonomyReader
name|taxonomyReader
init|=
name|index
operator|.
name|getTaxonomyReader
argument_list|()
decl_stmt|;
name|DocumentHitCollector
name|collector
init|=
operator|new
name|DocumentHitCollector
argument_list|(
name|db
argument_list|,
name|worker
argument_list|,
name|query
argument_list|,
name|qname
argument_list|,
name|contextId
argument_list|,
name|docs
argument_list|,
name|callback
argument_list|,
name|searchParams
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
return|return
name|collector
operator|.
name|getFacetResults
argument_list|()
return|;
block|}
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|List
argument_list|<
name|FacetResult
argument_list|>
name|query
parameter_list|(
name|LuceneIndexWorker
name|worker
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
parameter_list|,
name|int
name|contextId
parameter_list|,
name|String
name|queryStr
parameter_list|,
name|FacetSearchParams
name|searchParams
parameter_list|,
name|Properties
name|options
parameter_list|,
name|SearchCallback
argument_list|<
name|NodeProxy
argument_list|>
name|callback
parameter_list|)
throws|throws
name|IOException
throws|,
name|ParseException
throws|,
name|XPathException
block|{
specifier|final
name|LuceneIndex
name|index
init|=
name|worker
operator|.
name|index
decl_stmt|;
specifier|final
name|Database
name|db
init|=
name|index
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
name|db
operator|.
name|getActiveBroker
argument_list|()
decl_stmt|;
return|return
name|index
operator|.
name|withSearcher
argument_list|(
name|searcher
lambda|->
block|{
specifier|final
name|TaxonomyReader
name|taxonomyReader
init|=
name|index
operator|.
name|getTaxonomyReader
argument_list|()
decl_stmt|;
name|DocumentHitCollector
name|collector
init|=
operator|new
name|DocumentHitCollector
argument_list|(
name|db
argument_list|,
name|worker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|contextId
argument_list|,
name|docs
argument_list|,
name|callback
argument_list|,
name|searchParams
argument_list|,
name|taxonomyReader
argument_list|)
decl_stmt|;
specifier|final
name|List
argument_list|<
name|QName
argument_list|>
name|definedIndexes
init|=
name|worker
operator|.
name|getDefinedIndexes
argument_list|(
name|qnames
argument_list|)
decl_stmt|;
for|for
control|(
name|QName
name|qname
range|:
name|definedIndexes
control|)
block|{
name|String
name|field
init|=
name|LuceneUtil
operator|.
name|encodeQName
argument_list|(
name|qname
argument_list|,
name|db
operator|.
name|getSymbols
argument_list|()
argument_list|)
decl_stmt|;
name|Analyzer
name|analyzer
init|=
name|worker
operator|.
name|getAnalyzer
argument_list|(
literal|null
argument_list|,
name|qname
argument_list|,
name|broker
argument_list|,
name|docs
argument_list|)
decl_stmt|;
name|QueryParser
name|parser
init|=
operator|new
name|QueryParser
argument_list|(
name|LuceneIndex
operator|.
name|LUCENE_VERSION_IN_USE
argument_list|,
name|field
argument_list|,
name|analyzer
argument_list|)
decl_stmt|;
try|try
block|{
name|worker
operator|.
name|setOptions
argument_list|(
name|options
argument_list|,
name|parser
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
name|parser
operator|.
name|parse
argument_list|(
name|queryStr
argument_list|)
decl_stmt|;
name|collector
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|collector
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|collector
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ParseException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Syntax error in lucene query: "
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
return|return
name|collector
operator|.
name|getFacetResults
argument_list|()
return|;
block|}
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|DocumentHitCollector
extends|extends
name|QueryFacetCollector
block|{
specifier|private
name|BinaryDocValues
name|nodeIdValues
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|1024
index|]
decl_stmt|;
specifier|private
specifier|final
name|Database
name|db
decl_stmt|;
specifier|private
specifier|final
name|LuceneIndexWorker
name|worker
decl_stmt|;
specifier|private
name|Query
name|query
decl_stmt|;
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|private
specifier|final
name|int
name|contextId
decl_stmt|;
specifier|private
specifier|final
name|SearchCallback
argument_list|<
name|NodeProxy
argument_list|>
name|callback
decl_stmt|;
specifier|private
name|DocumentHitCollector
parameter_list|(
specifier|final
name|Database
name|db
parameter_list|,
specifier|final
name|LuceneIndexWorker
name|worker
parameter_list|,
specifier|final
name|Query
name|query
parameter_list|,
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|DocumentSet
name|docs
parameter_list|,
specifier|final
name|SearchCallback
argument_list|<
name|NodeProxy
argument_list|>
name|callback
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
name|super
argument_list|(
name|docs
argument_list|,
name|searchParams
argument_list|,
name|taxonomyReader
argument_list|)
expr_stmt|;
name|this
operator|.
name|db
operator|=
name|db
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
name|this
operator|.
name|query
operator|=
name|query
expr_stmt|;
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
name|this
operator|.
name|callback
operator|=
name|callback
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
name|super
operator|.
name|setNextReader
argument_list|(
name|atomicReaderContext
argument_list|)
expr_stmt|;
name|nodeIdValues
operator|=
name|this
operator|.
name|reader
operator|.
name|getBinaryDocValues
argument_list|(
name|LuceneUtil
operator|.
name|FIELD_NODE_ID
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|collect
parameter_list|(
name|int
name|doc
parameter_list|)
block|{
try|try
block|{
name|float
name|score
init|=
name|scorer
operator|.
name|score
argument_list|()
decl_stmt|;
name|int
name|docId
init|=
operator|(
name|int
operator|)
name|this
operator|.
name|docIdValues
operator|.
name|get
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|DocumentImpl
name|storedDocument
init|=
name|docs
operator|.
name|getDoc
argument_list|(
name|docId
argument_list|)
decl_stmt|;
if|if
condition|(
name|storedDocument
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
operator|!
name|docbits
operator|.
name|contains
argument_list|(
name|docId
argument_list|)
condition|)
block|{
name|docbits
operator|.
name|add
argument_list|(
name|storedDocument
argument_list|)
expr_stmt|;
name|bits
operator|.
name|set
argument_list|(
name|doc
argument_list|)
expr_stmt|;
if|if
condition|(
name|totalHits
operator|>=
name|scores
operator|.
name|length
condition|)
block|{
name|float
index|[]
name|newScores
init|=
operator|new
name|float
index|[
name|ArrayUtil
operator|.
name|oversize
argument_list|(
name|totalHits
operator|+
literal|1
argument_list|,
literal|4
argument_list|)
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|scores
argument_list|,
literal|0
argument_list|,
name|newScores
argument_list|,
literal|0
argument_list|,
name|totalHits
argument_list|)
expr_stmt|;
name|scores
operator|=
name|newScores
expr_stmt|;
block|}
name|scores
index|[
name|totalHits
index|]
operator|=
name|score
expr_stmt|;
name|totalHits
operator|++
expr_stmt|;
block|}
comment|// XXX: understand: check permissions here? No, it may slowdown, better to check final set
name|BytesRef
name|ref
init|=
operator|new
name|BytesRef
argument_list|(
name|buf
argument_list|)
decl_stmt|;
name|this
operator|.
name|nodeIdValues
operator|.
name|get
argument_list|(
name|doc
argument_list|,
name|ref
argument_list|)
expr_stmt|;
name|int
name|units
init|=
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
argument_list|)
decl_stmt|;
name|NodeId
name|nodeId
init|=
name|db
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromData
argument_list|(
name|units
argument_list|,
name|ref
operator|.
name|bytes
argument_list|,
name|ref
operator|.
name|offset
operator|+
literal|2
argument_list|)
decl_stmt|;
comment|//LOG.info("doc: " + docId + "; node: " + nodeId.toString() + "; units: " + units);
name|NodeProxy
name|storedNode
init|=
operator|new
name|NodeProxy
argument_list|(
name|storedDocument
argument_list|,
name|nodeId
argument_list|)
decl_stmt|;
if|if
condition|(
name|qname
operator|!=
literal|null
condition|)
name|storedNode
operator|.
name|setNodeType
argument_list|(
name|qname
operator|.
name|getNameType
argument_list|()
operator|==
name|ElementValue
operator|.
name|ATTRIBUTE
condition|?
name|Node
operator|.
name|ATTRIBUTE_NODE
else|:
name|Node
operator|.
name|ELEMENT_NODE
argument_list|)
expr_stmt|;
name|LuceneMatch
name|match
init|=
name|worker
operator|.
expr|new
name|LuceneMatch
argument_list|(
name|contextId
argument_list|,
name|nodeId
argument_list|,
name|query
argument_list|)
decl_stmt|;
name|match
operator|.
name|setScore
argument_list|(
name|score
argument_list|)
expr_stmt|;
name|storedNode
operator|.
name|addMatch
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|callback
operator|.
name|found
argument_list|(
name|storedNode
argument_list|,
name|score
argument_list|)
expr_stmt|;
comment|//resultSet.add(storedNode, sizeHint);
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

