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
name|IntegerValue
extends|extends
name|NumericValue
block|{
specifier|public
specifier|final
specifier|static
name|IntegerValue
name|ZERO
init|=
operator|new
name|IntegerValue
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|private
name|long
name|value
decl_stmt|;
specifier|public
name|IntegerValue
parameter_list|(
name|long
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
name|IntegerValue
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
name|Long
operator|.
name|parseLong
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
literal|"failed to convert '"
operator|+
name|stringValue
operator|+
literal|"' to an integer"
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
name|INTEGER
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
specifier|public
name|long
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|long
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
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|value
argument_list|)
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
name|NUMBER
case|:
case|case
name|Type
operator|.
name|INTEGER
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
name|DECIMAL
case|:
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
name|BOOLEAN
case|:
return|return
operator|(
name|value
operator|==
literal|0
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
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert integer '"
operator|+
name|value
operator|+
literal|"' to "
operator|+
name|requiredType
argument_list|)
throw|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getInt() 	 */
specifier|public
name|int
name|getInt
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|int
operator|)
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|double
operator|)
name|value
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
operator|!=
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
block|{
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#minus(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|minus
parameter_list|(
name|NumericValue
name|other
parameter_list|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#plus(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|plus
parameter_list|(
name|NumericValue
name|other
parameter_list|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#mult(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|mult
parameter_list|(
name|NumericValue
name|other
parameter_list|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|*
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xpath.value.NumericValue#div(org.exist.xpath.value.NumericValue) 	 */
specifier|public
name|NumericValue
name|div
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
name|long
name|value
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
if|if
condition|(
name|value
operator|<
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"division by zero"
argument_list|)
throw|;
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|/
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
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
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
operator|%
operator|(
operator|(
name|IntegerValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
block|}
block|}
end_class

end_unit

