begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|UUIDGenerator
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
name|io
operator|.
name|VariableByteInput
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
name|io
operator|.
name|VariableByteOutputStream
import|;
end_import

begin_comment
comment|/**  *  Class representing a locktoken. Introduced for webDAV locking.  *  * @author Dannes Wessels  */
end_comment

begin_class
specifier|public
class|class
name|LockToken
block|{
comment|// Lock type
specifier|private
name|byte
name|type
init|=
name|LOCK_TYPE_NOT_SET
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_TYPE_NONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_TYPE_WRITE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_TYPE_NOT_SET
init|=
literal|4
decl_stmt|;
comment|// Lock depth
specifier|private
name|byte
name|depth
init|=
name|LOCK_DEPTH_NOT_SET
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_DEPTH_0
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_DEPTH_1
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_DEPTH_INFINIY
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_DEPTH_NOT_SET
init|=
literal|4
decl_stmt|;
comment|// Lock scope
specifier|private
name|byte
name|scope
init|=
name|LOCK_SCOPE_NOT_SET
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_SCOPE_NONE
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_SCOPE_EXCLUSIVE
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_SCOPE_SHARED
init|=
literal|2
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|LOCK_SCOPE_NOT_SET
init|=
literal|4
decl_stmt|;
comment|// Timeout
specifier|public
specifier|final
specifier|static
name|long
name|LOCK_TIMEOUT_INFINITE
init|=
operator|-
literal|1L
decl_stmt|;
comment|// Write Locks and Null Resources
comment|// see http://www.webdav.org/specs/rfc2518.html#rfc.section.7.4
specifier|private
name|byte
name|resourceType
init|=
name|RESOURCE_TYPE_NOT_SPECIFIED
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|RESOURCE_TYPE_NOT_SPECIFIED
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|byte
name|RESOURCE_TYPE_NULL_RESOURCE
init|=
literal|1
decl_stmt|;
comment|// Other
specifier|private
name|String
name|owner
init|=
literal|null
decl_stmt|;
specifier|private
name|long
name|timeout
init|=
operator|-
literal|1L
decl_stmt|;
specifier|private
name|String
name|token
init|=
literal|null
decl_stmt|;
comment|/**      * Creates a new instance of LockToken      */
specifier|public
name|LockToken
parameter_list|()
block|{
comment|// Left empty intentionally
block|}
comment|// Getters and setters
specifier|public
name|byte
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|byte
name|type
parameter_list|)
block|{
name|this
operator|.
name|type
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|byte
name|getDepth
parameter_list|()
block|{
return|return
name|depth
return|;
block|}
specifier|public
name|void
name|setDepth
parameter_list|(
name|byte
name|depth
parameter_list|)
block|{
name|this
operator|.
name|depth
operator|=
name|depth
expr_stmt|;
block|}
specifier|public
name|byte
name|getScope
parameter_list|()
block|{
return|return
name|scope
return|;
block|}
specifier|public
name|void
name|setScope
parameter_list|(
name|byte
name|scope
parameter_list|)
block|{
name|this
operator|.
name|scope
operator|=
name|scope
expr_stmt|;
block|}
specifier|public
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
specifier|public
name|void
name|setOwner
parameter_list|(
name|String
name|owner
parameter_list|)
block|{
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
block|}
specifier|public
name|long
name|getTimeOut
parameter_list|()
block|{
return|return
name|timeout
return|;
block|}
specifier|public
name|void
name|setTimeOut
parameter_list|(
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
specifier|public
name|String
name|getOpaqueLockToken
parameter_list|()
block|{
return|return
name|token
return|;
block|}
specifier|public
name|void
name|setOpaqueLockToken
parameter_list|(
name|String
name|token
parameter_list|)
block|{
name|this
operator|.
name|token
operator|=
name|token
expr_stmt|;
block|}
comment|//
specifier|public
name|byte
name|getResourceType
parameter_list|()
block|{
return|return
name|resourceType
return|;
block|}
specifier|public
name|void
name|setResourceType
parameter_list|(
name|byte
name|type
parameter_list|)
block|{
name|resourceType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|boolean
name|isNullResource
parameter_list|()
block|{
return|return
operator|(
name|resourceType
operator|==
name|LockToken
operator|.
name|RESOURCE_TYPE_NULL_RESOURCE
operator|)
return|;
block|}
comment|// Create new UUID for token
specifier|public
name|void
name|createOpaqueLockToken
parameter_list|()
block|{
name|token
operator|=
name|LockToken
operator|.
name|generateUUID
argument_list|()
expr_stmt|;
block|}
comment|// Helper function.
specifier|public
specifier|static
name|String
name|generateUUID
parameter_list|()
block|{
return|return
name|UUIDGenerator
operator|.
name|getUUID
argument_list|()
return|;
block|}
specifier|public
name|void
name|write
parameter_list|(
name|VariableByteOutputStream
name|ostream
parameter_list|)
throws|throws
name|IOException
block|{
name|ostream
operator|.
name|writeByte
argument_list|(
name|type
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeByte
argument_list|(
name|depth
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeByte
argument_list|(
name|scope
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|owner
operator|!=
literal|null
condition|?
name|owner
else|:
literal|""
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeLong
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeUTF
argument_list|(
name|token
operator|!=
literal|null
condition|?
name|token
else|:
literal|""
argument_list|)
expr_stmt|;
name|ostream
operator|.
name|writeByte
argument_list|(
name|resourceType
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|read
parameter_list|(
name|VariableByteInput
name|istream
parameter_list|)
throws|throws
name|IOException
block|{
name|type
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|depth
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|scope
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
name|owner
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
if|if
condition|(
name|owner
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|owner
operator|=
literal|null
expr_stmt|;
block|}
name|timeout
operator|=
name|istream
operator|.
name|readLong
argument_list|()
expr_stmt|;
name|token
operator|=
name|istream
operator|.
name|readUTF
argument_list|()
expr_stmt|;
if|if
condition|(
name|token
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|token
operator|=
literal|null
expr_stmt|;
block|}
name|resourceType
operator|=
name|istream
operator|.
name|readByte
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

