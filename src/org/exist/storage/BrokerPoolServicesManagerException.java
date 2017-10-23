begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * Exception reported by BrokerPoolServicesManager  *  * @author Adam Retter<adam.retter@googlemail.com>  */
end_comment

begin_class
specifier|public
class|class
name|BrokerPoolServicesManagerException
extends|extends
name|Exception
block|{
specifier|private
specifier|final
name|List
argument_list|<
name|BrokerPoolServiceException
argument_list|>
name|serviceExceptions
decl_stmt|;
specifier|public
name|BrokerPoolServicesManagerException
parameter_list|(
specifier|final
name|List
argument_list|<
name|BrokerPoolServiceException
argument_list|>
name|serviceExceptions
parameter_list|)
block|{
name|this
operator|.
name|serviceExceptions
operator|=
name|serviceExceptions
expr_stmt|;
block|}
specifier|public
comment|/*@Nullable*/
name|List
argument_list|<
name|BrokerPoolServiceException
argument_list|>
name|getServiceExceptions
parameter_list|()
block|{
return|return
name|serviceExceptions
return|;
block|}
block|}
end_class

end_unit

