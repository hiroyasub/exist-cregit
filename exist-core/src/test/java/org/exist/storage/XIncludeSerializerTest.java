begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2015 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  */
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
import|import
name|org
operator|.
name|apache
operator|.
name|xmlrpc
operator|.
name|XmlRpcException
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
name|Namespaces
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
name|ExistWebServer
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
name|BeforeClass
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
name|Test
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
name|assertTrue
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
name|HttpURLConnection
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
name|net
operator|.
name|URL
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_comment
comment|/**  * @author jim fuller at webcomposite.com  *  * Test XInclude Serialiser via REST/XMLRPC/WEBDAV/SOAP interfaces  */
end_comment

begin_class
specifier|public
class|class
name|XIncludeSerializerTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistWebServer
name|existWebServer
init|=
operator|new
name|ExistWebServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
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
name|getXmlRpcApi
parameter_list|()
block|{
return|return
literal|"http://127.0.0.1:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/xmlrpc"
return|;
block|}
specifier|private
specifier|final
specifier|static
name|String
name|getRestUri
parameter_list|()
block|{
return|return
literal|"http://admin:admin@127.0.0.1:"
operator|+
name|existWebServer
operator|.
name|getPort
argument_list|()
operator|+
literal|"/db/xinclude_test"
return|;
block|}
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA1
init|=
literal|"<test xmlns:xi='"
operator|+
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
name|Namespaces
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
annotation|@
name|Test
specifier|public
name|void
name|absSimpleREST
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|// path needs to indicate indent and wrap is off
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_simple.xml?_indent=no&_wrap=no"
decl_stmt|;
comment|// we use honest http
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
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
init|)
block|{
name|String
name|line
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
block|}
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|relSimpleREST1
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_relative1.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|relSimpleREST2
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
comment|// path needs to indicate indent and wrap is off
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_relative2.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|xpointerREST3
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_xpointer1.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|xpointerREST4
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_xpointer2.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
specifier|final
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
annotation|@
name|Test
specifier|public
name|void
name|fallback1
parameter_list|()
throws|throws
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_fallback1.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
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
name|IOException
block|{
specifier|final
name|String
name|uri
init|=
name|getRestUri
argument_list|()
operator|+
literal|"/test_fallback2.xml?_indent=no&_wrap=no"
decl_stmt|;
specifier|final
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
specifier|final
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
specifier|final
name|StringBuilder
name|out
init|=
operator|new
name|StringBuilder
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
specifier|final
name|String
name|responseXML
init|=
name|out
operator|.
name|toString
argument_list|()
decl_stmt|;
block|}
comment|//TODO add full url test e.g. http://www.example.org/test.xml for xinclude
comment|//TODO add simple and relative url with xpointer
comment|//ex.<xi:include href="../javascript.xml#xpointer(html/head)"/>
comment|//TODO add simple and relative url with xpointer and namespaces
comment|// ex.<xi:include href="../javascript.xml#xmlns(x=http://www.w3.org/1999/xhtml)xpointer(/x:html/x:head)"/>
comment|/*      * XML-RPC tests      *      */
comment|//TODO check serialisation via this interface, simple and relative
comment|/*      * WebDAV tests      *      */
comment|//TODO check serialisation via this interface, simple and relative???
comment|// probably overkill
comment|/*      * SOAP tests      *      */
comment|// probably overkill
comment|//TODO check serialisation via this interface, simple and relative???
comment|// probably overkill
comment|/*      * helper functions      *      */
specifier|protected
name|HttpURLConnection
name|getConnection
parameter_list|(
specifier|final
name|String
name|url
parameter_list|)
throws|throws
name|IOException
block|{
specifier|final
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
specifier|private
specifier|static
name|XmlRpcClient
name|getClient
parameter_list|()
throws|throws
name|MalformedURLException
block|{
specifier|final
name|XmlRpcClient
name|client
init|=
operator|new
name|XmlRpcClient
argument_list|()
decl_stmt|;
specifier|final
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
name|getXmlRpcApi
argument_list|()
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
comment|//TODO create reader for xml
comment|/*      * SetUp / TearDown functions      *      */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
throws|throws
name|XmlRpcException
throws|,
name|MalformedURLException
block|{
specifier|final
name|XmlRpcClient
name|xmlrpc
init|=
name|getClient
argument_list|()
decl_stmt|;
specifier|final
name|List
argument_list|<
name|Object
argument_list|>
name|params
init|=
operator|new
name|ArrayList
argument_list|<>
argument_list|()
decl_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XINCLUDE_COLLECTION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XINCLUDE_NESTED_COLLECTION
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"createCollection"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA1
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_simple.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA2
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/metatags.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA2
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/data/metatags.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA3
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_relative1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA4
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_relative2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA5
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_xpointer1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA6
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_xpointer2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA7
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_fallback1.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
name|params
operator|.
name|clear
argument_list|()
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
name|XML_DATA8
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|"/db/xinclude_test/test_fallback2.xml"
argument_list|)
expr_stmt|;
name|params
operator|.
name|add
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|xmlrpc
operator|.
name|execute
argument_list|(
literal|"parse"
argument_list|,
name|params
argument_list|)
expr_stmt|;
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

end_unit
