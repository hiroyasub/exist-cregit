begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|memtree
operator|.
name|MemTreeBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|Configuration
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"watchdog"
argument_list|)
specifier|public
class|class
name|XQueryWatchDog
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|XQueryWatchDog
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|CONFIGURATION_ELEMENT_NAME
init|=
literal|"watchdog"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_QUERY_TIMEOUT
init|=
literal|"db-connection.watchdog.query-timeout"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PROPERTY_OUTPUT_SIZE_LIMIT
init|=
literal|"db-connection.watchdog.output-size-limit"
decl_stmt|;
specifier|private
specifier|final
name|XQueryContext
name|context
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"query-timeout"
argument_list|)
specifier|private
name|long
name|timeout
init|=
name|Long
operator|.
name|MAX_VALUE
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"output-size-limit"
argument_list|)
specifier|private
name|int
name|maxNodesLimit
init|=
name|Integer
operator|.
name|MAX_VALUE
decl_stmt|;
specifier|private
name|long
name|startTime
decl_stmt|;
specifier|private
name|boolean
name|terminate
init|=
literal|false
decl_stmt|;
comment|/**      *       */
specifier|public
name|XQueryWatchDog
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|context
expr_stmt|;
name|configureDefaults
argument_list|()
expr_stmt|;
name|reset
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|configureDefaults
parameter_list|()
block|{
name|Configuration
name|conf
init|=
name|context
operator|.
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getConfiguration
argument_list|()
decl_stmt|;
name|Object
name|option
init|=
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_QUERY_TIMEOUT
argument_list|)
decl_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
name|timeout
operator|=
operator|(
operator|(
name|Long
operator|)
name|option
operator|)
operator|.
name|longValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|timeout
operator|<=
literal|0
condition|)
name|timeout
operator|=
name|Long
operator|.
name|MAX_VALUE
expr_stmt|;
name|option
operator|=
name|conf
operator|.
name|getProperty
argument_list|(
name|PROPERTY_OUTPUT_SIZE_LIMIT
argument_list|)
expr_stmt|;
if|if
condition|(
name|option
operator|!=
literal|null
condition|)
name|maxNodesLimit
operator|=
operator|(
operator|(
name|Integer
operator|)
name|option
operator|)
operator|.
name|intValue
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|setTimeoutFromOption
parameter_list|(
name|Option
name|option
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|contents
init|=
name|option
operator|.
name|tokenizeContents
argument_list|()
decl_stmt|;
if|if
condition|(
name|contents
operator|.
name|length
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Option 'timeout' should have exactly one parameter: the timeout value."
argument_list|)
throw|;
try|try
block|{
name|timeout
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|contents
index|[
literal|0
index|]
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error parsing timeout value in option "
operator|+
name|option
operator|.
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
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
literal|"timeout set from option: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|timeout
argument_list|)
operator|+
literal|" ms."
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setMaxNodes
parameter_list|(
name|int
name|maxNodes
parameter_list|)
block|{
name|maxNodesLimit
operator|=
name|maxNodes
expr_stmt|;
block|}
specifier|public
name|void
name|setMaxNodesFromOption
parameter_list|(
name|Option
name|option
parameter_list|)
throws|throws
name|XPathException
block|{
name|String
index|[]
name|contents
init|=
name|option
operator|.
name|tokenizeContents
argument_list|()
decl_stmt|;
if|if
condition|(
name|contents
operator|.
name|length
operator|!=
literal|1
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Option 'output-size-limit' should have exactly one parameter: the output-size-limit value."
argument_list|)
throw|;
try|try
block|{
name|setMaxNodes
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|contents
index|[
literal|0
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"Error parsing output-size-limit value in option "
operator|+
name|option
operator|.
name|getQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
throw|;
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
literal|"output-size-limit set from option: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxNodesLimit
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|proceed
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
block|{
if|if
condition|(
name|terminate
condition|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
name|expr
operator|=
name|context
operator|.
name|getRootExpression
argument_list|()
expr_stmt|;
name|cleanUp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TerminatedException
argument_list|(
name|expr
operator|.
name|getLine
argument_list|()
argument_list|,
name|expr
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"The query has been killed by the server."
argument_list|)
throw|;
block|}
specifier|final
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
decl_stmt|;
if|if
condition|(
name|elapsed
operator|>
name|timeout
condition|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
name|expr
operator|=
name|context
operator|.
name|getRootExpression
argument_list|()
expr_stmt|;
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
name|warn
argument_list|(
literal|"Query exceeded predefined timeout ("
operator|+
name|nf
operator|.
name|format
argument_list|(
name|elapsed
argument_list|)
operator|+
literal|" ms.): "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
name|cleanUp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TerminatedException
operator|.
name|TimeoutException
argument_list|(
name|expr
operator|.
name|getLine
argument_list|()
argument_list|,
name|expr
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"The query exceeded the predefined timeout and has been killed."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|proceed
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|MemTreeBuilder
name|builder
parameter_list|)
throws|throws
name|TerminatedException
block|{
name|proceed
argument_list|(
name|expr
argument_list|)
expr_stmt|;
if|if
condition|(
name|maxNodesLimit
operator|>
literal|0
operator|&&
name|builder
operator|.
name|getSize
argument_list|()
operator|>
name|maxNodesLimit
condition|)
block|{
if|if
condition|(
name|expr
operator|==
literal|null
condition|)
name|expr
operator|=
name|context
operator|.
name|getRootExpression
argument_list|()
expr_stmt|;
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
name|warn
argument_list|(
literal|"Query exceeded predefined output-size-limit ("
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxNodesLimit
argument_list|)
operator|+
literal|") for document fragments: "
operator|+
name|ExpressionDumper
operator|.
name|dump
argument_list|(
name|expr
argument_list|)
argument_list|)
expr_stmt|;
name|cleanUp
argument_list|()
expr_stmt|;
throw|throw
operator|new
name|TerminatedException
operator|.
name|SizeLimitException
argument_list|(
name|expr
operator|.
name|getLine
argument_list|()
argument_list|,
name|expr
operator|.
name|getColumn
argument_list|()
argument_list|,
literal|"The constructed document fragment exceeded the predefined output-size-limit (current: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|builder
operator|.
name|getSize
argument_list|()
argument_list|)
operator|+
literal|"; allowed: "
operator|+
name|nf
operator|.
name|format
argument_list|(
name|maxNodesLimit
argument_list|)
operator|+
literal|"). The query has been killed."
argument_list|)
throw|;
block|}
block|}
specifier|public
name|void
name|cleanUp
parameter_list|()
block|{
block|}
specifier|public
name|void
name|kill
parameter_list|(
name|long
name|waitTime
parameter_list|)
block|{
name|terminate
operator|=
literal|true
expr_stmt|;
block|}
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|context
return|;
block|}
specifier|public
name|long
name|getStartTime
parameter_list|()
block|{
return|return
name|startTime
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|startTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|terminate
operator|=
literal|false
expr_stmt|;
block|}
specifier|public
name|boolean
name|isTerminating
parameter_list|()
block|{
return|return
operator|(
name|terminate
operator|)
return|;
block|}
block|}
end_class

end_unit

