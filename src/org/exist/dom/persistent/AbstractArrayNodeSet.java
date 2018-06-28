begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2014 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|Constants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * Removes duplication between NewArrayNodeSet  * and ExtArrayNodeSet  *  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractArrayNodeSet
extends|extends
name|AbstractNodeSet
implements|implements
name|DocumentSet
block|{
specifier|protected
specifier|static
specifier|final
name|int
name|INITIAL_SIZE
init|=
literal|64
decl_stmt|;
specifier|protected
name|int
name|size
init|=
literal|0
decl_stmt|;
specifier|protected
name|boolean
name|isSorted
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|hasOne
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|state
init|=
literal|0
decl_stmt|;
specifier|private
name|NodeProxy
name|lastAdded
init|=
literal|null
decl_stmt|;
comment|//  used to keep track of the type of added items.
specifier|protected
name|int
name|itemType
init|=
name|Type
operator|.
name|ANY_TYPE
decl_stmt|;
comment|/**      * Reset the ArrayNodeSet so that it      * may be reused      */
specifier|public
specifier|abstract
name|void
name|reset
parameter_list|()
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|isEmpty
parameter_list|()
block|{
return|return
name|size
operator|==
literal|0
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasOne
parameter_list|()
block|{
return|return
name|hasOne
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|NodeProxy
name|proxy
parameter_list|)
block|{
name|add
argument_list|(
name|proxy
argument_list|,
name|Constants
operator|.
name|NO_SIZE_HINT
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add a new node to the set. If a new array of nodes has to be allocated      * for the document, use the sizeHint parameter to determine the size of      * the newly allocated array. This will overwrite the default array size.      *      * If the size hint is correct, no further reallocations will be required.      */
annotation|@
name|Override
specifier|public
name|void
name|add
parameter_list|(
specifier|final
name|NodeProxy
name|proxy
parameter_list|,
specifier|final
name|int
name|sizeHint
parameter_list|)
block|{
if|if
condition|(
name|size
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|hasOne
condition|)
block|{
if|if
condition|(
name|isSorted
condition|)
block|{
name|this
operator|.
name|hasOne
operator|=
name|get
argument_list|(
name|proxy
argument_list|)
operator|!=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|hasOne
operator|=
name|lastAdded
operator|==
literal|null
operator|||
name|lastAdded
operator|.
name|compareTo
argument_list|(
name|proxy
argument_list|)
operator|==
literal|0
expr_stmt|;
block|}
block|}
block|}
else|else
block|{
name|this
operator|.
name|hasOne
operator|=
literal|true
expr_stmt|;
block|}
name|addInternal
argument_list|(
name|proxy
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
name|this
operator|.
name|isSorted
operator|=
literal|false
expr_stmt|;
name|setHasChanged
argument_list|()
expr_stmt|;
name|checkItemType
argument_list|(
name|proxy
operator|.
name|getType
argument_list|()
argument_list|)
expr_stmt|;
name|this
operator|.
name|lastAdded
operator|=
name|proxy
expr_stmt|;
block|}
comment|/**      * Just add the node to this set      * all of the checks have been      * done in @see AbstractArrayNodeSet#add(NodeProxy, int)      */
specifier|protected
specifier|abstract
name|void
name|addInternal
parameter_list|(
specifier|final
name|NodeProxy
name|proxy
parameter_list|,
specifier|final
name|int
name|sizeHint
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|void
name|addAll
parameter_list|(
specifier|final
name|NodeSet
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
if|else if
condition|(
name|other
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|add
argument_list|(
operator|(
name|NodeProxy
operator|)
name|other
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
specifier|final
name|NodeProxy
name|node
range|:
name|other
control|)
block|{
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * The method<code>getItemType</code>      *      * @return an<code>int</code> value      */
annotation|@
name|Override
specifier|public
name|int
name|getItemType
parameter_list|()
block|{
return|return
name|itemType
return|;
block|}
specifier|private
name|void
name|checkItemType
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|NODE
operator|||
name|itemType
operator|==
name|type
condition|)
block|{
return|return;
block|}
if|if
condition|(
name|itemType
operator|==
name|Type
operator|.
name|ANY_TYPE
condition|)
block|{
name|itemType
operator|=
name|type
expr_stmt|;
block|}
else|else
block|{
name|itemType
operator|=
name|Type
operator|.
name|NODE
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|setHasChanged
parameter_list|()
block|{
name|this
operator|.
name|state
operator|=
operator|(
name|state
operator|==
name|Integer
operator|.
name|MAX_VALUE
condition|?
literal|0
else|:
name|state
operator|+
literal|1
operator|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLength
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isSorted
argument_list|()
condition|)
block|{
comment|// sort to remove duplicates
name|sort
argument_list|()
expr_stmt|;
block|}
return|return
name|size
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getItemCountLong
parameter_list|()
block|{
return|return
name|getLength
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|item
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
specifier|final
name|NodeProxy
name|p
init|=
name|get
argument_list|(
name|pos
argument_list|)
decl_stmt|;
return|return
name|p
operator|==
literal|null
condition|?
literal|null
else|:
name|p
operator|.
name|getNode
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Item
name|itemAt
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
name|sortInDocumentOrder
argument_list|()
expr_stmt|;
return|return
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|selectParentChild
parameter_list|(
specifier|final
name|NodeSet
name|al
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|int
name|contextId
parameter_list|)
block|{
name|sort
argument_list|()
expr_stmt|;
if|if
condition|(
name|al
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
return|return
name|super
operator|.
name|selectParentChild
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
name|contextId
argument_list|)
return|;
block|}
return|return
name|getDescendantsInSet
argument_list|(
name|al
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|,
name|mode
argument_list|,
name|contextId
argument_list|,
literal|true
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|selectAncestorDescendant
parameter_list|(
specifier|final
name|NodeSet
name|al
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|boolean
name|includeSelf
parameter_list|,
name|int
name|contextId
parameter_list|,
name|boolean
name|copyMatches
parameter_list|)
block|{
name|sort
argument_list|()
expr_stmt|;
if|if
condition|(
name|al
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
return|return
name|super
operator|.
name|selectAncestorDescendant
argument_list|(
name|al
argument_list|,
name|mode
argument_list|,
name|includeSelf
argument_list|,
name|contextId
argument_list|,
name|copyMatches
argument_list|)
return|;
block|}
return|return
name|getDescendantsInSet
argument_list|(
name|al
argument_list|,
literal|false
argument_list|,
name|includeSelf
argument_list|,
name|mode
argument_list|,
name|contextId
argument_list|,
name|copyMatches
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|selectAncestors
parameter_list|(
specifier|final
name|NodeSet
name|al
parameter_list|,
specifier|final
name|boolean
name|includeSelf
parameter_list|,
specifier|final
name|int
name|contextId
parameter_list|)
block|{
name|sort
argument_list|()
expr_stmt|;
return|return
name|super
operator|.
name|selectAncestors
argument_list|(
name|al
argument_list|,
name|includeSelf
argument_list|,
name|contextId
argument_list|)
return|;
block|}
specifier|protected
specifier|abstract
name|NodeSet
name|getDescendantsInSet
parameter_list|(
specifier|final
name|NodeSet
name|al
parameter_list|,
specifier|final
name|boolean
name|childOnly
parameter_list|,
specifier|final
name|boolean
name|includeSelf
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|,
specifier|final
name|int
name|contextId
parameter_list|,
specifier|final
name|boolean
name|copyMatches
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|DocumentSet
name|getDocumentSet
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|protected
name|boolean
name|isSorted
parameter_list|()
block|{
return|return
name|isSorted
return|;
block|}
comment|/**      * Remove all duplicate nodes, but merge their      * contexts.      */
specifier|public
name|void
name|mergeDuplicates
parameter_list|()
block|{
name|sort
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sorts the nodes in the set      * into document order.      *      * Same as calling @see #sort(false)      */
specifier|public
name|void
name|sortInDocumentOrder
parameter_list|()
block|{
name|sort
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
comment|/**      * Sorts the nodes in the set      * without merging their contexts.      *      * Same as calling @see #sort(false)      */
specifier|public
name|void
name|sort
parameter_list|()
block|{
name|sort
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|sort
parameter_list|(
specifier|final
name|boolean
name|mergeContexts
parameter_list|)
function_decl|;
annotation|@
name|Override
specifier|public
name|boolean
name|hasChanged
parameter_list|(
specifier|final
name|int
name|previousState
parameter_list|)
block|{
return|return
name|state
operator|!=
name|previousState
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|getState
parameter_list|()
block|{
return|return
name|state
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isCacheable
parameter_list|()
block|{
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"ArrayNodeSet#"
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

