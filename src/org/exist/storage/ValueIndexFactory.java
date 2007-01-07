begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|DatatypeConfigurationException
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
name|DatatypeFactory
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
name|org
operator|.
name|apache
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|util
operator|.
name|UTF8
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
name|value
operator|.
name|AbstractDateTimeValue
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
name|value
operator|.
name|BooleanValue
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
name|value
operator|.
name|DateTimeValue
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
name|value
operator|.
name|DoubleValue
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
name|value
operator|.
name|FloatValue
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
name|value
operator|.
name|IntegerValue
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
name|value
operator|.
name|StringValue
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
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_comment
comment|//TODO : rename as NativeIndexValueFactory ? -pb
end_comment

begin_class
specifier|public
class|class
name|ValueIndexFactory
block|{
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|ValueIndexFactory
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//TODO : check
specifier|public
specifier|static
name|int
name|OFFSET_COLLECTION_ID
init|=
literal|0
decl_stmt|;
comment|//TODO : check
specifier|public
specifier|static
name|int
name|OFFSET_TYPE
init|=
name|OFFSET_COLLECTION_ID
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
decl_stmt|;
comment|//2
specifier|public
specifier|static
name|int
name|LENGTH_VALUE_TYPE
init|=
literal|1
decl_stmt|;
comment|//sizeof byte
specifier|public
specifier|static
name|int
name|OFFSET_VALUE
init|=
name|OFFSET_TYPE
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
decl_stmt|;
comment|//3
specifier|public
specifier|final
specifier|static
name|Indexable
name|deserialize
parameter_list|(
name|byte
index|[]
name|data
parameter_list|,
name|int
name|start
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|EXistException
block|{
name|int
name|type
init|=
name|data
index|[
name|start
operator|+
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
index|]
decl_stmt|;
comment|//TODO : improve deserialization (use static methods in the org.exist.xquery.Value package
comment|/* xs:string */
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
name|String
name|s
decl_stmt|;
try|try
block|{
name|s
operator|=
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|,
name|len
operator|-
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|UnsupportedEncodingException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|EXistException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
operator|new
name|StringValue
argument_list|(
name|s
argument_list|)
return|;
block|}
comment|/* xs:dateTime */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|DATE_TIME
argument_list|)
condition|)
block|{
comment|//get the dateTime back as a long
name|long
name|value
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data
argument_list|,
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|)
decl_stmt|;
comment|//Create a GregorianCalendar from the long (normalized datetime as milliseconds since the Epoch)
name|GregorianCalendar
name|utccal
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|utccal
operator|.
name|setTimeInMillis
argument_list|(
name|value
argument_list|)
expr_stmt|;
comment|//Create a XMLGregorianCalendar from the GregorianCalendar
try|try
block|{
name|XMLGregorianCalendar
name|xmlutccal
init|=
name|DatatypeFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|utccal
argument_list|)
decl_stmt|;
return|return
operator|new
name|DateTimeValue
argument_list|(
name|xmlutccal
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DatatypeConfigurationException
name|dtce
parameter_list|)
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Could not deserialize xs:dateTime data type for range index key: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
operator|+
literal|" - "
operator|+
name|dtce
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
block|}
comment|/* xs:integer */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
condition|)
block|{
return|return
operator|new
name|IntegerValue
argument_list|(
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data
argument_list|,
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|)
operator|^
literal|0x8000000000000000L
argument_list|)
return|;
block|}
comment|/* xs:double */
if|else if
condition|(
name|type
operator|==
name|Type
operator|.
name|DOUBLE
condition|)
block|{
name|long
name|bits
init|=
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|data
argument_list|,
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|)
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|double
name|d
init|=
name|Double
operator|.
name|longBitsToDouble
argument_list|(
name|bits
argument_list|)
decl_stmt|;
return|return
operator|new
name|DoubleValue
argument_list|(
name|d
argument_list|)
return|;
block|}
comment|/* xs:float */
if|else if
condition|(
name|type
operator|==
name|Type
operator|.
name|FLOAT
condition|)
block|{
name|int
name|bits
init|=
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
argument_list|,
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
argument_list|)
operator|^
literal|0x80000000
decl_stmt|;
name|float
name|f
init|=
name|Float
operator|.
name|intBitsToFloat
argument_list|(
name|bits
argument_list|)
decl_stmt|;
return|return
operator|new
name|FloatValue
argument_list|(
name|f
argument_list|)
return|;
block|}
comment|/* xs:boolean */
if|else if
condition|(
name|type
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
return|return
operator|new
name|BooleanValue
argument_list|(
name|data
index|[
name|start
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
index|]
operator|==
literal|1
argument_list|)
return|;
block|}
comment|/* unknown! */
else|else
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unknown data type for deserialization: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|serialize
parameter_list|(
name|Indexable
name|value
parameter_list|,
name|short
name|collectionId
parameter_list|)
throws|throws
name|EXistException
block|{
comment|//TODO : refactor (only strings are case sensitive)
return|return
name|serialize
argument_list|(
name|value
argument_list|,
name|collectionId
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** 	 * @deprecated 	 * @param value 	 * @param collectionId 	 * @param caseSensitive 	 * @throws EXistException 	 */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|serialize
parameter_list|(
name|Indexable
name|value
parameter_list|,
name|short
name|collectionId
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
throws|throws
name|EXistException
block|{
comment|/* xs:string */
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
specifier|final
name|String
name|val
init|=
name|caseSensitive
condition|?
operator|(
operator|(
name|StringValue
operator|)
name|value
operator|)
operator|.
name|getStringValue
argument_list|()
else|:
operator|(
operator|(
name|StringValue
operator|)
name|value
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|UTF8
operator|.
name|encoded
argument_list|(
name|val
argument_list|)
operator|+
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
operator|(
name|byte
operator|)
name|value
operator|.
name|getType
argument_list|()
expr_stmt|;
comment|// TODO: cast to byte is not safe
name|UTF8
operator|.
name|encode
argument_list|(
name|val
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:dateTime */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|DATE_TIME
argument_list|)
condition|)
block|{
name|GregorianCalendar
name|utccal
init|=
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|value
operator|)
operator|.
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
name|millis
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
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
operator|+
literal|8
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
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
comment|//put the collectionId in the byte array
comment|//TODO : should we keep the actual type, i.e. value.getType() ?
name|data
index|[
name|OFFSET_TYPE
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
name|millis
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
comment|//put the long in the byte array
return|return
operator|(
name|data
operator|)
return|;
block|}
comment|/* xs:integer */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
condition|)
block|{
name|long
name|l
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
operator|-
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
operator|+
literal|8
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
operator|(
name|byte
operator|)
name|Type
operator|.
name|INTEGER
expr_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|l
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:double */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOUBLE
condition|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
operator|+
literal|8
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
operator|(
name|byte
operator|)
name|Type
operator|.
name|DOUBLE
expr_stmt|;
specifier|final
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
operator|(
operator|(
name|DoubleValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|bits
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:float */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|FLOAT
condition|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
operator|+
literal|4
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
operator|(
name|byte
operator|)
name|Type
operator|.
name|FLOAT
expr_stmt|;
specifier|final
name|int
name|bits
init|=
operator|(
name|int
operator|)
operator|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|(
operator|(
name|FloatValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
operator|^
literal|0x80000000
operator|)
decl_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|bits
argument_list|,
name|data
argument_list|,
name|OFFSET_VALUE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:boolean */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
operator|(
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|)
operator|+
literal|1
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|data
index|[
name|OFFSET_TYPE
index|]
operator|=
name|Type
operator|.
name|BOOLEAN
expr_stmt|;
name|data
index|[
name|OFFSET_VALUE
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
operator|(
name|BooleanValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* unknown! */
else|else
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unknown data type for serialization: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|serialize
parameter_list|(
name|Indexable
name|value
parameter_list|,
name|int
name|offset
parameter_list|)
throws|throws
name|EXistException
block|{
comment|//TODO : refactor (only strings are case sensitive)
return|return
name|serialize
argument_list|(
name|value
argument_list|,
name|offset
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/** 	 * @deprecated 	 * @param value 	 * @param offset 	 * @param caseSensitive 	 * @throws EXistException 	 */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|serialize
parameter_list|(
name|Indexable
name|value
parameter_list|,
name|int
name|offset
parameter_list|,
name|boolean
name|caseSensitive
parameter_list|)
throws|throws
name|EXistException
block|{
comment|/* xs:string */
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
specifier|final
name|String
name|val
init|=
name|caseSensitive
condition|?
operator|(
operator|(
name|StringValue
operator|)
name|value
operator|)
operator|.
name|getStringValue
argument_list|()
else|:
operator|(
operator|(
name|StringValue
operator|)
name|value
operator|)
operator|.
name|getStringValue
argument_list|()
operator|.
name|toLowerCase
argument_list|()
decl_stmt|;
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|+
name|UTF8
operator|.
name|encoded
argument_list|(
name|val
argument_list|)
index|]
decl_stmt|;
name|data
index|[
name|offset
index|]
operator|=
operator|(
name|byte
operator|)
name|value
operator|.
name|getType
argument_list|()
expr_stmt|;
comment|// TODO: cast to byte is not safe
name|UTF8
operator|.
name|encode
argument_list|(
name|val
argument_list|,
name|data
argument_list|,
name|offset
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:dateTime */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|DATE_TIME
argument_list|)
condition|)
block|{
name|GregorianCalendar
name|utccal
init|=
operator|(
operator|(
name|AbstractDateTimeValue
operator|)
name|value
operator|)
operator|.
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
name|millis
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
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
name|millis
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
comment|/* xs:integer */
if|else if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
condition|)
block|{
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|+
literal|8
index|]
decl_stmt|;
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
name|INTEGER
expr_stmt|;
name|long
name|l
init|=
operator|(
operator|(
name|IntegerValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
operator|-
name|Long
operator|.
name|MIN_VALUE
decl_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|l
argument_list|,
name|data
argument_list|,
name|offset
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:double */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|DOUBLE
condition|)
block|{
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|+
literal|8
index|]
decl_stmt|;
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
name|DOUBLE
expr_stmt|;
specifier|final
name|long
name|bits
init|=
name|Double
operator|.
name|doubleToLongBits
argument_list|(
operator|(
operator|(
name|DoubleValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
operator|^
literal|0x8000000000000000L
decl_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|bits
argument_list|,
name|data
argument_list|,
name|offset
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:float */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|FLOAT
condition|)
block|{
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|+
literal|4
index|]
decl_stmt|;
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
name|FLOAT
expr_stmt|;
specifier|final
name|int
name|bits
init|=
operator|(
name|int
operator|)
operator|(
name|Float
operator|.
name|floatToIntBits
argument_list|(
operator|(
operator|(
name|FloatValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
argument_list|)
operator|^
literal|0x80000000
operator|)
decl_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|bits
argument_list|,
name|data
argument_list|,
name|offset
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* xs:boolean */
if|else if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
condition|)
block|{
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
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
operator|+
literal|1
index|]
decl_stmt|;
name|data
index|[
name|offset
index|]
operator|=
name|Type
operator|.
name|BOOLEAN
expr_stmt|;
name|data
index|[
name|offset
operator|+
name|ValueIndexFactory
operator|.
name|LENGTH_VALUE_TYPE
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
operator|(
name|BooleanValue
operator|)
name|value
operator|)
operator|.
name|getValue
argument_list|()
condition|?
literal|1
else|:
literal|0
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/* unknown! */
else|else
block|{
throw|throw
operator|new
name|EXistException
argument_list|(
literal|"Unknown data type for serialization: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|value
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

