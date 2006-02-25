begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|http
operator|.
name|webdav
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
name|util
operator|.
name|Properties
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|servlets
operator|.
name|Authenticator
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
name|BasicAuthenticator
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
name|DigestAuthenticator
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
name|storage
operator|.
name|serializers
operator|.
name|EXistOutputKeys
import|;
end_import

begin_comment
comment|/**  * The main class for processing WebDAV requests.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|WebDAV
block|{
specifier|public
specifier|final
specifier|static
name|String
name|DAV_NS
init|=
literal|"DAV:"
decl_stmt|;
comment|// authentication methods
specifier|public
specifier|final
specifier|static
name|int
name|BASIC_AUTH
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|DIGEST_AUTH
init|=
literal|1
decl_stmt|;
comment|//	default content types
specifier|public
specifier|final
specifier|static
name|String
name|BINARY_CONTENT
init|=
literal|"application/octet-stream"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|XML_CONTENT
init|=
literal|"text/xml"
decl_stmt|;
comment|/** id of the database registred against the BrokerPool */
specifier|protected
name|String
name|databaseid
init|=
name|BrokerPool
operator|.
name|DEFAULT_INSTANCE_NAME
decl_stmt|;
comment|//	default output properties for the XML serialization
specifier|public
specifier|final
specifier|static
name|Properties
name|OUTPUT_PROPERTIES
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
static|static
block|{
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|EXPAND_XINCLUDES
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|OUTPUT_PROPERTIES
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|PROCESS_XSL_PI
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
block|}
comment|// additional response codes
specifier|public
specifier|final
specifier|static
name|int
name|SC_MULTI_STATUS
init|=
literal|207
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|WebDAV
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|int
name|defaultAuthMethod
decl_stmt|;
specifier|private
name|Authenticator
name|digestAuth
decl_stmt|;
specifier|private
name|Authenticator
name|basicAuth
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|WebDAV
parameter_list|(
name|int
name|authenticationMethod
parameter_list|,
name|String
name|id
parameter_list|)
throws|throws
name|ServletException
block|{
if|if
condition|(
name|id
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|id
argument_list|)
condition|)
name|this
operator|.
name|databaseid
operator|=
name|id
expr_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|this
operator|.
name|databaseid
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
literal|"Error found while initializing "
operator|+
literal|"database: "
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
name|defaultAuthMethod
operator|=
name|authenticationMethod
expr_stmt|;
name|digestAuth
operator|=
operator|new
name|DigestAuthenticator
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|basicAuth
operator|=
operator|new
name|BasicAuthenticator
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
comment|/**      * Process a WebDAV request. The request is delegated to the corresponding      * {@link WebDAVMethod} after authenticating the user.      *      * @param request           an HttpServletRequest object that contains      *                          the request the client has made of the servlet      * @param response          an HttpServletResponse object that contains the      *                          response the servlet sends to the client      * @throws ServletException if the request could not be handled      * @throws IOException      if an input or output error is detected when      *                          the servlet handles the request      */
specifier|public
name|void
name|process
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
comment|// TODO Return error code ?
comment|//            response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
comment|//                        "Please Supply credentials");
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
operator|||
name|path
operator|.
name|length
argument_list|()
operator|==
literal|0
operator|||
name|path
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|response
operator|.
name|sendRedirect
argument_list|(
name|request
operator|.
name|getRequestURI
argument_list|()
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"path='"
operator|+
name|path
operator|+
literal|"'; method='"
operator|+
name|request
operator|.
name|getMethod
argument_list|()
operator|+
literal|"'; user='"
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|"'; Lock-Token='"
operator|+
name|request
operator|.
name|getHeader
argument_list|(
literal|"Lock-Token"
argument_list|)
operator|+
literal|"'; If='"
operator|+
name|request
operator|.
name|getHeader
argument_list|(
literal|"If"
argument_list|)
operator|+
literal|"'"
argument_list|)
expr_stmt|;
name|WebDAVMethod
name|method
init|=
name|WebDAVMethodFactory
operator|.
name|create
argument_list|(
name|request
operator|.
name|getMethod
argument_list|()
argument_list|,
name|pool
argument_list|)
decl_stmt|;
if|if
condition|(
name|method
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
name|SC_METHOD_NOT_ALLOWED
argument_list|,
literal|"Method is not supported: "
operator|+
name|request
operator|.
name|getMethod
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
name|method
operator|.
name|process
argument_list|(
name|user
argument_list|,
name|request
argument_list|,
name|response
argument_list|,
name|path
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
name|IOException
block|{
name|String
name|credentials
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
name|credentials
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|defaultAuthMethod
operator|==
name|BASIC_AUTH
condition|)
name|basicAuth
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
else|else
name|digestAuth
operator|.
name|sendChallenge
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
if|if
condition|(
name|credentials
operator|.
name|toUpperCase
argument_list|()
operator|.
name|startsWith
argument_list|(
literal|"DIGEST"
argument_list|)
condition|)
block|{
return|return
name|digestAuth
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|basicAuth
operator|.
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

