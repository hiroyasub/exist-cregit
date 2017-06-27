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
name|storage
operator|.
name|ProcessMonitor
import|;
end_import

begin_comment
comment|/**  * Detail information about recently executed XQuery.  */
end_comment

begin_class
specifier|public
class|class
name|RecentQueryHistory
block|{
specifier|private
name|int
name|idx
decl_stmt|;
specifier|private
name|String
name|sourceKey
decl_stmt|;
specifier|private
name|int
name|recentInvocationCount
decl_stmt|;
specifier|private
name|long
name|mostRecentExecutionTime
decl_stmt|;
specifier|private
name|long
name|mostRecentExecutionDuration
decl_stmt|;
specifier|private
name|String
name|requestURI
decl_stmt|;
specifier|public
name|RecentQueryHistory
parameter_list|(
name|int
name|idx
parameter_list|,
name|ProcessMonitor
operator|.
name|QueryHistory
name|queryHistory
parameter_list|)
block|{
name|this
operator|.
name|idx
operator|=
name|idx
expr_stmt|;
name|this
operator|.
name|sourceKey
operator|=
name|queryHistory
operator|.
name|getSource
argument_list|()
expr_stmt|;
name|this
operator|.
name|recentInvocationCount
operator|=
name|queryHistory
operator|.
name|getInvocationCount
argument_list|()
expr_stmt|;
name|this
operator|.
name|mostRecentExecutionTime
operator|=
name|queryHistory
operator|.
name|getMostRecentExecutionTime
argument_list|()
expr_stmt|;
name|this
operator|.
name|mostRecentExecutionDuration
operator|=
name|queryHistory
operator|.
name|getMostRecentExecutionDuration
argument_list|()
expr_stmt|;
name|this
operator|.
name|requestURI
operator|=
name|queryHistory
operator|.
name|getRequestURI
argument_list|()
expr_stmt|;
block|}
specifier|public
name|int
name|getIdx
parameter_list|()
block|{
return|return
name|idx
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
name|int
name|getRecentInvocationCount
parameter_list|()
block|{
return|return
name|recentInvocationCount
return|;
block|}
specifier|public
name|long
name|getMostRecentExecutionTime
parameter_list|()
block|{
return|return
name|mostRecentExecutionTime
return|;
block|}
specifier|public
name|long
name|getMostRecentExecutionDuration
parameter_list|()
block|{
return|return
name|mostRecentExecutionDuration
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
block|}
end_class

end_unit

