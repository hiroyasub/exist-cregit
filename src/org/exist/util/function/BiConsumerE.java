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
comment|/**  * Similar to {@link org.exist.util.function.ConsumerE} but  * accepts two arguments  *  * @param<T> the type of the first input to the operation  * @param<U> the type of the first input to the operation  * @param<E> Function throws exception type  */
end_comment

begin_interface
annotation|@
name|FunctionalInterface
specifier|public
interface|interface
name|BiConsumerE
parameter_list|<
name|T
parameter_list|,
name|U
parameter_list|,
name|E
extends|extends
name|Throwable
parameter_list|>
block|{
name|void
name|accept
parameter_list|(
name|T
name|t
parameter_list|,
name|U
name|u
parameter_list|)
throws|throws
name|E
function_decl|;
block|}
end_interface

end_unit

