begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|indexing
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|BrokerPool
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
name|btree
operator|.
name|DBException
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
name|DatabaseConfigurationException
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
name|Element
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractIndex
implements|implements
name|Index
block|{
specifier|protected
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
name|String
name|name
init|=
literal|null
decl_stmt|;
specifier|public
specifier|abstract
name|Index
name|getInstance
parameter_list|()
function_decl|;
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
specifier|public
name|BrokerPool
name|getBrokerPool
parameter_list|()
block|{
return|return
name|pool
return|;
block|}
specifier|public
name|void
name|configure
parameter_list|(
name|BrokerPool
name|pool
parameter_list|,
name|String
name|dataDir
parameter_list|,
name|Element
name|config
parameter_list|)
throws|throws
name|DatabaseConfigurationException
block|{
name|this
operator|.
name|pool
operator|=
name|pool
expr_stmt|;
if|if
condition|(
name|config
operator|.
name|hasAttribute
argument_list|(
literal|"id"
argument_list|)
condition|)
name|name
operator|=
name|config
operator|.
name|getAttribute
argument_list|(
literal|"id"
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|abstract
name|void
name|open
parameter_list|()
throws|throws
name|DatabaseConfigurationException
function_decl|;
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
specifier|abstract
name|void
name|sync
parameter_list|()
throws|throws
name|DBException
function_decl|;
specifier|public
specifier|abstract
name|IndexWorker
name|getWorker
parameter_list|()
function_decl|;
specifier|public
specifier|abstract
name|void
name|remove
parameter_list|()
throws|throws
name|DBException
function_decl|;
block|}
end_class

end_unit

