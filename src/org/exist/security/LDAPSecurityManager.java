begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * LDAPSecurityManager.java  *  * Created on January 29, 2006, 8:22 PM  *  * (C) R. Alexander Milowski alex@milowski.com  */
end_comment

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
name|text
operator|.
name|MessageFormat
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Hashtable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|naming
operator|.
name|Context
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NameNotFoundException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingEnumeration
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|NamingException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|Attribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|Attributes
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|DirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|InitialDirContext
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchControls
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|naming
operator|.
name|directory
operator|.
name|SearchResult
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
name|security
operator|.
name|xacml
operator|.
name|ExistPDP
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

begin_comment
comment|/**  * Note: A lot of this code is "borrowed" from Tomcat's JNDIRealm.java  * @author R. Alexander Milowski  */
end_comment

begin_class
specifier|public
class|class
name|LDAPSecurityManager
implements|implements
name|SecurityManager
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
name|SecurityManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|Map
name|userByNameCache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Map
name|userByIdCache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Map
name|groupByNameCache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|Map
name|groupByIdCache
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|static
name|String
name|getProperty
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|defaultValue
parameter_list|)
block|{
name|String
name|value
init|=
name|System
operator|.
name|getProperty
argument_list|(
name|name
argument_list|)
decl_stmt|;
return|return
name|value
operator|==
literal|null
condition|?
name|defaultValue
else|:
name|value
return|;
block|}
specifier|protected
name|String
name|contextFactory
init|=
name|getProperty
argument_list|(
literal|"security.ldap.contextFactory"
argument_list|,
literal|"com.sun.jndi.ldap.LdapCtxFactory"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|connectionURL
init|=
name|getProperty
argument_list|(
literal|"security.ldap.connection.url"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|userPasswordAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.userPassword"
argument_list|,
literal|"userPassword"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|userDigestPasswordAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.userDigestPassword"
argument_list|,
literal|"digestPassword"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|uidAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.uid"
argument_list|,
literal|"uid"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|uidNumberAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.uidNumber"
argument_list|,
literal|"uidNumber"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|gidNumberAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.gidNumber"
argument_list|,
literal|"gidNumber"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|groupNameAttr
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.groupName"
argument_list|,
literal|"cn"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|groupMemberName
init|=
name|getProperty
argument_list|(
literal|"security.ldap.attr.groupMemberName"
argument_list|,
literal|"uniqueMember"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|groupClassName
init|=
name|getProperty
argument_list|(
literal|"security.ldap.groupClass"
argument_list|,
literal|"posixGroup"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|userClassName
init|=
name|getProperty
argument_list|(
literal|"security.ldap.userClass"
argument_list|,
literal|"posixAccount"
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|userBase
init|=
name|getProperty
argument_list|(
literal|"security.ldap.dn.user"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|groupBase
init|=
name|getProperty
argument_list|(
literal|"security.ldap.dn.group"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|protected
name|DirContext
name|context
init|=
literal|null
decl_stmt|;
comment|/**     * The message format used to form the distinguished name of a     * user, with "{0}" marking the spot where the specified username     * goes.     */
specifier|protected
name|String
name|userByNamePattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|userByIdPattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageFormat
name|userByNamePatternFormat
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageFormat
name|userByIdPatternFormat
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|groupByIdPattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|groupByNamePattern
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageFormat
name|groupByIdPatternFormat
init|=
literal|null
decl_stmt|;
specifier|protected
name|MessageFormat
name|groupByNamePatternFormat
init|=
literal|null
decl_stmt|;
specifier|protected
name|ExistPDP
name|pdp
init|=
literal|null
decl_stmt|;
comment|/** Creates a new instance of LDAPSecurityManager */
specifier|public
name|LDAPSecurityManager
parameter_list|()
block|{
name|setUserByNamePattern
argument_list|(
name|uidAttr
operator|+
literal|"={0},"
operator|+
name|userBase
argument_list|)
expr_stmt|;
name|setUserByIdPattern
argument_list|(
name|uidNumberAttr
operator|+
literal|"={0},"
operator|+
name|userBase
argument_list|)
expr_stmt|;
name|setGroupByIdPattern
argument_list|(
name|gidNumberAttr
operator|+
literal|"={0},"
operator|+
name|groupBase
argument_list|)
expr_stmt|;
name|setGroupByNamePattern
argument_list|(
name|groupNameAttr
operator|+
literal|"={0},"
operator|+
name|groupBase
argument_list|)
expr_stmt|;
block|}
comment|/**     * Set the message format pattern for selecting users in this Realm.     * This may be one simple pattern, or multiple patterns to be tried,     * separated by parentheses. (for example, either "cn={0}", or     * "(cn={0})(cn={0},o=myorg)" Full LDAP search strings are also supported,     * but only the "OR", "|" syntax, so "(|(cn={0})(cn={0},o=myorg))" is     * also valid. Complex search strings with&, etc are NOT supported.     *     * @param pattern The new user pattern     */
specifier|public
name|void
name|setUserByNamePattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|userByNamePattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|userByNamePatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|userByNamePattern
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setUserByIdPattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|userByIdPattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|userByIdPatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|userByIdPattern
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupByIdPattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|groupByIdPattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|groupByIdPatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|groupByIdPattern
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupByNamePattern
parameter_list|(
name|String
name|pattern
parameter_list|)
block|{
name|this
operator|.
name|groupByNamePattern
operator|=
name|pattern
expr_stmt|;
name|this
operator|.
name|groupByNamePatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
name|groupByNamePattern
argument_list|)
expr_stmt|;
block|}
comment|/**     * Return a String representing the value of the specified attribute.     *     * @param attrId Attribute name     * @param attrs Attributes containing the required value     *     * @exception NamingException if a directory server error occurs     */
specifier|private
name|String
name|getAttributeValue
parameter_list|(
name|String
name|attrId
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|NamingException
block|{
if|if
condition|(
name|attrId
operator|==
literal|null
operator|||
name|attrs
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|Attribute
name|attr
init|=
name|attrs
operator|.
name|get
argument_list|(
name|attrId
argument_list|)
decl_stmt|;
if|if
condition|(
name|attr
operator|==
literal|null
condition|)
block|{
return|return
operator|(
literal|null
operator|)
return|;
block|}
name|Object
name|value
init|=
name|attr
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
return|return
operator|(
literal|null
operator|)
return|;
block|}
name|String
name|valueString
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|value
operator|instanceof
name|byte
index|[]
condition|)
block|{
name|valueString
operator|=
operator|new
name|String
argument_list|(
operator|(
name|byte
index|[]
operator|)
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|valueString
operator|=
name|value
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
return|return
name|valueString
return|;
block|}
specifier|protected
name|Hashtable
name|getDirectoryEnvironment
parameter_list|()
block|{
if|if
condition|(
name|connectionURL
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The security.ldap.connection.url property is not set."
argument_list|)
throw|;
block|}
if|if
condition|(
name|userBase
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The security.ldap.dn.user property is not set."
argument_list|)
throw|;
block|}
if|if
condition|(
name|groupBase
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"The security.ldap.dn.group property is not set."
argument_list|)
throw|;
block|}
name|Hashtable
name|env
init|=
operator|new
name|Hashtable
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"security.ldap.contextFactory="
operator|+
name|contextFactory
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|INITIAL_CONTEXT_FACTORY
argument_list|,
name|contextFactory
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"security.ldap.connection.url="
operator|+
name|connectionURL
argument_list|)
expr_stmt|;
name|env
operator|.
name|put
argument_list|(
name|Context
operator|.
name|PROVIDER_URL
argument_list|,
name|connectionURL
argument_list|)
expr_stmt|;
return|return
name|env
return|;
block|}
comment|// TODO: need an exception to throw
specifier|public
name|void
name|attach
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|DBBroker
name|sysBroker
parameter_list|)
block|{
try|try
block|{
name|context
operator|=
operator|new
name|InitialDirContext
argument_list|(
name|getDirectoryEnvironment
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|enableXACML
init|=
operator|(
name|Boolean
operator|)
name|sysBroker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
literal|"xacml.enable"
argument_list|)
decl_stmt|;
if|if
condition|(
name|enableXACML
operator|!=
literal|null
operator|&&
name|enableXACML
operator|.
name|booleanValue
argument_list|()
condition|)
block|{
name|pdp
operator|=
operator|new
name|ExistPDP
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"XACML enabled"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Connecting to context failed for LDAP-based security: "
operator|+
name|connectionURL
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|User
name|getUserByName
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|String
name|username
parameter_list|)
throws|throws
name|NamingException
block|{
comment|// Form the dn from the user pattern
name|String
name|dn
init|=
name|userByNamePatternFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|username
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to get user by: "
operator|+
name|dn
argument_list|)
expr_stmt|;
return|return
name|getUser
argument_list|(
name|context
argument_list|,
name|dn
argument_list|)
return|;
block|}
specifier|protected
name|User
name|getUserById
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|int
name|uid
parameter_list|)
throws|throws
name|NamingException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Searching for "
operator|+
name|uidNumberAttr
operator|+
literal|"="
operator|+
name|uid
operator|+
literal|" in "
operator|+
name|userBase
argument_list|)
expr_stmt|;
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
name|NamingEnumeration
name|users
init|=
name|context
operator|.
name|search
argument_list|(
name|userBase
argument_list|,
literal|"("
operator|+
name|uidNumberAttr
operator|+
literal|"="
operator|+
name|uid
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
while|while
condition|(
name|users
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|users
operator|.
name|next
argument_list|()
decl_stmt|;
return|return
name|newUserFromAttributes
argument_list|(
name|context
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Group
name|getGroupById
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|int
name|gid
parameter_list|)
throws|throws
name|NamingException
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Searching for "
operator|+
name|gidNumberAttr
operator|+
literal|"="
operator|+
name|gid
operator|+
literal|" in "
operator|+
name|groupBase
argument_list|)
expr_stmt|;
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
name|NamingEnumeration
name|groups
init|=
name|context
operator|.
name|search
argument_list|(
name|groupBase
argument_list|,
literal|"("
operator|+
name|gidNumberAttr
operator|+
literal|"="
operator|+
name|gid
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
while|while
condition|(
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|cn
init|=
name|getAttributeValue
argument_list|(
name|groupNameAttr
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Constructing group "
operator|+
name|cn
argument_list|)
expr_stmt|;
return|return
operator|new
name|Group
argument_list|(
name|cn
argument_list|,
name|gid
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|Group
name|getGroupByName
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|String
name|name
parameter_list|)
throws|throws
name|NamingException
block|{
name|String
name|g_dn
init|=
name|groupByNamePatternFormat
operator|.
name|format
argument_list|(
operator|new
name|String
index|[]
block|{
name|name
block|}
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Attempting to get group by: "
operator|+
name|g_dn
argument_list|)
expr_stmt|;
try|try
block|{
name|Attributes
name|attrs
init|=
name|context
operator|.
name|getAttributes
argument_list|(
name|g_dn
argument_list|)
decl_stmt|;
name|String
name|cn
init|=
name|getAttributeValue
argument_list|(
name|groupNameAttr
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|int
name|gid
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getAttributeValue
argument_list|(
name|gidNumberAttr
argument_list|,
name|attrs
argument_list|)
argument_list|)
decl_stmt|;
return|return
operator|new
name|Group
argument_list|(
name|cn
argument_list|,
name|gid
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
name|e
parameter_list|)
block|{
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|User
name|newUserFromAttributes
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|Attributes
name|attrs
parameter_list|)
throws|throws
name|NamingException
block|{
name|String
name|username
init|=
name|getAttributeValue
argument_list|(
name|uidAttr
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|String
name|password
init|=
name|getAttributeValue
argument_list|(
name|userPasswordAttr
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|String
name|digestPassword
init|=
name|getAttributeValue
argument_list|(
name|userDigestPasswordAttr
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
name|String
name|gid
init|=
name|getAttributeValue
argument_list|(
name|gidNumberAttr
argument_list|,
name|attrs
argument_list|)
decl_stmt|;
comment|//String g_dn = groupByIdPatternFormat.format(new String[] { gid });
name|LOG
operator|.
name|info
argument_list|(
literal|"Searching for "
operator|+
name|gidNumberAttr
operator|+
literal|"="
operator|+
name|gid
operator|+
literal|" in "
operator|+
name|groupBase
argument_list|)
expr_stmt|;
name|String
name|mainGroup
init|=
literal|null
decl_stmt|;
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
name|NamingEnumeration
name|groups
init|=
name|context
operator|.
name|search
argument_list|(
name|groupBase
argument_list|,
literal|"("
operator|+
name|gidNumberAttr
operator|+
literal|"="
operator|+
name|gid
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
while|while
condition|(
name|mainGroup
operator|==
literal|null
operator|&&
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|mainGroup
operator|=
name|getAttributeValue
argument_list|(
name|groupNameAttr
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|mainGroup
operator|==
literal|null
operator|||
name|mainGroup
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Main group "
operator|+
name|gid
operator|+
literal|" for user "
operator|+
name|username
operator|+
literal|" is not able to be found in LDAP for group property "
operator|+
name|gidNumberAttr
argument_list|)
throw|;
block|}
name|int
name|uid
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|getAttributeValue
argument_list|(
name|uidNumberAttr
argument_list|,
name|attrs
argument_list|)
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Constructing user "
operator|+
name|username
operator|+
literal|"/"
operator|+
name|uid
operator|+
literal|" in group "
operator|+
operator|(
name|mainGroup
operator|==
literal|null
condition|?
literal|"<none>"
else|:
name|mainGroup
operator|)
argument_list|)
expr_stmt|;
name|User
name|user
init|=
operator|new
name|User
argument_list|(
name|username
argument_list|,
literal|null
argument_list|,
name|mainGroup
argument_list|)
decl_stmt|;
name|user
operator|.
name|setUID
argument_list|(
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|password
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
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
name|int
name|end
init|=
name|password
operator|.
name|indexOf
argument_list|(
literal|'}'
argument_list|)
decl_stmt|;
name|String
name|type
init|=
name|password
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|end
operator|+
literal|1
argument_list|)
decl_stmt|;
name|String
name|value
init|=
name|password
operator|.
name|substring
argument_list|(
name|end
operator|+
literal|1
argument_list|)
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"  digest: "
operator|+
name|type
operator|+
literal|", "
operator|+
name|value
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|type
operator|.
name|equals
argument_list|(
literal|"{MD5}"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"User "
operator|+
name|username
operator|+
literal|" has a non-md5 digested password: "
operator|+
name|type
argument_list|)
throw|;
block|}
name|user
operator|.
name|setEncodedPassword
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|user
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|digestPassword
operator|!=
literal|null
condition|)
block|{
name|user
operator|.
name|setPasswordDigest
argument_list|(
name|digestPassword
argument_list|)
expr_stmt|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"Finding additional groups..."
argument_list|)
expr_stmt|;
name|String
name|fullName
init|=
name|uidAttr
operator|+
literal|"="
operator|+
name|username
operator|+
literal|","
operator|+
name|userBase
decl_stmt|;
name|groups
operator|=
name|context
operator|.
name|search
argument_list|(
name|groupBase
argument_list|,
literal|"("
operator|+
name|groupMemberName
operator|+
literal|"="
operator|+
name|fullName
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
expr_stmt|;
while|while
condition|(
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|getAttributeValue
argument_list|(
name|groupNameAttr
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"Group associated with "
operator|+
name|username
operator|+
literal|" does not have a valid name for attribute "
operator|+
name|groupNameAttr
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|name
operator|.
name|equals
argument_list|(
name|mainGroup
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"   ...adding: "
operator|+
name|name
argument_list|)
expr_stmt|;
name|user
operator|.
name|addGroup
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|user
return|;
block|}
specifier|protected
name|User
name|getUser
parameter_list|(
name|DirContext
name|context
parameter_list|,
name|String
name|dn
parameter_list|)
throws|throws
name|NamingException
block|{
comment|// Get required attributes from user entry
name|Attributes
name|attrs
init|=
literal|null
decl_stmt|;
try|try
block|{
name|attrs
operator|=
name|context
operator|.
name|getAttributes
argument_list|(
name|dn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NameNotFoundException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot find user "
operator|+
name|dn
argument_list|,
name|ex
argument_list|)
expr_stmt|;
return|return
operator|(
literal|null
operator|)
return|;
block|}
if|if
condition|(
name|attrs
operator|==
literal|null
condition|)
block|{
return|return
operator|(
literal|null
operator|)
return|;
block|}
name|LOG
operator|.
name|info
argument_list|(
literal|"User "
operator|+
name|dn
operator|+
literal|" found, attempting to find group and construct..."
argument_list|)
expr_stmt|;
return|return
name|newUserFromAttributes
argument_list|(
name|context
argument_list|,
name|attrs
argument_list|)
return|;
block|}
specifier|public
name|void
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
block|}
specifier|public
name|void
name|deleteUser
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
block|}
specifier|public
name|void
name|deleteUser
parameter_list|(
name|User
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
block|}
specifier|public
name|int
name|getCollectionDefaultPerms
parameter_list|()
block|{
return|return
name|Permission
operator|.
name|DEFAULT_PERM
return|;
block|}
specifier|public
name|Group
name|getGroup
parameter_list|(
name|int
name|gid
parameter_list|)
block|{
name|Integer
name|igid
init|=
operator|new
name|Integer
argument_list|(
name|gid
argument_list|)
decl_stmt|;
name|Group
name|group
init|=
operator|(
name|Group
operator|)
name|groupByIdCache
operator|.
name|get
argument_list|(
name|igid
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|group
operator|=
name|getGroupById
argument_list|(
name|context
argument_list|,
name|gid
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|groupByIdCache
operator|.
name|put
argument_list|(
name|igid
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get group by #"
operator|+
name|gid
operator|+
literal|" due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|group
return|;
block|}
specifier|public
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Group
name|group
init|=
operator|(
name|Group
operator|)
name|groupByIdCache
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|group
operator|=
name|getGroupByName
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|group
operator|!=
literal|null
condition|)
block|{
name|groupByNameCache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get group "
operator|+
name|name
operator|+
literal|" due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|group
return|;
block|}
comment|// This needs to be an enumeration
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
block|{
try|try
block|{
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
name|NamingEnumeration
name|groups
init|=
name|context
operator|.
name|search
argument_list|(
name|groupBase
argument_list|,
literal|"(objectClass="
operator|+
name|groupClassName
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
name|List
name|groupList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|groups
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|groups
operator|.
name|next
argument_list|()
decl_stmt|;
name|groupList
operator|.
name|add
argument_list|(
name|getAttributeValue
argument_list|(
name|groupNameAttr
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
index|[]
name|retval
init|=
operator|new
name|String
index|[
name|groupList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|groupList
operator|.
name|toArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|retval
argument_list|,
literal|0
argument_list|,
name|retval
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|retval
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get a list of all groups due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|boolean
name|isXACMLEnabled
parameter_list|()
block|{
return|return
name|pdp
operator|!=
literal|null
return|;
block|}
specifier|public
name|ExistPDP
name|getPDP
parameter_list|()
block|{
return|return
name|pdp
return|;
block|}
specifier|public
name|int
name|getResourceDefaultPerms
parameter_list|()
block|{
return|return
name|Permission
operator|.
name|DEFAULT_PERM
return|;
block|}
specifier|public
name|User
name|getUser
parameter_list|(
name|int
name|uid
parameter_list|)
block|{
name|Integer
name|iuid
init|=
operator|new
name|Integer
argument_list|(
name|uid
argument_list|)
decl_stmt|;
name|User
name|user
init|=
operator|(
name|User
operator|)
name|userByIdCache
operator|.
name|get
argument_list|(
name|iuid
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|user
operator|=
name|getUserById
argument_list|(
name|context
argument_list|,
name|uid
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|userByIdCache
operator|.
name|put
argument_list|(
name|iuid
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get user by #"
operator|+
name|uid
operator|+
literal|" due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|user
return|;
block|}
specifier|public
name|User
name|getUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|User
name|user
init|=
operator|(
name|User
operator|)
name|userByNameCache
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|user
operator|=
name|getUserByName
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|!=
literal|null
condition|)
block|{
name|userByNameCache
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get user "
operator|+
name|name
operator|+
literal|" due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|user
return|;
block|}
comment|// This needs to be an enumeration
specifier|public
name|User
index|[]
name|getUsers
parameter_list|()
block|{
try|try
block|{
name|SearchControls
name|constraints
init|=
operator|new
name|SearchControls
argument_list|()
decl_stmt|;
name|constraints
operator|.
name|setSearchScope
argument_list|(
name|SearchControls
operator|.
name|ONELEVEL_SCOPE
argument_list|)
expr_stmt|;
name|NamingEnumeration
name|users
init|=
name|context
operator|.
name|search
argument_list|(
name|userBase
argument_list|,
literal|"(objectClass="
operator|+
name|userClassName
operator|+
literal|")"
argument_list|,
name|constraints
argument_list|)
decl_stmt|;
name|List
name|userList
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
while|while
condition|(
name|users
operator|.
name|hasMore
argument_list|()
condition|)
block|{
name|SearchResult
name|result
init|=
operator|(
name|SearchResult
operator|)
name|users
operator|.
name|next
argument_list|()
decl_stmt|;
name|userList
operator|.
name|add
argument_list|(
name|newUserFromAttributes
argument_list|(
name|context
argument_list|,
name|result
operator|.
name|getAttributes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|User
index|[]
name|retval
init|=
operator|new
name|User
index|[
name|userList
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|userList
operator|.
name|toArray
argument_list|()
argument_list|,
literal|0
argument_list|,
name|retval
argument_list|,
literal|0
argument_list|,
name|retval
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|retval
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot get the list of users due to exception."
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|// TODO: this shouldn't be in this interface
specifier|public
specifier|synchronized
name|boolean
name|hasAdminPrivileges
parameter_list|(
name|User
name|user
parameter_list|)
block|{
return|return
name|user
operator|.
name|hasDbaRole
argument_list|()
return|;
block|}
comment|// TODO: why is this here?
specifier|public
specifier|synchronized
name|boolean
name|hasUser
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|getUserByName
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot check for user "
operator|+
name|name
operator|+
literal|" due to exception"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|// TODO: why is this here?
specifier|public
specifier|synchronized
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
try|try
block|{
return|return
name|getGroupByName
argument_list|(
name|context
argument_list|,
name|name
argument_list|)
operator|!=
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|NamingException
name|ex
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Cannot check for group "
operator|+
name|name
operator|+
literal|" due to exception"
argument_list|,
name|ex
argument_list|)
expr_stmt|;
block|}
return|return
literal|false
return|;
block|}
comment|// TODO: this should be addUser
specifier|public
name|void
name|setUser
parameter_list|(
name|User
name|user
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

