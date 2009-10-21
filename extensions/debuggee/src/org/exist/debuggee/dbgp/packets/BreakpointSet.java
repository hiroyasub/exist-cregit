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
name|BreakpointSet
extends|extends
name|Command
implements|implements
name|Breakpoint
block|{
specifier|private
name|BreakpointImpl
name|breakpoint
decl_stmt|;
specifier|private
name|int
name|status
init|=
operator|-
literal|1
decl_stmt|;
specifier|public
name|BreakpointSet
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"breakpoint = "
operator|+
name|breakpoint
argument_list|)
expr_stmt|;
block|}
specifier|protected
name|void
name|init
parameter_list|()
block|{
name|breakpoint
operator|=
operator|new
name|BreakpointImpl
argument_list|()
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
literal|"t"
argument_list|)
condition|)
block|{
name|setType
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
if|if
condition|(
name|val
operator|.
name|equals
argument_list|(
literal|"enabled"
argument_list|)
condition|)
name|setState
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|else if
condition|(
name|val
operator|.
name|equals
argument_list|(
literal|"disabled"
argument_list|)
condition|)
name|setState
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//TODO: exception???
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"f"
argument_list|)
condition|)
block|{
name|setFilename
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
literal|"n"
argument_list|)
condition|)
block|{
name|setLineno
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"m"
argument_list|)
condition|)
block|{
name|setFunction
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
literal|"x"
argument_list|)
condition|)
block|{
name|setException
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
name|setHitValue
argument_list|(
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
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
name|setHitCondition
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
literal|"r"
argument_list|)
condition|)
block|{
if|if
condition|(
name|val
operator|.
name|equals
argument_list|(
literal|"1"
argument_list|)
condition|)
name|setTemporary
argument_list|(
literal|true
argument_list|)
expr_stmt|;
if|else if
condition|(
name|val
operator|.
name|equals
argument_list|(
literal|"0"
argument_list|)
condition|)
name|setTemporary
argument_list|(
literal|false
argument_list|)
expr_stmt|;
comment|//TODO: exception???
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
specifier|private
name|BreakpointImpl
name|getBreakpoint
parameter_list|()
block|{
return|return
name|breakpoint
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
name|status
operator|=
name|getJoint
argument_list|()
operator|.
name|setBreakpoint
argument_list|(
name|this
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
name|status
operator|==
literal|1
condition|)
block|{
name|String
name|responce
init|=
literal|"<response "
operator|+
literal|"command=\"breakpoint_set\" "
operator|+
literal|"state=\""
operator|+
name|getStateString
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"id=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|getId
argument_list|()
argument_list|)
operator|+
literal|"\" "
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
literal|"breakpoint_set"
argument_list|)
return|;
block|}
specifier|private
name|String
name|getStateString
parameter_list|()
block|{
if|if
condition|(
name|getBreakpoint
argument_list|()
operator|.
name|getState
argument_list|()
condition|)
return|return
literal|"enabled"
return|;
return|return
literal|"disabled"
return|;
block|}
specifier|private
name|String
name|getTemporaryString
parameter_list|()
block|{
if|if
condition|(
name|getTemporary
argument_list|()
condition|)
return|return
literal|"1"
return|;
return|return
literal|"0"
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
literal|"breakpoint_set"
operator|+
literal|" -i "
operator|+
name|transactionID
operator|+
literal|" -t "
operator|+
name|getType
argument_list|()
operator|+
literal|" -s "
operator|+
name|getStateString
argument_list|()
operator|+
literal|" -f "
operator|+
name|getFilename
argument_list|()
operator|+
literal|" -h "
operator|+
name|getHitValue
argument_list|()
operator|+
literal|" -o "
operator|+
name|getHitCondition
argument_list|()
operator|+
literal|" -r "
operator|+
name|getTemporaryString
argument_list|()
decl_stmt|;
if|if
condition|(
name|getLineno
argument_list|()
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -s "
operator|+
name|getLineno
argument_list|()
expr_stmt|;
if|if
condition|(
name|getFunction
argument_list|()
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -m "
operator|+
name|getFunction
argument_list|()
expr_stmt|;
if|if
condition|(
name|getException
argument_list|()
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -x "
operator|+
name|getException
argument_list|()
expr_stmt|;
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
comment|///////////////////////////////////////////////////////////////////
comment|// Breakpoint's methods
comment|///////////////////////////////////////////////////////////////////
specifier|public
name|String
name|getException
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getException
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFilename
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getFilename
argument_list|()
return|;
block|}
specifier|public
name|String
name|getFunction
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getFunction
argument_list|()
return|;
block|}
specifier|public
name|String
name|getHitCondition
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getHitCondition
argument_list|()
return|;
block|}
specifier|public
name|int
name|getHitCount
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getHitCount
argument_list|()
return|;
block|}
specifier|public
name|int
name|getHitValue
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getHitValue
argument_list|()
return|;
block|}
specifier|public
name|Integer
name|getLineno
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getLineno
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getState
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getState
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getTemporary
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getTemporary
argument_list|()
return|;
block|}
specifier|public
name|void
name|setException
parameter_list|(
name|String
name|exception
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setException
argument_list|(
name|exception
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFilename
parameter_list|(
name|String
name|filename
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setFilename
argument_list|(
name|filename
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setFunction
parameter_list|(
name|String
name|function
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setFunction
argument_list|(
name|function
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setHitCondition
parameter_list|(
name|String
name|condition
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setHitCondition
argument_list|(
name|condition
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setHitCount
parameter_list|(
name|int
name|count
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setHitCount
argument_list|(
name|count
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setHitValue
parameter_list|(
name|int
name|value
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setHitValue
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setLineno
parameter_list|(
name|Integer
name|lineno
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setLineno
argument_list|(
name|lineno
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setState
parameter_list|(
name|boolean
name|state
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setState
argument_list|(
name|state
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|setTemporary
parameter_list|(
name|boolean
name|temporary
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setTemporary
argument_list|(
name|temporary
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getId
argument_list|()
return|;
block|}
specifier|public
name|void
name|setId
parameter_list|(
name|int
name|breakpointNo
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setId
argument_list|(
name|breakpointNo
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getType
parameter_list|()
block|{
return|return
name|getBreakpoint
argument_list|()
operator|.
name|getType
argument_list|()
return|;
block|}
specifier|public
name|void
name|setType
parameter_list|(
name|String
name|type
parameter_list|)
block|{
name|getBreakpoint
argument_list|()
operator|.
name|setType
argument_list|(
name|type
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getExpression
parameter_list|()
block|{
comment|//TODO: implement
return|return
literal|""
return|;
block|}
specifier|public
name|void
name|setExpression
parameter_list|(
name|String
name|expression
parameter_list|)
block|{
comment|//TODO: implement
empty_stmt|;
block|}
specifier|public
name|String
name|toXMLString
parameter_list|()
block|{
return|return
literal|"<breakpoint "
operator|+
literal|"id=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|getId
argument_list|()
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"type=\""
operator|+
name|getType
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"state=\""
operator|+
name|getStateString
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"filename=\""
operator|+
name|getFilename
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"lineno=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|getLineno
argument_list|()
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"function=\""
operator|+
name|getFunction
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"exception=\""
operator|+
name|getException
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"hit_value=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|getHitValue
argument_list|()
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"hit_condition=\""
operator|+
name|getHitCondition
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"hit_count=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|getHitCount
argument_list|()
argument_list|)
operator|+
literal|"\">"
operator|+
literal|"<expression>"
operator|+
name|getExpression
argument_list|()
operator|+
literal|"</expression>"
operator|+
literal|"</breakpoint>"
return|;
block|}
block|}
end_class

end_unit

