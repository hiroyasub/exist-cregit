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
implements|implements
name|Cloneable
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
argument_list|<>
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
name|bodyAnalyzed
init|=
literal|false
decl_stmt|;
specifier|private
name|FunctionCall
name|call
decl_stmt|;
specifier|private
name|boolean
name|hasBeenReset
init|=
literal|false
decl_stmt|;
specifier|protected
name|boolean
name|visited
init|=
literal|false
decl_stmt|;
specifier|private
name|List
argument_list|<
name|ClosureVariable
argument_list|>
name|closureVariables
init|=
literal|null
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
operator|.
name|simplify
argument_list|()
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
specifier|final
name|String
name|varName
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
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
name|addVariable
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|QName
operator|.
name|IllegalQNameException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|varName
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|addVariable
parameter_list|(
name|QName
name|varName
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|parameters
operator|.
name|contains
argument_list|(
name|varName
argument_list|)
condition|)
block|{
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
block|}
name|parameters
operator|.
name|add
argument_list|(
name|varName
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
name|hasBeenReset
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|call
operator|!=
literal|null
operator|&&
operator|!
name|call
operator|.
name|isRecursive
argument_list|()
condition|)
block|{
comment|// Save the local variable stack
specifier|final
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
if|if
condition|(
name|closureVariables
operator|!=
literal|null
condition|)
comment|// if this is a inline function, context variables are known
block|{
name|context
operator|.
name|restoreStack
argument_list|(
name|closureVariables
argument_list|)
expr_stmt|;
block|}
try|try
block|{
name|LocalVariable
name|var
decl_stmt|;
for|for
control|(
specifier|final
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
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|body
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
block|}
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
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.Expression#eval(org.exist.dom.persistent.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
comment|// make sure reset state is called after query has finished
name|hasBeenReset
operator|=
literal|false
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
literal|true
argument_list|)
decl_stmt|;
if|if
condition|(
name|closureVariables
operator|!=
literal|null
condition|)
block|{
name|context
operator|.
name|restoreStack
argument_list|(
name|closureVariables
argument_list|)
expr_stmt|;
block|}
name|Sequence
name|result
init|=
literal|null
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
block|{
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
block|}
name|context
operator|.
name|declareVariableBinding
argument_list|(
name|var
argument_list|)
expr_stmt|;
name|Cardinality
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
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EMPTY_SEQUENCE
expr_stmt|;
block|}
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
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|_MANY
expr_stmt|;
block|}
else|else
block|{
name|actualCardinality
operator|=
name|Cardinality
operator|.
name|EXACTLY_ONE
expr_stmt|;
block|}
if|if
condition|(
operator|!
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
operator|.
name|isSuperCardinalityOrEqualOf
argument_list|(
name|actualCardinality
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
literal|"Invalid cardinality for parameter $"
operator|+
name|varName
operator|+
literal|". Expected "
operator|+
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
operator|.
name|getHumanDescription
argument_list|()
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
block|}
name|result
operator|=
name|body
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
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
argument_list|,
name|result
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
specifier|final
name|FunctionSignature
name|signature
init|=
name|getSignature
argument_list|()
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
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
block|}
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
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
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
specifier|final
name|FunctionSignature
name|signature
init|=
name|getSignature
argument_list|()
decl_stmt|;
specifier|final
name|StringBuilder
name|buf
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|signature
operator|.
name|getName
argument_list|()
operator|!=
literal|null
condition|)
block|{
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
block|}
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
block|{
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
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
if|if
condition|(
name|hasBeenReset
condition|)
block|{
return|return;
block|}
name|hasBeenReset
operator|=
literal|true
expr_stmt|;
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
comment|// Question: understand this test. Why not reset even is not in recursion ?
comment|// Answer: would lead to an infinite loop if the function is recursive.
name|bodyAnalyzed
operator|=
literal|false
expr_stmt|;
if|if
condition|(
name|body
operator|!=
literal|null
condition|)
block|{
name|body
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
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
if|if
condition|(
name|visited
condition|)
block|{
return|return;
block|}
name|visited
operator|=
literal|true
expr_stmt|;
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
specifier|public
specifier|synchronized
name|Object
name|clone
parameter_list|()
block|{
try|try
block|{
specifier|final
name|UserDefinedFunction
name|clone
init|=
operator|(
name|UserDefinedFunction
operator|)
name|super
operator|.
name|clone
argument_list|()
decl_stmt|;
name|clone
operator|.
name|currentArguments
operator|=
literal|null
expr_stmt|;
name|clone
operator|.
name|contextDocs
operator|=
literal|null
expr_stmt|;
name|clone
operator|.
name|body
operator|=
name|this
operator|.
name|body
expr_stmt|;
comment|// so body will be analyzed and optimized for all calls of such functions in recursion.
return|return
name|clone
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|CloneNotSupportedException
name|e
parameter_list|)
block|{
comment|// this shouldn't happen, since we are Cloneable
throw|throw
operator|new
name|InternalError
argument_list|()
throw|;
block|}
block|}
specifier|public
name|FunctionCall
name|getCaller
parameter_list|()
block|{
return|return
name|call
return|;
block|}
specifier|public
name|void
name|setCaller
parameter_list|(
name|FunctionCall
name|call
parameter_list|)
block|{
name|this
operator|.
name|call
operator|=
name|call
expr_stmt|;
block|}
specifier|public
name|void
name|setClosureVariables
parameter_list|(
name|List
argument_list|<
name|ClosureVariable
argument_list|>
name|vars
parameter_list|)
block|{
name|this
operator|.
name|closureVariables
operator|=
name|vars
expr_stmt|;
if|if
condition|(
name|vars
operator|!=
literal|null
condition|)
block|{
comment|// register the closure with the context so it gets cleared after execution
name|context
operator|.
name|pushClosure
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|List
argument_list|<
name|ClosureVariable
argument_list|>
name|getClosureVariables
parameter_list|()
block|{
return|return
name|closureVariables
return|;
block|}
specifier|protected
name|Sequence
index|[]
name|getCurrentArguments
parameter_list|()
block|{
return|return
name|currentArguments
return|;
block|}
block|}
end_class

end_unit

