begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2016 The eXist-db Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
package|;
end_package

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
name|Optional
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
name|transform
operator|.
name|OutputKeys
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
name|security
operator|.
name|AuthenticationException
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
name|txn
operator|.
name|TransactionManager
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
name|test
operator|.
name|TestConstants
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
name|exist
operator|.
name|xquery
operator|.
name|value
operator|.
name|SequenceIterator
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
name|exist
operator|.
name|util
operator|.
name|PropertiesBuilder
operator|.
name|propertiesBuilder
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
import|;
end_import

begin_comment
comment|/**  * Tests the indexer.  *   * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|IndexerTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
name|propertiesBuilder
argument_list|()
operator|.
name|set
argument_list|(
name|Indexer
operator|.
name|PROPERTY_SUPPRESS_WHITESPACE
argument_list|,
literal|"none"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<?xml version=\"1.0\"?>\n"
operator|+
literal|"<x>\n"
operator|+
literal|"<y>a<b>b</b> c</y>\n"
operator|+
literal|"<z>a<b>b</b>c</z>\n"
operator|+
literal|"</x>\n"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML_XSLT
init|=
literal|"<?xml version=\"1.0\"?>\n"
operator|+
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
operator|+
literal|"<xsl:template match=\"processing-instruction()\" mode=\"xml2string\">\n"
operator|+
literal|"<xsl:text>&lt;?</xsl:text>\n"
operator|+
literal|"<xsl:value-of select=\"name()\"/>\n"
operator|+
literal|"<xsl:text> \n"
operator|+
literal|"\n"
operator|+
literal|"</xsl:text>\n"
operator|+
literal|"<xsl:value-of select=\".\"/>\n"
operator|+
literal|"<xsl:text>?&gt;</xsl:text>\n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>\n"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RESULT_NO_PRESERVE_MIXED_WS_XML
init|=
literal|"<result>\n"
operator|+
literal|"<node n=\"1\">\n"
operator|+
literal|"<y>a\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"            c</y>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"2\">a</node>\n"
operator|+
literal|"<node n=\"3\">\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"4\">b</node>\n"
operator|+
literal|"<node n=\"5\"> c</node>\n"
operator|+
literal|"<node n=\"6\">\n"
operator|+
literal|"<z>a\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"            c</z>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"7\">a</node>\n"
operator|+
literal|"<node n=\"8\">\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"9\">b</node>\n"
operator|+
literal|"<node n=\"10\">c</node>\n"
operator|+
literal|"</result>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RESULT_PRESERVE_MIXED_WS_XML
init|=
literal|"<result>\n"
operator|+
literal|"<node n=\"1\">\n"
operator|+
literal|"<y>a<b>b</b> c</y>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"2\">a</node>\n"
operator|+
literal|"<node n=\"3\">\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"4\">b</node>\n"
operator|+
literal|"<node n=\"5\"> c</node>\n"
operator|+
literal|"<node n=\"6\">\n"
operator|+
literal|"<z>a<b>b</b> c</z>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"7\">a</node>\n"
operator|+
literal|"<node n=\"8\">\n"
operator|+
literal|"<b>b</b>\n"
operator|+
literal|"</node>\n"
operator|+
literal|"<node n=\"9\">b</node>\n"
operator|+
literal|"<node n=\"10\">c</node>\n"
operator|+
literal|"</result>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|RESULT_XML_XSLT
init|=
literal|"<result>\n"
operator|+
literal|"<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\">\n"
operator|+
literal|"<xsl:template match=\"processing-instruction()\" mode=\"xml2string\">\n"
operator|+
literal|"<xsl:text>&lt;?</xsl:text>\n"
operator|+
literal|"<xsl:value-of select=\"name()\"/>\n"
operator|+
literal|"<xsl:text> \n"
operator|+
literal|"\n"
operator|+
literal|"</xsl:text>\n"
operator|+
literal|"<xsl:value-of select=\".\"/>\n"
operator|+
literal|"<xsl:text>?&gt;</xsl:text>\n"
operator|+
literal|"</xsl:template>\n"
operator|+
literal|"</xsl:stylesheet>\n"
operator|+
literal|"</result>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"let $test := doc('"
operator|+
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|TestConstants
operator|.
name|TEST_XML_URI
operator|.
name|toString
argument_list|()
operator|+
literal|"')/* "
operator|+
literal|"return "
operator|+
literal|"<result>"
operator|+
literal|"    {"
operator|+
literal|"        for $node at $i in $test//node()\n"
operator|+
literal|"        return<node n=\"{$i}\">{$node}</node>\n"
operator|+
literal|"    }"
operator|+
literal|"</result>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY_XSLT
init|=
literal|"let $test := doc('"
operator|+
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
operator|.
name|toString
argument_list|()
operator|+
literal|"/"
operator|+
name|TestConstants
operator|.
name|TEST_XML_URI
operator|.
name|toString
argument_list|()
operator|+
literal|"')/* "
operator|+
literal|"return "
operator|+
literal|"<result>{$test}</result>"
decl_stmt|;
specifier|private
name|void
name|store_preserve_ws_mixed_content_value
parameter_list|(
specifier|final
name|boolean
name|propValue
parameter_list|,
specifier|final
name|String
name|xml
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|EXistException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|AuthenticationException
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
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|Indexer
operator|.
name|PROPERTY_PRESERVE_WS_MIXED_CONTENT
argument_list|,
name|propValue
argument_list|)
expr_stmt|;
specifier|final
name|TransactionManager
name|txnMgr
init|=
name|pool
operator|.
name|getTransactionManager
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
name|authenticate
argument_list|(
literal|"admin"
argument_list|,
literal|""
argument_list|)
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|txn
init|=
name|txnMgr
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|TestConstants
operator|.
name|TEST_XML_URI
argument_list|,
name|xml
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|collection
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|xml
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
specifier|final
name|org
operator|.
name|exist
operator|.
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
name|doc
init|=
name|info
operator|.
name|getDocument
argument_list|()
decl_stmt|;
name|broker
operator|.
name|flush
argument_list|()
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|txnMgr
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|String
name|store_and_retrieve_ws_mixed_content_value
parameter_list|(
specifier|final
name|boolean
name|preserve
parameter_list|,
specifier|final
name|String
name|typeXml
parameter_list|,
specifier|final
name|String
name|typeXquery
parameter_list|)
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|AuthenticationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
block|{
name|store_preserve_ws_mixed_content_value
argument_list|(
name|preserve
argument_list|,
name|typeXml
argument_list|)
expr_stmt|;
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
init|)
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
specifier|final
name|Sequence
name|result
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|typeXquery
argument_list|,
literal|null
argument_list|)
decl_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|Properties
name|props
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
name|props
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
specifier|final
name|SAXSerializer
name|serializer
init|=
operator|new
name|SAXSerializer
argument_list|(
name|out
argument_list|,
name|props
argument_list|)
decl_stmt|;
name|serializer
operator|.
name|startDocument
argument_list|()
expr_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|result
operator|.
name|iterate
argument_list|()
init|;
name|i
operator|.
name|hasNext
argument_list|()
condition|;
control|)
block|{
specifier|final
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
name|next
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|serializer
argument_list|,
name|props
argument_list|)
expr_stmt|;
block|}
name|serializer
operator|.
name|endDocument
argument_list|()
expr_stmt|;
return|return
name|out
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|retrieve_preserve_mixed_ws
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|AuthenticationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
block|{
comment|//Nodes 1, 7 and 13 are not in mixed-contents and should not be preserved. They are the spaces between elements x and y, y and z, and z and x.
name|assertEquals
argument_list|(
name|RESULT_PRESERVE_MIXED_WS_XML
argument_list|,
name|store_and_retrieve_ws_mixed_content_value
argument_list|(
literal|true
argument_list|,
name|XML
argument_list|,
name|XQUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Ignore
annotation|@
name|Test
specifier|public
name|void
name|retrieve_no_preserve_mixed_ws
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|AuthenticationException
throws|,
name|SAXException
throws|,
name|XPathException
block|{
name|assertEquals
argument_list|(
name|RESULT_NO_PRESERVE_MIXED_WS_XML
argument_list|,
name|store_and_retrieve_ws_mixed_content_value
argument_list|(
literal|false
argument_list|,
name|XML
argument_list|,
name|XQUERY
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|retrieve_xslt_preserve_mixed_ws
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
throws|,
name|AuthenticationException
throws|,
name|SAXException
throws|,
name|XPathException
block|{
name|assertEquals
argument_list|(
name|RESULT_XML_XSLT
argument_list|,
name|store_and_retrieve_ws_mixed_content_value
argument_list|(
literal|true
argument_list|,
name|XML_XSLT
argument_list|,
name|XQUERY_XSLT
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

