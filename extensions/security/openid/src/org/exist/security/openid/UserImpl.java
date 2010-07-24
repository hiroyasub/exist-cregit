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
name|lang
operator|.
name|Override
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
name|UserAttributes
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_import
import|import
name|org
operator|.
name|openid4java
operator|.
name|discovery
operator|.
name|Identifier
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|UserImpl
implements|implements
name|User
block|{
name|Identifier
name|_identifier
init|=
literal|null
decl_stmt|;
specifier|public
name|UserImpl
parameter_list|(
name|Identifier
name|identifier
parameter_list|)
block|{
name|_identifier
operator|=
name|identifier
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|String
index|[]
name|getGroups
parameter_list|()
block|{
return|return
operator|new
name|String
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
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getUID
parameter_list|()
block|{
return|return
operator|-
literal|1
return|;
block|}
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
block|}
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
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isAuthenticated
parameter_list|()
block|{
return|return
operator|(
name|_identifier
operator|!=
literal|null
operator|)
return|;
block|}
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
annotation|@
name|Override
specifier|public
name|String
name|getPassword
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
name|getDigestPassword
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
name|getName
parameter_list|()
block|{
name|String
name|name
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
name|UserAttributes
operator|.
name|FIRTSNAME
argument_list|)
condition|)
name|name
operator|+=
name|attributes
operator|.
name|get
argument_list|(
name|UserAttributes
operator|.
name|FIRTSNAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|attributes
operator|.
name|containsKey
argument_list|(
name|UserAttributes
operator|.
name|LASTNAME
argument_list|)
condition|)
block|{
if|if
condition|(
name|name
operator|!=
literal|""
condition|)
name|name
operator|+=
literal|" "
expr_stmt|;
name|name
operator|+=
name|attributes
operator|.
name|get
argument_list|(
name|UserAttributes
operator|.
name|LASTNAME
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|name
operator|+=
name|attributes
operator|.
name|get
argument_list|(
name|UserAttributes
operator|.
name|FULLNAME
argument_list|)
expr_stmt|;
if|if
condition|(
name|name
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
return|return
name|_identifier
operator|.
name|getIdentifier
argument_list|()
return|;
return|return
name|name
return|;
block|}
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Add a named attribute.      *      * @param name      * @param value      */
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
name|String
name|id
init|=
name|UserAttributes
operator|.
name|alias
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|==
literal|null
condition|)
name|attributes
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
else|else
name|attributes
operator|.
name|put
argument_list|(
name|id
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
comment|/**      * Get the named attribute value.      *      * @param name The String that is the name of the attribute.      * @return The value associated with the name or null if no value is associated with the name.      */
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
name|String
name|id
init|=
name|UserAttributes
operator|.
name|alias
operator|.
name|get
argument_list|(
name|name
argument_list|)
decl_stmt|;
if|if
condition|(
name|id
operator|!=
literal|null
condition|)
return|return
name|attributes
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
if|if
condition|(
name|name
operator|.
name|equalsIgnoreCase
argument_list|(
literal|"id"
argument_list|)
condition|)
return|return
name|_identifier
operator|.
name|getIdentifier
argument_list|()
return|;
return|return
name|attributes
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
comment|/**      * Returns the set of attributes names.      *      * @return the Set of attribute names.      */
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
return|return
name|attributes
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
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

