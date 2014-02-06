begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|collections
operator|.
name|CollectionConfiguration
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|CollectionTriggers
implements|implements
name|CollectionTrigger
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|CollectionTrigger
argument_list|>
name|triggers
decl_stmt|;
specifier|public
name|CollectionTriggers
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|TriggerException
block|{
name|this
argument_list|(
name|broker
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CollectionTriggers
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|TriggerException
block|{
name|this
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|,
name|collection
operator|.
name|getConfiguration
argument_list|(
name|broker
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CollectionTriggers
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|CollectionConfiguration
name|config
parameter_list|)
throws|throws
name|TriggerException
block|{
name|List
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
argument_list|>
name|colTriggers
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|config
operator|!=
literal|null
condition|)
block|{
name|colTriggers
operator|=
name|config
operator|.
name|collectionTriggers
argument_list|()
expr_stmt|;
block|}
name|java
operator|.
name|util
operator|.
name|Collection
argument_list|<
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
argument_list|>
name|masterTriggers
init|=
name|broker
operator|.
name|getDatabase
argument_list|()
operator|.
name|getCollectionTriggers
argument_list|()
decl_stmt|;
name|triggers
operator|=
operator|new
name|ArrayList
argument_list|<
name|CollectionTrigger
argument_list|>
argument_list|(
name|masterTriggers
operator|.
name|size
argument_list|()
operator|+
operator|(
name|colTriggers
operator|==
literal|null
condition|?
literal|0
else|:
name|colTriggers
operator|.
name|size
argument_list|()
operator|)
argument_list|)
expr_stmt|;
for|for
control|(
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
name|colTrigger
range|:
name|masterTriggers
control|)
block|{
name|CollectionTrigger
name|instance
init|=
name|colTrigger
operator|.
name|newInstance
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|register
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|colTriggers
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|TriggerProxy
argument_list|<
name|?
extends|extends
name|CollectionTrigger
argument_list|>
name|colTrigger
range|:
name|colTriggers
control|)
block|{
name|CollectionTrigger
name|instance
init|=
name|colTrigger
operator|.
name|newInstance
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
decl_stmt|;
name|register
argument_list|(
name|instance
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|register
parameter_list|(
name|CollectionTrigger
name|trigger
parameter_list|)
block|{
name|triggers
operator|.
name|add
argument_list|(
name|trigger
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
name|col
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
name|CollectionTrigger
name|trigger
range|:
name|triggers
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
block|{
for|for
control|(
name|CollectionTrigger
name|trigger
range|:
name|triggers
control|)
block|{
try|try
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Trigger
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|CollectionTrigger
name|trigger
range|:
name|triggers
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
block|{
for|for
control|(
name|CollectionTrigger
name|trigger
range|:
name|triggers
control|)
block|{
try|try
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Trigger
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|CollectionTrigger
name|trigger
range|:
name|triggers
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
block|{
for|for
control|(
name|CollectionTrigger
name|trigger
range|:
name|triggers
control|)
block|{
try|try
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Trigger
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
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
name|CollectionTrigger
name|trigger
range|:
name|triggers
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
block|{
for|for
control|(
name|CollectionTrigger
name|trigger
range|:
name|triggers
control|)
block|{
try|try
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
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|Trigger
operator|.
name|LOG
operator|.
name|error
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

