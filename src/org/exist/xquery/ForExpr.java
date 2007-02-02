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
name|IntegerValue
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_comment
comment|/**  * Represents an XQuery "for" expression.  *   * @author Wolfgang Meier<wolfgang@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|ForExpr
extends|extends
name|BindingExpression
block|{
specifier|private
name|String
name|positionalVariable
init|=
literal|null
decl_stmt|;
specifier|public
name|ForExpr
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
comment|/** 	 * A "for" expression may have an optional positional variable whose 	 * QName can be set via this method. 	 *  	 * @param var 	 */
specifier|public
name|void
name|setPositionalVariable
parameter_list|(
name|String
name|var
parameter_list|)
block|{
name|positionalVariable
operator|=
name|var
expr_stmt|;
block|}
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
parameter_list|)
throws|throws
name|XPathException
block|{
name|analyze
argument_list|(
name|contextInfo
argument_list|,
name|orderBy
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
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
comment|// Declare positional variable
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
comment|//could probably be detected by the parser
if|if
condition|(
name|varName
operator|.
name|equals
argument_list|(
name|positionalVariable
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XQST0089: bound variable and positional variable have the same name"
argument_list|)
throw|;
name|LocalVariable
name|posVar
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
name|positionalVariable
argument_list|,
literal|null
argument_list|)
argument_list|)
decl_stmt|;
name|posVar
operator|.
name|setSequenceType
argument_list|(
name|POSITIONAL_VAR_TYPE
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|posVar
argument_list|)
expr_stmt|;
block|}
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
name|newContextInfo
operator|.
name|setContextId
argument_list|(
name|getExpressionId
argument_list|()
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
comment|// the order by specs should be analyzed by the last binding expression
comment|// in the chain to have access to all variables. So if the return expression
comment|// is another binding expression, we just forward the order specs.
if|if
condition|(
name|returnExpr
operator|instanceof
name|BindingExpression
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
name|addFlag
argument_list|(
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
operator|(
operator|(
name|BindingExpression
operator|)
name|returnExpr
operator|)
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|,
name|orderBy
argument_list|,
name|groupBy
argument_list|)
expr_stmt|;
block|}
else|else
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
name|addFlag
argument_list|(
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
comment|//analyze the order specs and the group specs
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
name|newContextInfo
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
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
name|returnExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
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
comment|/** 	 * This implementation tries to process the "where" clause in advance, i.e. in one single 	 * step. This is possible if the input sequence is a node set and the where expression 	 * has no dependencies on other variables than those declared in this "for" statement. 	 *  	 * @see org.exist.xquery.Expression#eval(Sequence, Item) 	 */
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
comment|// bv - Declare grouping variables and initiate grouped sequence
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
comment|// Evaluate the "in" expression
name|Sequence
name|in
init|=
name|inputSequence
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
name|registerUpdateListener
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Declare positional variable
name|LocalVariable
name|at
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
name|at
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
name|positionalVariable
argument_list|,
literal|null
argument_list|)
argument_list|)
expr_stmt|;
name|at
operator|.
name|setSequenceType
argument_list|(
name|POSITIONAL_VAR_TYPE
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|at
argument_list|)
expr_stmt|;
block|}
comment|// Assign the whole input sequence to the bound variable.
comment|// This is required if we process the "where" or "order by" clause
comment|// in one step.
name|var
operator|.
name|setValue
argument_list|(
name|in
argument_list|)
expr_stmt|;
comment|// Save the current context document set to the variable as a hint
comment|// for path expressions occurring in the "return" clause.
if|if
condition|(
name|in
operator|instanceof
name|NodeSet
condition|)
block|{
name|DocumentSet
name|contextDocs
init|=
operator|(
operator|(
name|NodeSet
operator|)
name|in
operator|)
operator|.
name|getDocumentSet
argument_list|()
decl_stmt|;
name|var
operator|.
name|setContextDocs
argument_list|(
name|contextDocs
argument_list|)
expr_stmt|;
block|}
else|else
name|var
operator|.
name|setContextDocs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
comment|// Check if we can speed up the processing of the "order by" clause.
name|boolean
name|fastOrderBy
init|=
literal|false
decl_stmt|;
comment|// checkOrderSpecs(in);
comment|// See if we can process the "where" clause in a single step (instead of
comment|// calling the where expression for each item in the input sequence)
comment|// This is possible if the input sequence is a node set and has no
comment|// dependencies on the current context item.
name|boolean
name|fastExec
init|=
name|whereExpr
operator|!=
literal|null
operator|&&
name|at
operator|==
literal|null
operator|&&
operator|!
name|Dependency
operator|.
name|dependsOn
argument_list|(
name|whereExpr
argument_list|,
name|Dependency
operator|.
name|CONTEXT_ITEM
argument_list|)
operator|&&
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
decl_stmt|;
comment|// If possible, apply the where expression ahead of the iteration
if|if
condition|(
name|fastExec
condition|)
block|{
if|if
condition|(
operator|!
name|in
operator|.
name|isCached
argument_list|()
condition|)
name|setContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
name|in
operator|=
name|applyWhereExpression
argument_list|(
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|in
operator|.
name|isCached
argument_list|()
condition|)
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
name|in
argument_list|)
expr_stmt|;
block|}
comment|// PreorderedValueSequence applies the order specs to all items
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
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|resultSequence
operator|=
operator|new
name|ValueSequence
argument_list|()
expr_stmt|;
block|}
name|Sequence
name|val
init|=
literal|null
decl_stmt|;
name|int
name|p
init|=
literal|1
decl_stmt|;
name|IntegerValue
name|atVal
init|=
operator|new
name|IntegerValue
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
name|at
operator|.
name|setValue
argument_list|(
name|atVal
argument_list|)
expr_stmt|;
comment|// Loop through each variable binding
name|p
operator|=
literal|0
expr_stmt|;
for|for
control|(
name|SequenceIterator
name|i
init|=
name|in
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
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|contextItem
operator|=
name|i
operator|.
name|nextItem
argument_list|()
expr_stmt|;
name|context
operator|.
name|setContextPosition
argument_list|(
name|p
argument_list|)
expr_stmt|;
comment|//			atVal.setValue(p); // seb: this does not create a new Value. the old Value is referenced from results
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
name|at
operator|.
name|setValue
argument_list|(
operator|new
name|IntegerValue
argument_list|(
name|p
operator|+
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
comment|// set variable value to current item
name|var
operator|.
name|setValue
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
name|val
operator|=
name|contextSequence
expr_stmt|;
comment|// check optional where clause
if|if
condition|(
name|whereExpr
operator|!=
literal|null
operator|&&
operator|(
operator|!
name|fastExec
operator|)
condition|)
block|{
if|if
condition|(
name|contextItem
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|contextItem
operator|)
operator|.
name|addContextNode
argument_list|(
name|getExpressionId
argument_list|()
argument_list|,
operator|(
name|NodeProxy
operator|)
name|contextItem
argument_list|)
expr_stmt|;
name|Sequence
name|bool
init|=
name|applyWhereExpression
argument_list|(
literal|null
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextItem
operator|instanceof
name|NodeProxy
condition|)
operator|(
operator|(
name|NodeProxy
operator|)
name|contextItem
operator|)
operator|.
name|clearContext
argument_list|(
name|getExpressionId
argument_list|()
argument_list|)
expr_stmt|;
comment|// if where returned false, continue
if|if
condition|(
operator|!
name|bool
operator|.
name|effectiveBooleanValue
argument_list|()
condition|)
continue|continue;
block|}
else|else
name|val
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
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
comment|// otherwise call the return expression and add results to resultSequence
else|else
block|{
name|val
operator|=
name|returnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|resultSequence
operator|.
name|addAll
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
comment|/* bv : special processing for groupby :                   if returnExpr is a Binding expression, pass the groupedSequence.                   Else, add item to groupedSequence and don't evaluate here !                   */
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
block|}
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
comment|// bv : Special processing for groupBy : one return per group in groupedSequence
comment|//TODO : positional variable !
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
comment|//context.setContextPosition(k); //bv : not tested
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
name|val
operator|=
name|groupReturnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|resultSequence
operator|.
name|addAll
argument_list|(
name|val
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
return|return
name|resultSequence
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
literal|"for "
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
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" at "
argument_list|)
operator|.
name|display
argument_list|(
name|positionalVariable
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|" as "
argument_list|)
operator|.
name|display
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|" in "
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
operator|.
name|nl
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
name|display
argument_list|(
literal|"where"
argument_list|,
name|whereExpr
operator|.
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|startIndent
argument_list|()
expr_stmt|;
name|whereExpr
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
operator|.
name|nl
argument_list|()
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
name|dumper
operator|.
name|display
argument_list|(
name|orderSpecs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|nl
argument_list|()
expr_stmt|;
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
literal|" "
argument_list|,
name|returnExpr
operator|.
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|dumper
operator|.
name|display
argument_list|(
literal|"return"
argument_list|,
name|returnExpr
operator|.
name|getASTNode
argument_list|()
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
operator|.
name|nl
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
literal|"for "
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
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" at "
argument_list|)
operator|.
name|append
argument_list|(
name|positionalVariable
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|!=
literal|null
condition|)
name|result
operator|.
name|append
argument_list|(
literal|" as "
argument_list|)
operator|.
name|append
argument_list|(
name|sequenceType
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|" in "
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
literal|"where"
argument_list|)
expr_stmt|;
name|result
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
name|whereExpr
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
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
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
literal|" "
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
comment|/* (non-Javadoc)     * @see org.exist.xquery.AbstractExpression#resetState()     */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|super
operator|.
name|resetState
argument_list|()
expr_stmt|;
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
name|visitForExpression
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

