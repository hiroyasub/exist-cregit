begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|webdav
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|OutputStream
import|;
end_import

begin_comment
comment|/**  *  * @author wessels  */
end_comment

begin_class
specifier|public
class|class
name|ByteCountOutputStream
extends|extends
name|OutputStream
block|{
specifier|private
name|long
name|nrOfBytes
decl_stmt|;
comment|/**      * @see java.io.OutputStream#write(int)      */
specifier|public
name|void
name|write
parameter_list|(
name|int
name|b
parameter_list|)
throws|throws
name|IOException
block|{
name|nrOfBytes
operator|++
expr_stmt|;
block|}
comment|/**      * @see java.io.OutputStream#write(byte[], int, int)      */
specifier|public
name|void
name|write
parameter_list|(
name|byte
name|b
index|[]
parameter_list|,
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|nrOfBytes
operator|+=
name|len
expr_stmt|;
block|}
comment|/**      * @return Number of bytes written to the stream      */
specifier|public
name|long
name|getByteCount
parameter_list|()
block|{
return|return
name|nrOfBytes
return|;
block|}
block|}
end_class

end_unit

