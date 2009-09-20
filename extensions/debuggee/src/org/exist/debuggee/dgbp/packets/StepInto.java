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
name|exist
operator|.
name|debuggee
operator|.
name|DebuggeeJoint
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|StepInto
extends|extends
name|Command
block|{
specifier|private
name|String
name|status
init|=
literal|null
decl_stmt|;
specifier|public
name|StepInto
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
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|void
name|exec
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"StepInto.exec thread = "
operator|+
name|Thread
operator|.
name|currentThread
argument_list|()
argument_list|)
expr_stmt|;
name|status
operator|=
name|joint
operator|.
name|stepInto
argument_list|()
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#toBytes() 	 */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|byte
index|[]
name|toBytes
parameter_list|()
block|{
name|String
name|responce
init|=
literal|"<response "
operator|+
literal|"command=\"step_into\" "
operator|+
literal|"status=\""
operator|+
name|status
operator|+
literal|"\" "
operator|+
literal|"reason=\"ok\" "
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
block|}
end_class

end_unit

