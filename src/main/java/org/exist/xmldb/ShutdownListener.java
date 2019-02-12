begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001,  Wolfgang M. Meier (meier@ifs.tu-darmstadt.de)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU Library General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *  *  $Id:  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_comment
comment|/**  * Instances of this class can be registered with the BrokerPool to get informed when the  * database shuts down. The shutdown method is called after the database instance has  * been shut down.  *  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|ShutdownListener
block|{
comment|/**      * The database instance identified by dbname has been shut down.      *      * @param dbname             The name of the database instance.      * @param remainingInstances Number of remaining database instances.      */
name|void
name|shutdown
parameter_list|(
name|String
name|dbname
parameter_list|,
name|int
name|remainingInstances
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

