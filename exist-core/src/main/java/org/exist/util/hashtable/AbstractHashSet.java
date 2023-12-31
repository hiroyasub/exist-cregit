begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/**  * Abstract base class for all hashset implementations.  */
end_comment

begin_class
annotation|@
name|NotThreadSafe
specifier|public
specifier|abstract
class|class
name|AbstractHashSet
parameter_list|<
name|K
parameter_list|>
implements|implements
name|Iterable
argument_list|<
name|K
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|int
name|DEFAULT_SIZE
init|=
literal|1031
decl_stmt|;
comment|// must be a prime number
comment|// marker for removed objects
specifier|protected
specifier|final
specifier|static
name|Object
name|REMOVED
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
specifier|protected
name|int
name|tabSize
decl_stmt|;
specifier|protected
name|int
name|items
decl_stmt|;
specifier|private
name|int
name|maxRehash
init|=
literal|0
decl_stmt|;
comment|/**      * Create a new hashset with default size (1031).      */
name|AbstractHashSet
parameter_list|()
block|{
name|items
operator|=
literal|0
expr_stmt|;
name|tabSize
operator|=
name|DEFAULT_SIZE
expr_stmt|;
block|}
comment|/**      * Create a new hashtable using the specified size.      *      * The actual size will be next prime number following      * iSize * 1.5.      *      * @param iSize Initial size of the hash set      */
specifier|public
name|AbstractHashSet
parameter_list|(
name|int
name|iSize
parameter_list|)
block|{
name|items
operator|=
literal|0
expr_stmt|;
if|if
condition|(
name|iSize
operator|<
literal|1
condition|)
block|{
name|tabSize
operator|=
name|DEFAULT_SIZE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
operator|!
name|isPrime
argument_list|(
name|iSize
argument_list|)
condition|)
block|{
name|iSize
operator|=
operator|(
name|iSize
operator|*
literal|3
operator|)
operator|/
literal|2
expr_stmt|;
name|iSize
operator|=
operator|(
name|int
operator|)
name|nextPrime
argument_list|(
operator|(
name|long
operator|)
name|iSize
argument_list|)
expr_stmt|;
block|}
name|tabSize
operator|=
name|iSize
expr_stmt|;
block|}
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|items
return|;
block|}
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|items
operator|==
literal|0
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isPrime
parameter_list|(
specifier|final
name|long
name|number
parameter_list|)
block|{
if|if
condition|(
name|number
operator|<
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|number
operator|==
literal|2
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|number
operator|%
literal|2
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
if|else if
condition|(
name|number
operator|==
literal|3
condition|)
block|{
return|return
literal|true
return|;
block|}
if|else if
condition|(
name|number
operator|%
literal|3
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
name|int
name|y
init|=
literal|2
decl_stmt|;
specifier|final
name|int
name|x
init|=
operator|(
name|int
operator|)
name|Math
operator|.
name|sqrt
argument_list|(
name|number
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|5
init|;
name|i
operator|<=
name|x
condition|;
name|i
operator|+=
name|y
operator|,
name|y
operator|=
literal|6
operator|-
name|y
control|)
block|{
if|if
condition|(
name|number
operator|%
name|i
operator|==
literal|0
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
specifier|static
name|long
name|nextPrime
parameter_list|(
specifier|final
name|long
name|iVal
parameter_list|)
block|{
name|long
name|retval
init|=
name|iVal
decl_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
operator|++
name|retval
expr_stmt|;
if|if
condition|(
name|isPrime
argument_list|(
name|retval
argument_list|)
condition|)
block|{
return|return
name|retval
return|;
block|}
block|}
block|}
specifier|public
name|int
name|getMaxRehash
parameter_list|()
block|{
return|return
name|maxRehash
return|;
block|}
enum|enum
name|IteratorType
block|{
name|KEYS
block|,
name|VALUES
block|}
specifier|abstract
class|class
name|AbstractHashSetIterator
parameter_list|<
name|T
parameter_list|>
implements|implements
name|Iterator
argument_list|<
name|T
argument_list|>
block|{
specifier|protected
specifier|final
name|IteratorType
name|returnType
decl_stmt|;
name|AbstractHashSetIterator
parameter_list|(
specifier|final
name|IteratorType
name|type
parameter_list|)
block|{
name|this
operator|.
name|returnType
operator|=
name|type
expr_stmt|;
block|}
block|}
specifier|public
specifier|final
specifier|static
class|class
name|HashSetOverflowException
extends|extends
name|Exception
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|4679763007424266920L
decl_stmt|;
specifier|public
name|HashSetOverflowException
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

