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
name|config
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|ElementAtExist
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|PermissionDeniedException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_interface
specifier|public
interface|interface
name|Configuration
block|{
specifier|public
name|String
name|NS
init|=
literal|"http://exist-db.org/Configuration"
decl_stmt|;
specifier|public
name|String
name|ID
init|=
literal|"id"
decl_stmt|;
specifier|public
name|Configuration
name|getConfiguration
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|List
argument_list|<
name|Configuration
argument_list|>
name|getConfigurations
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|Set
argument_list|<
name|String
argument_list|>
name|getProperties
parameter_list|()
function_decl|;
specifier|public
name|boolean
name|hasProperty
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|String
name|getProperty
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
specifier|public
name|Integer
name|getPropertyInteger
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
specifier|public
name|Long
name|getPropertyLong
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
specifier|public
name|Boolean
name|getPropertyBoolean
parameter_list|(
name|String
name|property
parameter_list|)
function_decl|;
specifier|public
name|Class
argument_list|<
name|?
argument_list|>
name|getPropertyClass
parameter_list|(
name|String
name|propertySecurityClass
parameter_list|)
function_decl|;
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|String
name|value
parameter_list|)
function_decl|;
specifier|public
name|void
name|setProperty
parameter_list|(
name|String
name|property
parameter_list|,
name|Integer
name|value
parameter_list|)
function_decl|;
specifier|public
name|Object
name|putObject
parameter_list|(
name|String
name|name
parameter_list|,
name|Object
name|object
parameter_list|)
function_decl|;
specifier|public
name|Object
name|getObject
parameter_list|(
name|String
name|name
parameter_list|)
function_decl|;
specifier|public
name|String
name|getName
parameter_list|()
function_decl|;
specifier|public
name|ElementAtExist
name|getElement
parameter_list|()
function_decl|;
specifier|public
name|void
name|checkForUpdates
parameter_list|(
name|ElementAtExist
name|document
parameter_list|)
function_decl|;
specifier|public
name|void
name|save
parameter_list|()
throws|throws
name|PermissionDeniedException
function_decl|;
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|obj
parameter_list|,
name|String
name|uniqField
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

