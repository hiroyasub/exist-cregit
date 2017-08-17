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
name|net
operator|.
name|URI
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URISyntaxException
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|exist
operator|.
name|Namespaces
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
name|HttpResponseWrapper
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
name|urlrewrite
operator|.
name|XQueryURLRewrite
import|;
end_import

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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Base class for all rewritten URLs.  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|URLRewrite
block|{
specifier|private
specifier|final
specifier|static
name|String
name|UNSET
init|=
literal|""
decl_stmt|;
specifier|protected
name|String
name|uri
decl_stmt|;
specifier|protected
name|String
name|target
decl_stmt|;
specifier|protected
name|String
name|prefix
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|method
init|=
literal|null
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|attributes
init|=
literal|null
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|parameters
init|=
literal|null
decl_stmt|;
specifier|protected
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|headers
init|=
literal|null
decl_stmt|;
specifier|protected
name|boolean
name|absolute
init|=
literal|false
decl_stmt|;
specifier|protected
name|URLRewrite
parameter_list|(
name|Element
name|config
parameter_list|,
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"absolute"
argument_list|)
condition|)
name|absolute
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|config
operator|.
name|getAttribute
argument_list|(
literal|"absolute"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"method"
argument_list|)
condition|)
block|{
name|method
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"method"
argument_list|)
operator|.
name|toUpperCase
argument_list|()
expr_stmt|;
block|}
comment|// Check for add-parameter elements etc.
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
name|Node
name|node
init|=
name|config
operator|.
name|getFirstChild
argument_list|()
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
specifier|final
name|String
name|ns
init|=
name|node
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|ns
operator|!=
literal|null
operator|&&
name|Namespaces
operator|.
name|EXIST_NS
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
condition|)
block|{
specifier|final
name|Element
name|elem
init|=
operator|(
name|Element
operator|)
name|node
decl_stmt|;
if|if
condition|(
literal|"add-parameter"
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|addParameter
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"set-attribute"
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|setAttribute
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"clear-attribute"
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|unsetAttribute
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"set-header"
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|)
condition|)
block|{
name|setHeader
argument_list|(
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|,
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"value"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|URLRewrite
parameter_list|(
name|URLRewrite
name|other
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|other
operator|.
name|uri
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|other
operator|.
name|target
expr_stmt|;
name|this
operator|.
name|prefix
operator|=
name|other
operator|.
name|prefix
expr_stmt|;
name|this
operator|.
name|method
operator|=
name|other
operator|.
name|method
expr_stmt|;
block|}
specifier|protected
name|void
name|updateRequest
parameter_list|(
name|XQueryURLRewrite
operator|.
name|RequestWrapper
name|request
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|!=
literal|null
condition|)
block|{
name|request
operator|.
name|removePathPrefix
argument_list|(
name|prefix
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|rewriteRequest
parameter_list|(
name|XQueryURLRewrite
operator|.
name|RequestWrapper
name|request
parameter_list|)
block|{
comment|// do nothing by default
block|}
specifier|protected
name|void
name|setAbsolutePath
parameter_list|(
name|XQueryURLRewrite
operator|.
name|RequestWrapper
name|request
parameter_list|)
block|{
name|request
operator|.
name|setPaths
argument_list|(
name|target
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|getMethod
parameter_list|()
block|{
return|return
name|method
return|;
block|}
specifier|protected
name|boolean
name|doResolve
parameter_list|()
block|{
return|return
operator|!
name|absolute
return|;
block|}
comment|/**      * Resolve the target of this rewrite rule against the current request context.      *      * @return the new target path excluding context path      */
specifier|protected
name|String
name|resolve
parameter_list|(
name|XQueryURLRewrite
operator|.
name|RequestWrapper
name|request
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|String
name|path
init|=
name|request
operator|.
name|getInContextPath
argument_list|()
decl_stmt|;
if|if
condition|(
name|target
operator|==
literal|null
condition|)
block|{
return|return
name|path
return|;
block|}
name|String
name|fixedTarget
decl_stmt|;
if|if
condition|(
name|request
operator|.
name|getBasePath
argument_list|()
operator|!=
literal|null
operator|&&
name|target
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|fixedTarget
operator|=
name|request
operator|.
name|getBasePath
argument_list|()
operator|+
name|target
expr_stmt|;
block|}
else|else
block|{
name|fixedTarget
operator|=
name|target
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|URI
name|reqURI
init|=
operator|new
name|URI
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|reqURI
operator|.
name|resolve
argument_list|(
name|fixedTarget
argument_list|)
operator|.
name|toASCIIString
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
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
specifier|protected
name|void
name|copyFrom
parameter_list|(
name|URLRewrite
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|headers
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|headers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|other
operator|.
name|headers
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|other
operator|.
name|attributes
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|other
operator|.
name|attributes
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|other
operator|.
name|parameters
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|parameters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|entry
range|:
name|other
operator|.
name|parameters
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|this
operator|.
name|parameters
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|abstract
name|URLRewrite
name|copy
parameter_list|()
function_decl|;
specifier|private
name|void
name|setHeader
parameter_list|(
name|String
name|key
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|headers
operator|==
literal|null
condition|)
block|{
name|headers
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|headers
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addNameValue
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|map
parameter_list|)
block|{
name|List
argument_list|<
name|String
argument_list|>
name|values
init|=
name|map
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|values
operator|==
literal|null
condition|)
block|{
name|values
operator|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|values
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|values
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addParameter
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|parameters
operator|==
literal|null
condition|)
block|{
name|parameters
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|addNameValue
argument_list|(
name|name
argument_list|,
name|value
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|attributes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|unsetAttribute
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|attributes
operator|==
literal|null
condition|)
block|{
name|attributes
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
block|}
name|attributes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|UNSET
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTarget
parameter_list|(
name|String
name|target
parameter_list|)
block|{
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
block|}
specifier|public
name|String
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
specifier|public
name|void
name|setURI
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|this
operator|.
name|uri
operator|=
name|uri
expr_stmt|;
block|}
specifier|public
name|String
name|getURI
parameter_list|()
block|{
return|return
name|uri
return|;
block|}
specifier|public
name|void
name|setPrefix
parameter_list|(
name|String
name|prefix
parameter_list|)
block|{
if|if
condition|(
name|prefix
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|prefix
operator|=
name|prefix
operator|.
name|replaceFirst
argument_list|(
literal|"/+$"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|prefix
operator|=
name|prefix
expr_stmt|;
block|}
specifier|public
name|String
name|getPrefix
parameter_list|()
block|{
return|return
name|prefix
return|;
block|}
specifier|public
specifier|abstract
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
function_decl|;
specifier|public
name|void
name|prepareRequest
parameter_list|(
name|XQueryURLRewrite
operator|.
name|RequestWrapper
name|request
parameter_list|)
block|{
if|if
condition|(
name|parameters
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|String
argument_list|>
argument_list|>
name|param
range|:
name|parameters
operator|.
name|entrySet
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|String
name|paramValue
range|:
name|param
operator|.
name|getValue
argument_list|()
control|)
block|{
name|request
operator|.
name|addParameter
argument_list|(
name|param
operator|.
name|getKey
argument_list|()
argument_list|,
name|paramValue
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|attributes
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|attributes
operator|.
name|entrySet
argument_list|()
control|)
block|{
specifier|final
name|String
name|value
init|=
name|entry
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|equals
argument_list|(
name|UNSET
argument_list|)
condition|)
block|{
name|request
operator|.
name|removeAttribute
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|.
name|setAttribute
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|protected
name|void
name|setHeaders
parameter_list|(
name|HttpResponseWrapper
name|response
parameter_list|)
block|{
if|if
condition|(
name|headers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|headers
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|response
operator|.
name|setHeader
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|boolean
name|isControllerForward
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
specifier|protected
specifier|static
name|String
name|normalizePath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|path
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|path
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
name|path
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'/'
condition|)
block|{
if|if
condition|(
name|i
operator|==
literal|0
operator|||
name|path
operator|.
name|charAt
argument_list|(
name|i
operator|-
literal|1
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|sb
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

