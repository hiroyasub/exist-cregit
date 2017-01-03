begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|util
package|;
end_package

begin_import
import|import
name|com
operator|.
name|evolvedbinary
operator|.
name|j8fu
operator|.
name|tuple
operator|.
name|Tuple2
import|;
end_import

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
name|LockedDocument
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
name|transform
operator|.
name|OutputKeys
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
name|nio
operator|.
name|file
operator|.
name|Files
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
name|Optional
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|ThreadLocalRandom
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

begin_comment
comment|/**  * Tests around security exploits of the {@link org.xml.sax.XMLReader}  */
end_comment

begin_class
specifier|public
class|class
name|XMLReaderSecurityTest
block|{
specifier|private
specifier|final
specifier|static
name|int
name|START_CHAR_RANGE
init|=
literal|'@'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|END_CHAR_RANGE
init|=
literal|'~'
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|int
name|SECRET_LENGTH
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|TEST_COLLECTION
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXTERNAL_FILE_PLACEHOLDER
init|=
literal|"file:///topsecret"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXPANSION_DOC
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
operator|+
literal|"<!DOCTYPE foo [\n"
operator|+
literal|"<!ELEMENT foo ANY>\n"
operator|+
literal|"<!ENTITY xxe SYSTEM \""
operator|+
name|EXTERNAL_FILE_PLACEHOLDER
operator|+
literal|"\">]>\n"
operator|+
literal|"<foo>&xxe;</foo>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXPECTED_EXPANSION_DISABLED_DOC
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo/>\n"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|EXPECTED_EXPANDED_DOC
init|=
literal|"<?xml version=\"1.0\" encoding=\"UTF-8\"?><foo>"
operator|+
name|EXTERNAL_FILE_PLACEHOLDER
operator|+
literal|"</foo>\n"
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|final
specifier|static
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
name|setupTestData
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
name|brokerPool
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
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|createCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Collection
name|createCollection
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
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
return|return
name|collection
return|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|removeTestData
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
name|brokerPool
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
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
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
name|getCollection
argument_list|(
name|TEST_COLLECTION
argument_list|)
decl_stmt|;
if|if
condition|(
name|testCollection
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|testCollection
argument_list|)
condition|)
block|{
name|transaction
operator|.
name|abort
argument_list|()
expr_stmt|;
name|fail
argument_list|(
literal|"Unable to remove test collection"
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
annotation|@
name|Test
specifier|public
name|void
name|expandExternalEntities
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|TransformerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserConfig
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|parserConfig
operator|.
name|put
argument_list|(
literal|"http://xml.org/sax/features/external-general-entities"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|XMLReaderPool
operator|.
name|XmlParser
operator|.
name|XML_PARSER_FEATURES_PROPERTY
argument_list|,
name|parserConfig
argument_list|)
expr_stmt|;
comment|// create a temporary file on disk that contains secret info
specifier|final
name|Tuple2
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|secret
init|=
name|createTempSecretFile
argument_list|()
decl_stmt|;
specifier|final
name|XmldbURI
name|docName
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"expand-secret.xml"
argument_list|)
decl_stmt|;
comment|// attempt to store a document with an external entity which would be expanded to the content of the secret file
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
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
init|)
block|{
specifier|final
name|String
name|docContent
init|=
name|EXPANSION_DOC
operator|.
name|replace
argument_list|(
name|EXTERNAL_FILE_PLACEHOLDER
argument_list|,
name|secret
operator|.
name|_2
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|testCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
name|docContent
argument_list|)
decl_stmt|;
name|testCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|docContent
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// read back the document, to confirm that it does not contain the secret
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
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
name|READ_LOCK
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|LockedDocument
name|testDoc
init|=
name|testCollection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docName
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
init|)
block|{
comment|// release the collection lock early inline with asymmetrical locking
name|testCollection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|expected
init|=
name|EXPECTED_EXPANDED_DOC
operator|.
name|replace
argument_list|(
name|EXTERNAL_FILE_PLACEHOLDER
argument_list|,
name|secret
operator|.
name|_1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|expected
argument_list|,
name|serialize
argument_list|(
name|testDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
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
annotation|@
name|Test
specifier|public
name|void
name|cannotExpandExternalEntitiesWhenDisabled
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|TransformerException
block|{
specifier|final
name|BrokerPool
name|brokerPool
init|=
name|existEmbeddedServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|Boolean
argument_list|>
name|parserConfig
init|=
operator|new
name|HashMap
argument_list|<>
argument_list|()
decl_stmt|;
name|parserConfig
operator|.
name|put
argument_list|(
literal|"http://xml.org/sax/features/external-general-entities"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|brokerPool
operator|.
name|getConfiguration
argument_list|()
operator|.
name|setProperty
argument_list|(
name|XMLReaderPool
operator|.
name|XmlParser
operator|.
name|XML_PARSER_FEATURES_PROPERTY
argument_list|,
name|parserConfig
argument_list|)
expr_stmt|;
comment|// create a temporary file on disk that contains secret info
specifier|final
name|Tuple2
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|secret
init|=
name|createTempSecretFile
argument_list|()
decl_stmt|;
specifier|final
name|XmldbURI
name|docName
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"expand-secret.xml"
argument_list|)
decl_stmt|;
comment|// attempt to store a document with an external entity which would be expanded to the content of the secret file
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
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
init|;
init|)
block|{
specifier|final
name|String
name|docContent
init|=
name|EXPANSION_DOC
operator|.
name|replace
argument_list|(
name|EXTERNAL_FILE_PLACEHOLDER
argument_list|,
name|secret
operator|.
name|_2
operator|.
name|toUri
argument_list|()
operator|.
name|toString
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|IndexInfo
name|indexInfo
init|=
name|testCollection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|docName
argument_list|,
name|docContent
argument_list|)
decl_stmt|;
name|testCollection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|docContent
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// read back the document, to confirm that it does not contain the secret
try|try
init|(
specifier|final
name|DBBroker
name|broker
init|=
name|brokerPool
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|brokerPool
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
name|brokerPool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
try|try
init|(
specifier|final
name|Collection
name|testCollection
init|=
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
name|READ_LOCK
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|LockedDocument
name|testDoc
init|=
name|testCollection
operator|.
name|getDocumentWithLock
argument_list|(
name|broker
argument_list|,
name|docName
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
comment|// release the collection lock early inline with asymmetrical locking
name|testCollection
operator|.
name|close
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|testDoc
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|EXPECTED_EXPANSION_DISABLED_DOC
argument_list|,
name|serialize
argument_list|(
name|testDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
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
specifier|private
name|String
name|serialize
parameter_list|(
specifier|final
name|Document
name|doc
parameter_list|)
throws|throws
name|TransformerException
throws|,
name|IOException
block|{
specifier|final
name|Transformer
name|transformer
init|=
name|TransformerFactory
operator|.
name|newInstance
argument_list|()
operator|.
name|newTransformer
argument_list|()
decl_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|INDENT
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
name|transformer
operator|.
name|setOutputProperty
argument_list|(
name|OutputKeys
operator|.
name|OMIT_XML_DECLARATION
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
try|try
init|(
specifier|final
name|StringWriter
name|writer
init|=
operator|new
name|StringWriter
argument_list|()
init|)
block|{
specifier|final
name|StreamResult
name|result
init|=
operator|new
name|StreamResult
argument_list|(
name|writer
argument_list|)
decl_stmt|;
specifier|final
name|DOMSource
name|source
init|=
operator|new
name|DOMSource
argument_list|(
name|doc
argument_list|)
decl_stmt|;
name|transformer
operator|.
name|transform
argument_list|(
name|source
argument_list|,
name|result
argument_list|)
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
comment|/**      * @return A tuple whose first item is the secret, and the second which is the path to a temporary file containing the secret      */
specifier|private
name|Tuple2
argument_list|<
name|String
argument_list|,
name|Path
argument_list|>
name|createTempSecretFile
parameter_list|()
throws|throws
name|IOException
block|{
specifier|final
name|Path
name|file
init|=
name|Files
operator|.
name|createTempFile
argument_list|(
literal|"exist.XMLReaderSecurityTest"
argument_list|,
literal|"topsecret"
argument_list|)
decl_stmt|;
specifier|final
name|String
name|randomSecret
init|=
name|generateRandomString
argument_list|(
name|SECRET_LENGTH
argument_list|)
decl_stmt|;
return|return
operator|new
name|Tuple2
argument_list|<>
argument_list|(
name|randomSecret
argument_list|,
name|Files
operator|.
name|write
argument_list|(
name|file
argument_list|,
name|randomSecret
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
specifier|private
name|String
name|generateRandomString
parameter_list|(
specifier|final
name|int
name|length
parameter_list|)
block|{
specifier|final
name|char
index|[]
name|chars
init|=
operator|new
name|char
index|[
name|length
index|]
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
name|length
condition|;
name|i
operator|++
control|)
block|{
specifier|final
name|char
name|c
init|=
operator|(
name|char
operator|)
name|ThreadLocalRandom
operator|.
name|current
argument_list|()
operator|.
name|nextInt
argument_list|(
name|START_CHAR_RANGE
argument_list|,
name|END_CHAR_RANGE
operator|+
literal|1
argument_list|)
decl_stmt|;
name|chars
index|[
name|i
index|]
operator|=
name|c
expr_stmt|;
block|}
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|chars
argument_list|)
return|;
block|}
block|}
end_class

end_unit

