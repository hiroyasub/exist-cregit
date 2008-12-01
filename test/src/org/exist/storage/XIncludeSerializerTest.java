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
name|storage
package|;
end_package

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
import|import
name|java
operator|.
name|io
operator|.
name|BufferedReader
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
name|InputStreamReader
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|BindException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|HttpURLConnection
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|MalformedURLException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Vector
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClientConfigImpl
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|client
operator|.
name|XmlRpcClient
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
name|Diff
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|StandaloneServer
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
name|serializers
operator|.
name|XIncludeFilter
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
name|BeforeClass
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
name|mortbay
operator|.
name|util
operator|.
name|MultiException
import|;
end_import

begin_comment
comment|/**  * @author jim fuller at webcomposite.com  *   * Test XInclude Serialiser via REST/XMLRPC/WEBDAV/SOAP interfaces  *   * TODO: need to refacotr to avoid catching unexpected exceptions  *   *   */
end_comment

begin_class
specifier|public
class|class
name|XIncludeSerializerTest
block|{
specifier|private
specifier|static
name|StandaloneServer
name|server
init|=
literal|null
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|XINCLUDE_COLLECTION
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"xinclude_test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|XINCLUDE_NESTED_COLLECTION
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"xinclude_test/data"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XMLRPC_URI
init|=
literal|"http://127.0.0.1:8088/xmlrpc"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|REST_URI
init|=
literal|"http://admin:admin@127.0.0.1:8088/db/xinclude_test"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA1
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='metatags.xml'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA2
init|=
literal|"<html>"
operator|+
literal|"<head>"
operator|+
literal|"<metatag xml:id='metatag' name='test' description='test'/>"
operator|+
literal|"</head>"
operator|+
literal|"</html>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA3
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='../xinclude_test/data/metatags.xml'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA4
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='data/metatags.xml'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA5
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='data/metatags.xml' xpointer='xpointer(//metatag)'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA6
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='data/metatags.xml' xpointer='metatag'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA7
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='data/unknown.xml'>"
operator|+
literal|"<xi:fallback><warning>Not found</warning></xi:fallback>"
operator|+
literal|"</xi:include>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA8
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<xi:include href='data/unknown.xml'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_RESULT
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<html>"
operator|+
literal|"<head>"
operator|+
literal|"<metatag xml:id='metatag' name='test' description='test'/>"
operator|+
literal|"</head>"
operator|+
literal|"</html>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_RESULT_XPOINTER
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<metatag xml:id='metatag' name='test' description='test'/>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_RESULT_FALLBACK1
init|=
literal|"<test xmlns:xi='"
operator|+
name|XIncludeFilter
operator|.
name|XINCLUDE_NS
operator|+
literal|"'>"
operator|+
literal|"<root>"
operator|+
literal|"<warning>Not found</warning>"
operator|+
literal|"</root>"
operator|+
literal|"</test>"
decl_stmt|;
comment|/*      * REST tests      *      */
annotation|@
name|Test
specifier|public
name|void
name|absSimpleREST
parameter_list|()
block|{
try|try
block|{
comment|// path needs to indicate indent and wrap is off
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_simple.xml?_indent=no&_wrap=no"
decl_stmt|;
comment|// we use honest http
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|//absSimpleREST
annotation|@
name|Test
specifier|public
name|void
name|relSimpleREST1
parameter_list|()
block|{
try|try
block|{
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_relative1.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|//relSimpleREST1
annotation|@
name|Test
specifier|public
name|void
name|relSimpleREST2
parameter_list|()
block|{
try|try
block|{
comment|// path needs to indicate indent and wrap is off
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_relative2.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|//relSimpleREST
annotation|@
name|Test
specifier|public
name|void
name|xPointerREST3
parameter_list|()
block|{
try|try
block|{
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_xpointer1.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT_XPOINTER
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT_XPOINTER
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|Test
specifier|public
name|void
name|xPointerREST4
parameter_list|()
block|{
try|try
block|{
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_xpointer2.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT_XPOINTER
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT_XPOINTER
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|Test
specifier|public
name|void
name|fallback1
parameter_list|()
block|{
try|try
block|{
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_fallback1.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"control XML"
operator|+
name|XML_RESULT_FALLBACK1
argument_list|)
expr_stmt|;
name|Diff
name|myDiff
init|=
operator|new
name|Diff
argument_list|(
name|XML_RESULT_FALLBACK1
argument_list|,
name|responseXML
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
literal|"pieces of XML are similar "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|similar
argument_list|()
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
literal|"but are they identical? "
operator|+
name|myDiff
argument_list|,
name|myDiff
operator|.
name|identical
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|Test
argument_list|(
name|expected
operator|=
name|IOException
operator|.
name|class
argument_list|)
specifier|public
name|void
name|fallback2
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|uri
init|=
name|REST_URI
operator|+
literal|"/test_fallback2.xml?_indent=no&_wrap=no"
decl_stmt|;
name|HttpURLConnection
name|connect
init|=
name|getConnection
argument_list|(
name|uri
argument_list|)
decl_stmt|;
name|connect
operator|.
name|setRequestMethod
argument_list|(
literal|"GET"
argument_list|)
expr_stmt|;
name|connect
operator|.
name|connect
argument_list|()
expr_stmt|;
name|BufferedReader
name|reader
init|=
operator|new
name|BufferedReader
argument_list|(
operator|new
name|InputStreamReader
argument_list|(
name|connect
operator|.
name|getInputStream
argument_list|()
argument_list|,
literal|"UTF-8"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|line
decl_stmt|;
name|StringBuffer
name|out
init|=
operator|new
name|StringBuffer
argument_list|()
decl_stmt|;
while|while
condition|(
operator|(
name|line
operator|=
name|reader
operator|.
name|readLine
argument_list|()
operator|)
operator|!=
literal|null
condition|)
block|{
name|out
operator|.
name|append
argument_list|(
name|line
argument_list|)
expr_stmt|;
name|out
operator|.
name|append
argument_list|(
literal|"\r\n"
argument_list|)
expr_stmt|;
block|}
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"response XML:"
operator|+
name|responseXML
argument_list|)
expr_stmt|;
block|}
comment|// @TODO add full url test e.g. http://www.example.org/test.xml for xinclude
comment|// @TODO add simple and relative url with xpointer
comment|//ex.<xi:include href="../javascript.xml#xpointer(html/head)"/>
comment|// @TODO add simple and relative url with xpointer and namespaces
comment|// ex.<xi:include href="../javascript.xml#xmlns(x=http://www.w3.org/1999/xhtml)xpointer(/x:html/x:head)"/>
comment|/*      * XML-RPC tests      *      */
comment|// @TODO check serialisation via this interface, simple and relative
comment|/*      * WebDAV tests      *      */
comment|// @TODO check serialisation via this interface, simple and relative???
comment|// probably overkill
comment|/*      * SOAP tests      *      */
comment|// probably overkill
comment|// @TODO check serialisation via this interface, simple and relative???
comment|// probably overkill
comment|/*      * helper functions      *      */
specifier|protected
name|HttpURLConnection
name|getConnection
parameter_list|(
name|String
name|url
parameter_list|)
block|{
try|try
block|{
name|URL
name|u
init|=
operator|new
name|URL
argument_list|(
name|url
argument_list|)
decl_stmt|;
return|return
operator|(
name|HttpURLConnection
operator|)
name|u
operator|.
name|openConnection
argument_list|()
return|;
block|}
catch|catch
parameter_list|(
name|Exception
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
return|return
literal|null
return|;
block|}
comment|//httpurlconnection
specifier|protected
specifier|static
name|XmlRpcClient
name|getClient
parameter_list|()
block|{
try|try
block|{
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
name|XmlRpcClientConfigImpl
name|config
init|=
operator|new
name|XmlRpcClientConfigImpl
argument_list|()
decl_stmt|;
name|config
operator|.
name|setEnabledForExtensions
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|config
operator|.
name|setServerURL
argument_list|(
operator|new
name|URL
argument_list|(
name|XMLRPC_URI
argument_list|)
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicUserName
argument_list|(
literal|"admin"
argument_list|)
expr_stmt|;
name|config
operator|.
name|setBasicPassword
argument_list|(
literal|""
argument_list|)
expr_stmt|;
name|client
operator|.
name|setConfig
argument_list|(
name|config
argument_list|)
expr_stmt|;
return|return
name|client
return|;
block|}
catch|catch
parameter_list|(
name|MalformedURLException
name|e
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
comment|// @TODO create reader for xml
comment|/*      * SetUp / TearDown functions      *      */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
block|{
comment|//Don't worry about closing the server : the shutdown hook will do the job
try|try
block|{
if|if
condition|(
name|server
operator|==
literal|null
condition|)
block|{
name|server
operator|=
operator|new
name|StandaloneServer
argument_list|()
expr_stmt|;
if|if
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting standalone server..."
argument_list|)
expr_stmt|;
name|String
index|[]
name|args
init|=
block|{}
decl_stmt|;
name|server
operator|.
name|run
argument_list|(
name|args
argument_list|)
expr_stmt|;
while|while
condition|(
operator|!
name|server
operator|.
name|isStarted
argument_list|()
condition|)
block|{
name|Thread
operator|.
name|sleep
argument_list|(
literal|1000
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|MultiException
name|e
parameter_list|)
block|{
name|boolean
name|rethrow
init|=
literal|true
decl_stmt|;
name|Iterator
name|i
init|=
name|e
operator|.
name|getExceptions
argument_list|()
operator|.
name|iterator
argument_list|()
decl_stmt|;
while|while
condition|(
name|i
operator|.
name|hasNext
argument_list|()
condition|)
block|{
name|Exception
name|e0
init|=
operator|(
name|Exception
operator|)
name|i
operator|.
name|next
argument_list|()
decl_stmt|;
if|if
condition|(
name|e0
operator|instanceof
name|BindException
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"A server is running already !"
argument_list|)
expr_stmt|;
name|rethrow
operator|=
literal|false
expr_stmt|;
break|break;
block|}
block|}
if|if
condition|(
name|rethrow
condition|)
throw|throw
name|e
throw|;
block|}
block|}
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Creating collection "
operator|+
name|XINCLUDE_COLLECTION
argument_list|)
expr_stmt|;
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XINCLUDE_COLLECTION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|resultColl1
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XINCLUDE_NESTED_COLLECTION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|Boolean
name|resultColl2
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Loading test document data"
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA1
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_simple.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile1
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA2
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/metatags.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile3
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA2
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/data/metatags.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile4
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA3
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_relative1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile5
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA4
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_relative2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile6
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA5
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_xpointer1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile7
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA6
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_xpointer2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile8
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA7
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_fallback1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile9
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
name|XML_DATA8
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test/test_fallback2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
operator|new
name|Integer
argument_list|(
literal|1
argument_list|)
argument_list|)
expr_stmt|;
name|Boolean
name|resultFile10
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|stopDB
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
name|Vector
name|params
init|=
operator|new
name|Vector
argument_list|()
decl_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|addElement
argument_list|(
literal|"/db/xinclude_test"
argument_list|)
expr_stmt|;
name|Boolean
name|resultRemove
init|=
operator|(
name|Boolean
operator|)
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"removeCollection"
argument_list|,
name|params
argument_list|)
decl_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
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
comment|//tearDown
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
name|org
operator|.
name|junit
operator|.
name|runner
operator|.
name|JUnitCore
operator|.
name|runClasses
argument_list|(
name|XIncludeSerializerTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

begin_comment
comment|//XIncludeserializertest
end_comment

end_unit

