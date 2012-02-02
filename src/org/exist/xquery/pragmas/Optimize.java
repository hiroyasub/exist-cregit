begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  \$Id\$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|pragmas
package|;
end_package

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
name|Namespaces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
name|StructuralIndex
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
name|QNameRangeIndexSpec
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|functions
operator|.
name|fn
operator|.
name|ExtFulltext
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
name|Iterator
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

begin_class
specifier|public
class|class
name|Optimize
extends|extends
name|Pragma
block|{
specifier|public
specifier|final
specifier|static
name|QName
name|OPTIMIZE_PRAGMA
init|=
operator|new
name|QName
argument_list|(
literal|"optimize"
argument_list|,
name|Namespaces
operator|.
name|EXIST_NS
argument_list|,
literal|"exist"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Optimize
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|enabled
init|=
literal|true
decl_stmt|;
specifier|private
name|XQueryContext
name|context
decl_stmt|;
specifier|private
name|Optimizable
name|optimizables
index|[]
decl_stmt|;
specifier|private
name|Expression
name|innerExpr
init|=
literal|null
decl_stmt|;
specifier|private
name|LocationStep
name|contextStep
init|=
literal|null
decl_stmt|;
specifier|private
name|VariableReference
name|contextVar
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|contextId
init|=
name|Expression
operator|.
name|NO_CONTEXT_ID
decl_stmt|;
specifier|private
name|NodeSet
name|cachedContext
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|cachedTimestamp
decl_stmt|;
specifier|private
name|boolean
name|cachedOptimize
decl_stmt|;
specifier|public
name|Optimize
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|QName
name|pragmaName
parameter_list|,
name|String
name|contents
parameter_list|,
name|boolean
name|explicit
parameter_list|)
throws|throws
name|XPathException
block|{
name|super
argument_list|(
name|pragmaName
argument_list|,
name|contents
argument_list|)
expr_stmt|;
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|this
operator|.
name|enabled
operator|=
name|explicit
operator|||
name|context
operator|.
name|optimizationsEnabled
argument_list|()
expr_stmt|;
if|if
condition|(
name|contents
operator|!=
literal|null
operator|&&
name|contents
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|String
name|param
index|[]
init|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|contents
argument_list|)
decl_stmt|;
if|if
condition|(
name|param
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Invalid content found for pragma exist:optimize: "
operator|+
name|contents
argument_list|)
throw|;
if|if
condition|(
literal|"enable"
operator|.
name|equals
argument_list|(
name|param
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|enabled
operator|=
literal|"yes"
operator|.
name|equals
argument_list|(
name|param
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
block|}
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
name|super
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
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
name|useCached
init|=
literal|false
decl_stmt|;
name|boolean
name|optimize
init|=
literal|false
decl_stmt|;
name|NodeSet
name|originalContext
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
condition|)
block|{
comment|// don't try to optimize in-memory node sets!
comment|// contextSequence will be overwritten
name|originalContext
operator|=
name|contextSequence
operator|==
literal|null
condition|?
literal|null
else|:
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
expr_stmt|;
if|if
condition|(
name|cachedContext
operator|!=
literal|null
operator|&&
name|cachedContext
operator|==
name|originalContext
condition|)
name|useCached
operator|=
operator|!
name|originalContext
operator|.
name|hasChanged
argument_list|(
name|cachedTimestamp
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextVar
operator|!=
literal|null
condition|)
block|{
name|contextSequence
operator|=
name|contextVar
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
block|}
comment|// check if all Optimizable expressions signal that they can indeed optimize
comment|// in the current context
if|if
condition|(
name|useCached
condition|)
name|optimize
operator|=
name|cachedOptimize
expr_stmt|;
else|else
block|{
if|if
condition|(
name|optimizables
operator|!=
literal|null
operator|&&
name|optimizables
operator|.
name|length
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|optimizables
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|optimizables
index|[
name|i
index|]
operator|.
name|canOptimize
argument_list|(
name|contextSequence
argument_list|)
condition|)
name|optimize
operator|=
literal|true
expr_stmt|;
else|else
block|{
name|optimize
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
block|}
if|if
condition|(
name|optimize
condition|)
block|{
name|cachedContext
operator|=
name|originalContext
expr_stmt|;
name|cachedTimestamp
operator|=
name|originalContext
operator|==
literal|null
condition|?
literal|0
else|:
name|originalContext
operator|.
name|getState
argument_list|()
expr_stmt|;
name|cachedOptimize
operator|=
literal|true
expr_stmt|;
name|NodeSet
name|ancestors
decl_stmt|;
name|NodeSet
name|result
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|current
init|=
literal|0
init|;
name|current
operator|<
name|optimizables
operator|.
name|length
condition|;
name|current
operator|++
control|)
block|{
name|NodeSet
name|selection
init|=
name|optimizables
index|[
name|current
index|]
operator|.
name|preSelect
argument_list|(
name|contextSequence
argument_list|,
name|current
operator|>
literal|0
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: pre-selection: "
operator|+
name|selection
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
comment|// determine the set of potential ancestors for which the predicate has to
comment|// be re-evaluated to filter out wrong matches
if|if
condition|(
name|selection
operator|.
name|isEmpty
argument_list|()
condition|)
name|ancestors
operator|=
name|selection
expr_stmt|;
if|else if
condition|(
name|contextStep
operator|==
literal|null
operator|||
name|current
operator|>
literal|0
condition|)
block|{
name|ancestors
operator|=
name|selection
operator|.
name|selectAncestorDescendant
argument_list|(
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|NodeSet
operator|.
name|ANCESTOR
argument_list|,
literal|true
argument_list|,
name|contextId
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|//                    NodeSelector selector;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|//                    selector = new AncestorSelector(selection, contextId, true, false);
name|StructuralIndex
name|index
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getStructuralIndex
argument_list|()
decl_stmt|;
name|QName
name|ancestorQN
init|=
name|contextStep
operator|.
name|getTest
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|optimizables
index|[
name|current
index|]
operator|.
name|optimizeOnSelf
argument_list|()
condition|)
block|{
name|ancestors
operator|=
name|index
operator|.
name|findAncestorsByTagName
argument_list|(
name|ancestorQN
operator|.
name|getNameType
argument_list|()
argument_list|,
name|ancestorQN
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|,
name|selection
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|selection
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|ancestors
operator|=
name|index
operator|.
name|findAncestorsByTagName
argument_list|(
name|ancestorQN
operator|.
name|getNameType
argument_list|()
argument_list|,
name|ancestorQN
argument_list|,
name|optimizables
index|[
name|current
index|]
operator|.
name|optimizeOnChild
argument_list|()
condition|?
name|Constants
operator|.
name|PARENT_AXIS
else|:
name|Constants
operator|.
name|ANCESTOR_SELF_AXIS
argument_list|,
name|selection
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|selection
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"Ancestor selection took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"Found: "
operator|+
name|ancestors
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
name|result
operator|=
name|ancestors
expr_stmt|;
name|contextSequence
operator|=
name|result
expr_stmt|;
block|}
if|if
condition|(
name|contextStep
operator|==
literal|null
condition|)
block|{
return|return
name|innerExpr
operator|.
name|eval
argument_list|(
name|result
argument_list|)
return|;
block|}
else|else
block|{
name|contextStep
operator|.
name|setPreloadedData
argument_list|(
name|result
operator|.
name|getDocumentSet
argument_list|()
argument_list|,
name|result
argument_list|)
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: context after optimize: "
operator|+
name|result
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|originalContext
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|originalContext
operator|.
name|filterDocuments
argument_list|(
name|result
argument_list|)
expr_stmt|;
else|else
name|contextSequence
operator|=
literal|null
expr_stmt|;
name|Sequence
name|seq
init|=
name|innerExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: inner expr took "
operator|+
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
operator|)
operator|+
literal|"; found: "
operator|+
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|seq
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: Cannot optimize expression."
argument_list|)
expr_stmt|;
if|if
condition|(
name|originalContext
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|originalContext
expr_stmt|;
return|return
name|innerExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
block|}
specifier|public
name|void
name|before
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|innerExpr
operator|!=
literal|null
condition|)
return|return;
name|innerExpr
operator|=
name|expression
expr_stmt|;
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
name|innerExpr
operator|.
name|accept
argument_list|(
operator|new
name|BasicExpressionVisitor
argument_list|()
block|{
specifier|public
name|void
name|visitPathExpr
parameter_list|(
name|PathExpr
name|expression
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|expression
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|next
init|=
name|expression
operator|.
name|getExpression
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|next
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visitLocationStep
parameter_list|(
name|LocationStep
name|locationStep
parameter_list|)
block|{
name|List
argument_list|<
name|Predicate
argument_list|>
name|predicates
init|=
name|locationStep
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
for|for
control|(
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|pred
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visitFilteredExpr
parameter_list|(
name|FilteredExpression
name|filtered
parameter_list|)
block|{
name|Expression
name|filteredExpr
init|=
name|filtered
operator|.
name|getExpression
argument_list|()
decl_stmt|;
if|if
condition|(
name|filteredExpr
operator|instanceof
name|VariableReference
condition|)
name|contextVar
operator|=
operator|(
name|VariableReference
operator|)
name|filteredExpr
expr_stmt|;
name|List
argument_list|<
name|Predicate
argument_list|>
name|predicates
init|=
name|filtered
operator|.
name|getPredicates
argument_list|()
decl_stmt|;
for|for
control|(
name|Predicate
name|pred
range|:
name|predicates
control|)
block|{
name|pred
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|visit
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|super
operator|.
name|visit
argument_list|(
name|expression
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitFtExpression
parameter_list|(
name|ExtFulltext
name|fulltext
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: found optimizable: "
operator|+
name|fulltext
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|addOptimizable
argument_list|(
name|fulltext
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitGeneralComparison
parameter_list|(
name|GeneralComparison
name|comparison
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: found optimizable: "
operator|+
name|comparison
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|addOptimizable
argument_list|(
name|comparison
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitPredicate
parameter_list|(
name|Predicate
name|predicate
parameter_list|)
block|{
name|predicate
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
operator|.
name|accept
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|visitBuiltinFunction
parameter_list|(
name|Function
name|function
parameter_list|)
block|{
if|if
condition|(
name|function
operator|instanceof
name|Optimizable
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: found optimizable function: "
operator|+
name|function
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|addOptimizable
argument_list|(
operator|(
name|Optimizable
operator|)
name|function
argument_list|)
expr_stmt|;
block|}
block|}
block|}
argument_list|)
expr_stmt|;
name|contextStep
operator|=
name|BasicExpressionVisitor
operator|.
name|findFirstStep
argument_list|(
name|innerExpr
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextStep
operator|!=
literal|null
operator|&&
name|contextStep
operator|.
name|getTest
argument_list|()
operator|.
name|isWildcardTest
argument_list|()
condition|)
name|contextStep
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: context step: "
operator|+
name|contextStep
argument_list|)
expr_stmt|;
name|LOG
operator|.
name|trace
argument_list|(
literal|"exist:optimize: context var: "
operator|+
name|contextVar
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|after
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Expression
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
specifier|private
name|void
name|addOptimizable
parameter_list|(
name|Optimizable
name|optimizable
parameter_list|)
block|{
name|int
name|axis
init|=
name|optimizable
operator|.
name|getOptimizeAxis
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
operator|(
name|axis
operator|==
name|Constants
operator|.
name|CHILD_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|SELF_AXIS
operator|||
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
operator|||
name|axis
operator|==
name|Constants
operator|.
name|ATTRIBUTE_AXIS
operator|||
name|axis
operator|==
name|Constants
operator|.
name|DESCENDANT_ATTRIBUTE_AXIS
operator|)
condition|)
block|{
comment|// reverse axes cannot be optimized
return|return;
block|}
if|if
condition|(
name|optimizables
operator|==
literal|null
condition|)
block|{
name|optimizables
operator|=
operator|new
name|Optimizable
index|[
literal|1
index|]
expr_stmt|;
name|optimizables
index|[
literal|0
index|]
operator|=
name|optimizable
expr_stmt|;
block|}
else|else
block|{
name|Optimizable
name|o
index|[]
init|=
operator|new
name|Optimizable
index|[
name|optimizables
operator|.
name|length
operator|+
literal|1
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|optimizables
argument_list|,
literal|0
argument_list|,
name|o
argument_list|,
literal|0
argument_list|,
name|optimizables
operator|.
name|length
argument_list|)
expr_stmt|;
name|o
index|[
name|optimizables
operator|.
name|length
index|]
operator|=
name|optimizable
expr_stmt|;
name|optimizables
operator|=
name|o
expr_stmt|;
block|}
block|}
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
name|cachedContext
operator|=
literal|null
expr_stmt|;
block|}
comment|/**      * Check every collection in the context sequence for an existing range index by QName.      *      * @param contextSequence      * @return the type of a usable index or {@link org.exist.xquery.value.Type#ITEM} if there is no common      *  index.      */
specifier|public
specifier|static
name|int
name|getQNameIndexType
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|QName
name|qname
parameter_list|)
block|{
if|if
condition|(
name|contextSequence
operator|==
literal|null
operator|||
name|qname
operator|==
literal|null
condition|)
return|return
name|Type
operator|.
name|ITEM
return|;
name|String
name|enforceIndexUse
init|=
operator|(
name|String
operator|)
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|XQueryContext
operator|.
name|PROPERTY_ENFORCE_INDEX_USE
argument_list|)
decl_stmt|;
name|int
name|indexType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
for|for
control|(
name|Iterator
argument_list|<
name|Collection
argument_list|>
name|i
init|=
name|contextSequence
operator|.
name|getCollectionIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Collection
name|collection
init|=
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|collection
operator|.
name|getURI
argument_list|()
operator|.
name|startsWith
argument_list|(
name|XmldbURI
operator|.
name|SYSTEM_COLLECTION_URI
argument_list|)
condition|)
continue|continue;
name|QNameRangeIndexSpec
name|config
init|=
name|collection
operator|.
name|getIndexByQNameConfiguration
argument_list|(
name|context
operator|.
name|getBroker
argument_list|()
argument_list|,
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|config
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"Cannot optimize: collection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|" does not define an index "
operator|+
literal|"on "
operator|+
name|qname
argument_list|)
expr_stmt|;
return|return
name|Type
operator|.
name|ITEM
return|;
comment|// found a collection without index
block|}
name|int
name|type
init|=
name|config
operator|.
name|getType
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexType
operator|==
name|Type
operator|.
name|ITEM
condition|)
block|{
name|indexType
operator|=
name|type
expr_stmt|;
if|if
condition|(
name|enforceIndexUse
operator|!=
literal|null
operator|&&
literal|"always"
operator|.
name|equals
argument_list|(
name|enforceIndexUse
argument_list|)
condition|)
return|return
name|indexType
return|;
block|}
if|else if
condition|(
name|indexType
operator|!=
name|type
condition|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isTraceEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|trace
argument_list|(
literal|"Cannot optimize: collection "
operator|+
name|collection
operator|.
name|getURI
argument_list|()
operator|+
literal|" does not define an index "
operator|+
literal|"with the required type "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|type
argument_list|)
operator|+
literal|" on "
operator|+
name|qname
argument_list|)
expr_stmt|;
return|return
name|Type
operator|.
name|ITEM
return|;
comment|// found a collection with a different type
block|}
block|}
return|return
name|indexType
return|;
block|}
block|}
end_class

end_unit

