begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|range
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|range
operator|.
name|*
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
name|NodePath
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
name|*
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
name|StringValue
import|;
end_import

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

begin_comment
comment|/**  * Query rewriter for the range index. May replace path expressions like a[b = "c"] or a[b = "c"][d = "e"]  * with either a[range:equals(b, "c")] or range:field-equals(...).  */
end_comment

begin_class
specifier|public
class|class
name|RangeQueryRewriter
extends|extends
name|QueryRewriter
block|{
specifier|private
specifier|final
name|RangeIndexWorker
name|worker
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|configs
decl_stmt|;
specifier|public
name|RangeQueryRewriter
parameter_list|(
name|RangeIndexWorker
name|worker
parameter_list|,
name|List
argument_list|<
name|Object
argument_list|>
name|configs
parameter_list|,
name|XQueryContext
name|context
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|worker
operator|=
name|worker
expr_stmt|;
name|this
operator|.
name|configs
operator|=
name|configs
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|rewriteLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|locationStep
operator|.
name|hasPredicates
argument_list|()
condition|)
block|{
name|Expression
name|parentExpr
init|=
name|locationStep
operator|.
name|getParentExpression
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|parentExpr
operator|instanceof
name|RewritableExpression
operator|)
condition|)
block|{
return|return
literal|true
return|;
block|}
specifier|final
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
init|=
name|locationStep
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
comment|// get path of path expression before the predicates
name|NodePath
name|contextPath
init|=
name|toNodePath
argument_list|(
name|getPrecedingSteps
argument_list|(
name|locationStep
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|tryRewriteToFields
argument_list|(
name|locationStep
argument_list|,
operator|(
name|RewritableExpression
operator|)
name|parentExpr
argument_list|,
name|preds
argument_list|,
name|contextPath
argument_list|)
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// Step 2: process the remaining predicates
for|for
control|(
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
if|if
condition|(
name|pred
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
block|{
comment|// can only optimize predicates with one expression
break|break;
block|}
name|Expression
name|innerExpr
init|=
name|pred
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
init|=
name|getStepsToOptimize
argument_list|(
name|innerExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|steps
operator|==
literal|null
condition|)
block|{
comment|// no optimizable steps found
continue|continue;
block|}
comment|// compute left hand path
name|NodePath
name|innerPath
init|=
name|toNodePath
argument_list|(
name|steps
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerPath
operator|==
literal|null
condition|)
block|{
continue|continue;
block|}
name|NodePath
name|path
decl_stmt|;
if|if
condition|(
name|contextPath
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|innerPath
expr_stmt|;
block|}
else|else
block|{
name|path
operator|=
operator|new
name|NodePath
argument_list|(
name|contextPath
argument_list|)
expr_stmt|;
name|path
operator|.
name|append
argument_list|(
name|innerPath
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// find a range index configuration matching the full path to the predicate expression
name|RangeIndexConfigElement
name|rice
init|=
name|findConfiguration
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
decl_stmt|;
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
operator|!
name|rice
operator|.
name|isComplex
argument_list|()
condition|)
block|{
comment|// found simple index configuration: replace with call to lookup function
comment|// collect arguments
name|Lookup
name|func
init|=
name|rewrite
argument_list|(
name|innerExpr
argument_list|)
decl_stmt|;
comment|// preserve original comparison: may need it for in-memory lookups
name|func
operator|.
name|setFallback
argument_list|(
name|innerExpr
argument_list|)
expr_stmt|;
name|func
operator|.
name|setLocation
argument_list|(
name|innerExpr
operator|.
name|getLine
argument_list|()
argument_list|,
name|innerExpr
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
comment|// replace comparison with range:eq
name|pred
operator|.
name|replace
argument_list|(
name|innerExpr
argument_list|,
operator|new
name|InternalFunctionCall
argument_list|(
name|func
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
return|return
literal|true
return|;
block|}
specifier|private
name|boolean
name|tryRewriteToFields
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|,
name|RewritableExpression
name|parentExpr
parameter_list|,
name|List
argument_list|<
name|Predicate
argument_list|>
name|preds
parameter_list|,
name|NodePath
name|contextPath
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// without context path, we cannot rewrite the entire query
if|if
condition|(
name|contextPath
operator|!=
literal|null
condition|)
block|{
name|RangeIndex
operator|.
name|Operator
name|operator
init|=
literal|null
decl_stmt|;
name|List
argument_list|<
name|Expression
argument_list|>
name|args
init|=
literal|null
decl_stmt|;
name|SequenceConstructor
name|arg0
init|=
literal|null
decl_stmt|;
name|SequenceConstructor
name|arg1
init|=
literal|null
decl_stmt|;
comment|// walk through the predicates attached to the current location step
comment|// check if expression can be optimized
for|for
control|(
specifier|final
name|Predicate
name|pred
range|:
name|preds
control|)
block|{
if|if
condition|(
name|pred
operator|.
name|getLength
argument_list|()
operator|!=
literal|1
condition|)
block|{
comment|// can only optimize predicates with one expression
return|return
literal|false
return|;
block|}
name|Expression
name|innerExpr
init|=
name|pred
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
init|=
name|getStepsToOptimize
argument_list|(
name|innerExpr
argument_list|)
decl_stmt|;
if|if
condition|(
name|steps
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
comment|// compute left hand path
name|NodePath
name|innerPath
init|=
name|toNodePath
argument_list|(
name|steps
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerPath
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|(
name|contextPath
argument_list|)
decl_stmt|;
name|path
operator|.
name|append
argument_list|(
name|innerPath
argument_list|)
expr_stmt|;
if|if
condition|(
name|path
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// find a range index configuration matching the full path to the predicate expression
name|RangeIndexConfigElement
name|rice
init|=
name|findConfiguration
argument_list|(
name|path
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|// found index configuration with sub-fields
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
name|rice
operator|.
name|isComplex
argument_list|()
operator|&&
name|rice
operator|.
name|getNodePath
argument_list|()
operator|.
name|match
argument_list|(
name|contextPath
argument_list|)
operator|&&
name|findConfiguration
argument_list|(
name|path
argument_list|,
literal|false
argument_list|)
operator|==
literal|null
condition|)
block|{
comment|// check for a matching sub-path and retrieve field information
name|RangeIndexConfigField
name|field
init|=
operator|(
operator|(
name|ComplexRangeIndexConfigElement
operator|)
name|rice
operator|)
operator|.
name|getField
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|field
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|args
operator|==
literal|null
condition|)
block|{
comment|// initialize args
name|args
operator|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|(
literal|4
argument_list|)
expr_stmt|;
name|arg0
operator|=
operator|new
name|SequenceConstructor
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
name|arg1
operator|=
operator|new
name|SequenceConstructor
argument_list|(
name|getContext
argument_list|()
argument_list|)
expr_stmt|;
name|args
operator|.
name|add
argument_list|(
name|arg1
argument_list|)
expr_stmt|;
block|}
comment|// field is added to the sequence in first parameter
name|arg0
operator|.
name|add
argument_list|(
operator|new
name|LiteralValue
argument_list|(
name|getContext
argument_list|()
argument_list|,
operator|new
name|StringValue
argument_list|(
name|field
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// operator
name|arg1
operator|.
name|add
argument_list|(
operator|new
name|LiteralValue
argument_list|(
name|getContext
argument_list|()
argument_list|,
operator|new
name|StringValue
argument_list|(
name|getOperator
argument_list|(
name|innerExpr
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|// append right hand expression as additional parameter
name|args
operator|.
name|add
argument_list|(
name|getKeyArg
argument_list|(
name|innerExpr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
if|if
condition|(
name|args
operator|!=
literal|null
condition|)
block|{
comment|// the entire filter expression can be replaced
name|RewritableExpression
name|parent
init|=
name|parentExpr
decl_stmt|;
comment|// create range:field-equals function
name|FieldLookup
name|func
init|=
operator|new
name|FieldLookup
argument_list|(
name|getContext
argument_list|()
argument_list|,
name|FieldLookup
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|func
operator|.
name|setFallback
argument_list|(
name|locationStep
argument_list|)
expr_stmt|;
name|func
operator|.
name|setLocation
argument_list|(
name|locationStep
operator|.
name|getLine
argument_list|()
argument_list|,
name|locationStep
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|func
operator|.
name|setArguments
argument_list|(
name|args
argument_list|)
expr_stmt|;
name|parent
operator|.
name|replace
argument_list|(
name|locationStep
argument_list|,
operator|new
name|InternalFunctionCall
argument_list|(
name|func
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
return|;
block|}
specifier|private
name|Lookup
name|rewrite
parameter_list|(
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
name|ArrayList
argument_list|<
name|Expression
argument_list|>
name|eqArgs
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|(
literal|2
argument_list|)
decl_stmt|;
if|if
condition|(
name|expression
operator|instanceof
name|GeneralComparison
condition|)
block|{
name|GeneralComparison
name|comparison
init|=
operator|(
name|GeneralComparison
operator|)
name|expression
decl_stmt|;
name|eqArgs
operator|.
name|add
argument_list|(
name|comparison
operator|.
name|getLeft
argument_list|()
argument_list|)
expr_stmt|;
name|eqArgs
operator|.
name|add
argument_list|(
name|comparison
operator|.
name|getRight
argument_list|()
argument_list|)
expr_stmt|;
name|Lookup
name|func
init|=
name|Lookup
operator|.
name|create
argument_list|(
name|comparison
operator|.
name|getContext
argument_list|()
argument_list|,
name|getOperator
argument_list|(
name|expression
argument_list|)
argument_list|)
decl_stmt|;
name|func
operator|.
name|setArguments
argument_list|(
name|eqArgs
argument_list|)
expr_stmt|;
return|return
name|func
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|Expression
name|getKeyArg
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
if|if
condition|(
name|expression
operator|instanceof
name|GeneralComparison
condition|)
block|{
return|return
operator|(
operator|(
name|GeneralComparison
operator|)
name|expression
operator|)
operator|.
name|getRight
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|List
argument_list|<
name|LocationStep
argument_list|>
name|getStepsToOptimize
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|expr
operator|instanceof
name|GeneralComparison
condition|)
block|{
name|GeneralComparison
name|comparison
init|=
operator|(
name|GeneralComparison
operator|)
name|expr
decl_stmt|;
return|return
name|BasicExpressionVisitor
operator|.
name|findLocationSteps
argument_list|(
name|comparison
operator|.
name|getLeft
argument_list|()
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|RangeIndex
operator|.
name|Operator
name|getOperator
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|RangeIndex
operator|.
name|Operator
name|operator
init|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|EQ
decl_stmt|;
if|if
condition|(
name|expr
operator|instanceof
name|GeneralComparison
condition|)
block|{
name|GeneralComparison
name|comparison
init|=
operator|(
name|GeneralComparison
operator|)
name|expr
decl_stmt|;
name|int
name|relation
init|=
name|comparison
operator|.
name|getRelation
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|relation
condition|)
block|{
case|case
name|Constants
operator|.
name|LT
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|LT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|GT
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|GT
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|LTEQ
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|LE
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|GTEQ
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|GE
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|EQ
case|:
switch|switch
condition|(
name|comparison
operator|.
name|getTruncation
argument_list|()
condition|)
block|{
case|case
name|Constants
operator|.
name|TRUNC_BOTH
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|CONTAINS
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|TRUNC_LEFT
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|ENDS_WITH
expr_stmt|;
break|break;
case|case
name|Constants
operator|.
name|TRUNC_RIGHT
case|:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|STARTS_WITH
expr_stmt|;
break|break;
default|default:
name|operator
operator|=
name|RangeIndex
operator|.
name|Operator
operator|.
name|EQ
expr_stmt|;
break|break;
block|}
break|break;
block|}
block|}
return|return
name|operator
return|;
block|}
comment|/**      * Scan all index configurations to find one matching path.      */
specifier|private
name|RangeIndexConfigElement
name|findConfiguration
parameter_list|(
name|NodePath
name|path
parameter_list|,
name|boolean
name|complex
parameter_list|)
block|{
for|for
control|(
name|Object
name|configObj
range|:
name|configs
control|)
block|{
specifier|final
name|RangeIndexConfig
name|config
init|=
operator|(
name|RangeIndexConfig
operator|)
name|configObj
decl_stmt|;
specifier|final
name|RangeIndexConfigElement
name|rice
init|=
name|config
operator|.
name|find
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|rice
operator|!=
literal|null
operator|&&
operator|(
operator|(
name|complex
operator|&&
name|rice
operator|.
name|isComplex
argument_list|()
operator|)
operator|||
operator|(
operator|!
name|complex
operator|&&
operator|!
name|rice
operator|.
name|isComplex
argument_list|()
operator|)
operator|)
condition|)
block|{
return|return
name|rice
return|;
block|}
block|}
return|return
literal|null
return|;
block|}
specifier|private
name|NodePath
name|toNodePath
parameter_list|(
name|List
argument_list|<
name|LocationStep
argument_list|>
name|steps
parameter_list|)
block|{
name|NodePath
name|path
init|=
operator|new
name|NodePath
argument_list|()
decl_stmt|;
for|for
control|(
name|LocationStep
name|step
range|:
name|steps
control|)
block|{
if|if
condition|(
name|step
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|NodeTest
name|test
init|=
name|step
operator|.
name|getTest
argument_list|()
decl_stmt|;
if|if
condition|(
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|step
operator|.
name|getAxis
argument_list|()
operator|==
name|Constants
operator|.
name|SELF_AXIS
condition|)
block|{
comment|//return path;
continue|continue;
block|}
if|if
condition|(
operator|!
name|test
operator|.
name|isWildcardTest
argument_list|()
operator|&&
name|test
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|int
name|axis
init|=
name|step
operator|.
name|getAxis
argument_list|()
decl_stmt|;
if|if
condition|(
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_SELF_AXIS
condition|)
block|{
name|path
operator|.
name|addComponent
argument_list|(
name|NodePath
operator|.
name|SKIP
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|axis
operator|!=
name|Constants
operator|.
name|CHILD_AXIS
operator|&&
name|axis
operator|!=
name|Constants
operator|.
name|ATTRIBUTE_AXIS
condition|)
block|{
return|return
literal|null
return|;
comment|// not optimizable
block|}
name|path
operator|.
name|addComponent
argument_list|(
name|test
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|path
return|;
block|}
specifier|private
name|List
argument_list|<
name|LocationStep
argument_list|>
name|getPrecedingSteps
parameter_list|(
name|LocationStep
name|current
parameter_list|)
block|{
name|Expression
name|parentExpr
init|=
name|current
operator|.
name|getParentExpression
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|parentExpr
operator|instanceof
name|RewritableExpression
operator|)
condition|)
block|{
return|return
literal|null
return|;
block|}
specifier|final
name|List
argument_list|<
name|LocationStep
argument_list|>
name|prevSteps
init|=
operator|new
name|ArrayList
argument_list|<
name|LocationStep
argument_list|>
argument_list|()
decl_stmt|;
name|prevSteps
operator|.
name|add
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|RewritableExpression
name|parent
init|=
operator|(
name|RewritableExpression
operator|)
name|parentExpr
decl_stmt|;
name|Expression
name|previous
init|=
name|parent
operator|.
name|getPrevious
argument_list|(
name|current
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
operator|!=
literal|null
condition|)
block|{
while|while
condition|(
name|previous
operator|!=
literal|null
operator|&&
name|previous
operator|!=
name|parent
operator|.
name|getFirst
argument_list|()
operator|&&
name|previous
operator|instanceof
name|LocationStep
condition|)
block|{
specifier|final
name|LocationStep
name|prevStep
init|=
operator|(
name|LocationStep
operator|)
name|previous
decl_stmt|;
name|prevSteps
operator|.
name|add
argument_list|(
literal|0
argument_list|,
name|prevStep
argument_list|)
expr_stmt|;
name|previous
operator|=
name|parent
operator|.
name|getPrevious
argument_list|(
name|previous
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|prevSteps
return|;
block|}
block|}
end_class

end_unit

