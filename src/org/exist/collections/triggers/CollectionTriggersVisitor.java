begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
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
name|collections
operator|.
name|Collection
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
name|xmldb
operator|.
name|XmldbURI
import|;
end_import

begin_comment
comment|/**  *  * @author aretter  */
end_comment

begin_class
specifier|public
class|class
name|CollectionTriggersVisitor
extends|extends
name|AbstractTriggersVisitor
argument_list|<
name|CollectionTrigger
argument_list|,
name|CollectionTriggerProxies
argument_list|>
implements|implements
name|CollectionTrigger
block|{
specifier|protected
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
name|CollectionTriggersVisitor
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|CollectionTriggerProxies
name|proxies
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|proxies
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|configure
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|parent
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
throws|throws
name|TriggerException
block|{
comment|//ignore triggers are already configured by this stage!
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCreateCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|beforeCreateCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCreateCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|afterCreateCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeCopyCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|beforeCopyCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|,
name|newUri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterCopyCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|afterCopyCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|,
name|oldUri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeMoveCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|newUri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|beforeMoveCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|,
name|newUri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterMoveCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|XmldbURI
name|oldUri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|afterMoveCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|,
name|oldUri
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|beforeDeleteCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|beforeDeleteCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|afterDeleteCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|TriggerException
block|{
for|for
control|(
specifier|final
name|CollectionTrigger
name|trigger
range|:
name|getTriggers
argument_list|()
control|)
block|{
name|trigger
operator|.
name|afterDeleteCollection
argument_list|(
name|broker
argument_list|,
name|txn
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

