begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|inspect
package|;
end_package

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
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|memtree
operator|.
name|ElementImpl
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
name|storage
operator|.
name|BrokerPool
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
name|DBBroker
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
name|lock
operator|.
name|Lock
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
name|txn
operator|.
name|Txn
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
name|ExistEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|LockException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|FastByteArrayInputStream
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
name|XQuery
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
name|Item
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
name|junit
operator|.
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|java
operator|.
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertNotNull
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
name|assertTrue
import|;
end_import

begin_class
specifier|public
class|class
name|InspectModuleTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test-inspectModule"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|TEST_MODULE
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xqm"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|MODULE
init|=
literal|"xquery version \"1.0\";\n"
operator|+
literal|"module namespace x = \"http://xyz.com\";\n"
operator|+
literal|"\n"
operator|+
literal|"(:~\n"
operator|+
literal|" : Some description.\n"
operator|+
literal|" : @return taxonomy[@type = \"reign\"]\n"
operator|+
literal|" :)\n"
operator|+
literal|"declare function x:fun1() as xs:string {\n"
operator|+
literal|"  \"hello from fun1\"\n"
operator|+
literal|"};\n"
operator|+
literal|"\n"
operator|+
literal|"(:~\n"
operator|+
literal|" : Some other description.\n"
operator|+
literal|" : \n"
operator|+
literal|" : @param one first parameter\n"
operator|+
literal|" : @param two second parameter\n"
operator|+
literal|" : \n"
operator|+
literal|" : @return our result\n"
operator|+
literal|" :)\n"
operator|+
literal|"declare function x:fun2($one as xs:int, $two as xs:float) as xs:string {\n"
operator|+
literal|"  \"hello from fun2\"\n"
operator|+
literal|"};\n"
operator|+
literal|"\n"
operator|+
literal|"(:~\n"
operator|+
literal|" : This is a multiline description and therefore\n"
operator|+
literal|" : spans multiple\n"
operator|+
literal|" : lines.\n"
operator|+
literal|" : \n"
operator|+
literal|" : @return another result\n"
operator|+
literal|" :)\n"
operator|+
literal|"declare function x:fun3() {\n"
operator|+
literal|"  \"hello from fun3\"\n"
operator|+
literal|"};\n"
operator|+
literal|"\n"
operator|+
literal|"(:~\n"
operator|+
literal|" : An annotated function.\n"
operator|+
literal|" : \n"
operator|+
literal|" : @return another result\n"
operator|+
literal|" :)\n"
operator|+
literal|"declare %public %rest:path(\"/x/y/z\") function x:fun4() {\n"
operator|+
literal|"  \"hello from fun4\"\n"
operator|+
literal|"};\n"
decl_stmt|;
annotation|@
name|Ignore
argument_list|(
literal|"https://github.com/eXist-db/exist/issues/1386"
argument_list|)
annotation|@
name|Test
specifier|public
name|void
name|xqDoc_withAtSignInline
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace inspect = \"http://exist-db.org/xquery/inspection\";\n"
operator|+
literal|"inspect:inspect-module(xs:anyURI(\"xmldb:exist://"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|TEST_MODULE
argument_list|)
operator|.
name|toCollectionPathURI
argument_list|()
operator|+
literal|"\"))\n"
operator|+
literal|"/function[@name eq \"x:fun1\"]"
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|item1
init|=
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|item1
operator|instanceof
name|ElementImpl
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|function
init|=
operator|(
name|Element
operator|)
name|item1
decl_stmt|;
specifier|final
name|NodeList
name|descriptions
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|descriptions
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Some description."
argument_list|,
name|descriptions
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|arguments
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"argument"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|arguments
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|returns
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"returns"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|returns
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"taxonomy[@type = \"reign\"]"
argument_list|,
name|returns
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|xqDoc_withParamsAndReturn
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace inspect = \"http://exist-db.org/xquery/inspection\";\n"
operator|+
literal|"inspect:inspect-module(xs:anyURI(\"xmldb:exist://"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|TEST_MODULE
argument_list|)
operator|.
name|toCollectionPathURI
argument_list|()
operator|+
literal|"\"))\n"
operator|+
literal|"/function[@name eq \"x:fun2\"]"
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|item1
init|=
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|item1
operator|instanceof
name|ElementImpl
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|function
init|=
operator|(
name|Element
operator|)
name|item1
decl_stmt|;
specifier|final
name|NodeList
name|descriptions
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|descriptions
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Some other description."
argument_list|,
name|descriptions
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|arguments
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"argument"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|arguments
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"first parameter"
argument_list|,
name|arguments
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"second parameter"
argument_list|,
name|arguments
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|returns
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"returns"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|returns
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"our result"
argument_list|,
name|returns
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|xqDoc_multilineDesciption
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace inspect = \"http://exist-db.org/xquery/inspection\";\n"
operator|+
literal|"inspect:inspect-module(xs:anyURI(\"xmldb:exist://"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|TEST_MODULE
argument_list|)
operator|.
name|toCollectionPathURI
argument_list|()
operator|+
literal|"\"))\n"
operator|+
literal|"/function[@name eq \"x:fun3\"]"
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|item1
init|=
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|item1
operator|instanceof
name|ElementImpl
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|function
init|=
operator|(
name|Element
operator|)
name|item1
decl_stmt|;
specifier|final
name|NodeList
name|descriptions
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|descriptions
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"This is a multiline description and therefore\n spans multiple\n lines."
argument_list|,
name|descriptions
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|arguments
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"argument"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|arguments
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|returns
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"returns"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|returns
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"another result"
argument_list|,
name|returns
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
comment|//@Ignore("https://github.com/eXist-db/exist/issues/1386")
annotation|@
name|Test
specifier|public
name|void
name|xqDoc_onAnnotatedFunction
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xqueryService
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|String
name|query
init|=
literal|"import module namespace inspect = \"http://exist-db.org/xquery/inspection\";\n"
operator|+
literal|"inspect:inspect-module(xs:anyURI(\"xmldb:exist://"
operator|+
name|TEST_COLLECTION
operator|.
name|append
argument_list|(
name|TEST_MODULE
argument_list|)
operator|.
name|toCollectionPathURI
argument_list|()
operator|+
literal|"\"))\n"
operator|+
literal|"/function[@name eq \"x:fun4\"]"
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xqueryService
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|query
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|item1
init|=
name|result
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|item1
operator|instanceof
name|ElementImpl
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|function
init|=
operator|(
name|Element
operator|)
name|item1
decl_stmt|;
specifier|final
name|NodeList
name|descriptions
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"description"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|descriptions
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"An annotated function."
argument_list|,
name|descriptions
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|annotations
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"annotation"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|annotations
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"public"
argument_list|,
operator|(
operator|(
name|Element
operator|)
name|annotations
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"rest:path"
argument_list|,
operator|(
operator|(
name|Element
operator|)
name|annotations
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|)
operator|.
name|getAttribute
argument_list|(
literal|"name"
argument_list|)
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"/x/y/z"
argument_list|,
name|annotations
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|arguments
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"argument"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|arguments
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|returns
init|=
name|function
operator|.
name|getElementsByTagName
argument_list|(
literal|"returns"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|returns
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"another result"
argument_list|,
name|returns
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getFirstChild
argument_list|()
operator|.
name|getNodeValue
argument_list|()
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setup
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|LockException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|MODULE
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
init|)
block|{
name|testCollection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TEST_MODULE
argument_list|,
name|is
argument_list|,
literal|"application/xquery"
argument_list|,
name|MODULE
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|teardown
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|testCollection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|testCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TEST_COLLECTION
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

