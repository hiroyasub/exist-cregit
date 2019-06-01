begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|xquery
operator|.
name|value
operator|.
name|FunctionParameterSequenceType
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
name|FunctionReturnSequenceType
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
name|Arrays
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|stream
operator|.
name|Stream
import|;
end_import

begin_comment
comment|/**  * A small DSL which makes defining Functions in Java code  * much simpler and more readable.  *  * It allows you to define functions signatures by using a DSL like:  *  *<pre>  * {@code  * private static final String FS_INSERT_BEFORE_NAME = "insert-before";  * static final FunctionSignature FS_INSERT_BEFORE = functionSignature(  *         FS_INSERT_BEFORE_NAME,  *         "Returns a specified part of binary data.",  *         returnsOpt(Type.BASE64_BINARY),  *         optParam("in", Type.BASE64_BINARY, "The binary data"),  *         param("offset", Type.INTEGER, "The offset to insert at"),  *         optParam("extra", Type.BASE64_BINARY, "The binary data to insert")  * );  * }  *</pre>  *  * If you have a function, that is "overloaded" with multiple arity  * applications possible, you can define it by using a DSL like:  *  *<pre>  * {@code  * private static final String FS_PART_NAME = "part";  * private static final FunctionParameterSequenceType FS_OPT_PARAM_IN = optParam("in", Type.BASE64_BINARY, "The binary data");  * private static final FunctionParameterSequenceType FS_PART_PARAM_OFFSET = param("offset", Type.INTEGER, "The offset to start reading from");  * static final FunctionSignature[] FS_PART = functionSignatures(  *         FS_PART_NAME,  *         "Returns a specified part of binary data.",  *         returnsOpt(Type.BASE64_BINARY),  *         arities(  *             arity(  *                 FS_OPT_PARAM_IN,  *                 FS_PART_PARAM_OFFSET  *             ),  *             arity(  *                 FS_OPT_PARAM_IN,  *                 FS_PART_PARAM_OFFSET,  *                 param("size", Type.INTEGER, "The number of octets to read from the offset")  *             )  *         )  * );  * }  *</pre>  *  * Finally, registering function definitions in a {@link AbstractInternalModule}  * can be done by using a DSL like:  *  *<pre>  * {@code  * public static final FunctionDef[] functions = functionDefs(  *         functionDefs(ConversionFunctions.class,  *             ConversionFunctions.FS_HEX,  *             ConversionFunctions.FS_BIN),  *  *         functionDefs(BasicFunctions.class,  *             BasicFunctions.FS_INSERT_BEFORE,  *             BasicFunctions.FS_PART[0],  *             BasicFunctions.FS_PART[1])  * );  * }  *</pre>  *  * @author<a href="mailto: adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|FunctionDSL
block|{
comment|/**      * Convenience DSL method to create a Function Definition      *      * @param functionSignature The signature of the function      * @param clazz The {@link Function} clazz where the function is implemented      *      * @return The function definition object      */
specifier|public
specifier|static
name|FunctionDef
name|functionDef
parameter_list|(
specifier|final
name|FunctionSignature
name|functionSignature
parameter_list|,
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
name|clazz
parameter_list|)
block|{
return|return
operator|new
name|FunctionDef
argument_list|(
name|functionSignature
argument_list|,
name|clazz
argument_list|)
return|;
block|}
comment|/**      * Convenience DSL method for supplying multiple function definitions using Varargs syntax      * where the implementations are all within the same {@link Function} class      *      * @param clazz The {@link Function} class which holds all the implementations described by {@code functionSignature}      * @param functionSignatures The signatures which are implemented by {@code clazz}      *      * @return The array of function definitions      */
specifier|public
specifier|static
name|FunctionDef
index|[]
name|functionDefs
parameter_list|(
specifier|final
name|Class
argument_list|<
name|?
extends|extends
name|Function
argument_list|>
name|clazz
parameter_list|,
specifier|final
name|FunctionSignature
modifier|...
name|functionSignatures
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|functionSignatures
argument_list|)
operator|.
name|map
argument_list|(
name|fs
lambda|->
name|functionDef
argument_list|(
name|fs
argument_list|,
name|clazz
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|FunctionDef
index|[]
operator|::
operator|new
argument_list|)
return|;
block|}
comment|/**      * Convenience DSL method for merging arrays of functions definitions using Varags syntax      *      * @param functionDefss The arrays of function definitions      *      * @return An array containing all function definitions supplied in {@code functionDefss}      */
specifier|public
specifier|static
name|FunctionDef
index|[]
name|functionDefs
parameter_list|(
specifier|final
name|FunctionDef
index|[]
modifier|...
name|functionDefss
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|functionDefss
argument_list|)
operator|.
name|map
argument_list|(
name|Arrays
operator|::
name|stream
argument_list|)
operator|.
name|reduce
argument_list|(
name|Stream
operator|::
name|concat
argument_list|)
operator|.
name|map
argument_list|(
name|s
lambda|->
name|s
operator|.
name|toArray
argument_list|(
name|FunctionDef
index|[]
operator|::
operator|new
argument_list|)
argument_list|)
operator|.
name|orElse
argument_list|(
operator|new
name|FunctionDef
index|[
literal|0
index|]
argument_list|)
return|;
block|}
comment|/**      * Creates a new Function signature using Varargs syntax      * for the parameters      *      * @param name The name of the function      * @param description A description of the purpose of the function      * @param returnType The type that is returned by the function      * @param paramTypes The (types of) parameters that the function accepts      *      * @return The function signature object      */
specifier|public
specifier|static
name|FunctionSignature
name|functionSignature
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|FunctionReturnSequenceType
name|returnType
parameter_list|,
specifier|final
name|FunctionParameterSequenceType
modifier|...
name|paramTypes
parameter_list|)
block|{
return|return
operator|new
name|FunctionSignature
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
name|paramTypes
argument_list|,
name|returnType
argument_list|)
return|;
block|}
comment|/**      * Deprecates a function signature      *      * @param deprecationDescription An explanation of the purpose for deprecation      * @param functionSignature The functionSignature to deprecate      *      * @return The function signature object      */
specifier|public
specifier|static
name|FunctionSignature
name|deprecated
parameter_list|(
specifier|final
name|String
name|deprecationDescription
parameter_list|,
specifier|final
name|FunctionSignature
name|functionSignature
parameter_list|)
block|{
return|return
operator|new
name|FunctionSignature
argument_list|(
name|functionSignature
operator|.
name|getName
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getDescription
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getArgumentTypes
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|deprecationDescription
argument_list|)
return|;
block|}
comment|/**      * Deprecates a function signature      *      * @param fsDeprecates The new functionSignature which deprecates<code>functionSignature</code>      * @param functionSignature The functionSignature to deprecate      *      * @return The function signature object      */
specifier|public
specifier|static
name|FunctionSignature
name|deprecated
parameter_list|(
specifier|final
name|FunctionSignature
name|fsDeprecates
parameter_list|,
specifier|final
name|FunctionSignature
name|functionSignature
parameter_list|)
block|{
return|return
operator|new
name|FunctionSignature
argument_list|(
name|functionSignature
operator|.
name|getName
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getDescription
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getArgumentTypes
argument_list|()
argument_list|,
name|functionSignature
operator|.
name|getReturnType
argument_list|()
argument_list|,
name|fsDeprecates
argument_list|)
return|;
block|}
comment|/**      * Creates multiple Function signatures for functions that have multiple arity definitions      *      * The {@code name}, {@code description} and {@code returnType} parameters remain the same for each function arity      * however the {@code variableParamType} allows you to specify different arguments for each arity definition      *      * @param name The name of the functions      * @param description A description of the purpose of the functions      * @param returnType The type that is returned by all arities of the function      * @param variableParamTypes An array, where each entry is an arry of parameter types for a specific arity of the function      *      * @return The function signature objects      */
specifier|public
specifier|static
name|FunctionSignature
index|[]
name|functionSignatures
parameter_list|(
specifier|final
name|QName
name|name
parameter_list|,
specifier|final
name|String
name|description
parameter_list|,
specifier|final
name|FunctionReturnSequenceType
name|returnType
parameter_list|,
specifier|final
name|FunctionParameterSequenceType
index|[]
index|[]
name|variableParamTypes
parameter_list|)
block|{
return|return
name|Arrays
operator|.
name|stream
argument_list|(
name|variableParamTypes
argument_list|)
operator|.
name|map
argument_list|(
name|paramTypes
lambda|->
name|functionSignature
argument_list|(
name|name
argument_list|,
name|description
argument_list|,
name|returnType
argument_list|,
name|paramTypes
argument_list|)
argument_list|)
operator|.
name|toArray
argument_list|(
name|FunctionSignature
index|[]
operator|::
operator|new
argument_list|)
return|;
block|}
comment|/**      * Wraps the parameter types for a specific function arity      *      * A DSL convenience method to be used to supply multiple {@link #arity(FunctionParameterSequenceType...)} results      * to {@link #functionSignatures(QName, String, FunctionReturnSequenceType, FunctionParameterSequenceType[][])}      *      * @param variableParamTypes A convenience Varargs for the function signature arities      *      * @return The arities of function parameters for a function signature      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
index|[]
index|[]
name|arities
parameter_list|(
specifier|final
name|FunctionParameterSequenceType
index|[]
modifier|...
name|variableParamTypes
parameter_list|)
block|{
return|return
name|variableParamTypes
return|;
block|}
comment|/**      * Specifies the specific parameter types for an arity of a function signature      *      * A DSL convenience method to be used inside {@link #arities(FunctionParameterSequenceType[]...)}      *      * @param paramTypes A convenience Varargs for the parameter types for a function arity      *      * @return The parameter types for a function arity      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
index|[]
name|arity
parameter_list|(
specifier|final
name|FunctionParameterSequenceType
modifier|...
name|paramTypes
parameter_list|)
block|{
return|return
name|paramTypes
return|;
block|}
comment|/**      * An optional  DSL convenience method for function parameter types      * that may make the function signature DSL more readable      *      * @param paramTypes The parameter types      *      * @return The parameter types      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
index|[]
name|params
parameter_list|(
specifier|final
name|FunctionParameterSequenceType
modifier|...
name|paramTypes
parameter_list|)
block|{
return|return
name|paramTypes
return|;
block|}
comment|/**      * Creates a Function Parameter which has a cardinality of {@link Cardinality#ZERO_OR_ONE}      *      * @param name The name of the parameter      * @param type The XDM type of the parameter, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the parameter      *      * @return The function parameter object      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
name|optParam
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|param
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Parameter which has a cardinality of {@link Cardinality#EXACTLY_ONE}      *      * @param name The name of the parameter      * @param type The XDM type of the parameter, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the parameter      *      * @return The function parameter object      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
name|param
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|param
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Parameter which has a cardinality of {@link Cardinality#ONE_OR_MORE}      *      * @param name The name of the parameter      * @param type The XDM type of the parameter, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the parameter      *      * @return The function parameter object      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
name|manyParam
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|param
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Parameter which has a cardinality of {@link Cardinality#ZERO_OR_ONE}      *      * @param name The name of the parameter      * @param type The XDM type of the parameter, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the parameter      *      * @return The function parameter object      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
name|optManyParam
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|param
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Parameter      *      * @param name The name of the parameter      * @param type The XDM type of the parameter, i.e. one of {@link org.exist.xquery.value.Type}      * @param cardinality The cardinality of the parameter, i.e. one of {@link Cardinality}      * @param description A description of the parameter      *      * @return The function parameter object      */
specifier|public
specifier|static
name|FunctionParameterSequenceType
name|param
parameter_list|(
specifier|final
name|String
name|name
parameter_list|,
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|int
name|cardinality
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
operator|new
name|FunctionParameterSequenceType
argument_list|(
name|name
argument_list|,
name|type
argument_list|,
name|cardinality
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ZERO_OR_ONE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsOpt
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ZERO_OR_ONE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the return value      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsOpt
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#EXACTLY_ONE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returns
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#EXACTLY_ONE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the return value      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returns
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ONE_OR_MORE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsMany
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ONE_OR_MORE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the return value      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsMany
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ONE_OR_MORE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ZERO_OR_MORE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsOptMany
parameter_list|(
specifier|final
name|int
name|type
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which has a cardinality of {@link Cardinality#ZERO_OR_MORE}      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param description A description of the return value      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsOptMany
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_MORE
argument_list|,
name|description
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type which describes no result.      *      * @return a Function Return Type which has a cardinality of {@link Cardinality#EMPTY} and {@link Type#EMPTY}      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returnsNothing
parameter_list|()
block|{
return|return
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param cardinality The cardinality of the return type, i.e. one of {@link Cardinality}      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returns
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|int
name|cardinality
parameter_list|)
block|{
return|return
name|returns
argument_list|(
name|type
argument_list|,
name|cardinality
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * Creates a Function Return Type      *      * @param type The XDM type of the return value, i.e. one of {@link org.exist.xquery.value.Type}      * @param cardinality The cardinality of the return type, i.e. one of {@link Cardinality}      * @param description A description of the parameter      *      * @return The function return type object      */
specifier|public
specifier|static
name|FunctionReturnSequenceType
name|returns
parameter_list|(
specifier|final
name|int
name|type
parameter_list|,
specifier|final
name|int
name|cardinality
parameter_list|,
specifier|final
name|String
name|description
parameter_list|)
block|{
return|return
operator|new
name|FunctionReturnSequenceType
argument_list|(
name|type
argument_list|,
name|cardinality
argument_list|,
name|description
argument_list|)
return|;
block|}
block|}
end_class

end_unit
