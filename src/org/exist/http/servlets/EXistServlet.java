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
name|InputStreamReader
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
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|HashMap
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
name|ServletOutputStream
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
name|xmlrpc
operator|.
name|Base64
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
name|http
operator|.
name|Response
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

begin_class
specifier|public
class|class
name|EXistServlet
extends|extends
name|HttpServlet
block|{
specifier|private
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
name|User
name|defaultUser
init|=
literal|null
decl_stmt|;
specifier|private
name|RESTServer
name|server
init|=
operator|new
name|RESTServer
argument_list|()
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
argument_list|(
literal|"DatabaseAdminServlet: exist.home="
operator|+
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
name|this
operator|.
name|log
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
name|defaultUser
operator|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|SecurityManager
operator|.
name|GUEST_USER
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
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
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
argument_list|,
literal|"Permission denied: unknown user "
operator|+
literal|"or password"
argument_list|)
expr_stmt|;
return|return;
block|}
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
operator|>
operator|-
literal|1
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
literal|"exist"
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
name|Response
name|r
init|=
name|server
operator|.
name|doPut
argument_list|(
name|broker
argument_list|,
name|tempFile
argument_list|,
name|request
operator|.
name|getContentType
argument_list|()
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|writeResponse
argument_list|(
name|r
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
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
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
argument_list|,
literal|"Permission denied: unknown user "
operator|+
literal|"or password"
argument_list|)
expr_stmt|;
return|return;
block|}
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
operator|>
operator|-
literal|1
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
name|Map
name|parameters
init|=
name|getParameters
argument_list|(
name|request
argument_list|)
decl_stmt|;
name|Response
name|r
init|=
name|server
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|parameters
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|writeResponse
argument_list|(
name|r
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
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
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
argument_list|,
literal|"Permission denied: unknown user "
operator|+
literal|"or password"
argument_list|)
expr_stmt|;
return|return;
block|}
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
operator|>
operator|-
literal|1
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
name|Response
name|r
init|=
name|server
operator|.
name|doDelete
argument_list|(
name|broker
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|writeResponse
argument_list|(
name|r
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
name|User
name|user
init|=
name|authenticate
argument_list|(
name|request
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
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
argument_list|,
literal|"Permission denied: unknown user "
operator|+
literal|"or password"
argument_list|)
expr_stmt|;
return|return;
block|}
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
name|path
operator|=
literal|""
expr_stmt|;
else|else
block|{
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
operator|>
operator|-
literal|1
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
block|}
name|String
name|encoding
init|=
name|request
operator|.
name|getCharacterEncoding
argument_list|()
decl_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|encoding
operator|=
literal|"UTF-8"
expr_stmt|;
name|ServletInputStream
name|is
init|=
name|request
operator|.
name|getInputStream
argument_list|()
decl_stmt|;
name|Reader
name|reader
init|=
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|encoding
argument_list|)
decl_stmt|;
name|StringWriter
name|content
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|char
name|ch
index|[]
init|=
operator|new
name|char
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|len
init|=
literal|0
decl_stmt|;
while|while
condition|(
operator|(
name|len
operator|=
name|reader
operator|.
name|read
argument_list|(
name|ch
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
name|content
operator|.
name|write
argument_list|(
name|ch
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|String
name|xml
init|=
name|content
operator|.
name|toString
argument_list|()
decl_stmt|;
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
name|Response
name|r
init|=
name|server
operator|.
name|doPost
argument_list|(
name|broker
argument_list|,
name|xml
argument_list|,
name|path
argument_list|)
decl_stmt|;
name|writeResponse
argument_list|(
name|r
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
parameter_list|)
block|{
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
condition|)
return|return
name|defaultUser
return|;
name|byte
index|[]
name|c
init|=
name|Base64
operator|.
name|decode
argument_list|(
name|auth
operator|.
name|substring
argument_list|(
literal|6
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|int
name|p
init|=
name|s
operator|.
name|indexOf
argument_list|(
literal|':'
argument_list|)
decl_stmt|;
name|String
name|username
init|=
name|s
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|s
operator|.
name|substring
argument_list|(
name|p
operator|+
literal|1
argument_list|)
decl_stmt|;
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
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
operator|!
name|user
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
return|return
literal|null
return|;
return|return
name|user
return|;
block|}
specifier|private
name|Map
name|getParameters
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|Map
name|params
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|String
name|key
decl_stmt|;
for|for
control|(
name|Enumeration
name|e
init|=
name|request
operator|.
name|getParameterNames
argument_list|()
init|;
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|;
control|)
block|{
name|key
operator|=
operator|(
name|String
operator|)
name|e
operator|.
name|nextElement
argument_list|()
expr_stmt|;
name|params
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|request
operator|.
name|getParameter
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|params
return|;
block|}
specifier|private
name|void
name|writeResponse
parameter_list|(
name|Response
name|internal
parameter_list|,
name|HttpServletResponse
name|response
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|internal
operator|.
name|getResponseCode
argument_list|()
operator|!=
name|HttpServletResponse
operator|.
name|SC_OK
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|internal
operator|.
name|getResponseCode
argument_list|()
argument_list|,
name|internal
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|contentType
init|=
name|internal
operator|.
name|getContentType
argument_list|()
operator|+
literal|"; charset="
operator|+
name|internal
operator|.
name|getEncoding
argument_list|()
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
if|if
condition|(
name|internal
operator|.
name|getContent
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|internal
operator|.
name|getDescription
argument_list|()
operator|!=
literal|null
condition|)
name|internal
operator|.
name|setContent
argument_list|(
name|internal
operator|.
name|getDescription
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|internal
operator|.
name|setContent
argument_list|(
literal|"OK"
argument_list|)
expr_stmt|;
block|}
name|ServletOutputStream
name|os
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|os
operator|.
name|write
argument_list|(
name|internal
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
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
name|this
operator|.
name|log
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

