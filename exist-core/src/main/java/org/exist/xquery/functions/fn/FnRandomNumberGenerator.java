begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|ints
operator|.
name|IntArrayList
import|;
end_import

begin_import
import|import
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastutil
operator|.
name|ints
operator|.
name|IntList
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
name|MapType
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
name|util
operator|.
name|Random
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDSL
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
operator|.
name|FnModule
operator|.
name|functionSignature
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
operator|.
name|FnModule
operator|.
name|functionSignatures
import|;
end_import

begin_class
specifier|public
class|class
name|FnRandomNumberGenerator
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|String
name|FS_RANDOM_NUMBER_GENERATOR_NAME
init|=
literal|"random-number-generator"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|FS_RANDOM_NUMBER_GENERATOR_RETURN_TYPE
init|=
name|returns
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
literal|"The function returns a random number generator. "
operator|+
literal|"A random number generator is represented as a map containing three entries. "
operator|+
literal|"The keys of each entry are strings: `number`, `next`, and `permute`."
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|FunctionSignature
index|[]
name|FS_RANDOM_NUMBER_GENERATOR
init|=
name|functionSignatures
argument_list|(
name|FS_RANDOM_NUMBER_GENERATOR_NAME
argument_list|,
literal|"Returns a random number generator, which can be used to generate sequences of random numbers."
argument_list|,
name|FS_RANDOM_NUMBER_GENERATOR_RETURN_TYPE
argument_list|,
name|arities
argument_list|(
name|arity
argument_list|()
argument_list|,
name|arity
argument_list|(
name|param
argument_list|(
literal|"seed"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
literal|"A seed value for the random generator"
argument_list|)
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|FnRandomNumberGenerator
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Random
name|random
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|1
operator|&&
operator|!
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|random
operator|=
operator|new
name|Random
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toJavaObject
argument_list|(
name|long
operator|.
name|class
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|random
operator|=
operator|new
name|Random
argument_list|()
expr_stmt|;
block|}
return|return
name|buildResult
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|MapType
name|buildResult
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Random
name|random
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|MapType
name|result
init|=
operator|new
name|MapType
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"number"
argument_list|)
argument_list|,
operator|new
name|DoubleValue
argument_list|(
name|random
operator|.
name|nextDouble
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"next"
argument_list|)
argument_list|,
name|nextFunction
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"permute"
argument_list|)
argument_list|,
name|permuteFunction
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
specifier|static
name|FunctionReference
name|nextFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Random
name|random
parameter_list|)
block|{
specifier|final
name|NextFunction
name|nextFunction
init|=
operator|new
name|NextFunction
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
decl_stmt|;
specifier|final
name|FunctionCall
name|nextFunctionCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|nextFunction
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionReference
argument_list|(
name|nextFunctionCall
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|FunctionReference
name|permuteFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Random
name|random
parameter_list|)
block|{
specifier|final
name|PermuteFunction
name|permuteFunction
init|=
operator|new
name|PermuteFunction
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
decl_stmt|;
specifier|final
name|FunctionCall
name|permuteFunctionCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|permuteFunction
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunctionReference
argument_list|(
name|permuteFunctionCall
argument_list|)
return|;
block|}
specifier|private
specifier|static
class|class
name|NextFunction
extends|extends
name|UserDefinedFunction
block|{
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
specifier|public
name|NextFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|functionSignature
argument_list|(
literal|"random-number-generator-next"
argument_list|,
literal|"Gets the next random number generator."
argument_list|,
name|FS_RANDOM_NUMBER_GENERATOR_RETURN_TYPE
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|buildResult
argument_list|(
name|context
argument_list|,
name|random
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
if|if
condition|(
name|visited
condition|)
block|{
return|return;
block|}
name|visited
operator|=
literal|true
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
class|class
name|PermuteFunction
extends|extends
name|UserDefinedFunction
block|{
specifier|private
specifier|final
name|Random
name|random
decl_stmt|;
specifier|public
name|PermuteFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|Random
name|random
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|functionSignature
argument_list|(
literal|"random-number-generator-permute"
argument_list|,
literal|"Takes an arbitrary sequence as its argument, and returns a random permutation of that sequence."
argument_list|,
name|returnsOptMany
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|)
argument_list|,
name|optManyParam
argument_list|(
literal|"arg"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
literal|"An arbitrary sequence"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|this
operator|.
name|random
operator|=
name|random
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|args
index|[]
init|=
name|getCurrentArguments
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|==
literal|null
operator|||
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|Sequence
name|in
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|in
return|;
block|}
name|int
name|remaining
init|=
name|in
operator|.
name|getItemCount
argument_list|()
decl_stmt|;
specifier|final
name|IntList
name|availableIndexes
init|=
operator|new
name|IntArrayList
argument_list|(
name|remaining
argument_list|)
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
name|remaining
condition|;
name|i
operator|++
control|)
block|{
name|availableIndexes
operator|.
name|add
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|(
name|remaining
argument_list|)
decl_stmt|;
name|result
operator|.
name|setIsOrdered
argument_list|(
literal|true
argument_list|)
expr_stmt|;
while|while
condition|(
name|remaining
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|x
init|=
name|random
operator|.
name|nextInt
argument_list|(
name|remaining
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idx
init|=
name|availableIndexes
operator|.
name|removeInt
argument_list|(
name|x
argument_list|)
decl_stmt|;
name|result
operator|.
name|add
argument_list|(
name|in
operator|.
name|itemAt
argument_list|(
name|idx
argument_list|)
argument_list|)
expr_stmt|;
name|remaining
operator|--
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
if|if
condition|(
name|visited
condition|)
block|{
return|return;
block|}
name|visited
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
