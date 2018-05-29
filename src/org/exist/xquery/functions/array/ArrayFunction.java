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
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|function
operator|.
name|FunctionE
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
name|HashMap
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
comment|/**  * Functions on arrays {@link http://www.w3.org/TR/xpath-functions-31/#array-functions}.  *  * @author Wolf  */
end_comment

begin_class
specifier|public
class|class
name|ArrayFunction
extends|extends
name|BasicFunction
block|{
specifier|private
enum|enum
name|Fn
block|{
name|SIZE
argument_list|(
literal|"size"
argument_list|)
block|,
name|GET
argument_list|(
literal|"get"
argument_list|)
block|,
name|APPEND
argument_list|(
literal|"append"
argument_list|)
block|,
name|HEAD
argument_list|(
literal|"head"
argument_list|)
block|,
name|TAIL
argument_list|(
literal|"tail"
argument_list|)
block|,
name|SUBARRAY
argument_list|(
literal|"subarray"
argument_list|)
block|,
name|REMOVE
argument_list|(
literal|"remove"
argument_list|)
block|,
name|INSERT_BEFORE
argument_list|(
literal|"insert-before"
argument_list|)
block|,
name|REVERSE
argument_list|(
literal|"reverse"
argument_list|)
block|,
name|JOIN
argument_list|(
literal|"join"
argument_list|)
block|,
name|FOR_EACH
argument_list|(
literal|"for-each"
argument_list|)
block|,
name|FILTER
argument_list|(
literal|"filter"
argument_list|)
block|,
name|FOLD_LEFT
argument_list|(
literal|"fold-left"
argument_list|)
block|,
name|FOLD_RIGHT
argument_list|(
literal|"fold-right"
argument_list|)
block|,
name|FOR_EACH_PAIR
argument_list|(
literal|"for-each-pair"
argument_list|)
block|,
name|FLATTEN
argument_list|(
literal|"flatten"
argument_list|)
block|;
specifier|final
specifier|static
name|Map
argument_list|<
name|String
argument_list|,
name|Fn
argument_list|>
name|fnMap
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
static|static
block|{
for|for
control|(
name|Fn
name|fn
range|:
name|Fn
operator|.
name|values
argument_list|()
control|)
block|{
name|fnMap
operator|.
name|put
argument_list|(
name|fn
operator|.
name|fname
argument_list|,
name|fn
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|Fn
name|get
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|fnMap
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|private
specifier|final
name|String
name|fname
decl_stmt|;
name|Fn
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|fname
operator|=
name|name
expr_stmt|;
block|}
block|}
specifier|public
specifier|static
specifier|final
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
name|Fn
operator|.
name|SIZE
operator|.
name|fname
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
literal|"Returns the number of members in the supplied array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The number of members in the supplied array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|GET
operator|.
name|fname
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
literal|"Gets the value at the specified position in the supplied array (counting from 1). This is the same "
operator|+
literal|"as calling $array($index)."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
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
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The value at $index"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|APPEND
operator|.
name|fname
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
literal|"Returns an array containing all the members of the supplied array, plus one additional"
operator|+
literal|"member at the end."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"appendage"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The items to append"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"A copy of $array with the new member attached"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|HEAD
operator|.
name|fname
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
literal|"Returns the first member of an array, i.e. $array(1)"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
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
literal|"The first member of the array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|TAIL
operator|.
name|fname
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
literal|"Returns an array containing all members except the first from a supplied array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A new array containing all members except the first"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|SUBARRAY
operator|.
name|fname
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
literal|"Gets an array containing all members from a supplied array starting at a supplied position, up to the end of the array"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The start index"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A new array containing all members from $start"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|SUBARRAY
operator|.
name|fname
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
literal|"Gets an array containing all members from a supplied array starting at a supplied position, up to a specified length."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The start index"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"length"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Length of the subarray"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A new array containing all members from $start up to the specified length"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|REMOVE
operator|.
name|fname
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
literal|"Returns an array containing all members from $array except the member whose position is $position."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"position"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Position of the member to remove"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A new array containing all members except the one at $position"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|INSERT_BEFORE
operator|.
name|fname
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
literal|"Returns an array containing all the members of the supplied array, with one additional member at a specified position."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"position"
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Position at which the new member is inserted"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"member"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The member to insert"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"A new array containing all members plus the new member"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|REVERSE
operator|.
name|fname
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
literal|"Returns an array containing all the members of the supplied array, but in reverse order."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array in reverse order"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|JOIN
operator|.
name|fname
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
literal|"Concatenates the contents of several arrays into a single array"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"arrays"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The arrays to join"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resulting array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FOR_EACH
operator|.
name|fname
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
literal|"Returns an array whose size is the same as array:size($array), in which each member is computed by applying "
operator|+
literal|"$function to the corresponding member of $array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array to process"
argument_list|)
block|,
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
literal|"The function called on each member of the array"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resulting array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FILTER
operator|.
name|fname
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
literal|"Returns an array containing those members of the $array for which $function returns true."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array to process"
argument_list|)
block|,
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
literal|"The function called on each member of the array"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resulting array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FOLD_LEFT
operator|.
name|fname
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
literal|"Evaluates the supplied function cumulatively on successive values of the supplied array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array to process"
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
literal|"Start value"
argument_list|)
block|,
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
literal|"The function to call"
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
literal|"The result of the cumulative function call"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FOLD_RIGHT
operator|.
name|fname
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
literal|"Evaluates the supplied function cumulatively on successive values of the supplied array."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The array to process"
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
literal|"Start value"
argument_list|)
block|,
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
literal|"The function to call"
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
literal|"The result of the cumulative function call"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FOR_EACH_PAIR
operator|.
name|fname
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
literal|"Returns an array obtained by evaluating the supplied function once for each pair of members at the same position in the two "
operator|+
literal|"supplied arrays."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array1"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The first array to process"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"array2"
argument_list|,
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The second array to process"
argument_list|)
block|,
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
literal|"The function to call for each pair"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ARRAY
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The resulting array"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
name|Fn
operator|.
name|FLATTEN
operator|.
name|fname
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
literal|"Replaces an array appearing in a supplied sequence with the members of the array, recursively."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"input"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The sequence to flatten"
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
literal|"The resulting sequence"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|private
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|ArrayFunction
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
if|if
condition|(
name|context
operator|.
name|getXQueryVersion
argument_list|()
operator|<
literal|31
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
name|EXXQDY0004
argument_list|,
literal|"arrays are only available in XQuery 3.1, but version declaration states "
operator|+
name|context
operator|.
name|getXQueryVersion
argument_list|()
argument_list|)
throw|;
block|}
specifier|final
name|Fn
name|called
init|=
name|Fn
operator|.
name|get
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|getLocalPart
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|called
condition|)
block|{
case|case
name|JOIN
case|:
specifier|final
name|List
argument_list|<
name|ArrayType
argument_list|>
name|arrays
init|=
operator|new
name|ArrayList
argument_list|<
name|ArrayType
argument_list|>
argument_list|(
name|args
index|[
literal|0
index|]
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
name|args
index|[
literal|0
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
name|arrays
operator|.
name|add
argument_list|(
operator|(
name|ArrayType
operator|)
name|i
operator|.
name|nextItem
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|ArrayType
operator|.
name|join
argument_list|(
name|context
argument_list|,
name|arrays
argument_list|)
return|;
case|case
name|FLATTEN
case|:
specifier|final
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|(
name|args
index|[
literal|0
index|]
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
name|ArrayType
operator|.
name|flatten
argument_list|(
name|args
index|[
literal|0
index|]
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
default|default:
specifier|final
name|ArrayType
name|array
init|=
operator|(
name|ArrayType
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
switch|switch
condition|(
name|called
condition|)
block|{
case|case
name|SIZE
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
name|array
operator|.
name|getSize
argument_list|()
argument_list|)
return|;
case|case
name|GET
case|:
specifier|final
name|IntegerValue
name|index
init|=
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|array
operator|.
name|get
argument_list|(
name|index
operator|.
name|getInt
argument_list|()
operator|-
literal|1
argument_list|)
return|;
case|case
name|APPEND
case|:
return|return
name|array
operator|.
name|append
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|)
return|;
case|case
name|HEAD
case|:
if|if
condition|(
name|array
operator|.
name|getSize
argument_list|()
operator|==
literal|0
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
literal|"Array is empty"
argument_list|)
throw|;
block|}
return|return
name|array
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
case|case
name|TAIL
case|:
if|if
condition|(
name|array
operator|.
name|getSize
argument_list|()
operator|==
literal|0
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
literal|"Array is empty"
argument_list|)
throw|;
block|}
return|return
name|array
operator|.
name|tail
argument_list|()
return|;
case|case
name|SUBARRAY
case|:
specifier|final
name|int
name|start
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|int
name|end
init|=
name|array
operator|.
name|getSize
argument_list|()
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|3
condition|)
block|{
specifier|final
name|int
name|length
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|2
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|+
name|length
operator|>
name|array
operator|.
name|getSize
argument_list|()
operator|+
literal|1
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
literal|"Array index out of bounds: "
operator|+
operator|(
name|start
operator|+
name|length
operator|-
literal|1
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
name|length
operator|<
literal|0
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
name|FOAY0002
argument_list|,
literal|"Specified length< 0"
argument_list|)
throw|;
block|}
name|end
operator|=
name|start
operator|+
name|length
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|start
operator|<
literal|1
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
literal|"Start index into array is< 1"
argument_list|)
throw|;
block|}
return|return
name|array
operator|.
name|subarray
argument_list|(
name|start
operator|-
literal|1
argument_list|,
name|end
argument_list|)
return|;
case|case
name|REMOVE
case|:
specifier|final
name|int
name|rpos
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|rpos
operator|<
literal|1
operator|||
name|rpos
operator|>
name|array
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
literal|"Index of item to remove ("
operator|+
name|rpos
operator|+
literal|") is out of bounds"
argument_list|)
throw|;
block|}
return|return
name|array
operator|.
name|remove
argument_list|(
name|rpos
operator|-
literal|1
argument_list|)
return|;
case|case
name|INSERT_BEFORE
case|:
specifier|final
name|int
name|ipos
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
if|if
condition|(
name|ipos
operator|<
literal|1
operator|||
name|ipos
operator|>
name|array
operator|.
name|getSize
argument_list|()
operator|+
literal|1
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
literal|"Index of item to insert ("
operator|+
name|ipos
operator|+
literal|") is out of bounds"
argument_list|)
throw|;
block|}
return|return
name|array
operator|.
name|insertBefore
argument_list|(
name|ipos
operator|-
literal|1
argument_list|,
name|args
index|[
literal|2
index|]
argument_list|)
return|;
case|case
name|REVERSE
case|:
return|return
name|array
operator|.
name|reverse
argument_list|()
return|;
case|case
name|FOR_EACH
case|:
return|return
name|getFunction
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|,
name|array
operator|::
name|forEach
argument_list|)
return|;
case|case
name|FILTER
case|:
return|return
name|getFunction
argument_list|(
name|args
index|[
literal|1
index|]
argument_list|,
name|array
operator|::
name|filter
argument_list|)
return|;
case|case
name|FOLD_LEFT
case|:
return|return
name|getFunction
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|,
name|ref
lambda|->
name|array
operator|.
name|foldLeft
argument_list|(
name|ref
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
case|case
name|FOLD_RIGHT
case|:
return|return
name|getFunction
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|,
name|ref
lambda|->
name|array
operator|.
name|foldRight
argument_list|(
name|ref
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
argument_list|)
return|;
case|case
name|FOR_EACH_PAIR
case|:
return|return
name|getFunction
argument_list|(
name|args
index|[
literal|2
index|]
argument_list|,
name|ref
lambda|->
name|array
operator|.
name|forEachPair
argument_list|(
operator|(
name|ArrayType
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|,
name|ref
argument_list|)
argument_list|)
return|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown function: "
operator|+
name|getName
argument_list|()
argument_list|)
throw|;
block|}
specifier|private
name|Sequence
name|getFunction
parameter_list|(
name|Sequence
name|arg
parameter_list|,
name|FunctionE
argument_list|<
name|FunctionReference
argument_list|,
name|Sequence
argument_list|,
name|XPathException
argument_list|>
name|action
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
init|(
specifier|final
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|arg
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
init|)
block|{
name|ref
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
return|return
name|action
operator|.
name|apply
argument_list|(
name|ref
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

