begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
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
comment|/**  * Interface for triggers that can be registered with collection-related events.  *   * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|CollectionTrigger
extends|extends
name|Trigger
block|{
comment|/**      * This method is called once before the database will actually create, remove or rename a collection. You may       * take any action here, using the supplied broker instance.      *       * @param event the type of event that triggered this call (see the constants defined in this interface).      * @param broker the database instance used to process the current action.      * @param collection the {@link Collection} which fired this event.      * @param newName optional: if event is a {@link Trigger#RENAME_COLLECTION_EVENT},      *  this parameter contains the new name of the collection. It is null otherwise.      * @throws TriggerException throwing a TriggerException will abort the current action.      */
specifier|public
name|void
name|prepare
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
name|newName
parameter_list|)
throws|throws
name|TriggerException
function_decl|;
comment|/**      * This method is called after the operation has completed.        **/
specifier|public
name|void
name|finish
parameter_list|(
name|int
name|event
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
name|newName
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

