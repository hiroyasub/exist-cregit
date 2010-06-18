begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debugger
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
name|net
operator|.
name|InetSocketAddress
import|;
end_import

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
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSession
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|filter
operator|.
name|codec
operator|.
name|ProtocolCodecFilter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|transport
operator|.
name|socket
operator|.
name|nio
operator|.
name|NioSocketAcceptor
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
name|*
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
name|Debugger
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
name|DebuggingSource
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
name|dbgp
operator|.
name|CodecFactory
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
name|dbgp
operator|.
name|ProtocolHandler
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
name|dbgp
operator|.
name|ResponseImpl
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
name|debugger
operator|.
name|model
operator|.
name|BreakpointImpl
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
name|Location
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
name|LocationImpl
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
name|Variable
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
name|VariableImpl
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
name|Base64Decoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Text
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *   */
end_comment

begin_class
specifier|public
class|class
name|DebuggerImpl
implements|implements
name|Debugger
implements|,
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|Status
block|{
specifier|protected
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DebuggerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|DebuggerImpl
name|instance
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|Debugger
name|getDebugger
parameter_list|()
throws|throws
name|IOException
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
name|instance
operator|=
operator|new
name|DebuggerImpl
argument_list|()
expr_stmt|;
return|return
name|instance
return|;
block|}
specifier|public
specifier|static
name|void
name|shutdownDebugger
parameter_list|()
block|{
if|if
condition|(
name|instance
operator|==
literal|null
condition|)
return|return;
name|instance
operator|.
name|acceptor
operator|.
name|unbind
argument_list|()
expr_stmt|;
block|}
specifier|private
name|NioSocketAcceptor
name|acceptor
decl_stmt|;
specifier|private
name|int
name|eventPort
init|=
literal|9000
decl_stmt|;
specifier|private
name|IoSession
name|session
decl_stmt|;
comment|// uri -> source
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|DebuggingSource
argument_list|>
name|sources
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|DebuggingSource
argument_list|>
argument_list|()
decl_stmt|;
name|int
name|currentTransactionId
init|=
literal|1
decl_stmt|;
comment|//	private String lastStatus = FIRST_RUN;
specifier|protected
name|int
name|responseCode
init|=
literal|0
decl_stmt|;
specifier|private
name|DebuggerImpl
parameter_list|()
throws|throws
name|IOException
block|{
name|acceptor
operator|=
operator|new
name|NioSocketAcceptor
argument_list|()
expr_stmt|;
name|acceptor
operator|.
name|setCloseOnDeactivation
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|acceptor
operator|.
name|getFilterChain
argument_list|()
operator|.
name|addLast
argument_list|(
literal|"protocol"
argument_list|,
operator|new
name|ProtocolCodecFilter
argument_list|(
operator|new
name|CodecFactory
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|acceptor
operator|.
name|setHandler
argument_list|(
operator|new
name|ProtocolHandler
argument_list|(
name|this
argument_list|)
argument_list|)
expr_stmt|;
name|acceptor
operator|.
name|bind
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|eventPort
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|int
name|getNextTransaction
parameter_list|()
block|{
return|return
name|currentTransactionId
operator|++
return|;
block|}
specifier|protected
name|void
name|setSession
parameter_list|(
name|IoSession
name|session
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
block|}
specifier|public
name|DebuggingSource
name|init
parameter_list|(
name|String
name|url
parameter_list|)
throws|throws
name|IOException
throws|,
name|ExceptionTimeout
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Debugger is listening at port "
operator|+
name|eventPort
argument_list|)
expr_stmt|;
if|if
condition|(
name|this
operator|.
name|session
operator|!=
literal|null
condition|)
operator|new
name|IOException
argument_list|(
literal|"Another debugging session is active."
argument_list|)
expr_stmt|;
name|responseCode
operator|=
literal|0
expr_stmt|;
name|Thread
name|session
init|=
operator|new
name|Thread
argument_list|(
operator|new
name|HttpSession
argument_list|(
name|this
argument_list|,
name|url
argument_list|)
argument_list|)
decl_stmt|;
name|session
operator|.
name|start
argument_list|()
expr_stmt|;
comment|// 30s timeout
name|ResponseImpl
name|response
init|=
operator|(
name|ResponseImpl
operator|)
name|getResponse
argument_list|(
literal|"init"
argument_list|,
literal|30
operator|*
literal|1000
argument_list|)
decl_stmt|;
name|this
operator|.
name|session
operator|=
name|response
operator|.
name|getSession
argument_list|()
expr_stmt|;
comment|// TODO: fileuri as constant???
return|return
name|getSource
argument_list|(
name|response
operator|.
name|getAttribute
argument_list|(
literal|"fileuri"
argument_list|)
argument_list|)
return|;
block|}
comment|/* 	 * (non-Javadoc) 	 *  	 * @see org.exist.debugger.Debugger#source(java.lang.String) 	 */
specifier|public
name|DebuggingSource
name|getSource
parameter_list|(
name|String
name|fileURI
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|fileURI
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|sources
operator|.
name|containsKey
argument_list|(
name|fileURI
argument_list|)
condition|)
return|return
name|sources
operator|.
name|get
argument_list|(
name|fileURI
argument_list|)
return|;
name|Source
name|command
init|=
operator|new
name|Source
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setFileURI
argument_list|(
name|fileURI
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"1"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getAttribute
argument_list|(
literal|"success"
argument_list|)
argument_list|)
condition|)
block|{
name|DebuggingSourceImpl
name|source
init|=
operator|new
name|DebuggingSourceImpl
argument_list|(
name|this
argument_list|,
name|fileURI
argument_list|)
decl_stmt|;
name|Base64Decoder
name|dec
init|=
operator|new
name|Base64Decoder
argument_list|()
decl_stmt|;
name|dec
operator|.
name|translate
argument_list|(
name|response
operator|.
name|getText
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|c
init|=
name|dec
operator|.
name|getByteArray
argument_list|()
decl_stmt|;
name|String
name|s
init|=
operator|new
name|String
argument_list|(
name|c
argument_list|)
decl_stmt|;
name|source
operator|.
name|setText
argument_list|(
name|s
argument_list|)
expr_stmt|;
name|sources
operator|.
name|put
argument_list|(
name|fileURI
argument_list|,
name|source
argument_list|)
expr_stmt|;
return|return
name|source
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|List
argument_list|<
name|Variable
argument_list|>
name|getVariables
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getVariables
argument_list|(
literal|null
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Variable
argument_list|>
name|getLocalVariables
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getVariables
argument_list|(
name|ContextNames
operator|.
name|LOCAL
argument_list|)
return|;
block|}
specifier|public
name|List
argument_list|<
name|Variable
argument_list|>
name|getGlobalVariables
parameter_list|()
throws|throws
name|IOException
block|{
return|return
name|getVariables
argument_list|(
name|ContextNames
operator|.
name|GLOBAL
argument_list|)
return|;
block|}
specifier|private
name|List
argument_list|<
name|Variable
argument_list|>
name|getVariables
parameter_list|(
name|String
name|contextID
parameter_list|)
throws|throws
name|IOException
block|{
name|ContextGet
name|command
init|=
operator|new
name|ContextGet
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextID
operator|!=
literal|null
condition|)
name|command
operator|.
name|setContextID
argument_list|(
name|contextID
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
comment|//XXX: handle errors
name|List
argument_list|<
name|Variable
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<
name|Variable
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|response
operator|.
name|getElemetsByName
argument_list|(
literal|"property"
argument_list|)
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|variables
operator|.
name|add
argument_list|(
operator|new
name|VariableImpl
argument_list|(
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|variables
return|;
block|}
specifier|public
name|List
argument_list|<
name|Location
argument_list|>
name|getStackFrames
parameter_list|()
throws|throws
name|IOException
block|{
name|StackGet
name|command
init|=
operator|new
name|StackGet
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
comment|//XXX: handle errors
name|List
argument_list|<
name|Location
argument_list|>
name|variables
init|=
operator|new
name|ArrayList
argument_list|<
name|Location
argument_list|>
argument_list|()
decl_stmt|;
name|NodeList
name|children
init|=
name|response
operator|.
name|getElemetsByName
argument_list|(
literal|"stack"
argument_list|)
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|variables
operator|.
name|add
argument_list|(
operator|new
name|LocationImpl
argument_list|(
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|variables
return|;
block|}
specifier|public
name|void
name|sessionClosed
parameter_list|()
block|{
if|if
condition|(
operator|!
name|session
operator|.
name|isClosing
argument_list|()
condition|)
name|session
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|session
operator|=
literal|null
expr_stmt|;
block|}
comment|// weak map???
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Response
argument_list|>
name|responses
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Response
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
specifier|synchronized
name|void
name|addResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|currentCommand
operator|!=
literal|null
operator|&&
name|currentCommand
operator|.
name|getTransactionId
argument_list|()
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getTransactionID
argument_list|()
argument_list|)
condition|)
name|currentCommand
operator|.
name|putResponse
argument_list|(
name|response
argument_list|)
expr_stmt|;
comment|//		if (response.hasAttribute("status"))
comment|//			lastStatus = response.getAttribute("status");
comment|//it should be commands map, this implementation is dangerous
comment|//rethink!!!
name|responses
operator|.
name|put
argument_list|(
name|response
operator|.
name|getTransactionID
argument_list|()
argument_list|,
name|response
argument_list|)
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Response
name|getResponse
parameter_list|(
name|String
name|transactionID
parameter_list|)
throws|throws
name|IOException
block|{
try|try
block|{
return|return
name|getResponse
argument_list|(
name|transactionID
argument_list|,
literal|0
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|ExceptionTimeout
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
specifier|synchronized
name|Response
name|getResponse
parameter_list|(
name|String
name|transactionID
parameter_list|,
name|int
name|timeout
parameter_list|)
throws|throws
name|ExceptionTimeout
throws|,
name|IOException
block|{
name|long
name|sTime
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|responses
operator|.
name|containsKey
argument_list|(
name|transactionID
argument_list|)
condition|)
block|{
try|try
block|{
if|if
condition|(
name|responseCode
operator|!=
literal|0
condition|)
block|{
if|if
condition|(
name|responses
operator|.
name|containsKey
argument_list|(
name|transactionID
argument_list|)
condition|)
break|break;
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Got responce code "
operator|+
name|responseCode
operator|+
literal|" on debugging request"
argument_list|)
throw|;
block|}
if|else if
condition|(
name|timeout
operator|==
literal|0
condition|)
name|wait
argument_list|(
literal|10
argument_list|)
expr_stmt|;
comment|//slow down next check
else|else
name|wait
argument_list|(
name|timeout
argument_list|)
expr_stmt|;
if|if
condition|(
name|timeout
operator|!=
literal|0
operator|&&
operator|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|sTime
operator|)
operator|>
name|timeout
condition|)
throw|throw
operator|new
name|ExceptionTimeout
argument_list|()
throw|;
block|}
catch|catch
parameter_list|(
name|InterruptedException
name|e
parameter_list|)
block|{
block|}
block|}
if|if
condition|(
name|responses
operator|.
name|containsKey
argument_list|(
name|transactionID
argument_list|)
condition|)
block|{
name|Response
name|response
init|=
name|responses
operator|.
name|get
argument_list|(
name|transactionID
argument_list|)
decl_stmt|;
name|responses
operator|.
name|remove
argument_list|(
name|transactionID
argument_list|)
expr_stmt|;
return|return
name|response
return|;
block|}
comment|//UNDERSTAND: throw error???
return|return
literal|null
return|;
block|}
specifier|private
name|AbstractCommandContinuation
name|currentCommand
init|=
literal|null
decl_stmt|;
specifier|private
name|void
name|waitFor
parameter_list|(
name|String
name|transactionId
parameter_list|,
name|String
name|status
parameter_list|)
throws|throws
name|IOException
block|{
name|Response
name|response
init|=
literal|null
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|response
operator|=
name|getResponse
argument_list|(
name|transactionId
argument_list|)
expr_stmt|;
if|if
condition|(
name|response
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|response
operator|.
name|getElemetsByName
argument_list|(
literal|"error"
argument_list|)
operator|.
name|getLength
argument_list|()
operator|!=
literal|0
condition|)
break|break;
name|String
name|getStatus
init|=
name|response
operator|.
name|getAttribute
argument_list|(
literal|"status"
argument_list|)
decl_stmt|;
if|if
condition|(
name|getStatus
operator|.
name|equals
argument_list|(
name|status
argument_list|)
condition|)
block|{
break|break;
block|}
if|else if
condition|(
name|getStatus
operator|.
name|equals
argument_list|(
name|STOPPED
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
try|try
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|500
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
block|}
block|}
specifier|public
name|void
name|run
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|Run
name|command
init|=
operator|new
name|Run
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|addResponseListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|run
parameter_list|()
throws|throws
name|IOException
block|{
name|Run
name|command
init|=
operator|new
name|Run
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|BREAK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stepInto
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|StepInto
name|command
init|=
operator|new
name|StepInto
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|addResponseListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stepInto
parameter_list|()
throws|throws
name|IOException
block|{
name|StepInto
name|command
init|=
operator|new
name|StepInto
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|BREAK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stepOut
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|StepOut
name|command
init|=
operator|new
name|StepOut
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|addResponseListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stepOut
parameter_list|()
throws|throws
name|IOException
block|{
name|StepOut
name|command
init|=
operator|new
name|StepOut
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|BREAK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stepOver
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|StepOver
name|command
init|=
operator|new
name|StepOver
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|addResponseListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stepOver
parameter_list|()
throws|throws
name|IOException
block|{
name|StepOver
name|command
init|=
operator|new
name|StepOver
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|BREAK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|Stop
name|command
init|=
operator|new
name|Stop
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|addResponseListener
argument_list|(
name|listener
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|stop
parameter_list|()
throws|throws
name|IOException
block|{
name|Stop
name|command
init|=
operator|new
name|Stop
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|waitFor
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|,
name|BREAK
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|setBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|BreakpointSet
name|command
init|=
operator|new
name|BreakpointSet
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBreakpoint
argument_list|(
operator|(
name|BreakpointImpl
operator|)
name|breakpoint
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
comment|//XXX: handle error
name|breakpoint
operator|.
name|setState
argument_list|(
literal|"enabled"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getAttribute
argument_list|(
literal|"state"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|breakpoint
operator|.
name|setId
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|response
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|updateBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|BreakpointUpdate
name|command
init|=
operator|new
name|BreakpointUpdate
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBreakpoint
argument_list|(
name|breakpoint
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
comment|//		Response response =
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
comment|//XXX: handle error
return|return
literal|true
return|;
block|}
specifier|public
name|boolean
name|removeBreakpoint
parameter_list|(
name|BreakpointImpl
name|breakpoint
parameter_list|)
throws|throws
name|IOException
block|{
name|BreakpointRemove
name|command
init|=
operator|new
name|BreakpointRemove
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setBreakpoint
argument_list|(
name|breakpoint
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
comment|//		Response response =
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
expr_stmt|;
comment|//XXX: handle error
return|return
literal|true
return|;
block|}
specifier|protected
specifier|synchronized
name|void
name|terminate
parameter_list|(
name|String
name|url
parameter_list|,
name|int
name|code
parameter_list|)
block|{
name|responseCode
operator|=
name|code
expr_stmt|;
name|notifyAll
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"setResponseCode responseCode = "
operator|+
name|responseCode
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|getText
parameter_list|(
name|NodeList
name|nodes
parameter_list|)
block|{
if|if
condition|(
operator|(
name|nodes
operator|.
name|getLength
argument_list|()
operator|==
literal|1
operator|)
operator|&&
operator|(
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeType
argument_list|()
operator|==
name|Node
operator|.
name|TEXT_NODE
operator|)
condition|)
return|return
operator|(
operator|(
name|Text
operator|)
name|nodes
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getData
argument_list|()
return|;
return|return
literal|""
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|evaluate
parameter_list|(
name|String
name|script
parameter_list|)
throws|throws
name|IOException
block|{
name|Eval
name|command
init|=
operator|new
name|Eval
argument_list|(
name|session
argument_list|,
literal|" -i "
operator|+
name|getNextTransaction
argument_list|()
argument_list|)
decl_stmt|;
name|command
operator|.
name|setScript
argument_list|(
name|script
argument_list|)
expr_stmt|;
name|command
operator|.
name|toDebuggee
argument_list|()
expr_stmt|;
name|Response
name|response
init|=
name|getResponse
argument_list|(
name|command
operator|.
name|getTransactionId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"1"
operator|.
name|equals
argument_list|(
name|response
operator|.
name|getAttribute
argument_list|(
literal|"success"
argument_list|)
argument_list|)
condition|)
block|{
name|Node
name|property
init|=
name|response
operator|.
name|getElemetsByName
argument_list|(
literal|"property"
argument_list|)
operator|.
name|item
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Base64Decoder
name|dec
init|=
operator|new
name|Base64Decoder
argument_list|()
decl_stmt|;
name|dec
operator|.
name|translate
argument_list|(
name|getText
argument_list|(
name|property
operator|.
name|getChildNodes
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|byte
index|[]
name|c
init|=
name|dec
operator|.
name|getByteArray
argument_list|()
decl_stmt|;
return|return
operator|new
name|String
argument_list|(
name|c
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

