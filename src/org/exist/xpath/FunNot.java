begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|AVLTreeNodeSet
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
name|ContextItem
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

begin_class
specifier|public
class|class
name|FunNot
extends|extends
name|Function
block|{
specifier|public
name|FunNot
parameter_list|()
block|{
name|super
argument_list|(
literal|"not"
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
name|NODE
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
throws|throws
name|XPathException
block|{
return|return
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|,
name|context
argument_list|)
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
name|NodeSet
name|result
init|=
operator|new
name|AVLTreeNodeSet
argument_list|()
decl_stmt|;
name|Expression
name|path
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|result
operator|.
name|addAll
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
name|NodeProxy
name|current
decl_stmt|;
if|if
condition|(
name|inPredicate
condition|)
for|for
control|(
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|current
operator|.
name|addContextNode
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
comment|// evaluate argument expression
name|Sequence
name|argSeq
init|=
name|path
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|NodeProxy
name|parent
decl_stmt|;
name|long
name|pid
decl_stmt|;
name|ContextItem
name|contextNode
decl_stmt|;
name|NodeProxy
name|next
decl_stmt|;
name|Item
name|item
decl_stmt|;
comment|// iterate through nodes and remove hits from result
for|for
control|(
name|SequenceIterator
name|i
init|=
name|argSeq
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|item
operator|=
operator|(
name|Item
operator|)
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|item
expr_stmt|;
name|contextNode
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextNode
operator|==
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"context node is missing!"
argument_list|)
expr_stmt|;
break|break;
block|}
while|while
condition|(
name|contextNode
operator|!=
literal|null
condition|)
block|{
name|next
operator|=
name|contextNode
operator|.
name|getNode
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|parent
operator|=
name|result
operator|.
name|get
argument_list|(
name|next
argument_list|)
operator|)
operator|!=
literal|null
condition|)
name|result
operator|.
name|remove
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|contextNode
operator|=
name|contextNode
operator|.
name|getNextItem
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"not("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
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

