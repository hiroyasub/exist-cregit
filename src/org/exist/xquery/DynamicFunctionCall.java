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
name|FunctionReference
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
name|Type
import|;
end_import

begin_class
specifier|public
class|class
name|DynamicFunctionCall
extends|extends
name|AbstractExpression
block|{
specifier|private
name|Expression
name|functionExpr
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|arguments
decl_stmt|;
specifier|private
name|boolean
name|isPartial
init|=
literal|false
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|DynamicFunctionCall
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|fun
parameter_list|,
name|List
argument_list|<
name|Expression
argument_list|>
name|args
parameter_list|,
name|boolean
name|partial
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|functionExpr
operator|=
name|fun
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|isPartial
operator|=
name|partial
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
name|cachedContextInfo
operator|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
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
name|functionExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|Expression
name|arg
range|:
name|arguments
control|)
block|{
name|arg
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
literal|')'
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
name|Sequence
name|funcSeq
init|=
name|functionExpr
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
name|funcSeq
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|EXACTLY_ONE
condition|)
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
literal|"Expected exactly one item for the function to be called, got "
operator|+
name|funcSeq
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
name|Item
name|item0
init|=
name|funcSeq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|item0
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|)
condition|)
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
literal|"Type error: expected function, got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|item0
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|item0
decl_stmt|;
comment|// if the call is a partial application, create a new function
if|if
condition|(
name|isPartial
condition|)
block|{
try|try
block|{
name|FunctionCall
name|call
init|=
name|ref
operator|.
name|getCall
argument_list|()
decl_stmt|;
name|call
operator|.
name|setArguments
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
name|PartialFunctionApplication
name|partialApp
init|=
operator|new
name|PartialFunctionApplication
argument_list|(
name|context
argument_list|,
name|call
argument_list|)
decl_stmt|;
return|return
name|partialApp
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
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
name|line
argument_list|,
name|column
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
else|else
block|{
name|ref
operator|.
name|setArguments
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
comment|// need to create a new AnalyzeContextInfo to avoid memory leak
comment|// cachedContextInfo will stay in memory
name|ref
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|cachedContextInfo
argument_list|)
argument_list|)
expr_stmt|;
comment|// Evaluate the function
return|return
name|ref
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
return|;
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
comment|// Unknown until the reference is resolved
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
block|}
block|}
end_class

end_unit

