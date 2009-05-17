begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist team  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|java
operator|.
name|io
operator|.
name|StringReader
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
name|PathExpr
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
name|parser
operator|.
name|XQueryLexer
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
name|XQueryParser
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
name|XQueryTreeParser
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
name|EmptySequence
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

begin_import
import|import
name|antlr
operator|.
name|RecognitionException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|TokenStreamException
import|;
end_import

begin_import
import|import
name|antlr
operator|.
name|collections
operator|.
name|AST
import|;
end_import

begin_class
specifier|public
class|class
name|Compile
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
literal|"compile"
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
literal|"Dynamically evaluates the XPath/XQuery expression specified in $a within "
operator|+
literal|"the current instance of the query engine."
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
name|EXACTLY_ONE
argument_list|)
argument_list|)
decl_stmt|;
specifier|public
name|Compile
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
comment|// get the query expression
name|String
name|expr
init|=
name|args
index|[
literal|0
index|]
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|expr
operator|.
name|trim
argument_list|()
argument_list|)
condition|)
return|return
operator|new
name|EmptySequence
argument_list|()
return|;
name|context
operator|.
name|pushNamespaceContext
argument_list|()
expr_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"eval: "
operator|+
name|expr
argument_list|)
expr_stmt|;
comment|// TODO(pkaminsk2): why replicate XQuery.compile here?
name|XQueryLexer
name|lexer
init|=
operator|new
name|XQueryLexer
argument_list|(
name|context
argument_list|,
operator|new
name|StringReader
argument_list|(
name|expr
argument_list|)
argument_list|)
decl_stmt|;
name|XQueryParser
name|parser
init|=
operator|new
name|XQueryParser
argument_list|(
name|lexer
argument_list|)
decl_stmt|;
comment|// shares the context of the outer expression
name|XQueryTreeParser
name|astParser
init|=
operator|new
name|XQueryTreeParser
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
name|parser
operator|.
name|xpath
argument_list|()
expr_stmt|;
if|if
condition|(
name|parser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error found while executing expression: "
operator|+
name|parser
operator|.
name|getErrorMessage
argument_list|()
argument_list|)
throw|;
block|}
name|AST
name|ast
init|=
name|parser
operator|.
name|getAST
argument_list|()
decl_stmt|;
name|PathExpr
name|path
init|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
decl_stmt|;
name|astParser
operator|.
name|xpath
argument_list|(
name|ast
argument_list|,
name|path
argument_list|)
expr_stmt|;
if|if
condition|(
name|astParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"error found while executing expression: "
operator|+
name|astParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
name|astParser
operator|.
name|getLastException
argument_list|()
argument_list|)
throw|;
block|}
name|path
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|e
parameter_list|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|TokenStreamException
name|e
parameter_list|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|XPathException
name|e
parameter_list|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
return|return
operator|new
name|StringValue
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
return|;
block|}
finally|finally
block|{
name|context
operator|.
name|popNamespaceContext
argument_list|()
expr_stmt|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
block|}
end_class

end_unit

