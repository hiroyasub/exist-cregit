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
name|lock
operator|.
name|Lock
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|LockTable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|LockTable
operator|.
name|LockCountTraces
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|LockTable
operator|.
name|LockModeOwner
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * JMX MXBean interface for examining the LockTable  *  * @author Adam Retter<adam@evolvedbinary.com>  */
end_comment

begin_interface
specifier|public
interface|interface
name|LockTableMXBean
extends|extends
name|PerInstanceMBean
block|{
comment|/**      * Get information about acquired locks      *      * @return information about acquired locks      */
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Lock
operator|.
name|LockType
argument_list|,
name|Map
argument_list|<
name|Lock
operator|.
name|LockMode
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|LockCountTraces
argument_list|>
argument_list|>
argument_list|>
argument_list|>
name|getAcquired
parameter_list|()
function_decl|;
comment|/**      * Get information about outstanding attempts to acquire locks      *      * @return information about outstanding attempts to acquire locks      */
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|Lock
operator|.
name|LockType
argument_list|,
name|List
argument_list|<
name|LockModeOwner
argument_list|>
argument_list|>
argument_list|>
name|getAttempting
parameter_list|()
function_decl|;
name|void
name|dumpToConsole
parameter_list|()
function_decl|;
name|void
name|dumpToLog
parameter_list|()
function_decl|;
name|void
name|xmlDumpToConsole
parameter_list|()
function_decl|;
name|void
name|xmlDumpToLog
parameter_list|()
function_decl|;
name|void
name|fullDumpToConsole
parameter_list|()
function_decl|;
name|void
name|fullDumpToLog
parameter_list|()
function_decl|;
name|void
name|xmlFullDumpToConsole
parameter_list|()
function_decl|;
name|void
name|xmlFullDumpToLog
parameter_list|()
function_decl|;
block|}
end_interface

end_unit
