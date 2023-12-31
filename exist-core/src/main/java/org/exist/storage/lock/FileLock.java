begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2006-2007 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

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
name|storage
operator|.
name|BrokerPool
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|ReadOnlyException
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
name|nio
operator|.
name|ByteBuffer
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
name|SeekableByteChannel
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
name|text
operator|.
name|DateFormat
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
import|import
name|java
operator|.
name|util
operator|.
name|Date
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|READ
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|SYNC
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|StandardOpenOption
operator|.
name|WRITE
import|;
end_import

begin_comment
comment|/**  * Cooperative inter-process file locking, used to synchronize access to database files across  * processes, i.e. across different Java VMs or separate database instances within one  * VM. This is similar to the native file locks provided by Java NIO. However, the NIO  * implementation has various problems. Among other things, we observed that locks  * were not properly released on WinXP.  *   * FileLock implements a cooperative approach. The class attempts to write a lock file  * at the specified location. Every lock file stores 1) a magic word to make sure that the  * file was really written by eXist, 2) a heartbeat timestamp. The procedure for acquiring the  * lock in {@link #tryLock()} is as follows:  *   * If a lock file does already exist in the specified location, we check its heartbeat timestamp.  * If the timestamp is more than {@link #HEARTBEAT} milliseconds in the past, we assume  * that the lock is stale and its owner process has died. The lock file is removed and we create  * a new one.  *   * If the heartbeat indicates that the owner process is still alive, the lock  * attempt is aborted and {@link #tryLock()} returns false.  *   * Otherwise, we create a new lock file and start a daemon thread to periodically update  * the lock file's heart-beat value.  *   * @author Wolfgang Meier  *   */
end_comment

