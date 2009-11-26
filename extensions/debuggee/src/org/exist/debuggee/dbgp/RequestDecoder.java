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
name|buffer
operator|.
name|IoBuffer
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
name|CumulativeProtocolDecoder
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
name|ProtocolDecoderOutput
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
name|RequestDecoder
extends|extends
name|CumulativeProtocolDecoder
block|{
specifier|private
name|String
name|sCommand
init|=
literal|""
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|boolean
name|doDecode
parameter_list|(
name|IoSession
name|session
parameter_list|,
name|IoBuffer
name|in
parameter_list|,
name|ProtocolDecoderOutput
name|out
parameter_list|)
throws|throws
name|Exception
block|{
name|byte
name|b
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
if|if
condition|(
name|in
operator|.
name|remaining
argument_list|()
operator|>
literal|0
condition|)
block|{
name|b
operator|=
name|in
operator|.
name|get
argument_list|()
expr_stmt|;
if|if
condition|(
name|b
operator|==
operator|(
name|byte
operator|)
literal|0
condition|)
block|{
name|Command
name|command
init|=
name|Command
operator|.
name|parse
argument_list|(
name|session
argument_list|,
name|sCommand
argument_list|)
decl_stmt|;
name|command
operator|.
name|exec
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
operator|(
name|command
operator|instanceof
name|CommandContinuation
operator|)
condition|)
block|{
name|out
operator|.
name|write
argument_list|(
name|command
argument_list|)
expr_stmt|;
block|}
name|sCommand
operator|=
literal|""
expr_stmt|;
continue|continue;
block|}
name|sCommand
operator|+=
operator|(
name|char
operator|)
name|b
expr_stmt|;
block|}
else|else
block|{
return|return
literal|false
return|;
block|}
block|}
block|}
block|}
end_class

end_unit

