begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/* eXist xml document repository and xpath implementation  * Copyright (C) 2000-2014,  Wolfgang Meier (meier@ifs.tu-darmstadt.de)  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU General Public License for more details.  *  * You should have received a copy of the GNU General Public License  * along with this program; if not, write to the Free Software  * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|dom
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
name|Node
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
name|NodeList
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

begin_class
specifier|public
class|class
name|NodeListImpl
extends|extends
name|ArrayList
argument_list|<
name|Node
argument_list|>
implements|implements
name|NodeList
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
literal|5505309345079983721L
decl_stmt|;
specifier|public
name|NodeListImpl
parameter_list|()
block|{
name|super
argument_list|()
expr_stmt|;
block|}
specifier|public
name|NodeListImpl
parameter_list|(
specifier|final
name|int
name|initialCapacity
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|add
parameter_list|(
specifier|final
name|Node
name|node
parameter_list|)
block|{
if|if
condition|(
name|node
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|super
operator|.
name|add
argument_list|(
name|node
argument_list|)
return|;
block|}
comment|/**      * Add all elements of the other NodeList to      * this NodeList      * @param other NodeList to add      * @return true if all elements were added, false      *   if none or only some were added.      */
specifier|public
name|boolean
name|addAll
parameter_list|(
specifier|final
name|NodeList
name|other
parameter_list|)
block|{
if|if
condition|(
name|other
operator|.
name|getLength
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
literal|false
return|;
block|}
else|else
block|{
name|boolean
name|result
init|=
literal|true
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|other
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
operator|!
name|add
argument_list|(
name|other
operator|.
name|item
argument_list|(
name|i
argument_list|)
argument_list|)
condition|)
block|{
name|result
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
return|return
name|result
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|getLength
parameter_list|()
block|{
return|return
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|Node
name|item
parameter_list|(
specifier|final
name|int
name|pos
parameter_list|)
block|{
if|if
condition|(
name|pos
operator|>=
name|size
argument_list|()
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
name|get
argument_list|(
name|pos
argument_list|)
return|;
block|}
block|}
end_class

end_unit

