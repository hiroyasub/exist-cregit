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
name|com
operator|.
name|sun
operator|.
name|xacml
operator|.
name|ctx
operator|.
name|RequestCtx
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
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|ExistPDP
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
name|Error
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
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Represents a call to a user-defined function   * {@link org.exist.xquery.UserDefinedFunction}.  *   * FunctionCall wraps around a user-defined function. It makes sure that all function parameters  * are checked against the signature of the function.   *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunctionCall
extends|extends
name|Function
block|{
specifier|private
name|UserDefinedFunction
name|functionDef
decl_stmt|;
specifier|private
name|Expression
name|expression
decl_stmt|;
comment|// the name of the function. Used for forward references.
specifier|private
name|QName
name|name
init|=
literal|null
decl_stmt|;
specifier|private
name|List
name|arguments
init|=
literal|null
decl_stmt|;
specifier|private
name|boolean
name|isRecursive
init|=
literal|false
decl_stmt|;
specifier|private
name|boolean
name|analyzed
init|=
literal|false
decl_stmt|;
specifier|public
name|FunctionCall
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|QName
name|name
parameter_list|,
name|List
name|arguments
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|arguments
operator|=
name|arguments
expr_stmt|;
block|}
specifier|public
name|FunctionCall
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|UserDefinedFunction
name|functionDef
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|)
expr_stmt|;
name|setFunction
argument_list|(
name|functionDef
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|setFunction
parameter_list|(
name|UserDefinedFunction
name|functionDef
parameter_list|)
block|{
name|this
operator|.
name|functionDef
operator|=
name|functionDef
expr_stmt|;
name|this
operator|.
name|mySignature
operator|=
name|functionDef
operator|.
name|getSignature
argument_list|()
expr_stmt|;
name|this
operator|.
name|expression
operator|=
name|functionDef
expr_stmt|;
name|SequenceType
name|returnType
init|=
name|functionDef
operator|.
name|getSignature
argument_list|()
operator|.
name|getReturnType
argument_list|()
decl_stmt|;
comment|// add return type checks
if|if
condition|(
name|returnType
operator|.
name|getCardinality
argument_list|()
operator|!=
name|Cardinality
operator|.
name|ZERO_OR_MORE
condition|)
name|expression
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|returnType
operator|.
name|getCardinality
argument_list|()
argument_list|,
name|expression
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_RETURN_CARDINALITY
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|expression
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expression
argument_list|)
expr_stmt|;
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|NUMBER
argument_list|)
condition|)
name|expression
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|returnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|expression
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_RETURN_TYPE
argument_list|)
argument_list|)
expr_stmt|;
if|else if
condition|(
name|returnType
operator|.
name|getPrimaryType
argument_list|()
operator|!=
name|Type
operator|.
name|ITEM
condition|)
name|expression
operator|=
operator|new
name|DynamicTypeCheck
argument_list|(
name|context
argument_list|,
name|returnType
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|expression
argument_list|)
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
name|analyzed
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
name|removeFlag
argument_list|(
name|IN_NODE_CONSTRUCTOR
argument_list|)
expr_stmt|;
name|super
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
if|if
condition|(
name|context
operator|.
name|tailRecursiveCall
argument_list|(
name|functionDef
operator|.
name|getSignature
argument_list|()
argument_list|)
condition|)
block|{
name|isRecursive
operator|=
literal|true
expr_stmt|;
block|}
name|context
operator|.
name|functionStart
argument_list|(
name|functionDef
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|expression
operator|.
name|analyze
argument_list|(
name|newContextInfo
argument_list|)
expr_stmt|;
name|analyzed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|context
operator|.
name|functionEnd
argument_list|()
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Called by {@link XQueryContext} to resolve a call to a function that has not      * yet been declared. XQueryContext remembers all calls to undeclared functions      * and tries to resolve them after parsing has completed.      *       * @param functionDef      * @throws XPathException      */
specifier|public
name|void
name|resolveForwardReference
parameter_list|(
name|UserDefinedFunction
name|functionDef
parameter_list|)
throws|throws
name|XPathException
block|{
name|setFunction
argument_list|(
name|functionDef
argument_list|)
expr_stmt|;
name|setArguments
argument_list|(
name|arguments
argument_list|)
expr_stmt|;
name|arguments
operator|=
literal|null
expr_stmt|;
name|name
operator|=
literal|null
expr_stmt|;
block|}
specifier|public
name|int
name|getArgumentCount
parameter_list|()
block|{
if|if
condition|(
name|arguments
operator|==
literal|null
condition|)
return|return
name|super
operator|.
name|getArgumentCount
argument_list|()
return|;
else|else
return|return
name|arguments
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**  	 * Evaluates all arguments, then forwards them to the user-defined function. 	 *  	 * The return value of the user-defined function will be checked against the 	 * provided function signature. 	 *  	 * @see org.exist.xquery.Expression#eval(Sequence, Item) 	 */
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
name|Sequence
index|[]
name|seq
init|=
operator|new
name|Sequence
index|[
name|getArgumentCount
argument_list|()
index|]
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
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|seq
index|[
name|i
index|]
operator|=
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
expr_stmt|;
comment|//			System.out.println("found " + seq[i].getLength() + " for " + getArgument(i).pprint());
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getLine
argument_list|()
operator|==
literal|0
condition|)
name|e
operator|.
name|setASTNode
argument_list|(
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
comment|// append location of the function call to the exception message:
name|e
operator|.
name|addFunctionCall
argument_list|(
name|functionDef
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
block|}
name|Sequence
name|result
init|=
name|evalFunction
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|,
name|seq
argument_list|)
decl_stmt|;
try|try
block|{
comment|//Don't check deferred calls : it would result in a stack overflow
comment|//TODO : find a solution or... is it already here ?
if|if
condition|(
operator|!
operator|(
name|result
operator|instanceof
name|DeferredFunctionCall
operator|)
operator|&&
comment|//Don't test on empty sequences since they can have several types
comment|//TODO : add a prior cardinality check on wether an empty result is allowed or not
comment|//TODO : should we introduce a deffered type check on VirtualNodeSet
comment|// and trigger it when the nodeSet is realized ?
operator|!
operator|(
name|result
operator|instanceof
name|VirtualNodeSet
operator|)
operator|&&
operator|!
name|result
operator|.
name|isEmpty
argument_list|()
condition|)
name|getSignature
argument_list|()
operator|.
name|getReturnType
argument_list|()
operator|.
name|checkType
argument_list|(
name|result
operator|.
name|getItemType
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"err:XPTY0004 in function '"
operator|+
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
operator|+
literal|"'. "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
throw|;
block|}
return|return
name|result
return|;
block|}
comment|/**      * @param contextSequence      * @param contextItem      * @param seq      * @throws XPathException      */
specifier|public
name|Sequence
name|evalFunction
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|,
name|Sequence
index|[]
name|seq
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
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
block|}
comment|//check access to the method
try|try
block|{
name|ExistPDP
name|pdp
init|=
name|context
operator|.
name|getPDP
argument_list|()
decl_stmt|;
if|if
condition|(
name|pdp
operator|!=
literal|null
condition|)
block|{
name|RequestCtx
name|request
init|=
name|pdp
operator|.
name|getRequestHelper
argument_list|()
operator|.
name|createFunctionRequest
argument_list|(
name|context
argument_list|,
literal|null
argument_list|,
name|getName
argument_list|()
argument_list|)
decl_stmt|;
comment|//if request is null, this function belongs to a main module and is allowed to be called
comment|//otherwise, the access must be checked
if|if
condition|(
name|request
operator|!=
literal|null
condition|)
name|pdp
operator|.
name|evaluate
argument_list|(
name|request
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|PermissionDeniedException
name|pde
parameter_list|)
block|{
name|XPathException
name|xe
init|=
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Access to function '"
operator|+
name|getName
argument_list|()
operator|+
literal|"'  denied."
argument_list|,
name|pde
argument_list|)
decl_stmt|;
name|xe
operator|.
name|addFunctionCall
argument_list|(
name|functionDef
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|xe
throw|;
block|}
name|functionDef
operator|.
name|setArguments
argument_list|(
name|seq
argument_list|)
expr_stmt|;
if|if
condition|(
name|isRecursive
condition|)
block|{
comment|//            LOG.warn("Tail recursive function: " + functionDef.getSignature().toString());
return|return
operator|new
name|DeferredFunctionCallImpl
argument_list|(
name|functionDef
operator|.
name|getSignature
argument_list|()
argument_list|,
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
return|;
block|}
else|else
block|{
name|context
operator|.
name|functionStart
argument_list|(
name|functionDef
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
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
name|Sequence
name|returnSeq
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
while|while
condition|(
name|returnSeq
operator|instanceof
name|DeferredFunctionCall
operator|&&
name|functionDef
operator|.
name|getSignature
argument_list|()
operator|.
name|equals
argument_list|(
operator|(
operator|(
name|DeferredFunctionCall
operator|)
name|returnSeq
operator|)
operator|.
name|getSignature
argument_list|()
argument_list|)
condition|)
block|{
comment|//    				 LOG.debug("Executing function: " + functionDef.getSignature());
name|returnSeq
operator|=
operator|(
operator|(
name|DeferredFunctionCall
operator|)
name|returnSeq
operator|)
operator|.
name|execute
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|context
operator|.
name|isProfilingEnabled
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
name|returnSeq
argument_list|)
expr_stmt|;
return|return
name|returnSeq
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getLine
argument_list|()
operator|==
literal|0
condition|)
name|e
operator|.
name|setASTNode
argument_list|(
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
comment|// append location of the function call to the exception message:
name|e
operator|.
name|addFunctionCall
argument_list|(
name|functionDef
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
name|context
operator|.
name|functionEnd
argument_list|()
expr_stmt|;
block|}
block|}
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
name|super
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|functionDef
operator|.
name|resetState
argument_list|(
name|postOptimization
argument_list|)
expr_stmt|;
name|analyzed
operator|=
literal|false
expr_stmt|;
comment|//TODO : reset expression ?
block|}
specifier|public
name|void
name|accept
parameter_list|(
name|ExpressionVisitor
name|visitor
parameter_list|)
block|{
comment|// forward to the called function
name|functionDef
operator|.
name|accept
argument_list|(
name|visitor
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|DeferredFunctionCallImpl
extends|extends
name|DeferredFunctionCall
block|{
specifier|private
name|Sequence
name|contextSequence
decl_stmt|;
specifier|private
name|Item
name|contextItem
decl_stmt|;
specifier|public
name|DeferredFunctionCallImpl
parameter_list|(
name|FunctionSignature
name|signature
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
block|{
name|super
argument_list|(
name|signature
argument_list|)
expr_stmt|;
name|this
operator|.
name|contextSequence
operator|=
name|contextSequence
expr_stmt|;
name|this
operator|.
name|contextItem
operator|=
name|contextItem
expr_stmt|;
block|}
specifier|protected
name|Sequence
name|execute
parameter_list|()
throws|throws
name|XPathException
block|{
name|context
operator|.
name|pushDocumentContext
argument_list|()
expr_stmt|;
name|context
operator|.
name|functionStart
argument_list|(
name|functionDef
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
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
name|Sequence
name|returnSeq
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|,
name|contextItem
argument_list|)
decl_stmt|;
comment|//                LOG.debug("Returning from execute()");
return|return
name|returnSeq
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
if|if
condition|(
name|e
operator|.
name|getLine
argument_list|()
operator|==
literal|0
condition|)
name|e
operator|.
name|setASTNode
argument_list|(
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
comment|// append location of the function call to the exception message:
name|e
operator|.
name|addFunctionCall
argument_list|(
name|functionDef
argument_list|,
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
name|context
operator|.
name|functionEnd
argument_list|()
expr_stmt|;
name|context
operator|.
name|popDocumentContext
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

