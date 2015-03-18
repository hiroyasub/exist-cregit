begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * Used by {@link XmldbRequestProcessorFactory} to cache query results. Each query result  * is identified by a unique integer id.  */
end_comment

begin_class
specifier|public
class|class
name|QueryResultCache
block|{
specifier|public
specifier|final
specifier|static
name|int
name|TIMEOUT
init|=
literal|180000
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|INITIAL_SIZE
init|=
literal|254
decl_stmt|;
specifier|private
name|AbstractCachedResult
index|[]
name|results
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|QueryResultCache
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|QueryResultCache
parameter_list|()
block|{
name|results
operator|=
operator|new
name|AbstractCachedResult
index|[
name|INITIAL_SIZE
index|]
expr_stmt|;
block|}
specifier|public
name|int
name|add
parameter_list|(
specifier|final
name|AbstractCachedResult
name|qr
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|results
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|results
index|[
name|i
index|]
operator|=
name|qr
expr_stmt|;
return|return
name|i
return|;
block|}
block|}
comment|// no empty bucket. need to resize.
specifier|final
name|AbstractCachedResult
index|[]
name|temp
init|=
operator|new
name|AbstractCachedResult
index|[
operator|(
name|results
operator|.
name|length
operator|*
literal|3
operator|)
operator|/
literal|2
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|results
argument_list|,
literal|0
argument_list|,
name|temp
argument_list|,
literal|0
argument_list|,
name|results
operator|.
name|length
argument_list|)
expr_stmt|;
specifier|final
name|int
name|pos
init|=
name|results
operator|.
name|length
decl_stmt|;
name|temp
index|[
name|pos
index|]
operator|=
name|qr
expr_stmt|;
name|results
operator|=
name|temp
expr_stmt|;
return|return
name|pos
return|;
block|}
specifier|public
name|AbstractCachedResult
name|get
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|<
literal|0
operator|||
name|pos
operator|>=
name|results
operator|.
name|length
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|results
index|[
name|pos
index|]
return|;
block|}
specifier|public
name|QueryResult
name|getResult
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|AbstractCachedResult
name|acr
init|=
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
return|return
operator|(
name|acr
operator|!=
literal|null
operator|&&
name|acr
operator|instanceof
name|QueryResult
operator|)
condition|?
operator|(
name|QueryResult
operator|)
name|acr
else|:
literal|null
return|;
block|}
specifier|public
name|SerializedResult
name|getSerializedResult
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
specifier|final
name|AbstractCachedResult
name|acr
init|=
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
return|return
operator|(
name|acr
operator|!=
literal|null
operator|&&
name|acr
operator|instanceof
name|SerializedResult
operator|)
condition|?
operator|(
name|SerializedResult
operator|)
name|acr
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|>
operator|-
literal|1
operator|&&
name|pos
operator|<
name|results
operator|.
name|length
condition|)
block|{
comment|// Perhaps we should not free resources here
comment|// but an explicit remove implies you want
comment|// to free resources
if|if
condition|(
name|results
index|[
name|pos
index|]
operator|!=
literal|null
condition|)
block|{
comment|// Prevent NPE
name|results
index|[
name|pos
index|]
operator|.
name|free
argument_list|()
expr_stmt|;
name|results
index|[
name|pos
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|,
specifier|final
name|int
name|hash
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|>
operator|-
literal|1
operator|&&
name|pos
operator|<
name|results
operator|.
name|length
operator|&&
operator|(
name|results
index|[
name|pos
index|]
operator|!=
literal|null
operator|&&
name|results
index|[
name|pos
index|]
operator|.
name|hashCode
argument_list|()
operator|==
name|hash
operator|)
condition|)
block|{
comment|// Perhaps we should not free resources here
comment|// but an explicit remove implies you want
comment|// to free resources
name|results
index|[
name|pos
index|]
operator|.
name|free
argument_list|()
expr_stmt|;
name|results
index|[
name|pos
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|checkTimestamps
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
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
name|results
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|AbstractCachedResult
name|result
init|=
name|results
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|now
operator|-
name|result
operator|.
name|getTimestamp
argument_list|()
operator|>
name|TIMEOUT
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Removing result set "
operator|+
operator|new
name|Date
argument_list|(
name|result
operator|.
name|getTimestamp
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Here we should not free resources, because they could be still in use
comment|// by other threads, so leave the work to the garbage collector
name|results
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
end_class

end_unit

