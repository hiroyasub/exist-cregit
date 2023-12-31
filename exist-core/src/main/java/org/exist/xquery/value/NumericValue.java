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
name|com
operator|.
name|ibm
operator|.
name|icu
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

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|IntSupplier
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
specifier|abstract
name|boolean
name|isNegative
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isPositive
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
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
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|compareTo
parameter_list|(
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
name|Comparison
name|operator
parameter_list|,
specifier|final
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
name|subTypeOfUnion
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
name|Comparison
operator|.
name|NEQ
return|;
block|}
block|}
specifier|final
name|IntSupplier
name|comparison
init|=
name|createComparisonWith
argument_list|(
operator|(
name|NumericValue
operator|)
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Type error: cannot apply operator to numeric value"
argument_list|)
throw|;
block|}
switch|switch
condition|(
name|operator
condition|)
block|{
case|case
name|EQ
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|==
literal|0
return|;
case|case
name|NEQ
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|!=
literal|0
return|;
case|case
name|GT
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|>
literal|0
return|;
case|case
name|GTEQ
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|>=
literal|0
return|;
case|case
name|LT
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|<
literal|0
return|;
case|case
name|LTEQ
case|:
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
operator|<=
literal|0
return|;
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Type error: cannot apply operator to numeric value"
argument_list|)
throw|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
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
comment|/**      * Creates a function which when called performs a comparison between this NumericValue      * and the {@code other} NumericValue.      *      * @param other the other numberic value to compare this against.      *      * @return the comparison function or null.      */
specifier|protected
specifier|abstract
annotation|@
name|Nullable
name|IntSupplier
name|createComparisonWith
parameter_list|(
specifier|final
name|NumericValue
name|other
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
specifier|final
name|int
name|compareTo
parameter_list|(
specifier|final
name|Collator
name|collator
parameter_list|,
specifier|final
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
name|Constants
operator|.
name|INFERIOR
return|;
block|}
if|if
condition|(
name|Type
operator|.
name|subTypeOfUnion
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
name|Constants
operator|.
name|INFERIOR
return|;
block|}
block|}
specifier|final
name|IntSupplier
name|comparison
init|=
name|createComparisonWith
argument_list|(
operator|(
name|NumericValue
operator|)
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|comparison
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Type error: cannot apply operator to numeric value"
argument_list|)
throw|;
block|}
return|return
name|comparison
operator|.
name|getAsInt
argument_list|()
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
name|XPTY0004
argument_list|,
literal|"cannot compare numeric value to non-numeric value"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|NumericValue
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|obj
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
try|try
block|{
return|return
name|compareTo
argument_list|(
literal|null
argument_list|,
name|Comparison
operator|.
name|EQ
argument_list|,
operator|(
name|NumericValue
operator|)
name|obj
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
comment|// should not be possible due to type check
block|}
return|return
literal|false
return|;
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
block|}
end_class

end_unit

