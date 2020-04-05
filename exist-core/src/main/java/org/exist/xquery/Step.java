begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *   *  Copyright (C) 2001-06,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|CopyOnWriteArrayList
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|Step
extends|extends
name|AbstractExpression
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|Step
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
name|int
name|axis
init|=
name|Constants
operator|.
name|UNKNOWN_AXIS
decl_stmt|;
specifier|protected
name|boolean
name|abbreviatedStep
init|=
literal|false
decl_stmt|;
specifier|protected
name|List
argument_list|<
name|Predicate
argument_list|>
name|predicates
init|=
operator|new
name|CopyOnWriteArrayList
argument_list|<>
argument_list|()
decl_stmt|;
specifier|protected
name|NodeTest
name|test
decl_stmt|;
specifier|protected
name|boolean
name|inPredicate
init|=
literal|false
decl_stmt|;
specifier|protected
name|int
name|staticReturnType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
specifier|public
name|Step
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|axis
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
block|}
specifier|public
name|Step
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|int
name|axis
parameter_list|,
name|NodeTest
name|test
parameter_list|)
block|{
name|this
argument_list|(
name|context
argument_list|,
name|axis
argument_list|)
expr_stmt|;
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
block|}
specifier|public
name|void
name|addPredicate
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|predicates
operator|.
name|add
argument_list|(
operator|(
name|Predicate
operator|)
name|expr
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|insertPredicate
parameter_list|(
name|Expression
name|previous
parameter_list|,
name|Expression
name|predicate
parameter_list|)
block|{
specifier|final
name|int
name|idx
init|=
name|predicates
operator|.
name|indexOf
argument_list|(
name|previous
argument_list|)
decl_stmt|;
if|if
condition|(
name|idx
operator|<
literal|0
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Old predicate not found: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|previous
argument_list|)
operator|+
literal|"; in: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|predicates
operator|.
name|add
argument_list|(
name|idx
operator|+
literal|1
argument_list|,
operator|(
name|Predicate
operator|)
name|predicate
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|hasPredicates
parameter_list|()
block|{
return|return
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
return|;
block|}
specifier|public
name|List
argument_list|<
name|Predicate
argument_list|>
name|getPredicates
parameter_list|()
block|{
return|return
name|predicates
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.AnalyzeContextInfo)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|test
operator|!=
literal|null
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|.
name|getPrefix
argument_list|()
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|test
operator|.
name|getName
argument_list|()
operator|.
name|getPrefix
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|)
operator|&&
name|context
operator|.
name|getInScopePrefixes
argument_list|()
operator|!=
literal|null
operator|&&
name|context
operator|.
name|getURIForPrefix
argument_list|(
name|test
operator|.
name|getName
argument_list|()
operator|.
name|getPrefix
argument_list|()
argument_list|)
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"undeclared prefix '"
operator|+
name|test
operator|.
name|getName
argument_list|()
operator|.
name|getPrefix
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
block|}
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
if|if
condition|(
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
specifier|final
name|AnalyzeContextInfo
name|newContext
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContext
operator|.
name|setStaticType
argument_list|(
name|this
operator|.
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|?
name|contextInfo
operator|.
name|getStaticType
argument_list|()
else|:
name|Type
operator|.
name|NODE
argument_list|)
expr_stmt|;
name|newContext
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|newContext
operator|.
name|setContextStep
argument_list|(
name|this
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|pred
operator|.
name|analyze
argument_list|(
name|newContext
argument_list|)
expr_stmt|;
block|}
block|}
comment|// if we are on the self axis, remember the static return type given in the context
if|if
condition|(
name|this
operator|.
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
name|staticReturnType
operator|=
name|contextInfo
operator|.
name|getStaticType
argument_list|()
expr_stmt|;
block|}
block|}
comment|/**      * Static check if the location steps first filter is a positional predicate.      * If yes, set a flag on the {@link LocationStep}      *      * @param inPredicate true if in a predicate, false otherwise      *      * @return true if the first filter is a positional predicate      */
specifier|protected
name|boolean
name|checkPositionalFilters
parameter_list|(
specifier|final
name|boolean
name|inPredicate
parameter_list|)
block|{
if|if
condition|(
operator|!
name|inPredicate
operator|&&
name|this
operator|.
name|hasPredicates
argument_list|()
condition|)
block|{
specifier|final
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
init|=
name|this
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
specifier|final
name|Predicate
name|predicate
init|=
name|preds
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
specifier|final
name|Expression
name|predExpr
init|=
name|predicate
operator|.
name|getFirst
argument_list|()
decl_stmt|;
comment|// only apply optimization if the static return type is a single number
comment|// and there are no dependencies on the context item
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|predExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|predExpr
argument_list|,
name|Dependency
operator|.
name|CONTEXT_POSITION
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|public
specifier|abstract
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
function_decl|;
specifier|public
name|int
name|getAxis
parameter_list|()
block|{
return|return
name|axis
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#setPrimaryAxis(int)      */
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
block|}
specifier|public
name|int
name|getPrimaryAxis
parameter_list|()
block|{
return|return
name|this
operator|.
name|axis
return|;
block|}
specifier|public
name|boolean
name|isAbbreviated
parameter_list|()
block|{
return|return
name|abbreviatedStep
return|;
block|}
specifier|public
name|void
name|setAbbreviated
parameter_list|(
name|boolean
name|abbrev
parameter_list|)
block|{
name|abbreviatedStep
operator|=
name|abbrev
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)      */
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
name|axis
operator|!=
name|Constants
operator|.
name|UNKNOWN_AXIS
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
name|Constants
operator|.
name|AXISSPECIFIERS
index|[
name|axis
index|]
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|"::"
argument_list|)
expr_stmt|;
if|if
condition|(
name|test
operator|!=
literal|null
condition|)
comment|//TODO : toString() or... dump ?
block|{
name|dumper
operator|.
name|display
argument_list|(
name|test
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"node()"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|pred
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
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|axis
operator|!=
name|Constants
operator|.
name|UNKNOWN_AXIS
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|Constants
operator|.
name|AXISSPECIFIERS
index|[
name|axis
index|]
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|"::"
argument_list|)
expr_stmt|;
if|if
condition|(
name|test
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
name|test
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|"node()"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|predicates
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|result
operator|.
name|append
argument_list|(
name|pred
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|result
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
comment|//Polysemy of "." which might be atomic if the context sequence is atomic itself
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
comment|//Type.ITEM by default : this may change *after* evaluation
return|return
name|staticReturnType
return|;
block|}
else|else
block|{
return|return
name|Type
operator|.
name|NODE
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Cardinality
name|getCardinality
parameter_list|()
block|{
return|return
name|Cardinality
operator|.
name|ZERO_OR_MORE
return|;
block|}
specifier|public
name|void
name|setAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
name|this
operator|.
name|axis
operator|=
name|axis
expr_stmt|;
block|}
specifier|public
name|void
name|setTest
parameter_list|(
name|NodeTest
name|test
parameter_list|)
block|{
name|this
operator|.
name|test
operator|=
name|test
expr_stmt|;
block|}
specifier|public
name|NodeTest
name|getTest
parameter_list|()
block|{
return|return
name|test
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.AbstractExpression#resetState()      */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|pred
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

