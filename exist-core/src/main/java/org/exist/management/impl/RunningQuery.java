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
name|management
operator|.
name|impl
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryWatchDog
import|;
end_import

begin_comment
comment|/**  * Detail information about a running XQuery  */
end_comment

begin_class
specifier|public
class|class
name|RunningQuery
block|{
name|int
name|id
decl_stmt|;
name|String
name|sourceType
decl_stmt|;
name|String
name|sourceKey
decl_stmt|;
name|boolean
name|terminating
decl_stmt|;
name|String
name|requestURI
decl_stmt|;
name|String
name|thread
decl_stmt|;
name|long
name|elapsed
decl_stmt|;
specifier|public
name|RunningQuery
parameter_list|(
specifier|final
name|XQueryWatchDog
name|watchdog
parameter_list|,
specifier|final
name|String
name|requestURI
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|hashCode
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceType
operator|=
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|getSource
argument_list|()
operator|.
name|type
argument_list|()
expr_stmt|;
name|this
operator|.
name|sourceKey
operator|=
name|watchdog
operator|.
name|getContext
argument_list|()
operator|.
name|getSource
argument_list|()
operator|.
name|path
argument_list|()
expr_stmt|;
name|this
operator|.
name|terminating
operator|=
name|watchdog
operator|.
name|isTerminating
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestURI
operator|=
name|requestURI
expr_stmt|;
name|this
operator|.
name|thread
operator|=
name|watchdog
operator|.
name|getRunningThread
argument_list|()
expr_stmt|;
name|this
operator|.
name|elapsed
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|watchdog
operator|.
name|getStartTime
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
specifier|public
name|String
name|getSourceType
parameter_list|()
block|{
return|return
name|sourceType
return|;
block|}
specifier|public
name|String
name|getSourceKey
parameter_list|()
block|{
return|return
name|sourceKey
return|;
block|}
specifier|public
name|String
name|getRequestURI
parameter_list|()
block|{
return|return
name|requestURI
return|;
block|}
specifier|public
name|boolean
name|isTerminating
parameter_list|()
block|{
return|return
name|terminating
return|;
block|}
specifier|public
name|String
name|getThread
parameter_list|()
block|{
return|return
name|thread
return|;
block|}
specifier|public
name|long
name|getElapsed
parameter_list|()
block|{
return|return
name|elapsed
return|;
block|}
block|}
end_class

end_unit

