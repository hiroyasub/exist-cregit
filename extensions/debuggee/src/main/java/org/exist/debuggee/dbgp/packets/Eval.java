begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|io
operator|.
name|IOException
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
name|util
operator|.
name|Base64Encoder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayOutputStream
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|Eval
extends|extends
name|Command
block|{
name|String
name|script
decl_stmt|;
name|String
name|result
decl_stmt|;
name|Exception
name|exception
decl_stmt|;
specifier|public
name|Eval
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
name|script
operator|=
literal|null
expr_stmt|;
name|result
operator|=
literal|null
expr_stmt|;
name|exception
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
literal|"-"
argument_list|)
condition|)
block|{
name|script
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
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
name|byte
index|[]
name|response
decl_stmt|;
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
name|response
operator|=
name|errorBytes
argument_list|(
literal|"eval"
argument_list|,
name|Errors
operator|.
name|ERR_206
argument_list|,
name|exception
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|String
name|head
init|=
name|xml_declaration
operator|+
literal|"<response "
operator|+
name|namespaces
operator|+
literal|"command=\"eval\" "
operator|+
literal|"success=\""
operator|+
name|isSuccess
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\">"
operator|+
literal|"<property>"
decl_stmt|;
name|String
name|tail
init|=
literal|"</property>"
operator|+
literal|"</response>"
decl_stmt|;
name|Base64Encoder
name|enc
init|=
operator|new
name|Base64Encoder
argument_list|()
decl_stmt|;
name|enc
operator|.
name|translate
argument_list|(
name|result
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|FastByteArrayOutputStream
name|baos
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|(
name|head
operator|.
name|length
argument_list|()
operator|+
operator|(
operator|(
name|result
operator|.
name|length
argument_list|()
operator|/
literal|100
operator|)
operator|*
literal|33
operator|)
operator|+
name|tail
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
try|try
block|{
name|baos
operator|.
name|write
argument_list|(
name|head
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
operator|new
name|String
argument_list|(
name|enc
operator|.
name|getCharArray
argument_list|()
argument_list|)
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|baos
operator|.
name|write
argument_list|(
name|tail
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
block|}
name|response
operator|=
name|baos
operator|.
name|toByteArray
argument_list|()
expr_stmt|;
block|}
return|return
name|response
return|;
block|}
specifier|private
name|String
name|isSuccess
parameter_list|()
block|{
if|if
condition|(
name|result
operator|!=
literal|null
condition|)
return|return
literal|"1"
return|;
return|return
literal|"0"
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
try|try
block|{
name|result
operator|=
name|getJoint
argument_list|()
operator|.
name|evalution
argument_list|(
name|script
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
specifier|public
name|byte
index|[]
name|commandBytes
parameter_list|()
block|{
name|String
name|command
init|=
literal|"eval -i "
operator|+
name|transactionID
operator|+
literal|" -- "
operator|+
name|script
decl_stmt|;
return|return
name|command
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|public
name|void
name|setScript
parameter_list|(
name|String
name|script
parameter_list|)
block|{
name|this
operator|.
name|script
operator|=
name|script
expr_stmt|;
block|}
block|}
end_class

end_unit

