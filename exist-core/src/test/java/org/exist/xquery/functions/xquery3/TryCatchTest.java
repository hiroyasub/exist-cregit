begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2010 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
name|xquery3
package|;
end_package

begin_import
import|import
name|com
operator|.
name|googlecode
operator|.
name|junittoolbox
operator|.
name|ParallelRunner
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
name|XMLUnit
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
name|ClassRule
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|RunWith
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
name|ResourceSet
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
name|ErrorCodes
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
name|base
operator|.
name|XMLDBException
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

begin_comment
comment|/**  * @author wessels  */
end_comment

begin_class
annotation|@
name|RunWith
argument_list|(
name|ParallelRunner
operator|.
name|class
argument_list|)
specifier|public
class|class
name|TryCatchTest
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
annotation|@
name|Test
specifier|public
name|void
name|encapsulated_1
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"<a>{ try { 'b' + 7 } catch * { 'c' } }</a>"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"<a>c</a>"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|encapsulated_2
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"for $i in (1,2,3,4) return<a>{ try { 'b' + $i } catch * { 'c' } }</a>"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
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
name|assertEquals
argument_list|(
literal|"<a>c</a>"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|encapsulated_3
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"<foo>{ for $i in (1,2,3,4) return<a>{ try { 'b' + $i } catch * { 'c' } }</a> }</foo>"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
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
name|XMLUnit
operator|.
name|setIgnoreWhitespace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|XMLAssert
operator|.
name|assertXMLEqual
argument_list|(
literal|"<foo><a>c</a><a>c</a><a>c</a><a>c</a></foo>"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|xQuery3_1
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '1.0';"
operator|+
literal|"try { a + 7 } catch * { 1 }"
decl_stmt|;
try|try
block|{
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|fail
argument_list|(
literal|"exception expected"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|Throwable
name|t
parameter_list|)
block|{
specifier|final
name|Throwable
name|cause
init|=
name|t
operator|.
name|getCause
argument_list|()
decl_stmt|;
if|if
condition|(
name|cause
operator|instanceof
name|XPathException
condition|)
block|{
specifier|final
name|XPathException
name|ex
init|=
operator|(
name|XPathException
operator|)
name|cause
decl_stmt|;
name|assertEquals
argument_list|(
literal|"exerr:EXXQDY0003"
argument_list|,
name|ex
operator|.
name|getErrorCode
argument_list|()
operator|.
name|getErrorQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
name|t
throw|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|simpleCatch
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } catch * { 1 }"
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
name|assertEquals
argument_list|(
literal|"1"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchWithCodeAndDescription
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch * "
operator|+
literal|"{  $err:code, $err:description } "
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
literal|2
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r1
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
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|.
name|getErrorQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|r1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r2
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|.
name|getDescription
argument_list|()
operator|+
literal|" Undefined context sequence for 'child::{}a'"
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchWithError3Matches
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch err:XPDY0001 { 1 }"
operator|+
literal|"catch err:XPDY0002 { 2 }"
operator|+
literal|"catch err:XPDY0003 { 3 }"
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
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|r
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
name|catchWithErrorNoMatches
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch err:XPDY0001 { 1 }"
operator|+
literal|"catch err:XPDY0002 { a }"
operator|+
literal|"catch err:XPDY0003 { 3 }"
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
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchWithMultipleMatches
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch err:XPDY0001 | err:XPDY0003 { 13 }"
operator|+
literal|"catch err:XPDY0002 { 2 }"
operator|+
literal|"catch err:XPDY0004 | err:XPDY0005 { 45 }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
argument_list|)
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"2"
argument_list|,
name|r
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query2
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch err:XPDY0001 | * { 13 }"
operator|+
literal|"catch err:XPDY0002 { 2 }"
operator|+
literal|"catch err:XPDY0004 | err:XPDY0005 { 45 }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results2
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query2
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r2
init|=
operator|(
name|String
operator|)
name|results2
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
literal|"13"
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchFnError
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try {"
operator|+
literal|" fn:error( fn:QName('http://www.w3.org/2005/xqt-errors', 'err:FOER0000') ) "
operator|+
literal|"} catch * "
operator|+
literal|"{ $err:code }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
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
name|r1
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
name|assertEquals
argument_list|(
literal|"err:FOER0000"
argument_list|,
name|r1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query2
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try {"
operator|+
literal|" fn:error( fn:QName('http://www.w3.org/2005/xqt-errors', 'err:FOER0000') ) "
operator|+
literal|"} catch * "
operator|+
literal|"{ $err:code }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results2
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results2
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r2
init|=
operator|(
name|String
operator|)
name|results2
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
literal|"err:FOER0000"
argument_list|,
name|r2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query3
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try {"
operator|+
literal|" fn:error(fn:QName('http://www.w3.org/2005/xqt-errors', 'err:FOER0000'), 'TEST') "
operator|+
literal|"} catch * "
operator|+
literal|"{ $err:code, $err:description }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results3
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query3
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results3
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r31
init|=
operator|(
name|String
operator|)
name|results3
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
literal|"err:FOER0000"
argument_list|,
name|r31
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r32
init|=
operator|(
name|String
operator|)
name|results3
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TEST"
argument_list|,
name|r32
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query4
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try {"
operator|+
literal|" fn:error(fn:QName('http://www.w3.org/2005/xqt-errors', 'err:FOER0000'), 'TEST') "
operator|+
literal|"} catch *  "
operator|+
literal|"{ $err:code, $err:description }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results4
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query4
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|2
argument_list|,
name|results4
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r41
init|=
operator|(
name|String
operator|)
name|results4
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
literal|"err:FOER0000"
argument_list|,
name|r41
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r42
init|=
operator|(
name|String
operator|)
name|results4
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TEST"
argument_list|,
name|r42
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query5
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try {"
operator|+
literal|" fn:error(fn:QName('http://www.w3.org/2005/xqt-errors', 'err:FOER0000'), 'TEST',<ab/>) "
operator|+
literal|"} catch *  "
operator|+
literal|"{ $err:code, $err:description, $err:value }"
decl_stmt|;
specifier|final
name|ResourceSet
name|results5
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query5
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|results5
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r51
init|=
operator|(
name|String
operator|)
name|results5
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
literal|"err:FOER0000"
argument_list|,
name|r51
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r52
init|=
operator|(
name|String
operator|)
name|results5
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"TEST"
argument_list|,
name|r52
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r53
init|=
operator|(
name|String
operator|)
name|results5
operator|.
name|getResource
argument_list|(
literal|2
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<ab/>"
argument_list|,
name|r53
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchFullErrorCode
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"try { a + 7 } "
operator|+
literal|"catch *  "
operator|+
literal|"{  $err:code, $err:description, empty($err:value) } "
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
literal|3
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r1
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
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|.
name|getErrorQName
argument_list|()
operator|.
name|getStringValue
argument_list|()
argument_list|,
name|r1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r2
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|1
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|ErrorCodes
operator|.
name|XPDY0002
operator|.
name|getDescription
argument_list|()
operator|+
literal|" Undefined context sequence for 'child::{}a'"
argument_list|,
name|r2
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r3
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|2
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r3
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchDefinedNamespace
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query1
init|=
literal|"xquery version '3.0';"
operator|+
literal|"declare namespace foo='http://foo.com'; "
operator|+
literal|"try { "
operator|+
literal|"     fn:error(fn:QName('http://foo.com', 'ERRORNAME'), 'ERRORTEXT') "
operator|+
literal|"} "
operator|+
literal|"catch foo:ERRORNAME  { 'good' } "
operator|+
literal|"catch *  { 'bad' } "
decl_stmt|;
specifier|final
name|ResourceSet
name|results
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query1
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
name|r1
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
name|assertEquals
argument_list|(
literal|"good"
argument_list|,
name|r1
argument_list|)
expr_stmt|;
specifier|final
name|String
name|query2
init|=
literal|"xquery version '3.0';"
operator|+
literal|"declare namespace foo='http://foo.com'; "
operator|+
literal|"try { "
operator|+
literal|"     fn:error(fn:QName('http://foo.com', 'ERRORNAME'), 'ERRORTEXT') "
operator|+
literal|"} "
operator|+
literal|"catch foo:ERRORNAME { $err:code } "
operator|+
literal|"catch *  { 'bad' } "
decl_stmt|;
specifier|final
name|ResourceSet
name|results2
init|=
name|existEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query2
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results2
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|String
name|r2
init|=
operator|(
name|String
operator|)
name|results2
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
literal|"foo:ERRORNAME"
argument_list|,
name|r2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|catchDefinedNamespace2
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"xquery version '3.0';"
operator|+
literal|"declare namespace foo='http://foo.com'; "
operator|+
literal|"try { "
operator|+
literal|"     fn:error(fn:QName('http://foo.com', 'ERRORNAME'), 'ERRORTEXT')"
operator|+
literal|"} "
operator|+
literal|"catch foo:ERRORNAME { 'good' } "
operator|+
literal|"catch * { 'wrong' } "
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
name|r1
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
name|assertEquals
argument_list|(
literal|"good"
argument_list|,
name|r1
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

