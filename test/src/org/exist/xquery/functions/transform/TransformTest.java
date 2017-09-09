begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2017 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|transform
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|EXistException
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|Collection
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|IndexInfo
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
name|triggers
operator|.
name|TriggerException
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
name|PermissionDeniedException
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
name|exist
operator|.
name|storage
operator|.
name|lock
operator|.
name|Lock
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
name|txn
operator|.
name|Txn
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
name|ExistEmbeddedServer
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
name|LockException
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
name|exist
operator|.
name|xquery
operator|.
name|XQuery
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
name|value
operator|.
name|Item
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
name|value
operator|.
name|Sequence
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
name|SAXException
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
name|util
operator|.
name|Optional
import|;
end_import

begin_import
import|import static
name|junit
operator|.
name|framework
operator|.
name|TestCase
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
name|assertEquals
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

begin_comment
comment|/**  * @see https://github.com/eXist-db/exist/issues/1506  *  * @author<a href="mailto:adam@evolvedbinary.com">Adam Retter</a>  */
end_comment

begin_class
specifier|public
class|class
name|TransformTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|TEST_COLLECTION
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"/db/transform-test"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|INPUT_XML_NAME
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"inputListOps.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|INPUT_XML
init|=
literal|"<listOps>\n"
operator|+
literal|"<ops id=\"IRCANTEC\"/>\n"
operator|+
literal|"<ops id=\"CIBTP\"/>\n"
operator|+
literal|"<ops id=\"AGIRC-ARRCO\"/>\n"
operator|+
literal|"<ops id=\"CTIP-FFSA-FNMF\"/>\n"
operator|+
literal|"</listOps>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|DICTIONARY_XML_NAME
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"listOpsErr.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|DICTIONARY_XML
init|=
literal|"<listOps>\n"
operator|+
literal|"<ops id=\"IRCANTEC\" doEntiteAff=\"false\" doGenerateB20=\"true\"></ops>\n"
operator|+
literal|"<ops id=\"CIBTP\" doEntiteAff=\"true\" doGenerateB20=\"true\"/>\n"
operator|+
literal|"<ops id=\"AGIRC-ARRCO\" doEntiteAff=\"true\" doGenerateB20=\"false\"></ops>\n"
operator|+
literal|"<ops id=\"CTIP-FFSA-FNMF\" doEntiteAff=\"true\" doGenerateB20=\"true\"></ops>\n"
operator|+
literal|"<ops id=\"POLEEMPLOI\" doEntiteAff=\"true\" doGenerateB20=\"true\"></ops>\n"
operator|+
literal|"</listOps>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|LIST_OPS_XSLT_NAME
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"testListOps.xsl"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LIST_OPS_XSLT
init|=
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" xmlns:ts=\"http://www.talentia-software.fr\" version=\"2.0\">\n"
operator|+
literal|"<xsl:output method=\"xml\" indent=\"no\" encoding=\"UTF-8\"/>\n"
operator|+
literal|"<!-- -->\n"
operator|+
literal|"<xsl:param name=\"listOpsFileUri\" required=\"yes\"/>\n"
operator|+
literal|"\n"
operator|+
literal|"<!-- -->\n"
operator|+
literal|"<xsl:variable name=\"ts:listOps\" select=\"doc($listOpsFileUri)\"/>\n"
operator|+
literal|"\n"
operator|+
literal|"<xsl:key name=\"ts:listOpsById\" match=\"//ops\" use=\"@id\"/>\n"
operator|+
literal|"\n"
operator|+
literal|"<!-- -->\n"
operator|+
literal|"<xsl:template match=\"/\">\n"
operator|+
literal|"<xsl:if test=\"empty($ts:listOps)\">\n"
operator|+
literal|"<xsl:message terminate=\"yes\">Could not find listOpsFileUri document</xsl:message>\n"
operator|+
literal|"</xsl:if>\n"
operator|+
literal|"\n"
operator|+
literal|"<DSN_FLAT>\n"
operator|+
literal|"<xsl:for-each select=\"//ops\">\n"
operator|+
literal|"<xsl:variable name=\"keyId\" select=\"@id\"/>\n"
operator|+
literal|"<xsl:variable name=\"refListOpsEntry\" select=\"$ts:listOps/key('ts:listOpsById', $keyId)\"/>\n"
operator|+
literal|"<xsl:element name=\"keyId\">\n"
operator|+
literal|"<xsl:value-of select=\"$keyId\"/>\n"
operator|+
literal|"</xsl:element>\n"
operator|+
literal|"<xsl:element name=\"listOpsEntry\">\n"
operator|+
literal|"<xsl:for-each select=\"$refListOpsEntry/@*\">\n"
operator|+
literal|"<xsl:value-of select=\"concat(name(), ': ', ., ' ')\"/>\n"
operator|+
literal|"</xsl:for-each>\n"
operator|+
literal|"</xsl:element>\n"
operator|+
literal|"</xsl:for-each>\n"
operator|+
literal|"</DSN_FLAT>\n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|LIST_OPS_XQUERY
init|=
literal|"xquery version \"3.0\";\n"
operator|+
literal|"\n"
operator|+
literal|"(:Read document with xsl:for-each and look for key in the dictionary document :)\n"
operator|+
literal|"declare variable $xsltPath as xs:string := '"
operator|+
name|TEST_COLLECTION
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"';\n"
operator|+
literal|"declare variable $listOpsFileUri as xs:string := '"
operator|+
name|TEST_COLLECTION
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/listOpsErr.xml';\n"
operator|+
literal|"declare variable $inputFileUri as xs:string := '"
operator|+
name|TEST_COLLECTION
operator|.
name|getCollectionPath
argument_list|()
operator|+
literal|"/inputListOps.xml';\n"
operator|+
literal|"\n"
operator|+
literal|"let $params :=<parameters>\n"
operator|+
literal|"<param name=\"listOpsFileUri\" value=\"{$listOpsFileUri}\" />\n"
operator|+
literal|"</parameters>\n"
operator|+
literal|"\n"
operator|+
literal|"let $xmlData := doc($inputFileUri)\n"
operator|+
literal|"\n"
operator|+
literal|"return transform:transform($xmlData, doc(concat($xsltPath, '/', 'testListOps.xsl')),$params)"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|keys
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|XPathException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
comment|/*final Txn transaction = existEmbeddedServer.getBrokerPool().getTransactionManager().beginTransaction() */
init|)
block|{
specifier|final
name|Sequence
name|sequence
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|LIST_OPS_XQUERY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|sequence
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|sequence
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|Item
name|item
init|=
name|sequence
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|item
operator|instanceof
name|Element
argument_list|)
expr_stmt|;
specifier|final
name|Element
name|dsn_flat
init|=
operator|(
operator|(
name|Element
operator|)
name|item
operator|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"DSN_FLAT"
argument_list|,
name|dsn_flat
operator|.
name|getNodeName
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|NodeList
name|nodeList
init|=
name|dsn_flat
operator|.
name|getElementsByTagName
argument_list|(
literal|"listOpsEntry"
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|4
argument_list|,
name|nodeList
operator|.
name|getLength
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id: IRCANTEC doEntiteAff: false doGenerateB20: true "
argument_list|,
name|nodeList
operator|.
name|item
argument_list|(
literal|0
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id: CIBTP doEntiteAff: true doGenerateB20: true "
argument_list|,
name|nodeList
operator|.
name|item
argument_list|(
literal|1
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id: AGIRC-ARRCO doEntiteAff: true doGenerateB20: false "
argument_list|,
name|nodeList
operator|.
name|item
argument_list|(
literal|2
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"id: CTIP-FFSA-FNMF doEntiteAff: true doGenerateB20: true "
argument_list|,
name|nodeList
operator|.
name|item
argument_list|(
literal|3
argument_list|)
operator|.
name|getTextContent
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|storeXml
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|transaction
parameter_list|,
specifier|final
name|Collection
name|collection
parameter_list|,
specifier|final
name|XmldbURI
name|name
parameter_list|,
specifier|final
name|String
name|xml
parameter_list|)
throws|throws
name|LockException
throws|,
name|SAXException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|name
argument_list|,
name|xml
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|storeResources
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
try|try
block|{
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
name|storeXml
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|testCollection
argument_list|,
name|LIST_OPS_XSLT_NAME
argument_list|,
name|LIST_OPS_XSLT
argument_list|)
expr_stmt|;
name|storeXml
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|testCollection
argument_list|,
name|INPUT_XML_NAME
argument_list|,
name|INPUT_XML
argument_list|)
expr_stmt|;
name|storeXml
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|testCollection
argument_list|,
name|DICTIONARY_XML_NAME
argument_list|,
name|DICTIONARY_XML
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|acquire
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanupResources
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|pool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|testCollection
init|=
literal|null
decl_stmt|;
try|try
block|{
name|testCollection
operator|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TEST_COLLECTION
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
block|}
block|}
finally|finally
block|{
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
name|testCollection
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit
