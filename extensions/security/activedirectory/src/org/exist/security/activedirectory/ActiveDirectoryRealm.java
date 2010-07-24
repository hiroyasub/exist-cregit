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
name|activedirectory
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
name|User
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
class|class
name|ActiveDirectoryRealm
implements|implements
name|Realm
block|{
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getId() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getAccount(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|User
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getAccounts() 	 */
annotation|@
name|Override
specifier|public
name|Collection
argument_list|<
name|User
argument_list|>
name|getAccounts
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#hasAccount(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasAccount
parameter_list|(
name|String
name|accountName
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#updateAccount(org.exist.security.User) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|updateAccount
parameter_list|(
name|User
name|account
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getRoles() 	 */
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getRole(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|getRole
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#hasRole(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|hasRole
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#authenticate(java.lang.String, java.lang.Object) 	 */
specifier|public
name|User
name|authenticate
parameter_list|(
name|String
name|username
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getAccount(int) 	 */
annotation|@
name|Override
specifier|public
name|User
name|getAccount
parameter_list|(
name|int
name|id
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#getRole(int) 	 */
annotation|@
name|Override
specifier|public
name|Group
name|getRole
parameter_list|(
name|int
name|id
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.Realm#startUp(org.exist.storage.DBBroker) 	 */
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
block|}
end_class

end_unit

