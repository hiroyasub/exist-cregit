begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id: ConfigurableObject.java 13769 2011-02-12 17:47:00Z shabanovd $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|mapping
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|config
operator|.
name|Configurable
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
name|Configuration
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
name|Configurator
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
name|NewClass
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
name|ConfigurationFieldAsElement
argument_list|(
literal|"mappedConfig"
argument_list|)
annotation|@
name|NewClass
argument_list|(
name|name
operator|=
literal|"org.exist.config.mapping.MappedClass"
argument_list|,
name|mapper
operator|=
literal|"org/exist/config/mapping/Mapping.xml"
argument_list|)
specifier|protected
name|MappedClass
name|subclasses
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

