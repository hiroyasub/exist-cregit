begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
name|exist
operator|.
name|security
operator|.
name|AbstractRealm
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
name|AbstractAccount
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
name|config
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
name|Configurator
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
name|Group
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
name|SchemaType
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
name|ArrayList
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
name|boolean
name|CHECK_PASSWORDS
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|SecurityProperties
name|securityProperties
init|=
operator|new
name|SecurityProperties
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|SecurityProperties
name|getSecurityProperties
parameter_list|()
block|{
return|return
name|securityProperties
return|;
block|}
comment|/*     static {         Properties props = new Properties();         try {             props.load(AccountImpl.class.getClassLoader().getResourceAsStream(                     "org/exist/security/security.properties"));         } catch(IOException e) {         }         String option = props.getProperty("passwords.encoding", "md5");         setPasswordEncoding(option);         option = props.getProperty("passwords.check", "yes");         CHECK_PASSWORDS = option.equalsIgnoreCase("yes")                 || option.equalsIgnoreCase("true");     }      static public void enablePasswordChecks(boolean check) {         CHECK_PASSWORDS = check;     }      static public void setPasswordEncoding(String encoding) {         if(encoding != null) {             LOG.equals("Setting password encoding to " + encoding);             if(encoding.equalsIgnoreCase("plain")) {                 PASSWORD_ENCODING = PLAIN_ENCODING;             } else if(encoding.equalsIgnoreCase("md5")) {                 PASSWORD_ENCODING = MD5_ENCODING;             } else {                 PASSWORD_ENCODING = SIMPLE_MD5_ENCODING;             }         }     }*/
specifier|static
specifier|public
name|Subject
name|getUserFromServletRequest
parameter_list|(
name|HttpServletRequest
name|request
parameter_list|)
block|{
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
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
specifier|final
name|SecurityException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalArgumentException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
specifier|final
name|IllegalAccessException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchMethodException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
specifier|final
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
comment|/**      * Create a new user with name and password      *      * @param realm      * @param name      * @param password      * @throws ConfigurationException      */
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
name|Group
name|group
parameter_list|,
name|boolean
name|hasDbaRole
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
name|this
operator|.
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
name|this
operator|.
name|hasDbaRole
operator|=
name|hasDbaRole
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
name|String
name|name
parameter_list|,
name|String
name|password
parameter_list|,
name|Group
name|group
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
name|this
operator|.
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
comment|/**      * Create a new user with name      *      * @param name      *            The account name      * @throws ConfigurationException      */
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
name|Account
operator|.
name|UNDEFINED_ID
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
comment|//    /**
comment|//     * Create a new user with name, password and primary group
comment|//     *
comment|//     * @param name
comment|//     * @param password
comment|//     * @param primaryGroup
comment|//     * @throws ConfigurationException
comment|//     * @throws PermissionDeniedException
comment|//     */
comment|//	public AccountImpl(AbstractRealm realm, int id, String name, String password, String primaryGroup) throws ConfigurationException {
comment|//		this(realm, id, name, password);
comment|//		addGroup(primaryGroup);
comment|//	}
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
throws|,
name|PermissionDeniedException
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
name|instantiate
argument_list|(
name|from_user
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
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
throws|,
name|PermissionDeniedException
block|{
name|super
argument_list|(
name|broker
argument_list|,
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
name|instantiate
argument_list|(
name|from_user
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|instantiate
parameter_list|(
name|Account
name|from_user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//copy metadata
for|for
control|(
specifier|final
name|SchemaType
name|metadataKey
range|:
name|from_user
operator|.
name|getMetadataKeys
argument_list|()
control|)
block|{
specifier|final
name|String
name|metadataValue
init|=
name|from_user
operator|.
name|getMetadataValue
argument_list|(
name|metadataKey
argument_list|)
decl_stmt|;
name|setMetadataValue
argument_list|(
name|metadataKey
argument_list|,
name|metadataValue
argument_list|)
expr_stmt|;
block|}
comment|//copy umask
name|setUserMask
argument_list|(
name|from_user
operator|.
name|getUserMask
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|from_user
operator|instanceof
name|AccountImpl
condition|)
block|{
specifier|final
name|AccountImpl
name|user
init|=
operator|(
name|AccountImpl
operator|)
name|from_user
decl_stmt|;
name|groups
operator|=
operator|new
name|ArrayList
argument_list|<
name|Group
argument_list|>
argument_list|(
name|user
operator|.
name|groups
argument_list|)
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
specifier|final
name|UserAider
name|user
init|=
operator|(
name|UserAider
operator|)
name|from_user
decl_stmt|;
specifier|final
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
else|else
block|{
name|addGroup
argument_list|(
name|from_user
operator|.
name|getDefaultGroup
argument_list|()
argument_list|)
expr_stmt|;
comment|//TODO: groups
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
comment|//copy metadata
for|for
control|(
specifier|final
name|SchemaType
name|metadataKey
range|:
name|from_user
operator|.
name|getMetadataKeys
argument_list|()
control|)
block|{
specifier|final
name|String
name|metadataValue
init|=
name|from_user
operator|.
name|getMetadataValue
argument_list|(
name|metadataKey
argument_list|)
decl_stmt|;
name|setMetadataValue
argument_list|(
name|metadataKey
argument_list|,
name|metadataValue
argument_list|)
expr_stmt|;
block|}
name|groups
operator|=
name|from_user
operator|.
name|groups
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
comment|//this.realm = realm;   //set via super()
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|Configuration
name|configuration
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|realm
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
comment|//it require, because class's fields initializing after super constructor
if|if
condition|(
name|this
operator|.
name|configuration
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|this
operator|.
name|configuration
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|hasDbaRole
operator|=
name|this
operator|.
name|hasGroup
argument_list|(
name|SecurityManager
operator|.
name|DBA_GROUP
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AccountImpl
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|Configuration
name|configuration
parameter_list|,
name|boolean
name|removed
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
argument_list|(
name|realm
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|this
operator|.
name|removed
operator|=
name|removed
expr_stmt|;
block|}
comment|/**      * Get the user's password      *      * @return Description of the Return Value      * @deprecated      */
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
comment|/*      * (non-Javadoc)      *      * @see org.exist.security.User#setPassword(java.lang.String)      */
annotation|@
name|Override
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
name|this
argument_list|,
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
name|_cred
operator|.
name|getDigest
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
class|class
name|SecurityProperties
block|{
specifier|private
specifier|final
specifier|static
name|boolean
name|DEFAULT_CHECK_PASSWORDS
init|=
literal|true
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PROP_CHECK_PASSWORDS
init|=
literal|"passwords.check"
decl_stmt|;
specifier|private
name|Properties
name|loadedSecurityProperties
init|=
literal|null
decl_stmt|;
specifier|private
name|Boolean
name|checkPasswords
init|=
literal|null
decl_stmt|;
specifier|public
specifier|synchronized
name|boolean
name|isCheckPasswords
parameter_list|()
block|{
if|if
condition|(
name|checkPasswords
operator|==
literal|null
condition|)
block|{
specifier|final
name|String
name|property
init|=
name|getProperty
argument_list|(
name|PROP_CHECK_PASSWORDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|property
operator|==
literal|null
operator|||
name|property
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|checkPasswords
operator|=
name|DEFAULT_CHECK_PASSWORDS
expr_stmt|;
block|}
else|else
block|{
name|checkPasswords
operator|=
name|property
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"yes"
argument_list|)
operator|||
name|property
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"true"
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|checkPasswords
operator|.
name|booleanValue
argument_list|()
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|enableCheckPasswords
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
name|this
operator|.
name|checkPasswords
operator|=
name|enable
expr_stmt|;
block|}
specifier|private
specifier|synchronized
name|String
name|getProperty
parameter_list|(
name|String
name|propertyName
parameter_list|)
block|{
if|if
condition|(
name|loadedSecurityProperties
operator|==
literal|null
condition|)
block|{
name|loadedSecurityProperties
operator|=
operator|new
name|Properties
argument_list|()
expr_stmt|;
name|InputStream
name|is
init|=
literal|null
decl_stmt|;
try|try
block|{
name|is
operator|=
name|AccountImpl
operator|.
name|class
operator|.
name|getResourceAsStream
argument_list|(
literal|"security.properties"
argument_list|)
expr_stmt|;
if|if
condition|(
name|is
operator|!=
literal|null
condition|)
block|{
name|loadedSecurityProperties
operator|.
name|load
argument_list|(
name|is
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"Unable to load security.properties, using defaults. "
operator|+
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
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
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
block|}
empty_stmt|;
block|}
block|}
block|}
return|return
name|loadedSecurityProperties
operator|.
name|getProperty
argument_list|(
name|propertyName
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

