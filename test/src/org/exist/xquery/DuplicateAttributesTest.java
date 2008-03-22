begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|XQueryService
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
name|fail
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
name|AfterClass
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
name|exist
operator|.
name|xmldb
operator|.
name|DatabaseInstanceManager
import|;
end_import

begin_class
specifier|public
class|class
name|DuplicateAttributesTest
block|{
specifier|private
specifier|static
name|Collection
name|testCollection
decl_stmt|;
specifier|private
specifier|static
name|String
name|STORED_DOC1
init|=
literal|"<node attr='ab'/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|STORED_DOC2
init|=
literal|"<node attr2='ab'/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|DOC_WITH_DTD
init|=
literal|"<!DOCTYPE IDS [\n"
operator|+
literal|"<!ELEMENT IDS (elementwithid-1+, elementwithid-2+,\n"
operator|+
literal|"               elementwithidrefattr-1+,elementwithidrefattr-2+)>\n"
operator|+
literal|"<!ELEMENT elementwithid-1 (#PCDATA)>\n"
operator|+
literal|"<!ELEMENT elementwithid-2 (#PCDATA)>\n"
operator|+
literal|"<!ELEMENT elementwithidrefattr-1 (#PCDATA)>\n"
operator|+
literal|"<!ELEMENT elementwithidrefattr-2 (#PCDATA)>\n"
operator|+
literal|"<!ATTLIST elementwithid-1 anId  ID #REQUIRED>\n"
operator|+
literal|"<!ATTLIST elementwithid-2 anId  ID #REQUIRED>\n"
operator|+
literal|"<!ATTLIST elementwithidrefattr-1 anIdRef IDREF #REQUIRED>  \n"
operator|+
literal|"<!ATTLIST elementwithidrefattr-2 anIdRef IDREF #REQUIRED>\n"
operator|+
literal|"]>\n"
operator|+
literal|"<IDS>\n"
operator|+
literal|"<elementwithid-1 anId = \"id1\"/>\n"
operator|+
literal|"<elementwithid-2 anId = \"id2\"/>\n"
operator|+
literal|"<elementwithidrefattr-1 anIdRef = \"id1\"/>\n"
operator|+
literal|"<elementwithidrefattr-2 anIdRef = \"id2\"/> \n"
operator|+
literal|"</IDS>"
decl_stmt|;
comment|/**      * Add attribute to element which already has an attribute of that name.      */
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
name|appendStoredAttrFail
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XQueryService
name|xqs
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
literal|"let $a := \n"
operator|+
literal|"<node attr=\"a\" b=\"c\">{doc(\"/db/test/stored1.xml\")//@attr}</node>"
operator|+
literal|"return $a"
decl_stmt|;
name|xqs
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add attribute to element which has no conflicting attributes.      */
annotation|@
name|Test
specifier|public
name|void
name|appendStoredAttrOK
parameter_list|()
block|{
try|try
block|{
name|XQueryService
name|xqs
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
literal|"let $a := \n"
operator|+
literal|"<node attr=\"a\" b=\"c\">{doc(\"/db/test/stored2.xml\")//@attr2}</node>"
operator|+
literal|"return $a"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqs
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
name|assertEquals
argument_list|(
literal|"<node attr=\"a\" b=\"c\" attr2=\"ab\"/>"
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
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
comment|/**      * Add constructed in-memory attribute to element which already has an      * attribute of that name.      */
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
name|appendConstrAttr
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XQueryService
name|xqs
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
literal|"let $a :=<root attr=\"ab\"/>"
operator|+
literal|"let $b := \n"
operator|+
literal|"<node attr=\"a\" b=\"c\">{$a//@attr}</node>"
operator|+
literal|"return $a"
decl_stmt|;
name|xqs
operator|.
name|query
argument_list|(
name|query
argument_list|)
expr_stmt|;
block|}
comment|/**      * Add attribute to element which already has an      * attribute of that name (using idref).      */
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
name|appendIdref
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XQueryService
name|xqs
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
literal|"<results>{fn:idref(('id1', 'id2'), doc('/db/test/docdtd.xml')/IDS)}</results>"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xqs
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
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
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|initDB
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
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|Resource
name|resource
init|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"stored1.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|STORED_DOC1
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"stored2.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|STORED_DOC2
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"docdtd.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|DOC_WITH_DTD
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|stopDB
parameter_list|()
block|{
try|try
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
literal|"test"
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|mgr
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
name|mgr
operator|.
name|shutdown
argument_list|()
expr_stmt|;
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
block|}
end_class

end_unit

