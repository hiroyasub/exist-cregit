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
name|range
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
name|dom
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
name|range
operator|.
name|RangeIndex
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
name|range
operator|.
name|RangeIndexWorker
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
name|util
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
name|Arrays
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
name|FieldLookup
extends|extends
name|Function
implements|implements
name|Optimizable
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
literal|"field-equals"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
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
name|FunctionParameterSequenceType
argument_list|(
literal|"fields"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The name of the field(s) to search"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"key"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
literal|"The keys to look up for each field."
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
literal|"all nodes from the field set whose node value is equal to the key."
argument_list|)
argument_list|,
literal|true
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
name|FieldLookup
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
name|path
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|path
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
literal|"1"
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|arguments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|arg
init|=
name|arguments
operator|.
name|get
argument_list|(
name|i
argument_list|)
operator|.
name|simplify
argument_list|()
decl_stmt|;
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
name|arg
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
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
literal|"1"
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
block|}
annotation|@
name|Override
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
name|Sequence
name|fieldSeq
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
name|Sequence
index|[]
name|keys
init|=
operator|new
name|Sequence
index|[
name|getArgumentCount
argument_list|()
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|keys
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
name|DocumentSet
name|docs
init|=
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|RangeIndexWorker
name|index
init|=
operator|(
name|RangeIndexWorker
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
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
try|try
block|{
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
name|fieldSeq
argument_list|,
name|keys
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
name|LOG
operator|.
name|info
argument_list|(
literal|"preselect for "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|keys
argument_list|)
operator|+
literal|" on "
operator|+
name|contextSequence
operator|.
name|getItemCount
argument_list|()
operator|+
literal|"returned "
operator|+
name|preselectResult
operator|.
name|getItemCount
argument_list|()
operator|+
literal|" and took "
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
annotation|@
name|Override
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
name|DocumentSet
name|docs
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
name|Sequence
name|fields
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
name|Sequence
index|[]
name|keys
init|=
operator|new
name|Sequence
index|[
name|getArgumentCount
argument_list|()
operator|-
literal|1
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|keys
index|[
name|i
operator|-
literal|1
index|]
operator|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
name|RangeIndexWorker
name|index
init|=
operator|(
name|RangeIndexWorker
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
name|RangeIndex
operator|.
name|ID
argument_list|)
decl_stmt|;
try|try
block|{
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
name|fields
argument_list|,
name|keys
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
comment|//            LOG.info("eval plain took " + (System.currentTimeMillis() - start));
block|}
else|else
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
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
name|ANCESTOR
argument_list|,
literal|true
argument_list|,
name|getExpressionId
argument_list|()
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"eval took "
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
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|boolean
name|optimizeOnSelf
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|optimizeOnChild
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getOptimizeAxis
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|CHILD_AXIS
return|;
block|}
annotation|@
name|Override
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

