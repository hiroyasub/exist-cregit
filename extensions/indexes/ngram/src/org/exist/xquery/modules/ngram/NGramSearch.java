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
name|ngram
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
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
name|indexing
operator|.
name|ngram
operator|.
name|NGramIndex
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
name|ngram
operator|.
name|NGramIndexWorker
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
name|util
operator|.
name|Error
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
name|Item
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
name|Sequence
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
name|SequenceType
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
name|Type
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
name|NGramSearch
extends|extends
name|Function
implements|implements
name|Optimizable
block|{
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
literal|"contains"
argument_list|,
name|NGramModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|NGramModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|""
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
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
name|NGramSearch
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
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|arg
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|arg
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|arg
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
name|contextInfo
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
name|NGramIndexWorker
name|index
init|=
operator|(
name|NGramIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getIndexWorkerById
argument_list|(
name|NGramIndex
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
name|String
name|key
init|=
name|getArgument
argument_list|(
literal|1
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
name|String
index|[]
name|ngrams
init|=
name|index
operator|.
name|getDistinctNGrams
argument_list|(
name|key
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
name|preselectResult
operator|=
name|processMatches
argument_list|(
name|index
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
name|ngrams
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
name|NodeSet
operator|.
name|DESCENDANT
argument_list|)
expr_stmt|;
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
argument_list|,
name|contextItem
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
name|NGramIndexWorker
name|index
init|=
operator|(
name|NGramIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getIndexWorkerById
argument_list|(
name|NGramIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
comment|//Alternate design
comment|//NGramIndexWorker index = (NGramIndexWorker)context.getBroker().getBrokerPool().getIndexManager().getIndexById(NGramIndex.ID).getWorker();
name|String
name|key
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
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
index|[]
name|ngrams
init|=
name|index
operator|.
name|getDistinctNGrams
argument_list|(
name|key
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
name|result
operator|=
name|processMatches
argument_list|(
name|index
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
name|ngrams
argument_list|,
name|inNodes
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|contextStep
operator|.
name|setPreloadNodeSets
argument_list|(
literal|true
argument_list|)
expr_stmt|;
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
name|NodeSet
name|processMatches
parameter_list|(
name|NGramIndexWorker
name|index
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|List
name|qnames
parameter_list|,
name|String
index|[]
name|ngrams
parameter_list|,
name|NodeSet
name|nodeSet
parameter_list|,
name|int
name|axis
parameter_list|)
throws|throws
name|TerminatedException
block|{
name|NodeSet
name|result
init|=
literal|null
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
name|ngrams
operator|.
name|length
condition|;
name|i
operator|++
control|)
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
name|ngram
init|=
name|ngrams
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|ngram
operator|.
name|length
argument_list|()
operator|<
name|index
operator|.
name|getN
argument_list|()
operator|&&
name|i
operator|>
literal|0
condition|)
block|{
comment|// if this is the last ngram and its length is too small,
comment|// fill it up with characters from the previous ngram. too short
comment|// ngrams lead to a considerable performance loss.
name|int
name|fill
init|=
name|index
operator|.
name|getN
argument_list|()
operator|-
name|ngram
operator|.
name|length
argument_list|()
decl_stmt|;
name|ngram
operator|=
name|ngrams
index|[
name|i
operator|-
literal|1
index|]
operator|.
name|substring
argument_list|(
name|index
operator|.
name|getN
argument_list|()
operator|-
name|fill
argument_list|)
operator|+
name|ngram
expr_stmt|;
block|}
name|NodeSet
name|nodes
init|=
name|index
operator|.
name|search
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
name|ngram
argument_list|,
name|ngrams
index|[
name|i
index|]
argument_list|,
name|context
argument_list|,
name|nodeSet
argument_list|,
name|axis
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"Found "
operator|+
name|nodes
operator|.
name|getLength
argument_list|()
operator|+
literal|" for "
operator|+
name|ngram
operator|+
literal|" in "
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
name|result
operator|==
literal|null
condition|)
name|result
operator|=
name|nodes
expr_stmt|;
else|else
block|{
name|NodeSet
name|temp
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
for|for
control|(
name|NodeSetIterator
name|iterator
init|=
name|nodes
operator|.
name|iterator
argument_list|()
init|;
name|iterator
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|NodeProxy
name|next
init|=
operator|(
name|NodeProxy
operator|)
name|iterator
operator|.
name|next
argument_list|()
decl_stmt|;
name|NodeProxy
name|before
init|=
name|result
operator|.
name|get
argument_list|(
name|next
argument_list|)
decl_stmt|;
if|if
condition|(
name|before
operator|!=
literal|null
condition|)
block|{
name|Match
name|match
init|=
literal|null
decl_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Match
name|mb
init|=
name|before
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|mb
operator|!=
literal|null
operator|&&
operator|!
name|found
condition|)
block|{
name|Match
name|mn
init|=
name|next
operator|.
name|getMatches
argument_list|()
decl_stmt|;
while|while
condition|(
name|mn
operator|!=
literal|null
operator|&&
operator|!
name|found
condition|)
block|{
if|if
condition|(
operator|(
name|match
operator|=
name|mb
operator|.
name|isAfter
argument_list|(
name|mn
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
block|}
name|mn
operator|=
name|mn
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
name|mb
operator|=
name|mb
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|found
condition|)
block|{
name|Match
name|m
init|=
name|next
operator|.
name|getMatches
argument_list|()
decl_stmt|;
name|next
operator|.
name|setMatches
argument_list|(
literal|null
argument_list|)
expr_stmt|;
while|while
condition|(
name|m
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|m
operator|.
name|getContextId
argument_list|()
operator|!=
name|getExpressionId
argument_list|()
condition|)
name|next
operator|.
name|addMatch
argument_list|(
name|m
argument_list|)
expr_stmt|;
name|m
operator|=
name|m
operator|.
name|getNextMatch
argument_list|()
expr_stmt|;
block|}
name|next
operator|.
name|addMatch
argument_list|(
name|match
argument_list|)
expr_stmt|;
name|temp
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|result
operator|=
name|temp
expr_stmt|;
block|}
block|}
return|return
name|result
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

