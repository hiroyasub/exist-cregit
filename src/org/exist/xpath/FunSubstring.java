begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * NativeBroker.java - eXist Open Source Native XML Database  * Copyright (C) 2001 Wolfgang M. Meier  * meier@ifs.tu-darmstadt.de  * http://exist.sourceforge.net  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  */
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

begin_comment
comment|/**  * xpath-library function: string(object)  *  */
end_comment

begin_class
specifier|public
class|class
name|FunSubstring
extends|extends
name|Function
block|{
specifier|public
name|FunSubstring
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"substring"
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
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|<
literal|2
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"substring requires at least two arguments"
argument_list|)
throw|;
name|Expression
name|arg0
init|=
name|getArgument
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|Expression
name|arg1
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
decl_stmt|;
name|Expression
name|arg2
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|>
literal|2
condition|)
name|arg2
operator|=
name|getArgument
argument_list|(
literal|2
argument_list|)
expr_stmt|;
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
name|int
name|start
init|=
operator|(
name|int
operator|)
name|arg1
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
name|getNumericValue
argument_list|()
decl_stmt|;
name|int
name|length
init|=
literal|1
decl_stmt|;
if|if
condition|(
name|arg2
operator|!=
literal|null
condition|)
name|length
operator|=
operator|(
name|int
operator|)
name|arg2
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
name|getNumericValue
argument_list|()
expr_stmt|;
if|if
condition|(
name|start
operator|<=
literal|0
operator|||
name|length
operator|<
literal|0
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Illegal start or length argument"
argument_list|)
throw|;
name|Value
name|nodes
init|=
name|arg0
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSet
argument_list|)
decl_stmt|;
name|String
name|result
init|=
name|nodes
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|start
operator|<
literal|0
operator|||
operator|--
name|start
operator|+
name|length
operator|>=
name|result
operator|.
name|length
argument_list|()
condition|)
return|return
operator|new
name|ValueString
argument_list|(
literal|""
argument_list|)
return|;
return|return
operator|new
name|ValueString
argument_list|(
operator|(
name|length
operator|>
literal|0
operator|)
condition|?
name|result
operator|.
name|substring
argument_list|(
name|start
argument_list|,
name|start
operator|+
name|length
argument_list|)
else|:
name|result
operator|.
name|substring
argument_list|(
name|start
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

