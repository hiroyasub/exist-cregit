begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000-01,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id:  */
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
name|ArraySet
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
name|Attr
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
name|Element
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
comment|/**  * xpath-library function: string(object)  *  */
end_comment

begin_class
specifier|public
class|class
name|FunName
extends|extends
name|Function
block|{
specifier|public
name|FunName
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"name"
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
name|DocumentSet
name|docs
parameter_list|,
name|NodeSet
name|context
parameter_list|,
name|NodeProxy
name|node
parameter_list|)
block|{
name|Node
name|n
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"check: "
operator|+
name|node
operator|.
name|gid
argument_list|)
expr_stmt|;
name|ArraySet
name|set
init|=
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|set
operator|.
name|add
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|DocumentSet
name|dset
init|=
operator|new
name|DocumentSet
argument_list|()
decl_stmt|;
name|dset
operator|.
name|add
argument_list|(
name|node
operator|.
name|doc
argument_list|)
expr_stmt|;
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
name|dset
argument_list|,
name|set
argument_list|,
literal|null
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
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
else|else
name|n
operator|=
name|node
operator|.
name|getNode
argument_list|()
expr_stmt|;
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
operator|(
operator|(
name|Element
operator|)
name|n
operator|)
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
operator|(
operator|(
name|Attr
operator|)
name|n
operator|)
operator|.
name|getName
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
specifier|public
name|String
name|pprint
parameter_list|()
block|{
name|StringBuffer
name|buf
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|"name("
argument_list|)
expr_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|0
condition|)
name|buf
operator|.
name|append
argument_list|(
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
return|return
name|buf
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

