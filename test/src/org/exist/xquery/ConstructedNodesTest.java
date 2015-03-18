begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2007-2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *    * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *   * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *    *  $Id$  */
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
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|DatabaseInstanceManager
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
name|Database
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
name|XPathQueryService
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_comment
comment|/** Tests for various constructed node operations (in-memory nodes)  * @author Adam Retter<adam.retter@devon.gov.uk>  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|ConstructedNodesTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|TEST_DB_USER
init|=
literal|"admin"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_DB_PWD
init|=
literal|""
decl_stmt|;
specifier|private
name|XPathQueryService
name|service
decl_stmt|;
specifier|private
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
name|Database
name|database
init|=
literal|null
decl_stmt|;
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
throws|throws
name|XPathException
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|ConstructedNodesTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Constructor for ConstructedNodesTest. 	 * @param arg0 	 */
specifier|public
name|ConstructedNodesTest
parameter_list|(
name|String
name|arg0
parameter_list|)
block|{
name|super
argument_list|(
name|arg0
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Iteratively constructs some nodes 	 */
specifier|public
name|void
name|testIterateConstructNodes
parameter_list|()
throws|throws
name|XPathException
block|{
name|String
name|xquery
init|=
literal|"declare variable $categories := \n"
operator|+
literal|"<categories>\n"
operator|+
literal|"<category uid=\"1\">Fruit</category>\n"
operator|+
literal|"<category uid=\"2\">Vegetable</category>\n"
operator|+
literal|"<category uid=\"3\">Meat</category>\n"
operator|+
literal|"<category uid=\"4\">Dairy</category>\n"
operator|+
literal|"</categories>\n"
operator|+
literal|";\n\n"
operator|+
literal|"for $category in $categories/category return\n"
operator|+
literal|"	element option {\n"
operator|+
literal|"		attribute value {\n"
operator|+
literal|"			$category/@uid\n"
operator|+
literal|"		},\n"
operator|+
literal|"		text { $category }\n"
operator|+
literal|"	}"
decl_stmt|;
name|String
name|expectedResults
index|[]
init|=
block|{
literal|"<option value=\"1\">Fruit</option>"
block|,
literal|"<option value=\"2\">Vegetable</option>"
block|,
literal|"<option value=\"3\">Meat</option>"
block|,
literal|"<option value=\"4\">Dairy</option>"
block|}
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
operator|.
name|length
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedResults
index|[
name|i
index|]
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
block|}
comment|/*** 	 * Test sorting of constructed nodes 	 */
specifier|public
name|void
name|testConstructedNodesSort
parameter_list|()
block|{
name|String
name|xquery
init|=
literal|"declare variable $categories := \n"
operator|+
literal|"<categories>\n"
operator|+
literal|"<category uid=\"1\">Fruit</category>\n"
operator|+
literal|"<category uid=\"2\">Vegetable</category>\n"
operator|+
literal|"<category uid=\"3\">Meat</category>\n"
operator|+
literal|"<category uid=\"4\">Dairy</category>\n"
operator|+
literal|"</categories>\n"
operator|+
literal|";\n\n"
operator|+
literal|"for $category in $categories/category order by $category/@uid descending return $category"
decl_stmt|;
name|String
name|expectedResults
index|[]
init|=
block|{
literal|"<category uid=\"4\">Dairy</category>"
block|,
literal|"<category uid=\"3\">Meat</category>"
block|,
literal|"<category uid=\"2\">Vegetable</category>"
block|,
literal|"<category uid=\"1\">Fruit</category>"
block|}
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
operator|.
name|length
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedResults
index|[
name|i
index|]
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
block|}
comment|/** 	 * Test retrieving sorted nodes by position 	 */
specifier|public
name|void
name|bugtestConstructedNodesPosition
parameter_list|()
block|{
name|String
name|xquery
init|=
literal|"declare variable $categories := \n"
operator|+
literal|"<categories>\n"
operator|+
literal|"<category uid=\"1\">Fruit</category>\n"
operator|+
literal|"<category uid=\"2\">Vegetable</category>\n"
operator|+
literal|"<category uid=\"3\">Meat</category>\n"
operator|+
literal|"<category uid=\"4\">Dairy</category>\n"
operator|+
literal|"</categories>\n"
operator|+
literal|";\n\n"
operator|+
literal|"$categories/category[1],\n"
operator|+
literal|"$categories/category[position() eq 1]"
decl_stmt|;
name|String
name|expectedResults
index|[]
init|=
block|{
literal|"<option value=\"1\">Fruit</option>"
block|,
literal|"<option value=\"1\">Fruit</option>"
block|}
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
operator|.
name|length
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedResults
index|[
name|i
index|]
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
block|}
comment|/** 	 * Test storing constructed (text) nodes 	 * Tests absence of bug #2646744 which gave err:XPTY0018 for $hello-text-first 	 * cf org.exist.xquery.XQueryTest.testXPTY0018_mixedsequences_2429093() 	 */
specifier|public
name|void
name|testConstructedTextNodes
parameter_list|()
block|{
name|String
name|xquery
init|=
literal|"declare variable $hello-text-first :=<a>{ \"hello\" }<b>world</b></a>;\n"
operator|+
literal|"declare variable $hello-text-last :=<a><b>world</b>{ \"hello\" }</a>;\n"
operator|+
literal|"($hello-text-first, $hello-text-last)"
decl_stmt|;
name|String
name|expectedResults
index|[]
init|=
block|{
literal|"<a>hello<b>world</b></a>"
block|,
literal|"<a><b>world</b>hello</a>"
block|}
decl_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|String
name|oki
init|=
name|service
operator|.
name|getProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|)
decl_stmt|;
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
operator|.
name|length
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedResults
index|[
name|i
index|]
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// Restore indent property to ensure test atomicity
name|service
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
name|oki
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
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
block|}
comment|/* 	 * @see TestCase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
comment|// initialize driver
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
argument_list|,
name|TEST_DB_USER
argument_list|,
name|TEST_DB_PWD
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|XPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
comment|/* 	 * @see TestCase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
comment|// clear instance variables
name|service
operator|=
literal|null
expr_stmt|;
name|root
operator|=
literal|null
expr_stmt|;
block|}
block|}
end_class

end_unit

