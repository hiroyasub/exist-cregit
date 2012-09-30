begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2012 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
comment|/**  * The instance must start-up after configuration and   * shutdown at database stopping process.  *    * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
annotation|@
name|Deprecated
specifier|public
interface|interface
name|Startable
block|{
annotation|@
name|Deprecated
specifier|public
name|void
name|startUp
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|//public void shutDown(DBBroker broker) throws EXistException;
block|}
end_interface

end_unit

