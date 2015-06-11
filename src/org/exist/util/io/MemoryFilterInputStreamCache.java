begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  Copyright (c) 2012, Adam Retter  All rights reserved.   Redistribution and use in source and binary forms, with or without  modification, are permitted provided that the following conditions are met:  * Redistributions of source code must retain the above copyright  notice, this list of conditions and the following disclaimer.  * Redistributions in binary form must reproduce the above copyright  notice, this list of conditions and the following disclaimer in the  documentation and/or other materials provided with the distribution.  * Neither the name of Adam Retter Consulting nor the  names of its contributors may be used to endorse or promote products  derived from this software without specific prior written permission.   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE  DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY  DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
import|;
end_import

begin_comment
comment|/**  * Cache implementation for CachingFilterInputStream Backed by an in-memory byte  * array  *  * @version 1.1  *  * @author Adam Retter<adam.retter@googlemail.com>  * @author Tobi Krebs<tobi.krebs AT gmail.com>  */
end_comment

begin_class
specifier|public
class|class
name|MemoryFilterInputStreamCache
extends|extends
name|AbstractFilterInputStreamCache
block|{
specifier|private
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
name|cache
init|=
operator|new
name|java
operator|.
name|io
operator|.
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
specifier|public
name|MemoryFilterInputStreamCache
parameter_list|(
name|InputStream
name|src
parameter_list|)
block|{
name|super
argument_list|(
name|src
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|cache
operator|.
name|write
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
specifier|final
name|int
name|i
parameter_list|)
throws|throws
name|IOException
block|{
name|cache
operator|.
name|write
argument_list|(
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
specifier|final
name|int
name|off
parameter_list|)
block|{
return|return
name|cache
operator|.
name|toByteArray
argument_list|()
index|[
name|off
index|]
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
name|cache
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|copyTo
parameter_list|(
specifier|final
name|int
name|cacheOffset
parameter_list|,
specifier|final
name|byte
index|[]
name|b
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
name|System
operator|.
name|arraycopy
argument_list|(
name|cache
operator|.
name|toByteArray
argument_list|()
argument_list|,
name|cacheOffset
argument_list|,
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
name|invalidate
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|cache
operator|!=
literal|null
condition|)
block|{
name|cache
operator|.
name|close
argument_list|()
expr_stmt|;
name|cache
operator|=
literal|null
expr_stmt|;
block|}
block|}
comment|/**      * Updates to the cache are not reflected in the underlying input stream      */
comment|//TODO refactor this so that updates to the cache are reflected
comment|/*@Override      public InputStream getIndependentInputStream() {      return new ByteArrayInputStream(cache.toByteArray());      }*/
block|}
end_class

end_unit

