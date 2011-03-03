begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
package|;
end_package

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
name|util
operator|.
name|DatabaseConfigurationException
import|;
end_import

begin_comment
comment|/**  * A dummy agent which will be used if JMX is disabled. It just acts as an empty  * placeholder.  */
end_comment

begin_class
specifier|public
class|class
name|DummyAgent
implements|implements
name|Agent
block|{
comment|// @Override
specifier|public
name|void
name|initDBInstance
parameter_list|(
name|BrokerPool
name|instance
parameter_list|)
block|{
comment|// do nothing
block|}
comment|// @Override
specifier|public
name|void
name|closeDBInstance
parameter_list|(
name|BrokerPool
name|instance
parameter_list|)
block|{
comment|// nothing to do
block|}
comment|// @Override
specifier|public
name|void
name|addMBean
parameter_list|(
name|String
name|dbInstance
parameter_list|,
name|String
name|name
parameter_list|,
name|Object
name|mbean
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
comment|// just do nothing
block|}
comment|// @Override
specifier|public
name|void
name|changeStatus
parameter_list|(
name|BrokerPool
name|instance
parameter_list|,
name|TaskStatus
name|actualStatus
parameter_list|)
block|{
comment|// nothing to do
block|}
comment|// @Override
specifier|public
name|void
name|updateStatus
parameter_list|(
name|BrokerPool
name|instance
parameter_list|,
name|int
name|percentage
parameter_list|)
block|{
comment|// nothing to do
block|}
block|}
end_class

end_unit

