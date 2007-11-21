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
name|util
operator|.
name|Messages
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
comment|// Declare it in Namespaces instead? /ljo
specifier|public
specifier|final
specifier|static
name|String
name|BUILTIN_FUNCTION_NS
init|=
literal|"http://www.w3.org/2005/xpath-functions"
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
comment|/** 	 * Create a built-in function from the specified class. 	 * @return the created function or null if the class could not be initialized. 	 */
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
name|FunctionDef
name|def
parameter_list|)
throws|throws
name|XPathException
block|{
name|Class
name|fclass
init|=
name|def
operator|.
name|getImplementingClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
operator|||
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
literal|"Class for function is null"
argument_list|)
throw|;
try|try
block|{
name|Object
name|initArgs
index|[]
init|=
block|{
name|context
block|}
decl_stmt|;
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
literal|null
decl_stmt|;
try|try
block|{
name|construct
operator|=
name|fclass
operator|.
name|getConstructor
argument_list|(
name|constructorArgs
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NoSuchMethodException
name|e
parameter_list|)
block|{
block|}
comment|// not found: check if the constructor takes two arguments
if|if
condition|(
name|construct
operator|==
literal|null
condition|)
block|{
name|constructorArgs
operator|=
operator|new
name|Class
index|[
literal|2
index|]
expr_stmt|;
name|constructorArgs
index|[
literal|0
index|]
operator|=
name|XQueryContext
operator|.
name|class
expr_stmt|;
name|constructorArgs
index|[
literal|1
index|]
operator|=
name|FunctionSignature
operator|.
name|class
expr_stmt|;
name|construct
operator|=
name|fclass
operator|.
name|getConstructor
argument_list|(
name|constructorArgs
argument_list|)
expr_stmt|;
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
literal|"Constructor not found"
argument_list|)
throw|;
name|initArgs
operator|=
operator|new
name|Object
index|[
literal|2
index|]
expr_stmt|;
name|initArgs
index|[
literal|0
index|]
operator|=
name|context
expr_stmt|;
name|initArgs
index|[
literal|1
index|]
operator|=
name|def
operator|.
name|getSignature
argument_list|()
expr_stmt|;
block|}
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
literal|"Function object does not implement interface function"
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
literal|"Function implementation class "
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
comment|/** 	 * Returns the expression from which this function 	 * gets called.          */
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
name|steps
operator|=
name|arguments
expr_stmt|;
block|}
comment|/**      * @throws XPathException      */
specifier|protected
name|void
name|checkArguments
parameter_list|()
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
name|getArgumentCount
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
name|getArgument
argument_list|(
name|i
argument_list|)
argument_list|,
name|argType
argument_list|,
name|i
operator|+
literal|1
argument_list|)
expr_stmt|;
name|steps
operator|.
name|set
argument_list|(
name|i
argument_list|,
name|next
argument_list|)
expr_stmt|;
block|}
block|}
comment|/** 	 * Statically check an argument against the sequence type specified in 	 * the signature. 	 *  	 * @param expr 	 * @param type 	 * @return The passed expression 	 * @throws XPathException 	 */
specifier|protected
name|Expression
name|checkArgument
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|SequenceType
name|type
parameter_list|,
name|int
name|argPosition
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
operator|!
name|cardinalityMatches
condition|)
block|{
if|if
condition|(
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
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|FUNC_EMPTY_SEQ_DISALLOWED
argument_list|,
operator|new
name|Integer
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
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
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_CARDINALITY
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|mySignature
argument_list|)
argument_list|)
expr_stmt|;
comment|// check return type if both types are not Type.ITEM
name|int
name|returnType
init|=
name|expr
operator|.
name|returnsType
argument_list|()
decl_stmt|;
if|if
condition|(
name|returnType
operator|==
name|Type
operator|.
name|ANY_TYPE
operator|||
name|returnType
operator|==
name|Type
operator|.
name|EMPTY
condition|)
name|returnType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
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
block|{
if|if
condition|(
name|type
operator|.
name|getNodeName
argument_list|()
operator|!=
literal|null
condition|)
name|expr
operator|=
operator|new
name|DynamicNameCheck
argument_list|(
name|context
argument_list|,
operator|new
name|NameTest
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|type
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|,
name|expr
argument_list|)
expr_stmt|;
return|return
name|expr
return|;
block|}
comment|//Loose argument check : we may move this, or a part hereof, to UntypedValueCheck
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
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_TYPE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|mySignature
argument_list|)
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
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_TYPE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|mySignature
argument_list|)
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
comment|//Strict argument check : we may move this, or a part hereof, to UntypedValueCheck
block|}
else|else
block|{
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
comment|//if (!(type.getPrimaryType() == Type.ATOMIC))
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
argument_list|,
operator|new
name|Error
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_TYPE
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|mySignature
argument_list|)
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
block|}
if|if
condition|(
name|returnType
operator|!=
name|Type
operator|.
name|ITEM
operator|&&
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
operator|!
operator|(
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
operator|||
comment|//because () is seen as a node
operator|(
name|type
operator|.
name|getPrimaryType
argument_list|()
operator|==
name|Type
operator|.
name|EMPTY
operator|&&
name|returnType
operator|==
name|Type
operator|.
name|NODE
operator|)
operator|)
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|FUNC_PARAM_TYPE_STATIC
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|argPosition
argument_list|)
argument_list|,
name|mySignature
argument_list|,
name|type
operator|.
name|toString
argument_list|()
argument_list|,
name|Type
operator|.
name|getTypeName
argument_list|(
name|returnType
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
operator|!
name|typeMatches
condition|)
block|{
if|if
condition|(
name|type
operator|.
name|getNodeName
argument_list|()
operator|!=
literal|null
condition|)
name|expr
operator|=
operator|new
name|DynamicNameCheck
argument_list|(
name|context
argument_list|,
operator|new
name|NameTest
argument_list|(
name|type
operator|.
name|getPrimaryType
argument_list|()
argument_list|,
name|type
operator|.
name|getNodeName
argument_list|()
argument_list|)
argument_list|,
name|expr
argument_list|)
expr_stmt|;
else|else
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
block|}
return|return
name|expr
return|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#analyze(org.exist.xquery.Expression)      */
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
comment|// statically check the argument list
name|checkArguments
argument_list|()
expr_stmt|;
comment|// call analyze for each argument
name|inPredicate
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|IN_PREDICATE
operator|)
operator|>
literal|0
expr_stmt|;
name|contextId
operator|=
name|contextInfo
operator|.
name|getContextId
argument_list|()
expr_stmt|;
name|contextInfo
operator|.
name|setParent
argument_list|(
name|this
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
name|getArgumentCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
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
comment|/** 	 * Get an argument expression by its position in the 	 * argument list. 	 *  	 * @param pos 	 */
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
comment|/** 	 * Get the number of arguments passed to this function. 	 *  	 * @return number of arguments 	 */
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
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
name|int
name|axis
parameter_list|)
block|{
block|}
comment|/** 	 * Return the name of this function. 	 *  	 * @return name of this function 	 */
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
comment|/** 	 * Get the signature of this function. 	 *  	 * @return signature of this function 	 */
specifier|public
name|FunctionSignature
name|getSignature
parameter_list|()
block|{
return|return
name|mySignature
return|;
block|}
specifier|public
name|boolean
name|isCalledAs
parameter_list|(
name|String
name|localName
parameter_list|)
block|{
return|return
name|localName
operator|.
name|equals
argument_list|(
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getLocalName
argument_list|()
argument_list|)
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
comment|/* (non-Javadoc)      * @see org.exist.xquery.PathExpr#dump(org.exist.xquery.util.ExpressionDumper)      */
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
name|boolean
name|moreThanOne
init|=
literal|false
decl_stmt|;
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
if|if
condition|(
name|moreThanOne
condition|)
name|dumper
operator|.
name|display
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|moreThanOne
operator|=
literal|true
expr_stmt|;
name|e
operator|.
name|dump
argument_list|(
name|dumper
argument_list|)
expr_stmt|;
block|}
name|dumper
operator|.
name|display
argument_list|(
literal|')'
argument_list|)
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
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|boolean
name|moreThanOne
init|=
literal|false
decl_stmt|;
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
if|if
condition|(
name|moreThanOne
condition|)
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|moreThanOne
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|e
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
literal|')'
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
name|visitBuiltinFunction
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

