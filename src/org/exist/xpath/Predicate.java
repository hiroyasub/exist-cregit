begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public  *  License along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id:  */
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|dom
operator|.
name|SingleNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LongLinkedList
import|;
end_import

begin_comment
comment|/**  *  Handles predicate expressions.  *  *@author     Wolfgang Meier<meier@ifs.tu-darmstadt.de>  */
end_comment

begin_class
specifier|public
class|class
name|Predicate
extends|extends
name|PathExpr
block|{
specifier|protected
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Predicate
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|Predicate
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
block|{
comment|//long start = System.currentTimeMillis();
name|ArraySet
name|result
init|=
operator|new
name|ArraySet
argument_list|(
literal|100
argument_list|)
decl_stmt|;
name|Expression
name|first
init|=
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|==
literal|null
condition|)
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|contextSet
argument_list|)
return|;
if|if
condition|(
name|contextNode
operator|!=
literal|null
condition|)
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|first
operator|.
name|returnsType
argument_list|()
condition|)
block|{
case|case
name|Constants
operator|.
name|TYPE_NODELIST
case|:
block|{
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|NodeSet
name|nodes
init|=
operator|(
name|NodeSet
operator|)
name|super
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
name|NodeProxy
name|current
decl_stmt|,
name|parent
decl_stmt|;
name|LongLinkedList
name|contextNodes
decl_stmt|;
name|LongLinkedList
operator|.
name|ListItem
name|next
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|nodes
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
block|{
name|current
operator|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
expr_stmt|;
name|contextNodes
operator|=
name|current
operator|.
name|getContext
argument_list|()
expr_stmt|;
if|if
condition|(
name|contextNodes
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
for|for
control|(
name|Iterator
name|j
init|=
name|contextNodes
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|next
operator|=
operator|(
name|LongLinkedList
operator|.
name|ListItem
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
name|parent
operator|=
name|contextSet
operator|.
name|get
argument_list|(
name|current
operator|.
name|doc
argument_list|,
name|next
operator|.
name|l
argument_list|)
operator|)
operator|!=
literal|null
condition|)
block|{
name|parent
operator|.
name|addMatches
argument_list|(
name|current
operator|.
name|matches
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|result
operator|.
name|contains
argument_list|(
name|parent
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
block|}
block|}
break|break;
block|}
case|case
name|Constants
operator|.
name|TYPE_BOOL
case|:
case|case
name|Constants
operator|.
name|TYPE_STRING
case|:
block|{
comment|//string has no special meaning
name|NodeProxy
name|p
decl_stmt|;
name|NodeSet
name|set
decl_stmt|;
name|DocumentSet
name|dset
decl_stmt|;
name|Value
name|v
decl_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|contextSet
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
name|set
operator|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|set
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
name|dset
operator|=
operator|new
name|DocumentSet
argument_list|()
expr_stmt|;
name|dset
operator|.
name|add
argument_list|(
name|p
operator|.
name|doc
argument_list|)
expr_stmt|;
name|v
operator|=
name|first
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|dset
argument_list|,
name|set
argument_list|,
name|p
argument_list|)
expr_stmt|;
if|if
condition|(
name|v
operator|.
name|getBooleanValue
argument_list|()
condition|)
name|result
operator|.
name|add
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
case|case
name|Constants
operator|.
name|TYPE_NUM
case|:
block|{
name|NodeProxy
name|p
decl_stmt|;
name|NodeProxy
name|n
decl_stmt|;
name|NodeSet
name|set
decl_stmt|;
name|int
name|level
decl_stmt|;
name|int
name|count
decl_stmt|;
name|double
name|pos
decl_stmt|;
name|long
name|pid
decl_stmt|;
name|long
name|last_pid
init|=
literal|0
decl_stmt|;
name|long
name|f_gid
decl_stmt|;
name|long
name|e_gid
decl_stmt|;
name|DocumentImpl
name|doc
decl_stmt|;
name|DocumentImpl
name|last_doc
init|=
literal|null
decl_stmt|;
comment|// evaluate predicate expression for each context node
for|for
control|(
name|Iterator
name|i
init|=
name|contextSet
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
name|pos
operator|=
name|first
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|,
name|p
argument_list|)
operator|.
name|getNumericValue
argument_list|()
expr_stmt|;
name|doc
operator|=
operator|(
name|DocumentImpl
operator|)
name|p
operator|.
name|getDoc
argument_list|()
expr_stmt|;
name|level
operator|=
name|doc
operator|.
name|getTreeLevel
argument_list|(
name|p
operator|.
name|getGID
argument_list|()
argument_list|)
expr_stmt|;
name|pid
operator|=
operator|(
name|p
operator|.
name|getGID
argument_list|()
operator|-
name|doc
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
operator|)
operator|/
name|doc
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|doc
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|pid
operator|==
name|last_pid
operator|&&
name|last_doc
operator|!=
literal|null
operator|&&
name|doc
operator|.
name|getDocId
argument_list|()
operator|==
name|last_doc
operator|.
name|getDocId
argument_list|()
condition|)
continue|continue;
name|last_pid
operator|=
name|pid
expr_stmt|;
name|last_doc
operator|=
name|doc
expr_stmt|;
name|f_gid
operator|=
operator|(
name|pid
operator|-
name|doc
operator|.
name|getLevelStartPoint
argument_list|(
name|level
operator|-
literal|1
argument_list|)
operator|)
operator|*
name|doc
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
operator|+
name|doc
operator|.
name|getLevelStartPoint
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|e_gid
operator|=
name|f_gid
operator|+
name|doc
operator|.
name|getTreeLevelOrder
argument_list|(
name|level
argument_list|)
expr_stmt|;
name|count
operator|=
literal|1
expr_stmt|;
name|set
operator|=
name|contextSet
operator|.
name|getRange
argument_list|(
name|doc
argument_list|,
name|f_gid
argument_list|,
name|e_gid
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|j
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|j
operator|.
name|hasNext
argument_list|()
condition|;
name|count
operator|++
control|)
block|{
name|n
operator|=
operator|(
name|NodeProxy
operator|)
name|j
operator|.
name|next
argument_list|()
expr_stmt|;
if|if
condition|(
name|count
operator|==
name|pos
condition|)
block|{
name|result
operator|.
name|add
argument_list|(
name|n
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
comment|//		LOG.debug(
comment|//			"predicate expression found "
comment|//				+ result.getLength()
comment|//				+ " in "
comment|//				+ (System.currentTimeMillis() - start)
comment|//				+ "ms.");
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|result
argument_list|)
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
block|{
name|DocumentSet
name|docs
init|=
name|in_docs
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|docs
operator|=
operator|(
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
operator|)
operator|.
name|preselect
argument_list|(
name|docs
argument_list|)
expr_stmt|;
return|return
name|docs
return|;
block|}
specifier|public
name|Value
name|evalBody
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
block|{
if|if
condition|(
name|docs
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|ValueNodeSet
argument_list|(
name|NodeSet
operator|.
name|EMPTY_SET
argument_list|)
return|;
name|Value
name|r
decl_stmt|;
if|if
condition|(
name|contextSet
operator|!=
literal|null
condition|)
name|r
operator|=
operator|new
name|ValueNodeSet
argument_list|(
name|contextSet
argument_list|)
expr_stmt|;
else|else
name|r
operator|=
operator|new
name|ValueNodeSet
argument_list|(
name|NodeSet
operator|.
name|EMPTY_SET
argument_list|)
expr_stmt|;
name|NodeSet
name|set
decl_stmt|;
name|Expression
name|expr
decl_stmt|;
for|for
control|(
name|Iterator
name|iter
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|iter
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|set
operator|=
operator|(
name|NodeSet
operator|)
name|r
operator|.
name|getNodeList
argument_list|()
expr_stmt|;
name|expr
operator|=
operator|(
name|Expression
operator|)
name|iter
operator|.
name|next
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"processing "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|expr
operator|.
name|returnsType
argument_list|()
operator|!=
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|Literal
operator|||
name|expr
operator|instanceof
name|IntNumber
condition|)
return|return
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
literal|null
argument_list|)
return|;
name|ValueSet
name|values
init|=
operator|new
name|ValueSet
argument_list|()
decl_stmt|;
for|for
control|(
name|Iterator
name|iter2
init|=
name|set
operator|.
name|iterator
argument_list|()
init|;
name|iter2
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|values
operator|.
name|add
argument_list|(
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
operator|(
name|NodeProxy
operator|)
name|iter2
operator|.
name|next
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|values
return|;
block|}
name|r
operator|=
name|expr
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|set
argument_list|,
name|contextNode
argument_list|)
expr_stmt|;
block|}
return|return
name|r
return|;
block|}
block|}
end_class

end_unit

