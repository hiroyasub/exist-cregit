begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  Trigger.java - eXist Open Source Native XML Database  *  Copyright (C) 2003 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist-db.org  *   *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *   * $Id$  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|collections
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
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
name|storage
operator|.
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ContentHandler
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|ext
operator|.
name|LexicalHandler
import|;
end_import

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_interface
specifier|public
interface|interface
name|Trigger
extends|extends
name|ContentHandler
extends|,
name|LexicalHandler
block|{
specifier|public
specifier|final
specifier|static
name|int
name|STORE_EVENT
init|=
literal|0
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|UPDATE_EVENT
init|=
literal|1
decl_stmt|;
specifier|public
specifier|final
specifier|static
name|int
name|REMOVE_EVENT
init|=
literal|2
decl_stmt|;
specifier|public
name|void
name|configure
parameter_list|(
name|Map
name|parameters
parameter_list|)
throws|throws
name|CollectionConfigurationException
function_decl|;
specifier|public
name|void
name|prepare
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|collection
parameter_list|,
name|String
name|documentName
parameter_list|,
name|Document
name|existingDocument
parameter_list|)
throws|throws
name|TriggerException
function_decl|;
specifier|public
name|void
name|setValidating
parameter_list|(
name|boolean
name|validating
parameter_list|)
function_decl|;
specifier|public
name|boolean
name|isValidating
parameter_list|()
function_decl|;
specifier|public
name|void
name|setOutputHandler
parameter_list|(
name|ContentHandler
name|handler
parameter_list|)
function_decl|;
specifier|public
name|void
name|setLexicalOutputHandler
parameter_list|(
name|LexicalHandler
name|handler
parameter_list|)
function_decl|;
specifier|public
name|ContentHandler
name|getOutputHandler
parameter_list|()
function_decl|;
specifier|public
name|ContentHandler
name|getInputHandler
parameter_list|()
function_decl|;
specifier|public
name|LexicalHandler
name|getLexicalOutputHandler
parameter_list|()
function_decl|;
specifier|public
name|LexicalHandler
name|getLexicalInputHandler
parameter_list|()
function_decl|;
specifier|public
name|Logger
name|getLogger
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

