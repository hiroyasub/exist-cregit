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
name|storage
operator|.
name|cache
package|;
end_package

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
name|Long2ObjectHashMap
import|;
end_import

begin_comment
comment|/**  * Simple clock implementation of a cache.  *   * Implements a replacement strategy similar to LRU, but uses a  * single bit flag in each page. On every access to a page, the flag  * is set to 1. If the cache is full, the class iterates through all pages  * in the cache. The first page with flag = 0 is removed. Otherwise, if a page   * with flag = 1 is found, the flag is set to 0. Thus, each page survives  * at least two iterations through the array.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ClockCache
implements|implements
name|Cache
block|{
specifier|protected
name|Long2ObjectHashMap
name|map
decl_stmt|;
specifier|protected
name|Cacheable
index|[]
name|items
decl_stmt|;
specifier|protected
name|int
name|size
decl_stmt|;
specifier|protected
name|int
name|count
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|hits
init|=
literal|0
decl_stmt|,
name|fails
init|=
literal|0
decl_stmt|;
specifier|private
name|long
name|lastSync
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
name|long
name|syncPeriod
init|=
literal|30000
decl_stmt|;
specifier|public
name|ClockCache
parameter_list|(
name|int
name|size
parameter_list|)
block|{
name|this
operator|.
name|size
operator|=
name|size
expr_stmt|;
name|items
operator|=
operator|new
name|Cacheable
index|[
name|size
index|]
expr_stmt|;
name|map
operator|=
operator|new
name|Long2ObjectHashMap
argument_list|(
name|size
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#add(org.exist.storage.cache.Cacheable, int) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|,
name|int
name|initialRefCount
parameter_list|)
block|{
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#add(org.exist.storage.cache.Cacheable) 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|Cacheable
name|old
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|get
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|old
operator|.
name|setReferenceCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|item
operator|.
name|setReferenceCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|count
operator|<
name|size
condition|)
block|{
name|items
index|[
name|count
operator|++
index|]
operator|=
name|item
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|,
name|item
argument_list|)
expr_stmt|;
block|}
else|else
name|removeOne
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
comment|//if(System.currentTimeMillis() - lastSync> syncPeriod)
comment|//	flush();
block|}
specifier|protected
name|Cacheable
name|removeOne
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|int
name|bucket
init|=
operator|-
literal|1
decl_stmt|;
name|Cacheable
name|old
decl_stmt|;
do|do
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
name|count
condition|;
name|i
operator|++
control|)
block|{
name|old
operator|=
name|items
index|[
name|i
index|]
expr_stmt|;
if|if
condition|(
name|old
operator|==
literal|null
condition|)
block|{
name|bucket
operator|=
name|i
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|old
operator|.
name|getReferenceCount
argument_list|()
operator|==
literal|0
operator|&&
name|old
operator|.
name|allowUnload
argument_list|()
condition|)
block|{
if|if
condition|(
name|bucket
operator|<
literal|0
condition|)
name|bucket
operator|=
name|i
expr_stmt|;
block|}
else|else
name|old
operator|.
name|setReferenceCount
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
name|bucket
operator|<
literal|0
condition|)
do|;
name|old
operator|=
name|items
index|[
name|bucket
index|]
expr_stmt|;
if|if
condition|(
name|old
operator|!=
literal|null
condition|)
block|{
name|map
operator|.
name|remove
argument_list|(
name|old
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|old
operator|.
name|sync
argument_list|()
expr_stmt|;
block|}
name|items
index|[
name|bucket
index|]
operator|=
name|item
expr_stmt|;
name|map
operator|.
name|put
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|,
name|item
argument_list|)
expr_stmt|;
return|return
name|old
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#get(org.exist.storage.cache.Cacheable) 	 */
specifier|public
name|Cacheable
name|get
parameter_list|(
name|long
name|key
parameter_list|)
block|{
name|Cacheable
name|item
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|item
operator|==
literal|null
condition|)
block|{
name|fails
operator|++
expr_stmt|;
block|}
else|else
name|hits
operator|++
expr_stmt|;
return|return
name|item
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#get(long) 	 */
specifier|public
name|Cacheable
name|get
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
return|return
name|get
argument_list|(
name|item
operator|.
name|getKey
argument_list|()
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#remove(org.exist.storage.cache.Cacheable) 	 */
specifier|public
name|void
name|remove
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
name|long
name|key
init|=
name|item
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|Cacheable
name|cacheable
init|=
operator|(
name|Cacheable
operator|)
name|map
operator|.
name|remove
argument_list|(
name|key
argument_list|)
decl_stmt|;
if|if
condition|(
name|cacheable
operator|==
literal|null
condition|)
return|return;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|count
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
operator|&&
name|items
index|[
name|i
index|]
operator|.
name|getKey
argument_list|()
operator|==
name|key
condition|)
block|{
name|items
index|[
name|i
index|]
operator|=
literal|null
expr_stmt|;
return|return;
block|}
block|}
name|LOG
operator|.
name|error
argument_list|(
literal|"item not found in list"
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#flush() 	 */
specifier|public
name|void
name|flush
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
name|count
condition|;
name|i
operator|++
control|)
if|if
condition|(
name|items
index|[
name|i
index|]
operator|!=
literal|null
condition|)
name|items
index|[
name|i
index|]
operator|.
name|sync
argument_list|()
expr_stmt|;
name|lastSync
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#getBuffers() 	 */
specifier|public
name|int
name|getBuffers
parameter_list|()
block|{
return|return
name|size
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#getUsedBuffers() 	 */
specifier|public
name|int
name|getUsedBuffers
parameter_list|()
block|{
return|return
name|count
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#getHits() 	 */
specifier|public
name|int
name|getHits
parameter_list|()
block|{
return|return
name|hits
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#getFails() 	 */
specifier|public
name|int
name|getFails
parameter_list|()
block|{
return|return
name|fails
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.storage.cache.Cache#setFileName(java.lang.String) 	 */
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|fileName
parameter_list|)
block|{
block|}
block|}
end_class

end_unit

