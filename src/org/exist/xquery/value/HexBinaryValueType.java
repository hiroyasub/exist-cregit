begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
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
name|io
operator|.
name|HexOutputStream
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@existsolutions.com>  */
end_comment

begin_class
specifier|public
class|class
name|HexBinaryValueType
extends|extends
name|BinaryValueType
argument_list|<
name|HexOutputStream
argument_list|>
block|{
specifier|public
name|HexBinaryValueType
parameter_list|()
block|{
name|super
argument_list|(
name|Type
operator|.
name|HEX_BINARY
argument_list|,
name|HexOutputStream
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

