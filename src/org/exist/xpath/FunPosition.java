begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

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
name|exist
operator|.
name|dom
operator|.
name|ArraySet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeProxy
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|NodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|IntegerValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
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
name|xpath
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|SequenceIterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_comment
comment|/**  * xpath-library function: position()  *  */
end_comment

begin_class
specifier|public
class|class
name|FunPosition
extends|extends
name|Function
block|{
specifier|public
name|FunPosition
parameter_list|()
block|{
name|super
argument_list|(
literal|"position"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|INTEGER
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|,
name|StaticContext
name|context
parameter_list|)
block|{
return|return
name|in_docs
return|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
name|int
name|count
init|=
literal|1
decl_stmt|;
switch|switch
condition|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
condition|)
block|{
case|case
name|Type
operator|.
name|NODE
case|:
name|NodeProxy
name|contextNode
init|=
operator|(
name|NodeProxy
operator|)
name|contextItem
decl_stmt|;
name|NodeSet
name|contextSet
init|=
operator|(
name|NodeSet
operator|)
name|contextSequence
decl_stmt|;
name|DocumentImpl
name|doc
init|=
name|contextNode
operator|.
name|getDoc
argument_list|()
decl_stmt|;
name|NodeSet
name|set
init|=
operator|(
operator|(
name|ArraySet
operator|)
name|contextSet
operator|)
operator|.
name|getSiblings
argument_list|(
name|doc
argument_list|,
name|contextNode
operator|.
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
comment|// determine position of current node in the set
name|NodeProxy
name|p
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|p
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|p
operator|.
name|gid
operator|==
name|contextNode
operator|.
name|gid
operator|&&
name|contextNode
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
name|contextNode
operator|.
name|doc
operator|.
name|getDocId
argument_list|()
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|count
argument_list|)
return|;
block|}
break|break;
default|default:
for|for
control|(
name|SequenceIterator
name|i
init|=
name|contextSequence
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|.
name|nextItem
argument_list|()
operator|==
name|contextItem
condition|)
return|return
operator|new
name|IntegerValue
argument_list|(
name|count
argument_list|)
return|;
block|}
break|break;
block|}
return|return
operator|new
name|IntegerValue
argument_list|(
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|"position()"
return|;
block|}
block|}
end_class

end_unit

