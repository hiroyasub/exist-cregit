begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|ArrayList
import|;
end_import

begin_comment
comment|/**  * A hash set on objects. Objects are compared for equality by  * calling Object.equals().  *   * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ObjectHashSet
extends|extends
name|AbstractHashtable
block|{
specifier|protected
name|Object
index|[]
name|keys
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|ObjectHashSet
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
block|}
comment|/** 	 * @param iSize 	 */
specifier|public
name|ObjectHashSet
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
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Object
name|key
parameter_list|)
block|{
try|try
block|{
name|insert
argument_list|(
name|key
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
name|copyKeys
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
name|add
argument_list|(
name|copyKeys
index|[
name|k
index|]
argument_list|)
expr_stmt|;
block|}
name|add
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|insert
parameter_list|(
name|Object
name|key
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
specifier|public
name|boolean
name|contains
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
name|Object
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
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
name|keys
index|[
name|idx
index|]
expr_stmt|;
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
name|key
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
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
name|keys
index|[
name|idx
index|]
expr_stmt|;
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
name|key
return|;
block|}
block|}
return|return
literal|null
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
specifier|public
name|List
name|keys
parameter_list|()
block|{
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|(
name|items
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
if|if
condition|(
name|keys
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|keys
index|[
name|i
index|]
operator|!=
name|REMOVED
condition|)
name|list
operator|.
name|add
argument_list|(
name|keys
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
name|list
return|;
block|}
comment|/* (non-Javadoc)       * @see org.exist.util.hashtable.AbstractHashtable#iterator()       */
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ObjectHashSetIterator
argument_list|()
return|;
block|}
specifier|public
name|Iterator
name|stableIterator
parameter_list|()
block|{
return|return
operator|new
name|ObjectHashSetStableIterator
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.util.hashtable.AbstractHashtable#valueIterator() 	 */
specifier|public
name|Iterator
name|valueIterator
parameter_list|()
block|{
return|return
literal|null
return|;
block|}
specifier|protected
class|class
name|ObjectHashSetIterator
implements|implements
name|Iterator
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
specifier|public
name|ObjectHashSetIterator
parameter_list|()
block|{
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
return|return
name|keys
index|[
name|idx
operator|++
index|]
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
block|}
specifier|protected
class|class
name|ObjectHashSetStableIterator
implements|implements
name|Iterator
block|{
name|int
name|idx
init|=
literal|0
decl_stmt|;
name|Object
name|mKeys
index|[]
decl_stmt|;
specifier|public
name|ObjectHashSetStableIterator
parameter_list|()
block|{
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
return|return
name|mKeys
index|[
name|idx
operator|++
index|]
return|;
block|}
comment|/* (non-Javadoc) 		 * @see java.util.Iterator#remove() 		 */
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
block|}
block|}
end_class

end_unit

