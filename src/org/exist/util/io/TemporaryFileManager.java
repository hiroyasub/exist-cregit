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
name|util
operator|.
name|Stack
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|UUID
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|Log
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|logging
operator|.
name|LogFactory
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
comment|/**  * Temporary File Manager  *   * Attempts to create and delete temporary files  * working around the issues of some JDK platforms  * (e.g. Windows). Where deleting files is impossible,  * used but finished with temporary files will be re-used  * where possible if they cannot be deleted.  *  * @version 1.0  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|TemporaryFileManager
block|{
specifier|private
specifier|final
specifier|static
name|Log
name|LOG
init|=
name|LogFactory
operator|.
name|getLog
argument_list|(
name|TemporaryFileManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|FOLDER_PREFIX
init|=
literal|"_mmtfm_"
decl_stmt|;
specifier|private
specifier|final
name|Stack
argument_list|<
name|Path
argument_list|>
name|available
init|=
operator|new
name|Stack
argument_list|<>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Path
name|tmpFolder
decl_stmt|;
specifier|private
specifier|final
specifier|static
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
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
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
comment|//add hook to JVM to delete the file on exit
comment|//unfortunately this does not always work on all (e.g. Windows) platforms
comment|//will be recovered on restart by cleanupOldTempFolders
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
name|Path
name|tempFile
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|available
init|)
block|{
if|if
condition|(
operator|!
name|available
operator|.
name|empty
argument_list|()
condition|)
block|{
name|tempFile
operator|=
name|available
operator|.
name|pop
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
name|tempFile
operator|==
literal|null
condition|)
block|{
name|tempFile
operator|=
name|Files
operator|.
name|createTempFile
argument_list|(
name|tmpFolder
argument_list|,
literal|"mmtf_"
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|,
literal|".tmp"
argument_list|)
expr_stmt|;
comment|//add hook to JVM to delete the file on exit
comment|//unfortunately this does not always work on all (e.g. Windows) platforms
name|tempFile
operator|.
name|toFile
argument_list|()
operator|.
name|deleteOnExit
argument_list|()
expr_stmt|;
block|}
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
comment|//attempt to delete the temporary file
name|boolean
name|deleted
init|=
literal|false
decl_stmt|;
try|try
block|{
name|deleted
operator|=
name|Files
operator|.
name|deleteIfExists
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|error
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
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|deleted
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
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Could not delete temporary file: "
operator|+
name|tempFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
operator|+
literal|". Returning to stack for re-use."
argument_list|)
expr_stmt|;
comment|//if we couldn't delete it, add it to the stack of available files
comment|//for reuse in the future.
comment|//Typically there are problems deleting these files on Windows
comment|//platforms which is why this facility was added
synchronized|synchronized
init|(
name|available
init|)
block|{
name|available
operator|.
name|push
argument_list|(
name|tempFile
argument_list|)
expr_stmt|;
block|}
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
name|Files
operator|.
name|list
argument_list|(
name|tmpDir
argument_list|)
operator|.
name|filter
argument_list|(
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
operator|.
name|forEach
argument_list|(
name|FileUtils
operator|::
name|deleteQuietly
argument_list|)
expr_stmt|;
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
name|error
argument_list|(
literal|"Unable to delete old temporary folders"
argument_list|,
name|ioe
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|finalize
parameter_list|()
throws|throws
name|Throwable
block|{
name|super
operator|.
name|finalize
argument_list|()
expr_stmt|;
comment|//remove references to available files
name|available
operator|.
name|clear
argument_list|()
expr_stmt|;
comment|//try and remove our temporary folder
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|tmpFolder
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

