begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|client
operator|.
name|security
package|;
end_package

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
name|java
operator|.
name|util
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|SortedSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|swing
operator|.
name|AbstractListModel
import|;
end_import

begin_class
specifier|public
class|class
name|SortedListModel
parameter_list|<
name|T
extends|extends
name|Object
parameter_list|>
extends|extends
name|AbstractListModel
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|8156990970750901747L
decl_stmt|;
specifier|private
specifier|final
name|SortedSet
argument_list|<
name|T
argument_list|>
name|model
decl_stmt|;
specifier|public
name|SortedListModel
parameter_list|()
block|{
name|model
operator|=
operator|new
name|TreeSet
argument_list|<>
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getSize
parameter_list|()
block|{
return|return
name|model
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|T
name|getElementAt
parameter_list|(
specifier|final
name|int
name|index
parameter_list|)
block|{
return|return
operator|(
name|T
operator|)
name|model
operator|.
name|toArray
argument_list|()
index|[
name|index
index|]
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|T
name|element
parameter_list|)
block|{
if|if
condition|(
name|model
operator|.
name|add
argument_list|(
name|element
argument_list|)
condition|)
block|{
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|addAll
parameter_list|(
specifier|final
name|T
name|elements
index|[]
parameter_list|)
block|{
specifier|final
name|Collection
argument_list|<
name|T
argument_list|>
name|c
init|=
name|Arrays
operator|.
name|asList
argument_list|(
name|elements
argument_list|)
decl_stmt|;
name|model
operator|.
name|addAll
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|clear
parameter_list|()
block|{
name|model
operator|.
name|clear
argument_list|()
expr_stmt|;
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Object
name|element
parameter_list|)
block|{
return|return
name|model
operator|.
name|contains
argument_list|(
name|element
argument_list|)
return|;
block|}
specifier|public
name|T
name|firstElement
parameter_list|()
block|{
return|return
name|model
operator|.
name|first
argument_list|()
return|;
block|}
specifier|public
name|Iterator
argument_list|<
name|T
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
name|model
operator|.
name|iterator
argument_list|()
return|;
block|}
specifier|public
name|T
name|lastElement
parameter_list|()
block|{
return|return
name|model
operator|.
name|last
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|removeElement
parameter_list|(
specifier|final
name|T
name|element
parameter_list|)
block|{
specifier|final
name|boolean
name|removed
init|=
name|model
operator|.
name|remove
argument_list|(
name|element
argument_list|)
decl_stmt|;
if|if
condition|(
name|removed
condition|)
block|{
name|fireContentsChanged
argument_list|(
name|this
argument_list|,
literal|0
argument_list|,
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|removed
return|;
block|}
block|}
end_class

end_unit

