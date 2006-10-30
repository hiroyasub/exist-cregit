begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigDecimal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TimeZone
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
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|DatatypeConfigurationException
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
name|DatatypeFactory
import|;
end_import

begin_comment
comment|/**  * Centralizes access to time-related utility functions.  Mostly delegates to the  * XML datatype factory, serving as a central chokepoint to control concurrency  * issues.  It's not clear if instances of the factory are in fact thread-safe or not;  * if they turn out not to be, it will be easy to either synchronize access or create  * more instances here as required.  *  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|TimeUtils
block|{
specifier|private
specifier|static
specifier|final
name|TimeUtils
name|INSTANCE
init|=
operator|new
name|TimeUtils
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|TimeUtils
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|// assume it's thread-safe, if not synchronize all access
specifier|private
specifier|final
name|DatatypeFactory
name|factory
decl_stmt|;
specifier|private
name|int
name|timezoneOffset
decl_stmt|;
specifier|private
name|boolean
name|timezoneOverriden
decl_stmt|;
specifier|private
name|TimeUtils
parameter_list|()
block|{
comment|// singleton, keep constructor private
try|try
block|{
name|factory
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
literal|"unable to instantiate an XML datatype factory"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Set the offset of the local timezone, ignoring the default provided by the OS. 	 * Mainly useful for testing. 	 * 	 * @param millis the timezone offset in milliseconds, positive or negative 	 */
specifier|public
name|void
name|overrideLocalTimezoneOffset
parameter_list|(
name|int
name|millis
parameter_list|)
block|{
name|timezoneOffset
operator|=
name|millis
expr_stmt|;
name|timezoneOverriden
operator|=
literal|true
expr_stmt|;
block|}
comment|/** 	 * Cancel any timezone override that may be in effect, reverting back to the OS value. 	 */
specifier|public
name|void
name|resetLocalTimezoneOffset
parameter_list|()
block|{
name|timezoneOverriden
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|int
name|getLocalTimezoneOffsetMillis
parameter_list|()
block|{
return|return
name|timezoneOverriden
condition|?
name|timezoneOffset
else|:
name|TimeZone
operator|.
name|getDefault
argument_list|()
operator|.
name|getRawOffset
argument_list|()
return|;
block|}
specifier|public
name|int
name|getLocalTimezoneOffsetMinutes
parameter_list|()
block|{
return|return
name|getLocalTimezoneOffsetMillis
argument_list|()
operator|/
literal|60000
return|;
block|}
specifier|public
name|Duration
name|newDuration
parameter_list|(
name|long
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDuration
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDuration
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDuration
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDuration
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|,
name|int
name|arg5
parameter_list|,
name|int
name|arg6
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDuration
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|,
name|arg5
argument_list|,
name|arg6
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDuration
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|BigInteger
name|arg1
parameter_list|,
name|BigInteger
name|arg2
parameter_list|,
name|BigInteger
name|arg3
parameter_list|,
name|BigInteger
name|arg4
parameter_list|,
name|BigInteger
name|arg5
parameter_list|,
name|BigDecimal
name|arg6
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDuration
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|,
name|arg5
argument_list|,
name|arg6
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationDayTime
parameter_list|(
name|long
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationDayTime
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationDayTime
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationDayTime
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationDayTime
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationDayTime
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationDayTime
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|BigInteger
name|arg1
parameter_list|,
name|BigInteger
name|arg2
parameter_list|,
name|BigInteger
name|arg3
parameter_list|,
name|BigInteger
name|arg4
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationDayTime
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationYearMonth
parameter_list|(
name|long
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationYearMonth
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationYearMonth
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationYearMonth
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationYearMonth
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationYearMonth
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
return|;
block|}
specifier|public
name|Duration
name|newDurationYearMonth
parameter_list|(
name|boolean
name|arg0
parameter_list|,
name|BigInteger
name|arg1
parameter_list|,
name|BigInteger
name|arg2
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newDurationYearMonth
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendar
parameter_list|()
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendar
argument_list|()
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendar
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|,
name|int
name|arg5
parameter_list|,
name|int
name|arg6
parameter_list|,
name|int
name|arg7
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|,
name|arg5
argument_list|,
name|arg6
argument_list|,
name|arg7
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendar
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendar
parameter_list|(
name|BigInteger
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|,
name|int
name|arg5
parameter_list|,
name|BigDecimal
name|arg6
parameter_list|,
name|int
name|arg7
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|,
name|arg5
argument_list|,
name|arg6
argument_list|,
name|arg7
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendar
parameter_list|(
name|GregorianCalendar
name|arg0
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|arg0
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendarDate
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendarDate
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendarTime
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendarTime
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendarTime
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|int
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendarTime
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
return|;
block|}
specifier|public
name|XMLGregorianCalendar
name|newXMLGregorianCalendarTime
parameter_list|(
name|int
name|arg0
parameter_list|,
name|int
name|arg1
parameter_list|,
name|int
name|arg2
parameter_list|,
name|BigDecimal
name|arg3
parameter_list|,
name|int
name|arg4
parameter_list|)
block|{
return|return
name|factory
operator|.
name|newXMLGregorianCalendarTime
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|,
name|arg2
argument_list|,
name|arg3
argument_list|,
name|arg4
argument_list|)
return|;
block|}
block|}
end_class

end_unit

