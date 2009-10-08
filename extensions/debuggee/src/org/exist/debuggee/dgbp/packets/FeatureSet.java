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

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|FeatureSet
extends|extends
name|Command
block|{
name|String
name|name
decl_stmt|;
name|String
name|value
decl_stmt|;
name|boolean
name|success
init|=
literal|false
decl_stmt|;
specifier|public
name|FeatureSet
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
literal|"n"
argument_list|)
condition|)
block|{
name|name
operator|=
name|val
expr_stmt|;
block|}
if|else if
condition|(
name|arg
operator|.
name|equals
argument_list|(
literal|"v"
argument_list|)
condition|)
block|{
name|value
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
name|int
name|getLength
parameter_list|()
block|{
return|return
name|responseBytes
argument_list|()
operator|.
name|length
return|;
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
literal|"command=\"feature_set\" "
operator|+
literal|"feature=\""
operator|+
name|name
operator|+
literal|"\" "
operator|+
literal|"success=\""
operator|+
name|getStringStatus
argument_list|()
operator|+
literal|"\" "
operator|+
literal|"transaction_id=\""
operator|+
name|transactionID
operator|+
literal|"\"/>"
decl_stmt|;
return|return
name|response
operator|.
name|getBytes
argument_list|()
return|;
block|}
specifier|public
name|String
name|getStringStatus
parameter_list|()
block|{
if|if
condition|(
name|success
condition|)
return|return
literal|"1"
return|;
else|else
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
name|success
operator|=
name|getJoint
argument_list|()
operator|.
name|featureSet
argument_list|(
name|name
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

