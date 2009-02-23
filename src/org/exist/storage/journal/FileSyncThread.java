begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|journal
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

begin_comment
comment|/**  * Sync the current journal file by calling {@link java.nio.channels.FileChannel#force(boolean)}.  * This operation is quite expensive, so we delegate it to a background thread. The main  * logging thread can continue to write into the log buffer and does not need to wait until  * the force operation returns.   *   * However, we have to make sure that only one sync operation is running at a time. So if  * the main logging thread triggers another sync while one is already in progress, it has to  * wait until the sync operation has finished.  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|FileSyncThread
extends|extends
name|Thread
block|{
specifier|private
name|FileChannel
name|endOfLog
decl_stmt|;
specifier|private
name|boolean
name|syncTriggered
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|shutdown
init|=
literal|false
decl_stmt|;
specifier|private
name|Object
name|latch
decl_stmt|;
comment|/**      * Create a new FileSyncThread, using the specified latch      * to synchronize on.      *       * @param latch      */
specifier|public
name|FileSyncThread
parameter_list|(
name|Object
name|latch
parameter_list|)
block|{
name|super
argument_list|()
expr_stmt|;
name|this
operator|.
name|latch
operator|=
name|latch
expr_stmt|;
block|}
comment|/**      * Set the channel opened on the current journal file.      * Called by {@link Journal} when it switches to      * a new file.      *       * @param channel      */
specifier|public
name|void
name|setChannel
parameter_list|(
name|FileChannel
name|channel
parameter_list|)
block|{
name|endOfLog
operator|=
name|channel
expr_stmt|;
block|}
comment|/**      * Trigger a sync on the journal. If a sync is already in progress,      * the method will just wait until the sync has completed.      */
specifier|public
specifier|synchronized
name|void
name|triggerSync
parameter_list|()
block|{
comment|// trigger a sync
name|syncTriggered
operator|=
literal|true
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
comment|/**      * Shutdown the sync thread.      */
specifier|public
specifier|synchronized
name|void
name|shutdown
parameter_list|()
block|{
name|shutdown
operator|=
literal|true
expr_stmt|;
name|interrupt
argument_list|()
expr_stmt|;
block|}
comment|/**      * Close the underlying channel.      */
specifier|public
name|void
name|closeChannel
parameter_list|()
block|{
synchronized|synchronized
init|(
name|latch
init|)
block|{
try|try
block|{
name|endOfLog
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
block|}
block|}
block|}
comment|/**      * Wait for a sync event or shutdown.      */
specifier|public
name|void
name|run
parameter_list|()
block|{
while|while
condition|(
operator|!
name|shutdown
condition|)
block|{
synchronized|synchronized
init|(
name|this
init|)
block|{
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
if|if
condition|(
name|syncTriggered
condition|)
block|{
name|sync
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|// shutdown: sync the file and close it
name|sync
argument_list|()
expr_stmt|;
name|closeChannel
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|sync
parameter_list|()
block|{
synchronized|synchronized
init|(
name|latch
init|)
block|{
try|try
block|{
name|endOfLog
operator|.
name|force
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|// may occur during shutdown
block|}
name|syncTriggered
operator|=
literal|false
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

