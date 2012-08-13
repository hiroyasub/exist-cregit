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
name|IOException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletConfig
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|ServletException
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
operator|.
name|AbstractExistHttpServlet
import|;
end_import

begin_import
import|import
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
operator|.
name|HttpServletRequestAdapter
import|;
end_import

begin_import
import|import
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
operator|.
name|HttpServletResponseAdapter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Subject
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
name|DBBroker
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
name|Configuration
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
name|HttpRequest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|RestXqServiceRegistry
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|RestXqServlet
extends|extends
name|AbstractExistHttpServlet
block|{
specifier|final
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
name|RestXqServiceRegistry
name|getRegistry
parameter_list|()
block|{
return|return
name|RestXqServiceRegistryManager
operator|.
name|getRegistry
argument_list|(
name|getPool
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
operator|.
name|init
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|service
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
comment|//authenticate
specifier|final
name|Subject
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user or password");
return|return;
block|}
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
specifier|final
name|Configuration
name|configuration
init|=
name|broker
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
specifier|final
name|HttpRequest
name|requestAdapter
init|=
operator|new
name|HttpServletRequestAdapter
argument_list|(
name|request
argument_list|,
operator|new
name|FilterInputStreamCacheConfiguration
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getCacheClass
parameter_list|()
block|{
return|return
operator|(
name|String
operator|)
name|configuration
operator|.
name|getProperty
argument_list|(
name|Configuration
operator|.
name|BINARY_CACHE_CLASS_PROPERTY
argument_list|)
return|;
block|}
block|}
argument_list|)
decl_stmt|;
specifier|final
name|RestXqService
name|service
init|=
name|getRegistry
argument_list|()
operator|.
name|findService
argument_list|(
name|requestAdapter
argument_list|)
decl_stmt|;
if|if
condition|(
name|service
operator|!=
literal|null
condition|)
block|{
name|service
operator|.
name|service
argument_list|(
name|requestAdapter
argument_list|,
operator|new
name|HttpServletResponseAdapter
argument_list|(
name|response
argument_list|)
argument_list|,
operator|new
name|ResourceFunctionExecutorImpl
argument_list|(
name|getPool
argument_list|()
argument_list|)
argument_list|,
operator|new
name|RestXqServiceSerializerImpl
argument_list|(
name|getPool
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|service
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|EXistException
name|ee
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|ee
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ee
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|RestXqServiceException
name|rqse
parameter_list|)
block|{
comment|//TODO should probably be caught higher up and returned as a HTTP Response? maybe need two different types of exception to differentiate critical vs processing exception
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|rqse
operator|.
name|getMessage
argument_list|()
argument_list|,
name|rqse
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|rqse
operator|.
name|getMessage
argument_list|()
argument_list|,
name|rqse
argument_list|)
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
block|{
name|getPool
argument_list|()
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|Logger
name|getLog
parameter_list|()
block|{
return|return
name|log
return|;
block|}
block|}
end_class

end_unit
