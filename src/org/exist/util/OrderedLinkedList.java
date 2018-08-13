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
comment|/* eXist Native XML Database  * Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  * $Id$  */
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

begin_class
specifier|public
class|class
name|OrderedLinkedList
block|{
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
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
name|OrderedLinkedList
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|abstract
specifier|static
class|class
name|Node
block|{
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
name|getNextNode
parameter_list|()
block|{
return|return
name|next
return|;
block|}
specifier|public
name|Node
name|getPrevNode
parameter_list|()
block|{
return|return
name|prev
return|;
block|}
specifier|public
specifier|abstract
name|int
name|compareTo
parameter_list|(
name|Node
name|other
parameter_list|)
function_decl|;
specifier|public
specifier|abstract
name|boolean
name|equals
parameter_list|(
name|Node
name|other
parameter_list|)
function_decl|;
block|}
specifier|public
specifier|static
class|class
name|SimpleNode
extends|extends
name|Node
block|{
name|Comparable
name|data
decl_stmt|;
specifier|public
name|SimpleNode
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
specifier|public
name|int
name|compareTo
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
specifier|final
name|SimpleNode
name|o
init|=
operator|(
name|SimpleNode
operator|)
name|other
decl_stmt|;
return|return
name|data
operator|.
name|compareTo
argument_list|(
name|o
operator|.
name|data
argument_list|)
return|;
block|}
specifier|public
name|boolean
name|equals
parameter_list|(
name|Node
name|other
parameter_list|)
block|{
return|return
operator|(
operator|(
name|SimpleNode
operator|)
name|other
operator|)
operator|.
name|data
operator|.
name|equals
argument_list|(
name|data
argument_list|)
return|;
block|}
specifier|public
name|Comparable
name|getData
parameter_list|()
block|{
return|return
name|data
return|;
block|}
block|}
specifier|protected
name|Node
name|header
init|=
literal|null
decl_stmt|;
specifier|protected
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
name|Node
name|add
parameter_list|(
name|Node
name|newNode
parameter_list|)
block|{
name|newNode
operator|.
name|next
operator|=
literal|null
expr_stmt|;
name|newNode
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
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
name|newNode
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
name|node
init|=
name|header
decl_stmt|;
while|while
condition|(
name|newNode
operator|.
name|compareTo
argument_list|(
name|node
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
block|{
name|newNode
operator|.
name|prev
operator|.
name|next
operator|=
name|newNode
expr_stmt|;
block|}
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
block|{
name|header
operator|=
name|newNode
expr_stmt|;
block|}
return|return
name|newNode
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|Node
name|n
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
name|equals
argument_list|(
name|n
argument_list|)
condition|)
block|{
name|removeNode
argument_list|(
name|n
argument_list|)
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
name|void
name|removeNode
parameter_list|(
name|Node
name|node
parameter_list|)
block|{
operator|--
name|size
expr_stmt|;
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
block|{
name|header
operator|=
literal|null
expr_stmt|;
block|}
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
block|{
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
block|}
else|else
block|{
name|last
operator|=
name|node
operator|.
name|prev
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|Node
name|removeFirst
parameter_list|()
block|{
specifier|final
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
block|{
name|header
operator|.
name|prev
operator|=
literal|null
expr_stmt|;
block|}
operator|--
name|size
expr_stmt|;
return|return
name|node
return|;
block|}
specifier|public
name|Node
name|removeLast
parameter_list|()
block|{
specifier|final
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
return|;
block|}
specifier|public
name|Node
name|getLast
parameter_list|()
block|{
return|return
name|last
operator|==
literal|null
condition|?
literal|null
else|:
name|last
return|;
block|}
specifier|public
name|Node
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
operator|++
operator|==
name|pos
condition|)
block|{
return|return
name|node
return|;
block|}
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
name|Node
index|[]
name|getData
parameter_list|()
block|{
specifier|final
name|Node
index|[]
name|data
init|=
operator|new
name|Node
index|[
name|size
index|]
decl_stmt|;
name|Node
name|next
init|=
name|header
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|data
index|[
name|i
operator|++
index|]
operator|=
name|next
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
specifier|public
name|Node
index|[]
name|toArray
parameter_list|(
name|Node
index|[]
name|data
parameter_list|)
block|{
name|Node
name|next
init|=
name|header
decl_stmt|;
name|int
name|i
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
name|data
index|[
name|i
operator|++
index|]
operator|=
name|next
expr_stmt|;
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
name|data
return|;
block|}
specifier|public
name|boolean
name|contains
parameter_list|(
name|Node
name|c
parameter_list|)
block|{
name|Node
name|next
init|=
name|header
decl_stmt|;
while|while
condition|(
name|next
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|next
operator|.
name|equals
argument_list|(
name|c
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
name|next
operator|=
name|next
operator|.
name|next
expr_stmt|;
block|}
return|return
literal|false
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
block|{
return|return
literal|null
return|;
block|}
specifier|final
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
block|}
end_class

end_unit

