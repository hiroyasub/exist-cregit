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
name|org
operator|.
name|bouncycastle
operator|.
name|crypto
operator|.
name|ExtendedDigest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|bouncycastle
operator|.
name|crypto
operator|.
name|digests
operator|.
name|SHA256Digest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|crypto
operator|.
name|digest
operator|.
name|DigestType
operator|.
name|SHA_256
import|;
end_import

begin_comment
comment|/**  * Implementation of SHA-256 streamable digest.  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|SHA256StreamableDigest
implements|implements
name|StreamableDigest
block|{
specifier|private
specifier|final
name|ExtendedDigest
name|ed
init|=
operator|new
name|SHA256Digest
argument_list|()
decl_stmt|;
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
specifier|final
name|byte
name|b
parameter_list|)
block|{
name|ed
operator|.
name|update
argument_list|(
name|b
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|update
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|,
specifier|final
name|int
name|len
parameter_list|)
block|{
name|ed
operator|.
name|update
argument_list|(
name|buf
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|DigestType
name|getDigestType
parameter_list|()
block|{
return|return
name|SHA_256
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getMessageDigest
parameter_list|()
block|{
specifier|final
name|byte
index|[]
name|digestBytes
init|=
operator|new
name|byte
index|[
name|SHA_256
operator|.
name|getDigestLengthBytes
argument_list|()
index|]
decl_stmt|;
name|ed
operator|.
name|doFinal
argument_list|(
name|digestBytes
argument_list|,
literal|0
argument_list|)
expr_stmt|;
return|return
name|digestBytes
return|;
block|}
annotation|@
name|Override
specifier|public
name|MessageDigest
name|copyMessageDigest
parameter_list|()
block|{
return|return
operator|new
name|MessageDigest
argument_list|(
name|SHA_256
argument_list|,
name|Arrays
operator|.
name|copyOf
argument_list|(
name|getMessageDigest
argument_list|()
argument_list|,
name|SHA_256
operator|.
name|getDigestLengthBytes
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|ed
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

