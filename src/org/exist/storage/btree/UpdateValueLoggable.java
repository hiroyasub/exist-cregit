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
name|btree
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
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
name|log
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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|UpdateValueLoggable
extends|extends
name|BTAbstractLoggable
block|{
specifier|protected
name|long
name|pageNum
decl_stmt|;
specifier|protected
name|int
name|idx
decl_stmt|;
specifier|protected
name|Value
name|key
decl_stmt|;
specifier|protected
name|long
name|pointer
decl_stmt|;
specifier|protected
name|long
name|oldPointer
decl_stmt|;
comment|/**      * @param type      * @param transaction      */
specifier|public
name|UpdateValueLoggable
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|byte
name|fileId
parameter_list|,
name|long
name|pageNum
parameter_list|,
name|int
name|idx
parameter_list|,
name|Value
name|key
parameter_list|,
name|long
name|pointer
parameter_list|,
name|long
name|oldPointer
parameter_list|)
block|{
name|super
argument_list|(
name|BTree
operator|.
name|LOG_UPDATE_VALUE
argument_list|,
name|fileId
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|this
operator|.
name|pageNum
operator|=
name|pageNum
expr_stmt|;
name|this
operator|.
name|idx
operator|=
name|idx
expr_stmt|;
name|this
operator|.
name|key
operator|=
name|key
expr_stmt|;
name|this
operator|.
name|pointer
operator|=
name|pointer
expr_stmt|;
name|this
operator|.
name|oldPointer
operator|=
name|oldPointer
expr_stmt|;
block|}
comment|/**      * @param type      * @param broker      * @param transactionId      */
specifier|public
name|UpdateValueLoggable
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|long
name|transactionId
parameter_list|)
block|{
name|super
argument_list|(
name|BTree
operator|.
name|LOG_UPDATE_VALUE
argument_list|,
name|broker
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.log.Loggable#write(java.nio.ByteBuffer)      */
specifier|public
name|void
name|write
parameter_list|(
name|ByteBuffer
name|out
parameter_list|)
block|{
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|pageNum
argument_list|)
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|idx
argument_list|)
expr_stmt|;
name|out
operator|.
name|putLong
argument_list|(
name|pointer
argument_list|)
expr_stmt|;
name|out
operator|.
name|putLong
argument_list|(
name|oldPointer
argument_list|)
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|key
operator|.
name|data
argument_list|()
argument_list|,
name|key
operator|.
name|start
argument_list|()
argument_list|,
name|key
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.log.Loggable#read(java.nio.ByteBuffer)      */
specifier|public
name|void
name|read
parameter_list|(
name|ByteBuffer
name|in
parameter_list|)
block|{
name|super
operator|.
name|read
argument_list|(
name|in
argument_list|)
expr_stmt|;
name|pageNum
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|idx
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|pointer
operator|=
name|in
operator|.
name|getLong
argument_list|()
expr_stmt|;
name|oldPointer
operator|=
name|in
operator|.
name|getLong
argument_list|()
expr_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|in
operator|.
name|getShort
argument_list|()
index|]
decl_stmt|;
name|in
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|key
operator|=
operator|new
name|Value
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.log.Loggable#getLogSize()      */
specifier|public
name|int
name|getLogSize
parameter_list|()
block|{
return|return
name|super
operator|.
name|getLogSize
argument_list|()
operator|+
literal|24
operator|+
name|key
operator|.
name|getLength
argument_list|()
return|;
block|}
specifier|public
name|void
name|redo
parameter_list|()
throws|throws
name|LogException
block|{
name|getStorage
argument_list|()
operator|.
name|redoUpdateValue
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|undo
parameter_list|()
throws|throws
name|LogException
block|{
name|getStorage
argument_list|()
operator|.
name|undoUpdateValue
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|dump
parameter_list|()
block|{
return|return
name|super
operator|.
name|dump
argument_list|()
operator|+
literal|" - update btree key on page: "
operator|+
name|pageNum
return|;
block|}
block|}
end_class

end_unit

