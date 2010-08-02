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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|ByteArrayInputStream
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|InputStream
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
name|User
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|AfterClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|BeforeClass
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Test
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|ActiveDirectoryRealmTest
block|{
specifier|private
specifier|static
name|String
name|config
init|=
literal|"<ActiveDirectory>"
operator|+
literal|"<context "
operator|+
literal|"		domain='local.domain' "
operator|+
literal|"		searchBase='cn=users,dc=local,dc=domain' "
operator|+
literal|"		url='ldap://localhost:389'/>"
operator|+
literal|"</ActiveDirectory>"
decl_stmt|;
specifier|private
specifier|static
name|ActiveDirectoryRealm
name|realm
decl_stmt|;
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|config
operator|.
name|getBytes
argument_list|(
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
name|Configurator
operator|.
name|parse
argument_list|(
name|is
argument_list|)
decl_stmt|;
name|realm
operator|=
operator|new
name|ActiveDirectoryRealm
argument_list|(
name|config
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
block|}
comment|/** 	 * Test method for {@link org.exist.security.realm.activedirectory.ActiveDirectoryRealm#authenticate(java.lang.String, java.lang.Object)}. 	 */
annotation|@
name|Test
specifier|public
name|void
name|testAuthenticate
parameter_list|()
block|{
name|User
name|account
init|=
literal|null
decl_stmt|;
try|try
block|{
name|account
operator|=
name|realm
operator|.
name|authenticate
argument_list|(
literal|"admin"
argument_list|,
literal|"passwd"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|AuthenticationException
name|e
parameter_list|)
block|{
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|account
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

