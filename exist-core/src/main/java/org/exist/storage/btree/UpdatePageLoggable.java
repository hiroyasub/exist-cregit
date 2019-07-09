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
name|UpdatePageLoggable
extends|extends
name|BTAbstractLoggable
block|{
specifier|protected
name|Value
name|prefix
init|=
name|Value
operator|.
name|EMPTY_VALUE
decl_stmt|;
specifier|protected
name|Value
name|values
index|[]
decl_stmt|;
specifier|protected
name|long
name|pointers
index|[]
decl_stmt|;
specifier|protected
name|long
name|pageNum
decl_stmt|;
specifier|protected
name|int
name|nValues
decl_stmt|;
specifier|protected
name|int
name|nPointers
decl_stmt|;
specifier|public
name|UpdatePageLoggable
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
name|Value
name|prefix
parameter_list|,
name|Value
name|values
index|[]
parameter_list|,
name|int
name|nValues
parameter_list|,
name|long
name|pointers
index|[]
parameter_list|,
name|int
name|nPointers
parameter_list|)
block|{
name|super
argument_list|(
name|BTree
operator|.
name|LOG_UPDATE_PAGE
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
name|prefix
operator|=
name|prefix
expr_stmt|;
name|this
operator|.
name|values
operator|=
name|values
expr_stmt|;
name|this
operator|.
name|nValues
operator|=
name|nValues
expr_stmt|;
name|this
operator|.
name|pointers
operator|=
name|pointers
expr_stmt|;
name|this
operator|.
name|nPointers
operator|=
name|nPointers
expr_stmt|;
block|}
specifier|public
name|UpdatePageLoggable
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
name|LOG_UPDATE_PAGE
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
name|putLong
argument_list|(
name|pageNum
argument_list|)
expr_stmt|;
specifier|final
name|short
name|pfxLen
init|=
operator|(
name|short
operator|)
name|prefix
operator|.
name|getLength
argument_list|()
decl_stmt|;
name|out
operator|.
name|putShort
argument_list|(
name|pfxLen
argument_list|)
expr_stmt|;
if|if
condition|(
name|pfxLen
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|put
argument_list|(
name|prefix
operator|.
name|data
argument_list|()
argument_list|,
name|prefix
operator|.
name|start
argument_list|()
argument_list|,
name|pfxLen
argument_list|)
expr_stmt|;
block|}
name|out
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|nValues
argument_list|)
expr_stmt|;
name|short
name|len
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nValues
condition|;
name|i
operator|++
control|)
block|{
name|len
operator|=
operator|(
name|short
operator|)
name|values
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
name|len
argument_list|)
expr_stmt|;
if|if
condition|(
name|len
operator|>
literal|0
condition|)
block|{
name|out
operator|.
name|put
argument_list|(
name|values
index|[
name|i
index|]
operator|.
name|data
argument_list|()
argument_list|,
name|values
index|[
name|i
index|]
operator|.
name|start
argument_list|()
argument_list|,
name|len
argument_list|)
expr_stmt|;
block|}
block|}
name|out
operator|.
name|putShort
argument_list|(
operator|(
name|short
operator|)
name|nPointers
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nPointers
condition|;
name|i
operator|++
control|)
block|{
name|out
operator|.
name|putLong
argument_list|(
name|pointers
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
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
name|getLong
argument_list|()
expr_stmt|;
specifier|final
name|short
name|pfxLen
init|=
name|in
operator|.
name|getShort
argument_list|()
decl_stmt|;
if|if
condition|(
name|pfxLen
operator|>
literal|0
condition|)
block|{
specifier|final
name|byte
index|[]
name|pdata
init|=
operator|new
name|byte
index|[
name|pfxLen
index|]
decl_stmt|;
name|in
operator|.
name|get
argument_list|(
name|pdata
argument_list|)
expr_stmt|;
name|prefix
operator|=
operator|new
name|Value
argument_list|(
name|pdata
argument_list|)
expr_stmt|;
block|}
name|nValues
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|values
operator|=
operator|new
name|Value
index|[
name|nValues
index|]
expr_stmt|;
name|int
name|dataLen
decl_stmt|;
name|byte
index|[]
name|data
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nValues
condition|;
name|i
operator|++
control|)
block|{
name|dataLen
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|data
operator|=
operator|new
name|byte
index|[
name|dataLen
index|]
expr_stmt|;
if|if
condition|(
name|dataLen
operator|>
literal|0
condition|)
block|{
name|in
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|values
index|[
name|i
index|]
operator|=
operator|new
name|Value
argument_list|(
name|data
argument_list|)
expr_stmt|;
block|}
name|nPointers
operator|=
name|in
operator|.
name|getShort
argument_list|()
expr_stmt|;
name|pointers
operator|=
operator|new
name|long
index|[
name|nPointers
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nPointers
condition|;
name|i
operator|++
control|)
block|{
name|pointers
index|[
name|i
index|]
operator|=
name|in
operator|.
name|getLong
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.log.Loggable#getLogSize()      */
specifier|public
name|int
name|getLogSize
parameter_list|()
block|{
name|int
name|len
init|=
name|super
operator|.
name|getLogSize
argument_list|()
operator|+
literal|14
operator|+
operator|(
name|nPointers
operator|*
literal|8
operator|)
operator|+
name|prefix
operator|.
name|getLength
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|nValues
condition|;
name|i
operator|++
control|)
name|len
operator|+=
name|values
index|[
name|i
index|]
operator|.
name|getLength
argument_list|()
operator|+
literal|2
expr_stmt|;
return|return
name|len
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
name|redoUpdatePage
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
literal|" - updated page "
operator|+
name|pageNum
return|;
block|}
block|}
end_class

end_unit

