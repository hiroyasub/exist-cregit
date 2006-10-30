begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|Collator
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
name|ExtArrayNodeSet
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
name|xquery
operator|.
name|util
operator|.
name|ExpressionDumper
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
name|AtomicValue
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
name|BooleanValue
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
name|Sequence
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValueComparison
extends|extends
name|GeneralComparison
block|{
comment|/** 	 * @param context 	 * @param relation 	 */
specifier|public
name|ValueComparison
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|relation
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @param context 	 * @param left 	 * @param right 	 * @param relation 	 */
specifier|public
name|ValueComparison
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|left
parameter_list|,
name|Expression
name|right
parameter_list|,
name|int
name|relation
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|left
argument_list|,
name|right
argument_list|,
name|relation
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|Sequence
name|genericCompare
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATION_FLAGS
argument_list|,
literal|"OPTIMIZATION CHOICE"
argument_list|,
literal|"genericCompare"
argument_list|)
expr_stmt|;
name|Sequence
name|ls
init|=
name|getLeft
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
name|Sequence
name|rs
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
if|if
condition|(
name|ls
operator|.
name|isEmpty
argument_list|()
operator|||
name|rs
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
if|if
condition|(
name|ls
operator|.
name|hasOne
argument_list|()
operator|&&
name|rs
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|AtomicValue
name|lv
decl_stmt|,
name|rv
decl_stmt|;
name|lv
operator|=
name|ls
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|rv
operator|=
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
expr_stmt|;
name|Collator
name|collator
init|=
name|getCollator
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
return|return
name|BooleanValue
operator|.
name|valueOf
argument_list|(
name|compareValues
argument_list|(
name|collator
argument_list|,
name|lv
argument_list|,
name|rv
argument_list|)
argument_list|)
return|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Type error: sequence with more than one item is not allowed here"
argument_list|)
throw|;
block|}
specifier|protected
name|Sequence
name|nodeSetCompare
parameter_list|(
name|NodeSet
name|nodes
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|isEnabled
argument_list|()
condition|)
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|this
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATION_FLAGS
argument_list|,
literal|"OPTIMIZATION CHOICE"
argument_list|,
literal|"nodeSetCompare"
argument_list|)
expr_stmt|;
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|Collator
name|collator
init|=
name|getCollator
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
operator|!
name|contextSequence
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
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
name|NodeProxy
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|ContextItem
name|context
init|=
name|current
operator|.
name|getContext
argument_list|()
decl_stmt|;
if|if
condition|(
name|context
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Context is missing for node set comparison"
argument_list|)
throw|;
block|}
do|do
block|{
name|AtomicValue
name|lv
init|=
name|current
operator|.
name|atomize
argument_list|()
decl_stmt|;
name|Sequence
name|rs
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
name|context
operator|.
name|getNode
argument_list|()
operator|.
name|toSequence
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|hasOne
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Type error: sequence with less or more than one item is not allowed here"
argument_list|)
throw|;
if|if
condition|(
name|compareValues
argument_list|(
name|collator
argument_list|,
name|lv
argument_list|,
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
do|while
condition|(
operator|(
name|context
operator|=
name|context
operator|.
name|getNextDirect
argument_list|()
operator|)
operator|!=
literal|null
condition|)
do|;
block|}
block|}
else|else
block|{
name|Sequence
name|rs
init|=
name|getRight
argument_list|()
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|rs
operator|.
name|hasOne
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Type error: sequence with less or more than one item is not allowed here"
argument_list|)
throw|;
name|AtomicValue
name|rv
init|=
name|rs
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|atomize
argument_list|()
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
name|NodeProxy
name|current
init|=
operator|(
name|NodeProxy
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|AtomicValue
name|lv
init|=
name|current
operator|.
name|atomize
argument_list|()
decl_stmt|;
if|if
condition|(
name|compareValues
argument_list|(
name|collator
argument_list|,
name|lv
argument_list|,
name|rv
argument_list|)
condition|)
name|result
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|getLeft
argument_list|()
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" "
argument_list|)
operator|.
name|display
argument_list|(
name|Constants
operator|.
name|VOPS
index|[
name|relation
index|]
argument_list|)
operator|.
name|display
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|getRight
argument_list|()
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuffer
name|result
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getLeft
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|VOPS
index|[
name|relation
index|]
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getRight
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

