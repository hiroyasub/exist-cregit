begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2005-09 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|system
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
name|AbstractInternalModule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionDef
import|;
end_import

begin_comment
comment|/**  * Module function definitions for system module.  *  * @author Wolfgang Meier (wolfgang@exist-db.org)  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|SystemModule
extends|extends
name|AbstractInternalModule
block|{
specifier|public
specifier|static
specifier|final
name|String
name|NAMESPACE_URI
init|=
literal|"http://exist-db.org/xquery/system"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|PREFIX
init|=
literal|"system"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|INCLUSION_DATE
init|=
literal|"2005-06-15"
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|String
name|RELEASED_IN_VERSION
init|=
literal|"eXist-1.0"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|FunctionDef
index|[]
name|functions
init|=
block|{
operator|new
name|FunctionDef
argument_list|(
name|FtIndexLookup
operator|.
name|signature
argument_list|,
name|FtIndexLookup
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CountInstances
operator|.
name|countInstancesMax
argument_list|,
name|CountInstances
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CountInstances
operator|.
name|countInstancesActive
argument_list|,
name|CountInstances
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|CountInstances
operator|.
name|countInstancesAvailable
argument_list|,
name|CountInstances
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetMemory
operator|.
name|getMemoryMax
argument_list|,
name|GetMemory
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetMemory
operator|.
name|getMemoryTotal
argument_list|,
name|GetMemory
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetMemory
operator|.
name|getMemoryFree
argument_list|,
name|GetMemory
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetVersion
operator|.
name|signature
argument_list|,
name|GetVersion
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetBuild
operator|.
name|signature
argument_list|,
name|GetBuild
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetRevision
operator|.
name|signature
argument_list|,
name|GetRevision
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetExistHome
operator|.
name|signature
argument_list|,
name|GetExistHome
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Shutdown
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|Shutdown
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Shutdown
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|Shutdown
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetModuleLoadPath
operator|.
name|signature
argument_list|,
name|GetModuleLoadPath
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|TriggerSystemTask
operator|.
name|signature
argument_list|,
name|TriggerSystemTask
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|AsUser
operator|.
name|signature
argument_list|,
name|AsUser
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetIndexStatistics
operator|.
name|signature
argument_list|,
name|GetIndexStatistics
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|UpdateStatistics
operator|.
name|signature
argument_list|,
name|UpdateStatistics
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetRunningXQueries
operator|.
name|signature
argument_list|,
name|GetRunningXQueries
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|KillRunningXQuery
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|KillRunningXQuery
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|KillRunningXQuery
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|KillRunningXQuery
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetRunningJobs
operator|.
name|signature
argument_list|,
name|GetRunningJobs
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetScheduledJobs
operator|.
name|signature
argument_list|,
name|GetScheduledJobs
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|Restore
operator|.
name|signature
argument_list|,
name|Restore
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionTrace
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|FunctionTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionTrace
operator|.
name|signatures
index|[
literal|1
index|]
argument_list|,
name|FunctionTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionTrace
operator|.
name|signatures
index|[
literal|2
index|]
argument_list|,
name|FunctionTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionTrace
operator|.
name|signatures
index|[
literal|3
index|]
argument_list|,
name|FunctionTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionTrace
operator|.
name|signatures
index|[
literal|4
index|]
argument_list|,
name|FunctionTrace
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetLibFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetLibFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetLibInfoFunction
operator|.
name|signatures
index|[
literal|0
index|]
argument_list|,
name|GetLibInfoFunction
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|GetUptime
operator|.
name|signature
argument_list|,
name|GetUptime
operator|.
name|class
argument_list|)
block|,
operator|new
name|FunctionDef
argument_list|(
name|FunctionAvailable
operator|.
name|signature
argument_list|,
name|FunctionAvailable
operator|.
name|class
argument_list|)
block|}
decl_stmt|;
specifier|public
name|SystemModule
parameter_list|()
block|{
name|super
argument_list|(
name|functions
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getNamespaceURI
parameter_list|()
block|{
return|return
name|NAMESPACE_URI
return|;
block|}
specifier|public
name|String
name|getDefaultPrefix
parameter_list|()
block|{
return|return
name|PREFIX
return|;
block|}
specifier|public
name|String
name|getDescription
parameter_list|()
block|{
return|return
literal|"A module for retrieving information about eXist and the system."
return|;
block|}
specifier|public
name|String
name|getReleaseVersion
parameter_list|()
block|{
return|return
name|RELEASED_IN_VERSION
return|;
block|}
block|}
end_class

end_unit

