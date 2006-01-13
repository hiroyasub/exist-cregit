begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; er version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:  */
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

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpc
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
name|XmlRpcClient
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

begin_comment
comment|/**  * The XMLDB driver class for eXist. This driver manages two different  * internal implementations. The first communicates with a remote   * database using the XMLRPC protocol. The second has direct access  * to an embedded database instance running in the same virtual machine.  * The driver chooses an implementation depending on the XML:DB URI passed  * to getCollection().  *   * When running in embedded mode, the driver can create a new database  * instance if none is available yet. It will do so if the property  * "create-database" is set to "true" or if there is a system property  * "exist.initdb" with value "true".  *   * You may optionally provide the location of an alternate configuration  * file through the "configuration" property. The driver is also able to  * address different database instances - which may have been installed at  * different places.  *   * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseImpl
implements|implements
name|Database
block|{
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
name|rpcClients
init|=
operator|new
name|HashMap
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
specifier|public
name|DatabaseImpl
parameter_list|()
block|{
try|try
block|{
comment|//TODO : make this configurable
name|XmlRpc
operator|.
name|setEncoding
argument_list|(
literal|"UTF-8"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
block|}
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
comment|/**      *  In embedded mode: configure the database instance      *      *@exception  XMLDBException  Description of the Exception      */
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
name|String
name|home
decl_stmt|;
name|String
name|file
init|=
literal|"conf.xml"
decl_stmt|;
if|if
condition|(
name|configuration
operator|==
literal|null
condition|)
block|{
name|home
operator|=
name|findExistHomeFromProperties
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|f
operator|.
name|isAbsolute
argument_list|()
condition|)
name|f
operator|=
operator|new
name|File
argument_list|(
operator|new
name|File
argument_list|(
name|findExistHomeFromProperties
argument_list|()
argument_list|)
argument_list|,
name|configuration
argument_list|)
operator|.
name|getAbsoluteFile
argument_list|()
expr_stmt|;
name|file
operator|=
name|f
operator|.
name|getName
argument_list|()
expr_stmt|;
name|home
operator|=
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Configuring '"
operator|+
name|instanceName
operator|+
literal|"' using "
operator|+
name|home
operator|+
name|File
operator|.
name|separatorChar
operator|+
name|file
argument_list|)
expr_stmt|;
try|try
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|file
argument_list|,
name|home
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
literal|"configuration error"
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
comment|/**      * @return Exist Home dir. From system Properties      */
specifier|private
name|String
name|findExistHomeFromProperties
parameter_list|()
block|{
name|String
name|home
decl_stmt|;
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
return|return
name|home
return|;
block|}
comment|/* @deprecated  Although part of the xmldb API, the design is somewhat inconsistent.         * @see org.xmldb.api.base.Database#acceptsURI(java.lang.String)      */
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
operator|new
name|XmldbURI
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
comment|/* Returns a collection from the given "uri".      * @deprecated  Although part of the xmldb API, the design is somewhat inconsistent.           * @see org.exist.xmldb.DatabaseImpl#getCollection(org.exist.xmldb.XmldbURI, java.lang.String, java.lang.String)      * @see org.xmldb.api.base.Database#getCollection(java.lang.String, java.lang.String, java.lang.String)      */
specifier|public
name|Collection
name|getCollection
parameter_list|(
name|String
name|uri
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
name|XmldbURI
name|xmldbURI
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|//Ugly workaround for non-URI compliant collection names (most likely IRIs)
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
operator|new
name|XmldbURI
argument_list|(
name|XmldbURI
operator|.
name|XMLDB_URI_PREFIX
operator|+
name|newURIString
argument_list|)
expr_stmt|;
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
specifier|public
name|Collection
name|getCollection
parameter_list|(
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
else|else
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
comment|/**      * @param xmldbURI      * @param user      * @param password      * @return      * @throws XMLDBException      */
specifier|private
name|Collection
name|getLocalCollection
parameter_list|(
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
name|configure
argument_list|(
name|xmldbURI
operator|.
name|getInstanceName
argument_list|()
argument_list|)
expr_stmt|;
else|else
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
name|User
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
name|Collection
name|current
init|=
operator|new
name|LocalCollection
argument_list|(
name|u
argument_list|,
name|pool
argument_list|,
name|xmldbURI
operator|.
name|getCollectionPath
argument_list|()
argument_list|)
decl_stmt|;
return|return
operator|(
name|current
operator|!=
literal|null
operator|)
condition|?
name|current
else|:
literal|null
return|;
block|}
catch|catch
parameter_list|(
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
return|return
literal|null
return|;
default|default:
throw|throw
name|e
throw|;
block|}
block|}
block|}
comment|/**      * @param xmldbURI      * @param user      * @param password      * @return      * @throws XMLDBException      */
specifier|private
name|Collection
name|getRemoteCollection
parameter_list|(
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
literal|"guest"
expr_stmt|;
name|password
operator|=
literal|"guest"
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|==
literal|null
condition|)
name|password
operator|=
literal|""
expr_stmt|;
try|try
block|{
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"http"
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
name|getCollectionPath
argument_list|()
argument_list|,
name|rpcClient
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
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
block|}
specifier|public
specifier|static
name|Collection
name|readCollection
parameter_list|(
name|String
name|c
parameter_list|,
name|XmlRpcClient
name|rpcClient
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|String
index|[]
name|components
init|=
name|XmldbURI
operator|.
name|getPathComponents
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|String
name|rootName
init|=
name|components
index|[
literal|0
index|]
decl_stmt|;
if|if
condition|(
name|DBBroker
operator|.
name|ROOT_COLLECTION_NAME
operator|.
name|equals
argument_list|(
name|rootName
argument_list|)
condition|)
name|rootName
operator|=
name|DBBroker
operator|.
name|ROOT_COLLECTION
expr_stmt|;
name|Collection
name|current
init|=
operator|new
name|RemoteCollection
argument_list|(
name|rpcClient
argument_list|,
literal|null
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
block|}
return|return
name|current
return|;
block|}
comment|/**      * @param user      * @param pool      * @return the User object corresponding to the username in<code>user</code>      * @throws XMLDBException      */
specifier|private
name|User
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
literal|"guest"
expr_stmt|;
name|password
operator|=
literal|"guest"
expr_stmt|;
block|}
name|User
name|u
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
decl_stmt|;
if|if
condition|(
name|u
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
name|PERMISSION_DENIED
argument_list|,
literal|"User '"
operator|+
name|user
operator|+
literal|"' does not exist"
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|u
operator|.
name|validate
argument_list|(
name|password
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"Invalid password for user '"
operator|+
name|user
operator|+
literal|"'"
argument_list|)
throw|;
block|}
return|return
name|u
return|;
block|}
comment|/**      * RpcClients are cached by address+user. The password is transparently changed.      * @param user      * @param password      * @param address      * @throws XMLDBException      */
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
operator|(
name|XmlRpcClient
operator|)
name|rpcClients
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
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
argument_list|(
name|url
argument_list|)
expr_stmt|;
if|if
condition|(
name|client
operator|!=
literal|null
condition|)
block|{
name|client
operator|.
name|setBasicAuthentication
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
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
block|}
return|return
name|client
return|;
block|}
comment|/**      * Register a ShutdownListener for the current database instance. The ShutdownListener is called      * after the database has shut down. You have to register a listener before any calls to getCollection().      *       * @param listener      * @throws XMLDBException      */
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
comment|//TODO : consider multivalued property
return|return
name|currentInstanceName
return|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"configuration"
argument_list|)
condition|)
return|return
name|configuration
return|;
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
name|autoCreate
operator|=
name|value
operator|.
name|equals
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
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
comment|//TODO : consider multivalued property
name|currentInstanceName
operator|=
name|value
expr_stmt|;
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"configuration"
argument_list|)
condition|)
name|configuration
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class

end_unit

