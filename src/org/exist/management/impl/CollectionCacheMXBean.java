begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|CollectionCache
import|;
end_import

begin_comment
comment|/**  * JMX MXBean interface for examining the CollectionCache  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|CollectionCacheMXBean
extends|extends
name|PerInstanceMBean
block|{
comment|/**      * Get a statistics snapshot of the Collection Cache      *      * @return Statistics for the Collection Cache      */
name|CollectionCache
operator|.
name|Statistics
name|getStatistics
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

