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
name|security
operator|.
name|realm
operator|.
name|activedirectory
package|;
end_package

begin_import
import|import
name|java
operator|.
name|text
operator|.
name|MessageFormat
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

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|realm
operator|.
name|ldap
operator|.
name|LdapContextFactory
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
annotation|@
name|ConfigurationClass
argument_list|(
literal|"context"
argument_list|)
specifier|public
class|class
name|ContextFactory
extends|extends
name|LdapContextFactory
block|{
annotation|@
name|ConfigurationField
argument_list|(
literal|"domain"
argument_list|)
specifier|protected
name|String
name|domain
init|=
literal|null
decl_stmt|;
annotation|@
name|ConfigurationField
argument_list|(
literal|"searchBase"
argument_list|)
specifier|private
name|String
name|searchBase
init|=
literal|null
decl_stmt|;
specifier|protected
name|ContextFactory
parameter_list|(
name|Configuration
name|config
parameter_list|)
block|{
name|super
argument_list|(
name|config
argument_list|)
expr_stmt|;
if|if
condition|(
name|domain
operator|==
literal|null
condition|)
block|{
comment|//throw error?
name|domain
operator|=
literal|""
expr_stmt|;
block|}
name|principalPatternFormat
operator|=
operator|new
name|MessageFormat
argument_list|(
literal|"{0}@"
operator|+
name|domain
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|getSearchBase
parameter_list|()
block|{
return|return
name|searchBase
return|;
block|}
specifier|public
name|String
name|getDomain
parameter_list|()
block|{
return|return
name|domain
return|;
block|}
block|}
end_class

end_unit

