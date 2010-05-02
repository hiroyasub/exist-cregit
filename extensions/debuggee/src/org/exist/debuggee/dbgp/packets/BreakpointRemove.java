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
name|debuggee
operator|.
name|dbgp
operator|.
name|packets
package|;
end_package

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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|BreakpointRemove
extends|extends
name|Command
block|{
comment|/** 	 * is the unique session breakpoint id returned by breakpoint_set. 	 */
specifier|private
name|Integer
name|breakpointID
decl_stmt|;
specifier|private
name|Breakpoint
name|breakpoint
decl_stmt|;
specifier|public
name|BreakpointRemove
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
specifier|protected
name|void
name|init
parameter_list|()
block|{
name|breakpointID
operator|=
literal|null
expr_stmt|;
block|}
specifier|protected
name|void
name|setArgument
parameter_list|(
name|String
name|arg
parameter_list|,
name|String
name|val
parameter_list|)
block|{
if|if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"d"
argument_list|)
condition|)
block|{
name|breakpointID
operator|=
name|Integer
operator|.
name|valueOf
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|setArgument
argument_list|(
name|arg
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
if|if
condition|(
name|breakpointID
operator|!=
literal|null
condition|)
name|breakpoint
operator|=
name|getJoint
argument_list|()
operator|.
name|removeBreakpoint
argument_list|(
name|breakpointID
argument_list|)
expr_stmt|;
block|}
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
if|if
condition|(
name|breakpoint
operator|!=
literal|null
condition|)
block|{
name|String
name|responce
init|=
literal|"<response "
operator|+
literal|"command=\"breakpoint_remove\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\"/>"
decl_stmt|;
return|return
name|responce
operator|.
name|getBytes
argument_list|()
return|;
block|}
return|return
name|errorBytes
argument_list|(
literal|"breakpoint_remove"
argument_list|)
return|;
block|}
specifier|public
name|byte
index|[]
name|commandBytes
parameter_list|()
block|{
if|if
condition|(
name|breakpoint
operator|!=
literal|null
condition|)
block|{
name|String
name|command
init|=
literal|"breakpoint_remove -i "
operator|+
name|transactionID
operator|+
literal|" -d "
operator|+
name|breakpoint
operator|.
name|getId
argument_list|()
decl_stmt|;
return|return
name|command
operator|.
name|getBytes
argument_list|()
return|;
block|}
return|return
literal|null
return|;
block|}
specifier|public
name|void
name|setBreakpoint
parameter_list|(
name|BreakpointImpl
name|breakpoint
parameter_list|)
block|{
name|this
operator|.
name|breakpoint
operator|=
name|breakpoint
expr_stmt|;
block|}
block|}
end_class

end_unit

