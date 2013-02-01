begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; er version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClientConfigImpl
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
name|security
operator|.
name|AuthenticationException
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
name|xacml
operator|.
name|AccessContext
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
name|SSLHelper
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
name|XMLDBException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
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
name|net
operator|.
name|URL
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

begin_comment
comment|/**  * The XMLDB driver class for eXist. This driver manages two different  * internal implementations. The first communicates with a remote  * database using the XMLRPC protocol. The second has direct access  * to an embedded database instance running in the same virtual machine.  * The driver chooses an implementation depending on the XML:DB URI passed  * to getCollection().  *   * When running in embedded mode, the driver can create a new database  * instance if none is available yet. It will do so if the property  * "create-database" is set to "true" or if there is a system property  * "exist.initdb" with value "true".  *   * You may optionally provide the location of an alternate configuration  * file through the "configuration" property. The driver is also able to  * address different database instances - which may have been installed at  * different places.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseImpl
implements|implements
name|Database
block|{
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
name|DatabaseImpl
operator|.
name|class
argument_list|)
decl_stmt|;
comment|//TODO : discuss about other possible values
specifier|protected
specifier|final
specifier|static
name|String
name|LOCAL_HOSTNAME
init|=
literal|""
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|UNKNOWN_CONNECTION
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|LOCAL_CONNECTION
init|=
literal|0
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|REMOTE_CONNECTION
init|=
literal|1
decl_stmt|;
comment|/** Default config filename to configure an Instance */
specifier|public
specifier|final
specifier|static
name|String
name|CONF_XML
init|=
literal|"conf.xml"
decl_stmt|;
specifier|protected
name|boolean
name|autoCreate
init|=
literal|false
decl_stmt|;
specifier|protected
name|String
name|configuration
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|currentInstanceName
init|=
literal|null
decl_stmt|;
specifier|private
name|HashMap
argument_list|<
name|String
argument_list|,
name|XmlRpcClient
argument_list|>
name|rpcClients
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|XmlRpcClient
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|ShutdownListener
name|shutdown
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|mode
init|=
name|UNKNOWN_CONNECTION
decl_stmt|;
specifier|private
name|Boolean
name|ssl_enable
init|=
literal|false
decl_stmt|;
specifier|private
name|Boolean
name|ssl_allow_self_signed
init|=
literal|true
decl_stmt|;
specifier|private
name|Boolean
name|ssl_verify_hostname
init|=
literal|false
decl_stmt|;
specifier|public
name|DatabaseImpl
parameter_list|()
block|{
name|String
name|initdb
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.initdb"
argument_list|)
decl_stmt|;
if|if
condition|(
name|initdb
operator|!=
literal|null
condition|)
name|autoCreate
operator|=
name|initdb
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|/**      *  In embedded mode: configure the database instance    *       *@exception  XMLDBException  Description of the Exception    */
specifier|private
name|void
name|configure
parameter_list|(
name|String
name|instanceName
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|// System.out.println("Configuring '" + instanceName + "' using " + Configuration.getPath(configuration, null));
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|configuration
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
name|instanceName
argument_list|,
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|shutdown
operator|!=
literal|null
condition|)
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|instanceName
argument_list|)
operator|.
name|registerShutdownListener
argument_list|(
name|shutdown
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
literal|"configuration error: "
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
name|currentInstanceName
operator|=
name|instanceName
expr_stmt|;
block|}
comment|/* @deprecated  Although part of the xmldb API, the design is somewhat inconsistent.    * @see org.xmldb.api.base.Database#acceptsURI(java.lang.String)    */
specifier|public
name|boolean
name|acceptsURI
parameter_list|(
name|String
name|uri
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XmldbURI
name|xmldbURI
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//Ugly workaround for non-URI compliant collection (resources ?) names (most likely IRIs)
name|String
name|newURIString
init|=
name|XmldbURI
operator|.
name|recoverPseudoURIs
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|//Remember that DatabaseManager (provided in xmldb.jar) trims the leading "xmldb:" !!!
comment|//... prepend it to have a real xmldb URI again...
name|xmldbURI
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
operator|+
name|newURIString
argument_list|)
expr_stmt|;
return|return
name|acceptsURI
argument_list|(
name|xmldbURI
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|//... even in the error message
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"xmldb URI is not well formed: "
operator|+
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
specifier|public
name|boolean
name|acceptsURI
parameter_list|(
name|XmldbURI
name|xmldbURI
parameter_list|)
throws|throws
name|XMLDBException
block|{
comment|//TODO : smarter processing (resources names, protocols, servers accessibility...) ? -pb
return|return
literal|true
return|;
block|}
comment|/* Returns a collection from the given "uri".      * @deprecated  Although part of the xmldb API, the design is somewhat inconsistent.      * @see org.exist.xmldb.DatabaseImpl#getCollection(org.exist.xmldb.XmldbURI, java.lang.String, java.lang.String)      * @see org.xmldb.api.base.Database#getCollection(java.lang.String, java.lang.String, java.lang.String)      */
annotation|@
name|Override
specifier|public
name|Collection
name|getCollection
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|XMLDBException
block|{
try|try
block|{
comment|//Ugly workaround for non-URI compliant collection names (most likely IRIs)
specifier|final
name|String
name|newURIString
init|=
name|XmldbURI
operator|.
name|recoverPseudoURIs
argument_list|(
name|uri
argument_list|)
decl_stmt|;
comment|//Remember that DatabaseManager (provided in xmldb.jar) trims the leading "xmldb:" !!!
comment|//... prepend it to have a real xmldb URI again...
specifier|final
name|XmldbURI
name|xmldbURI
init|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
operator|+
name|newURIString
argument_list|)
decl_stmt|;
return|return
name|getCollection
argument_list|(
name|xmldbURI
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|URISyntaxException
name|e
parameter_list|)
block|{
comment|//... even in the error message
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"xmldb URI is not well formed: "
operator|+
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
operator|+
name|uri
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|(
specifier|final
name|XmldbURI
name|xmldbURI
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|XmldbURI
operator|.
name|API_LOCAL
operator|.
name|equals
argument_list|(
name|xmldbURI
operator|.
name|getApiName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|getLocalCollection
argument_list|(
name|xmldbURI
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
if|else if
condition|(
name|XmldbURI
operator|.
name|API_XMLRPC
operator|.
name|equals
argument_list|(
name|xmldbURI
operator|.
name|getApiName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|getRemoteCollection
argument_list|(
name|xmldbURI
argument_list|,
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
else|else
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"Unknown or unparsable API for: "
operator|+
name|xmldbURI
argument_list|)
throw|;
block|}
block|}
comment|/**      * @param xmldbURI      * @param user      * @param password      * @return The collection      * @throws XMLDBException      */
specifier|private
name|Collection
name|getLocalCollection
parameter_list|(
specifier|final
name|XmldbURI
name|xmldbURI
parameter_list|,
specifier|final
name|String
name|user
parameter_list|,
specifier|final
name|String
name|password
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|mode
operator|=
name|LOCAL_CONNECTION
expr_stmt|;
comment|// use local database instance
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|xmldbURI
operator|.
name|getInstanceName
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|autoCreate
condition|)
block|{
name|configure
argument_list|(
name|xmldbURI
operator|.
name|getInstanceName
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|COLLECTION_CLOSED
argument_list|,
literal|"Local database server is not running"
argument_list|)
throw|;
block|}
block|}
name|BrokerPool
name|pool
decl_stmt|;
try|try
block|{
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|(
name|xmldbURI
operator|.
name|getInstanceName
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
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
literal|"Can not access to local database instance"
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|Subject
name|u
init|=
name|getUser
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|pool
argument_list|)
decl_stmt|;
try|try
block|{
return|return
operator|new
name|LocalCollection
argument_list|(
name|u
argument_list|,
name|pool
argument_list|,
name|xmldbURI
operator|.
name|toCollectionPathURI
argument_list|()
argument_list|,
name|AccessContext
operator|.
name|XMLDB
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
switch|switch
condition|(
name|e
operator|.
name|errorCode
condition|)
block|{
case|case
name|ErrorCodes
operator|.
name|NO_SUCH_RESOURCE
case|:
case|case
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
case|:
case|case
name|ErrorCodes
operator|.
name|INVALID_COLLECTION
case|:
case|case
name|ErrorCodes
operator|.
name|INVALID_RESOURCE
case|:
name|LOG
operator|.
name|info
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
default|default:
name|LOG
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
name|e
throw|;
block|}
block|}
block|}
comment|/**      * @param xmldbURI      * @param user      * @param password      * @return The collection      * @throws XMLDBException      */
specifier|private
name|Collection
name|getRemoteCollection
parameter_list|(
specifier|final
name|XmldbURI
name|xmldbURI
parameter_list|,
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|mode
operator|=
name|REMOTE_CONNECTION
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
comment|//TODO : read this from configuration
name|user
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
name|password
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|password
operator|=
literal|""
expr_stmt|;
block|}
try|try
block|{
specifier|final
name|String
name|protocol
init|=
name|ssl_enable
condition|?
literal|"https"
else|:
literal|"http"
decl_stmt|;
if|if
condition|(
name|ssl_enable
condition|)
block|{
name|SSLHelper
operator|.
name|initialize
argument_list|(
name|ssl_allow_self_signed
argument_list|,
name|ssl_verify_hostname
argument_list|)
expr_stmt|;
block|}
specifier|final
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
name|protocol
argument_list|,
name|xmldbURI
operator|.
name|getHost
argument_list|()
argument_list|,
name|xmldbURI
operator|.
name|getPort
argument_list|()
argument_list|,
name|xmldbURI
operator|.
name|getContext
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|XmlRpcClient
name|rpcClient
init|=
name|getRpcClient
argument_list|(
name|user
argument_list|,
name|password
argument_list|,
name|url
argument_list|)
decl_stmt|;
return|return
name|readCollection
argument_list|(
name|xmldbURI
operator|.
name|getRawCollectionPath
argument_list|()
argument_list|,
name|rpcClient
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|MalformedURLException
name|e
parameter_list|)
block|{
comment|//Should never happen
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
name|e
throw|;
comment|//return null;
block|}
block|}
specifier|public
specifier|static
name|Collection
name|readCollection
parameter_list|(
specifier|final
name|String
name|c
parameter_list|,
specifier|final
name|XmlRpcClient
name|rpcClient
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XmldbURI
name|path
decl_stmt|;
try|try
block|{
name|path
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
name|c
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
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_URI
argument_list|,
name|e
argument_list|)
throw|;
block|}
specifier|final
name|XmldbURI
index|[]
name|components
init|=
name|path
operator|.
name|getPathSegments
argument_list|()
decl_stmt|;
if|if
condition|(
name|components
operator|.
name|length
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Could not find collection: "
operator|+
name|path
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
name|XmldbURI
name|rootName
init|=
name|components
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|XmldbURI
operator|.
name|RELATIVE_ROOT_COLLECTION_URI
operator|.
name|equals
argument_list|(
name|rootName
argument_list|)
condition|)
block|{
name|rootName
operator|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
expr_stmt|;
block|}
name|Collection
name|current
init|=
name|RemoteCollection
operator|.
name|instance
argument_list|(
name|rpcClient
argument_list|,
name|rootName
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|components
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|current
operator|=
operator|(
operator|(
name|RemoteCollection
operator|)
name|current
operator|)
operator|.
name|getChildCollection
argument_list|(
name|components
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|current
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
name|NO_SUCH_COLLECTION
argument_list|,
literal|"Could not find collection: "
operator|+
name|c
argument_list|)
throw|;
block|}
block|}
return|return
name|current
return|;
block|}
comment|/**    * @param user    * @param pool    * @return the User object corresponding to the username in<code>user</code>    * @throws XMLDBException    */
specifier|private
name|Subject
name|getUser
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
name|user
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
name|password
operator|=
name|SecurityManager
operator|.
name|GUEST_USER
expr_stmt|;
block|}
name|SecurityManager
name|securityManager
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
try|try
block|{
return|return
name|securityManager
operator|.
name|authenticate
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
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
comment|/**    * RpcClients are cached by address+user. The password is transparently changed.    * @param user    * @param password    * @param url    * @throws XMLDBException    */
specifier|private
name|XmlRpcClient
name|getRpcClient
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|URL
name|url
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
name|key
init|=
name|user
operator|+
literal|"@"
operator|+
name|url
operator|.
name|toString
argument_list|()
decl_stmt|;
name|XmlRpcClient
name|client
init|=
name|rpcClients
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|XmlRpcClientConfigImpl
name|config
init|=
operator|new
name|XmlRpcClientConfigImpl
argument_list|()
decl_stmt|;
name|config
operator|.
name|setEnabledForExtensions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setServerURL
argument_list|(
name|url
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicUserName
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|==
literal|null
condition|)
block|{
name|client
operator|=
operator|new
name|XmlRpcClient
argument_list|()
expr_stmt|;
name|rpcClients
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|client
argument_list|)
expr_stmt|;
block|}
name|client
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
comment|/**    * Register a ShutdownListener for the current database instance. The ShutdownListener is called      * after the database has shut down. You have to register a listener before any calls to getCollection().    *     * @param listener    * @throws XMLDBException    */
specifier|public
name|void
name|setDatabaseShutdownListener
parameter_list|(
name|ShutdownListener
name|listener
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|shutdown
operator|=
name|listener
expr_stmt|;
block|}
specifier|public
name|String
name|getConformanceLevel
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//TODO : what is to be returned here ? -pb
return|return
literal|"0"
return|;
block|}
comment|//WARNING : returning such a default value is dangerous IMHO ? -pb
comment|/** @deprecated */
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|(
name|currentInstanceName
operator|!=
literal|null
operator|)
condition|?
name|currentInstanceName
else|:
literal|"exist"
return|;
block|}
comment|//WARNING : returning such *a* default value is dangerous IMHO ? -pb
specifier|public
name|String
index|[]
name|getNames
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
operator|new
name|String
index|[]
block|{
operator|(
name|currentInstanceName
operator|!=
literal|null
operator|)
condition|?
name|currentInstanceName
else|:
literal|"exist"
block|}
return|;
block|}
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"create-database"
argument_list|)
condition|)
block|{
comment|//TODO : rename ?
return|return
name|Boolean
operator|.
name|valueOf
argument_list|(
name|autoCreate
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"database-id"
argument_list|)
condition|)
block|{
comment|//TODO : consider multivalued property
return|return
name|currentInstanceName
return|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"configuration"
argument_list|)
condition|)
block|{
return|return
name|configuration
return|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-enable"
argument_list|)
condition|)
block|{
return|return
literal|""
operator|+
name|ssl_enable
return|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-allow-self-signed"
argument_list|)
condition|)
block|{
return|return
literal|""
operator|+
name|ssl_allow_self_signed
return|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-verify-hostname"
argument_list|)
condition|)
block|{
return|return
literal|""
operator|+
name|ssl_allow_self_signed
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"create-database"
argument_list|)
condition|)
block|{
name|autoCreate
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
comment|//TODO : rename ?
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"database-id"
argument_list|)
condition|)
block|{
comment|//TODO : consider multivalued property
name|currentInstanceName
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"configuration"
argument_list|)
condition|)
block|{
name|configuration
operator|=
name|value
expr_stmt|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-enable"
argument_list|)
condition|)
block|{
name|ssl_enable
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-allow-self-signed"
argument_list|)
condition|)
block|{
name|ssl_allow_self_signed
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"ssl-verify-hostname"
argument_list|)
condition|)
block|{
name|ssl_verify_hostname
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

