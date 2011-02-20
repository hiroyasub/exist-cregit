begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|openid
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSession
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionAttributeListener
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpSessionBindingEvent
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|http
operator|.
name|security
operator|.
name|Constraint
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|security
operator|.
name|UserAuthentication
import|;
end_import

begin_import
import|import
name|org
operator|.
name|eclipse
operator|.
name|jetty
operator|.
name|server
operator|.
name|UserIdentity
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SessionAuthentication
extends|extends
name|UserAuthentication
implements|implements
name|HttpSessionAttributeListener
block|{
specifier|public
specifier|static
name|String
name|__J_AUTHENTICATED
init|=
literal|"org.eclipse.jetty.security.UserIdentity"
decl_stmt|;
name|HttpSession
name|_session
decl_stmt|;
specifier|public
name|SessionAuthentication
parameter_list|(
name|HttpSession
name|session
parameter_list|,
name|UserIdentity
name|userIdentity
parameter_list|)
block|{
name|super
argument_list|(
name|Constraint
operator|.
name|__FORM_AUTH
argument_list|,
name|userIdentity
argument_list|)
expr_stmt|;
name|_session
operator|=
name|session
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|attributeAdded
parameter_list|(
name|HttpSessionBindingEvent
name|event
parameter_list|)
block|{
block|}
annotation|@
name|Override
specifier|public
name|void
name|attributeRemoved
parameter_list|(
name|HttpSessionBindingEvent
name|event
parameter_list|)
block|{
name|super
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|attributeReplaced
parameter_list|(
name|HttpSessionBindingEvent
name|event
parameter_list|)
block|{
if|if
condition|(
name|event
operator|.
name|getValue
argument_list|()
operator|==
literal|null
condition|)
name|super
operator|.
name|logout
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|()
block|{
name|_session
operator|.
name|removeAttribute
argument_list|(
name|SessionAuthentication
operator|.
name|__J_AUTHENTICATED
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Session"
operator|+
name|super
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

