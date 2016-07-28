begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

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
name|text
operator|.
name|Collator
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
name|Collations
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
name|Constants
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
name|Constants
operator|.
name|Comparison
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
name|ErrorCodes
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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|UntypedAtomicValue
extends|extends
name|AtomicValue
block|{
specifier|private
name|String
name|value
decl_stmt|;
specifier|public
name|UntypedAtomicValue
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
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#getType()      */
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|UNTYPED_ATOMIC
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#getStringValue()      */
annotation|@
name|Override
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Sequence#convertTo(int)      */
annotation|@
name|Override
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
return|return
name|convertTo
argument_list|(
name|this
argument_list|,
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|AtomicValue
name|convertTo
parameter_list|(
name|String
name|value
parameter_list|,
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|convertTo
argument_list|(
literal|null
argument_list|,
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|AtomicValue
name|convertTo
parameter_list|(
name|UntypedAtomicValue
name|strVal
parameter_list|,
name|String
name|value
parameter_list|,
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
name|UNTYPED_ATOMIC
case|:
return|return
name|strVal
operator|==
literal|null
condition|?
operator|new
name|UntypedAtomicValue
argument_list|(
name|value
argument_list|)
else|:
name|strVal
return|;
case|case
name|Type
operator|.
name|STRING
case|:
return|return
operator|new
name|StringValue
argument_list|(
name|value
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
specifier|final
name|String
name|trimmed
init|=
name|StringValue
operator|.
name|trimWhitespace
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"0"
operator|.
name|equals
argument_list|(
name|trimmed
argument_list|)
operator|||
literal|"false"
operator|.
name|equals
argument_list|(
name|trimmed
argument_list|)
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
if|else if
condition|(
literal|"1"
operator|.
name|equals
argument_list|(
name|trimmed
argument_list|)
operator|||
literal|"true"
operator|.
name|equals
argument_list|(
name|trimmed
argument_list|)
condition|)
block|{
return|return
name|BooleanValue
operator|.
name|TRUE
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"cannot cast '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|)
operator|+
literal|"(\""
operator|+
name|value
operator|+
literal|"\")' to "
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
case|case
name|Type
operator|.
name|FLOAT
case|:
return|return
operator|new
name|FloatValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DOUBLE
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
name|NUMBER
case|:
comment|//TODO : more complicated
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
name|POSITIVE_INTEGER
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
comment|/*          The problem is that if this UntypedAtomicValue is constructed from a text() node          stored in the database, which contains base64 or hex encoded data, then the string value could be huge          and it has already been constructed and stored in memort by UntypedAtomicValue          this should be defered           TODO replace UntypedAtomicValue with something that can allow lazily reading text()          values from the database.          */
case|case
name|Type
operator|.
name|BASE64_BINARY
case|:
return|return
operator|new
name|BinaryValueFromBinaryString
argument_list|(
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|,
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|HEX_BINARY
case|:
return|return
operator|new
name|BinaryValueFromBinaryString
argument_list|(
operator|new
name|HexBinaryValueType
argument_list|()
argument_list|,
name|value
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
name|GYEAR
case|:
return|return
operator|new
name|GYearValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|GMONTH
case|:
return|return
operator|new
name|GMonthValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|GDAY
case|:
return|return
operator|new
name|GDayValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|GYEARMONTH
case|:
return|return
operator|new
name|GYearMonthValue
argument_list|(
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|GMONTHDAY
case|:
return|return
operator|new
name|GMonthDayValue
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
specifier|final
name|DayTimeDurationValue
name|dtdv
init|=
operator|new
name|DayTimeDurationValue
argument_list|(
name|value
argument_list|)
decl_stmt|;
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|dtdv
operator|.
name|getCanonicalDuration
argument_list|()
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|FORG0001
argument_list|,
literal|"cannot cast '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|Type
operator|.
name|ATOMIC
argument_list|)
operator|+
literal|"(\""
operator|+
name|value
operator|+
literal|"\")' to "
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
annotation|@
name|Override
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|Comparison
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
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
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
operator|||
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
name|UNTYPED_ATOMIC
argument_list|)
condition|)
block|{
specifier|final
name|int
name|cmp
init|=
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
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
name|EQ
case|:
return|return
name|cmp
operator|==
literal|0
return|;
case|case
name|NEQ
case|:
return|return
name|cmp
operator|!=
literal|0
return|;
case|case
name|LT
case|:
return|return
name|cmp
operator|<
literal|0
return|;
case|case
name|LTEQ
case|:
return|return
name|cmp
operator|<=
literal|0
return|;
case|case
name|GT
case|:
return|return
name|cmp
operator|>
literal|0
return|;
case|case
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
literal|"Type error: operands are not comparable; expected xdt:untypedAtomic; got "
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#compareTo(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#max(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|AtomicValue
name|max
parameter_list|(
name|Collator
name|collator
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
name|UNTYPED_ATOMIC
argument_list|)
condition|)
block|{
return|return
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
operator|(
operator|(
name|UntypedAtomicValue
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
block|}
return|return
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#min(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|AtomicValue
name|min
parameter_list|(
name|Collator
name|collator
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
name|UNTYPED_ATOMIC
argument_list|)
condition|)
block|{
return|return
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
operator|(
operator|(
name|UntypedAtomicValue
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
block|}
return|return
name|Collations
operator|.
name|compare
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#startsWith(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|boolean
name|startsWith
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|Collations
operator|.
name|startsWith
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#endsWith(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|boolean
name|endsWith
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|Collations
operator|.
name|endsWith
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.AtomicValue#contains(org.exist.xquery.value.AtomicValue)      */
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|Collator
name|collator
parameter_list|,
name|AtomicValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|Collations
operator|.
name|indexOf
argument_list|(
name|collator
argument_list|,
name|value
argument_list|,
name|other
operator|.
name|getStringValue
argument_list|()
argument_list|)
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
comment|// If its operand is a singleton value of type xs:string, xs:anyURI, xs:untypedAtomic,
comment|//or a type derived from one of these, fn:boolean returns false if the operand value has zero length; otherwise it returns true.
return|return
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class)      */
annotation|@
name|Override
specifier|public
name|int
name|conversionPreference
parameter_list|(
name|Class
argument_list|<
name|?
argument_list|>
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
block|{
return|return
literal|0
return|;
block|}
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
block|{
return|return
literal|1
return|;
block|}
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
block|{
return|return
literal|2
return|;
block|}
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
block|{
return|return
literal|10
return|;
block|}
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
block|{
return|return
literal|11
return|;
block|}
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
block|{
return|return
literal|12
return|;
block|}
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
block|{
return|return
literal|13
return|;
block|}
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
block|{
return|return
literal|14
return|;
block|}
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
block|{
return|return
literal|15
return|;
block|}
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
block|{
return|return
literal|16
return|;
block|}
if|if
condition|(
name|javaClass
operator|==
name|Object
operator|.
name|class
condition|)
block|{
return|return
literal|20
return|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class)      */
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|(
specifier|final
name|Class
argument_list|<
name|T
argument_list|>
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
name|UntypedAtomicValue
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
operator|(
name|T
operator|)
name|this
return|;
block|}
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
block|{
return|return
operator|(
name|T
operator|)
name|value
return|;
block|}
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
specifier|final
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
operator|(
name|T
operator|)
name|Double
operator|.
name|valueOf
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
specifier|final
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
operator|(
name|T
operator|)
name|Float
operator|.
name|valueOf
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
specifier|final
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
operator|(
name|T
operator|)
name|Long
operator|.
name|valueOf
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
specifier|final
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
operator|(
name|T
operator|)
name|Integer
operator|.
name|valueOf
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
specifier|final
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
operator|(
name|T
operator|)
name|Short
operator|.
name|valueOf
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
specifier|final
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
operator|(
name|T
operator|)
name|Byte
operator|.
name|valueOf
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
operator|(
name|T
operator|)
name|Boolean
operator|.
name|valueOf
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert string with length = 0 or length> 1 to Java character"
argument_list|)
throw|;
block|}
return|return
operator|(
name|T
operator|)
name|Character
operator|.
name|valueOf
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
name|getType
argument_list|()
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
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|UntypedAtomicValue
condition|)
block|{
return|return
name|value
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|UntypedAtomicValue
operator|)
name|obj
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|value
operator|.
name|hashCode
argument_list|()
return|;
block|}
block|}
end_class

end_unit

