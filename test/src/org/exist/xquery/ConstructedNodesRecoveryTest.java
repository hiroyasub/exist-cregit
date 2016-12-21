begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
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
name|source
operator|.
name|StringSource
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
operator|.
name|LockMode
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
name|Serializer
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
name|TransactionException
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
name|util
operator|.
name|serializer
operator|.
name|SerializerPool
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
name|InputSource
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
name|StringReader
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
name|xml
operator|.
name|sax
operator|.
name|SAXException
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
comment|/**  * Tests for recovery of database corruption after constructed node operations (in-memory nodes)  * @author Adam Retter<adam.retter@devon.gov.uk>  */
end_comment

begin_class
specifier|public
class|class
name|ConstructedNodesRecoveryTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|xquery
init|=
literal|"declare variable $categories := \n"
operator|+
literal|"<categories>\n"
operator|+
literal|"<category uid=\"1\">Fruit</category>\n"
operator|+
literal|"<category uid=\"2\">Vegetable</category>\n"
operator|+
literal|"<category uid=\"3\">Meat</category>\n"
operator|+
literal|"<category uid=\"4\">Dairy</category>\n"
operator|+
literal|"</categories>\n"
operator|+
literal|";\n\n"
operator|+
literal|"for $category in $categories/category return\n"
operator|+
literal|"	element option {\n"
operator|+
literal|"		attribute value {\n"
operator|+
literal|"			$category/@uid\n"
operator|+
literal|"		},\n"
operator|+
literal|"		text { $category }\n"
operator|+
literal|"	}"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|expectedResults
index|[]
init|=
block|{
literal|"Fruit"
block|,
literal|"Vegetable"
block|,
literal|"Meat"
block|,
literal|"Dairy"
block|}
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|testDocument
init|=
literal|"<fruit>"
operator|+
literal|"<apple colour=\"green\"/>"
operator|+
literal|"<pear colour=\"green\"/>"
operator|+
literal|"<orange colour=\"orange\"/>"
operator|+
literal|"<dragonfruit colour=\"pink\"/>"
operator|+
literal|"<grapefruit colour=\"yellow\"/>"
operator|+
literal|"</fruit>"
decl_stmt|;
comment|/** 	 * Issues a query against constructed nodes and then corrupts the database (intentionally) 	 */
annotation|@
name|Test
specifier|public
name|void
name|constructedNodesCorrupt
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
name|constructedNodeQuery
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/** 	 * Recovers from corruption (intentional) and then issues a query against constructed nodes 	 */
annotation|@
name|Test
specifier|public
name|void
name|constructedNodesRecover
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|DatabaseConfigurationException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|XPathException
throws|,
name|EXistException
block|{
name|constructedNodeQuery
argument_list|(
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|storeTestDocument
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|TransactionManager
name|transact
parameter_list|,
name|String
name|documentName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|EXistException
block|{
comment|//create a transaction
try|try
init|(
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
comment|//get the test collection
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|//store test document
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
name|documentName
argument_list|)
argument_list|,
name|testDocument
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
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
operator|new
name|InputSource
argument_list|(
operator|new
name|StringReader
argument_list|(
name|testDocument
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
comment|//commit the transaction
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|createTempChildCollection
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|TransactionManager
name|transact
parameter_list|,
name|String
name|childCollectionName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|TransactionException
block|{
comment|//create a transaction
try|try
init|(
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
comment|//get the test collection
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|TEMP_COLLECTION_URI
operator|.
name|append
argument_list|(
name|childCollectionName
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|//commit the transaction
name|transact
operator|.
name|commit
argument_list|(
name|transaction
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|testDocumentIsValid
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|TransactionManager
name|transact
parameter_list|,
name|String
name|documentName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|TransactionException
block|{
comment|//create a transaction
try|try
init|(
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
comment|//get the test collection
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|root
argument_list|)
expr_stmt|;
comment|//get the test document
name|DocumentImpl
name|doc
init|=
name|root
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|documentName
argument_list|)
argument_list|,
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
decl_stmt|;
name|Serializer
name|serializer
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
decl_stmt|;
name|serializer
operator|.
name|reset
argument_list|()
expr_stmt|;
name|SAXSerializer
name|sax
init|=
literal|null
decl_stmt|;
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
decl_stmt|;
name|sax
operator|=
operator|(
name|SAXSerializer
operator|)
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|borrowObject
argument_list|(
name|SAXSerializer
operator|.
name|class
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
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|outputProperties
operator|.
name|setProperty
argument_list|(
name|OutputKeys
operator|.
name|ENCODING
argument_list|,
literal|"UTF-8"
argument_list|)
expr_stmt|;
name|sax
operator|.
name|setOutput
argument_list|(
name|writer
argument_list|,
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|outputProperties
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setSAXHandlers
argument_list|(
name|sax
argument_list|,
name|sax
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|toSAX
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|SerializerPool
operator|.
name|getInstance
argument_list|()
operator|.
name|returnObject
argument_list|(
name|sax
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|testDocument
argument_list|,
name|writer
operator|.
name|toString
argument_list|()
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
specifier|private
name|void
name|testTempChildCollectionExists
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|TransactionManager
name|transact
parameter_list|,
name|String
name|childCollectionName
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
throws|,
name|TransactionException
block|{
comment|//create a transaction
try|try
init|(
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
comment|//get the temp child collection
name|Collection
name|tempChildCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|XmldbURI
operator|.
name|TEMP_COLLECTION_URI
operator|.
name|append
argument_list|(
name|childCollectionName
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|tempChildCollection
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|tempChildCollection
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
comment|/** 	 * Performs a query against constructed nodes, with the option of forcefully corrupting the database 	 *  	 * @param forceCorruption	Should the database be forcefully corrupted 	 */
specifier|private
name|void
name|constructedNodeQuery
parameter_list|(
name|boolean
name|forceCorruption
parameter_list|)
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|XPathException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
name|forceCorruption
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDB
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
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|transact
argument_list|)
expr_stmt|;
comment|//only store the documents the first time
if|if
condition|(
name|forceCorruption
condition|)
block|{
comment|//store a first test document
name|storeTestDocument
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testcr1.xml"
argument_list|)
expr_stmt|;
comment|//store a second test document
name|storeTestDocument
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testcr2.xml"
argument_list|)
expr_stmt|;
block|}
comment|//create some child collections in TEMP collection
name|createTempChildCollection
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testchild1"
argument_list|)
expr_stmt|;
name|createTempChildCollection
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testchild2"
argument_list|)
expr_stmt|;
comment|//execute an xquery
name|XQuery
name|service
init|=
name|pool
operator|.
name|getXQueryService
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|service
argument_list|)
expr_stmt|;
name|CompiledXQuery
name|compiled
init|=
name|service
operator|.
name|compile
argument_list|(
name|broker
argument_list|,
operator|new
name|XQueryContext
argument_list|(
name|pool
argument_list|)
argument_list|,
operator|new
name|StringSource
argument_list|(
name|xquery
argument_list|)
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|compiled
argument_list|)
expr_stmt|;
name|Sequence
name|result
init|=
name|service
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
name|compiled
argument_list|,
literal|null
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|result
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|expectedResults
operator|.
name|length
argument_list|,
name|result
operator|.
name|getItemCount
argument_list|()
argument_list|)
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
name|getItemCount
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|assertEquals
argument_list|(
name|expectedResults
index|[
name|i
index|]
argument_list|,
operator|(
name|String
operator|)
name|result
operator|.
name|itemAt
argument_list|(
name|i
argument_list|)
operator|.
name|getStringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|//read the first test document
name|testDocumentIsValid
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testcr1.xml"
argument_list|)
expr_stmt|;
comment|//read the second test document
name|testDocumentIsValid
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testcr1.xml"
argument_list|)
expr_stmt|;
comment|//test the child collections exist
name|testTempChildCollectionExists
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testchild1"
argument_list|)
expr_stmt|;
name|testTempChildCollectionExists
argument_list|(
name|broker
argument_list|,
name|transact
argument_list|,
literal|"testchild2"
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getJournalManager
argument_list|()
operator|.
name|get
argument_list|()
operator|.
name|flush
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
throws|throws
name|DatabaseConfigurationException
throws|,
name|EXistException
block|{
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|()
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
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
return|;
block|}
annotation|@
name|After
specifier|public
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

