begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|oro
operator|.
name|text
operator|.
name|perl
operator|.
name|Perl5Util
import|;
end_import

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
name|util
operator|.
name|XMLChar
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
name|Constants
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
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|StringValue
extends|extends
name|AtomicValue
block|{
specifier|public
specifier|final
specifier|static
name|StringValue
name|EMPTY_STRING
init|=
operator|new
name|StringValue
argument_list|(
literal|""
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|type
init|=
name|Type
operator|.
name|STRING
decl_stmt|;
specifier|protected
name|String
name|value
decl_stmt|;
specifier|public
name|StringValue
parameter_list|(
name|String
name|stringValue
parameter_list|,
name|int
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|type
operator|==
name|Type
operator|.
name|STRING
condition|)
name|this
operator|.
name|value
operator|=
name|stringValue
expr_stmt|;
if|else if
condition|(
name|type
operator|==
name|Type
operator|.
name|NORMALIZED_STRING
condition|)
name|this
operator|.
name|value
operator|=
name|normalizeWhitespace
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
else|else
block|{
name|this
operator|.
name|value
operator|=
name|collapseWhitespace
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
name|checkType
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|StringValue
parameter_list|(
name|String
name|stringValue
parameter_list|)
block|{
name|value
operator|=
name|stringValue
expr_stmt|;
block|}
specifier|private
name|void
name|checkType
parameter_list|()
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|type
condition|)
block|{
case|case
name|Type
operator|.
name|NORMALIZED_STRING
case|:
case|case
name|Type
operator|.
name|TOKEN
case|:
return|return;
case|case
name|Type
operator|.
name|LANGUAGE
case|:
name|Perl5Util
name|util
init|=
operator|new
name|Perl5Util
argument_list|()
decl_stmt|;
name|String
name|regex
init|=
literal|"/(([a-z]|[A-Z])([a-z]|[A-Z])|"
comment|// ISO639Code
operator|+
literal|"([iI]-([a-z]|[A-Z])+)|"
comment|// IanaCode
operator|+
literal|"([xX]-([a-z]|[A-Z])+))"
comment|// UserCode
operator|+
literal|"(-([a-z]|[A-Z])+)*/"
decl_stmt|;
comment|// Subcode
if|if
condition|(
operator|!
name|util
operator|.
name|match
argument_list|(
name|regex
argument_list|,
name|value
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|value
operator|+
literal|" is not valid for type xs:language"
argument_list|)
throw|;
return|return;
case|case
name|Type
operator|.
name|NAME
case|:
if|if
condition|(
operator|!
name|QName
operator|.
name|isQName
argument_list|(
name|value
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|value
operator|+
literal|" is not a valid xs:Name"
argument_list|)
throw|;
return|return;
case|case
name|Type
operator|.
name|NCNAME
case|:
case|case
name|Type
operator|.
name|ID
case|:
case|case
name|Type
operator|.
name|IDREF
case|:
case|case
name|Type
operator|.
name|ENTITY
case|:
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNCName
argument_list|(
name|value
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|value
operator|+
literal|" is not a valid "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
case|case
name|Type
operator|.
name|NMTOKEN
case|:
if|if
condition|(
operator|!
name|XMLChar
operator|.
name|isValidNmtoken
argument_list|(
name|value
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: string "
operator|+
name|value
operator|+
literal|" is not a valid xs:NMTOKEN"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|Item
name|itemAt
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|pos
operator|==
literal|0
condition|?
name|this
else|:
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#convertTo(int) 	 */
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|requiredType
condition|)
block|{
case|case
name|Type
operator|.
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|ITEM
case|:
case|case
name|Type
operator|.
name|STRING
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|NORMALIZED_STRING
case|:
case|case
name|Type
operator|.
name|TOKEN
case|:
case|case
name|Type
operator|.
name|LANGUAGE
case|:
case|case
name|Type
operator|.
name|NMTOKEN
case|:
case|case
name|Type
operator|.
name|NAME
case|:
case|case
name|Type
operator|.
name|NCNAME
case|:
case|case
name|Type
operator|.
name|ID
case|:
case|case
name|Type
operator|.
name|IDREF
case|:
case|case
name|Type
operator|.
name|ENTITY
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
case|case
name|Type
operator|.
name|ANY_URI
case|:
return|return
operator|new
name|AnyURIValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"false"
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
if|else if
condition|(
name|value
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
operator|||
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert string '"
operator|+
name|value
operator|+
literal|"' to boolean"
argument_list|)
throw|;
case|case
name|Type
operator|.
name|FLOAT
case|:
case|case
name|Type
operator|.
name|DOUBLE
case|:
case|case
name|Type
operator|.
name|NUMBER
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DECIMAL
case|:
return|return
operator|new
name|DecimalValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|INTEGER
case|:
case|case
name|Type
operator|.
name|NON_POSITIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|NEGATIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|LONG
case|:
case|case
name|Type
operator|.
name|INT
case|:
case|case
name|Type
operator|.
name|SHORT
case|:
case|case
name|Type
operator|.
name|BYTE
case|:
case|case
name|Type
operator|.
name|NON_NEGATIVE_INTEGER
case|:
case|case
name|Type
operator|.
name|UNSIGNED_LONG
case|:
case|case
name|Type
operator|.
name|UNSIGNED_INT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_SHORT
case|:
case|case
name|Type
operator|.
name|UNSIGNED_BYTE
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE_TIME
case|:
return|return
operator|new
name|DateTimeValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|TIME
case|:
return|return
operator|new
name|TimeValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DATE
case|:
return|return
operator|new
name|DateValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DURATION
case|:
return|return
operator|new
name|DurationValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
operator|new
name|YearMonthDurationValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|value
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert string '"
operator|+
name|value
operator|+
literal|"' to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
name|javaClass
parameter_list|)
block|{
if|if
condition|(
name|javaClass
operator|.
name|isAssignableFrom
argument_list|(
name|StringValue
operator|.
name|class
argument_list|)
condition|)
return|return
literal|0
return|;
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
operator|||
name|javaClass
operator|==
name|CharSequence
operator|.
name|class
condition|)
return|return
literal|1
return|;
if|if
condition|(
name|javaClass
operator|==
name|Character
operator|.
name|class
operator|||
name|javaClass
operator|==
name|char
operator|.
name|class
condition|)
return|return
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|Double
operator|.
name|class
operator|||
name|javaClass
operator|==
name|double
operator|.
name|class
condition|)
return|return
literal|10
return|;
if|if
condition|(
name|javaClass
operator|==
name|Float
operator|.
name|class
operator|||
name|javaClass
operator|==
name|float
operator|.
name|class
condition|)
return|return
literal|11
return|;
if|if
condition|(
name|javaClass
operator|==
name|Long
operator|.
name|class
operator|||
name|javaClass
operator|==
name|long
operator|.
name|class
condition|)
return|return
literal|12
return|;
if|if
condition|(
name|javaClass
operator|==
name|Integer
operator|.
name|class
operator|||
name|javaClass
operator|==
name|int
operator|.
name|class
condition|)
return|return
literal|13
return|;
if|if
condition|(
name|javaClass
operator|==
name|Short
operator|.
name|class
operator|||
name|javaClass
operator|==
name|short
operator|.
name|class
condition|)
return|return
literal|14
return|;
if|if
condition|(
name|javaClass
operator|==
name|Byte
operator|.
name|class
operator|||
name|javaClass
operator|==
name|byte
operator|.
name|class
condition|)
return|return
literal|15
return|;
if|if
condition|(
name|javaClass
operator|==
name|Boolean
operator|.
name|class
operator|||
name|javaClass
operator|==
name|boolean
operator|.
name|class
condition|)
return|return
literal|16
return|;
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
return|return
literal|20
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
specifier|public
name|Object
name|toJavaObject
parameter_list|(
name|Class
name|target
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|target
operator|.
name|isAssignableFrom
argument_list|(
name|StringValue
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|else if
condition|(
name|target
operator|==
name|Object
operator|.
name|class
operator|||
name|target
operator|==
name|String
operator|.
name|class
operator|||
name|target
operator|==
name|CharSequence
operator|.
name|class
condition|)
return|return
name|value
return|;
if|else if
condition|(
name|target
operator|==
name|double
operator|.
name|class
operator|||
name|target
operator|==
name|Double
operator|.
name|class
condition|)
block|{
name|DoubleValue
name|v
init|=
operator|(
name|DoubleValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
decl_stmt|;
return|return
operator|new
name|Double
argument_list|(
name|v
operator|.
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|float
operator|.
name|class
operator|||
name|target
operator|==
name|Float
operator|.
name|class
condition|)
block|{
name|FloatValue
name|v
init|=
operator|(
name|FloatValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|FLOAT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Float
argument_list|(
name|v
operator|.
name|value
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|long
operator|.
name|class
operator|||
name|target
operator|==
name|Long
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
decl_stmt|;
return|return
operator|new
name|Long
argument_list|(
name|v
operator|.
name|getInt
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|int
operator|.
name|class
operator|||
name|target
operator|==
name|Integer
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Integer
argument_list|(
name|v
operator|.
name|getInt
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|short
operator|.
name|class
operator|||
name|target
operator|==
name|Short
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|SHORT
argument_list|)
decl_stmt|;
return|return
operator|new
name|Short
argument_list|(
operator|(
name|short
operator|)
name|v
operator|.
name|getInt
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|byte
operator|.
name|class
operator|||
name|target
operator|==
name|Byte
operator|.
name|class
condition|)
block|{
name|IntegerValue
name|v
init|=
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|BYTE
argument_list|)
decl_stmt|;
return|return
operator|new
name|Byte
argument_list|(
operator|(
name|byte
operator|)
name|v
operator|.
name|getInt
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|boolean
operator|.
name|class
operator|||
name|target
operator|==
name|Boolean
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Boolean
argument_list|(
name|effectiveBooleanValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|char
operator|.
name|class
operator|||
name|target
operator|==
name|Character
operator|.
name|class
condition|)
block|{
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|||
name|value
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
literal|"cannot convert string with length = 0 or length> 1 to Java character"
argument_list|)
throw|;
return|return
operator|new
name|Character
argument_list|(
name|value
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
operator|+
literal|" to Java object of type "
operator|+
name|target
operator|.
name|getName
argument_list|()
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#compareTo(int, org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|int
name|operator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|boolean
name|substringCompare
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|operator
operator|==
name|Constants
operator|.
name|EQ
condition|)
block|{
name|String
name|otherVal
init|=
name|other
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|truncation
init|=
name|Constants
operator|.
name|TRUNC_NONE
decl_stmt|;
if|if
condition|(
name|otherVal
operator|.
name|length
argument_list|()
operator|>
literal|0
operator|&&
name|otherVal
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'%'
condition|)
block|{
name|otherVal
operator|=
name|otherVal
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|truncation
operator|=
name|Constants
operator|.
name|TRUNC_LEFT
expr_stmt|;
block|}
if|if
condition|(
name|otherVal
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|otherVal
operator|.
name|charAt
argument_list|(
name|otherVal
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|'%'
condition|)
block|{
name|otherVal
operator|=
name|otherVal
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|otherVal
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|truncation
operator|=
operator|(
name|truncation
operator|==
name|Constants
operator|.
name|TRUNC_LEFT
operator|)
condition|?
name|Constants
operator|.
name|TRUNC_BOTH
else|:
name|Constants
operator|.
name|TRUNC_RIGHT
expr_stmt|;
block|}
switch|switch
condition|(
name|truncation
condition|)
block|{
case|case
name|Constants
operator|.
name|TRUNC_BOTH
case|:
return|return
name|value
operator|.
name|indexOf
argument_list|(
name|otherVal
argument_list|)
operator|>
operator|-
literal|1
return|;
case|case
name|Constants
operator|.
name|TRUNC_LEFT
case|:
return|return
name|value
operator|.
name|startsWith
argument_list|(
name|otherVal
argument_list|)
return|;
case|case
name|Constants
operator|.
name|TRUNC_RIGHT
case|:
return|return
name|value
operator|.
name|endsWith
argument_list|(
name|otherVal
argument_list|)
return|;
case|case
name|Constants
operator|.
name|TRUNC_NONE
case|:
return|return
name|value
operator|.
name|equals
argument_list|(
name|otherVal
argument_list|)
return|;
block|}
block|}
name|int
name|cmp
init|=
name|value
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|Constants
operator|.
name|EQ
case|:
return|return
name|cmp
operator|==
literal|0
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|cmp
operator|!=
literal|0
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
name|cmp
operator|<
literal|0
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|cmp
operator|<=
literal|0
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|cmp
operator|>
literal|0
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|cmp
operator|>=
literal|0
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot apply operand to string value"
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: operands are not comparable; expected xs:string; got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#compareTo(org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|int
name|compareTo
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|compareTo
argument_list|(
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#toString() 	 */
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
specifier|final
specifier|static
name|String
name|normalizeWhitespace
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
block|{
name|StringBuffer
name|copy
init|=
operator|new
name|StringBuffer
argument_list|(
name|seq
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|char
name|ch
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
name|seq
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
case|case
literal|'\t'
case|:
name|copy
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
break|break;
default|default :
name|copy
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|copy
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|String
name|collapseWhitespace
parameter_list|(
name|CharSequence
name|in
parameter_list|)
block|{
if|if
condition|(
name|in
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
name|in
operator|.
name|toString
argument_list|()
return|;
block|}
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|in
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|boolean
name|inWhitespace
init|=
literal|true
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|in
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|in
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
switch|switch
condition|(
name|c
condition|)
block|{
case|case
literal|'\n'
case|:
case|case
literal|'\r'
case|:
case|case
literal|'\t'
case|:
case|case
literal|' '
case|:
if|if
condition|(
name|inWhitespace
condition|)
block|{
comment|// remove the whitespace
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
name|inWhitespace
operator|=
literal|true
expr_stmt|;
block|}
break|break;
default|default :
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|inWhitespace
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|sb
operator|.
name|charAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|==
literal|' '
condition|)
block|{
name|sb
operator|.
name|deleteCharAt
argument_list|(
name|sb
operator|.
name|length
argument_list|()
operator|-
literal|1
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
specifier|public
specifier|final
specifier|static
name|String
name|expand
parameter_list|(
name|CharSequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
name|seq
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|StringBuffer
name|entityRef
init|=
literal|null
decl_stmt|;
name|char
name|ch
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
name|seq
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|ch
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'&'
case|:
if|if
condition|(
name|entityRef
operator|==
literal|null
condition|)
name|entityRef
operator|=
operator|new
name|StringBuffer
argument_list|()
expr_stmt|;
else|else
name|entityRef
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
name|i
operator|+
literal|1
init|;
name|j
operator|<
name|seq
operator|.
name|length
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|ch
operator|=
name|seq
operator|.
name|charAt
argument_list|(
name|j
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|!=
literal|';'
condition|)
name|entityRef
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
else|else
block|{
name|found
operator|=
literal|true
expr_stmt|;
name|i
operator|=
name|j
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|found
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|expandEntity
argument_list|(
name|entityRef
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'&'
argument_list|)
expr_stmt|;
block|}
break|break;
default|default :
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
name|char
name|expandEntity
parameter_list|(
name|String
name|buf
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"amp"
argument_list|)
condition|)
return|return
literal|'&'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"lt"
argument_list|)
condition|)
return|return
literal|'<'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"gt"
argument_list|)
condition|)
return|return
literal|'>'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"quot"
argument_list|)
condition|)
return|return
literal|'"'
return|;
if|else if
condition|(
name|buf
operator|.
name|equals
argument_list|(
literal|"apos"
argument_list|)
condition|)
return|return
literal|'\''
return|;
if|else if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|buf
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'#'
condition|)
return|return
name|expandCharRef
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|)
return|;
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown entity reference: "
operator|+
name|buf
argument_list|)
throw|;
block|}
specifier|private
specifier|final
specifier|static
name|char
name|expandCharRef
parameter_list|(
name|String
name|buf
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|buf
operator|.
name|length
argument_list|()
operator|>
literal|1
operator|&&
name|buf
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'x'
condition|)
block|{
comment|// Hex
return|return
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
argument_list|,
literal|16
argument_list|)
return|;
block|}
else|else
return|return
operator|(
name|char
operator|)
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown character reference: "
operator|+
name|buf
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#max(org.exist.xpath.value.AtomicValue) 	 */
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
return|return
name|value
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
else|else
return|return
name|value
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|value
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
return|return
name|value
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
return|;
else|else
return|return
name|value
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|StringValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|value
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
block|}
end_class

end_unit

