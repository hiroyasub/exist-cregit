begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
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
name|storage
operator|.
name|DBBroker
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
name|btree
operator|.
name|BTree
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
name|btree
operator|.
name|DBException
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

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractIndex
implements|implements
name|Index
block|{
comment|/**      * Holds an id which uniquely identifies this index. This is usually the class name.       */
specifier|protected
specifier|static
name|String
name|ID
init|=
literal|"Give me an ID !"
decl_stmt|;
specifier|public
specifier|static
name|String
name|getID
parameter_list|()
block|{
return|return
name|ID
return|;
block|}
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
comment|//Probably not useful for every kind of index. Anyway...
specifier|private
name|Path
name|dataDir
init|=
literal|null
decl_stmt|;
specifier|protected
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Path
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|dataDir
operator|=
name|dataDir
expr_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
operator|&&
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"id"
argument_list|)
condition|)
block|{
name|name
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|getIndexId
parameter_list|()
block|{
return|return
name|getID
argument_list|()
return|;
block|}
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|void
name|setName
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
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
comment|//TODO : declare in interface ?
specifier|public
name|Path
name|getDataDir
parameter_list|()
block|{
return|return
name|dataDir
return|;
block|}
specifier|public
specifier|abstract
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
function_decl|;
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
specifier|abstract
name|IndexWorker
name|getWorker
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|checkIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|BTree
name|getStorage
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

