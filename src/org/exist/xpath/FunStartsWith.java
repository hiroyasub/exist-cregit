begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Open Source Native XML Database  * Copyright (C) 2000,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id:  */
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

begin_class
specifier|public
class|class
name|FunStartsWith
extends|extends
name|Function
block|{
specifier|protected
name|Expression
name|arg1
decl_stmt|,
name|arg2
decl_stmt|;
specifier|public
name|FunStartsWith
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|Expression
name|arg1
parameter_list|,
name|Expression
name|arg2
parameter_list|)
block|{
name|super
argument_list|(
name|pool
argument_list|,
literal|"starts-with"
argument_list|)
expr_stmt|;
name|this
operator|.
name|arg1
operator|=
name|arg1
expr_stmt|;
name|this
operator|.
name|arg2
operator|=
name|arg2
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
name|TYPE_BOOL
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
name|String
name|s1
init|=
name|arg1
operator|.
name|eval
argument_list|(
name|dset
argument_list|,
name|set
argument_list|,
name|node
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|arg2
operator|.
name|eval
argument_list|(
name|dset
argument_list|,
name|set
argument_list|,
name|node
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
if|if
condition|(
name|s1
operator|.
name|startsWith
argument_list|(
name|s2
argument_list|)
condition|)
return|return
operator|new
name|ValueBoolean
argument_list|(
literal|true
argument_list|)
return|;
else|else
return|return
operator|new
name|ValueBoolean
argument_list|(
literal|false
argument_list|)
return|;
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
literal|"starts-with("
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|arg1
operator|.
name|pprint
argument_list|()
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
literal|", "
argument_list|)
expr_stmt|;
name|buf
operator|.
name|append
argument_list|(
name|arg2
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

