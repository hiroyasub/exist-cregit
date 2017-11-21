begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmlrpc
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|lazy
operator|.
name|AtomicLazyValE
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
name|XmlRpcHandler
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
name|server
operator|.
name|AbstractReflectiveHandlerMapping
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
name|server
operator|.
name|RequestProcessorFactoryFactory
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
name|server
operator|.
name|XmlRpcHandlerMapping
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
name|webserver
operator|.
name|XmlRpcServlet
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
name|Descriptor
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
name|HttpServletRequestWrapper
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
name|ServletContext
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|Either
operator|.
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|RpcServlet
extends|extends
name|XmlRpcServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|1003413291835771186L
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|RpcServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicReference
argument_list|<
name|RequestProcessorFactoryFactory
argument_list|>
name|XMLDB_REQUEST_PROCESSOR_FACTORY_FACTORY
init|=
operator|new
name|AtomicReference
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|boolean
name|DEFAULT_USE_DEFAULT_USER
init|=
literal|true
decl_stmt|;
specifier|private
name|boolean
name|useDefaultUser
init|=
name|DEFAULT_USE_DEFAULT_USER
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
specifier|final
name|ServletConfig
name|pConfig
parameter_list|)
throws|throws
name|ServletException
block|{
specifier|final
name|String
name|useDefaultUser
init|=
name|pConfig
operator|.
name|getInitParameter
argument_list|(
literal|"useDefaultUser"
argument_list|)
decl_stmt|;
if|if
condition|(
name|useDefaultUser
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|useDefaultUser
operator|=
name|Boolean
operator|.
name|parseBoolean
argument_list|(
name|useDefaultUser
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|useDefaultUser
operator|=
name|DEFAULT_USE_DEFAULT_USER
expr_stmt|;
block|}
name|super
operator|.
name|init
argument_list|(
operator|new
name|FilteredServletConfig
argument_list|(
name|pConfig
argument_list|,
name|paramName
lambda|->
operator|!
literal|"useDefaultUser"
operator|.
name|equals
argument_list|(
name|paramName
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
specifier|final
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
throws|,
name|ServletException
block|{
comment|// Request logger
specifier|final
name|Descriptor
name|descriptor
init|=
name|Descriptor
operator|.
name|getDescriptorSingleton
argument_list|()
decl_stmt|;
if|if
condition|(
name|descriptor
operator|.
name|allowRequestLogging
argument_list|()
operator|&&
operator|!
name|descriptor
operator|.
name|requestsFiltered
argument_list|()
condition|)
block|{
comment|// Wrap HttpServletRequest, because both request Logger and xmlrpc
comment|// need the request InputStream, which is consumed when read.
name|request
operator|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|request
argument_list|,
comment|/*formEncoding*/
literal|"utf-8"
argument_list|)
expr_stmt|;
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|super
operator|.
name|doPost
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Problem during XmlRpc execution"
argument_list|,
name|e
argument_list|)
expr_stmt|;
specifier|final
name|String
name|exceptionMessage
decl_stmt|;
if|if
condition|(
name|e
operator|instanceof
name|XmlRpcException
condition|)
block|{
specifier|final
name|Throwable
name|linkedException
init|=
operator|(
operator|(
name|XmlRpcException
operator|)
name|e
operator|)
operator|.
name|linkedException
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|linkedException
operator|.
name|getMessage
argument_list|()
argument_list|,
name|linkedException
argument_list|)
expr_stmt|;
name|exceptionMessage
operator|=
literal|"An error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
operator|+
literal|": "
operator|+
name|linkedException
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|exceptionMessage
operator|=
literal|"An unknown error occurred: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|ServletException
argument_list|(
name|exceptionMessage
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|XmlRpcHandlerMapping
name|newXmlRpcHandlerMapping
parameter_list|()
throws|throws
name|XmlRpcException
block|{
specifier|final
name|DefaultHandlerMapping
name|mapping
init|=
operator|new
name|DefaultHandlerMapping
argument_list|()
decl_stmt|;
name|mapping
operator|.
name|setVoidMethodEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|setRequestProcessorFactoryFactory
argument_list|(
name|XMLDB_REQUEST_PROCESSOR_FACTORY_FACTORY
operator|.
name|updateAndGet
argument_list|(
name|prev
lambda|->
name|prev
operator|!=
literal|null
condition|?
name|prev
else|:
operator|new
name|XmldbRequestProcessorFactoryFactory
argument_list|(
name|useDefaultUser
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|mapping
operator|.
name|loadDefault
argument_list|(
name|RpcConnection
operator|.
name|class
argument_list|)
expr_stmt|;
return|return
name|mapping
return|;
block|}
specifier|private
specifier|static
class|class
name|XmldbRequestProcessorFactoryFactory
extends|extends
name|RequestProcessorFactoryFactory
operator|.
name|RequestSpecificProcessorFactoryFactory
block|{
specifier|private
specifier|final
name|AtomicLazyValE
argument_list|<
name|RequestProcessorFactory
argument_list|,
name|XmlRpcException
argument_list|>
name|instance
decl_stmt|;
specifier|public
name|XmldbRequestProcessorFactoryFactory
parameter_list|(
specifier|final
name|boolean
name|useDefaultUser
parameter_list|)
block|{
name|instance
operator|=
operator|new
name|AtomicLazyValE
argument_list|<>
argument_list|(
parameter_list|()
lambda|->
block|{
try|try
block|{
return|return
name|Right
argument_list|(
operator|new
name|XmldbRequestProcessorFactory
argument_list|(
literal|"exist"
argument_list|,
name|useDefaultUser
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
return|return
name|Left
argument_list|(
operator|new
name|XmlRpcException
argument_list|(
literal|"Failed to initialize XMLRPC interface: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
argument_list|)
return|;
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|RequestProcessorFactory
name|getRequestProcessorFactory
parameter_list|(
specifier|final
name|Class
name|pClass
parameter_list|)
throws|throws
name|XmlRpcException
block|{
return|return
name|instance
operator|.
name|get
argument_list|()
return|;
block|}
block|}
specifier|private
specifier|static
class|class
name|DefaultHandlerMapping
extends|extends
name|AbstractReflectiveHandlerMapping
block|{
specifier|private
name|DefaultHandlerMapping
parameter_list|()
throws|throws
name|XmlRpcException
block|{
block|}
specifier|public
name|void
name|loadDefault
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
parameter_list|)
throws|throws
name|XmlRpcException
block|{
name|registerPublicMethods
argument_list|(
literal|"Default"
argument_list|,
name|clazz
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|XmlRpcHandler
name|getHandler
parameter_list|(
name|String
name|pHandlerName
parameter_list|)
throws|throws
name|XmlRpcException
block|{
if|if
condition|(
name|pHandlerName
operator|.
name|indexOf
argument_list|(
literal|'.'
argument_list|)
operator|<
literal|0
condition|)
block|{
name|pHandlerName
operator|=
literal|"Default."
operator|+
name|pHandlerName
expr_stmt|;
block|}
return|return
name|super
operator|.
name|getHandler
argument_list|(
name|pHandlerName
argument_list|)
return|;
block|}
block|}
comment|/**      * Filters parameters from an existing {@link ServletConfig}.      */
specifier|private
specifier|static
class|class
name|FilteredServletConfig
implements|implements
name|ServletConfig
block|{
specifier|private
specifier|final
name|ServletConfig
name|config
decl_stmt|;
specifier|private
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|parameterPredicate
decl_stmt|;
comment|/**          * @param config a ServletConfig          * @param parameterPredicate a predicate which includes parameters from {@code config} in this config.          */
specifier|private
name|FilteredServletConfig
parameter_list|(
specifier|final
name|ServletConfig
name|config
parameter_list|,
specifier|final
name|Predicate
argument_list|<
name|String
argument_list|>
name|parameterPredicate
parameter_list|)
block|{
name|this
operator|.
name|config
operator|=
name|config
expr_stmt|;
name|this
operator|.
name|parameterPredicate
operator|=
name|parameterPredicate
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getServletName
parameter_list|()
block|{
return|return
name|config
operator|.
name|getServletName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ServletContext
name|getServletContext
parameter_list|()
block|{
return|return
name|config
operator|.
name|getServletContext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getInitParameter
parameter_list|(
specifier|final
name|String
name|s
parameter_list|)
block|{
if|if
condition|(
name|parameterPredicate
operator|.
name|test
argument_list|(
name|s
argument_list|)
condition|)
block|{
return|return
name|config
operator|.
name|getInitParameter
argument_list|(
name|s
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Enumeration
argument_list|<
name|String
argument_list|>
name|getInitParameterNames
parameter_list|()
block|{
specifier|final
name|Enumeration
argument_list|<
name|String
argument_list|>
name|names
init|=
name|config
operator|.
name|getInitParameterNames
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|String
argument_list|>
name|filteredNames
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
while|while
condition|(
name|names
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
specifier|final
name|String
name|name
init|=
name|names
operator|.
name|nextElement
argument_list|()
decl_stmt|;
if|if
condition|(
name|parameterPredicate
operator|.
name|test
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|filteredNames
operator|.
name|add
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|Collections
operator|.
name|enumeration
argument_list|(
name|filteredNames
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

