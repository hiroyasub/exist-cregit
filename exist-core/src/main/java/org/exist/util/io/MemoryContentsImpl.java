begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
package|;
end_package

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|max
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|Math
operator|.
name|min
import|;
end_import

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
name|io
operator|.
name|OutputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
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
name|storage
operator|.
name|lock
operator|.
name|Lock
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
name|lock
operator|.
name|ManagedLock
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:patrick@reini.net">Patrick Reinhart</a>  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|MemoryContentsImpl
implements|implements
name|MemoryContents
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|MemoryContentsImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The object header size of an array. Two words (flags&amp; class oop) plus      * array size (2 *64 bit + 32 bit on 64 bit, 2 *32 bit + 32 bit on 32 bit).      */
specifier|private
specifier|static
specifier|final
name|int
name|ARRAY_HEADER
init|=
literal|8
operator|+
literal|8
operator|+
literal|4
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|BLOCK_SIZE
init|=
literal|4096
operator|-
name|ARRAY_HEADER
decl_stmt|;
comment|// make sure it fits into a 4k memory region
specifier|private
specifier|static
specifier|final
name|int
name|NUMBER_OF_BLOCKS
init|=
name|BLOCK_SIZE
decl_stmt|;
specifier|private
specifier|final
name|int
name|initialBlocks
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|lock
decl_stmt|;
comment|/**      * To store the contents efficiently we store the first {@value #BLOCK_SIZE}      * bytes in a {@value #BLOCK_SIZE} direct {@code byte[]}. The next      * {@value #NUMBER_OF_BLOCKS} * {@value #BLOCK_SIZE} bytes go into a indirect      * {@code byte[][]} that is lazily allocated.      */
specifier|private
name|byte
index|[]
name|directBlock
decl_stmt|;
specifier|private
name|byte
index|[]
index|[]
name|indirectBlocks
decl_stmt|;
specifier|private
name|long
name|size
decl_stmt|;
specifier|private
name|int
name|indirectBlocksAllocated
decl_stmt|;
specifier|public
specifier|static
name|MemoryContents
name|createWithInitialBlocks
parameter_list|(
name|int
name|initialBlocks
parameter_list|)
block|{
return|return
operator|new
name|MemoryContentsImpl
argument_list|(
name|initialBlocks
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|MemoryContents
name|createWithInMemorySize
parameter_list|(
name|int
name|inMemorySize
parameter_list|)
block|{
return|return
name|createWithInitialBlocks
argument_list|(
name|max
argument_list|(
name|inMemorySize
operator|/
literal|10
operator|/
name|MemoryContentsImpl
operator|.
name|BLOCK_SIZE
argument_list|,
literal|1
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|MemoryContentsImpl
parameter_list|(
name|int
name|initialBlocks
parameter_list|)
block|{
name|this
operator|.
name|initialBlocks
operator|=
name|initialBlocks
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Initializing with {} initial blocks"
argument_list|,
name|Integer
operator|.
name|valueOf
argument_list|(
name|initialBlocks
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|lock
operator|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
expr_stmt|;
name|initialize
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|initialize
parameter_list|()
block|{
name|directBlock
operator|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
if|if
condition|(
name|initialBlocks
operator|>
literal|1
condition|)
block|{
name|indirectBlocks
operator|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
index|[]
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
name|initialBlocks
operator|-
literal|1
condition|;
operator|++
name|i
control|)
block|{
name|indirectBlocks
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
block|}
name|indirectBlocksAllocated
operator|=
name|initialBlocks
operator|-
literal|1
expr_stmt|;
block|}
name|size
operator|=
literal|0L
expr_stmt|;
block|}
specifier|private
name|byte
index|[]
name|getBlock
parameter_list|(
name|int
name|currentBlock
parameter_list|)
block|{
if|if
condition|(
name|currentBlock
operator|==
literal|0
condition|)
block|{
return|return
name|directBlock
return|;
block|}
else|else
block|{
return|return
name|indirectBlocks
index|[
name|currentBlock
operator|-
literal|1
index|]
return|;
block|}
block|}
specifier|private
name|void
name|ensureCapacity
parameter_list|(
name|long
name|capacity
parameter_list|)
block|{
comment|// if direct block is enough do nothing
if|if
condition|(
name|capacity
operator|<=
name|BLOCK_SIZE
condition|)
block|{
return|return;
block|}
comment|// lazily allocate indirect blocks
if|if
condition|(
name|indirectBlocks
operator|==
literal|null
condition|)
block|{
name|indirectBlocks
operator|=
operator|new
name|byte
index|[
name|NUMBER_OF_BLOCKS
index|]
index|[]
expr_stmt|;
block|}
comment|// consider already present direct block, don't add + 1
name|int
name|blocksRequired
init|=
operator|(
name|int
operator|)
operator|(
operator|(
name|capacity
operator|-
literal|1L
operator|)
operator|/
name|BLOCK_SIZE
operator|)
decl_stmt|;
if|if
condition|(
name|blocksRequired
operator|>
name|NUMBER_OF_BLOCKS
condition|)
block|{
throw|throw
operator|new
name|AssertionError
argument_list|(
literal|"memory values bigger than 16MB not supported"
argument_list|)
throw|;
block|}
if|if
condition|(
name|blocksRequired
operator|>
name|indirectBlocksAllocated
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|indirectBlocksAllocated
init|;
name|i
operator|<
name|blocksRequired
condition|;
operator|++
name|i
control|)
block|{
name|indirectBlocks
index|[
name|i
index|]
operator|=
operator|new
name|byte
index|[
name|BLOCK_SIZE
index|]
expr_stmt|;
name|indirectBlocksAllocated
operator|+=
literal|1
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|ManagedLock
name|readLock
parameter_list|()
block|{
return|return
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
return|;
block|}
specifier|private
name|ManagedLock
name|writeLock
parameter_list|()
block|{
return|return
name|ManagedLock
operator|.
name|acquire
argument_list|(
name|lock
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Reset content"
argument_list|)
expr_stmt|;
block|}
try|try
init|(
name|ManagedLock
name|lock
init|=
name|writeLock
argument_list|()
init|)
block|{
name|initialize
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|size
parameter_list|()
block|{
try|try
init|(
name|ManagedLock
name|lock
init|=
name|readLock
argument_list|()
init|)
block|{
return|return
name|size
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
name|byte
index|[]
name|dst
parameter_list|,
name|long
name|position
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
try|try
init|(
name|ManagedLock
name|lock
init|=
name|readLock
argument_list|()
init|)
block|{
if|if
condition|(
name|position
operator|>=
name|size
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
name|int
name|toRead
init|=
operator|(
name|int
operator|)
name|min
argument_list|(
name|min
argument_list|(
name|size
operator|-
name|position
argument_list|,
name|len
argument_list|)
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|int
name|currentBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|/
name|BLOCK_SIZE
operator|)
decl_stmt|;
name|int
name|startIndexInBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|-
operator|(
name|currentBlock
operator|*
operator|(
name|long
operator|)
name|BLOCK_SIZE
operator|)
operator|)
decl_stmt|;
name|int
name|read
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|read
operator|<
name|toRead
condition|)
block|{
name|int
name|lengthInBlock
init|=
name|min
argument_list|(
name|BLOCK_SIZE
operator|-
name|startIndexInBlock
argument_list|,
name|toRead
operator|-
name|read
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|getBlock
argument_list|(
name|currentBlock
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|block
argument_list|,
name|startIndexInBlock
argument_list|,
name|dst
argument_list|,
name|off
operator|+
name|read
argument_list|,
name|lengthInBlock
argument_list|)
expr_stmt|;
name|read
operator|+=
name|lengthInBlock
expr_stmt|;
name|startIndexInBlock
operator|=
literal|0
expr_stmt|;
name|currentBlock
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|read
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|long
name|transferTo
parameter_list|(
name|OutputStream
name|target
parameter_list|,
name|long
name|position
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
name|ManagedLock
name|lock
init|=
name|readLock
argument_list|()
init|)
block|{
name|long
name|transferred
init|=
literal|0L
decl_stmt|;
name|long
name|toTransfer
init|=
name|size
operator|-
name|position
decl_stmt|;
name|int
name|currentBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|/
name|BLOCK_SIZE
operator|)
decl_stmt|;
name|int
name|startIndexInBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|-
operator|(
name|currentBlock
operator|*
operator|(
name|long
operator|)
name|BLOCK_SIZE
operator|)
operator|)
decl_stmt|;
while|while
condition|(
name|transferred
operator|<
name|toTransfer
condition|)
block|{
name|int
name|lengthInBlock
init|=
operator|(
name|int
operator|)
name|min
argument_list|(
name|BLOCK_SIZE
operator|-
name|startIndexInBlock
argument_list|,
name|toTransfer
operator|-
name|transferred
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|getBlock
argument_list|(
name|currentBlock
argument_list|)
decl_stmt|;
name|target
operator|.
name|write
argument_list|(
name|block
argument_list|,
name|startIndexInBlock
argument_list|,
name|lengthInBlock
argument_list|)
expr_stmt|;
name|transferred
operator|+=
name|lengthInBlock
expr_stmt|;
name|startIndexInBlock
operator|=
literal|0
expr_stmt|;
name|currentBlock
operator|+=
literal|1
expr_stmt|;
block|}
return|return
name|transferred
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|write
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|long
name|position
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
try|try
init|(
name|ManagedLock
name|lock
init|=
name|writeLock
argument_list|()
init|)
block|{
name|ensureCapacity
argument_list|(
name|position
operator|+
name|len
argument_list|)
expr_stmt|;
name|int
name|toWrite
init|=
name|min
argument_list|(
name|len
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
name|int
name|currentBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|/
name|BLOCK_SIZE
operator|)
decl_stmt|;
name|int
name|startIndexInBlock
init|=
operator|(
name|int
operator|)
operator|(
name|position
operator|-
operator|(
name|currentBlock
operator|*
operator|(
name|long
operator|)
name|BLOCK_SIZE
operator|)
operator|)
decl_stmt|;
name|int
name|written
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|written
operator|<
name|toWrite
condition|)
block|{
name|int
name|lengthInBlock
init|=
name|min
argument_list|(
name|BLOCK_SIZE
operator|-
name|startIndexInBlock
argument_list|,
name|toWrite
operator|-
name|written
argument_list|)
decl_stmt|;
name|byte
index|[]
name|block
init|=
name|getBlock
argument_list|(
name|currentBlock
argument_list|)
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|src
argument_list|,
name|off
operator|+
name|written
argument_list|,
name|block
argument_list|,
name|startIndexInBlock
argument_list|,
name|lengthInBlock
argument_list|)
expr_stmt|;
name|written
operator|+=
name|lengthInBlock
expr_stmt|;
name|startIndexInBlock
operator|=
literal|0
expr_stmt|;
name|currentBlock
operator|+=
literal|1
expr_stmt|;
block|}
comment|// REVIEW, possibility to fill with random data
name|size
operator|=
name|max
argument_list|(
name|size
argument_list|,
name|position
operator|+
name|written
argument_list|)
expr_stmt|;
return|return
name|written
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|writeAtEnd
parameter_list|(
name|byte
index|[]
name|src
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
block|{
try|try
init|(
name|ManagedLock
name|lock
init|=
name|writeLock
argument_list|()
init|)
block|{
return|return
name|write
argument_list|(
name|src
argument_list|,
name|size
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

