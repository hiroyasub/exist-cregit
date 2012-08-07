begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|triggers
operator|.
name|CollectionTrigger
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
name|dom
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
name|security
operator|.
name|PermissionDeniedException
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
name|lock
operator|.
name|Lock
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
name|CollectionEvents
implements|implements
name|CollectionTrigger
block|{
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
block|}
annotation|@
name|Override
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|Collection
name|newCollection
parameter_list|)
throws|throws
name|TriggerException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Txn
name|txn
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|Collection
name|newCollection
parameter_list|)
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"beforeMoveCollection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Plugin
operator|.
name|_
operator|.
name|md
operator|.
name|moveMetas
argument_list|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|append
argument_list|(
name|doc
operator|.
name|getFileURI
argument_list|()
argument_list|)
argument_list|,
name|newUri
operator|.
name|append
argument_list|(
name|doc
operator|.
name|getFileURI
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|e
argument_list|)
throw|;
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
block|}
specifier|private
name|void
name|deleteCollectionRecursive
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
for|for
control|(
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|collection
operator|.
name|iterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|DocumentImpl
name|doc
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|Plugin
operator|.
name|_
operator|.
name|md
operator|.
name|delMetas
argument_list|(
name|doc
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|final
name|XmldbURI
name|uri
init|=
name|collection
operator|.
name|getURI
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|XmldbURI
argument_list|>
name|i
init|=
name|collection
operator|.
name|collectionIterator
argument_list|(
name|broker
argument_list|)
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|XmldbURI
name|childName
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
comment|//TODO : resolve URIs !!! name.resolve(childName)
specifier|final
name|Collection
name|child
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
operator|.
name|append
argument_list|(
name|childName
argument_list|)
argument_list|,
name|Lock
operator|.
name|NO_LOCK
argument_list|)
decl_stmt|;
if|if
condition|(
name|child
operator|==
literal|null
condition|)
block|{
comment|//                LOG.warn("Child collection " + childName + " not found");
block|}
else|else
block|{
try|try
block|{
name|deleteCollectionRecursive
argument_list|(
name|broker
argument_list|,
name|child
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|child
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|NO_LOCK
argument_list|)
expr_stmt|;
block|}
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"beforeDeleteCollection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|deleteCollectionRecursive
argument_list|(
name|broker
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|TriggerException
argument_list|(
name|e
argument_list|)
throw|;
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
block|}
block|}
end_class

end_unit

