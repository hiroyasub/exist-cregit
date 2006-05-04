begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-06 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_comment
comment|/**  * @author Wolfgang Meier (wolfgang@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DescendantSelector
implements|implements
name|NodeSelector
block|{
specifier|final
specifier|protected
name|NodeSet
name|context
decl_stmt|;
specifier|final
specifier|protected
name|int
name|contextId
decl_stmt|;
specifier|public
name|DescendantSelector
parameter_list|(
name|NodeSet
name|contextSet
parameter_list|,
name|int
name|contextId
parameter_list|)
block|{
name|this
operator|.
name|context
operator|=
name|contextSet
expr_stmt|;
name|this
operator|.
name|contextId
operator|=
name|contextId
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.xquery.NodeSelector#match(org.exist.dom.NodeProxy) 	 */
specifier|public
name|NodeProxy
name|match
parameter_list|(
name|NodeProxy
name|proxy
parameter_list|)
block|{
switch|switch
condition|(
name|proxy
operator|.
name|getNodeType
argument_list|()
condition|)
block|{
case|case
name|NodeProxy
operator|.
name|UNKNOWN_NODE_TYPE
case|:
break|break;
case|case
name|Node
operator|.
name|ELEMENT_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|ATTRIBUTE_NODE
case|:
break|break;
case|case
name|Node
operator|.
name|TEXT_NODE
case|:
return|return
literal|null
return|;
case|case
name|Node
operator|.
name|PROCESSING_INSTRUCTION_NODE
case|:
return|return
literal|null
return|;
case|case
name|Node
operator|.
name|COMMENT_NODE
case|:
return|return
literal|null
return|;
case|case
name|Node
operator|.
name|DOCUMENT_NODE
case|:
break|break;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Unknown node type"
argument_list|)
throw|;
block|}
comment|//TODO : help proxy by adding a node type ?
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|proxy
operator|.
name|getDocument
argument_list|()
argument_list|,
name|proxy
operator|.
name|getGID
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|p
operator|==
literal|null
condition|)
return|return
literal|null
return|;
name|NodeProxy
name|contextNode
init|=
name|context
operator|.
name|parentWithChild
argument_list|(
name|proxy
operator|.
name|getDocument
argument_list|()
argument_list|,
name|proxy
operator|.
name|getGID
argument_list|()
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|,
name|NodeProxy
operator|.
name|UNKNOWN_NODE_LEVEL
argument_list|)
decl_stmt|;
if|if
condition|(
name|contextNode
operator|==
literal|null
condition|)
return|return
literal|null
return|;
if|if
condition|(
name|Expression
operator|.
name|NO_CONTEXT_ID
operator|!=
name|contextId
condition|)
block|{
name|p
operator|.
name|deepCopyContext
argument_list|(
name|contextNode
argument_list|,
name|contextId
argument_list|)
expr_stmt|;
block|}
else|else
name|p
operator|.
name|copyContext
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
return|return
name|p
return|;
block|}
block|}
end_class

end_unit

