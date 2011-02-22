begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
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
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|cache
operator|.
name|Cache
import|;
end_import

begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-06 The eXist Project *  http://exist-db.org *   *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. *   *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. *   *  You should have received a copy of the GNU Lesser General Public License *  along with this program; if not, write to the Free Software *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA. *   *  $Id$ */
end_comment

begin_interface
specifier|public
interface|interface
name|CacheManager
block|{
specifier|public
specifier|final
specifier|static
name|String
name|BTREE_CACHE
init|=
literal|"BTREE"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|DATA_CACHE
init|=
literal|"DATA"
decl_stmt|;
comment|/**      * Register a cache, i.e. put it under control of      * the cache manager.      *      * @param cache      */
name|void
name|registerCache
parameter_list|(
name|Cache
name|cache
parameter_list|)
function_decl|;
name|void
name|deregisterCache
parameter_list|(
name|Cache
name|cache
parameter_list|)
function_decl|;
comment|/**      * Called by a cache if it wants to grow. The cache manager      * will either deny the request, for example, if there are no spare      * pages left, or calculate a new cache size and call the cache's      * {@link org.exist.storage.cache.Cache#resize(int)} method to resize the cache. The amount      * of pages by which the cache will grow is determined by the cache's      * growthFactor: {@link org.exist.storage.cache.Cache#getGrowthFactor()}.      *      * @param cache      * @return new cache size, or -1 if no free pages available.      */
name|int
name|requestMem
parameter_list|(
name|Cache
name|cache
parameter_list|)
function_decl|;
comment|/**      * Called from the global major sync event to check if caches can      * be shrinked.      *      * If shrinked, the cache will be reset to the default initial cache size.      */
name|void
name|checkCaches
parameter_list|()
function_decl|;
comment|/**      * Called from the global minor sync event to check if a smaller      * cache wants to be resized. If a huge cache is available, the method      * might decide to shrink this cache by a certain amount to make      * room for the smaller cache to grow.      */
name|void
name|checkDistribution
parameter_list|()
function_decl|;
comment|/**      * @return Maximum size of all Caches (unit of measurement is implementation defined)      */
name|long
name|getMaxTotal
parameter_list|()
function_decl|;
comment|/**      * @return Maximum size of a single Cache in bytes (unit of measurement is implementation defined)      */
name|long
name|getMaxSingle
parameter_list|()
function_decl|;
comment|/**      * @return Current size of all Caches in bytes (unit of measurement is implementation defined)      */
name|long
name|getCurrentSize
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

