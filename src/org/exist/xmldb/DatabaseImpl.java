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
name|util
operator|.
name|StringTokenizer
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
specifier|protected
specifier|final
specifier|static
name|String
name|DEFAULT_HOST
init|=
literal|"localhost:8081"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|DEFAULT_NAME
init|=
literal|"exist"
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|LOCAL
init|=
literal|0
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|int
name|REMOTE
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
name|dbName
init|=
name|DEFAULT_NAME
decl_stmt|;
specifier|protected
name|String
name|selector
init|=
name|dbName
operator|+
literal|':'
decl_stmt|;
specifier|protected
name|XmlRpcClient
name|rpcClient
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
literal|0
decl_stmt|;
specifier|public
name|DatabaseImpl
parameter_list|()
block|{
try|try
block|{
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
parameter_list|,
name|String
name|address
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|StringTokenizer
name|tok
init|=
operator|new
name|StringTokenizer
argument_list|(
name|c
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
name|String
name|temp
init|=
name|tok
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
name|temp
operator|.
name|equals
argument_list|(
literal|"db"
argument_list|)
condition|)
name|temp
operator|=
literal|'/'
operator|+
name|temp
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
name|address
argument_list|,
name|temp
argument_list|)
decl_stmt|;
while|while
condition|(
name|current
operator|!=
literal|null
operator|&&
name|tok
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|temp
operator|=
name|tok
operator|.
name|nextToken
argument_list|()
expr_stmt|;
name|current
operator|=
name|current
operator|.
name|getChildCollection
argument_list|(
operator|(
operator|(
name|RemoteCollection
operator|)
name|current
operator|)
operator|.
name|getPath
argument_list|()
operator|+
literal|'/'
operator|+
name|temp
argument_list|)
expr_stmt|;
block|}
return|return
name|current
return|;
block|}
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
return|return
name|uri
operator|.
name|startsWith
argument_list|(
name|selector
argument_list|)
return|;
block|}
comment|/**      *  In embedded mode: configure the database instance      *      *@exception  XMLDBException  Description of the Exception      */
specifier|private
name|void
name|configure
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|home
decl_stmt|,
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
name|home
operator|=
name|f
operator|.
name|getParentFile
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|file
operator|=
name|f
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"configuring "
operator|+
name|dbName
operator|+
literal|" using "
operator|+
name|home
operator|+
literal|'/'
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
name|dbName
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
name|dbName
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
block|}
specifier|public
name|Collection
name|getCollection
parameter_list|(
name|String
name|collection
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
operator|!
name|collection
operator|.
name|startsWith
argument_list|(
name|selector
argument_list|)
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"collection "
operator|+
name|collection
operator|+
literal|" does not start with '"
operator|+
name|selector
operator|+
literal|"'"
argument_list|)
throw|;
name|String
name|address
init|=
name|DEFAULT_HOST
decl_stmt|;
name|String
name|c
init|=
name|collection
operator|.
name|substring
argument_list|(
name|selector
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
name|Collection
name|current
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|c
operator|.
name|startsWith
argument_list|(
literal|"///"
argument_list|)
condition|)
block|{
name|mode
operator|=
name|LOCAL
expr_stmt|;
comment|// use local database instance
if|if
condition|(
operator|!
name|BrokerPool
operator|.
name|isConfigured
argument_list|(
name|dbName
argument_list|)
condition|)
block|{
if|if
condition|(
name|autoCreate
condition|)
name|configure
argument_list|()
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
literal|"local database server not running"
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
name|dbName
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
literal|"db not correctly initialized"
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|User
name|u
init|=
literal|null
decl_stmt|;
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
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|u
operator|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getUser
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
name|u
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"user "
operator|+
name|user
operator|+
literal|" does not exist"
argument_list|)
throw|;
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
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|PERMISSION_DENIED
argument_list|,
literal|"invalid password"
argument_list|)
throw|;
block|}
try|try
block|{
name|current
operator|=
operator|new
name|LocalCollection
argument_list|(
name|u
argument_list|,
name|pool
argument_list|,
name|c
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
return|return
operator|(
name|current
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|LocalCollection
operator|)
name|current
operator|)
operator|.
name|isValid
argument_list|()
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
if|else if
condition|(
name|c
operator|.
name|startsWith
argument_list|(
literal|"//"
argument_list|)
condition|)
block|{
comment|// use remote database via XML-RPC
name|mode
operator|=
name|REMOTE
expr_stmt|;
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
if|else if
condition|(
name|password
operator|==
literal|null
condition|)
name|password
operator|=
literal|""
expr_stmt|;
comment|// try to figure out server address
name|int
name|p
init|=
literal|0
decl_stmt|;
if|if
condition|(
operator|(
name|p
operator|=
name|c
operator|.
name|indexOf
argument_list|(
literal|"/db"
argument_list|,
literal|2
argument_list|)
operator|)
operator|>
operator|-
literal|1
condition|)
block|{
name|address
operator|=
literal|"http://"
operator|+
name|c
operator|.
name|substring
argument_list|(
literal|2
argument_list|,
name|p
argument_list|)
expr_stmt|;
name|c
operator|=
name|c
operator|.
name|substring
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"malformed url: "
operator|+
name|address
argument_list|)
throw|;
if|if
condition|(
name|rpcClient
operator|==
literal|null
condition|)
try|try
block|{
name|rpcClient
operator|=
operator|new
name|XmlRpcClient
argument_list|(
name|address
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"malformed url: "
operator|+
name|address
argument_list|,
name|e
argument_list|)
throw|;
block|}
name|rpcClient
operator|.
name|setBasicAuthentication
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
return|return
name|readCollection
argument_list|(
name|c
argument_list|,
name|rpcClient
argument_list|,
name|address
argument_list|)
return|;
block|}
else|else
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|INVALID_DATABASE
argument_list|,
literal|"malformed url: "
operator|+
name|address
argument_list|)
throw|;
block|}
specifier|public
name|String
name|getConformanceLevel
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
literal|"0"
return|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|dbName
return|;
block|}
comment|/** 	 * Register a ShutdownListener for the current database instance. The ShutdownListener is called 	 * after the database has shut down. You have to register a listener before any calls to getCollection(). 	 *  	 * @param listener 	 * @throws XMLDBException 	 */
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
if|if
condition|(
name|property
operator|.
name|equals
argument_list|(
literal|"database-id"
argument_list|)
condition|)
return|return
name|dbName
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
name|dbName
operator|=
name|value
expr_stmt|;
name|selector
operator|=
name|dbName
operator|+
literal|':'
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
name|configuration
operator|=
name|value
expr_stmt|;
block|}
block|}
end_class

end_unit

