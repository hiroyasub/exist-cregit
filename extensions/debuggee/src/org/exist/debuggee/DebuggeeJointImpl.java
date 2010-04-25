begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

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
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
operator|.
name|AbstractCommandContinuation
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
operator|.
name|Command
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
operator|.
name|Init
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|debugger
operator|.
name|model
operator|.
name|Breakpoint
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
name|storage
operator|.
name|BrokerPool
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
name|CompiledXQuery
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
name|Expression
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
name|TerminatedException
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
name|Variable
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
name|XQuery
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggeeJointImpl
implements|implements
name|DebuggeeJoint
implements|,
name|Status
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
name|DebuggeeJoint
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|features
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|(
name|DebuggeeImpl
operator|.
name|SET_GET_FEATURES
argument_list|)
decl_stmt|;
specifier|private
name|Expression
name|firstExpression
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|stack
init|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|stackDepth
init|=
literal|0
decl_stmt|;
specifier|private
name|CommandContinuation
name|command
init|=
literal|null
decl_stmt|;
specifier|private
name|Stack
argument_list|<
name|CommandContinuation
argument_list|>
name|commands
init|=
operator|new
name|Stack
argument_list|<
name|CommandContinuation
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|int
name|breakpointNo
init|=
literal|0
decl_stmt|;
comment|//<fileName, Map<line, breakpoint>>
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|>
name|filesBreakpoints
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|//id, breakpoint
specifier|private
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|breakpoints
init|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|CompiledXQuery
name|compiledXQuery
decl_stmt|;
specifier|public
name|DebuggeeJointImpl
parameter_list|()
block|{
block|}
specifier|protected
name|void
name|setCompiledScript
parameter_list|(
name|CompiledXQuery
name|compiledXQuery
parameter_list|)
block|{
name|this
operator|.
name|compiledXQuery
operator|=
name|compiledXQuery
expr_stmt|;
name|stack
operator|.
name|add
argument_list|(
literal|null
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stackEnter
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|""
operator|+
name|expr
operator|.
name|getLine
argument_list|()
operator|+
literal|" expr = "
operator|+
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stack
operator|.
name|add
argument_list|(
name|expr
argument_list|)
expr_stmt|;
name|stackDepth
operator|++
expr_stmt|;
block|}
specifier|public
name|void
name|stackLeave
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|""
operator|+
name|expr
operator|.
name|getLine
argument_list|()
operator|+
literal|" expr = "
operator|+
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|stack
operator|.
name|remove
argument_list|(
name|stack
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
name|stackDepth
operator|--
expr_stmt|;
if|if
condition|(
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|STEP_OUT
argument_list|)
operator|&&
name|command
operator|.
name|getCallStackDepth
argument_list|()
operator|>
name|stackDepth
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|RUNNING
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.DebuggeeJoint#expressionStart(org.exist.xquery.Expression) 	 */
specifier|public
name|void
name|expressionStart
parameter_list|(
name|Expression
name|expr
parameter_list|)
throws|throws
name|TerminatedException
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|""
operator|+
name|expr
operator|.
name|getLine
argument_list|()
operator|+
literal|" expr = "
operator|+
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|compiledXQuery
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|firstExpression
operator|==
literal|null
condition|)
name|firstExpression
operator|=
name|expr
expr_stmt|;
name|stack
operator|.
name|set
argument_list|(
name|stackDepth
argument_list|,
name|expr
argument_list|)
expr_stmt|;
name|String
name|fileName
init|=
name|Command
operator|.
name|getFileuri
argument_list|(
name|expr
operator|.
name|getSource
argument_list|()
argument_list|)
decl_stmt|;
name|Integer
name|lineNo
init|=
name|expr
operator|.
name|getLine
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|fileBreakpoints
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
comment|//didn't receive any command, wait for any
if|if
condition|(
name|command
operator|==
literal|null
operator|||
comment|//the status is break, wait for changes
name|command
operator|.
name|isStatus
argument_list|(
name|BREAK
argument_list|)
condition|)
block|{
name|waitCommand
argument_list|()
expr_stmt|;
continue|continue;
block|}
comment|//wait for connection
if|if
condition|(
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|INIT
argument_list|)
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|STARTING
argument_list|)
condition|)
block|{
name|Init
name|init
init|=
operator|(
name|Init
operator|)
name|command
decl_stmt|;
name|init
operator|.
name|getSession
argument_list|()
operator|.
name|setAttribute
argument_list|(
literal|"joint"
argument_list|,
name|this
argument_list|)
expr_stmt|;
name|init
operator|.
name|setFileURI
argument_list|(
name|compiledXQuery
operator|.
name|getSource
argument_list|()
argument_list|)
expr_stmt|;
comment|//break on first line
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
block|}
comment|//disconnected
if|if
condition|(
name|compiledXQuery
operator|==
literal|null
condition|)
return|return;
comment|//stop command, terminate
if|if
condition|(
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|STOP
argument_list|)
operator|&&
operator|!
name|command
operator|.
name|isStatus
argument_list|(
name|STOPPED
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|STOPPED
argument_list|)
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
literal|"Debuggee STOP command."
argument_list|)
throw|;
block|}
comment|//step-into is done
if|if
condition|(
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|STEP_INTO
argument_list|)
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|RUNNING
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
comment|//step-over should stop on same call's stack depth
block|}
if|else if
condition|(
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|STEP_OVER
argument_list|)
operator|&&
name|command
operator|.
name|getCallStackDepth
argument_list|()
operator|==
name|stackDepth
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|RUNNING
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
block|}
comment|//checking breakpoints
synchronized|synchronized
init|(
name|breakpoints
init|)
block|{
if|if
condition|(
name|filesBreakpoints
operator|.
name|containsKey
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|fileBreakpoints
operator|=
name|filesBreakpoints
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileBreakpoints
operator|.
name|containsKey
argument_list|(
name|lineNo
argument_list|)
condition|)
block|{
name|Breakpoint
name|breakpoint
init|=
name|fileBreakpoints
operator|.
name|get
argument_list|(
name|lineNo
argument_list|)
decl_stmt|;
if|if
condition|(
name|breakpoint
operator|.
name|getState
argument_list|()
operator|&&
name|breakpoint
operator|.
name|getType
argument_list|()
operator|.
name|equals
argument_list|(
name|Breakpoint
operator|.
name|TYPE_LINE
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
comment|//waitCommand();
comment|//break;
block|}
block|}
block|}
block|}
comment|//RUS command with status RUNNING can be break only on breakpoints
if|if
condition|(
name|command
operator|.
name|getType
argument_list|()
operator|>=
name|CommandContinuation
operator|.
name|RUN
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|RUNNING
argument_list|)
condition|)
block|{
break|break;
comment|//any continuation command with status RUNNING
block|}
if|else if
condition|(
name|command
operator|.
name|getType
argument_list|()
operator|>=
name|CommandContinuation
operator|.
name|RUN
operator|&&
name|command
operator|.
name|isStatus
argument_list|(
name|STARTING
argument_list|)
condition|)
block|{
name|command
operator|.
name|setStatus
argument_list|(
name|RUNNING
argument_list|)
expr_stmt|;
break|break;
block|}
name|waitCommand
argument_list|()
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.DebuggeeJoint#expressionEnd(org.exist.xquery.Expression) 	 */
specifier|public
name|void
name|expressionEnd
parameter_list|(
name|Expression
name|expr
parameter_list|)
block|{
if|if
condition|(
name|LOG
operator|.
name|isDebugEnabled
argument_list|()
condition|)
name|LOG
operator|.
name|debug
argument_list|(
literal|"expr = "
operator|+
name|expr
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|firstExpression
operator|==
name|expr
condition|)
block|{
name|firstExpression
operator|=
literal|null
expr_stmt|;
name|command
operator|.
name|setStatus
argument_list|(
name|BREAK
argument_list|)
expr_stmt|;
name|command
operator|.
name|setStatus
argument_list|(
name|STOPPED
argument_list|)
expr_stmt|;
name|sessionClosed
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|//TODO: check this values
name|stackDepth
operator|=
literal|0
expr_stmt|;
name|stack
operator|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
expr_stmt|;
name|command
operator|=
literal|null
expr_stmt|;
name|commands
operator|=
operator|new
name|Stack
argument_list|<
name|CommandContinuation
argument_list|>
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|synchronized
name|void
name|waitCommand
parameter_list|()
block|{
if|if
condition|(
name|command
operator|.
name|isStatus
argument_list|(
name|BREAK
argument_list|)
operator|&&
name|commands
operator|.
name|size
argument_list|()
operator|!=
literal|0
condition|)
block|{
name|command
operator|=
name|commands
operator|.
name|pop
argument_list|()
expr_stmt|;
operator|(
operator|(
name|AbstractCommandContinuation
operator|)
name|command
operator|)
operator|.
name|setCallStackDepth
argument_list|(
name|stackDepth
argument_list|)
expr_stmt|;
return|return;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
try|try
block|{
name|wait
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
comment|//UNDERSTAND: what to do?
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.DebuggeeJoint#getContext() 	 */
specifier|public
name|XQueryContext
name|getContext
parameter_list|()
block|{
return|return
name|compiledXQuery
operator|.
name|getContext
argument_list|()
return|;
block|}
specifier|public
name|void
name|reset
parameter_list|()
block|{
name|firstExpression
operator|=
literal|null
expr_stmt|;
name|stack
operator|=
operator|new
name|ArrayList
argument_list|<
name|Expression
argument_list|>
argument_list|()
expr_stmt|;
name|stackDepth
operator|=
literal|0
expr_stmt|;
name|command
operator|=
literal|null
expr_stmt|;
name|breakpointNo
operator|=
literal|0
expr_stmt|;
name|filesBreakpoints
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|>
argument_list|()
expr_stmt|;
name|breakpoints
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|()
expr_stmt|;
block|}
specifier|public
specifier|synchronized
name|void
name|continuation
parameter_list|(
name|CommandContinuation
name|command
parameter_list|)
block|{
if|if
condition|(
name|firstExpression
operator|==
literal|null
operator|&&
operator|!
name|command
operator|.
name|is
argument_list|(
name|CommandContinuation
operator|.
name|INIT
argument_list|)
condition|)
name|command
operator|.
name|setStatus
argument_list|(
name|STOPPED
argument_list|)
expr_stmt|;
else|else
name|command
operator|.
name|setStatus
argument_list|(
name|STARTING
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|command
operator|==
literal|null
operator|||
name|this
operator|.
name|command
operator|.
name|isStatus
argument_list|(
name|STOPPED
argument_list|)
condition|)
block|{
operator|(
operator|(
name|AbstractCommandContinuation
operator|)
name|command
operator|)
operator|.
name|setCallStackDepth
argument_list|(
name|stackDepth
argument_list|)
expr_stmt|;
name|this
operator|.
name|command
operator|=
name|command
expr_stmt|;
block|}
else|else
block|{
name|commands
operator|.
name|add
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
name|notifyAll
argument_list|()
expr_stmt|;
block|}
specifier|public
name|boolean
name|featureSet
parameter_list|(
name|String
name|name
parameter_list|,
name|String
name|value
parameter_list|)
block|{
if|if
condition|(
name|features
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|features
operator|.
name|put
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|public
name|String
name|featureGet
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|DebuggeeImpl
operator|.
name|GET_FEATURES
operator|.
name|containsKey
argument_list|(
name|name
argument_list|)
condition|)
return|return
name|DebuggeeImpl
operator|.
name|GET_FEATURES
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
return|return
name|features
operator|.
name|get
argument_list|(
name|name
argument_list|)
return|;
block|}
specifier|public
specifier|synchronized
name|List
argument_list|<
name|Expression
argument_list|>
name|stackGet
parameter_list|()
block|{
comment|//wait, script didn't started
while|while
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
return|return
name|stack
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getVariables
parameter_list|()
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
argument_list|()
return|;
name|Expression
name|expr
init|=
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|expr
operator|.
name|getContext
argument_list|()
operator|.
name|getVariables
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getLocalVariables
parameter_list|()
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
argument_list|()
return|;
name|Expression
name|expr
init|=
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|expr
operator|.
name|getContext
argument_list|()
operator|.
name|getLocalVariables
argument_list|()
return|;
block|}
specifier|public
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|getGlobalVariables
parameter_list|()
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
operator|new
name|HashMap
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
argument_list|()
return|;
name|Expression
name|expr
init|=
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
name|expr
operator|.
name|getContext
argument_list|()
operator|.
name|getGlobalVariables
argument_list|()
return|;
block|}
specifier|public
name|Variable
name|getVariable
parameter_list|(
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|stack
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
literal|null
return|;
name|Expression
name|expr
init|=
name|stack
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
try|try
block|{
return|return
name|expr
operator|.
name|getContext
argument_list|()
operator|.
name|resolveVariable
argument_list|(
name|name
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
literal|null
return|;
block|}
block|}
specifier|public
name|int
name|setBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
block|{
synchronized|synchronized
init|(
name|breakpoints
init|)
block|{
name|breakpointNo
operator|++
expr_stmt|;
name|breakpoint
operator|.
name|setId
argument_list|(
name|breakpointNo
argument_list|)
expr_stmt|;
name|breakpoints
operator|.
name|put
argument_list|(
name|breakpointNo
argument_list|,
name|breakpoint
argument_list|)
expr_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|fileBreakpoints
decl_stmt|;
name|String
name|fileName
init|=
name|breakpoint
operator|.
name|getFilename
argument_list|()
decl_stmt|;
if|if
condition|(
name|filesBreakpoints
operator|.
name|containsKey
argument_list|(
name|fileName
argument_list|)
condition|)
name|fileBreakpoints
operator|=
name|filesBreakpoints
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
else|else
block|{
name|fileBreakpoints
operator|=
operator|new
name|HashMap
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
argument_list|()
expr_stmt|;
name|filesBreakpoints
operator|.
name|put
argument_list|(
name|fileName
argument_list|,
name|fileBreakpoints
argument_list|)
expr_stmt|;
block|}
name|fileBreakpoints
operator|.
name|put
argument_list|(
name|breakpoint
operator|.
name|getLineno
argument_list|()
argument_list|,
name|breakpoint
argument_list|)
expr_stmt|;
block|}
return|return
literal|1
return|;
comment|//TODO: create constant
block|}
specifier|public
name|Breakpoint
name|getBreakpoint
parameter_list|(
name|int
name|breakpointID
parameter_list|)
block|{
return|return
name|breakpoints
operator|.
name|get
argument_list|(
name|breakpointID
argument_list|)
return|;
block|}
specifier|public
name|Breakpoint
name|removeBreakpoint
parameter_list|(
name|int
name|breakpointID
parameter_list|)
block|{
name|Breakpoint
name|breakpoint
init|=
name|breakpoints
operator|.
name|get
argument_list|(
name|breakpointID
argument_list|)
decl_stmt|;
if|if
condition|(
name|breakpoint
operator|==
literal|null
condition|)
return|return
name|breakpoint
return|;
name|String
name|fileName
init|=
name|breakpoint
operator|.
name|getFilename
argument_list|()
decl_stmt|;
name|Integer
name|lineNo
init|=
name|breakpoint
operator|.
name|getLineno
argument_list|()
decl_stmt|;
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|fileBreakpoints
init|=
literal|null
decl_stmt|;
synchronized|synchronized
init|(
name|breakpoints
init|)
block|{
if|if
condition|(
name|filesBreakpoints
operator|.
name|containsKey
argument_list|(
name|fileName
argument_list|)
condition|)
block|{
name|fileBreakpoints
operator|=
name|filesBreakpoints
operator|.
name|get
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileBreakpoints
operator|.
name|containsKey
argument_list|(
name|lineNo
argument_list|)
condition|)
block|{
name|fileBreakpoints
operator|.
name|remove
argument_list|(
name|lineNo
argument_list|)
expr_stmt|;
if|if
condition|(
name|fileBreakpoints
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|filesBreakpoints
operator|.
name|remove
argument_list|(
name|fileName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|breakpoints
operator|.
name|remove
argument_list|(
name|breakpointID
argument_list|)
expr_stmt|;
block|}
return|return
name|breakpoint
return|;
block|}
specifier|public
name|Map
argument_list|<
name|Integer
argument_list|,
name|Breakpoint
argument_list|>
name|getBreakpoints
parameter_list|()
block|{
return|return
name|breakpoints
return|;
block|}
specifier|public
specifier|synchronized
name|void
name|sessionClosed
parameter_list|(
name|boolean
name|disconnect
parameter_list|)
block|{
comment|//disconnected already
if|if
condition|(
name|compiledXQuery
operator|==
literal|null
condition|)
return|return;
comment|//disconnect debuggee& compiled source
name|XQueryContext
name|context
init|=
name|compiledXQuery
operator|.
name|getContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setDebuggeeJoint
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|compiledXQuery
operator|=
literal|null
expr_stmt|;
if|if
condition|(
name|command
operator|!=
literal|null
operator|&&
name|disconnect
condition|)
name|command
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
specifier|public
name|CommandContinuation
name|getCurrentCommand
parameter_list|()
block|{
return|return
name|command
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|evalution
parameter_list|(
name|String
name|script
parameter_list|)
throws|throws
name|Exception
block|{
name|XQueryContext
name|context
init|=
name|compiledXQuery
operator|.
name|getContext
argument_list|()
operator|.
name|copyContext
argument_list|()
decl_stmt|;
name|context
operator|.
name|setDebuggeeJoint
argument_list|(
literal|null
argument_list|)
expr_stmt|;
name|context
operator|.
name|undeclareGlobalVariable
argument_list|(
name|Debuggee
operator|.
name|SESSION
argument_list|)
expr_stmt|;
name|XQuery
name|service
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|get
argument_list|(
literal|null
argument_list|)
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|context
argument_list|,
name|script
argument_list|)
decl_stmt|;
name|Sequence
name|resultSequence
init|=
name|service
operator|.
name|execute
argument_list|(
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
return|return
name|resultSequence
operator|.
name|getStringValue
argument_list|()
return|;
block|}
block|}
end_class

end_unit

