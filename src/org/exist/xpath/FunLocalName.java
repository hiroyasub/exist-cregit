begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xpath
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|DocumentSet
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
name|NodeProxy
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
name|NodeSet
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
name|SingleNodeSet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * xpath-library function: local-name(object)  *  */
end_comment

begin_class
specifier|public
class|class
name|FunLocalName
extends|extends
name|Function
block|{
specifier|public
name|FunLocalName
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"local-name"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Constants
operator|.
name|TYPE_STRING
return|;
block|}
specifier|public
name|Value
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|contextSet
parameter_list|,
name|NodeProxy
name|contextNode
parameter_list|)
block|{
name|Node
name|n
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|contextNode
operator|!=
literal|null
condition|)
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|NodeSet
name|result
init|=
operator|(
name|NodeSet
operator|)
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
if|if
condition|(
name|result
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
name|n
operator|=
name|result
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|contextSet
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
name|n
operator|=
name|contextSet
operator|.
name|item
argument_list|(
literal|0
argument_list|)
expr_stmt|;
else|else
return|return
operator|new
name|ValueString
argument_list|(
literal|""
argument_list|)
return|;
if|if
condition|(
name|n
operator|!=
literal|null
condition|)
block|{
switch|switch
condition|(
name|n
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
return|return
operator|new
name|ValueString
argument_list|(
name|n
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
return|return
operator|new
name|ValueString
argument_list|(
name|n
operator|.
name|getLocalName
argument_list|()
argument_list|)
return|;
default|default:
return|return
operator|new
name|ValueString
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
return|return
operator|new
name|ValueString
argument_list|(
literal|""
argument_list|)
return|;
block|}
block|}
end_class

end_unit

