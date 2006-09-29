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
name|math
operator|.
name|BigDecimal
import|;
end_import

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
name|FastStringBuffer
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
name|FloatingPointConverter
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
name|DoubleValue
extends|extends
name|NumericValue
block|{
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|ZERO
init|=
operator|new
name|DoubleValue
argument_list|(
literal|0.0E0
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|POSITIVE_INFINITY
init|=
operator|new
name|DoubleValue
argument_list|(
name|Double
operator|.
name|POSITIVE_INFINITY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|NEGATIVE_INFINITY
init|=
operator|new
name|DoubleValue
argument_list|(
name|Double
operator|.
name|NEGATIVE_INFINITY
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|DoubleValue
name|NaN
init|=
operator|new
name|DoubleValue
argument_list|(
name|Double
operator|.
name|NaN
argument_list|)
decl_stmt|;
specifier|private
name|double
name|value
decl_stmt|;
specifier|public
name|DoubleValue
parameter_list|(
name|double
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
name|DoubleValue
parameter_list|(
name|AtomicValue
name|otherValue
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|otherValue
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"INF"
argument_list|)
condition|)
name|value
operator|=
name|Double
operator|.
name|POSITIVE_INFINITY
expr_stmt|;
if|else if
condition|(
name|otherValue
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"-INF"
argument_list|)
condition|)
name|value
operator|=
name|Double
operator|.
name|NEGATIVE_INFINITY
expr_stmt|;
if|else if
condition|(
name|otherValue
operator|.
name|getStringValue
argument_list|()
operator|.
name|equals
argument_list|(
literal|"NaN"
argument_list|)
condition|)
name|value
operator|=
name|Double
operator|.
name|NaN
expr_stmt|;
else|else
name|value
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|otherValue
operator|.
name|getStringValue
argument_list|()
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
literal|"FORG0001: cannot construct "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
literal|" from '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|otherValue
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
literal|"(\""
operator|+
name|otherValue
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"\")'"
argument_list|)
throw|;
block|}
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
name|DOUBLE
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.Item#getStringValue() 	 */
comment|//	public String getStringValue() throws XPathException {
comment|//		return Double.toString(value);
comment|//	}
comment|//Copied from Saxon 8.6.1
specifier|static
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
name|nonExponentialPattern
init|=
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
operator|.
name|compile
argument_list|(
literal|"(-?[0-9])([0-9]+?)(0*)\\.([0-9]*)"
argument_list|)
decl_stmt|;
comment|//End of copy
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
comment|/* 		if (value == Float.POSITIVE_INFINITY) 			return "INF"; ; 		if (value == Float.NEGATIVE_INFINITY) 			return "-INF";		 		String s = String.valueOf(value); 		s = s.replaceAll("\\.0+$", "");		 		return s; 		/* 		int e = s.indexOf('E'); 		if (e == Constants.STRING_NOT_FOUND) { 			if (s.equals("Infinity")) { 				return "INF"; 			} else if (s.equals("-Infinity")) { 				return "-INF"; 			} 			// For some reason, Double.toString() in Java can return strings such as "0.0040" 			// so we remove any trailing zeros 			while (s.charAt(len - 1) == '0'&& s.charAt(len - 2) != '.') { 				s = s.substring(0, --len); 			} 			return s; 		} 		int exp = Integer.parseInt(s.substring(e + 1)); 		String sign; 		if (s.charAt(0) == '-') { 			sign = "-"; 			s = s.substring(1); 			--e; 		} else 			sign = ""; 		int nDigits = e - 2; 		if (exp>= nDigits) { 			return sign + s.substring(0, 1) + s.substring(2, e) + zeros(exp - nDigits); 		} else if (exp> 0) { 			return sign 				+ s.substring(0, 1) 				+ s.substring(2, 2 + exp) 				+ "." 				+ s.substring(2 + exp, e); 		} else { 			while (s.charAt(e - 1) == '0') 				e--; 			return sign + "0." + zeros(-1 - exp) + s.substring(0, 1) + s.substring(2, e); 		} 		*/
comment|/* 		String javaString = String.valueOf(value); 		//Copied from Saxon-B 8.6.1         if (value==0.0) {             if (javaString.charAt(0) == '-') {                 return "-0";             } else {                 return "0";             }         }         if (Double.isInfinite(value)) {             return (value> 0 ? "INF" : "-INF");         }         if (Double.isNaN(value)) {             return "NaN";         }         final double absval = Math.abs(value);         String s = javaString;         if (absval< 1.0e-6 || absval>= 1.0e+6) {             if (s.indexOf('E')<0) {                 // need to use scientific notation, but Java isn't using it                 // (Java's cutoff is 1.0E7, while XPath's is 1.0E6)                 // So we have for example -2000000.0 rather than -2.0e6                 FastStringBuffer sb = new FastStringBuffer(32);                 Matcher matcher = nonExponentialPattern.matcher(s);                 if (matcher.matches()) {                     sb.append(matcher.group(1));                     sb.append('.');                     sb.append(matcher.group(2));                     final String fraction = matcher.group(4);                     if ("0".equals(fraction)) {                         sb.append("E" + (matcher.group(2).length() + matcher.group(3).length()));                         return sb.toString();                     } else {                         sb.append(matcher.group(3));                         sb.append(matcher.group(4));                         sb.append("E" + (matcher.group(2).length() + matcher.group(3).length()));                         return sb.toString();                     }                 } else {                     // fallback, this shouldn't happen                     return s;                 }             } else {                 return s;             }         }         int len = s.length();         if (s.endsWith("E0")) {             s = s.substring(0, len - 2);         }         if (s.endsWith(".0")) {             return s.substring(0, len - 2);         }         int e = s.indexOf('E');         if (e< 0) {             // For some reason, Double.toString() in Java can return strings such as "0.0040"             // so we remove any trailing zeros             while (s.charAt(len - 1) == '0'&& s.charAt(len - 2) != '.') {                 s = s.substring(0, --len);             }             return s;         }         int exp = Integer.parseInt(s.substring(e + 1));         String sign;         if (s.charAt(0) == '-') {             sign = "-";             s = s.substring(1);             --e;         } else {             sign = "";         }         int nDigits = e - 2;         if (exp>= nDigits) {             return sign + s.substring(0, 1) + s.substring(2, e) + zeros(exp - nDigits);         } else if (exp> 0) {             return sign + s.substring(0, 1) + s.substring(2, 2 + exp) + '.' + s.substring(2 + exp, e);         } else {             while (s.charAt(e-1) == '0') e--;             return sign + "0." + zeros(-1 - exp) + s.substring(0, 1) + s.substring(2, e);         }         //End of copy          */
name|FastStringBuffer
name|sb
init|=
operator|new
name|FastStringBuffer
argument_list|(
literal|20
argument_list|)
decl_stmt|;
comment|//0 is a dummy parameter
name|FloatingPointConverter
operator|.
name|appendDouble
argument_list|(
name|sb
argument_list|,
name|value
argument_list|)
operator|.
name|getNormalizedString
argument_list|(
literal|0
argument_list|)
expr_stmt|;
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|static
specifier|private
name|String
name|zeros
parameter_list|(
name|int
name|n
parameter_list|)
block|{
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
name|n
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
name|n
condition|;
name|i
operator|++
control|)
name|buf
index|[
name|i
index|]
operator|=
literal|'0'
expr_stmt|;
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
specifier|public
name|double
name|getValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#isNaN() 	 */
specifier|public
name|boolean
name|isNaN
parameter_list|()
block|{
return|return
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|isInfinite
parameter_list|()
block|{
return|return
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
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
name|DOUBLE
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|FLOAT
case|:
comment|//if (Float.compare(value, 0.0f)&& (value< Float.MIN_VALUE || value> Float.MAX_VALUE)
comment|//	throw new XPathException("Value is out of range for type xs:float");
comment|//return new FloatValue((float) value);
return|return
operator|new
name|FloatValue
argument_list|(
operator|new
name|Float
argument_list|(
name|value
argument_list|)
operator|.
name|floatValue
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
if|if
condition|(
name|isNaN
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: can not convert "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"') to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
if|if
condition|(
name|isInfinite
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: can not convert "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"') to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
return|return
operator|new
name|DecimalValue
argument_list|(
operator|new
name|BigDecimal
argument_list|(
name|value
argument_list|)
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
if|if
condition|(
name|isNaN
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: can not convert "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"') to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: can not convert "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|"('"
operator|+
name|getStringValue
argument_list|()
operator|+
literal|"') to "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|requiredType
argument_list|)
argument_list|)
throw|;
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
operator|new
name|BooleanValue
argument_list|(
name|this
operator|.
name|effectiveBooleanValue
argument_list|()
argument_list|)
return|;
default|default :
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: cannot cast '"
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|this
operator|.
name|getItemType
argument_list|()
argument_list|)
operator|+
literal|"(\""
operator|+
name|getStringValue
argument_list|()
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.AtomicValue#effectiveBooleanValue() 	 */
specifier|public
name|boolean
name|effectiveBooleanValue
parameter_list|()
throws|throws
name|XPathException
block|{
if|if
condition|(
name|value
operator|==
literal|0.0
condition|)
return|return
literal|false
return|;
if|if
condition|(
name|isNaN
argument_list|()
condition|)
return|return
literal|false
return|;
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getDouble() 	 */
specifier|public
name|double
name|getDouble
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
name|value
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getInt() 	 */
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
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#getLong() 	 */
specifier|public
name|long
name|getLong
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|long
operator|)
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
return|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|double
name|val
parameter_list|)
block|{
name|value
operator|=
name|val
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#ceiling() 	 */
specifier|public
name|NumericValue
name|ceiling
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#floor() 	 */
specifier|public
name|NumericValue
name|floor
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|floor
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#round() 	 */
specifier|public
name|NumericValue
name|round
parameter_list|()
throws|throws
name|XPathException
block|{
comment|//Copied from Saxon
if|if
condition|(
name|Double
operator|.
name|isNaN
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
if|if
condition|(
name|Double
operator|.
name|isInfinite
argument_list|(
name|value
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
if|if
condition|(
name|value
operator|==
literal|0.0
condition|)
block|{
return|return
name|this
return|;
comment|// handles the negative zero case
block|}
if|if
condition|(
name|value
operator|>
operator|-
literal|0.5
operator|&&
name|value
operator|<
literal|0.0
condition|)
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
operator|-
literal|0.0
argument_list|)
return|;
block|}
if|if
condition|(
name|value
operator|>
name|Long
operator|.
name|MIN_VALUE
operator|&&
name|value
operator|<
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|round
argument_list|(
name|value
argument_list|)
argument_list|)
return|;
block|}
comment|// A double holds fewer significant digits than a long. Therefore,
comment|// if the double is outside the range of a long, it cannot have
comment|// any signficant digits after the decimal point. So in this
comment|// case, we return the original value unchanged
return|return
name|this
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#round(org.exist.xquery.value.IntegerValue) 	 */
specifier|public
name|NumericValue
name|round
parameter_list|(
name|IntegerValue
name|precision
parameter_list|)
throws|throws
name|XPathException
block|{
comment|/* use the decimal rounding method */
return|return
operator|(
name|DoubleValue
operator|)
operator|(
operator|(
name|DecimalValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|DECIMAL
argument_list|)
operator|)
operator|.
name|round
argument_list|(
name|precision
argument_list|)
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#minus(org.exist.xquery.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|-
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|minus
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#plus(org.exist.xquery.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|+
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|plus
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#mult(org.exist.xquery.value.NumericValue) 	 */
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
name|DOUBLE
case|:
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|*
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
case|case
name|Type
operator|.
name|DAY_TIME_DURATION
case|:
case|case
name|Type
operator|.
name|YEAR_MONTH_DURATION
case|:
return|return
name|other
operator|.
name|mult
argument_list|(
name|this
argument_list|)
return|;
default|default:
return|return
name|mult
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#div(org.exist.xquery.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|/
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|div
argument_list|(
operator|(
name|ComputableValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|IntegerValue
name|idiv
parameter_list|(
name|NumericValue
name|other
parameter_list|)
throws|throws
name|XPathException
block|{
name|ComputableValue
name|result
init|=
name|div
argument_list|(
name|other
argument_list|)
decl_stmt|;
return|return
operator|new
name|IntegerValue
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|result
operator|.
name|convertTo
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|)
operator|)
operator|.
name|getLong
argument_list|()
argument_list|)
return|;
comment|/* 		if (Type.subTypeOf(other.getType(), Type.DOUBLE)) { 			double result = value / ((DoubleValue) other).value; 			if (result == Double.NaN || result == Double.POSITIVE_INFINITY || result == Double.NEGATIVE_INFINITY) 				throw new XPathException("illegal arguments to idiv"); 			return new IntegerValue(new BigDecimal(result).toBigInteger(), Type.INTEGER); 		} 		throw new XPathException("idiv called with incompatible argument type: " + getType() + " vs " + other.getType()); 		*/
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#mod(org.exist.xquery.value.NumericValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|value
operator|%
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
else|else
return|return
name|mod
argument_list|(
operator|(
name|NumericValue
operator|)
name|other
operator|.
name|convertTo
argument_list|(
name|getType
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#negate() 	 */
specifier|public
name|NumericValue
name|negate
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
argument_list|(
operator|-
name|value
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#abs() 	 */
specifier|public
name|NumericValue
name|abs
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|new
name|DoubleValue
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.value.NumericValue#max(org.exist.xquery.value.AtomicValue) 	 */
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|max
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
argument_list|)
return|;
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
name|DOUBLE
argument_list|)
condition|)
return|return
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
operator|new
name|DoubleValue
argument_list|(
name|Math
operator|.
name|min
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
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
argument_list|)
return|;
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
name|DoubleValue
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
literal|3
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
literal|4
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
literal|5
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
literal|6
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
literal|1
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
literal|2
return|;
if|if
condition|(
name|javaClass
operator|==
name|String
operator|.
name|class
condition|)
return|return
literal|7
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
literal|8
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
name|DoubleValue
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
name|Double
operator|.
name|class
operator|||
name|target
operator|==
name|double
operator|.
name|class
condition|)
return|return
operator|new
name|Double
argument_list|(
name|value
argument_list|)
return|;
if|else if
condition|(
name|target
operator|==
name|Float
operator|.
name|class
operator|||
name|target
operator|==
name|float
operator|.
name|class
condition|)
return|return
operator|new
name|Float
argument_list|(
name|value
argument_list|)
return|;
if|else if
condition|(
name|target
operator|==
name|Long
operator|.
name|class
operator|||
name|target
operator|==
name|long
operator|.
name|class
condition|)
block|{
return|return
operator|new
name|Long
argument_list|(
operator|(
operator|(
name|IntegerValue
operator|)
name|convertTo
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
operator|)
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
name|Integer
operator|.
name|class
operator|||
name|target
operator|==
name|int
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
operator|(
name|int
operator|)
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
name|Short
operator|.
name|class
operator|||
name|target
operator|==
name|short
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
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|Byte
operator|.
name|class
operator|||
name|target
operator|==
name|byte
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
name|getValue
argument_list|()
argument_list|)
return|;
block|}
if|else if
condition|(
name|target
operator|==
name|String
operator|.
name|class
condition|)
return|return
name|getStringValue
argument_list|()
return|;
if|else if
condition|(
name|target
operator|==
name|Boolean
operator|.
name|class
condition|)
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|effectiveBooleanValue
argument_list|()
argument_list|)
return|;
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
comment|/** size writen by {@link #serialize(short, boolean)} */
specifier|public
name|int
name|getSerializedSize
parameter_list|()
block|{
return|return
literal|1
operator|+
literal|8
return|;
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
name|DOUBLE
argument_list|)
condition|)
return|return
name|Double
operator|.
name|compare
argument_list|(
name|value
argument_list|,
operator|(
operator|(
name|DoubleValue
operator|)
name|other
operator|)
operator|.
name|value
argument_list|)
return|;
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

