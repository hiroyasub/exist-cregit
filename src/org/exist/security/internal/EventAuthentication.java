begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|internal
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EventListener
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
name|dom
operator|.
name|persistent
operator|.
name|QName
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
name|Subject
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"authentication"
argument_list|)
specifier|public
class|class
name|EventAuthentication
implements|implements
name|EventListener
argument_list|<
name|Subject
argument_list|>
implements|,
name|Configurable
block|{
specifier|protected
specifier|final
specifier|static
name|QName
name|functionName
init|=
operator|new
name|QName
argument_list|(
literal|"authentication"
argument_list|,
name|SMEvents
operator|.
name|NAMESPACE_URI
argument_list|)
decl_stmt|;
comment|//XXX: @ConfigurationFieldAsText
specifier|private
name|String
name|script
init|=
literal|""
decl_stmt|;
specifier|private
name|SMEvents
name|em
decl_stmt|;
specifier|private
name|Configuration
name|configuration
init|=
literal|null
decl_stmt|;
specifier|public
name|EventAuthentication
parameter_list|(
name|SMEvents
name|em
parameter_list|,
name|Configuration
name|config
parameter_list|)
block|{
name|this
operator|.
name|em
operator|=
name|em
expr_stmt|;
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
annotation|@
name|Override
specifier|public
name|void
name|onEvent
parameter_list|(
name|Subject
name|subject
parameter_list|)
block|{
name|em
operator|.
name|runScript
argument_list|(
name|subject
argument_list|,
literal|null
argument_list|,
name|script
argument_list|,
name|functionName
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isConfigured
parameter_list|()
block|{
return|return
name|configuration
operator|!=
literal|null
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

