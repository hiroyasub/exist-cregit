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
name|java
operator|.
name|util
operator|.
name|List
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

begin_comment
comment|/**  * Represents a call to a user-defined function   * {@link org.exist.xquery.functions.UserDefinedFunction}.  *   * FunctionCall wraps around a user-defined function. It makes sure that all function parameters  * are checked against the signature of the function.   *   * @author wolf  */
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
specifier|public
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
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
comment|/**  	 * Evaluates all arguments, then forwards them to the user-defined function. 	 *  	 * The return value of the user-defined function will be checked against the 	 * provided function signature. 	 *  	 * @see org.exist.xquery.Expression#eval(org.exist.dom.DocumentSet, org.exist.xquery.value.Sequence, org.exist.xquery.value.Item) 	 */
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
name|functionDef
operator|.
name|setArguments
argument_list|(
name|seq
argument_list|)
expr_stmt|;
comment|//		context.pushLocalContext(true);
name|LocalVariable
name|mark
init|=
name|context
operator|.
name|markLocalVariables
argument_list|()
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
name|e
operator|.
name|prependMessage
argument_list|(
literal|"in call to function "
operator|+
name|functionDef
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
comment|//			context.popLocalContext();
name|context
operator|.
name|popLocalVariables
argument_list|(
name|mark
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#resetState() 	 */
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|functionDef
operator|.
name|resetState
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

