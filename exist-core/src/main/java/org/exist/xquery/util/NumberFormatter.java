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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|LambdaMetafactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|DateFormatSymbols
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|DayOfWeek
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|Month
import|;
end_import

begin_import
import|import
name|java
operator|.
name|time
operator|.
name|format
operator|.
name|TextStyle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Locale
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_comment
comment|/**  * Formatter for numbers and dates. Concrete implementations are language-dependant.  *  * @author Wolfgang  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|NumberFormatter
block|{
specifier|private
specifier|static
name|int
index|[]
name|zeroDigits
init|=
block|{
literal|0x0030
block|,
literal|0x0660
block|,
literal|0x06f0
block|,
literal|0x0966
block|,
literal|0x09e6
block|,
literal|0x0a66
block|,
literal|0x0ae6
block|,
literal|0x0b66
block|,
literal|0x0be6
block|,
literal|0x0c66
block|,
literal|0x0ce6
block|,
literal|0x0d66
block|,
literal|0x0e50
block|,
literal|0x0ed0
block|,
literal|0x0f20
block|,
literal|0x1040
block|,
literal|0x17e0
block|,
literal|0x1810
block|,
literal|0x1946
block|,
literal|0x19d0
block|,
literal|0xff10
block|,
literal|0x104a0
block|,
literal|0x107ce
block|,
literal|0x107d8
block|,
literal|0x107e2
block|,
literal|0x107ec
block|,
literal|0x107f6
block|}
decl_stmt|;
specifier|private
specifier|static
name|char
name|OPTIONAL_DIGIT_SIGN
init|=
literal|'#'
decl_stmt|;
comment|/**      * Get the zero digit corresponding to the digit family of the given value.      * This method was taken from saxon {@link<a href="http://saxon.sourceforge.net/">http://saxon.sourceforge.net/</a>}.      *      */
specifier|public
specifier|static
name|int
name|getZeroDigit
parameter_list|(
name|int
name|val
parameter_list|)
block|{
for|for
control|(
name|int
name|z
init|=
literal|0
init|;
name|z
operator|<
name|zeroDigits
operator|.
name|length
condition|;
name|z
operator|++
control|)
block|{
if|if
condition|(
name|val
operator|<=
name|zeroDigits
index|[
name|z
index|]
operator|+
literal|9
condition|)
block|{
if|if
condition|(
name|val
operator|>=
name|zeroDigits
index|[
name|z
index|]
condition|)
block|{
return|return
name|zeroDigits
index|[
name|z
index|]
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
block|}
return|return
operator|-
literal|1
return|;
block|}
specifier|private
specifier|final
name|Locale
name|locale
decl_stmt|;
specifier|public
name|NumberFormatter
parameter_list|(
specifier|final
name|Locale
name|locale
parameter_list|)
block|{
name|this
operator|.
name|locale
operator|=
name|locale
expr_stmt|;
block|}
specifier|public
name|String
name|getMonth
parameter_list|(
name|int
name|month
parameter_list|)
block|{
return|return
name|Month
operator|.
name|of
argument_list|(
name|month
argument_list|)
operator|.
name|getDisplayName
argument_list|(
name|TextStyle
operator|.
name|FULL
argument_list|,
name|locale
argument_list|)
return|;
block|}
specifier|public
name|String
name|getDay
parameter_list|(
name|int
name|day
parameter_list|)
block|{
return|return
name|DayOfWeek
operator|.
name|of
argument_list|(
name|day
argument_list|)
operator|.
name|getDisplayName
argument_list|(
name|TextStyle
operator|.
name|FULL
argument_list|,
name|locale
argument_list|)
return|;
block|}
specifier|public
name|String
name|getAmPm
parameter_list|(
name|int
name|hour
parameter_list|)
block|{
specifier|final
name|DateFormatSymbols
name|symbols
init|=
name|DateFormatSymbols
operator|.
name|getInstance
argument_list|(
name|locale
argument_list|)
decl_stmt|;
specifier|final
name|String
index|[]
name|amPm
init|=
name|symbols
operator|.
name|getAmPmStrings
argument_list|()
decl_stmt|;
if|if
condition|(
name|hour
operator|>
literal|12
condition|)
block|{
return|return
name|amPm
index|[
literal|1
index|]
return|;
block|}
return|return
name|amPm
index|[
literal|0
index|]
return|;
block|}
specifier|public
specifier|abstract
name|String
name|getOrdinalSuffix
parameter_list|(
name|long
name|number
parameter_list|)
function_decl|;
specifier|public
name|String
name|formatNumber
parameter_list|(
name|long
name|number
parameter_list|,
name|String
name|picture
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|int
name|min
init|=
name|getMinDigits
argument_list|(
name|picture
argument_list|)
decl_stmt|;
specifier|final
name|int
name|max
init|=
name|getMaxDigits
argument_list|(
name|picture
argument_list|)
decl_stmt|;
return|return
name|formatNumber
argument_list|(
name|number
argument_list|,
name|picture
argument_list|,
name|min
argument_list|,
name|max
argument_list|)
return|;
block|}
specifier|public
name|String
name|formatNumber
parameter_list|(
name|long
name|number
parameter_list|,
name|String
name|picture
parameter_list|,
name|int
name|min
parameter_list|,
name|int
name|max
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|picture
operator|==
literal|null
condition|)
block|{
return|return
literal|""
operator|+
name|number
return|;
block|}
name|boolean
name|ordinal
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|picture
operator|.
name|endsWith
argument_list|(
literal|"o"
argument_list|)
condition|)
block|{
name|ordinal
operator|=
literal|true
expr_stmt|;
name|picture
operator|=
name|picture
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|picture
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|picture
operator|.
name|endsWith
argument_list|(
literal|"c"
argument_list|)
condition|)
block|{
name|picture
operator|=
name|picture
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|picture
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|int
name|digitSign
init|=
name|getFirstDigit
argument_list|(
name|picture
argument_list|)
decl_stmt|;
specifier|final
name|int
name|zero
init|=
name|getZeroDigit
argument_list|(
name|digitSign
argument_list|)
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
name|long
name|n
init|=
name|number
decl_stmt|;
while|while
condition|(
name|n
operator|>
literal|0
condition|)
block|{
specifier|final
name|int
name|digit
init|=
name|zero
operator|+
operator|(
name|int
operator|)
name|n
operator|%
literal|10
decl_stmt|;
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
operator|(
name|char
operator|)
name|digit
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|max
condition|)
block|{
break|break;
block|}
name|n
operator|=
name|n
operator|/
literal|10
expr_stmt|;
block|}
if|if
condition|(
name|sb
operator|.
name|length
argument_list|()
operator|<
name|min
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|sb
operator|.
name|length
argument_list|()
init|;
name|i
operator|<
name|min
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|insert
argument_list|(
literal|0
argument_list|,
operator|(
name|char
operator|)
name|zero
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|ordinal
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|getOrdinalSuffix
argument_list|(
name|number
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|int
name|getFirstDigit
parameter_list|(
name|String
name|picture
parameter_list|)
throws|throws
name|XPathException
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
name|picture
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|picture
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|ch
operator|!=
name|OPTIONAL_DIGIT_SIGN
condition|)
block|{
return|return
name|ch
return|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"There should be at least one digit sign in the picture string: "
operator|+
name|picture
argument_list|)
throw|;
block|}
specifier|public
specifier|static
name|int
name|getMinDigits
parameter_list|(
name|String
name|picture
parameter_list|)
block|{
name|int
name|count
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
name|picture
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|picture
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|==
literal|'o'
operator|||
name|ch
operator|==
literal|'c'
operator|)
operator|&&
name|i
operator|==
name|picture
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
break|break;
block|}
if|if
condition|(
name|ch
operator|!=
name|OPTIONAL_DIGIT_SIGN
condition|)
block|{
name|count
operator|++
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
specifier|public
specifier|static
name|int
name|getMaxDigits
parameter_list|(
name|String
name|picture
parameter_list|)
block|{
name|int
name|count
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
name|picture
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|ch
init|=
name|picture
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
operator|(
name|ch
operator|==
literal|'o'
operator|||
name|ch
operator|==
literal|'c'
operator|)
operator|&&
name|i
operator|==
name|picture
operator|.
name|length
argument_list|()
operator|-
literal|1
condition|)
block|{
break|break;
block|}
name|count
operator|++
expr_stmt|;
block|}
return|return
name|count
return|;
block|}
specifier|public
specifier|static
name|NumberFormatter
name|getInstance
parameter_list|(
specifier|final
name|String
name|language
parameter_list|)
block|{
specifier|final
name|String
name|className
init|=
name|NumberFormatter
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|"_"
operator|+
name|language
decl_stmt|;
specifier|final
name|Locale
name|locale
init|=
operator|new
name|Locale
argument_list|(
name|language
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|Class
name|langClazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandles
operator|.
name|Lookup
name|lookup
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|lookup
operator|.
name|findConstructor
argument_list|(
name|langClazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|Locale
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|Locale
argument_list|,
name|NumberFormatter
argument_list|>
name|constructor
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|Locale
argument_list|,
name|NumberFormatter
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|lookup
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
decl_stmt|;
return|return
name|constructor
operator|.
name|apply
argument_list|(
name|locale
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// NOTE: must set interrupted flag
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|NumberFormatter_en
argument_list|(
name|locale
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit
