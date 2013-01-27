begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2001-06,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
comment|/**  * Boolean operator "and".  *   * @author Wolfgang<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|OpAnd
extends|extends
name|LogicalOp
block|{
specifier|public
name|OpAnd
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Sequence
name|eval
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
block|{
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|start
argument_list|(
name|this
argument_list|)
expr_stmt|;
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
name|DEPENDENCIES
argument_list|,
literal|"DEPENDENCIES"
argument_list|,
name|Dependency
operator|.
name|getDependenciesName
argument_list|(
name|this
operator|.
name|getDependencies
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextSequence
operator|!=
literal|null
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
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT SEQUENCE"
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
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
name|START_SEQUENCES
argument_list|,
literal|"CONTEXT ITEM"
argument_list|,
name|contextItem
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|result
decl_stmt|;
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
name|result
operator|=
name|Sequence
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|boolean
name|doOptimize
init|=
name|optimize
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
name|isPersistentSet
argument_list|()
condition|)
name|doOptimize
operator|=
literal|false
expr_stmt|;
name|Expression
name|left
init|=
name|getLeft
argument_list|()
decl_stmt|;
name|Expression
name|right
init|=
name|getRight
argument_list|()
decl_stmt|;
comment|//            setContextId(getExpressionId());
if|if
condition|(
name|doOptimize
operator|&&
name|contextSequence
operator|!=
literal|null
condition|)
name|contextSequence
operator|.
name|setSelfAsContext
argument_list|(
name|getContextId
argument_list|()
argument_list|)
expr_stmt|;
name|Sequence
name|ls
init|=
name|left
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|doOptimize
operator|=
name|doOptimize
operator|&&
operator|(
name|ls
operator|.
name|isPersistentSet
argument_list|()
operator|||
name|ls
operator|.
name|isEmpty
argument_list|()
operator|)
expr_stmt|;
if|if
condition|(
name|doOptimize
condition|)
block|{
if|if
condition|(
name|inPredicate
condition|)
block|{
name|NodeSet
name|lr
init|=
name|ls
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|lr
operator|=
name|lr
operator|.
name|getContextNodes
argument_list|(
name|getContextId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|lr
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
name|Sequence
name|rs
init|=
name|right
operator|.
name|eval
argument_list|(
name|lr
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|NodeSet
name|rr
init|=
name|rs
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|result
operator|=
name|rr
operator|.
name|getContextNodes
argument_list|(
name|getContextId
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|rs
init|=
name|right
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|rs
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
name|NodeSet
name|rl
init|=
name|ls
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|rl
operator|=
name|rl
operator|.
name|getContextNodes
argument_list|(
name|left
operator|.
name|getContextId
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|rl
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
name|NodeSet
operator|.
name|EMPTY_SET
return|;
comment|// TODO: optimize and return false if rl.isEmpty() ?
name|NodeSet
name|rr
init|=
name|rs
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|rr
operator|=
name|rr
operator|.
name|getContextNodes
argument_list|(
name|right
operator|.
name|getContextId
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|rr
operator|.
name|intersection
argument_list|(
name|rl
argument_list|)
expr_stmt|;
comment|//<test>{() and ()}</test> has to return<test>false</test>
if|if
condition|(
name|getParent
argument_list|()
operator|instanceof
name|EnclosedExpr
operator|||
comment|//First, the intermediate PathExpr
operator|(
name|getParent
argument_list|()
operator|!=
literal|null
operator|&&
name|getParent
argument_list|()
operator|.
name|getParent
argument_list|()
operator|==
literal|null
operator|)
condition|)
block|{
name|result
operator|=
name|result
operator|.
name|isEmpty
argument_list|()
condition|?
name|BooleanValue
operator|.
name|FALSE
else|:
name|BooleanValue
operator|.
name|TRUE
expr_stmt|;
block|}
block|}
else|else
block|{
comment|// fall back if right sequence is not persistent
name|boolean
name|rl
init|=
name|ls
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|rl
condition|)
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
else|else
block|{
name|boolean
name|rr
init|=
name|rs
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|rl
operator|&&
name|rr
operator|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
block|}
block|}
else|else
block|{
name|boolean
name|rl
init|=
name|ls
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
comment|//Immediately return false if the left operand is false
if|if
condition|(
operator|!
name|rl
condition|)
block|{
name|result
operator|=
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|rs
init|=
name|right
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|boolean
name|rr
init|=
name|rs
operator|.
name|effectiveBooleanValue
argument_list|()
decl_stmt|;
name|result
operator|=
operator|(
name|rl
operator|&&
name|rr
operator|)
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
expr_stmt|;
block|}
block|}
block|}
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
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
name|visitor
operator|.
name|visitAndExpr
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return;
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|") and ("
argument_list|)
expr_stmt|;
name|getExpression
argument_list|(
name|i
argument_list|)
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
if|if
condition|(
name|getLength
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|""
return|;
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"("
argument_list|)
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|1
init|;
name|i
operator|<
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|") and ("
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getExpression
argument_list|(
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|")"
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

