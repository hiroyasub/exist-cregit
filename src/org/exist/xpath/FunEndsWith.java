begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist Native XML Database  * Copyright (C) 2000-03,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  * This library is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This library is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   * $Id$  */
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
name|xpath
operator|.
name|value
operator|.
name|BooleanValue
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Item
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xpath
operator|.
name|value
operator|.
name|Type
import|;
end_import

begin_class
specifier|public
class|class
name|FunEndsWith
extends|extends
name|Function
block|{
specifier|public
name|FunEndsWith
parameter_list|()
block|{
name|super
argument_list|(
literal|"ends-with"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|returnsType
parameter_list|()
block|{
return|return
name|Type
operator|.
name|STRING
return|;
block|}
specifier|public
name|Sequence
name|eval
parameter_list|(
name|StaticContext
name|context
parameter_list|,
name|DocumentSet
name|docs
parameter_list|,
name|Sequence
name|contextSequence
parameter_list|,
name|Item
name|contextItem
parameter_list|)
throws|throws
name|XPathException
block|{
if|if
condition|(
name|getArgumentCount
argument_list|()
operator|!=
literal|2
condition|)
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"ends-with expects two arguments"
argument_list|)
throw|;
if|if
condition|(
name|contextItem
operator|!=
literal|null
condition|)
name|contextSequence
operator|=
name|contextItem
operator|.
name|toSequence
argument_list|()
expr_stmt|;
name|String
name|s1
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
name|contextSequence
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
name|String
name|s2
init|=
name|getArgument
argument_list|(
literal|1
argument_list|)
operator|.
name|eval
argument_list|(
name|context
argument_list|,
name|docs
argument_list|,
name|contextSequence
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
name|BooleanValue
argument_list|(
literal|true
argument_list|)
return|;
else|else
return|return
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
return|;
block|}
block|}
end_class

end_unit

