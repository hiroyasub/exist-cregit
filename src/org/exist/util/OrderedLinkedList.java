begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_comment
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * $Id$  */
end_comment

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_class
specifier|public
class|class
name|OrderedLinkedList
block|{
specifier|private
specifier|final
specifier|static
class|class
name|Node
block|{
name|Comparable
name|data
decl_stmt|;
name|Node
name|next
init|=
literal|null
decl_stmt|;
name|Node
name|prev
init|=
literal|null
decl_stmt|;
specifier|public
name|Node
parameter_list|(
name|Comparable
name|data
parameter_list|)
block|{
name|this
operator|.
name|data
operator|=
name|data
expr_stmt|;
block|}
block|}
specifier|private
name|Node
name|header
init|=
literal|null
decl_stmt|;
specifier|private
name|Node
name|last
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|size
init|=
literal|0
decl_stmt|;
specifier|public
name|OrderedLinkedList
parameter_list|()
block|{
block|}
specifier|public
name|Node
name|add
parameter_list|(
name|Comparable
name|obj
parameter_list|)
block|{
name|size
operator|++
expr_stmt|;
if|if
condition|(
name|header
operator|==
literal|null
condition|)
block|{
name|header
operator|=
operator|new
name|Node
argument_list|(
name|obj
argument_list|)
expr_stmt|;
name|last
operator|=
name|header
expr_stmt|;
return|return
name|header
return|;
block|}
name|Node
name|newNode
init|=
operator|new
name|Node
argument_list|(
name|obj
argument_list|)
decl_stmt|;
name|Node
name|node
init|=
name|header
decl_stmt|;
while|while
condition|(
name|obj
operator|.
name|compareTo
argument_list|(
name|node
operator|.
name|data
argument_list|)
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|next
operator|==
literal|null
condition|)
block|{
comment|// append to end of list
name|node
operator|.
name|next
operator|=
name|newNode
expr_stmt|;
name|newNode
operator|.
name|prev
operator|=
name|node
expr_stmt|;
name|last
operator|=
name|newNode
expr_stmt|;
return|return
name|newNode
return|;
block|}
name|node
operator|=
name|node
operator|.
name|next
expr_stmt|;
block|}
comment|// insert node
name|newNode
operator|.
name|prev
operator|=
name|node
operator|.
name|prev
expr_stmt|;
if|if
condition|(
name|newNode
operator|.
name|prev
operator|!=
literal|null
condition|)
name|newNode
operator|.
name|prev
operator|.
name|next
operator|=
name|newNode
expr_stmt|;
name|node
operator|.
name|prev
operator|=
name|newNode
expr_stmt|;
name|newNode
operator|.
name|next
operator|=
name|node
expr_stmt|;
if|if
condition|(
name|node
operator|==
name|header
condition|)
name|header
operator|=
name|newNode
expr_stmt|;
return|return
name|newNode
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|Comparable
name|obj
parameter_list|)
block|{
name|Node
name|node
init|=
name|header
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|data
operator|==
name|obj
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|prev
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|node
operator|.
name|next
operator|!=
literal|null
condition|)
block|{
name|node
operator|.
name|next
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
name|header
operator|=
name|node
operator|.
name|next
expr_stmt|;
block|}
else|else
name|header
operator|=
literal|null
expr_stmt|;
block|}
else|else
block|{
name|node
operator|.
name|prev
operator|.
name|next
operator|=
name|node
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|node
operator|.
name|next
operator|!=
literal|null
condition|)
name|node
operator|.
name|next
operator|.
name|prev
operator|=
name|node
operator|.
name|prev
expr_stmt|;
else|else
name|last
operator|=
name|node
operator|.
name|prev
expr_stmt|;
block|}
name|size
operator|--
expr_stmt|;
return|return;
block|}
name|node
operator|=
name|node
operator|.
name|next
expr_stmt|;
block|}
block|}
specifier|public
name|Object
name|removeFirst
parameter_list|()
block|{
name|Node
name|node
init|=
name|header
decl_stmt|;
name|header
operator|=
name|node
operator|.
name|next
expr_stmt|;
if|if
condition|(
name|header
operator|!=
literal|null
condition|)
name|header
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
operator|--
name|size
expr_stmt|;
return|return
name|node
operator|.
name|data
return|;
block|}
specifier|public
name|Object
name|removeLast
parameter_list|()
block|{
name|Node
name|node
init|=
name|last
decl_stmt|;
name|last
operator|=
name|node
operator|.
name|prev
expr_stmt|;
name|last
operator|.
name|next
operator|=
literal|null
expr_stmt|;
operator|--
name|size
expr_stmt|;
return|return
name|node
operator|.
name|data
return|;
block|}
specifier|public
name|Object
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
name|Node
name|node
init|=
name|header
decl_stmt|;
name|int
name|count
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|count
operator|==
name|pos
condition|)
return|return
name|node
operator|.
name|data
return|;
name|node
operator|=
name|node
operator|.
name|next
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|int
name|size
parameter_list|()
block|{
return|return
name|size
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|header
operator|=
literal|null
expr_stmt|;
name|last
operator|=
literal|null
expr_stmt|;
name|size
operator|=
literal|0
expr_stmt|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|OrderedListIterator
argument_list|(
name|header
argument_list|)
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|OrderedListIterator
implements|implements
name|Iterator
block|{
specifier|private
name|Node
name|next
decl_stmt|;
specifier|public
name|OrderedListIterator
parameter_list|(
name|Node
name|header
parameter_list|)
block|{
name|this
operator|.
name|next
operator|=
name|header
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|next
operator|!=
literal|null
operator|)
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
if|if
condition|(
name|next
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|Node
name|current
init|=
name|next
decl_stmt|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
return|return
name|current
operator|.
name|data
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"not implemented"
argument_list|)
throw|;
block|}
block|}
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
name|args
index|[]
parameter_list|)
block|{
name|OrderedLinkedList
name|list
init|=
operator|new
name|OrderedLinkedList
argument_list|()
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Adam"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Sabine"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Adam"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Georg"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Heinrich"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Georg"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Wolfgang"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Egon"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Berta"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Fritz"
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
literal|"Berta"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Hans"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Xerces"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Hubert"
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
literal|"Georg"
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
literal|"Xerces"
argument_list|)
expr_stmt|;
name|list
operator|.
name|remove
argument_list|(
literal|"Wolfgang"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|list
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|(
name|String
operator|)
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

