begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
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
name|assertNotNull
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|AbstractUpdateTest
block|{
specifier|protected
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
specifier|protected
specifier|static
name|String
name|TEST_XML
init|=
literal|"<?xml version=\"1.0\"?>"
operator|+
literal|"<products/>"
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|read
parameter_list|()
throws|throws
name|EXistException
throws|,
name|DatabaseConfigurationException
throws|,
name|PermissionDeniedException
throws|,
name|SAXException
throws|,
name|XPathException
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
specifier|final
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
init|;
init|)
block|{
specifier|final
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
name|DocumentImpl
name|doc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|doc
operator|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2/test.xml"
argument_list|)
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document '"
operator|+
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2/test.xml"
argument_list|)
operator|+
literal|"' should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
specifier|final
name|String
name|data
init|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
decl_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
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
name|seq
init|=
name|xquery
operator|.
name|execute
argument_list|(
name|broker
argument_list|,
literal|"/products/product[last()]"
argument_list|,
literal|null
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|SequenceIterator
name|i
init|=
name|seq
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
name|Item
name|next
init|=
name|i
operator|.
name|nextItem
argument_list|()
decl_stmt|;
block|}
block|}
block|}
specifier|protected
name|IndexInfo
name|init
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|TransactionManager
name|transact
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
name|IndexInfo
name|info
init|=
literal|null
decl_stmt|;
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
name|Collection
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
literal|"test2"
argument_list|)
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
name|info
operator|=
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
literal|"test.xml"
argument_list|)
argument_list|,
name|TEST_XML
argument_list|)
expr_stmt|;
comment|//TODO : unlock the collection here ?
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
argument_list|,
literal|false
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
return|return
name|info
return|;
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

