begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2004-2012 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
package|;
end_package

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
name|ExistXmldbEmbeddedServer
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
name|Resource
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
name|ResourceSet
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
name|CollectionManagementService
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
name|XPathQueryService
import|;
end_import

begin_class
specifier|public
class|class
name|AnnotationsTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|final
specifier|static
name|ExistXmldbEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|public
name|AnnotationsTest
parameter_list|()
block|{
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|ClassNotFoundException
throws|,
name|InstantiationException
throws|,
name|IllegalAccessException
block|{
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
comment|// testCollection.removeResource( testCollection .getResource(file_name));
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
expr_stmt|;
block|}
specifier|private
name|Collection
name|getTestCollection
parameter_list|()
throws|throws
name|XMLDBException
block|{
return|return
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db/test"
argument_list|,
literal|"admin"
argument_list|,
literal|""
argument_list|)
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|annotation
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://world.com';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|result
operator|.
name|getIterator
argument_list|()
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_VALUE_CONSTANT
argument_list|,
name|res
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|annotationWithLiterals
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://world.com';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world('a=b', 'b=c')\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|result
operator|.
name|getIterator
argument_list|()
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|TEST_VALUE_CONSTANT
argument_list|,
name|res
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXMLNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/XML/1998/namespace';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXMLSchemaNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/2001/XMLSchema';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXMLSchemaInstanceNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/2001/XMLSchema-instance';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXPathFunctionsNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/2005/xpath-functions';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXPathFunctionsMathNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/2005/xpath-functions/math';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|XMLDBException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|annotationInXQueryOptionsNamespaceFails
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|TEST_VALUE_CONSTANT
init|=
literal|"hello world"
decl_stmt|;
specifier|final
name|String
name|query
init|=
literal|"declare namespace hello = 'http://www.w3.org/2011/xquery-options';\n"
operator|+
literal|"declare\n"
operator|+
literal|"%hello:world\n"
operator|+
literal|"function local:hello() {\n"
operator|+
literal|"'"
operator|+
name|TEST_VALUE_CONSTANT
operator|+
literal|"'\n"
operator|+
literal|"};\n"
operator|+
literal|"local:hello()"
decl_stmt|;
specifier|final
name|XPathQueryService
name|service
init|=
name|getQueryService
argument_list|()
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
specifier|private
name|XPathQueryService
name|getQueryService
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|testCollection
init|=
name|getTestCollection
argument_list|()
decl_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
return|return
name|service
return|;
block|}
block|}
end_class

end_unit

