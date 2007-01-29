begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|OutputStreamWriter
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintWriter
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
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSession
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
name|source
operator|.
name|FileSource
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
name|xmldb
operator|.
name|CollectionImpl
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
name|XQueryService
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
name|functions
operator|.
name|request
operator|.
name|RequestModule
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
name|functions
operator|.
name|response
operator|.
name|ResponseModule
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
name|functions
operator|.
name|session
operator|.
name|SessionModule
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
name|util
operator|.
name|HTTPUtils
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
name|ResourceIterator
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

begin_comment
comment|/**  * Servlet to generate HTML output from an XQuery file.  *  * The servlet responds to an URL pattern as specified in the  * WEB-INF/web.xml configuration file of the application. It will  * interpret the path with which it is called as leading to a valid  * XQuery file. The XQuery file is loaded, compiled and executed.  * Any output of the script is sent back to the client.  *  * The servlet accepts the following initialization parameters in web.xml:  *  *<table border="0">  *<tr><td>user</td><td>The user identity with which the script is executed.</td></tr>  *<tr><td>password</td><td>Password for the user.</td></tr>  *<tr><td>uri</td><td>A valid XML:DB URI leading to the root collection used to  * 	process the request.</td></tr>  *<tr><td>encoding</td><td>The character encoding used for XQuery files.</td></tr>  *<tr><td>container-encoding</td><td>The character encoding used by the servlet  * 	container.</td></tr>  *<tr><td>form-encoding</td><td>The character encoding used by parameters posted  * 	from HTML forms.</td></tr>  *</table>  *  * User identity and password may also be specified through the HTTP session attributes  * "user" and "password". These attributes will overwrite any other settings.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|XQueryServlet
extends|extends
name|HttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XQueryServlet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_USER
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_PASS
init|=
literal|"guest"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|XmldbURI
name|DEFAULT_URI
init|=
name|XmldbURI
operator|.
name|EMBEDDED_SERVER_URI
operator|.
name|append
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_ENCODING
init|=
literal|"UTF-8"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DEFAULT_CONTENT_TYPE
init|=
literal|"text/html"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|String
name|user
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
specifier|private
name|XmldbURI
name|collectionURI
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|containerEncoding
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|formEncoding
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|encoding
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|contentType
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc)      * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)      */
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
name|user
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
name|user
operator|==
literal|null
condition|)
name|user
operator|=
name|DEFAULT_USER
expr_stmt|;
name|password
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
name|password
operator|==
literal|null
condition|)
name|password
operator|=
name|DEFAULT_PASS
expr_stmt|;
name|String
name|confCollectionURI
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"uri"
argument_list|)
decl_stmt|;
if|if
condition|(
name|confCollectionURI
operator|==
literal|null
condition|)
block|{
name|collectionURI
operator|=
name|DEFAULT_URI
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|collectionURI
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|confCollectionURI
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Invalid XmldbURI for parameter 'uri': "
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
name|LOG
operator|.
name|info
argument_list|(
literal|"form-encoding = "
operator|+
name|formEncoding
argument_list|)
expr_stmt|;
name|containerEncoding
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"container-encoding"
argument_list|)
expr_stmt|;
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
name|LOG
operator|.
name|info
argument_list|(
literal|"container-encoding = "
operator|+
name|containerEncoding
argument_list|)
expr_stmt|;
name|encoding
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"encoding"
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|==
literal|null
condition|)
name|encoding
operator|=
name|DEFAULT_ENCODING
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"encoding = "
operator|+
name|encoding
argument_list|)
expr_stmt|;
name|contentType
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"content-type"
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
name|contentType
operator|=
name|DEFAULT_CONTENT_TYPE
expr_stmt|;
try|try
block|{
name|Class
name|driver
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|driver
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
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
name|Exception
name|e
parameter_list|)
block|{
name|String
name|errorMessage
init|=
literal|"Failed to initialize database driver"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errorMessage
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|ServletException
argument_list|(
name|errorMessage
operator|+
literal|": "
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
comment|/* (non-Javadoc)      * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
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
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)      */
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
name|process
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
comment|/**      * Processes incoming HTTP requests for XQuery      */
specifier|protected
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
comment|//first, adjust the path
name|String
name|path
init|=
name|request
operator|.
name|getPathTranslated
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
name|request
operator|.
name|getRequestURI
argument_list|()
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
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
name|path
operator|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|request
operator|.
name|getCharacterEncoding
argument_list|()
operator|==
literal|null
condition|)
name|request
operator|.
name|setCharacterEncoding
argument_list|(
name|formEncoding
argument_list|)
expr_stmt|;
name|ServletOutputStream
name|sout
init|=
name|response
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|PrintWriter
name|output
init|=
operator|new
name|PrintWriter
argument_list|(
operator|new
name|OutputStreamWriter
argument_list|(
name|sout
argument_list|,
name|formEncoding
argument_list|)
argument_list|)
decl_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|contentType
operator|+
literal|"; charset="
operator|+
name|formEncoding
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"pragma"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|response
operator|.
name|addHeader
argument_list|(
literal|"Cache-Control"
argument_list|,
literal|"no-cache"
argument_list|)
expr_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|canRead
argument_list|()
condition|)
block|{
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|)
expr_stmt|;
name|sendError
argument_list|(
name|output
argument_list|,
literal|"Cannot read source file"
argument_list|,
name|path
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//allow source viewing for GET?
if|if
condition|(
name|request
operator|.
name|getMethod
argument_list|()
operator|.
name|toUpperCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"GET"
argument_list|)
condition|)
block|{
name|String
name|option
decl_stmt|;
name|boolean
name|source
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|(
name|option
operator|=
name|request
operator|.
name|getParameter
argument_list|(
literal|"_source"
argument_list|)
operator|)
operator|!=
literal|null
condition|)
name|source
operator|=
name|option
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
comment|//Should we display the source of the XQuery or execute it
if|if
condition|(
name|source
operator|&&
name|descriptor
operator|!=
literal|null
condition|)
block|{
comment|//show the source
comment|//check are we allowed to show the xquery source - descriptor.xml
comment|//                System.out.println("path="+path);
if|if
condition|(
name|descriptor
operator|.
name|allowSourceXQuery
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|//Show the source of the XQuery
comment|//writeResourceAs(resource, broker, stylesheet, encoding, "text/plain", outputProperties, response);
name|response
operator|.
name|setContentType
argument_list|(
literal|"text/plain;charset="
operator|+
name|formEncoding
argument_list|)
expr_stmt|;
name|FileSource
name|fs
init|=
operator|new
name|FileSource
argument_list|(
name|f
argument_list|,
name|encoding
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|output
operator|.
name|write
argument_list|(
name|fs
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
return|return;
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
literal|"Permission to view XQuery source for: "
operator|+
name|path
operator|+
literal|" denied. Must be explicitly defined in descriptor.xml"
argument_list|)
expr_stmt|;
return|return;
block|}
block|}
block|}
comment|//-------------------------------
comment|// Added by Igor Abade (igoravl@cosespseguros.com.br)
comment|// Date: Aug/06/2004
comment|//-------------------------------
name|String
name|contentType
init|=
name|this
operator|.
name|contentType
decl_stmt|;
try|try
block|{
name|contentType
operator|=
name|getServletContext
argument_list|()
operator|.
name|getMimeType
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|contentType
operator|==
literal|null
condition|)
name|contentType
operator|=
name|this
operator|.
name|contentType
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|contentType
operator|=
name|this
operator|.
name|contentType
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|contentType
operator|.
name|startsWith
argument_list|(
literal|"text/"
argument_list|)
operator|||
operator|(
name|contentType
operator|.
name|endsWith
argument_list|(
literal|"+xml"
argument_list|)
operator|)
condition|)
name|contentType
operator|+=
literal|";charset="
operator|+
name|formEncoding
expr_stmt|;
name|response
operator|.
name|setContentType
argument_list|(
name|contentType
argument_list|)
expr_stmt|;
block|}
comment|//-------------------------------
comment|//        URI baseUri;
comment|//        try {
comment|//            baseUri = new URI(request.getScheme(),
comment|//                    null/*user info?*/, request.getLocalName(), request.getLocalPort(),
comment|//                    request.getRequestURI(), null, null);
comment|//        } catch(URISyntaxException e) {
comment|//            baseUri = null;
comment|//        }
name|String
name|requestPath
init|=
name|request
operator|.
name|getRequestURI
argument_list|()
decl_stmt|;
name|int
name|p
init|=
name|requestPath
operator|.
name|lastIndexOf
argument_list|(
literal|"/"
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
name|requestPath
operator|=
name|requestPath
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|String
name|moduleLoadPath
init|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|requestPath
operator|.
name|substring
argument_list|(
name|request
operator|.
name|getContextPath
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|actualUser
init|=
literal|null
decl_stmt|;
name|String
name|actualPassword
init|=
literal|null
decl_stmt|;
name|HttpSession
name|session
init|=
name|request
operator|.
name|getSession
argument_list|()
decl_stmt|;
if|if
condition|(
name|session
operator|!=
literal|null
operator|&&
name|request
operator|.
name|isRequestedSessionIdValid
argument_list|()
condition|)
block|{
name|actualUser
operator|=
name|getSessionAttribute
argument_list|(
name|session
argument_list|,
literal|"user"
argument_list|)
expr_stmt|;
name|actualPassword
operator|=
name|getSessionAttribute
argument_list|(
name|session
argument_list|,
literal|"password"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|actualUser
operator|==
literal|null
condition|)
name|actualUser
operator|=
name|user
expr_stmt|;
if|if
condition|(
name|actualPassword
operator|==
literal|null
condition|)
name|actualPassword
operator|=
name|password
expr_stmt|;
try|try
block|{
name|Collection
name|collection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|collectionURI
operator|.
name|toString
argument_list|()
argument_list|,
name|actualUser
argument_list|,
name|actualPassword
argument_list|)
decl_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|collection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
literal|"base-uri"
argument_list|,
name|collectionURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|service
operator|.
name|setModuleLoadPath
argument_list|(
name|moduleLoadPath
argument_list|)
expr_stmt|;
comment|//service.setNamespace(prefix, RequestModule.NAMESPACE_URI);
if|if
condition|(
operator|!
operator|(
operator|(
name|CollectionImpl
operator|)
name|collection
operator|)
operator|.
name|isRemoteCollection
argument_list|()
condition|)
block|{
name|service
operator|.
name|declareVariable
argument_list|(
name|RequestModule
operator|.
name|PREFIX
operator|+
literal|":request"
argument_list|,
operator|new
name|HttpRequestWrapper
argument_list|(
name|request
argument_list|,
name|formEncoding
argument_list|,
name|containerEncoding
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
name|ResponseModule
operator|.
name|PREFIX
operator|+
literal|":response"
argument_list|,
operator|new
name|HttpResponseWrapper
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
name|service
operator|.
name|declareVariable
argument_list|(
name|SessionModule
operator|.
name|PREFIX
operator|+
literal|":session"
argument_list|,
operator|new
name|HttpSessionWrapper
argument_list|(
name|session
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|Source
name|source
init|=
operator|new
name|FileSource
argument_list|(
name|f
argument_list|,
name|encoding
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|String
name|mediaType
init|=
name|service
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|MEDIA_TYPE
argument_list|)
decl_stmt|;
if|if
condition|(
name|mediaType
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|response
operator|.
name|isCommitted
argument_list|()
condition|)
name|response
operator|.
name|setContentType
argument_list|(
name|mediaType
operator|+
literal|"; charset="
operator|+
name|formEncoding
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|ResourceIterator
name|i
init|=
name|result
operator|.
name|getIterator
argument_list|()
init|;
name|i
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|res
init|=
name|i
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|output
operator|.
name|println
argument_list|(
name|res
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|response
operator|.
name|setStatus
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|)
expr_stmt|;
name|sendError
argument_list|(
name|output
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
name|output
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
specifier|private
name|String
name|getSessionAttribute
parameter_list|(
name|HttpSession
name|session
parameter_list|,
name|String
name|attribute
parameter_list|)
block|{
name|Object
name|obj
init|=
name|session
operator|.
name|getAttribute
argument_list|(
name|attribute
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|obj
operator|instanceof
name|Sequence
condition|)
try|try
block|{
return|return
operator|(
operator|(
name|Sequence
operator|)
name|obj
operator|)
operator|.
name|getStringValue
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|obj
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|void
name|sendError
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|message
parameter_list|,
name|XMLDBException
name|e
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<html><head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<title>XQueryServlet Error</title>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<link rel=\"stylesheet\" type=\"text/css\" href=\"error.css\"></link></head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<body><div id=\"container\"><h1>Error found</h1>"
argument_list|)
expr_stmt|;
name|Throwable
name|t
init|=
name|e
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|t
operator|instanceof
name|XPathException
condition|)
block|{
name|XPathException
name|xe
init|=
operator|(
name|XPathException
operator|)
name|t
decl_stmt|;
name|out
operator|.
name|println
argument_list|(
name|xe
operator|.
name|getMessageAsHTML
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<h2>Message:"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</h2>"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|t
operator|!=
literal|null
condition|)
block|{
comment|// t can be null
name|out
operator|.
name|print
argument_list|(
name|HTTPUtils
operator|.
name|printStackTraceHTML
argument_list|(
name|t
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|print
argument_list|(
literal|"</div></body></html>"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|sendError
parameter_list|(
name|PrintWriter
name|out
parameter_list|,
name|String
name|message
parameter_list|,
name|String
name|description
parameter_list|)
block|{
name|out
operator|.
name|print
argument_list|(
literal|"<html><head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<title>XQueryServlet Error</title>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<link rel=\"stylesheet\" type=\"text/css\" href=\"error.css\"></link></head>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"<body><h1>Error found</h1>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"<div class='message'><b>Message:</b>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</div><div class='description'>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
name|description
argument_list|)
expr_stmt|;
name|out
operator|.
name|print
argument_list|(
literal|"</div></body></html>"
argument_list|)
expr_stmt|;
name|out
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
comment|// -jmvanel : never used locally
comment|//	private static final class CachedQuery {
comment|//
comment|//		long lastModified;
comment|//		String sourcePath;
comment|//		CompiledExpression expression;
comment|//
comment|//		public CachedQuery(File sourceFile, CompiledExpression expression) {
comment|//			this.sourcePath = sourceFile.getAbsolutePath();
comment|//			this.lastModified = sourceFile.lastModified();
comment|//			this.expression = expression;
comment|//		}
comment|//
comment|//		public boolean isValid() {
comment|//			File f = new File(sourcePath);
comment|//			if(f.lastModified()> lastModified)
comment|//				return false;
comment|//			return true;
comment|//		}
comment|//
comment|//		public CompiledExpression getExpression() {
comment|//			return expression;
comment|//		}
comment|//	}
block|}
end_class

end_unit

