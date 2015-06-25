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
name|txn
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
name|Date
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
name|LogEntryTypes
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Checkpoint
extends|extends
name|AbstractLoggable
block|{
specifier|private
name|long
name|timestamp
decl_stmt|;
specifier|private
name|long
name|storedLsn
decl_stmt|;
specifier|private
specifier|final
name|DateFormat
name|df
init|=
name|DateFormat
operator|.
name|getDateTimeInstance
argument_list|(
name|DateFormat
operator|.
name|MEDIUM
argument_list|,
name|DateFormat
operator|.
name|MEDIUM
argument_list|)
decl_stmt|;
specifier|public
name|Checkpoint
parameter_list|(
name|long
name|transactionId
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Checkpoint
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
name|LogEntryTypes
operator|.
name|CHECKPOINT
argument_list|,
name|transactionId
argument_list|)
expr_stmt|;
name|timestamp
operator|=
operator|new
name|Date
argument_list|()
operator|.
name|getTime
argument_list|()
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
name|out
operator|.
name|putLong
argument_list|(
name|lsn
argument_list|)
expr_stmt|;
name|out
operator|.
name|putLong
argument_list|(
name|timestamp
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
name|storedLsn
operator|=
name|in
operator|.
name|getLong
argument_list|()
expr_stmt|;
name|timestamp
operator|=
name|in
operator|.
name|getLong
argument_list|()
expr_stmt|;
block|}
specifier|public
name|long
name|getStoredLsn
parameter_list|()
block|{
return|return
name|storedLsn
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.storage.log.Loggable#getLogSize()      */
specifier|public
name|int
name|getLogSize
parameter_list|()
block|{
return|return
literal|16
return|;
block|}
specifier|public
name|String
name|getDateString
parameter_list|()
block|{
return|return
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
return|;
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
literal|" - checkpoint at "
operator|+
name|df
operator|.
name|format
argument_list|(
operator|new
name|Date
argument_list|(
name|timestamp
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

