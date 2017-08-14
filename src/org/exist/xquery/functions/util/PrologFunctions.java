begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-09 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|LogManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|logging
operator|.
name|log4j
operator|.
name|Logger
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
name|*
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
name|StringValue
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
name|PrologFunctions
extends|extends
name|BasicFunction
block|{
specifier|protected
specifier|static
specifier|final
name|Logger
name|logger
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|PrologFunctions
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"import-module"
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
literal|"Dynamically imports an XQuery module into the current context. The parameters have the same "
operator|+
literal|"meaning as in an 'import module ...' expression in the query prolog."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"module-uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI of the module"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"prefix"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The prefix to be assigned to the namespace"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"location"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The location of the module"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"declare-namespace"
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
literal|"Dynamically declares a namespace/prefix mapping for the current context."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"prefix"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The prefix to be assigned to the namespace"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"namespace-uri"
argument_list|,
name|Type
operator|.
name|ANY_URI
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The namespace URI"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"declare-option"
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
literal|"Dynamically declares a serialization option as with 'declare option'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The serialization option name"
argument_list|)
block|,
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"option"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The serialization option value"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|ITEM
argument_list|,
name|Cardinality
operator|.
name|EMPTY
argument_list|)
argument_list|)
block|,
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"get-option"
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
literal|"Gets the value of a serialization option as set with 'declare option'."
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|FunctionParameterSequenceType
argument_list|(
literal|"name"
argument_list|,
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|,
literal|"The serialization option name"
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|ZERO_OR_ONE
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|PrologFunctions
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
if|if
condition|(
name|isCalledAs
argument_list|(
literal|"declare-namespace"
argument_list|)
condition|)
block|{
name|declareNamespace
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"declare-option"
argument_list|)
condition|)
block|{
name|declareOption
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|isCalledAs
argument_list|(
literal|"get-option"
argument_list|)
condition|)
block|{
return|return
name|getOption
argument_list|(
name|args
argument_list|)
return|;
block|}
else|else
block|{
name|importModule
argument_list|(
name|args
argument_list|)
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|void
name|declareNamespace
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|saveState
argument_list|()
expr_stmt|;
name|String
name|prefix
decl_stmt|;
if|if
condition|(
name|args
index|[
literal|0
index|]
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|prefix
operator|=
literal|""
expr_stmt|;
block|}
else|else
block|{
name|prefix
operator|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
expr_stmt|;
block|}
specifier|final
name|String
name|uri
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|context
operator|.
name|declareNamespace
argument_list|(
name|prefix
argument_list|,
name|uri
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|importModule
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
name|context
operator|.
name|saveState
argument_list|()
expr_stmt|;
specifier|final
name|String
name|uri
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|prefix
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|location
init|=
name|args
index|[
literal|2
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Module
name|module
init|=
name|context
operator|.
name|importModule
argument_list|(
name|uri
argument_list|,
name|prefix
argument_list|,
name|location
argument_list|)
decl_stmt|;
name|context
operator|.
name|getRootContext
argument_list|()
operator|.
name|resolveForwardReferences
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|module
operator|.
name|isInternalModule
argument_list|()
condition|)
block|{
operator|(
operator|(
name|ExternalModule
operator|)
name|module
operator|)
operator|.
name|getRootExpression
argument_list|()
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//		context.getRootContext().analyzeAndOptimizeIfModulesChanged((PathExpr) context.getRootExpression());
block|}
specifier|private
name|void
name|declareOption
parameter_list|(
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|qname
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|String
name|options
init|=
name|args
index|[
literal|1
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|context
operator|.
name|addDynamicOption
argument_list|(
name|qname
argument_list|,
name|options
argument_list|)
expr_stmt|;
block|}
specifier|private
name|Sequence
name|getOption
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|String
name|qnameString
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
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
name|qnameString
argument_list|,
name|context
operator|.
name|getDefaultFunctionNamespace
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Option
name|option
init|=
name|context
operator|.
name|getOption
argument_list|(
name|qname
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|option
operator|.
name|getContents
argument_list|()
argument_list|)
return|;
block|}
else|else
block|{
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
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
name|this
argument_list|,
name|ErrorCodes
operator|.
name|XPST0081
argument_list|,
literal|"No namespace defined for prefix "
operator|+
name|qnameString
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

