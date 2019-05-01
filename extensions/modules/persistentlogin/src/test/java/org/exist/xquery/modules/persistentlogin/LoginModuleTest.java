begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|modules
operator|.
name|persistentlogin
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpEntity
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpResponse
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|HttpClient
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|client
operator|.
name|methods
operator|.
name|HttpGet
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|BasicCookieStore
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|impl
operator|.
name|client
operator|.
name|HttpClientBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|util
operator|.
name|EntityUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistWebServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|TestConstants
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|EXistResource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|UserManagementService
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|XmldbURI
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
name|ClassRule
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

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|XMLDBException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|BinaryResource
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|http
operator|.
name|HttpStatus
operator|.
name|SC_OK
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
import|;
end_import

begin_class
specifier|public
class|class
name|LoginModuleTest
block|{
specifier|private
specifier|static
name|String
name|XQUERY
init|=
literal|"import module namespace login=\"http://exist-db.org/xquery/login\" "
operator|+
literal|"at \"resource:org/exist/xquery/modules/persistentlogin/login.xql\";"
operator|+
literal|"login:set-user('org.exist.login', (), false()),"
operator|+
literal|"sm:id()/(descendant::sm:effective,descendant::sm:real)[1]/sm:username/string()"
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY_FILENAME
init|=
literal|"test-login.xql"
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|root
decl_stmt|;
specifier|private
specifier|static
name|HttpClient
name|client
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|beforeClass
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/xmlrpc"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_PWD
argument_list|)
expr_stmt|;
specifier|final
name|BinaryResource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|root
operator|.
name|createResource
argument_list|(
name|XQUERY_FILENAME
argument_list|,
literal|"BinaryResource"
argument_list|)
decl_stmt|;
operator|(
operator|(
name|EXistResource
operator|)
name|res
operator|)
operator|.
name|setMimeType
argument_list|(
literal|"application/xquery"
argument_list|)
expr_stmt|;
name|res
operator|.
name|setContent
argument_list|(
name|XQUERY
argument_list|)
expr_stmt|;
name|root
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
name|res
argument_list|,
literal|0777
argument_list|)
expr_stmt|;
specifier|final
name|BasicCookieStore
name|store
init|=
operator|new
name|BasicCookieStore
argument_list|()
decl_stmt|;
name|client
operator|=
name|HttpClientBuilder
operator|.
name|create
argument_list|()
operator|.
name|setDefaultCookieStore
argument_list|(
name|store
argument_list|)
operator|.
name|build
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|afterClass
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|BinaryResource
name|res
init|=
operator|(
name|BinaryResource
operator|)
name|root
operator|.
name|getResource
argument_list|(
name|XQUERY_FILENAME
argument_list|)
decl_stmt|;
name|root
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|loginAndLogout
parameter_list|()
throws|throws
name|IOException
block|{
comment|// not logged in
name|doGet
argument_list|(
literal|null
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|)
expr_stmt|;
comment|// log in as admin
name|doGet
argument_list|(
literal|"user="
operator|+
name|TestUtils
operator|.
name|ADMIN_DB_USER
operator|+
literal|"&password="
operator|+
name|TestUtils
operator|.
name|ADMIN_DB_PWD
operator|+
literal|"&duration=P1D"
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|)
expr_stmt|;
comment|// second request should stay logged in
name|doGet
argument_list|(
literal|null
argument_list|,
name|TestUtils
operator|.
name|ADMIN_DB_USER
argument_list|)
expr_stmt|;
comment|// log off returns to guest user
name|doGet
argument_list|(
literal|"logout=true"
argument_list|,
name|TestUtils
operator|.
name|GUEST_DB_USER
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|doGet
parameter_list|(
annotation|@
name|Nullable
name|String
name|params
parameter_list|,
name|String
name|expected
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
name|HttpGet
name|httpGet
init|=
operator|new
name|HttpGet
argument_list|(
literal|"http://localhost:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/rest"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|'/'
operator|+
name|XQUERY_FILENAME
operator|+
operator|(
name|params
operator|==
literal|null
condition|?
literal|""
else|:
literal|"?"
operator|+
name|params
operator|)
argument_list|)
decl_stmt|;
name|HttpResponse
name|response
init|=
name|client
operator|.
name|execute
argument_list|(
name|httpGet
argument_list|)
decl_stmt|;
name|HttpEntity
name|entity
init|=
name|response
operator|.
name|getEntity
argument_list|()
decl_stmt|;
specifier|final
name|String
name|responseBody
init|=
name|EntityUtils
operator|.
name|toString
argument_list|(
name|entity
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|responseBody
argument_list|,
name|SC_OK
argument_list|,
name|response
operator|.
name|getStatusLine
argument_list|()
operator|.
name|getStatusCode
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|responseBody
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

