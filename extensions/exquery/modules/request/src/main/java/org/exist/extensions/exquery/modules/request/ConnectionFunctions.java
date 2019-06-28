begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2013, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
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
name|modules
operator|.
name|request
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|FunctionReturnSequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|IntegerValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|StringValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
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
comment|/**  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|ConnectionFunctions
extends|extends
name|AbstractRequestModuleFunction
block|{
specifier|private
specifier|final
specifier|static
name|QName
name|qnAddress
init|=
operator|new
name|QName
argument_list|(
literal|"address"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnRemoteHostname
init|=
operator|new
name|QName
argument_list|(
literal|"remote-hostname"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnRemoteAddress
init|=
operator|new
name|QName
argument_list|(
literal|"remote-address"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|QName
name|qnRemotePort
init|=
operator|new
name|QName
argument_list|(
literal|"remote-port"
argument_list|,
name|RequestModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RequestModule
operator|.
name|PREFIX
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_ADDRESS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnAddress
argument_list|,
literal|"Gets the IP address of the server that received the HTTP Request"
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The IP address of the server."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOTE_HOSTNAME
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRemoteHostname
argument_list|,
literal|"Gets the fully qualified hostname of the client or the last proxy that sent the HTTP Request. If the name of the remote host cannot be established, this method behaves as request:remote-address(), and returns the IP address."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The Hostname of the client that issues the HTTP Request."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOTE_ADDRESS
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRemoteAddress
argument_list|,
literal|"Gets the IP address of the client or the last proxy that sent the HTTP Request."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The IP address of the client."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|FNS_REMOTE_PORT
init|=
operator|new
name|FunctionSignature
argument_list|(
name|qnRemotePort
argument_list|,
literal|"Gets the TCP port number of the client socket or the last proxy that sent the HTTP Request.."
argument_list|,
literal|null
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The TCP port number of the client."
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|ConnectionFunctions
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|HttpRequest
name|request
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
name|qnAddress
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|request
operator|.
name|getAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnRemoteHostname
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|request
operator|.
name|getRemoteHostname
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnRemoteAddress
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|StringValue
argument_list|(
name|request
operator|.
name|getRemoteAddress
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
name|qnRemotePort
operator|.
name|getLocalPart
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|=
operator|new
name|IntegerValue
argument_list|(
name|request
operator|.
name|getRemotePort
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Unknown function call: "
operator|+
name|getSignature
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

