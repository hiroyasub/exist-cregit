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
operator|.
name|crypto
operator|.
name|digest
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FilterInputStream
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
name|InputStream
import|;
end_import

begin_comment
comment|/**  * An input stream which calculates a digest of the  * data that is read.  *  * @author<a href="mailto:adam@exist-db.org">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|DigestInputStream
extends|extends
name|FilterInputStream
block|{
specifier|private
specifier|final
name|StreamableDigest
name|streamableDigest
decl_stmt|;
comment|/**      * Creates an input stream filter which calculates a digest      * as the underlying input stream is read.      *      * @param is the input stream      * @param streamableDigest the streamable digest      */
specifier|public
name|DigestInputStream
parameter_list|(
specifier|final
name|InputStream
name|is
parameter_list|,
specifier|final
name|StreamableDigest
name|streamableDigest
parameter_list|)
block|{
name|super
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|this
operator|.
name|streamableDigest
operator|=
name|streamableDigest
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|()
throws|throws
name|IOException
block|{
name|int
name|b
init|=
name|in
operator|.
name|read
argument_list|()
decl_stmt|;
if|if
condition|(
name|b
operator|!=
operator|-
literal|1
condition|)
block|{
name|streamableDigest
operator|.
name|update
argument_list|(
operator|(
name|byte
operator|)
operator|(
name|b
operator|&
literal|0xFF
operator|)
argument_list|)
expr_stmt|;
block|}
return|return
name|b
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|read
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|,
specifier|final
name|int
name|off
parameter_list|,
name|int
name|len
parameter_list|)
throws|throws
name|IOException
block|{
name|len
operator|=
name|in
operator|.
name|read
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|!=
operator|-
literal|1
condition|)
block|{
name|streamableDigest
operator|.
name|update
argument_list|(
name|buf
argument_list|,
name|off
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
return|return
name|len
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|skip
parameter_list|(
specifier|final
name|long
name|n
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
name|long
name|total
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|total
operator|<
name|n
condition|)
block|{
name|long
name|len
init|=
name|n
operator|-
name|total
decl_stmt|;
name|len
operator|=
name|read
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|len
operator|<
name|buf
operator|.
name|length
condition|?
operator|(
name|int
operator|)
name|len
else|:
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|==
operator|-
literal|1
condition|)
block|{
return|return
name|total
return|;
block|}
name|total
operator|+=
name|len
expr_stmt|;
block|}
return|return
name|total
return|;
block|}
specifier|public
name|StreamableDigest
name|getStreamableDigest
parameter_list|()
block|{
return|return
name|streamableDigest
return|;
block|}
block|}
end_class

end_unit

