begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|xquery
operator|.
name|Expression
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|StackGet
extends|extends
name|Command
block|{
specifier|private
name|Integer
name|stackDepth
init|=
literal|null
decl_stmt|;
specifier|private
name|List
argument_list|<
name|Expression
argument_list|>
name|stacks
decl_stmt|;
specifier|public
name|StackGet
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
specifier|public
name|byte
index|[]
name|responseBytes
parameter_list|()
block|{
name|StringBuilder
name|response
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|response
operator|.
name|append
argument_list|(
name|xml_declaration
argument_list|)
expr_stmt|;
name|response
operator|.
name|append
argument_list|(
literal|"<response "
operator|+
name|namespaces
operator|+
literal|"command=\"stack_get\" transaction_id=\""
argument_list|)
expr_stmt|;
name|response
operator|.
name|append
argument_list|(
name|transactionID
argument_list|)
expr_stmt|;
name|response
operator|.
name|append
argument_list|(
literal|"\">\n"
argument_list|)
expr_stmt|;
if|if
condition|(
name|stackDepth
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|stacks
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|-
name|stackDepth
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|stacks
operator|.
name|size
argument_list|()
condition|)
name|response
operator|.
name|append
argument_list|(
name|stackToString
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|index
init|=
name|stacks
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|index
operator|>=
literal|0
condition|;
name|index
operator|--
control|)
name|response
operator|.
name|append
argument_list|(
name|stackToString
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|append
argument_list|(
literal|"</response>"
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|toString
argument_list|()
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|private
name|StringBuilder
name|stackToString
parameter_list|(
name|int
name|index
parameter_list|)
block|{
name|StringBuilder
name|result
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
if|if
condition|(
name|stacks
operator|==
literal|null
operator|||
name|stacks
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
return|return
name|result
return|;
name|Expression
name|expr
init|=
name|stacks
operator|.
name|get
argument_list|(
name|index
argument_list|)
decl_stmt|;
name|int
name|level
init|=
name|stacks
operator|.
name|size
argument_list|()
operator|-
name|index
operator|-
literal|1
decl_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"<stack level=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|level
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" lineno=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|expr
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" type=\"file\" filename=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|getFileuri
argument_list|(
name|expr
operator|.
name|getSource
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\" "
argument_list|)
expr_stmt|;
comment|//					+
comment|//					"where=\"\" " +
name|result
operator|.
name|append
argument_list|(
literal|"cmdbegin=\""
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|expr
operator|.
name|getLine
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
name|expr
operator|.
name|getColumn
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|.
name|append
argument_list|(
literal|"\"  />"
argument_list|)
expr_stmt|;
comment|//					"cmdend=\""+(expr.getLine())+":"+(expr.getColumn()+1)+"\"/>";
return|return
name|result
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
name|stacks
operator|=
name|getJoint
argument_list|()
operator|.
name|stackGet
argument_list|()
expr_stmt|;
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
literal|"stack_get -i "
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
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|response
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|response
operator|.
name|append
argument_list|(
literal|"stack_get "
argument_list|)
expr_stmt|;
if|if
condition|(
name|stackDepth
operator|!=
literal|null
condition|)
block|{
name|int
name|index
init|=
name|stacks
operator|.
name|size
argument_list|()
operator|-
literal|1
operator|-
name|stackDepth
decl_stmt|;
if|if
condition|(
name|index
operator|>=
literal|0
operator|&&
name|index
operator|<
name|stacks
operator|.
name|size
argument_list|()
condition|)
name|response
operator|.
name|append
argument_list|(
name|stackToString
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
for|for
control|(
name|int
name|index
init|=
name|stacks
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|index
operator|>=
literal|0
condition|;
name|index
operator|--
control|)
name|response
operator|.
name|append
argument_list|(
name|stackToString
argument_list|(
name|index
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|response
operator|.
name|append
argument_list|(
literal|"["
operator|+
name|transactionID
operator|+
literal|"]"
argument_list|)
expr_stmt|;
return|return
name|response
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

