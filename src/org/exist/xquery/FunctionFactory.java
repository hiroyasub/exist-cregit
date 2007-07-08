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
name|Namespaces
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
name|functions
operator|.
name|ExtNear
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
name|functions
operator|.
name|ExtPhrase
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
name|Type
import|;
end_import

begin_class
specifier|public
class|class
name|FunctionFactory
block|{
specifier|public
specifier|static
specifier|final
name|String
name|ENABLE_JAVA_BINDING_ATTRIBUTE
init|=
literal|"enable-java-binding"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PROPERTY_ENABLE_JAVA_BINDING
init|=
literal|"xquery.enable-java-binding"
decl_stmt|;
comment|/** 	 * Create a function call.  	 *  	 * This method handles all calls to built-in or user-defined 	 * functions. It also deals with constructor functions and 	 * optimizes some function calls like starts-with, ends-with or 	 * contains.  	 */
specifier|public
specifier|static
name|Expression
name|createFunction
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|XQueryAST
name|ast
parameter_list|,
name|PathExpr
name|parent
parameter_list|,
name|List
name|params
parameter_list|)
throws|throws
name|XPathException
block|{
name|QName
name|qname
init|=
literal|null
decl_stmt|;
try|try
block|{
name|qname
operator|=
name|QName
operator|.
name|parse
argument_list|(
name|context
argument_list|,
name|ast
operator|.
name|getText
argument_list|()
argument_list|,
name|context
operator|.
name|getDefaultFunctionNamespace
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
name|e
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
name|String
name|local
init|=
name|qname
operator|.
name|getLocalName
argument_list|()
decl_stmt|;
name|String
name|uri
init|=
name|qname
operator|.
name|getNamespaceURI
argument_list|()
decl_stmt|;
name|Expression
name|step
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|Function
operator|.
name|BUILTIN_FUNCTION_NS
argument_list|)
condition|)
block|{
comment|// near(node-set, string)
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"near"
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function near() requires two arguments"
argument_list|)
throw|;
name|PathExpr
name|p1
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Second argument to near is empty"
argument_list|)
throw|;
name|Expression
name|e1
init|=
name|p1
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ExtNear
name|near
init|=
operator|new
name|ExtNear
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|near
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|near
operator|.
name|addTerm
argument_list|(
name|e1
argument_list|)
expr_stmt|;
name|near
operator|.
name|setPath
argument_list|(
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|>
literal|2
condition|)
block|{
name|p1
operator|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|2
argument_list|)
expr_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Max distance argument to near is empty"
argument_list|)
throw|;
block|}
name|near
operator|.
name|setMaxDistance
argument_list|(
name|p1
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|==
literal|4
condition|)
block|{
name|p1
operator|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|3
argument_list|)
expr_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Min distance argument to near is empty"
argument_list|)
throw|;
block|}
name|near
operator|.
name|setMinDistance
argument_list|(
name|p1
argument_list|)
expr_stmt|;
block|}
block|}
name|step
operator|=
name|near
expr_stmt|;
block|}
comment|// phrase(node-set, string)
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"phrase"
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function phrase() requires two arguments"
argument_list|)
throw|;
name|PathExpr
name|p1
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Second argument to phrase is empty"
argument_list|)
throw|;
name|Expression
name|e1
init|=
name|p1
operator|.
name|getExpression
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|ExtPhrase
name|phrase
init|=
operator|new
name|ExtPhrase
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|phrase
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|addTerm
argument_list|(
name|e1
argument_list|)
expr_stmt|;
name|phrase
operator|.
name|setPath
argument_list|(
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
argument_list|)
expr_stmt|;
name|step
operator|=
name|phrase
expr_stmt|;
block|}
comment|// starts-with(node-set, string)
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"starts-with"
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function starts-with() requires two or three arguments"
argument_list|)
throw|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function starts-with() requires two or three arguments"
argument_list|)
throw|;
name|PathExpr
name|p0
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|PathExpr
name|p1
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Second argument to starts-with is empty"
argument_list|)
throw|;
name|GeneralComparison
name|op
init|=
operator|new
name|GeneralComparison
argument_list|(
name|context
argument_list|,
name|p0
argument_list|,
name|p1
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|,
name|Constants
operator|.
name|TRUNC_RIGHT
argument_list|)
decl_stmt|;
name|op
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
comment|//TODO : not sure for parent -pb
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|parent
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATIONS
argument_list|,
literal|"OPTIMIZATION"
argument_list|,
literal|"Rewritten start-with as a general comparison with a right truncature"
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
name|op
operator|.
name|setCollation
argument_list|(
operator|(
name|Expression
operator|)
name|params
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|step
operator|=
name|op
expr_stmt|;
block|}
comment|// ends-with(node-set, string)
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"ends-with"
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017 : Function ends-with() requires two or three arguments"
argument_list|)
throw|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017 : Function ends-with() requires two or three arguments"
argument_list|)
throw|;
name|PathExpr
name|p0
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|PathExpr
name|p1
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Second argument to ends-with is empty"
argument_list|)
throw|;
name|GeneralComparison
name|op
init|=
operator|new
name|GeneralComparison
argument_list|(
name|context
argument_list|,
name|p0
argument_list|,
name|p1
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|,
name|Constants
operator|.
name|TRUNC_LEFT
argument_list|)
decl_stmt|;
comment|//TODO : not sure for parent -pb
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|parent
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATIONS
argument_list|,
literal|"OPTIMIZATION"
argument_list|,
literal|"Rewritten ends-with as a general comparison with a left truncature"
argument_list|)
expr_stmt|;
name|op
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
name|op
operator|.
name|setCollation
argument_list|(
operator|(
name|Expression
operator|)
name|params
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|step
operator|=
name|op
expr_stmt|;
block|}
comment|// contains(node-set, string)
if|if
condition|(
name|local
operator|.
name|equals
argument_list|(
literal|"contains"
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function contains() requires two or three arguments"
argument_list|)
throw|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|>
literal|3
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"XPST0017: Function contains() requires two or three arguments"
argument_list|)
throw|;
name|PathExpr
name|p0
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|PathExpr
name|p1
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|p1
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Second argument to contains is empty"
argument_list|)
throw|;
name|GeneralComparison
name|op
init|=
operator|new
name|GeneralComparison
argument_list|(
name|context
argument_list|,
name|p0
argument_list|,
name|p1
argument_list|,
name|Constants
operator|.
name|EQ
argument_list|,
name|Constants
operator|.
name|TRUNC_BOTH
argument_list|)
decl_stmt|;
comment|//TODO : not sure for parent -pb
name|context
operator|.
name|getProfiler
argument_list|()
operator|.
name|message
argument_list|(
name|parent
argument_list|,
name|Profiler
operator|.
name|OPTIMIZATIONS
argument_list|,
literal|"OPTIMIZATION"
argument_list|,
literal|"Rewritten contains as a general comparison with left and right truncatures"
argument_list|)
expr_stmt|;
name|op
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|==
literal|3
condition|)
name|op
operator|.
name|setCollation
argument_list|(
operator|(
name|Expression
operator|)
name|params
operator|.
name|get
argument_list|(
literal|2
argument_list|)
argument_list|)
expr_stmt|;
name|step
operator|=
name|op
expr_stmt|;
block|}
comment|// Check if the namespace belongs to one of the schema namespaces.
comment|// If yes, the function is a constructor function
block|}
if|else if
condition|(
name|uri
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|SCHEMA_NS
argument_list|)
operator|||
name|uri
operator|.
name|equals
argument_list|(
name|Namespaces
operator|.
name|XPATH_DATATYPES_NS
argument_list|)
condition|)
block|{
if|if
condition|(
name|params
operator|.
name|size
argument_list|()
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Wrong number of arguments for constructor function"
argument_list|)
throw|;
name|PathExpr
name|arg
init|=
operator|(
name|PathExpr
operator|)
name|params
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|int
name|code
init|=
name|Type
operator|.
name|getType
argument_list|(
name|qname
argument_list|)
decl_stmt|;
name|CastExpression
name|castExpr
init|=
operator|new
name|CastExpression
argument_list|(
name|context
argument_list|,
name|arg
argument_list|,
name|code
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
decl_stmt|;
name|castExpr
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|step
operator|=
name|castExpr
expr_stmt|;
comment|// Check if the namespace URI starts with "java:". If yes, treat the function call as a call to
comment|// an arbitrary Java function.
block|}
if|else if
condition|(
name|uri
operator|.
name|startsWith
argument_list|(
literal|"java:"
argument_list|)
condition|)
block|{
comment|//Only allow java binding if specified in config file<xquery enable-java-binding="yes">
name|String
name|javabinding
init|=
operator|(
name|String
operator|)
name|context
operator|.
name|broker
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|PROPERTY_ENABLE_JAVA_BINDING
argument_list|)
decl_stmt|;
if|if
condition|(
name|javabinding
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|javabinding
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
condition|)
block|{
name|JavaCall
name|call
init|=
operator|new
name|JavaCall
argument_list|(
name|context
argument_list|,
name|qname
argument_list|)
decl_stmt|;
name|call
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|call
operator|.
name|setArguments
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|step
operator|=
name|call
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Java binding is disabled in the current configuration (see conf.xml). Call to "
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
operator|+
literal|" denied."
argument_list|)
throw|;
block|}
block|}
else|else
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Java binding is disabled in the current configuration (see conf.xml). Call to "
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
operator|+
literal|" denied."
argument_list|)
throw|;
block|}
block|}
comment|// None of the above matched: function is either a builtin function or
comment|// a user-defined function
if|if
condition|(
name|step
operator|==
literal|null
condition|)
block|{
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|uri
argument_list|)
decl_stmt|;
if|if
condition|(
name|module
operator|!=
literal|null
condition|)
block|{
comment|// Function belongs to a module
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
comment|// for internal modules: create a new function instance from the class
name|FunctionDef
name|def
init|=
operator|(
operator|(
name|InternalModule
operator|)
name|module
operator|)
operator|.
name|getFunctionDef
argument_list|(
name|qname
argument_list|,
name|params
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|def
operator|==
literal|null
condition|)
block|{
name|List
name|funcs
init|=
operator|(
operator|(
name|InternalModule
operator|)
name|module
operator|)
operator|.
name|getFunctionsByName
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|funcs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Function "
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"() "
operator|+
literal|" is not defined in module namespace: "
operator|+
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
throw|;
else|else
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
literal|"Unexpectedly received "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|params
operator|.
name|size
argument_list|()
operator|+
literal|" parameter(s) in call to function "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"'"
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"()'. "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"Defined function signatures are:\r\n"
argument_list|)
expr_stmt|;
for|for
control|(
name|Iterator
name|i
init|=
name|funcs
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
name|FunctionSignature
name|sig
init|=
operator|(
name|FunctionSignature
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
name|sig
operator|.
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
throw|;
block|}
block|}
name|Function
name|func
init|=
name|Function
operator|.
name|createFunction
argument_list|(
name|context
argument_list|,
name|ast
argument_list|,
name|def
argument_list|)
decl_stmt|;
name|func
operator|.
name|setArguments
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|step
operator|=
operator|new
name|InternalFunctionCall
argument_list|(
name|func
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// function is from an imported XQuery module
name|UserDefinedFunction
name|func
init|=
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getFunction
argument_list|(
name|qname
argument_list|,
name|params
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|func
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|ast
argument_list|,
literal|"Function "
operator|+
name|qname
operator|.
name|getStringValue
argument_list|()
operator|+
literal|"() is not defined in namespace '"
operator|+
name|qname
operator|.
name|getNamespaceURI
argument_list|()
operator|+
literal|"'"
argument_list|)
throw|;
name|FunctionCall
name|call
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|call
operator|.
name|setArguments
argument_list|(
name|params
argument_list|)
expr_stmt|;
name|call
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|step
operator|=
name|call
expr_stmt|;
block|}
block|}
else|else
block|{
name|UserDefinedFunction
name|func
init|=
name|context
operator|.
name|resolveFunction
argument_list|(
name|qname
argument_list|,
name|params
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
name|FunctionCall
name|call
decl_stmt|;
if|if
condition|(
name|func
operator|!=
literal|null
condition|)
block|{
name|call
operator|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|func
argument_list|)
expr_stmt|;
name|call
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|call
operator|.
name|setArguments
argument_list|(
name|params
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// create a forward reference which will be resolved later
name|call
operator|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|qname
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|call
operator|.
name|setASTNode
argument_list|(
name|ast
argument_list|)
expr_stmt|;
name|context
operator|.
name|addForwardReference
argument_list|(
name|call
argument_list|)
expr_stmt|;
block|}
name|step
operator|=
name|call
expr_stmt|;
block|}
block|}
return|return
name|step
return|;
block|}
block|}
end_class

end_unit

