begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|debuggee
operator|.
name|dbgp
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
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|service
operator|.
name|IoHandlerAdapter
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
name|IdleStatus
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
name|exist
operator|.
name|debuggee
operator|.
name|DebuggeeJoint
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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ProtocolHandler
extends|extends
name|IoHandlerAdapter
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|LogManager
operator|.
name|getLogger
argument_list|(
name|ProtocolHandler
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|ProtocolHandler
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sessionOpened
parameter_list|(
name|IoSession
name|session
parameter_list|)
block|{
comment|// Set reader idle time to 10 minutes.
name|session
operator|.
name|getConfig
argument_list|()
operator|.
name|setIdleTime
argument_list|(
name|IdleStatus
operator|.
name|READER_IDLE
argument_list|,
literal|10
operator|*
literal|60
operator|*
literal|1000
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sessionClosed
parameter_list|(
name|IoSession
name|session
parameter_list|)
block|{
name|DebuggeeJoint
name|joint
init|=
operator|(
name|DebuggeeJoint
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"joint"
argument_list|)
decl_stmt|;
if|if
condition|(
name|joint
operator|!=
literal|null
condition|)
name|joint
operator|.
name|sessionClosed
argument_list|(
literal|false
argument_list|)
expr_stmt|;
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
literal|"Total "
operator|+
name|session
operator|.
name|getReadBytes
argument_list|()
operator|+
literal|" byte(s) readed, "
operator|+
name|session
operator|.
name|getWrittenBytes
argument_list|()
operator|+
literal|" byte(s) writed."
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|sessionIdle
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|IdleStatus
name|status
parameter_list|)
block|{
comment|// Close the connection if reader is idle.
if|if
condition|(
name|status
operator|==
name|IdleStatus
operator|.
name|READER_IDLE
condition|)
block|{
name|session
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|messageReceived
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|Object
name|message
parameter_list|)
block|{
name|Command
name|command
init|=
operator|(
name|Command
operator|)
name|message
decl_stmt|;
comment|//		command.exec();
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
name|command
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|session
operator|.
name|write
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|exceptionCaught
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|Throwable
name|cause
parameter_list|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|cause
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
