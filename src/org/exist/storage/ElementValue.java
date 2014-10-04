begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|dom
operator|.
name|persistent
operator|.
name|SymbolTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|btree
operator|.
name|Value
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ElementValue
extends|extends
name|Value
block|{
specifier|public
specifier|static
specifier|final
name|byte
name|UNKNOWN
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|ELEMENT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|byte
name|ATTRIBUTE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|static
name|int
name|LENGTH_TYPE
init|=
literal|1
decl_stmt|;
comment|//size of byte
specifier|public
specifier|static
name|int
name|OFFSET_COLLECTION_ID
init|=
literal|0
decl_stmt|;
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
name|OFFSET_SYMBOL
init|=
name|OFFSET_TYPE
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
decl_stmt|;
comment|//3
specifier|public
specifier|static
name|int
name|OFFSET_NSSYMBOL
init|=
name|OFFSET_SYMBOL
operator|+
name|SymbolTable
operator|.
name|LENGTH_LOCAL_NAME
decl_stmt|;
comment|//5
specifier|public
specifier|static
name|int
name|OFFSET_ID_STRING_VALUE
init|=
name|OFFSET_TYPE
operator|+
name|LENGTH_TYPE
decl_stmt|;
comment|//3
specifier|public
specifier|static
specifier|final
name|String
index|[]
name|type
init|=
block|{
literal|"element"
block|,
literal|"attribute"
block|,
literal|"id"
block|}
decl_stmt|;
name|ElementValue
parameter_list|(
name|int
name|collectionId
parameter_list|)
block|{
name|len
operator|=
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
expr_stmt|;
name|pos
operator|=
name|OFFSET_COLLECTION_ID
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|collectionId
parameter_list|)
block|{
name|len
operator|=
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
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
name|type
expr_stmt|;
name|pos
operator|=
name|OFFSET_COLLECTION_ID
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|collectionId
parameter_list|,
name|short
name|symbol
parameter_list|)
block|{
name|len
operator|=
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
operator|+
name|SymbolTable
operator|.
name|LENGTH_LOCAL_NAME
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
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
name|type
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|symbol
argument_list|,
name|data
argument_list|,
name|OFFSET_SYMBOL
argument_list|)
expr_stmt|;
name|pos
operator|=
name|OFFSET_COLLECTION_ID
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|collectionId
parameter_list|,
name|short
name|symbol
parameter_list|,
name|short
name|nsSymbol
parameter_list|)
block|{
name|len
operator|=
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
operator|+
name|SymbolTable
operator|.
name|LENGTH_LOCAL_NAME
operator|+
name|OFFSET_NSSYMBOL
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
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
name|type
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|symbol
argument_list|,
name|data
argument_list|,
name|OFFSET_SYMBOL
argument_list|)
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|nsSymbol
argument_list|,
name|data
argument_list|,
name|OFFSET_NSSYMBOL
argument_list|)
expr_stmt|;
name|pos
operator|=
name|OFFSET_COLLECTION_ID
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|int
name|collectionId
parameter_list|,
name|String
name|idStringValue
parameter_list|)
block|{
comment|//Note that the type expected to be ElementValue.ATTRIBUTE_ID
comment|//TODO : add sanity check for this ?
name|len
operator|=
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
operator|+
name|UTF8
operator|.
name|encoded
argument_list|(
name|idStringValue
argument_list|)
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|intToByte
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
name|type
expr_stmt|;
name|UTF8
operator|.
name|encode
argument_list|(
name|idStringValue
argument_list|,
name|data
argument_list|,
name|OFFSET_ID_STRING_VALUE
argument_list|)
expr_stmt|;
comment|//TODO : reset pos, just like in other contructors ?
block|}
name|int
name|getCollectionId
parameter_list|()
block|{
return|return
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Collection id : "
operator|+
name|ByteConversion
operator|.
name|byteToInt
argument_list|(
name|data
argument_list|,
name|OFFSET_COLLECTION_ID
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
name|OFFSET_COLLECTION_ID
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" Type : "
operator|+
name|type
index|[
name|data
index|[
name|OFFSET_TYPE
index|]
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|==
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
operator|+
name|SymbolTable
operator|.
name|LENGTH_LOCAL_NAME
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" Symbol id : "
operator|+
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|OFFSET_SYMBOL
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|len
operator|==
name|Collection
operator|.
name|LENGTH_COLLECTION_ID
operator|+
name|ElementValue
operator|.
name|LENGTH_TYPE
operator|+
name|SymbolTable
operator|.
name|LENGTH_LOCAL_NAME
operator|+
name|SymbolTable
operator|.
name|LENGTH_NS_URI
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" Symbol id : "
operator|+
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|OFFSET_SYMBOL
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|" NSSymbol id : "
operator|+
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
name|OFFSET_NSSYMBOL
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"Invalid data length !!!"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

