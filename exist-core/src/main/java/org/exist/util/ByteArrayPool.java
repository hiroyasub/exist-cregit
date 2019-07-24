begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
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
name|ThreadSafe
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_comment
comment|/**  * A pool for byte arrays.  *  * This pool is primarily used while parsing documents: serializing the  * DOM nodes generates a lot of small byte chunks. Only byte arrays  * with length&lt; MAX are kept in the pool. Large arrays are rarely  * reused.  */
end_comment

begin_class
annotation|@
name|ThreadSafe
specifier|public
class|class
name|ByteArrayPool
block|{
specifier|private
specifier|static
specifier|final
name|int
name|POOL_SIZE
init|=
literal|32
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|MAX
init|=
literal|128
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|ThreadLocal
argument_list|<
name|byte
index|[]
index|[]
argument_list|>
name|pools_
init|=
operator|new
name|PoolThreadLocal
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|AtomicInteger
name|slot_
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
name|ByteArrayPool
parameter_list|()
block|{
block|}
specifier|public
specifier|static
name|byte
index|[]
name|getByteArray
parameter_list|(
specifier|final
name|int
name|size
parameter_list|)
block|{
specifier|final
name|byte
index|[]
index|[]
name|pool
init|=
name|pools_
operator|.
name|get
argument_list|()
decl_stmt|;
if|if
condition|(
name|size
operator|<
name|MAX
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
name|pool
operator|.
name|length
init|;
name|i
operator|--
operator|>
literal|0
condition|;
control|)
block|{
if|if
condition|(
name|pool
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|pool
index|[
name|i
index|]
operator|.
name|length
operator|==
name|size
condition|)
block|{
comment|//System.out.println("found byte[" + size + "]");
specifier|final
name|byte
index|[]
name|b
init|=
name|pool
index|[
name|i
index|]
decl_stmt|;
name|pool
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
return|return
name|b
return|;
block|}
block|}
block|}
return|return
operator|new
name|byte
index|[
name|size
index|]
return|;
block|}
specifier|public
specifier|static
name|void
name|releaseByteArray
parameter_list|(
specifier|final
name|byte
index|[]
name|b
parameter_list|)
block|{
if|if
condition|(
name|b
operator|==
literal|null
operator|||
name|b
operator|.
name|length
operator|>
name|MAX
condition|)
block|{
return|return;
block|}
comment|//System.out.println("releasing byte[" + b.length + "]");
specifier|final
name|byte
index|[]
index|[]
name|pool
init|=
name|pools_
operator|.
name|get
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|pool
operator|.
name|length
init|;
name|i
operator|--
operator|>
literal|0
condition|;
control|)
block|{
if|if
condition|(
name|pool
index|[
name|i
index|]
operator|==
literal|null
condition|)
block|{
name|pool
index|[
name|i
index|]
operator|=
name|b
expr_stmt|;
return|return;
block|}
block|}
name|int
name|s
init|=
name|slot_
operator|.
name|incrementAndGet
argument_list|()
decl_stmt|;
if|if
condition|(
name|s
operator|<
literal|0
condition|)
block|{
name|s
operator|=
operator|-
name|s
expr_stmt|;
block|}
name|pool
index|[
name|s
operator|%
name|pool
operator|.
name|length
index|]
operator|=
name|b
expr_stmt|;
block|}
specifier|private
specifier|static
specifier|final
class|class
name|PoolThreadLocal
extends|extends
name|ThreadLocal
argument_list|<
name|byte
index|[]
index|[]
argument_list|>
block|{
annotation|@
name|Override
specifier|protected
name|byte
index|[]
index|[]
name|initialValue
parameter_list|()
block|{
return|return
operator|new
name|byte
index|[
name|POOL_SIZE
index|]
index|[]
return|;
block|}
block|}
block|}
end_class

end_unit

