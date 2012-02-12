begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ByteConversion
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  *  Static methods to deal with the signature of a node stored  *  in the first byte of the node data in the persistent DOM.  *    *  The bits in the signature are used as follows:  *    *<pre>  *  8 4 2 1 8 4 2 1  *  T T T N 0 0 I I  *</pre>  *    *   where T = node type, N = has-namespace flag, I = number of bytes used   *   to store the name of the node (local name for elements and attributes).  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|Signatures
block|{
specifier|public
specifier|final
specifier|static
name|int
name|Char
init|=
literal|0x0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|Elem
init|=
literal|0x1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|Proc
init|=
literal|0x2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|Comm
init|=
literal|0x3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|Attr
init|=
literal|0x4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|Cdata
init|=
literal|0x5
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|intContent
init|=
literal|0x1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|byteContent
init|=
literal|0x3
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|noContent
init|=
literal|0x0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|shortContent
init|=
literal|0x2
decl_stmt|;
comment|/**      *  Returns the storage size of the given type as      *  number of bytes required.      */
specifier|public
specifier|final
specifier|static
name|int
name|getLength
parameter_list|(
name|int
name|type
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|intContent
case|:
return|return
literal|4
return|;
case|case
name|shortContent
case|:
return|return
literal|2
return|;
case|case
name|byteContent
case|:
return|return
literal|1
return|;
block|}
comment|//TODO : throw an exception there ? -pb
return|return
literal|0
return|;
block|}
comment|/**      *  Returns one of IntContent, ShortContent, ByteContent      *  or NoContent based on the number of bytes required      *  to store the integer value given in length.      */
specifier|public
specifier|final
specifier|static
name|byte
name|getSizeType
parameter_list|(
name|int
name|length
parameter_list|)
block|{
if|if
condition|(
name|length
operator|>
name|Short
operator|.
name|MAX_VALUE
condition|)
return|return
name|intContent
return|;
if|else if
condition|(
name|length
operator|>
name|Byte
operator|.
name|MAX_VALUE
condition|)
return|return
name|shortContent
return|;
if|else if
condition|(
name|length
operator|>
literal|0
condition|)
return|return
name|byteContent
return|;
else|else
return|return
name|noContent
return|;
block|}
comment|/**      *  From the signature in byte 0 of the node data,      *  extract the node type and return a constant      *  as defined in {@link Node}.      */
specifier|public
specifier|final
specifier|static
name|short
name|getType
parameter_list|(
name|byte
name|signature
parameter_list|)
block|{
name|byte
name|type
init|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|signature
operator|&
literal|0xE0
operator|)
operator|>>
literal|0x5
operator|)
decl_stmt|;
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Char
case|:
return|return
name|Node
operator|.
name|TEXT_NODE
return|;
case|case
name|Elem
case|:
return|return
name|Node
operator|.
name|ELEMENT_NODE
return|;
case|case
name|Attr
case|:
return|return
name|Node
operator|.
name|ATTRIBUTE_NODE
return|;
case|case
name|Proc
case|:
return|return
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
return|;
case|case
name|Comm
case|:
return|return
name|Node
operator|.
name|COMMENT_NODE
return|;
case|case
name|Cdata
case|:
return|return
name|Node
operator|.
name|CDATA_SECTION_NODE
return|;
block|}
comment|//TODO : thorw exception here -pb
name|System
operator|.
name|err
operator|.
name|println
argument_list|(
literal|"Unknown node type : "
operator|+
name|type
argument_list|)
expr_stmt|;
return|return
operator|-
literal|1
return|;
block|}
specifier|public
specifier|final
specifier|static
name|int
name|read
parameter_list|(
name|int
name|type
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|intContent
case|:
return|return
operator|(
name|int
operator|)
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
return|;
case|case
name|shortContent
case|:
return|return
operator|(
name|int
operator|)
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|pos
argument_list|)
return|;
case|case
name|byteContent
case|:
return|return
operator|(
name|int
operator|)
name|data
index|[
name|pos
index|]
return|;
block|}
return|return
literal|0
return|;
block|}
specifier|public
specifier|final
specifier|static
name|void
name|write
parameter_list|(
name|int
name|type
parameter_list|,
name|int
name|size
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|pos
parameter_list|)
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|intContent
case|:
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|size
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
break|break;
case|case
name|shortContent
case|:
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
operator|(
name|short
operator|)
name|size
argument_list|,
name|data
argument_list|,
name|pos
argument_list|)
expr_stmt|;
break|break;
case|case
name|byteContent
case|:
name|data
index|[
name|pos
index|]
operator|=
operator|(
name|byte
operator|)
name|size
expr_stmt|;
break|break;
block|}
comment|//TODO : throw exception here ? -pb
block|}
block|}
end_class

end_unit

