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
name|xquery
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
name|extensions
operator|.
name|exquery
operator|.
name|restxq
operator|.
name|impl
operator|.
name|ResourceFunctionExecutorImpl
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
name|BasicFunction
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
name|Variable
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
name|Type
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|UriFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"base-uri"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function returns the implementation defined base URI of the Resource Function."
argument_list|,
name|FunctionSignature
operator|.
name|NO_ARGS
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The base URI of the Resource Function."
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"uri"
argument_list|,
name|RestXqModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|RestXqModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"This function is returns the complete URI that addresses the Resource Function. Typically this is the rest:base-uri() appended with the path from the Path Annotation (if present) of the Resource Function."
argument_list|,
name|FunctionSignature
operator|.
name|NO_ARGS
argument_list|,
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|ONE
argument_list|,
literal|"The URI which addressed the Resource Function."
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|UriFunctions
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
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|Variable
name|var
decl_stmt|;
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"base-uri"
argument_list|)
condition|)
block|{
name|var
operator|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|ResourceFunctionExecutorImpl
operator|.
name|XQ_VAR_BASE_URI
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|var
operator|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|ResourceFunctionExecutorImpl
operator|.
name|XQ_VAR_URI
argument_list|)
expr_stmt|;
block|}
return|return
name|var
operator|.
name|getValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit
