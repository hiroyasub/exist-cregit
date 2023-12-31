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
name|array
package|;
end_package

begin_import
import|import
name|com
operator|.
name|github
operator|.
name|krukow
operator|.
name|clj_lang
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

begin_comment
comment|/**  * Implements the array type (XQuery 3.1). An array is also a function. This class thus extends  * {@link FunctionReference} to allow the item to be called in a dynamic function  * call.  *  * Based on immutable, persistent vectors. Operations like append, head, tail, reverse should be fast.  * Remove and insert-before require copying the array.  *  * @author Wolf  */
end_comment

begin_class
specifier|public
class|class
name|ArrayType
extends|extends
name|FunctionReference
implements|implements
name|Lookup
operator|.
name|LookupSupport
block|{
comment|// the signature of the function which is evaluated if the map is called as a function item
specifier|private
specifier|static
specifier|final
name|FunctionSignature
name|ACCESSOR
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get"
argument_list|,
name|ArrayModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|ArrayModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Internal accessor function for arrays."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"index"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The index"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|InternalFunctionCall
name|accessorFunc
decl_stmt|;
specifier|private
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
name|vector
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|ArrayType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|List
argument_list|<
name|Sequence
argument_list|>
name|items
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|vector
operator|=
name|PersistentVector
operator|.
name|create
argument_list|(
name|items
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ArrayType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|items
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|Sequence
argument_list|>
name|itemList
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|items
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|items
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
name|itemList
operator|.
name|add
argument_list|(
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|vector
operator|=
name|PersistentVector
operator|.
name|create
argument_list|(
name|itemList
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ArrayType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
name|vector
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|vector
operator|=
name|vector
expr_stmt|;
block|}
specifier|private
name|ArrayType
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
specifier|final
name|Function
name|fn
init|=
operator|new
name|AccessorFunc
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|this
operator|.
name|accessorFunc
operator|=
operator|new
name|InternalFunctionCall
argument_list|(
name|fn
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|get
parameter_list|(
name|int
name|n
parameter_list|)
block|{
return|return
name|vector
operator|.
name|nth
argument_list|(
name|n
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|get
parameter_list|(
specifier|final
name|AtomicValue
name|key
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
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
name|INTEGER
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Position argument for array lookup must be a positive integer"
argument_list|)
throw|;
block|}
specifier|final
name|int
name|pos
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|key
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|pos
operator|<=
literal|0
operator|||
name|pos
operator|>
name|getSize
argument_list|()
condition|)
block|{
specifier|final
name|String
name|startIdx
init|=
name|vector
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|?
literal|"0"
else|:
literal|"1"
decl_stmt|;
specifier|final
name|String
name|endIdx
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|vector
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FOAY0001
argument_list|,
literal|"Array index "
operator|+
name|pos
operator|+
literal|" out of bounds ("
operator|+
name|startIdx
operator|+
literal|".."
operator|+
name|endIdx
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
name|get
argument_list|(
name|pos
operator|-
literal|1
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|keys
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|asSequence
argument_list|()
return|;
block|}
specifier|public
name|Sequence
name|tail
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|vector
operator|.
name|length
argument_list|()
operator|==
literal|2
condition|)
block|{
specifier|final
name|Sequence
name|tail
init|=
name|vector
operator|.
name|nth
argument_list|(
literal|1
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
name|tail
argument_list|)
return|;
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
name|RT
operator|.
name|subvec
argument_list|(
name|vector
argument_list|,
literal|1
argument_list|,
name|vector
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|subarray
parameter_list|(
name|int
name|start
parameter_list|,
name|int
name|end
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
name|RT
operator|.
name|subvec
argument_list|(
name|vector
argument_list|,
name|start
argument_list|,
name|end
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|remove
parameter_list|(
name|int
name|position
parameter_list|)
throws|throws
name|XPathException
block|{
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
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
name|vector
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|position
operator|!=
name|i
condition|)
block|{
name|ret
operator|=
name|ret
operator|.
name|conj
argument_list|(
name|vector
operator|.
name|nth
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|insertBefore
parameter_list|(
name|int
name|position
parameter_list|,
name|Sequence
name|member
parameter_list|)
throws|throws
name|XPathException
block|{
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
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
name|vector
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|position
operator|==
name|i
condition|)
block|{
name|ret
operator|=
name|ret
operator|.
name|conj
argument_list|(
name|member
argument_list|)
expr_stmt|;
block|}
name|ret
operator|=
name|ret
operator|.
name|conj
argument_list|(
name|vector
operator|.
name|nth
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|position
operator|==
name|vector
operator|.
name|length
argument_list|()
condition|)
block|{
name|ret
operator|=
name|ret
operator|.
name|conj
argument_list|(
name|member
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|put
parameter_list|(
name|int
name|position
parameter_list|,
name|Sequence
name|member
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
name|vector
operator|.
name|assocN
argument_list|(
name|position
argument_list|,
name|member
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|ArrayType
name|join
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|List
argument_list|<
name|ArrayType
argument_list|>
name|arrays
parameter_list|)
block|{
specifier|final
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
argument_list|()
decl_stmt|;
for|for
control|(
name|ArrayType
name|type
range|:
name|arrays
control|)
block|{
for|for
control|(
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
init|=
name|type
operator|.
name|vector
operator|.
name|seq
argument_list|()
init|;
name|seq
operator|!=
literal|null
condition|;
name|seq
operator|=
name|seq
operator|.
name|next
argument_list|()
control|)
block|{
name|ret
operator|.
name|conj
argument_list|(
name|seq
operator|.
name|first
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Add member. Modifies the array! Don't use unless you're constructing a new array.      *      * @param seq the member sequence to add      */
specifier|public
name|void
name|add
parameter_list|(
name|Sequence
name|seq
parameter_list|)
block|{
name|vector
operator|=
name|vector
operator|.
name|cons
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return a new array with a member appended.      *      * @param seq the member sequence to append      * @return new array      */
specifier|public
name|ArrayType
name|append
parameter_list|(
name|Sequence
name|seq
parameter_list|)
block|{
return|return
operator|new
name|ArrayType
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|vector
operator|.
name|cons
argument_list|(
name|seq
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|reverse
parameter_list|()
block|{
specifier|final
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
name|rvec
init|=
name|PersistentVector
operator|.
name|create
argument_list|(
name|vector
operator|.
name|rseq
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|new
name|ArrayType
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|rvec
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|asSequence
parameter_list|()
throws|throws
name|XPathException
block|{
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|(
name|vector
operator|.
name|length
argument_list|()
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
name|vector
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|addAll
argument_list|(
name|vector
operator|.
name|nth
argument_list|(
name|i
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|public
name|Sequence
index|[]
name|toArray
parameter_list|()
block|{
specifier|final
name|Sequence
index|[]
name|array
init|=
operator|new
name|Sequence
index|[
name|vector
operator|.
name|length
argument_list|()
index|]
decl_stmt|;
return|return
operator|(
name|Sequence
index|[]
operator|)
name|RT
operator|.
name|seqToPassedArray
argument_list|(
name|vector
operator|.
name|seq
argument_list|()
argument_list|,
name|array
argument_list|)
return|;
block|}
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|vector
operator|.
name|length
argument_list|()
return|;
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
name|accessorFunc
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
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|accessorFunc
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|evalFunction
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|,
specifier|final
name|Sequence
index|[]
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AccessorFunc
name|af
init|=
operator|(
name|AccessorFunc
operator|)
name|accessorFunc
operator|.
name|getFunction
argument_list|()
decl_stmt|;
return|return
name|af
operator|.
name|eval
argument_list|(
name|seq
argument_list|,
name|contextSequence
argument_list|)
return|;
block|}
annotation|@
name|Override
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
name|accessorFunc
operator|.
name|setArguments
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
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
name|accessorFunc
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ARRAY
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ARRAY
return|;
block|}
annotation|@
name|Override
specifier|public
name|AtomicValue
name|atomize
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|vector
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|vector
operator|.
name|length
argument_list|()
operator|>
literal|1
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Expected single atomic value but found array with length "
operator|+
name|vector
operator|.
name|length
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Sequence
name|member
init|=
name|vector
operator|.
name|nth
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|member
operator|.
name|hasMany
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Expected single atomic value but found sequence of length "
operator|+
name|member
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|member
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
return|;
block|}
specifier|public
name|ArrayType
name|forEach
parameter_list|(
name|FunctionReference
name|ref
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|fargs
index|[]
init|=
operator|new
name|Sequence
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
init|=
name|vector
operator|.
name|seq
argument_list|()
init|;
name|seq
operator|!=
literal|null
condition|;
name|seq
operator|=
name|seq
operator|.
name|next
argument_list|()
control|)
block|{
name|fargs
index|[
literal|0
index|]
operator|=
name|seq
operator|.
name|first
argument_list|()
expr_stmt|;
name|ret
operator|.
name|conj
argument_list|(
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|fargs
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|forEachPair
parameter_list|(
name|ArrayType
name|other
parameter_list|,
name|FunctionReference
name|ref
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
argument_list|()
decl_stmt|;
for|for
control|(
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|i1
init|=
name|vector
operator|.
name|seq
argument_list|()
init|,
name|i2
init|=
name|other
operator|.
name|vector
operator|.
name|seq
argument_list|()
init|;
name|i1
operator|!=
literal|null
operator|&&
name|i2
operator|!=
literal|null
condition|;
name|i1
operator|=
name|i1
operator|.
name|next
argument_list|()
operator|,
name|i2
operator|=
name|i2
operator|.
name|next
argument_list|()
control|)
block|{
name|ret
operator|.
name|conj
argument_list|(
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|i1
operator|.
name|first
argument_list|()
block|,
name|i2
operator|.
name|first
argument_list|()
block|}
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|ArrayType
name|filter
parameter_list|(
name|FunctionReference
name|ref
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|ITransientCollection
argument_list|<
name|Sequence
argument_list|>
name|ret
init|=
name|PersistentVector
operator|.
name|emptyVector
argument_list|()
operator|.
name|asTransient
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|fargs
index|[]
init|=
operator|new
name|Sequence
index|[
literal|1
index|]
decl_stmt|;
for|for
control|(
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
init|=
name|vector
operator|.
name|seq
argument_list|()
init|;
name|seq
operator|!=
literal|null
condition|;
name|seq
operator|=
name|seq
operator|.
name|next
argument_list|()
control|)
block|{
name|fargs
index|[
literal|0
index|]
operator|=
name|seq
operator|.
name|first
argument_list|()
expr_stmt|;
specifier|final
name|Sequence
name|fret
init|=
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|fargs
argument_list|)
decl_stmt|;
if|if
condition|(
name|fret
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
block|{
name|ret
operator|.
name|conj
argument_list|(
name|fargs
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|ArrayType
argument_list|(
name|context
argument_list|,
operator|(
name|IPersistentVector
argument_list|<
name|Sequence
argument_list|>
operator|)
name|ret
operator|.
name|persistent
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|foldLeft
parameter_list|(
name|FunctionReference
name|ref
parameter_list|,
name|Sequence
name|zero
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
init|=
name|vector
operator|.
name|seq
argument_list|()
init|;
name|seq
operator|!=
literal|null
condition|;
name|seq
operator|=
name|seq
operator|.
name|next
argument_list|()
control|)
block|{
name|zero
operator|=
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
operator|new
name|Sequence
index|[]
block|{
name|zero
block|,
name|seq
operator|.
name|first
argument_list|()
block|}
argument_list|)
expr_stmt|;
block|}
return|return
name|zero
return|;
block|}
specifier|public
name|Sequence
name|foldRight
parameter_list|(
name|FunctionReference
name|ref
parameter_list|,
name|Sequence
name|zero
parameter_list|)
throws|throws
name|XPathException
block|{
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
init|=
name|vector
operator|.
name|seq
argument_list|()
decl_stmt|;
return|return
name|foldRight
argument_list|(
name|ref
argument_list|,
name|zero
argument_list|,
name|seq
argument_list|)
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
name|ISeq
argument_list|<
name|Sequence
argument_list|>
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|seq
operator|==
literal|null
condition|)
block|{
return|return
name|zero
return|;
block|}
specifier|final
name|Sequence
name|head
init|=
name|seq
operator|.
name|first
argument_list|()
decl_stmt|;
specifier|final
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
name|next
argument_list|()
argument_list|)
decl_stmt|;
return|return
name|ref
operator|.
name|evalFunction
argument_list|(
literal|null
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
specifier|protected
specifier|static
name|Sequence
name|flatten
parameter_list|(
name|Sequence
name|input
parameter_list|,
name|ValueSequence
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
for|for
control|(
name|SequenceIterator
name|i
init|=
name|input
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
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
specifier|final
name|Sequence
name|members
init|=
operator|(
operator|(
name|ArrayType
operator|)
name|item
operator|)
operator|.
name|asSequence
argument_list|()
decl_stmt|;
name|flatten
argument_list|(
name|members
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
specifier|static
name|Sequence
name|flatten
parameter_list|(
name|Item
name|item
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|item
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
specifier|final
name|Sequence
name|members
init|=
operator|(
operator|(
name|ArrayType
operator|)
name|item
operator|)
operator|.
name|asSequence
argument_list|()
decl_stmt|;
return|return
name|flatten
argument_list|(
name|members
argument_list|,
operator|new
name|ValueSequence
argument_list|(
name|members
operator|.
name|getItemCount
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
return|return
name|item
operator|.
name|toSequence
argument_list|()
return|;
block|}
comment|/**      * Flatten the given sequence by recursively replacing arrays with their member sequence.      *      * @param input the sequence to flatten      * @return flattened sequence      * @throws XPathException in case of dynamic error      */
specifier|public
specifier|static
name|Sequence
name|flatten
parameter_list|(
name|Sequence
name|input
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|input
operator|.
name|hasOne
argument_list|()
condition|)
block|{
return|return
name|flatten
argument_list|(
name|input
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
name|boolean
name|flatten
init|=
literal|false
decl_stmt|;
specifier|final
name|int
name|itemType
init|=
name|input
operator|.
name|getItemType
argument_list|()
decl_stmt|;
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
name|flatten
operator|=
literal|true
expr_stmt|;
block|}
if|else if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
comment|// may contain arrays - check
for|for
control|(
name|SequenceIterator
name|i
init|=
name|input
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
if|if
condition|(
name|i
operator|.
name|nextItem
argument_list|()
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|)
block|{
name|flatten
operator|=
literal|true
expr_stmt|;
break|break;
block|}
block|}
block|}
return|return
name|flatten
condition|?
name|flatten
argument_list|(
name|input
argument_list|,
operator|new
name|ValueSequence
argument_list|(
name|input
operator|.
name|getItemCount
argument_list|()
operator|*
literal|2
argument_list|)
argument_list|)
else|:
name|input
return|;
block|}
comment|/**      * The accessor function which will be evaluated if the map is called      * as a function item.      */
specifier|private
class|class
name|AccessorFunc
extends|extends
name|BasicFunction
block|{
specifier|public
name|AccessorFunc
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|ACCESSOR
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
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
name|n
init|=
name|v
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|n
operator|<=
literal|0
operator|||
name|n
operator|>
name|ArrayType
operator|.
name|this
operator|.
name|getSize
argument_list|()
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
name|FOAY0001
argument_list|,
literal|"Position "
operator|+
name|n
operator|+
literal|" does not exist in this array. Length is "
operator|+
name|ArrayType
operator|.
name|this
operator|.
name|getSize
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|ArrayType
operator|.
name|this
operator|.
name|get
argument_list|(
name|n
operator|-
literal|1
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

