begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2018 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|Database
import|;
end_import

begin_comment
comment|/**  * Simple utility functions for creating named threads  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|ThreadUtils
block|{
specifier|public
specifier|static
name|String
name|nameInstanceThreadGroup
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|)
block|{
return|return
literal|"exist.db."
operator|+
name|instanceId
return|;
block|}
specifier|public
specifier|static
name|ThreadGroup
name|newInstanceSubThreadGroup
parameter_list|(
specifier|final
name|Database
name|database
parameter_list|,
specifier|final
name|String
name|subThreadGroupName
parameter_list|)
block|{
return|return
operator|new
name|ThreadGroup
argument_list|(
name|database
operator|.
name|getThreadGroup
argument_list|()
argument_list|,
name|subThreadGroupName
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|nameInstanceThread
parameter_list|(
specifier|final
name|Database
name|database
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|)
block|{
return|return
literal|"db."
operator|+
name|database
operator|.
name|getId
argument_list|()
operator|+
literal|"."
operator|+
name|threadName
return|;
block|}
specifier|public
specifier|static
name|String
name|nameInstanceThread
parameter_list|(
specifier|final
name|String
name|instanceId
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|)
block|{
return|return
literal|"db."
operator|+
name|instanceId
operator|+
literal|"."
operator|+
name|threadName
return|;
block|}
specifier|public
specifier|static
name|String
name|nameInstanceSchedulerThread
parameter_list|(
specifier|final
name|Database
name|database
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|)
block|{
return|return
literal|"db."
operator|+
name|database
operator|.
name|getId
argument_list|()
operator|+
literal|".scheduler."
operator|+
name|threadName
return|;
block|}
specifier|public
specifier|static
name|Thread
name|newInstanceThread
parameter_list|(
specifier|final
name|Database
name|database
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|,
specifier|final
name|Runnable
name|runnable
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|database
operator|.
name|getThreadGroup
argument_list|()
argument_list|,
name|runnable
argument_list|,
name|nameInstanceThread
argument_list|(
name|database
argument_list|,
name|threadName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Thread
name|newInstanceThread
parameter_list|(
specifier|final
name|ThreadGroup
name|threadGroup
parameter_list|,
specifier|final
name|String
name|instanceId
parameter_list|,
specifier|final
name|String
name|threadName
parameter_list|,
specifier|final
name|Runnable
name|runnable
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|threadGroup
argument_list|,
name|runnable
argument_list|,
name|nameInstanceThread
argument_list|(
name|instanceId
argument_list|,
name|threadName
argument_list|)
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|String
name|nameGlobalThread
parameter_list|(
specifier|final
name|String
name|threadName
parameter_list|)
block|{
return|return
literal|"global."
operator|+
name|threadName
return|;
block|}
specifier|public
specifier|static
name|Thread
name|newGlobalThread
parameter_list|(
specifier|final
name|String
name|threadName
parameter_list|,
specifier|final
name|Runnable
name|runnable
parameter_list|)
block|{
return|return
operator|new
name|Thread
argument_list|(
name|runnable
argument_list|,
name|nameGlobalThread
argument_list|(
name|threadName
argument_list|)
argument_list|)
return|;
block|}
block|}
end_class

end_unit

