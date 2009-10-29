begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2008-2009 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xslt
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
name|*
import|;
end_import

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
name|java
operator|.
name|io
operator|.
name|FileInputStream
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
name|CharBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|MappedByteBuffer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|channels
operator|.
name|FileChannel
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|Charset
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|CharsetDecoder
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|StringTokenizer
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Matcher
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|regex
operator|.
name|Pattern
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
name|BrokerPool
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
name|DBBroker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
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
name|Before
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|XSLTestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSLT_COLLECTION
init|=
literal|"xslt_tests"
decl_stmt|;
specifier|static
name|File
name|existDir
init|=
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
decl_stmt|;
specifier|private
name|Collection
name|col
init|=
literal|null
decl_stmt|;
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUpBeforeClass
parameter_list|()
throws|throws
name|Exception
block|{
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|tearDownAfterClass
parameter_list|()
throws|throws
name|Exception
block|{
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
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
name|col
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
operator|+
literal|"/"
operator|+
name|XSLT_COLLECTION
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|==
literal|null
condition|)
block|{
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|mgtService
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
name|col
operator|=
name|mgtService
operator|.
name|createCollection
argument_list|(
name|XSLT_COLLECTION
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"collection created."
argument_list|)
expr_stmt|;
block|}
name|BrokerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|TransformerFactoryAllocator
operator|.
name|PROPERTY_TRANSFORMER_CLASS
argument_list|,
literal|"org.exist.xslt.TransformerFactoryImpl"
argument_list|)
expr_stmt|;
name|loadBench
argument_list|(
literal|"test/src/org/exist/xslt/test/bench/v1_0"
argument_list|,
name|bench
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
specifier|private
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|bench
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
name|void
name|loadBench
parameter_list|(
name|String
name|benchLocation
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|bench
parameter_list|)
throws|throws
name|Exception
block|{
name|File
name|testConf
init|=
operator|new
name|File
argument_list|(
name|benchLocation
operator|+
literal|"/default.conf"
argument_list|)
decl_stmt|;
if|if
condition|(
name|testConf
operator|.
name|canRead
argument_list|()
condition|)
block|{
comment|// Open the file and then get a channel from the stream
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|testConf
argument_list|)
decl_stmt|;
name|FileChannel
name|fc
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
comment|// Get the file's size and then map it into memory
name|int
name|sz
init|=
operator|(
name|int
operator|)
name|fc
operator|.
name|size
argument_list|()
decl_stmt|;
name|MappedByteBuffer
name|bb
init|=
name|fc
operator|.
name|map
argument_list|(
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|sz
argument_list|)
decl_stmt|;
comment|// Charset and decoder for ISO-8859-15
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"ISO-8859-15"
argument_list|)
decl_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|charset
operator|.
name|newDecoder
argument_list|()
decl_stmt|;
comment|// Decode the file into a char buffer
name|CharBuffer
name|cb
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|bb
argument_list|)
decl_stmt|;
comment|// Perform the search
name|loadBench
argument_list|(
name|testConf
argument_list|,
name|cb
argument_list|,
name|bench
argument_list|)
expr_stmt|;
comment|// Close the channel and the stream
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|loadBench
parameter_list|(
name|File
name|testConf
parameter_list|,
name|CharBuffer
name|cb
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|>
name|bench
parameter_list|)
block|{
comment|// Pattern used to parse lines
name|Pattern
name|linePattern
init|=
name|Pattern
operator|.
name|compile
argument_list|(
literal|".*\r?\n"
argument_list|)
decl_stmt|;
name|String
name|testName
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testInfo
init|=
literal|null
decl_stmt|;
name|int
name|position
decl_stmt|;
name|Matcher
name|lm
init|=
name|linePattern
operator|.
name|matcher
argument_list|(
name|cb
argument_list|)
decl_stmt|;
comment|// Line matcher
name|int
name|lines
init|=
literal|0
decl_stmt|;
while|while
condition|(
name|lm
operator|.
name|find
argument_list|()
condition|)
block|{
name|lines
operator|++
expr_stmt|;
name|CharSequence
name|cs
init|=
name|lm
operator|.
name|group
argument_list|()
decl_stmt|;
comment|// The current line
name|String
name|str
init|=
name|cs
operator|.
name|toString
argument_list|()
decl_stmt|;
if|if
condition|(
name|cs
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|==
operator|(
name|char
operator|)
literal|0x005B
condition|)
block|{
name|position
operator|=
name|str
operator|.
name|indexOf
argument_list|(
literal|"]"
argument_list|)
expr_stmt|;
name|testName
operator|=
name|str
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|position
argument_list|)
expr_stmt|;
if|if
condition|(
name|bench
operator|.
name|containsKey
argument_list|(
name|testName
argument_list|)
condition|)
block|{
name|testInfo
operator|=
name|bench
operator|.
name|get
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|testInfo
operator|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
argument_list|()
expr_stmt|;
name|bench
operator|.
name|put
argument_list|(
name|testName
argument_list|,
name|testInfo
argument_list|)
expr_stmt|;
block|}
block|}
if|else if
condition|(
name|testName
operator|!=
literal|null
condition|)
block|{
name|position
operator|=
name|str
operator|.
name|indexOf
argument_list|(
literal|"="
argument_list|)
expr_stmt|;
if|if
condition|(
name|position
operator|!=
operator|-
literal|1
condition|)
block|{
name|String
name|key
init|=
name|str
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|position
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|String
name|value
init|=
name|str
operator|.
name|substring
argument_list|(
name|position
operator|+
literal|1
argument_list|)
operator|.
name|trim
argument_list|()
decl_stmt|;
name|testInfo
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lm
operator|.
name|end
argument_list|()
operator|==
name|cb
operator|.
name|limit
argument_list|()
condition|)
break|break;
block|}
block|}
comment|/** 	 * @throws java.lang.Exception 	 */
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
block|}
annotation|@
name|Test
specifier|public
name|void
name|testSimpleTransform
parameter_list|()
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"xquery version \"1.0\";\n"
operator|+
literal|"declare namespace transform=\"http://exist-db.org/xquery/transform\";\n"
operator|+
literal|"declare variable $xml {\n"
operator|+
literal|"<node xmlns=\"http://www.w3.org/1999/xhtml\">text</node>\n"
operator|+
literal|"};\n"
operator|+
literal|"declare variable $xslt {\n"
operator|+
literal|"<xsl:stylesheet xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"2.0\">\n"
operator|+
literal|"<xsl:template match=\"node\">\n"
operator|+
literal|"<div><xsl:value-of select=\".\"/></div>\n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>\n"
operator|+
literal|"};\n"
operator|+
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">\n"
operator|+
literal|"<body>\n"
operator|+
literal|"		{transform:transform($xml, $xslt, ())}\n"
operator|+
literal|"</body>\n"
operator|+
literal|"</html>"
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
comment|//check there is one result
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
name|String
name|content
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
comment|//            System.out.println(content);
comment|//check the namespace
name|assertTrue
argument_list|(
name|content
operator|.
name|startsWith
argument_list|(
literal|"<html xmlns=\"http://www.w3.org/1999/xhtml\">"
argument_list|)
argument_list|)
expr_stmt|;
comment|//check the content
name|assertTrue
argument_list|(
name|content
operator|.
name|indexOf
argument_list|(
literal|"<div>text</div>"
argument_list|)
operator|>
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
comment|//            fail(e.getMessage());
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testComplexTransform
parameter_list|()
throws|throws
name|Exception
block|{
try|try
block|{
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
name|query
init|=
literal|"xquery version \"1.0\";\n"
operator|+
literal|"declare namespace transform=\"http://exist-db.org/xquery/transform\";\n"
operator|+
literal|"declare variable $xml {\n"
operator|+
literal|"<salesdata>\n"
operator|+
literal|"<year>\n"
operator|+
literal|"<year>1997</year>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>west</name>\n"
operator|+
literal|"<sales unit=\"millions\">32</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>central</name>\n"
operator|+
literal|"<sales unit=\"millions\">11</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>east</name>\n"
operator|+
literal|"<sales unit=\"millions\">19</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"</year>\n"
operator|+
literal|"<year>\n"
operator|+
literal|"<year>1998</year>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>west</name>\n"
operator|+
literal|"<sales unit=\"millions\">35</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>central</name>\n"
operator|+
literal|"<sales unit=\"millions\">12</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>east</name>\n"
operator|+
literal|"<sales unit=\"millions\">25</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"</year>\n"
operator|+
literal|"<year>\n"
operator|+
literal|"<year>1999</year>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>west</name>\n"
operator|+
literal|"<sales unit=\"millions\">36</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>central</name>\n"
operator|+
literal|"<sales unit=\"millions\">12</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>east</name>\n"
operator|+
literal|"<sales unit=\"millions\">31</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"</year>\n"
operator|+
literal|"<year>\n"
operator|+
literal|"<year>2000</year>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>west</name>\n"
operator|+
literal|"<sales unit=\"millions\">37</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>central</name>\n"
operator|+
literal|"<sales unit=\"millions\">11</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"<region>\n"
operator|+
literal|"<name>east</name>\n"
operator|+
literal|"<sales unit=\"millions\">40</sales>\n"
operator|+
literal|"</region>\n"
operator|+
literal|"</year>\n"
operator|+
literal|"</salesdata>\n"
operator|+
literal|"};\n"
operator|+
literal|"declare variable $xslt {\n"
operator|+
literal|"<xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
operator|+
literal|"<xsl:output method=\"html\" encoding=\"utf-8\"/>\n"
operator|+
literal|"<xsl:template match=\"/\">\n"
operator|+
literal|"<html>\n"
operator|+
literal|"<table border=\"1\">\n"
operator|+
literal|"<tr>\n"
operator|+
literal|"<td colspan=\"2\">Total Sales</td>\n"
operator|+
literal|"</tr>\n"
operator|+
literal|"<xsl:for-each select=\"salesdata/year\">\n"
operator|+
literal|"<tr>\n"
operator|+
literal|"<td>\n"
operator|+
literal|"<xsl:value-of select=\"year\"/>\n"
operator|+
literal|"</td>\n"
operator|+
literal|"<td align=\"right\">\n"
operator|+
literal|"<xsl:value-of select=\"sum(region/sales)\"/>\n"
operator|+
literal|"</td>\n"
operator|+
literal|"</tr>\n"
operator|+
literal|"</xsl:for-each>\n"
operator|+
literal|"<tr>\n"
operator|+
literal|"<td>Grand Total</td>\n"
operator|+
literal|"<td align=\"right\">\n"
operator|+
literal|"<xsl:value-of select=\"sum(salesdata/year/region/sales)\"/>\n"
operator|+
literal|"</td>\n"
operator|+
literal|"</tr>\n"
operator|+
literal|"</table>\n"
operator|+
literal|"</html>\n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>\n"
operator|+
literal|"};\n"
operator|+
literal|"transform:transform($xml, $xslt, ())"
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
comment|//check there is one result
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
name|String
name|content
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
comment|//            System.out.println(content);
comment|//check the content
name|assertTrue
argument_list|(
name|checkResult
argument_list|(
literal|"total.ref"
argument_list|,
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|XMLDBException
name|e
parameter_list|)
block|{
comment|//            fail(e.getMessage());
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|boolean
name|checkResult
parameter_list|(
name|String
name|file
parameter_list|,
name|String
name|result
parameter_list|)
throws|throws
name|Exception
block|{
name|int
name|tokenCount
init|=
literal|0
decl_stmt|;
name|String
name|ref
init|=
name|loadFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"\\n"
argument_list|,
literal|" "
argument_list|)
expr_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"<dgnorm_document>"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|ref
operator|=
name|ref
operator|.
name|replaceAll
argument_list|(
literal|"</dgnorm_document>"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
name|String
name|delim
init|=
literal|" \t\n\r\f<>"
decl_stmt|;
name|StringTokenizer
name|refTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|ref
argument_list|,
name|delim
argument_list|)
decl_stmt|;
name|StringTokenizer
name|resTokenizer
init|=
operator|new
name|StringTokenizer
argument_list|(
name|result
argument_list|,
name|delim
argument_list|)
decl_stmt|;
while|while
condition|(
name|refTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|tokenCount
operator|++
expr_stmt|;
name|String
name|refToken
init|=
name|refTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|resTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have: "
operator|+
name|refToken
operator|+
literal|", but get EOF (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
name|String
name|resToken
init|=
name|resTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|refToken
operator|.
name|equals
argument_list|(
name|resToken
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have: "
operator|+
name|refToken
operator|+
literal|", but get "
operator|+
name|resToken
operator|+
literal|" (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
block|}
if|if
condition|(
name|resTokenizer
operator|.
name|hasMoreTokens
argument_list|()
condition|)
block|{
name|String
name|resToken
init|=
name|resTokenizer
operator|.
name|nextToken
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|ref
argument_list|)
expr_stmt|;
throw|throw
operator|new
name|Exception
argument_list|(
literal|"result should have nothing, but get "
operator|+
name|resToken
operator|+
literal|" (at "
operator|+
name|tokenCount
operator|+
literal|")"
argument_list|)
throw|;
block|}
return|return
literal|true
return|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBench
parameter_list|()
throws|throws
name|Exception
block|{
name|long
name|start_time
decl_stmt|;
name|long
name|end_time
decl_stmt|;
name|String
name|query
init|=
literal|null
decl_stmt|;
name|String
name|content
init|=
literal|null
decl_stmt|;
name|int
name|passed
init|=
literal|0
decl_stmt|;
name|boolean
name|passing
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|testInfo
decl_stmt|;
name|String
name|reqTest
init|=
literal|null
decl_stmt|;
comment|//"avts";
for|for
control|(
name|String
name|testName
range|:
name|bench
operator|.
name|keySet
argument_list|()
control|)
block|{
if|if
condition|(
operator|(
name|reqTest
operator|!=
literal|null
operator|)
operator|&&
operator|(
operator|!
name|testName
operator|.
name|equals
argument_list|(
name|reqTest
argument_list|)
operator|)
condition|)
continue|continue;
name|passing
operator|=
literal|true
expr_stmt|;
name|query
operator|=
literal|null
expr_stmt|;
name|content
operator|=
literal|null
expr_stmt|;
name|testInfo
operator|=
name|bench
operator|.
name|get
argument_list|(
name|testName
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
name|testName
operator|+
literal|": "
argument_list|)
expr_stmt|;
if|if
condition|(
name|testInfo
operator|.
name|containsKey
argument_list|(
literal|"storeBeforeTest"
argument_list|)
condition|)
block|{
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|"skipping"
argument_list|)
expr_stmt|;
if|if
condition|(
name|testInfo
operator|.
name|containsKey
argument_list|(
literal|"comment"
argument_list|)
condition|)
name|System
operator|.
name|out
operator|.
name|print
argument_list|(
literal|" ("
operator|+
name|testInfo
operator|.
name|get
argument_list|(
literal|"comment"
argument_list|)
operator|+
literal|")"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
continue|continue;
block|}
name|String
name|input
init|=
name|loadFile
argument_list|(
name|testInfo
operator|.
name|get
argument_list|(
literal|"input"
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|stylesheet
init|=
name|loadFile
argument_list|(
name|testInfo
operator|.
name|get
argument_list|(
literal|"stylesheet"
argument_list|)
argument_list|)
decl_stmt|;
try|try
block|{
name|start_time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|col
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|query
operator|=
literal|"xquery version \"1.0\";\n"
operator|+
literal|"declare namespace transform=\"http://exist-db.org/xquery/transform\";\n"
operator|+
literal|"declare variable $xml {"
operator|+
name|input
operator|+
literal|"};\n"
operator|+
literal|"declare variable $xslt {"
operator|+
name|stylesheet
operator|+
literal|"};\n"
operator|+
literal|"transform:transform($xml, $xslt, ())\n"
expr_stmt|;
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
name|end_time
operator|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
comment|//check there is one result
comment|//	            assertEquals(1, result.getSize());
name|content
operator|=
literal|""
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
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
name|content
operator|=
name|content
operator|+
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
comment|//check the content
name|assertTrue
argument_list|(
name|checkResult
argument_list|(
name|testInfo
operator|.
name|get
argument_list|(
literal|"reference"
argument_list|)
argument_list|,
name|content
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|//	        	System.out.println("************************************** query ******************************");
comment|//	        	System.out.println(query);
name|System
operator|.
name|out
operator|.
name|println
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"************************************* content ******************************"
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|passing
operator|=
literal|false
expr_stmt|;
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
if|if
condition|(
name|passing
condition|)
block|{
name|end_time
operator|=
name|end_time
operator|-
name|start_time
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"pass ("
operator|+
name|end_time
operator|+
literal|" ms)"
argument_list|)
expr_stmt|;
name|passed
operator|++
expr_stmt|;
block|}
else|else
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"faild"
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|" "
operator|+
name|passed
operator|+
literal|" of "
operator|+
name|bench
operator|.
name|keySet
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//TODO: test<!-- reassembles an xml tree in reverse order -->
specifier|private
name|String
name|loadFile
parameter_list|(
name|String
name|string
parameter_list|)
throws|throws
name|IOException
block|{
name|String
name|result
init|=
literal|null
decl_stmt|;
name|File
name|file
init|=
operator|new
name|File
argument_list|(
literal|"test/src/org/exist/xslt/test/bench/v1_0/"
operator|+
name|string
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|file
operator|.
name|canRead
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"can load information."
argument_list|)
throw|;
block|}
else|else
block|{
comment|// Open the file and then get a channel from the stream
name|FileInputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|FileChannel
name|fc
init|=
name|fis
operator|.
name|getChannel
argument_list|()
decl_stmt|;
comment|// Get the file's size and then map it into memory
name|int
name|sz
init|=
operator|(
name|int
operator|)
name|fc
operator|.
name|size
argument_list|()
decl_stmt|;
name|MappedByteBuffer
name|bb
init|=
name|fc
operator|.
name|map
argument_list|(
name|FileChannel
operator|.
name|MapMode
operator|.
name|READ_ONLY
argument_list|,
literal|0
argument_list|,
name|sz
argument_list|)
decl_stmt|;
comment|// Charset and decoder for ISO-8859-15
name|Charset
name|charset
init|=
name|Charset
operator|.
name|forName
argument_list|(
literal|"ISO-8859-15"
argument_list|)
decl_stmt|;
name|CharsetDecoder
name|decoder
init|=
name|charset
operator|.
name|newDecoder
argument_list|()
decl_stmt|;
comment|// Decode the file into a char buffer
name|CharBuffer
name|cb
init|=
name|decoder
operator|.
name|decode
argument_list|(
name|bb
argument_list|)
decl_stmt|;
name|result
operator|=
name|cb
operator|.
name|toString
argument_list|()
expr_stmt|;
comment|//TODO: rewrite to handle<?xml*?>
if|if
condition|(
name|result
operator|.
name|startsWith
argument_list|(
literal|"<?xml version=\"1.0\"?>"
argument_list|)
condition|)
name|result
operator|=
name|result
operator|.
name|substring
argument_list|(
literal|"<?xml version=\"1.0\"?>"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
if|if
condition|(
name|result
operator|.
name|startsWith
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
argument_list|)
condition|)
name|result
operator|=
name|result
operator|.
name|substring
argument_list|(
literal|"<?xml version=\"1.0\" encoding=\"utf-8\"?>"
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
comment|//XXX: rethink: prexslt query processing
comment|//			result = result.replaceAll("{", "{{");
comment|//			result = result.replaceAll("}", "}}");
comment|// Close the channel and the stream
name|fc
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
block|}
end_class

end_unit

