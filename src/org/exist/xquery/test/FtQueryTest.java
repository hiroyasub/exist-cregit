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
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLTestCase
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
name|XQueryService
import|;
end_import

begin_class
specifier|public
class|class
name|FtQueryTest
extends|extends
name|XMLTestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|FILES
index|[]
init|=
block|{
literal|"hamlet.xml"
block|,
literal|"macbeth.xml"
block|,
literal|"r_and_j.xml"
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|File
name|SHAKES_DIR
init|=
operator|new
name|File
argument_list|(
literal|"samples"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"shakespeare"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|File
name|MODS_DIR
init|=
operator|new
name|File
argument_list|(
literal|"samples"
operator|+
name|File
operator|.
name|separator
operator|+
literal|"mods"
argument_list|)
decl_stmt|;
specifier|private
name|Database
name|database
decl_stmt|;
specifier|private
name|Collection
name|testCollection
decl_stmt|;
specifier|public
name|void
name|testFtOperators
parameter_list|()
throws|throws
name|Exception
block|{
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'love']"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|160
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'thou']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|290
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'thou']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|290
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'fenny snake']/LINE[1]"
argument_list|)
expr_stmt|;
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
name|assertXMLEqual
argument_list|(
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"<LINE>Fillet of a fenny snake,</LINE>"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'god*']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|79
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'god in heaven']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[SPEAKER&= 'Nurse']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|90
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"declare namespace mods='http://www.loc.gov/mods/v3'; //mods:titleInfo[mods:title&= 'self*']"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"declare namespace mods='http://www.loc.gov/mods/v3'; //mods:titleInfo[mods:title&= 'self employed']"
argument_list|)
expr_stmt|;
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
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"declare namespace mods='http://www.loc.gov/mods/v3'; //mods:titleInfo[match-all(mods:title, '.*ploy.*')]"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFtScan
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|queryBody
init|=
literal|"declare namespace f=\'http://exist-db.org/xquery/test\';\n"
operator|+
literal|"declare namespace mods='http://www.loc.gov/mods/v3';\n"
operator|+
literal|"import module namespace t=\'http://exist-db.org/xquery/text\';\n"
operator|+
literal|"\n"
operator|+
literal|"declare function f:term-callback($term as xs:string, $data as xs:int+)\n"
operator|+
literal|"as element()+ {\n"
operator|+
literal|"<item>\n"
operator|+
literal|"<term>{$term}</term>\n"
operator|+
literal|"<frequency>{$data[1]}</frequency>\n"
operator|+
literal|"</item>\n"
operator|+
literal|"};\n"
operator|+
literal|"\n"
decl_stmt|;
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
name|queryBody
operator|+
literal|"t:index-terms(collection(\'/db\'), \'is\', util:function(\'f:term-callback\', 2), 1000)"
decl_stmt|;
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
literal|7
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
name|queryBody
operator|+
literal|"t:index-terms(collection(\'/db\')//LINE, \'is\', util:function(\'f:term-callback\', 2), 1000)"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|6
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|query
operator|=
name|queryBody
operator|+
literal|"t:index-terms(collection(\'/db\')//mods:title, \'s\', util:function(\'f:term-callback\', 2), 1000)"
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|20
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testFtUpdate
parameter_list|()
throws|throws
name|Exception
block|{
name|XQueryService
name|service
init|=
operator|(
name|XQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|service
operator|.
name|query
argument_list|(
literal|"update insert<SPEAKER>First Witch</SPEAKER> preceding //SPEECH[LINE&= 'fenny snake']/SPEAKER"
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'fenny snake']/SPEAKER"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'fenny snake' and SPEAKER&= 'first']"
argument_list|)
expr_stmt|;
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
name|service
operator|.
name|query
argument_list|(
literal|"update delete //SPEECH[LINE&= 'fenny snake']/SPEAKER[2]"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"//SPEECH[LINE&= 'fenny snake' and SPEAKER&= 'first']"
argument_list|)
expr_stmt|;
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
block|}
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db"
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
literal|"testft"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
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
name|FILES
operator|.
name|length
condition|;
name|i
operator|++
control|)
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
name|FILES
index|[
name|i
index|]
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
operator|new
name|File
argument_list|(
name|SHAKES_DIR
argument_list|,
name|FILES
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
operator|.
name|getResource
argument_list|(
name|FILES
index|[
name|i
index|]
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|File
name|modsFiles
index|[]
init|=
name|MODS_DIR
operator|.
name|listFiles
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|modsFiles
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|modsFiles
index|[
name|i
index|]
operator|.
name|isFile
argument_list|()
condition|)
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
name|modsFiles
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
name|modsFiles
index|[
name|i
index|]
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
operator|.
name|getResource
argument_list|(
name|modsFiles
index|[
name|i
index|]
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
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
comment|/*      * @see TestCase#tearDown()      */
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist:///db"
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
name|service
operator|.
name|removeCollection
argument_list|(
literal|"testft"
argument_list|)
expr_stmt|;
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
name|testCollection
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"tearDown PASSED"
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

