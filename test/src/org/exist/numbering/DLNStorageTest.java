begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|numbering
package|;
end_package

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
name|dom
operator|.
name|persistent
operator|.
name|NodeHandle
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
name|StorageAddress
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
name|util
operator|.
name|Configuration
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
name|Sequence
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
import|import
name|org
operator|.
name|w3c
operator|.
name|dom
operator|.
name|Attr
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
name|Text
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Path
import|;
end_import

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|file
operator|.
name|Paths
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

begin_class
specifier|public
class|class
name|DLNStorageTest
block|{
specifier|private
specifier|static
name|XmldbURI
name|TEST_COLLECTION
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|TEST_XML
init|=
literal|"<test>\n"
operator|+
literal|"<para>My first paragraph.</para>\n"
operator|+
literal|"<!-- A comment -->\n"
operator|+
literal|"<para>This one contains a<a href=\"#\">link</a>.</para>\n"
operator|+
literal|"<?echo \"A processing instruction\"?>\n"
operator|+
literal|"<para>Another<b>paragraph</b>.</para>\n"
operator|+
literal|"</test>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|nodeStorage
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
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
name|XQuery
name|xquery
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|xquery
argument_list|)
expr_stmt|;
comment|// test element ids
name|Sequence
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"doc('/db/test/test_string.xml')/test/para"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|3
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|comment
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.1"
argument_list|)
expr_stmt|;
name|comment
operator|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.3"
argument_list|)
expr_stmt|;
name|comment
operator|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|comment
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|,
literal|"1.5"
argument_list|)
expr_stmt|;
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"doc('/db/test/test_string.xml')/test//a"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|a
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.3.2"
argument_list|,
name|a
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test attribute id
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"doc('/db/test/test_string.xml')/test//a/@href"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|href
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|StorageAddress
operator|.
name|toString
argument_list|(
name|href
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"1.3.2.1"
argument_list|,
name|href
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test Attr deserialization
name|Attr
name|attr
init|=
operator|(
name|Attr
operator|)
name|href
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|StorageAddress
operator|.
name|toString
argument_list|(
operator|(
operator|(
name|NodeHandle
operator|)
name|attr
operator|)
argument_list|)
expr_stmt|;
comment|// test Attr fields
name|assertEquals
argument_list|(
name|attr
operator|.
name|getNodeName
argument_list|()
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getName
argument_list|()
argument_list|,
literal|"href"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|attr
operator|.
name|getValue
argument_list|()
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
comment|// test DOMFile.getNodeValue()
name|assertEquals
argument_list|(
name|href
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"#"
argument_list|)
expr_stmt|;
comment|// test text node
name|seq
operator|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"doc('/db/test/test_string.xml')/test//b/text()"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|seq
operator|.
name|getItemCount
argument_list|()
argument_list|)
expr_stmt|;
name|NodeProxy
name|text
init|=
operator|(
name|NodeProxy
operator|)
name|seq
operator|.
name|itemAt
argument_list|(
literal|0
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|"1.5.2.1"
argument_list|,
name|text
operator|.
name|getNodeId
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
comment|// test DOMFile.getNodeValue()
name|assertEquals
argument_list|(
name|text
operator|.
name|getStringValue
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
comment|// test Text deserialization
name|Text
name|node
init|=
operator|(
name|Text
operator|)
name|text
operator|.
name|getNode
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
name|node
operator|.
name|getNodeValue
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|node
operator|.
name|getData
argument_list|()
argument_list|,
literal|"paragraph"
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|String
name|file
init|=
literal|"conf.xml"
decl_stmt|;
specifier|final
name|Optional
argument_list|<
name|Path
argument_list|>
name|home
init|=
name|Optional
operator|.
name|ofNullable
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|,
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
argument_list|)
argument_list|)
operator|.
name|map
argument_list|(
name|Paths
operator|::
name|get
argument_list|)
decl_stmt|;
specifier|final
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|file
argument_list|,
name|home
argument_list|)
decl_stmt|;
name|BrokerPool
operator|.
name|configure
argument_list|(
literal|1
argument_list|,
literal|5
argument_list|,
name|config
argument_list|)
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
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
name|test
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
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|test
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|test
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
literal|"test_string.xml"
argument_list|)
argument_list|,
name|TEST_XML
argument_list|)
decl_stmt|;
comment|//TODO : unlock the collection here ?
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|test
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|TEST_XML
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
name|After
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

