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
name|storage
package|;
end_package

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
specifier|final
name|byte
name|ATTRIBUTE_ID
init|=
literal|2
decl_stmt|;
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
name|short
name|collectionId
parameter_list|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
literal|2
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|len
operator|=
literal|2
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|short
name|collectionId
parameter_list|)
block|{
name|data
operator|=
operator|new
name|byte
index|[
literal|3
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|data
index|[
literal|2
index|]
operator|=
name|type
expr_stmt|;
name|len
operator|=
literal|3
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|short
name|collectionId
parameter_list|,
name|short
name|symbol
parameter_list|)
block|{
name|len
operator|=
literal|5
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
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|data
index|[
literal|2
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
literal|3
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|short
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
literal|7
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
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|data
index|[
literal|2
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
literal|3
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
literal|5
argument_list|)
expr_stmt|;
name|pos
operator|=
literal|0
expr_stmt|;
block|}
name|ElementValue
parameter_list|(
name|byte
name|type
parameter_list|,
name|short
name|collectionId
parameter_list|,
name|String
name|idValue
parameter_list|)
block|{
name|len
operator|=
literal|3
operator|+
name|UTF8
operator|.
name|encoded
argument_list|(
name|idValue
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
name|shortToByte
argument_list|(
name|collectionId
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|data
index|[
literal|2
index|]
operator|=
name|type
expr_stmt|;
name|UTF8
operator|.
name|encode
argument_list|(
name|idValue
argument_list|,
name|data
argument_list|,
literal|3
argument_list|)
expr_stmt|;
block|}
name|short
name|getCollectionId
parameter_list|()
block|{
return|return
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
return|;
block|}
block|}
end_class

end_unit

