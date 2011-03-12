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
name|ArrayList
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

begin_comment
comment|/**  * Group details.  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  */
end_comment

begin_class
specifier|public
class|class
name|GroupAider
implements|implements
name|Group
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
name|List
argument_list|<
name|Account
argument_list|>
name|managers
init|=
operator|new
name|ArrayList
argument_list|<
name|Account
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|GroupAider
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
name|GroupAider
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
name|GroupAider
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
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|realmId
operator|=
name|realmId
expr_stmt|;
block|}
specifier|public
name|GroupAider
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
operator|-
literal|1
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
comment|//XXX: parse name for realmId, use default as workaround
name|this
operator|.
name|realmId
operator|=
name|RealmImpl
operator|.
name|ID
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
comment|/* (non-Javadoc) 	 * @see org.exist.security.Group#getId() 	 */
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
name|boolean
name|isManager
parameter_list|(
name|Account
name|account
parameter_list|)
block|{
for|for
control|(
name|Account
name|manager
range|:
name|managers
control|)
block|{
if|if
condition|(
name|manager
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getName
argument_list|()
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
name|void
name|addManager
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
if|if
condition|(
operator|!
name|managers
operator|.
name|contains
argument_list|(
name|account
argument_list|)
condition|)
block|{
name|managers
operator|.
name|add
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addManagers
parameter_list|(
name|List
argument_list|<
name|Account
argument_list|>
name|managers
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
for|for
control|(
name|Account
name|manager
range|:
name|managers
control|)
block|{
name|addManager
argument_list|(
name|manager
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|Account
argument_list|>
name|getManagers
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
return|return
name|managers
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeManager
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
for|for
control|(
name|Account
name|manager
range|:
name|managers
control|)
block|{
if|if
condition|(
name|manager
operator|.
name|getName
argument_list|()
operator|.
name|equals
argument_list|(
name|account
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
name|managers
operator|.
name|remove
argument_list|(
name|manager
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|assertCanModifyGroup
parameter_list|(
name|Account
name|account
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//do nothing
comment|//TODO do we need to check any permissions?
block|}
annotation|@
name|Override
specifier|public
name|Realm
name|getRealm
parameter_list|()
block|{
throw|throw
operator|new
name|UnsupportedOperationException
argument_list|(
literal|"Not supported yet."
argument_list|)
throw|;
block|}
block|}
end_class

end_unit

