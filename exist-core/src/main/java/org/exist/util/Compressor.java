begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|GZIPInputStream
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
name|GZIPOutputStream
import|;
end_import

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
name|FastByteArrayInputStream
import|;
end_import

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
name|FastByteArrayOutputStream
import|;
end_import

begin_class
specifier|public
class|class
name|Compressor
block|{
comment|/**      * Compress the byte array using GZip compression.      *      * GZip compression has some overhead for headers etc,      * so it does not make sense to use this with perfectly      * compressible buffers smaller than 23 bytes.      * In reality buffers are unlikely to be perfectly compressible,      * so you likely want to only use it with large buffers.      *      * @param buf the data to compress.      *      * @return the compressed data.      *      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
return|return
name|compress
argument_list|(
name|buf
argument_list|,
name|buf
operator|.
name|length
argument_list|)
return|;
block|}
comment|/**      * Compress the byte array using GZip compression.      *      * GZip compression has some overhead for headers etc,      * so it does not make sense to use this with perfectly      * compressible buffers smaller than 23 bytes.      * In reality buffers are unlikely to be perfectly compressible,      * so you likely want to only use it with large buffers.      *      * @param buf the data to compress.      * @param len the number of bytes from buf to compress.      *      * @return the compressed data.      *      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|compress
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
name|len
argument_list|)
init|;
specifier|final
name|GZIPOutputStream
name|gzos
init|=
operator|new
name|GZIPOutputStream
argument_list|(
name|baos
argument_list|)
init|)
block|{
name|gzos
operator|.
name|write
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
argument_list|)
expr_stmt|;
name|gzos
operator|.
name|finish
argument_list|()
expr_stmt|;
return|return
name|baos
operator|.
name|toByteArray
argument_list|()
return|;
block|}
block|}
comment|/**      * Uncompress the byte array using GZip compression.      *      * @param buf the data to uncompress.      *      * @return the uncompressed data.      *      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|byte
index|[]
name|uncompress
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|)
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|uncompress
argument_list|(
name|buf
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
block|}
comment|/**      * Uncompress the byte array using GZip compression.      *      * @param buf the data to uncompress.      * @param os the destination for the uncompressed data;      *      *      * @exception IOException if an error occurs      */
specifier|public
specifier|static
name|int
name|uncompress
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|,
specifier|final
name|OutputStream
name|os
parameter_list|)
throws|throws
name|IOException
block|{
name|int
name|written
init|=
literal|0
decl_stmt|;
try|try
init|(
specifier|final
name|FastByteArrayInputStream
name|bais
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|buf
argument_list|)
init|;
specifier|final
name|InputStream
name|gzis
init|=
operator|new
name|GZIPInputStream
argument_list|(
name|bais
argument_list|)
init|)
block|{
specifier|final
name|byte
index|[]
name|tmp
init|=
operator|new
name|byte
index|[
literal|4096
index|]
decl_stmt|;
name|int
name|read
decl_stmt|;
while|while
condition|(
operator|(
name|read
operator|=
name|gzis
operator|.
name|read
argument_list|(
name|tmp
argument_list|)
operator|)
operator|!=
operator|-
literal|1
condition|)
block|{
name|os
operator|.
name|write
argument_list|(
name|tmp
argument_list|,
literal|0
argument_list|,
name|read
argument_list|)
expr_stmt|;
name|written
operator|+=
name|read
expr_stmt|;
block|}
block|}
return|return
name|written
return|;
block|}
block|}
end_class

end_unit
