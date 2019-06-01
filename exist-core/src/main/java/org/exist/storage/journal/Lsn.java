begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
package|;
end_package

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
name|java
operator|.
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_comment
comment|/**  * Log Sequence Number: identifies a log record within the journal file.  * A LSN is represented by a Java long and consists of the file number  * of the journal file and an offset into the file.  *  * An LSN is 10 bytes, the first 8 bytes are the offset, the last 2 bytes  * are the fileNumber. The LSN is in<i>big-endian</i> byte-order: the  * most significant byte is in the zeroth element.  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|Lsn
implements|implements
name|Comparable
argument_list|<
name|Lsn
argument_list|>
block|{
comment|/**      * Length of the LSN in bytes.      */
specifier|public
specifier|static
specifier|final
name|int
name|RAW_LENGTH
init|=
literal|10
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FILE_NUMBER_OFFSET
init|=
literal|0
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|FILE_OFFSET_OFFSET
init|=
literal|2
decl_stmt|;
comment|/**      * This mask is used to obtain the value of an int as if it were unsigned.      */
specifier|private
specifier|static
specifier|final
name|long
name|LONG_MASK
init|=
literal|0xffffffffL
decl_stmt|;
comment|/**      * Singleton which represents an Invalid LSN      */
specifier|public
specifier|static
specifier|final
name|Lsn
name|LSN_INVALID
init|=
operator|new
name|Lsn
argument_list|(
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|,
operator|-
literal|1l
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|lsn
decl_stmt|;
specifier|public
name|Lsn
parameter_list|(
specifier|final
name|short
name|fileNumber
parameter_list|,
specifier|final
name|long
name|offset
parameter_list|)
block|{
name|this
operator|.
name|lsn
operator|=
operator|new
name|byte
index|[
name|RAW_LENGTH
index|]
expr_stmt|;
name|ByteConversion
operator|.
name|shortToByteH
argument_list|(
name|fileNumber
argument_list|,
name|lsn
argument_list|,
name|FILE_NUMBER_OFFSET
argument_list|)
expr_stmt|;
name|ByteConversion
operator|.
name|longToByte
argument_list|(
name|offset
argument_list|,
name|lsn
argument_list|,
name|FILE_OFFSET_OFFSET
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Lsn
parameter_list|(
specifier|final
name|byte
name|data
index|[]
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
name|this
operator|.
name|lsn
operator|=
operator|new
name|byte
index|[
name|RAW_LENGTH
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|offset
argument_list|,
name|lsn
argument_list|,
literal|0
argument_list|,
name|RAW_LENGTH
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Lsn
parameter_list|(
specifier|final
name|byte
name|lsn
index|[]
parameter_list|)
block|{
name|this
operator|.
name|lsn
operator|=
name|lsn
expr_stmt|;
block|}
specifier|public
specifier|static
name|Lsn
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|lsn
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
return|return
operator|new
name|Lsn
argument_list|(
name|lsn
argument_list|,
name|offset
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Lsn
name|read
parameter_list|(
specifier|final
name|ByteBuffer
name|buffer
parameter_list|)
block|{
specifier|final
name|byte
index|[]
name|lsn
init|=
operator|new
name|byte
index|[
name|RAW_LENGTH
index|]
decl_stmt|;
name|buffer
operator|.
name|get
argument_list|(
name|lsn
argument_list|)
expr_stmt|;
return|return
operator|new
name|Lsn
argument_list|(
name|lsn
argument_list|)
return|;
block|}
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|buffer
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|lsn
argument_list|,
literal|0
argument_list|,
name|buffer
argument_list|,
name|offset
argument_list|,
name|RAW_LENGTH
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|ByteBuffer
name|buffer
parameter_list|)
block|{
name|buffer
operator|.
name|put
argument_list|(
name|lsn
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the file number encoded in the passed LSN.      *      * @return file number      */
specifier|public
name|long
name|getFileNumber
parameter_list|()
block|{
return|return
name|ByteConversion
operator|.
name|byteToShortH
argument_list|(
name|lsn
argument_list|,
name|FILE_NUMBER_OFFSET
argument_list|)
return|;
block|}
comment|/**      * Returns the file offset encoded in the passed LSN.      *      * @return file offset      */
specifier|public
name|long
name|getOffset
parameter_list|()
block|{
return|return
name|ByteConversion
operator|.
name|byteToLong
argument_list|(
name|lsn
argument_list|,
name|FILE_OFFSET_OFFSET
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
specifier|final
name|Lsn
name|other
parameter_list|)
block|{
specifier|final
name|boolean
name|thisInvalid
init|=
name|this
operator|==
name|LSN_INVALID
operator|||
name|LSN_INVALID
operator|.
name|equals
argument_list|(
name|this
argument_list|)
decl_stmt|;
specifier|final
name|boolean
name|otherInvalid
init|=
name|other
operator|==
name|LSN_INVALID
operator|||
name|LSN_INVALID
operator|.
name|equals
argument_list|(
name|other
argument_list|)
decl_stmt|;
if|if
condition|(
name|thisInvalid
operator|&&
name|otherInvalid
condition|)
block|{
return|return
literal|0
return|;
block|}
if|else if
condition|(
name|thisInvalid
operator|&&
operator|!
name|otherInvalid
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
if|else if
condition|(
name|otherInvalid
operator|&&
operator|!
name|thisInvalid
condition|)
block|{
return|return
literal|1
return|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|RAW_LENGTH
condition|;
name|i
operator|++
control|)
block|{
name|int
name|a
init|=
name|lsn
index|[
name|i
index|]
decl_stmt|;
name|int
name|b
init|=
name|other
operator|.
name|lsn
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|a
operator|!=
name|b
condition|)
return|return
operator|(
operator|(
name|a
operator|&
name|LONG_MASK
operator|)
operator|<
operator|(
name|b
operator|&
name|LONG_MASK
operator|)
operator|)
condition|?
operator|-
literal|1
else|:
literal|1
return|;
block|}
return|return
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
return|return
literal|false
return|;
specifier|final
name|Lsn
name|other
init|=
operator|(
name|Lsn
operator|)
name|o
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|lsn
argument_list|,
name|other
operator|.
name|lsn
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Arrays
operator|.
name|hashCode
argument_list|(
name|lsn
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getFileNumber
argument_list|()
operator|+
literal|", "
operator|+
name|getOffset
argument_list|()
return|;
block|}
block|}
end_class

end_unit
