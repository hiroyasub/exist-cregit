begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2012, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
import|import
name|java
operator|.
name|io
operator|.
name|File
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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
name|RandomAccessFile
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_comment
comment|/**  * Cache implementation for CachingFilterInputStream  * Backed by a Memory Mapped File  *  * @version 1.0  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|MemoryMappedFileFilterInputStreamCache
implements|implements
name|FilterInputStreamCache
block|{
specifier|private
specifier|final
specifier|static
name|long
name|DEFAULT_MEMORY_MAP_SIZE
init|=
literal|64
operator|*
literal|1024
operator|*
literal|1024
decl_stmt|;
comment|//4MB
specifier|private
specifier|final
name|RandomAccessFile
name|raf
decl_stmt|;
specifier|private
specifier|final
name|FileChannel
name|channel
decl_stmt|;
specifier|private
name|MappedByteBuffer
name|buf
decl_stmt|;
specifier|private
name|File
name|tempFile
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
name|long
name|memoryMapSize
init|=
name|DEFAULT_MEMORY_MAP_SIZE
decl_stmt|;
specifier|private
name|boolean
name|externalFile
init|=
literal|true
decl_stmt|;
specifier|public
name|MemoryMappedFileFilterInputStreamCache
parameter_list|()
throws|throws
name|IOException
block|{
name|this
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|MemoryMappedFileFilterInputStreamCache
parameter_list|(
specifier|final
name|File
name|f
parameter_list|)
throws|throws
name|FileNotFoundException
throws|,
name|IOException
block|{
if|if
condition|(
name|f
operator|==
literal|null
condition|)
block|{
name|tempFile
operator|=
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
operator|.
name|getTemporaryFile
argument_list|()
expr_stmt|;
name|externalFile
operator|=
literal|false
expr_stmt|;
block|}
else|else
block|{
name|tempFile
operator|=
name|f
expr_stmt|;
name|externalFile
operator|=
literal|true
expr_stmt|;
block|}
comment|/**          * Check the applicability of these bugs to this code:          *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4724038          *  http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6417205 (fixed in 1.6)          */
name|this
operator|.
name|raf
operator|=
operator|new
name|RandomAccessFile
argument_list|(
name|tempFile
argument_list|,
literal|"rw"
argument_list|)
expr_stmt|;
name|this
operator|.
name|channel
operator|=
name|raf
operator|.
name|getChannel
argument_list|()
expr_stmt|;
name|this
operator|.
name|buf
operator|=
name|channel
operator|.
name|map
argument_list|(
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_WRITE
argument_list|,
literal|0
argument_list|,
name|getMemoryMapSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|long
name|getMemoryMapSize
parameter_list|()
block|{
return|return
name|memoryMapSize
return|;
block|}
specifier|private
name|void
name|increaseSize
parameter_list|(
name|long
name|bytes
parameter_list|)
throws|throws
name|IOException
block|{
name|long
name|factor
init|=
operator|(
name|bytes
operator|/
name|getMemoryMapSize
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
name|factor
operator|==
literal|0
operator|||
name|bytes
operator|%
name|getMemoryMapSize
argument_list|()
operator|>
literal|0
condition|)
block|{
name|factor
operator|++
expr_stmt|;
block|}
name|buf
operator|.
name|force
argument_list|()
expr_stmt|;
comment|//TODO revisit this based on the comment below, I now believe setting position in map does work, but you have to have the correct offset added in as well! Adam
name|int
name|position
init|=
name|buf
operator|.
name|position
argument_list|()
decl_stmt|;
name|buf
operator|=
name|channel
operator|.
name|map
argument_list|(
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_WRITE
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|capacity
argument_list|()
operator|+
operator|(
name|getMemoryMapSize
argument_list|()
operator|*
name|factor
operator|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|position
argument_list|(
name|position
argument_list|)
expr_stmt|;
comment|//setting the position in the map() call above does not seem to work!
comment|//bufAccessor.refresh();
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|<
name|len
condition|)
block|{
comment|//we need to remap the file
name|increaseSize
argument_list|(
name|len
operator|-
name|buf
operator|.
name|remaining
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|put
argument_list|(
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|buf
operator|.
name|remaining
argument_list|()
operator|<
literal|1
condition|)
block|{
comment|//we need to remap the file
name|increaseSize
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|put
argument_list|(
operator|(
name|byte
operator|)
name|i
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|byte
name|get
parameter_list|(
name|int
name|off
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|off
operator|>
name|buf
operator|.
name|capacity
argument_list|()
condition|)
block|{
comment|//we need to remap the file
name|increaseSize
argument_list|(
name|off
operator|-
name|buf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|get
argument_list|(
name|off
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|buf
operator|.
name|capacity
argument_list|()
operator|-
name|buf
operator|.
name|remaining
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyTo
parameter_list|(
name|int
name|cacheOffset
parameter_list|,
name|byte
index|[]
name|b
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|off
operator|+
name|len
operator|>
name|buf
operator|.
name|capacity
argument_list|()
condition|)
block|{
comment|//we need to remap the file
name|increaseSize
argument_list|(
name|off
operator|+
name|len
operator|-
name|buf
operator|.
name|capacity
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//get the current position
specifier|final
name|int
name|position
init|=
name|buf
operator|.
name|position
argument_list|()
decl_stmt|;
try|try
block|{
comment|//move to the offset
name|buf
operator|.
name|position
argument_list|(
name|cacheOffset
argument_list|)
expr_stmt|;
comment|//read the data;
name|byte
name|data
index|[]
init|=
operator|new
name|byte
index|[
name|len
index|]
decl_stmt|;
name|buf
operator|.
name|get
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|b
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|//reset the position
name|buf
operator|.
name|position
argument_list|(
name|position
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|invalidate
parameter_list|()
throws|throws
name|IOException
block|{
name|buf
operator|.
name|force
argument_list|()
expr_stmt|;
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
name|raf
operator|.
name|close
argument_list|()
expr_stmt|;
comment|//System.gc();
if|if
condition|(
name|tempFile
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|externalFile
operator|)
condition|)
block|{
name|TemporaryFileManager
operator|.
name|getInstance
argument_list|()
operator|.
name|returnTemporaryFile
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

