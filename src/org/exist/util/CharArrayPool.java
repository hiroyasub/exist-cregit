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
comment|/**  * A pool for char arrays.  *   * This pool is used by class XMLString. Whenever an XMLString needs to  * reallocate the backing char[], the old array is released into the pool. However,  * only char[] with length&lt; MAX are kept in the pool. Larger char[] are rarely reused.  *   * The pool is bound to the current thread.  */
end_comment

begin_class
specifier|public
class|class
name|CharArrayPool
block|{
specifier|public
specifier|static
specifier|final
name|int
name|POOL_SIZE
init|=
literal|128
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|MAX
init|=
literal|128
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|ThreadLocal
name|pools_
init|=
operator|new
name|PoolThreadLocal
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|int
name|slot_
init|=
literal|0
decl_stmt|;
specifier|public
name|CharArrayPool
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|char
index|[]
name|getCharArray
parameter_list|(
name|int
name|size
parameter_list|)
block|{
if|if
condition|(
name|MAX
operator|>
name|size
condition|)
block|{
specifier|final
name|char
index|[]
index|[]
name|pool
init|=
operator|(
name|char
index|[]
index|[]
operator|)
name|pools_
operator|.
name|get
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
name|pool
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pool
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|pool
index|[
name|i
index|]
operator|.
name|length
operator|==
name|size
condition|)
block|{
name|char
index|[]
name|b
init|=
name|pool
index|[
name|i
index|]
decl_stmt|;
name|pool
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
block|}
comment|//System.out.println("creating new char[" + size + "]");
return|return
operator|new
name|char
index|[
name|size
index|]
return|;
block|}
specifier|public
specifier|static
name|void
name|releaseCharArray
parameter_list|(
specifier|final
name|char
index|[]
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
operator|||
name|b
operator|.
name|length
operator|>
name|MAX
condition|)
return|return;
specifier|final
name|char
index|[]
index|[]
name|pool
init|=
operator|(
name|char
index|[]
index|[]
operator|)
name|pools_
operator|.
name|get
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
name|pool
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|pool
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|pool
index|[
name|i
index|]
operator|=
name|b
expr_stmt|;
return|return;
block|}
block|}
name|int
name|s
init|=
name|slot_
operator|++
decl_stmt|;
if|if
condition|(
name|s
operator|<
literal|0
condition|)
name|s
operator|=
operator|-
name|s
expr_stmt|;
name|pool
index|[
name|s
operator|%
name|pool
operator|.
name|length
index|]
operator|=
name|b
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|PoolThreadLocal
extends|extends
name|ThreadLocal
block|{
specifier|protected
name|Object
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|char
index|[
name|POOL_SIZE
index|]
index|[]
return|;
block|}
block|}
block|}
end_class

end_unit

