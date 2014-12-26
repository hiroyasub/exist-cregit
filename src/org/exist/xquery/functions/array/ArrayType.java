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
name|clj_ds
operator|.
name|TransientVector
import|;
end_import

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
name|Iterator
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
comment|/**  * Implements the array type (XQuery 3.1). An array is also a function. This class thus extends  * {@link FunctionReference} to allow the item to be called in a dynamic function  * call.  */
end_comment

begin_class
specifier|public
class|class
name|ArrayType
extends|extends
name|FunctionReference
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
literal|"n"
argument_list|,
name|Type
operator|.
name|POSITIVE_INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the position of the item to retrieve from the array"
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
argument_list|<
name|Sequence
argument_list|>
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
name|tail
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|ARRAY
condition|?
name|tail
else|:
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

