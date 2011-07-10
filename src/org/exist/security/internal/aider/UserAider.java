begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
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
name|AXSchemaType
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * Account details.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|UserAider
implements|implements
name|Account
block|{
specifier|private
name|String
name|realmId
decl_stmt|;
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|int
name|id
decl_stmt|;
specifier|private
name|Group
name|defaultRole
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
name|roles
init|=
operator|new
name|LinkedHashMap
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|UserAider
parameter_list|(
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
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|realmId
operator|=
literal|"exist"
expr_stmt|;
comment|//XXX:parse name for realm id
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|id
operator|=
operator|-
literal|1
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
name|String
name|realmId
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
operator|-
literal|1
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
name|int
name|id
parameter_list|,
name|String
name|realmId
parameter_list|,
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
name|String
name|realmId
parameter_list|,
name|String
name|name
parameter_list|,
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
name|defaultRole
operator|=
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
name|String
name|name
parameter_list|,
name|Group
name|group
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
comment|//XXX: parse name for realmId, use default as workaround
name|defaultRole
operator|=
name|addGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see java.security.Principal#getName() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Principal#getId() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#addGroup(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|Group
name|role
init|=
operator|new
name|GroupAider
argument_list|(
name|realmId
argument_list|,
name|name
argument_list|)
decl_stmt|;
name|roles
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|role
argument_list|)
expr_stmt|;
return|return
name|role
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#addGroup(org.exist.security.Group) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|Group
name|role
parameter_list|)
block|{
return|return
name|addGroup
argument_list|(
name|role
operator|.
name|getName
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#remGroup(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|void
name|remGroup
parameter_list|(
name|String
name|role
parameter_list|)
block|{
name|roles
operator|.
name|remove
argument_list|(
name|role
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#setGroups(java.lang.String[]) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setGroups
parameter_list|(
name|String
index|[]
name|names
parameter_list|)
block|{
name|roles
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Group
argument_list|>
argument_list|()
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
name|names
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|addGroup
argument_list|(
name|names
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getGroups() 	 */
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
block|{
return|return
name|roles
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#hasDbaRole() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getPrimaryGroup() 	 */
annotation|@
name|Override
specifier|public
name|String
name|getPrimaryGroup
parameter_list|()
block|{
if|if
condition|(
name|defaultRole
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
name|defaultRole
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#hasGroup(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasGroup
parameter_list|(
name|String
name|group
parameter_list|)
block|{
return|return
name|roles
operator|.
name|containsKey
argument_list|(
name|group
argument_list|)
return|;
block|}
specifier|private
name|XmldbURI
name|homeCollection
init|=
literal|null
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#setHome(org.exist.xmldb.XmldbURI) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setHome
parameter_list|(
name|XmldbURI
name|homeCollection
parameter_list|)
block|{
name|this
operator|.
name|homeCollection
operator|=
name|homeCollection
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getHome() 	 */
annotation|@
name|Override
specifier|public
name|XmldbURI
name|getHome
parameter_list|()
block|{
return|return
name|homeCollection
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getRealm() 	 */
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
specifier|private
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
argument_list|<
name|SchemaType
argument_list|,
name|String
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|getMetadataValue
parameter_list|(
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
name|SchemaType
name|schemaType
parameter_list|,
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
name|Group
name|getDefaultGroup
parameter_list|()
block|{
return|return
name|defaultRole
return|;
block|}
specifier|private
name|String
name|password
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|setEncodedPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
name|password
operator|=
name|passwd
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#setPassword(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setPassword
parameter_list|(
name|String
name|passwd
parameter_list|)
block|{
name|password
operator|=
name|passwd
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getPassword() 	 */
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
specifier|private
name|String
name|passwordDigest
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|setPasswordDigest
parameter_list|(
name|String
name|password
parameter_list|)
block|{
name|passwordDigest
operator|=
name|password
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getDigestPassword() 	 */
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isEnabled
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
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
name|assertCanModifyAccount
parameter_list|(
name|Account
name|user
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//do nothing
comment|//TODO do we need to check any permissions?
block|}
block|}
end_class

end_unit

