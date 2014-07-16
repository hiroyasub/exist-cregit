begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|range
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
name|indexing
operator|.
name|range
operator|.
name|RangeIndexWorker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Occurrences
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

begin_class
specifier|public
class|class
name|IndexKeys
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
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
literal|"index-keys-for-field"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Retrieve all index keys contained in a range index which has been defined with a field name. Similar to"
operator|+
literal|"util:index-keys, but works with fields."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"field"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The field to use"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function-reference"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function reference as created by the util:function function. "
operator|+
literal|"It can be an arbitrary user-defined function, but it should take exactly 2 arguments: "
operator|+
literal|"1) the current index key as found in the range index as an atomic value, 2) a sequence "
operator|+
literal|"containing three int values: a) the overall frequency of the key within the node set, "
operator|+
literal|"b) the number of distinct documents in the node set the key occurs in, "
operator|+
literal|"c) the current position of the key in the whole list of keys returned."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"max-number-returned"
argument_list|,
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The maximum number of returned keys"
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
literal|"the results of the eval of the $function-reference"
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"index-keys-for-field"
argument_list|,
name|RangeIndexModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RangeIndexModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Retrieve all index keys contained in a range index which has been defined with a field name. Similar to"
operator|+
literal|"util:index-keys, but works with fields."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"field"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The field to use"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"start-value"
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"Only index keys of the same type but being greater than $start-value will be reported for non-string types. For string types, only keys starting with the given prefix are reported."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"function-reference"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function reference as created by the util:function function. "
operator|+
literal|"It can be an arbitrary user-defined function, but it should take exactly 2 arguments: "
operator|+
literal|"1) the current index key as found in the range index as an atomic value, 2) a sequence "
operator|+
literal|"containing three int values: a) the overall frequency of the key within the node set, "
operator|+
literal|"b) the number of distinct documents in the node set the key occurs in, "
operator|+
literal|"c) the current position of the key in the whole list of keys returned."
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"max-number-returned"
argument_list|,
name|Type
operator|.
name|INT
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The maximum number of returned keys"
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
literal|"the results of the eval of the $function-reference"
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|IndexKeys
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
name|int
name|arg
init|=
literal|0
decl_stmt|;
specifier|final
name|String
name|field
init|=
name|args
index|[
name|arg
operator|++
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|start
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|4
condition|)
block|{
name|start
operator|=
name|args
index|[
name|arg
operator|++
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
operator|)
name|args
index|[
name|arg
operator|++
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|max
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
operator|!
name|args
index|[
name|arg
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|max
operator|=
operator|(
operator|(
name|IntegerValue
operator|)
name|args
index|[
name|arg
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
expr_stmt|;
block|}
specifier|final
name|Sequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
specifier|final
name|RangeIndexWorker
name|worker
init|=
operator|(
name|RangeIndexWorker
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getIndexController
argument_list|()
operator|.
name|getWorkerByIndexName
argument_list|(
literal|"range-index"
argument_list|)
decl_stmt|;
name|Occurrences
index|[]
name|occur
init|=
name|worker
operator|.
name|scanIndexByField
argument_list|(
name|field
argument_list|,
name|contextSequence
operator|==
literal|null
condition|?
name|context
operator|.
name|getStaticallyKnownDocuments
argument_list|()
else|:
name|contextSequence
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|start
argument_list|,
name|max
argument_list|)
decl_stmt|;
specifier|final
name|int
name|len
init|=
operator|(
name|max
operator|!=
operator|-
literal|1
operator|&&
name|occur
operator|.
name|length
operator|>
name|max
condition|?
name|max
else|:
name|occur
operator|.
name|length
operator|)
decl_stmt|;
specifier|final
name|Sequence
name|params
index|[]
init|=
operator|new
name|Sequence
index|[
literal|2
index|]
decl_stmt|;
name|ValueSequence
name|data
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|params
index|[
literal|0
index|]
operator|=
operator|new
name|StringValue
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getTerm
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getOccurrences
argument_list|()
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|occur
index|[
name|j
index|]
operator|.
name|getDocuments
argument_list|()
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|j
operator|+
literal|1
argument_list|,
name|Type
operator|.
name|UNSIGNED_INT
argument_list|)
argument_list|)
expr_stmt|;
name|params
index|[
literal|1
index|]
operator|=
name|data
expr_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|ref
operator|.
name|evalFunction
argument_list|(
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|,
literal|null
argument_list|,
name|params
argument_list|)
argument_list|)
expr_stmt|;
name|data
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit
