begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software Foundation  *  Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Properties
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|XMLConstants
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilder
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|ParserConfigurationException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParser
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|SAXParserFactory
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|transform
operator|.
name|OutputKeys
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
name|dom
operator|.
name|QName
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|security
operator|.
name|Account
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
name|ExistSAXParserFactory
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
name|exist
operator|.
name|util
operator|.
name|serializer
operator|.
name|AttrList
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
name|serializer
operator|.
name|SAXSerializer
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
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
operator|.
name|UTF_8
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|TestUtils
operator|.
name|GUEST_DB_USER
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|xmldb
operator|.
name|AbstractLocal
operator|.
name|PROP_JOIN_TRANSACTION_IF_PRESENT
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
name|assertNull
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
name|w3c
operator|.
name|dom
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Element
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Node
import|;
end_import

begin_import
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|NodeList
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
name|*
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
name|ResourceIterator
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
name|XPathQueryService
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

begin_class
specifier|public
class|class
name|ResourceTest
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
name|TEST_COLLECTION
init|=
literal|"testResource"
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|prepareXmldbJoinTransactions
parameter_list|()
block|{
name|System
operator|.
name|setProperty
argument_list|(
name|PROP_JOIN_TRANSACTION_IF_PRESENT
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|releaseXmldbJoinTransactions
parameter_list|()
block|{
name|System
operator|.
name|clearProperty
argument_list|(
name|PROP_JOIN_TRANSACTION_IF_PRESENT
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readNonExistingResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|Resource
name|nonExistent
init|=
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"12345.xml"
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|nonExistent
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|readResource
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
specifier|final
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
specifier|final
name|String
index|[]
name|resources
init|=
name|testCollection
operator|.
name|listResources
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|resources
operator|.
name|length
argument_list|,
name|testCollection
operator|.
name|getResourceCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resources
index|[
literal|0
index|]
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"ISO-8859-1"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
specifier|final
name|ContentHandler
name|xmlout
init|=
operator|new
name|SAXSerializer
argument_list|(
name|sout
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|doc
operator|.
name|getContentAsSAX
argument_list|(
name|xmlout
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|testRecursiveSerailization
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
specifier|final
name|String
name|xmlDoc1
init|=
literal|"<test><title>Title</title>"
operator|+
literal|"<import href=\"recurseSer2.xml\"></import>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|final
name|String
name|xmlDoc2
init|=
literal|"<test2><title>Title2</title></test2>"
decl_stmt|;
specifier|final
name|String
name|doc1Name
init|=
literal|"recurseSer1.xml"
decl_stmt|;
specifier|final
name|String
name|doc2Name
init|=
literal|"recurseSer2.xml"
decl_stmt|;
specifier|final
name|XMLResource
name|resource1
init|=
name|addResource
argument_list|(
name|doc1Name
argument_list|,
name|xmlDoc1
argument_list|)
decl_stmt|;
specifier|final
name|XMLResource
name|resource2
init|=
name|addResource
argument_list|(
name|doc2Name
argument_list|,
name|xmlDoc2
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|sout
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|METHOD
argument_list|,
literal|"xml"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|put
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|ContentHandler
name|importHandler
init|=
operator|new
name|ImportingContentHandler
argument_list|(
name|sout
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|resource1
operator|.
name|getContentAsSAX
argument_list|(
name|importHandler
argument_list|)
expr_stmt|;
specifier|final
name|String
name|result
init|=
name|sout
operator|.
name|getBuffer
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"<test>"
operator|+
literal|"<title>Title</title>"
operator|+
literal|"<test2>"
operator|+
literal|"<title>Title2</title>"
operator|+
literal|"</test2>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
argument_list|,
name|result
operator|.
name|trim
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|readDOM
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|XMLResource
name|doc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"r_and_j.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|Node
name|n
init|=
name|doc
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|Element
name|elem
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|n
operator|instanceof
name|Element
condition|)
block|{
name|elem
operator|=
operator|(
name|Element
operator|)
name|n
expr_stmt|;
block|}
if|else if
condition|(
name|n
operator|instanceof
name|Document
condition|)
block|{
name|elem
operator|=
operator|(
operator|(
name|Document
operator|)
name|n
operator|)
operator|.
name|getDocumentElement
argument_list|()
expr_stmt|;
block|}
name|assertNotNull
argument_list|(
name|elem
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|elem
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"PLAY"
argument_list|)
expr_stmt|;
name|NodeList
name|children
init|=
name|elem
operator|.
name|getChildNodes
argument_list|()
decl_stmt|;
name|Node
name|node
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
name|children
operator|.
name|getLength
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|node
operator|=
name|children
operator|.
name|item
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|node
argument_list|)
expr_stmt|;
name|node
operator|=
name|node
operator|.
name|getFirstChild
argument_list|()
expr_stmt|;
while|while
condition|(
name|node
operator|!=
literal|null
condition|)
block|{
name|node
operator|=
name|node
operator|.
name|getNextSibling
argument_list|()
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|setContentAsSAX
parameter_list|()
throws|throws
name|SAXException
throws|,
name|ParserConfigurationException
throws|,
name|XMLDBException
throws|,
name|IOException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
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
literal|"test.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
decl_stmt|;
name|ContentHandler
name|handler
init|=
name|doc
operator|.
name|setContentAsSAX
argument_list|()
decl_stmt|;
name|SAXParserFactory
name|saxFactory
init|=
name|ExistSAXParserFactory
operator|.
name|getSAXParserFactory
argument_list|()
decl_stmt|;
name|saxFactory
operator|.
name|setNamespaceAware
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|saxFactory
operator|.
name|setValidating
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|SAXParser
name|sax
init|=
name|saxFactory
operator|.
name|newSAXParser
argument_list|()
decl_stmt|;
name|XMLReader
name|reader
init|=
name|sax
operator|.
name|getXMLReader
argument_list|()
decl_stmt|;
name|reader
operator|.
name|setContentHandler
argument_list|(
name|handler
argument_list|)
expr_stmt|;
name|reader
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|setContentAsDOM
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
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
literal|"dom.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|String
name|xml
init|=
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
decl_stmt|;
name|DocumentBuilderFactory
name|docFactory
init|=
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|DocumentBuilder
name|builder
init|=
name|docFactory
operator|.
name|newDocumentBuilder
argument_list|()
decl_stmt|;
name|Document
name|dom
init|=
name|builder
operator|.
name|parse
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
argument_list|)
argument_list|)
decl_stmt|;
name|doc
operator|.
name|setContentAsDOM
argument_list|(
name|dom
operator|.
name|getDocumentElement
argument_list|()
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|setContentAsSource_Reader
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
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
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
literal|"source.xml"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|xml
init|=
literal|"<test><title>Title1</title>"
operator|+
literal|"<para>Paragraph3</para>"
operator|+
literal|"<para>Paragraph4</para>"
operator|+
literal|"</test>"
decl_stmt|;
name|doc
operator|.
name|setContent
argument_list|(
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|xml
argument_list|)
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
name|XMLResource
name|newDoc
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
literal|"source.xml"
argument_list|)
decl_stmt|;
name|String
name|newDocXml
init|=
operator|(
name|String
operator|)
name|newDoc
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"Title1"
argument_list|,
literal|"/test/title/text()"
argument_list|,
name|newDocXml
argument_list|)
expr_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"2"
argument_list|,
literal|"count(/test/para)"
argument_list|,
name|newDocXml
argument_list|)
expr_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"Paragraph3"
argument_list|,
literal|"/test/para[1]/text()"
argument_list|,
name|newDocXml
argument_list|)
expr_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"Paragraph4"
argument_list|,
literal|"/test/para[2]/text()"
argument_list|,
name|newDocXml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|queryRemoveResource
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|Resource
name|resource
init|=
literal|null
decl_stmt|;
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|testCollection
argument_list|)
expr_stmt|;
name|String
name|resourceName
init|=
literal|"QueryTestPerson.xml"
decl_stmt|;
name|String
name|id
init|=
literal|"test."
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|String
name|content
init|=
literal|"<?xml version='1.0'?><person id=\""
operator|+
name|id
operator|+
literal|"\"><name>Jason</name></person>"
decl_stmt|;
name|resource
operator|=
name|testCollection
operator|.
name|createResource
argument_list|(
name|resourceName
argument_list|,
literal|"XMLResource"
argument_list|)
expr_stmt|;
name|resource
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XPathQueryService
name|service
init|=
operator|(
name|XPathQueryService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|rs
init|=
name|service
operator|.
name|query
argument_list|(
literal|"/person[@id='"
operator|+
name|id
operator|+
literal|"']"
argument_list|)
decl_stmt|;
for|for
control|(
name|ResourceIterator
name|iterator
init|=
name|rs
operator|.
name|getIterator
argument_list|()
init|;
name|iterator
operator|.
name|hasMoreResources
argument_list|()
condition|;
control|)
block|{
name|Resource
name|r
init|=
name|iterator
operator|.
name|nextResource
argument_list|()
decl_stmt|;
name|testCollection
operator|.
name|removeResource
argument_list|(
name|r
argument_list|)
expr_stmt|;
name|resource
operator|=
literal|null
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|addRemove
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|resourceID
init|=
literal|"addremove.xml"
decl_stmt|;
name|XMLResource
name|created
init|=
name|addResource
argument_list|(
name|resourceID
argument_list|,
name|xmlForTest
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|created
argument_list|)
expr_stmt|;
comment|// need to test documents xml structure
name|XMLResource
name|located
init|=
name|resourceForId
argument_list|(
name|resourceID
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|located
argument_list|)
expr_stmt|;
comment|//assertEquals((String) created.getContent(), (String) located.getContent());
name|removeDocument
argument_list|(
name|resourceID
argument_list|)
expr_stmt|;
name|XMLResource
name|locatedAfterRemove
init|=
name|resourceForId
argument_list|(
name|resourceID
argument_list|)
decl_stmt|;
name|assertNull
argument_list|(
name|locatedAfterRemove
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|addRemoveAddWithIds
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|resourceID
init|=
literal|"removeWithIds;1.xml"
decl_stmt|;
name|addResource
argument_list|(
name|resourceID
argument_list|,
literal|"<foo1 xml:id='f'/>"
argument_list|)
expr_stmt|;
name|removeDocument
argument_list|(
name|resourceID
argument_list|)
expr_stmt|;
name|addResource
argument_list|(
name|resourceID
argument_list|,
literal|"<foo xml:id='f'/>"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeDocument
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|XMLResource
name|resource
init|=
name|resourceForId
argument_list|(
name|id
argument_list|)
decl_stmt|;
if|if
condition|(
literal|null
operator|!=
name|resource
condition|)
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|collection
operator|.
name|removeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|XMLResource
name|addResource
parameter_list|(
name|String
name|id
parameter_list|,
name|String
name|content
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|XMLResource
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|createResource
argument_list|(
name|id
argument_list|,
name|XMLResource
operator|.
name|RESOURCE_TYPE
argument_list|)
expr_stmt|;
name|result
operator|.
name|setContent
argument_list|(
name|content
argument_list|)
expr_stmt|;
name|collection
operator|.
name|storeResource
argument_list|(
name|result
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|XMLResource
name|resourceForId
parameter_list|(
name|String
name|id
parameter_list|)
throws|throws
name|XMLDBException
block|{
name|Collection
name|collection
init|=
literal|null
decl_stmt|;
name|XMLResource
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|collection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|result
operator|=
operator|(
name|XMLResource
operator|)
name|collection
operator|.
name|getResource
argument_list|(
name|id
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|closeCollection
argument_list|(
name|collection
argument_list|)
expr_stmt|;
block|}
return|return
name|result
return|;
block|}
specifier|private
name|void
name|closeCollection
parameter_list|(
name|Collection
name|collection
parameter_list|)
throws|throws
name|XMLDBException
block|{
if|if
condition|(
literal|null
operator|!=
name|collection
condition|)
block|{
name|collection
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|xmlForTest
parameter_list|()
block|{
return|return
literal|"<test><title>Title</title>"
operator|+
literal|"<para>Paragraph1</para>"
operator|+
literal|"<para>Paragraph2</para>"
operator|+
literal|"</test>"
return|;
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|IOException
block|{
comment|//create a test collection
specifier|final
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|testCollection
init|=
name|cms
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
specifier|final
name|UserManagementService
name|ums
init|=
operator|(
name|UserManagementService
operator|)
name|testCollection
operator|.
name|getService
argument_list|(
literal|"UserManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
comment|// change ownership to guest
specifier|final
name|Account
name|guest
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|GUEST_DB_USER
argument_list|)
decl_stmt|;
name|ums
operator|.
name|chown
argument_list|(
name|guest
argument_list|,
name|guest
operator|.
name|getPrimaryGroup
argument_list|()
argument_list|)
expr_stmt|;
name|ums
operator|.
name|chmod
argument_list|(
literal|"rwxr-xr-x"
argument_list|)
expr_stmt|;
comment|//store sample files as guest
specifier|final
name|Collection
name|testCollectionAsGuest
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|LOCAL_DB
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|String
name|sampleName
range|:
name|SAMPLES
operator|.
name|getShakespeareXmlSampleNames
argument_list|()
control|)
block|{
specifier|final
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|testCollectionAsGuest
operator|.
name|createResource
argument_list|(
name|sampleName
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getShakespeareSample
argument_list|(
name|sampleName
argument_list|)
init|)
block|{
name|res
operator|.
name|setContent
argument_list|(
name|InputStreamUtil
operator|.
name|readString
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|testCollectionAsGuest
operator|.
name|storeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
comment|//delete the test collection
name|CollectionManagementService
name|cms
init|=
operator|(
name|CollectionManagementService
operator|)
name|existEmbeddedServer
operator|.
name|getRoot
argument_list|()
operator|.
name|getService
argument_list|(
literal|"CollectionManagementService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|cms
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
block|}
specifier|private
class|class
name|ImportingContentHandler
extends|extends
name|SAXSerializer
block|{
specifier|private
specifier|static
specifier|final
name|String
name|IMPORT_ELEM_NAME
init|=
literal|"import"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|HREF_ATTR_NAME
init|=
literal|"href"
decl_stmt|;
specifier|private
specifier|final
name|Writer
name|writer
decl_stmt|;
specifier|private
specifier|final
name|Properties
name|outputProperties
decl_stmt|;
name|ImportingContentHandler
parameter_list|(
specifier|final
name|Writer
name|writer
parameter_list|,
specifier|final
name|Properties
name|outputProperties
parameter_list|)
block|{
name|super
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|outputProperties
operator|=
name|outputProperties
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|,
specifier|final
name|AttrList
name|attribs
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|qname
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|IMPORT_ELEM_NAME
argument_list|)
condition|)
block|{
name|importDoc
argument_list|(
name|attribs
operator|.
name|getValue
argument_list|(
operator|new
name|QName
argument_list|(
name|HREF_ATTR_NAME
argument_list|,
name|XMLConstants
operator|.
name|NULL_NS_URI
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startElement
argument_list|(
name|qname
argument_list|,
name|attribs
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|startElement
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qName
parameter_list|,
specifier|final
name|Attributes
name|attributes
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
name|localName
operator|.
name|equals
argument_list|(
name|IMPORT_ELEM_NAME
argument_list|)
condition|)
block|{
name|importDoc
argument_list|(
name|attributes
operator|.
name|getValue
argument_list|(
name|HREF_ATTR_NAME
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|super
operator|.
name|startElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|,
name|attributes
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|importDoc
parameter_list|(
specifier|final
name|String
name|href
parameter_list|)
throws|throws
name|SAXException
block|{
try|try
block|{
specifier|final
name|XMLResource
name|resource
init|=
name|resourceForId
argument_list|(
name|href
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
operator|new
name|ImportingContentHandler
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
specifier|final
name|XMLDBException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|SAXException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|QName
name|qname
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|qname
operator|.
name|getLocalPart
argument_list|()
operator|.
name|equals
argument_list|(
name|IMPORT_ELEM_NAME
argument_list|)
condition|)
block|{
name|super
operator|.
name|endElement
argument_list|(
name|qname
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|endElement
parameter_list|(
specifier|final
name|String
name|uri
parameter_list|,
specifier|final
name|String
name|localName
parameter_list|,
specifier|final
name|String
name|qName
parameter_list|)
throws|throws
name|SAXException
block|{
if|if
condition|(
operator|!
name|localName
operator|.
name|equals
argument_list|(
name|IMPORT_ELEM_NAME
argument_list|)
condition|)
block|{
name|super
operator|.
name|endElement
argument_list|(
name|uri
argument_list|,
name|localName
argument_list|,
name|qName
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

