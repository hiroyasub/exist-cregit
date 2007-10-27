begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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

begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

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
name|Namespaces
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
name|w3c
operator|.
name|dom
operator|.
name|Document
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
name|servlet
operator|.
name|RequestDispatcher
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
comment|/**  * Servlet to redirect HTTP requests. The request is passed to an XQuery whose return value  * determines where the request will be redirected to. The query should return a single XML element:  *  *<pre>  *&lt;exist:dispatch xmlns:exist="http://exist.sourceforge.net/NS/exist"  *      path="/preview.xql" servlet-name="MyServlet" redirect="path">  *&lt;exist:add-parameter name="new-param" value="new-param-value"/>  *&lt;/exist:dispatch>  *</pre>  *  * The element should have one of three attributes:<em>path</em>,<em>servlet-name</em> or  *<em>redirect</em>.  *  * If the servlet-name attribute is present, the request will be forwarded to the named servlet  * (name as specified in web.xml). Alternatively, path can point to an arbitrary resource. It can be either absolute or relative.  * Relative paths are resolved relative to the original request.  *  * The request is forwarded via {@link javax.servlet.RequestDispatcher#forward(javax.servlet.ServletRequest, javax.servlet.ServletResponse)}.  * Contrary to HTTP forwarding, there is no additional roundtrip to the client. It all happens on  * the server. The client will not notice the redirect.  *  * When forwarding to other servlets, the fields in {@link javax.servlet.http.HttpServletRequest} will be  * updated to point to the new, redirected URI. However, the original request URI is stored in the  * request attribute org.exist.forward.request-uri.  *  * If present, the "redirect" attribute causes the server to send a redirect request to the client, which will usually respond  * with a new request to the redirected location. Note that this is quite different from a forwarding via RequestDispatcher,  * which is completely transparent to the client.  *  * RedirectorServlet takes a single parameter in web.xml: "xquery". This parameter should point to an  * XQuery script. It should be relative to the current web context.  *  *<pre>  *&lt;servlet>  *&lt;servlet-name>RedirectorServlet</servlet-name>  *&lt;servlet-class>org.exist.http.servlets.RedirectorServlet</servlet-class>  *  *&lt;init-param>  *&lt;param-name>xquery</param-name>  *&lt;param-value>dispatcher.xql</param-value>  *&lt;/init-param>  *&lt;/servlet>  *  *&lt;servlet-mapping>  *&lt;servlet-name>RedirectorServlet</servlet-name>  *&lt;url-pattern>/wiki/*</url-pattern>  *&lt;/servlet-mapping>  *</pre>  */
end_comment

