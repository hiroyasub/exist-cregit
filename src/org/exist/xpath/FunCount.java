begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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

begin_class
specifier|public
class|class
name|FunCount
extends|extends
name|Function
block|{
specifier|public
name|FunCount
parameter_list|()
block|{
name|super
argument_list|(
literal|"count"
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
name|TYPE_NUM
return|;
block|}
specifier|public
name|DocumentSet
name|preselect
parameter_list|(
name|DocumentSet
name|in_docs
parameter_list|,
name|StaticContext
name|context
parameter_list|)
throws|throws
name|XPathException
block|{
return|return
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|preselect
argument_list|(
name|in_docs
argument_list|,
name|context
argument_list|)
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
throws|throws
name|XPathException
block|{
if|if
condition|(
name|contextNode
operator|!=
literal|null
condition|)
block|{
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
block|}
name|NodeSet
name|temp
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
return|return
operator|new
name|ValueNumber
argument_list|(
name|temp
operator|.
name|getLength
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

