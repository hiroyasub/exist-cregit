begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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

begin_comment
comment|/**  * A hashtable which maps long keys to object values.  *  * @author Stephan KÃ¶rnig  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
class|class
name|Long2ObjectHashMap
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
specifier|private
name|long
index|[]
name|keys
decl_stmt|;
specifier|private
name|V
index|[]
name|values
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|Long2ObjectHashMap
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
name|V
index|[]
operator|)
operator|new
name|Object
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
name|Long2ObjectHashMap
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
name|V
index|[]
operator|)
operator|new
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
block|}
comment|/**      * Puts a new key/value pair into the hashtable.      * If the key does already exist, just the value is updated.      *      * @param key The key      * @param value The value      */
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
try|try
block|{
name|insert
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|HashSetOverflowException
name|e
parameter_list|)
block|{
specifier|final
name|long
index|[]
name|copyKeys
init|=
name|keys
decl_stmt|;
specifier|final
name|V
index|[]
name|copyValues
init|=
name|values
decl_stmt|;
comment|// enlarge the table with a prime value
name|tabSize
operator|=
operator|(
name|int
operator|)
name|nextPrime
argument_list|(
name|tabSize
operator|+
name|tabSize
operator|/
literal|2
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
name|V
index|[]
operator|)
operator|new
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
name|items
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|int
name|k
init|=
literal|0
init|;
name|k
operator|<
name|copyValues
operator|.
name|length
condition|;
name|k
operator|++
control|)
block|{
if|if
condition|(
name|copyValues
index|[
name|k
index|]
operator|!=
literal|null
operator|&&
name|copyValues
index|[
name|k
index|]
operator|!=
name|REMOVED
condition|)
block|{
name|put
argument_list|(
name|copyKeys
index|[
name|k
index|]
argument_list|,
name|copyValues
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
block|}
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|// key does not exist
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
specifier|final
name|int
name|rehashVal
init|=
name|rehash
argument_list|(
name|idx
argument_list|)
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|=
operator|(
name|idx
operator|+
name|rehashVal
operator|)
operator|%
name|tabSize
expr_stmt|;
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
comment|// key not found
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
specifier|public
name|V
name|remove
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
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
return|return
literal|null
return|;
comment|// key has already been removed
block|}
specifier|final
name|V
name|o
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
name|values
index|[
name|idx
index|]
operator|=
operator|(
name|V
operator|)
name|REMOVED
expr_stmt|;
operator|--
name|items
expr_stmt|;
return|return
name|o
return|;
block|}
specifier|final
name|int
name|rehashVal
init|=
name|rehash
argument_list|(
name|idx
argument_list|)
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|=
operator|(
name|idx
operator|+
name|rehashVal
operator|)
operator|%
name|tabSize
expr_stmt|;
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
comment|// key not found
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
return|return
literal|null
return|;
comment|// key has already been removed
block|}
specifier|final
name|V
name|o
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
name|values
index|[
name|idx
index|]
operator|=
operator|(
name|V
operator|)
name|REMOVED
expr_stmt|;
operator|--
name|items
expr_stmt|;
return|return
name|o
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
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
name|values
operator|.
name|length
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
block|}
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
name|Long2ObjectIterator
argument_list|<>
argument_list|(
name|IteratorType
operator|.
name|KEYS
argument_list|)
return|;
block|}
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
name|Long2ObjectIterator
argument_list|<>
argument_list|(
name|IteratorType
operator|.
name|VALUES
argument_list|)
return|;
block|}
specifier|private
name|V
name|insert
parameter_list|(
specifier|final
name|long
name|key
parameter_list|,
specifier|final
name|V
name|value
parameter_list|)
throws|throws
name|HashSetOverflowException
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
name|int
name|bucket
init|=
operator|-
literal|1
decl_stmt|;
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
name|value
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
comment|// remember the bucket, but continue to check
comment|// for duplicate keys
name|bucket
operator|=
name|idx
expr_stmt|;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
comment|// duplicate value
specifier|final
name|V
name|dup
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
name|values
index|[
name|idx
index|]
operator|=
name|value
expr_stmt|;
return|return
name|dup
return|;
block|}
specifier|final
name|int
name|rehashVal
init|=
name|rehash
argument_list|(
name|idx
argument_list|)
decl_stmt|;
name|int
name|rehashCnt
init|=
literal|1
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
name|tabSize
condition|;
name|i
operator|++
control|)
block|{
name|idx
operator|=
operator|(
name|idx
operator|+
name|rehashVal
operator|)
operator|%
name|tabSize
expr_stmt|;
if|if
condition|(
name|values
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
if|if
condition|(
name|bucket
operator|==
operator|-
literal|1
condition|)
block|{
name|bucket
operator|=
name|idx
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|values
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|bucket
operator|>
operator|-
literal|1
condition|)
block|{
comment|// store key into the empty bucket first found
name|idx
operator|=
name|bucket
expr_stmt|;
block|}
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
name|value
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
literal|null
return|;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
name|key
condition|)
block|{
comment|// duplicate value
specifier|final
name|V
name|dup
init|=
name|values
index|[
name|idx
index|]
decl_stmt|;
name|values
index|[
name|idx
index|]
operator|=
name|value
expr_stmt|;
return|return
name|dup
return|;
block|}
operator|++
name|rehashCnt
expr_stmt|;
block|}
comment|// should never happen, but just to be sure:
comment|// if the key has not been inserted yet, do it now
if|if
condition|(
name|bucket
operator|>
operator|-
literal|1
condition|)
block|{
name|keys
index|[
name|bucket
index|]
operator|=
name|key
expr_stmt|;
name|values
index|[
name|bucket
index|]
operator|=
name|value
expr_stmt|;
operator|++
name|items
expr_stmt|;
return|return
literal|null
return|;
block|}
throw|throw
operator|new
name|HashSetOverflowException
argument_list|()
throw|;
block|}
specifier|private
name|int
name|rehash
parameter_list|(
specifier|final
name|int
name|iVal
parameter_list|)
block|{
name|int
name|retVal
init|=
operator|(
name|iVal
operator|+
name|iVal
operator|/
literal|2
operator|)
operator|%
name|tabSize
decl_stmt|;
if|if
condition|(
name|retVal
operator|==
literal|0
condition|)
block|{
name|retVal
operator|=
literal|1
expr_stmt|;
block|}
return|return
name|retVal
return|;
block|}
specifier|private
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
specifier|public
class|class
name|Long2ObjectIterator
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
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|Long2ObjectIterator
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
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|==
name|tabSize
condition|)
block|{
return|return
literal|false
return|;
block|}
while|while
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
operator|++
name|idx
expr_stmt|;
if|if
condition|(
name|idx
operator|==
name|tabSize
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
name|idx
operator|==
name|tabSize
condition|)
block|{
return|return
literal|null
return|;
block|}
while|while
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
operator|++
name|idx
expr_stmt|;
if|if
condition|(
name|idx
operator|==
name|tabSize
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
switch|switch
condition|(
name|returnType
condition|)
block|{
case|case
name|VALUES
case|:
return|return
operator|(
name|T
operator|)
name|values
index|[
name|idx
operator|++
index|]
return|;
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
name|keys
index|[
name|idx
operator|++
index|]
argument_list|)
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

