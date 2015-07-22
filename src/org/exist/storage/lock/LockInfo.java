begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|PrintStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Arrays
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Debuggable
import|;
end_import

begin_comment
comment|/**  * Encapsulates debug information about a log. This information can be exported  * via the JMX management interface, if enabled.  */
end_comment

begin_class
specifier|public
class|class
name|LockInfo
implements|implements
name|Debuggable
block|{
specifier|public
specifier|final
specifier|static
name|String
name|COLLECTION_LOCK
init|=
literal|"COLLECTION"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RESOURCE_LOCK
init|=
literal|"RESOURCE"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|READ_LOCK
init|=
literal|"READ"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|WRITE_LOCK
init|=
literal|"WRITE"
decl_stmt|;
specifier|private
name|String
name|lockType
decl_stmt|;
specifier|private
name|String
name|lockMode
decl_stmt|;
specifier|private
name|String
name|id
decl_stmt|;
specifier|private
name|String
index|[]
name|owners
decl_stmt|;
specifier|private
name|String
index|[]
name|waitingForWrite
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|String
index|[]
name|waitingForRead
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|private
name|String
index|[]
name|readLocks
init|=
operator|new
name|String
index|[
literal|0
index|]
decl_stmt|;
specifier|public
name|LockInfo
parameter_list|(
name|String
name|lockType
parameter_list|,
name|String
name|lockMode
parameter_list|,
name|String
name|id
parameter_list|,
name|String
index|[]
name|owners
parameter_list|)
block|{
name|this
operator|.
name|lockType
operator|=
name|lockType
expr_stmt|;
name|this
operator|.
name|lockMode
operator|=
name|lockMode
expr_stmt|;
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|owners
operator|=
name|owners
expr_stmt|;
block|}
specifier|public
name|String
name|getLockType
parameter_list|()
block|{
return|return
name|lockType
return|;
block|}
specifier|public
name|String
name|getLockMode
parameter_list|()
block|{
return|return
name|lockMode
return|;
block|}
specifier|public
name|String
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
index|[]
name|getOwners
parameter_list|()
block|{
return|return
name|owners
return|;
block|}
specifier|public
name|String
index|[]
name|getWaitingForWrite
parameter_list|()
block|{
return|return
name|waitingForWrite
return|;
block|}
specifier|public
name|void
name|setWaitingForWrite
parameter_list|(
name|String
index|[]
name|waitingForWrite
parameter_list|)
block|{
name|this
operator|.
name|waitingForWrite
operator|=
name|waitingForWrite
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getWaitingForRead
parameter_list|()
block|{
return|return
name|waitingForRead
return|;
block|}
specifier|public
name|void
name|setWaitingForRead
parameter_list|(
name|String
index|[]
name|waitingForRead
parameter_list|)
block|{
name|this
operator|.
name|waitingForRead
operator|=
name|waitingForRead
expr_stmt|;
block|}
specifier|public
name|String
index|[]
name|getReadLocks
parameter_list|()
block|{
return|return
name|readLocks
return|;
block|}
specifier|public
name|void
name|setReadLocks
parameter_list|(
name|String
index|[]
name|readLocks
parameter_list|)
block|{
name|this
operator|.
name|readLocks
operator|=
name|readLocks
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|debug
parameter_list|(
name|PrintStream
name|out
parameter_list|)
block|{
name|out
operator|.
name|println
argument_list|(
literal|"Lock type: "
operator|+
name|getLockType
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Lock mode: "
operator|+
name|getLockMode
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Lock id: "
operator|+
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Held by: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|getOwners
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Read locks: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|getReadLocks
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Wait for read: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|getWaitingForRead
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|out
operator|.
name|println
argument_list|(
literal|"Wait for write: "
operator|+
name|Arrays
operator|.
name|toString
argument_list|(
name|getWaitingForWrite
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

