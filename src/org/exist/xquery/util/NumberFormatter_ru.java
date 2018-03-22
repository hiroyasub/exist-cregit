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
comment|/**  * Russian formatter for numbers and dates.  *  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  * @author Wolfgang  */
end_comment

begin_class
specifier|public
class|class
name|NumberFormatter_ru
extends|extends
name|NumberFormatter
block|{
specifier|public
name|NumberFormatter_ru
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
comment|/*          * TODO          * "ÐÐ´Ð¸Ð½","ÐÐ²Ð°","Ð¢ÑÐ¸","Ð§ÐµÑÑÑÐµ","ÐÑÑÑ","Ð¨ÐµÑÑÑ","Ð¡ÐµÐ¼Ñ","ÐÐ¾ÑÐµÐ¼Ñ","ÐÐµÐ²ÑÑÑ","ÐÐ´Ð½Ð°","ÐÐ²Ðµ",          * "ÐÐµÑÑÑÑ","ÐÐ´Ð¸Ð½Ð½Ð°Ð´ÑÐ°ÑÑ","ÐÐ²ÐµÐ½Ð°Ð´ÑÐ°ÑÑ","Ð¢ÑÐ¸Ð½Ð°Ð´ÑÐ°ÑÑ","Ð§ÐµÑÑÑÐ½Ð°Ð´ÑÐ°ÑÑ","ÐÑÑÐ½Ð°Ð´ÑÐ°ÑÑ","Ð¨ÐµÑÑÐ½Ð°Ð´ÑÐ°ÑÑ","Ð¡ÐµÐ¼Ð½Ð°Ð´ÑÐ°ÑÑ","ÐÐ¾ÑÐµÐ¼Ð½Ð°Ð´ÑÐ°ÑÑ","ÐÐµÐ²ÑÑÐ½Ð°Ð´ÑÐ°ÑÑ",          * "ÐÐ²Ð°Ð´ÑÐ°ÑÑ","Ð¢ÑÐ¸Ð´ÑÐ°ÑÑ","Ð¡Ð¾ÑÐ¾Ðº","ÐÑÑÑÐ´ÐµÑÑÑ","Ð¨ÐµÑÑÑÐ´ÐµÑÑÑ","Ð¡ÐµÐ¼ÑÐ´ÐµÑÑÑ","ÐÐ¾ÑÐµÐ¼ÑÐ´ÐµÑÑÑ","ÐÐµÐ²ÑÐ½Ð¾ÑÑÐ¾",          * "Ð¡ÑÐ¾","ÐÐ²ÐµÑÑÐ¸","Ð¢ÑÐ¸ÑÑÐ°","Ð§ÐµÑÑÑÐµÑÑÐ°","ÐÑÑÑÑÐ¾Ñ","Ð¨ÐµÑÑÑÑÐ¾Ñ","Ð¡ÐµÐ¼ÑÑÐ¾Ñ","ÐÐ¾ÑÐµÐ¼ÑÑÐ¾Ñ","ÐÐµÐ²ÑÑÑÑÐ¾Ñ",          * "Ð¢ÑÑÑÑÐ°","Ð¢ÑÑÑÑÐ¸","Ð¢ÑÑÑÑ",          * "ÐÐ¸Ð»Ð»Ð¸Ð¾Ð½","ÐÐ¸Ð»Ð»Ð¸Ð¾Ð½Ð°","ÐÐ¸Ð»Ð»Ð¸Ð¾Ð½Ð¾Ð²",          * "ÐÐ¸Ð»Ð»Ð¸Ð°ÑÐ´","ÐÐ¸Ð»Ð»Ð¸Ð°ÑÐ´Ð°","ÐÐ¸Ð»Ð»Ð¸Ð°ÑÐ´Ð¾Ð²",          * "Ð¢ÑÐ¸Ð»Ð»Ð¸Ð¾Ð½","Ð¢ÑÐ¸Ð»Ð»Ð¸Ð¾Ð½Ð°","Ð¢ÑÐ¸Ð»Ð»Ð¸Ð¾Ð½Ð¾Ð²",          * "ÐÐ¾Ð»Ñ"          */
specifier|public
name|String
name|getOrdinalSuffix
parameter_list|(
name|long
name|number
parameter_list|)
block|{
return|return
literal|""
return|;
block|}
block|}
end_class

end_unit

