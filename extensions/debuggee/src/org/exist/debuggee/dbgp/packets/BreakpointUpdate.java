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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|BreakpointUpdate
extends|extends
name|Command
block|{
comment|/** 	 * is the unique session breakpoint id returned by breakpoint_set. 	 */
specifier|private
name|int
name|breakpointID
decl_stmt|;
comment|/** 	 * breakpoint state [optional, defaults to "enabled"] 	 */
specifier|private
name|Boolean
name|state
init|=
literal|null
decl_stmt|;
comment|/** 	 * the line number (lineno) of the breakpoint [optional] 	 */
specifier|private
name|Integer
name|lineNo
init|=
literal|null
decl_stmt|;
comment|/** 	 * hit value (hit_value) used with the hit condition to determine if should break; a value of zero indicates hit count processing is disabled for this breakpoint [optional, defaults to zero (i.e. disabled)] 	 */
specifier|private
name|Integer
name|hitValue
init|=
literal|null
decl_stmt|;
comment|/** 	 * hit condition string (hit_condition); see HIT_CONDITION hit_condition documentation above; BTW 'o' stands for 'operator' [optional, defaults to '>='] 	 */
specifier|private
name|String
name|hitCondition
init|=
literal|null
decl_stmt|;
specifier|private
name|Breakpoint
name|breakpoint
decl_stmt|;
specifier|public
name|BreakpointUpdate
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
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"s"
argument_list|)
condition|)
block|{
name|state
operator|=
literal|true
expr_stmt|;
comment|//TODO: parsing required
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"n"
argument_list|)
condition|)
block|{
name|lineNo
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"h"
argument_list|)
condition|)
block|{
name|hitValue
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"o"
argument_list|)
condition|)
block|{
name|hitCondition
operator|=
name|val
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
name|breakpoint
operator|=
name|getJoint
argument_list|()
operator|.
name|getBreakpoint
argument_list|(
name|breakpointID
argument_list|)
expr_stmt|;
if|if
condition|(
name|breakpoint
operator|==
literal|null
condition|)
return|return;
if|if
condition|(
name|state
operator|!=
literal|null
condition|)
name|breakpoint
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
if|if
condition|(
name|lineNo
operator|!=
literal|null
condition|)
name|breakpoint
operator|.
name|setLineno
argument_list|(
name|lineNo
argument_list|)
expr_stmt|;
if|if
condition|(
name|hitValue
operator|!=
literal|null
condition|)
name|breakpoint
operator|.
name|setHitValue
argument_list|(
name|hitValue
argument_list|)
expr_stmt|;
if|if
condition|(
name|hitCondition
operator|!=
literal|null
condition|)
name|breakpoint
operator|.
name|setHitCondition
argument_list|(
name|hitCondition
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
literal|"command=\"breakpoint_update\" "
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
literal|"breakpoint_update"
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
literal|"breakpoint_update"
operator|+
literal|" -i "
operator|+
name|transactionID
operator|+
comment|//					" -t " + getType() +
comment|//					" -s " + getStateString() +
comment|//					" -f " + getFilename() +
literal|" -h "
operator|+
name|breakpoint
operator|.
name|getHitValue
argument_list|()
operator|+
literal|" -o "
operator|+
name|breakpoint
operator|.
name|getHitCondition
argument_list|()
decl_stmt|;
comment|//					" -r " + getTemporaryString();
if|if
condition|(
name|breakpoint
operator|.
name|getLineno
argument_list|()
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -s "
operator|+
name|breakpoint
operator|.
name|getLineno
argument_list|()
expr_stmt|;
comment|//			if (getFunction() != null)
comment|//				responce += " -m " + getFunction();
comment|//
comment|//			if (getException() != null)
comment|//				responce += " -x " + getException();
comment|//TODO: EXPRESSION
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
name|Breakpoint
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

