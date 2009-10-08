begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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

begin_comment
comment|/**  * @author<a href="mailto:wolfgang@exist-db.org">Wolfgang Meier</a>  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ContextNames
extends|extends
name|Command
block|{
specifier|private
name|Integer
name|stackDepth
init|=
literal|null
decl_stmt|;
specifier|public
name|ContextNames
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
name|stackDepth
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|val
argument_list|)
expr_stmt|;
else|else
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
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
name|String
name|response
init|=
literal|"<response "
operator|+
literal|"command=\"context_names\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\">"
operator|+
literal|"<context name=\"Local\" id=\"0\"/>"
operator|+
literal|"<context name=\"Global\" id=\"1\"/>"
operator|+
literal|"<context name=\"Class\" id=\"2\"/>"
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
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|commandBytes
parameter_list|()
block|{
name|String
name|command
init|=
literal|"context_names -i "
operator|+
name|transactionID
decl_stmt|;
if|if
condition|(
name|stackDepth
operator|!=
literal|null
condition|)
name|command
operator|+=
literal|" -d "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|stackDepth
argument_list|)
expr_stmt|;
return|return
name|command
operator|.
name|getBytes
argument_list|()
return|;
block|}
block|}
end_class

end_unit

