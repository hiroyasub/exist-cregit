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
name|util
operator|.
name|function
operator|.
name|Supplier
import|;
end_import

begin_comment
comment|/**  * An enumeration of message digest types  * used by eXist-db.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_enum
specifier|public
enum|enum
name|DigestType
block|{
name|MD_2
argument_list|(
operator|(
name|byte
operator|)
literal|0x01
argument_list|,
literal|128
argument_list|,
name|MD2StreamableDigest
operator|::
operator|new
argument_list|,
literal|"MD2"
argument_list|)
block|,
name|MD_4
argument_list|(
operator|(
name|byte
operator|)
literal|0x02
argument_list|,
literal|128
argument_list|,
name|MD4StreamableDigest
operator|::
operator|new
argument_list|,
literal|"MD4"
argument_list|)
block|,
name|MD_5
argument_list|(
operator|(
name|byte
operator|)
literal|0x03
argument_list|,
literal|128
argument_list|,
name|MD5StreamableDigest
operator|::
operator|new
argument_list|,
literal|"MD5"
argument_list|)
block|,
name|SHA_1
argument_list|(
operator|(
name|byte
operator|)
literal|0x10
argument_list|,
literal|160
argument_list|,
name|SHA1StreamableDigest
operator|::
operator|new
argument_list|,
literal|"SHA-1"
argument_list|)
block|,
name|SHA_256
argument_list|(
operator|(
name|byte
operator|)
literal|0x11
argument_list|,
literal|256
argument_list|,
name|SHA256StreamableDigest
operator|::
operator|new
argument_list|,
literal|"SHA-256"
argument_list|)
block|,
name|SHA_512
argument_list|(
operator|(
name|byte
operator|)
literal|0x12
argument_list|,
literal|512
argument_list|,
name|SHA512StreamableDigest
operator|::
operator|new
argument_list|,
literal|"SHA-512"
argument_list|)
block|,
name|RIPEMD_160
argument_list|(
operator|(
name|byte
operator|)
literal|0x20
argument_list|,
literal|160
argument_list|,
name|RIPEMD160StreamableDigest
operator|::
operator|new
argument_list|,
literal|"RIPEMD-160"
argument_list|,
literal|"RIPEMD160"
argument_list|)
block|,
name|RIPEMD_256
argument_list|(
operator|(
name|byte
operator|)
literal|0x21
argument_list|,
literal|256
argument_list|,
name|RIPEMD256StreamableDigest
operator|::
operator|new
argument_list|,
literal|"RIPEMD-256"
argument_list|,
literal|"RIPEMD256"
argument_list|)
block|,
name|BLAKE_160
argument_list|(
operator|(
name|byte
operator|)
literal|0x30
argument_list|,
literal|160
argument_list|,
name|Blake160StreamableDigest
operator|::
operator|new
argument_list|,
literal|"BLAKE2B-160"
argument_list|,
literal|"BLAKE-160"
argument_list|)
block|,
name|BLAKE_256
argument_list|(
operator|(
name|byte
operator|)
literal|0x31
argument_list|,
literal|256
argument_list|,
name|Blake256StreamableDigest
operator|::
operator|new
argument_list|,
literal|"BLAKE2B-256"
argument_list|,
literal|"BLAKE-256"
argument_list|)
block|,
name|BLAKE_512
argument_list|(
operator|(
name|byte
operator|)
literal|0x31
argument_list|,
literal|512
argument_list|,
name|Blake512StreamableDigest
operator|::
operator|new
argument_list|,
literal|"BLAKE2B-512"
argument_list|,
literal|"BLAKE-512"
argument_list|)
block|;
specifier|private
specifier|final
name|byte
name|id
decl_stmt|;
specifier|private
specifier|final
name|int
name|bits
decl_stmt|;
specifier|private
specifier|final
name|Supplier
argument_list|<
name|StreamableDigest
argument_list|>
name|streamableFactory
decl_stmt|;
specifier|private
specifier|final
name|String
index|[]
name|commonNames
decl_stmt|;
name|DigestType
parameter_list|(
specifier|final
name|byte
name|id
parameter_list|,
specifier|final
name|int
name|bits
parameter_list|,
specifier|final
name|Supplier
argument_list|<
name|StreamableDigest
argument_list|>
name|streamableFactory
parameter_list|,
specifier|final
name|String
modifier|...
name|commonNames
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|bits
operator|=
name|bits
expr_stmt|;
name|this
operator|.
name|streamableFactory
operator|=
name|streamableFactory
expr_stmt|;
name|this
operator|.
name|commonNames
operator|=
name|commonNames
expr_stmt|;
block|}
comment|/**      * Get the id of the message digest.      *      * @return the id of the message digest      */
specifier|public
name|byte
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Get the digest type by id.      *      * @param id the id of the digest type      *      * @return the digest type      *      * @throws IllegalArgumentException if the id is invalid.      */
specifier|public
specifier|static
name|DigestType
name|forId
parameter_list|(
specifier|final
name|byte
name|id
parameter_list|)
block|{
for|for
control|(
specifier|final
name|DigestType
name|digestType
range|:
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|id
operator|==
name|digestType
operator|.
name|getId
argument_list|()
condition|)
block|{
return|return
name|digestType
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown digest type id: "
operator|+
name|id
argument_list|)
throw|;
block|}
comment|/**      * Get the common names for the digest type.      *      * @return the common names.      */
specifier|public
name|String
index|[]
name|getCommonNames
parameter_list|()
block|{
return|return
name|commonNames
return|;
block|}
comment|/**      * Get the digest type by common name.      *      * @param commonName the common name of the digest type      *      * @return the digest type      *      * @throws IllegalArgumentException if the common name is invalid.      */
specifier|public
specifier|static
name|DigestType
name|forCommonName
parameter_list|(
specifier|final
name|String
name|commonName
parameter_list|)
block|{
for|for
control|(
specifier|final
name|DigestType
name|digestType
range|:
name|values
argument_list|()
control|)
block|{
for|for
control|(
specifier|final
name|String
name|cn
range|:
name|digestType
operator|.
name|commonNames
control|)
block|{
if|if
condition|(
name|cn
operator|.
name|equals
argument_list|(
name|commonName
argument_list|)
condition|)
block|{
return|return
name|digestType
return|;
block|}
block|}
block|}
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown digest type common name: "
operator|+
name|commonName
argument_list|)
throw|;
block|}
comment|/***      * The length of the generated message digest      *      * @return the message digest length in bits      */
specifier|public
name|int
name|getDigestLength
parameter_list|()
block|{
return|return
name|bits
return|;
block|}
comment|/***      * The length of the generated message digest      *      * @return the message digest length in bytes      */
specifier|public
name|int
name|getDigestLengthBytes
parameter_list|()
block|{
return|return
name|bits
operator|/
literal|8
return|;
block|}
specifier|public
name|StreamableDigest
name|newStreamableDigest
parameter_list|()
block|{
return|return
name|streamableFactory
operator|.
name|get
argument_list|()
return|;
block|}
block|}
end_enum

end_unit

