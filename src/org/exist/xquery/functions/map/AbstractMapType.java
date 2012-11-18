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
name|map
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
name|fn
operator|.
name|FunDistinctValues
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
name|text
operator|.
name|Collator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Comparator
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * Abstract base class for map types. A map item is also a function item. This class thus extends  * {@link FunctionReference} to allow the item to be called in a dynamic function  * call.  *  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractMapType
extends|extends
name|FunctionReference
implements|implements
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
implements|,
name|Iterable
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|AtomicValue
argument_list|>
name|DEFAULT_COMPARATOR
init|=
operator|new
name|FunDistinctValues
operator|.
name|ValueComparator
argument_list|(
literal|null
argument_list|)
decl_stmt|;
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
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Internal accessor function for maps."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
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
name|EXACTLY_ONE
argument_list|,
literal|"The key to look up"
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
init|=
literal|null
decl_stmt|;
specifier|protected
name|XQueryContext
name|context
decl_stmt|;
specifier|public
name|AbstractMapType
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
block|}
specifier|public
specifier|abstract
name|Sequence
name|get
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|contains
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|Sequence
name|keys
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|AbstractMapType
name|remove
parameter_list|(
name|AtomicValue
name|key
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|int
name|getItemCount
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|int
name|getItemType
parameter_list|()
function_decl|;
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
name|MAP
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|setValue
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|()
throw|;
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
name|getAccessorFunc
argument_list|()
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
name|getAccessorFunc
argument_list|()
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
name|getAccessorFunc
argument_list|()
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
name|getAccessorFunc
argument_list|()
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Comparator
argument_list|<
name|AtomicValue
argument_list|>
name|getComparator
parameter_list|(
name|String
name|collation
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|collation
operator|!=
literal|null
condition|)
block|{
name|Collator
name|collator
init|=
name|this
operator|.
name|context
operator|.
name|getCollator
argument_list|(
name|collation
argument_list|)
decl_stmt|;
return|return
operator|new
name|FunDistinctValues
operator|.
name|ValueComparator
argument_list|(
name|collator
argument_list|)
return|;
block|}
return|return
name|DEFAULT_COMPARATOR
return|;
block|}
comment|/**      * Return the accessor function. Will be created on demand.      */
specifier|protected
name|InternalFunctionCall
name|getAccessorFunc
parameter_list|()
block|{
name|initFunction
argument_list|()
expr_stmt|;
return|return
name|accessorFunc
return|;
block|}
comment|/**      * Lazy initialization of the accessor function. Creating      * this for every map would be too expensive and we thus      * only instantiate it on demand.      */
specifier|protected
name|void
name|initFunction
parameter_list|()
block|{
if|if
condition|(
name|this
operator|.
name|accessorFunc
operator|!=
literal|null
condition|)
return|return;
name|Function
name|fn
init|=
operator|new
name|AccessorFunc
argument_list|(
name|this
operator|.
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
return|return
name|AbstractMapType
operator|.
name|this
operator|.
name|get
argument_list|(
operator|(
name|AtomicValue
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
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

