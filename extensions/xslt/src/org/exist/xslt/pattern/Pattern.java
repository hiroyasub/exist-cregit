begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
operator|.
name|pattern
package|;
end_package

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
name|interpreter
operator|.
name|ContextAtExist
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
name|AnyNodeTest
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
name|Constants
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
name|LocationStep
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
name|StaticXQueryException
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
name|util
operator|.
name|ExpressionDumper
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

begin_comment
comment|/**  * [1]    Pattern    ::=    PathPattern<br>  *    | Pattern '|' PathPattern<br>  * [2]    PathPattern    ::=    RelativePathPattern<br>  *    | '/' RelativePathPattern?<br>  *    | '//' RelativePathPattern<br>  *    | IdKeyPattern (('/' | '//') RelativePathPattern)?<br>  * [3]    RelativePathPattern    ::=    PatternStep (('/' | '//') RelativePathPattern)?<br>  * [4]    PatternStep    ::=    PatternAxis? NodeTest XP PredicateListXP<br>  * [5]    PatternAxis    ::=    ('child' '::' | 'attribute' '::' | '@')<br>  * [6]    IdKeyPattern    ::=    'id' '(' IdValue ')'<br>  *    | 'key' '(' StringLiteralXP ',' KeyValue ')'<br>  * [7]    IdValue    ::=    StringLiteralXP | VarRef XP<br>  * [8]    KeyValue    ::=    Literal XP | VarRef XP<br>  *<br>  * The constructs NodeTest XP, PredicateList XP, VarRef XP, Literal XP, and StringLiteral XP are part of the XPath expression language, and are defined in [XPath 2.0].  *   * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Pattern
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
name|Pattern
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|String
name|ELEMENT
init|=
literal|"element()"
decl_stmt|;
specifier|static
specifier|final
name|String
name|ELEMENT_A
init|=
literal|"element(*)"
decl_stmt|;
specifier|static
specifier|final
name|String
name|ATTRIBUTE
init|=
literal|"attribute()"
decl_stmt|;
specifier|static
specifier|final
name|String
name|ATTRIBUTE_A
init|=
literal|"attribute(*)"
decl_stmt|;
specifier|public
specifier|static
name|void
name|parse
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|String
name|pattern
parameter_list|,
name|PathExpr
name|content
parameter_list|)
throws|throws
name|XPathException
block|{
name|boolean
name|xpointer
init|=
literal|false
decl_stmt|;
comment|//TODO: rewrite RootNode?
if|if
condition|(
name|pattern
operator|.
name|equals
argument_list|(
literal|"//"
argument_list|)
condition|)
block|{
name|content
operator|.
name|add
argument_list|(
operator|new
name|LocationStep
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|,
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
if|if
condition|(
name|pattern
operator|.
name|equals
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|content
operator|.
name|add
argument_list|(
operator|new
name|LocationStep
argument_list|(
name|context
argument_list|,
name|Constants
operator|.
name|SELF_AXIS
argument_list|,
operator|new
name|AnyNodeTest
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
name|Source
name|source
init|=
operator|new
name|StringSource
argument_list|(
name|pattern
argument_list|)
decl_stmt|;
name|Reader
name|reader
decl_stmt|;
try|try
block|{
name|reader
operator|=
name|source
operator|.
name|getReader
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e1
parameter_list|)
block|{
return|return;
comment|//TODO: report error???
block|}
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
name|expr
operator|.
name|analyze
argument_list|(
operator|new
name|AnalyzeContextInfo
argument_list|(
name|context
argument_list|)
argument_list|)
expr_stmt|;
comment|//            if (context.optimizationsEnabled()) {
comment|//                Optimizer optimizer = new Optimizer(context);
comment|//                expr.accept(optimizer);
comment|//                if (optimizer.hasOptimized()) {
comment|//                    context.reset(true);
comment|//                    expr.resetState(true);
comment|//                    expr.analyze(new AnalyzeContextInfo());
comment|//                }
comment|//            }
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
comment|//return
name|content
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
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
name|e
operator|.
name|getLine
argument_list|()
argument_list|,
name|e
operator|.
name|getColumn
argument_list|()
argument_list|,
name|msg
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
block|}
end_class

end_unit

