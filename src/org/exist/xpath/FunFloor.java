begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2001-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id:  */
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

begin_class
specifier|public
class|class
name|FunFloor
extends|extends
name|Function
block|{
specifier|public
name|FunFloor
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"floor"
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
name|contextSet
operator|=
operator|new
name|SingleNodeSet
argument_list|(
name|contextNode
argument_list|)
expr_stmt|;
name|double
name|val
decl_stmt|;
comment|// Argument is a node list
if|if
condition|(
name|getArgument
argument_list|(
literal|0
argument_list|)
operator|.
name|returnsType
argument_list|()
operator|==
name|Constants
operator|.
name|TYPE_NODELIST
condition|)
block|{
name|ValueSet
name|values
init|=
operator|new
name|ValueSet
argument_list|()
decl_stmt|;
name|NodeSet
name|args
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
argument_list|,
name|contextNode
argument_list|)
operator|.
name|getNodeList
argument_list|()
decl_stmt|;
if|if
condition|(
name|args
operator|.
name|getLength
argument_list|()
operator|>
literal|0
condition|)
block|{
try|try
block|{
name|val
operator|=
name|Double
operator|.
name|parseDouble
argument_list|(
name|args
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|ValueNumber
argument_list|(
name|Math
operator|.
name|floor
argument_list|(
name|val
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|NumberFormatException
name|nfe
parameter_list|)
block|{
block|}
block|}
return|return
name|values
return|;
block|}
else|else
block|{
comment|// does argument return a value set?
name|Value
name|v
init|=
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
argument_list|,
name|contextNode
argument_list|)
decl_stmt|;
return|return
operator|new
name|ValueNumber
argument_list|(
name|Math
operator|.
name|ceil
argument_list|(
name|v
operator|.
name|getNumericValue
argument_list|()
argument_list|)
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
literal|"floor("
argument_list|)
expr_stmt|;
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
literal|')'
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

