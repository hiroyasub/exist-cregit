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
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_class
specifier|public
class|class
name|PartialFunctionApplication
extends|extends
name|AbstractExpression
block|{
specifier|public
specifier|final
specifier|static
name|String
name|PARTIAL_FUN_PREFIX
init|=
literal|"partial"
decl_stmt|;
specifier|protected
name|FunctionCall
name|function
decl_stmt|;
specifier|protected
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|PartialFunctionApplication
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionCall
name|call
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|function
operator|=
name|call
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
name|this
operator|.
name|cachedContextInfo
operator|=
name|contextInfo
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
name|function
operator|.
name|dump
argument_list|(
name|dumper
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
specifier|final
name|FunctionReference
name|newRef
init|=
name|createPartial
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|function
argument_list|)
decl_stmt|;
return|return
name|newRef
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
name|FUNCTION_REFERENCE
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
specifier|private
name|FunctionReference
name|createPartial
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|FunctionCall
name|staticCall
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|FunctionSignature
name|signature
init|=
name|staticCall
operator|.
name|getSignature
argument_list|()
decl_stmt|;
specifier|final
name|SequenceType
index|[]
name|paramTypes
init|=
name|signature
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
comment|// the parameters of the newly created inline function:
comment|// old params except the fixed ones
specifier|final
name|List
argument_list|<
name|SequenceType
argument_list|>
name|newParamTypes
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// the arguments to pass to the inner call
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
name|callArgs
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// parameter variables of the new inline function
specifier|final
name|List
argument_list|<
name|QName
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
comment|// the inline function
specifier|final
name|int
name|argCount
init|=
name|staticCall
operator|.
name|getArgumentCount
argument_list|()
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
name|argCount
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|Expression
name|param
init|=
name|staticCall
operator|.
name|getArgument
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|instanceof
name|Function
operator|.
name|Placeholder
condition|)
block|{
comment|// copy parameter sequence types
comment|// overloaded functions like concat may have an arbitrary number of arguments
if|if
condition|(
name|i
operator|<
name|paramTypes
operator|.
name|length
condition|)
block|{
name|newParamTypes
operator|.
name|add
argument_list|(
name|paramTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
else|else
comment|// overloaded function: add last sequence type
block|{
name|newParamTypes
operator|.
name|add
argument_list|(
name|paramTypes
index|[
name|paramTypes
operator|.
name|length
operator|-
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
comment|// create local parameter variables
specifier|final
name|QName
name|varName
init|=
operator|new
name|QName
argument_list|(
literal|"vp"
operator|+
name|i
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
name|variables
operator|.
name|add
argument_list|(
name|varName
argument_list|)
expr_stmt|;
comment|// the argument to the inner call is a variable ref
specifier|final
name|VariableReference
name|ref
init|=
operator|new
name|VariableReference
argument_list|(
name|context
argument_list|,
name|varName
argument_list|)
decl_stmt|;
name|callArgs
operator|.
name|add
argument_list|(
name|ref
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// fixed argument: just compute the argument value
try|try
block|{
name|param
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
specifier|final
name|Sequence
name|seq
init|=
name|param
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|callArgs
operator|.
name|add
argument_list|(
operator|new
name|PrecomputedValue
argument_list|(
name|context
argument_list|,
name|seq
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getLine
argument_list|()
operator|<=
literal|0
condition|)
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
block|}
comment|// append location of the function call to the exception message:
name|e
operator|.
name|addFunctionCall
argument_list|(
name|function
operator|.
name|functionDef
argument_list|,
name|this
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
block|}
specifier|final
name|SequenceType
index|[]
name|newParamArray
init|=
name|newParamTypes
operator|.
name|toArray
argument_list|(
operator|new
name|SequenceType
index|[
name|newParamTypes
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
specifier|final
name|QName
name|name
init|=
operator|new
name|QName
argument_list|(
name|PARTIAL_FUN_PREFIX
operator|+
name|hashCode
argument_list|()
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
decl_stmt|;
specifier|final
name|FunctionSignature
name|newSignature
init|=
operator|new
name|FunctionSignature
argument_list|(
name|name
argument_list|,
name|newParamArray
argument_list|,
name|signature
operator|.
name|getReturnType
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|UserDefinedFunction
name|func
init|=
operator|new
name|UserDefinedFunction
argument_list|(
name|context
argument_list|,
name|newSignature
argument_list|)
decl_stmt|;
name|func
operator|.
name|setLocation
argument_list|(
name|staticCall
operator|.
name|getLine
argument_list|()
argument_list|,
name|staticCall
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
comment|// add the created parameter variables to the function
for|for
control|(
specifier|final
name|QName
name|varName
range|:
name|variables
control|)
block|{
name|func
operator|.
name|addVariable
argument_list|(
name|varName
argument_list|)
expr_stmt|;
block|}
specifier|final
name|FunctionCall
name|innerCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|staticCall
argument_list|)
decl_stmt|;
name|innerCall
operator|.
name|setArguments
argument_list|(
name|callArgs
argument_list|)
expr_stmt|;
name|func
operator|.
name|setFunctionBody
argument_list|(
name|innerCall
argument_list|)
expr_stmt|;
specifier|final
name|FunctionCall
name|newCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|newCall
operator|.
name|setLocation
argument_list|(
name|staticCall
operator|.
name|getLine
argument_list|()
argument_list|,
name|staticCall
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
return|return
operator|new
name|FunctionReference
argument_list|(
name|newCall
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|PrecomputedValue
extends|extends
name|AbstractExpression
block|{
name|Sequence
name|sequence
decl_stmt|;
specifier|public
name|PrecomputedValue
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|input
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|sequence
operator|=
name|input
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
return|return
name|sequence
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
name|sequence
operator|.
name|getItemType
argument_list|()
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
block|}
block|}
end_class

end_unit

