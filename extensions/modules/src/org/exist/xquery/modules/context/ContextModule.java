begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Context Module Extension  *  * Released under the BSD License  *  * Copyright (c) 2006, Adam retter<adam.retter@devon.gov.uk>  * All rights reserved.  *   * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:  * 		Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.  *  	Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the documentation and/or other materials provided with the distribution.  *  	Neither the name of the Devon Portal Project nor the names of its contributors may be used to endorse or promote products derived from this software without specific prior written permission.  *    *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS  *  IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE  *  IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR  *  PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR  *  CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  *  SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,  *  PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;  *  OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF  *  LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR  *  OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF  *  ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|context
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * eXist Context Module Extension  *   * An extension module for the eXist Native XML Database that allows operations  * on the Context of the executing XQuery to be performed  *   * @author Adam Retter<adam.retter@devon.gov.uk>  * @serial 2006-03-01  * @version 1.3  *  * @see org.exist.xquery.AbstractInternalModule#AbstractInternalModule(org.exist.xquery.FunctionDef[])  */
end_comment

begin_class
specifier|public
class|class
name|ContextModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/context"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"context"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|SetSerializerFunction
operator|.
name|signature
argument_list|,
name|SetSerializerFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|SetVarFunction
operator|.
name|signature
argument_list|,
name|SetVarFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetVarFunction
operator|.
name|signature
argument_list|,
name|GetVarFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|ContextModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for performing operations in the context of the current xquery"
return|;
block|}
block|}
end_class

end_unit

