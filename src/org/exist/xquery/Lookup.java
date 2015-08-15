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
name|ExpressionDumper
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

begin_comment
comment|/**  * Implements the XQuery 3.1 lookup operator on maps and arrays.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|Lookup
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|contextExpression
decl_stmt|;
specifier|private
name|Sequence
name|keys
init|=
literal|null
decl_stmt|;
specifier|private
name|Expression
name|keyExpression
init|=
literal|null
decl_stmt|;
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|ctxExpr
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextExpression
operator|=
name|ctxExpr
expr_stmt|;
block|}
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|ctxExpr
parameter_list|,
name|String
name|keyString
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|ctxExpr
argument_list|)
expr_stmt|;
name|this
operator|.
name|keys
operator|=
operator|new
name|StringValue
argument_list|(
name|keyString
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|ctxExpr
parameter_list|,
name|int
name|position
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|ctxExpr
argument_list|)
expr_stmt|;
name|this
operator|.
name|keys
operator|=
operator|new
name|IntegerValue
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Lookup
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|ctxExpr
parameter_list|,
name|Expression
name|keyExpression
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|ctxExpr
argument_list|)
expr_stmt|;
name|this
operator|.
name|keyExpression
operator|=
name|keyExpression
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
specifier|final
name|AnalyzeContextInfo
name|contextCopy
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextExpression
operator|!=
literal|null
condition|)
block|{
name|contextExpression
operator|.
name|analyze
argument_list|(
name|contextCopy
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyExpression
operator|!=
literal|null
condition|)
block|{
name|keyExpression
operator|.
name|analyze
argument_list|(
name|contextCopy
argument_list|)
expr_stmt|;
block|}
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
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
name|Sequence
name|leftSeq
decl_stmt|;
if|if
condition|(
name|contextExpression
operator|==
literal|null
condition|)
block|{
name|leftSeq
operator|=
name|contextSequence
expr_stmt|;
block|}
else|else
block|{
name|leftSeq
operator|=
name|contextExpression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
specifier|final
name|int
name|contextType
init|=
name|leftSeq
operator|.
name|getItemType
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextType
argument_list|,
name|Type
operator|.
name|MAP
argument_list|)
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextType
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|)
operator|)
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
name|XPTY0004
argument_list|,
literal|"expression to the left of a lookup operator needs to be a sequence of maps or arrays"
argument_list|)
throw|;
block|}
if|if
condition|(
name|keyExpression
operator|!=
literal|null
condition|)
block|{
name|keys
operator|=
name|keyExpression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|keys
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
try|try
block|{
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|leftSeq
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
name|LookupSupport
name|item
init|=
operator|(
name|LookupSupport
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|keys
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|SequenceIterator
name|j
init|=
name|keys
operator|.
name|iterate
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|AtomicValue
name|key
init|=
name|j
operator|.
name|nextItem
argument_list|()
operator|.
name|atomize
argument_list|()
decl_stmt|;
name|Sequence
name|value
init|=
name|item
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|result
operator|.
name|addAll
argument_list|(
name|item
operator|.
name|keys
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|setLocation
argument_list|(
name|getLine
argument_list|()
argument_list|,
name|getColumn
argument_list|()
argument_list|,
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
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
name|ITEM
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
name|ZERO_OR_MORE
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
if|if
condition|(
name|contextExpression
operator|!=
literal|null
condition|)
block|{
name|contextExpression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"?"
argument_list|)
expr_stmt|;
if|if
condition|(
name|keyExpression
operator|==
literal|null
operator|&&
name|keys
operator|!=
literal|null
operator|&&
name|keys
operator|.
name|getItemCount
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|dumper
operator|.
name|display
argument_list|(
name|keys
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
comment|// impossible
block|}
block|}
if|else if
condition|(
name|keyExpression
operator|!=
literal|null
condition|)
block|{
name|keyExpression
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
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
name|contextExpression
operator|!=
literal|null
condition|)
block|{
name|contextExpression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|keyExpression
operator|!=
literal|null
condition|)
block|{
name|keyExpression
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
interface|interface
name|LookupSupport
block|{
specifier|public
name|Sequence
name|get
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Sequence
name|keys
parameter_list|()
throws|throws
name|XPathException
function_decl|;
block|}
block|}
end_class

end_unit

