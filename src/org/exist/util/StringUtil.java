begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * StringUtil.java  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|StringUtil
block|{
specifier|public
specifier|final
specifier|static
name|void
name|utfwrite
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|len
parameter_list|,
name|FastStringBuffer
name|s
parameter_list|)
block|{
specifier|final
name|int
name|slen
init|=
name|s
operator|.
name|length
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
name|slen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
if|else if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
block|}
else|else
block|{
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xE0
operator||
operator|(
name|code
operator|>>>
literal|12
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|final
specifier|static
name|void
name|utfwrite
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|len
parameter_list|,
name|String
name|s
parameter_list|)
block|{
specifier|final
name|int
name|slen
init|=
name|s
operator|.
name|length
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
name|slen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
name|code
expr_stmt|;
if|else if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xC0
operator||
operator|(
name|code
operator|>>
literal|6
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
block|}
else|else
block|{
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0xE0
operator||
operator|(
name|code
operator|>>>
literal|12
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
operator|(
name|code
operator|>>
literal|6
operator|)
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
name|data
index|[
name|len
operator|++
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
literal|0x80
operator||
operator|(
name|code
operator|&
literal|0x3F
operator|)
operator|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
specifier|final
specifier|static
name|int
name|utflen
parameter_list|(
name|FastStringBuffer
name|s
parameter_list|)
block|{
specifier|final
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|0
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
name|slen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
operator|++
name|len
expr_stmt|;
if|else if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|len
operator|+=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|len
operator|+=
literal|3
expr_stmt|;
block|}
block|}
return|return
name|len
return|;
block|}
specifier|public
specifier|final
specifier|static
name|int
name|utflen
parameter_list|(
name|String
name|s
parameter_list|)
block|{
specifier|final
name|int
name|slen
init|=
name|s
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|len
init|=
literal|0
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
name|slen
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|code
init|=
operator|(
name|int
operator|)
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|code
operator|>=
literal|0x01
operator|&&
name|code
operator|<=
literal|0x7F
condition|)
operator|++
name|len
expr_stmt|;
if|else if
condition|(
operator|(
operator|(
name|code
operator|>=
literal|0x80
operator|)
operator|&&
operator|(
name|code
operator|<=
literal|0x7FF
operator|)
operator|)
operator|||
name|code
operator|==
literal|0
condition|)
block|{
name|len
operator|+=
literal|2
expr_stmt|;
block|}
else|else
block|{
name|len
operator|+=
literal|3
expr_stmt|;
block|}
block|}
return|return
name|len
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|hexDump
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
return|return
name|hexDump
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|data
operator|.
name|length
argument_list|)
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|hexDump
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|len
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|start
operator|+
name|len
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
operator|(
name|int
operator|)
name|data
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

