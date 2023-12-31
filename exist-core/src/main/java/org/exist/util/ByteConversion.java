begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang Meier (wolfgang@exist-db.org)  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/**  * A collection of static methods to write integer values from/to a  * byte array.  *  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  */
end_comment

begin_class
specifier|public
class|class
name|ByteConversion
block|{
comment|/**      * Read an integer value from the specified byte array, starting at start.      *      * @param data the input data      * @param start the offset to start from in the input data.      *      * @return the integer      *      * @deprecated reads the lowest byte first. will be replaced with      *     {@link #byteToIntH(byte[], int)} for consistency.      */
specifier|public
specifier|final
specifier|static
name|int
name|byteToInt
parameter_list|(
specifier|final
name|byte
name|data
index|[]
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
return|return
operator|(
name|data
index|[
name|start
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
return|;
block|}
comment|/**      * Read an integer value from the specified byte array, starting at start.      *      * This version of the method reads the highest byte first.      *      * @param data the input data      * @param start the offset to start from in the input data.      *      * @return the integer      */
specifier|public
specifier|final
specifier|static
name|int
name|byteToIntH
parameter_list|(
specifier|final
name|byte
name|data
index|[]
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
return|return
operator|(
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|&
literal|0xff
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
name|data
index|[
name|start
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|24
operator|)
return|;
block|}
comment|/**      *  Read a long value from the specified byte array, starting at start.      *      * @param data the input data      * @param start the offset to start from in the input data.      *      * @return the long integer      */
specifier|public
specifier|final
specifier|static
name|long
name|byteToLong
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
return|return
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|56
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|48
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|40
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|32
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|4
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|24
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|5
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|16
operator|)
operator||
operator|(
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|6
index|]
operator|)
operator|&
literal|0xffL
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
operator|(
operator|(
name|long
operator|)
name|data
index|[
name|start
operator|+
literal|7
index|]
operator|)
operator|&
literal|0xffL
operator|)
return|;
block|}
comment|/**      * Read a short value from the specified byte array, starting at start.      *      * @deprecated reads the lowest byte first. will be replaced with      *     {@link #byteToShortH(byte[], int)} for consistency.      *      * @param data the input data      * @param start the offset to start from in the input data.      *      * @return the short integer      */
specifier|public
specifier|final
specifier|static
name|short
name|byteToShort
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
operator|(
operator|(
operator|(
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|data
index|[
name|start
index|]
operator|&
literal|0xff
operator|)
operator|)
return|;
block|}
comment|/**      * Read a short value from the specified byte array, starting at start.      *      * This version of the method reads the highest byte first.      *      * @param data the input data      * @param start the offset to start from in the input data.      *      * @return the short integer      */
specifier|public
specifier|final
specifier|static
name|short
name|byteToShortH
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
return|return
operator|(
name|short
operator|)
operator|(
operator|(
operator|(
name|data
index|[
name|start
index|]
operator|&
literal|0xff
operator|)
operator|<<
literal|8
operator|)
operator||
operator|(
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|&
literal|0xff
operator|)
operator|)
return|;
block|}
comment|/**      * Write an int value to the specified byte array. The first byte is written      * into the location specified by start.      *      * @deprecated this version of the method writes the lowest byte first. It will      * be replaced by {@link #intToByteH(int, byte[], int)} for consistency.      * @param  v the value      * @param  data  the byte array to write into      * @param  start  the offset      * @return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|intToByte
parameter_list|(
specifier|final
name|int
name|v
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
name|data
index|[
name|start
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      * Write an int value to the specified byte array. The first byte is written      * into the location specified by start.      *      * This version of the method writes the highest byte first.      *      *@param  v the value      *@param  data  the byte array to write into      *@param  start  the offset      *@return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|intToByteH
parameter_list|(
specifier|final
name|int
name|v
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      * Write a long value to the specified byte array. The first byte is written      * into the location specified by start.      *      *@param  v the value      *@param  data  the byte array to write into      *@param  start  the offset      *@return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|longToByte
parameter_list|(
specifier|final
name|long
name|v
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
name|data
index|[
name|start
operator|+
literal|7
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|6
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|5
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|4
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|32
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|40
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|48
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|56
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      * Write an int value to a newly allocated byte array.      *      *@param  v the value      *@return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|longToByte
parameter_list|(
specifier|final
name|long
name|v
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|data
index|[
literal|7
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|6
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|5
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|16
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|4
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|24
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|3
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|32
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|2
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|40
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|48
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
literal|0
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|56
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      * Write a short value to the specified byte array. The first byte is written      * into the location specified by start.      *      * @deprecated this version of the method writes the lowest byte first. It will be replaced      * by {@link #shortToByteH(short, byte[], int)} for consistency.      *      * @param  v the value      * @param  data  the byte array to write into      * @param  start  the offset      * @return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|shortToByte
parameter_list|(
specifier|final
name|short
name|v
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
name|data
index|[
name|start
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
comment|/**      * Write a short value to the specified byte array. The first byte is written      * into the location specified by start.      *      * This version writes the highest byte first.      *      * @param  v the value      * @param  data  the byte array to write into      * @param  start  the offset      * @return   the byte array      */
specifier|public
specifier|final
specifier|static
name|byte
index|[]
name|shortToByteH
parameter_list|(
specifier|final
name|short
name|v
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|start
parameter_list|)
block|{
name|data
index|[
name|start
operator|+
literal|1
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|0
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
name|data
index|[
name|start
index|]
operator|=
operator|(
name|byte
operator|)
operator|(
operator|(
name|v
operator|>>>
literal|8
operator|)
operator|&
literal|0xff
operator|)
expr_stmt|;
return|return
name|data
return|;
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
specifier|final
name|short
name|i
init|=
literal|783
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
literal|2
index|]
decl_stmt|;
name|ByteConversion
operator|.
name|shortToByte
argument_list|(
name|i
argument_list|,
name|data
argument_list|,
literal|0
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"i = "
operator|+
name|ByteConversion
operator|.
name|byteToShort
argument_list|(
name|data
argument_list|,
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

