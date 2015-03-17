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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractSubject
implements|implements
name|Subject
block|{
specifier|protected
specifier|final
name|AbstractAccount
name|account
decl_stmt|;
specifier|protected
specifier|final
name|Session
name|session
decl_stmt|;
specifier|public
name|AbstractSubject
parameter_list|(
specifier|final
name|AbstractAccount
name|account
parameter_list|)
block|{
name|this
operator|.
name|account
operator|=
name|account
expr_stmt|;
name|this
operator|.
name|session
operator|=
operator|new
name|Session
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
throws|throws
name|PermissionDeniedException
block|{
return|return
name|account
operator|.
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
specifier|final
name|Group
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
return|return
name|account
operator|.
name|addGroup
argument_list|(
name|group
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
name|account
operator|.
name|setPrimaryGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remGroup
parameter_list|(
specifier|final
name|String
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|account
operator|.
name|remGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
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
name|account
operator|.
name|getGroups
argument_list|()
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
name|account
operator|.
name|getGroupIds
argument_list|()
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
name|account
operator|.
name|hasDbaRole
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryGroup
parameter_list|()
block|{
return|return
name|account
operator|.
name|getPrimaryGroup
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Group
name|getDefaultGroup
parameter_list|()
block|{
return|return
name|account
operator|.
name|getDefaultGroup
argument_list|()
return|;
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
name|account
operator|.
name|hasGroup
argument_list|(
name|group
argument_list|)
return|;
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
name|account
operator|.
name|setPassword
argument_list|(
name|passwd
argument_list|)
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
name|account
operator|.
name|setCredential
argument_list|(
name|credential
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Realm
name|getRealm
parameter_list|()
block|{
return|return
name|account
operator|.
name|getRealm
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPassword
parameter_list|()
block|{
return|return
name|account
operator|.
name|getPassword
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getDigestPassword
parameter_list|()
block|{
return|return
name|account
operator|.
name|getDigestPassword
argument_list|()
return|;
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
name|groups
parameter_list|)
block|{
name|account
operator|.
name|setGroups
argument_list|(
name|groups
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getRealmId
parameter_list|()
block|{
return|return
name|account
operator|.
name|getRealmId
argument_list|()
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
name|account
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|account
operator|.
name|getName
argument_list|()
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
name|account
operator|.
name|isConfigured
argument_list|()
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
name|account
operator|.
name|getConfiguration
argument_list|()
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
name|account
operator|.
name|getUsername
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
name|account
operator|.
name|isAccountNonExpired
argument_list|()
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
name|account
operator|.
name|isAccountNonLocked
argument_list|()
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
name|account
operator|.
name|isCredentialsNonExpired
argument_list|()
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
name|account
operator|.
name|setEnabled
argument_list|(
name|enabled
argument_list|)
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
name|account
operator|.
name|isEnabled
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
return|return
name|account
operator|.
name|equals
argument_list|(
name|obj
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getSessionId
parameter_list|()
block|{
return|return
name|session
operator|.
name|getId
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Session
name|getSession
parameter_list|()
block|{
return|return
name|session
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
name|String
name|getMetadataValue
parameter_list|(
specifier|final
name|SchemaType
name|schemaType
parameter_list|)
block|{
return|return
name|account
operator|.
name|getMetadataValue
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
name|account
operator|.
name|setMetadataValue
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
name|account
operator|.
name|getMetadataKeys
argument_list|()
return|;
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
name|account
operator|.
name|assertCanModifyAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clearMetadata
parameter_list|()
block|{
name|account
operator|.
name|clearMetadata
argument_list|()
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
name|account
operator|.
name|getUserMask
argument_list|()
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
name|account
operator|.
name|setUserMask
argument_list|(
name|umask
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

