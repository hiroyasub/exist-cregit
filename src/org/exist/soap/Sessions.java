begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|soap
package|;
end_package

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
name|it
operator|.
name|unimi
operator|.
name|dsi
operator|.
name|fastUtil
operator|.
name|Int2ObjectOpenHashMap
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|Value
import|;
end_import

begin_class
specifier|public
class|class
name|Sessions
block|{
specifier|private
specifier|static
name|long
name|TIMEOUT
init|=
literal|600000
decl_stmt|;
specifier|private
specifier|static
name|Sessions
name|instance
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|Sessions
name|getInstance
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
name|instance
operator|=
operator|new
name|Sessions
argument_list|()
expr_stmt|;
return|return
name|instance
return|;
block|}
specifier|public
specifier|static
name|long
name|getTimeout
parameter_list|()
block|{
return|return
name|TIMEOUT
return|;
block|}
specifier|public
specifier|static
name|void
name|setTimeout
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|TIMEOUT
operator|=
name|timeout
expr_stmt|;
block|}
name|Int2ObjectOpenHashMap
name|resultSets
init|=
operator|new
name|Int2ObjectOpenHashMap
argument_list|(
literal|25
argument_list|)
decl_stmt|;
specifier|public
name|int
name|addQueryResult
parameter_list|(
name|Value
name|val
parameter_list|)
block|{
name|QueryResult
name|result
init|=
operator|new
name|QueryResult
argument_list|(
name|val
argument_list|)
decl_stmt|;
name|resultSets
operator|.
name|put
argument_list|(
name|result
operator|.
name|hashCode
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|public
name|Value
name|getQueryResult
parameter_list|(
name|int
name|sessionId
parameter_list|)
block|{
name|QueryResult
name|qr
init|=
operator|(
name|QueryResult
operator|)
name|resultSets
operator|.
name|get
argument_list|(
name|sessionId
argument_list|)
decl_stmt|;
return|return
name|qr
operator|!=
literal|null
condition|?
name|qr
operator|.
name|result
else|:
literal|null
return|;
block|}
specifier|private
name|void
name|checkResultSets
parameter_list|()
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|resultSets
operator|.
name|values
argument_list|()
operator|.
name|iterator
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
name|QueryResult
name|qr
init|=
operator|(
name|QueryResult
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|long
name|ts
init|=
operator|(
operator|(
name|QueryResult
operator|)
name|qr
operator|)
operator|.
name|timestamp
decl_stmt|;
if|if
condition|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|ts
operator|>
name|TIMEOUT
condition|)
block|{
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
class|class
name|QueryResult
block|{
name|Value
name|result
decl_stmt|;
name|long
name|timestamp
init|=
literal|0
decl_stmt|;
specifier|public
name|QueryResult
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
name|this
operator|.
name|result
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|timestamp
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

