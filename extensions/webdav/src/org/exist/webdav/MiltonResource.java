begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webdav
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
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Auth
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|LockInfo
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|LockTimeout
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|LockToken
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Request
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|com
operator|.
name|bradmcevoy
operator|.
name|http
operator|.
name|Resource
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
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|GregorianCalendar
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|DatatypeFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|DatatypeConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|datatype
operator|.
name|XMLGregorianCalendar
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
name|BrokerPool
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

begin_comment
comment|/**  * Generic class representing a Milton Resource.  *   * @author Dannes Wessels<dannes@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|MiltonResource
implements|implements
name|Resource
block|{
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
name|MiltonResource
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|XmldbURI
name|resourceXmldbUri
decl_stmt|;
specifier|protected
name|BrokerPool
name|brokerPool
decl_stmt|;
specifier|protected
name|String
name|host
decl_stmt|;
specifier|protected
name|Subject
name|subject
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|AUTHENTICATED
init|=
literal|"AUTHENTICATED"
decl_stmt|;
specifier|protected
name|String
name|REALM
init|=
literal|"exist"
decl_stmt|;
specifier|protected
name|ExistResource
name|existResource
decl_stmt|;
comment|// Used for Long to DateTime conversion
specifier|private
name|DatatypeFactory
name|datatypeFactory
decl_stmt|;
specifier|public
name|MiltonResource
parameter_list|()
block|{
if|if
condition|(
name|datatypeFactory
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|datatypeFactory
operator|=
name|DatatypeFactory
operator|.
name|newInstance
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DatatypeConfigurationException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
name|XmldbURI
name|getXmldbUri
parameter_list|()
block|{
return|return
name|resourceXmldbUri
return|;
block|}
specifier|protected
name|String
name|getHost
parameter_list|()
block|{
return|return
name|host
return|;
block|}
specifier|private
name|Subject
name|getUserAsSubject
parameter_list|()
block|{
return|return
name|subject
return|;
block|}
comment|/**      * Convert date to dateTime XML format.      * s      * @param date Representation of data      * @return ISO8601 like formatted representation of date.s      */
specifier|protected
name|String
name|getXmlDateTime
parameter_list|(
name|Long
name|date
parameter_list|)
block|{
comment|// Convert to Calendar
name|GregorianCalendar
name|gc
init|=
operator|new
name|GregorianCalendar
argument_list|()
decl_stmt|;
name|gc
operator|.
name|setTime
argument_list|(
operator|new
name|Date
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
comment|// COnvert to XML dateTimes
name|XMLGregorianCalendar
name|xgc
init|=
name|datatypeFactory
operator|.
name|newXMLGregorianCalendar
argument_list|(
name|gc
argument_list|)
decl_stmt|;
return|return
name|xgc
operator|.
name|toXMLFormat
argument_list|()
return|;
block|}
comment|/**      *  Converts an org.exist.dom.LockToken into com.bradmcevoy.http.LockToken.      *      * @param existLT Exist-db representation of a webdav token.      * @return Milton representation of a webdav token.      */
specifier|protected
name|LockToken
name|convertToken
parameter_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|existLT
parameter_list|)
block|{
comment|// LockInfo : construct scope
name|LockInfo
operator|.
name|LockScope
name|scope
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|existLT
operator|.
name|getScope
argument_list|()
condition|)
block|{
case|case
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_SCOPE_SHARED
case|:
name|scope
operator|=
name|LockInfo
operator|.
name|LockScope
operator|.
name|SHARED
expr_stmt|;
break|break;
case|case
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_SCOPE_EXCLUSIVE
case|:
name|scope
operator|=
name|LockInfo
operator|.
name|LockScope
operator|.
name|EXCLUSIVE
expr_stmt|;
break|break;
default|default:
name|scope
operator|=
name|LockInfo
operator|.
name|LockScope
operator|.
name|NONE
expr_stmt|;
break|break;
block|}
comment|// LockInfo : construct type
name|LockInfo
operator|.
name|LockType
name|type
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|existLT
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_TYPE_WRITE
case|:
name|type
operator|=
name|LockInfo
operator|.
name|LockType
operator|.
name|WRITE
expr_stmt|;
break|break;
default|default:
comment|// DWES: if not WRITE then READ. typical :-)
name|type
operator|=
name|LockInfo
operator|.
name|LockType
operator|.
name|READ
expr_stmt|;
break|break;
block|}
comment|// LockInfo : get owner
name|String
name|owner
init|=
name|existLT
operator|.
name|getOwner
argument_list|()
decl_stmt|;
comment|// LockInfo : construct depth
name|LockInfo
operator|.
name|LockDepth
name|depth
init|=
literal|null
decl_stmt|;
switch|switch
condition|(
name|existLT
operator|.
name|getDepth
argument_list|()
condition|)
block|{
case|case
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_DEPTH_INFINIY
case|:
name|depth
operator|=
name|LockInfo
operator|.
name|LockDepth
operator|.
name|INFINITY
expr_stmt|;
break|break;
default|default:
comment|// TODO either zero or infinity?
name|depth
operator|=
name|LockInfo
operator|.
name|LockDepth
operator|.
name|ZERO
expr_stmt|;
break|break;
block|}
comment|// LockInfo
name|LockInfo
name|li
init|=
operator|new
name|LockInfo
argument_list|(
name|scope
argument_list|,
name|type
argument_list|,
name|owner
argument_list|,
name|depth
argument_list|)
decl_stmt|;
comment|// Lock Timeout
name|Long
name|timeout
init|=
name|existLT
operator|.
name|getTimeOut
argument_list|()
decl_stmt|;
comment|// Special treatment when no LOCK was present
if|if
condition|(
name|timeout
operator|==
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|NO_LOCK_TIMEOUT
condition|)
block|{
name|timeout
operator|=
literal|null
expr_stmt|;
comment|// Special treatment infinite lock
block|}
if|else if
condition|(
name|timeout
operator|==
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_TIMEOUT_INFINITE
condition|)
block|{
name|timeout
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
block|}
name|LockTimeout
name|lt
init|=
operator|new
name|LockTimeout
argument_list|(
name|timeout
argument_list|)
decl_stmt|;
comment|// Token Id
name|String
name|id
init|=
name|existLT
operator|.
name|getOpaqueLockToken
argument_list|()
decl_stmt|;
comment|// Return values in Milton object
return|return
operator|new
name|LockToken
argument_list|(
name|id
argument_list|,
name|li
argument_list|,
name|lt
argument_list|)
return|;
block|}
comment|/**      *  Converts an org.exist.dom.LockToken into com.bradmcevoy.http.LockToken.      */
specifier|protected
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|convertToken
parameter_list|(
name|LockTimeout
name|timeout
parameter_list|,
name|LockInfo
name|lockInfo
parameter_list|)
block|{
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
name|existToken
init|=
operator|new
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
argument_list|()
decl_stmt|;
comment|// Set lock depth
switch|switch
condition|(
name|lockInfo
operator|.
name|depth
condition|)
block|{
case|case
name|ZERO
case|:
name|existToken
operator|.
name|setDepth
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_DEPTH_0
argument_list|)
expr_stmt|;
break|break;
case|case
name|INFINITY
case|:
name|existToken
operator|.
name|setDepth
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_DEPTH_INFINIY
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Set lock scope
switch|switch
condition|(
name|lockInfo
operator|.
name|scope
condition|)
block|{
case|case
name|EXCLUSIVE
case|:
name|existToken
operator|.
name|setScope
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_SCOPE_EXCLUSIVE
argument_list|)
expr_stmt|;
break|break;
case|case
name|SHARED
case|:
name|existToken
operator|.
name|setScope
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_SCOPE_SHARED
argument_list|)
expr_stmt|;
break|break;
case|case
name|NONE
case|:
name|existToken
operator|.
name|setScope
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_SCOPE_NONE
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Set lock type (read,write)
switch|switch
condition|(
name|lockInfo
operator|.
name|type
condition|)
block|{
case|case
name|READ
case|:
name|existToken
operator|.
name|setScope
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_TYPE_NONE
argument_list|)
expr_stmt|;
break|break;
case|case
name|WRITE
case|:
name|existToken
operator|.
name|setScope
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_TYPE_WRITE
argument_list|)
expr_stmt|;
break|break;
block|}
comment|// Set timeouts
if|if
condition|(
name|timeout
operator|==
literal|null
operator|||
name|timeout
operator|.
name|getSeconds
argument_list|()
operator|==
literal|null
condition|)
block|{
name|existToken
operator|.
name|setTimeOut
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|NO_LOCK_TIMEOUT
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|timeout
operator|.
name|getSeconds
argument_list|()
operator|==
name|Long
operator|.
name|MAX_VALUE
condition|)
block|{
name|existToken
operator|.
name|setTimeOut
argument_list|(
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|LockToken
operator|.
name|LOCK_TIMEOUT_INFINITE
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Long
name|futureDate
init|=
operator|(
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
operator|)
operator|/
literal|1000
operator|+
name|timeout
operator|.
name|getSeconds
argument_list|()
decl_stmt|;
name|existToken
operator|.
name|setTimeOut
argument_list|(
name|futureDate
argument_list|)
expr_stmt|;
block|}
comment|// Copy username if existent
name|String
name|user
init|=
name|lockInfo
operator|.
name|lockedByUser
decl_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|existToken
operator|.
name|setOwner
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
return|return
name|existToken
return|;
block|}
comment|/**      *  Convert % encoded string back to text      */
specifier|protected
name|XmldbURI
name|decodePath
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
block|{
name|XmldbURI
name|retval
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|path
init|=
operator|new
name|URI
argument_list|(
name|uri
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|retval
operator|=
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
literal|""
operator|+
name|path
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
comment|// oops
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|retval
return|;
block|}
comment|/**      *  Convert % encoded string back to text      */
specifier|protected
name|String
name|decodePath
parameter_list|(
name|String
name|uri
parameter_list|)
block|{
name|String
name|path
init|=
literal|null
decl_stmt|;
try|try
block|{
name|path
operator|=
operator|new
name|URI
argument_list|(
name|uri
argument_list|)
operator|.
name|getPath
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|URISyntaxException
name|ex
parameter_list|)
block|{
comment|// oops
name|LOG
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
comment|/* ========      * Resource      * ======== */
annotation|@
name|Override
specifier|public
name|String
name|getUniqueId
parameter_list|()
block|{
return|return
literal|null
return|;
comment|// disables the ETag field
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|decodePath
argument_list|(
literal|""
operator|+
name|resourceXmldbUri
operator|.
name|lastSegment
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Object
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|String
name|password
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authenticating user "
operator|+
name|username
operator|+
literal|" for "
operator|+
name|resourceXmldbUri
argument_list|)
expr_stmt|;
comment|// Check if username is provided.
if|if
condition|(
name|username
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// Check is subject was already authenticated.
if|if
condition|(
name|subject
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User was already authenticated."
argument_list|)
expr_stmt|;
return|return
name|AUTHENTICATED
return|;
block|}
comment|// Authenticate subject with password
name|subject
operator|=
name|existResource
operator|.
name|authenticate
argument_list|(
name|username
argument_list|,
name|password
argument_list|)
expr_stmt|;
comment|// Quick return if no subject object was returned
if|if
condition|(
name|subject
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User could not be authenticated."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Guest is not allowed to access.
name|Subject
name|guest
init|=
name|brokerPool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGuestSubject
argument_list|()
decl_stmt|;
if|if
condition|(
name|guest
operator|.
name|equals
argument_list|(
name|subject
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"The user "
operator|+
name|guest
operator|.
name|getName
argument_list|()
operator|+
literal|" is prohibited from logging in through WebDAV."
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|// Note: If User object is returned, authentication was OK
comment|// Collect data for this resource
name|existResource
operator|.
name|initMetadata
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User '"
operator|+
name|subject
operator|.
name|getName
argument_list|()
operator|+
literal|"' has been authenticated."
argument_list|)
expr_stmt|;
return|return
name|AUTHENTICATED
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|authorise
parameter_list|(
name|Request
name|request
parameter_list|,
name|Method
name|method
parameter_list|,
name|Auth
name|auth
parameter_list|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|method
operator|.
name|toString
argument_list|()
operator|+
literal|" "
operator|+
name|resourceXmldbUri
operator|+
literal|" (write="
operator|+
name|method
operator|.
name|isWrite
operator|+
literal|")"
argument_list|)
expr_stmt|;
comment|/*          * First perform checks on Milton authentication          */
if|if
condition|(
name|auth
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User hasn't been authenticated."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
comment|// Get effective username
name|String
name|userName
init|=
name|auth
operator|.
name|getUser
argument_list|()
decl_stmt|;
comment|// Get authentication object
name|Object
name|tag
init|=
name|auth
operator|.
name|getTag
argument_list|()
decl_stmt|;
comment|// Get URI. no idea why value is null.
name|String
name|authURI
init|=
name|auth
operator|.
name|getUri
argument_list|()
decl_stmt|;
comment|// If object does not exist, there was no successfull authentication
if|if
condition|(
name|tag
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"No tag, user "
operator|+
name|userName
operator|+
literal|" not authenticated"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|tag
operator|instanceof
name|String
condition|)
block|{
name|String
name|value
init|=
operator|(
name|String
operator|)
name|tag
decl_stmt|;
if|if
condition|(
name|AUTHENTICATED
operator|.
name|equals
argument_list|(
name|value
argument_list|)
condition|)
block|{
comment|// The correct TAG is returned!
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"Authentication tag contains wrong value, user "
operator|+
name|userName
operator|+
literal|" is not authenticated"
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
comment|/*          * Second perform checks on actual exist-db permissions          */
if|if
condition|(
name|method
operator|.
name|isWrite
condition|)
block|{
if|if
condition|(
operator|!
name|existResource
operator|.
name|writeAllowed
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|userName
operator|+
literal|" is NOT authorized to write resource, abort."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
operator|!
name|existResource
operator|.
name|readAllowed
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|userName
operator|+
literal|" is NOT authorized to read resource, abort."
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|auth
operator|.
name|getUri
argument_list|()
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"URI is null"
argument_list|)
expr_stmt|;
comment|// not sure why the null value can be there
block|}
name|String
name|action
init|=
name|method
operator|.
name|isWrite
condition|?
literal|"write"
else|:
literal|"read"
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"User "
operator|+
name|userName
operator|+
literal|" is authorized to "
operator|+
name|action
operator|+
literal|" resource "
operator|+
name|resourceXmldbUri
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRealm
parameter_list|()
block|{
return|return
name|REALM
return|;
block|}
annotation|@
name|Override
specifier|public
name|Date
name|getModifiedDate
parameter_list|()
block|{
name|Date
name|modifiedDate
init|=
literal|null
decl_stmt|;
name|Long
name|time
init|=
name|existResource
operator|.
name|getLastModified
argument_list|()
decl_stmt|;
if|if
condition|(
name|time
operator|!=
literal|null
condition|)
block|{
name|modifiedDate
operator|=
operator|new
name|Date
argument_list|(
name|time
argument_list|)
expr_stmt|;
block|}
comment|//        if(LOG.isDebugEnabled())
comment|//            LOG.debug("Modified date=" + modifiedDate);
return|return
name|modifiedDate
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|checkRedirect
parameter_list|(
name|Request
name|request
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

