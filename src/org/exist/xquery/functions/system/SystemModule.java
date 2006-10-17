begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Team  *  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
literal|"Functions to retrieve information about eXist and the system."
return|;
block|}
block|}
end_class

end_unit

