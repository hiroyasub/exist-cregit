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
comment|/**  * Message Digest.  *  * @author Adam Reter<adam@evolvedbinary.com>  */
end_comment

begin_class
specifier|public
class|class
name|MessageDigest
block|{
specifier|private
specifier|final
name|DigestType
name|digestType
decl_stmt|;
specifier|private
specifier|final
name|byte
index|[]
name|value
decl_stmt|;
comment|/**      * @param digestType the type of the message digest      * @param value the message digest value      */
specifier|public
name|MessageDigest
parameter_list|(
specifier|final
name|DigestType
name|digestType
parameter_list|,
specifier|final
name|byte
index|[]
name|value
parameter_list|)
block|{
name|this
operator|.
name|digestType
operator|=
name|digestType
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
comment|/**      * Get the message digest type.      *      * @return the message digest type.      */
specifier|public
name|DigestType
name|getDigestType
parameter_list|()
block|{
return|return
name|digestType
return|;
block|}
comment|/**      * Get the message digest value.      *      * @return the message digest value.      */
specifier|public
name|byte
index|[]
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
block|}
end_class

end_unit

