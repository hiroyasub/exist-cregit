begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Startable
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
name|DBBroker
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|LifeCycle
extends|extends
name|Startable
block|{
specifier|public
name|void
name|start
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
specifier|public
name|void
name|sync
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
specifier|public
name|void
name|stop
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|//    public boolean isRunning();
comment|//
comment|//    public boolean isStarted();
comment|//    public boolean isStarting();
comment|//
comment|//    public boolean isStopping();
comment|//    public boolean isStopped();
comment|//
comment|//    public boolean isFailed();
block|}
end_interface

end_unit

