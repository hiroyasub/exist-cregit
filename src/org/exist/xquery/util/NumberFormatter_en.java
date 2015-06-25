begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * English formatter for numbers and dates.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|NumberFormatter_en
extends|extends
name|NumberFormatter
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|DAYS
init|=
block|{
literal|"Sunday"
block|,
literal|"Monday"
block|,
literal|"Tuesday"
block|,
literal|"Wednesday"
block|,
literal|"Thursday"
block|,
literal|"Friday"
block|,
literal|"Saturday"
block|}
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|MONTHS
init|=
block|{
literal|"January"
block|,
literal|"February"
block|,
literal|"March"
block|,
literal|"April"
block|,
literal|"May"
block|,
literal|"June"
block|,
literal|"July"
block|,
literal|"August"
block|,
literal|"September"
block|,
literal|"October"
block|,
literal|"November"
block|,
literal|"December"
block|}
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getMonth
parameter_list|(
name|int
name|month
parameter_list|)
block|{
return|return
name|MONTHS
index|[
name|month
operator|-
literal|1
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDay
parameter_list|(
name|int
name|day
parameter_list|)
block|{
return|return
name|DAYS
index|[
name|day
operator|-
literal|1
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getAmPm
parameter_list|(
name|int
name|hour
parameter_list|)
block|{
if|if
condition|(
name|hour
operator|>
literal|12
condition|)
block|{
return|return
literal|"pm"
return|;
block|}
else|else
block|{
return|return
literal|"am"
return|;
block|}
block|}
specifier|public
name|String
name|getOrdinalSuffix
parameter_list|(
name|long
name|number
parameter_list|)
block|{
if|if
condition|(
name|number
operator|>
literal|10
operator|&&
name|number
operator|<
literal|20
condition|)
block|{
return|return
literal|"th"
return|;
block|}
specifier|final
name|long
name|mod
init|=
name|number
operator|%
literal|10
decl_stmt|;
if|if
condition|(
name|mod
operator|==
literal|1
condition|)
block|{
return|return
literal|"st"
return|;
block|}
if|else if
condition|(
name|mod
operator|==
literal|2
condition|)
block|{
return|return
literal|"nd"
return|;
block|}
if|else if
condition|(
name|mod
operator|==
literal|3
condition|)
block|{
return|return
literal|"rd"
return|;
block|}
else|else
block|{
return|return
literal|"th"
return|;
block|}
block|}
block|}
end_class

end_unit

