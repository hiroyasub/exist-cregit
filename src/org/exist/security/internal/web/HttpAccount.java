begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2003-2013 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
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
operator|.
name|web
package|;
end_package

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

begin_import
import|import
name|javax
operator|.
name|servlet
operator|.
name|http
operator|.
name|HttpServletRequest
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|InvocationTargetException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|reflect
operator|.
name|Method
import|;
end_import

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_class
specifier|public
class|class
name|HttpAccount
block|{
specifier|public
specifier|static
name|Subject
name|getUserFromServletRequest
parameter_list|(
specifier|final
name|HttpServletRequest
name|request
parameter_list|)
block|{
specifier|final
name|Principal
name|principal
init|=
name|request
operator|.
name|getUserPrincipal
argument_list|()
decl_stmt|;
if|if
condition|(
name|principal
operator|instanceof
name|Subject
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|principal
return|;
comment|//workaroud strange jetty authentication method, why encapsulate user object??? -shabanovd
block|}
if|else if
condition|(
name|principal
operator|!=
literal|null
operator|&&
literal|"org.eclipse.jetty.plus.jaas.JAASUserPrincipal"
operator|.
name|equals
argument_list|(
name|principal
operator|.
name|getClass
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
try|try
block|{
specifier|final
name|Method
name|method
init|=
name|principal
operator|.
name|getClass
argument_list|()
operator|.
name|getMethod
argument_list|(
literal|"getCurrentSubject"
argument_list|)
decl_stmt|;
specifier|final
name|Object
name|obj
init|=
name|method
operator|.
name|invoke
argument_list|(
name|principal
argument_list|)
decl_stmt|;
if|if
condition|(
name|obj
operator|instanceof
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
condition|)
block|{
specifier|final
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
name|subject
init|=
operator|(
name|javax
operator|.
name|security
operator|.
name|auth
operator|.
name|Subject
operator|)
name|obj
decl_stmt|;
for|for
control|(
specifier|final
name|Principal
name|_principal_
range|:
name|subject
operator|.
name|getPrincipals
argument_list|()
control|)
block|{
if|if
condition|(
name|_principal_
operator|instanceof
name|Subject
condition|)
block|{
return|return
operator|(
name|Subject
operator|)
name|_principal_
return|;
block|}
block|}
block|}
block|}
catch|catch
parameter_list|(
specifier|final
name|SecurityException
decl||
name|InvocationTargetException
decl||
name|NoSuchMethodException
decl||
name|IllegalAccessException
decl||
name|IllegalArgumentException
name|e
parameter_list|)
block|{
block|}
block|}
return|return
literal|null
return|;
block|}
block|}
end_class

end_unit

