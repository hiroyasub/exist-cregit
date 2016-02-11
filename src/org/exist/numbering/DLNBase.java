begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|io
operator|.
name|VariableByteInput
import|;
end_import

begin_comment
comment|/**  * Base class representing a node id in the form of a dynamic level number (DLN).  * See {@link DLN}. DLNBase handles the efficient binary encoding of node ids.  *  * Level values are stored consecutively, using a fixed prefix free encoding. The number of  * units to be used for encoding a single level value is dynamically adjusted. We start with  * one unit and use its n - 1 lower bits. If the number exceeds the lower bits, we add another  * unit and set the highest bit to 1. This process is repeated for larger numbers. As a result,  * the first 1 bits of a level id indicate the number of fixed-size units used for encoding a level id.  * We thus don't need separator bits between the units themselves.  *  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|DLNBase
block|{
comment|/**      * The default number of bits used per fixed      * size unit.      */
specifier|public
specifier|final
specifier|static
name|int
name|BITS_PER_UNIT
init|=
literal|4
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
index|[]
name|BIT_MASK
init|=
operator|new
name|int
index|[
literal|8
index|]
decl_stmt|;
static|static
block|{
name|BIT_MASK
index|[
literal|0
index|]
operator|=
literal|0x80
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
literal|8
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|int
name|mask
init|=
literal|1
operator|<<
operator|(
literal|7
operator|-
name|i
operator|)
decl_stmt|;
name|BIT_MASK
index|[
name|i
index|]
operator|=
name|mask
operator|+
name|BIT_MASK
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
block|}
comment|/**      * Lists the maximum number that can be encoded      * by a given number of units. PER_COMPONENT_SIZE[0]      * corresponds to 1 unit used, PER_COMPONENT_SIZE[1]      * to 2 units, and so on. With BITS_PER_UNIT = 4, the largest       * number to be encoded by 1 unit is 7, for 2 units it's 71, for      * 3 units 583 ...      */
specifier|protected
specifier|final
specifier|static
name|int
index|[]
name|PER_COMPONENT_SIZE
init|=
name|initComponents
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|int
index|[]
name|initComponents
parameter_list|()
block|{
specifier|final
name|int
name|size
index|[]
init|=
operator|new
name|int
index|[
literal|10
index|]
decl_stmt|;
name|size
index|[
literal|0
index|]
operator|=
literal|7
expr_stmt|;
comment|// = Math.pow(2, 3) - 1;
name|int
name|components
decl_stmt|,
name|numBits
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|size
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|components
operator|=
name|i
operator|+
literal|1
expr_stmt|;
name|numBits
operator|=
name|components
operator|*
name|BITS_PER_UNIT
operator|-
name|components
expr_stmt|;
name|size
index|[
name|i
index|]
operator|=
operator|(
name|int
operator|)
operator|(
name|Math
operator|.
name|pow
argument_list|(
literal|2
argument_list|,
name|numBits
argument_list|)
operator|)
operator|+
name|size
index|[
name|i
operator|-
literal|1
index|]
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
specifier|protected
specifier|final
specifier|static
name|int
name|UNIT_SHIFT
init|=
literal|3
decl_stmt|;
comment|/** A 0-bit is used to mark the start of a new level */
specifier|protected
specifier|final
specifier|static
name|int
name|LEVEL_SEPARATOR
init|=
literal|0
decl_stmt|;
comment|/**       * A 1-bit marks the start of a sub level, which is logically a part      * of the current level.      */
specifier|protected
specifier|final
specifier|static
name|int
name|SUBLEVEL_SEPARATOR
init|=
literal|1
decl_stmt|;
comment|// the bits are stored in a byte[]
specifier|protected
name|byte
index|[]
name|bits
decl_stmt|;
comment|// the current index into the byte[] used
comment|// for appending new bits
specifier|protected
name|int
name|bitIndex
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|DLNBase
parameter_list|()
block|{
name|bits
operator|=
operator|new
name|byte
index|[
literal|1
index|]
expr_stmt|;
block|}
specifier|public
name|DLNBase
parameter_list|(
specifier|final
name|DLNBase
name|dln
parameter_list|)
block|{
name|this
operator|.
name|bits
operator|=
operator|new
name|byte
index|[
name|dln
operator|.
name|bits
operator|.
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|dln
operator|.
name|bits
argument_list|,
literal|0
argument_list|,
name|this
operator|.
name|bits
argument_list|,
literal|0
argument_list|,
name|dln
operator|.
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
name|this
operator|.
name|bitIndex
operator|=
name|dln
operator|.
name|bitIndex
expr_stmt|;
block|}
specifier|public
name|DLNBase
parameter_list|(
specifier|final
name|int
name|units
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|startOffset
parameter_list|)
block|{
if|if
condition|(
name|units
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Negative size for DLN: "
operator|+
name|units
argument_list|)
throw|;
block|}
name|int
name|blen
init|=
name|units
operator|/
literal|8
decl_stmt|;
if|if
condition|(
name|units
operator|%
literal|8
operator|>
literal|0
condition|)
block|{
operator|++
name|blen
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|byte
index|[
name|blen
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
name|startOffset
argument_list|,
name|bits
argument_list|,
literal|0
argument_list|,
name|blen
argument_list|)
expr_stmt|;
name|bitIndex
operator|=
name|units
operator|-
literal|1
expr_stmt|;
block|}
specifier|protected
name|DLNBase
parameter_list|(
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|nbits
parameter_list|)
block|{
specifier|final
name|int
name|remainder
init|=
name|nbits
operator|%
literal|8
decl_stmt|;
specifier|final
name|int
name|len
init|=
name|nbits
operator|/
literal|8
decl_stmt|;
name|bits
operator|=
operator|new
name|byte
index|[
name|len
operator|+
operator|(
name|remainder
operator|>
literal|0
condition|?
literal|1
else|:
literal|0
operator|)
index|]
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|bits
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|remainder
operator|>
literal|0
condition|)
block|{
name|byte
name|b
init|=
literal|0
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
name|remainder
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|(
name|data
index|[
name|len
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|i
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|b
operator||=
literal|1
operator|<<
operator|(
literal|7
operator|-
name|i
operator|)
expr_stmt|;
block|}
block|}
name|bits
index|[
name|len
index|]
operator|=
name|b
expr_stmt|;
block|}
name|bitIndex
operator|=
name|nbits
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|DLNBase
parameter_list|(
specifier|final
name|short
name|bitCnt
parameter_list|,
specifier|final
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|blen
init|=
name|bitCnt
operator|/
literal|8
decl_stmt|;
if|if
condition|(
name|bitCnt
operator|%
literal|8
operator|>
literal|0
condition|)
block|{
operator|++
name|blen
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|byte
index|[
name|blen
index|]
expr_stmt|;
name|is
operator|.
name|read
argument_list|(
name|bits
argument_list|)
expr_stmt|;
name|bitIndex
operator|=
name|bitCnt
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|DLNBase
parameter_list|(
specifier|final
name|byte
name|prefixLen
parameter_list|,
specifier|final
name|DLNBase
name|previous
parameter_list|,
specifier|final
name|short
name|bitCnt
parameter_list|,
specifier|final
name|VariableByteInput
name|is
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|blen
init|=
name|bitCnt
operator|/
literal|8
decl_stmt|;
if|if
condition|(
name|bitCnt
operator|%
literal|8
operator|>
literal|0
condition|)
block|{
operator|++
name|blen
expr_stmt|;
block|}
name|bits
operator|=
operator|new
name|byte
index|[
name|blen
index|]
expr_stmt|;
if|if
condition|(
name|previous
operator|.
name|bits
operator|.
name|length
operator|<
name|prefixLen
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Found wrong prefix len: "
operator|+
name|prefixLen
operator|+
literal|". Previous: "
operator|+
name|previous
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|System
operator|.
name|arraycopy
argument_list|(
name|previous
operator|.
name|bits
argument_list|,
literal|0
argument_list|,
name|bits
argument_list|,
literal|0
argument_list|,
name|prefixLen
argument_list|)
expr_stmt|;
name|is
operator|.
name|read
argument_list|(
name|bits
argument_list|,
name|prefixLen
argument_list|,
name|blen
operator|-
name|prefixLen
argument_list|)
expr_stmt|;
name|bitIndex
operator|=
name|bitCnt
operator|-
literal|1
expr_stmt|;
block|}
comment|/**      * Set the level id which starts at offset to the      * given id value.      *      * @param offset      * @param levelId      */
specifier|public
name|void
name|setLevelId
parameter_list|(
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|levelId
parameter_list|)
block|{
name|bitIndex
operator|=
name|offset
operator|-
literal|1
expr_stmt|;
name|setCurrentLevelId
argument_list|(
name|levelId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Adds a new level to the node id, using levelId      * as initial value.      *      * @param levelId initial value      */
specifier|public
name|void
name|addLevelId
parameter_list|(
specifier|final
name|int
name|levelId
parameter_list|,
specifier|final
name|boolean
name|isSubLevel
parameter_list|)
block|{
if|if
condition|(
name|bitIndex
operator|>
operator|-
literal|1
condition|)
block|{
name|setNextBit
argument_list|(
name|isSubLevel
argument_list|)
expr_stmt|;
block|}
name|setCurrentLevelId
argument_list|(
name|levelId
argument_list|)
expr_stmt|;
block|}
comment|/**      * Increments the last level id by one.      */
specifier|public
name|void
name|incrementLevelId
parameter_list|()
block|{
specifier|final
name|int
name|last
init|=
name|lastFieldPosition
argument_list|()
decl_stmt|;
name|bitIndex
operator|=
name|last
operator|-
literal|1
expr_stmt|;
name|setCurrentLevelId
argument_list|(
name|getLevelId
argument_list|(
name|last
argument_list|)
operator|+
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|decrementLevelId
parameter_list|()
block|{
specifier|final
name|int
name|last
init|=
name|lastFieldPosition
argument_list|()
decl_stmt|;
name|bitIndex
operator|=
name|last
operator|-
literal|1
expr_stmt|;
name|int
name|levelId
init|=
name|getLevelId
argument_list|(
name|last
argument_list|)
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|levelId
operator|<
literal|1
condition|)
block|{
name|levelId
operator|=
literal|0
expr_stmt|;
block|}
name|setCurrentLevelId
argument_list|(
name|levelId
argument_list|)
expr_stmt|;
comment|// after decrementing, the DLN may need less bytes
comment|// than before. Remove the unused bytes, otherwise binary
comment|// comparisons may get wrong.
specifier|final
name|int
name|len
init|=
name|bitIndex
operator|+
literal|1
decl_stmt|;
name|int
name|blen
init|=
name|len
operator|/
literal|8
decl_stmt|;
if|if
condition|(
name|len
operator|%
literal|8
operator|>
literal|0
condition|)
block|{
operator|++
name|blen
expr_stmt|;
block|}
if|if
condition|(
name|blen
operator|<
name|bits
operator|.
name|length
condition|)
block|{
specifier|final
name|byte
index|[]
name|nbits
init|=
operator|new
name|byte
index|[
name|blen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|nbits
argument_list|,
literal|0
argument_list|,
name|blen
argument_list|)
expr_stmt|;
name|bits
operator|=
name|nbits
expr_stmt|;
block|}
block|}
comment|/**      * Set the level id for the last level that has been written.      * The data array will be resized automatically if the bit set is      * too small to encode the id.      *      * @param levelId      */
specifier|protected
name|void
name|setCurrentLevelId
parameter_list|(
name|int
name|levelId
parameter_list|)
block|{
specifier|final
name|int
name|units
init|=
name|getUnitsRequired
argument_list|(
name|levelId
argument_list|)
decl_stmt|;
specifier|final
name|int
name|numBits
init|=
name|bitWidth
argument_list|(
name|units
argument_list|)
decl_stmt|;
if|if
condition|(
name|units
operator|>
literal|1
condition|)
block|{
name|levelId
operator|-=
name|PER_COMPONENT_SIZE
index|[
name|units
operator|-
literal|2
index|]
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|units
condition|;
name|i
operator|++
control|)
block|{
name|setNextBit
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|setNextBit
argument_list|(
literal|false
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numBits
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|setNextBit
argument_list|(
operator|(
operator|(
name|levelId
operator|>>>
name|i
operator|)
operator|&
literal|1
operator|)
operator|!=
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the id starting at offset.      *      * @param startBit      * @return the level id      */
specifier|public
name|int
name|getLevelId
parameter_list|(
name|int
name|startBit
parameter_list|)
block|{
specifier|final
name|int
name|units
init|=
name|unitsUsed
argument_list|(
name|startBit
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|startBit
operator|+=
name|units
expr_stmt|;
specifier|final
name|int
name|numBits
init|=
name|bitWidth
argument_list|(
name|units
argument_list|)
decl_stmt|;
comment|//System.err.println("startBit: " + startBit + "; bitIndex: " + bitIndex +
comment|//"; units: " + units + ": numBits: " + numBits + " " + toBitString() +
comment|//"; bits: " + bits.length);
name|int
name|id
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|numBits
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
if|if
condition|(
operator|(
name|bits
index|[
name|startBit
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|startBit
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
name|id
operator||=
literal|1
operator|<<
name|i
expr_stmt|;
block|}
block|}
if|if
condition|(
name|units
operator|>
literal|1
condition|)
block|{
name|id
operator|+=
name|PER_COMPONENT_SIZE
index|[
name|units
operator|-
literal|2
index|]
expr_stmt|;
block|}
return|return
name|id
return|;
block|}
comment|/**      * Returns the number of units currently used      * to encode the id. The size of a single unit is      * given by {@link #BITS_PER_UNIT}.      *      * @return the number of units      */
specifier|public
name|int
name|units
parameter_list|()
block|{
return|return
name|bitIndex
operator|+
literal|1
return|;
block|}
comment|/**      * Returns the size of this id by counting the bytes      * used to encode it.      *      * @return the size in bytes      */
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|bits
operator|.
name|length
return|;
block|}
specifier|private
specifier|static
name|int
name|unitsUsed
parameter_list|(
name|int
name|startBit
parameter_list|,
specifier|final
name|byte
index|[]
name|bits
parameter_list|)
block|{
name|int
name|units
init|=
literal|1
decl_stmt|;
while|while
condition|(
operator|(
name|bits
index|[
name|startBit
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|startBit
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|!=
literal|0
condition|)
block|{
operator|++
name|units
expr_stmt|;
block|}
return|return
name|units
return|;
block|}
specifier|public
name|boolean
name|isLevelSeparator
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|bits
index|[
name|index
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|index
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|==
literal|0
return|;
block|}
comment|/**      * Returns the number of level in this id, which corresponds      * to the depth at which the node occurs within the node tree.      *      * @return the number of levels in this id      */
specifier|public
name|int
name|getLevelCount
parameter_list|(
specifier|final
name|int
name|startOffset
parameter_list|)
block|{
name|int
name|bit
init|=
name|startOffset
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bit
operator|>
operator|-
literal|1
operator|&&
name|bit
operator|<=
name|bitIndex
condition|)
block|{
specifier|final
name|int
name|units
init|=
name|unitsUsed
argument_list|(
name|bit
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|bit
operator|+=
name|units
expr_stmt|;
name|bit
operator|+=
name|bitWidth
argument_list|(
name|units
argument_list|)
expr_stmt|;
if|if
condition|(
name|bit
operator|<
name|bitIndex
condition|)
block|{
if|if
condition|(
operator|(
name|bits
index|[
name|bit
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|bit
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|==
name|LEVEL_SEPARATOR
condition|)
block|{
operator|++
name|count
expr_stmt|;
block|}
block|}
else|else
block|{
operator|++
name|count
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * Returns the number of sub-levels in the id starting at      * startOffset. This is required to determine where a node      * can be inserted.      *       * @param startOffset      * @return number of sub-levels      */
specifier|public
name|int
name|getSubLevelCount
parameter_list|(
specifier|final
name|int
name|startOffset
parameter_list|)
block|{
name|int
name|bit
init|=
name|startOffset
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bit
operator|>
operator|-
literal|1
operator|&&
name|bit
operator|<=
name|bitIndex
condition|)
block|{
specifier|final
name|int
name|units
init|=
name|unitsUsed
argument_list|(
name|bit
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|bit
operator|+=
name|units
expr_stmt|;
name|bit
operator|+=
name|bitWidth
argument_list|(
name|units
argument_list|)
expr_stmt|;
if|if
condition|(
name|bit
operator|<
name|bitIndex
condition|)
block|{
operator|++
name|count
expr_stmt|;
if|if
condition|(
operator|(
name|bits
index|[
name|bit
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|bit
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|==
name|LEVEL_SEPARATOR
condition|)
block|{
break|break;
block|}
block|}
else|else
block|{
operator|++
name|count
expr_stmt|;
block|}
block|}
return|return
name|count
return|;
block|}
comment|/**      * Return all level ids converted to int.      *      * @return all level ids in this node id.      */
specifier|public
name|int
index|[]
name|getLevelIds
parameter_list|()
block|{
specifier|final
name|int
name|count
init|=
name|getLevelCount
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|int
index|[]
name|ids
init|=
operator|new
name|int
index|[
name|count
index|]
decl_stmt|;
name|int
name|offset
init|=
literal|0
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|ids
index|[
name|i
index|]
operator|=
name|getLevelId
argument_list|(
name|offset
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|getUnitsRequired
argument_list|(
name|ids
index|[
name|i
index|]
argument_list|)
operator|*
name|BITS_PER_UNIT
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
comment|/**      * Find the last level in the id and return its offset.      *      * @return start-offset of the last level id.      */
specifier|public
name|int
name|lastLevelOffset
parameter_list|()
block|{
name|int
name|bit
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bit
operator|<=
name|bitIndex
condition|)
block|{
comment|// check if the next bit starts a new level or just a sub-level component
if|if
condition|(
name|bit
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|bits
index|[
name|bit
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|bit
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|==
name|LEVEL_SEPARATOR
condition|)
block|{
name|lastOffset
operator|=
name|bit
operator|+
literal|1
expr_stmt|;
block|}
operator|++
name|bit
expr_stmt|;
block|}
specifier|final
name|int
name|units
init|=
name|unitsUsed
argument_list|(
name|bit
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|bit
operator|+=
name|units
expr_stmt|;
name|bit
operator|+=
name|bitWidth
argument_list|(
name|units
argument_list|)
expr_stmt|;
block|}
return|return
name|lastOffset
return|;
block|}
specifier|protected
name|int
name|lastFieldPosition
parameter_list|()
block|{
name|int
name|bit
init|=
literal|0
decl_stmt|;
name|int
name|lastOffset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|bit
operator|<=
name|bitIndex
condition|)
block|{
if|if
condition|(
name|bit
operator|>
literal|0
condition|)
block|{
name|lastOffset
operator|=
operator|++
name|bit
expr_stmt|;
block|}
specifier|final
name|int
name|units
init|=
name|unitsUsed
argument_list|(
name|bit
argument_list|,
name|bits
argument_list|)
decl_stmt|;
name|bit
operator|+=
name|units
expr_stmt|;
name|bit
operator|+=
name|bitWidth
argument_list|(
name|units
argument_list|)
expr_stmt|;
block|}
return|return
name|lastOffset
return|;
block|}
comment|/**      * Set (or unset) the next bit in the current sequence      * of bits. The current position is moved forward and the      * bit set is resized if necessary.      *      * @param value the value of the bit to set, i.e. 1 (true) or 0 (false)      */
specifier|private
name|void
name|setNextBit
parameter_list|(
specifier|final
name|boolean
name|value
parameter_list|)
block|{
operator|++
name|bitIndex
expr_stmt|;
if|if
condition|(
operator|(
name|bitIndex
operator|>>
name|UNIT_SHIFT
operator|)
operator|>=
name|bits
operator|.
name|length
condition|)
block|{
specifier|final
name|byte
index|[]
name|new_bits
init|=
operator|new
name|byte
index|[
name|bits
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|new_bits
argument_list|,
literal|0
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
name|bits
operator|=
name|new_bits
expr_stmt|;
block|}
if|if
condition|(
name|value
condition|)
block|{
name|bits
index|[
name|bitIndex
operator|>>
name|UNIT_SHIFT
index|]
operator||=
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|bitIndex
operator|)
operator|&
literal|7
operator|)
expr_stmt|;
block|}
else|else
block|{
name|bits
index|[
name|bitIndex
operator|>>
name|UNIT_SHIFT
index|]
operator|&=
operator|~
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|bitIndex
operator|)
operator|&
literal|7
operator|)
operator|)
expr_stmt|;
block|}
block|}
comment|/**      * Calculates the number of bits available in a bit set      * that uses the given number of units. These are the bits      * that can be actually used for the id, not including the      * trailing address bits.      *       * @param units      * @return number of bits available      */
specifier|protected
specifier|static
name|int
name|bitWidth
parameter_list|(
specifier|final
name|int
name|units
parameter_list|)
block|{
return|return
operator|(
name|units
operator|*
name|BITS_PER_UNIT
operator|)
operator|-
name|units
return|;
block|}
comment|/**      * Calculates the minimum number of units that would be required      * to properly encode the given integer.      *       * @param levelId the integer to encode in the level id      * @return number of units required      */
specifier|protected
specifier|static
name|int
name|getUnitsRequired
parameter_list|(
specifier|final
name|int
name|levelId
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|PER_COMPONENT_SIZE
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|levelId
operator|<
name|PER_COMPONENT_SIZE
index|[
name|i
index|]
condition|)
block|{
return|return
name|i
operator|+
literal|1
return|;
block|}
block|}
comment|// can't happen
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Number of nodes exceeds the internal limit"
argument_list|)
throw|;
block|}
specifier|protected
name|void
name|compact
parameter_list|()
block|{
specifier|final
name|int
name|units
init|=
name|bitIndex
operator|+
literal|1
decl_stmt|;
name|int
name|blen
init|=
name|units
operator|/
literal|8
decl_stmt|;
if|if
condition|(
name|units
operator|%
literal|8
operator|>
literal|0
condition|)
block|{
operator|++
name|blen
expr_stmt|;
block|}
specifier|final
name|byte
index|[]
name|nbits
init|=
operator|new
name|byte
index|[
name|blen
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|bits
argument_list|,
literal|0
argument_list|,
name|nbits
argument_list|,
literal|0
argument_list|,
name|blen
argument_list|)
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|nbits
expr_stmt|;
block|}
specifier|public
name|void
name|serialize
parameter_list|(
specifier|final
name|byte
index|[]
name|data
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
name|bits
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
name|offset
argument_list|,
name|bits
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|int
name|getLengthInBytes
parameter_list|(
specifier|final
name|int
name|units
parameter_list|,
specifier|final
name|byte
index|[]
name|data
parameter_list|,
specifier|final
name|int
name|startOffset
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
name|Math
operator|.
name|ceil
argument_list|(
name|units
operator|/
literal|8.0
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|DLNBase
name|other
parameter_list|)
block|{
if|if
condition|(
name|bitIndex
operator|!=
name|other
operator|.
name|bitIndex
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|bits
argument_list|,
name|other
operator|.
name|bits
argument_list|)
return|;
block|}
comment|//    public int compareTo(final DLNBase other) {
comment|//        if (other == null)
comment|//            return 1;
comment|//        final int a1len = bits.length;
comment|//        final int a2len = other.bits.length;
comment|//
comment|//        int limit = a1len<= a2len ? a1len : a2len;
comment|//        byte[] obits = other.bits;
comment|//        for (int i = 0; i< limit; i++) {
comment|//            byte b1 = bits[i];
comment|//            byte b2 = obits[i];
comment|//            if (b1 != b2)
comment|//                return (b1& 0xFF) - (b2& 0xFF);
comment|//        }
comment|//        return (a1len - a2len);
comment|//    }
comment|//
comment|//    public int compareTo(Object obj) {
comment|//        DLNBase other = (DLNBase) obj;
comment|//        return compareTo(other);
comment|//    }
specifier|public
name|int
name|compareBits
parameter_list|(
specifier|final
name|DLNBase
name|other
parameter_list|,
specifier|final
name|int
name|bitCount
parameter_list|)
block|{
specifier|final
name|int
name|bytes
init|=
name|bitCount
operator|/
literal|8
decl_stmt|;
specifier|final
name|int
name|remaining
init|=
name|bitCount
operator|%
literal|8
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
name|bytes
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|!=
name|other
operator|.
name|bits
index|[
name|i
index|]
condition|)
block|{
return|return
operator|(
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
operator|)
operator|-
operator|(
name|other
operator|.
name|bits
index|[
name|i
index|]
operator|&
literal|0xFF
operator|)
return|;
block|}
block|}
return|return
operator|(
name|bits
index|[
name|bytes
index|]
operator|&
name|BIT_MASK
index|[
name|remaining
index|]
operator|)
operator|-
operator|(
name|other
operator|.
name|bits
index|[
name|bytes
index|]
operator|&
name|BIT_MASK
index|[
name|remaining
index|]
operator|)
return|;
block|}
comment|/**      * Checks if the current DLN starts with the      * same bit sequence as other. This is used      * to test ancestor-descendant relationships.      *       * @param other      */
specifier|public
name|boolean
name|startsWith
parameter_list|(
specifier|final
name|DLNBase
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|bitIndex
operator|>
name|bitIndex
condition|)
block|{
return|return
literal|false
return|;
block|}
specifier|final
name|int
name|bytes
init|=
name|other
operator|.
name|bitIndex
operator|/
literal|8
decl_stmt|;
specifier|final
name|int
name|remaining
init|=
name|other
operator|.
name|bitIndex
operator|%
literal|8
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
name|bytes
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|bits
index|[
name|i
index|]
operator|!=
name|other
operator|.
name|bits
index|[
name|i
index|]
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
operator|(
name|bits
index|[
name|bytes
index|]
operator|&
name|BIT_MASK
index|[
name|remaining
index|]
operator|)
operator|==
operator|(
name|other
operator|.
name|bits
index|[
name|bytes
index|]
operator|&
name|BIT_MASK
index|[
name|remaining
index|]
operator|)
return|;
block|}
specifier|public
name|String
name|debug
parameter_list|()
block|{
return|return
name|toString
argument_list|()
operator|+
literal|" = "
operator|+
name|toBitString
argument_list|()
operator|+
literal|" ["
operator|+
operator|(
name|bitIndex
operator|+
literal|1
operator|)
operator|+
literal|']'
return|;
block|}
annotation|@
name|Override
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
name|int
name|offset
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|offset
operator|<=
name|bitIndex
condition|)
block|{
if|if
condition|(
name|offset
operator|>
literal|0
condition|)
block|{
if|if
condition|(
operator|(
name|bits
index|[
name|offset
operator|>>
name|UNIT_SHIFT
index|]
operator|&
operator|(
literal|1
operator|<<
operator|(
operator|(
literal|7
operator|-
name|offset
operator|++
operator|)
operator|&
literal|7
operator|)
operator|)
operator|)
operator|==
literal|0
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'.'
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|int
name|id
init|=
name|getLevelId
argument_list|(
name|offset
argument_list|)
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|id
argument_list|)
expr_stmt|;
name|offset
operator|+=
name|getUnitsRequired
argument_list|(
name|id
argument_list|)
operator|*
name|BITS_PER_UNIT
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|String
name|toBitString
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
specifier|final
name|int
name|len
init|=
name|bits
operator|.
name|length
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
name|len
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|toBitString
argument_list|(
name|bits
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
name|char
index|[]
name|digits
init|=
block|{
literal|'0'
block|,
literal|'1'
block|}
decl_stmt|;
comment|/**      * Returns a string showing the bit representation      * of the given byte.      *       * @param b the byte to display      * @return string representation      */
specifier|public
specifier|static
name|String
name|toBitString
parameter_list|(
name|byte
name|b
parameter_list|)
block|{
specifier|final
name|char
index|[]
name|buf
init|=
operator|new
name|char
index|[
literal|8
index|]
decl_stmt|;
name|int
name|charPos
init|=
literal|8
decl_stmt|;
specifier|final
name|int
name|radix
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|mask
init|=
name|radix
operator|-
literal|1
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
literal|8
condition|;
name|i
operator|++
control|)
block|{
name|buf
index|[
operator|--
name|charPos
index|]
operator|=
name|digits
index|[
name|b
operator|&
name|mask
index|]
expr_stmt|;
name|b
operator|>>>=
literal|1
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|buf
argument_list|)
return|;
block|}
block|}
end_class

end_unit

