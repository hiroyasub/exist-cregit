begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2016 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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

begin_comment
comment|/**  * Implemented by all objects that should be stored into a cache.  *   * Each object should provide a unique key, an internal reference counter,  * and a timestamp marker (used to measure how long the object has stayed  * in the cache). It depends on the concrete cache implementation if and how  * these fields are used.  *   * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cacheable
block|{
name|int
name|MAX_REF
init|=
literal|10000
decl_stmt|;
comment|/** 	 * Get a unique key for the object. 	 *  	 * Usually this is the page number. 	 *  	 * @return unique key 	 */
name|long
name|getKey
parameter_list|()
function_decl|;
comment|/** 	 * Get the current reference count. 	 *  	 * @return The count value.  	 */
name|int
name|getReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Increase the reference count of this object by one 	 * and return it. 	 *  	 * @return the reference count 	 */
name|int
name|incReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Decrease the reference count of this object by one 	 * and return it. 	 *  	 * @return the reference count 	 */
name|int
name|decReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Set the reference count of this object. 	 *  	 * @param count A reference count 	 */
name|void
name|setReferenceCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/** 	 * Set the timestamp marker. 	 *  	 * @param timestamp A timestamp marker 	 */
name|void
name|setTimestamp
parameter_list|(
name|int
name|timestamp
parameter_list|)
function_decl|;
comment|/** 	 * Get the current timestamp marker. 	 *  	 * @return timestamp marker 	 */
name|int
name|getTimestamp
parameter_list|()
function_decl|;
comment|/** 	 * Called before the object is released by the 	 * cache. The object should prepare to be garbage 	 * collected. All unwritten data should be flushed 	 * to disk. 	 */
name|boolean
name|sync
parameter_list|(
name|boolean
name|syncJournal
parameter_list|)
function_decl|;
comment|/** 	 * Is it safe to unload the Cacheable from the cache? 	 *  	 * Called before an object is actually removed. Return 	 * false to avoid being removed. 	 *  	 * @return A boolean where true indicates it can be unloaded. 	 */
name|boolean
name|allowUnload
parameter_list|()
function_decl|;
comment|/** 	 * Indicates whether the cacheable is dirty 	 * 	 * @return true if the cacheable is dirty 	 */
name|boolean
name|isDirty
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

