begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2000-01,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Library General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Library General Public License for more details.  *  * You should have received a copy of the GNU Library General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
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
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|*
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|ValueSet
extends|extends
name|Value
block|{
specifier|protected
name|ArrayList
name|list
init|=
operator|new
name|ArrayList
argument_list|()
decl_stmt|;
specifier|public
name|ValueSet
parameter_list|()
block|{
name|super
argument_list|(
name|Value
operator|.
name|isValueSet
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ValueSet
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
name|super
argument_list|(
name|Value
operator|.
name|isValueSet
argument_list|)
expr_stmt|;
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getType
parameter_list|()
block|{
return|return
name|type
return|;
block|}
specifier|public
name|void
name|add
parameter_list|(
name|Value
name|value
parameter_list|)
block|{
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|==
name|Value
operator|.
name|isValueSet
condition|)
block|{
for|for
control|(
name|Iterator
name|i
init|=
operator|(
operator|(
name|ValueSet
operator|)
name|value
operator|)
operator|.
name|iterator
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
name|list
operator|.
name|add
argument_list|(
name|i
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
name|list
operator|.
name|add
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Value
name|get
parameter_list|(
name|int
name|pos
parameter_list|)
block|{
return|return
operator|(
name|Value
operator|)
name|list
operator|.
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|list
operator|.
name|size
argument_list|()
return|;
block|}
specifier|public
name|NodeList
name|getNodeList
parameter_list|()
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
return|return
operator|new
name|ArraySet
argument_list|(
literal|1
argument_list|)
return|;
return|return
operator|(
operator|(
name|Value
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNodeList
argument_list|()
return|;
block|}
specifier|public
name|String
name|getStringValue
parameter_list|()
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
return|return
literal|""
return|;
return|return
operator|(
operator|(
name|Value
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getStringValue
argument_list|()
return|;
block|}
specifier|public
name|double
name|getNumericValue
parameter_list|()
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
return|return
name|Double
operator|.
name|NaN
return|;
return|return
operator|(
operator|(
name|Value
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getNumericValue
argument_list|()
return|;
block|}
specifier|public
name|boolean
name|getBooleanValue
parameter_list|()
block|{
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|<
literal|1
condition|)
return|return
literal|false
return|;
return|return
operator|(
operator|(
name|Value
operator|)
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getBooleanValue
argument_list|()
return|;
block|}
specifier|public
name|ValueSet
name|getValueSet
parameter_list|()
block|{
return|return
name|this
return|;
block|}
specifier|public
name|Iterator
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|ValueSetIterator
argument_list|()
return|;
block|}
specifier|public
class|class
name|ValueSetIterator
implements|implements
name|Iterator
block|{
specifier|protected
name|int
name|pos
init|=
literal|0
decl_stmt|;
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
operator|(
name|pos
operator|<
name|list
operator|.
name|size
argument_list|()
operator|)
condition|?
literal|true
else|:
literal|false
return|;
block|}
specifier|public
name|Object
name|next
parameter_list|()
block|{
return|return
name|hasNext
argument_list|()
condition|?
name|list
operator|.
name|get
argument_list|(
name|pos
operator|++
argument_list|)
else|:
literal|null
return|;
block|}
specifier|public
name|void
name|remove
parameter_list|()
block|{
block|}
block|}
block|}
end_class

end_unit

