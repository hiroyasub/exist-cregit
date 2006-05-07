begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
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
name|XPathException
import|;
end_import

begin_class
specifier|public
class|class
name|BooleanValue
extends|extends
name|AtomicValue
block|{
specifier|public
specifier|final
specifier|static
name|BooleanValue
name|TRUE
init|=
operator|new
name|BooleanValue
argument_list|(
literal|true
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|BooleanValue
name|FALSE
init|=
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|value
decl_stmt|;
comment|/** 	 * Returns one of the static fields TRUE or FALSE depending on 	 * the value of the parameter. 	 *  	 * @param bool 	 * @return 	 */
specifier|public
specifier|final
specifier|static
name|BooleanValue
name|valueOf
parameter_list|(
name|boolean
name|bool
parameter_list|)
block|{
return|return
name|bool
condition|?
name|TRUE
else|:
name|FALSE
return|;
block|}
specifier|public
name|BooleanValue
parameter_list|(
name|boolean
name|bool
parameter_list|)
block|{
name|value
operator|=
name|bool
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#getType() 	 */
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|BOOLEAN
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#getStringValue() 	 */
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
condition|?
literal|"true"
else|:
literal|"false"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#convertTo(int) 	 */
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
name|BOOLEAN
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
name|NUMBER
case|:
case|case
name|Type
operator|.
name|INTEGER
case|:
return|return
operator|new
name|IntegerValue
argument_list|(
name|value
condition|?
literal|1
else|:
literal|0
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
condition|?
literal|1
else|:
literal|0
argument_list|)
return|;
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
condition|?
literal|1
else|:
literal|0
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
condition|?
literal|1
else|:
literal|0
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
literal|"cannot convert 'xs:boolean("
operator|+
name|value
operator|+
literal|")' to "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(int, org.exist.xquery.value.AtomicValue) 	 */
specifier|public
name|boolean
name|compareTo
parameter_list|(
name|Collator
name|collator
parameter_list|,
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
name|BOOLEAN
argument_list|)
condition|)
block|{
name|boolean
name|otherVal
init|=
operator|(
operator|(
name|BooleanValue
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
name|value
operator|==
name|otherVal
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|value
operator|!=
name|otherVal
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
operator|(
operator|!
name|value
operator|)
operator|&&
name|otherVal
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|value
operator|==
name|otherVal
operator|||
operator|(
operator|!
name|value
operator|)
operator|&&
name|otherVal
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|value
operator|&&
operator|(
operator|!
name|otherVal
operator|)
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|value
operator|==
name|otherVal
operator|||
name|value
operator|&&
operator|(
operator|!
name|otherVal
operator|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot apply this operator to a boolean value"
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot convert operand to boolean"
argument_list|)
throw|;
block|}
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
name|boolean
name|otherVal
init|=
name|other
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|otherVal
operator|==
name|value
condition|)
return|return
name|Constants
operator|.
name|EQUAL
return|;
if|else if
condition|(
name|value
condition|)
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
else|else
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
return|;
block|}
specifier|public
name|boolean
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#max(org.exist.xquery.value.AtomicValue) 	 */
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
name|boolean
name|otherValue
init|=
operator|(
operator|(
name|BooleanValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
return|return
name|value
operator|&&
operator|(
operator|!
name|otherValue
operator|)
condition|?
name|this
else|:
name|other
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid argument to aggregate function: expected boolean, got: "
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
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
name|boolean
name|otherValue
init|=
operator|(
operator|(
name|BooleanValue
operator|)
name|other
operator|)
operator|.
name|value
decl_stmt|;
return|return
operator|(
operator|!
name|value
operator|)
operator|&&
name|otherValue
condition|?
name|this
else|:
name|other
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid argument to aggregate function: expected boolean, got: "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#conversionPreference(java.lang.Class) 	 */
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
name|BooleanValue
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
literal|1
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
literal|2
return|;
return|return
name|Integer
operator|.
name|MAX_VALUE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#toJavaObject(java.lang.Class) 	 */
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
name|BooleanValue
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
name|Boolean
operator|.
name|class
operator|||
name|target
operator|==
name|boolean
operator|.
name|class
operator|||
name|target
operator|==
name|Object
operator|.
name|class
condition|)
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
return|;
if|else if
condition|(
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
name|StringValue
name|v
init|=
operator|(
name|StringValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
return|return
name|v
operator|.
name|value
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
name|BOOLEAN
argument_list|)
condition|)
block|{
if|if
condition|(
name|value
operator|==
operator|(
operator|(
name|BooleanValue
operator|)
name|other
operator|)
operator|.
name|value
condition|)
return|return
name|Constants
operator|.
name|EQUAL
return|;
if|else if
condition|(
name|value
condition|)
return|return
name|Constants
operator|.
name|SUPERIOR
return|;
else|else
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
else|else
return|return
name|getType
argument_list|()
operator|<
name|other
operator|.
name|getType
argument_list|()
condition|?
name|Constants
operator|.
name|INFERIOR
else|:
name|Constants
operator|.
name|SUPERIOR
return|;
block|}
block|}
end_class

end_unit

