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
name|exist
operator|.
name|util
operator|.
name|io
operator|.
name|InputStreamUtil
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
name|exist
operator|.
name|samples
operator|.
name|Samples
operator|.
name|SAMPLES
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
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
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
comment|/**  * Tests for the validation:jaxp() function with Catalog (resolvers).  *   * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|JaxpParseTest
block|{
specifier|private
specifier|static
specifier|final
name|String
index|[]
name|TEST_RESOURCES
init|=
block|{
literal|"defaultValue.xml"
block|,
literal|"defaultValue.xsd"
block|}
decl_stmt|;
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
specifier|static
specifier|final
name|String
name|noValidation
init|=
literal|"<?xml version='1.0'?>"
operator|+
literal|"<collection xmlns='http://exist-db.org/collection-config/1.0'>"
operator|+
literal|"<validation mode='no'/>"
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
literal|"system/config/db/parse_validate"
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
name|Collection
name|schemasCollection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|schemasCollection
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
literal|"parse_validate"
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|testResource
range|:
name|TEST_RESOURCES
control|)
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getSample
argument_list|(
literal|"validation/parse_validate/"
operator|+
name|testResource
argument_list|)
init|)
block|{
name|assertNotNull
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|ExistXmldbEmbeddedServer
operator|.
name|storeResource
argument_list|(
name|schemasCollection
argument_list|,
name|testResource
argument_list|,
name|InputStreamUtil
operator|.
name|readAll
argument_list|(
name|is
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|schemasCollection
operator|!=
literal|null
condition|)
block|{
name|schemasCollection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|clearGrammarCache
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
decl_stmt|;
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|parse_and_fill_defaults
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|SAXException
block|{
name|String
name|query
init|=
literal|"validation:pre-parse-grammar(xs:anyURI('/db/parse_validate/defaultValue.xsd'))"
decl_stmt|;
name|String
name|result
init|=
name|execute
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
argument_list|,
literal|"defaultTest"
argument_list|)
expr_stmt|;
name|query
operator|=
literal|"declare option exist:serialize 'indent=no'; "
operator|+
literal|"validation:jaxp-parse(xs:anyURI('/db/parse_validate/defaultValue.xml'), true(), ())"
expr_stmt|;
name|result
operator|=
name|execute
argument_list|(
name|query
argument_list|)
expr_stmt|;
name|String
name|expected
init|=
literal|"<ns1:root xmlns:ns1=\"defaultTest\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
operator|+
literal|"<color>red</color>\n"
operator|+
literal|"<shoesize country=\"nl\">43</shoesize>\n"
operator|+
literal|"</ns1:root>"
decl_stmt|;
name|XMLAssert
operator|.
name|assertXMLEqual
argument_list|(
name|expected
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|execute
parameter_list|(
specifier|final
name|String
name|query
parameter_list|)
throws|throws
name|XMLDBException
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
return|return
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
return|;
block|}
block|}
end_class

end_unit

