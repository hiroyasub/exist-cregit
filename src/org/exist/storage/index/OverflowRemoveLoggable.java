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
name|index
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
name|OverflowRemoveLoggable
extends|extends
name|AbstractBFileLoggable
block|{
specifier|protected
name|byte
name|status
decl_stmt|;
specifier|protected
name|long
name|pageNum
decl_stmt|;
specifier|protected
name|byte
index|[]
name|data
decl_stmt|;
specifier|protected
name|int
name|length
decl_stmt|;
specifier|protected
name|long
name|nextInChain
decl_stmt|;
comment|/**      * @param type      * @param fileId      * @param transaction      */
specifier|public
name|OverflowRemoveLoggable
parameter_list|(
name|byte
name|fileId
parameter_list|,
name|Txn
name|transaction
parameter_list|,
name|byte
name|status
parameter_list|,
name|long
name|pageNum
parameter_list|,
name|byte
index|[]
name|data
parameter_list|,
name|int
name|length
parameter_list|,
name|long
name|nextInChain
parameter_list|)
block|{
name|super
argument_list|(
name|BFile
operator|.
name|LOG_OVERFLOW_REMOVE
argument_list|,
name|fileId
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
name|this
operator|.
name|pageNum
operator|=
name|pageNum
expr_stmt|;
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
name|this
operator|.
name|length
operator|=
name|length
expr_stmt|;
name|this
operator|.
name|nextInChain
operator|=
name|nextInChain
expr_stmt|;
block|}
comment|/**      * @param broker      * @param transactionId      */
specifier|public
name|OverflowRemoveLoggable
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Length = "
operator|+
name|length
argument_list|)
expr_stmt|;
name|super
operator|.
name|write
argument_list|(
name|out
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|status
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
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|nextInChain
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|data
argument_list|,
literal|0
argument_list|,
name|length
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
name|status
operator|=
name|in
operator|.
name|get
argument_list|()
expr_stmt|;
name|pageNum
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|nextInChain
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|length
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|length
index|]
expr_stmt|;
name|in
operator|.
name|get
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
literal|13
operator|+
name|length
return|;
block|}
specifier|public
name|void
name|redo
parameter_list|()
throws|throws
name|LogException
block|{
name|getIndexFile
argument_list|()
operator|.
name|redoRemoveOverflow
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
name|getIndexFile
argument_list|()
operator|.
name|undoRemoveOverflow
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
literal|" - remove overflow page "
operator|+
name|pageNum
return|;
block|}
block|}
end_class

end_unit

