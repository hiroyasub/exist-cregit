begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|xquery
operator|.
name|XPathException
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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  * Tests for the validation:jing() function with NVDLs  *  * @author jim.fuller@webcomposite.com  * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|JingOnvdlTest
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
argument_list|,
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RNG_DATA1
init|=
literal|"<element  name=\'Book\' xmlns='http://relaxng.org/ns/structure/1.0'  ns=\'http://www.books.org\'> "
operator|+
literal|"<element name=\'Title\'><text/></element>"
operator|+
literal|"<element name=\'Author\'><text/></element>"
operator|+
literal|"<element name=\'Date\'><text/></element>"
operator|+
literal|"<element name=\'ISBN\'><text/></element>"
operator|+
literal|"<element name=\'Publisher\'><text/></element>"
operator|+
literal|"</element>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|NVDL_DATA1
init|=
literal|"<rules xmlns='http://purl.oclc.org/dsdl/nvdl/ns/structure/1.0'> "
operator|+
literal|"<namespace ns=\'http://www.books.org\'>"
operator|+
literal|"<validate schema=\"Book.rng\" />"
operator|+
literal|"</namespace>"
operator|+
literal|"</rules>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA1
init|=
literal|"<Book xmlns='http://www.books.org'> "
operator|+
literal|"<Title>The Wisdom of Crowds</Title>"
operator|+
literal|"<Author>James Surowiecki</Author>"
operator|+
literal|"<Date>2005</Date>"
operator|+
literal|"<ISBN>0-385-72170-6</ISBN>"
operator|+
literal|"<Publisher>Anchor Books</Publisher>"
operator|+
literal|"</Book>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA2
init|=
literal|"<Book xmlns='http://www.books.org'> "
operator|+
literal|"<Title>The Wisdom of Crowds</Title>"
operator|+
literal|"<Author>James Surowiecki</Author>"
operator|+
literal|"<Dateee>2005</Dateee>"
operator|+
literal|"<ISBN>0-385-72170-6</ISBN>"
operator|+
literal|"<Publisher>Anchor Books</Publisher>"
operator|+
literal|"</Book>"
decl_stmt|;
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|query
init|=
literal|"xmldb:create-collection('xmldb:exist:///db','validate-test')"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query1
init|=
literal|"xmldb:store('/db/validate-test', 'test.nvdl',"
operator|+
name|NVDL_DATA1
operator|+
literal|")"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query2
init|=
literal|"xmldb:store('/db/validate-test', 'Book.rng',"
operator|+
name|RNG_DATA1
operator|+
literal|")"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data1
init|=
literal|"xmldb:store('/db/validate-test', 'valid.xml',"
operator|+
name|XML_DATA1
operator|+
literal|")"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|data1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data2
init|=
literal|"xmldb:store('/db/validate-test', 'invalid.xml',"
operator|+
name|XML_DATA2
operator|+
literal|")"
decl_stmt|;
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|data2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_valid
parameter_list|()
throws|throws
name|XPathException
throws|,
name|IOException
throws|,
name|XpathException
throws|,
name|SAXException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $a := "
operator|+
name|XML_DATA1
operator|+
literal|"let $b := xs:anyURI('/db/validate-test/test.nvdl')"
operator|+
literal|"return "
operator|+
literal|"validation:jing-report($a,$b)"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"valid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_invalid
parameter_list|()
throws|throws
name|XPathException
throws|,
name|IOException
throws|,
name|XpathException
throws|,
name|SAXException
throws|,
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"let $a :=<test/>"
operator|+
literal|"let $b := xs:anyURI('/db/validate-test/test.nvdl')"
operator|+
literal|"return "
operator|+
literal|"validation:jing-report($a,$b)"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_stored_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"doc('/db/validate-test/valid.xml'), "
operator|+
literal|"doc('/db/validate-test/test.nvdl') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"valid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_stored_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"doc('/db/validate-test/invalid.xml'), "
operator|+
literal|"doc('/db/validate-test/test.nvdl') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_anyuri_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/valid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/test.nvdl') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"valid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_anyuri_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/invalid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/test.nvdl') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|onvdl_anyuri_valid_boolean
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"validation:jing( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/valid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/validate-test/test.nvdl') )"
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
name|assertEquals
argument_list|(
name|query
argument_list|,
literal|"true"
argument_list|,
name|results
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
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|executeAndEvaluate
parameter_list|(
specifier|final
name|String
name|query
parameter_list|,
specifier|final
name|String
name|expectedValue
parameter_list|)
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
name|expectedValue
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

