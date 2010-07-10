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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|UserDefinedFunction
extends|extends
name|Function
block|{
specifier|private
name|Expression
name|body
decl_stmt|;
specifier|private
name|List
argument_list|<
name|QName
argument_list|>
name|parameters
init|=
operator|new
name|ArrayList
argument_list|<
name|QName
argument_list|>
argument_list|(
literal|5
argument_list|)
decl_stmt|;
specifier|private
name|Sequence
index|[]
name|currentArguments
init|=
literal|null
decl_stmt|;
specifier|private
name|DocumentSet
index|[]
name|contextDocs
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|inRecursion
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|bodyAnalyzed
init|=
literal|false
decl_stmt|;
specifier|public
name|UserDefinedFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFunctionBody
parameter_list|(
name|Expression
name|body
parameter_list|)
block|{
name|this
operator|.
name|body
operator|=
name|body
expr_stmt|;
block|}
specifier|public
name|Expression
name|getFunctionBody
parameter_list|()
block|{
return|return
name|body
return|;
block|}
specifier|public
name|void
name|addVariable
parameter_list|(
name|String
name|varName
parameter_list|)
throws|throws
name|XPathException
block|{
name|QName
name|qname
init|=
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
decl_stmt|;
if|if
condition|(
name|parameters
operator|.
name|contains
argument_list|(
name|qname
argument_list|)
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"XQST0039: function "
operator|+
name|getName
argument_list|()
operator|+
literal|" is already have parameter with the name "
operator|+
name|varName
argument_list|)
throw|;
name|parameters
operator|.
name|add
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Function#setArguments(java.util.List) 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|DocumentSet
index|[]
name|contextDocs
parameter_list|)
throws|throws
name|XPathException
block|{
name|this
operator|.
name|currentArguments
operator|=
name|args
expr_stmt|;
name|this
operator|.
name|contextDocs
operator|=
name|contextDocs
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Function#analyze(org.exist.xquery.AnalyzeContextInfo) 	 */
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
operator|!
name|inRecursion
condition|)
block|{
name|inRecursion
operator|=
literal|true
expr_stmt|;
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|LocalVariable
name|var
decl_stmt|;
for|for
control|(
name|QName
name|varName
range|:
name|parameters
control|)
block|{
name|var
operator|=
operator|new
name|LocalVariable
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
block|}
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|bodyAnalyzed
condition|)
block|{
name|body
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
name|bodyAnalyzed
operator|=
literal|true
expr_stmt|;
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
argument_list|)
expr_stmt|;
block|}
name|inRecursion
operator|=
literal|false
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
comment|//        context.expressionStart(this);
name|context
operator|.
name|stackEnter
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|// Save the local variable stack
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|(
literal|true
argument_list|)
decl_stmt|;
try|try
block|{
name|QName
name|varName
decl_stmt|;
name|LocalVariable
name|var
decl_stmt|;
name|int
name|j
init|=
literal|0
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|parameters
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
operator|,
name|j
operator|++
control|)
block|{
name|varName
operator|=
name|parameters
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|var
operator|=
operator|new
name|LocalVariable
argument_list|(
name|varName
argument_list|)
expr_stmt|;
name|var
operator|.
name|setValue
argument_list|(
name|currentArguments
index|[
name|j
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|contextDocs
operator|!=
literal|null
condition|)
name|var
operator|.
name|setContextDocs
argument_list|(
name|contextDocs
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
name|int
name|actualCardinality
decl_stmt|;
if|if
condition|(
name|currentArguments
index|[
name|j
index|]
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
name|currentArguments
index|[
name|j
index|]
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
if|if
condition|(
operator|!
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getArgumentTypes
argument_list|()
index|[
name|j
index|]
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
name|this
argument_list|,
literal|"err:XPTY0004: Invalid cardinality for parameter $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
name|Cardinality
operator|.
name|getDescription
argument_list|(
name|getSignature
argument_list|()
operator|.
name|getArgumentTypes
argument_list|()
index|[
name|j
index|]
operator|.
name|getCardinality
argument_list|()
argument_list|)
operator|+
literal|", got "
operator|+
name|currentArguments
index|[
name|j
index|]
operator|.
name|getItemCount
argument_list|()
argument_list|)
throw|;
block|}
name|Sequence
name|result
init|=
name|body
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
return|return
name|result
return|;
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
name|context
operator|.
name|stackLeave
argument_list|(
name|this
argument_list|)
expr_stmt|;
comment|//            context.expressionEnd(this);
block|}
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.Function#dump(org.exist.xquery.util.ExpressionDumper)      */
specifier|public
name|void
name|dump
parameter_list|(
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|FunctionSignature
name|signature
init|=
name|getSignature
argument_list|()
decl_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
literal|'('
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
name|signature
operator|.
name|getArgumentTypes
argument_list|()
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
name|signature
operator|.
name|getArgumentTypes
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|") "
argument_list|)
expr_stmt|;
name|dumper
operator|.
name|display
argument_list|(
name|signature
operator|.
name|getReturnType
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see java.lang.Object#toString()      */
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|FunctionSignature
name|signature
init|=
name|getSignature
argument_list|()
decl_stmt|;
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|signature
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|'('
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
name|signature
operator|.
name|getArgumentTypes
argument_list|()
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
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|signature
operator|.
name|getArgumentTypes
argument_list|()
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.functions.Function#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|CONTEXT_ITEM
operator|+
name|Dependency
operator|.
name|CONTEXT_POSITION
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|(
name|boolean
name|postOptimization
parameter_list|)
block|{
comment|// Question: understand this test. Why not reset even is not in recursion ?
comment|// Answer: would lead to an infinite loop if the function is recursive.
if|if
condition|(
operator|!
name|inRecursion
condition|)
block|{
name|inRecursion
operator|=
literal|true
expr_stmt|;
name|bodyAnalyzed
operator|=
literal|false
expr_stmt|;
name|body
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|inRecursion
operator|=
literal|false
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|postOptimization
condition|)
block|{
name|currentArguments
operator|=
literal|null
expr_stmt|;
name|contextDocs
operator|=
literal|null
expr_stmt|;
block|}
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
name|visitUserFunction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
comment|/**      * Return the functions parameters list      *       * @return List of function parameters      */
specifier|public
name|List
argument_list|<
name|QName
argument_list|>
name|getParameters
parameter_list|()
block|{
return|return
name|parameters
return|;
block|}
block|}
end_class

end_unit

