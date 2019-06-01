begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-2016 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|storage
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
name|Tuple3
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
name|QName
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|indexing
operator|.
name|StructuralIndex
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
name|NodeSelector
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
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertFalse
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
import|import static
name|org
operator|.
name|exist
operator|.
name|storage
operator|.
name|ElementValue
operator|.
name|ELEMENT
import|;
end_import

begin_class
specifier|public
class|class
name|MoveOverwriteCollectionTest
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
literal|true
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML1
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<test1>"
operator|+
literal|"<title>Hello1</title>"
operator|+
literal|"</test1>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML2
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<test2>"
operator|+
literal|"<title>Hello2</title>"
operator|+
literal|"</test2>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|XML3
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<test3>"
operator|+
literal|"<title>Hello3</title>"
operator|+
literal|"</test3>"
decl_stmt|;
specifier|private
specifier|final
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
literal|"test"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|SUB_TEST_COLLECTION_URI
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|TEST3_COLLECTION_URI
init|=
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test3"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|doc1Name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"doc1.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|doc2Name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"doc2.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|XmldbURI
name|doc3Name
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"doc3.xml"
argument_list|)
decl_stmt|;
comment|/**      * This test ensures that when moving an Collection over the top of an existing Collection,      * the overwritten resource is completely removed from the database;      * i.e. its nodes are no longer present in the structural index      */
annotation|@
name|Test
specifier|public
name|void
name|moveAndOverwriteCollection
parameter_list|()
throws|throws
name|Exception
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
specifier|final
name|Tuple3
argument_list|<
name|Collection
argument_list|,
name|Collection
argument_list|,
name|Collection
argument_list|>
name|collections
init|=
name|store
argument_list|(
name|broker
argument_list|)
decl_stmt|;
try|try
block|{
specifier|final
name|DefaultDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|collections
operator|.
name|_1
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|doc1Name
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|collections
operator|.
name|_2
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|doc2Name
argument_list|)
argument_list|)
expr_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|collections
operator|.
name|_3
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|doc3Name
argument_list|)
argument_list|)
expr_stmt|;
name|moveToRoot
argument_list|(
name|broker
argument_list|,
name|collections
operator|.
name|_3
argument_list|)
expr_stmt|;
specifier|final
name|Collection
name|col
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
name|docs
operator|.
name|add
argument_list|(
name|col
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|doc3Name
argument_list|)
argument_list|)
expr_stmt|;
name|checkIndex
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|collections
operator|.
name|_3
operator|.
name|close
argument_list|()
expr_stmt|;
name|collections
operator|.
name|_2
operator|.
name|close
argument_list|()
expr_stmt|;
name|collections
operator|.
name|_1
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|Tuple3
argument_list|<
name|Collection
argument_list|,
name|Collection
argument_list|,
name|Collection
argument_list|>
name|store
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
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
name|test1
init|=
name|createCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|test2
init|=
name|createCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|SUB_TEST_COLLECTION_URI
argument_list|)
decl_stmt|;
specifier|final
name|Collection
name|test3
init|=
name|createCollection
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|TEST3_COLLECTION_URI
argument_list|)
decl_stmt|;
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|test1
argument_list|,
name|doc1Name
argument_list|,
name|XML1
argument_list|)
expr_stmt|;
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|test2
argument_list|,
name|doc2Name
argument_list|,
name|XML2
argument_list|)
expr_stmt|;
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|test3
argument_list|,
name|doc3Name
argument_list|,
name|XML3
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
return|return
operator|new
name|Tuple3
argument_list|<>
argument_list|(
name|test1
argument_list|,
name|test2
argument_list|,
name|test3
argument_list|)
return|;
block|}
block|}
specifier|private
name|Collection
name|createCollection
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DBBroker
name|broker
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
name|col
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|uri
argument_list|)
decl_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
return|return
name|col
return|;
block|}
specifier|private
name|void
name|store
parameter_list|(
specifier|final
name|Txn
name|txn
parameter_list|,
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Collection
name|col
parameter_list|,
specifier|final
name|XmldbURI
name|name
parameter_list|,
specifier|final
name|String
name|data
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
name|name
argument_list|,
name|data
argument_list|)
decl_stmt|;
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
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|moveToRoot
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Collection
name|sourceCollection
parameter_list|)
throws|throws
name|Exception
block|{
try|try
init|(
specifier|final
name|Txn
name|transaction
init|=
name|broker
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|;
specifier|final
name|Collection
name|root
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION_URI
argument_list|)
init|)
block|{
name|broker
operator|.
name|moveCollection
argument_list|(
name|transaction
argument_list|,
name|sourceCollection
argument_list|,
name|root
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"test"
argument_list|)
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
name|void
name|checkIndex
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|DocumentSet
name|docs
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|StructuralIndex
name|index
init|=
name|broker
operator|.
name|getStructuralIndex
argument_list|()
decl_stmt|;
specifier|final
name|NodeSelector
name|selector
init|=
name|NodeProxy
operator|::
operator|new
decl_stmt|;
name|NodeSet
name|nodes
decl_stmt|;
name|nodes
operator|=
name|index
operator|.
name|findElementsByTagName
argument_list|(
name|ELEMENT
argument_list|,
name|docs
argument_list|,
operator|new
name|QName
argument_list|(
literal|"test2"
argument_list|)
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|index
operator|.
name|findElementsByTagName
argument_list|(
name|ELEMENT
argument_list|,
name|docs
argument_list|,
operator|new
name|QName
argument_list|(
literal|"test1"
argument_list|)
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|nodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
name|nodes
operator|=
name|index
operator|.
name|findElementsByTagName
argument_list|(
name|ELEMENT
argument_list|,
name|docs
argument_list|,
operator|new
name|QName
argument_list|(
literal|"test3"
argument_list|)
argument_list|,
name|selector
argument_list|)
expr_stmt|;
name|assertFalse
argument_list|(
name|nodes
operator|.
name|isEmpty
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit
