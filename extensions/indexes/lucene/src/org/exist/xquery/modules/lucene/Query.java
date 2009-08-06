begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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

begin_import
import|import
name|org
operator|.
name|apache
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
name|queryParser
operator|.
name|ParseException
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
name|AnalyzeContextInfo
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
name|BasicExpressionVisitor
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
name|Cardinality
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
name|Constants
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
name|Dependency
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
name|DynamicCardinalityCheck
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
name|Expression
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
name|Function
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
name|FunctionSignature
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
name|LocationStep
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
name|NodeTest
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
name|Optimizable
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
name|modules
operator|.
name|lucene
operator|.
name|LuceneModule
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
name|Logger
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
name|signature
init|=
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
argument_list|)
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
specifier|public
name|Query
parameter_list|(
name|XQueryContext
name|context
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
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|Expression
name|path
init|=
operator|(
name|Expression
operator|)
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
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
literal|1
argument_list|)
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
operator|(
name|LocationStep
operator|)
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
operator|(
name|LocationStep
operator|)
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
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
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
argument_list|)
expr_stmt|;
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
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
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
block|}
else|else
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
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
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
argument_list|)
expr_stmt|;
if|if
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
name|contextQName
operator|.
name|setNameType
argument_list|(
name|ElementValue
operator|.
name|ATTRIBUTE
argument_list|)
expr_stmt|;
name|axis
operator|=
name|firstStep
operator|.
name|getAxis
argument_list|()
expr_stmt|;
name|contextStep
operator|=
name|lastStep
expr_stmt|;
block|}
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
name|qnames
init|=
operator|new
name|ArrayList
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
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
argument_list|)
expr_stmt|;
else|else
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
name|key
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|DESCENDANT
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
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|)
expr_stmt|;
else|else
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
name|key
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
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
block|}
else|else
block|{
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
specifier|private
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
block|}
end_class

end_unit

