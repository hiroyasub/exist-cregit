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
name|XPathException
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FloatValue
extends|extends
name|NumericValue
block|{
specifier|public
specifier|final
specifier|static
name|FloatValue
name|NaN
init|=
operator|new
name|FloatValue
argument_list|(
name|Float
operator|.
name|NaN
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FloatValue
name|ZERO
init|=
operator|new
name|FloatValue
argument_list|(
literal|0.0E0f
argument_list|)
decl_stmt|;
specifier|protected
name|float
name|value
decl_stmt|;
specifier|public
name|FloatValue
parameter_list|(
name|float
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
specifier|public
name|FloatValue
parameter_list|(
name|String
name|stringValue
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
name|value
operator|=
name|Float
operator|.
name|parseFloat
argument_list|(
name|stringValue
argument_list|)
expr_stmt|;
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
literal|"cannot convert string '"
operator|+
name|stringValue
operator|+
literal|"' into a float"
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|Float
operator|.
name|toString
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#isNaN() 	 */
specifier|public
name|boolean
name|isNaN
parameter_list|()
block|{
return|return
name|value
operator|==
name|Float
operator|.
name|NaN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Sequence#convertTo(int) 	 */
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
name|NUMBER
case|:
case|case
name|Type
operator|.
name|FLOAT
case|:
return|return
name|this
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
name|DECIMAL
case|:
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
case|case
name|Type
operator|.
name|POSITIVE_INTEGER
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
name|long
operator|)
name|value
argument_list|,
name|requiredType
argument_list|)
return|;
case|case
name|Type
operator|.
name|BOOLEAN
case|:
return|return
operator|(
name|value
operator|==
literal|0.0
operator|&&
name|value
operator|==
name|Double
operator|.
name|NaN
operator|)
condition|?
name|BooleanValue
operator|.
name|FALSE
else|:
name|BooleanValue
operator|.
name|TRUE
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert double value '"
operator|+
name|value
operator|+
literal|"' into "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#negate() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|-
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|floor
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
operator|(
name|float
operator|)
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#minus(org.exist.xpath.value.NumericValue) 	 */
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|minus
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#plus(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|plus
parameter_list|(
name|ComputableValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|plus
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mult(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|mult
parameter_list|(
name|ComputableValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|*
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|mult
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#div(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|ComputableValue
name|div
parameter_list|(
name|ComputableValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|/
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|ComputableValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|div
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mod(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|mod
parameter_list|(
name|NumericValue
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|value
operator|%
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|NumericValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|mod
argument_list|(
name|other
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#abs() 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|FloatValue
argument_list|(
name|Math
operator|.
name|abs
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#max(org.exist.xpath.value.AtomicValue) 	 */
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|FloatValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|max
argument_list|(
name|other
argument_list|)
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
name|FLOAT
argument_list|)
condition|)
return|return
operator|new
name|FloatValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|FloatValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
argument_list|)
return|;
else|else
return|return
operator|(
operator|(
name|FloatValue
operator|)
name|convertTo
argument_list|(
name|other
operator|.
name|getType
argument_list|()
argument_list|)
operator|)
operator|.
name|min
argument_list|(
name|other
argument_list|)
return|;
block|}
block|}
end_class

end_unit

