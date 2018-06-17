begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|txn
operator|.
name|bridge
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|xmldb
operator|.
name|LocalCollection
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
name|LocalCollectionManagementService
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
name|exist
operator|.
name|xmldb
operator|.
name|function
operator|.
name|LocalXmldbFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|ErrorCodes
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter  */
end_comment

begin_class
specifier|public
class|class
name|InTxnLocalCollectionManagementService
extends|extends
name|LocalCollectionManagementService
block|{
specifier|public
name|InTxnLocalCollectionManagementService
parameter_list|(
specifier|final
name|Subject
name|user
parameter_list|,
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|LocalCollection
name|parent
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|pool
argument_list|,
name|parent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Collection
name|createCollection
parameter_list|(
specifier|final
name|XmldbURI
name|name
parameter_list|,
specifier|final
name|Date
name|created
parameter_list|)
throws|throws
name|XMLDBException
block|{
specifier|final
name|XmldbURI
name|collName
init|=
name|resolve
argument_list|(
name|name
argument_list|)
decl_stmt|;
name|withDb
argument_list|(
parameter_list|(
name|broker
parameter_list|,
name|transaction
parameter_list|)
lambda|->
block|{
try|try
block|{
specifier|final
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
name|coll
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|collName
argument_list|)
decl_stmt|;
if|if
condition|(
name|created
operator|!=
literal|null
condition|)
block|{
name|coll
operator|.
name|setCreationTime
argument_list|(
name|created
operator|.
name|getTime
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|coll
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|TriggerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XMLDBException
argument_list|(
name|ErrorCodes
operator|.
name|VENDOR_ERROR
argument_list|,
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
operator|new
name|InTxnLocalCollection
argument_list|(
name|user
argument_list|,
name|brokerPool
argument_list|,
name|collection
argument_list|,
name|collName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|protected
parameter_list|<
name|R
parameter_list|>
name|R
name|withDb
parameter_list|(
specifier|final
name|LocalXmldbFunction
argument_list|<
name|R
argument_list|>
name|dbOperation
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|InTxnLocalCollection
operator|.
name|withDb
argument_list|(
name|brokerPool
argument_list|,
name|user
argument_list|,
name|dbOperation
argument_list|)
return|;
block|}
block|}
end_class

end_unit
