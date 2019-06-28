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

begin_comment
comment|/**  * Interface for a Streamable Digest implementation.  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|StreamableDigest
block|{
comment|/**      * Update the message digest calculation with more data.      *      * @param b the data      */
name|void
name|update
parameter_list|(
specifier|final
name|byte
name|b
parameter_list|)
function_decl|;
comment|/**      * Update the message digest calculation with more data.      *      * @param buf the data      * @param offset the position in the {@code buf} to start reading from      * @param len the number of bytes to read from the {@code offset}      */
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
function_decl|;
comment|/**      * Updates the message digest calculation with more data.      *      * @param buf the data      */
specifier|default
name|void
name|update
parameter_list|(
specifier|final
name|byte
index|[]
name|buf
parameter_list|)
block|{
name|update
argument_list|(
name|buf
argument_list|,
literal|0
argument_list|,
name|buf
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
comment|/**      * Gets the type of the message digest      *      * @return the type of the message digest      */
name|DigestType
name|getDigestType
parameter_list|()
function_decl|;
comment|/**      * Gets the current message digest.      *      * NOTE this does not produce a copy of the digest,      * calls to {@link #reset()} or {@code #update} will      * modify the returned value!      *      * @return the message digest      */
name|byte
index|[]
name|getMessageDigest
parameter_list|()
function_decl|;
comment|/**      * Gets the current message digest as a {@code Message Digest}.      *      * The underlying byte array will be copied.      *      * @return a copy of the message digest.      */
name|MessageDigest
name|copyMessageDigest
parameter_list|()
function_decl|;
comment|/**      * Reset the digest function so that it can be reused      * for a new stream.      */
name|void
name|reset
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

