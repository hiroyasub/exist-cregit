begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_class
specifier|public
class|class
name|HexEncoder
block|{
specifier|private
specifier|final
specifier|static
name|char
index|[]
name|HEX_ARRAY
init|=
literal|"0123456789abcdef"
operator|.
name|toCharArray
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|String
name|bytesToHex
parameter_list|(
specifier|final
name|byte
index|[]
name|bytes
parameter_list|)
block|{
specifier|final
name|char
index|[]
name|hexChars
init|=
operator|new
name|char
index|[
name|bytes
operator|.
name|length
operator|*
literal|2
index|]
decl_stmt|;
for|for
control|(
name|int
name|j
init|=
literal|0
init|;
name|j
operator|<
name|bytes
operator|.
name|length
condition|;
name|j
operator|++
control|)
block|{
specifier|final
name|int
name|v
init|=
name|bytes
index|[
name|j
index|]
operator|&
literal|0xFF
decl_stmt|;
name|hexChars
index|[
name|j
operator|*
literal|2
index|]
operator|=
name|HEX_ARRAY
index|[
name|v
operator|>>>
literal|4
index|]
expr_stmt|;
name|hexChars
index|[
name|j
operator|*
literal|2
operator|+
literal|1
index|]
operator|=
name|HEX_ARRAY
index|[
name|v
operator|&
literal|0x0F
index|]
expr_stmt|;
block|}
return|return
operator|new
name|String
argument_list|(
name|hexChars
argument_list|)
return|;
block|}
block|}
end_class

end_unit

