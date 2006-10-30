begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Created on Sep 8, 2003  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_class
specifier|public
class|class
name|Range
block|{
specifier|private
name|long
name|start_
decl_stmt|;
specifier|private
name|long
name|end_
decl_stmt|;
specifier|public
name|Range
parameter_list|(
name|long
name|start
parameter_list|,
name|long
name|end
parameter_list|)
block|{
name|start_
operator|=
name|start
expr_stmt|;
name|end_
operator|=
name|end
expr_stmt|;
block|}
specifier|public
name|long
name|getStart
parameter_list|()
block|{
return|return
name|start_
return|;
block|}
specifier|public
name|long
name|getEnd
parameter_list|()
block|{
return|return
name|end_
return|;
block|}
specifier|public
name|boolean
name|inRange
parameter_list|(
name|long
name|value
parameter_list|)
block|{
return|return
name|value
operator|>=
name|start_
operator|&&
name|value
operator|<=
name|end_
return|;
block|}
specifier|public
name|int
name|getDistance
parameter_list|()
block|{
return|return
operator|(
name|int
operator|)
operator|(
name|end_
operator|-
name|start_
operator|)
operator|+
literal|1
return|;
block|}
block|}
end_class

end_unit

