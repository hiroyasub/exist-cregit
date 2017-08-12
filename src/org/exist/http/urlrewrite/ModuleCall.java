begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|urlrewrite
package|;
end_package

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
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
name|Expression
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
name|Module
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
name|UserDefinedFunction
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
name|ExternalModule
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
name|FunctionCall
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
name|dom
operator|.
name|QName
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
name|javax
operator|.
name|servlet
operator|.
name|FilterChain
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
name|util
operator|.
name|ArrayList
import|;
end_import

begin_class
specifier|public
class|class
name|ModuleCall
extends|extends
name|URLRewrite
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ModuleCall
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|FunctionCall
name|call
decl_stmt|;
specifier|public
name|ModuleCall
parameter_list|(
name|Element
name|config
parameter_list|,
name|XQueryContext
name|context
parameter_list|,
name|String
name|uri
parameter_list|)
throws|throws
name|ServletException
block|{
name|super
argument_list|(
name|config
argument_list|,
name|uri
argument_list|)
expr_stmt|;
name|String
name|funcName
init|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"function"
argument_list|)
decl_stmt|;
if|if
condition|(
name|funcName
operator|==
literal|null
operator|||
name|funcName
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"<exist:call> requires an attribute 'function'."
argument_list|)
throw|;
block|}
name|int
name|arity
init|=
literal|0
decl_stmt|;
specifier|final
name|int
name|p
init|=
name|funcName
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|>
operator|-
literal|1
condition|)
block|{
specifier|final
name|String
name|arityStr
init|=
name|funcName
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
decl_stmt|;
try|try
block|{
name|arity
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|arityStr
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"<exist:call>: could not parse parameter count in function attribute: "
operator|+
name|arityStr
argument_list|)
throw|;
block|}
name|funcName
operator|=
name|funcName
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|QName
name|fqn
init|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|funcName
argument_list|)
decl_stmt|;
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|fqn
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|UserDefinedFunction
name|func
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|module
operator|!=
literal|null
condition|)
block|{
name|func
operator|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|fqn
argument_list|,
name|arity
argument_list|,
name|context
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|func
operator|=
name|context
operator|.
name|resolveFunction
argument_list|(
name|fqn
argument_list|,
name|arity
argument_list|)
expr_stmt|;
block|}
name|call
operator|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|func
argument_list|)
expr_stmt|;
name|call
operator|.
name|setArguments
argument_list|(
operator|new
name|ArrayList
argument_list|<>
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
decl||
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|protected
name|ModuleCall
parameter_list|(
name|ModuleCall
name|other
parameter_list|)
block|{
name|super
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|this
operator|.
name|call
operator|=
name|other
operator|.
name|call
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doRewrite
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
try|try
block|{
specifier|final
name|Sequence
name|result
init|=
name|call
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Found: "
operator|+
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|request
operator|.
name|setAttribute
argument_list|(
name|XQueryURLRewrite
operator|.
name|RQ_ATTR_RESULT
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Called function threw exception: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|URLRewrite
name|copy
parameter_list|()
block|{
return|return
operator|new
name|ModuleCall
argument_list|(
name|this
argument_list|)
return|;
block|}
block|}
end_class

end_unit

