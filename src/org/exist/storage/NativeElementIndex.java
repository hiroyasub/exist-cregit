begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
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
name|Arrays
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
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
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
name|Category
import|;
end_import

begin_import
import|import
name|org
operator|.
name|dbxml
operator|.
name|core
operator|.
name|DBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|dbxml
operator|.
name|core
operator|.
name|data
operator|.
name|Value
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
name|dom
operator|.
name|DocumentSet
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
name|NodeProxy
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
name|NodeSet
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
name|Configuration
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
name|ProgressIndicator
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
name|ReadOnlyException
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
name|VariableByteOutputStream
import|;
end_import

begin_comment
comment|/**  *  ElementIndex collects all element occurrences. It uses the name of the  *  element and the current doc_id as keys and stores all occurrences of this  *  element in a blob. This means that the blob just contains an array of gid's  *  which may be compressed if useCompression is true. Storing all occurrences  *  in one large blob is much faster than storing each of them in a single table  *  row.  *  *@author     Wolfgang Meier (meier@ifs.tu-darmstadt.de)  */
end_comment

begin_class
specifier|public
class|class
name|NativeElementIndex
extends|extends
name|ElementIndex
block|{
specifier|private
specifier|static
name|Category
name|LOG
init|=
name|Category
operator|.
name|getInstance
argument_list|(
name|NativeElementIndex
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|PARTITION_SIZE
init|=
literal|102400
decl_stmt|;
specifier|protected
name|BFile
name|dbElement
decl_stmt|;
specifier|protected
name|int
name|memMinFree
decl_stmt|;
specifier|private
specifier|final
name|Runtime
name|run
init|=
name|Runtime
operator|.
name|getRuntime
argument_list|()
decl_stmt|;
specifier|public
name|NativeElementIndex
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Configuration
name|config
parameter_list|,
name|BFile
name|dbElement
parameter_list|)
block|{
name|super
argument_list|(
name|broker
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|dbElement
operator|=
name|dbElement
expr_stmt|;
if|if
condition|(
operator|(
name|memMinFree
operator|=
name|config
operator|.
name|getInteger
argument_list|(
literal|"db-connection.min_free_memory"
argument_list|)
operator|)
operator|<
literal|0
condition|)
name|memMinFree
operator|=
literal|5000000
expr_stmt|;
block|}
specifier|public
name|void
name|addRow
parameter_list|(
name|String
name|elementName
parameter_list|,
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|ArrayList
name|buf
decl_stmt|;
if|if
condition|(
name|elementIds
operator|.
name|containsKey
argument_list|(
name|elementName
argument_list|)
condition|)
name|buf
operator|=
operator|(
name|ArrayList
operator|)
name|elementIds
operator|.
name|get
argument_list|(
name|elementName
argument_list|)
expr_stmt|;
else|else
block|{
name|buf
operator|=
operator|new
name|ArrayList
argument_list|(
literal|50
argument_list|)
expr_stmt|;
name|elementIds
operator|.
name|put
argument_list|(
name|elementName
argument_list|,
name|buf
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|add
argument_list|(
name|proxy
argument_list|)
expr_stmt|;
name|int
name|percent
init|=
operator|(
name|int
operator|)
operator|(
name|run
operator|.
name|freeMemory
argument_list|()
operator|/
operator|(
name|run
operator|.
name|totalMemory
argument_list|()
operator|/
literal|100
operator|)
operator|)
decl_stmt|;
if|if
condition|(
name|percent
operator|<
name|memMinFree
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"total memory: "
operator|+
name|run
operator|.
name|totalMemory
argument_list|()
operator|+
literal|"; free: "
operator|+
name|run
operator|.
name|freeMemory
argument_list|()
argument_list|)
expr_stmt|;
name|flush
argument_list|()
expr_stmt|;
name|System
operator|.
name|gc
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"total memory: "
operator|+
name|run
operator|.
name|totalMemory
argument_list|()
operator|+
literal|"; free: "
operator|+
name|run
operator|.
name|freeMemory
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|reindex
parameter_list|(
name|DocumentImpl
name|oldDoc
parameter_list|)
block|{
if|if
condition|(
name|elementIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
name|String
name|elementName
decl_stmt|;
name|NodeSet
name|oldList
decl_stmt|;
name|ArrayList
name|newList
decl_stmt|,
name|idList
decl_stmt|;
name|NodeProxy
name|p
decl_stmt|;
name|NodeProxy
name|nodeList
index|[]
decl_stmt|;
name|VariableByteOutputStream
name|os
init|=
operator|new
name|VariableByteOutputStream
argument_list|()
decl_stmt|;
name|int
name|count
init|=
literal|1
decl_stmt|,
name|len
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
name|Value
name|ref
decl_stmt|;
name|Value
name|val
decl_stmt|;
name|long
name|prevId
decl_stmt|,
name|cid
decl_stmt|,
name|addr
decl_stmt|;
name|short
name|collectionId
init|=
name|oldDoc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|elementIds
operator|.
name|entrySet
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
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|idList
operator|=
operator|(
name|ArrayList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|elementName
operator|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|DocumentSet
name|docs
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|oldDoc
argument_list|)
expr_stmt|;
name|oldList
operator|=
name|broker
operator|.
name|findElementsByTagName
argument_list|(
name|docs
argument_list|,
name|elementName
argument_list|)
expr_stmt|;
name|newList
operator|=
operator|new
name|ArrayList
argument_list|(
name|oldList
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|oldList
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|oldDoc
operator|.
name|getTreeLevel
argument_list|(
name|p
operator|.
name|gid
argument_list|)
operator|<
name|oldDoc
operator|.
name|reindexRequired
argument_list|()
condition|)
name|newList
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
name|newList
operator|.
name|addAll
argument_list|(
name|idList
argument_list|)
expr_stmt|;
name|nodeList
operator|=
operator|new
name|NodeProxy
index|[
name|newList
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|nodeList
operator|=
operator|(
name|NodeProxy
index|[]
operator|)
name|newList
operator|.
name|toArray
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
name|len
operator|=
name|nodeList
operator|.
name|length
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|prevId
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|cid
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|gid
operator|-
name|prevId
expr_stmt|;
name|prevId
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|gid
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|cid
argument_list|)
expr_stmt|;
name|addr
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|internalAddress
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|DOMFile
operator|.
name|pageFromPointer
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|DOMFile
operator|.
name|tidFromPointer
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
name|os
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|os
operator|.
name|clear
argument_list|()
expr_stmt|;
name|short
name|sym
init|=
name|NativeBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
name|ref
operator|=
operator|new
name|NativeBroker
operator|.
name|ElementValue
argument_list|(
name|collectionId
argument_list|,
name|sym
argument_list|)
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|this
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|lock
operator|.
name|enter
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dbElement
operator|.
name|put
argument_list|(
name|ref
argument_list|,
operator|new
name|Value
argument_list|(
name|data
argument_list|)
argument_list|)
condition|)
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not save index for element "
operator|+
name|elementName
argument_list|)
expr_stmt|;
continue|continue;
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"could not acquire lock on elements"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|count
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"database is read only"
argument_list|)
expr_stmt|;
block|}
name|elementIds
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|flush
parameter_list|()
block|{
if|if
condition|(
name|elementIds
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return;
specifier|final
name|ProgressIndicator
name|progress
init|=
operator|new
name|ProgressIndicator
argument_list|(
name|elementIds
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|NodeProxy
name|proxy
decl_stmt|;
name|String
name|elementName
decl_stmt|;
name|ArrayList
name|idList
decl_stmt|;
name|int
name|count
init|=
literal|1
decl_stmt|,
name|len
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
name|String
name|name
decl_stmt|;
name|Value
name|ref
decl_stmt|;
name|Value
name|val
decl_stmt|;
name|Map
operator|.
name|Entry
name|entry
decl_stmt|;
name|NodeProxy
name|nodeList
index|[]
decl_stmt|;
name|VariableByteOutputStream
name|os
init|=
operator|new
name|VariableByteOutputStream
argument_list|()
decl_stmt|;
comment|// get collection id for this collection
specifier|final
name|String
name|docName
init|=
name|doc
operator|.
name|getFileName
argument_list|()
decl_stmt|;
name|long
name|prevId
decl_stmt|;
name|long
name|cid
decl_stmt|;
name|long
name|addr
decl_stmt|;
name|short
name|collectionId
init|=
name|doc
operator|.
name|getCollection
argument_list|()
operator|.
name|getId
argument_list|()
decl_stmt|;
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
for|for
control|(
name|Iterator
name|i
init|=
name|elementIds
operator|.
name|entrySet
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
name|entry
operator|=
operator|(
name|Map
operator|.
name|Entry
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|elementName
operator|=
operator|(
name|String
operator|)
name|entry
operator|.
name|getKey
argument_list|()
expr_stmt|;
name|idList
operator|=
operator|(
name|ArrayList
operator|)
name|entry
operator|.
name|getValue
argument_list|()
expr_stmt|;
name|i
operator|.
name|remove
argument_list|()
expr_stmt|;
name|nodeList
operator|=
operator|new
name|NodeProxy
index|[
name|idList
operator|.
name|size
argument_list|()
index|]
expr_stmt|;
name|nodeList
operator|=
operator|(
name|NodeProxy
index|[]
operator|)
name|idList
operator|.
name|toArray
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
name|len
operator|=
name|nodeList
operator|.
name|length
expr_stmt|;
name|Arrays
operator|.
name|sort
argument_list|(
name|nodeList
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|doc
operator|.
name|getDocId
argument_list|()
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|len
argument_list|)
expr_stmt|;
name|prevId
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|len
condition|;
name|j
operator|++
control|)
block|{
name|cid
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|gid
operator|-
name|prevId
expr_stmt|;
name|prevId
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|gid
expr_stmt|;
name|os
operator|.
name|writeLong
argument_list|(
name|cid
argument_list|)
expr_stmt|;
name|addr
operator|=
name|nodeList
index|[
name|j
index|]
operator|.
name|internalAddress
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|DOMFile
operator|.
name|pageFromPointer
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
name|os
operator|.
name|writeInt
argument_list|(
name|DOMFile
operator|.
name|tidFromPointer
argument_list|(
name|addr
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|data
operator|=
name|os
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
name|os
operator|.
name|clear
argument_list|()
expr_stmt|;
name|short
name|sym
init|=
name|NativeBroker
operator|.
name|getSymbols
argument_list|()
operator|.
name|getSymbol
argument_list|(
name|elementName
argument_list|)
decl_stmt|;
name|ref
operator|=
operator|new
name|NativeBroker
operator|.
name|ElementValue
argument_list|(
name|collectionId
argument_list|,
name|sym
argument_list|)
expr_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|this
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|lock
operator|.
name|enter
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|dbElement
operator|.
name|append
argument_list|(
name|ref
argument_list|,
operator|new
name|Value
argument_list|(
name|data
argument_list|)
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not save index for element "
operator|+
name|elementName
argument_list|)
expr_stmt|;
continue|continue;
block|}
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
argument_list|(
literal|"could not acquire lock on elements"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
name|progress
operator|.
name|setValue
argument_list|(
name|count
argument_list|)
expr_stmt|;
name|setChanged
argument_list|()
expr_stmt|;
name|notifyObservers
argument_list|(
name|progress
argument_list|)
expr_stmt|;
name|count
operator|++
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|ReadOnlyException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"database is read-only"
argument_list|)
expr_stmt|;
return|return;
block|}
comment|//elementIds.clear();
name|elementIds
operator|=
operator|new
name|TreeMap
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Value
name|findPartition
parameter_list|(
name|short
name|collectionId
parameter_list|,
name|short
name|symbol
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|NativeBroker
operator|.
name|ElementValue
name|ref
init|=
operator|new
name|NativeBroker
operator|.
name|ElementValue
argument_list|(
name|collectionId
argument_list|,
name|symbol
argument_list|,
operator|(
name|short
operator|)
literal|0
argument_list|)
decl_stmt|;
return|return
name|ref
return|;
comment|//        IndexQuery query =
comment|//                new IndexQuery(null, IndexQuery.TRUNC_RIGHT, ref);
comment|//        synchronized (dbElement) {
comment|//            try {
comment|//                ArrayList partitions = dbElement.findKeys(query);
comment|//                Value next;
comment|//                int size;
comment|//                for(Iterator i = partitions.iterator(); i.hasNext(); ) {
comment|//                    next = (Value) i.next();
comment|//                    size = dbElement.getValueSize( next );
comment|//                    if( size + len< PARTITION_SIZE ) {
comment|//                        return next;
comment|//		    }
comment|//                }
comment|//            } catch(IOException e) {
comment|//                LOG.warn(e);
comment|//            } catch(BTreeException e) {
comment|//                LOG.warn(e);
comment|//            }
comment|//            return null;
comment|//        }
block|}
comment|/**  Description of the Method */
specifier|public
name|void
name|sync
parameter_list|()
block|{
name|Lock
name|lock
init|=
name|dbElement
operator|.
name|getLock
argument_list|()
decl_stmt|;
try|try
block|{
name|lock
operator|.
name|acquire
argument_list|(
name|this
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|lock
operator|.
name|enter
argument_list|(
name|this
argument_list|)
expr_stmt|;
try|try
block|{
name|dbElement
operator|.
name|flush
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|DBException
name|dbe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
name|dbe
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|LockException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"could not acquire lock for elements"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|release
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

