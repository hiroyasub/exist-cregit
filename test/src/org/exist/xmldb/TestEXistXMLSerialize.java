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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|StringWriter
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
name|transform
operator|.
name|Transformer
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
name|TransformerException
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
name|TransformerFactory
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
name|dom
operator|.
name|DOMSource
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
name|stream
operator|.
name|StreamResult
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|OutputFormat
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|xml
operator|.
name|serialize
operator|.
name|XMLSerializer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|DOMSerializer
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
name|After
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
name|XMLResource
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
name|XmldbLocalTests
operator|.
name|*
import|;
end_import

begin_comment
comment|/**  *  * @author  bmadigan  */
end_comment

begin_class
specifier|public
class|class
name|TestEXistXMLSerialize
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML_DATA
init|=
literal|"<test>"
operator|+
literal|"<para>Ã¤Ã¤Ã¶Ã¶Ã¼Ã¼ÃÃÃÃÃÃÃÃ</para>"
operator|+
literal|"<para>\uC5F4\uB2E8\uACC4</para>"
operator|+
literal|"</test>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XSL_DATA
init|=
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" "
operator|+
literal|"version=\"1.0\">"
operator|+
literal|"<xsl:param name=\"testparam\"/>"
operator|+
literal|"<xsl:template match=\"test\"><test><xsl:apply-templates/></test></xsl:template>"
operator|+
literal|"<xsl:template match=\"para\">"
operator|+
literal|"<p><xsl:value-of select=\"$testparam\"/>:<xsl:apply-templates/></p></xsl:template>"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|File
name|testFile
init|=
operator|new
name|File
argument_list|(
name|getExistDir
argument_list|()
argument_list|,
literal|"test/src/org/exist/xmldb/PerformanceTest.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testXmlSerialize"
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
name|ROOT_URI
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
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
name|Collection
name|testCollection
init|=
name|service
operator|.
name|createCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
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
name|Account
name|guest
init|=
name|ums
operator|.
name|getAccount
argument_list|(
name|GUEST_UID
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
name|Collection
name|root
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
argument_list|,
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
decl_stmt|;
name|CollectionManagementService
name|cms
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
name|cms
operator|.
name|removeCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
comment|//shutdownDB the db
name|DatabaseInstanceManager
name|dim
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
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|serialize1
parameter_list|()
throws|throws
name|TransformerException
throws|,
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
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
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
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|( )
decl_stmt|;
name|node
operator|=
name|node
operator|.
name|getOwnerDocument
argument_list|()
expr_stmt|;
comment|//Attempting serialization
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|node
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|( )
decl_stmt|;
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|out
argument_list|)
decl_stmt|;
name|Transformer
name|xformer
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|xformer
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|serialize2
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XMLDBException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
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
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|OutputFormat
name|format
init|=
operator|new
name|OutputFormat
argument_list|( )
decl_stmt|;
name|format
operator|.
name|setLineWidth
argument_list|(
literal|0
argument_list|)
expr_stmt|;
name|format
operator|.
name|setIndent
argument_list|(
literal|5
argument_list|)
expr_stmt|;
name|format
operator|.
name|setPreserveSpace
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|out
init|=
operator|new
name|ByteArrayOutputStream
argument_list|( )
decl_stmt|;
name|XMLSerializer
name|serializer
init|=
operator|new
name|XMLSerializer
argument_list|(
name|out
argument_list|,
name|format
argument_list|)
decl_stmt|;
if|if
condition|(
name|node
operator|instanceof
name|Document
condition|)
block|{
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|Document
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
if|else if
condition|(
name|node
operator|instanceof
name|Element
condition|)
block|{
name|serializer
operator|.
name|serialize
argument_list|(
operator|(
name|Element
operator|)
name|node
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fail
argument_list|(
literal|"Can't serialize node type: "
operator|+
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|serialize3
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XMLDBException
throws|,
name|TransformerException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
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
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|DOMSerializer
name|serializer
init|=
operator|new
name|DOMSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|serialize
argument_list|(
name|node
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|serialize4
parameter_list|()
throws|throws
name|ParserConfigurationException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XMLDBException
block|{
name|Collection
name|testCollection
init|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|Document
name|doc
init|=
name|javax
operator|.
name|xml
operator|.
name|parsers
operator|.
name|DocumentBuilderFactory
operator|.
name|newInstance
argument_list|( )
operator|.
name|newDocumentBuilder
argument_list|()
operator|.
name|parse
argument_list|(
name|testFile
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|null
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|resource
operator|.
name|setContentAsDOM
argument_list|(
name|doc
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
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|getResource
argument_list|(
name|resource
operator|.
name|getId
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|resource
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|Node
name|node
init|=
name|resource
operator|.
name|getContentAsDOM
argument_list|()
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|serialize5
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
name|ROOT_URI
operator|+
literal|"/"
operator|+
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
name|XMLResource
name|resource
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
name|resource
operator|.
name|setContent
argument_list|(
name|XML_DATA
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|resource
argument_list|)
expr_stmt|;
name|XMLResource
name|style
init|=
operator|(
name|XMLResource
operator|)
name|testCollection
operator|.
name|createResource
argument_list|(
literal|"test.xsl"
argument_list|,
literal|"XMLResource"
argument_list|)
decl_stmt|;
name|style
operator|.
name|setContent
argument_list|(
name|XSL_DATA
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|storeResource
argument_list|(
name|style
argument_list|)
expr_stmt|;
name|Properties
name|outputProperties
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
literal|"indent"
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|setProperty
argument_list|(
literal|"stylesheet"
argument_list|,
literal|"test.xsl"
argument_list|)
expr_stmt|;
name|testCollection
operator|.
name|setProperty
argument_list|(
literal|"stylesheet-param.testparam"
argument_list|,
literal|"TEST"
argument_list|)
expr_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
decl_stmt|;
name|resource
operator|.
name|getContentAsSAX
argument_list|(
name|serializer
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

