begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|io
operator|.
name|UnsupportedEncodingException
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
name|apache
operator|.
name|xmlrpc
operator|.
name|Base64
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
name|HexBinary
extends|extends
name|BinaryValue
block|{
specifier|public
name|HexBinary
parameter_list|(
name|byte
index|[]
name|data
parameter_list|)
block|{
name|super
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
specifier|public
name|HexBinary
parameter_list|(
name|String
name|in
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
operator|(
name|in
operator|.
name|length
argument_list|()
operator|&
literal|1
operator|)
operator|!=
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: A hexBinary value must contain an even "
operator|+
literal|"number of characters"
argument_list|)
throw|;
block|}
name|data
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|length
argument_list|()
operator|/
literal|2
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|data
index|[
name|i
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|fromHex
argument_list|(
name|in
operator|.
name|charAt
argument_list|(
literal|2
operator|*
name|i
argument_list|)
argument_list|)
operator|<<
literal|4
operator|)
operator|+
operator|(
name|fromHex
argument_list|(
name|in
operator|.
name|charAt
argument_list|(
literal|2
operator|*
name|i
operator|+
literal|1
argument_list|)
argument_list|)
operator|)
operator|)
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|HEX_BINARY
return|;
block|}
comment|/**      * Decode a single hex digit      * @param c the hex digit      * @return the numeric value of the hex digit      * @throws XPathException if it isn't a hex digit      */
specifier|private
name|int
name|fromHex
parameter_list|(
name|char
name|c
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|d
init|=
literal|"0123456789ABCDEFabcdef"
operator|.
name|indexOf
argument_list|(
name|c
argument_list|)
decl_stmt|;
if|if
condition|(
name|d
operator|>
literal|15
condition|)
block|{
name|d
operator|=
name|d
operator|-
literal|6
expr_stmt|;
block|}
if|if
condition|(
name|d
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0001: Invalid hexadecimal digit: "
operator|+
name|c
argument_list|)
throw|;
block|}
return|return
name|d
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
name|HEX_BINARY
case|:
return|return
name|this
return|;
case|case
name|Type
operator|.
name|BASE64_BINARY
case|:
return|return
operator|new
name|Base64Binary
argument_list|(
name|data
argument_list|)
return|;
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
comment|//Added trim() since it looks like a new line character is added
return|return
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
operator|.
name|trim
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
default|default:
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"cannot convert "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" to "
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
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
name|String
name|digits
init|=
literal|"0123456789ABCDEF"
decl_stmt|;
name|StringBuffer
name|sb
init|=
operator|new
name|StringBuffer
argument_list|(
name|data
operator|.
name|length
operator|*
literal|2
argument_list|)
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
name|data
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|digits
operator|.
name|charAt
argument_list|(
operator|(
name|data
index|[
name|i
index|]
operator|>>
literal|4
operator|)
operator|&
literal|0xf
argument_list|)
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|digits
operator|.
name|charAt
argument_list|(
name|data
index|[
name|i
index|]
operator|&
literal|0xf
argument_list|)
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
name|HexBinary
operator|.
name|class
argument_list|)
condition|)
return|return
name|this
return|;
if|if
condition|(
name|target
operator|.
name|isArray
argument_list|()
operator|&&
name|target
operator|==
name|Byte
operator|.
name|class
condition|)
return|return
name|data
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
block|}
end_class

end_unit

