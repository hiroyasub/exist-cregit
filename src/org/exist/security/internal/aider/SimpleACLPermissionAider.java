begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id: UnixStylePermission.java 14502 2011-05-23 10:12:51Z deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
operator|.
name|aider
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|ACLPermission
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
name|PermissionDeniedException
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
name|SimpleACLPermission
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|SimpleACLPermissionAider
extends|extends
name|UnixStylePermissionAider
implements|implements
name|ACLPermission
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|ACEAider
argument_list|>
name|aces
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|public
name|SimpleACLPermissionAider
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|SimpleACLPermissionAider
parameter_list|(
name|int
name|mode
parameter_list|)
block|{
name|super
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SimpleACLPermissionAider
parameter_list|(
name|String
name|user
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|super
argument_list|(
name|user
argument_list|,
name|group
argument_list|,
name|mode
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|short
name|getVersion
parameter_list|()
block|{
return|return
name|SimpleACLPermission
operator|.
name|VERSION
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addACE
parameter_list|(
name|ACE_ACCESS_TYPE
name|access_type
parameter_list|,
name|ACE_TARGET
name|target
parameter_list|,
name|String
name|who
parameter_list|,
name|int
name|mode
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
comment|//TODO validate()
name|aces
operator|.
name|add
argument_list|(
operator|new
name|ACEAider
argument_list|(
name|access_type
argument_list|,
name|target
argument_list|,
name|who
argument_list|,
name|mode
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getACECount
parameter_list|()
block|{
return|return
name|aces
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ACE_ACCESS_TYPE
name|getACEAccessType
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|aces
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getAccessType
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|ACE_TARGET
name|getACETarget
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|aces
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getTarget
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getACEWho
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|aces
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getWho
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getACEMode
parameter_list|(
name|int
name|index
parameter_list|)
block|{
return|return
name|aces
operator|.
name|get
argument_list|(
name|index
argument_list|)
operator|.
name|getMode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|PermissionDeniedException
block|{
comment|//TODO validate()
name|aces
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCurrentSubjectCanWriteACL
parameter_list|()
block|{
comment|//TODO validate()
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit

