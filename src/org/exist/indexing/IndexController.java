begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *   *  $Id$  */
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
name|NodePath
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
name|txn
operator|.
name|Txn
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|*
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
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|Node
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
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Internally used by the {@link DBBroker} to dispatch an operation to each of the  * registered indexes.  *   */
end_comment

begin_class
specifier|public
class|class
name|IndexController
block|{
specifier|protected
name|Map
name|indexWorkers
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
specifier|protected
name|StreamListener
name|listener
init|=
literal|null
decl_stmt|;
specifier|public
name|IndexController
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|IndexWorker
index|[]
name|workers
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getIndexManager
argument_list|()
operator|.
name|getWorkers
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|workers
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|indexWorkers
operator|.
name|put
argument_list|(
name|workers
index|[
name|i
index|]
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|workers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|IndexWorker
name|getIndexWorker
parameter_list|(
name|String
name|indexId
parameter_list|)
block|{
return|return
operator|(
name|IndexWorker
operator|)
name|indexWorkers
operator|.
name|get
argument_list|(
name|indexId
argument_list|)
return|;
block|}
specifier|public
name|StreamListener
name|getStreamListener
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|!=
literal|null
condition|)
block|{
name|StreamListener
name|next
init|=
name|listener
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|setDocument
argument_list|(
name|document
argument_list|,
name|mode
argument_list|)
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|getNextInChain
argument_list|()
expr_stmt|;
block|}
return|return
name|listener
return|;
block|}
name|StreamListener
name|first
init|=
literal|null
decl_stmt|;
name|StreamListener
name|current
decl_stmt|,
name|previous
init|=
literal|null
decl_stmt|;
name|IndexWorker
name|worker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|worker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|current
operator|=
name|worker
operator|.
name|getListener
argument_list|(
name|mode
argument_list|,
name|document
argument_list|)
expr_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|current
expr_stmt|;
block|}
else|else
block|{
name|previous
operator|.
name|setNextInChain
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
name|previous
operator|=
name|current
expr_stmt|;
block|}
name|listener
operator|=
name|first
expr_stmt|;
return|return
name|listener
return|;
block|}
specifier|public
name|Map
name|configure
parameter_list|(
name|NodeList
name|configNodes
parameter_list|,
name|Map
name|namespaces
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|Map
name|map
init|=
operator|new
name|HashMap
argument_list|()
decl_stmt|;
name|IndexWorker
name|indexWorker
decl_stmt|;
name|Object
name|conf
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|conf
operator|=
name|indexWorker
operator|.
name|configure
argument_list|(
name|configNodes
argument_list|,
name|namespaces
argument_list|)
expr_stmt|;
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
name|map
operator|.
name|put
argument_list|(
name|indexWorker
operator|.
name|getIndexId
argument_list|()
argument_list|,
name|conf
argument_list|)
expr_stmt|;
block|}
return|return
name|map
return|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|indexWorker
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
name|IndexWorker
name|indexWorker
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|indexWorkers
operator|.
name|values
argument_list|()
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|indexWorker
operator|=
operator|(
name|IndexWorker
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|indexWorker
operator|.
name|removeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|removeNode
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|StoredNode
name|node
parameter_list|)
block|{
name|getStreamListener
argument_list|(
name|node
operator|.
name|getDocument
argument_list|()
argument_list|,
name|StreamListener
operator|.
name|REMOVE_NODES
argument_list|)
expr_stmt|;
name|IndexUtils
operator|.
name|scanNode
argument_list|(
name|transaction
argument_list|,
name|node
argument_list|,
name|listener
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

