begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|FileOutputStream
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
name|io
operator|.
name|OutputStream
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
name|ServletInputStream
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
name|BadRequestException
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
name|NotFoundException
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
name|RESTServer
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
name|PermissionDeniedException
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
name|User
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
name|storage
operator|.
name|DBBroker
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
name|exist
operator|.
name|validation
operator|.
name|XmlLibraryChecker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|Constants
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

begin_comment
comment|/**  * Implements the REST-style interface if eXist is running within  * a servlet engine. The real work is done by class   * {@link org.exist.http.RESTServer}.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|EXistServlet
extends|extends
name|HttpServlet
block|{
specifier|private
name|String
name|formEncoding
init|=
literal|null
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|EXistServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
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
name|RESTServer
name|server
decl_stmt|;
specifier|private
name|Authenticator
name|authenticator
decl_stmt|;
specifier|private
name|User
name|defaultUser
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig) 	 */
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
comment|// Configure BrokerPool
try|try
block|{
if|if
condition|(
name|BrokerPool
operator|.
name|isConfigured
argument_list|()
condition|)
block|{
name|LOG
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
name|confFile
operator|=
literal|"conf.xml"
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"EXistServlet: exist.home="
operator|+
name|dbHome
argument_list|)
expr_stmt|;
name|System
operator|.
name|setProperty
argument_list|(
literal|"exist.home"
argument_list|,
name|dbHome
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"reading configuration from "
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"configuration file "
operator|+
name|confFile
operator|+
literal|" not found or not readable"
argument_list|)
throw|;
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
name|start
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
condition|)
name|startup
argument_list|(
name|configuration
argument_list|)
expr_stmt|;
block|}
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
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
name|option
operator|.
name|trim
argument_list|()
operator|.
name|equals
argument_list|(
literal|"true"
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
name|defaultUsername
operator|=
name|option
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
name|defaultPassword
operator|=
name|option
expr_stmt|;
name|defaultUser
operator|=
name|getDefaultUser
argument_list|()
expr_stmt|;
if|if
condition|(
name|defaultUser
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Using default user "
operator|+
name|defaultUsername
operator|+
literal|" for all unauthorized requests."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Default user "
operator|+
name|defaultUsername
operator|+
literal|" cannot be found.  A BASIC AUTH challenge will be the default."
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No default user.  All requires must be authorized or will result in a BASIC AUTH challenge."
argument_list|)
expr_stmt|;
name|defaultUser
operator|=
literal|null
expr_stmt|;
block|}
name|authenticator
operator|=
operator|new
name|BasicAuthenticator
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
comment|// Instantiate REST server
name|formEncoding
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"form-encoding"
argument_list|)
expr_stmt|;
if|if
condition|(
name|formEncoding
operator|==
literal|null
condition|)
name|formEncoding
operator|=
name|DEFAULT_ENCODING
expr_stmt|;
name|String
name|containerEncoding
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
name|containerEncoding
operator|==
literal|null
condition|)
name|containerEncoding
operator|=
name|DEFAULT_ENCODING
expr_stmt|;
name|server
operator|=
operator|new
name|RESTServer
argument_list|(
name|formEncoding
argument_list|,
name|containerEncoding
argument_list|)
expr_stmt|;
comment|// XML lib checks....
if|if
condition|(
name|XmlLibraryChecker
operator|.
name|isXercesVersionOK
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Detected "
operator|+
name|XmlLibraryChecker
operator|.
name|XERCESVERSION
operator|+
literal|", OK."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"eXist requires '"
operator|+
name|XmlLibraryChecker
operator|.
name|XERCESVERSION
operator|+
literal|"' but detected '"
operator|+
name|XmlLibraryChecker
operator|.
name|getXercesVersion
argument_list|()
operator|+
literal|"'. Please add the correct version to the "
operator|+
literal|"class-path, e.g. in the 'endorsed' folder of "
operator|+
literal|"the servlet container or in the 'endorsed' folder "
operator|+
literal|"of the JRE."
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|XmlLibraryChecker
operator|.
name|isXalanVersionOK
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Detected "
operator|+
name|XmlLibraryChecker
operator|.
name|XALANVERSION
operator|+
literal|", OK."
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"eXist requires '"
operator|+
name|XmlLibraryChecker
operator|.
name|XALANVERSION
operator|+
literal|"' but detected '"
operator|+
name|XmlLibraryChecker
operator|.
name|getXalanVersion
argument_list|()
operator|+
literal|"'. Please add the correct version to the "
operator|+
literal|"class-path, e.g. in the 'endorsed' folder of "
operator|+
literal|"the servlet container or in the 'endorsed' folder "
operator|+
literal|"of the JRE."
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 	 */
specifier|protected
name|void
name|doPut
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
comment|//first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|//second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|//TODO: figure out a way to log PUT requests with HttpServletRequestWrapper and Descriptor.doLogRequestInReplayLog()
comment|//map's the path if a mapping is specified in the descriptor
name|path
operator|=
name|descriptor
operator|.
name|mapPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|//third, authenticate the user
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|//response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|//		"Permission denied: unknown user or password");
return|return;
block|}
comment|//fourth, process the request
name|ServletInputStream
name|is
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|int
name|len
init|=
name|request
operator|.
name|getContentLength
argument_list|()
decl_stmt|;
comment|// put may send a lot of data, so save it
comment|// to a temporary file first.
name|File
name|tempFile
init|=
name|File
operator|.
name|createTempFile
argument_list|(
literal|"existSRV"
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
name|OutputStream
name|os
init|=
operator|new
name|FileOutputStream
argument_list|(
name|tempFile
argument_list|)
decl_stmt|;
name|byte
index|[]
name|buffer
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|count
decl_stmt|,
name|l
init|=
literal|0
decl_stmt|;
do|do
block|{
name|count
operator|=
name|is
operator|.
name|read
argument_list|(
name|buffer
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|>
literal|0
condition|)
name|os
operator|.
name|write
argument_list|(
name|buffer
argument_list|,
literal|0
argument_list|,
name|count
argument_list|)
expr_stmt|;
name|l
operator|+=
name|count
expr_stmt|;
block|}
do|while
condition|(
name|l
operator|<
name|len
condition|)
do|;
name|os
operator|.
name|close
argument_list|()
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|server
operator|.
name|doPut
argument_list|(
name|broker
argument_list|,
name|tempFile
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|tempFile
operator|.
name|delete
argument_list|()
expr_stmt|;
block|}
comment|/**      * @param request      * @return      */
specifier|private
name|String
name|adjustPath
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|String
name|path
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|path
operator|.
name|lastIndexOf
argument_list|(
literal|';'
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|!=
name|Constants
operator|.
name|STRING_NOT_FOUND
condition|)
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
return|return
name|path
return|;
block|}
comment|/* (non-Javadoc) 	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 	 */
specifier|protected
name|void
name|doGet
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
comment|//first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|//second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|//logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//map's the path if a mapping is specified in the descriptor
name|path
operator|=
name|descriptor
operator|.
name|mapPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|//third, authenticate the user
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|//response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|//		"Permission denied: unknown user " + "or password");
return|return;
block|}
comment|//fouth, process the request
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|server
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|doHead
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
comment|//first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|//second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|//logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//map's the path if a mapping is specified in the descriptor
name|path
operator|=
name|descriptor
operator|.
name|mapPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|//third, authenticate the user
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|//response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|//		"Permission denied: unknown user " + "or password");
return|return;
block|}
comment|//fourth, process the request
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|server
operator|.
name|doHead
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 	 */
specifier|protected
name|void
name|doDelete
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
comment|//first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|//second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|//map's the path if a mapping is specified in the descriptor
name|path
operator|=
name|descriptor
operator|.
name|mapPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|//third, authenticate the user
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|//response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|//		"Permission denied: unknown user " + "or password");
return|return;
block|}
comment|//fourth, process the request
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|server
operator|.
name|doDelete
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|path
argument_list|)
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse) 	 */
specifier|protected
name|void
name|doPost
parameter_list|(
name|HttpServletRequest
name|req
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|ServletException
throws|,
name|IOException
block|{
name|HttpServletRequest
name|request
init|=
literal|null
decl_stmt|;
comment|//For POST request, If we are logging the requests we must wrap HttpServletRequest in HttpServletRequestWrapper
comment|//otherwise we cannot access the POST parameters from the content body of the request!!! - deliriumsky
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
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|descriptor
operator|.
name|allowRequestLogging
argument_list|()
condition|)
block|{
name|request
operator|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|req
argument_list|,
name|formEncoding
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|request
operator|=
name|req
expr_stmt|;
block|}
block|}
else|else
block|{
name|request
operator|=
name|req
expr_stmt|;
block|}
comment|//first, adjust the path
name|String
name|path
init|=
name|request
operator|.
name|getPathInfo
argument_list|()
decl_stmt|;
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
name|adjustPath
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
comment|//second, perform descriptor actions
if|if
condition|(
name|descriptor
operator|!=
literal|null
condition|)
block|{
comment|//logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|//map's the path if a mapping is specified in the descriptor
name|path
operator|=
name|descriptor
operator|.
name|mapPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|//third, authenticate the user
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
comment|//response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|//		"Permission denied: unknown user " + "or password");
return|return;
block|}
comment|//fouth, process the request
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|server
operator|.
name|doPost
argument_list|(
name|broker
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_FORBIDDEN
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|e
parameter_list|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see javax.servlet.GenericServlet#destroy()      */
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
specifier|private
name|User
name|authenticate
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|java
operator|.
name|io
operator|.
name|IOException
block|{
comment|// First try to validate the principial if passed from the servlet engine
name|Principal
name|principal
init|=
name|request
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|XmldbPrincipal
condition|)
block|{
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Validating Principle: "
operator|+
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|username
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|password
operator|.
name|equalsIgnoreCase
argument_list|(
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Valid User: "
operator|+
name|user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|user
return|;
block|}
else|else
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Password invalid for user: "
operator|+
name|username
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"User not found: "
operator|+
name|principal
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
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
name|defaultUser
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultUser
return|;
block|}
return|return
name|authenticator
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
comment|/* 		byte[] c = Base64.decode(auth.substring(6).getBytes()); 		String s = new String(c); 		int p = s.indexOf(':'); 		if (p == Constants.STRING_NOT_FOUND) { 			 return null; 			 } 		String username = s.substring(0, p); 		String password = s.substring(p + 1); 		 		User user = pool.getSecurityManager().getUser(username); 		if (user == null) 			return null; 		if (!user.validate(password)) 			return null; 		return user;                  */
block|}
specifier|private
name|User
name|getDefaultUser
parameter_list|()
block|{
if|if
condition|(
name|defaultUsername
operator|!=
literal|null
condition|)
block|{
name|User
name|user
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|defaultUsername
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|user
operator|.
name|validate
argument_list|(
name|defaultPassword
argument_list|)
condition|)
return|return
literal|null
return|;
block|}
return|return
name|user
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|void
name|startup
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
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"database has not been "
operator|+
literal|"configured"
argument_list|)
throw|;
name|LOG
operator|.
name|info
argument_list|(
literal|"configuring eXist instance"
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
argument_list|()
condition|)
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
catch|catch
parameter_list|(
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
argument_list|)
throw|;
block|}
try|try
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"registering XMLDB driver"
argument_list|)
expr_stmt|;
name|Class
name|clazz
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
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
name|ClassNotFoundException
name|e
parameter_list|)
block|{
name|LOG
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
name|InstantiationException
name|e
parameter_list|)
block|{
name|LOG
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
name|IllegalAccessException
name|e
parameter_list|)
block|{
name|LOG
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
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
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
block|}
end_class

end_unit

