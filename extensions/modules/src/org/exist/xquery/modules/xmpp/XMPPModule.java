begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|xmpp
package|;
end_package

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
name|exist
operator|.
name|xquery
operator|.
name|AbstractInternalModule
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
name|FunctionDef
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
name|jivesoftware
operator|.
name|smack
operator|.
name|Chat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jivesoftware
operator|.
name|smack
operator|.
name|XMPPConnection
import|;
end_import

begin_comment
comment|/**  * eXist XMPP Module Extension  *   * An extension module for the eXist Native XML Database that allows  * chats via XMPP protocol.  *     * @author Evgeny Gazdovsky<gazdovsky@gmail.com>  * @version 1.5  *  */
end_comment

begin_class
specifier|public
class|class
name|XMPPModule
extends|extends
name|AbstractInternalModule
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
name|XMPPModule
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/xmpp"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|PREFIX
init|=
literal|"xmpp"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2010-02-14"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.5 (Smack-based in trunk)"
decl_stmt|;
specifier|private
specifier|static
name|HashMap
argument_list|<
name|Long
argument_list|,
name|XMPPConnection
argument_list|>
name|connections
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|XMPPConnection
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Chat
argument_list|>
name|chats
init|=
operator|new
name|HashMap
argument_list|<
name|Long
argument_list|,
name|Chat
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|XMPPConnectionFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPConnectionFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPDisconnectFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPDisconnectFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPConnectFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPConnectFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPLoginFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPLoginFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPLoginFunction
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|XMPPLoginFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPLoginFunction
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|XMPPLoginFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPChatFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPChatFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|XMPPSendMessageFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|XMPPSendMessageFunction
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CONNECTIONS_CONTEXTVAR
init|=
literal|"_eXist_xmpp_connections"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|CHATS_CONTEXTVAR
init|=
literal|"_eXist_xmpp_chats"
decl_stmt|;
specifier|private
specifier|static
name|long
name|currentSessionHandle
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|public
name|XMPPModule
parameter_list|(
name|Map
argument_list|<
name|String
argument_list|,
name|List
argument_list|<
name|?
extends|extends
name|Object
argument_list|>
argument_list|>
name|parameters
parameter_list|)
block|{
name|super
argument_list|(
name|functions
argument_list|,
name|parameters
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
operator|(
name|NAMESPACE_URI
operator|)
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
operator|(
name|PREFIX
operator|)
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
operator|(
literal|"A module for XMPP messaging"
operator|)
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
specifier|final
specifier|static
name|XMPPConnection
name|retrieveConnection
parameter_list|(
name|long
name|connectionHandle
parameter_list|)
block|{
return|return
operator|(
name|connections
operator|.
name|get
argument_list|(
operator|new
name|Long
argument_list|(
name|connectionHandle
argument_list|)
argument_list|)
operator|)
return|;
block|}
specifier|final
specifier|static
specifier|synchronized
name|long
name|storeConnection
parameter_list|(
name|XMPPConnection
name|connection
parameter_list|)
block|{
name|long
name|connectionHandle
init|=
name|getHandle
argument_list|()
decl_stmt|;
name|connections
operator|.
name|put
argument_list|(
operator|new
name|Long
argument_list|(
name|connectionHandle
argument_list|)
argument_list|,
name|connection
argument_list|)
expr_stmt|;
return|return
operator|(
name|connectionHandle
operator|)
return|;
block|}
specifier|final
specifier|static
specifier|synchronized
name|void
name|closeConnection
parameter_list|(
name|long
name|connectionHandle
parameter_list|)
block|{
name|XMPPConnection
name|connection
init|=
name|connections
operator|.
name|get
argument_list|(
name|connectionHandle
argument_list|)
decl_stmt|;
name|connection
operator|.
name|disconnect
argument_list|()
expr_stmt|;
name|connections
operator|.
name|remove
argument_list|(
operator|new
name|Long
argument_list|(
name|connectionHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
specifier|static
name|Chat
name|retrieveChat
parameter_list|(
name|long
name|chatHandle
parameter_list|)
block|{
return|return
operator|(
name|chats
operator|.
name|get
argument_list|(
operator|new
name|Long
argument_list|(
name|chatHandle
argument_list|)
argument_list|)
operator|)
return|;
block|}
specifier|final
specifier|static
specifier|synchronized
name|long
name|storeChat
parameter_list|(
name|Chat
name|chat
parameter_list|,
name|long
name|chatHandle
parameter_list|)
block|{
name|chats
operator|.
name|put
argument_list|(
operator|new
name|Long
argument_list|(
name|chatHandle
argument_list|)
argument_list|,
name|chat
argument_list|)
expr_stmt|;
return|return
operator|(
name|chatHandle
operator|)
return|;
block|}
specifier|final
specifier|static
specifier|synchronized
name|long
name|closeChat
parameter_list|(
name|XQueryContext
name|context
parameter_list|,
name|Chat
name|chat
parameter_list|)
block|{
return|return
name|storeChat
argument_list|(
name|chat
argument_list|,
name|getHandle
argument_list|()
argument_list|)
return|;
block|}
specifier|final
specifier|static
specifier|synchronized
name|void
name|removeChat
parameter_list|(
name|long
name|chatHandle
parameter_list|)
block|{
name|chats
operator|.
name|remove
argument_list|(
operator|new
name|Long
argument_list|(
name|chatHandle
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|protected
specifier|static
specifier|synchronized
name|long
name|getHandle
parameter_list|()
block|{
return|return
operator|(
name|currentSessionHandle
operator|++
operator|)
return|;
block|}
block|}
end_class

end_unit

