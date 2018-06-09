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
name|Map
import|;
end_import

begin_comment
comment|/**  * Implements all functions of the map module.  */
end_comment

begin_class
specifier|public
class|class
name|MapFunction
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|QName
name|QN_MERGE
init|=
operator|new
name|QName
argument_list|(
literal|"merge"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_SIZE
init|=
operator|new
name|QName
argument_list|(
literal|"size"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_ENTRY
init|=
operator|new
name|QName
argument_list|(
literal|"entry"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_GET
init|=
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
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_PUT
init|=
operator|new
name|QName
argument_list|(
literal|"put"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_CONTAINS
init|=
operator|new
name|QName
argument_list|(
literal|"contains"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_KEYS
init|=
operator|new
name|QName
argument_list|(
literal|"keys"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_REMOVE
init|=
operator|new
name|QName
argument_list|(
literal|"remove"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|QName
name|QN_FOR_EACH
init|=
operator|new
name|QName
argument_list|(
literal|"for-each"
argument_list|,
name|MapModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|MapModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_MERGE
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_MERGE
argument_list|,
literal|"Returns a map that combines the entries from a number of existing maps."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"maps"
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"Existing maps to merge to create a new map."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_SIZE
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_SIZE
argument_list|,
literal|"Returns the number of entries in the supplied map."
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
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"Any map to determine the size of."
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_KEYS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_KEYS
argument_list|,
literal|"Returns a sequence containing all the key values present in a map."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|MapModule
operator|.
name|PREFIX
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_CONTAINS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_CONTAINS
argument_list|,
literal|"Tests whether a supplied map contains an entry for a given key."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|MapModule
operator|.
name|PREFIX
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|,
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
name|BOOLEAN
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_GET
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_GET
argument_list|,
literal|"Returns the value associated with a supplied key in a given map."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|MapModule
operator|.
name|PREFIX
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|,
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
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_PUT
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_PUT
argument_list|,
literal|"Returns a map containing all the contents of the supplied map, but with an additional entry, which replaces any existing entry for the same key."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|MapModule
operator|.
name|PREFIX
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|,
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
literal|"The key for the entry to insert"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The value for the entry to insert"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ENTRY
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_ENTRY
argument_list|,
literal|"Creates a map that contains a single entry (a key-value pair)."
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
literal|"The key"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The associated value"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOVE
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_REMOVE
argument_list|,
literal|"Constructs a new map by removing an entry from an existing map."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|MapModule
operator|.
name|PREFIX
argument_list|,
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|,
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
name|ZERO_OR_MORE
argument_list|,
literal|"The key to remove"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_FOR_EACH
init|=
operator|new
name|FunctionSignature
argument_list|(
name|QN_FOR_EACH
argument_list|,
literal|"takes any map as its $input argument and applies the supplied function to each entry in the map, in implementation-dependent order; the result is the sequence obtained by concatenating the results of these function calls. "
operator|+
literal|"The function supplied as $action takes two arguments. It is called supplying the key of the map entry as the first argument, and the associated value as the second argument."
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
name|MAP
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The map"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"action"
argument_list|,
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The function to be called for each entry"
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
name|AnalyzeContextInfo
name|cachedContextInfo
decl_stmt|;
specifier|public
name|MapFunction
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
name|void
name|analyze
parameter_list|(
specifier|final
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
if|if
condition|(
name|isCalledAs
argument_list|(
name|QN_MERGE
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|merge
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_SIZE
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|size
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_KEYS
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|keys
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_CONTAINS
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|contains
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_GET
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|get
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_PUT
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|put
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_ENTRY
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|entry
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_REMOVE
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|remove
argument_list|(
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|QN_FOR_EACH
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|forEach
argument_list|(
name|args
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Sequence
name|remove
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
return|return
name|map
operator|.
name|remove
argument_list|(
operator|(
name|AtomicValue
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
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|keys
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
return|return
name|map
operator|.
name|keys
argument_list|()
return|;
block|}
specifier|private
name|Sequence
name|contains
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|map
operator|.
name|contains
argument_list|(
operator|(
name|AtomicValue
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
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|get
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
name|Sequence
name|value
init|=
name|map
operator|.
name|get
argument_list|(
operator|(
name|AtomicValue
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
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
return|return
name|value
return|;
block|}
else|else
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
specifier|private
name|Sequence
name|put
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
return|return
name|map
operator|.
name|put
argument_list|(
operator|(
name|AtomicValue
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
name|args
index|[
literal|2
index|]
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|entry
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AtomicValue
name|key
init|=
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
decl_stmt|;
return|return
operator|new
name|SingleKeyMapType
argument_list|(
name|this
operator|.
name|context
argument_list|,
literal|null
argument_list|,
name|key
argument_list|,
name|args
index|[
literal|1
index|]
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|size
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
return|return
operator|new
name|IntegerValue
argument_list|(
name|map
operator|.
name|size
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
return|;
block|}
specifier|private
name|Sequence
name|merge
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|)
return|;
block|}
specifier|final
name|MapType
name|map
init|=
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
index|]
operator|.
name|unorderedIterator
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
name|AbstractMapType
name|m
init|=
operator|(
name|AbstractMapType
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
annotation|@
name|Deprecated
specifier|private
name|Sequence
name|newMap
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|)
return|;
block|}
name|String
name|collation
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|length
operator|==
literal|2
condition|)
block|{
name|collation
operator|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|MapType
name|map
init|=
operator|new
name|MapType
argument_list|(
name|this
operator|.
name|context
argument_list|,
name|collation
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|args
index|[
literal|0
index|]
operator|.
name|unorderedIterator
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
name|AbstractMapType
name|m
init|=
operator|(
name|AbstractMapType
operator|)
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|map
operator|.
name|add
argument_list|(
name|m
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|private
name|Sequence
name|forEach
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AbstractMapType
name|map
init|=
operator|(
name|AbstractMapType
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
try|try
init|(
specifier|final
name|FunctionReference
name|ref
init|=
operator|(
name|FunctionReference
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
init|)
block|{
name|ref
operator|.
name|analyze
argument_list|(
name|cachedContextInfo
argument_list|)
expr_stmt|;
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
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|AtomicValue
argument_list|,
name|Sequence
argument_list|>
name|entry
range|:
name|map
control|)
block|{
specifier|final
name|Sequence
name|s
init|=
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
name|entry
operator|.
name|getKey
argument_list|()
block|,
name|entry
operator|.
name|getValue
argument_list|()
block|}
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|s
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
block|}
end_class

end_unit

