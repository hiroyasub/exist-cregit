begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|BitSet
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|numbering
operator|.
name|NodeId
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
name|util
operator|.
name|LockException
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
name|hashtable
operator|.
name|Int2ObjectHashMap
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Manages a set of documents.  *   * This class implements the NodeList interface for a collection of documents.  * It also contains methods to retrieve the collections these documents  * belong to.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|DefaultDocumentSet
extends|extends
name|Int2ObjectHashMap
implements|implements
name|MutableDocumentSet
block|{
specifier|private
name|BitSet
name|docIds
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
specifier|private
name|BitSet
name|collectionIds
init|=
operator|new
name|BitSet
argument_list|()
decl_stmt|;
specifier|private
name|TreeSet
argument_list|<
name|Collection
argument_list|>
name|collections
init|=
operator|new
name|TreeSet
argument_list|<
name|Collection
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|DefaultDocumentSet
parameter_list|()
block|{
name|super
argument_list|(
literal|29
argument_list|,
literal|1.75
argument_list|)
expr_stmt|;
block|}
specifier|public
name|DefaultDocumentSet
parameter_list|(
name|int
name|initialSize
parameter_list|)
block|{
name|super
argument_list|(
name|initialSize
argument_list|,
literal|1.75
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|super
operator|.
name|clear
argument_list|()
expr_stmt|;
name|docIds
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
name|collectionIds
operator|=
operator|new
name|BitSet
argument_list|()
expr_stmt|;
name|collections
operator|=
operator|new
name|TreeSet
argument_list|<
name|Collection
argument_list|>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|)
block|{
name|add
argument_list|(
name|doc
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
name|DocumentImpl
name|doc
parameter_list|,
name|boolean
name|checkDuplicates
parameter_list|)
block|{
specifier|final
name|int
name|docId
init|=
name|doc
operator|.
name|getDocId
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkDuplicates
operator|&&
name|contains
argument_list|(
name|docId
argument_list|)
condition|)
block|{
return|return;
block|}
name|docIds
operator|.
name|set
argument_list|(
name|docId
argument_list|)
expr_stmt|;
name|put
argument_list|(
name|docId
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|collection
init|=
name|doc
operator|.
name|getCollection
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|collectionIds
operator|.
name|get
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|collectionIds
operator|.
name|set
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|collections
operator|.
name|add
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
operator|!
operator|(
name|node
operator|instanceof
name|DocumentImpl
operator|)
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"wrong implementation"
argument_list|)
throw|;
block|}
name|add
argument_list|(
operator|(
name|DocumentImpl
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addAll
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|other
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|addCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
block|{
if|if
condition|(
operator|!
name|collectionIds
operator|.
name|get
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
condition|)
block|{
name|collectionIds
operator|.
name|set
argument_list|(
name|collection
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|collections
operator|.
name|add
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|getDocumentIterator
parameter_list|()
block|{
return|return
name|valueIterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|getCollectionIterator
parameter_list|()
block|{
return|return
name|collections
operator|.
name|iterator
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getDocumentCount
parameter_list|()
block|{
return|return
name|size
argument_list|()
return|;
block|}
specifier|public
name|int
name|getCollectionCount
parameter_list|()
block|{
return|return
name|collections
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentImpl
name|getDoc
parameter_list|(
name|int
name|docId
parameter_list|)
block|{
return|return
operator|(
name|DocumentImpl
operator|)
name|get
argument_list|(
name|docId
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|XmldbURI
index|[]
name|getNames
parameter_list|()
block|{
specifier|final
name|XmldbURI
name|result
index|[]
init|=
operator|new
name|XmldbURI
index|[
name|size
argument_list|()
index|]
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|j
operator|++
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|result
index|[
name|j
index|]
operator|=
name|d
operator|.
name|getFileURI
argument_list|()
expr_stmt|;
block|}
name|Arrays
operator|.
name|sort
argument_list|(
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|DocumentSet
name|intersection
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
specifier|final
name|DefaultDocumentSet
name|r
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|other
operator|.
name|contains
argument_list|(
name|d
operator|.
name|getDocId
argument_list|()
argument_list|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|other
operator|.
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|contains
argument_list|(
name|d
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|&&
operator|(
operator|!
name|r
operator|.
name|contains
argument_list|(
name|d
operator|.
name|getDocId
argument_list|()
argument_list|)
operator|)
condition|)
block|{
name|r
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|r
return|;
block|}
specifier|public
name|DocumentSet
name|union
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
specifier|final
name|DefaultDocumentSet
name|result
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|other
argument_list|)
expr_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|contains
argument_list|(
name|d
operator|.
name|getDocId
argument_list|()
argument_list|)
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|d
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|getDocumentCount
argument_list|()
operator|>
name|size
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|tabSize
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
continue|continue;
block|}
specifier|final
name|DocumentImpl
name|d
init|=
operator|(
name|DocumentImpl
operator|)
name|values
index|[
name|idx
index|]
decl_stmt|;
if|if
condition|(
operator|!
name|contains
argument_list|(
name|d
operator|.
name|getDocId
argument_list|()
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|contains
parameter_list|(
name|int
name|id
parameter_list|)
block|{
return|return
name|docIds
operator|.
name|get
argument_list|(
name|id
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|docsToNodeSet
parameter_list|()
block|{
specifier|final
name|NodeSet
name|result
init|=
operator|new
name|NewArrayNodeSet
argument_list|(
name|getDocumentCount
argument_list|()
argument_list|)
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|doc
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|doc
operator|.
name|getResourceType
argument_list|()
operator|==
name|DocumentImpl
operator|.
name|XML_FILE
condition|)
block|{
comment|// skip binary resources
name|result
operator|.
name|add
argument_list|(
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|NodeId
operator|.
name|DOCUMENT_NODE
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|int
name|getMinDocId
parameter_list|()
block|{
name|int
name|min
init|=
name|DocumentImpl
operator|.
name|UNKNOWN_DOCUMENT_ID
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|min
operator|==
name|DocumentImpl
operator|.
name|UNKNOWN_DOCUMENT_ID
condition|)
block|{
name|min
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
block|}
if|else if
condition|(
name|d
operator|.
name|getDocId
argument_list|()
operator|<
name|min
condition|)
block|{
name|min
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|min
return|;
block|}
specifier|public
name|int
name|getMaxDocId
parameter_list|()
block|{
name|int
name|max
init|=
name|DocumentImpl
operator|.
name|UNKNOWN_DOCUMENT_ID
decl_stmt|;
name|DocumentImpl
name|d
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|d
operator|=
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|d
operator|.
name|getDocId
argument_list|()
operator|>
name|max
condition|)
block|{
name|max
operator|=
name|d
operator|.
name|getDocId
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|max
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equalDocs
parameter_list|(
name|DocumentSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
comment|// we are comparing the same objects
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|size
argument_list|()
operator|!=
name|other
operator|.
name|getDocumentCount
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|tabSize
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
continue|continue;
block|}
if|if
condition|(
operator|!
name|other
operator|.
name|contains
argument_list|(
name|keys
index|[
name|idx
index|]
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|lock
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|boolean
name|exclusive
parameter_list|,
name|boolean
name|checkExisting
parameter_list|)
throws|throws
name|LockException
block|{
name|DocumentImpl
name|d
decl_stmt|;
name|Lock
name|dlock
decl_stmt|;
comment|//final Thread thread = Thread.currentThread();
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|tabSize
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
continue|continue;
block|}
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|values
index|[
name|idx
index|]
expr_stmt|;
name|dlock
operator|=
name|d
operator|.
name|getUpdateLock
argument_list|()
expr_stmt|;
comment|//if (checkExisting&& dlock.hasLock(thread))
comment|//continue;
if|if
condition|(
name|exclusive
condition|)
block|{
name|dlock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dlock
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|unlock
parameter_list|(
name|boolean
name|exclusive
parameter_list|)
block|{
name|DocumentImpl
name|d
decl_stmt|;
name|Lock
name|dlock
decl_stmt|;
specifier|final
name|Thread
name|thread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|idx
init|=
literal|0
init|;
name|idx
operator|<
name|tabSize
condition|;
name|idx
operator|++
control|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
continue|continue;
block|}
name|d
operator|=
operator|(
name|DocumentImpl
operator|)
name|values
index|[
name|idx
index|]
expr_stmt|;
name|dlock
operator|=
name|d
operator|.
name|getUpdateLock
argument_list|()
expr_stmt|;
if|if
condition|(
name|exclusive
condition|)
block|{
name|dlock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|dlock
operator|.
name|isLockedForRead
argument_list|(
name|thread
argument_list|)
condition|)
block|{
name|dlock
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
specifier|final
name|Iterator
argument_list|<
name|DocumentImpl
argument_list|>
name|i
init|=
name|getDocumentIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

