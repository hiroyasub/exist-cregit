begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2012 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id: EXistServlet.java 14945 2011-07-22 20:05:08Z deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|servlets
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
name|HttpServlet
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
name|urlrewrite
operator|.
name|XQueryURLRewrite
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
name|AuthenticationException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
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
name|BrokerPool
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
name|DatabaseConfigurationException
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
name|DatabaseManager
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
name|Database
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
name|exist
operator|.
name|security
operator|.
name|SecurityManager
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
name|XmldbPrincipal
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
name|internal
operator|.
name|AccountImpl
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractExistHttpServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|804071766041263220L
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|String
name|formEncoding
init|=
name|DEFAULT_ENCODING
decl_stmt|;
specifier|private
name|String
name|containerEncoding
init|=
name|DEFAULT_ENCODING
decl_stmt|;
specifier|private
name|String
name|defaultUsername
init|=
name|SecurityManager
operator|.
name|GUEST_USER
decl_stmt|;
specifier|private
name|String
name|defaultPassword
init|=
name|SecurityManager
operator|.
name|GUEST_USER
decl_stmt|;
specifier|private
name|Authenticator
name|authenticator
decl_stmt|;
specifier|private
name|Subject
name|defaultUser
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|internalOnly
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|init
parameter_list|(
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
comment|//prepare the database
try|try
block|{
name|setPool
argument_list|(
name|getOrCreateBrokerPool
argument_list|(
name|config
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"No database instance available"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Unable to configure database instance: "
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
comment|//general eXist Servlet config
name|doGeneralExistServletConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|destroy
parameter_list|()
block|{
name|super
operator|.
name|destroy
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|Logger
name|getLog
parameter_list|()
function_decl|;
specifier|private
name|BrokerPool
name|getOrCreateBrokerPool
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|ServletException
block|{
comment|// Configure BrokerPool
if|if
condition|(
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
argument_list|)
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Database already started. Skipping configuration ..."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|confFile
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"configuration"
argument_list|)
decl_stmt|;
name|String
name|dbHome
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"basedir"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|start
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"start"
argument_list|)
decl_stmt|;
if|if
condition|(
name|confFile
operator|==
literal|null
condition|)
block|{
name|confFile
operator|=
literal|"conf.xml"
expr_stmt|;
block|}
name|dbHome
operator|=
operator|(
name|dbHome
operator|==
literal|null
operator|)
condition|?
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
literal|"."
argument_list|)
else|:
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|dbHome
argument_list|)
expr_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"EXistServlet: exist.home="
operator|+
name|dbHome
argument_list|)
expr_stmt|;
specifier|final
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|dbHome
operator|+
name|File
operator|.
name|separator
operator|+
name|confFile
argument_list|)
decl_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Reading configuration from "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Configuration file "
operator|+
name|confFile
operator|+
literal|" not found or not readable"
argument_list|)
throw|;
block|}
specifier|final
name|Configuration
name|configuration
init|=
operator|new
name|Configuration
argument_list|(
name|confFile
argument_list|,
name|dbHome
argument_list|)
decl_stmt|;
if|if
condition|(
name|start
operator|!=
literal|null
operator|&&
literal|"true"
operator|.
name|equals
argument_list|(
name|start
argument_list|)
condition|)
block|{
name|doDatabaseStartup
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
return|;
block|}
specifier|private
name|void
name|doDatabaseStartup
parameter_list|(
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ServletException
block|{
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Database has not been "
operator|+
literal|"configured"
argument_list|)
throw|;
block|}
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Configuring eXist instance"
argument_list|)
expr_stmt|;
try|try
block|{
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
argument_list|)
condition|)
block|{
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
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
catch|catch
parameter_list|(
specifier|final
name|DatabaseConfigurationException
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
try|try
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Registering XMLDB driver"
argument_list|)
expr_stmt|;
specifier|final
name|Class
argument_list|<
name|?
argument_list|>
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
specifier|final
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|clazz
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InstantiationException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"ERROR"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|doGeneralExistServletConfig
parameter_list|(
name|ServletConfig
name|config
parameter_list|)
block|{
name|String
name|option
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"use-default-user"
argument_list|)
decl_stmt|;
name|boolean
name|useDefaultUser
init|=
literal|true
decl_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
name|useDefaultUser
operator|=
literal|"true"
operator|.
name|equals
argument_list|(
name|option
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|useDefaultUser
condition|)
block|{
name|option
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"user"
argument_list|)
expr_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
name|setDefaultUsername
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|option
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"password"
argument_list|)
expr_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
name|setDefaultPassword
argument_list|(
name|option
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getDefaultUsername
argument_list|()
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|setDefaultUser
argument_list|(
name|getPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|getDefaultUsername
argument_list|()
argument_list|,
name|getDefaultPassword
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
name|setDefaultUser
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|setDefaultUser
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|setDefaultUser
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGuestSubject
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|getDefaultUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Using default user "
operator|+
name|getDefaultUsername
argument_list|()
operator|+
literal|" for all unauthorized requests."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"Default user "
operator|+
name|getDefaultUsername
argument_list|()
operator|+
literal|" cannot be found.  A BASIC AUTH challenge will be the default."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"No default user.  All requires must be authorized or will result in a BASIC AUTH challenge."
argument_list|)
expr_stmt|;
name|setDefaultUser
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
name|setAuthenticator
argument_list|(
operator|new
name|BasicAuthenticator
argument_list|(
name|getPool
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
comment|// get form and container encoding's
specifier|final
name|String
name|configFormEncoding
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"form-encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|configFormEncoding
operator|!=
literal|null
condition|)
block|{
name|setFormEncoding
argument_list|(
name|configFormEncoding
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|configContainerEncoding
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"container-encoding"
argument_list|)
decl_stmt|;
if|if
condition|(
name|configContainerEncoding
operator|!=
literal|null
condition|)
block|{
name|setContainerEncoding
argument_list|(
name|configContainerEncoding
argument_list|)
expr_stmt|;
block|}
specifier|final
name|String
name|param
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"hidden"
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|!=
literal|null
condition|)
block|{
name|internalOnly
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|param
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|Subject
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|isInternalOnly
argument_list|()
operator|&&
name|request
operator|.
name|getAttribute
argument_list|(
name|XQueryURLRewrite
operator|.
name|RQ_ATTR
argument_list|)
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Principal
name|principal
init|=
name|AccountImpl
operator|.
name|getUserFromServletRequest
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|principal
return|;
block|}
comment|// Try to validate the principal if passed from the Servlet engine
name|principal
operator|=
name|request
operator|.
name|getUserPrincipal
argument_list|()
expr_stmt|;
if|if
condition|(
name|principal
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|XmldbPrincipal
operator|.
name|class
operator|.
name|isAssignableFrom
argument_list|(
name|principal
operator|.
name|getClass
argument_list|()
argument_list|)
condition|)
block|{
specifier|final
name|String
name|username
init|=
operator|(
operator|(
name|XmldbPrincipal
operator|)
name|principal
operator|)
operator|.
name|getName
argument_list|()
decl_stmt|;
specifier|final
name|String
name|password
init|=
operator|(
operator|(
name|XmldbPrincipal
operator|)
name|principal
operator|)
operator|.
name|getPassword
argument_list|()
decl_stmt|;
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Validating Principle: "
operator|+
name|username
argument_list|)
expr_stmt|;
try|try
block|{
return|return
name|getPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|AuthenticationException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|principal
operator|instanceof
name|Subject
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|principal
return|;
block|}
block|}
comment|// Secondly try basic authentication
specifier|final
name|String
name|auth
init|=
name|request
operator|.
name|getHeader
argument_list|(
literal|"Authorization"
argument_list|)
decl_stmt|;
if|if
condition|(
name|auth
operator|==
literal|null
operator|&&
name|getDefaultUser
argument_list|()
operator|!=
literal|null
condition|)
block|{
return|return
name|getDefaultUser
argument_list|()
return|;
block|}
return|return
name|getAuthenticator
argument_list|()
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
block|}
specifier|protected
name|boolean
name|isInternalOnly
parameter_list|()
block|{
return|return
name|internalOnly
return|;
block|}
specifier|private
name|void
name|setInternalOnly
parameter_list|(
name|boolean
name|internalOnly
parameter_list|)
block|{
name|this
operator|.
name|internalOnly
operator|=
name|internalOnly
expr_stmt|;
block|}
specifier|protected
name|Subject
name|getDefaultUser
parameter_list|()
block|{
return|return
name|defaultUser
return|;
block|}
specifier|private
name|void
name|setDefaultUser
parameter_list|(
name|Subject
name|defaultUser
parameter_list|)
block|{
name|this
operator|.
name|defaultUser
operator|=
name|defaultUser
expr_stmt|;
block|}
specifier|protected
name|Authenticator
name|getAuthenticator
parameter_list|()
block|{
return|return
name|authenticator
return|;
block|}
specifier|private
name|void
name|setAuthenticator
parameter_list|(
name|Authenticator
name|authenticator
parameter_list|)
block|{
name|this
operator|.
name|authenticator
operator|=
name|authenticator
expr_stmt|;
block|}
specifier|protected
name|String
name|getDefaultPassword
parameter_list|()
block|{
return|return
name|defaultPassword
return|;
block|}
specifier|private
name|void
name|setDefaultPassword
parameter_list|(
name|String
name|defaultPassword
parameter_list|)
block|{
name|this
operator|.
name|defaultPassword
operator|=
name|defaultPassword
expr_stmt|;
block|}
specifier|protected
name|String
name|getDefaultUsername
parameter_list|()
block|{
return|return
name|defaultUsername
return|;
block|}
specifier|private
name|void
name|setDefaultUsername
parameter_list|(
name|String
name|defaultUsername
parameter_list|)
block|{
name|this
operator|.
name|defaultUsername
operator|=
name|defaultUsername
expr_stmt|;
block|}
specifier|protected
name|String
name|getContainerEncoding
parameter_list|()
block|{
return|return
name|containerEncoding
return|;
block|}
specifier|private
name|void
name|setContainerEncoding
parameter_list|(
name|String
name|containerEncoding
parameter_list|)
block|{
name|this
operator|.
name|containerEncoding
operator|=
name|containerEncoding
expr_stmt|;
block|}
specifier|protected
name|String
name|getFormEncoding
parameter_list|()
block|{
return|return
name|formEncoding
return|;
block|}
specifier|private
name|void
name|setFormEncoding
parameter_list|(
name|String
name|formEncoding
parameter_list|)
block|{
name|this
operator|.
name|formEncoding
operator|=
name|formEncoding
expr_stmt|;
block|}
specifier|protected
name|BrokerPool
name|getPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|private
name|void
name|setPool
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
block|}
block|}
end_class

end_unit

