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
name|QName
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
name|*
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
specifier|private
name|boolean
name|allowEmpty
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|isOuterFor
init|=
literal|true
decl_stmt|;
specifier|public
name|ForExpr
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|boolean
name|allowingEmpty
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|allowEmpty
operator|=
name|allowingEmpty
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClauseType
name|getType
parameter_list|()
block|{
return|return
name|ClauseType
operator|.
name|FOR
return|;
block|}
comment|/**      * A "for" expression may have an optional positional variable whose      * QName can be set via this method.      *       * @param var      */
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
comment|// Save the local variable stack
specifier|final
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
try|try
block|{
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
specifier|final
name|AnalyzeContextInfo
name|varContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|inputSequence
operator|.
name|analyze
argument_list|(
name|varContextInfo
argument_list|)
expr_stmt|;
comment|// Declare the iteration variable
specifier|final
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
name|inVar
operator|.
name|setStaticType
argument_list|(
name|varContextInfo
operator|.
name|getStaticReturnType
argument_list|()
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XQST0089
argument_list|,
literal|"bound variable and positional variable have the same name"
argument_list|)
throw|;
block|}
specifier|final
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
name|posVar
operator|.
name|setStaticType
argument_list|(
name|Type
operator|.
name|INTEGER
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
specifier|final
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
name|returnExpr
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * This implementation tries to process the "where" clause in advance, i.e. in one single      * step. This is possible if the input sequence is a node set and the where expression      * has no dependencies on other variables than those declared in this "for" statement.      *       * @see org.exist.xquery.Expression#eval(Sequence, Item)      */
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
block|{
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
block|}
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
block|{
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
block|}
name|context
operator|.
name|expressionStart
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|LocalVariable
name|var
decl_stmt|;
name|Sequence
name|in
decl_stmt|;
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
name|resultSequence
init|=
operator|new
name|ValueSequence
argument_list|(
name|unordered
argument_list|)
decl_stmt|;
try|try
block|{
comment|// Evaluate the "in" expression
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
name|var
operator|=
name|createVariable
argument_list|(
name|varName
argument_list|)
expr_stmt|;
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
name|var
operator|.
name|setContextDocs
argument_list|(
name|in
operator|.
name|getDocumentSet
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|var
operator|.
name|setContextDocs
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
comment|// See if we can process the "where" clause in a single step (instead of
comment|// calling the where expression for each item in the input sequence)
comment|// This is possible if the input sequence is a node set and has no
comment|// dependencies on the current context item.
if|if
condition|(
name|at
operator|==
literal|null
operator|&&
name|returnExpr
operator|instanceof
name|FLWORClause
operator|&&
name|isOuterFor
condition|)
block|{
name|in
operator|=
operator|(
operator|(
name|FLWORClause
operator|)
name|returnExpr
operator|)
operator|.
name|preEval
argument_list|(
name|in
argument_list|)
expr_stmt|;
block|}
specifier|final
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
block|{
name|at
operator|.
name|setValue
argument_list|(
name|atVal
argument_list|)
expr_stmt|;
block|}
comment|//Type.EMPTY is *not* a subtype of other types ;
comment|//the tests below would fail without this prior cardinality check
if|if
condition|(
name|in
operator|.
name|isEmpty
argument_list|()
operator|&&
name|sequenceType
operator|!=
literal|null
operator|&&
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
name|Cardinality
operator|.
name|EMPTY
argument_list|)
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
name|XPTY0004
argument_list|,
literal|"Invalid cardinality for variable $"
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
name|in
operator|.
name|getCardinality
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|// Loop through each variable binding
name|int
name|p
init|=
literal|0
decl_stmt|;
if|if
condition|(
name|in
operator|.
name|isEmpty
argument_list|()
operator|&&
name|allowEmpty
condition|)
block|{
name|processItem
argument_list|(
name|var
argument_list|,
name|AtomicValue
operator|.
name|EMPTY_VALUE
argument_list|,
name|Sequence
operator|.
name|EMPTY_SEQUENCE
argument_list|,
name|resultSequence
argument_list|,
name|at
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
specifier|final
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
name|processItem
argument_list|(
name|var
argument_list|,
name|i
operator|.
name|nextItem
argument_list|()
argument_list|,
name|in
argument_list|,
name|resultSequence
argument_list|,
name|at
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
comment|// restore the local variable stack
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
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
name|sequenceType
operator|!=
literal|null
condition|)
block|{
comment|//Type.EMPTY is *not* a subtype of other types ; checking cardinality first
comment|//only a check on empty sequence is accurate here
if|if
condition|(
name|resultSequence
operator|.
name|isEmpty
argument_list|()
operator|&&
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
name|Cardinality
operator|.
name|EMPTY
argument_list|)
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
name|XPTY0004
argument_list|,
literal|"Invalid cardinality for variable $"
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
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
throw|;
block|}
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
name|resultSequence
operator|.
name|isEmpty
argument_list|()
operator|&&
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|resultSequence
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPTY0004
argument_list|,
literal|"Invalid type for variable $"
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
name|resultSequence
operator|.
name|getItemType
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
comment|//trigger the old behaviour
block|}
else|else
block|{
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
block|}
name|setActualReturnType
argument_list|(
name|resultSequence
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|callPostEval
argument_list|()
condition|)
block|{
name|resultSequence
operator|=
name|postEval
argument_list|(
name|resultSequence
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|expressionEnd
argument_list|(
name|this
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
block|{
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
block|}
return|return
name|resultSequence
return|;
block|}
specifier|private
name|void
name|processItem
parameter_list|(
name|LocalVariable
name|var
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
name|in
parameter_list|,
name|Sequence
name|resultSequence
parameter_list|,
name|LocalVariable
name|at
parameter_list|,
name|int
name|p
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|proceed
argument_list|(
name|this
argument_list|)
expr_stmt|;
name|context
operator|.
name|setContextSequencePosition
argument_list|(
name|p
argument_list|,
name|in
argument_list|)
expr_stmt|;
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
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
block|}
specifier|final
name|Sequence
name|contextSequence
init|=
name|contextItem
operator|.
name|toSequence
argument_list|()
decl_stmt|;
comment|// set variable value to current item
name|var
operator|.
name|setValue
argument_list|(
name|contextSequence
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequenceType
operator|==
literal|null
condition|)
block|{
name|var
operator|.
name|checkType
argument_list|()
expr_stmt|;
block|}
comment|//because it makes some conversions !
comment|//Reset the context position
name|context
operator|.
name|setContextSequencePosition
argument_list|(
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|resultSequence
operator|.
name|addAll
argument_list|(
name|returnExpr
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
argument_list|)
expr_stmt|;
comment|// free resources
name|var
operator|.
name|destroy
argument_list|(
name|context
argument_list|,
name|resultSequence
argument_list|)
expr_stmt|;
block|}
specifier|private
name|boolean
name|callPostEval
parameter_list|()
block|{
name|FLWORClause
name|prev
init|=
name|getPreviousClause
argument_list|()
decl_stmt|;
while|while
condition|(
name|prev
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|prev
operator|.
name|getType
argument_list|()
condition|)
block|{
case|case
name|LET
case|:
case|case
name|FOR
case|:
return|return
literal|false
return|;
case|case
name|ORDERBY
case|:
case|case
name|GROUPBY
case|:
return|return
literal|true
return|;
block|}
name|prev
operator|=
name|prev
operator|.
name|getPreviousClause
argument_list|()
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|preEval
parameter_list|(
name|Sequence
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// if preEval gets called, we know we're inside another FOR
name|isOuterFor
operator|=
literal|false
expr_stmt|;
return|return
name|super
operator|.
name|preEval
argument_list|(
name|seq
argument_list|)
return|;
block|}
comment|/* (non-Javadoc)          * @see org.exist.xquery.Expression#dump(org.exist.xquery.util.ExpressionDumper)          */
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
name|line
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
name|sequenceType
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|allowEmpty
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" allowing empty "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
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
block|}
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
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|" "
argument_list|,
name|returnExpr
operator|.
name|getLine
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
literal|"return"
argument_list|,
name|returnExpr
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
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
name|sequenceType
operator|!=
literal|null
condition|)
block|{
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
block|}
if|if
condition|(
name|allowEmpty
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" allowing empty "
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|positionalVariable
operator|!=
literal|null
condition|)
block|{
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
block|}
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
comment|//TODO : QuantifiedExpr
if|if
condition|(
name|returnExpr
operator|instanceof
name|LetExpr
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|result
operator|.
name|append
argument_list|(
literal|"return "
argument_list|)
expr_stmt|;
block|}
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
comment|/* (non-Javadoc)     * @see org.exist.xquery.AbstractExpression#resetState()     */
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

