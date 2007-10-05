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
name|QName
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
name|GroupedValueSequence
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
name|OrderedValueSequence
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
name|PreorderedValueSequence
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
comment|/**  * Implements an XQuery let-expression.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|LetExpr
extends|extends
name|BindingExpression
block|{
specifier|public
name|LetExpr
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.BindingExpression#analyze(org.exist.xquery.Expression, int, org.exist.xquery.OrderSpec[]) 	 */
specifier|public
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
block|{
comment|// bv : Declare the grouping variable
if|if
condition|(
name|groupVarName
operator|!=
literal|null
condition|)
block|{
name|LocalVariable
name|groupVar
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|groupVarName
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|groupVar
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|groupVar
argument_list|)
expr_stmt|;
block|}
comment|// bv : Declare grouping key variable(s)
if|if
condition|(
name|groupBy
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
name|groupBy
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|LocalVariable
name|groupKeyVar
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|groupBy
index|[
name|i
index|]
operator|.
name|getKeyVarName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|groupKeyVar
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|groupKeyVar
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|inputSequence
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
comment|// Declare the iteration variable
name|LocalVariable
name|inVar
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|varName
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|inVar
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|inVar
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|AnalyzeContextInfo
name|newContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|newContextInfo
operator|.
name|setFlags
argument_list|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator||
name|IN_PREDICATE
operator||
name|IN_WHERE_CLAUSE
argument_list|)
expr_stmt|;
name|whereExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
comment|//Reset the context position
name|context
operator|.
name|setContextPosition
argument_list|(
literal|0
argument_list|)
expr_stmt|;
if|if
condition|(
name|returnExpr
operator|instanceof
name|BindingExpression
condition|)
block|{
operator|(
operator|(
name|BindingExpression
operator|)
name|returnExpr
operator|)
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|,
name|orderBy
argument_list|,
name|groupBy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|orderBy
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
name|orderBy
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|orderBy
index|[
name|i
index|]
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groupBy
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
name|groupBy
operator|.
name|length
condition|;
name|i
operator|++
control|)
name|groupBy
index|[
name|i
index|]
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
name|returnExpr
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
specifier|public
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
if|if
condition|(
name|resultSequence
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
literal|"RESULT SEQUENCE"
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
try|try
block|{
comment|//bv : Declare grouping variables and initiate grouped sequence
name|LocalVariable
name|groupVar
init|=
literal|null
decl_stmt|;
name|LocalVariable
name|groupKeyVar
index|[]
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|groupSpecs
operator|!=
literal|null
condition|)
block|{
name|groupedSequence
operator|=
operator|new
name|GroupedValueSequenceTable
argument_list|(
name|groupSpecs
argument_list|,
name|toGroupVarName
argument_list|,
name|context
argument_list|)
expr_stmt|;
name|groupVar
operator|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|groupVarName
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|groupVar
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|groupVar
argument_list|)
expr_stmt|;
name|groupKeyVar
operator|=
operator|new
name|LocalVariable
index|[
name|groupSpecs
operator|.
name|length
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|groupKeyVar
index|[
name|i
index|]
operator|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getKeyVarName
argument_list|()
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|groupKeyVar
index|[
name|i
index|]
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|groupKeyVar
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
block|}
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|false
argument_list|)
decl_stmt|;
name|Sequence
name|in
decl_stmt|;
name|boolean
name|fastOrderBy
decl_stmt|;
try|try
block|{
comment|// evaluate input sequence
name|in
operator|=
name|inputSequence
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
comment|// Declare the iteration variable
name|LocalVariable
name|var
init|=
operator|new
name|LocalVariable
argument_list|(
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|varName
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|var
operator|.
name|setSequenceType
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|==
literal|null
condition|)
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
comment|//Just because it makes conversions !
name|var
operator|.
name|setContextDocs
argument_list|(
name|inputSequence
operator|.
name|getContextDocSet
argument_list|()
argument_list|)
expr_stmt|;
name|registerUpdateListener
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|Sequence
name|filtered
init|=
name|applyWhereExpression
argument_list|(
literal|null
argument_list|)
decl_stmt|;
comment|// TODO: don't use returnsType here
if|if
condition|(
name|filtered
operator|.
name|isEmpty
argument_list|()
condition|)
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
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
if|else if
condition|(
name|filtered
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|BOOLEAN
operator|&&
operator|!
name|filtered
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
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
name|end
argument_list|(
name|this
argument_list|,
literal|""
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|)
expr_stmt|;
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
comment|// Check if we can speed up the processing of the "order by" clause.
name|fastOrderBy
operator|=
name|checkOrderSpecs
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|//  PreorderedValueSequence applies the order specs to all items
comment|// in one single processing step
if|if
condition|(
name|fastOrderBy
condition|)
block|{
name|in
operator|=
operator|new
name|PreorderedValueSequence
argument_list|(
name|orderSpecs
argument_list|,
name|in
operator|.
name|toNodeSet
argument_list|()
argument_list|,
name|getExpressionId
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Otherwise, if there's an order by clause, wrap the result into
comment|// an OrderedValueSequence. OrderedValueSequence will compute
comment|// order expressions for every item when it is added to the result sequence.
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
operator|&&
operator|!
name|fastOrderBy
condition|)
name|resultSequence
operator|=
operator|new
name|OrderedValueSequence
argument_list|(
name|orderSpecs
argument_list|,
name|in
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groupedSequence
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|returnExpr
operator|instanceof
name|BindingExpression
condition|)
block|{
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
name|resultSequence
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
operator|(
operator|(
name|BindingExpression
operator|)
name|returnExpr
operator|)
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|resultSequence
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|in
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
name|resultSequence
operator|=
name|in
expr_stmt|;
else|else
name|resultSequence
operator|.
name|addAll
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|/* bv : special processing for groupby :                     if returnExpr is a Binding expression, pass the groupedSequence.                     Else, add item to groupedSequence and don't evaluate here !                     */
if|if
condition|(
name|returnExpr
operator|instanceof
name|BindingExpression
condition|)
block|{
if|if
condition|(
name|resultSequence
operator|==
literal|null
condition|)
name|resultSequence
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
operator|(
operator|(
name|BindingExpression
operator|)
name|returnExpr
operator|)
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
name|resultSequence
argument_list|,
name|groupedSequence
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Sequence
name|toGroupSequence
init|=
name|context
operator|.
name|resolveVariable
argument_list|(
name|groupedSequence
operator|.
name|getToGroupVarName
argument_list|()
argument_list|)
operator|.
name|getValue
argument_list|()
decl_stmt|;
name|groupedSequence
operator|.
name|addAll
argument_list|(
name|toGroupSequence
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
block|{
name|int
name|actualCardinality
decl_stmt|;
if|if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY
expr_stmt|;
if|else if
condition|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|hasMany
argument_list|()
condition|)
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|MANY
expr_stmt|;
else|else
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|ONE
expr_stmt|;
comment|//Type.EMPTY is *not* a subtype of other types ; checking cardinality first
if|if
condition|(
operator|!
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|sequenceType
operator|.
name|getCardinality
argument_list|()
argument_list|,
name|actualCardinality
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"XPTY0004: Invalid cardinality for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|sequenceType
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|actualCardinality
argument_list|)
argument_list|)
throw|;
comment|//TODO : ignore nodes right now ; they are returned as xs:untypedAtomicType
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"XPTY0004: Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
comment|//Here is an attempt to process the nodes correctly
block|}
else|else
block|{
comment|//Same as above : we probably may factorize
if|if
condition|(
operator|!
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|,
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"XPTY0004: Invalid type for variable $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|var
operator|.
name|getValue
argument_list|()
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
block|}
block|}
finally|finally
block|{
comment|// Restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
comment|//Special processing for groupBy : one return per group in groupedSequence
if|if
condition|(
name|groupSpecs
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Iterator
name|it
init|=
name|groupedSequence
operator|.
name|iterate
argument_list|()
init|;
name|it
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Object
name|key
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|GroupedValueSequence
name|currentGroup
init|=
operator|(
name|GroupedValueSequence
operator|)
name|groupedSequence
operator|.
name|get
argument_list|(
name|key
argument_list|)
decl_stmt|;
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// set the grouping variable to current group nodes
name|groupVar
operator|.
name|setValue
argument_list|(
name|currentGroup
argument_list|)
expr_stmt|;
name|groupVar
operator|.
name|checkType
argument_list|()
expr_stmt|;
comment|//set value of grouping keys for the current group
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupKeyVar
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|groupKeyVar
index|[
name|i
index|]
operator|.
name|setValue
argument_list|(
name|currentGroup
operator|.
name|getGroupKey
argument_list|()
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|toSequence
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//evaluate real return expression
name|Sequence
name|val
init|=
name|groupReturnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
decl_stmt|;
name|resultSequence
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
operator|&&
operator|!
name|fastOrderBy
condition|)
operator|(
operator|(
name|OrderedValueSequence
operator|)
name|resultSequence
operator|)
operator|.
name|sort
argument_list|()
expr_stmt|;
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
comment|//            // Restore the local variable stack
comment|//            context.popLocalVariables(mark);
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
name|resultSequence
argument_list|)
expr_stmt|;
name|actualReturnType
operator|=
name|resultSequence
operator|.
name|getItemType
argument_list|()
expr_stmt|;
return|return
name|resultSequence
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
return|return
name|sequenceType
operator|.
name|getPrimaryType
argument_list|()
return|;
comment|//Type.ITEM by default : this may change *after* evaluation
return|return
name|actualReturnType
return|;
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
name|dumper
operator|.
name|display
argument_list|(
literal|"let "
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"$"
argument_list|)
operator|.
name|display
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" := "
argument_list|)
expr_stmt|;
name|inputSequence
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"where "
argument_list|)
expr_stmt|;
name|whereExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groupSpecs
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|"group "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"$"
argument_list|)
operator|.
name|display
argument_list|(
name|toGroupVarName
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"$"
argument_list|)
operator|.
name|display
argument_list|(
name|groupVarName
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" by "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getGroupExpression
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|"$"
argument_list|)
operator|.
name|display
argument_list|(
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getKeyVarName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
condition|)
block|{
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"order by "
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
comment|//TODO : toString() or... dump ?
name|dumper
operator|.
name|display
argument_list|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
else|else
name|dumper
operator|.
name|nl
argument_list|()
operator|.
name|display
argument_list|(
literal|"return "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|returnExpr
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|endIndent
argument_list|()
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
literal|"let "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
operator|.
name|append
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" := "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|inputSequence
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
expr_stmt|;
if|if
condition|(
name|whereExpr
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" where "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|whereExpr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|groupSpecs
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|"group "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
operator|.
name|append
argument_list|(
name|toGroupVarName
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
operator|.
name|append
argument_list|(
name|groupVarName
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" by "
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|groupSpecs
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getGroupExpression
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
literal|" as "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"$"
argument_list|)
operator|.
name|append
argument_list|(
name|groupSpecs
index|[
name|i
index|]
operator|.
name|getKeyVarName
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|orderSpecs
operator|!=
literal|null
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" order by "
argument_list|)
expr_stmt|;
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
if|if
condition|(
name|i
operator|>
literal|0
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|orderSpecs
index|[
name|i
index|]
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
else|else
name|result
operator|.
name|append
argument_list|(
literal|"return "
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|returnExpr
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
name|visitLetExpression
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