begin_class
specifier|public
class|class
name|RedirectorServlet
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
name|RedirectorServlet
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
name|query
init|=
literal|null
decl_stmt|;
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
name|query
operator|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"xquery"
argument_list|)
expr_stmt|;
if|if
condition|(
name|query
operator|==
literal|null
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"RedirectorServlet requires a parameter 'xquery'."
argument_list|)
throw|;
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
specifier|protected
name|void
name|service
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
comment|// Try to find the XQuery
name|String
name|qpath
init|=
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|qpath
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|f
operator|.
name|canRead
argument_list|()
operator|&&
name|f
operator|.
name|isFile
argument_list|()
operator|)
condition|)
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot read XQuery source from "
operator|+
name|f
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
throw|;
name|FileSource
name|source
init|=
operator|new
name|FileSource
argument_list|(
name|f
argument_list|,
literal|"UTF-8"
argument_list|,
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Prepare and execute the XQuery
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
name|user
argument_list|,
name|password
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
literal|"UTF-8"
argument_list|,
literal|"UTF-8"
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
name|request
operator|.
name|getSession
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|redirectTo
init|=
literal|null
decl_stmt|;
name|String
name|servletName
init|=
literal|null
decl_stmt|;
name|String
name|path
init|=
literal|null
decl_stmt|;
name|RequestWrapper
name|modifiedRequest
init|=
literal|null
decl_stmt|;
comment|// parse the query result element
if|if
condition|(
name|result
operator|.
name|getSize
argument_list|()
operator|==
literal|1
condition|)
block|{
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
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
name|DOCUMENT_NODE
condition|)
name|node
operator|=
operator|(
operator|(
name|Document
operator|)
name|node
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|getNodeType
argument_list|()
operator|!=
name|Node
operator|.
name|ELEMENT_NODE
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Redirect XQuery should return an XML element. Received: "
operator|+
name|resource
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
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
operator|!
operator|(
name|Namespaces
operator|.
name|EXIST_NS
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
operator|&&
literal|"dispatch"
operator|.
name|equals
argument_list|(
name|elem
operator|.
name|getLocalName
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Redirect XQuery should return an element<exist:dispatch>. Received: "
operator|+
name|resource
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
literal|"path"
argument_list|)
condition|)
name|path
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"path"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
literal|"servlet-name"
argument_list|)
condition|)
name|servletName
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"servlet-name"
argument_list|)
expr_stmt|;
if|else if
condition|(
name|elem
operator|.
name|hasAttribute
argument_list|(
literal|"redirect"
argument_list|)
condition|)
name|redirectTo
operator|=
name|elem
operator|.
name|getAttribute
argument_list|(
literal|"redirect"
argument_list|)
expr_stmt|;
else|else
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"Element<exist:dispatch> should either provide an attribute 'path' or 'servlet-name'. Received: "
operator|+
name|resource
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Check for add-parameter elements etc.
if|if
condition|(
name|elem
operator|.
name|hasChildNodes
argument_list|()
condition|)
block|{
name|node
operator|=
name|elem
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
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
name|Namespaces
operator|.
name|EXIST_NS
operator|.
name|equals
argument_list|(
name|node
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
condition|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|node
expr_stmt|;
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
if|if
condition|(
name|modifiedRequest
operator|==
literal|null
condition|)
name|modifiedRequest
operator|=
operator|new
name|RequestWrapper
argument_list|(
name|request
argument_list|)
expr_stmt|;
name|modifiedRequest
operator|.
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
if|if
condition|(
name|redirectTo
operator|!=
literal|null
condition|)
block|{
comment|// directly redirect to the specified URI
name|response
operator|.
name|sendRedirect
argument_list|(
name|redirectTo
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// Get a RequestDispatcher, either from the servlet context or the request
name|RequestDispatcher
name|dispatcher
decl_stmt|;
if|if
condition|(
name|servletName
operator|!=
literal|null
operator|&&
name|servletName
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
name|dispatcher
operator|=
name|getServletContext
argument_list|()
operator|.
name|getNamedDispatcher
argument_list|(
name|servletName
argument_list|)
expr_stmt|;
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Dispatching to "
operator|+
name|path
argument_list|)
expr_stmt|;
name|dispatcher
operator|=
name|getServletContext
argument_list|()
operator|.
name|getRequestDispatcher
argument_list|(
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|dispatcher
operator|==
literal|null
condition|)
name|dispatcher
operator|=
name|request
operator|.
name|getRequestDispatcher
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|dispatcher
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
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"Could not create a request dispatcher. Giving up."
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|modifiedRequest
operator|!=
literal|null
condition|)
name|request
operator|=
name|modifiedRequest
expr_stmt|;
comment|// store the original request URI to org.exist.forward.request-uri
name|request
operator|.
name|setAttribute
argument_list|(
literal|"org.exist.forward.request-uri"
argument_list|,
name|request
operator|.
name|getRequestURI
argument_list|()
argument_list|)
expr_stmt|;
comment|// finally, execute the forward
name|dispatcher
operator|.
name|forward
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"An error occurred while initializing RedirectorServlet: "
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
specifier|private
class|class
name|RequestWrapper
extends|extends
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequestWrapper
block|{
name|Map
name|addedParams
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|private
name|RequestWrapper
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|super
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
specifier|public
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
name|addedParams
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|addedParams
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
return|return
name|value
return|;
return|return
name|super
operator|.
name|getParameter
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
name|Map
name|getParameterMap
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|public
name|Enumeration
name|getParameterNames
parameter_list|()
block|{
name|Vector
name|v
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|addedParams
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|v
operator|.
name|addElement
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Iterator
name|i
init|=
name|super
operator|.
name|getParameterMap
argument_list|()
operator|.
name|keySet
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|String
name|key
init|=
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|v
operator|.
name|addElement
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
return|return
name|v
operator|.
name|elements
argument_list|()
return|;
block|}
specifier|public
name|String
index|[]
name|getParameterValues
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|addedParams
operator|.
name|get
argument_list|(
name|s
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
return|return
operator|new
name|String
index|[]
block|{
name|value
block|}
return|;
return|return
name|super
operator|.
name|getParameterValues
argument_list|(
name|s
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

