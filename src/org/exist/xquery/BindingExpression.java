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
name|dom
operator|.
name|StoredNode
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
name|VirtualNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|numbering
operator|.
name|NodeId
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
name|UpdateListener
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
name|GroupedValueSequenceTable
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
name|SequenceIterator
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
name|SequenceType
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
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|ValueSequence
import|;
end_import

begin_comment
comment|/**  * Abstract superclass for the variable binding expressions "for" and "let".  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|BindingExpression
extends|extends
name|AbstractExpression
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|BindingExpression
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|SequenceType
name|POSITIONAL_VAR_TYPE
init|=
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
decl_stmt|;
specifier|protected
name|String
name|varName
decl_stmt|;
specifier|protected
name|SequenceType
name|sequenceType
init|=
literal|null
decl_stmt|;
specifier|protected
name|Expression
name|inputSequence
decl_stmt|;
specifier|protected
name|Expression
name|returnExpr
decl_stmt|;
specifier|protected
name|Expression
name|whereExpr
decl_stmt|;
specifier|protected
name|OrderSpec
name|orderSpecs
index|[]
init|=
literal|null
decl_stmt|;
specifier|protected
name|int
name|actualReturnType
init|=
name|Type
operator|.
name|ITEM
decl_stmt|;
comment|/* bv : variables for group by  	    group toGroupVarName as groupVarName as groupSpecs... return groupReturnExpr */
specifier|protected
name|GroupSpec
name|groupSpecs
index|[]
init|=
literal|null
decl_stmt|;
specifier|protected
name|Expression
name|groupReturnExpr
decl_stmt|;
specifier|protected
name|String
name|groupVarName
decl_stmt|;
specifier|protected
name|String
name|toGroupVarName
decl_stmt|;
specifier|private
name|ExprUpdateListener
name|listener
decl_stmt|;
specifier|public
name|BindingExpression
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
name|void
name|setVariable
parameter_list|(
name|String
name|qname
parameter_list|)
block|{
name|varName
operator|=
name|qname
expr_stmt|;
block|}
specifier|public
name|String
name|getVariable
parameter_list|()
block|{
return|return
name|this
operator|.
name|varName
return|;
block|}
comment|/** 	 * Set the sequence type of the variable (as specified in the "as" clause). 	 *  	 * @param type 	 */
specifier|public
name|void
name|setSequenceType
parameter_list|(
name|SequenceType
name|type
parameter_list|)
block|{
name|this
operator|.
name|sequenceType
operator|=
name|type
expr_stmt|;
block|}
specifier|public
name|void
name|setInputSequence
parameter_list|(
name|Expression
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|inputSequence
operator|=
name|sequence
expr_stmt|;
block|}
specifier|public
name|Expression
name|getInputSequence
parameter_list|()
block|{
return|return
name|this
operator|.
name|inputSequence
return|;
block|}
specifier|public
name|void
name|setReturnExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|returnExpr
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|Expression
name|getReturnExpression
parameter_list|()
block|{
return|return
name|this
operator|.
name|returnExpr
return|;
block|}
specifier|public
name|void
name|setWhereExpression
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|whereExpr
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|Expression
name|getWhereExpression
parameter_list|()
block|{
return|return
name|this
operator|.
name|whereExpr
return|;
block|}
specifier|public
name|void
name|setOrderSpecs
parameter_list|(
name|OrderSpec
name|specs
index|[]
parameter_list|)
block|{
name|this
operator|.
name|orderSpecs
operator|=
name|specs
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupSpecs
parameter_list|(
name|GroupSpec
name|specs
index|[]
parameter_list|)
block|{
name|this
operator|.
name|groupSpecs
operator|=
name|specs
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupReturnExpr
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|this
operator|.
name|groupReturnExpr
operator|=
name|expr
expr_stmt|;
block|}
specifier|public
name|void
name|setGroupVariable
parameter_list|(
name|String
name|qname
parameter_list|)
block|{
name|groupVarName
operator|=
name|qname
expr_stmt|;
block|}
specifier|public
name|void
name|setToGroupVariable
parameter_list|(
name|String
name|qname
parameter_list|)
block|{
name|toGroupVarName
operator|=
name|qname
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression, int)      */
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
name|unordered
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|UNORDERED
operator|)
operator|>
literal|0
expr_stmt|;
name|analyze
argument_list|(
name|contextInfo
argument_list|,
name|orderSpecs
argument_list|,
name|groupSpecs
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|analyze
parameter_list|(
name|AnalyzeContextInfo
name|contextInfo
parameter_list|,
name|OrderSpec
name|orderBy
index|[]
parameter_list|,
name|GroupSpec
name|groupBy
index|[]
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#eval(org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
return|return
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
parameter_list|,
name|Sequence
name|resultSequence
parameter_list|,
name|GroupedValueSequenceTable
name|groupedSequence
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|protected
name|Sequence
name|applyWhereExpression
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|contextSequence
operator|!=
literal|null
operator|&&
name|Type
operator|.
name|subTypeOf
argument_list|(
name|contextSequence
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|&&
name|contextSequence
operator|.
name|isPersistentSet
argument_list|()
operator|&&
comment|//We might not be sure of the return type at this level
name|Type
operator|.
name|subTypeOf
argument_list|(
name|whereExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|ITEM
argument_list|)
condition|)
block|{
name|Sequence
name|seq
init|=
name|whereExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
comment|//But *now*, we are ;-)
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|whereExpr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
name|NodeSet
name|nodes
init|=
name|seq
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
comment|// if the where expression returns a node set, check the context
comment|// node of each node in the set
name|NodeSet
name|contextSet
init|=
name|contextSequence
operator|.
name|toNodeSet
argument_list|()
decl_stmt|;
name|boolean
name|contextIsVirtual
init|=
name|contextSet
operator|instanceof
name|VirtualNodeSet
decl_stmt|;
name|NodeSet
name|result
init|=
operator|new
name|ExtArrayNodeSet
argument_list|()
decl_stmt|;
name|DocumentImpl
name|lastDoc
init|=
literal|null
decl_stmt|;
for|for
control|(
name|NodeProxy
name|current
range|:
name|nodes
control|)
block|{
name|int
name|sizeHint
init|=
name|Constants
operator|.
name|NO_SIZE_HINT
decl_stmt|;
if|if
condition|(
name|lastDoc
operator|==
literal|null
operator|||
name|current
operator|.
name|getDocument
argument_list|()
operator|!=
name|lastDoc
condition|)
block|{
name|lastDoc
operator|=
name|current
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|sizeHint
operator|=
name|nodes
operator|.
name|getSizeHint
argument_list|(
name|lastDoc
argument_list|)
expr_stmt|;
block|}
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
name|this
argument_list|,
literal|"Internal evaluation error: context node is missing for node "
operator|+
name|current
operator|.
name|getNodeId
argument_list|()
operator|+
literal|"!"
argument_list|)
throw|;
block|}
comment|//				LOG.debug(current.debugContext());
while|while
condition|(
name|context
operator|!=
literal|null
condition|)
block|{
comment|//TODO : Is this the context we want ? Not sure... would have prefered the LetExpr.
if|if
condition|(
name|context
operator|.
name|getContextId
argument_list|()
operator|==
name|whereExpr
operator|.
name|getContextId
argument_list|()
condition|)
block|{
name|NodeProxy
name|contextNode
init|=
name|context
operator|.
name|getNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|contextIsVirtual
operator|||
name|contextSet
operator|.
name|contains
argument_list|(
name|contextNode
argument_list|)
condition|)
block|{
name|contextNode
operator|.
name|addMatches
argument_list|(
name|current
argument_list|)
expr_stmt|;
name|result
operator|.
name|add
argument_list|(
name|contextNode
argument_list|,
name|sizeHint
argument_list|)
expr_stmt|;
block|}
block|}
name|context
operator|=
name|context
operator|.
name|getNextDirect
argument_list|()
expr_stmt|;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
if|if
condition|(
name|contextSequence
operator|==
literal|null
condition|)
block|{
name|Sequence
name|innerSeq
init|=
name|whereExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
return|return
name|innerSeq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|?
name|BooleanValue
operator|.
name|TRUE
else|:
name|BooleanValue
operator|.
name|FALSE
return|;
block|}
else|else
block|{
comment|// general where clause: just check the effective boolean value
name|ValueSequence
name|result
init|=
operator|new
name|ValueSequence
argument_list|()
decl_stmt|;
name|int
name|p
init|=
literal|0
decl_stmt|;
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
name|p
operator|++
control|)
block|{
name|Item
name|item
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|context
operator|.
name|setContextSequencePosition
argument_list|(
name|p
argument_list|,
name|contextSequence
argument_list|)
expr_stmt|;
name|Sequence
name|innerSeq
init|=
name|whereExpr
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|item
argument_list|)
decl_stmt|;
if|if
condition|(
name|innerSeq
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
name|result
operator|.
name|add
argument_list|(
name|item
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
comment|/** 	 * Check all order specs to see if we can process them in 	 * one single step. In general, this is possible if all order  	 * expressions return nodes. 	 *  	 * @return Whether or not the order specs can be processed in one signle step. 	 */
specifier|protected
name|boolean
name|checkOrderSpecs
parameter_list|(
name|Sequence
name|in
parameter_list|)
block|{
if|if
condition|(
name|orderSpecs
operator|==
literal|null
condition|)
return|return
literal|false
return|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|in
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
return|return
literal|false
return|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|orderSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Expression
name|expr
init|=
name|orderSpecs
index|[
name|i
index|]
operator|.
name|getSortExpression
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|expr
operator|.
name|returnsType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
operator|||
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|expr
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
condition|)
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#preselect(org.exist.dom.DocumentSet, org.exist.xquery.StaticContext) 	 */
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|in_docs
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#resetState() 	 */
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
name|inputSequence
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
name|whereExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|returnExpr
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
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
name|orderSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|orderSpecs
index|[
name|i
index|]
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|final
specifier|static
name|void
name|setContext
parameter_list|(
name|int
name|contextId
parameter_list|,
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|seq
operator|instanceof
name|VirtualNodeSet
condition|)
block|{
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|seq
operator|)
operator|.
name|setInPredicate
argument_list|(
literal|true
argument_list|)
expr_stmt|;
operator|(
operator|(
name|VirtualNodeSet
operator|)
name|seq
operator|)
operator|.
name|setSelfIsContext
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|Item
name|next
decl_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|seq
operator|.
name|unorderedIterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|next
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
if|if
condition|(
name|next
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|next
operator|)
operator|.
name|addContextNode
argument_list|(
name|contextId
argument_list|,
operator|(
name|NodeProxy
operator|)
name|next
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|protected
specifier|final
specifier|static
name|void
name|clearContext
parameter_list|(
name|int
name|contextId
parameter_list|,
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|seq
operator|!=
literal|null
operator|&&
operator|!
operator|(
name|seq
operator|instanceof
name|VirtualNodeSet
operator|)
condition|)
block|{
name|seq
operator|.
name|clearContext
argument_list|(
name|contextId
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|registerUpdateListener
parameter_list|(
specifier|final
name|Sequence
name|sequence
parameter_list|)
block|{
if|if
condition|(
name|listener
operator|==
literal|null
condition|)
block|{
name|listener
operator|=
operator|new
name|ExprUpdateListener
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
name|context
operator|.
name|registerUpdateListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
else|else
name|listener
operator|.
name|setSequence
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|ExprUpdateListener
implements|implements
name|UpdateListener
block|{
specifier|private
name|Sequence
name|sequence
decl_stmt|;
specifier|public
name|ExprUpdateListener
parameter_list|(
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
specifier|public
name|void
name|setSequence
parameter_list|(
name|Sequence
name|sequence
parameter_list|)
block|{
name|this
operator|.
name|sequence
operator|=
name|sequence
expr_stmt|;
block|}
specifier|public
name|void
name|documentUpdated
parameter_list|(
name|DocumentImpl
name|document
parameter_list|,
name|int
name|event
parameter_list|)
block|{
block|}
specifier|public
name|void
name|nodeMoved
parameter_list|(
name|NodeId
name|oldNodeId
parameter_list|,
name|StoredNode
name|newNode
parameter_list|)
block|{
name|sequence
operator|.
name|nodeMoved
argument_list|(
name|oldNodeId
argument_list|,
name|newNode
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|unsubscribe
parameter_list|()
block|{
name|BindingExpression
operator|.
name|this
operator|.
name|listener
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|void
name|debug
parameter_list|()
block|{
block|}
block|}
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|returnExpr
operator|.
name|getDependencies
argument_list|()
return|;
block|}
block|}
end_class

end_unit

