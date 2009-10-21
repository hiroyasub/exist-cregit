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
name|java
operator|.
name|util
operator|.
name|Map
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
name|dom
operator|.
name|QName
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
name|Variable
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
name|XQueryContext
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ContextGet
extends|extends
name|Command
block|{
comment|/** 	 * stack depth (optional) 	 */
specifier|private
name|Integer
name|stackDepth
init|=
literal|null
decl_stmt|;
comment|/** 	 * context id (optional, retrieved by context-names) 	 */
specifier|private
name|String
name|contextID
init|=
literal|""
decl_stmt|;
specifier|private
name|Map
argument_list|<
name|QName
argument_list|,
name|Variable
argument_list|>
name|variables
decl_stmt|;
specifier|public
name|ContextGet
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
block|{
name|stackDepth
operator|=
name|Integer
operator|.
name|parseInt
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
literal|"c"
argument_list|)
condition|)
block|{
name|contextID
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
comment|/* (non-Javadoc) 	 * @see org.exist.debuggee.dgbp.packets.Command#exec() 	 */
annotation|@
name|Override
specifier|public
name|void
name|exec
parameter_list|()
block|{
comment|//TODO: different stack depth& context id
name|variables
operator|=
name|getJoint
argument_list|()
operator|.
name|getVariables
argument_list|()
expr_stmt|;
block|}
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
literal|"command=\"context_get\" "
operator|+
literal|"context=\""
operator|+
name|contextID
operator|+
literal|"\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\"> "
operator|+
name|getPropertiesString
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
name|getPropertiesString
parameter_list|()
block|{
name|String
name|properties
init|=
literal|""
decl_stmt|;
if|if
condition|(
name|variables
operator|==
literal|null
condition|)
return|return
name|properties
return|;
comment|//XXX: error?
name|XQueryContext
name|ctx
init|=
name|getJoint
argument_list|()
operator|.
name|getContext
argument_list|()
decl_stmt|;
for|for
control|(
name|Variable
name|variable
range|:
name|variables
operator|.
name|values
argument_list|()
control|)
block|{
name|properties
operator|+=
name|PropertyGet
operator|.
name|getPropertyString
argument_list|(
name|variable
argument_list|,
name|ctx
argument_list|)
expr_stmt|;
block|}
return|return
name|properties
return|;
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
literal|"context_get -i "
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
if|if
condition|(
name|contextID
operator|!=
literal|null
operator|&&
name|contextID
operator|.
name|equals
argument_list|(
literal|""
argument_list|)
condition|)
name|command
operator|+=
literal|" -c "
operator|+
name|String
operator|.
name|valueOf
argument_list|(
name|contextID
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

