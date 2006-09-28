begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
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
name|util
operator|.
name|DatabaseConfigurationException
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
name|EXistException
import|;
end_import

begin_comment
comment|/**  *  Represents a user within the database.  *  *@author     Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|User
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
name|User
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|User
name|DEFAULT
init|=
operator|new
name|User
argument_list|(
literal|"guest"
argument_list|,
literal|null
argument_list|,
literal|"guest"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|GROUP
init|=
literal|"group"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NAME
init|=
literal|"name"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|PASS
init|=
literal|"password"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DIGEST_PASS
init|=
literal|"digest-password"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|USER_ID
init|=
literal|"uid"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|HOME
init|=
literal|"home"
decl_stmt|;
specifier|private
specifier|static
name|String
name|realm
init|=
literal|"exist"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PLAIN_ENCODING
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|SIMPLE_MD5_ENCODING
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|MD5_ENCODING
init|=
literal|2
decl_stmt|;
specifier|public
specifier|static
name|int
name|PASSWORD_ENCODING
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
name|User
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
name|encoding
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
name|encoding
argument_list|)
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
name|void
name|setPasswordRealm
parameter_list|(
name|String
name|value
parameter_list|)
block|{
name|realm
operator|=
name|value
expr_stmt|;
block|}
specifier|private
name|String
index|[]
name|groups
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
name|String
name|digestPassword
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|user
decl_stmt|;
specifier|private
name|int
name|uid
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|XmldbURI
name|home
init|=
literal|null
decl_stmt|;
comment|/**       * Indicates if the user belongs to the dba group,      * i.e. is a superuser.      */
specifier|private
name|boolean
name|hasDbaRole
init|=
literal|false
decl_stmt|;
comment|/**      *  Create a new user with name and password      *      *@param  user      Description of the Parameter      *@param  password  Description of the Parameter      */
specifier|public
name|User
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Create a new user with name      *      *@param  user  Description of the Parameter      */
specifier|public
name|User
parameter_list|(
name|String
name|user
parameter_list|)
block|{
name|this
operator|.
name|user
operator|=
name|user
expr_stmt|;
block|}
comment|/**      *  Create a new user with name, password and primary group      *      *@param  user          Description of the Parameter      *@param  password      Description of the Parameter      *@param  primaryGroup  Description of the Parameter      */
specifier|public
name|User
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|password
parameter_list|,
name|String
name|primaryGroup
parameter_list|)
block|{
name|this
argument_list|(
name|user
argument_list|,
name|password
argument_list|)
expr_stmt|;
name|addGroup
argument_list|(
name|primaryGroup
argument_list|)
expr_stmt|;
block|}
comment|/**      *  Read a new user from the given DOM node      *      *@param  node                                Description of the Parameter      *@exception  DatabaseConfigurationException  Description of the Exception      */
specifier|public
name|User
parameter_list|(
name|int
name|majorVersion
parameter_list|,
name|int
name|minorVersion
parameter_list|,
name|Element
name|node
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|user
operator|=
name|node
operator|.
name|getAttribute
argument_list|(
name|NAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"user needs a name"
argument_list|)
throw|;
if|if
condition|(
name|majorVersion
operator|==
literal|0
condition|)
block|{
name|this
operator|.
name|digestPassword
operator|=
name|node
operator|.
name|getAttribute
argument_list|(
name|PASS
argument_list|)
expr_stmt|;
name|this
operator|.
name|password
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
name|node
operator|.
name|getAttribute
argument_list|(
name|PASS
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|password
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|this
operator|.
name|password
operator|.
name|startsWith
argument_list|(
literal|"{MD5}"
argument_list|)
condition|)
block|{
name|this
operator|.
name|password
operator|=
name|this
operator|.
name|password
operator|.
name|substring
argument_list|(
literal|5
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|this
operator|.
name|password
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
literal|'{'
condition|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"Unrecognized password encoding "
operator|+
name|password
operator|+
literal|" for user "
operator|+
name|user
argument_list|)
throw|;
block|}
block|}
name|this
operator|.
name|digestPassword
operator|=
name|node
operator|.
name|getAttribute
argument_list|(
name|DIGEST_PASS
argument_list|)
expr_stmt|;
block|}
name|String
name|userId
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|USER_ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|userId
operator|==
literal|null
condition|)
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"attribute id missing"
argument_list|)
throw|;
try|try
block|{
name|uid
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|userId
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|DatabaseConfigurationException
argument_list|(
literal|"illegal user id: "
operator|+
name|userId
operator|+
literal|" for user "
operator|+
name|user
argument_list|)
throw|;
block|}
name|String
name|home
init|=
name|node
operator|.
name|getAttribute
argument_list|(
name|HOME
argument_list|)
decl_stmt|;
name|this
operator|.
name|home
operator|=
name|home
operator|==
literal|null
condition|?
literal|null
else|:
name|XmldbURI
operator|.
name|create
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|NodeList
name|gl
init|=
name|node
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|group
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
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|group
operator|=
name|gl
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|ELEMENT_NODE
operator|&&
name|group
operator|.
name|getLocalName
argument_list|()
operator|.
name|equals
argument_list|(
name|GROUP
argument_list|)
condition|)
name|addGroup
argument_list|(
name|group
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      *  Add the user to a group      *      *@param  group  The feature to be added to the Group attribute      */
specifier|public
specifier|final
name|void
name|addGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
name|groups
operator|=
operator|new
name|String
index|[
literal|1
index|]
expr_stmt|;
name|groups
index|[
literal|0
index|]
operator|=
name|group
expr_stmt|;
block|}
else|else
block|{
name|int
name|len
init|=
name|groups
operator|.
name|length
decl_stmt|;
name|String
index|[]
name|ngroups
init|=
operator|new
name|String
index|[
name|len
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|groups
argument_list|,
literal|0
argument_list|,
name|ngroups
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|ngroups
index|[
name|len
index|]
operator|=
name|group
expr_stmt|;
name|groups
operator|=
name|ngroups
expr_stmt|;
block|}
if|if
condition|(
name|SecurityManager
operator|.
name|DBA_GROUP
operator|.
name|equals
argument_list|(
name|group
argument_list|)
condition|)
name|hasDbaRole
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|setGroups
parameter_list|(
name|String
index|[]
name|groups
parameter_list|)
block|{
name|this
operator|.
name|groups
operator|=
name|groups
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|SecurityManager
operator|.
name|DBA_GROUP
operator|.
name|equals
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
condition|)
name|hasDbaRole
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      *  Get all groups this user belongs to      *      *@return    The groups value      */
specifier|public
specifier|final
name|String
index|[]
name|getGroups
parameter_list|()
block|{
return|return
name|groups
operator|==
literal|null
condition|?
operator|new
name|String
index|[
literal|0
index|]
else|:
name|groups
return|;
block|}
specifier|public
specifier|final
name|boolean
name|hasDbaRole
parameter_list|()
block|{
return|return
name|hasDbaRole
return|;
block|}
comment|/**      *  Get the user name      *      *@return    The user value      */
specifier|public
specifier|final
name|String
name|getName
parameter_list|()
block|{
return|return
name|user
return|;
block|}
specifier|public
specifier|final
name|int
name|getUID
parameter_list|()
block|{
return|return
name|uid
return|;
block|}
comment|/**      *  Get the user's password      *      *@return    Description of the Return Value      */
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
comment|/**      *  Get the primary group this user belongs to      *      *@return    The primaryGroup value      */
specifier|public
specifier|final
name|String
name|getPrimaryGroup
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|==
literal|null
operator|||
name|groups
operator|.
name|length
operator|==
literal|0
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|String
operator|)
name|groups
index|[
literal|0
index|]
return|;
block|}
comment|/**      *  Is the user a member of group?      *      *@param  group  Description of the Parameter      *@return        Description of the Return Value      */
specifier|public
specifier|final
name|boolean
name|hasGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|groups
index|[
name|i
index|]
operator|.
name|equals
argument_list|(
name|group
argument_list|)
condition|)
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
comment|/**      *  Sets the password attribute of the User object      *      *@param  passwd  The new password value      */
specifier|public
specifier|final
name|void
name|setPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
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
name|MD5
operator|.
name|md
argument_list|(
name|passwd
argument_list|,
literal|true
argument_list|)
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
comment|/**      *  Sets the digest passwod value of the User object      *      *@param  passwd  The new passwordDigest value      */
specifier|public
specifier|final
name|void
name|setPasswordDigest
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
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
comment|/**      *  Sets the encoded passwod value of the User object      *      *@param  passwd  The new passwordDigest value      */
specifier|public
specifier|final
name|void
name|setEncodedPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
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
name|MD5
operator|.
name|md
argument_list|(
name|user
operator|+
literal|":"
operator|+
name|realm
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
name|MD5
operator|.
name|md
argument_list|(
name|passwd
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<user name=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\" "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"uid=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toString
argument_list|(
name|uid
argument_list|)
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" password=\"{MD5}"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|digestPassword
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" digest-password=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|digestPassword
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|home
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|" home=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|home
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\">"
argument_list|)
expr_stmt|;
block|}
else|else
name|buf
operator|.
name|append
argument_list|(
literal|">"
argument_list|)
expr_stmt|;
if|if
condition|(
name|groups
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groups
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"<group>"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|groups
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"</group>"
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</user>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
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
comment|//Try to authenticate using LDAP
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
name|user
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
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|MD5
operator|.
name|md
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
specifier|public
name|void
name|setUID
parameter_list|(
name|int
name|uid
parameter_list|)
block|{
name|this
operator|.
name|uid
operator|=
name|uid
expr_stmt|;
block|}
specifier|public
name|void
name|setHome
parameter_list|(
name|XmldbURI
name|homeCollection
parameter_list|)
block|{
name|home
operator|=
name|homeCollection
expr_stmt|;
block|}
specifier|public
name|XmldbURI
name|getHome
parameter_list|()
block|{
return|return
name|home
return|;
block|}
comment|/* (non-Javadoc) 	 * @see java.lang.Object#equals(java.lang.Object) 	 */
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|User
name|other
init|=
operator|(
name|User
operator|)
name|obj
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
return|return
name|uid
operator|==
name|other
operator|.
name|uid
return|;
block|}
else|else
block|{
return|return
operator|(
literal|false
operator|)
return|;
block|}
block|}
block|}
end_class

end_unit

