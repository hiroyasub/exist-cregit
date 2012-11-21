begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2012 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  *  $Id$  */
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
name|util
operator|.
name|List
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
name|dom
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
name|serializers
operator|.
name|EXistOutputKeys
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
name|MatchDocumentsTest
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
comment|/** /db/moved **/
specifier|private
specifier|static
name|XmldbURI
name|col2uri
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
comment|/** /db/test/test_string1.xml **/
specifier|private
specifier|static
name|XmldbURI
name|doc1uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test_string1.xml"
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
comment|/** /db/test/test.binary **/
specifier|private
specifier|static
name|XmldbURI
name|doc3uri
init|=
name|col1uri
operator|.
name|append
argument_list|(
literal|"test.binary"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|XmldbURI
name|doc4uri
init|=
name|col2uri
operator|.
name|append
argument_list|(
literal|"test_string1.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|XML1
init|=
literal|"<test1/>"
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
name|KEY1
init|=
literal|"key1"
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
name|VALUE1
init|=
literal|"value1"
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
name|test_deleteCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|startDB
argument_list|()
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
name|md
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
name|DBBroker
name|broker
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
comment|//add first key-value
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|docMD
operator|.
name|put
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DocumentImpl
argument_list|>
name|list
init|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc1uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DELETE..."
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
name|list
operator|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|test_moveCollection
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|startDB
argument_list|()
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
name|md
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
name|DBBroker
name|broker
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
comment|//add first key-value
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|docMD
operator|.
name|put
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DocumentImpl
argument_list|>
name|list
init|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc1uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
name|Collection
name|parent
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col2uri
operator|.
name|removeLastSegment
argument_list|()
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|parent
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MOVE..."
argument_list|)
expr_stmt|;
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
name|broker
operator|.
name|moveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|,
name|parent
argument_list|,
name|col2uri
operator|.
name|lastSegment
argument_list|()
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
name|list
operator|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc4uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|test_moveResource
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|startDB
argument_list|()
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
name|md
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
name|DBBroker
name|broker
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
comment|//add first key-value
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|docMD
operator|.
name|put
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DocumentImpl
argument_list|>
name|list
init|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc1uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"MOVING..."
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
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
name|broker
operator|.
name|moveResource
argument_list|(
name|txn
argument_list|,
name|doc1
argument_list|,
name|col
argument_list|,
name|doc2uri
operator|.
name|lastSegment
argument_list|()
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
name|list
operator|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc2uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|test_deleteResource
parameter_list|()
throws|throws
name|Exception
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"test"
argument_list|)
expr_stmt|;
name|startDB
argument_list|()
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
name|md
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
name|DBBroker
name|broker
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
comment|//add first key-value
name|docMD
operator|.
name|put
argument_list|(
name|KEY1
argument_list|,
name|doc2
argument_list|)
expr_stmt|;
name|docMD
operator|.
name|put
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|List
argument_list|<
name|DocumentImpl
argument_list|>
name|list
init|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|doc1uri
argument_list|,
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
operator|.
name|getURI
argument_list|()
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DELETING..."
argument_list|)
expr_stmt|;
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|col1uri
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
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
name|broker
operator|.
name|removeXMLResource
argument_list|(
name|txn
argument_list|,
name|doc1
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"DONE."
argument_list|)
expr_stmt|;
name|list
operator|=
name|md
operator|.
name|matchDocuments
argument_list|(
name|KEY2
argument_list|,
name|VALUE1
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|0
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|pool
operator|.
name|release
argument_list|(
name|broker
argument_list|)
expr_stmt|;
block|}
block|}
specifier|public
name|Properties
name|contentsOutputProps
init|=
operator|new
name|Properties
argument_list|()
decl_stmt|;
block|{
name|contentsOutputProps
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
name|contentsOutputProps
operator|.
name|setProperty
argument_list|(
name|EXistOutputKeys
operator|.
name|OUTPUT_DOCTYPE
argument_list|,
literal|"yes"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|serializer
parameter_list|(
name|DBBroker
name|broker
parameter_list|,
name|DocumentImpl
name|document
parameter_list|)
throws|throws
name|SAXException
block|{
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
name|setUser
argument_list|(
name|broker
operator|.
name|getSubject
argument_list|()
argument_list|)
expr_stmt|;
name|serializer
operator|.
name|setProperties
argument_list|(
name|contentsOutputProps
argument_list|)
expr_stmt|;
return|return
name|serializer
operator|.
name|serialize
argument_list|(
name|document
argument_list|)
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
literal|"org.exist.storage.md.Plugin"
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
name|root
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|doc1uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML1
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
name|root
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML1
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
name|doc1
operator|=
name|info
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|info
operator|=
name|root
operator|.
name|validateXMLResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|doc2uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|info
argument_list|)
expr_stmt|;
name|root
operator|.
name|store
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|info
argument_list|,
name|XML2
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|doc2
operator|=
name|info
operator|.
name|getDocument
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"store "
operator|+
name|doc3uri
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
name|doc3uri
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
name|col
operator|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|col2uri
argument_list|)
expr_stmt|;
if|if
condition|(
name|col
operator|!=
literal|null
condition|)
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

