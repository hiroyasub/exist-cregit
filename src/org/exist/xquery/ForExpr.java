begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.Expression#analyze(org.exist.xquery.Expression)      */
specifier|public
name|void
name|analyze
parameter_list|(
name|Expression
name|parent
parameter_list|,
name|int
name|flags
parameter_list|,
name|OrderSpec
name|orderBy
index|[]
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|()
decl_stmt|;
name|inputSequence
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
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
name|declareVariable
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
name|declareVariable
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
name|whereExpr
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
operator||
name|IN_PREDICATE
operator||
name|IN_WHERE_CLAUSE
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
operator|(
operator|(
name|BindingExpression
operator|)
name|returnExpr
operator|)
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
operator||
name|SINGLE_STEP_EXECUTION
argument_list|,
name|orderBy
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// analyze the order specs
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
name|this
argument_list|,
name|flags
operator||
name|SINGLE_STEP_EXECUTION
argument_list|)
expr_stmt|;
block|}
name|returnExpr
operator|.
name|analyze
argument_list|(
name|this
argument_list|,
name|flags
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
comment|/** 	 * This implementation tries to process the "where" clause in advance, i.e. in one single 	 * step. This is possible if the input sequence is a node set and the where expression 	 * has no dependencies on other variables than those declared in this "for" statement. 	 *  	 * @see org.exist.xquery.Expression#eval(org.exist.xquery.StaticContext, org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|()
decl_stmt|;
comment|// Evaluate the "in" expression
name|Sequence
name|in
init|=
name|inputSequence
operator|.
name|eval
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|clearContext
argument_list|(
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
name|declareVariable
argument_list|(
name|var
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
name|declareVariable
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
operator|(
name|whereExpr
operator|.
name|getDependencies
argument_list|()
operator|&
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|)
operator|==
literal|0
operator|&&
name|at
operator|==
literal|null
operator|&&
name|in
operator|.
name|getItemType
argument_list|()
operator|==
name|Type
operator|.
name|NODE
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
argument_list|()
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
comment|/* if the returnExpr is another BindingExpression, call it 			 * with the result sequence. 			 */
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
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
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
block|}
end_class

end_unit

