begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|http
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
name|FileInputStream
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
name|InputStream
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
name|URL
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
name|HttpServletRequestWrapper
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
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
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
name|atom
operator|.
name|Atom
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|AtomModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
operator|.
name|AtomFeeds
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
operator|.
name|AtomProtocol
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|atom
operator|.
name|modules
operator|.
name|Query
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
name|servlets
operator|.
name|AbstractExistHttpServlet
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
name|security
operator|.
name|Account
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
name|Subject
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
name|validation
operator|.
name|XmlLibraryChecker
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
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|InputSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Implements a rest interface for exist collections as atom feeds  *   * @author Alex Milowski  */
end_comment

begin_class
specifier|public
class|class
name|AtomServlet
extends|extends
name|AbstractExistHttpServlet
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|1L
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
name|CONF_NS
init|=
literal|"http://www.exist-db.org/Vocabulary/AtomConfiguration/2006/1/0"
decl_stmt|;
comment|// authentication methods ; copied from original webdav classes
specifier|public
specifier|final
specifier|static
name|int
name|WEBDAV_BASIC_AUTH
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|WEBDAV_DIGEST_AUTH
init|=
literal|1
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
name|AtomServlet
operator|.
name|class
argument_list|)
decl_stmt|;
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
comment|/** 	 * A user principal object that implements XmldbPrincipal 	 */
specifier|static
class|class
name|UserXmldbPrincipal
implements|implements
name|XmldbPrincipal
block|{
name|int
name|authMethod
decl_stmt|;
name|Account
name|user
decl_stmt|;
name|UserXmldbPrincipal
parameter_list|(
name|int
name|authMethod
parameter_list|,
name|Account
name|user
parameter_list|)
block|{
name|this
operator|.
name|authMethod
operator|=
name|authMethod
expr_stmt|;
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|user
operator|.
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|authMethod
operator|==
name|WEBDAV_BASIC_AUTH
condition|?
name|user
operator|.
name|getPassword
argument_list|()
else|:
name|user
operator|.
name|getDigestPassword
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasRole
parameter_list|(
name|String
name|role
parameter_list|)
block|{
return|return
name|user
operator|.
name|hasGroup
argument_list|(
name|role
argument_list|)
return|;
block|}
block|}
comment|/** 	 * Module contexts that default to using the servlet's config 	 */
class|class
name|ModuleContext
implements|implements
name|AtomModule
operator|.
name|Context
block|{
name|ServletConfig
name|config
decl_stmt|;
name|String
name|moduleLoadPath
decl_stmt|;
name|ModuleContext
parameter_list|(
name|ServletConfig
name|config
parameter_list|,
name|String
name|subpath
parameter_list|,
name|String
name|moduleLoadPath
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
name|moduleLoadPath
operator|=
name|moduleLoadPath
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDefaultCharset
parameter_list|()
block|{
return|return
name|formEncoding
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getParameter
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|config
operator|.
name|getInitParameter
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getContextPath
parameter_list|()
block|{
comment|// TODO: finish
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|URL
name|getContextURL
parameter_list|()
block|{
comment|// TODO: finish
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getModuleLoadPath
parameter_list|()
block|{
return|return
name|moduleLoadPath
return|;
block|}
block|}
comment|// What I want...
comment|// private Map<String,AtomModule> modules;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|AtomModule
argument_list|>
name|modules
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|noAuth
decl_stmt|;
specifier|private
name|String
name|formEncoding
init|=
literal|null
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
name|Authenticator
name|authenticator
decl_stmt|;
specifier|private
name|Subject
name|defaultUser
decl_stmt|;
comment|/* 	 * (non-Javadoc) 	 *  	 * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig) 	 */
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
comment|// Load all the modules
comment|// modules = new HashMap<String,AtomModule>();
name|modules
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|AtomModule
argument_list|>
argument_list|()
expr_stmt|;
name|noAuth
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
argument_list|()
expr_stmt|;
name|String
name|configFileOpt
init|=
name|config
operator|.
name|getInitParameter
argument_list|(
literal|"config-file"
argument_list|)
decl_stmt|;
name|File
name|dbHome
init|=
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getExistHome
argument_list|()
decl_stmt|;
name|File
name|atomConf
decl_stmt|;
if|if
condition|(
name|configFileOpt
operator|==
literal|null
condition|)
name|atomConf
operator|=
operator|new
name|File
argument_list|(
name|dbHome
argument_list|,
literal|"atom-services.xml"
argument_list|)
expr_stmt|;
else|else
name|atomConf
operator|=
operator|new
name|File
argument_list|(
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|getRealPath
argument_list|(
name|configFileOpt
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Checking for atom configuration in "
operator|+
name|atomConf
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|atomConf
operator|.
name|exists
argument_list|()
condition|)
block|{
name|config
operator|.
name|getServletContext
argument_list|()
operator|.
name|log
argument_list|(
literal|"Loading configuration "
operator|+
name|atomConf
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|DocumentBuilderFactory
name|docFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|docFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|DocumentBuilder
name|docBuilder
init|=
literal|null
decl_stmt|;
name|Document
name|confDoc
init|=
literal|null
decl_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
operator|new
name|FileInputStream
argument_list|(
name|atomConf
argument_list|)
expr_stmt|;
name|InputSource
name|src
init|=
operator|new
name|InputSource
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|is
argument_list|,
name|formEncoding
argument_list|)
argument_list|)
decl_stmt|;
name|URI
name|docBaseURI
init|=
name|atomConf
operator|.
name|toURI
argument_list|()
decl_stmt|;
name|src
operator|.
name|setSystemId
argument_list|(
name|docBaseURI
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|docBuilder
operator|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
expr_stmt|;
name|confDoc
operator|=
name|docBuilder
operator|.
name|parse
argument_list|(
name|src
argument_list|)
expr_stmt|;
name|confDoc
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
comment|// Add all the modules
name|NodeList
name|moduleConfList
init|=
name|confDoc
operator|.
name|getElementsByTagNameNS
argument_list|(
name|CONF_NS
argument_list|,
literal|"module"
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
name|moduleConfList
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Element
name|moduleConf
init|=
operator|(
name|Element
operator|)
name|moduleConfList
operator|.
name|item
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|name
init|=
name|moduleConf
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|modules
operator|.
name|get
argument_list|(
name|name
argument_list|)
operator|!=
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Module '"
operator|+
name|name
operator|+
literal|"' is configured more than once ( child # "
operator|+
operator|(
name|i
operator|+
literal|1
operator|)
argument_list|)
throw|;
block|}
if|if
condition|(
literal|"false"
operator|.
name|equals
argument_list|(
name|moduleConf
operator|.
name|getAttribute
argument_list|(
literal|"authenticate"
argument_list|)
argument_list|)
condition|)
block|{
name|noAuth
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|Boolean
operator|.
name|TRUE
argument_list|)
expr_stmt|;
block|}
name|String
name|className
init|=
name|moduleConf
operator|.
name|getAttribute
argument_list|(
literal|"class"
argument_list|)
decl_stmt|;
if|if
condition|(
name|className
operator|!=
literal|null
operator|&&
name|className
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|moduleClass
init|=
name|Class
operator|.
name|forName
argument_list|(
name|className
argument_list|)
decl_stmt|;
name|AtomModule
name|amodule
init|=
operator|(
name|AtomModule
operator|)
name|moduleClass
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|amodule
argument_list|)
expr_stmt|;
name|amodule
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
name|name
argument_list|,
name|atomConf
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot instantiate class "
operator|+
name|className
operator|+
literal|" for module '"
operator|+
name|name
operator|+
literal|"' due to exception: "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
else|else
block|{
comment|// no class means query
name|Query
name|query
init|=
operator|new
name|Query
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|String
name|allowQueryPost
init|=
name|moduleConf
operator|.
name|getAttribute
argument_list|(
literal|"query-by-post"
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"true"
operator|.
name|equals
argument_list|(
name|allowQueryPost
argument_list|)
condition|)
block|{
name|query
operator|.
name|setQueryByPost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|NodeList
name|methodList
init|=
name|moduleConf
operator|.
name|getElementsByTagNameNS
argument_list|(
name|CONF_NS
argument_list|,
literal|"method"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|m
init|=
literal|0
init|;
name|m
operator|<
name|methodList
operator|.
name|getLength
argument_list|()
condition|;
name|m
operator|++
control|)
block|{
name|Element
name|methodConf
init|=
operator|(
name|Element
operator|)
name|methodList
operator|.
name|item
argument_list|(
name|m
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|methodConf
operator|.
name|getAttribute
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|type
operator|==
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
literal|"No type specified for method in module "
operator|+
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
comment|// What I want but can't have because of JDK 1.4
comment|// URI baseURI = URI.create(methodConf.getBaseURI());
name|URI
name|baseURI
init|=
name|docBaseURI
decl_stmt|;
name|String
name|queryRef
init|=
name|methodConf
operator|.
name|getAttribute
argument_list|(
literal|"query"
argument_list|)
decl_stmt|;
if|if
condition|(
name|queryRef
operator|==
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
literal|"No query specified for method "
operator|+
name|type
operator|+
literal|" in module "
operator|+
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|boolean
name|fromClasspath
init|=
literal|"true"
operator|.
name|equals
argument_list|(
name|methodConf
operator|.
name|getAttribute
argument_list|(
literal|"from-classpath"
argument_list|)
argument_list|)
decl_stmt|;
name|Query
operator|.
name|MethodConfiguration
name|mconf
init|=
name|query
operator|.
name|getMethodConfiguration
argument_list|(
name|type
argument_list|)
decl_stmt|;
if|if
condition|(
name|mconf
operator|==
literal|null
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
literal|"Unknown method "
operator|+
name|type
operator|+
literal|" in module "
operator|+
name|name
argument_list|)
expr_stmt|;
continue|continue;
block|}
name|String
name|responseContentType
init|=
name|methodConf
operator|.
name|getAttribute
argument_list|(
literal|"content-type"
argument_list|)
decl_stmt|;
if|if
condition|(
name|responseContentType
operator|!=
literal|null
operator|&&
name|responseContentType
operator|.
name|trim
argument_list|()
operator|.
name|length
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|mconf
operator|.
name|setContentType
argument_list|(
name|responseContentType
argument_list|)
expr_stmt|;
block|}
name|URL
name|queryURI
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|fromClasspath
condition|)
block|{
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Nope. Attempting to get resource "
operator|+
name|queryRef
operator|+
literal|" from "
operator|+
name|Atom
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|queryURI
operator|=
name|Atom
operator|.
name|class
operator|.
name|getResource
argument_list|(
name|queryRef
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryURI
operator|=
name|baseURI
operator|.
name|resolve
argument_list|(
name|queryRef
argument_list|)
operator|.
name|toURL
argument_list|()
expr_stmt|;
block|}
name|getLog
argument_list|()
operator|.
name|debug
argument_list|(
literal|"Loading from module "
operator|+
name|name
operator|+
literal|" method "
operator|+
name|type
operator|+
literal|" from resource "
operator|+
name|queryURI
operator|+
literal|" via classpath("
operator|+
name|fromClasspath
operator|+
literal|") and ref ("
operator|+
name|queryRef
operator|+
literal|")"
argument_list|)
expr_stmt|;
if|if
condition|(
name|queryURI
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Cannot find resource "
operator|+
name|queryRef
operator|+
literal|" for module "
operator|+
name|name
argument_list|)
throw|;
block|}
name|mconf
operator|.
name|setQuerySource
argument_list|(
name|queryURI
argument_list|)
expr_stmt|;
block|}
name|query
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
name|name
argument_list|,
name|atomConf
operator|.
name|getParent
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
catch|catch
parameter_list|(
name|SAXException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
catch|catch
parameter_list|(
name|ParserConfigurationException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|warn
argument_list|(
name|e
argument_list|)
expr_stmt|;
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
finally|finally
block|{
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
block|}
block|}
block|}
block|}
else|else
block|{
try|try
block|{
name|AtomProtocol
name|protocol
init|=
operator|new
name|AtomProtocol
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
literal|"edit"
argument_list|,
name|protocol
argument_list|)
expr_stmt|;
name|protocol
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
literal|"edit"
argument_list|,
name|dbHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|AtomFeeds
name|feeds
init|=
operator|new
name|AtomFeeds
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
literal|"content"
argument_list|,
name|feeds
argument_list|)
expr_stmt|;
name|feeds
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
literal|"content"
argument_list|,
name|dbHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|query
init|=
operator|new
name|Query
argument_list|()
decl_stmt|;
name|query
operator|.
name|setQueryByPost
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|modules
operator|.
name|put
argument_list|(
literal|"query"
argument_list|,
name|query
argument_list|)
expr_stmt|;
name|query
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
literal|"query"
argument_list|,
name|dbHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|topics
init|=
operator|new
name|Query
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
literal|"topic"
argument_list|,
name|topics
argument_list|)
expr_stmt|;
name|topics
operator|.
name|getMethodConfiguration
argument_list|(
literal|"GET"
argument_list|)
operator|.
name|setQuerySource
argument_list|(
name|topics
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"topic.xq"
argument_list|)
argument_list|)
expr_stmt|;
name|topics
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
literal|"topic"
argument_list|,
name|dbHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|Query
name|introspect
init|=
operator|new
name|Query
argument_list|()
decl_stmt|;
name|modules
operator|.
name|put
argument_list|(
literal|"introspect"
argument_list|,
name|introspect
argument_list|)
expr_stmt|;
name|introspect
operator|.
name|getMethodConfiguration
argument_list|(
literal|"GET"
argument_list|)
operator|.
name|setQuerySource
argument_list|(
name|introspect
operator|.
name|getClass
argument_list|()
operator|.
name|getResource
argument_list|(
literal|"introspect.xq"
argument_list|)
argument_list|)
expr_stmt|;
name|introspect
operator|.
name|init
argument_list|(
operator|new
name|ModuleContext
argument_list|(
name|config
argument_list|,
literal|"introspect"
argument_list|,
name|dbHome
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|ServletException
argument_list|(
literal|"Exception during module init(): "
operator|+
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ex
argument_list|)
throw|;
block|}
block|}
comment|// XML lib checks....
name|XmlLibraryChecker
operator|.
name|check
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
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
block|{
try|try
block|{
comment|// Get the path
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
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
literal|"URL has no extra path information specified."
argument_list|)
expr_stmt|;
return|return;
block|}
name|int
name|firstSlash
init|=
name|path
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|,
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|firstSlash
operator|<
literal|0
operator|&&
name|path
operator|.
name|length
argument_list|()
operator|==
literal|1
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
literal|"Module not specified."
argument_list|)
expr_stmt|;
return|return;
block|}
name|String
name|moduleName
init|=
name|firstSlash
operator|<
literal|0
condition|?
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
else|:
name|path
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|firstSlash
argument_list|)
decl_stmt|;
name|path
operator|=
name|firstSlash
operator|<
literal|0
condition|?
literal|""
else|:
name|path
operator|.
name|substring
argument_list|(
name|firstSlash
argument_list|)
expr_stmt|;
name|AtomModule
name|module
init|=
name|modules
operator|.
name|get
argument_list|(
name|moduleName
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
name|response
operator|.
name|sendError
argument_list|(
literal|400
argument_list|,
literal|"Module "
operator|+
name|moduleName
operator|+
literal|" not found."
argument_list|)
expr_stmt|;
return|return;
block|}
name|Subject
name|user
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|noAuth
operator|.
name|get
argument_list|(
name|moduleName
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// Authenticate
name|user
operator|=
name|authenticate
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|// You now get a challenge if there is no user
return|return;
block|}
block|}
specifier|final
name|Principal
name|principal
init|=
operator|new
name|UserXmldbPrincipal
argument_list|(
name|WEBDAV_BASIC_AUTH
argument_list|,
name|user
argument_list|)
decl_stmt|;
name|HttpServletRequest
name|wrappedRequest
init|=
operator|new
name|HttpServletRequestWrapper
argument_list|(
name|request
argument_list|)
block|{
specifier|public
name|Principal
name|getUserPrincipal
parameter_list|()
block|{
return|return
name|principal
return|;
block|}
block|}
decl_stmt|;
comment|// Handle the resource
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
name|module
operator|.
name|process
argument_list|(
name|broker
argument_list|,
operator|new
name|HttpRequestMessage
argument_list|(
name|request
argument_list|,
name|path
argument_list|,
literal|'/'
operator|+
name|moduleName
argument_list|)
argument_list|,
operator|new
name|HttpResponseMessage
argument_list|(
name|response
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NotFoundException
name|ex
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Resource "
operator|+
name|path
operator|+
literal|" not found by "
operator|+
name|moduleName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_NOT_FOUND
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|ex
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Permission denied to "
operator|+
name|path
operator|+
literal|" by "
operator|+
name|moduleName
operator|+
literal|" for "
operator|+
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_UNAUTHORIZED
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|BadRequestException
name|ex
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|info
argument_list|(
literal|"Bad request throw from module "
operator|+
name|moduleName
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_BAD_REQUEST
argument_list|,
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|ex
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|fatal
argument_list|(
literal|"Exception getting broker from pool for user "
operator|+
name|user
operator|.
name|getName
argument_list|()
argument_list|,
name|ex
argument_list|)
expr_stmt|;
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"Service is not available."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
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
catch|catch
parameter_list|(
name|IOException
name|ex
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|fatal
argument_list|(
literal|"I/O exception on request."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
try|try
block|{
name|response
operator|.
name|sendError
argument_list|(
name|HttpServletResponse
operator|.
name|SC_INTERNAL_SERVER_ERROR
argument_list|,
literal|"Service is not available."
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|finalEx
parameter_list|)
block|{
name|getLog
argument_list|()
operator|.
name|fatal
argument_list|(
literal|"Cannot return 500 on exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

