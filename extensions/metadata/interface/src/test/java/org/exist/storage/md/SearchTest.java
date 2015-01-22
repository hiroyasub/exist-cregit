begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2015 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|md
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
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
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
name|CollectionConfigurationManager
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
name|ConfigurationHelper
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
comment|/**  * @author<a href="mailto:shabanovd@gmail.com">Dmitriy Shabanov</a>  *  */
end_comment

begin_class
specifier|public
class|class
name|SearchTest
block|{
specifier|private
specifier|static
name|String
name|COLLECTION_CONFIG
init|=
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<index>"
operator|+
literal|"</index>"
operator|+
literal|"</collection>"
decl_stmt|;
comment|/** /db/test **/
specifier|private
specifier|static
name|XmldbURI
name|col1uri
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
decl_stmt|;
comment|/** /db/test/test2 **/
specifier|private
specifier|static
name|XmldbURI
name|col2uri
init|=
name|TestConstants
operator|.
name|TEST_COLLECTION_URI2
decl_stmt|;
comment|/** /db/moved **/
specifier|private
specifier|static
name|XmldbURI
name|col3uri
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"moved"
argument_list|)
decl_stmt|;
comment|/** /db/test/test_string.xml **/
specifier|private
specifier|static
name|XmldbURI
name|doc1uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test_string.xml"
argument_list|)
decl_stmt|;
comment|/** /db/test/test_string2.xml **/
specifier|private
specifier|static
name|XmldbURI
name|doc2uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test_string2.xml"
argument_list|)
decl_stmt|;
comment|/** /db/test/test2/test_2.xml **/
specifier|private
specifier|static
name|XmldbURI
name|doc3uri
init|=
name|col2uri
operator|.
name|append
argument_list|(
literal|"test_2.xml"
argument_list|)
decl_stmt|;
comment|/** /db/moved/test_string.xml **/
specifier|private
specifier|static
name|XmldbURI
name|doc4uri
init|=
name|col3uri
operator|.
name|append
argument_list|(
literal|"test_string.xml"
argument_list|)
decl_stmt|;
comment|/** /db/test/test.binary **/
specifier|private
specifier|static
name|XmldbURI
name|doc5uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test.binary"
argument_list|)
decl_stmt|;
comment|/** /db/moved/test.binary **/
specifier|private
specifier|static
name|XmldbURI
name|doc6uri
init|=
name|col3uri
operator|.
name|append
argument_list|(
literal|"test.binary"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML1
init|=
literal|"<test/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML2
init|=
literal|"<test2/>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|BINARY
init|=
literal|"test"
decl_stmt|;
specifier|private
specifier|static
name|String
name|wrongXML
init|=
literal|"<test>"
decl_stmt|;
specifier|private
specifier|static
name|String
name|KEY1
init|=
literal|"key1"
decl_stmt|;
specifier|private
specifier|static
name|String
name|VALUE1
init|=
literal|"value1"
decl_stmt|;
specifier|private
specifier|static
name|String
name|KEY2
init|=
literal|"key2"
decl_stmt|;
specifier|private
specifier|static
name|String
name|VALUE2
init|=
literal|"value2 auth"
decl_stmt|;
specifier|private
specifier|static
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|static
name|DocumentImpl
name|doc1
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|DocumentImpl
name|doc2
init|=
literal|null
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|test_00
parameter_list|()
throws|throws
name|Exception
block|{
name|startDB
argument_list|()
expr_stmt|;
name|BrokerPool
name|db
init|=
literal|null
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
try|try
block|{
name|db
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|broker
operator|=
name|db
operator|.
name|get
argument_list|(
name|db
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|MetaData
name|md
init|=
name|MetaData
operator|.
name|get
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|md
argument_list|)
expr_stmt|;
name|Metas
name|docMD
init|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getMetas
argument_list|(
name|doc1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|docMD
argument_list|)
expr_stmt|;
name|String
name|uuid
init|=
name|docMD
operator|.
name|getUUID
argument_list|()
decl_stmt|;
name|assertNotNull
argument_list|(
name|uuid
argument_list|)
expr_stmt|;
name|DocumentImpl
name|doc
init|=
name|MetaData
operator|.
name|get
argument_list|()
operator|.
name|getDocument
argument_list|(
name|uuid
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|doc
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|doc1
operator|.
name|equals
argument_list|(
name|doc
argument_list|)
argument_list|)
expr_stmt|;
comment|//add first key-value
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|Meta
name|meta
init|=
name|docMD
operator|.
name|get
argument_list|(
name|KEY1
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|meta
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|VALUE1
argument_list|,
name|meta
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|String
argument_list|>
name|dbRoot
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|dbRoot
operator|.
name|add
argument_list|(
literal|"/db"
argument_list|)
expr_stmt|;
comment|//        	assertEquals(
comment|//                "in-memory#element {results} {in-memory#element {search} {in-memory#attribute {uri} {/db/test/test_string.xml}  in-memory#attribute {score} {0.30685282} } } ",
comment|//                md.search("value1", dbRoot).toString()
comment|//            );
comment|//
comment|//        	assertEquals(
comment|//    	        "in-memory#element {results} {in-memory#element {search} {in-memory#attribute {uri} {/db/test/test_string.xml}  in-memory#attribute {score} {0.30685282} in-memory#element {field} {in-memory#attribute {name} {key1} in-memory#element {exist:match} {in-memory#text {value1} } } } } ",
comment|//    	        md.search("key1:value1", dbRoot).toString()
comment|//	        );
comment|//
comment|//            assertEquals(
comment|//                "in-memory#element {results} {in-memory#element {search} {in-memory#attribute {uri} {/db/test/test_string.xml}  in-memory#attribute {score} {0.30685282} in-memory#element {field} {in-memory#attribute {name} {key1} in-memory#element {exist:match} {in-memory#text {value1} } } } } ",
comment|//                md.search("key1:value*", dbRoot).toString()
comment|//            );
comment|//
comment|//            assertEquals(
comment|//                "in-memory#element {results} {} ",
comment|//                md.search("key1:value2", dbRoot).toString()
comment|//            );
comment|//    	//add second key-value
comment|//    	docMD.put(KEY2, VALUE2);
comment|//
comment|//    	meta = docMD.get(KEY2);
comment|//    	assertNotNull(meta);
comment|//
comment|//    	assertEquals(VALUE2, meta.getValue());
comment|//
comment|//    	//replace first key-value
comment|//    	docMD.put(KEY1, VALUE2);
comment|//
comment|//    	meta = docMD.get(KEY1);
comment|//    	assertNotNull(meta);
comment|//
comment|//    	assertEquals(VALUE2, meta.getValue());
comment|//
comment|//    	//second document
comment|//    	docMD = MetaData.get().getMetas(doc2uri);
comment|//
comment|//    	assertNotNull(docMD);
comment|//
comment|//    	uuid = docMD.getUUID();
comment|//    	assertNotNull(uuid);
comment|//
comment|//    	doc = MetaData.get().getDocument(uuid);
comment|//    	assertNotNull(doc);
comment|//    	assertTrue(doc2.equals(doc));
comment|//
comment|//    	//add first key-value
comment|//    	docMD.put(KEY1, VALUE2);
comment|//
comment|//    	meta = docMD.get(KEY1);
comment|//    	assertNotNull(meta);
comment|//
comment|//    	assertEquals(VALUE2, meta.getValue());
comment|//
comment|//    	//add second key-value
comment|//    	docMD.put(KEY2, VALUE1);
comment|//
comment|//    	meta = docMD.get(KEY2);
comment|//    	assertNotNull(meta);
comment|//
comment|//    	assertEquals(VALUE1, meta.getValue());
comment|//
comment|//    	//replace first key-value
comment|//    	docMD.put(KEY1, VALUE1);
comment|//
comment|//    	meta = docMD.get(KEY1);
comment|//    	assertNotNull(meta);
comment|//
comment|//    	assertEquals(VALUE1, meta.getValue());
block|}
finally|finally
block|{
if|if
condition|(
name|db
operator|!=
literal|null
condition|)
name|db
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|cleanup
argument_list|()
expr_stmt|;
block|}
specifier|private
specifier|static
name|DocumentImpl
name|storeDocument
parameter_list|(
name|Txn
name|txn
parameter_list|,
name|DBBroker
name|broker
parameter_list|,
name|Collection
name|col
parameter_list|,
name|XmldbURI
name|uri
parameter_list|,
name|String
name|data
parameter_list|)
throws|throws
name|TriggerException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|LockException
throws|,
name|IOException
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"STORING DOCUMENT...."
argument_list|)
expr_stmt|;
name|IndexInfo
name|info
init|=
name|col
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|data
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"STORING DOCUMENT....SECOND ROUND...."
argument_list|)
expr_stmt|;
name|col
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|data
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
operator|.
name|getDocument
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"STORING DOCUMENT....DONE."
argument_list|)
expr_stmt|;
return|return
name|info
operator|.
name|getDocument
argument_list|()
return|;
block|}
comment|//@BeforeClass
specifier|public
specifier|static
name|void
name|startDB
parameter_list|()
block|{
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|txnManager
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|File
name|confFile
init|=
name|ConfigurationHelper
operator|.
name|lookup
argument_list|(
literal|"conf.xml"
argument_list|)
decl_stmt|;
name|Configuration
name|config
init|=
operator|new
name|Configuration
argument_list|(
name|confFile
operator|.
name|getAbsolutePath
argument_list|()
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
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|pool
argument_list|)
expr_stmt|;
name|pool
operator|.
name|getPluginsManager
argument_list|()
operator|.
name|addPlugin
argument_list|(
literal|"org.exist.storage.md.MDStorageManager"
argument_list|)
expr_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|clean
argument_list|()
expr_stmt|;
name|txnManager
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|txnManager
argument_list|)
expr_stmt|;
name|txn
operator|=
name|txnManager
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|col1uri
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
name|txn
argument_list|,
name|root
argument_list|)
expr_stmt|;
name|Collection
name|test2
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|col2uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|test2
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|test2
argument_list|)
expr_stmt|;
name|CollectionConfigurationManager
name|mgr
init|=
name|pool
operator|.
name|getConfigurationManager
argument_list|()
decl_stmt|;
name|mgr
operator|.
name|addConfiguration
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|COLLECTION_CONFIG
argument_list|)
expr_stmt|;
name|doc1
operator|=
name|storeDocument
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|doc1uri
argument_list|,
name|XML1
argument_list|)
expr_stmt|;
name|doc2
operator|=
name|storeDocument
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|root
argument_list|,
name|doc2uri
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"store "
operator|+
name|doc5uri
argument_list|)
expr_stmt|;
name|root
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|doc5uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|BINARY
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|commit
argument_list|(
name|txn
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
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
comment|//@AfterClass
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|clean
argument_list|()
expr_stmt|;
name|shutdown
argument_list|()
expr_stmt|;
block|}
comment|//@AfterClass
specifier|private
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|false
argument_list|)
expr_stmt|;
name|pool
operator|=
literal|null
expr_stmt|;
name|doc1
operator|=
literal|null
expr_stmt|;
name|doc2
operator|=
literal|null
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"stopped"
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|clean
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CLEANING..."
argument_list|)
expr_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|txnManager
init|=
literal|null
decl_stmt|;
name|Txn
name|txn
init|=
literal|null
decl_stmt|;
try|try
block|{
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|broker
argument_list|)
expr_stmt|;
name|txnManager
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|txnManager
argument_list|)
expr_stmt|;
name|txn
operator|=
name|txnManager
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|assertNotNull
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Transaction started ..."
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
comment|//            col = broker.getOrCreateCollection(txn, col2uri);
comment|//            assertNotNull(col);
comment|//        	broker.removeCollection(txn, col);
name|col
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|col3uri
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|txnManager
operator|.
name|commit
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|txnManager
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|pool
operator|!=
literal|null
condition|)
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"CLEANED."
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

