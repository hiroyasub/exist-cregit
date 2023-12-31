begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2014 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|sax
operator|.
name|event
package|;
end_package

begin_comment
comment|/**  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|TextEvent
block|{
specifier|public
specifier|final
name|char
name|ch
index|[]
decl_stmt|;
specifier|public
name|TextEvent
parameter_list|(
specifier|final
name|char
name|ch
index|[]
parameter_list|,
specifier|final
name|int
name|start
parameter_list|,
specifier|final
name|int
name|length
parameter_list|)
block|{
name|this
operator|.
name|ch
operator|=
operator|new
name|char
index|[
name|length
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|ch
argument_list|,
name|start
argument_list|,
name|this
operator|.
name|ch
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

