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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_comment
comment|/**  * German language formatting of numbers and dates.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|NumberFormatter_de
extends|extends
name|NumberFormatter
block|{
specifier|public
name|NumberFormatter_de
parameter_list|(
name|Locale
name|locale
parameter_list|)
block|{
name|super
argument_list|(
name|locale
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getOrdinalSuffix
parameter_list|(
name|long
name|number
parameter_list|)
block|{
return|return
literal|"."
return|;
block|}
block|}
end_class

end_unit

