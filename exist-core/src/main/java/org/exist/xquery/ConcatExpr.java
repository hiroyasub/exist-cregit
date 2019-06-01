begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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

begin_class
specifier|public
class|class
name|ConcatExpr
extends|extends
name|PathExpr
block|{
specifier|public
name|ConcatExpr
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|getContext
argument_list|()
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|30
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0003
argument_list|,
literal|"string concatenation operator is not available before XQuery 3.0"
argument_list|)
throw|;
block|}
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|PathExpr
name|pathExpr
parameter_list|)
block|{
name|Expression
name|expr
init|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|pathExpr
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|expr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
name|expr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
block|}
name|super
operator|.
name|add
argument_list|(
name|expr
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
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|StringBuilder
name|concat
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Expression
name|step
range|:
name|steps
control|)
block|{
specifier|final
name|Sequence
name|seq
init|=
name|step
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|FOTY0013
argument_list|,
literal|"Got a function item as operand in string concatenation"
argument_list|)
throw|;
block|}
name|concat
operator|.
name|append
argument_list|(
name|item
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|StringValue
name|result
init|=
operator|new
name|StringValue
argument_list|(
name|concat
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
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
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|EXACTLY_ONE
return|;
block|}
annotation|@
name|Override
specifier|public
name|Expression
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
block|}
end_class

end_unit
