begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
name|storage
operator|.
name|BrokerPool
import|;
end_import

begin_class
specifier|public
class|class
name|VarBinding
extends|extends
name|Step
block|{
specifier|protected
name|String
name|name
decl_stmt|;
specifier|protected
name|Expression
name|binding
init|=
literal|null
decl_stmt|;
specifier|public
name|VarBinding
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|name
parameter_list|,
name|Expression
name|binding
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|binding
operator|=
name|binding
expr_stmt|;
block|}
specifier|public
name|VarBinding
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|0
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
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
name|binding
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"variable "
operator|+
name|name
operator|+
literal|" unbound"
argument_list|)
throw|;
return|return
name|binding
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
return|;
block|}
specifier|public
name|String
name|pprint
parameter_list|()
block|{
return|return
literal|" $"
operator|+
name|name
return|;
block|}
block|}
end_class

end_unit

