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
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|Logger
import|;
end_import

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
name|util
operator|.
name|Configuration
import|;
end_import

begin_comment
comment|/**  * Interface to be implemented by tasks used for system  * maintenance. System tasks require the database to be in  * a consistent state. All database operations will be stopped   * until the {@link #execute(DBBroker)} method returned  * or throws an exception. Any exception will be caught and a warning  * written to the log.  *   * A task can be scheduled for execution   * via {@link BrokerPool#triggerSystemTask(SystemTask)}.  *  * IMPORTANT: SystemTask implementations should avoid to acquire  * locks on collections! Doing so may lead to a deadlock situation.  * The system task runs in a privileged mode. Locking a collection is  * not required since no writing transactions will be allowed.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|SystemTask
block|{
specifier|final
specifier|static
name|Logger
name|LOG
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|SystemTask
operator|.
name|class
argument_list|)
decl_stmt|;
name|void
name|configure
parameter_list|(
name|Configuration
name|config
parameter_list|,
name|Properties
name|properties
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|/** 	 * Execute this task. 	 *  	 * @param broker a DBBroker object that can be used 	 *  	 * @throws EXistException 	 */
name|void
name|execute
parameter_list|(
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|EXistException
function_decl|;
comment|/** 	 * @return true if a checkpoint should be generated before this system task 	 * runs. A checkpoint guarantees that all changes were written to disk. 	 */
name|boolean
name|afterCheckpoint
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

