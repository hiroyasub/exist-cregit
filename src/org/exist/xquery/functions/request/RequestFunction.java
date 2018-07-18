begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
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
name|http
operator|.
name|servlets
operator|.
name|RequestWrapper
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
name|*
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
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_comment
comment|/**  * Abstract for functions in the {@link RequestModule}  * which need access to the http request.  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|RequestFunction
extends|extends
name|BasicFunction
block|{
specifier|public
name|RequestFunction
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
specifier|final
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
return|return
name|eval
argument_list|(
name|args
argument_list|,
name|Optional
operator|.
name|ofNullable
argument_list|(
name|context
operator|.
name|getHttpContext
argument_list|()
argument_list|)
operator|.
name|map
argument_list|(
name|XQueryContext
operator|.
name|HttpContext
operator|::
name|getRequest
argument_list|)
argument_list|)
return|;
block|}
comment|/**      * Evaluate the function with the Http Request.      *      * @param args the arguments to the function.      * @param request the http request      *      * @return the result of the function.      *      * @throws XPathException an XPath Exception      */
specifier|protected
specifier|abstract
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Optional
argument_list|<
name|RequestWrapper
argument_list|>
name|request
parameter_list|)
throws|throws
name|XPathException
function_decl|;
block|}
end_class

end_unit

