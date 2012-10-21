begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

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
name|java
operator|.
name|lang
operator|.
name|String
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

begin_comment
comment|/**  * fn:format-number($value as numeric?, $picture as xs:string) as xs:string   * fn:format-number($value as numeric?, $picture as xs:string, $decimal-format-name as xs:string) as xs:string   *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|FnFormatNumbers
extends|extends
name|BasicFunction
block|{
specifier|private
specifier|static
specifier|final
name|SequenceType
name|NUMBER_PARAMETER
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"value"
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
literal|"The number to format"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SequenceType
name|PICTURE
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"picture"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The format pattern string.  Please see the JavaDoc for java.text.DecimalFormat to get the specifics of this format string."
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|PICTURE_DESCRIPTION
init|=
literal|"The formatting of a number is controlled by a picture string. The picture string is a sequence of Â·charactersÂ·, in which the characters assigned to the variables decimal-separator-sign, grouping-sign, decimal-digit-family, optional-digit-sign and pattern-separator-sign are classified as active characters, and all other characters (including the percent-sign and per-mille-sign) are classified as passive characters."
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|SequenceType
name|DECIMAL_FORMAT
init|=
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"decimal-format-name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The decimal-format name must be a QName, which is expanded as described in [2.4 Qualified Names]. It is an error if the stylesheet does not contain a declaration of the decimal-format with the specified expanded-name."
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DECIMAL_FORMAT_DESCRIPTION
init|=
literal|""
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|FunctionReturnSequenceType
name|FUNCTION_RETURN_TYPE
init|=
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"the formatted string"
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"format-number"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|PICTURE_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NUMBER_PARAMETER
block|,
name|PICTURE
block|}
argument_list|,
name|FUNCTION_RETURN_TYPE
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"format-number"
argument_list|,
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|,
name|FnModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
name|DECIMAL_FORMAT_DESCRIPTION
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
name|NUMBER_PARAMETER
block|,
name|PICTURE
block|,
name|DECIMAL_FORMAT
block|}
argument_list|,
name|FUNCTION_RETURN_TYPE
argument_list|)
block|}
decl_stmt|;
comment|/** 	 * @param context 	 */
specifier|public
name|FnFormatNumbers
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
name|NumericValue
name|numericValue
init|=
operator|(
name|NumericValue
operator|)
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
name|Formatter
index|[]
name|formatters
init|=
name|prepare
argument_list|(
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|format
argument_list|(
name|formatters
index|[
literal|0
index|]
argument_list|,
name|numericValue
argument_list|)
decl_stmt|;
return|return
operator|new
name|StringValue
argument_list|(
name|value
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|java
operator|.
name|lang
operator|.
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
specifier|private
name|String
name|format
parameter_list|(
name|Formatter
name|f
parameter_list|,
name|NumericValue
name|numericValue
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|numericValue
operator|.
name|isNaN
argument_list|()
condition|)
block|{
return|return
name|NaN
return|;
block|}
name|String
name|minuSign
init|=
name|numericValue
operator|.
name|isNegative
argument_list|()
condition|?
name|String
operator|.
name|valueOf
argument_list|(
name|MINUS_SIGN
argument_list|)
else|:
literal|""
decl_stmt|;
if|if
condition|(
name|numericValue
operator|.
name|isInfinite
argument_list|()
condition|)
block|{
return|return
name|minuSign
operator|+
name|f
operator|.
name|prefix
operator|+
name|INFINITY
operator|+
name|f
operator|.
name|suffix
return|;
block|}
name|NumericValue
name|factor
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|f
operator|.
name|isPercent
condition|)
block|{
name|factor
operator|=
operator|new
name|IntegerValue
argument_list|(
literal|100
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|f
operator|.
name|isPerMille
condition|)
block|{
name|factor
operator|=
operator|new
name|IntegerValue
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|factor
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|numericValue
operator|=
operator|(
name|NumericValue
operator|)
name|numericValue
operator|.
name|mult
argument_list|(
name|factor
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
name|int
name|pl
init|=
literal|0
decl_stmt|;
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|numericValue
operator|.
name|hasFractionalPart
argument_list|()
condition|)
block|{
name|BigDecimal
name|val
init|=
operator|(
operator|(
name|DecimalValue
operator|)
name|numericValue
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|val
operator|=
name|val
operator|.
name|setScale
argument_list|(
name|f
operator|.
name|flMAX
argument_list|,
name|BigDecimal
operator|.
name|ROUND_HALF_EVEN
argument_list|)
expr_stmt|;
name|String
name|number
init|=
name|val
operator|.
name|toPlainString
argument_list|()
decl_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|number
argument_list|)
expr_stmt|;
name|pl
operator|=
name|number
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
if|if
condition|(
name|pl
operator|<
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|f
operator|.
name|flMIN
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
block|}
block|}
else|else
block|{
name|String
name|str
init|=
name|numericValue
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|pl
operator|=
name|str
operator|.
name|length
argument_list|()
expr_stmt|;
name|formatInt
argument_list|(
name|str
argument_list|,
name|sb
argument_list|,
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|.
name|mg
operator|!=
literal|0
condition|)
block|{
name|int
name|pos
init|=
name|pl
operator|-
name|f
operator|.
name|mg
decl_stmt|;
while|while
condition|(
name|pos
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|insert
argument_list|(
name|pos
argument_list|,
literal|','
argument_list|)
expr_stmt|;
name|pos
operator|-=
name|f
operator|.
name|mg
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|formatInt
parameter_list|(
name|String
name|number
parameter_list|,
name|StringBuilder
name|sb
parameter_list|,
name|Formatter
name|f
parameter_list|)
block|{
name|int
name|leadingZ
init|=
name|f
operator|.
name|mlMIN
operator|-
name|number
operator|.
name|length
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
name|leadingZ
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|number
argument_list|)
expr_stmt|;
if|if
condition|(
name|f
operator|.
name|flMIN
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|"."
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|f
operator|.
name|mlMIN
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'0'
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|final
name|char
name|DECIMAL_SEPARATOR_SIGN
init|=
literal|'.'
decl_stmt|;
specifier|private
specifier|final
name|char
name|GROUPING_SEPARATOR_SIGN
init|=
literal|','
decl_stmt|;
specifier|private
specifier|final
name|String
name|INFINITY
init|=
literal|"Infinity"
decl_stmt|;
specifier|private
specifier|final
name|char
name|MINUS_SIGN
init|=
literal|'-'
decl_stmt|;
specifier|private
specifier|final
name|String
name|NaN
init|=
literal|"NaN"
decl_stmt|;
specifier|private
specifier|final
name|char
name|PERCENT_SIGN
init|=
literal|'%'
decl_stmt|;
specifier|private
specifier|final
name|char
name|PER_MILLE_SIGN
init|=
literal|'\u2030'
decl_stmt|;
specifier|private
specifier|final
name|char
name|MANDATORY_DIGIT_SIGN
init|=
literal|'0'
decl_stmt|;
specifier|private
specifier|final
name|char
name|OPTIONAL_DIGIT_SIGN
init|=
literal|'#'
decl_stmt|;
specifier|private
specifier|final
name|char
name|PATTERN_SEPARATOR_SIGN
init|=
literal|';'
decl_stmt|;
specifier|private
name|Formatter
index|[]
name|prepare
parameter_list|(
name|String
name|picture
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|picture
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"format-number() picture is zero-length"
argument_list|)
throw|;
name|String
index|[]
name|pics
init|=
name|picture
operator|.
name|split
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|PATTERN_SEPARATOR_SIGN
argument_list|)
argument_list|)
decl_stmt|;
name|Formatter
index|[]
name|formatters
init|=
operator|new
name|Formatter
index|[
literal|2
index|]
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
name|pics
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|formatters
index|[
name|i
index|]
operator|=
operator|new
name|Formatter
argument_list|(
name|pics
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|formatters
return|;
block|}
class|class
name|Formatter
block|{
name|String
name|prefix
init|=
literal|""
decl_stmt|,
name|suffix
init|=
literal|""
decl_stmt|;
name|boolean
name|ds
init|=
literal|false
decl_stmt|,
name|isPercent
init|=
literal|false
decl_stmt|,
name|isPerMille
init|=
literal|false
decl_stmt|;
name|int
name|mlMAX
init|=
literal|0
decl_stmt|,
name|flMAX
init|=
literal|0
decl_stmt|;
name|int
name|mlMIN
init|=
literal|0
decl_stmt|,
name|flMIN
init|=
literal|0
decl_stmt|;
name|int
name|mg
init|=
literal|0
decl_stmt|,
name|fg
init|=
literal|0
decl_stmt|;
specifier|public
name|Formatter
parameter_list|(
name|String
name|picture
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|!
operator|(
name|picture
operator|.
name|contains
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|OPTIONAL_DIGIT_SIGN
argument_list|)
argument_list|)
operator|||
name|picture
operator|.
name|contains
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|MANDATORY_DIGIT_SIGN
argument_list|)
argument_list|)
operator|)
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must contain at least one character that is an optional-digit-sign or a member of the decimal-digit-family."
argument_list|)
throw|;
block|}
name|int
name|bmg
init|=
operator|-
literal|1
decl_stmt|,
name|bfg
init|=
operator|-
literal|1
decl_stmt|;
comment|// 0 - beginning passive-chars
comment|// 1 - digit signs
comment|// 2 - zero signs
comment|// 3 - fractional zero signs
comment|// 4 - fractional digit signs
comment|// 5 - ending passive-chars
name|short
name|phase
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
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
name|OPTIONAL_DIGIT_SIGN
case|:
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
name|mlMAX
operator|++
expr_stmt|;
name|phase
operator|=
literal|1
expr_stmt|;
break|break;
case|case
literal|2
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|""
argument_list|)
throw|;
case|case
literal|3
case|:
case|case
literal|4
case|:
name|flMAX
operator|++
expr_stmt|;
name|phase
operator|=
literal|4
expr_stmt|;
break|break;
case|case
literal|5
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain a passive character that is preceded by an active character and that is followed by another active character. "
operator|+
literal|"Found at optional-digit-sign."
argument_list|)
throw|;
block|}
break|break;
case|case
name|MANDATORY_DIGIT_SIGN
case|:
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
name|mlMIN
operator|++
expr_stmt|;
name|mlMAX
operator|++
expr_stmt|;
name|phase
operator|=
literal|2
expr_stmt|;
break|break;
case|case
literal|3
case|:
name|flMIN
operator|++
expr_stmt|;
name|flMAX
operator|++
expr_stmt|;
break|break;
case|case
literal|4
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|""
argument_list|)
throw|;
case|case
literal|5
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain a passive character that is preceded by an active character and that is followed by another active character. "
operator|+
literal|"Found at mandatory-digit-sign."
argument_list|)
throw|;
block|}
break|break;
case|case
name|GROUPING_SEPARATOR_SIGN
case|:
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
if|if
condition|(
name|bmg
operator|==
operator|-
literal|1
condition|)
block|{
name|bmg
operator|=
name|i
expr_stmt|;
block|}
else|else
block|{
name|mg
operator|=
name|i
operator|-
name|bmg
expr_stmt|;
name|bmg
operator|=
operator|-
literal|1
expr_stmt|;
block|}
break|break;
case|case
literal|3
case|:
case|case
literal|4
case|:
if|if
condition|(
name|bfg
operator|==
operator|-
literal|1
condition|)
block|{
name|bfg
operator|=
name|i
expr_stmt|;
block|}
else|else
block|{
name|fg
operator|=
name|i
operator|-
name|bfg
expr_stmt|;
name|bfg
operator|=
operator|-
literal|1
expr_stmt|;
block|}
break|break;
case|case
literal|5
case|:
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain a passive character that is preceded by an active character and that is followed by another active character. "
operator|+
literal|"Found at grouping-separator-sign."
argument_list|)
throw|;
block|}
break|break;
case|case
name|DECIMAL_SEPARATOR_SIGN
case|:
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
case|case
literal|1
case|:
case|case
literal|2
case|:
if|if
condition|(
name|bmg
operator|!=
operator|-
literal|1
condition|)
block|{
name|mg
operator|=
name|i
operator|-
name|bmg
operator|-
literal|1
expr_stmt|;
name|bmg
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|ds
operator|=
literal|true
expr_stmt|;
name|phase
operator|=
literal|3
expr_stmt|;
break|break;
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
if|if
condition|(
name|ds
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain more than one decimal-separator-sign."
argument_list|)
throw|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain a passive character that is preceded by an active character and that is followed by another active character. "
operator|+
literal|"Found at decimal-separator-sign."
argument_list|)
throw|;
block|}
break|break;
case|case
name|PERCENT_SIGN
case|:
case|case
name|PER_MILLE_SIGN
case|:
if|if
condition|(
name|isPercent
operator|||
name|isPerMille
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|FnFormatNumbers
operator|.
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XTDE1310
argument_list|,
literal|"A sub-picture must not contain more than one percent-sign or per-mille-sign, and it must not contain one of each."
argument_list|)
throw|;
name|isPercent
operator|=
name|ch
operator|==
name|PERCENT_SIGN
expr_stmt|;
name|isPerMille
operator|=
name|ch
operator|==
name|PER_MILLE_SIGN
expr_stmt|;
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
name|prefix
operator|+=
name|ch
expr_stmt|;
break|break;
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
name|phase
operator|=
literal|5
expr_stmt|;
name|suffix
operator|+=
name|ch
expr_stmt|;
break|break;
block|}
break|break;
default|default:
comment|//passive chars
switch|switch
condition|(
name|phase
condition|)
block|{
case|case
literal|0
case|:
name|prefix
operator|+=
name|ch
expr_stmt|;
break|break;
case|case
literal|1
case|:
case|case
literal|2
case|:
case|case
literal|3
case|:
case|case
literal|4
case|:
case|case
literal|5
case|:
if|if
condition|(
name|bmg
operator|!=
operator|-
literal|1
condition|)
block|{
name|mg
operator|=
name|i
operator|-
name|bmg
operator|-
literal|1
expr_stmt|;
name|bmg
operator|=
operator|-
literal|1
expr_stmt|;
block|}
name|suffix
operator|+=
name|ch
expr_stmt|;
name|phase
operator|=
literal|5
expr_stmt|;
break|break;
block|}
break|break;
block|}
block|}
if|if
condition|(
name|mlMIN
operator|==
literal|0
operator|&&
operator|!
name|ds
condition|)
name|mlMIN
operator|=
literal|1
expr_stmt|;
comment|//			System.out.println("prefix = "+prefix);
comment|//			System.out.println("suffix = "+suffix);
comment|//			System.out.println("ds = "+ds);
comment|//			System.out.println("isPercent = "+isPercent);
comment|//			System.out.println("isPerMille = "+isPerMille);
comment|//			System.out.println("ml = "+mlMAX);
comment|//			System.out.println("fl = "+flMAX);
comment|//			System.out.println("mg = "+mg);
comment|//			System.out.println("fg = "+fg);
block|}
block|}
block|}
end_class

end_unit

