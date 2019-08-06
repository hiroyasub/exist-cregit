begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|fn
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|xquery
operator|.
name|value
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
name|DatatypeConstants
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
name|XMLGregorianCalendar
import|;
end_import

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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|lang3
operator|.
name|StringUtils
import|;
end_import

begin_comment
comment|/**  * Parses a string containing the date and time in IETF format,  * returning the corresponding xs:dateTime value.  *  * @author Juri Leino (juri@existsolutions.com)  */
end_comment

begin_class
specifier|public
class|class
name|FunParseIetfDate
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
name|FunctionParameterSequenceType
name|IETF_DATE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The IETF-dateTime string"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|FunctionReturnSequenceType
name|RETURN
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|DATE_TIME
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The parsed date"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_PARSE_IETF_DATE
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"parse-ietf-date"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
argument_list|,
literal|"Parses a string containing the date and time in IETF format,\n"
operator|+
literal|"returning the corresponding xs:dateTime value."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|IETF_DATE
block|}
argument_list|,
name|RETURN
argument_list|)
decl_stmt|;
specifier|public
name|FunParseIetfDate
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|final
name|String
name|value
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Parser
name|p
init|=
operator|new
name|Parser
argument_list|(
name|value
operator|.
name|trim
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|DateTimeValue
argument_list|(
name|p
operator|.
name|parse
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|i
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0010
argument_list|,
literal|"Invalid Date time "
operator|+
name|value
argument_list|,
name|i
argument_list|)
throw|;
block|}
block|}
specifier|private
class|class
name|Parser
block|{
specifier|private
specifier|final
name|char
index|[]
name|WS
init|=
block|{
literal|0x000A
block|,
literal|0x0009
block|,
literal|0x000D
block|,
literal|0x0020
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
name|WS_STR
init|=
operator|new
name|String
argument_list|(
name|WS
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|dayNames
init|=
block|{
literal|"Mon"
block|,
literal|"Tue"
block|,
literal|"Wed"
block|,
literal|"Thu"
block|,
literal|"Fri"
block|,
literal|"Sat"
block|,
literal|"Sun"
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
block|,
literal|"Sunday"
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|monthNames
init|=
block|{
literal|"Jan"
block|,
literal|"Feb"
block|,
literal|"Mar"
block|,
literal|"Apr"
block|,
literal|"May"
block|,
literal|"Jun"
block|,
literal|"Jul"
block|,
literal|"Aug"
block|,
literal|"Sep"
block|,
literal|"Oct"
block|,
literal|"Nov"
block|,
literal|"Dec"
block|}
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|tzNames
init|=
block|{
literal|"UT"
block|,
literal|"UTC"
block|,
literal|"GMT"
block|,
literal|"EST"
block|,
literal|"EDT"
block|,
literal|"CST"
block|,
literal|"CDT"
block|,
literal|"MST"
block|,
literal|"MDT"
block|,
literal|"PST"
block|,
literal|"PDT"
block|}
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|TZ_MAP
init|=
name|initMap
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|value
decl_stmt|;
specifier|private
specifier|final
name|int
name|vlen
decl_stmt|;
specifier|private
name|int
name|vidx
decl_stmt|;
specifier|private
name|BigInteger
name|year
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|month
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|int
name|day
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|int
name|hour
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|int
name|minute
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|int
name|second
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|BigDecimal
name|fractionalSecond
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|timezone
init|=
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
decl_stmt|;
specifier|private
name|Parser
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|vlen
operator|=
name|value
operator|.
name|length
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|initMap
parameter_list|()
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Integer
argument_list|>
name|result
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"UT"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"UTC"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"GMT"
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"EST"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"EDT"
argument_list|,
operator|-
literal|4
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"CST"
argument_list|,
operator|-
literal|6
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"CDT"
argument_list|,
operator|-
literal|5
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"MST"
argument_list|,
operator|-
literal|7
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"MDT"
argument_list|,
operator|-
literal|6
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"PST"
argument_list|,
operator|-
literal|8
argument_list|)
expr_stmt|;
name|result
operator|.
name|put
argument_list|(
literal|"PDT"
argument_list|,
operator|-
literal|7
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**          *<p>Parse a formatted<code>String</code> into an<code>XMLGregorianCalendar</code>.</p>          *<p>          *<p>If<code>String</code> is not formatted as a legal<code>IETF Date</code> value,          * an<code>IllegalArgumentException</code> is thrown.</p>          *<pre>          * input	::=	(dayname ","? S)? ((datespec S time) | asctime)          * datespec	::=	daynum dsep monthname dsep year          * dsep	::=	S | (S? "-" S?)          * daynum	::=	digit digit?          * year	::=	digit digit (digit digit)?          * digit	::=	[0-9]          * time	::=	hours ":" minutes (":" seconds)? (S? timezone)?          * hours	::=	digit digit?          * minutes	::=	digit digit          * seconds	::=	digit digit ("." digit+)?          * S ::= (x0A|x09|x0D|x20)+          *</pre>          *          * @throws IllegalArgumentException If<code>String</code> is not formatted as a legal<code>IETF Date</code> value.          */
specifier|public
name|XMLGregorianCalendar
name|parse
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|dayName
argument_list|()
expr_stmt|;
name|dateSpec
argument_list|()
expr_stmt|;
if|if
condition|(
name|vidx
operator|!=
name|vlen
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
return|return
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|getFactory
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|year
argument_list|,
name|month
argument_list|,
name|day
argument_list|,
name|hour
argument_list|,
name|minute
argument_list|,
name|second
argument_list|,
name|fractionalSecond
argument_list|,
name|timezone
argument_list|)
return|;
block|}
specifier|private
name|void
name|dayName
parameter_list|()
block|{
if|if
condition|(
name|StringUtils
operator|.
name|startsWithAny
argument_list|(
name|value
argument_list|,
name|dayNames
argument_list|)
condition|)
block|{
name|skipTo
argument_list|(
name|WS_STR
argument_list|)
expr_stmt|;
name|vidx
operator|++
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dateSpec
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|skipWS
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|StringUtils
operator|.
name|startsWithAny
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|vidx
argument_list|)
argument_list|,
name|monthNames
argument_list|)
condition|)
block|{
name|asctime
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|rfcDate
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|rfcDate
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|day
argument_list|()
expr_stmt|;
name|dsep
argument_list|()
expr_stmt|;
name|month
argument_list|()
expr_stmt|;
name|dsep
argument_list|()
expr_stmt|;
name|year
argument_list|()
expr_stmt|;
name|skipWS
argument_list|()
expr_stmt|;
name|time
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|asctime
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|month
argument_list|()
expr_stmt|;
name|dsep
argument_list|()
expr_stmt|;
name|day
argument_list|()
expr_stmt|;
name|skipWS
argument_list|()
expr_stmt|;
name|time
argument_list|()
expr_stmt|;
name|skipWS
argument_list|()
expr_stmt|;
name|year
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|year
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|vstart
init|=
name|vidx
decl_stmt|;
while|while
condition|(
name|isDigit
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
specifier|final
name|int
name|digits
init|=
name|vidx
operator|-
name|vstart
decl_stmt|;
name|String
name|yearString
decl_stmt|;
if|if
condition|(
name|digits
operator|==
literal|2
condition|)
block|{
name|yearString
operator|=
literal|"19"
operator|+
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|digits
operator|==
literal|4
condition|)
block|{
name|yearString
operator|=
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
name|year
operator|=
operator|new
name|BigInteger
argument_list|(
name|yearString
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|month
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|vstart
init|=
name|vidx
decl_stmt|;
name|vidx
operator|+=
literal|3
expr_stmt|;
if|if
condition|(
name|vidx
operator|>=
name|vlen
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
specifier|final
name|String
name|monthName
init|=
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
decl_stmt|;
specifier|final
name|int
name|idx
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|monthNames
argument_list|)
operator|.
name|indexOf
argument_list|(
name|monthName
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
name|month
operator|=
name|idx
operator|+
literal|1
expr_stmt|;
block|}
specifier|private
name|void
name|day
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|day
operator|=
name|parseInt
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|time
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|hours
argument_list|()
expr_stmt|;
name|minutes
argument_list|()
expr_stmt|;
name|seconds
argument_list|()
expr_stmt|;
name|skipWS
argument_list|()
expr_stmt|;
name|timezone
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|hours
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|hour
operator|=
name|parseInt
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|minutes
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
name|skip
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|minute
operator|=
name|parseInt
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|checkMinutes
argument_list|(
name|minute
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|seconds
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|second
operator|=
literal|0
expr_stmt|;
return|return;
block|}
name|skip
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|second
operator|=
name|parseInt
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|fractionalSecond
operator|=
name|parseBigDecimal
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|timezone
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
operator|!
name|StringUtils
operator|.
name|startsWithAny
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|vidx
argument_list|)
argument_list|,
name|tzNames
argument_list|)
condition|)
block|{
name|tzoffset
argument_list|()
expr_stmt|;
return|return;
block|}
name|parseTimezoneName
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|parseTimezoneName
parameter_list|()
block|{
specifier|final
name|int
name|vstart
init|=
name|vidx
decl_stmt|;
while|while
condition|(
name|isUpperCaseLetter
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
specifier|final
name|String
name|tzName
init|=
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|TZ_MAP
operator|.
name|containsKey
argument_list|(
name|tzName
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
name|timezone
operator|=
name|TZ_MAP
operator|.
name|get
argument_list|(
name|tzName
argument_list|)
operator|*
literal|60
expr_stmt|;
block|}
specifier|private
name|void
name|tzoffset
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|char
name|sign
init|=
name|peek
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|sign
operator|==
literal|'+'
operator|||
name|sign
operator|==
literal|'-'
operator|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
name|vidx
operator|++
expr_stmt|;
specifier|final
name|int
name|h
init|=
name|parseInt
argument_list|(
literal|1
argument_list|,
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|peek
argument_list|()
operator|==
literal|':'
condition|)
block|{
name|skip
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
block|}
name|int
name|m
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|isDigit
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|m
operator|=
name|parseInt
argument_list|(
literal|2
argument_list|,
literal|2
argument_list|)
expr_stmt|;
block|}
name|checkMinutes
argument_list|(
name|m
argument_list|)
expr_stmt|;
specifier|final
name|int
name|offset
init|=
name|h
operator|*
literal|60
operator|+
name|m
decl_stmt|;
specifier|final
name|int
name|factor
init|=
operator|(
name|sign
operator|==
literal|'+'
condition|?
literal|1
else|:
operator|-
literal|1
operator|)
decl_stmt|;
name|timezone
operator|=
name|offset
operator|*
name|factor
expr_stmt|;
comment|// cut off whitespace and optional timezone in parenthesis
if|if
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
operator|||
name|peek
argument_list|()
operator|==
literal|'('
condition|)
block|{
name|vidx
operator|=
name|vlen
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|dsep
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|skipWS
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|peek
argument_list|()
operator|!=
literal|'-'
condition|)
block|{
return|return;
block|}
name|skip
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
if|if
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|skipWS
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|skipWS
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
operator|!
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
while|while
condition|(
name|isWS
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
block|}
specifier|private
name|char
name|peek
parameter_list|()
block|{
if|if
condition|(
name|vidx
operator|==
name|vlen
condition|)
block|{
return|return
operator|(
name|char
operator|)
operator|-
literal|1
return|;
block|}
return|return
name|value
operator|.
name|charAt
argument_list|(
name|vidx
argument_list|)
return|;
block|}
specifier|private
name|char
name|read
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|vidx
operator|==
name|vlen
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
return|return
name|value
operator|.
name|charAt
argument_list|(
name|vidx
operator|++
argument_list|)
return|;
block|}
specifier|private
name|void
name|skipTo
parameter_list|(
name|String
name|sequence
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
while|while
condition|(
name|sequence
operator|.
name|indexOf
argument_list|(
name|peek
argument_list|()
argument_list|)
operator|<
literal|0
condition|)
block|{
name|read
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|skip
parameter_list|(
name|char
name|ch
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|read
argument_list|()
operator|!=
name|ch
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
specifier|private
name|int
name|parseInt
parameter_list|(
name|int
name|minDigits
parameter_list|,
name|int
name|maxDigits
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|vstart
init|=
name|vidx
decl_stmt|;
while|while
condition|(
name|isDigit
argument_list|(
name|peek
argument_list|()
argument_list|)
operator|&&
operator|(
name|vidx
operator|-
name|vstart
operator|)
operator|<
name|maxDigits
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
if|if
condition|(
operator|(
name|vidx
operator|-
name|vstart
operator|)
operator|<
name|minDigits
condition|)
block|{
comment|// we are expecting more digits
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
return|return
name|Integer
operator|.
name|parseInt
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|BigDecimal
name|parseBigDecimal
parameter_list|()
throws|throws
name|IllegalArgumentException
block|{
specifier|final
name|int
name|vstart
init|=
name|vidx
decl_stmt|;
if|if
condition|(
name|peek
argument_list|()
operator|==
literal|'.'
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
else|else
block|{
return|return
operator|new
name|BigDecimal
argument_list|(
literal|"0"
argument_list|)
return|;
block|}
while|while
condition|(
name|isDigit
argument_list|(
name|peek
argument_list|()
argument_list|)
condition|)
block|{
name|vidx
operator|++
expr_stmt|;
block|}
return|return
operator|new
name|BigDecimal
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|vstart
argument_list|,
name|vidx
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|void
name|checkMinutes
parameter_list|(
name|int
name|m
parameter_list|)
block|{
if|if
condition|(
name|m
operator|>=
literal|60
operator|||
name|m
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|value
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|isWS
parameter_list|(
name|char
name|c
parameter_list|)
block|{
return|return
operator|(
name|WS_STR
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
operator|>=
literal|0
operator|)
return|;
block|}
specifier|private
name|boolean
name|isDigit
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
literal|'0'
operator|<=
name|ch
operator|&&
name|ch
operator|<=
literal|'9'
return|;
block|}
specifier|private
name|boolean
name|isUpperCaseLetter
parameter_list|(
name|char
name|ch
parameter_list|)
block|{
return|return
literal|'A'
operator|<=
name|ch
operator|&&
name|ch
operator|<=
literal|'Z'
return|;
block|}
block|}
block|}
end_class

end_unit
