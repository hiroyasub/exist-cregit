begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|ngram
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
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
name|persistent
operator|.
name|EmptyNodeSet
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
name|persistent
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
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|ngram
operator|.
name|NGramIndexWorker
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
name|XPathException
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
name|modules
operator|.
name|ngram
operator|.
name|utils
operator|.
name|NodeProxies
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
name|modules
operator|.
name|ngram
operator|.
name|utils
operator|.
name|NodeSets
import|;
end_import

begin_class
specifier|public
class|class
name|WildcardedExpressionSequence
implements|implements
name|EvaluatableExpression
block|{
comment|/**      *      */
specifier|private
specifier|final
name|List
argument_list|<
name|WildcardedExpression
argument_list|>
name|expressions
decl_stmt|;
specifier|private
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|WildcardedExpressionSequence
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|WildcardedExpressionSequence
parameter_list|(
specifier|final
name|List
argument_list|<
name|WildcardedExpression
argument_list|>
name|expressions
parameter_list|)
block|{
name|this
operator|.
name|expressions
operator|=
operator|new
name|ArrayList
argument_list|<>
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|WildcardedExpression
name|currentExpression
init|=
name|expressions
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
decl_stmt|;
for|for
control|(
name|WildcardedExpression
name|expression
range|:
name|expressions
control|)
block|{
if|if
condition|(
name|currentExpression
operator|instanceof
name|MergeableExpression
operator|&&
operator|(
operator|(
name|MergeableExpression
operator|)
name|currentExpression
operator|)
operator|.
name|mergeableWith
argument_list|(
name|expression
argument_list|)
condition|)
block|{
name|currentExpression
operator|=
operator|(
operator|(
name|MergeableExpression
operator|)
name|currentExpression
operator|)
operator|.
name|mergeWith
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|expressions
operator|.
name|add
argument_list|(
name|currentExpression
argument_list|)
expr_stmt|;
name|currentExpression
operator|=
name|expression
expr_stmt|;
block|}
block|}
name|this
operator|.
name|expressions
operator|.
name|add
argument_list|(
name|currentExpression
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|NodeSet
name|eval
parameter_list|(
specifier|final
name|NGramIndexWorker
name|index
parameter_list|,
specifier|final
name|DocumentSet
name|docs
parameter_list|,
specifier|final
name|List
argument_list|<
name|QName
argument_list|>
name|qnames
parameter_list|,
specifier|final
name|NodeSet
name|nodeSet
parameter_list|,
specifier|final
name|int
name|axis
parameter_list|,
specifier|final
name|int
name|expressionId
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|startAnchorPresent
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|expressions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|StartAnchor
condition|)
block|{
name|startAnchorPresent
operator|=
literal|true
expr_stmt|;
name|expressions
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
name|Wildcard
name|leadingWildcard
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|expressions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|Wildcard
condition|)
name|leadingWildcard
operator|=
operator|(
name|Wildcard
operator|)
name|expressions
operator|.
name|remove
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|boolean
name|endAnchorPresent
init|=
literal|false
decl_stmt|;
if|if
condition|(
operator|!
name|expressions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|expressions
operator|.
name|get
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|instanceof
name|EndAnchor
condition|)
block|{
name|endAnchorPresent
operator|=
literal|true
expr_stmt|;
name|expressions
operator|.
name|remove
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
name|Wildcard
name|trailingWildcard
init|=
literal|null
decl_stmt|;
if|if
condition|(
operator|!
name|expressions
operator|.
name|isEmpty
argument_list|()
operator|&&
name|expressions
operator|.
name|get
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
operator|instanceof
name|Wildcard
condition|)
name|trailingWildcard
operator|=
operator|(
name|Wildcard
operator|)
name|expressions
operator|.
name|remove
argument_list|(
name|expressions
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
while|while
condition|(
name|expressions
operator|.
name|size
argument_list|()
operator|>=
literal|3
condition|)
block|{
name|formEvaluatableTriples
argument_list|(
name|expressionId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|expressions
operator|.
name|isEmpty
argument_list|()
condition|)
return|return
operator|new
name|EmptyNodeSet
argument_list|()
return|;
comment|// TODO: Should probably return nodes the satisfying the size constraint when wildcards are present
if|if
condition|(
name|expressions
operator|.
name|size
argument_list|()
operator|!=
literal|1
operator|||
operator|!
operator|(
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|instanceof
name|EvaluatableExpression
operator|)
condition|)
block|{
comment|// Should not happen.
name|LOG
operator|.
name|error
argument_list|(
literal|"Expression "
operator|+
name|toString
argument_list|()
operator|+
literal|" could not be evaluated"
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Could not evaluate wildcarded query."
argument_list|)
throw|;
block|}
name|LOG
operator|.
name|trace
argument_list|(
literal|"Evaluating expression "
operator|+
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|NodeSet
name|result
init|=
operator|(
operator|(
name|EvaluatableExpression
operator|)
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|eval
argument_list|(
name|index
argument_list|,
name|docs
argument_list|,
name|qnames
argument_list|,
name|nodeSet
argument_list|,
name|axis
argument_list|,
name|expressionId
argument_list|)
decl_stmt|;
if|if
condition|(
name|leadingWildcard
operator|!=
literal|null
condition|)
name|result
operator|=
name|expandMatchesBackward
argument_list|(
name|leadingWildcard
argument_list|,
name|result
argument_list|,
name|expressionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|startAnchorPresent
condition|)
name|result
operator|=
name|NodeSets
operator|.
name|getNodesMatchingAtStart
argument_list|(
name|result
argument_list|,
name|expressionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|trailingWildcard
operator|!=
literal|null
condition|)
name|result
operator|=
name|expandMatchesForward
argument_list|(
name|trailingWildcard
argument_list|,
name|result
argument_list|,
name|expressionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|endAnchorPresent
condition|)
name|result
operator|=
name|NodeSets
operator|.
name|getNodesMatchingAtEnd
argument_list|(
name|result
argument_list|,
name|expressionId
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|private
name|NodeSet
name|expandMatchesForward
parameter_list|(
specifier|final
name|Wildcard
name|trailingWildcard
parameter_list|,
specifier|final
name|NodeSet
name|nodes
parameter_list|,
specifier|final
name|int
name|expressionId
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|NodeSets
operator|.
name|transformNodes
argument_list|(
name|nodes
argument_list|,
name|proxy
lambda|->
name|NodeProxies
operator|.
name|transformOwnMatches
argument_list|(
name|proxy
argument_list|,
name|match
lambda|->
name|match
operator|.
name|expandForward
argument_list|(
name|trailingWildcard
operator|.
name|minimumLength
argument_list|,
name|trailingWildcard
operator|.
name|maximumLength
argument_list|,
name|proxy
operator|.
name|getNodeValue
argument_list|()
operator|.
name|length
argument_list|()
argument_list|)
argument_list|,
name|expressionId
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|NodeSet
name|expandMatchesBackward
parameter_list|(
specifier|final
name|Wildcard
name|leadingWildcard
parameter_list|,
specifier|final
name|NodeSet
name|nodes
parameter_list|,
specifier|final
name|int
name|expressionId
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|NodeSets
operator|.
name|transformNodes
argument_list|(
name|nodes
argument_list|,
name|proxy
lambda|->
name|NodeProxies
operator|.
name|transformOwnMatches
argument_list|(
name|proxy
argument_list|,
name|match
lambda|->
name|match
operator|.
name|expandBackward
argument_list|(
name|leadingWildcard
operator|.
name|minimumLength
argument_list|,
name|leadingWildcard
operator|.
name|maximumLength
argument_list|)
argument_list|,
name|expressionId
argument_list|)
argument_list|)
return|;
block|}
comment|/**      *      */
specifier|private
name|void
name|formEvaluatableTriples
parameter_list|(
specifier|final
name|int
name|expressionId
parameter_list|)
block|{
name|WildcardedExpression
name|first
init|=
name|expressions
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|WildcardedExpression
name|second
init|=
name|expressions
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|WildcardedExpression
name|third
init|=
name|expressions
operator|.
name|get
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|first
operator|instanceof
name|EvaluatableExpression
operator|&&
name|second
operator|instanceof
name|Wildcard
operator|&&
name|third
operator|instanceof
name|EvaluatableExpression
condition|)
block|{
name|WildcardedExpressionTriple
name|triple
init|=
operator|new
name|WildcardedExpressionTriple
argument_list|(
operator|(
name|EvaluatableExpression
operator|)
name|first
argument_list|,
operator|(
name|Wildcard
operator|)
name|second
argument_list|,
operator|(
name|EvaluatableExpression
operator|)
name|third
argument_list|)
decl_stmt|;
name|expressions
operator|.
name|subList
argument_list|(
literal|0
argument_list|,
literal|3
argument_list|)
operator|.
name|clear
argument_list|()
expr_stmt|;
name|expressions
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|triple
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Could not form evaluatable triples at the beginning of "
operator|+
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|builder
init|=
operator|new
name|StringBuilder
argument_list|(
literal|"WildcardedExpressionSequence("
argument_list|)
decl_stmt|;
for|for
control|(
name|WildcardedExpression
name|expression
range|:
name|expressions
control|)
block|{
name|builder
operator|.
name|append
argument_list|(
name|expression
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|builder
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|builder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|builder
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

