begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2005-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id: Restore.java 15109 2011-08-09 13:03:09Z deliriumsky $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|backup
operator|.
name|restore
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
name|backup
operator|.
name|restore
operator|.
name|listener
operator|.
name|RestoreListener
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
operator|.
name|ACE_ACCESS_TYPE
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
operator|.
name|ACE_TARGET
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
name|internal
operator|.
name|aider
operator|.
name|ACEAider
import|;
end_import

begin_comment
comment|/**  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|abstract
class|class
name|AbstractDeferredPermission
parameter_list|<
name|T
parameter_list|>
implements|implements
name|DeferredPermission
block|{
specifier|final
specifier|private
name|RestoreListener
name|listener
decl_stmt|;
specifier|final
specifier|private
name|T
name|target
decl_stmt|;
specifier|final
specifier|private
name|String
name|owner
decl_stmt|;
specifier|final
specifier|private
name|String
name|group
decl_stmt|;
specifier|final
specifier|private
name|int
name|mode
decl_stmt|;
specifier|final
name|List
argument_list|<
name|ACEAider
argument_list|>
name|aces
init|=
operator|new
name|ArrayList
argument_list|<
name|ACEAider
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|AbstractDeferredPermission
parameter_list|(
name|RestoreListener
name|listener
parameter_list|,
name|T
name|target
parameter_list|,
name|String
name|owner
parameter_list|,
name|String
name|group
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
name|this
operator|.
name|listener
operator|=
name|listener
expr_stmt|;
name|this
operator|.
name|target
operator|=
name|target
expr_stmt|;
name|this
operator|.
name|owner
operator|=
name|owner
expr_stmt|;
name|this
operator|.
name|group
operator|=
name|group
expr_stmt|;
name|this
operator|.
name|mode
operator|=
name|mode
expr_stmt|;
block|}
specifier|protected
name|RestoreListener
name|getListener
parameter_list|()
block|{
return|return
name|listener
return|;
block|}
specifier|protected
name|T
name|getTarget
parameter_list|()
block|{
return|return
name|target
return|;
block|}
specifier|protected
name|List
argument_list|<
name|ACEAider
argument_list|>
name|getAces
parameter_list|()
block|{
return|return
name|aces
return|;
block|}
specifier|protected
name|String
name|getGroup
parameter_list|()
block|{
return|return
name|group
return|;
block|}
specifier|protected
name|int
name|getMode
parameter_list|()
block|{
return|return
name|mode
return|;
block|}
specifier|protected
name|String
name|getOwner
parameter_list|()
block|{
return|return
name|owner
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|addACE
parameter_list|(
name|int
name|index
parameter_list|,
name|ACE_TARGET
name|target
parameter_list|,
name|String
name|who
parameter_list|,
name|ACE_ACCESS_TYPE
name|access_type
parameter_list|,
name|int
name|mode
parameter_list|)
block|{
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
block|}
end_class

end_unit

