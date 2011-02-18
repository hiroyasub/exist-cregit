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
name|Packet
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
name|Errors
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Command
implements|implements
name|Packet
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|Packet
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|namespaces
init|=
literal|"xmlns=\"urn:debugger_protocol_v1\" "
operator|+
literal|"xmlns:xdebug=\"http://xdebug.org/dbgp/xdebug\" "
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|xml_declaration
init|=
literal|"<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n"
decl_stmt|;
specifier|protected
name|IoSession
name|session
decl_stmt|;
comment|/** 	 * Unique numerical ID for each command generated by the IDE 	 */
specifier|protected
name|String
name|transactionID
decl_stmt|;
specifier|public
name|Command
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|this
operator|.
name|session
operator|=
name|session
expr_stmt|;
name|init
argument_list|()
expr_stmt|;
name|String
index|[]
name|splited
init|=
name|args
operator|.
name|split
argument_list|(
literal|" -"
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
name|splited
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|splited
index|[
name|i
index|]
operator|.
name|length
argument_list|()
operator|<
literal|3
condition|)
continue|continue;
name|String
name|arg
init|=
name|splited
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
literal|1
argument_list|)
decl_stmt|;
name|String
name|val
init|=
name|splited
index|[
name|i
index|]
operator|.
name|substring
argument_list|(
literal|2
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|setArgument
argument_list|(
name|arg
argument_list|,
name|val
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|void
name|init
parameter_list|()
block|{
comment|//used to init original class vars
block|}
specifier|public
name|String
name|getTransactionId
parameter_list|()
block|{
return|return
name|transactionID
return|;
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
literal|"i"
argument_list|)
condition|)
name|transactionID
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|IoSession
name|getSession
parameter_list|()
block|{
return|return
name|session
return|;
block|}
specifier|protected
name|DebuggeeJoint
name|getJoint
parameter_list|()
block|{
if|if
condition|(
name|session
operator|==
literal|null
condition|)
return|return
literal|null
return|;
return|return
operator|(
name|DebuggeeJoint
operator|)
name|session
operator|.
name|getAttribute
argument_list|(
literal|"joint"
argument_list|)
return|;
block|}
specifier|public
name|byte
index|[]
name|errorBytes
parameter_list|(
name|String
name|commandName
parameter_list|)
block|{
return|return
name|errorBytes
argument_list|(
name|commandName
argument_list|,
name|Errors
operator|.
name|ERR_999
argument_list|,
name|Errors
operator|.
name|ERR_999_STR
argument_list|)
return|;
block|}
specifier|public
name|byte
index|[]
name|errorBytes
parameter_list|(
name|String
name|commandName
parameter_list|,
name|int
name|errorCode
parameter_list|,
name|String
name|errorMessage
parameter_list|)
block|{
name|String
name|response
init|=
literal|"<response "
operator|+
literal|"command=\""
operator|+
name|commandName
operator|+
literal|"\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\">"
operator|+
literal|"<error code=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|errorCode
argument_list|)
operator|+
literal|"\">"
operator|+
literal|"<message>"
operator|+
name|errorMessage
operator|+
literal|"</message>"
operator|+
literal|"</error>"
operator|+
literal|"</response>"
decl_stmt|;
return|return
name|response
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|public
specifier|abstract
name|void
name|exec
parameter_list|()
function_decl|;
specifier|public
name|void
name|toDebuggee
parameter_list|()
block|{
name|session
operator|.
name|write
argument_list|(
name|this
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Command
name|parse
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|String
name|message
parameter_list|)
block|{
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
literal|"get message = "
operator|+
name|message
argument_list|)
expr_stmt|;
name|int
name|pos
init|=
name|message
operator|.
name|indexOf
argument_list|(
literal|" "
argument_list|)
decl_stmt|;
name|String
name|command
init|=
name|message
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|pos
argument_list|)
decl_stmt|;
name|String
name|args
init|=
name|message
operator|.
name|substring
argument_list|(
name|command
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"run"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Run
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"step_into"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StepInto
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"step_over"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StepOver
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"step_out"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StepOut
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"stop"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Stop
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"stack_get"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StackGet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"property_get"
argument_list|)
condition|)
block|{
return|return
operator|new
name|PropertyGet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"context_get"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ContextGet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"context_names"
argument_list|)
condition|)
block|{
return|return
operator|new
name|ContextNames
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"breakpoint_set"
argument_list|)
condition|)
block|{
return|return
operator|new
name|BreakpointSet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"breakpoint_get"
argument_list|)
condition|)
block|{
return|return
operator|new
name|BreakpointGet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"breakpoint_update"
argument_list|)
condition|)
block|{
return|return
operator|new
name|BreakpointUpdate
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"breakpoint_remove"
argument_list|)
condition|)
block|{
return|return
operator|new
name|BreakpointRemove
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"breakpoint_list"
argument_list|)
condition|)
block|{
return|return
operator|new
name|BreakpointList
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"status"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Status
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"stdout"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StdOut
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"stderr"
argument_list|)
condition|)
block|{
return|return
operator|new
name|StdErr
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"source"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Source
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"feature_set"
argument_list|)
condition|)
block|{
return|return
operator|new
name|FeatureSet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"feature_get"
argument_list|)
condition|)
block|{
return|return
operator|new
name|FeatureGet
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
if|else if
condition|(
name|command
operator|.
name|equals
argument_list|(
literal|"eval"
argument_list|)
condition|)
block|{
return|return
operator|new
name|Eval
argument_list|(
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
return|return
operator|new
name|Error
argument_list|(
name|command
argument_list|,
name|session
argument_list|,
name|args
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|getFileuri
parameter_list|(
name|XACMLSource
name|fileuri
parameter_list|)
block|{
comment|//		System.out.println("getFileuri dbgp:"+fileuri.getType()+"://"+fileuri.getKey());
if|if
condition|(
name|fileuri
operator|.
name|getType
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|.
name|equals
argument_list|(
literal|"file"
argument_list|)
condition|)
return|return
literal|"file://"
operator|+
name|fileuri
operator|.
name|getKey
argument_list|()
return|;
else|else
return|return
literal|"dbgp://"
operator|+
name|fileuri
operator|.
name|getType
argument_list|()
operator|.
name|toLowerCase
argument_list|()
operator|+
name|fileuri
operator|.
name|getKey
argument_list|()
return|;
comment|//		return "http://localhost:8080/eXist/admin/admin.xql";
block|}
block|}
end_class

end_unit

