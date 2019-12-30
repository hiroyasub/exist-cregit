begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|collections
operator|.
name|Collection
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
name|DBBroker
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
name|txn
operator|.
name|Txn
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
name|EOFException
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
name|*
import|;
end_import

begin_comment
comment|/**  * Implements the REST-style interface if eXist is running within a Servlet  * engine. The real work is done by class {@link org.exist.http.RESTServer}.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|EXistServlet
extends|extends
name|AbstractExistHttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3563999345725645647L
decl_stmt|;
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
name|EXistServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|RESTServer
name|srvREST
decl_stmt|;
specifier|public
enum|enum
name|FeatureEnabled
block|{
name|FALSE
block|,
name|TRUE
block|,
name|AUTHENTICATED_USERS_ONLY
block|}
annotation|@
name|Override
specifier|public
name|Logger
name|getLog
parameter_list|()
block|{
return|return
name|LOG
return|;
block|}
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
name|String
name|useDynamicContentType
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"dynamic-content-type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|useDynamicContentType
operator|==
literal|null
condition|)
block|{
name|useDynamicContentType
operator|=
literal|"no"
expr_stmt|;
block|}
specifier|final
name|FeatureEnabled
name|xquerySubmission
init|=
name|parseFeatureEnabled
argument_list|(
name|config
argument_list|,
literal|"xquery-submission"
argument_list|,
name|FeatureEnabled
operator|.
name|TRUE
argument_list|)
decl_stmt|;
specifier|final
name|FeatureEnabled
name|xupdateSubmission
init|=
name|parseFeatureEnabled
argument_list|(
name|config
argument_list|,
literal|"xupdate-submission"
argument_list|,
name|FeatureEnabled
operator|.
name|TRUE
argument_list|)
decl_stmt|;
comment|// Instantiate REST Server
name|srvREST
operator|=
operator|new
name|RESTServer
argument_list|(
name|getPool
argument_list|()
argument_list|,
name|getFormEncoding
argument_list|()
argument_list|,
name|getContainerEncoding
argument_list|()
argument_list|,
name|useDynamicContentType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
operator|||
name|useDynamicContentType
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
argument_list|,
name|isInternalOnly
argument_list|()
argument_list|,
name|xquerySubmission
argument_list|,
name|xupdateSubmission
argument_list|)
expr_stmt|;
comment|// XML lib checks....
name|XmlLibraryChecker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
specifier|private
name|FeatureEnabled
name|parseFeatureEnabled
parameter_list|(
specifier|final
name|ServletConfig
name|config
parameter_list|,
specifier|final
name|String
name|paramName
parameter_list|,
specifier|final
name|FeatureEnabled
name|defaultValue
parameter_list|)
block|{
specifier|final
name|String
name|paramValue
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
name|paramName
argument_list|)
decl_stmt|;
if|if
condition|(
name|paramValue
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|paramValue
condition|)
block|{
case|case
literal|"disabled"
case|:
return|return
name|FeatureEnabled
operator|.
name|FALSE
return|;
case|case
literal|"enabled"
case|:
return|return
name|FeatureEnabled
operator|.
name|TRUE
return|;
case|case
literal|"authenticated"
case|:
return|return
name|FeatureEnabled
operator|.
name|AUTHENTICATED_USERS_ONLY
return|;
block|}
block|}
return|return
name|defaultValue
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see      * javax.servlet.http.HttpServlet#doPut(javax.servlet.http.HttpServletRequest      * , javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
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
comment|// first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|// TODO: figure out a way to log PUT requests with
comment|// HttpServletRequestWrapper and
comment|// Descriptor.doLogRequestInReplayLog()
comment|// map's the path if a mapping is specified in the descriptor
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
comment|// third, authenticate the user
specifier|final
name|Subject
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
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user or password");
return|return;
block|}
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|getPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|XmldbURI
name|dbpath
init|=
name|XmldbURI
operator|.
name|createInternal
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|dbpath
argument_list|)
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
literal|"A PUT request is not allowed against a plain collection path."
argument_list|)
expr_stmt|;
return|return;
block|}
name|srvREST
operator|.
name|doPut
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|dbpath
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BadRequestException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// If the current user is the Default User and they do not have permission
comment|// then send a challenge request to prompt the client for a username/password.
comment|// Else return a FORBIDDEN Error
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|getDefaultUser
argument_list|()
argument_list|)
condition|)
block|{
name|getAuthenticator
argument_list|()
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An unknown error occurred: "
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
comment|/**      * @param request      */
specifier|private
name|String
name|adjustPath
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
throws|throws
name|ServletException
block|{
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
return|return
literal|""
return|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|" In: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
comment|// path contains both required and superficial escapes,
comment|// as different user agents use different conventions;
comment|// for the sake of interoperability, remove any unnecessary escapes
try|try
block|{
comment|// URI.create undoes _all_ escaping, so protect slashes first
name|URI
name|u
init|=
name|URI
operator|.
name|create
argument_list|(
literal|"file://"
operator|+
name|path
operator|.
name|replaceAll
argument_list|(
literal|"%2F"
argument_list|,
literal|"%252F"
argument_list|)
argument_list|)
decl_stmt|;
comment|// URI four-argument constructor recreates all the necessary ones
name|URI
name|v
init|=
operator|new
name|URI
argument_list|(
literal|"http"
argument_list|,
literal|"host"
argument_list|,
name|u
operator|.
name|getPath
argument_list|()
argument_list|,
literal|null
argument_list|)
operator|.
name|normalize
argument_list|()
decl_stmt|;
comment|// unprotect slashes in now normalized path
name|path
operator|=
name|v
operator|.
name|getRawPath
argument_list|()
operator|.
name|replaceAll
argument_list|(
literal|"%252F"
argument_list|,
literal|"%2F"
argument_list|)
expr_stmt|;
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
comment|// eat trailing slashes, else collections might not be found
while|while
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
name|path
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|path
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
comment|// path now is in proper canonical encoded form
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Out: "
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/*      * (non-Javadoc)      *       * @see      * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest      * , javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
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
comment|// first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// second, perform descriptor actions
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
operator|!=
literal|null
operator|&&
operator|!
name|descriptor
operator|.
name|requestsFiltered
argument_list|()
condition|)
block|{
comment|// logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// map's the path if a mapping is specified in the descriptor
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
comment|// third, authenticate the user
specifier|final
name|Subject
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
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user " + "or password");
return|return;
block|}
comment|// fourth, process the request
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|getPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|srvREST
operator|.
name|doGet
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BadRequestException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// If the current user is the Default User and they do not have permission
comment|// then send a challenge request to prompt the client for a username/password.
comment|// Else return a FORBIDDEN Error
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|getDefaultUser
argument_list|()
argument_list|)
condition|)
block|{
name|getAuthenticator
argument_list|()
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
specifier|final
name|NotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|EOFException
name|ee
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
literal|"GET Connection has been interrupted"
argument_list|,
name|ee
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"GET Connection has been interrupted"
argument_list|,
name|ee
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An error occurred: "
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
comment|// first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// second, perform descriptor actions
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
operator|!=
literal|null
operator|&&
operator|!
name|descriptor
operator|.
name|requestsFiltered
argument_list|()
condition|)
block|{
comment|// logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// map's the path if a mapping is specified in the descriptor
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
comment|// third, authenticate the user
specifier|final
name|Subject
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
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user " + "or password");
return|return;
block|}
comment|// fourth, process the request
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|getPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|srvREST
operator|.
name|doHead
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|BadRequestException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// If the current user is the Default User and they do not have permission
comment|// then send a challenge request to prompt the client for a username/password.
comment|// Else return a FORBIDDEN Error
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|getDefaultUser
argument_list|()
argument_list|)
condition|)
block|{
name|getAuthenticator
argument_list|()
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
specifier|final
name|NotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An unknown error occurred: "
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
comment|/*      * (non-Javadoc)      *       * @see      * javax.servlet.http.HttpServlet#doDelete(javax.servlet.http.HttpServletRequest      * , javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
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
comment|// first, adjust the path
name|String
name|path
init|=
name|adjustPath
argument_list|(
name|request
argument_list|)
decl_stmt|;
comment|// second, perform descriptor actions
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
operator|!=
literal|null
condition|)
block|{
comment|// map's the path if a mapping is specified in the descriptor
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
comment|// third, authenticate the user
specifier|final
name|Subject
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
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user " + "or password");
return|return;
block|}
comment|// fourth, process the request
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|getPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|srvREST
operator|.
name|doDelete
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|path
argument_list|,
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// If the current user is the Default User and they do not have permission
comment|// then send a challenge request to prompt the client for a username/password.
comment|// Else return a FORBIDDEN Error
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|getDefaultUser
argument_list|()
argument_list|)
condition|)
block|{
name|getAuthenticator
argument_list|()
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
specifier|final
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
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An unknown error occurred: "
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
comment|/*      * (non-Javadoc)      *       * @see      * javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest      * , javax.servlet.http.HttpServletResponse)      */
annotation|@
name|Override
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
try|try
block|{
comment|// For POST request, If we are logging the requests we must wrap
comment|// HttpServletRequest in HttpServletRequestWrapper
comment|// otherwise we cannot access the POST parameters from the content body
comment|// of the request!!! - deliriumsky
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
parameter_list|()
lambda|->
operator|(
name|String
operator|)
name|getPool
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|Configuration
operator|.
name|BINARY_CACHE_CLASS_PROPERTY
argument_list|)
argument_list|,
name|req
argument_list|,
name|getFormEncoding
argument_list|()
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
comment|// first, adjust the path
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
comment|// second, perform descriptor actions
if|if
condition|(
name|descriptor
operator|!=
literal|null
operator|&&
operator|!
name|descriptor
operator|.
name|requestsFiltered
argument_list|()
condition|)
block|{
comment|// logs the request if specified in the descriptor
name|descriptor
operator|.
name|doLogRequestInReplayLog
argument_list|(
name|request
argument_list|)
expr_stmt|;
comment|// map's the path if a mapping is specified in the descriptor
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
comment|// third, authenticate the user
specifier|final
name|Subject
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
comment|// response.sendError(HttpServletResponse.SC_FORBIDDEN,
comment|// "Permission denied: unknown user " + "or password");
return|return;
block|}
comment|// fourth, process the request
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|getPool
argument_list|()
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|getPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|srvREST
operator|.
name|doPost
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
name|e
parameter_list|)
block|{
comment|// If the current user is the Default User and they do not have permission
comment|// then send a challenge request to prompt the client for a username/password.
comment|// Else return a FORBIDDEN Error
if|if
condition|(
name|user
operator|!=
literal|null
operator|&&
name|user
operator|.
name|equals
argument_list|(
name|getDefaultUser
argument_list|()
argument_list|)
condition|)
block|{
name|getAuthenticator
argument_list|()
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
else|else
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
block|}
catch|catch
parameter_list|(
specifier|final
name|EXistException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|BadRequestException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|NotFoundException
name|e
parameter_list|)
block|{
if|if
condition|(
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
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
specifier|final
name|Throwable
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|error
argument_list|(
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An unknown error occurred: "
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
finally|finally
block|{
if|if
condition|(
name|request
operator|!=
literal|null
operator|&&
name|request
operator|instanceof
name|HttpServletRequestWrapper
condition|)
block|{
operator|(
operator|(
name|HttpServletRequestWrapper
operator|)
name|request
operator|)
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

