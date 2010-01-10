begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|jetty
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
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
name|AuthenticationException
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
name|Realm
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
name|User
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
name|BrokerPool
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Request
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|Response
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|Credential
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|SSORealm
import|;
end_import

begin_import
import|import
name|org
operator|.
name|mortbay
operator|.
name|jetty
operator|.
name|security
operator|.
name|UserRealm
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XMLUserRealm
implements|implements
name|Realm
implements|,
name|UserRealm
implements|,
name|SSORealm
block|{
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|User
argument_list|>
name|users
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|User
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|SSORealm
name|ssoRealm
decl_stmt|;
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#authenticate(java.lang.String, java.lang.Object, org.mortbay.jetty.Request) 	 */
annotation|@
name|Override
specifier|public
name|Principal
name|authenticate
parameter_list|(
name|String
name|username
parameter_list|,
name|Object
name|credentials
parameter_list|,
name|Request
name|request
parameter_list|)
block|{
name|User
name|user
decl_stmt|;
synchronized|synchronized
init|(
name|this
init|)
block|{
name|user
operator|=
name|users
operator|.
name|get
argument_list|(
name|username
argument_list|)
expr_stmt|;
if|if
condition|(
name|user
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|user
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|this
argument_list|,
name|username
argument_list|,
name|credentials
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|EXistException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
name|users
operator|.
name|put
argument_list|(
name|username
argument_list|,
name|user
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|user
operator|.
name|authenticate
argument_list|(
name|credentials
argument_list|)
condition|)
return|return
name|user
return|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#disassociate(java.security.Principal) 	 */
annotation|@
name|Override
specifier|public
name|void
name|disassociate
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#getName() 	 */
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
literal|"eXist-DB"
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#getPrincipal(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Principal
name|getPrincipal
parameter_list|(
name|String
name|username
parameter_list|)
block|{
return|return
name|users
operator|.
name|get
argument_list|(
name|username
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#isUserInRole(java.security.Principal, java.lang.String) 	 */
annotation|@
name|Override
specifier|public
specifier|synchronized
name|boolean
name|isUserInRole
parameter_list|(
name|Principal
name|user
parameter_list|,
name|String
name|role
parameter_list|)
block|{
if|if
condition|(
name|user
operator|==
literal|null
operator|||
operator|!
operator|(
name|user
operator|instanceof
name|User
operator|)
operator|||
operator|(
operator|(
name|User
operator|)
name|user
operator|)
operator|.
name|getRealm
argument_list|()
operator|!=
name|this
condition|)
return|return
literal|false
return|;
comment|//role eq group ? -shabanovd
return|return
operator|(
operator|(
name|User
operator|)
name|user
operator|)
operator|.
name|hasGroup
argument_list|(
name|role
argument_list|)
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#logout(java.security.Principal) 	 */
annotation|@
name|Override
specifier|public
name|void
name|logout
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#popRole(java.security.Principal) 	 */
annotation|@
name|Override
specifier|public
name|Principal
name|popRole
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#pushRole(java.security.Principal, java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|Principal
name|pushRole
parameter_list|(
name|Principal
name|user
parameter_list|,
name|String
name|role
parameter_list|)
block|{
comment|// TODO Auto-generated method stub
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.UserRealm#reauthenticate(java.security.Principal) 	 */
annotation|@
name|Override
specifier|public
name|boolean
name|reauthenticate
parameter_list|(
name|Principal
name|user
parameter_list|)
block|{
if|if
condition|(
name|user
operator|instanceof
name|User
condition|)
return|return
operator|(
operator|(
name|User
operator|)
name|user
operator|)
operator|.
name|isAuthenticated
argument_list|()
return|;
return|return
literal|false
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.SSORealm#clearSingleSignOn(java.lang.String) 	 */
annotation|@
name|Override
specifier|public
name|void
name|clearSingleSignOn
parameter_list|(
name|String
name|username
parameter_list|)
block|{
if|if
condition|(
name|ssoRealm
operator|!=
literal|null
condition|)
name|ssoRealm
operator|.
name|clearSingleSignOn
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.SSORealm#getSingleSignOn(org.mortbay.jetty.Request, org.mortbay.jetty.Response) 	 */
annotation|@
name|Override
specifier|public
name|Credential
name|getSingleSignOn
parameter_list|(
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|)
block|{
if|if
condition|(
name|ssoRealm
operator|!=
literal|null
condition|)
return|return
name|ssoRealm
operator|.
name|getSingleSignOn
argument_list|(
name|request
argument_list|,
name|response
argument_list|)
return|;
return|return
literal|null
return|;
block|}
comment|/* (non-Javadoc) 	 * @see org.mortbay.jetty.security.SSORealm#setSingleSignOn(org.mortbay.jetty.Request, org.mortbay.jetty.Response, java.security.Principal, org.mortbay.jetty.security.Credential) 	 */
annotation|@
name|Override
specifier|public
name|void
name|setSingleSignOn
parameter_list|(
name|Request
name|request
parameter_list|,
name|Response
name|response
parameter_list|,
name|Principal
name|principal
parameter_list|,
name|Credential
name|credential
parameter_list|)
block|{
if|if
condition|(
name|ssoRealm
operator|!=
literal|null
condition|)
name|ssoRealm
operator|.
name|setSingleSignOn
argument_list|(
name|request
argument_list|,
name|response
argument_list|,
name|principal
argument_list|,
name|credential
argument_list|)
expr_stmt|;
block|}
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
literal|"Realm["
operator|+
name|getName
argument_list|()
operator|+
literal|"]=="
operator|+
name|users
operator|.
name|keySet
argument_list|()
return|;
block|}
block|}
end_class

end_unit

