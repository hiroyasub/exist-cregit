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
name|storage
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

begin_comment
comment|/**  * Interface to be implemented by tasks used for system  * maintenance. System tasks require the database to be in  * a consistent state. All database operations will be stopped   * until the {@link #execute(BrokerPool)} method returned  * or throws an exception. Any exception will be caught and a warning  * written to the log.  *   * A task can be scheduled for execution   * via {@link BrokerPool#triggerSystemTask(SystemTask)}  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|SystemTask
block|{
comment|/** 	 * Execute this task. 	 *  	 * @param pool the BrokerPool for this database instance. 	 * @throws EXistException 	 */
name|void
name|execute
parameter_list|(
name|BrokerPool
name|pool
parameter_list|)
throws|throws
name|EXistException
function_decl|;
block|}
end_interface

end_unit

