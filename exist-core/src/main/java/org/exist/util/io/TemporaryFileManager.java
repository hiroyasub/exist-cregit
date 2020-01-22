begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* Copyright (c) 2015, Adam Retter All rights reserved.  Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:     * Redistributions of source code must retain the above copyright       notice, this list of conditions and the following disclaimer.     * Redistributions in binary form must reproduce the above copyright       notice, this list of conditions and the following disclaimer in the       documentation and/or other materials provided with the distribution.     * Neither the name of Adam Retter Consulting nor the       names of its contributors may be used to endorse or promote products       derived from this software without specific prior written permission.  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Adam Retter BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
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
name|nio
operator|.
name|channels
operator|.
name|FileChannel
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
name|Files
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
name|nio
operator|.
name|file
operator|.
name|Paths
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
name|StandardOpenOption
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|FileUtils
import|;
end_import

begin_comment
comment|/**  * Temporary File Manager.  *  * Provides temporary files for use by eXist-db and deals with cleaning them  * up.  *  * Previously when returning a temporary file if it could not be deleted  * (which often occurred on Microsoft Windows) we would add it to a queue  * for reuse the next time a temporary file was required.  *  * On Microsoft Windows platforms this was shown to be unreliable. If the  * temporary file had been Memory Mapped, there would be a lingering open file  * handle which would only be closed when the GC reclaims the ByteBuffer  * objects resulting from the mapping. This exhibited two problems:  *     1. The previously memory mapped file could only be reused for further  *         memory mapped I/O. Any traditional I/O or file system operations  *         (e.g. copy, move, etc.) would result in a  *         java.nio.file.FileSystemException.  *     2. Keeping the previously memory mapped file in a queue, may result in  *     strong indirect references to the ByteBuffer objects meaning that they  *     will never be subject to GC, and therefore the file handles would never  *     be released.  * As such, we now never recycle temporary file objects. Instead we rely on the  * GC to eventually close the file handles of any previously memory mapped files  * and the Operating System to manage it's temporary file space.  *  * Relevant articles on the above described problems are:  *     1.https://bugs.java.com/view_bug.do?bug_id=4715154  *     2. https://bugs.openjdk.java.net/browse/JDK-8028683  *     3. https://bugs.java.com/view_bug.do?bug_id=4724038  *  * @version 2.0  *  * @author<a href="mailto:adam.retter@googlemail.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TemporaryFileManager
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|TemporaryFileManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FOLDER_PREFIX
init|=
literal|"exist-db-temp-file-manager"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|FILE_PREFIX
init|=
literal|"exist-db-temp"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LOCK_FILENAME
init|=
name|FOLDER_PREFIX
operator|+
literal|".lck"
decl_stmt|;
specifier|private
specifier|final
name|Path
name|tmpFolder
decl_stmt|;
specifier|private
specifier|final
name|FileChannel
name|lockChannel
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|TemporaryFileManager
name|instance
init|=
operator|new
name|TemporaryFileManager
argument_list|()
decl_stmt|;
specifier|public
specifier|static
name|TemporaryFileManager
name|getInstance
parameter_list|()
block|{
return|return
name|instance
return|;
block|}
specifier|private
name|TemporaryFileManager
parameter_list|()
block|{
name|cleanupOldTempFolders
argument_list|()
expr_stmt|;
try|try
block|{
name|this
operator|.
name|tmpFolder
operator|=
name|Files
operator|.
name|createTempDirectory
argument_list|(
name|FOLDER_PREFIX
operator|+
literal|'-'
argument_list|)
expr_stmt|;
name|this
operator|.
name|lockChannel
operator|=
name|FileChannel
operator|.
name|open
argument_list|(
name|tmpFolder
operator|.
name|resolve
argument_list|(
name|LOCK_FILENAME
argument_list|)
argument_list|,
name|StandardOpenOption
operator|.
name|CREATE_NEW
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|,
name|StandardOpenOption
operator|.
name|DELETE_ON_CLOSE
argument_list|)
expr_stmt|;
name|lockChannel
operator|.
name|lock
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unable to create temporary folder"
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
comment|/*         Add hook to JVM to delete the file on exit         unfortunately this does not always work on all (e.g. Windows) platforms         will be recovered on restart by cleanupOldTempFolders          */
name|tmpFolder
operator|.
name|toFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|info
argument_list|(
literal|"Temporary folder is: "
operator|+
name|tmpFolder
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|Path
name|getTemporaryFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|tempFile
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|tmpFolder
argument_list|,
name|FILE_PREFIX
operator|+
literal|'-'
argument_list|,
literal|".tmp"
argument_list|)
decl_stmt|;
comment|/*         add hook to JVM to delete the file on exit         unfortunately this does not always work on all (e.g. Windows) platforms          */
name|tempFile
operator|.
name|toFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
return|return
name|tempFile
return|;
block|}
specifier|public
name|void
name|returnTemporaryFile
parameter_list|(
specifier|final
name|Path
name|tempFile
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|tempFile
argument_list|)
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Deleted temporary file: "
operator|+
name|tempFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|// this can often occur on Microsoft Windows (especially if the file was memory mapped!) :-/
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete temporary file: "
operator|+
name|tempFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|" due to: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called at startup to attempt to cleanup      * any left-over temporary folders      * from the last time this was run      */
specifier|private
name|void
name|cleanupOldTempFolders
parameter_list|()
block|{
specifier|final
name|Path
name|tmpDir
init|=
name|Paths
operator|.
name|get
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"java.io.tmpdir"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
for|for
control|(
specifier|final
name|Path
name|dir
range|:
name|FileUtils
operator|.
name|list
argument_list|(
name|tmpDir
argument_list|,
name|path
lambda|->
name|Files
operator|.
name|isDirectory
argument_list|(
name|path
argument_list|)
operator|&&
name|path
operator|.
name|startsWith
argument_list|(
name|FOLDER_PREFIX
argument_list|)
argument_list|)
control|)
block|{
name|final
name|Path
name|lockPath
operator|=
name|dir
operator|.
name|resolve
argument_list|(
name|LOCK_FILENAME
argument_list|)
block|;
if|if
condition|(
operator|!
name|Files
operator|.
name|exists
argument_list|(
name|lockPath
argument_list|)
condition|)
block|{
comment|// no lock file present, so not in use
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// there is a lock file present, we must determine if it is locked (by another eXist-db instance)
try|try
init|(
name|FileChannel
name|otherLockChannel
init|=
name|FileChannel
operator|.
name|open
argument_list|(
name|lockPath
argument_list|,
name|StandardOpenOption
operator|.
name|WRITE
argument_list|)
init|)
block|{
if|if
condition|(
name|otherLockChannel
operator|.
name|tryLock
argument_list|()
operator|!=
literal|null
condition|)
block|{
comment|// not locked... so we now have the lock
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dir
argument_list|)
expr_stmt|;
block|}
block|}
comment|// will release the lock
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|ioe
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Unable to delete old temporary folders"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_function
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
try|try
block|{
name|lockChannel
operator|.
name|close
argument_list|()
expr_stmt|;
comment|// will release the lock on the lock file, and the lock file should be deleted
comment|//try and remove our temporary folder
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tmpFolder
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
block|}
block|}
end_function

unit|}
end_unit

