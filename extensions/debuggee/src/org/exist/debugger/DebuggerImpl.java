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
name|HashMap
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
name|Run
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
name|Source
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
name|StepInto
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
name|StepOut
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
name|StepOver
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
name|Stop
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
name|util
operator|.
name|Base64Decoder
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
specifier|public
name|DebuggerImpl
parameter_list|()
block|{
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
name|acceptor
operator|=
operator|new
name|NioSocketAcceptor
argument_list|()
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
name|LOG
operator|.
name|info
argument_list|(
literal|"Debugger is listening at port "
operator|+
name|eventPort
argument_list|)
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
block|{
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
name|Breakpoint
name|addBreakpoint
parameter_list|(
name|Breakpoint
name|breakpoint
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|sessionClosed
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
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
else|else
comment|//it should be commands map, this implementation is dangerous
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
comment|// throw error???
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
block|}
end_class

end_unit

