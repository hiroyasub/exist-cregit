begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-10 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xqj
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
name|XMLReaderObjectFactory
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
name|value
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
name|dom
operator|.
name|persistent
operator|.
name|DocumentImpl
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
name|persistent
operator|.
name|NodeProxy
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
name|javax
operator|.
name|xml
operator|.
name|stream
operator|.
name|XMLStreamException
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
name|io
operator|.
name|StringReader
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

begin_comment
comment|/**  * @author Wolfgang Meier  */
end_comment

begin_comment
comment|/**  * Test cases for the Marshaller class methods. The Marshaller class offers serialization services  * needed by the XQJ interfaces.  *   * @author Cherif YAYA  *  */
end_comment

begin_class
specifier|public
class|class
name|MarshallerTest
block|{
specifier|private
specifier|static
name|XmldbURI
name|TEST_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"xqjmarhallertest"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|TEST_DOC
init|=
literal|"<test>"
operator|+
literal|"<div xmlns=\"urn:foo\" xmlns:ns1=\"urn:baz\" id=\"div1\">"
operator|+
literal|"<head>Title</head>"
operator|+
literal|"<p ns1:attr=\"a\" rend=\"bold\">Some<hi>text</hi>.</p>"
operator|+
literal|"</div>"
operator|+
literal|"</test>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|atomicValues
parameter_list|()
throws|throws
name|EXistException
throws|,
name|XPathException
throws|,
name|SAXException
throws|,
name|XMLStreamException
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
init|)
block|{
name|ValueSequence
name|values
init|=
operator|new
name|ValueSequence
argument_list|(
literal|3
argument_list|)
decl_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|StringValue
argument_list|(
literal|"foo"
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
literal|2000
argument_list|,
name|Type
operator|.
name|INTEGER
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|IntegerValue
argument_list|(
literal|1000
argument_list|,
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|BooleanValue
argument_list|(
literal|false
argument_list|)
argument_list|)
expr_stmt|;
name|values
operator|.
name|add
argument_list|(
operator|new
name|DoubleValue
argument_list|(
literal|1000.1
argument_list|)
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
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|Marshaller
operator|.
name|marshall
argument_list|(
name|broker
argument_list|,
name|values
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
name|String
name|serialized
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Sequence
name|seq
init|=
name|Marshaller
operator|.
name|demarshall
argument_list|(
name|broker
argument_list|,
operator|new
name|StringReader
argument_list|(
name|serialized
argument_list|)
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"foo"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|1
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"2000"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|2
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"1000"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|3
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"false"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|seq
operator|.
name|itemAt
argument_list|(
literal|4
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"1000.1"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|nodes
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
name|XMLStreamException
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
init|)
block|{
name|DocumentImpl
name|doc
init|=
operator|(
name|DocumentImpl
operator|)
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|)
decl_stmt|;
name|NodeProxy
name|p
init|=
operator|new
name|NodeProxy
argument_list|(
name|doc
argument_list|,
name|pool
operator|.
name|getNodeFactory
argument_list|()
operator|.
name|createFromString
argument_list|(
literal|"1.1"
argument_list|)
argument_list|)
decl_stmt|;
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
operator|new
name|Properties
argument_list|()
argument_list|)
decl_stmt|;
name|Marshaller
operator|.
name|marshall
argument_list|(
name|broker
argument_list|,
name|p
argument_list|,
name|serializer
argument_list|)
expr_stmt|;
name|String
name|serialized
init|=
name|writer
operator|.
name|toString
argument_list|()
decl_stmt|;
name|Sequence
name|seq
init|=
name|Marshaller
operator|.
name|demarshall
argument_list|(
name|broker
argument_list|,
operator|new
name|StringReader
argument_list|(
name|serialized
argument_list|)
argument_list|)
decl_stmt|;
name|assertTrue
argument_list|(
name|Type
operator|.
name|subTypeOf
argument_list|(
name|seq
operator|.
name|getItemType
argument_list|()
argument_list|,
name|Type
operator|.
name|NODE
argument_list|)
argument_list|)
expr_stmt|;
name|NodeValue
name|n
init|=
operator|(
name|NodeValue
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|writer
operator|=
operator|new
name|StringWriter
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|serializer
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
name|n
operator|.
name|toSAX
argument_list|(
name|broker
argument_list|,
name|serializer
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|streamToNodeTest
parameter_list|()
throws|throws
name|XMLStreamException
block|{
name|Node
name|n
init|=
name|Marshaller
operator|.
name|streamToNode
argument_list|(
name|TEST_DOC
argument_list|)
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
comment|//            SAXSerializer serializer =
operator|new
name|SAXSerializer
argument_list|(
name|writer
argument_list|,
operator|new
name|Properties
argument_list|()
argument_list|)
expr_stmt|;
comment|//n.toSAX(null, serializer, new Properties());
name|assertEquals
argument_list|(
literal|"test"
argument_list|,
name|n
operator|.
name|getLocalName
argument_list|()
argument_list|)
expr_stmt|;
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
argument_list|()
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
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
specifier|final
name|TransactionManager
name|transact
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
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
specifier|final
name|IndexInfo
name|info
init|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test.xml"
argument_list|)
argument_list|,
name|TEST_DOC
argument_list|)
decl_stmt|;
name|root
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|TEST_DOC
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|shutdown
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
specifier|final
name|TransactionManager
name|transact
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
name|getSystemSubject
argument_list|()
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

