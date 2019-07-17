begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Copyright (C) 2018 Adam Retter  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|blob
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|RawDataBackup
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
operator|.
name|LogException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|txn
operator|.
name|Txn
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
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|FileNotFoundException
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

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
import|;
end_import

begin_comment
comment|/**  * Store for BLOBs (Binary Large Objects).  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|BlobStore
extends|extends
name|Closeable
block|{
comment|/**      * Open's the BLOB Store.      *      * Should be called before any other actions.      *      * @throws IOException if the store cannot be opened.      */
name|void
name|open
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Open's the BLOB Store for Recovery after a system crash.      *      * Should ONLY be called from {@link org.exist.storage.recovery.RecoveryManager#recover()}      * when stating up the database after a system crash.      *      * @throws FileNotFoundException if there is no existing blob.dbx to recover!      * @throws IOException if the store cannot be opened.      */
name|void
name|openForRecovery
parameter_list|()
throws|throws
name|FileNotFoundException
throws|,
name|IOException
function_decl|;
comment|/**      * Add a BLOB to the BLOB Store.      *      * @param transaction the current database transaction.      * @param is the input stream containing the blob data.      *      * @return an identifier representing the stored blob and the size of the blob in bytes.      *      * @throws IOException if the BLOB cannot be added.      */
name|Tuple2
argument_list|<
name|BlobId
argument_list|,
name|Long
argument_list|>
name|add
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|InputStream
name|is
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Make a copy of a BLOB in the the BLOB Store.      *      * There is no requirements that an implementation actually make      * a physical copy of a BLOB, so long as it can provide COPY like      * semantics.      *      * This function exists as an optimisation opportunity for      * implementations to avoid having to call {@link #add(Txn, InputStream)}      * with the BLOB data to make a copy.      *      * @param transaction the current database transaction.      * @param blobId the id of the BLOB to copy.      *      * @return an identifier representing the copied blob,      *     or null if there is no such BLOB to copy.      *      * @throws IOException if the BLOB cannot be copied.      */
name|BlobId
name|copy
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a BLOB from the BLOB Store.      *      * @param transaction the current database transaction.      * @param blobId the identifier representing the blob to be retrieved.      *      * @return an InputStream for accessing the BLOB data, or null if there is no such BLOB.      *     NOTE the stream MUST be closed when the caller has finished      *     with it to release any associated resources.      *      * @throws IOException if an error occurs whilst retrieving the BLOB.      */
annotation|@
name|Nullable
name|InputStream
name|get
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get the digest of a BLOB in the BLOB store.      *      * @param transaction the current database transaction.      * @param blobId the identifier representing the blob to be digested.      * @param digestType the type of digest to compute      *      * @return the digest of the BLOB, or null if there is no such BLOB.      *      * @throws IOException if an error occurs whilst computing the digest of the BLOB.      */
annotation|@
name|Nullable
name|MessageDigest
name|getDigest
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|,
specifier|final
name|DigestType
name|digestType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Perform an operation with a {@link Path} reference to a BLOB.      *      * NOTE: Use of this method should be avoided where possible. It only      * exists for integration with tools external to Java which can only      * work with File Paths and where making a copy of the file is not      * necessary.      *      * WARNING: The provided {@link Path} MUST ONLY be used for      * READ operations, any WRITE/DELETE operation will corrupt the      * integrity of the blob store.      *      * Consider if you really need to use this method. It is likely you could      * instead use {@link #get(Txn, BlobId)} and make a copy of the data to      * a temporary file.      *      * Note that any resources associated with the BLOB file      * may not be released until the {@code fnFile} has finished executing.      *      * USE WITH CAUTION!      *      * @param transaction the current database transaction.      * @param blobId the identifier representing the blob to be retrieved.      * @param<T> the type of the return value      * @param fnFile a function which performs a read-only operation on the BLOB file.      *     The Path will be null if the Blob does not exist in the Blob Store.      *     If you wish to handle exceptions in your function you should consider      *     {@link com.evolvedbinary.j8fu.Try} or similar.      *      * @return the result of the {@code fnFile} function.      *      * @throws IOException if an error occurs whilst retrieving the BLOB file.      * @return the result of the {@code fnFile} function      */
parameter_list|<
name|T
parameter_list|>
name|T
name|with
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|,
specifier|final
name|Function
argument_list|<
name|Path
argument_list|,
name|T
argument_list|>
name|fnFile
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Remove a BLOB from the BLOB Store.      *      * @param transaction the current database transaction.      * @param blobId the identifier representing the blob to be removed.      *      * @throws IOException if an error occurs whilst removing the BLOB.      */
name|void
name|remove
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|BlobId
name|blobId
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Backup the Blob Store to the backup.      *      * @param backup the backup to write the Blob Store to.      * @throws IOException if an error occurs whilst creating a backup of the BLOB.      */
name|void
name|backupToArchive
parameter_list|(
specifier|final
name|RawDataBackup
name|backup
parameter_list|)
throws|throws
name|IOException
function_decl|;
name|void
name|redo
parameter_list|(
specifier|final
name|BlobLoggable
name|blobLoggable
parameter_list|)
throws|throws
name|LogException
function_decl|;
name|void
name|undo
parameter_list|(
specifier|final
name|BlobLoggable
name|blobLoggable
parameter_list|)
throws|throws
name|LogException
function_decl|;
block|}
end_interface

end_unit

