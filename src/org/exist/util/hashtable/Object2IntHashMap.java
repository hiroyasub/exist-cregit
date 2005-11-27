begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A hashtable which maps object keys to long values.  *   * Keys are compared by their object equality, i.e. two objects are equal  * if object1.equals(object2).  *   * @author Stephan KÃ¶rnig  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Object2IntHashMap
extends|extends
name|AbstractHashtable
block|{
specifier|public
specifier|static
specifier|final
name|int
name|UNKNOWN_KEY
init|=
operator|-
literal|1
decl_stmt|;
specifier|protected
name|Object
index|[]
name|keys
decl_stmt|;
specifier|protected
name|int
index|[]
name|values
decl_stmt|;
specifier|public
name|Object2IntHashMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|keys
operator|=
operator|new
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|tabSize
index|]
expr_stmt|;
block|}
specifier|public
name|Object2IntHashMap
parameter_list|(
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
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|int
index|[
name|tabSize
index|]
expr_stmt|;
block|}
comment|/** 	 * Puts a new key/value pair into the hashtable. 	 *  	 * If the key does already exist, just the value is updated. 	 *  	 * @param key 	 * @param value 	 */
specifier|public
name|void
name|put
parameter_list|(
name|Object
name|key
parameter_list|,
name|int
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
name|HashtableOverflowException
name|e
parameter_list|)
block|{
name|Object
index|[]
name|copyKeys
init|=
name|keys
decl_stmt|;
name|int
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
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
name|values
operator|=
operator|new
name|int
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
name|copyKeys
index|[
name|k
index|]
operator|!=
literal|null
operator|&&
name|copyKeys
index|[
name|k
index|]
operator|!=
name|REMOVED
condition|)
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
name|int
name|get
parameter_list|(
name|Object
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
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
return|return
name|UNKNOWN_KEY
return|;
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
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
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
name|UNKNOWN_KEY
return|;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|return
name|UNKNOWN_KEY
return|;
block|}
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|Object
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
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
return|return
literal|false
return|;
comment|// key does not exist
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
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
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
comment|// key not found
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|int
name|remove
parameter_list|(
name|Object
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
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
if|if
condition|(
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
name|UNKNOWN_KEY
return|;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|keys
index|[
name|idx
index|]
operator|=
name|REMOVED
expr_stmt|;
operator|--
name|items
expr_stmt|;
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
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
name|keys
index|[
name|idx
index|]
operator|==
literal|null
condition|)
block|{
return|return
name|UNKNOWN_KEY
return|;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|keys
index|[
name|idx
index|]
operator|=
name|REMOVED
expr_stmt|;
operator|--
name|items
expr_stmt|;
return|return
name|values
index|[
name|idx
index|]
return|;
block|}
block|}
return|return
name|UNKNOWN_KEY
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Object2LongIterator
argument_list|(
name|HashtableIterator
operator|.
name|KEYS
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|valueIterator
parameter_list|()
block|{
return|return
operator|new
name|Object2LongIterator
argument_list|(
name|HashtableIterator
operator|.
name|VALUES
argument_list|)
return|;
block|}
specifier|public
name|Iterator
name|stableIterator
parameter_list|()
block|{
return|return
operator|new
name|Object2LongStableIterator
argument_list|(
name|HashtableIterator
operator|.
name|KEYS
argument_list|)
return|;
block|}
specifier|protected
name|void
name|insert
parameter_list|(
name|Object
name|key
parameter_list|,
name|int
name|value
parameter_list|)
throws|throws
name|HashtableOverflowException
block|{
if|if
condition|(
name|key
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal value: null"
argument_list|)
throw|;
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
name|idx
operator|*=
operator|-
literal|1
expr_stmt|;
name|int
name|bucket
init|=
operator|-
literal|1
decl_stmt|;
comment|// look for an empty bucket
if|if
condition|(
name|keys
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
return|return;
block|}
if|else if
condition|(
name|keys
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
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// duplicate value
name|values
index|[
name|idx
index|]
operator|=
name|value
expr_stmt|;
return|return;
block|}
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
name|keys
index|[
name|idx
index|]
operator|==
name|REMOVED
condition|)
block|{
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
return|return;
block|}
if|else if
condition|(
name|keys
index|[
name|idx
index|]
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// duplicate value
name|values
index|[
name|idx
index|]
operator|=
name|value
expr_stmt|;
return|return;
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
return|return;
block|}
throw|throw
operator|new
name|HashtableOverflowException
argument_list|()
throw|;
block|}
specifier|protected
name|int
name|rehash
parameter_list|(
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
name|retVal
operator|=
literal|1
expr_stmt|;
return|return
name|retVal
return|;
block|}
specifier|protected
specifier|final
specifier|static
name|int
name|hash
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
return|return
name|o
operator|.
name|hashCode
argument_list|()
return|;
block|}
specifier|protected
class|class
name|Object2LongIterator
extends|extends
name|HashtableIterator
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|Object2LongIterator
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#hasNext() 		 */
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
return|return
literal|false
return|;
while|while
condition|(
name|keys
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|keys
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#next() 		 */
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|==
name|tabSize
condition|)
return|return
literal|null
return|;
while|while
condition|(
name|keys
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|keys
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
return|return
literal|null
return|;
block|}
if|if
condition|(
name|returnType
operator|==
name|VALUES
condition|)
return|return
operator|new
name|Long
argument_list|(
name|values
index|[
name|idx
operator|++
index|]
argument_list|)
return|;
else|else
return|return
name|keys
index|[
name|idx
operator|++
index|]
return|;
block|}
block|}
specifier|protected
class|class
name|Object2LongStableIterator
extends|extends
name|HashtableIterator
block|{
name|Object
index|[]
name|mKeys
init|=
literal|null
decl_stmt|;
name|int
index|[]
name|mValues
init|=
literal|null
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|Object2LongStableIterator
parameter_list|(
name|int
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|mKeys
operator|=
operator|new
name|Object
index|[
name|tabSize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|keys
argument_list|,
literal|0
argument_list|,
name|mKeys
argument_list|,
literal|0
argument_list|,
name|tabSize
argument_list|)
expr_stmt|;
name|mValues
operator|=
operator|new
name|int
index|[
name|tabSize
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|values
argument_list|,
literal|0
argument_list|,
name|mValues
argument_list|,
literal|0
argument_list|,
name|tabSize
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#hasNext() 		 */
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
return|return
literal|false
return|;
while|while
condition|(
name|mKeys
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|mKeys
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
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#next() 		 */
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|==
name|tabSize
condition|)
return|return
literal|null
return|;
while|while
condition|(
name|mKeys
index|[
name|idx
index|]
operator|==
literal|null
operator|||
name|mKeys
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
return|return
literal|null
return|;
block|}
if|if
condition|(
name|returnType
operator|==
name|VALUES
condition|)
return|return
operator|new
name|Integer
argument_list|(
name|mValues
index|[
name|idx
operator|++
index|]
argument_list|)
return|;
else|else
return|return
name|mKeys
index|[
name|idx
operator|++
index|]
return|;
block|}
block|}
block|}
end_class

end_unit

