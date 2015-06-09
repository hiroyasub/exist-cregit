begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|function
package|;
end_package

begin_comment
comment|/**  * A tuple of two values  *  * @author Adam Retter<adam.retter@googlemail.com>  *  * @param<T1> The type of the first value  * @param<T2> The type of the second value  */
end_comment

begin_class
specifier|public
class|class
name|Tuple2
parameter_list|<
name|T1
parameter_list|,
name|T2
parameter_list|>
block|{
specifier|public
specifier|final
name|T1
name|_1
decl_stmt|;
specifier|public
specifier|final
name|T2
name|_2
decl_stmt|;
specifier|public
name|Tuple2
parameter_list|(
specifier|final
name|T1
name|_1
parameter_list|,
specifier|final
name|T2
name|_2
parameter_list|)
block|{
name|this
operator|.
name|_1
operator|=
name|_1
expr_stmt|;
name|this
operator|.
name|_2
operator|=
name|_2
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
specifier|final
name|Object
name|obj
parameter_list|)
block|{
if|if
condition|(
name|obj
operator|instanceof
name|Tuple2
condition|)
block|{
specifier|final
name|Tuple2
name|other
init|=
operator|(
name|Tuple2
operator|)
name|obj
decl_stmt|;
return|return
name|_1
operator|.
name|equals
argument_list|(
name|other
operator|.
name|_1
argument_list|)
operator|&&
name|_2
operator|.
name|equals
argument_list|(
name|other
operator|.
name|_2
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

