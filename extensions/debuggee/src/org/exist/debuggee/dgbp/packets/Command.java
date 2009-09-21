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
name|dgbp
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
name|dgbp
operator|.
name|DGBPPacket
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
extends|extends
name|DGBPPacket
block|{
specifier|protected
name|DebuggeeJoint
name|joint
decl_stmt|;
comment|/** 	 * Unique numerical ID for each command generated by the IDE 	 */
specifier|protected
name|String
name|transactionID
decl_stmt|;
specifier|public
name|Command
parameter_list|(
name|DebuggeeJoint
name|joint
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|this
operator|.
name|joint
operator|=
name|joint
expr_stmt|;
name|String
index|[]
name|splited
init|=
name|args
operator|.
name|split
argument_list|(
literal|"-"
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
operator|new
name|String
argument_list|(
name|toBytes
argument_list|()
argument_list|)
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
literal|"i"
argument_list|)
condition|)
name|transactionID
operator|=
name|val
expr_stmt|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|toBytes
argument_list|()
operator|.
name|length
return|;
block|}
specifier|public
name|byte
index|[]
name|toBytes
parameter_list|()
block|{
name|String
name|response
init|=
literal|"<response "
operator|+
literal|"command=\"command_name\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\">"
operator|+
literal|"<error code=\"error_code\" apperr=\"app_specific_error_code\">"
operator|+
literal|"<message>UI Usable Message</message>"
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
throws|throws
name|ParsingCommandException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" parsig - "
operator|+
name|message
argument_list|)
expr_stmt|;
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
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
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
name|joint
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
name|joint
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
name|joint
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
name|joint
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
name|joint
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
name|joint
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
name|joint
argument_list|,
name|args
argument_list|)
return|;
block|}
throw|throw
operator|new
name|ParsingCommandException
argument_list|(
name|command
argument_list|)
throw|;
block|}
specifier|protected
specifier|static
name|String
name|getFileuri
parameter_list|(
name|XACMLSource
name|fileuri
parameter_list|)
block|{
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
literal|"dbgp:"
operator|+
name|fileuri
operator|.
name|getType
argument_list|()
operator|+
literal|"://"
operator|+
name|fileuri
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
end_class

end_unit

