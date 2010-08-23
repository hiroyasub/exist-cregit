begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
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
name|config
operator|.
name|ConfigurationException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsElement
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
name|MessageDigester
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
name|internal
operator|.
name|aider
operator|.
name|UserAider
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
name|ldap
operator|.
name|LDAPbindSecurityManager
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
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
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
name|Properties
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

begin_comment
comment|/**  * Represents a user within the database.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  * @author {Marco.Tampucci, Massimo.Martinelli} @isti.cnr.it  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"account"
argument_list|)
specifier|public
class|class
name|AccountImpl
extends|extends
name|AbstractAccount
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
name|AccountImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|int
name|PASSWORD_ENCODING
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|CHECK_PASSWORDS
init|=
literal|true
decl_stmt|;
static|static
block|{
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
try|try
block|{
name|props
operator|.
name|load
argument_list|(
name|AccountImpl
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
operator|.
name|getResourceAsStream
argument_list|(
literal|"org/exist/security/security.properties"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
block|}
name|String
name|option
init|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"passwords.encoding"
argument_list|,
literal|"md5"
argument_list|)
decl_stmt|;
name|setPasswordEncoding
argument_list|(
name|option
argument_list|)
expr_stmt|;
name|option
operator|=
name|props
operator|.
name|getProperty
argument_list|(
literal|"passwords.check"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|CHECK_PASSWORDS
operator|=
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
operator|||
name|option
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
specifier|static
specifier|public
name|void
name|enablePasswordChecks
parameter_list|(
name|boolean
name|check
parameter_list|)
block|{
name|CHECK_PASSWORDS
operator|=
name|check
expr_stmt|;
block|}
specifier|static
specifier|public
name|void
name|setPasswordEncoding
parameter_list|(
name|String
name|encoding
parameter_list|)
block|{
if|if
condition|(
name|encoding
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|equals
argument_list|(
literal|"Setting password encoding to "
operator|+
name|encoding
argument_list|)
expr_stmt|;
if|if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"plain"
argument_list|)
condition|)
block|{
name|PASSWORD_ENCODING
operator|=
name|PLAIN_ENCODING
expr_stmt|;
block|}
if|else if
condition|(
name|encoding
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"md5"
argument_list|)
condition|)
block|{
name|PASSWORD_ENCODING
operator|=
name|MD5_ENCODING
expr_stmt|;
block|}
else|else
block|{
name|PASSWORD_ENCODING
operator|=
name|SIMPLE_MD5_ENCODING
expr_stmt|;
block|}
block|}
block|}
specifier|static
specifier|public
name|Subject
name|getUserFromServletRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
name|Principal
name|principal
init|=
name|request
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|Subject
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|principal
return|;
comment|//workaroud strange jetty authentication method, why encapsulate user object??? -shabanovd
block|}
if|else if
condition|(
name|principal
operator|!=
literal|null
operator|&&
literal|"org.eclipse.jetty.plus.jaas.JAASUserPrincipal"
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
name|Method
name|method
init|=
name|principal
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getSubject"
argument_list|)
decl_stmt|;
name|Object
name|obj
init|=
name|method
operator|.
name|invoke
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
condition|)
block|{
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
name|subject
init|=
operator|(
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
operator|)
name|obj
decl_stmt|;
for|for
control|(
name|Principal
name|_principal_
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
if|if
condition|(
name|_principal_
operator|instanceof
name|Subject
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|_principal_
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
name|SecurityException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|InvocationTargetException
name|e
parameter_list|)
block|{
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"password"
argument_list|)
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"digestPassword"
argument_list|)
specifier|private
name|String
name|digestPassword
init|=
literal|null
decl_stmt|;
comment|/** 	 * Create a new user with name and password 	 *  	 * @param user 	 *            Description of the Parameter 	 * @param password 	 *            Description of the Parameter 	 * @throws ConfigurationException  	 */
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|password
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Create a new user with name 	 *  	 * @param name 	 *            The account name 	 * @throws ConfigurationException  	 */
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
operator|-
literal|1
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Create a new user with name, password and primary group 	 *  	 *@param user 	 *            Description of the Parameter 	 *@param password 	 *            Description of the Parameter 	 *@param primaryGroup 	 *            Description of the Parameter 	 * @throws ConfigurationException  	 */
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|primaryGroup
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
argument_list|(
name|realm
argument_list|,
name|id
argument_list|,
name|name
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|defaultRole
operator|=
name|addGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
parameter_list|,
name|Account
name|from_user
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|id
argument_list|,
name|from_user
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|home
operator|=
name|from_user
operator|.
name|getHome
argument_list|()
expr_stmt|;
name|defaultRole
operator|=
name|from_user
operator|.
name|getDefaultGroup
argument_list|()
expr_stmt|;
if|if
condition|(
name|from_user
operator|instanceof
name|AccountImpl
condition|)
block|{
name|AccountImpl
name|user
init|=
operator|(
name|AccountImpl
operator|)
name|from_user
decl_stmt|;
name|defaultRole
operator|=
name|user
operator|.
name|defaultRole
expr_stmt|;
name|roles
operator|=
name|user
operator|.
name|roles
expr_stmt|;
name|password
operator|=
name|user
operator|.
name|password
expr_stmt|;
name|digestPassword
operator|=
name|user
operator|.
name|digestPassword
expr_stmt|;
name|hasDbaRole
operator|=
name|user
operator|.
name|hasDbaRole
expr_stmt|;
name|_cred
operator|=
name|user
operator|.
name|_cred
expr_stmt|;
block|}
if|else if
condition|(
name|from_user
operator|instanceof
name|UserAider
condition|)
block|{
name|UserAider
name|user
init|=
operator|(
name|UserAider
operator|)
name|from_user
decl_stmt|;
name|String
index|[]
name|gl
init|=
name|user
operator|.
name|getGroups
argument_list|()
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
name|gl
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addGroup
argument_list|(
name|gl
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|setPassword
argument_list|(
name|user
operator|.
name|getPassword
argument_list|()
argument_list|)
expr_stmt|;
name|digestPassword
operator|=
name|user
operator|.
name|getDigestPassword
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|AccountImpl
name|from_user
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|from_user
operator|.
name|id
argument_list|,
name|from_user
operator|.
name|name
argument_list|)
expr_stmt|;
name|home
operator|=
name|from_user
operator|.
name|home
expr_stmt|;
name|defaultRole
operator|=
name|from_user
operator|.
name|defaultRole
expr_stmt|;
name|roles
operator|=
name|from_user
operator|.
name|roles
expr_stmt|;
name|password
operator|=
name|from_user
operator|.
name|password
expr_stmt|;
name|digestPassword
operator|=
name|from_user
operator|.
name|digestPassword
expr_stmt|;
name|hasDbaRole
operator|=
name|from_user
operator|.
name|hasDbaRole
expr_stmt|;
name|_cred
operator|=
name|from_user
operator|.
name|_cred
expr_stmt|;
name|this
operator|.
name|realm
operator|=
name|realm
expr_stmt|;
block|}
comment|/** 	 * Get the user's password 	 *  	 * @return Description of the Return Value 	 * @deprecated 	 */
specifier|public
specifier|final
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
annotation|@
name|Deprecated
specifier|public
specifier|final
name|String
name|getDigestPassword
parameter_list|()
block|{
return|return
name|digestPassword
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.security.User#setPassword(java.lang.String) 	 */
specifier|public
specifier|final
name|void
name|setPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
name|_cred
operator|=
operator|new
name|Password
argument_list|(
name|passwd
argument_list|)
expr_stmt|;
if|if
condition|(
name|passwd
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|password
operator|=
literal|null
expr_stmt|;
name|this
operator|.
name|digestPassword
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|password
operator|=
name|_cred
operator|.
name|toString
argument_list|()
expr_stmt|;
name|this
operator|.
name|digestPassword
operator|=
name|digest
argument_list|(
name|passwd
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Sets the digest passwod value of the User object 	 *  	 * @param passwd 	 *            The new passwordDigest value 	 * @deprecated 	 */
specifier|public
specifier|final
name|void
name|setPasswordDigest
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
comment|//setPassword(passwd);
name|this
operator|.
name|digestPassword
operator|=
operator|(
name|passwd
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|passwd
expr_stmt|;
block|}
comment|/** 	 * Sets the encoded passwod value of the User object 	 *  	 * @param passwd 	 *            The new passwordDigest value 	 * @deprecated 	 */
specifier|public
specifier|final
name|void
name|setEncodedPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
name|setPassword
argument_list|(
literal|"{MD5}"
operator|+
name|passwd
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
operator|=
operator|(
name|passwd
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|passwd
expr_stmt|;
block|}
specifier|public
specifier|final
name|String
name|digest
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
switch|switch
condition|(
name|PASSWORD_ENCODING
condition|)
block|{
case|case
name|PLAIN_ENCODING
case|:
return|return
name|passwd
return|;
case|case
name|MD5_ENCODING
case|:
return|return
name|MessageDigester
operator|.
name|md5
argument_list|(
name|name
operator|+
literal|":"
operator|+
name|realm
operator|.
name|getId
argument_list|()
operator|+
literal|":"
operator|+
name|passwd
argument_list|,
literal|false
argument_list|)
return|;
default|default:
return|return
name|MessageDigester
operator|.
name|md5
argument_list|(
name|passwd
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
comment|/** 	 * Split up the validate method into two, to make it possible to 	 * authenticate users, which are not defined in the instance named "exist" 	 * without having impact on the standard functionality. 	 *  	 * @param passwd 	 * @return true if the password was correct, false if not, or if there was a 	 *         problem. 	 */
annotation|@
name|Deprecated
comment|//use SecurityManager.authenticate
specifier|public
specifier|final
name|boolean
name|validate
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
name|SecurityManager
name|sm
decl_stmt|;
try|try
block|{
name|sm
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
expr_stmt|;
return|return
name|validate
argument_list|(
name|passwd
argument_list|,
name|sm
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Failed to get security manager in validate: "
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
annotation|@
name|Deprecated
comment|//use SecurityManager.authenticate
specifier|public
specifier|final
name|boolean
name|validate
parameter_list|(
name|String
name|passwd
parameter_list|,
name|SecurityManager
name|sm
parameter_list|)
block|{
comment|// security management is disabled if in repair mode
if|if
condition|(
operator|!
name|CHECK_PASSWORDS
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|password
operator|==
literal|null
operator|&&
name|digestPassword
operator|==
literal|null
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|id
operator|==
literal|1
operator|&&
name|passwd
operator|==
literal|null
condition|)
block|{
name|passwd
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|passwd
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// [ 1557095 ] LDAP passwords patch
comment|// Try to authenticate using LDAP
if|if
condition|(
name|sm
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|sm
operator|instanceof
name|LDAPbindSecurityManager
condition|)
block|{
if|if
condition|(
operator|(
operator|(
name|LDAPbindSecurityManager
operator|)
name|sm
operator|)
operator|.
name|bind
argument_list|(
name|name
argument_list|,
name|passwd
argument_list|)
condition|)
return|return
literal|true
return|;
else|else
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|MessageDigester
operator|.
name|md5
argument_list|(
name|passwd
argument_list|,
literal|true
argument_list|)
operator|.
name|equals
argument_list|(
name|password
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
if|if
condition|(
name|digestPassword
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|digest
argument_list|(
name|passwd
argument_list|)
operator|.
name|equals
argument_list|(
name|digestPassword
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Deprecated
comment|//use SecurityManager.authenticate
specifier|public
specifier|final
name|boolean
name|validateDigest
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
if|if
condition|(
name|digestPassword
operator|==
literal|null
condition|)
return|return
literal|true
return|;
if|if
condition|(
name|passwd
operator|==
literal|null
condition|)
return|return
literal|false
return|;
return|return
name|digest
argument_list|(
name|passwd
argument_list|)
operator|.
name|equals
argument_list|(
name|digestPassword
argument_list|)
return|;
block|}
block|}
end_class

end_unit

