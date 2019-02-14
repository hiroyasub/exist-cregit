begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|commands
operator|.
name|info
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|command
operator|.
name|AbstractCommandResolver
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|plugin
operator|.
name|command
operator|.
name|Commands
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|InfoCommandResolver
extends|extends
name|AbstractCommandResolver
block|{
static|static
block|{
name|InfoCommandResolver
name|resolver
init|=
operator|new
name|InfoCommandResolver
argument_list|()
decl_stmt|;
name|resolver
operator|.
name|plug
argument_list|(
name|Lock
operator|.
name|class
argument_list|)
expr_stmt|;
name|resolver
operator|.
name|plug
argument_list|(
name|Transactions
operator|.
name|class
argument_list|)
expr_stmt|;
name|Commands
operator|.
name|plug
argument_list|(
literal|"info"
argument_list|,
name|resolver
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

