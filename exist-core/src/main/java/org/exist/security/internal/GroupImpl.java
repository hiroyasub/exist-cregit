begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|AbstractGroup
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
name|AbstractRealm
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
name|storage
operator|.
name|DBBroker
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
name|AbstractGroup
block|{
specifier|public
name|GroupImpl
parameter_list|(
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
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
name|GroupImpl
parameter_list|(
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|Configuration
name|configuration
parameter_list|,
specifier|final
name|boolean
name|removed
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
name|this
operator|.
name|removed
operator|=
name|removed
expr_stmt|;
block|}
specifier|public
name|GroupImpl
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|int
name|id
parameter_list|,
specifier|final
name|String
name|name
parameter_list|)
throws|throws
name|ConfigurationException
block|{
name|this
argument_list|(
name|broker
argument_list|,
name|realm
argument_list|,
name|id
argument_list|,
name|name
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|GroupImpl
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
name|int
name|id
parameter_list|,
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|List
argument_list|<
name|Account
argument_list|>
name|managers
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
name|id
argument_list|,
name|name
argument_list|,
name|managers
argument_list|)
expr_stmt|;
block|}
name|GroupImpl
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|AbstractRealm
name|realm
parameter_list|,
specifier|final
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
name|name
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
