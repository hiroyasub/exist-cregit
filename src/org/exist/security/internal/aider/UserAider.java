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
name|Set
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
name|Realm
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|UserAider
implements|implements
name|User
block|{
specifier|private
name|String
name|name
decl_stmt|;
specifier|private
name|int
name|id
init|=
operator|-
literal|1
decl_stmt|;
specifier|private
name|Group
name|defaultRole
init|=
literal|null
decl_stmt|;
specifier|public
name|UserAider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|UserAider
parameter_list|(
name|int
name|id
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
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
name|name
parameter_list|,
name|Group
name|group
parameter_list|)
block|{
name|this
argument_list|(
name|name
argument_list|)
expr_stmt|;
name|defaultRole
operator|=
name|group
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
comment|// TODO Auto-generated method stub
return|return
literal|null
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
name|group
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
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
name|group
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
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
name|groups
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
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
comment|// TODO Auto-generated method stub
return|return
literal|null
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
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getUID() 	 */
annotation|@
name|Override
specifier|public
name|int
name|getUID
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
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
comment|// TODO Auto-generated method stub
return|return
literal|null
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
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
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
comment|// TODO Auto-generated method stub
block|}
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
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getHome() 	 */
annotation|@
name|Override
specifier|public
name|XmldbURI
name|getHome
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#authenticate(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|authenticate
parameter_list|(
name|Object
name|credentials
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#isAuthenticated() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isAuthenticated
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#setUID(int) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setUID
parameter_list|(
name|int
name|uid
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#setAttribute(java.lang.String, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setAttribute
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|value
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getAttribute(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.User#getAttributeNames() 	 */
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getAttributeNames
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
block|}
end_class

end_unit

