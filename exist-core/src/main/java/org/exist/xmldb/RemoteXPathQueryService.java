begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
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
name|Writer
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
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|Leasable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmlrpc
operator|.
name|RpcAPI
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
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|CompiledExpression
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ResourceSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|XMLResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_class
specifier|public
class|class
name|RemoteXPathQueryService
extends|extends
name|AbstractRemote
implements|implements
name|EXistXPathQueryService
implements|,
name|EXistXQueryService
block|{
specifier|private
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|namespaceMappings
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|variableDecls
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|outputProperties
decl_stmt|;
specifier|private
name|String
name|moduleLoadPath
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|protectedMode
init|=
literal|false
decl_stmt|;
comment|/**      * Creates a new RemoteXPathQueryService instance.      *      * @param leasableXmlRpcClient the XML-RPC client lease      * @param collection a RemoteCollection value      */
specifier|public
name|RemoteXPathQueryService
parameter_list|(
specifier|final
name|Leasable
argument_list|<
name|XmlRpcClient
argument_list|>
name|leasableXmlRpcClient
parameter_list|,
specifier|final
name|RemoteCollection
name|collection
parameter_list|)
block|{
name|super
argument_list|(
name|collection
argument_list|)
expr_stmt|;
name|this
operator|.
name|leasableXmlRpcClient
operator|=
name|leasableXmlRpcClient
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|collection
operator|.
name|getProperties
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"XPathQueryService"
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getVersion
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"1.0"
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|query
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|query
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|optParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|SORT_EXPR
argument_list|,
name|sortExpr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
block|}
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|moduleLoadPath
operator|!=
literal|null
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|MODULE_LOAD_PATH
argument_list|,
name|moduleLoadPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|protectedMode
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|PROTECTED_MODE
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|optParams
argument_list|)
expr_stmt|;
specifier|final
name|Map
name|result
init|=
operator|(
name|Map
operator|)
name|collection
operator|.
name|execute
argument_list|(
literal|"queryPT"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|throwException
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
index|[]
name|resources
init|=
operator|(
name|Object
index|[]
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|hash
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|handle
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|hash
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"hash"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Properties
name|resourceSetProperties
init|=
operator|new
name|Properties
argument_list|(
name|outputProperties
argument_list|)
decl_stmt|;
name|resourceSetProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|XDM_SERIALIZATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
return|return
operator|new
name|RemoteResourceSet
argument_list|(
name|leasableXmlRpcClient
argument_list|,
name|collection
argument_list|,
name|resourceSetProperties
argument_list|,
name|resources
argument_list|,
name|handle
argument_list|,
name|hash
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|CompiledExpression
name|compile
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
return|return
name|compileAndCheck
argument_list|(
name|query
argument_list|)
return|;
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
specifier|public
name|CompiledExpression
name|compileAndCheck
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|XPathException
block|{
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|optParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|moduleLoadPath
operator|!=
literal|null
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|MODULE_LOAD_PATH
argument_list|,
name|moduleLoadPath
argument_list|)
expr_stmt|;
block|}
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|optParams
argument_list|)
expr_stmt|;
specifier|final
name|Map
name|result
init|=
operator|(
name|Map
operator|)
name|collection
operator|.
name|execute
argument_list|(
literal|"compile"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|throwXPathException
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RemoteCompiledExpression
argument_list|(
name|query
argument_list|)
return|;
block|}
specifier|private
name|void
name|throwException
parameter_list|(
specifier|final
name|Map
name|result
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|message
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|lineInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|LINE
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|columnInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
specifier|final
name|int
name|line
init|=
name|lineInt
operator|==
literal|null
condition|?
literal|0
else|:
name|lineInt
decl_stmt|;
specifier|final
name|int
name|column
init|=
name|columnInt
operator|==
literal|null
condition|?
literal|0
else|:
name|columnInt
decl_stmt|;
specifier|final
name|XPathException
name|cause
init|=
operator|new
name|XPathException
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
decl_stmt|;
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|message
argument_list|,
name|cause
argument_list|)
throw|;
block|}
specifier|private
name|void
name|throwXPathException
parameter_list|(
specifier|final
name|Map
name|result
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|message
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|lineInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|LINE
argument_list|)
decl_stmt|;
specifier|final
name|Integer
name|columnInt
init|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|COLUMN
argument_list|)
decl_stmt|;
specifier|final
name|int
name|line
init|=
name|lineInt
operator|==
literal|null
condition|?
literal|0
else|:
name|lineInt
decl_stmt|;
specifier|final
name|int
name|column
init|=
name|columnInt
operator|==
literal|null
condition|?
literal|0
else|:
name|columnInt
decl_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|line
argument_list|,
name|column
argument_list|,
name|message
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|execute
parameter_list|(
specifier|final
name|Source
name|source
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
specifier|final
name|String
name|xq
init|=
name|source
operator|.
name|getContent
argument_list|()
decl_stmt|;
return|return
name|query
argument_list|(
name|xq
argument_list|,
literal|null
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
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
specifier|public
name|ResourceSet
name|executeStoredQuery
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|uri
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Map
name|result
init|=
operator|(
name|Map
operator|)
name|collection
operator|.
name|execute
argument_list|(
literal|"executeT"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|throwException
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
index|[]
name|resources
init|=
operator|(
name|Object
index|[]
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|hash
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|handle
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|hash
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"hash"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Properties
name|resourceSetProperties
init|=
operator|new
name|Properties
argument_list|(
name|outputProperties
argument_list|)
decl_stmt|;
name|resourceSetProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|XDM_SERIALIZATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
return|return
operator|new
name|RemoteResourceSet
argument_list|(
name|leasableXmlRpcClient
argument_list|,
name|collection
argument_list|,
name|outputProperties
argument_list|,
name|resources
argument_list|,
name|handle
argument_list|,
name|hash
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|query
parameter_list|(
specifier|final
name|XMLResource
name|res
parameter_list|,
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|res
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|query
parameter_list|(
specifier|final
name|XMLResource
name|res
parameter_list|,
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|String
name|sortExpr
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|RemoteXMLResource
name|resource
init|=
operator|(
name|RemoteXMLResource
operator|)
name|res
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|optParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sortExpr
operator|!=
literal|null
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|SORT_EXPR
argument_list|,
name|sortExpr
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|moduleLoadPath
operator|!=
literal|null
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|MODULE_LOAD_PATH
argument_list|,
name|moduleLoadPath
argument_list|)
expr_stmt|;
block|}
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|protectedMode
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|PROTECTED_MODE
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|query
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|resource
operator|.
name|path
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|resource
operator|.
name|idIsPresent
argument_list|()
condition|?
name|resource
operator|.
name|getNodeId
argument_list|()
else|:
literal|""
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|optParams
argument_list|)
expr_stmt|;
specifier|final
name|Map
name|result
init|=
operator|(
name|Map
operator|)
name|collection
operator|.
name|execute
argument_list|(
literal|"queryPT"
argument_list|,
name|params
argument_list|)
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|get
argument_list|(
name|RpcAPI
operator|.
name|ERROR
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|throwException
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Object
index|[]
name|resources
init|=
operator|(
name|Object
index|[]
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"results"
argument_list|)
decl_stmt|;
name|int
name|handle
init|=
operator|-
literal|1
decl_stmt|;
name|int
name|hash
init|=
operator|-
literal|1
decl_stmt|;
if|if
condition|(
name|resources
operator|!=
literal|null
operator|&&
name|resources
operator|.
name|length
operator|>
literal|0
condition|)
block|{
name|handle
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
name|hash
operator|=
operator|(
name|Integer
operator|)
name|result
operator|.
name|get
argument_list|(
literal|"hash"
argument_list|)
expr_stmt|;
block|}
specifier|final
name|Properties
name|resourceSetProperties
init|=
operator|new
name|Properties
argument_list|(
name|outputProperties
argument_list|)
decl_stmt|;
name|resourceSetProperties
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|XDM_SERIALIZATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
return|return
operator|new
name|RemoteResourceSet
argument_list|(
name|leasableXmlRpcClient
argument_list|,
name|collection
argument_list|,
name|resourceSetProperties
argument_list|,
name|resources
argument_list|,
name|handle
argument_list|,
name|hash
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|queryResource
parameter_list|(
specifier|final
name|String
name|resource
parameter_list|,
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|Resource
name|res
init|=
name|collection
operator|.
name|getResource
argument_list|(
name|resource
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|res
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resource
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
literal|"XMLResource"
operator|.
name|equals
argument_list|(
name|res
operator|.
name|getResourceType
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
argument_list|,
literal|"Resource "
operator|+
name|resource
operator|+
literal|" is not an XML resource"
argument_list|)
throw|;
block|}
return|return
name|query
argument_list|(
operator|(
name|XMLResource
operator|)
name|res
argument_list|,
name|query
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|res
operator|!=
literal|null
operator|&&
name|res
operator|instanceof
name|AbstractRemoteResource
condition|)
operator|(
operator|(
name|AbstractRemoteResource
operator|)
name|res
operator|)
operator|.
name|freeResources
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCollection
parameter_list|(
specifier|final
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
block|}
annotation|@
name|Override
specifier|public
name|String
name|getProperty
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
specifier|final
name|String
name|property
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|property
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearNamespaces
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|namespaceMappings
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeNamespace
parameter_list|(
specifier|final
name|String
name|ns
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|namespaceMappings
operator|.
name|values
argument_list|()
operator|.
name|removeIf
argument_list|(
name|s
lambda|->
name|s
operator|.
name|equals
argument_list|(
name|ns
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setNamespace
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|,
specifier|final
name|String
name|namespace
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|namespaceMappings
operator|.
name|put
argument_list|(
name|prefix
operator|!=
literal|null
condition|?
name|prefix
else|:
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|,
name|namespace
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getNamespace
parameter_list|(
specifier|final
name|String
name|prefix
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|namespaceMappings
operator|.
name|get
argument_list|(
name|prefix
operator|!=
literal|null
condition|?
name|prefix
else|:
name|XMLConstants
operator|.
name|DEFAULT_NS_PREFIX
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|declareVariable
parameter_list|(
specifier|final
name|String
name|qname
parameter_list|,
specifier|final
name|Object
name|initialValue
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|variableDecls
operator|.
name|put
argument_list|(
name|qname
argument_list|,
name|initialValue
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearVariables
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|variableDecls
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|execute
parameter_list|(
specifier|final
name|CompiledExpression
name|expression
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|ResourceSet
name|execute
parameter_list|(
specifier|final
name|XMLResource
name|res
parameter_list|,
specifier|final
name|CompiledExpression
name|expression
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|query
argument_list|(
name|res
argument_list|,
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setXPathCompatibility
parameter_list|(
specifier|final
name|boolean
name|backwardsCompatible
parameter_list|)
block|{
comment|// TODO: not passed
block|}
comment|/**      * Calling this method has no effect. The server loads modules      * relative to its own context.      *      * @param path the module load path.      */
annotation|@
name|Override
specifier|public
name|void
name|setModuleLoadPath
parameter_list|(
specifier|final
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|moduleLoadPath
operator|=
name|path
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|CompiledExpression
name|expression
parameter_list|,
specifier|final
name|Writer
name|writer
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
operator|(
operator|(
name|RemoteCompiledExpression
operator|)
name|expression
operator|)
operator|.
name|getQuery
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|optParams
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
if|if
condition|(
name|namespaceMappings
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|NAMESPACES
argument_list|,
name|namespaceMappings
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|variableDecls
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|VARIABLES
argument_list|,
name|variableDecls
argument_list|)
expr_stmt|;
block|}
name|optParams
operator|.
name|put
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|outputProperties
operator|.
name|getProperty
argument_list|(
name|RpcAPI
operator|.
name|BASE_URI
argument_list|,
name|collection
operator|.
name|getPath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|optParams
argument_list|)
expr_stmt|;
try|try
block|{
specifier|final
name|String
name|dump
init|=
operator|(
name|String
operator|)
name|collection
operator|.
name|execute
argument_list|(
literal|"printDiagnostics"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|writer
operator|.
name|write
argument_list|(
name|dump
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|UNKNOWN_ERROR
argument_list|,
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
specifier|public
name|void
name|beginProtected
parameter_list|()
block|{
name|protectedMode
operator|=
literal|true
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|endProtected
parameter_list|()
block|{
name|protectedMode
operator|=
literal|false
expr_stmt|;
block|}
block|}
end_class

end_unit

