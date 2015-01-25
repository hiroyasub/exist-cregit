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
name|dom
operator|.
name|memtree
operator|.
name|DocumentBuilderReceiver
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
name|memtree
operator|.
name|DocumentImpl
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
name|memtree
operator|.
name|MemTreeBuilder
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
name|serializer
operator|.
name|XQuerySerializer
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
name|util
operator|.
name|SerializerUtils
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
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_class
specifier|public
class|class
name|FunSerialize
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
literal|"serialize"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function serializes the supplied input sequence $arg as described in XSLT and XQuery Serialization 3.0, returning the "
operator|+
literal|"serialized representation of the sequence as a string."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"args"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to serialize"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the string containing the serialized node set."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"serialize"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function serializes the supplied input sequence $arg as described in XSLT and XQuery Serialization 3.0, returning the "
operator|+
literal|"serialized representation of the sequence as a string."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"args"
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
literal|"The node set to serialize"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"parameters"
argument_list|,
name|Type
operator|.
name|NODE
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The serialization parameters"
argument_list|)
block|}
argument_list|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"result"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"the string containing the serialized node set."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|FunSerialize
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
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|==
literal|2
operator|&&
operator|!
name|args
index|[
literal|1
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SerializerUtils
operator|.
name|getSerializationOptions
argument_list|(
name|this
argument_list|,
operator|(
name|NodeValue
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
name|outputProperties
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|XQuerySerializer
name|xqSerializer
init|=
operator|new
name|XQuerySerializer
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|outputProperties
argument_list|,
name|writer
argument_list|)
decl_stmt|;
name|Sequence
name|seq
init|=
name|args
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|xqSerializer
operator|.
name|isJSON
argument_list|()
condition|)
block|{
name|seq
operator|=
name|normalize
argument_list|(
name|seq
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|xqSerializer
operator|.
name|serialize
argument_list|(
name|seq
argument_list|)
expr_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|writer
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/**      * Sequence normalization as described in      * http://www.w3.org/TR/xslt-xquery-serialization-30/#serdm      *      * @param input non-normalized sequence      * @return normalized sequence      * @throws XPathException      */
specifier|protected
name|Sequence
name|normalize
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
name|isEmpty
argument_list|()
condition|)
comment|// "If the sequence that is input to serialization is empty, create a sequence S1 that consists of a zero-length string."
block|{
return|return
name|StringValue
operator|.
name|EMPTY_STRING
return|;
block|}
specifier|final
name|ValueSequence
name|temp
init|=
operator|new
name|ValueSequence
argument_list|(
name|input
operator|.
name|getItemCount
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
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
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|ATTRIBUTE
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|NAMESPACE
operator|||
name|next
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|FUNCTION_REFERENCE
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
literal|"It is an error if an item in the sequence to serialize is an attribute node or a namespace node."
argument_list|)
throw|;
block|}
name|temp
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// atomic value
name|Item
name|last
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|temp
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|last
operator|=
name|temp
operator|.
name|itemAt
argument_list|(
name|temp
operator|.
name|getItemCount
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|last
operator|!=
literal|null
operator|&&
name|last
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|STRING
condition|)
comment|// "For each subsequence of adjacent strings in S2, copy a single string to the new sequence
comment|// equal to the values of the strings in the subsequence concatenated in order, each separated
comment|// by a single space."
block|{
operator|(
operator|(
name|StringValue
operator|)
name|last
operator|)
operator|.
name|append
argument_list|(
literal|" "
operator|+
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
comment|// "For each item in S1, if the item is atomic, obtain the lexical representation of the item by
comment|// casting it to an xs:string and copy the string representation to the new sequence;"
block|{
name|temp
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
specifier|final
name|MemTreeBuilder
name|builder
init|=
name|context
operator|.
name|getDocumentBuilder
argument_list|()
decl_stmt|;
specifier|final
name|DocumentBuilderReceiver
name|receiver
init|=
operator|new
name|DocumentBuilderReceiver
argument_list|(
name|builder
argument_list|,
literal|true
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|temp
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
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|next
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|next
operator|.
name|copyTo
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|receiver
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|receiver
operator|.
name|characters
argument_list|(
name|next
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|(
name|DocumentImpl
operator|)
name|receiver
operator|.
name|getDocument
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SAXException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|FnModule
operator|.
name|SENR0001
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

