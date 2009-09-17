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
specifier|public
name|StackGet
parameter_list|(
name|String
name|args
parameter_list|)
block|{
name|super
argument_list|(
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
literal|"\">"
operator|+
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
literal|"filename=\"file:///home/dmitriy/projects/eXist-svn/trunk/eXist/webapp/admin/admin.xql\" "
operator|+
literal|"lineno=\"5\" "
operator|+
literal|"where=\"\" "
operator|+
literal|"cmdbegin=\"5:5\" "
operator|+
literal|"cmdend=\"5:10\"/>"
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
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
comment|// TODO Auto-generated method stub
block|}
block|}
end_class

end_unit

