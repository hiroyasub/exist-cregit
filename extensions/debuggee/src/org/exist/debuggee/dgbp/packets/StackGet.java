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
name|DebuggeeJoint
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|PathExpr
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
name|int
name|stackDepth
init|=
literal|0
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
name|DebuggeeJoint
name|joint
parameter_list|,
name|String
name|args
parameter_list|)
block|{
name|super
argument_list|(
name|joint
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
name|toBytes
parameter_list|()
block|{
name|String
name|response
init|=
literal|""
operator|+
literal|"<response "
operator|+
literal|"command=\"stack_get\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\">\n"
operator|+
name|stackToString
argument_list|()
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
specifier|private
name|String
name|stackToString
parameter_list|()
block|{
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
literal|""
return|;
name|Expression
name|expr
init|=
name|stacks
operator|.
name|get
argument_list|(
literal|0
argument_list|)
decl_stmt|;
return|return
literal|"<stack level=\""
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|stackDepth
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"type=\"file\" "
operator|+
literal|"filename=\""
operator|+
name|getFileuri
argument_list|(
name|expr
operator|.
name|getSource
argument_list|()
argument_list|)
operator|+
literal|"\" "
operator|+
literal|"lineno=\""
operator|+
name|expr
operator|.
name|getLine
argument_list|()
operator|+
literal|"\" />"
return|;
comment|//					+
comment|//					"where=\"\" " +
comment|//					"cmdbegin=\""+expr.getLine()+":"+expr.getColumn()+"\" " +
comment|//					"cmdend=\""+(expr.getLine())+":"+(expr.getColumn()+1)+"\"/>";
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
name|joint
operator|.
name|stackGet
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

