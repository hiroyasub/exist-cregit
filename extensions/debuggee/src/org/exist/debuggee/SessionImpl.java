begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|net
operator|.
name|SocketAddress
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|filterchain
operator|.
name|IoFilterChain
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
name|future
operator|.
name|CloseFuture
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
name|future
operator|.
name|ReadFuture
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
name|future
operator|.
name|WriteFuture
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
name|IoHandler
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
name|IoService
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
name|TransportMetadata
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
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|session
operator|.
name|IoSessionConfig
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
name|write
operator|.
name|WriteRequest
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
name|write
operator|.
name|WriteRequestQueue
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SessionImpl
implements|implements
name|IoSession
implements|,
name|Session
block|{
specifier|private
name|long
name|creationTime
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
name|Map
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
name|attributes
init|=
operator|new
name|HashMap
argument_list|<
name|Object
argument_list|,
name|Object
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SessionImpl
parameter_list|()
block|{
name|creationTime
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#close() 	 */
annotation|@
name|Override
specifier|public
name|CloseFuture
name|close
parameter_list|()
block|{
name|closed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#close(boolean) 	 */
annotation|@
name|Override
specifier|public
name|CloseFuture
name|close
parameter_list|(
name|boolean
name|arg0
parameter_list|)
block|{
name|closed
operator|=
literal|true
expr_stmt|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#containsAttribute(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|containsAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getAttachment() 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getAttachment
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getAttribute(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|get
argument_list|(
name|arg0
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getAttribute(java.lang.Object, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getAttributeKeys() 	 */
annotation|@
name|Override
specifier|public
name|Set
argument_list|<
name|Object
argument_list|>
name|getAttributeKeys
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getBothIdleCount() 	 */
annotation|@
name|Override
specifier|public
name|int
name|getBothIdleCount
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getCloseFuture() 	 */
annotation|@
name|Override
specifier|public
name|CloseFuture
name|getCloseFuture
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getConfig() 	 */
annotation|@
name|Override
specifier|public
name|IoSessionConfig
name|getConfig
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getCreationTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getCreationTime
parameter_list|()
block|{
return|return
name|creationTime
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getCurrentWriteMessage() 	 */
annotation|@
name|Override
specifier|public
name|Object
name|getCurrentWriteMessage
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getCurrentWriteRequest() 	 */
annotation|@
name|Override
specifier|public
name|WriteRequest
name|getCurrentWriteRequest
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getFilterChain() 	 */
annotation|@
name|Override
specifier|public
name|IoFilterChain
name|getFilterChain
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getHandler() 	 */
annotation|@
name|Override
specifier|public
name|IoHandler
name|getHandler
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getId() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getId
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getIdleCount(org.apache.mina.core.session.IdleStatus) 	 */
annotation|@
name|Override
specifier|public
name|int
name|getIdleCount
parameter_list|(
name|IdleStatus
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastBothIdleTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastBothIdleTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastIdleTime(org.apache.mina.core.session.IdleStatus) 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastIdleTime
parameter_list|(
name|IdleStatus
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastIoTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastIoTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastReadTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastReadTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastReaderIdleTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastReaderIdleTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastWriteTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastWriteTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLastWriterIdleTime() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getLastWriterIdleTime
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getLocalAddress() 	 */
annotation|@
name|Override
specifier|public
name|SocketAddress
name|getLocalAddress
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getReadBytes() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getReadBytes
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getReadBytesThroughput() 	 */
annotation|@
name|Override
specifier|public
name|double
name|getReadBytesThroughput
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getReadMessages() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getReadMessages
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getReadMessagesThroughput() 	 */
annotation|@
name|Override
specifier|public
name|double
name|getReadMessagesThroughput
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getReaderIdleCount() 	 */
annotation|@
name|Override
specifier|public
name|int
name|getReaderIdleCount
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getRemoteAddress() 	 */
annotation|@
name|Override
specifier|public
name|SocketAddress
name|getRemoteAddress
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getScheduledWriteBytes() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getScheduledWriteBytes
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getScheduledWriteMessages() 	 */
annotation|@
name|Override
specifier|public
name|int
name|getScheduledWriteMessages
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getService() 	 */
annotation|@
name|Override
specifier|public
name|IoService
name|getService
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getServiceAddress() 	 */
annotation|@
name|Override
specifier|public
name|SocketAddress
name|getServiceAddress
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getTransportMetadata() 	 */
annotation|@
name|Override
specifier|public
name|TransportMetadata
name|getTransportMetadata
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWriteRequestQueue() 	 */
annotation|@
name|Override
specifier|public
name|WriteRequestQueue
name|getWriteRequestQueue
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWriterIdleCount() 	 */
annotation|@
name|Override
specifier|public
name|int
name|getWriterIdleCount
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWrittenBytes() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getWrittenBytes
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWrittenBytesThroughput() 	 */
annotation|@
name|Override
specifier|public
name|double
name|getWrittenBytesThroughput
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWrittenMessages() 	 */
annotation|@
name|Override
specifier|public
name|long
name|getWrittenMessages
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#getWrittenMessagesThroughput() 	 */
annotation|@
name|Override
specifier|public
name|double
name|getWrittenMessagesThroughput
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|0
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isBothIdle() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isBothIdle
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isClosing() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isClosing
parameter_list|()
block|{
return|return
name|closed
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isConnected() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isConnected
parameter_list|()
block|{
return|return
operator|!
name|closed
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isIdle(org.apache.mina.core.session.IdleStatus) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isIdle
parameter_list|(
name|IdleStatus
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isReadSuspended() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isReadSuspended
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isReaderIdle() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isReaderIdle
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isWriteSuspended() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isWriteSuspended
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#isWriterIdle() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isWriterIdle
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#read() 	 */
annotation|@
name|Override
specifier|public
name|ReadFuture
name|read
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#removeAttribute(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|removeAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#removeAttribute(java.lang.Object, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|removeAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#replaceAttribute(java.lang.Object, java.lang.Object, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|replaceAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|,
name|Object
name|arg2
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#resumeRead() 	 */
annotation|@
name|Override
specifier|public
name|void
name|resumeRead
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#resumeWrite() 	 */
annotation|@
name|Override
specifier|public
name|void
name|resumeWrite
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setAttachment(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|setAttachment
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setAttribute(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|setAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setAttribute(java.lang.Object, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|setAttribute
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
return|return
name|attributes
operator|.
name|put
argument_list|(
name|arg0
argument_list|,
name|arg1
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setAttributeIfAbsent(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|setAttributeIfAbsent
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setAttributeIfAbsent(java.lang.Object, java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|Object
name|setAttributeIfAbsent
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|Object
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#setCurrentWriteRequest(org.apache.mina.core.write.WriteRequest) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setCurrentWriteRequest
parameter_list|(
name|WriteRequest
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#suspendRead() 	 */
annotation|@
name|Override
specifier|public
name|void
name|suspendRead
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#suspendWrite() 	 */
annotation|@
name|Override
specifier|public
name|void
name|suspendWrite
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#updateThroughput(long, boolean) 	 */
annotation|@
name|Override
specifier|public
name|void
name|updateThroughput
parameter_list|(
name|long
name|arg0
parameter_list|,
name|boolean
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#write(java.lang.Object) 	 */
annotation|@
name|Override
specifier|public
name|WriteFuture
name|write
parameter_list|(
name|Object
name|arg0
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.apache.mina.core.session.IoSession#write(java.lang.Object, java.net.SocketAddress) 	 */
annotation|@
name|Override
specifier|public
name|WriteFuture
name|write
parameter_list|(
name|Object
name|arg0
parameter_list|,
name|SocketAddress
name|arg1
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

