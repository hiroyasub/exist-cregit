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
name|DatabaseConfigurationException
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
comment|/**  * Tests the indexer.  *  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|IndexerTest2
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<?xml version=\"1.0\"?>\n"
operator|+
literal|"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n"
operator|+
literal|"Government of new Territory of NevadaâGovernor<name>Nye</name><lb/>and the practical jokersâ<name>Mr. Clemens</name> begins journalistic life<lb/>on<name>Virginia City</name><name>Enterprise</name>.\n"
operator|+
literal|"</TEI>\n"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XQUERY
init|=
literal|"declare namespace tei=\"http://www.tei-c.org/ns/1.0\"; "
operator|+
literal|"declare boundary-space preserve; "
operator|+
literal|"declare function local:get-text($input as node()*) as item()* {"
operator|+
literal|"   for $node in $input/node() "
operator|+
literal|"       return "
operator|+
literal|"           typeswitch($node)"
operator|+
literal|"               case text() return $node "
operator|+
literal|"               default return local:get-text($node) "
operator|+
literal|"}; "
operator|+
literal|"let $in-memory := "
operator|+
literal|"<TEI xmlns=\"http://www.tei-c.org/ns/1.0\">\n"
operator|+
literal|"Government of new Territory of NevadaâGovernor<name>Nye</name><lb/>and the practical jokersâ<name>Mr. Clemens</name> begins journalistic life<lb/>on<name>Virginia City</name><name>Enterprise</name>.\n"
operator|+
literal|"</TEI>"
operator|+
literal|"let $stored := doc('"
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
name|TEST_XML_URI2
operator|.
name|toString
argument_list|()
operator|+
literal|"') "
operator|+
literal|"return "
operator|+
literal|"<result name=\""
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
name|TEST_XML_URI2
operator|.
name|toString
argument_list|()
operator|+
literal|"\">\n"
operator|+
literal|"<inline>{string-join(local:get-text($in-memory))}</inline>\n"
operator|+
literal|"<stored>{string-join(local:get-text($stored))}</stored>\n"
operator|+
literal|"</result>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|store_preserve_mixed_ws
parameter_list|()
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
name|XPathException
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
name|assertTrue
argument_list|(
operator|(
operator|(
name|Boolean
operator|)
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|Indexer
operator|.
name|PROPERTY_PRESERVE_WS_MIXED_CONTENT
argument_list|)
operator|)
operator|.
name|booleanValue
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"none"
argument_list|,
name|pool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|getProperty
argument_list|(
name|Indexer
operator|.
name|PROPERTY_SUPPRESS_WHITESPACE
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|retrieve_boundary_space_preserve_with_preserve_mixed_ws
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
throws|,
name|IOException
block|{
name|assertEquals
argument_list|(
literal|"<result name=\""
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
name|TEST_XML_URI2
operator|.
name|toString
argument_list|()
operator|+
literal|"\">\n"
operator|+
literal|"<inline>\n"
operator|+
literal|"Government of new Territory of NevadaâGovernor Nye and the practical jokersâMr. Clemens begins journalistic life on Virginia City Enterprise.\n"
operator|+
literal|"</inline>\n"
operator|+
literal|"<stored>\n"
operator|+
literal|"Government of new Territory of NevadaâGovernor Nye and the practical jokersâMr. Clemens begins journalistic life on Virginia City Enterprise.\n"
operator|+
literal|"</stored>\n"
operator|+
literal|"</result>"
argument_list|,
name|executeQuery
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|executeQuery
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
throws|,
name|IOException
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
name|StringWriter
name|out
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|XQuery
name|xquery
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
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
name|XQUERY
argument_list|,
literal|null
argument_list|)
decl_stmt|;
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
specifier|private
specifier|static
name|void
name|storeDoc
parameter_list|()
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
name|TEST_XML_URI2
argument_list|,
name|XML
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
name|XML
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
name|put
argument_list|(
name|Indexer
operator|.
name|PROPERTY_PRESERVE_WS_MIXED_CONTENT
argument_list|,
literal|true
argument_list|)
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
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|setUp
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|AuthenticationException
block|{
name|storeDoc
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

