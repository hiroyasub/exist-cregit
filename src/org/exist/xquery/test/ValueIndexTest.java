begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|test
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|IndexQueryService
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
name|XMLResource
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

begin_comment
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ValueIndexTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist:///db"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index xmlns:x=\"http://www.foo.com\">"
operator|+
literal|"<fulltext default=\"all\">"
operator|+
literal|"<include path=\"//item/name\"/>"
operator|+
literal|"<include path=\"//item/mixed\"/>"
operator|+
literal|"</fulltext>"
operator|+
literal|"<create path=\"//item/itemno\" type=\"xs:integer\"/>"
operator|+
literal|"<create path=\"//item/name\" type=\"xs:string\"/>"
operator|+
literal|"<create path=\"//item/stock\" type=\"xs:integer\"/>"
operator|+
literal|"<create path=\"//item/price\" type=\"xs:double\"/>"
operator|+
literal|"<create path=\"//item/prices/@specialprice\" type=\"xs:boolean\"/>"
operator|+
literal|"<create path=\"//item/x:rating\" type=\"xs:double\"/>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
comment|// initialize driver
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
decl_stmt|;
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|service
init|=
operator|(
name|CollectionManagementService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|testCollection
operator|=
name|service
operator|.
name|createCollection
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|IndexQueryService
name|idxConf
init|=
operator|(
name|IndexQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"IndexQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|idxConf
operator|.
name|configureCollection
argument_list|(
name|CONFIG
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ClassNotFoundException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|InstantiationException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|IllegalAccessException
name|e
parameter_list|)
block|{
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
specifier|public
name|void
name|testStrings
parameter_list|()
throws|throws
name|Exception
block|{
name|XPathQueryService
name|service
init|=
name|storeXMLFileAndGetQueryService
argument_list|(
literal|"items.xml"
argument_list|,
literal|"src/org/exist/xquery/test/items.xml"
argument_list|)
decl_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[name = 'Racing Bicycle']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[name> 'Racing Bicycle']"
argument_list|,
literal|4
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[itemno = 3]"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[stock<= 10]"
argument_list|,
literal|5
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[stock> 20]"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"declare namespace x=\"http://www.foo.com\"; //item[x:rating> 8.0]"
argument_list|,
literal|2
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[name&= 'Racing Bicycle']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|queryResource
argument_list|(
name|service
argument_list|,
literal|"items.xml"
argument_list|,
literal|"//item[mixed = 'uneven']"
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XPathQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|)
throws|throws
name|XMLDBException
block|{
return|return
name|queryResource
argument_list|(
name|service
argument_list|,
name|resource
argument_list|,
name|query
argument_list|,
name|expected
argument_list|,
literal|null
argument_list|)
return|;
block|}
comment|/**      * @param service      * @throws XMLDBException      */
specifier|private
name|ResourceSet
name|queryResource
parameter_list|(
name|XPathQueryService
name|service
parameter_list|,
name|String
name|resource
parameter_list|,
name|String
name|query
parameter_list|,
name|int
name|expected
parameter_list|,
name|String
name|message
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|ResourceSet
name|result
init|=
name|service
operator|.
name|queryResource
argument_list|(
name|resource
argument_list|,
name|query
argument_list|)
decl_stmt|;
if|if
condition|(
name|message
operator|==
literal|null
condition|)
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
else|else
name|assertEquals
argument_list|(
name|message
argument_list|,
name|expected
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
return|return
name|result
return|;
block|}
comment|/**      * @return      * @throws XMLDBException      */
specifier|private
name|XPathQueryService
name|storeXMLFileAndGetQueryService
parameter_list|(
name|String
name|documentName
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
name|documentName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|File
name|f
init|=
operator|new
name|File
argument_list|(
name|path
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
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
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ValueIndexTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

