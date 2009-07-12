begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database Copyright (C) 2001-2005, Wolfgang M.  * Meier (meier@ifs.tu-darmstadt.de)  *   * This library is free software; you can redistribute it and/or modify it under  * the terms of the GNU Library General Public License as published by the Free  * Software Foundation; either version 2 of the License, or (at your option) any  * later version.  *   * This library is distributed in the hope that it will be useful, but WITHOUT  * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS  * FOR A PARTICULAR PURPOSE. See the GNU Library General Public License for more  * details.  *   * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software Foundation, Inc.,  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.  *   * $Id$  */
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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipEntry
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|zip
operator|.
name|ZipOutputStream
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|external
operator|.
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|Compressor
block|{
comment|/**      * The method<code>compress</code>      *      * @param whatToCompress a<code>byte[]</code> value      * @return a<code>byte[]</code> value      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|whatToCompress
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compress
argument_list|(
name|whatToCompress
argument_list|,
name|whatToCompress
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * The method<code>compress</code>      *      * @param whatToCompress a<code>byte[]</code> value      * @param length an<code>int</code> value      * @return a<code>byte[]</code> value      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
name|byte
index|[]
name|whatToCompress
parameter_list|,
name|int
name|length
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|ZipOutputStream
name|gzos
init|=
operator|new
name|ZipOutputStream
argument_list|(
name|baos
argument_list|)
decl_stmt|;
name|gzos
operator|.
name|setMethod
argument_list|(
name|ZipOutputStream
operator|.
name|DEFLATED
argument_list|)
expr_stmt|;
name|gzos
operator|.
name|putNextEntry
argument_list|(
operator|new
name|ZipEntry
argument_list|(
name|length
operator|+
literal|""
argument_list|)
argument_list|)
expr_stmt|;
name|gzos
operator|.
name|write
argument_list|(
name|whatToCompress
argument_list|,
literal|0
argument_list|,
name|length
argument_list|)
expr_stmt|;
name|gzos
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|gzos
operator|.
name|finish
argument_list|()
expr_stmt|;
name|gzos
operator|.
name|close
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
comment|/**      * The method<code>uncompress</code>      *      * @param whatToUncompress a<code>byte[]</code> value      * @return a<code>byte[]</code> value      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|uncompress
parameter_list|(
name|byte
index|[]
name|whatToUncompress
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|uncompress
argument_list|(
name|whatToUncompress
argument_list|,
name|baos
argument_list|)
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|void
name|uncompress
parameter_list|(
name|byte
index|[]
name|whatToUncompress
parameter_list|,
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|ByteArrayInputStream
name|bais
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|whatToUncompress
argument_list|)
decl_stmt|;
name|ZipInputStream
name|gzis
init|=
operator|new
name|ZipInputStream
argument_list|(
name|bais
argument_list|)
decl_stmt|;
name|ZipEntry
name|zipentry
init|=
name|gzis
operator|.
name|getNextEntry
argument_list|()
decl_stmt|;
name|Integer
operator|.
name|parseInt
argument_list|(
name|zipentry
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|buf
init|=
operator|new
name|byte
index|[
literal|512
index|]
decl_stmt|;
name|int
name|bread
decl_stmt|;
while|while
condition|(
operator|(
name|bread
operator|=
name|gzis
operator|.
name|read
argument_list|(
name|buf
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
name|os
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|bread
argument_list|)
expr_stmt|;
name|gzis
operator|.
name|closeEntry
argument_list|()
expr_stmt|;
name|gzis
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

