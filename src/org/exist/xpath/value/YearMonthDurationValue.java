begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|YearMonthDurationValue
extends|extends
name|DurationValue
block|{
specifier|public
name|YearMonthDurationValue
parameter_list|(
name|DurationValue
name|other
parameter_list|)
block|{
name|this
operator|.
name|year
operator|=
name|other
operator|.
name|year
expr_stmt|;
name|this
operator|.
name|month
operator|=
name|other
operator|.
name|month
expr_stmt|;
block|}
specifier|public
name|YearMonthDurationValue
parameter_list|(
name|String
name|str
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|()
expr_stmt|;
comment|// format is: [+|-]PnYnM
if|if
condition|(
name|str
operator|.
name|length
argument_list|()
operator|<
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: xs:duration should start with [+|-]P"
argument_list|)
throw|;
name|char
name|ch
decl_stmt|;
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
name|int
name|state
init|=
literal|0
decl_stmt|;
name|int
name|value
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
name|p
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|ch
condition|)
block|{
case|case
literal|'-'
case|:
if|if
condition|(
name|state
operator|>
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error in xs:yearMonthDuration: "
operator|+
name|str
operator|+
literal|": - is not allowed here"
argument_list|)
throw|;
name|negative
operator|=
literal|true
expr_stmt|;
break|break;
case|case
literal|'P'
case|:
if|if
condition|(
name|state
operator|>
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error in xs:yearMonthDuration: "
operator|+
name|str
operator|+
literal|": P is not allowed here"
argument_list|)
throw|;
name|state
operator|++
expr_stmt|;
break|break;
case|case
literal|'Y'
case|:
if|if
condition|(
name|state
operator|!=
literal|1
operator|||
name|value
operator|<
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error in xs:yearMonthDuration: "
operator|+
name|str
operator|+
literal|": Y is not allowed to occur here"
argument_list|)
throw|;
name|year
operator|=
name|value
expr_stmt|;
name|value
operator|=
operator|-
literal|1
expr_stmt|;
name|state
operator|++
expr_stmt|;
break|break;
case|case
literal|'M'
case|:
if|if
condition|(
name|state
operator|>
literal|2
operator|||
name|value
operator|<
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error in xs:yearMonthDuration: "
operator|+
name|str
operator|+
literal|": M is not allowed to occur here"
argument_list|)
throw|;
name|month
operator|=
name|value
expr_stmt|;
name|state
operator|++
expr_stmt|;
name|value
operator|=
operator|-
literal|1
expr_stmt|;
break|break;
case|case
literal|'1'
case|:
case|case
literal|'2'
case|:
case|case
literal|'3'
case|:
case|case
literal|'4'
case|:
case|case
literal|'5'
case|:
case|case
literal|'6'
case|:
case|case
literal|'7'
case|:
case|case
literal|'8'
case|:
case|case
literal|'9'
case|:
case|case
literal|'0'
case|:
if|if
condition|(
name|state
operator|<
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error in xs:yearMonthDuration: "
operator|+
name|str
operator|+
literal|": numeric value not allowed at this position"
argument_list|)
throw|;
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
name|p
operator|++
expr_stmt|;
while|while
condition|(
name|p
operator|<
name|str
operator|.
name|length
argument_list|()
condition|)
block|{
name|ch
operator|=
name|str
operator|.
name|charAt
argument_list|(
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|ch
operator|==
literal|'1'
operator|||
name|ch
operator|==
literal|'2'
operator|||
name|ch
operator|==
literal|'3'
operator|||
name|ch
operator|==
literal|'4'
operator|||
name|ch
operator|==
literal|'5'
operator|||
name|ch
operator|==
literal|'6'
operator|||
name|ch
operator|==
literal|'7'
operator|||
name|ch
operator|==
literal|'8'
operator|||
name|ch
operator|==
literal|'9'
operator|||
name|ch
operator|==
literal|'0'
condition|)
name|buf
operator|.
name|append
argument_list|(
name|ch
argument_list|)
expr_stmt|;
else|else
block|{
name|value
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|p
operator|--
expr_stmt|;
break|break;
block|}
name|p
operator|++
expr_stmt|;
block|}
break|break;
block|}
name|p
operator|++
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getValue
parameter_list|()
block|{
return|return
name|year
operator|*
literal|12
operator|+
name|month
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.DurationValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|YEAR_MONTH_DURATION
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.DurationValue#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|negative
condition|)
name|buf
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'P'
argument_list|)
expr_stmt|;
if|if
condition|(
name|year
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
name|year
argument_list|)
operator|.
name|append
argument_list|(
literal|'Y'
argument_list|)
expr_stmt|;
if|if
condition|(
name|month
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
name|month
argument_list|)
operator|.
name|append
argument_list|(
literal|'M'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.DurationValue#convertTo(int) 	 */
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
name|ITEM
case|:
case|case
name|Type
operator|.
name|ATOMIC
case|:
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
name|this
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
name|DURATION
case|:
return|return
operator|new
name|DurationValue
argument_list|(
name|this
argument_list|)
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot cast xs:yearMonthDuration to "
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
block|{
name|int
name|v1
init|=
name|getValue
argument_list|()
decl_stmt|;
name|int
name|v2
init|=
operator|(
operator|(
name|YearMonthDurationValue
operator|)
name|other
operator|)
operator|.
name|getValue
argument_list|()
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
name|v1
operator|==
name|v2
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|v1
operator|!=
name|v2
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
name|v1
operator|<
name|v2
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|v1
operator|<=
name|v2
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|v1
operator|>
name|v2
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|v1
operator|>=
name|v2
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown operator type in comparison"
argument_list|)
throw|;
block|}
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:yearMonthDuration to "
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
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
block|{
name|int
name|v1
init|=
name|getValue
argument_list|()
decl_stmt|;
name|int
name|v2
init|=
operator|(
operator|(
name|YearMonthDurationValue
operator|)
name|other
operator|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|v1
operator|==
name|v2
condition|)
return|return
literal|0
return|;
if|else if
condition|(
name|v1
operator|<
name|v2
condition|)
return|return
operator|-
literal|1
return|;
else|else
return|return
literal|1
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare xs:yearMonthDuration to "
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
return|return
name|compareTo
argument_list|(
name|other
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
name|compareTo
argument_list|(
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|)
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|other
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.AtomicValue#max(org.exist.xpath.value.AtomicValue) 	 */
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|YEAR_MONTH_DURATION
condition|)
return|return
name|compareTo
argument_list|(
name|other
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
name|compareTo
argument_list|(
name|other
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|YEAR_MONTH_DURATION
argument_list|)
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

