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
operator|.
name|dbgp
operator|.
name|packets
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
name|List
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
name|CommandContinuation
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
name|Response
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
name|ResponseListener
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractCommandContinuation
extends|extends
name|Command
implements|implements
name|CommandContinuation
block|{
specifier|protected
name|String
name|status
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|callStackDepth
init|=
literal|0
decl_stmt|;
specifier|public
name|AbstractCommandContinuation
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
return|return
name|status
operator|.
name|equals
argument_list|(
name|getStatus
argument_list|()
argument_list|)
return|;
block|}
specifier|public
name|String
name|getStatus
parameter_list|()
block|{
return|return
name|status
return|;
block|}
specifier|public
name|void
name|setStatus
parameter_list|(
name|String
name|status
parameter_list|)
block|{
name|this
operator|.
name|status
operator|=
name|status
expr_stmt|;
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
name|write
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getCallStackDepth
parameter_list|()
block|{
return|return
name|callStackDepth
return|;
block|}
specifier|public
name|void
name|setCallStackDepth
parameter_list|(
name|int
name|callStackDepth
parameter_list|)
block|{
name|this
operator|.
name|callStackDepth
operator|=
name|callStackDepth
expr_stmt|;
block|}
specifier|public
name|void
name|disconnect
parameter_list|()
block|{
name|session
operator|.
name|close
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|ResponseListener
argument_list|>
name|listeners
init|=
operator|new
name|ArrayList
argument_list|<
name|ResponseListener
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|void
name|addResponseListener
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|add
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|removeResponseListener
parameter_list|(
name|ResponseListener
name|listener
parameter_list|)
block|{
name|listeners
operator|.
name|remove
argument_list|(
name|listener
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|putResponse
parameter_list|(
name|Response
name|response
parameter_list|)
block|{
name|status
operator|=
name|response
operator|.
name|getAttribute
argument_list|(
literal|"status"
argument_list|)
expr_stmt|;
for|for
control|(
name|ResponseListener
name|listener
range|:
name|listeners
control|)
block|{
name|listener
operator|.
name|responseEvent
argument_list|(
name|this
argument_list|,
name|response
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

