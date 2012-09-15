begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * \$Id\$  */
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
name|SequencedLongHashMap
import|;
end_import

begin_comment
comment|/**  * This cache implementation always tries to keep the inner btree pages in  * cache, while the leaf pages can be removed.  */
end_comment

begin_class
specifier|public
class|class
name|BTreeCache
extends|extends
name|LRUCache
block|{
specifier|public
name|BTreeCache
parameter_list|(
name|int
name|size
parameter_list|,
name|double
name|growthFactor
parameter_list|,
name|double
name|growthThreshold
parameter_list|,
name|String
name|type
parameter_list|)
block|{
name|super
argument_list|(
name|size
argument_list|,
name|growthFactor
argument_list|,
name|growthThreshold
argument_list|,
name|type
argument_list|)
expr_stmt|;
block|}
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
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|)
block|{
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
if|if
condition|(
name|map
operator|.
name|size
argument_list|()
operator|>=
name|max
operator|+
literal|1
condition|)
block|{
name|removeNext
argument_list|(
operator|(
name|BTreeCacheable
operator|)
name|item
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|removeNext
parameter_list|(
name|BTreeCacheable
name|item
parameter_list|)
block|{
name|boolean
name|removed
init|=
literal|false
decl_stmt|;
name|boolean
name|mustRemoveInner
init|=
literal|false
decl_stmt|;
name|SequencedLongHashMap
operator|.
name|Entry
argument_list|<
name|Cacheable
argument_list|>
name|next
init|=
name|map
operator|.
name|getFirstEntry
argument_list|()
decl_stmt|;
do|do
block|{
name|BTreeCacheable
name|cached
init|=
operator|(
name|BTreeCacheable
operator|)
name|next
operator|.
name|getValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|cached
operator|.
name|allowUnload
argument_list|()
operator|&&
name|cached
operator|.
name|getKey
argument_list|()
operator|!=
name|item
operator|.
name|getKey
argument_list|()
operator|&&
operator|(
name|mustRemoveInner
operator|||
operator|!
name|cached
operator|.
name|isInnerPage
argument_list|()
operator|)
condition|)
block|{
name|cached
operator|.
name|sync
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|map
operator|.
name|remove
argument_list|(
name|next
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
name|removed
operator|=
literal|true
expr_stmt|;
block|}
else|else
block|{
name|next
operator|=
name|next
operator|.
name|getNext
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|==
literal|null
condition|)
block|{
name|next
operator|=
name|map
operator|.
name|getFirstEntry
argument_list|()
expr_stmt|;
name|mustRemoveInner
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
do|while
condition|(
operator|!
name|removed
condition|)
do|;
name|accounting
operator|.
name|replacedPage
argument_list|(
name|item
argument_list|)
expr_stmt|;
if|if
condition|(
name|growthFactor
operator|>
literal|1.0
operator|&&
name|accounting
operator|.
name|resizeNeeded
argument_list|()
condition|)
block|{
name|cacheManager
operator|.
name|requestMem
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

