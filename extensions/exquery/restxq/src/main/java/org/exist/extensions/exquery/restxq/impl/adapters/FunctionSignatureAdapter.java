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
name|javax
operator|.
name|xml
operator|.
name|namespace
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
name|value
operator|.
name|FunctionParameterSequenceType
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
name|SequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery
operator|.
name|FunctionArgument
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|Annotation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exquery
operator|.
name|xquery3
operator|.
name|FunctionSignature
import|;
end_import

begin_comment
comment|/**  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
class|class
name|FunctionSignatureAdapter
implements|implements
name|FunctionSignature
block|{
specifier|private
name|QName
name|name
decl_stmt|;
specifier|private
name|int
name|argumentCount
decl_stmt|;
specifier|private
name|FunctionParameterSequenceTypeAdapter
index|[]
name|arguments
decl_stmt|;
specifier|private
name|AnnotationAdapter
index|[]
name|annotations
decl_stmt|;
specifier|protected
name|FunctionSignatureAdapter
parameter_list|()
block|{
block|}
specifier|public
name|FunctionSignatureAdapter
parameter_list|(
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
name|functionSignature
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|functionSignature
operator|.
name|getName
argument_list|()
operator|.
name|toJavaQName
argument_list|()
expr_stmt|;
name|this
operator|.
name|argumentCount
operator|=
name|functionSignature
operator|.
name|getArgumentCount
argument_list|()
expr_stmt|;
specifier|final
name|SequenceType
index|[]
name|fnArgumentTypes
init|=
name|functionSignature
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
if|if
condition|(
name|fnArgumentTypes
operator|!=
literal|null
condition|)
block|{
name|arguments
operator|=
operator|new
name|FunctionParameterSequenceTypeAdapter
index|[
name|fnArgumentTypes
operator|.
name|length
index|]
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
name|fnArgumentTypes
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|arguments
index|[
name|i
index|]
operator|=
operator|new
name|FunctionParameterSequenceTypeAdapter
argument_list|(
operator|(
name|FunctionParameterSequenceType
operator|)
name|fnArgumentTypes
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|arguments
operator|=
literal|null
expr_stmt|;
block|}
specifier|final
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Annotation
index|[]
name|fnAnnotations
init|=
name|functionSignature
operator|.
name|getAnnotations
argument_list|()
decl_stmt|;
if|if
condition|(
name|fnAnnotations
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|annotations
operator|=
operator|new
name|AnnotationAdapter
index|[
name|fnAnnotations
operator|.
name|length
index|]
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
name|fnAnnotations
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|this
operator|.
name|annotations
index|[
name|i
index|]
operator|=
operator|new
name|AnnotationAdapter
argument_list|(
name|fnAnnotations
index|[
name|i
index|]
argument_list|,
name|this
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|this
operator|.
name|annotations
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|String
name|str
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|str
operator|=
name|name
operator|.
name|getPrefix
argument_list|()
operator|+
literal|":"
operator|+
name|name
operator|.
name|getLocalPart
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|str
operator|=
name|name
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//clark-notation
block|}
name|str
operator|+=
literal|"#"
operator|+
name|getArgumentCount
argument_list|()
expr_stmt|;
return|return
name|str
return|;
block|}
annotation|@
name|Override
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|private
name|void
name|setName
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getArgumentCount
parameter_list|()
block|{
return|return
name|argumentCount
return|;
block|}
specifier|private
name|void
name|setArgumentCount
parameter_list|(
name|int
name|argumentCount
parameter_list|)
block|{
name|this
operator|.
name|argumentCount
operator|=
name|argumentCount
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Annotation
index|[]
name|getAnnotations
parameter_list|()
block|{
return|return
name|annotations
return|;
block|}
specifier|private
name|void
name|setAnnotations
parameter_list|(
name|AnnotationAdapter
index|[]
name|annotations
parameter_list|)
block|{
name|this
operator|.
name|annotations
operator|=
name|annotations
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|FunctionArgument
index|[]
name|getArguments
parameter_list|()
block|{
return|return
name|arguments
return|;
block|}
specifier|private
name|void
name|setParameters
parameter_list|(
specifier|final
name|FunctionParameterSequenceTypeAdapter
index|[]
name|parameters
parameter_list|)
block|{
name|this
operator|.
name|arguments
operator|=
name|parameters
expr_stmt|;
block|}
block|}
end_class

end_unit

