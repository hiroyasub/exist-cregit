begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-03 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   *  $Id$  */
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
name|QName
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

begin_comment
comment|/**  * An XQuery/XPath variable, consisting of a QName and a value.  *   * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|Variable
block|{
specifier|private
name|QName
name|qname
decl_stmt|;
specifier|private
name|Sequence
name|value
init|=
literal|null
decl_stmt|;
specifier|private
name|int
name|positionInStack
init|=
literal|0
decl_stmt|;
specifier|private
name|int
name|cardinality
init|=
name|Cardinality
operator|.
name|ZERO_OR_MORE
decl_stmt|;
comment|/** 	 *  	 */
specifier|public
name|Variable
parameter_list|(
name|QName
name|qname
parameter_list|)
block|{
name|this
operator|.
name|qname
operator|=
name|qname
expr_stmt|;
block|}
specifier|public
name|void
name|setValue
parameter_list|(
name|Sequence
name|value
parameter_list|)
block|{
name|this
operator|.
name|value
operator|=
name|value
expr_stmt|;
block|}
specifier|public
name|Sequence
name|getValue
parameter_list|()
block|{
return|return
name|value
return|;
block|}
specifier|public
name|QName
name|getQName
parameter_list|()
block|{
return|return
name|qname
return|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"$"
operator|+
name|qname
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|public
name|int
name|getDependencies
parameter_list|(
name|XQueryContext
name|context
parameter_list|)
block|{
if|if
condition|(
name|context
operator|.
name|getCurrentStackSize
argument_list|()
operator|>
name|positionInStack
condition|)
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|GLOBAL_VARS
return|;
else|else
return|return
name|Dependency
operator|.
name|CONTEXT_SET
operator|+
name|Dependency
operator|.
name|LOCAL_VARS
return|;
block|}
specifier|public
name|int
name|getCardinality
parameter_list|()
block|{
return|return
name|cardinality
return|;
block|}
specifier|public
name|void
name|setCardinality
parameter_list|(
name|int
name|card
parameter_list|)
block|{
name|this
operator|.
name|cardinality
operator|=
name|card
expr_stmt|;
block|}
specifier|public
name|void
name|setStackPosition
parameter_list|(
name|int
name|position
parameter_list|)
block|{
name|positionInStack
operator|=
name|position
expr_stmt|;
block|}
block|}
end_class

end_unit

