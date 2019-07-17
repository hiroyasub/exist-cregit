begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
operator|.
name|aider
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
name|Credential
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
name|Permission
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
name|internal
operator|.
name|RealmImpl
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
name|realm
operator|.
name|Realm
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
comment|/**  * Account details.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_comment
comment|//TODO UserAider (and all *Aider classes) is evil and must be destroyed. Its too easy to use a UserAider to securityManager.updateAccount
end_comment

begin_comment
comment|//and it turns out you have forgotten to set some property of the account and so it is removed from the configuration
end_comment

begin_comment
comment|//Note by Adam Retter 2012-12-29
end_comment

begin_class
specifier|public
class|class
name|UserAider
implements|implements
name|Account
block|{
specifier|private
specifier|final
name|String
name|realmId
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|SchemaType
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|passwordDigest
init|=
literal|null
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|groups
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|umask
init|=
name|Permission
operator|.
name|DEFAULT_UMASK
decl_stmt|;
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|int
name|id
parameter_list|)
block|{
name|this
argument_list|(
name|id
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|RealmImpl
operator|.
name|ID
argument_list|,
name|name
argument_list|)
expr_stmt|;
comment|//XXX:parse name for realm id
block|}
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|String
name|realmId
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|UNDEFINED_ID
argument_list|,
name|realmId
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|int
name|id
parameter_list|,
specifier|final
name|String
name|realmId
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|realmId
operator|=
name|realmId
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|String
name|realmId
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Group
name|group
parameter_list|)
block|{
name|this
argument_list|(
name|realmId
argument_list|,
name|name
argument_list|)
expr_stmt|;
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|Group
name|group
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|addGroup
argument_list|(
name|group
argument_list|)
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
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRealmId
parameter_list|()
block|{
return|return
name|realmId
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
specifier|final
name|String
name|name
parameter_list|)
block|{
specifier|final
name|Group
name|group
init|=
operator|new
name|GroupAider
argument_list|(
name|realmId
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|groups
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|group
argument_list|)
expr_stmt|;
return|return
name|group
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
specifier|final
name|Group
name|group
parameter_list|)
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
return|return
name|addGroup
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
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
if|if
condition|(
operator|!
name|groups
operator|.
name|containsKey
argument_list|(
name|group
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
specifier|final
name|List
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|>
name|entries
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|groups
operator|.
name|entrySet
argument_list|()
argument_list|)
decl_stmt|;
name|Collections
operator|.
name|sort
argument_list|(
name|entries
argument_list|,
parameter_list|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|o1
parameter_list|,
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|o2
parameter_list|)
lambda|->
block|{
if|if
condition|(
name|o1
operator|.
name|getKey
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
argument_list|)
expr_stmt|;
name|groups
operator|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
name|entry
range|:
name|entries
control|)
block|{
name|groups
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|remGroup
parameter_list|(
specifier|final
name|String
name|role
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|groups
operator|.
name|containsKey
argument_list|(
name|role
argument_list|)
operator|&&
name|groups
operator|.
name|size
argument_list|()
operator|<=
literal|1
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"You cannot remove the primary group of an account."
argument_list|)
throw|;
block|}
name|groups
operator|.
name|remove
argument_list|(
name|role
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setGroups
parameter_list|(
specifier|final
name|String
index|[]
name|names
parameter_list|)
block|{
name|groups
operator|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|name
range|:
name|names
control|)
block|{
name|addGroup
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
block|{
return|return
name|groups
operator|.
name|keySet
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
literal|0
index|]
argument_list|)
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
return|return
operator|new
name|int
index|[
literal|0
index|]
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasDbaRole
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryGroup
parameter_list|()
block|{
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
annotation|@
name|Override
specifier|public
name|boolean
name|hasGroup
parameter_list|(
specifier|final
name|String
name|group
parameter_list|)
block|{
return|return
name|groups
operator|.
name|containsKey
argument_list|(
name|group
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Realm
name|getRealm
parameter_list|()
block|{
return|return
literal|null
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
return|return
name|metadata
operator|.
name|keySet
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearMetadata
parameter_list|()
block|{
name|metadata
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
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
specifier|final
name|Iterator
argument_list|<
name|Group
argument_list|>
name|iterator
init|=
name|groups
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
if|if
condition|(
name|iterator
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
name|iterator
operator|.
name|next
argument_list|()
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setEncodedPassword
parameter_list|(
specifier|final
name|String
name|passwd
parameter_list|)
block|{
name|password
operator|=
name|passwd
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setPassword
parameter_list|(
specifier|final
name|String
name|passwd
parameter_list|)
block|{
name|password
operator|=
name|passwd
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setCredential
parameter_list|(
specifier|final
name|Credential
name|credential
parameter_list|)
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not yet implemented"
argument_list|)
throw|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|password
return|;
block|}
specifier|public
name|void
name|setPasswordDigest
parameter_list|(
specifier|final
name|String
name|password
parameter_list|)
block|{
name|passwordDigest
operator|=
name|password
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDigestPassword
parameter_list|()
block|{
return|return
name|passwordDigest
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
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
literal|null
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
literal|false
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
literal|false
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
literal|false
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
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|save
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//do nothing
block|}
annotation|@
name|Override
specifier|public
name|void
name|assertCanModifyAccount
parameter_list|(
specifier|final
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
name|user
operator|.
name|getId
argument_list|()
operator|!=
name|getId
argument_list|()
operator|&&
operator|!
name|user
operator|.
name|hasDbaRole
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|PermissionDeniedException
argument_list|(
literal|"Permission denied to modify user"
argument_list|)
throw|;
block|}
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
block|}
end_class

end_unit

