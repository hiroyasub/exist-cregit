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
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|adapters
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
name|io
operator|.
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|*
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
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
name|io
operator|.
name|CachingFilterInputStream
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
name|io
operator|.
name|FilterInputStreamCache
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
name|io
operator|.
name|FilterInputStreamCacheFactory
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
name|io
operator|.
name|FilterInputStreamCacheFactory
operator|.
name|FilterInputStreamCacheConfiguration
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|http
operator|.
name|HttpMethod
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|http
operator|.
name|HttpRequest
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|HttpServletRequestAdapter
implements|implements
name|HttpRequest
block|{
specifier|private
specifier|final
name|HttpServletRequest
name|request
decl_stmt|;
specifier|private
specifier|final
name|FilterInputStreamCacheConfiguration
name|cacheConfiguration
decl_stmt|;
specifier|private
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
specifier|private
name|FilterInputStreamCache
name|cache
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|formFields
init|=
literal|null
decl_stmt|;
specifier|public
name|HttpServletRequestAdapter
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|FilterInputStreamCacheConfiguration
name|cacheConfiguration
parameter_list|)
block|{
name|this
operator|.
name|request
operator|=
name|request
expr_stmt|;
name|this
operator|.
name|cacheConfiguration
operator|=
name|cacheConfiguration
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|HttpMethod
name|getMethod
parameter_list|()
block|{
return|return
name|HttpMethod
operator|.
name|valueOf
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|request
operator|.
name|getPathInfo
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|InputStream
name|getInputStream
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|is
operator|==
literal|null
condition|)
block|{
name|cache
operator|=
name|FilterInputStreamCacheFactory
operator|.
name|getCacheInstance
argument_list|(
name|cacheConfiguration
argument_list|)
expr_stmt|;
name|is
operator|=
operator|new
name|CachingFilterInputStream
argument_list|(
name|cache
argument_list|,
name|request
operator|.
name|getInputStream
argument_list|()
argument_list|)
expr_stmt|;
name|is
operator|.
name|mark
argument_list|(
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|is
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
return|return
name|is
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getContentLength
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContentLength
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContentType
parameter_list|()
block|{
return|return
name|request
operator|.
name|getContentType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getCharacterEncoding
parameter_list|()
block|{
return|return
name|request
operator|.
name|getCharacterEncoding
argument_list|()
return|;
block|}
comment|//TODO consider moving more of this code into EXQuery impl
annotation|@
name|Override
specifier|public
name|Object
name|getFormParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"GET"
argument_list|)
condition|)
block|{
return|return
name|getGetParameters
argument_list|(
name|key
argument_list|)
return|;
block|}
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"POST"
argument_list|)
operator|&&
name|request
operator|.
name|getContentType
argument_list|()
operator|.
name|equals
argument_list|(
literal|"application/x-www-form-urlencoded"
argument_list|)
condition|)
block|{
if|if
condition|(
name|formFields
operator|==
literal|null
condition|)
block|{
try|try
block|{
specifier|final
name|InputStream
name|in
init|=
name|getInputStream
argument_list|()
decl_stmt|;
name|formFields
operator|=
name|extractFormFields
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//TODO log or something?
name|ioe
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|formFieldValues
init|=
name|formFields
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|formFieldValues
operator|!=
literal|null
condition|)
block|{
return|return
name|formFieldValues
return|;
block|}
else|else
block|{
comment|//fallback to get parameters
return|return
name|getGetParameters
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|//TODO consider moving more of this code into EXQuery impl
annotation|@
name|Override
specifier|public
name|Object
name|getQueryParam
parameter_list|(
name|String
name|key
parameter_list|)
block|{
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|equals
argument_list|(
literal|"GET"
argument_list|)
condition|)
block|{
return|return
name|getGetParameters
argument_list|(
name|key
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Object
name|getGetParameters
parameter_list|(
name|String
name|key
parameter_list|)
block|{
specifier|final
name|String
index|[]
name|values
init|=
name|request
operator|.
name|getParameterValues
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|values
operator|.
name|length
operator|==
literal|1
condition|)
block|{
return|return
name|values
index|[
literal|0
index|]
return|;
block|}
else|else
block|{
return|return
name|Arrays
operator|.
name|asList
argument_list|(
name|values
argument_list|)
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|extractFormFields
parameter_list|(
name|InputStream
name|in
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|fields
init|=
operator|new
name|Hashtable
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
specifier|final
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|in
argument_list|)
decl_stmt|;
try|try
block|{
name|int
name|read
init|=
operator|-
literal|1
decl_stmt|;
name|char
index|[]
name|cbuf
init|=
operator|new
name|char
index|[
literal|1024
index|]
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|reader
operator|.
name|read
argument_list|(
name|cbuf
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|cbuf
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
specifier|final
name|StringTokenizer
name|st
init|=
operator|new
name|StringTokenizer
argument_list|(
name|builder
operator|.
name|toString
argument_list|()
argument_list|,
literal|"&"
argument_list|)
decl_stmt|;
name|String
name|key
init|=
literal|null
decl_stmt|;
name|String
name|val
init|=
literal|null
decl_stmt|;
while|while
condition|(
name|st
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|pair
init|=
name|st
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|int
name|pos
init|=
name|pair
operator|.
name|indexOf
argument_list|(
literal|'='
argument_list|)
decl_stmt|;
if|if
condition|(
name|pos
operator|==
operator|-
literal|1
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|()
throw|;
block|}
try|try
block|{
name|key
operator|=
name|java
operator|.
name|net
operator|.
name|URLDecoder
operator|.
name|decode
argument_list|(
name|pair
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
argument_list|)
expr_stmt|;
name|val
operator|=
name|java
operator|.
name|net
operator|.
name|URLDecoder
operator|.
name|decode
argument_list|(
name|pair
operator|.
name|substring
argument_list|(
name|pos
operator|+
literal|1
argument_list|,
name|pair
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|e
argument_list|)
throw|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|vals
init|=
name|fields
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|vals
operator|==
literal|null
condition|)
block|{
name|vals
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|vals
operator|.
name|add
argument_list|(
name|val
argument_list|)
expr_stmt|;
name|fields
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|vals
argument_list|)
expr_stmt|;
block|}
return|return
name|fields
return|;
block|}
block|}
end_class

end_unit

