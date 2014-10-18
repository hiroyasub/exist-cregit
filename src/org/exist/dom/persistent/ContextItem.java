begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2014,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Expression
import|;
end_import

begin_class
specifier|public
class|class
name|ContextItem
block|{
specifier|private
name|NodeProxy
name|node
decl_stmt|;
specifier|private
name|ContextItem
name|nextDirect
decl_stmt|;
specifier|private
specifier|final
name|int
name|contextId
decl_stmt|;
specifier|public
name|ContextItem
parameter_list|(
specifier|final
name|NodeProxy
name|node
parameter_list|)
block|{
name|this
argument_list|(
name|Expression
operator|.
name|NO_CONTEXT_ID
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ContextItem
parameter_list|(
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|NodeProxy
name|node
parameter_list|)
block|{
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
block|}
specifier|public
name|NodeProxy
name|getNode
parameter_list|()
block|{
return|return
name|node
return|;
block|}
specifier|public
name|int
name|getContextId
parameter_list|()
block|{
return|return
name|contextId
return|;
block|}
specifier|public
name|boolean
name|hasNextDirect
parameter_list|()
block|{
return|return
operator|(
name|nextDirect
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|ContextItem
name|getNextDirect
parameter_list|()
block|{
return|return
name|nextDirect
return|;
block|}
specifier|public
name|void
name|setNextContextItem
parameter_list|(
specifier|final
name|ContextItem
name|next
parameter_list|)
block|{
name|nextDirect
operator|=
name|next
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|node
argument_list|)
expr_stmt|;
if|if
condition|(
name|nextDirect
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|nextDirect
argument_list|)
expr_stmt|;
block|}
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

