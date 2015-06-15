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
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|EmbeddedExistTester
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
comment|/**  * Additional tests for the validation:jing() function with RNGs and XSDs  *  * @author jim.fuller@webcomposite.com  * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|AdditionalJingXsdRngTest
extends|extends
name|EmbeddedExistTester
block|{
annotation|@
name|Test
specifier|public
name|void
name|testValidateXSDwithJing
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|query
init|=
literal|"let $v :=<doc>\n"
operator|+
literal|"\t<title>Title</title>\n"
operator|+
literal|"\t<p>Some paragraph.</p>\n"
operator|+
literal|"</doc>\n"
operator|+
literal|"let $schema :=<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
operator|+
literal|"\t\t elementFormDefault=\"qualified\">\n"
operator|+
literal|"\t<xs:element name=\"doc\">\n"
operator|+
literal|"\t<xs:complexType>\n"
operator|+
literal|"\t<xs:sequence>\n"
operator|+
literal|"\t<xs:element minOccurs=\"0\" ref=\"title\"/>\n"
operator|+
literal|"\t<xs:element minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"p\"/>\n"
operator|+
literal|"\t</xs:sequence>\n"
operator|+
literal|"\t</xs:complexType>\n"
operator|+
literal|"\t</xs:element>\n"
operator|+
literal|"\t<xs:element name=\"title\" type=\"xs:string\"/>\n"
operator|+
literal|"\t<xs:element name=\"p\" type=\"xs:string\"/>\n"
operator|+
literal|"</xs:schema>\n"
operator|+
literal|"return\n"
operator|+
literal|"\n"
operator|+
literal|"\tvalidation:jing($v,$schema)"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xpxqService
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateXSDwithJing_invalid
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|query
init|=
literal|"let $v :=<doc>\n"
operator|+
literal|"\t<title1>Title</title1>\n"
operator|+
literal|"\t<p>Some paragraph.</p>\n"
operator|+
literal|"</doc>\n"
operator|+
literal|"let $schema :=<xs:schema xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"\n"
operator|+
literal|"\t\t elementFormDefault=\"qualified\">\n"
operator|+
literal|"\t<xs:element name=\"doc\">\n"
operator|+
literal|"\t<xs:complexType>\n"
operator|+
literal|"\t<xs:sequence>\n"
operator|+
literal|"\t<xs:element minOccurs=\"0\" ref=\"title\"/>\n"
operator|+
literal|"\t<xs:element minOccurs=\"0\" maxOccurs=\"unbounded\" ref=\"p\"/>\n"
operator|+
literal|"\t</xs:sequence>\n"
operator|+
literal|"\t</xs:complexType>\n"
operator|+
literal|"\t</xs:element>\n"
operator|+
literal|"\t<xs:element name=\"title\" type=\"xs:string\"/>\n"
operator|+
literal|"\t<xs:element name=\"p\" type=\"xs:string\"/>\n"
operator|+
literal|"</xs:schema>\n"
operator|+
literal|"return\n"
operator|+
literal|"\n"
operator|+
literal|"\tvalidation:jing($v,$schema)"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateRNGwithJing
parameter_list|()
throws|throws
name|XPathException
throws|,
name|XMLDBException
block|{
name|String
name|query
init|=
literal|"let $v :=<doc>\n"
operator|+
literal|"\t<title>Title</title>\n"
operator|+
literal|"\t<p>Some paragraph.</p>\n"
operator|+
literal|"</doc>\n"
operator|+
literal|"let $schema :=<grammar xmlns=\"http://relaxng.org/ns/structure/1.0\">\n"
operator|+
literal|"<start>\n"
operator|+
literal|"<ref name=\"doc\"/>\n"
operator|+
literal|"</start>\n"
operator|+
literal|"<define name=\"doc\">\n"
operator|+
literal|"<element name=\"doc\">\n"
operator|+
literal|"<optional>\n"
operator|+
literal|"<ref name=\"title\"/>\n"
operator|+
literal|"</optional>\n"
operator|+
literal|"<zeroOrMore>\n"
operator|+
literal|"<ref name=\"p\"/>\n"
operator|+
literal|"</zeroOrMore>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"<define name=\"title\">\n"
operator|+
literal|"<element name=\"title\">\n"
operator|+
literal|"<text/>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"<define name=\"p\">\n"
operator|+
literal|"<element name=\"p\">\n"
operator|+
literal|"<text/>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"</grammar>\n"
operator|+
literal|"return\n"
operator|+
literal|"\n"
operator|+
literal|"\tvalidation:jing($v,$schema)"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testValidateRNGwithJing_invalid
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|query
init|=
literal|"let $v :=<doc>\n"
operator|+
literal|"\t<title1>Title</title1>\n"
operator|+
literal|"\t<p>Some paragraph.</p>\n"
operator|+
literal|"</doc>\n"
operator|+
literal|"let $schema :=<grammar xmlns=\"http://relaxng.org/ns/structure/1.0\">\n"
operator|+
literal|"<start>\n"
operator|+
literal|"<ref name=\"doc\"/>\n"
operator|+
literal|"</start>\n"
operator|+
literal|"<define name=\"doc\">\n"
operator|+
literal|"<element name=\"doc\">\n"
operator|+
literal|"<optional>\n"
operator|+
literal|"<ref name=\"title\"/>\n"
operator|+
literal|"</optional>\n"
operator|+
literal|"<zeroOrMore>\n"
operator|+
literal|"<ref name=\"p\"/>\n"
operator|+
literal|"</zeroOrMore>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"<define name=\"title\">\n"
operator|+
literal|"<element name=\"title\">\n"
operator|+
literal|"<text/>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"<define name=\"p\">\n"
operator|+
literal|"<element name=\"p\">\n"
operator|+
literal|"<text/>\n"
operator|+
literal|"</element>\n"
operator|+
literal|"</define>\n"
operator|+
literal|"</grammar>\n"
operator|+
literal|"return\n"
operator|+
literal|"\n"
operator|+
literal|"\tvalidation:jing($v,$schema)"
decl_stmt|;
name|ResourceSet
name|result
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"false"
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
literal|"Looks good, but memory issue"
argument_list|)
specifier|public
name|void
name|repeatTests
parameter_list|()
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|1000
condition|;
name|i
operator|++
control|)
block|{
try|try
block|{
name|testValidateRNGwithJing
argument_list|()
expr_stmt|;
name|testValidateRNGwithJing_invalid
argument_list|()
expr_stmt|;
name|testValidateXSDwithJing
argument_list|()
expr_stmt|;
name|testValidateXSDwithJing_invalid
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

