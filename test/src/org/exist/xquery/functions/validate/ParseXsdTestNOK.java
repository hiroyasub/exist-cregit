begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: JingSchematronTest.java 9705 2009-08-08 13:52:37Z dizzzz $  */
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
name|validate
package|;
end_package

begin_import
import|import
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|exceptions
operator|.
name|XpathException
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
name|ExistXmldbEmbeddedServer
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
name|FileUtils
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
import|import static
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
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
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|function
operator|.
name|Predicate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xml
operator|.
name|sax
operator|.
name|SAXException
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

begin_comment
comment|/**  * Tests for the validation:jing() function with SCHs.  *   * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|ParseXsdTestNOK
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
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
specifier|private
specifier|static
specifier|final
name|String
name|noValidation
init|=
literal|"<?xml version='1.0'?>"
operator|+
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<validation mode=\"no\"/>"
operator|+
literal|"</collection>"
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|prepareResources
parameter_list|()
throws|throws
name|Exception
block|{
comment|// Switch off validation
name|Collection
name|conf
init|=
literal|null
decl_stmt|;
try|try
block|{
name|conf
operator|=
name|existEmbeddedServer
operator|.
name|createCollection
argument_list|(
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"system/config/db/addressbook"
argument_list|)
expr_stmt|;
name|ExistXmldbEmbeddedServer
operator|.
name|storeResource
argument_list|(
name|conf
argument_list|,
literal|"collection.xconf"
argument_list|,
name|noValidation
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|conf
operator|!=
literal|null
condition|)
block|{
name|conf
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
comment|// Create filter
specifier|final
name|Predicate
argument_list|<
name|Path
argument_list|>
name|filter
init|=
name|path
lambda|->
block|{
specifier|final
name|String
name|fileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|fileName
operator|.
name|endsWith
argument_list|(
literal|"xml"
argument_list|)
operator|||
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"address"
argument_list|)
return|;
block|}
decl_stmt|;
comment|// Store schematron 1.5 test files
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|existEmbeddedServer
operator|.
name|createCollection
argument_list|(
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
argument_list|,
literal|"addressbook"
argument_list|)
expr_stmt|;
specifier|final
name|Path
name|sources
init|=
name|TestUtils
operator|.
name|resolveSample
argument_list|(
literal|"validation/addressbook"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|file
range|:
name|FileUtils
operator|.
name|list
argument_list|(
name|sources
argument_list|,
name|filter
argument_list|)
control|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|TestUtils
operator|.
name|readFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|ExistXmldbEmbeddedServer
operator|.
name|storeResource
argument_list|(
name|collection
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|xsd_stored_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XpathException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jaxp-report( "
operator|+
literal|"doc('/db/addressbook/addressbook_valid.xml'), "
operator|+
literal|"xs:anyURI('/db/addressbook/addressbook.xsd'), () )"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"valid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"todo"
argument_list|)
specifier|public
name|void
name|xsd_stored_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XpathException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jaxp-report( doc('/db/tournament/1.5/Tournament-invalid.xml'), "
operator|+
literal|"doc('/db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"invalid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"todo"
argument_list|)
specifier|public
name|void
name|xsd_anyuri_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XpathException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jaxp-report( xs:anyURI('xmldb:exist:///db/tournament/1.5/Tournament-valid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"valid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
annotation|@
name|Ignore
argument_list|(
literal|"todo"
argument_list|)
specifier|public
name|void
name|xsd_anyuri_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XpathException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jaxp-report( xs:anyURI('xmldb:exist:///db/tournament/1.5/Tournament-invalid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"invalid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

