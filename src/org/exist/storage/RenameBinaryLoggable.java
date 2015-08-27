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
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|charset
operator|.
name|StandardCharsets
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
comment|/**  * @author alex  */
end_comment

begin_class
specifier|public
class|class
name|RenameBinaryLoggable
extends|extends
name|AbstractLoggable
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
name|RenameBinaryLoggable
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|File
name|original
decl_stmt|;
specifier|private
name|File
name|backup
decl_stmt|;
comment|/**      * Creates a new instance of RenameBinaryLoggable      */
specifier|public
name|RenameBinaryLoggable
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|File
name|original
parameter_list|,
specifier|final
name|File
name|backup
parameter_list|)
block|{
name|super
argument_list|(
name|NativeBroker
operator|.
name|LOG_RENAME_BINARY
argument_list|,
name|txn
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|original
operator|=
name|original
expr_stmt|;
name|this
operator|.
name|backup
operator|=
name|backup
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rename binary created "
operator|+
name|original
operator|+
literal|" -> "
operator|+
name|backup
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RenameBinaryLoggable
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
name|NativeBroker
operator|.
name|LOG_RENAME_BINARY
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rename binary created ..."
argument_list|)
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
specifier|final
name|String
name|originalPath
init|=
name|original
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
name|originalPath
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
decl_stmt|;
name|out
operator|.
name|putInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|data
argument_list|)
expr_stmt|;
specifier|final
name|String
name|backupPath
init|=
name|backup
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
name|data
operator|=
name|backupPath
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
expr_stmt|;
name|out
operator|.
name|putInt
argument_list|(
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
name|out
operator|.
name|put
argument_list|(
name|data
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
name|int
name|size
init|=
name|in
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|size
index|]
decl_stmt|;
name|in
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|original
operator|=
operator|new
name|File
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|size
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
name|size
index|]
expr_stmt|;
name|in
operator|.
name|get
argument_list|(
name|data
argument_list|)
expr_stmt|;
name|backup
operator|=
operator|new
name|File
argument_list|(
operator|new
name|String
argument_list|(
name|data
argument_list|,
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Rename binary read: "
operator|+
name|original
operator|+
literal|" -> "
operator|+
name|backup
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
literal|8
operator|+
name|original
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
operator|.
name|length
operator|+
name|backup
operator|.
name|getAbsolutePath
argument_list|()
operator|.
name|getBytes
argument_list|(
name|StandardCharsets
operator|.
name|UTF_8
argument_list|)
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
name|LOG
operator|.
name|debug
argument_list|(
literal|"Undo rename: "
operator|+
name|original
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|backup
operator|.
name|renameTo
argument_list|(
name|original
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|LogException
argument_list|(
literal|"Cannot move original "
operator|+
name|original
operator|+
literal|" to backup file "
operator|+
name|backup
argument_list|)
throw|;
block|}
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
literal|" - rename "
operator|+
name|original
operator|+
literal|" to "
operator|+
name|backup
return|;
block|}
block|}
end_class

end_unit

