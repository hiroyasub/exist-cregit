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
name|IOException
import|;
end_import

begin_comment
comment|/**  * Factory to instantiate a cache object  * takes a different behaviour on Windows to Unix.  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|FilterInputStreamCacheFactory
block|{
specifier|private
specifier|final
specifier|static
name|boolean
name|WINDOWS_PLATFORM
decl_stmt|;
static|static
block|{
specifier|final
name|String
name|osName
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"os.name"
argument_list|)
decl_stmt|;
name|WINDOWS_PLATFORM
operator|=
operator|(
name|osName
operator|!=
literal|null
operator|&&
name|osName
operator|.
name|toLowerCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"windows"
argument_list|)
operator|)
expr_stmt|;
block|}
comment|/**      * Get a suitable Cache instance      *       * By default, on Windows platforms FileFilterInputStreamCache is used      * on other platforms MemoryMappedFileFilterInputStreamCache.      * This is because Users reported problems with the      * memory use of MemoryMappedFileFilterInputStreamCache on Windows.      *       * The class used can be overriden by setting the system property 'filterInputStreamCache=type'      * where type is one of 'file', 'memoryMapped', or 'memory'.      *       * file: Random IO on a temporary file      * memoryMapped: Memory Mapped IO on a temporary file (fast for multiple reads)      * memory: All cache is kept in RAM (very fast for everything)      */
specifier|public
specifier|static
name|FilterInputStreamCache
name|getCacheInstance
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|String
name|cacheType
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"filterInputStreamCache"
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheType
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
block|{
return|return
operator|new
name|FileFilterInputStreamCache
argument_list|()
return|;
block|}
if|else if
condition|(
name|cacheType
operator|.
name|equals
argument_list|(
literal|"memoryMapped"
argument_list|)
condition|)
block|{
return|return
operator|new
name|MemoryMappedFileFilterInputStreamCache
argument_list|()
return|;
block|}
if|else if
condition|(
name|cacheType
operator|.
name|equals
argument_list|(
literal|"memory"
argument_list|)
condition|)
block|{
return|return
operator|new
name|MemoryFilterInputStreamCache
argument_list|()
return|;
block|}
else|else
block|{
if|if
condition|(
name|WINDOWS_PLATFORM
condition|)
block|{
return|return
operator|new
name|FileFilterInputStreamCache
argument_list|()
return|;
block|}
else|else
block|{
return|return
operator|new
name|MemoryMappedFileFilterInputStreamCache
argument_list|()
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

