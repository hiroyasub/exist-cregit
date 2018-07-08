begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-11 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|ldap
operator|.
name|xquery
package|;
end_package

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
name|org
operator|.
name|exist
operator|.
name|dom
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
name|Account
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
name|PermissionDeniedException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|BasicFunction
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|Cardinality
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|FunctionSignature
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XPathException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|XQueryContext
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Sequence
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceType
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|Type
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
name|SecurityManager
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
name|realm
operator|.
name|ldap
operator|.
name|LDAPRealm
import|;
end_import

begin_comment
comment|/**  * @author Adam Retter<adam@exist-db.org>  */
end_comment

begin_class
specifier|public
class|class
name|AccountFunctions
extends|extends
name|BasicFunction
block|{
specifier|public
specifier|static
specifier|final
name|FunctionSignature
name|signatures
index|[]
init|=
block|{
operator|new
name|FunctionSignature
argument_list|(
operator|new
name|QName
argument_list|(
literal|"update-account"
argument_list|,
name|LDAPModule
operator|.
name|NAMESPACE_URI
argument_list|,
name|LDAPModule
operator|.
name|PREFIX
argument_list|)
argument_list|,
literal|"Refreshed the cached LDAP account details from the LDAP directory"
argument_list|,
operator|new
name|SequenceType
index|[]
block|{
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|Cardinality
operator|.
name|EXACTLY_ONE
argument_list|)
block|}
argument_list|,
operator|new
name|SequenceType
argument_list|(
name|Type
operator|.
name|EMPTY
argument_list|,
name|Cardinality
operator|.
name|ZERO
argument_list|)
argument_list|)
block|}
decl_stmt|;
specifier|public
name|AccountFunctions
parameter_list|(
specifier|final
name|XQueryContext
name|context
parameter_list|,
specifier|final
name|FunctionSignature
name|signature
parameter_list|)
block|{
name|super
argument_list|(
name|context
argument_list|,
name|signature
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|Sequence
name|eval
parameter_list|(
specifier|final
name|Sequence
index|[]
name|args
parameter_list|,
specifier|final
name|Sequence
name|contextSequence
parameter_list|)
throws|throws
name|XPathException
block|{
specifier|final
name|SecurityManager
name|sm
init|=
name|context
operator|.
name|getBroker
argument_list|()
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
decl_stmt|;
specifier|final
name|LDAPRealm
name|ldapRealm
init|=
name|getLdapRealm
argument_list|(
name|sm
argument_list|)
decl_stmt|;
specifier|final
name|String
name|accountName
init|=
name|args
index|[
literal|0
index|]
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
decl_stmt|;
specifier|final
name|Account
name|ldapAccount
init|=
name|sm
operator|.
name|getAccount
argument_list|(
name|accountName
argument_list|)
decl_stmt|;
if|if
condition|(
name|ldapAccount
operator|==
literal|null
condition|)
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The Account '"
operator|+
name|accountName
operator|+
literal|"' does not exist!"
argument_list|)
throw|;
try|try
block|{
name|ldapRealm
operator|.
name|refreshAccountFromLdap
argument_list|(
name|ldapAccount
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|PermissionDeniedException
decl||
name|AuthenticationException
name|pde
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
name|pde
argument_list|)
throw|;
block|}
return|return
name|Sequence
operator|.
name|EMPTY_SEQUENCE
return|;
block|}
specifier|private
name|LDAPRealm
name|getLdapRealm
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|)
throws|throws
name|XPathException
block|{
try|try
block|{
specifier|final
name|Method
name|mFindRealm
init|=
name|sm
operator|.
name|getClass
argument_list|()
operator|.
name|getDeclaredMethod
argument_list|(
literal|"findRealmForRealmId"
argument_list|,
name|String
operator|.
name|class
argument_list|)
decl_stmt|;
name|mFindRealm
operator|.
name|setAccessible
argument_list|(
literal|true
argument_list|)
expr_stmt|;
specifier|final
name|Realm
name|realm
init|=
operator|(
name|Realm
operator|)
name|mFindRealm
operator|.
name|invoke
argument_list|(
name|sm
argument_list|,
name|LDAPRealm
operator|.
name|ID
argument_list|)
decl_stmt|;
if|if
condition|(
name|realm
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
literal|"The LDAP Realm is not in use!"
argument_list|)
throw|;
block|}
return|return
operator|(
name|LDAPRealm
operator|)
name|realm
return|;
block|}
catch|catch
parameter_list|(
specifier|final
name|NoSuchMethodException
name|ex
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"The LDAP Realm is not in use!"
argument_list|,
name|ex
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|SecurityException
decl||
name|IllegalArgumentException
decl||
name|IllegalAccessException
name|se
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"Permission to access the LDAP Realm is denied: "
operator|+
name|se
operator|.
name|getMessage
argument_list|()
argument_list|,
name|se
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
specifier|final
name|InvocationTargetException
name|ite
parameter_list|)
block|{
throw|throw
operator|new
name|XPathException
argument_list|(
name|this
argument_list|,
literal|"An error occured whilst accessing the LDAP Realm: "
operator|+
name|ite
operator|.
name|getMessage
argument_list|()
argument_list|,
name|ite
argument_list|)
throw|;
block|}
block|}
block|}
end_class

end_unit

