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
name|BasicFunction
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
name|value
operator|.
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|SequenceIterator
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_class
specifier|public
class|class
name|FunHigherOrderFun
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"map"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Applies the function item $function to every item from the sequence "
operator|+
literal|"$sequence in turn, returning the concatenation of the resulting sequences in order."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the function to call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sequence"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence on which to apply the function"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of applying the function to each item of the sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"filter"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Returns those items from the sequence $sequence for which the supplied function $function returns true."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the function to call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sequence"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence to filter"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of filtering the sequence"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"fold-left"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Processes the supplied sequence from left to right, applying the supplied function repeatedly to each "
operator|+
literal|"item in turn, together with an accumulated result value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the function to call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"zero"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"initial value to start with"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sequence"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence to filter"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of the fold-left operation"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"fold-right"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Processes the supplied sequence from right to left, applying the supplied function repeatedly to each "
operator|+
literal|"item in turn, together with an accumulated result value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the function to call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"zero"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"initial value to start with"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"sequence"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"the sequence to filter"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of the fold-right operation"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"map-pairs"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Applies the function item $f to successive pairs of items taken one from $seq1 and one from $seq2, "
operator|+
literal|"returning the concatenation of the resulting sequences in order."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the function to call"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"seq1"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"first sequence to take items from"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"seq2"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"second sequence to take items from"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"result of the map-pairs operation"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|FunHigherOrderFun
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
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
literal|0
index|]
decl_stmt|;
name|ref
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"map"
argument_list|)
condition|)
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|1
index|]
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|r
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|item
operator|.
name|toSequence
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"filter"
argument_list|)
condition|)
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|1
index|]
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
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|Sequence
name|r
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|item
operator|.
name|toSequence
argument_list|()
block|}
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"fold-left"
argument_list|)
condition|)
block|{
name|Sequence
name|zero
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|Sequence
name|input
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
while|while
condition|(
operator|!
name|input
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SequenceIterator
name|i
init|=
name|input
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|zero
operator|=
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|zero
block|,
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|toSequence
argument_list|()
block|}
argument_list|)
expr_stmt|;
name|ValueSequence
name|tail
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|tail
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|input
operator|=
name|tail
expr_stmt|;
block|}
name|result
operator|=
name|zero
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"fold-right"
argument_list|)
condition|)
block|{
name|Sequence
name|zero
init|=
name|args
index|[
literal|1
index|]
decl_stmt|;
name|Sequence
name|input
init|=
name|args
index|[
literal|2
index|]
decl_stmt|;
name|result
operator|=
name|foldRight
argument_list|(
name|ref
argument_list|,
name|zero
argument_list|,
name|input
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"map-pairs"
argument_list|)
condition|)
block|{
name|SequenceIterator
name|i1
init|=
name|args
index|[
literal|1
index|]
operator|.
name|iterate
argument_list|()
decl_stmt|;
name|SequenceIterator
name|i2
init|=
name|args
index|[
literal|2
index|]
operator|.
name|iterate
argument_list|()
decl_stmt|;
while|while
condition|(
name|i1
operator|.
name|hasNext
argument_list|()
operator|&&
name|i2
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Sequence
name|r
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|i1
operator|.
name|nextItem
argument_list|()
operator|.
name|toSequence
argument_list|()
block|,
name|i2
operator|.
name|nextItem
argument_list|()
operator|.
name|toSequence
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|private
name|Sequence
name|foldRight
parameter_list|(
name|FunctionReference
name|ref
parameter_list|,
name|Sequence
name|zero
parameter_list|,
name|Sequence
name|seq
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|seq
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|zero
return|;
name|Sequence
name|head
init|=
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toSequence
argument_list|()
decl_stmt|;
name|Sequence
name|tailResult
init|=
name|foldRight
argument_list|(
name|ref
argument_list|,
name|zero
argument_list|,
name|seq
operator|.
name|tail
argument_list|()
argument_list|,
name|contextSequence
argument_list|)
decl_stmt|;
return|return
name|ref
operator|.
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|head
block|,
name|tailResult
block|}
argument_list|)
return|;
block|}
block|}
end_class

end_unit

