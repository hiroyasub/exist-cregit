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
name|NodeSet
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
name|value
operator|.
name|*
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
name|QueryField
extends|extends
name|Query
implements|implements
name|Optimizable
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Query
operator|.
name|class
argument_list|)
decl_stmt|;
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
literal|"query-field"
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
literal|"Queries a Lucene field, which has to be explicitely created in the index configuration."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"field"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The lucene field name."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The query to search for, provided either as a string or text in Lucene's default query "
operator|+
literal|"syntax or as an XML fragment to bypass Lucene's default query parser"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set matching the query. match highlighting information "
operator|+
literal|"will be available for all returned nodes. Lucene's match score can be retrieved via "
operator|+
literal|"the ft:score function."
argument_list|)
argument_list|,
literal|"Use an index definition with nested fields and ft:query instead"
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"query-field"
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
literal|"Queries a Lucene field, which has to be explicitely created in the index configuration."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"field"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The lucene field name."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"query"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The query to search for, provided either as a string or text in Lucene's default query "
operator|+
literal|"syntax or as an XML fragment to bypass Lucene's default query parser"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"options"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"An XML fragment containing options to be passed to Lucene's query parser. The following "
operator|+
literal|"options are supported (a description can be found in the docs):\n"
operator|+
literal|"<options>\n"
operator|+
literal|"<default-operator>and|or</default-operator>\n"
operator|+
literal|"<phrase-slop>number</phrase-slop>\n"
operator|+
literal|"<leading-wildcard>yes|no</leading-wildcard>\n"
operator|+
literal|"<filter-rewrite>yes|no</filter-rewrite>\n"
operator|+
literal|"</options>"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"all nodes from the input node set matching the query. match highlighting information "
operator|+
literal|"will be available for all returned nodes. Lucene's match score can be retrieved via "
operator|+
literal|"the ft:score function."
argument_list|)
argument_list|,
literal|"Use an index definition with nested fields and ft:query instead"
argument_list|)
block|}
decl_stmt|;
specifier|private
name|NodeSet
name|preselectResult
init|=
literal|null
decl_stmt|;
specifier|public
name|QueryField
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
comment|/* (non-Javadoc)     * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.Expression)     */
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|canOptimize
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
block|{
return|return
literal|true
return|;
block|}
specifier|public
name|int
name|getOptimizeAxis
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
return|;
block|}
specifier|public
name|NodeSet
name|preSelect
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|boolean
name|useContext
parameter_list|)
throws|throws
name|XPathException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// the expression can be called multiple times, so we need to clear the previous preselectResult
name|preselectResult
operator|=
literal|null
expr_stmt|;
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
name|String
name|field
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|DocumentSet
name|docs
init|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|Item
name|query
init|=
name|getKey
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|QueryOptions
name|options
init|=
name|parseOptions
argument_list|(
name|this
argument_list|,
name|contextSequence
argument_list|,
literal|null
argument_list|,
literal|3
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|query
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
name|preselectResult
operator|=
name|index
operator|.
name|queryField
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|useContext
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
literal|null
argument_list|,
name|field
argument_list|,
operator|(
name|Element
operator|)
operator|(
operator|(
name|NodeValue
operator|)
name|query
operator|)
operator|.
name|getNode
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|options
argument_list|)
expr_stmt|;
else|else
name|preselectResult
operator|=
name|index
operator|.
name|queryField
argument_list|(
name|context
argument_list|,
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|useContext
condition|?
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
else|:
literal|null
argument_list|,
name|field
argument_list|,
name|query
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|options
argument_list|)
expr_stmt|;
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
literal|"Error while querying full text index: "
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Lucene query took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceFunctions
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceIndexUsage
argument_list|(
name|context
argument_list|,
literal|"lucene"
argument_list|,
name|this
argument_list|,
name|PerformanceStats
operator|.
name|OPTIMIZED_INDEX
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
return|return
name|preselectResult
return|;
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
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|NodeSet
name|result
decl_stmt|;
if|if
condition|(
name|preselectResult
operator|==
literal|null
condition|)
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|field
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|Item
name|query
init|=
name|getKey
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|DocumentSet
name|docs
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
name|docs
operator|=
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
expr_stmt|;
else|else
name|docs
operator|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
expr_stmt|;
name|NodeSet
name|contextSet
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
name|contextSet
operator|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
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
name|QueryOptions
name|options
init|=
name|parseOptions
argument_list|(
name|this
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
literal|3
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|query
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
name|result
operator|=
name|index
operator|.
name|queryField
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|field
argument_list|,
operator|(
name|Element
operator|)
operator|(
operator|(
name|NodeValue
operator|)
name|query
operator|)
operator|.
name|getNode
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|options
argument_list|)
expr_stmt|;
else|else
name|result
operator|=
name|index
operator|.
name|queryField
argument_list|(
name|context
argument_list|,
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|field
argument_list|,
name|query
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|options
argument_list|)
expr_stmt|;
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
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceFunctions
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|traceIndexUsage
argument_list|(
name|context
argument_list|,
literal|"lucene"
argument_list|,
name|this
argument_list|,
name|PerformanceStats
operator|.
name|BASIC_INDEX
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|result
operator|=
name|preselectResult
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
literal|true
argument_list|,
name|getContextId
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
empty_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|postOptimization
condition|)
block|{
name|preselectResult
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
