begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|hashtable
package|;
end_package

begin_comment
comment|/**  * A double-linked hash map additionally providing access to entries in the order in which  * they were added.  *  * If a duplicate entry is added, the old entry is removed from the list and appended to the end. The  * map thus implements a "Last Recently Used" (LRU) behaviour.  */
end_comment

begin_import
import|import
name|net
operator|.
name|jcip
operator|.
name|annotations
operator|.
name|NotThreadSafe
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

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|SequencedLongHashMap
parameter_list|<
name|V
parameter_list|>
extends|extends
name|AbstractHashtable
argument_list|<
name|Long
argument_list|,
name|V
argument_list|>
block|{
comment|/**      * Represents an entry in the map. Each entry      * has a link to the next and previous entries in      * the order in which they were inserted.      *      * @author wolf      */
specifier|public
specifier|final
specifier|static
class|class
name|Entry
parameter_list|<
name|V
parameter_list|>
block|{
specifier|final
name|long
name|key
decl_stmt|;
name|V
name|value
decl_stmt|;
comment|/** points to the next entry in insertion order. */
name|Entry
argument_list|<
name|V
argument_list|>
name|next
init|=
literal|null
decl_stmt|;
comment|/** points to the previous entry in insertion order. */
name|Entry
argument_list|<
name|V
argument_list|>
name|prev
init|=
literal|null
decl_stmt|;
comment|/** points to the prev entry if more than one key maps          * to the same bucket in the table.          */
name|Entry
argument_list|<
name|V
argument_list|>
name|prevDup
init|=
literal|null
decl_stmt|;
comment|/** points to the next entry if more than one key maps          * to the same bucket in the table.          */
name|Entry
argument_list|<
name|V
argument_list|>
name|nextDup
init|=
literal|null
decl_stmt|;
specifier|public
name|Entry
parameter_list|(
specifier|final
name|long
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|)
block|{
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|Entry
argument_list|<
name|V
argument_list|>
name|getNext
parameter_list|()
block|{
return|return
name|next
return|;
block|}
specifier|public
name|long
name|getKey
parameter_list|()
block|{
return|return
name|key
return|;
block|}
specifier|public
name|V
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|Long
operator|.
name|toString
argument_list|(
name|key
argument_list|)
return|;
block|}
block|}
specifier|private
name|long
index|[]
name|keys
decl_stmt|;
specifier|private
name|Entry
argument_list|<
name|V
argument_list|>
index|[]
name|values
decl_stmt|;
comment|/** points to the first entry inserted. */
specifier|private
name|Entry
argument_list|<
name|V
argument_list|>
name|first
init|=
literal|null
decl_stmt|;
comment|/** points to the last inserted entry. */
specifier|private
name|Entry
argument_list|<
name|V
argument_list|>
name|last
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|SequencedLongHashMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|keys
operator|=
operator|new
name|long
index|[
name|tabSize
index|]
expr_stmt|;
name|values
operator|=
operator|(
name|Entry
argument_list|<
name|V
argument_list|>
index|[]
operator|)
operator|new
name|Entry
index|[
name|tabSize
index|]
expr_stmt|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|SequencedLongHashMap
parameter_list|(
specifier|final
name|int
name|iSize
parameter_list|)
block|{
name|super
argument_list|(
name|iSize
argument_list|)
expr_stmt|;
name|keys
operator|=
operator|new
name|long
index|[
name|tabSize
index|]
expr_stmt|;
name|values
operator|=
operator|(
name|Entry
argument_list|<
name|V
argument_list|>
index|[]
operator|)
operator|new
name|Entry
index|[
name|tabSize
index|]
expr_stmt|;
block|}
comment|/**      * Add a new entry for the key.      *      * @param key The key      * @param value The value      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|void
name|put
parameter_list|(
specifier|final
name|long
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|)
block|{
specifier|final
name|Entry
argument_list|<
name|V
argument_list|>
name|entry
init|=
name|insert
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
name|first
operator|=
name|entry
expr_stmt|;
name|last
operator|=
name|first
expr_stmt|;
block|}
else|else
block|{
name|last
operator|.
name|next
operator|=
name|entry
expr_stmt|;
name|entry
operator|.
name|prev
operator|=
name|last
expr_stmt|;
name|last
operator|=
name|entry
expr_stmt|;
block|}
block|}
specifier|protected
name|Entry
name|insert
parameter_list|(
name|long
name|key
parameter_list|,
name|V
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value: null"
argument_list|)
throw|;
block|}
name|int
name|idx
init|=
name|hash
argument_list|(
name|key
argument_list|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
comment|// look for an empty bucket
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
name|keys
index|[
name|idx
index|]
operator|=
name|key
expr_stmt|;
name|values
index|[
name|idx
index|]
operator|=
operator|new
name|Entry
argument_list|<>
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
name|Entry
argument_list|<
name|V
argument_list|>
name|next
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|key
operator|==
name|key
condition|)
block|{
comment|// duplicate value
name|next
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|removeEntry
argument_list|(
name|next
argument_list|)
expr_stmt|;
return|return
name|next
return|;
block|}
name|next
operator|=
name|next
operator|.
name|nextDup
expr_stmt|;
block|}
comment|// add a new entry to the chain
name|next
operator|=
operator|new
name|Entry
argument_list|<>
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
name|next
operator|.
name|nextDup
operator|=
name|values
index|[
name|idx
index|]
expr_stmt|;
name|values
index|[
name|idx
index|]
operator|.
name|prevDup
operator|=
name|next
expr_stmt|;
name|values
index|[
name|idx
index|]
operator|=
name|next
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
name|next
return|;
block|}
comment|/**      * Returns the value for key or null if the key      * is not in the map.      *      * @param key The key to retrieve the value for      *      * @return the value associated with the key, or null if the key is absent      */
specifier|public
name|V
name|get
parameter_list|(
specifier|final
name|long
name|key
parameter_list|)
block|{
name|int
name|idx
init|=
name|hash
argument_list|(
name|key
argument_list|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
comment|// key does not exist
name|Entry
argument_list|<
name|V
argument_list|>
name|next
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|key
operator|==
name|key
condition|)
block|{
return|return
name|next
operator|.
name|value
return|;
block|}
name|next
operator|=
name|next
operator|.
name|nextDup
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns the first entry added to the map.      *      * @return the first entry      */
specifier|public
name|Entry
argument_list|<
name|V
argument_list|>
name|getFirstEntry
parameter_list|()
block|{
return|return
name|first
return|;
block|}
comment|/**      * Remove the entry specified by key from the map.      *      * @param key The key      *      * @return the previous value      */
specifier|public
name|V
name|remove
parameter_list|(
specifier|final
name|long
name|key
parameter_list|)
block|{
specifier|final
name|Entry
argument_list|<
name|V
argument_list|>
name|entry
init|=
name|removeFromHashtable
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|entry
operator|!=
literal|null
condition|)
block|{
name|removeEntry
argument_list|(
name|entry
argument_list|)
expr_stmt|;
return|return
name|entry
operator|.
name|value
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|private
name|Entry
argument_list|<
name|V
argument_list|>
name|removeFromHashtable
parameter_list|(
specifier|final
name|long
name|key
parameter_list|)
block|{
name|int
name|idx
init|=
name|hash
argument_list|(
name|key
argument_list|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
block|}
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
comment|// key does not exist
block|}
name|Entry
argument_list|<
name|V
argument_list|>
name|next
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|key
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|prevDup
operator|==
literal|null
condition|)
block|{
name|values
index|[
name|idx
index|]
operator|=
name|next
operator|.
name|nextDup
expr_stmt|;
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|!=
literal|null
condition|)
block|{
name|values
index|[
name|idx
index|]
operator|.
name|prevDup
operator|=
literal|null
expr_stmt|;
block|}
block|}
else|else
block|{
name|next
operator|.
name|prevDup
operator|.
name|nextDup
operator|=
name|next
operator|.
name|nextDup
expr_stmt|;
if|if
condition|(
name|next
operator|.
name|nextDup
operator|!=
literal|null
condition|)
block|{
name|next
operator|.
name|nextDup
operator|.
name|prevDup
operator|=
name|next
operator|.
name|prevDup
expr_stmt|;
block|}
block|}
operator|--
name|items
expr_stmt|;
return|return
name|next
return|;
block|}
name|next
operator|=
name|next
operator|.
name|nextDup
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Remove the first entry added to the map.      *      * @return the first entry from the map, or null if the map is empty      */
specifier|public
name|Entry
argument_list|<
name|V
argument_list|>
name|removeFirst
parameter_list|()
block|{
if|if
condition|(
name|first
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Entry
argument_list|<
name|V
argument_list|>
name|head
init|=
name|first
decl_stmt|;
name|removeFromHashtable
argument_list|(
name|first
operator|.
name|key
argument_list|)
expr_stmt|;
name|removeEntry
argument_list|(
name|first
argument_list|)
expr_stmt|;
return|return
name|head
return|;
block|}
comment|/**      * Remove an entry.      *      * @param entry The entry to remove      */
specifier|private
name|void
name|removeEntry
parameter_list|(
specifier|final
name|Entry
argument_list|<
name|V
argument_list|>
name|entry
parameter_list|)
block|{
if|if
condition|(
name|entry
operator|.
name|prev
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|entry
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|first
operator|=
literal|null
expr_stmt|;
name|last
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|next
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
name|first
operator|=
name|entry
operator|.
name|next
expr_stmt|;
block|}
block|}
else|else
block|{
name|entry
operator|.
name|prev
operator|.
name|next
operator|=
name|entry
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|entry
operator|.
name|next
operator|==
literal|null
condition|)
block|{
name|last
operator|=
name|entry
operator|.
name|prev
expr_stmt|;
block|}
else|else
block|{
name|entry
operator|.
name|next
operator|.
name|prev
operator|=
name|entry
operator|.
name|prev
expr_stmt|;
block|}
block|}
name|entry
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
name|entry
operator|.
name|next
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Clear the map.      */
specifier|public
name|void
name|clear
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|values
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
block|}
name|items
operator|=
literal|0
expr_stmt|;
name|first
operator|=
literal|null
expr_stmt|;
name|last
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
specifier|static
name|int
name|hash
parameter_list|(
specifier|final
name|long
name|l
parameter_list|)
block|{
return|return
operator|(
name|int
operator|)
operator|(
name|l
operator|^
operator|(
name|l
operator|>>>
literal|32
operator|)
operator|)
return|;
block|}
comment|/**      * Returns an iterator over all keys in the      * order in which they were inserted.      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Long
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|SequencedLongIterator
argument_list|<>
argument_list|(
name|IteratorType
operator|.
name|KEYS
argument_list|)
return|;
block|}
comment|/**      * Returns an iterator over all values in the order      * in which they were inserted.      */
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|V
argument_list|>
name|valueIterator
parameter_list|()
block|{
return|return
operator|new
name|SequencedLongIterator
argument_list|<>
argument_list|(
name|IteratorType
operator|.
name|VALUES
argument_list|)
return|;
block|}
specifier|public
class|class
name|SequencedLongIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|AbstractHashSetIterator
argument_list|<
name|T
argument_list|>
block|{
specifier|private
name|Entry
argument_list|<
name|V
argument_list|>
name|current
decl_stmt|;
specifier|public
name|SequencedLongIterator
parameter_list|(
specifier|final
name|IteratorType
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|current
operator|=
name|first
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|current
operator|!=
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Override
specifier|public
name|T
name|next
parameter_list|()
block|{
if|if
condition|(
name|current
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|Entry
name|next
init|=
name|current
decl_stmt|;
name|current
operator|=
name|current
operator|.
name|next
expr_stmt|;
switch|switch
condition|(
name|returnType
condition|)
block|{
case|case
name|KEYS
case|:
return|return
operator|(
name|T
operator|)
name|Long
operator|.
name|valueOf
argument_list|(
name|next
operator|.
name|key
argument_list|)
return|;
case|case
name|VALUES
case|:
return|return
operator|(
name|T
operator|)
name|next
operator|.
name|value
return|;
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This never happens"
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

