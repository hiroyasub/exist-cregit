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

begin_comment
comment|/**  * Implemented by all objects that should be stored into a cache.  *   * Each object should provide a unique key, an internal reference counter,  * and a timestamp marker (used to measure how long the object has stayed  * in the cache). It depends on the concrete cache implementation if and how  * these fields are used.  *   * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_interface
specifier|public
interface|interface
name|Cacheable
block|{
specifier|public
specifier|final
specifier|static
name|int
name|MAX_REF
init|=
literal|10000
decl_stmt|;
comment|/** 	 * Get a unique key for the object. 	 *  	 * Usually this is the page number. 	 *  	 * @return unique key 	 */
specifier|public
name|long
name|getKey
parameter_list|()
function_decl|;
comment|/** 	 * Get the current reference count. 	 *  	 * @return 	 */
specifier|public
name|int
name|getReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Increase the reference count of this object by one 	 * and return it. 	 *  	 * @return the reference count 	 */
specifier|public
name|int
name|incReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Decrease the reference count of this object by one 	 * and return it. 	 *  	 * @return the reference count 	 */
specifier|public
name|int
name|decReferenceCount
parameter_list|()
function_decl|;
comment|/** 	 * Set the reference count of this object. 	 *  	 * @param count 	 */
specifier|public
name|void
name|setReferenceCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/** 	 * Set the timestamp marker. 	 *  	 * @param timestamp 	 */
specifier|public
name|void
name|setTimestamp
parameter_list|(
name|int
name|timestamp
parameter_list|)
function_decl|;
comment|/** 	 * Get the current timestamp marker. 	 *  	 * @return timestamp marker 	 */
specifier|public
name|int
name|getTimestamp
parameter_list|()
function_decl|;
comment|/** 	 * Called before the object is released by the 	 * cache. The object should prepare to be garbage 	 * collected. All unwritten data should be flushed 	 * to disk. 	 */
specifier|public
name|void
name|sync
parameter_list|()
function_decl|;
comment|/** 	 * Is it safe to unload the Cacheable from the cache? 	 *  	 * Called before an object is actually removed. Return 	 * false to avoid being removed. 	 *  	 * @return 	 */
specifier|public
name|boolean
name|allowUnload
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

