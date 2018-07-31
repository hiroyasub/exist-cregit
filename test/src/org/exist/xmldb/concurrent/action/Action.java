begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2000-04,  Wolfgang M. Meier (wolfgang@exist-db.org)  *  *  This library is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Library General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This library is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Library General Public License for more details.  *  *  You should have received a copy of the GNU General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.  *   *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|concurrent
operator|.
name|action
package|;
end_package

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_comment
comment|/**  * Abstract base class for an action to be tested.  *   * @author wolf  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|Action
block|{
specifier|protected
specifier|final
name|String
name|collectionPath
decl_stmt|;
specifier|protected
specifier|final
name|String
name|resourceName
decl_stmt|;
specifier|public
name|Action
parameter_list|(
specifier|final
name|String
name|collectionPath
parameter_list|,
specifier|final
name|String
name|resourceName
parameter_list|)
block|{
name|this
operator|.
name|collectionPath
operator|=
name|collectionPath
expr_stmt|;
name|this
operator|.
name|resourceName
operator|=
name|resourceName
expr_stmt|;
block|}
comment|/** 	 * Returns true if execution compled successfully. 	 * 	 * @return true if execution completed successfully, false otherwise. 	 */
specifier|abstract
specifier|public
name|boolean
name|execute
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
function_decl|;
block|}
end_class

end_unit

