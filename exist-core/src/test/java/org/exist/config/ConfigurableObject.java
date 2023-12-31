begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsAttribute
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldAsElement
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|annotation
operator|.
name|ConfigurationFieldClassMask
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"instance"
argument_list|)
specifier|public
class|class
name|ConfigurableObject
implements|implements
name|Configurable
block|{
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"valueString"
argument_list|)
specifier|protected
name|String
name|some
init|=
literal|"default"
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"valueInteger"
argument_list|)
specifier|protected
name|Integer
name|someInteger
init|=
literal|7
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"valueInt"
argument_list|)
specifier|protected
name|int
name|simpleInteger
init|=
literal|5
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"value"
argument_list|)
specifier|protected
name|int
name|defaultInteger
init|=
literal|3
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"valueboolean"
argument_list|)
specifier|protected
name|boolean
name|someboolean
init|=
literal|false
decl_stmt|;
annotation|@
name|ConfigurationFieldAsAttribute
argument_list|(
literal|"valueBoolean"
argument_list|)
specifier|protected
name|Boolean
name|someBoolean
init|=
literal|true
decl_stmt|;
annotation|@
name|ConfigurationFieldAsElement
argument_list|(
literal|"spice"
argument_list|)
annotation|@
name|ConfigurationFieldClassMask
argument_list|(
literal|"org.exist.config.Spice"
argument_list|)
specifier|protected
name|List
argument_list|<
name|Spice
argument_list|>
name|spices
init|=
literal|null
decl_stmt|;
specifier|private
name|Configuration
name|configuration
decl_stmt|;
specifier|public
name|ConfigurableObject
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|configuration
operator|=
name|Configurator
operator|.
name|configure
argument_list|(
name|this
argument_list|,
name|config
argument_list|)
expr_stmt|;
block|}
specifier|public
name|List
argument_list|<
name|Spice
argument_list|>
name|getSpices
parameter_list|()
block|{
return|return
name|spices
return|;
block|}
specifier|public
name|void
name|addSpice
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|spices
operator|.
name|add
argument_list|(
operator|new
name|Spice
argument_list|(
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.exist.config.Configurable#isConfigured() 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
operator|(
name|configuration
operator|==
literal|null
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Configuration
name|getConfiguration
parameter_list|()
block|{
return|return
name|configuration
return|;
block|}
block|}
end_class

end_unit

