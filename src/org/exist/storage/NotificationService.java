begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2005-2010 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|persistent
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|IdentityHashMap
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
name|persistent
operator|.
name|IStoredNode
import|;
end_import

begin_comment
comment|/**  * Global notification service for document updates. Other classes  * can subscribe to this service to be notified of document modifications,  * removals or additions.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|NotificationService
extends|extends
name|IdentityHashMap
argument_list|<
name|UpdateListener
argument_list|,
name|Object
argument_list|>
implements|implements
name|BrokerPoolService
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|3629584664969740903L
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|NotificationService
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|NotificationService
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Subscribe an {@link UpdateListener} to receive notifications. 	 *  	 * @param listener 	 */
specifier|public
specifier|synchronized
name|void
name|subscribe
parameter_list|(
name|UpdateListener
name|listener
parameter_list|)
block|{
name|put
argument_list|(
name|listener
argument_list|,
operator|new
name|Object
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Unsubscribe an {@link UpdateListener}. 	 *  	 * @param listener 	 */
specifier|public
specifier|synchronized
name|void
name|unsubscribe
parameter_list|(
name|UpdateListener
name|listener
parameter_list|)
block|{
specifier|final
name|Object
name|i
init|=
name|remove
argument_list|(
name|listener
argument_list|)
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|hashCode
argument_list|()
operator|+
literal|" listener not found: "
operator|+
name|listener
operator|.
name|hashCode
argument_list|()
argument_list|)
throw|;
block|}
name|listener
operator|.
name|unsubscribe
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Notify all subscribers that a document has been updated/removed or 	 * a new document has been added. 	 *  	 * @param document 	 * @param event 	 */
specifier|public
specifier|synchronized
name|void
name|notifyUpdate
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
block|{
for|for
control|(
specifier|final
name|UpdateListener
name|listener
range|:
name|keySet
argument_list|()
control|)
block|{
name|listener
operator|.
name|documentUpdated
argument_list|(
name|document
argument_list|,
name|event
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Notify all subscribers that a node has been moved. Nodes may be moved during a      * defragmentation run. 	 */
specifier|public
specifier|synchronized
name|void
name|notifyMove
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|IStoredNode
name|newNode
parameter_list|)
block|{
for|for
control|(
specifier|final
name|UpdateListener
name|listener
range|:
name|keySet
argument_list|()
control|)
block|{
name|listener
operator|.
name|nodeMoved
argument_list|(
name|oldNodeId
argument_list|,
name|newNode
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|debug
parameter_list|()
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Registered UpdateListeners:"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|UpdateListener
name|listener
range|:
name|keySet
argument_list|()
control|)
block|{
name|listener
operator|.
name|debug
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

