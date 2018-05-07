begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|invoke
operator|.
name|LambdaMetafactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandle
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodHandles
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

begin_import
import|import static
name|java
operator|.
name|lang
operator|.
name|invoke
operator|.
name|MethodType
operator|.
name|methodType
import|;
end_import

begin_comment
comment|/**  * Abstract base class for all built-in and user-defined functions.  *<p>  * Built-in functions just extend this class. A new function instance  * will be created for each function call. Subclasses<b>have</b> to  * provide a function signature to the constructor.  *<p>  * User-defined functions extend class {@link org.exist.xquery.UserDefinedFunction},  * which is again a subclass of Function. They will not be called directly, but through a  * {@link org.exist.xquery.FunctionCall} object, which checks the type and cardinality of  * all arguments and takes care that the current execution context is saved properly.  *  * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Function
extends|extends
name|PathExpr
block|{
specifier|private
specifier|static
specifier|final
name|MethodHandles
operator|.
name|Lookup
name|LOOKUP
init|=
name|MethodHandles
operator|.
name|lookup
argument_list|()
decl_stmt|;
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
comment|/**      * Flag to indicate if argument types are statically checked.      * This is set to true by default (meaning: no further checks needed).      * Method {@link #setArguments(java.util.List)} will set it to false      * (unless overwritten), thus enforcing a check.      */
specifier|protected
name|boolean
name|argumentsChecked
init|=
literal|true
decl_stmt|;
comment|/**      * Internal constructor. Subclasses should<b>always</b> call this and      * pass the current context and their function signature.      *      * @param context      * @param signature      */
specifier|protected
name|Function
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
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
specifier|final
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
comment|/**      * Returns the module to which this function belongs      */
specifier|protected
name|Module
name|getParentModule
parameter_list|()
block|{
return|return
name|context
operator|.
name|getModule
argument_list|(
name|mySignature
operator|.
name|getName
argument_list|()
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
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
block|{
return|return
name|Type
operator|.
name|ITEM
return|;
block|}
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
block|{
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
block|}
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
annotation|@
name|Override
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
block|{
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
block|}
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
comment|/**      * Create a built-in function from the specified class.      *      * @return the created function or null if the class could not be initialized.      */
specifier|public
specifier|static
name|Function
name|createFunction
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|XQueryAST
name|ast
parameter_list|,
specifier|final
name|FunctionDef
name|def
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|,
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"Class for function is null"
argument_list|)
throw|;
block|}
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
name|fclazz
init|=
name|def
operator|.
name|getImplementingClass
argument_list|()
decl_stmt|;
if|if
condition|(
name|fclazz
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|,
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"Class for function is null"
argument_list|)
throw|;
block|}
try|try
block|{
name|Function
name|function
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// attempt for a constructor that takes 1 argument
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|LOOKUP
operator|.
name|findConstructor
argument_list|(
name|fclazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|XQueryContext
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|XQueryContext
argument_list|,
name|Function
argument_list|>
name|ctor
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
argument_list|<
name|XQueryContext
argument_list|,
name|Function
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|LOOKUP
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Function
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
decl_stmt|;
name|function
operator|=
name|ctor
operator|.
name|apply
argument_list|(
name|context
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchMethodException
name|nsme1
parameter_list|)
block|{
try|try
block|{
comment|// attempt for a constructor that takes 2 arguments
specifier|final
name|MethodHandle
name|methodHandle
init|=
name|LOOKUP
operator|.
name|findConstructor
argument_list|(
name|fclazz
argument_list|,
name|methodType
argument_list|(
name|void
operator|.
name|class
argument_list|,
name|XQueryContext
operator|.
name|class
argument_list|,
name|FunctionSignature
operator|.
name|class
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
argument_list|<
name|XQueryContext
argument_list|,
name|FunctionSignature
argument_list|,
name|Function
argument_list|>
name|ctor
init|=
operator|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
argument_list|<
name|XQueryContext
argument_list|,
name|FunctionSignature
argument_list|,
name|Function
argument_list|>
operator|)
name|LambdaMetafactory
operator|.
name|metafactory
argument_list|(
name|LOOKUP
argument_list|,
literal|"apply"
argument_list|,
name|methodType
argument_list|(
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|BiFunction
operator|.
name|class
argument_list|)
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
operator|.
name|erase
argument_list|()
argument_list|,
name|methodHandle
argument_list|,
name|methodHandle
operator|.
name|type
argument_list|()
argument_list|)
operator|.
name|getTarget
argument_list|()
operator|.
name|invokeExact
argument_list|()
decl_stmt|;
name|function
operator|=
name|ctor
operator|.
name|apply
argument_list|(
name|context
argument_list|,
name|def
operator|.
name|getSignature
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchMethodException
name|nsme2
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|,
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"Constructor not found"
argument_list|)
throw|;
block|}
block|}
name|function
operator|.
name|setLocation
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|,
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|function
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
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
operator|.
name|getLine
argument_list|()
argument_list|,
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"Function implementation class "
operator|+
name|fclazz
operator|.
name|getName
argument_list|()
operator|+
literal|" not found"
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
comment|/**      * Set the parent expression of this function, i.e. the      * expression from which the function is called.      *      * @param parent      */
specifier|public
name|void
name|setParent
parameter_list|(
specifier|final
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
comment|/**      * Returns the expression from which this function gets called.      */
annotation|@
name|Override
specifier|public
name|Expression
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
comment|/**      * Set the (static) arguments for this function from a list of expressions.      *<p>      * This will also trigger a check on the type and cardinality of the      * passed argument expressions. By default, the method sets the      * argumentsChecked property to false, thus triggering the analyze method to      * perform a type check.      *<p>      * Classes overwriting this method are typically optimized functions and will      * handle type checks for arguments themselves.      *      * @param arguments      * @throws XPathException      */
specifier|public
name|void
name|setArguments
parameter_list|(
specifier|final
name|List
argument_list|<
name|Expression
argument_list|>
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
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0017
argument_list|,
literal|"Number of arguments of function "
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
block|}
name|steps
operator|.
name|clear
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|Expression
name|argument
range|:
name|arguments
control|)
block|{
name|steps
operator|.
name|add
argument_list|(
name|argument
operator|.
name|simplify
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|argumentsChecked
operator|=
literal|false
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
if|if
condition|(
operator|!
name|argumentsChecked
condition|)
block|{
specifier|final
name|SequenceType
index|[]
name|argumentTypes
init|=
name|mySignature
operator|.
name|getArgumentTypes
argument_list|()
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
block|{
name|argType
operator|=
name|argumentTypes
index|[
name|i
index|]
expr_stmt|;
block|}
specifier|final
name|Expression
name|next
init|=
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
decl_stmt|;
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
name|argumentsChecked
operator|=
literal|true
expr_stmt|;
block|}
comment|/**      * Statically check an argument against the sequence type specified in      * the signature.      *      * @param expr      * @param type      * @return The passed expression      * @throws XPathException      */
specifier|protected
name|Expression
name|checkArgument
parameter_list|(
name|Expression
name|expr
parameter_list|,
specifier|final
name|SequenceType
name|type
parameter_list|,
specifier|final
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
operator|||
name|expr
operator|instanceof
name|Placeholder
condition|)
block|{
return|return
name|expr
return|;
block|}
comment|// check cardinality if expected cardinality is not zero or more
name|boolean
name|cardinalityMatches
init|=
name|expr
operator|instanceof
name|VariableReference
operator|||
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
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|FUNC_EMPTY_SEQ_DISALLOWED
argument_list|,
name|Integer
operator|.
name|valueOf
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
name|argPosition
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
block|{
name|returnType
operator|=
name|Type
operator|.
name|ITEM
expr_stmt|;
block|}
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
block|{
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
block|}
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
comment|//If the required type is an atomic type, convert the argument to an atomic
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
block|}
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
block|{
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
block|}
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
comment|//If the required type is an atomic type, convert the argument to an atomic
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
comment|//Because () is seen as a node
operator|(
name|Cardinality
operator|.
name|checkCardinality
argument_list|(
name|type
operator|.
name|getCardinality
argument_list|()
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
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
name|this
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
block|{
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
block|}
else|else
block|{
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
block|}
return|return
name|expr
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
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
name|unordered
operator|=
operator|(
name|contextInfo
operator|.
name|getFlags
argument_list|()
operator|&
name|UNORDERED
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
specifier|final
name|AnalyzeContextInfo
name|argContextInfo
init|=
operator|new
name|AnalyzeContextInfo
argument_list|(
name|contextInfo
argument_list|)
decl_stmt|;
name|getArgument
argument_list|(
name|i
argument_list|)
operator|.
name|analyze
argument_list|(
name|argContextInfo
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
specifier|abstract
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
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
specifier|final
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
block|{
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
block|}
specifier|final
name|int
name|argCount
init|=
name|getArgumentCount
argument_list|()
decl_stmt|;
specifier|final
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
comment|/**      * Get an argument expression by its position in the      * argument list.      *      * @param pos      */
specifier|public
name|Expression
name|getArgument
parameter_list|(
specifier|final
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
comment|/**      * Get the number of arguments passed to this function.      *      * @return number of arguments      */
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
annotation|@
name|Override
specifier|public
name|void
name|setPrimaryAxis
parameter_list|(
specifier|final
name|int
name|axis
parameter_list|)
block|{
block|}
comment|/**      * Return the name of this function.      *      * @return name of this function      */
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
comment|/**      * Get the signature of this function.      *      * @return signature of this function      */
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
specifier|final
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
name|getLocalPart
argument_list|()
argument_list|)
return|;
block|}
annotation|@
name|Override
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
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
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
specifier|final
name|Expression
name|e
range|:
name|steps
control|)
block|{
if|if
condition|(
name|moreThanOne
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
annotation|@
name|Override
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
specifier|final
name|Expression
name|step
range|:
name|steps
control|)
block|{
if|if
condition|(
name|moreThanOne
condition|)
block|{
name|result
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
block|}
name|moreThanOne
operator|=
literal|true
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|step
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
annotation|@
name|Override
specifier|public
name|void
name|accept
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|Expression
name|simplify
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
specifier|static
class|class
name|Placeholder
extends|extends
name|AbstractExpression
block|{
specifier|public
name|Placeholder
parameter_list|(
specifier|final
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
annotation|@
name|Override
specifier|public
name|void
name|analyze
parameter_list|(
specifier|final
name|AnalyzeContextInfo
name|contextInfo
parameter_list|)
throws|throws
name|XPathException
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|dump
parameter_list|(
specifier|final
name|ExpressionDumper
name|dumper
parameter_list|)
block|{
name|dumper
operator|.
name|display
argument_list|(
literal|'?'
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
name|contextSequence
parameter_list|,
specifier|final
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|ErrorCodes
operator|.
name|EXXQDY0001
argument_list|,
literal|"Internal error: function argument placeholder not expanded."
argument_list|)
throw|;
block|}
annotation|@
name|Override
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
block|}
end_class

end_unit

