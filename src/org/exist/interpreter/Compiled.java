begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|interpreter
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|xacml
operator|.
name|XACMLSource
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
name|XPathException
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
name|value
operator|.
name|Sequence
import|;
end_import

begin_interface
specifier|public
interface|interface
name|Compiled
extends|extends
name|IPathExpr
block|{
specifier|public
name|void
name|reset
parameter_list|()
function_decl|;
comment|/**      * @return the {@link Context} used to create this query      */
specifier|public
name|Context
name|getContext
parameter_list|()
function_decl|;
specifier|public
name|void
name|setContext
parameter_list|(
name|Context
name|context
parameter_list|)
function_decl|;
comment|/**      * Execute the compiled query, optionally using the specified      * sequence as context.      *       * @param contextSequence      * @throws XPathException      */
specifier|public
name|Sequence
name|eval
parameter_list|(
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
function_decl|;
comment|/**      * Is the compiled expression still valid? Returns false if, for example,      * the source code of one of the imported modules has changed.      */
specifier|public
name|boolean
name|isValid
parameter_list|()
function_decl|;
comment|/**      * Gets the source of this query.      *      * @return This query's source      */
specifier|public
name|XACMLSource
name|getSource
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

