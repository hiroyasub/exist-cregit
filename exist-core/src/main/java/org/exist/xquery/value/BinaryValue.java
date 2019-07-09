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
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|CloseShieldOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|XPathException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:adam@existsolutions.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BinaryValue
extends|extends
name|AtomicValue
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|BinaryValue
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
name|int
name|READ_BUFFER_SIZE
init|=
literal|16
operator|*
literal|1024
decl_stmt|;
comment|//16kb
specifier|private
specifier|final
name|BinaryValueManager
name|binaryValueManager
decl_stmt|;
specifier|private
specifier|final
name|BinaryValueType
name|binaryValueType
decl_stmt|;
specifier|protected
name|BinaryValue
parameter_list|(
name|BinaryValueManager
name|binaryValueManager
parameter_list|,
name|BinaryValueType
name|binaryValueType
parameter_list|)
block|{
name|this
operator|.
name|binaryValueManager
operator|=
name|binaryValueManager
expr_stmt|;
name|this
operator|.
name|binaryValueType
operator|=
name|binaryValueType
expr_stmt|;
block|}
specifier|protected
specifier|final
name|BinaryValueManager
name|getManager
parameter_list|()
block|{
return|return
name|binaryValueManager
return|;
block|}
specifier|protected
name|BinaryValueType
name|getBinaryValueType
parameter_list|()
block|{
return|return
name|binaryValueType
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|getBinaryValueType
argument_list|()
operator|.
name|getXQueryType
argument_list|()
return|;
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
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|HEX_BINARY
operator|||
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BASE64_BINARY
condition|)
block|{
specifier|final
name|int
name|value
init|=
name|compareTo
argument_list|(
operator|(
name|BinaryValue
operator|)
name|other
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
name|value
operator|==
literal|0
return|;
case|case
name|NEQ
case|:
return|return
name|value
operator|!=
literal|0
return|;
case|case
name|GT
case|:
return|return
name|value
operator|>
literal|0
return|;
case|case
name|GTEQ
case|:
return|return
name|value
operator|>=
literal|0
return|;
case|case
name|LT
case|:
return|return
name|value
operator|<
literal|0
return|;
case|case
name|LTEQ
case|:
return|return
name|value
operator|<=
literal|0
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
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot compare value of type xs:hexBinary with "
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
if|if
condition|(
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|HEX_BINARY
operator|||
name|other
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BASE64_BINARY
condition|)
block|{
return|return
name|compareTo
argument_list|(
operator|(
name|BinaryValue
operator|)
name|other
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot compare value of type xs:hexBinary with "
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
specifier|private
name|int
name|compareTo
parameter_list|(
name|BinaryValue
name|otherValue
parameter_list|)
block|{
specifier|final
name|InputStream
name|is
init|=
name|getInputStream
argument_list|()
decl_stmt|;
specifier|final
name|InputStream
name|otherIs
init|=
name|otherValue
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
if|if
condition|(
name|is
operator|==
literal|null
operator|&&
name|otherIs
operator|==
literal|null
condition|)
block|{
return|return
literal|0
return|;
block|}
if|else if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|else if
condition|(
name|otherIs
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|otherRead
init|=
operator|-
literal|1
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
try|try
block|{
name|read
operator|=
name|is
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
return|return
operator|-
literal|1
return|;
block|}
try|try
block|{
name|otherRead
operator|=
name|otherIs
operator|.
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
return|return
literal|1
return|;
block|}
return|return
name|read
operator|-
name|otherRead
return|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|(
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
name|getClass
argument_list|()
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
if|if
condition|(
name|target
operator|==
name|byte
index|[]
operator|.
name|class
condition|)
block|{
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|streamBinaryTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
return|return
operator|(
name|T
operator|)
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to Stream BinaryValue to byte[]: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot convert value of type "
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
comment|/**      * Return the underlying Java object for this binary value. Might be a File or byte[].      *      * @param<T> either File or byte[]      * @return the value converted to a corresponding java object      * @throws XPathException in case of dynamic error      */
specifier|public
parameter_list|<
name|T
parameter_list|>
name|T
name|toJavaObject
parameter_list|()
throws|throws
name|XPathException
block|{
return|return
operator|(
name|T
operator|)
name|toJavaObject
argument_list|(
name|byte
index|[]
operator|.
expr|class
argument_list|)
return|;
block|}
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot compare values of type "
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Cannot compare values of type "
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
annotation|@
name|Override
specifier|public
name|AtomicValue
name|convertTo
parameter_list|(
specifier|final
name|int
name|requiredType
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|AtomicValue
name|result
decl_stmt|;
if|if
condition|(
name|requiredType
operator|==
name|getType
argument_list|()
operator|||
name|requiredType
operator|==
name|Type
operator|.
name|ITEM
operator|||
name|requiredType
operator|==
name|Type
operator|.
name|ATOMIC
condition|)
block|{
name|result
operator|=
name|this
expr_stmt|;
block|}
else|else
block|{
switch|switch
condition|(
name|requiredType
condition|)
block|{
case|case
name|Type
operator|.
name|BASE64_BINARY
case|:
name|result
operator|=
name|convertTo
argument_list|(
operator|new
name|Base64BinaryValueType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|HEX_BINARY
case|:
name|result
operator|=
name|convertTo
argument_list|(
operator|new
name|HexBinaryValueType
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|UNTYPED_ATOMIC
case|:
comment|//TODO still needed? Added trim() since it looks like a new line character is added
name|result
operator|=
operator|new
name|UntypedAtomicValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
case|case
name|Type
operator|.
name|STRING
case|:
comment|//TODO still needed? Added trim() since it looks like a new line character is added
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
break|break;
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
return|return
name|result
return|;
block|}
specifier|public
specifier|abstract
name|BinaryValue
name|convertTo
parameter_list|(
name|BinaryValueType
name|binaryValueType
parameter_list|)
throws|throws
name|XPathException
function_decl|;
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
name|isArray
argument_list|()
operator|&&
name|javaClass
operator|.
name|isInstance
argument_list|(
name|Byte
operator|.
name|class
argument_list|)
condition|)
block|{
return|return
literal|0
return|;
block|}
return|return
name|Integer
operator|.
name|MAX_VALUE
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"FORG0006: value of type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|getType
argument_list|()
argument_list|)
operator|+
literal|" has no boolean value."
argument_list|)
throw|;
block|}
comment|//TODO ideally this should be moved out into serialization where we can stream the output from the buf/channel by calling streamTo()
annotation|@
name|Override
specifier|public
name|String
name|getStringValue
parameter_list|()
throws|throws
name|XPathException
block|{
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
decl_stmt|;
try|try
block|{
name|streamTo
argument_list|(
name|baos
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unable to encode string value: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
finally|finally
block|{
try|try
block|{
name|baos
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//close the stream to ensure all data is flushed
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to close stream: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|baos
operator|.
name|toString
argument_list|(
name|UTF_8
argument_list|)
return|;
block|}
comment|/**      * Streams the raw binary data      *      * @param os the output to stream to      * @throws IOException if an error occurs while writing to the stream      */
specifier|public
specifier|abstract
name|void
name|streamBinaryTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Streams the encoded binary data      * @param os the output to stream to      * @throws IOException if an error occurs while writing to the stream      */
specifier|public
name|void
name|streamTo
parameter_list|(
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
comment|//we need to create a safe output stream that cannot be closed
specifier|final
name|OutputStream
name|safeOutputStream
init|=
operator|new
name|CloseShieldOutputStream
argument_list|(
name|os
argument_list|)
decl_stmt|;
comment|//get the encoder
specifier|final
name|FilterOutputStream
name|fos
init|=
name|getBinaryValueType
argument_list|()
operator|.
name|getEncoder
argument_list|(
name|safeOutputStream
argument_list|)
decl_stmt|;
comment|//stream with the encoder
name|streamBinaryTo
argument_list|(
name|fos
argument_list|)
expr_stmt|;
comment|//we do have to close the encoders output stream though
comment|//to ensure that all bytes have been written, this is
comment|//particularly nessecary for Apache Commons Codec stream encoders
try|try
block|{
name|fos
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to close stream: {}"
argument_list|,
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|abstract
name|InputStream
name|getInputStream
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|isClosed
parameter_list|()
function_decl|;
comment|/**      * Increments the number of shared references to this binary value.      */
specifier|public
specifier|abstract
name|void
name|incrementSharedReferences
parameter_list|()
function_decl|;
block|}
end_class

end_unit

