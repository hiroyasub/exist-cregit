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
name|nio
operator|.
name|ByteBuffer
import|;
end_import

begin_comment
comment|/**  * Interface to be implemented by all objects that can be written or read  * from the journalling log.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Loggable
block|{
comment|/** 	 * Returns the type id of the log entry. This is the type registered 	 * with class {@link LogEntryTypes}. The returned id is used by 	 * {@link JournalReader} to find the correct Loggable instance 	 * that can handle the entry.  	 *  	 * @return 	 */
specifier|public
name|byte
name|getLogType
parameter_list|()
function_decl|;
comment|/**      * Returns the transaction id of the transaction to which the      * logged operation belongs.      *       * @return      */
specifier|public
name|long
name|getTransactionId
parameter_list|()
function_decl|;
comment|/**      * Returns the {@link Lsn} of the entry.      *       * @return      */
specifier|public
name|long
name|getLsn
parameter_list|()
function_decl|;
comment|/**      * Set the {@link Lsn} of the entry.      *       * @param lsn      */
specifier|public
name|void
name|setLsn
parameter_list|(
name|long
name|lsn
parameter_list|)
function_decl|;
comment|/**      * Write this entry to the specified ByteBuffer.      *       * @param out      */
specifier|public
name|void
name|write
parameter_list|(
name|ByteBuffer
name|out
parameter_list|)
function_decl|;
comment|/**      * Read the entry.      *       * @param in      */
specifier|public
name|void
name|read
parameter_list|(
name|ByteBuffer
name|in
parameter_list|)
function_decl|;
comment|/**      * Returns the size of the work load of this      * entry.      *       * @return      */
specifier|public
name|int
name|getLogSize
parameter_list|()
function_decl|;
comment|/**      * Redo the underlying operation. This method is      * called by {@link org.exist.storage.recovery.RecoveryManager}.      *       * @throws LogException      */
specifier|public
name|void
name|redo
parameter_list|()
throws|throws
name|LogException
function_decl|;
comment|/**      * Undo, i.e. roll back, the underlying operation. The method      * is called by {@link org.exist.storage.recovery.RecoveryManager}.      *       * @throws LogException      */
specifier|public
name|void
name|undo
parameter_list|()
throws|throws
name|LogException
function_decl|;
comment|/**      * Returns a description of the entry for debugging purposes.      *       * @return      */
specifier|public
name|String
name|dump
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

