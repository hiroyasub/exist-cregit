begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|openid
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|AuthenticationException
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ReamlImpl
implements|implements
name|Realm
block|{
comment|/* (non-Javadoc) 	 * @see org.exist.security.realm.AuthenticatingRealm#authenticate(java.lang.String, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Subject
name|authenticate
parameter_list|(
name|String
name|accountName
parameter_list|,
name|Object
name|credentials
parameter_list|)
throws|throws
name|AuthenticationException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#addAccount(org.exist.security.User) 	 */
annotation|@
name|Override
specifier|public
name|Account
name|addAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#getAccount(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Account
name|getAccount
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#hasAccount(org.exist.security.User) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasAccount
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#hasAccount(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasAccount
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#updateAccount(org.exist.security.User) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|updateAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.AccountsManagement#deleteAccount(org.exist.security.User) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|deleteAccount
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|ConfigurationException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#addGroup(org.exist.security.Group) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|addGroup
parameter_list|(
name|Group
name|role
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#getGroup(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|getGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#hasGroup(org.exist.security.Group) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasGroup
parameter_list|(
name|Group
name|role
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#hasGroup(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasGroup
parameter_list|(
name|String
name|name
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#updateGroup(org.exist.security.Group) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|updateGroup
parameter_list|(
name|Group
name|role
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.management.GroupsManagement#deleteGroup(org.exist.security.Group) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|deleteGroup
parameter_list|(
name|Group
name|role
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.realm.Realm#getId() 	 */
annotation|@
name|Override
specifier|public
name|String
name|getId
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.realm.Realm#getAccounts() 	 */
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Account
argument_list|>
name|getAccounts
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.realm.Realm#getRoles() 	 */
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|Group
argument_list|>
name|getRoles
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.realm.Realm#startUp(org.exist.storage.DBBroker) 	 */
annotation|@
name|Override
specifier|public
name|void
name|startUp
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
block|{
comment|// TODO Auto-generated method stub
block|}
annotation|@
name|Override
specifier|public
name|BrokerPool
name|getDatabase
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

