begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|fluent
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
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
name|util
operator|.
name|Base64Encoder
import|;
end_import

begin_comment
comment|/**  * A bunch of static data conversion utility methods.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|DataUtils
block|{
specifier|private
name|DataUtils
parameter_list|()
block|{
block|}
comment|/** 	 * A comparator for dateTimes (XMLGregorianCalendar objects), that uses the partial order defined on dateTimes and throws an exception if the order is indeterminate. 	 */
specifier|public
specifier|static
specifier|final
name|Comparator
argument_list|<
name|XMLGregorianCalendar
argument_list|>
name|DATE_TIME_COMPARATOR
init|=
operator|new
name|Comparator
argument_list|<
name|XMLGregorianCalendar
argument_list|>
argument_list|()
block|{
specifier|public
name|int
name|compare
parameter_list|(
name|XMLGregorianCalendar
name|a
parameter_list|,
name|XMLGregorianCalendar
name|b
parameter_list|)
block|{
name|int
name|r
init|=
name|a
operator|.
name|compare
argument_list|(
name|b
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|==
name|DatatypeConstants
operator|.
name|INDETERMINATE
condition|)
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"date-times not comparable:  "
operator|+
name|a
operator|+
literal|" and "
operator|+
name|b
argument_list|)
throw|;
return|return
name|r
return|;
block|}
block|}
decl_stmt|;
specifier|private
specifier|static
name|DatatypeFactory
name|datatypeFactory
decl_stmt|;
static|static
block|{
try|try
block|{
name|datatypeFactory
operator|=
name|DatatypeFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatatypeConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"unable to configure datatype factory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Return a shared instance of a datatype factory, used for creating new XML data objects. 	 * 	 * @return a shared datatype factory 	 */
specifier|public
specifier|static
name|DatatypeFactory
name|datatypeFactory
parameter_list|()
block|{
return|return
name|datatypeFactory
return|;
block|}
comment|/** 	 * Convert an XML date/time to its<code>java.util.Date</code> equivalent. 	 *  	 * @param dateTime the XML date/time to convert 	 * @return a Java date 	 */
specifier|public
specifier|static
name|Date
name|toDate
parameter_list|(
name|XMLGregorianCalendar
name|dateTime
parameter_list|)
block|{
return|return
name|dateTime
operator|.
name|toGregorianCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/** 	 * Convert a Java date to its XML date/time equivalent. 	 * 	 * @param date the Java date to convert 	 * @return an XML date/time 	 */
specifier|public
specifier|static
name|XMLGregorianCalendar
name|toDateTime
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|GregorianCalendar
name|cal
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
return|return
name|datatypeFactory
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|cal
argument_list|)
operator|.
name|normalize
argument_list|()
return|;
block|}
comment|/** 	 * Convert milliseconds offset to its XML date/time equivalent. 	 * 	 * @param millis a millisecond count since the epoch 	 * @return an XML date/time 	 */
specifier|public
specifier|static
name|XMLGregorianCalendar
name|toDateTime
parameter_list|(
name|long
name|millis
parameter_list|)
block|{
name|GregorianCalendar
name|cal
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|cal
operator|.
name|setTimeInMillis
argument_list|(
name|millis
argument_list|)
expr_stmt|;
return|return
name|datatypeFactory
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|cal
argument_list|)
operator|.
name|normalize
argument_list|()
return|;
block|}
comment|/** 	 * Convert a Java object to its equivalent XML datatype string representation. 	 * At the moment, there is special treatment for<code>java.util.Date</code>, 	 *<code>java.util.Calendar</code> and<code>byte[]</code> (Base64 encoding); 	 * for all other objects, we simply invoke<code>toString()</code>. 	 *  	 * @param o the object to convert 	 * @return a string representation of the object, according to XML Schema Datatype rules if possible 	 */
specifier|public
specifier|static
name|String
name|toXMLString
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|o
operator|instanceof
name|Date
condition|)
block|{
return|return
name|toDateTime
argument_list|(
operator|(
name|Date
operator|)
name|o
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|Calendar
condition|)
block|{
return|return
name|toDateTime
argument_list|(
operator|(
operator|(
name|Calendar
operator|)
name|o
operator|)
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|else if
condition|(
name|o
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|Base64Encoder
name|encoder
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|encoder
operator|.
name|translate
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|o
argument_list|)
expr_stmt|;
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|encoder
operator|.
name|getCharArray
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|o
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

