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
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * A hashtable which maps int keys to object values.  *   * @author Stephan KÃ¶rnig  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|Int2ObjectHashMap
parameter_list|<
name|V
parameter_list|>
extends|extends
name|AbstractHashtable
argument_list|<
name|Integer
argument_list|,
name|V
argument_list|>
block|{
specifier|protected
name|int
index|[]
name|keys
decl_stmt|;
specifier|protected
name|V
index|[]
name|values
decl_stmt|;
specifier|protected
name|double
name|growthFactor
init|=
literal|1.5
decl_stmt|;
specifier|public
name|Int2ObjectHashMap
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
name|keys
operator|=
operator|new
name|int
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
specifier|public
name|Int2ObjectHashMap
parameter_list|(
name|int
name|iSize
parameter_list|)
block|{
name|this
argument_list|(
name|iSize
argument_list|,
literal|1.5
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Int2ObjectHashMap
parameter_list|(
name|int
name|iSize
parameter_list|,
name|double
name|growth
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
name|int
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
name|growthFactor
operator|=
name|growth
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|items
operator|=
literal|0
expr_stmt|;
name|keys
operator|=
operator|new
name|int
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
comment|/** 	 * Puts a new key/value pair into the hashtable. 	 *  	 * If the key does already exist, just the value is updated. 	 *  	 * @param key 	 * @param value 	 */
specifier|public
name|void
name|put
parameter_list|(
name|int
name|key
parameter_list|,
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
name|HashtableOverflowException
name|e
parameter_list|)
block|{
specifier|final
name|int
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
operator|(
name|int
operator|)
operator|(
name|tabSize
operator|*
name|growthFactor
operator|)
argument_list|)
expr_stmt|;
name|keys
operator|=
operator|new
name|int
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
name|int
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
specifier|public
name|boolean
name|containsKey
parameter_list|(
name|int
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
literal|false
return|;
block|}
comment|// key does not exist
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
literal|false
return|;
block|}
return|return
literal|true
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
literal|false
return|;
block|}
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
name|Object
name|remove
parameter_list|(
name|int
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
comment|//			System.out.println(key + " not found for remove");
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
comment|// key has already been removed
specifier|final
name|Object
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
comment|//				System.out.println(key + " not found for remove");
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
comment|// key has already been removed
specifier|final
name|Object
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
name|Iterator
argument_list|<
name|Integer
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|Int2ObjectIterator
argument_list|(
name|IteratorType
operator|.
name|KEYS
argument_list|)
return|;
block|}
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
name|Int2ObjectIterator
argument_list|(
name|IteratorType
operator|.
name|VALUES
argument_list|)
return|;
block|}
specifier|protected
name|void
name|insert
parameter_list|(
name|int
name|key
parameter_list|,
name|V
name|value
parameter_list|)
throws|throws
name|HashtableOverflowException
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
return|return;
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
name|values
index|[
name|idx
index|]
operator|=
name|value
expr_stmt|;
return|return;
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
name|bucket
operator|=
name|idx
expr_stmt|;
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
return|return;
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
name|boolean
name|hasEqualKeys
parameter_list|(
name|Int2ObjectHashMap
argument_list|<
name|?
argument_list|>
name|other
parameter_list|)
block|{
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
name|containsKey
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
specifier|protected
specifier|static
name|int
name|hash
parameter_list|(
name|int
name|i
parameter_list|)
block|{
return|return
name|i
return|;
block|}
specifier|protected
class|class
name|Int2ObjectIterator
parameter_list|<
name|T
parameter_list|>
extends|extends
name|HashtableIterator
argument_list|<
name|T
argument_list|>
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|Int2ObjectIterator
parameter_list|(
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
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#next() 		 */
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
name|Integer
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
comment|/* (non-Javadoc) 		 * @see org.exist.util.hashtable.AbstractHashtable.HashtableIterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
if|if
condition|(
name|idx
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"remove called before next"
argument_list|)
throw|;
block|}
name|values
index|[
name|idx
operator|-
literal|1
index|]
operator|=
operator|(
name|V
operator|)
name|REMOVED
expr_stmt|;
name|items
operator|--
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

