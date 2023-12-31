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
package|;
end_package

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
name|org
operator|.
name|apache
operator|.
name|mina
operator|.
name|core
operator|.
name|RuntimeIoException
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
name|ConnectFuture
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
name|NioSocketConnector
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
name|CodecFactory
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
name|ProtocolHandler
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|DebuggeeConnectionTCP
implements|implements
name|DebuggeeConnection
block|{
specifier|private
name|String
name|host
init|=
literal|"127.0.0.1"
decl_stmt|;
specifier|private
name|int
name|port
init|=
literal|9000
decl_stmt|;
specifier|private
name|NioSocketConnector
name|connector
decl_stmt|;
specifier|public
name|DebuggeeConnectionTCP
parameter_list|()
block|{
comment|// Create TCP/IP connector.
name|connector
operator|=
operator|new
name|NioSocketConnector
argument_list|()
expr_stmt|;
comment|// Set connect timeout for 30 seconds.
name|connector
operator|.
name|setConnectTimeoutMillis
argument_list|(
literal|30
operator|*
literal|1000L
argument_list|)
expr_stmt|;
name|connector
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
comment|// Start communication.
name|connector
operator|.
name|setHandler
argument_list|(
operator|new
name|ProtocolHandler
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IoSession
name|connect
parameter_list|()
block|{
synchronized|synchronized
init|(
name|connector
init|)
block|{
try|try
block|{
name|ConnectFuture
name|future
init|=
name|connector
operator|.
name|connect
argument_list|(
operator|new
name|InetSocketAddress
argument_list|(
name|host
argument_list|,
name|port
argument_list|)
argument_list|)
decl_stmt|;
name|future
operator|.
name|awaitUninterruptibly
argument_list|()
expr_stmt|;
return|return
name|future
operator|.
name|getSession
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|RuntimeIoException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

