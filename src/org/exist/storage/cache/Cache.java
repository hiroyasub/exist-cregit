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
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

begin_comment
comment|/**  * Base interface for all cache implementations that are used for  * buffering btree and data pages.  *   * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cache
block|{
comment|/** 	 * Add the item to the cache. If it is already in the cache, 	 * update the references. 	 *  	 * @param item 	 */
specifier|public
name|void
name|add
parameter_list|(
name|Cacheable
name|item
parameter_list|)
function_decl|;
comment|/** 	 * Add the item to the cache. If it is already in the cache, 	 * update the references. 	 *  	 * @param item 	 * @param initialRefCount the initial reference count for the item 	 */
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
function_decl|;
comment|/** 	 * Retrieve an item from the cache. 	 *  	 * @param item 	 * @return the item in the cache or null if it does not exist. 	 */
specifier|public
name|Cacheable
name|get
parameter_list|(
name|Cacheable
name|item
parameter_list|)
function_decl|;
comment|/** 	 * Retrieve an item by its key. 	 *  	 * @param key a unique key, usually the page number 	 * @return the item in the cache or null if it does not exist. 	 */
specifier|public
name|Cacheable
name|get
parameter_list|(
name|long
name|key
parameter_list|)
function_decl|;
comment|/** 	 * Remove an item from the cache. 	 *  	 * @param item 	 */
specifier|public
name|void
name|remove
parameter_list|(
name|Cacheable
name|item
parameter_list|)
function_decl|;
comment|/** 	 * Call release on all items, but without 	 * actually removing them from the cache. 	 *  	 * This gives the items a chance to write all 	 * unwritten data to disk. 	 */
specifier|public
name|void
name|flush
parameter_list|()
function_decl|;
comment|/** 	 * Get the size of this cache. 	 *  	 * @return 	 */
specifier|public
name|int
name|getBuffers
parameter_list|()
function_decl|;
comment|/** 	 * Get the number of buffers currently used. 	 *  	 * @return 	 */
specifier|public
name|int
name|getUsedBuffers
parameter_list|()
function_decl|;
comment|/** 	 * Get the number of times where an object has been successfully 	 * loaded from the cache. 	 * @return 	 */
specifier|public
name|int
name|getHits
parameter_list|()
function_decl|;
comment|/** 	 * Get the number of times where an object could not be 	 * found in the cache. 	 *  	 * @return 	 */
specifier|public
name|int
name|getFails
parameter_list|()
function_decl|;
specifier|public
name|void
name|setFileName
parameter_list|(
name|String
name|fileName
parameter_list|)
function_decl|;
specifier|public
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Cache
operator|.
name|class
argument_list|)
decl_stmt|;
block|}
end_interface

end_unit

