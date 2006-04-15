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
name|util
operator|.
name|Date
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
name|javax
operator|.
name|xml
operator|.
name|namespace
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
name|storage
operator|.
name|Indexable
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
name|ByteConversion
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
comment|/**  * Represents a value of type xs:dateTime.  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author<a href="mailto:piotr@ideanest.com">Piotr Kaminski</a>  */
end_comment

begin_class
specifier|public
class|class
name|DateTimeValue
extends|extends
name|AbstractDateTimeValue
implements|implements
name|Indexable
block|{
specifier|public
name|DateTimeValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
operator|new
name|GregorianCalendar
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DateTimeValue
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
block|{
name|super
argument_list|(
name|fillCalendar
argument_list|(
operator|(
name|XMLGregorianCalendar
operator|)
name|calendar
operator|.
name|clone
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DateTimeValue
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|super
argument_list|(
name|dateToXMLGregorianCalendar
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DateTimeValue
parameter_list|(
name|String
name|dateTime
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|dateTime
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
name|calendar
operator|.
name|getXMLSchemaType
argument_list|()
operator|!=
name|DatatypeConstants
operator|.
name|DATETIME
condition|)
throw|throw
operator|new
name|IllegalStateException
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|IllegalStateException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"xs:dateTime instance must have all fields set"
argument_list|)
throw|;
block|}
block|}
specifier|private
specifier|static
name|XMLGregorianCalendar
name|dateToXMLGregorianCalendar
parameter_list|(
name|Date
name|date
parameter_list|)
block|{
name|GregorianCalendar
name|gc
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setTime
argument_list|(
name|date
argument_list|)
expr_stmt|;
name|XMLGregorianCalendar
name|xgc
init|=
name|TimeUtils
operator|.
name|getInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|gc
argument_list|)
decl_stmt|;
name|xgc
operator|.
name|normalize
argument_list|()
expr_stmt|;
return|return
name|xgc
return|;
block|}
specifier|private
specifier|static
name|XMLGregorianCalendar
name|fillCalendar
parameter_list|(
name|XMLGregorianCalendar
name|calendar
parameter_list|)
block|{
if|if
condition|(
name|calendar
operator|.
name|getHour
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
name|calendar
operator|.
name|setHour
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|calendar
operator|.
name|getMinute
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
name|calendar
operator|.
name|setMinute
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|calendar
operator|.
name|getSecond
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
name|calendar
operator|.
name|setSecond
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|calendar
operator|.
name|getMillisecond
argument_list|()
operator|==
name|DatatypeConstants
operator|.
name|FIELD_UNDEFINED
condition|)
name|calendar
operator|.
name|setMillisecond
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|calendar
return|;
block|}
specifier|protected
name|AbstractDateTimeValue
name|createSameKind
parameter_list|(
name|XMLGregorianCalendar
name|cal
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DateTimeValue
argument_list|(
name|cal
argument_list|)
return|;
block|}
specifier|protected
name|QName
name|getXMLSchemaType
parameter_list|()
block|{
return|return
name|DatatypeConstants
operator|.
name|DATETIME
return|;
block|}
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0006: effective boolean value invalid operand type: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|DATE_TIME
return|;
block|}
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
name|DATE_TIME
case|:
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
return|return
name|this
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
name|calendar
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
name|calendar
argument_list|)
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
name|getStringValue
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast xs:dateTime to "
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
name|ComputableValue
name|minus
parameter_list|(
name|ComputableValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
switch|switch
condition|(
name|other
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|DATE_TIME
case|:
return|return
operator|new
name|DayTimeDurationValue
argument_list|(
name|getTimeInMillis
argument_list|()
operator|-
operator|(
operator|(
name|DateTimeValue
operator|)
name|other
operator|)
operator|.
name|getTimeInMillis
argument_list|()
argument_list|)
return|;
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
operator|(
operator|(
name|YearMonthDurationValue
operator|)
name|other
operator|)
operator|.
name|negate
argument_list|()
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
return|return
operator|(
operator|(
name|DayTimeDurationValue
operator|)
name|other
operator|)
operator|.
name|negate
argument_list|()
operator|.
name|plus
argument_list|(
name|this
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Operand to minus should be of type xs:dateTime, xdt:dayTimeDuration or xdt:yearMonthDuration; got: "
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
block|}
specifier|public
name|Date
name|getDate
parameter_list|()
block|{
return|return
name|calendar
operator|.
name|toGregorianCalendar
argument_list|()
operator|.
name|getTime
argument_list|()
return|;
block|}
comment|/** @deprecated      * @see org.exist.storage.Indexable#serialize(short)      */
specifier|public
name|byte
index|[]
name|serialize
parameter_list|(
name|short
name|collectionId
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|GregorianCalendar
name|utccal
init|=
name|calendar
operator|.
name|normalize
argument_list|()
operator|.
name|toGregorianCalendar
argument_list|()
decl_stmt|;
comment|//Get the dateTime (XMLGregorianCalendar) normalized to UTC (as a GregorianCalendar)
name|long
name|value
init|=
name|utccal
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
comment|//Get the normalized dateTime as a long (milliseconds since the Epoch)
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|11
index|]
decl_stmt|;
comment|//alocate a byte array for holding collectionId,Type,long (11 = (byte)short + byte + (byte)long)
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
comment|//put the collectionId in the byte array
name|data
index|[
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
name|Type
operator|.
name|DATE_TIME
expr_stmt|;
comment|//put the Type in the byte array
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|value
argument_list|,
name|data
argument_list|,
literal|3
argument_list|)
expr_stmt|;
comment|//put the long in the byte array
return|return
operator|(
name|data
operator|)
return|;
comment|//return the byte array
block|}
comment|/** Serialize for the persistant storage */
specifier|public
name|byte
index|[]
name|serializeValue
parameter_list|(
name|int
name|offset
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
block|{
name|GregorianCalendar
name|utccal
init|=
name|calendar
operator|.
name|normalize
argument_list|()
operator|.
name|toGregorianCalendar
argument_list|()
decl_stmt|;
comment|//Get the dateTime (XMLGregorianCalendar) normalized to UTC (as a GregorianCalendar)
name|long
name|value
init|=
name|utccal
operator|.
name|getTimeInMillis
argument_list|()
decl_stmt|;
comment|//Get the normalized dateTime as a long (milliseconds since the Epoch)
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|offset
operator|+
literal|1
operator|+
literal|8
index|]
decl_stmt|;
comment|//allocate an appropriately sized byte array for holding Type,long
name|data
index|[
name|offset
index|]
operator|=
operator|(
name|byte
operator|)
name|Type
operator|.
name|DATE_TIME
expr_stmt|;
comment|//put the Type in the byte array
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|value
argument_list|,
name|data
argument_list|,
name|offset
operator|+
literal|1
argument_list|)
expr_stmt|;
comment|//put the long into the byte array
return|return
operator|(
name|data
operator|)
return|;
comment|//return the byte array
block|}
comment|/* (non-Javadoc)      * @see java.lang.Comparable#compareTo(java.lang.Object)      */
specifier|public
name|int
name|compareTo
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
specifier|final
name|AtomicValue
name|other
init|=
operator|(
name|AtomicValue
operator|)
name|o
decl_stmt|;
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
name|DATE_TIME
argument_list|)
condition|)
return|return
name|calendar
operator|.
name|compare
argument_list|(
operator|(
name|XMLGregorianCalendar
operator|)
name|o
argument_list|)
return|;
else|else
return|return
name|getType
argument_list|()
operator|>
name|other
operator|.
name|getType
argument_list|()
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
end_class

end_unit

