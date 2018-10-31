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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|CreatePageLoggable
extends|extends
name|AbstractLoggable
block|{
specifier|protected
name|long
name|prevPage
decl_stmt|;
specifier|protected
name|long
name|newPage
decl_stmt|;
specifier|protected
name|long
name|nextPage
decl_stmt|;
specifier|protected
name|short
name|nextTID
decl_stmt|;
specifier|private
name|DOMFile
name|domDb
init|=
literal|null
decl_stmt|;
specifier|public
name|CreatePageLoggable
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|long
name|prevPage
parameter_list|,
specifier|final
name|long
name|newPage
parameter_list|,
specifier|final
name|long
name|nextPage
parameter_list|)
block|{
name|this
argument_list|(
name|transaction
argument_list|,
name|prevPage
argument_list|,
name|newPage
argument_list|,
name|nextPage
argument_list|,
operator|(
name|short
operator|)
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CreatePageLoggable
parameter_list|(
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|long
name|prevPage
parameter_list|,
specifier|final
name|long
name|newPage
parameter_list|,
specifier|final
name|long
name|nextPage
parameter_list|,
specifier|final
name|short
name|nextTID
parameter_list|)
block|{
name|super
argument_list|(
name|DOMFile
operator|.
name|LOG_CREATE_PAGE
argument_list|,
name|transaction
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|prevPage
operator|=
name|prevPage
expr_stmt|;
name|this
operator|.
name|newPage
operator|=
name|newPage
expr_stmt|;
name|this
operator|.
name|nextPage
operator|=
name|nextPage
expr_stmt|;
name|this
operator|.
name|nextTID
operator|=
name|nextTID
expr_stmt|;
block|}
specifier|public
name|CreatePageLoggable
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|long
name|transactId
parameter_list|)
block|{
name|super
argument_list|(
name|DOMFile
operator|.
name|LOG_CREATE_PAGE
argument_list|,
name|transactId
argument_list|)
expr_stmt|;
name|this
operator|.
name|domDb
operator|=
name|broker
operator|==
literal|null
condition|?
literal|null
else|:
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
name|prevPage
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|newPage
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
operator|(
name|int
operator|)
name|nextPage
argument_list|)
expr_stmt|;
name|out
operator|.
name|putShort
argument_list|(
name|nextTID
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
name|prevPage
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|newPage
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|nextPage
operator|=
name|in
operator|.
name|getInt
argument_list|()
expr_stmt|;
name|nextTID
operator|=
name|in
operator|.
name|getShort
argument_list|()
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
literal|14
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
name|redoCreatePage
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
name|undoCreatePage
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
literal|" - new page created: "
operator|+
name|newPage
operator|+
literal|"; prev. page: "
operator|+
name|prevPage
operator|+
literal|"; next page: "
operator|+
name|nextPage
return|;
block|}
block|}
end_class

end_unit

