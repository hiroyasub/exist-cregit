begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id:$  */
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
name|ConfigurationField
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
name|ConfigurationField
argument_list|(
literal|"value1"
argument_list|)
specifier|protected
name|String
name|some
init|=
literal|"default"
decl_stmt|;
annotation|@
name|ConfigurationField
argument_list|(
literal|"value2"
argument_list|)
specifier|protected
name|Integer
name|someInteger
init|=
literal|7
decl_stmt|;
specifier|public
name|ConfigurableObject
parameter_list|(
name|ConfigElementImpl
name|config
parameter_list|)
block|{
name|Configurater
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
comment|// TODO Auto-generated method stub
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

