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

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|RemoveValueLoggable
extends|extends
name|AbstractBFileLoggable
block|{
specifier|protected
name|long
name|page
decl_stmt|;
specifier|protected
name|short
name|tid
decl_stmt|;
specifier|protected
name|byte
index|[]
name|oldData
decl_stmt|;
specifier|protected
name|int
name|offset
init|=
literal|0
decl_stmt|;
specifier|protected
name|int
name|len
decl_stmt|;
comment|/**      *       *       * @param page       * @param tid       * @param oldData       * @param offset       * @param len       * @param fileId       * @param transaction       */
specifier|public
name|RemoveValueLoggable
parameter_list|(
name|Txn
name|transaction
parameter_list|,
name|byte
name|fileId
parameter_list|,
name|long
name|page
parameter_list|,
name|short
name|tid
parameter_list|,
name|byte
index|[]
name|oldData
parameter_list|,
name|int
name|offset
parameter_list|,
name|int
name|len
parameter_list|)
block|{
name|super
argument_list|(
name|BFile
operator|.
name|LOG_REMOVE_VALUE
argument_list|,
name|fileId
argument_list|,
name|transaction
argument_list|)
expr_stmt|;
name|this
operator|.
name|page
operator|=
name|page
expr_stmt|;
name|this
operator|.
name|tid
operator|=
name|tid
expr_stmt|;
name|this
operator|.
name|oldData
operator|=
name|oldData
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
name|this
operator|.
name|len
operator|=
name|len
expr_stmt|;
block|}
comment|/**      * @param broker      * @param transactionId      */
specifier|public
name|RemoveValueLoggable
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
name|page
argument_list|)
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
name|tid
argument_list|)
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|len
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|oldData
argument_list|,
name|offset
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
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
name|page
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|tid
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|len
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|oldData
operator|=
operator|new
name|byte
index|[
name|len
index|]
expr_stmt|;
name|in
operator|.
name|get
argument_list|(
name|oldData
argument_list|)
expr_stmt|;
block|}
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
name|len
operator|+
literal|8
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
name|redoRemoveValue
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
name|undoRemoveValue
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
literal|" - remove value with tid "
operator|+
name|tid
operator|+
literal|" from page "
operator|+
name|page
return|;
block|}
block|}
end_class

end_unit

