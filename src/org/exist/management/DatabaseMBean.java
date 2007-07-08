begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|management
package|;
end_package

begin_comment
comment|/* *  eXist Open Source Native XML Database *  Copyright (C) 2001-07 The eXist Project *  http://exist-db.org * *  This program is free software; you can redistribute it and/or *  modify it under the terms of the GNU Lesser General Public License *  as published by the Free Software Foundation; either version 2 *  of the License, or (at your option) any later version. * *  This program is distributed in the hope that it will be useful, *  but WITHOUT ANY WARRANTY; without even the implied warranty of *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the *  GNU Lesser General Public License for more details. * *  You should have received a copy of the GNU Lesser General Public *  License along with this library; if not, write to the Free Software *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA * * $Id$ */
end_comment

begin_interface
specifier|public
interface|interface
name|DatabaseMBean
block|{
name|String
name|getInstanceId
parameter_list|()
function_decl|;
name|int
name|getMaxBrokers
parameter_list|()
function_decl|;
name|int
name|getAvailableBrokers
parameter_list|()
function_decl|;
name|int
name|getActiveBrokers
parameter_list|()
function_decl|;
name|long
name|getReservedMem
parameter_list|()
function_decl|;
name|long
name|getCacheMem
parameter_list|()
function_decl|;
name|long
name|getCollectionCacheMem
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

