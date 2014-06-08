begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|*
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
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldSettings
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldSettings
operator|.
name|OCTAL_STRING_KEY
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
name|ConfigurationReferenceBy
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

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|""
argument_list|)
specifier|public
specifier|abstract
class|class
name|AbstractAccount
extends|extends
name|AbstractPrincipal
implements|implements
name|Account
block|{
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"group"
argument_list|)
annotation|@
name|ConfigurationReferenceBy
argument_list|(
literal|"name"
argument_list|)
specifier|protected
name|List
argument_list|<
name|Group
argument_list|>
name|groups
init|=
operator|new
name|ArrayList
argument_list|<
name|Group
argument_list|>
argument_list|()
decl_stmt|;
comment|//used for internal locking
specifier|private
name|boolean
name|accountLocked
init|=
literal|false
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"expired"
argument_list|)
specifier|private
name|boolean
name|accountExpired
init|=
literal|false
decl_stmt|;
comment|//@ConfigurationFieldAsElement("credentials-expired")
specifier|private
name|boolean
name|credentialsExpired
init|=
literal|false
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"enabled"
argument_list|)
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"umask"
argument_list|)
annotation|@
name|ConfigurationFieldSettings
argument_list|(
name|OCTAL_STRING_KEY
argument_list|)
specifier|private
name|int
name|umask
init|=
name|Permission
operator|.
name|DEFAULT_UMASK
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"metadata"
argument_list|)
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|protected
name|Credential
name|_cred
init|=
literal|null
decl_stmt|;
comment|/** 	 * Indicates if the user belongs to the dba group, i.e. is a superuser. 	 */
specifier|protected
name|boolean
name|hasDbaRole
init|=
literal|false
decl_stmt|;
specifier|public
name|AbstractAccount
parameter_list|(
name|AbstractRealm
name|realm
parameter_list|,
name|int
name|id
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
name|realm
operator|.
name|collectionAccounts
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|AbstractAccount
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
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|realm
argument_list|,
name|realm
operator|.
name|collectionAccounts
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|AbstractAccount
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
block|}
specifier|public
name|boolean
name|checkCredentials
parameter_list|(
name|Object
name|credentials
parameter_list|)
block|{
return|return
name|_cred
operator|==
literal|null
condition|?
literal|false
else|:
name|_cred
operator|.
name|check
argument_list|(
name|credentials
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|Group
name|group
init|=
name|getRealm
argument_list|()
operator|.
name|getGroup
argument_list|(
name|name
argument_list|)
decl_stmt|;
comment|//if we cant find the group in our own realm, try other realms
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
name|group
operator|=
name|getRealm
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getGroup
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|addGroup
argument_list|(
name|group
argument_list|)
return|;
block|}
comment|//this method used by Configurator
specifier|protected
specifier|final
name|Group
name|addGroup
parameter_list|(
name|Configuration
name|conf
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|conf
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|String
name|name
init|=
name|conf
operator|.
name|getProperty
argument_list|(
literal|"name"
argument_list|)
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|addGroup
argument_list|(
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|group
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Account
name|user
init|=
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|group
operator|.
name|assertCanModifyGroup
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|groups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|groups
operator|.
name|add
argument_list|(
name|group
argument_list|)
expr_stmt|;
if|if
condition|(
name|SecurityManager
operator|.
name|DBA_GROUP
operator|.
name|equals
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|hasDbaRole
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|group
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryGroup
parameter_list|(
specifier|final
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Account
name|user
init|=
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
name|group
operator|.
name|assertCanModifyGroup
argument_list|(
name|user
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|groups
operator|.
name|contains
argument_list|(
name|group
argument_list|)
condition|)
block|{
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
name|Collections
operator|.
name|sort
argument_list|(
name|groups
argument_list|,
operator|new
name|Comparator
argument_list|<
name|Group
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
specifier|final
name|Group
name|o1
parameter_list|,
specifier|final
name|Group
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
else|else
block|{
return|return
literal|1
return|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|void
name|remGroup
parameter_list|(
name|String
name|name
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
specifier|final
name|Account
name|subject
init|=
name|getDatabase
argument_list|()
operator|.
name|getSubject
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Group
name|group
range|:
name|groups
control|)
block|{
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|group
operator|.
name|assertCanModifyGroup
argument_list|(
name|subject
argument_list|)
expr_stmt|;
comment|//remove from the group
name|groups
operator|.
name|remove
argument_list|(
name|group
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|SecurityManager
operator|.
name|DBA_GROUP
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|hasDbaRole
operator|=
literal|false
expr_stmt|;
block|}
block|}
annotation|@
name|Override
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
comment|//		this.groups = groups;
comment|//		for (int i = 0; i< groups.length; i++)
comment|//			if (SecurityManager.DBA_GROUP.equals(groups[i]))
comment|//				hasDbaRole = true;
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|String
index|[
literal|0
index|]
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|String
index|[]
name|names
init|=
operator|new
name|String
index|[
name|groups
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
specifier|final
name|Group
name|role
range|:
name|groups
control|)
block|{
name|names
index|[
name|i
operator|++
index|]
operator|=
name|role
operator|.
name|getName
argument_list|()
expr_stmt|;
block|}
return|return
name|names
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
index|[]
name|getGroupIds
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
return|return
operator|new
name|int
index|[
literal|0
index|]
return|;
block|}
name|int
name|i
init|=
literal|0
decl_stmt|;
specifier|final
name|int
index|[]
name|ids
init|=
operator|new
name|int
index|[
name|groups
operator|.
name|size
argument_list|()
index|]
decl_stmt|;
for|for
control|(
specifier|final
name|Group
name|group
range|:
name|groups
control|)
block|{
name|ids
index|[
name|i
operator|++
index|]
operator|=
name|group
operator|.
name|getId
argument_list|()
expr_stmt|;
block|}
return|return
name|ids
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|groups
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
specifier|final
name|Group
name|group
range|:
name|groups
control|)
block|{
if|if
condition|(
name|group
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|name
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
name|Override
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
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<account name=\""
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|name
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
literal|"id=\""
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
name|id
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
specifier|final
name|Group
name|group
range|:
name|groups
control|)
block|{
name|buf
operator|.
name|append
argument_list|(
name|group
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"</account>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|)
block|{
name|AbstractAccount
name|other
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|AbstractSubject
condition|)
block|{
name|other
operator|=
operator|(
operator|(
name|AbstractSubject
operator|)
name|obj
operator|)
operator|.
name|account
expr_stmt|;
block|}
if|else if
condition|(
name|obj
operator|instanceof
name|AbstractAccount
condition|)
block|{
name|other
operator|=
operator|(
name|AbstractAccount
operator|)
name|obj
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
return|return
operator|(
name|getRealm
argument_list|()
operator|==
name|other
operator|.
name|getRealm
argument_list|()
operator|&&
name|name
operator|.
name|equals
argument_list|(
name|other
operator|.
name|name
argument_list|)
operator|)
return|;
block|}
comment|//id == other.id;
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
specifier|final
name|String
name|getPrimaryGroup
parameter_list|()
block|{
comment|//TODO this function should return Group and not String
specifier|final
name|Group
name|defaultGroup
init|=
name|getDefaultGroup
argument_list|()
decl_stmt|;
if|if
condition|(
name|defaultGroup
operator|!=
literal|null
condition|)
block|{
return|return
name|defaultGroup
operator|.
name|getName
argument_list|()
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
comment|/**          * @deprecated user getPrimaryGroup instead;          */
annotation|@
name|Deprecated
annotation|@
name|Override
specifier|public
name|Group
name|getDefaultGroup
parameter_list|()
block|{
if|if
condition|(
name|groups
operator|!=
literal|null
operator|&&
name|groups
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
return|return
name|groups
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getUsername
parameter_list|()
block|{
return|return
name|getName
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAccountNonExpired
parameter_list|()
block|{
return|return
operator|!
name|accountExpired
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAccountNonLocked
parameter_list|()
block|{
return|return
operator|!
name|accountLocked
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCredentialsNonExpired
parameter_list|()
block|{
return|return
operator|!
name|credentialsExpired
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setEnabled
parameter_list|(
specifier|final
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getMetadataValue
parameter_list|(
specifier|final
name|SchemaType
name|schemaType
parameter_list|)
block|{
return|return
name|metadata
operator|.
name|get
argument_list|(
name|schemaType
operator|.
name|getNamespace
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setMetadataValue
parameter_list|(
specifier|final
name|SchemaType
name|schemaType
parameter_list|,
specifier|final
name|String
name|value
parameter_list|)
block|{
name|metadata
operator|.
name|put
argument_list|(
name|schemaType
operator|.
name|getNamespace
argument_list|()
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|SchemaType
argument_list|>
name|getMetadataKeys
parameter_list|()
block|{
specifier|final
name|Set
argument_list|<
name|SchemaType
argument_list|>
name|metadataKeys
init|=
operator|new
name|HashSet
argument_list|<
name|SchemaType
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|key
range|:
name|metadata
operator|.
name|keySet
argument_list|()
control|)
block|{
comment|//XXX: other types?
if|if
condition|(
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadataKeys
operator|.
name|add
argument_list|(
name|AXSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|EXistSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
operator|!=
literal|null
condition|)
block|{
name|metadataKeys
operator|.
name|add
argument_list|(
name|EXistSchemaType
operator|.
name|valueOfNamespace
argument_list|(
name|key
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|metadataKeys
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearMetadata
parameter_list|()
block|{
if|if
condition|(
name|metadata
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|metadata
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|assertCanModifyAccount
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Unspecified User is not allowed to modify account '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
if|else if
condition|(
operator|!
name|user
operator|.
name|hasDbaRole
argument_list|()
operator|&&
operator|!
name|user
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|getName
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"User '"
operator|+
name|user
operator|.
name|getName
argument_list|()
operator|+
literal|"' is not allowed to modify account '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|setUserMask
parameter_list|(
specifier|final
name|int
name|umask
parameter_list|)
block|{
name|this
operator|.
name|umask
operator|=
name|umask
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getUserMask
parameter_list|()
block|{
return|return
name|umask
return|;
block|}
block|}
end_class

end_unit

