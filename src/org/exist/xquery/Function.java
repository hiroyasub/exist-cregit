begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Native XML Database  *  Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|lang
operator|.
name|reflect
operator|.
name|Constructor
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
name|parser
operator|.
name|XQueryAST
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
comment|/**  * Abstract base class for all built-in and user-defined functions.  *   * Built-in functions just extend this class. A new function instance  * will be created for each function call. Subclasses<b>have</b> to  * provide a function signature to the constructor.  *   * User-defined functions extend class {@link org.exist.xquery.UserDefinedFunction},  * which is again a subclass of Function. They will not be called directly, but through a  * {@link org.exist.xquery.FunctionCall} object, which checks the type and cardinality of  * all arguments and takes care that the current execution context is saved properly.  *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Function
extends|extends
name|PathExpr
block|{
specifier|public
specifier|final
specifier|static
name|String
name|BUILTIN_FUNCTION_NS
init|=
literal|"http://www.w3.org/2003/05/xpath-functions"
decl_stmt|;
comment|// The signature of the function.
specifier|protected
name|FunctionSignature
name|mySignature
decl_stmt|;
comment|// The parent expression from which this function is called.
specifier|private
name|Expression
name|parent
decl_stmt|;
specifier|private
name|XQueryAST
name|astNode
init|=
literal|null
decl_stmt|;
comment|/** 	 * Internal constructor. Subclasses should<b>always</b> call this and 	 * pass the current context and their function signature. 	 *  	 * @param context 	 * @param signature 	 */
specifier|protected
name|Function
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
argument_list|)
expr_stmt|;
name|this
operator|.
name|mySignature
operator|=
name|signature
expr_stmt|;
block|}
specifier|protected
name|Function
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
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.PathExpr#returnsType() 	 */
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
if|if
condition|(
name|mySignature
operator|==
literal|null
condition|)
return|return
name|Type
operator|.
name|ITEM
return|;
comment|// Type is not known yet
if|if
condition|(
name|mySignature
operator|.
name|getReturnType
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Return type for function "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|+
literal|" is not defined"
argument_list|)
throw|;
return|return
name|mySignature
operator|.
name|getReturnType
argument_list|()
operator|.
name|getPrimaryType
argument_list|()
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getCardinality() 	 */
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
if|if
condition|(
name|mySignature
operator|.
name|getReturnType
argument_list|()
operator|==
literal|null
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Return type for function "
operator|+
name|mySignature
operator|.
name|getName
argument_list|()
operator|+
literal|" is not defined"
argument_list|)
throw|;
return|return
name|mySignature
operator|.
name|getReturnType
argument_list|()
operator|.
name|getCardinality
argument_list|()
return|;
block|}
comment|/** 	 * Create a built-in function from the specified class. 	 *  	 * @param context 	 * @param fclass 	 * @return the created function or null if the class could not be initialized. 	 */
specifier|public
specifier|static
name|Function
name|createFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|XQueryAST
name|ast
parameter_list|,
name|Class
name|fclass
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
if|if
condition|(
name|fclass
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"class for function is null"
argument_list|)
throw|;
name|Class
name|constructorArgs
index|[]
init|=
block|{
name|XQueryContext
operator|.
name|class
block|}
decl_stmt|;
name|Constructor
name|construct
init|=
name|fclass
operator|.
name|getConstructor
argument_list|(
name|constructorArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|construct
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"constructor not found"
argument_list|)
throw|;
name|Object
name|initArgs
index|[]
init|=
block|{
name|context
block|}
decl_stmt|;
name|Object
name|obj
init|=
name|construct
operator|.
name|newInstance
argument_list|(
name|initArgs
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|Function
condition|)
block|{
operator|(
operator|(
name|Function
operator|)
name|obj
operator|)
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
return|return
operator|(
name|Function
operator|)
name|obj
return|;
block|}
else|else
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"function object does not implement interface function"
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"function "
operator|+
name|fclass
operator|.
name|getName
argument_list|()
operator|+
literal|" not found"
argument_list|)
throw|;
block|}
block|}
comment|/** 	 * Set the parent expression of this function, i.e. the 	 * expression from which the function is called. 	 *  	 * @param parent 	 */
specifier|public
name|void
name|setParent
parameter_list|(
name|Expression
name|parent
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
block|}
comment|/** 	 * Returns the expression from which this function 	 * gets called. 	 *  	 * @return 	 */
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/** 	 * Set the (static) arguments for this function from a list of expressions. 	 *  	 * This will also check the type and cardinality of the 	 * passed argument expressions. 	 *  	 * @param arguments 	 * @throws XPathException 	 */
specifier|public
name|void
name|setArguments
parameter_list|(
name|List
name|arguments
parameter_list|)
throws|throws
name|XPathException
block|{
name|SequenceType
index|[]
name|argumentTypes
init|=
name|mySignature
operator|.
name|getArgumentTypes
argument_list|()
decl_stmt|;
if|if
condition|(
operator|(
operator|!
name|mySignature
operator|.
name|isOverloaded
argument_list|()
operator|)
operator|&&
name|arguments
operator|.
name|size
argument_list|()
operator|!=
name|mySignature
operator|.
name|getArgumentCount
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"number of arguments to function "
operator|+
name|getName
argument_list|()
operator|+
literal|" doesn't match function signature (expected "
operator|+
name|mySignature
operator|.
name|getArgumentCount
argument_list|()
operator|+
literal|", got "
operator|+
name|arguments
operator|.
name|size
argument_list|()
operator|+
literal|')'
argument_list|)
throw|;
name|Expression
name|next
decl_stmt|;
name|SequenceType
name|argType
init|=
literal|null
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
name|arguments
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|argumentTypes
operator|!=
literal|null
operator|&&
name|i
operator|<
name|argumentTypes
operator|.
name|length
condition|)
name|argType
operator|=
name|argumentTypes
index|[
name|i
index|]
expr_stmt|;
name|next
operator|=
name|checkArgument
argument_list|(
operator|(
name|Expression
operator|)
name|arguments
operator|.
name|get
argument_list|(
name|i
argument_list|)
argument_list|,
name|argType
argument_list|)
expr_stmt|;
name|steps
operator|.
name|add
argument_list|(
name|next
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Statically check an argument against the sequence type specified in 	 * the signature. 	 *  	 * @param expr 	 * @param type 	 * @return 	 * @throws XPathException 	 */
specifier|protected
name|Expression
name|checkArgument
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|SequenceType
name|type
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|type
operator|==
literal|null
condition|)
return|return
name|expr
return|;
comment|// check cardinality if expected cardinality is not zero or more
name|boolean
name|cardinalityMatches
init|=
name|type
operator|.
name|getCardinality
argument_list|()
operator|==
name|Cardinality
operator|.
name|ZERO_OR_MORE
decl_stmt|;
if|if
condition|(
operator|!
name|cardinalityMatches
condition|)
block|{
name|cardinalityMatches
operator|=
operator|(
name|expr
operator|.
name|getCardinality
argument_list|()
operator||
name|type
operator|.
name|getCardinality
argument_list|()
operator|)
operator|==
name|type
operator|.
name|getCardinality
argument_list|()
expr_stmt|;
if|if
condition|(
operator|(
operator|!
name|cardinalityMatches
operator|)
operator|&&
name|expr
operator|.
name|getCardinality
argument_list|()
operator|==
name|Cardinality
operator|.
name|ZERO
operator|&&
operator|(
name|type
operator|.
name|getCardinality
argument_list|()
operator|&
name|Cardinality
operator|.
name|ZERO
operator|)
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|astNode
argument_list|,
literal|"Argument "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
operator|+
literal|" is empty. An "
operator|+
literal|"empty argument is not allowed here."
argument_list|)
throw|;
block|}
comment|// check return type if both types are not Type.ITEM
name|int
name|returnType
init|=
name|expr
operator|.
name|returnsType
argument_list|()
decl_stmt|;
name|boolean
name|typeMatches
init|=
name|type
operator|.
name|getPrimaryType
argument_list|()
operator|==
name|Type
operator|.
name|ITEM
decl_stmt|;
name|typeMatches
operator|=
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
argument_list|,
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|typeMatches
operator|&&
name|cardinalityMatches
condition|)
return|return
name|expr
return|;
if|if
condition|(
name|context
operator|.
name|isBackwardsCompatible
argument_list|()
condition|)
block|{
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|STRING
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
name|expr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|Type
operator|.
name|ATOMIC
expr_stmt|;
block|}
name|expr
operator|=
operator|new
name|AtomicToString
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|Type
operator|.
name|STRING
expr_stmt|;
block|}
if|else if
condition|(
name|type
operator|.
name|getPrimaryType
argument_list|()
operator|==
name|Type
operator|.
name|NUMBER
operator|||
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|DOUBLE
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
name|expr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|Type
operator|.
name|ATOMIC
expr_stmt|;
block|}
name|expr
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|type
operator|.
name|getPrimaryType
argument_list|()
expr_stmt|;
block|}
block|}
comment|// if the required type is an atomic type, convert the argument to an atomic
if|if
condition|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
argument_list|,
name|Type
operator|.
name|ATOMIC
argument_list|)
condition|)
name|expr
operator|=
operator|new
name|Atomize
argument_list|(
name|context
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|type
operator|.
name|getPrimaryType
argument_list|()
operator|==
name|Type
operator|.
name|ATOMIC
operator|)
condition|)
name|expr
operator|=
operator|new
name|UntypedValueCheck
argument_list|(
name|context
argument_list|,
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|returnType
operator|=
name|expr
operator|.
name|returnsType
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|returnType
argument_list|,
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
operator|(
operator|!
name|Type
operator|.
name|subTypeOf
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|returnType
argument_list|)
operator|)
operator|&&
name|returnType
operator|!=
name|Type
operator|.
name|ITEM
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|astNode
argument_list|,
literal|"Supplied argument "
operator|+
name|expr
operator|.
name|pprint
argument_list|()
operator|+
literal|" doesn't match required type: required: "
operator|+
name|type
operator|.
name|toString
argument_list|()
operator|+
literal|"; got: "
operator|+
name|Type
operator|.
name|getTypeName
argument_list|(
name|returnType
argument_list|)
operator|+
name|Cardinality
operator|.
name|display
argument_list|(
name|expr
operator|.
name|getCardinality
argument_list|()
argument_list|)
argument_list|)
throw|;
block|}
if|if
condition|(
operator|!
name|typeMatches
condition|)
name|expr
operator|=
operator|new
name|DynamicTypeCheck
argument_list|(
name|context
argument_list|,
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
operator|!
name|cardinalityMatches
condition|)
name|expr
operator|=
operator|new
name|DynamicCardinalityCheck
argument_list|(
name|context
argument_list|,
name|type
operator|.
name|getCardinality
argument_list|()
argument_list|,
name|expr
argument_list|)
expr_stmt|;
return|return
name|expr
return|;
block|}
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
parameter_list|)
throws|throws
name|XPathException
function_decl|;
specifier|public
name|Sequence
index|[]
name|getArguments
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
specifier|final
name|int
name|argCount
init|=
name|getArgumentCount
argument_list|()
decl_stmt|;
name|Sequence
index|[]
name|args
init|=
operator|new
name|Sequence
index|[
name|argCount
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
name|argCount
condition|;
name|i
operator|++
control|)
block|{
name|args
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
block|}
return|return
name|args
return|;
block|}
comment|/** 	 * Get an argument expression by its position in the 	 * argument list. 	 *  	 * @param pos 	 * @return 	 */
specifier|public
name|Expression
name|getArgument
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
name|getExpression
argument_list|(
name|pos
argument_list|)
return|;
block|}
comment|/** 	 * Get the number of arguments passed to this function. 	 *  	 * @return 	 */
specifier|public
name|int
name|getArgumentCount
parameter_list|()
block|{
return|return
name|steps
operator|.
name|size
argument_list|()
return|;
block|}
comment|/** 	 * Return the name of this function. 	 *  	 * @return 	 */
specifier|public
name|QName
name|getName
parameter_list|()
block|{
return|return
name|mySignature
operator|.
name|getName
argument_list|()
return|;
block|}
comment|/** 	 * Get the signature of this function. 	 *  	 * @return 	 */
specifier|public
name|FunctionSignature
name|getSignature
parameter_list|()
block|{
return|return
name|mySignature
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.AbstractExpression#getDependencies() 	 */
specifier|public
name|int
name|getDependencies
parameter_list|()
block|{
return|return
name|Dependency
operator|.
name|CONTEXT_ITEM
operator||
name|Dependency
operator|.
name|CONTEXT_SET
return|;
block|}
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
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
name|Iterator
name|i
init|=
name|steps
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
name|Expression
name|e
init|=
operator|(
name|Expression
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|e
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|deleteCharAt
argument_list|(
name|buf
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
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
specifier|public
name|void
name|setASTNode
parameter_list|(
name|XQueryAST
name|ast
parameter_list|)
block|{
name|this
operator|.
name|astNode
operator|=
name|ast
expr_stmt|;
block|}
specifier|public
name|XQueryAST
name|getASTNode
parameter_list|()
block|{
return|return
name|astNode
return|;
block|}
block|}
end_class

end_unit

