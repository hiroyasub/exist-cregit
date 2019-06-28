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
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|blob
operator|.
name|BlobId
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
name|crypto
operator|.
name|digest
operator|.
name|DigestType
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
name|crypto
operator|.
name|digest
operator|.
name|MessageDigest
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|BinaryResource
import|;
end_import

begin_comment
comment|/**  * Extensions for Binary Resources in eXist-db.  */
end_comment

begin_interface
specifier|public
interface|interface
name|EXistBinaryResource
extends|extends
name|BinaryResource
block|{
comment|/**      * Get the ID of the BLOB.      *      * @return the id of the BLOB.      *      * @throws XMLDBException if an error occurs retrieving the blobId.      */
name|BlobId
name|getBlobId
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get the length of the binary content.      *      * @return the length of the binary content.      *      * @throws XMLDBException if an error occurs getting the content length.      */
name|long
name|getContentLength
parameter_list|()
throws|throws
name|XMLDBException
function_decl|;
comment|/**      * Get the digest of the BLOB content.      *      * @param digestType the message digest to use.      *      * @return the digest of the BLOB's content      *      * @throws XMLDBException if an error occurs getting the content digest.      */
name|MessageDigest
name|getContentDigest
parameter_list|(
specifier|final
name|DigestType
name|digestType
parameter_list|)
throws|throws
name|XMLDBException
function_decl|;
block|}
end_interface

end_unit

