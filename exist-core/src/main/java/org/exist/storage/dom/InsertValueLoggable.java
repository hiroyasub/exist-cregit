begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  *  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|dom
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
name|NativeBroker
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
name|AbstractLoggable
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
comment|/**  * Insert a value into a data page.  *  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|InsertValueLoggable
extends|extends
name|AbstractLoggable
block|{
specifier|private
name|DOMFile
name|domDb
decl_stmt|;
specifier|protected
name|byte
name|isOverflow
decl_stmt|;
specifier|protected
name|long
name|pageNum
decl_stmt|;
specifier|protected
name|short
name|tid
decl_stmt|;
specifier|protected
name|byte
index|[]
name|value
decl_stmt|;
specifier|protected
name|int
name|offset
decl_stmt|;
specifier|public
name|InsertValueLoggable
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|long
name|pageNum
parameter_list|,
specifier|final
name|boolean
name|isOverflow
parameter_list|,
specifier|final
name|short
name|tid
parameter_list|,
specifier|final
name|byte
index|[]
name|value
parameter_list|,
specifier|final
name|int
name|offset
parameter_list|)
block|{
name|super
argument_list|(
name|DOMFile
operator|.
name|LOG_INSERT_RECORD
argument_list|,
name|transaction
operator|.
name|getId
argument_list|()
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
name|isOverflow
operator|=
operator|(
name|isOverflow
condition|?
operator|(
name|byte
operator|)
literal|1
else|:
literal|0
operator|)
expr_stmt|;
name|this
operator|.
name|tid
operator|=
name|tid
expr_stmt|;
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
name|this
operator|.
name|offset
operator|=
name|offset
expr_stmt|;
block|}
specifier|public
name|InsertValueLoggable
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|long
name|transactionId
parameter_list|)
block|{
name|super
argument_list|(
name|DOMFile
operator|.
name|LOG_INSERT_RECORD
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|this
operator|.
name|domDb
operator|=
operator|(
operator|(
name|NativeBroker
operator|)
name|broker
operator|)
operator|.
name|getDOMFile
argument_list|()
expr_stmt|;
block|}
specifier|protected
name|boolean
name|isOverflow
parameter_list|()
block|{
return|return
name|isOverflow
operator|==
literal|1
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|write
parameter_list|(
specifier|final
name|ByteBuffer
name|out
parameter_list|)
block|{
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
name|put
argument_list|(
name|isOverflow
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
name|offset
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
name|value
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|read
parameter_list|(
specifier|final
name|ByteBuffer
name|in
parameter_list|)
block|{
name|pageNum
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|isOverflow
operator|=
name|in
operator|.
name|get
argument_list|()
expr_stmt|;
name|offset
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
name|value
operator|=
operator|new
name|byte
index|[
name|in
operator|.
name|getShort
argument_list|()
index|]
expr_stmt|;
name|in
operator|.
name|get
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLogSize
parameter_list|()
block|{
return|return
literal|13
operator|+
name|value
operator|.
name|length
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|redo
parameter_list|()
throws|throws
name|LogException
block|{
name|domDb
operator|.
name|redoInsertValue
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|undo
parameter_list|()
throws|throws
name|LogException
block|{
name|domDb
operator|.
name|undoInsertValue
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
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
literal|" - inserted value; tid = "
operator|+
name|tid
operator|+
literal|" in page "
operator|+
name|pageNum
operator|+
literal|"; bytes: "
operator|+
name|value
operator|.
name|length
operator|+
literal|"; offset: "
operator|+
name|offset
return|;
block|}
block|}
end_class

end_unit

