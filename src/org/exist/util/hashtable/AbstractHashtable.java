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
comment|/**  * Abstract base class for all hashtable implementations.  *   * @author Stephan KÃ¶rnig  * @author Wolfgang Meier  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractHashtable
parameter_list|<
name|K
parameter_list|,
name|V
parameter_list|>
extends|extends
name|AbstractHashSet
argument_list|<
name|K
argument_list|>
block|{
comment|/** 	 * Create a new hashtable with default size (1031). 	 */
specifier|protected
name|AbstractHashtable
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
comment|/** 	 * Create a new hashtable using the specified size. 	 *  	 * The actual size will be next prime number following 	 * iSize * 1.5. 	 *  	 * @param iSize 	 */
specifier|protected
name|AbstractHashtable
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
block|}
specifier|public
specifier|abstract
name|Iterator
argument_list|<
name|V
argument_list|>
name|valueIterator
parameter_list|()
function_decl|;
block|}
end_class

end_unit

