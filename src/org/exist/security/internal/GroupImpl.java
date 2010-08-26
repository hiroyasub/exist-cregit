begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
package|;
end_package

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
name|security
operator|.
name|Group
import|;
end_import

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"group"
argument_list|)
specifier|public
class|class
name|GroupImpl
extends|extends
name|AbstractPrincipal
implements|implements
name|Comparable
argument_list|<
name|Object
argument_list|>
implements|,
name|Group
block|{
specifier|public
name|GroupImpl
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
name|collectionGroups
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Deprecated
comment|//remove after old LDAP security manager remove
specifier|public
name|GroupImpl
parameter_list|(
name|String
name|name
parameter_list|,
name|int
name|id
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|super
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|id
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GroupImpl
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
name|int
name|compareTo
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|other
operator|instanceof
name|GroupImpl
operator|)
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"wrong type"
argument_list|)
throw|;
return|return
name|name
operator|.
name|compareTo
argument_list|(
operator|(
operator|(
name|GroupImpl
operator|)
name|other
operator|)
operator|.
name|name
argument_list|)
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"<group name=\""
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
literal|"\" id=\""
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
literal|"\"/>"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

