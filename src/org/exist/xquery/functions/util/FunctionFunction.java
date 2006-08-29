begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|util
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
name|AnalyzeContextInfo
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
name|BasicFunction
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
name|Cardinality
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
name|ExternalModule
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
name|FunctionCall
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
name|FunctionSignature
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
name|Module
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
name|UserDefinedFunction
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
name|XPathException
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
name|XQueryContext
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
name|FunctionReference
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
name|NumericValue
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|FunctionFunction
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signature
init|=
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"function"
argument_list|,
name|UtilModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|UtilModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Creates a reference to an XQuery function which can later be called from util:call. "
operator|+
literal|"This allows for higher-order functions to be implemented in XQuery. A higher-order "
operator|+
literal|"function is a function that takes another function as argument. "
operator|+
literal|"The first argument represents the name of the function, which should be"
operator|+
literal|"a valid QName. The second argument is the arity of the function. If no"
operator|+
literal|"function can be found that matches the name and arity, an error is thrown. "
operator|+
literal|"Please note: due to the special character of util:function, the arguments to this function "
operator|+
literal|"have to be literals or need to be resolvable at compile time at least."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|INTEGER
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|FUNCTION_REFERENCE
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|private
name|FunctionCall
name|resolvedFunction
init|=
literal|null
decl_stmt|;
comment|/**      * @param context      * @param signature      */
specifier|public
name|FunctionFunction
parameter_list|(
name|XQueryContext
name|context
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
name|String
name|funcName
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|arity
init|=
operator|(
operator|(
name|NumericValue
operator|)
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
literal|null
argument_list|)
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|FunctionCall
name|funcCall
init|=
name|lookupFunction
argument_list|(
name|funcName
argument_list|,
name|arity
argument_list|)
decl_stmt|;
name|funcCall
operator|.
name|analyze
argument_list|(
name|contextInfo
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc)      * @see org.exist.xquery.BasicFunction#eval(org.exist.xquery.value.Sequence[], org.exist.xquery.value.Sequence)      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
name|funcName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|int
name|arity
init|=
operator|(
operator|(
name|NumericValue
operator|)
name|args
index|[
literal|1
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getInt
argument_list|()
decl_stmt|;
name|this
operator|.
name|resolvedFunction
operator|=
name|lookupFunction
argument_list|(
name|funcName
argument_list|,
name|arity
argument_list|)
expr_stmt|;
return|return
operator|new
name|FunctionReference
argument_list|(
name|resolvedFunction
argument_list|)
return|;
block|}
specifier|private
name|FunctionCall
name|lookupFunction
parameter_list|(
name|String
name|funcName
parameter_list|,
name|int
name|arity
parameter_list|)
throws|throws
name|XPathException
block|{
comment|// try to parse the qname
name|QName
name|qname
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
name|funcName
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
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
name|e
throw|;
block|}
comment|// check if the function is from a module
name|Module
name|module
init|=
name|context
operator|.
name|getModule
argument_list|(
name|qname
operator|.
name|getNamespaceURI
argument_list|()
argument_list|)
decl_stmt|;
name|UserDefinedFunction
name|func
decl_stmt|;
if|if
condition|(
name|module
operator|==
literal|null
condition|)
block|{
name|func
operator|=
name|context
operator|.
name|resolveFunction
argument_list|(
name|qname
argument_list|,
name|arity
argument_list|)
expr_stmt|;
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
name|getASTNode
argument_list|()
argument_list|,
name|Messages
operator|.
name|getMessage
argument_list|(
name|Error
operator|.
name|FUNC_NOT_FOUND
argument_list|,
name|qname
argument_list|,
name|Integer
operator|.
name|toString
argument_list|(
name|arity
argument_list|)
argument_list|)
argument_list|)
throw|;
block|}
else|else
block|{
if|if
condition|(
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
name|getASTNode
argument_list|()
argument_list|,
literal|"Cannot create a reference to an internal Java function"
argument_list|)
throw|;
name|func
operator|=
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
name|arity
argument_list|)
expr_stmt|;
block|}
name|FunctionCall
name|funcCall
init|=
operator|new
name|FunctionCall
argument_list|(
name|context
argument_list|,
name|func
argument_list|)
decl_stmt|;
name|funcCall
operator|.
name|setASTNode
argument_list|(
name|getASTNode
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|funcCall
return|;
block|}
specifier|public
name|void
name|resetState
parameter_list|()
block|{
name|super
operator|.
name|resetState
argument_list|()
expr_stmt|;
if|if
condition|(
name|resolvedFunction
operator|!=
literal|null
condition|)
name|resolvedFunction
operator|.
name|resetState
argument_list|()
expr_stmt|;
name|resolvedFunction
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

