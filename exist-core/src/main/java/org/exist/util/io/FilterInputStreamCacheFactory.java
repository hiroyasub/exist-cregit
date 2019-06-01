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

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|LambdaMetafactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
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
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_comment
comment|/**  * Factory to instantiate a cache object  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|FilterInputStreamCacheFactory
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
name|FilterInputStreamCacheFactory
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|MethodHandles
operator|.
name|Lookup
name|LOOKUP
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
specifier|public
interface|interface
name|FilterInputStreamCacheConfiguration
block|{
name|String
name|getCacheClass
parameter_list|()
function_decl|;
block|}
specifier|private
name|FilterInputStreamCacheFactory
parameter_list|()
block|{
block|}
comment|/**      * Get a suitable Cache instance      *      */
specifier|public
specifier|static
name|FilterInputStreamCache
name|getCacheInstance
parameter_list|(
specifier|final
name|FilterInputStreamCacheConfiguration
name|cacheConfiguration
parameter_list|,
specifier|final
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|FilterInputStreamCache
name|cache
init|=
operator|new
name|FilterInputStreamCacheFactory
argument_list|()
operator|.
name|instantiate
argument_list|(
name|cacheConfiguration
argument_list|,
name|is
argument_list|)
decl_stmt|;
if|if
condition|(
name|cache
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Could not load cache for class: "
operator|+
name|cacheConfiguration
operator|.
name|getCacheClass
argument_list|()
argument_list|)
throw|;
block|}
name|FilterInputStreamCacheMonitor
operator|.
name|getInstance
argument_list|()
operator|.
name|register
argument_list|(
name|cache
argument_list|)
expr_stmt|;
return|return
name|cache
return|;
block|}
specifier|private
name|FilterInputStreamCache
name|instantiate
parameter_list|(
specifier|final
name|FilterInputStreamCacheConfiguration
name|cacheConfiguration
parameter_list|,
specifier|final
name|InputStream
name|is
parameter_list|)
block|{
try|try
block|{
specifier|final
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
name|cacheConfiguration
operator|.
name|getCacheClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|LOOKUP
operator|.
name|findConstructor
argument_list|(
name|clazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|InputStream
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Function
argument_list|<
name|InputStream
argument_list|,
name|FilterInputStreamCache
argument_list|>
name|constructor
init|=
operator|(
name|Function
argument_list|<
name|InputStream
argument_list|,
name|FilterInputStreamCache
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|LOOKUP
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|Function
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
decl_stmt|;
return|return
name|constructor
operator|.
name|apply
argument_list|(
name|is
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|instanceof
name|InterruptedException
condition|)
block|{
comment|// NOTE: must set interrupted flag
name|Thread
operator|.
name|currentThread
argument_list|()
operator|.
name|interrupt
argument_list|()
expr_stmt|;
block|}
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
end_class

end_unit
