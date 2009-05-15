begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|Stack
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
name|Sequence
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
name|BrokerPool
import|;
end_import

begin_comment
comment|/**  * XQuery profiling output. Profiling information is written to a  * logger. The profiler can be enabled/disabled and configured  * via an XQuery pragma or "declare option" expression. Example:  *   *<pre>declare option exist:profiling "enabled=yes verbosity=10 logger=profiler";</pre>  *   * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|Profiler
block|{
comment|/** value for Verbosity property: basic profiling : just elapsed time */
specifier|public
specifier|static
name|int
name|TIME
init|=
literal|1
decl_stmt|;
comment|/** value for Verbosity property: For optimizations */
specifier|public
specifier|static
name|int
name|OPTIMIZATIONS
init|=
literal|2
decl_stmt|;
comment|/** For computations that will trigger further optimizations */
specifier|public
specifier|static
name|int
name|OPTIMIZATION_FLAGS
init|=
literal|3
decl_stmt|;
comment|/** Indicates the dependencies of the expression */
specifier|public
specifier|static
name|int
name|DEPENDENCIES
init|=
literal|4
decl_stmt|;
comment|/** An abstract level for viewing the expression's context sequence/item */
specifier|public
specifier|static
name|int
name|START_SEQUENCES
init|=
literal|4
decl_stmt|;
comment|/** Just returns the number of items in the sequence */
specifier|public
specifier|static
name|int
name|ITEM_COUNT
init|=
literal|5
decl_stmt|;
comment|/** For a truncated string representation of the context sequence (TODO) */
specifier|public
specifier|static
name|int
name|SEQUENCE_PREVIEW
init|=
literal|6
decl_stmt|;
comment|/** For a full representation of the context sequence (TODO) */
specifier|public
specifier|static
name|int
name|SEQUENCE_DUMP
init|=
literal|8
decl_stmt|;
comment|/**      * The logger where all output goes.      */
specifier|private
name|Logger
name|log
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
literal|"xquery.profiling"
argument_list|)
decl_stmt|;
specifier|private
name|Stack
name|stack
init|=
operator|new
name|Stack
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|(
literal|64
argument_list|)
decl_stmt|;
specifier|private
name|boolean
name|enabled
init|=
literal|false
decl_stmt|;
specifier|private
name|int
name|verbosity
init|=
literal|0
decl_stmt|;
specifier|private
name|PerformanceStats
name|stats
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|Long
argument_list|>
name|functionStack
decl_stmt|;
specifier|private
name|long
name|queryStart
init|=
literal|0
decl_stmt|;
specifier|private
name|BrokerPool
name|pool
decl_stmt|;
specifier|public
name|Profiler
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
name|this
operator|.
name|stats
operator|=
operator|new
name|PerformanceStats
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|this
operator|.
name|functionStack
operator|=
operator|new
name|Stack
argument_list|<
name|Long
argument_list|>
argument_list|()
expr_stmt|;
block|}
comment|/**      * Configure the profiler from an XQuery pragma.      * Parameters are:      *       *<ul>      *<li><strong>enabled</strong>: yes|no.</li>      *<li><strong>logger</strong>: name of the logger to use.</li>      *<li><strong>verbosity</strong>: integer value&gt; 0. 1 does only output function calls.</li>      *</ul>      * @param pragma      */
specifier|public
specifier|final
name|void
name|configure
parameter_list|(
name|Option
name|pragma
parameter_list|)
block|{
name|String
name|options
index|[]
init|=
name|pragma
operator|.
name|tokenizeContents
argument_list|()
decl_stmt|;
name|String
name|params
index|[]
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
name|options
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|params
operator|=
name|Option
operator|.
name|parseKeyValuePair
argument_list|(
name|options
index|[
name|i
index|]
argument_list|)
expr_stmt|;
if|if
condition|(
name|params
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|params
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"trace"
argument_list|)
condition|)
block|{
name|stats
operator|.
name|setEnabled
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|params
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"logger"
argument_list|)
condition|)
block|{
name|log
operator|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|params
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|params
index|[
literal|0
index|]
operator|.
name|equals
argument_list|(
literal|"enabled"
argument_list|)
condition|)
block|{
name|enabled
operator|=
name|params
index|[
literal|1
index|]
operator|.
name|equals
argument_list|(
literal|"yes"
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
literal|"verbosity"
operator|.
name|equals
argument_list|(
name|params
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
try|try
block|{
name|verbosity
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|params
index|[
literal|1
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
name|log
operator|.
name|warn
argument_list|(
literal|"invalid value for verbosity: "
operator|+
literal|"should be an integer between 0 and "
operator|+
name|SEQUENCE_DUMP
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|verbosity
operator|==
literal|0
condition|)
name|enabled
operator|=
literal|false
expr_stmt|;
block|}
comment|/**      * Is profiling enabled?      *       * @return True if profiling is enabled      */
specifier|public
specifier|final
name|boolean
name|isEnabled
parameter_list|()
block|{
return|return
name|enabled
return|;
block|}
specifier|public
specifier|final
name|boolean
name|traceFunctions
parameter_list|()
block|{
return|return
name|stats
operator|.
name|isEnabled
argument_list|()
return|;
block|}
comment|/**      * @return the verbosity of the profiler.      */
specifier|public
specifier|final
name|int
name|verbosity
parameter_list|()
block|{
return|return
name|verbosity
return|;
block|}
specifier|public
specifier|final
name|void
name|traceQueryStart
parameter_list|()
block|{
name|queryStart
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|traceQueryEnd
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
name|stats
operator|.
name|recordQuery
argument_list|(
name|context
operator|.
name|getSourceKey
argument_list|()
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|queryStart
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|traceFunctionStart
parameter_list|(
name|FunctionCall
name|function
parameter_list|)
block|{
name|functionStack
operator|.
name|push
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|traceFunctionEnd
parameter_list|(
name|FunctionCall
name|function
parameter_list|)
block|{
if|if
condition|(
name|functionStack
operator|.
name|isEmpty
argument_list|()
condition|)
comment|// may happen if profiling was enabled in the middle of a query
return|return;
name|long
name|startTime
init|=
name|functionStack
operator|.
name|pop
argument_list|()
decl_stmt|;
name|stats
operator|.
name|recordFunctionCall
argument_list|(
name|function
operator|.
name|getSignature
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|,
name|function
operator|.
name|getContext
argument_list|()
operator|.
name|getSourceKey
argument_list|()
argument_list|,
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|startTime
operator|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|save
parameter_list|()
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
block|{
name|pool
operator|.
name|getPerformanceStats
argument_list|()
operator|.
name|merge
argument_list|(
name|stats
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called by an expression to indicate the start of an operation.      * The profiler registers the start time.      *       * @param expr the expression.      */
specifier|public
specifier|final
name|void
name|start
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|start
argument_list|(
name|expr
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
comment|/**      * Called by an expression to indicate the start of an operation.      * The profiler registers the start time.      *       * @param expr the expression.      * @param message if not null, contains an optional message to print in the log.      */
specifier|public
specifier|final
name|void
name|start
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"QUERY START"
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|ProfiledExpr
name|e
init|=
operator|new
name|ProfiledExpr
argument_list|(
name|expr
argument_list|)
decl_stmt|;
name|stack
operator|.
name|push
argument_list|(
name|e
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"START\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"MSG\t"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Called by an expression to indicate the end of an operation.      * The profiler computes the elapsed time.      *       * @param expr the expression.      * @param message required: a message to be printed to the log.      */
specifier|public
specifier|final
name|void
name|end
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|String
name|message
parameter_list|,
name|Sequence
name|result
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
try|try
block|{
name|ProfiledExpr
name|e
init|=
operator|(
name|ProfiledExpr
operator|)
name|stack
operator|.
name|pop
argument_list|()
decl_stmt|;
if|if
condition|(
name|e
operator|.
name|expr
operator|!=
name|expr
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Error: the object passed to end() does not correspond to the expression on top of the stack."
argument_list|)
expr_stmt|;
name|stack
operator|.
name|clear
argument_list|()
expr_stmt|;
return|return;
block|}
name|long
name|elapsed
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|e
operator|.
name|start
decl_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"MSG\t"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|verbosity
operator|>
name|START_SEQUENCES
condition|)
block|{
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"RESULT\t"
argument_list|)
expr_stmt|;
comment|/* if (verbosity>= SEQUENCE_DUMP)                      buf.append(result.toString());                                else if (verbosity>= SEQUENCE_PREVIEW)                     buf.append(sequencePreview(result));                 else*/
if|if
condition|(
name|verbosity
operator|>=
name|ITEM_COUNT
condition|)
name|buf
operator|.
name|append
argument_list|(
name|result
operator|.
name|getItemCount
argument_list|()
operator|+
literal|" item(s)"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|verbosity
operator|>=
name|TIME
condition|)
block|{
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"TIME\t"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|elapsed
operator|+
literal|" ms"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"END\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|e
operator|.
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"QUERY END"
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|RuntimeException
name|e
parameter_list|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"Profiler: could not pop from expression stack - "
operator|+
name|expr
operator|+
literal|" - "
operator|+
name|message
operator|+
literal|". Error : "
operator|+
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Print out a single profiling message for the given       * expression object.      *       *       * @param level       * @param title       * @param sequence       * @param expr       */
specifier|public
specifier|final
name|void
name|message
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|int
name|level
parameter_list|,
name|String
name|title
parameter_list|,
name|Sequence
name|sequence
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
if|if
condition|(
name|level
operator|>
name|verbosity
condition|)
return|return;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|title
argument_list|)
condition|)
name|buf
operator|.
name|append
argument_list|(
name|title
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|"MSG"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
comment|/* if (verbosity>= SEQUENCE_DUMP)              buf.append(sequence.toString());          else if (verbosity>= SEQUENCE_PREVIEW)             buf.append(sequencePreview(sequence));         else */
if|if
condition|(
name|verbosity
operator|>=
name|ITEM_COUNT
condition|)
name|buf
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|getItemCount
argument_list|()
operator|+
literal|" item(s)"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|final
name|void
name|message
parameter_list|(
name|Expression
name|expr
parameter_list|,
name|int
name|level
parameter_list|,
name|String
name|title
parameter_list|,
name|String
name|message
parameter_list|)
block|{
if|if
condition|(
operator|!
name|enabled
condition|)
return|return;
if|if
condition|(
name|level
operator|>
name|verbosity
condition|)
return|return;
name|buf
operator|.
name|setLength
argument_list|(
literal|0
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
name|stack
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|;
name|i
operator|++
control|)
name|buf
operator|.
name|append
argument_list|(
literal|'\t'
argument_list|)
expr_stmt|;
if|if
condition|(
name|title
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|title
argument_list|)
condition|)
name|buf
operator|.
name|append
argument_list|(
name|title
argument_list|)
expr_stmt|;
else|else
name|buf
operator|.
name|append
argument_list|(
literal|"MSG"
argument_list|)
expr_stmt|;
if|if
condition|(
name|message
operator|!=
literal|null
operator|&&
operator|!
literal|""
operator|.
name|equals
argument_list|(
name|message
argument_list|)
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|message
argument_list|)
expr_stmt|;
block|}
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
name|printPosition
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
name|buf
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
name|log
operator|.
name|debug
argument_list|(
literal|"QUERY RESET"
argument_list|)
expr_stmt|;
name|stack
operator|.
name|clear
argument_list|()
expr_stmt|;
if|if
condition|(
name|stats
operator|.
name|isEnabled
argument_list|()
operator|&&
name|stats
operator|.
name|hasData
argument_list|()
condition|)
block|{
name|save
argument_list|()
expr_stmt|;
name|stats
operator|.
name|reset
argument_list|()
expr_stmt|;
block|}
name|functionStack
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|printPosition
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
name|XQueryAST
name|ast
init|=
name|expr
operator|.
name|getASTNode
argument_list|()
decl_stmt|;
if|if
condition|(
name|ast
operator|!=
literal|null
condition|)
block|{
name|buf
operator|.
name|append
argument_list|(
literal|'['
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|ast
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|ast
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"]\t"
argument_list|)
expr_stmt|;
block|}
else|else
name|buf
operator|.
name|append
argument_list|(
literal|"\t"
argument_list|)
expr_stmt|;
block|}
comment|//TODO : find a way to preview "abstract" sequences
comment|// never used locally
specifier|private
name|String
name|sequencePreview
parameter_list|(
name|Sequence
name|sequence
parameter_list|)
block|{
name|StringBuffer
name|truncation
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
if|if
condition|(
name|sequence
operator|.
name|isEmpty
argument_list|()
condition|)
name|truncation
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|else if
condition|(
name|sequence
operator|.
name|hasOne
argument_list|()
condition|)
block|{
name|truncation
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|20
condition|)
name|truncation
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"... "
argument_list|)
expr_stmt|;
else|else
name|truncation
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|truncation
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|truncation
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
if|if
condition|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|length
argument_list|()
operator|>
literal|20
condition|)
name|truncation
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|20
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"... "
argument_list|)
expr_stmt|;
else|else
name|truncation
operator|.
name|append
argument_list|(
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|truncation
operator|.
name|append
argument_list|(
literal|", ... )"
argument_list|)
expr_stmt|;
block|}
return|return
name|truncation
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|final
specifier|static
class|class
name|ProfiledExpr
block|{
name|long
name|start
decl_stmt|;
name|Expression
name|expr
decl_stmt|;
specifier|private
name|ProfiledExpr
parameter_list|(
name|Expression
name|expression
parameter_list|)
block|{
name|this
operator|.
name|expr
operator|=
name|expression
expr_stmt|;
name|this
operator|.
name|start
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|setEnabled
parameter_list|(
name|boolean
name|enabled
parameter_list|)
block|{
name|this
operator|.
name|enabled
operator|=
name|enabled
expr_stmt|;
block|}
specifier|public
name|void
name|setVerbosity
parameter_list|(
name|int
name|verbosity
parameter_list|)
block|{
name|this
operator|.
name|verbosity
operator|=
name|verbosity
expr_stmt|;
block|}
block|}
end_class

end_unit

