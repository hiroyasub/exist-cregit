begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
specifier|abstract
class|class
name|NumericValue
extends|extends
name|ComputableValue
block|{
specifier|public
specifier|abstract
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|AtomicValue
name|convertTo
parameter_list|(
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
operator|(
name|DoubleValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
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
operator|(
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getValue
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
name|boolean
name|hasFractionalPart
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isNaN
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isInfinite
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isZero
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//If its operand is a singleton value of any numeric type or derived from a numeric type,
comment|//fn:boolean returns false if the operand value is NaN or is numerically equal to zero;
comment|//otherwise it returns true.
return|return
operator|!
operator|(
name|isNaN
argument_list|()
operator|||
name|isZero
argument_list|()
operator|)
return|;
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
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//Never equal, or inequal...
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
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
name|isNaN
argument_list|()
condition|)
block|{
comment|//NaN does not equal itself.
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
block|{
return|return
name|operator
operator|==
name|Constants
operator|.
name|NEQ
return|;
block|}
block|}
name|double
name|otherVal
init|=
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|double
name|val
init|=
name|getDouble
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
name|val
operator|==
name|otherVal
return|;
case|case
name|Constants
operator|.
name|NEQ
case|:
return|return
name|val
operator|!=
name|otherVal
return|;
case|case
name|Constants
operator|.
name|GT
case|:
return|return
name|val
operator|>
name|otherVal
return|;
case|case
name|Constants
operator|.
name|GTEQ
case|:
return|return
name|val
operator|>=
name|otherVal
return|;
case|case
name|Constants
operator|.
name|LT
case|:
return|return
name|val
operator|<
name|otherVal
return|;
case|case
name|Constants
operator|.
name|LTEQ
case|:
return|return
name|val
operator|<=
name|otherVal
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot apply operator to numeric value"
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Type error: cannot compare operands: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" and "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#compareTo(org.exist.xquery.value.AtomicValue) 	 */
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
name|NUMBER
argument_list|)
condition|)
block|{
if|if
condition|(
name|isNaN
argument_list|()
condition|)
block|{
comment|//NaN does not equal itself.
if|if
condition|(
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|isNaN
argument_list|()
condition|)
return|return
name|Constants
operator|.
name|INFERIOR
return|;
block|}
name|double
name|otherVal
init|=
operator|(
operator|(
name|NumericValue
operator|)
name|other
operator|)
operator|.
name|getDouble
argument_list|()
decl_stmt|;
name|double
name|val
init|=
name|getDouble
argument_list|()
decl_stmt|;
if|if
condition|(
name|val
operator|==
name|otherVal
condition|)
return|return
name|Constants
operator|.
name|EQUAL
return|;
if|else if
condition|(
name|val
operator|>
name|otherVal
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot compare numeric value to non-numeric value"
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|abstract
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|ceiling
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|floor
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|round
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|round
parameter_list|(
name|IntegerValue
name|precision
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|mod
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|//TODO : implement here ?
specifier|public
specifier|abstract
name|IntegerValue
name|idiv
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
function_decl|;
specifier|public
specifier|abstract
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
function_decl|;
specifier|public
specifier|abstract
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
function_decl|;
block|}
end_class

end_unit

