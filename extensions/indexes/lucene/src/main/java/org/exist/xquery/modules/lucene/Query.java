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
name|persistent
operator|.
name|VirtualNodeSet
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
name|List
import|;
end_import

begin_class
specifier|public
class|class
name|Query
extends|extends
name|Function
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
literal|"query"
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
literal|"Queries a node set using a Lucene full text index; a lucene index "
operator|+
literal|"must already be defined on the nodes, because if no index is available "
operator|+
literal|"on a node, nothing will be found. Indexes on descendant nodes are not "
operator|+
literal|"used. The context of the Lucene query is determined by the given input "
operator|+
literal|"node set. The query is specified either as a query string based on "
operator|+
literal|"Lucene's default query syntax or as an XML fragment. "
operator|+
literal|"See http://exist-db.org/lucene.html#N1029E for complete documentation."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to search using a Lucene full text index which is defined on those nodes"
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
name|ZERO_OR_ONE
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
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"query"
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
literal|"Queries a node set using a Lucene full text index; a lucene index "
operator|+
literal|"must already be defined on the nodes, because if no index is available "
operator|+
literal|"on a node, nothing will be found. Indexes on descendant nodes are not "
operator|+
literal|"used. The context of the Lucene query is determined by the given input "
operator|+
literal|"node set. The query is specified either as a query string based on "
operator|+
literal|"Lucene's default query syntax or as an XML fragment. "
operator|+
literal|"See http://exist-db.org/lucene.html#N1029E for complete documentation."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"nodes"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to search using a Lucene full text index which is defined on those nodes"
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
name|ZERO_OR_ONE
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
name|ITEM
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
argument_list|)
block|}
decl_stmt|;
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|protected
name|QName
name|contextQName
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|axis
init|=
name|Constants
operator|.
name|UNKNOWN_AXIS
decl_stmt|;
specifier|private
name|NodeSet
name|preselectResult
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|optimizeSelf
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|optimizeChild
init|=
literal|false
decl_stmt|;
specifier|public
name|Query
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
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|steps
operator|.
name|clear
argument_list|()
expr_stmt|;
name|Expression
name|path
init|=
name|arguments
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|Expression
name|arg
init|=
name|arguments
operator|.
name|get
argument_list|(
literal|1
argument_list|)
operator|.
name|simplify
argument_list|()
decl_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|arg
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
argument_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"2"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
if|if
condition|(
name|arguments
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
block|{
name|arg
operator|=
name|arguments
operator|.
name|get
argument_list|(
literal|2
argument_list|)
operator|.
name|simplify
argument_list|()
expr_stmt|;
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|arg
argument_list|,
operator|new
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
argument_list|(
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
operator|.
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"2"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|arg
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.Expression)     */
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
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
init|=
name|BasicExpressionVisitor
operator|.
name|findLocationSteps
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|steps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|LocationStep
name|firstStep
init|=
name|steps
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|LocationStep
name|lastStep
init|=
name|steps
operator|.
name|get
argument_list|(
name|steps
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstStep
operator|!=
literal|null
operator|&&
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|firstStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
name|Expression
name|outerExpr
init|=
name|contextInfo
operator|.
name|getContextStep
argument_list|()
decl_stmt|;
if|if
condition|(
name|outerExpr
operator|!=
literal|null
operator|&&
name|outerExpr
operator|instanceof
name|LocationStep
condition|)
block|{
name|LocationStep
name|outerStep
init|=
operator|(
name|LocationStep
operator|)
name|outerExpr
decl_stmt|;
name|NodeTest
name|test
init|=
name|outerStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
specifier|final
name|byte
name|contextQNameType
decl_stmt|;
if|if
condition|(
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|outerStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
block|{
name|contextQNameType
operator|=
name|ElementValue
operator|.
name|ATTRIBUTE
expr_stmt|;
block|}
else|else
block|{
name|contextQNameType
operator|=
name|ElementValue
operator|.
name|ELEMENT
expr_stmt|;
block|}
if|if
condition|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|contextQNameType
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|,
name|contextQNameType
argument_list|)
expr_stmt|;
block|}
name|contextStep
operator|=
name|firstStep
expr_stmt|;
name|axis
operator|=
name|outerStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|optimizeSelf
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|lastStep
operator|!=
literal|null
operator|&&
name|firstStep
operator|!=
literal|null
condition|)
block|{
name|NodeTest
name|test
init|=
name|lastStep
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|getName
argument_list|()
operator|==
literal|null
condition|)
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
if|else if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
condition|)
name|contextQName
operator|=
name|test
operator|.
name|getName
argument_list|()
expr_stmt|;
if|else                  if
condition|(
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|lastStep
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
condition|)
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|,
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|contextQName
operator|=
operator|new
name|QName
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|axis
operator|=
name|firstStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|optimizeChild
operator|=
name|steps
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
operator|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|)
expr_stmt|;
name|contextStep
operator|=
name|lastStep
expr_stmt|;
block|}
block|}
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
name|contextQName
operator|!=
literal|null
return|;
block|}
specifier|public
name|boolean
name|optimizeOnSelf
parameter_list|()
block|{
return|return
name|optimizeSelf
return|;
block|}
specifier|public
name|boolean
name|optimizeOnChild
parameter_list|()
block|{
return|return
name|optimizeChild
return|;
block|}
specifier|public
name|int
name|getOptimizeAxis
parameter_list|()
block|{
return|return
name|axis
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
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
operator|!
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
comment|// in-memory docs won't have an index
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
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
comment|// DW: contextSequence can be null
name|DocumentSet
name|docs
init|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|Item
name|key
init|=
name|getKey
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|qnames
operator|.
name|add
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
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
name|key
operator|!=
literal|null
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|key
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|queryXML
init|=
name|key
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Element
operator|)
operator|(
operator|(
name|NodeValue
operator|)
name|key
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|preselectResult
operator|=
name|index
operator|.
name|query
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
name|qnames
argument_list|,
name|queryXML
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|query
init|=
name|key
operator|==
literal|null
condition|?
literal|null
else|:
name|key
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|preselectResult
operator|=
name|index
operator|.
name|query
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
name|qnames
argument_list|,
name|query
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
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
name|trace
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
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
operator|!
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
comment|// in-memory docs won't have an index
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
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
name|Sequence
name|input
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
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|input
operator|instanceof
name|VirtualNodeSet
operator|)
operator|&&
name|input
operator|.
name|isEmpty
argument_list|()
condition|)
name|result
operator|=
name|NodeSet
operator|.
name|EMPTY_SET
expr_stmt|;
else|else
block|{
name|NodeSet
name|inNodes
init|=
name|input
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|DocumentSet
name|docs
init|=
name|inNodes
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
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
name|Item
name|key
init|=
name|getKey
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextQName
operator|!=
literal|null
condition|)
block|{
name|qnames
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|qnames
operator|.
name|add
argument_list|(
name|contextQName
argument_list|)
expr_stmt|;
block|}
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
name|key
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|queryXML
init|=
name|key
operator|==
literal|null
condition|?
literal|null
else|:
operator|(
name|Element
operator|)
operator|(
operator|(
name|NodeValue
operator|)
name|key
operator|)
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|result
operator|=
name|index
operator|.
name|query
argument_list|(
name|context
argument_list|,
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|inNodes
argument_list|,
name|qnames
argument_list|,
name|queryXML
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
else|else
block|{
specifier|final
name|String
name|query
init|=
name|key
operator|==
literal|null
condition|?
literal|null
else|:
name|key
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|result
operator|=
name|index
operator|.
name|query
argument_list|(
name|context
argument_list|,
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|inNodes
argument_list|,
name|qnames
argument_list|,
name|query
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
decl||
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
comment|// DW: contextSequence can be null
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|preselectResult
argument_list|)
expr_stmt|;
name|result
operator|=
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
name|toNodeSet
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|protected
name|Item
name|getKey
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
name|Sequence
name|keySeq
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|keySeq
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Item
name|key
init|=
name|keySeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|key
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|key
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Second argument to ft:query should either be a query string or "
operator|+
literal|"an XML element describing the query. Found: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|key
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
return|return
name|key
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
specifier|final
name|Expression
name|stringArg
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|stringArg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|stringArg
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
else|else
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
return|;
block|}
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
specifier|protected
specifier|static
name|QueryOptions
name|parseOptions
parameter_list|(
name|Function
name|funct
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|int
name|position
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|funct
operator|.
name|getArgumentCount
argument_list|()
operator|<
name|position
condition|)
return|return
operator|new
name|QueryOptions
argument_list|()
return|;
name|Sequence
name|optSeq
init|=
name|funct
operator|.
name|getArgument
argument_list|(
name|position
operator|-
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|optSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|ELEMENT
argument_list|)
condition|)
block|{
return|return
operator|new
name|QueryOptions
argument_list|(
name|funct
operator|.
name|getContext
argument_list|()
argument_list|,
operator|(
name|NodeValue
operator|)
name|optSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|optSeq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|MAP
argument_list|)
condition|)
block|{
return|return
operator|new
name|QueryOptions
argument_list|(
operator|(
name|AbstractMapType
operator|)
name|optSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|funct
argument_list|,
name|LuceneModule
operator|.
name|EXXQDYFT0004
argument_list|,
literal|"Argument 3 should be either a map or an XML element"
argument_list|)
throw|;
block|}
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

