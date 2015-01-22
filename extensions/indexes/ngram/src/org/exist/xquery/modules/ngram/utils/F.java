begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ngram
operator|.
name|utils
package|;
end_package

begin_comment
comment|/**  * A transformation or function from<code>A</code> to<code>B</code>.  */
end_comment

begin_interface
specifier|public
interface|interface
name|F
parameter_list|<
name|A
parameter_list|,
name|B
parameter_list|>
block|{
comment|/**      * Transform<code>A</code> to<code>B</code>.      *       * @param a      *            The<code>A</code> to transform.      * @return The result of the transformation.      */
name|B
name|f
parameter_list|(
name|A
name|a
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