begin_class
specifier|public
class|class
name|FileLock
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
name|FileLock
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/** The heartbeat period in milliseconds */
specifier|private
specifier|final
name|long
name|HEARTBEAT
init|=
literal|10100
decl_stmt|;
comment|/** Magic word to be written to the start of the lock file */
specifier|private
specifier|final
specifier|static
name|byte
index|[]
name|MAGIC
init|=
block|{
literal|0x65
block|,
literal|0x58
block|,
literal|0x69
block|,
literal|0x73
block|,
literal|0x74
block|,
literal|0x2D
block|,
literal|0x64
block|,
literal|0x62
block|}
decl_stmt|;
comment|// "eXist-db"
comment|/** BrokerPool provides access the SyncDaemon */
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
comment|/** The lock file */
specifier|private
name|Path
name|lockFile
decl_stmt|;
comment|/** An open channel to the lock file */
specifier|private
name|SeekableByteChannel
name|channel
init|=
literal|null
decl_stmt|;
comment|/** Temporary buffer used for writing */
specifier|private
specifier|final
name|ByteBuffer
name|buf
init|=
name|ByteBuffer
operator|.
name|allocate
argument_list|(
name|MAGIC
operator|.
name|length
operator|+
literal|8
argument_list|)
decl_stmt|;
comment|/** The time (in milliseconds) of the last heartbeat written to the lock file */
specifier|private
name|long
name|lastHeartbeat
init|=
operator|-
literal|1L
decl_stmt|;
specifier|public
name|FileLock
parameter_list|(
specifier|final
name|BrokerPool
name|pool
parameter_list|,
specifier|final
name|Path
name|path
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|lockFile
operator|=
name|path
expr_stmt|;
block|}
comment|/**      * Attempt to create the lock file and thus acquire a lock.      *       * @return false if another process holds the lock      * @throws ReadOnlyException if the lock file could not be created or saved      * due to IO errors. The caller may want to switch to read-only mode.      */
specifier|public
name|boolean
name|tryLock
parameter_list|()
throws|throws
name|ReadOnlyException
block|{
name|int
name|attempt
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|lockFile
argument_list|)
condition|)
block|{
if|if
condition|(
operator|++
name|attempt
operator|>
literal|2
condition|)
block|{
return|return
literal|false
return|;
block|}
try|try
block|{
name|read
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
name|message
argument_list|(
literal|"Failed to read lock file"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
comment|//Check if there's a heart-beat. If not, remove the stale .lck file and try again
if|if
condition|(
name|checkHeartbeat
argument_list|()
condition|)
block|{
comment|//There seems to be a heart-beat...
comment|//Sometimes Java does not properly delete files, so we may have an old
comment|//lock file from a previous db run, which has not timed out yet. We thus
comment|//give the db a second chance and wait for HEARTBEAT + 100 milliseconds
comment|//before we check the heart-beat a second time.
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|message
argument_list|(
literal|"Waiting a short time for the lock to be released..."
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|wait
argument_list|(
name|HEARTBEAT
operator|+
literal|100
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
block|}
try|try
block|{
comment|//Close the open channel, so it can be read again
if|if
condition|(
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|channel
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
comment|//Nothing to do
block|}
block|}
block|}
try|try
block|{
name|this
operator|.
name|lockFile
operator|=
name|Files
operator|.
name|createFile
argument_list|(
name|lockFile
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
throw|throw
operator|new
name|ReadOnlyException
argument_list|(
name|message
argument_list|(
literal|"Could not create lock file"
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
try|try
block|{
name|save
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|ReadOnlyException
argument_list|(
name|message
argument_list|(
literal|"Caught exception while trying to write lock file"
argument_list|,
name|e
argument_list|)
argument_list|)
throw|;
block|}
comment|//Schedule the heart-beat for the file lock
specifier|final
name|Properties
name|params
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|params
operator|.
name|put
argument_list|(
name|FileLock
operator|.
name|class
operator|.
name|getName
argument_list|()
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getScheduler
argument_list|()
operator|.
name|createPeriodicJob
argument_list|(
name|HEARTBEAT
argument_list|,
operator|new
name|FileLockHeartBeat
argument_list|(
name|lockFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|,
operator|-
literal|1
argument_list|,
name|params
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
comment|/**      * Release the lock. Removes the lock file and closes all      * open channels.      */
specifier|public
name|void
name|release
parameter_list|()
block|{
try|try
block|{
if|if
condition|(
name|channel
operator|.
name|isOpen
argument_list|()
condition|)
block|{
name|channel
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|channel
operator|=
literal|null
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Exception
name|e
parameter_list|)
block|{
name|message
argument_list|(
literal|"Failed to close lock file"
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|Files
operator|.
name|exists
argument_list|(
name|lockFile
argument_list|)
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Deleting lock file: "
operator|+
name|lockFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|lockFile
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns the last heartbeat written to the lock file.      *       * @return last heartbeat      */
specifier|public
name|Date
name|getLastHeartbeat
parameter_list|()
block|{
return|return
operator|new
name|Date
argument_list|(
name|lastHeartbeat
argument_list|)
return|;
block|}
comment|/**      * Returns the lock file that represents the active lock held by      * the FileLock.      *       * @return lock file      */
specifier|public
name|Path
name|getFile
parameter_list|()
block|{
return|return
name|lockFile
return|;
block|}
comment|/**      * Check if the lock has an active heartbeat, i.e. if it was updated      * during the past {@link #HEARTBEAT} milliseconds.      *       * @return true if there's an active heartbeat      */
specifier|private
name|boolean
name|checkHeartbeat
parameter_list|()
block|{
specifier|final
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|lastHeartbeat
operator|<
literal|0
operator|||
name|now
operator|-
name|lastHeartbeat
operator|>
name|HEARTBEAT
condition|)
block|{
name|message
argument_list|(
literal|"Found a stale lockfile. Trying to remove it: "
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|release
argument_list|()
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|void
name|open
parameter_list|()
throws|throws
name|IOException
block|{
name|this
operator|.
name|channel
operator|=
name|Files
operator|.
name|newByteChannel
argument_list|(
name|lockFile
argument_list|,
name|READ
argument_list|,
name|WRITE
argument_list|,
name|SYNC
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|save
parameter_list|()
throws|throws
name|IOException
block|{
try|try
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
name|open
argument_list|()
expr_stmt|;
block|}
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
name|buf
operator|.
name|put
argument_list|(
name|MAGIC
argument_list|)
expr_stmt|;
name|buf
operator|.
name|putLong
argument_list|(
name|now
argument_list|)
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
name|channel
operator|.
name|position
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|channel
operator|.
name|write
argument_list|(
name|buf
argument_list|)
expr_stmt|;
comment|//channel.force(true); //handled by SYNC on open option
name|lastHeartbeat
operator|=
name|now
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NullPointerException
name|npe
parameter_list|)
block|{
if|if
condition|(
name|pool
operator|.
name|isShuttingDown
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"No need to save FileLock, database is shutting down"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|npe
throw|;
block|}
block|}
block|}
specifier|private
name|void
name|read
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|channel
operator|==
literal|null
condition|)
block|{
name|open
argument_list|()
expr_stmt|;
block|}
name|channel
operator|.
name|read
argument_list|(
name|buf
argument_list|)
expr_stmt|;
name|buf
operator|.
name|flip
argument_list|()
expr_stmt|;
if|if
condition|(
name|buf
operator|.
name|limit
argument_list|()
operator|<
literal|16
condition|)
block|{
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|(
literal|"Could not read file lock."
argument_list|,
literal|null
argument_list|)
argument_list|)
throw|;
block|}
specifier|final
name|byte
index|[]
name|magic
init|=
operator|new
name|byte
index|[
literal|8
index|]
decl_stmt|;
name|buf
operator|.
name|get
argument_list|(
name|magic
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|Arrays
operator|.
name|equals
argument_list|(
name|magic
argument_list|,
name|MAGIC
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
name|message
argument_list|(
literal|"Bad signature in lock file. It does not seem to be an eXist lock file"
argument_list|,
literal|null
argument_list|)
argument_list|)
throw|;
block|}
name|lastHeartbeat
operator|=
name|buf
operator|.
name|getLong
argument_list|()
expr_stmt|;
name|buf
operator|.
name|clear
argument_list|()
expr_stmt|;
specifier|final
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateInstance
argument_list|()
decl_stmt|;
name|message
argument_list|(
literal|"File lock last access timestamp: "
operator|+
name|df
operator|.
name|format
argument_list|(
name|getLastHeartbeat
argument_list|()
argument_list|)
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|String
name|message
parameter_list|(
name|String
name|message
parameter_list|,
specifier|final
name|Exception
name|e
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|str
init|=
operator|new
name|StringBuilder
argument_list|(
name|message
argument_list|)
decl_stmt|;
name|str
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
operator|.
name|append
argument_list|(
name|lockFile
operator|.
name|toAbsolutePath
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|e
operator|!=
literal|null
condition|)
block|{
name|str
operator|.
name|append
argument_list|(
literal|": "
argument_list|)
operator|.
name|append
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|message
operator|=
name|str
operator|.
name|toString
argument_list|()
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isInfoEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|info
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
return|return
name|message
return|;
block|}
block|}
end_class

end_unit

