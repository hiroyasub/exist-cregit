begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2007 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|apache
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
name|AccessContext
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
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|Source
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|source
operator|.
name|StringSource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|XQueryPool
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
name|HTTPUtils
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
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Reader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|NumberFormat
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|XQuery
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XQuery
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|DBBroker
name|broker
decl_stmt|;
comment|/**      *       */
specifier|public
name|XQuery
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
block|{
name|this
operator|.
name|broker
operator|=
name|broker
expr_stmt|;
block|}
specifier|public
name|XQueryContext
name|newContext
parameter_list|(
name|AccessContext
name|accessCtx
parameter_list|)
block|{
return|return
operator|new
name|XQueryContext
argument_list|(
name|broker
argument_list|,
name|accessCtx
argument_list|)
return|;
block|}
specifier|public
name|XQueryPool
name|getXQueryPool
parameter_list|()
block|{
return|return
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getXQueryPool
argument_list|()
return|;
block|}
specifier|public
name|CompiledXQuery
name|compile
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|expression
parameter_list|)
throws|throws
name|XPathException
block|{
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|expression
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//should not happen because expression is a String
throw|throw
operator|new
name|XPathException
argument_list|(
name|ioe
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ioe
argument_list|)
throw|;
block|}
block|}
specifier|public
name|CompiledXQuery
name|compile
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Source
name|source
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
return|return
name|compile
argument_list|(
name|context
argument_list|,
name|source
argument_list|,
literal|false
argument_list|)
return|;
block|}
specifier|public
name|CompiledXQuery
name|compile
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Source
name|source
parameter_list|,
name|boolean
name|xpointer
parameter_list|)
throws|throws
name|XPathException
throws|,
name|IOException
block|{
name|String
name|sourceClassName
init|=
name|source
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
name|context
operator|.
name|setSourceKey
argument_list|(
name|source
operator|.
name|getKey
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// Extract the source type from the classname by removing the package prefix and the "Source" suffix
name|context
operator|.
name|setSourceType
argument_list|(
name|sourceClassName
operator|.
name|substring
argument_list|(
literal|17
argument_list|,
name|sourceClassName
operator|.
name|length
argument_list|()
operator|-
literal|6
argument_list|)
argument_list|)
expr_stmt|;
name|XACMLSource
name|xsource
init|=
name|XACMLSource
operator|.
name|getInstance
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Reader
name|reader
init|=
name|source
operator|.
name|getReader
argument_list|()
decl_stmt|;
try|try
block|{
name|CompiledXQuery
name|compiled
init|=
name|compile
argument_list|(
name|context
argument_list|,
name|reader
argument_list|,
name|xpointer
argument_list|)
decl_stmt|;
name|compiled
operator|.
name|setSource
argument_list|(
name|xsource
argument_list|)
expr_stmt|;
return|return
name|compiled
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|CompiledXQuery
name|compile
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Reader
name|reader
parameter_list|,
name|boolean
name|xpointer
parameter_list|)
throws|throws
name|XPathException
block|{
comment|//TODO: move XQueryContext.getUserFromHttpSession() here, have to check if servlet.jar is in the classpath
comment|//before compiling/executing that code though to avoid a dependency on servlet.jar - reflection? - deliriumsky
comment|// how about - if(XQuery.class.getResource("servlet.jar") != null) do load my class with dependency and call method?
comment|/*<|wolf77|> I think last time I checked, I already had problems with the call to<|wolf77|> HTTPUtils.addLastModifiedHeader( result, context );<|wolf77|> in line 184 of XQuery.java, because it introduces another dependency on HTTP.     	 */
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|XQueryLexer
name|lexer
init|=
operator|new
name|XQueryLexer
argument_list|(
name|context
argument_list|,
name|reader
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
name|XQueryTreeParser
name|treeParser
init|=
operator|new
name|XQueryTreeParser
argument_list|(
name|context
argument_list|)
decl_stmt|;
try|try
block|{
if|if
condition|(
name|xpointer
condition|)
name|parser
operator|.
name|xpointer
argument_list|()
expr_stmt|;
else|else
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
name|StaticXQueryException
argument_list|(
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
if|if
condition|(
name|ast
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Unknown XQuery parser error: the parser returned an empty syntax tree."
argument_list|)
throw|;
comment|//            LOG.debug("Generated AST: " + ast.toStringTree());
name|PathExpr
name|expr
init|=
operator|new
name|PathExpr
argument_list|(
name|context
argument_list|)
decl_stmt|;
if|if
condition|(
name|xpointer
condition|)
name|treeParser
operator|.
name|xpointer
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
else|else
name|treeParser
operator|.
name|xpath
argument_list|(
name|ast
argument_list|,
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|treeParser
operator|.
name|foundErrors
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|StaticXQueryException
argument_list|(
name|treeParser
operator|.
name|getErrorMessage
argument_list|()
argument_list|,
name|treeParser
operator|.
name|getLastException
argument_list|()
argument_list|)
throw|;
block|}
name|context
operator|.
name|analyzeAndOptimizeIfModulesChanged
argument_list|(
name|expr
argument_list|)
expr_stmt|;
comment|// Log the query if it is not too large, but avoid
comment|// dumping huge queries to the log
if|if
condition|(
name|context
operator|.
name|getExpressionCount
argument_list|()
operator|<
literal|150
condition|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Query diagnostics:\n"
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Query diagnostics:\n"
operator|+
literal|"[skipped: more than 150 expressions]"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Compilation took "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
return|return
name|expr
return|;
block|}
catch|catch
parameter_list|(
name|RecognitionException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error compiling query: "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
expr_stmt|;
name|String
name|msg
init|=
name|e
operator|.
name|getMessage
argument_list|()
decl_stmt|;
if|if
condition|(
name|msg
operator|.
name|endsWith
argument_list|(
literal|", found 'null'"
argument_list|)
condition|)
name|msg
operator|=
name|msg
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|msg
operator|.
name|length
argument_list|()
operator|-
literal|", found 'null'"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|StaticXQueryException
argument_list|(
name|msg
argument_list|,
name|e
operator|.
name|getLine
argument_list|()
argument_list|,
name|e
operator|.
name|getColumn
argument_list|()
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|TokenStreamException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|debug
argument_list|(
literal|"Error compiling query: "
operator|+
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
name|StaticXQueryException
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|,
name|e
argument_list|)
throw|;
block|}
block|}
specifier|public
name|Sequence
name|execute
parameter_list|(
name|CompiledXQuery
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|execute
argument_list|(
name|expression
argument_list|,
name|contextSequence
argument_list|,
literal|null
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|execute
parameter_list|(
name|CompiledXQuery
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Properties
name|outputProperties
parameter_list|)
throws|throws
name|XPathException
block|{
name|XQueryContext
name|context
init|=
name|expression
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|Sequence
name|result
init|=
name|execute
argument_list|(
name|expression
argument_list|,
name|contextSequence
argument_list|,
name|outputProperties
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|//TODO : move this elsewhere !
name|HTTPUtils
operator|.
name|addLastModifiedHeader
argument_list|(
name|result
argument_list|,
name|context
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
specifier|public
name|Sequence
name|execute
parameter_list|(
name|CompiledXQuery
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|boolean
name|resetContext
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|execute
argument_list|(
name|expression
argument_list|,
name|contextSequence
argument_list|,
literal|null
argument_list|,
name|resetContext
argument_list|)
return|;
block|}
specifier|public
name|Sequence
name|execute
parameter_list|(
name|CompiledXQuery
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Properties
name|outputProperties
parameter_list|,
name|boolean
name|resetContext
parameter_list|)
throws|throws
name|XPathException
block|{
name|long
name|start
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|XQueryContext
name|context
init|=
name|expression
operator|.
name|getContext
argument_list|()
decl_stmt|;
comment|//check access to the query
name|XACMLSource
name|source
init|=
name|expression
operator|.
name|getSource
argument_list|()
decl_stmt|;
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
name|createQueryRequest
argument_list|(
name|context
argument_list|,
name|source
argument_list|)
decl_stmt|;
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
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Permission to execute query: "
operator|+
name|source
operator|.
name|createId
argument_list|()
operator|+
literal|" denied."
argument_list|,
name|pde
argument_list|)
throw|;
block|}
name|expression
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|resetContext
condition|)
block|{
name|context
operator|.
name|setBroker
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|context
operator|.
name|getWatchDog
argument_list|()
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
comment|//do any preparation before execution
name|context
operator|.
name|prepare
argument_list|()
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getProcessMonitor
argument_list|()
operator|.
name|queryStarted
argument_list|(
name|context
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|Sequence
name|result
init|=
name|expression
operator|.
name|eval
argument_list|(
name|contextSequence
argument_list|)
decl_stmt|;
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|NumberFormat
name|nf
init|=
name|NumberFormat
operator|.
name|getNumberInstance
argument_list|()
decl_stmt|;
name|LOG
operator|.
name|debug
argument_list|(
literal|"Execution took "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|start
argument_list|)
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
block|}
name|context
operator|.
name|checkOptions
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
comment|//must be done before context.reset!
return|return
name|result
return|;
block|}
finally|finally
block|{
name|expression
operator|.
name|reset
argument_list|()
expr_stmt|;
if|if
condition|(
name|resetContext
condition|)
name|context
operator|.
name|reset
argument_list|()
expr_stmt|;
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getProcessMonitor
argument_list|()
operator|.
name|queryCompleted
argument_list|(
name|context
operator|.
name|getWatchDog
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Sequence
name|execute
parameter_list|(
name|String
name|expression
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|AccessContext
name|accessCtx
parameter_list|)
throws|throws
name|XPathException
block|{
name|XQueryContext
name|context
init|=
operator|new
name|XQueryContext
argument_list|(
name|broker
argument_list|,
name|accessCtx
argument_list|)
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|compile
argument_list|(
name|context
argument_list|,
name|expression
argument_list|)
decl_stmt|;
return|return
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
return|;
block|}
block|}
end_class

end_unit

