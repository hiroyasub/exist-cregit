begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2013 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
package|;
end_package

begin_comment
comment|/**  * Forward reference resolution pattern  *   * Delayed object resolution, usually because of cross references,   * Such a reference is not possible to resolve at initialization time,  * because the target is not yet loaded  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Reference
parameter_list|<
name|R
parameter_list|,
name|O
parameter_list|>
block|{
comment|/** 	 * Reference resolver. 	 *  	 * @return resolver 	 */
specifier|public
name|R
name|resolver
parameter_list|()
function_decl|;
comment|/** 	 * Resolve reference and return referent. 	 *   	 * @return referent 	 */
specifier|public
name|O
name|resolve
parameter_list|()
function_decl|;
comment|/**          * Get the name of the reference          */
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

