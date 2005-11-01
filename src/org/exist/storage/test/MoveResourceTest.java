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
operator|.
name|test
package|;
end_package

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
name|SecurityManager
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
name|xmldb
operator|.
name|CollectionManagementServiceImpl
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
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|DatabaseManager
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Database
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|base
operator|.
name|Resource
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmldb
operator|.
name|api
operator|.
name|modules
operator|.
name|CollectionManagementService
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|TestCase
import|;
end_import

begin_import
import|import
name|junit
operator|.
name|textui
operator|.
name|TestRunner
import|;
end_import

begin_class
specifier|public
class|class
name|MoveResourceTest
extends|extends
name|TestCase
block|{
specifier|public
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|TestRunner
operator|.
name|run
argument_list|(
name|MoveResourceTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testStore
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|true
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
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
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
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
name|transaction
argument_list|,
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
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
name|Collection
name|test
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|transaction
argument_list|,
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/test2"
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
name|File
name|f
init|=
operator|new
name|File
argument_list|(
literal|"samples/shakespeare/r_and_j.xml"
argument_list|)
decl_stmt|;
name|IndexInfo
name|info
init|=
name|test
operator|.
name|validate
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
literal|"test.xml"
argument_list|,
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
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
operator|new
name|InputSource
argument_list|(
name|f
operator|.
name|toURI
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
argument_list|,
literal|false
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Moving document test.xml to new_test.xml ..."
argument_list|)
expr_stmt|;
name|broker
operator|.
name|moveResource
argument_list|(
name|transaction
argument_list|,
name|info
operator|.
name|getDocument
argument_list|()
argument_list|,
name|root
argument_list|,
literal|"new_test.xml"
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
name|transact
operator|.
name|commit
argument_list|(
name|transaction
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
name|void
name|testRead
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
operator|.
name|FORCE_CORRUPTION
operator|=
literal|false
expr_stmt|;
name|BrokerPool
name|pool
init|=
name|startDB
argument_list|()
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"testRead() ...\n"
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
name|SecurityManager
operator|.
name|SYSTEM_USER
argument_list|)
expr_stmt|;
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
decl_stmt|;
name|String
name|data
decl_stmt|;
name|doc
operator|=
name|broker
operator|.
name|openDocument
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test/new_test.xml"
argument_list|,
name|Lock
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Document should not be null"
argument_list|,
name|doc
argument_list|)
expr_stmt|;
name|data
operator|=
name|serializer
operator|.
name|serialize
argument_list|(
name|doc
argument_list|)
expr_stmt|;
comment|//	        System.out.println(data);
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
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
decl_stmt|;
name|Txn
name|transaction
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
decl_stmt|;
name|Collection
name|root
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
decl_stmt|;
name|transaction
operator|.
name|registerLock
argument_list|(
name|root
operator|.
name|getLock
argument_list|()
argument_list|,
name|Lock
operator|.
name|WRITE_LOCK
argument_list|)
expr_stmt|;
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
comment|//	public void testStoreAborted() throws Exception {
comment|//		BrokerPool.FORCE_CORRUPTION = true;
comment|//		BrokerPool pool = startDB();
comment|//
comment|//		DBBroker broker = null;
comment|//		try {
comment|//			broker = pool.get(SecurityManager.SYSTEM_USER);
comment|//
comment|//			TransactionManager transact = pool.getTransactionManager();
comment|//			Txn transaction = transact.beginTransaction();
comment|//
comment|//			System.out.println("Transaction started ...");
comment|//
comment|//			Collection root = broker.getOrCreateCollection(transaction,
comment|//					DBBroker.ROOT_COLLECTION +  "/test");
comment|//			broker.saveCollection(transaction, root);
comment|//
comment|//			Collection test = broker.getOrCreateCollection(transaction,
comment|//					DBBroker.ROOT_COLLECTION + "/test/test2");
comment|//			broker.saveCollection(transaction, test);
comment|//
comment|//			File f = new File("samples/shakespeare/r_and_j.xml");
comment|//			IndexInfo info = test.validate(transaction, broker, "test2.xml",
comment|//					new InputSource(f.toURI().toASCIIString()));
comment|//			test.store(transaction, broker, info, new InputSource(f.toURI()
comment|//					.toASCIIString()), false);
comment|//
comment|//			transact.commit(transaction);
comment|//
comment|//			transaction = transact.beginTransaction();
comment|//
comment|//			broker.moveResource(transaction, info.getDocument(), root,
comment|//					"new_test2.xml");
comment|//			broker.saveCollection(transaction, root);
comment|//
comment|//			pool.getTransactionManager().getLogManager().flushToLog(true);
comment|//		} finally {
comment|//			pool.release(broker);
comment|//		}
comment|//	}
comment|//	public void testReadAborted() throws Exception {
comment|//	    BrokerPool.FORCE_CORRUPTION = false;
comment|//	    BrokerPool pool = startDB();
comment|//
comment|//	    System.out.println("testRead() ...\n");
comment|//
comment|//	    DBBroker broker = null;
comment|//	    try {
comment|//	        broker = pool.get(SecurityManager.SYSTEM_USER);
comment|//	        Serializer serializer = broker.getSerializer();
comment|//	        serializer.reset();
comment|//
comment|//	        DocumentImpl doc;
comment|//	        String data;
comment|//
comment|//	        doc = broker.openDocument(DBBroker.ROOT_COLLECTION + "/test/test2/test2.xml", Lock.READ_LOCK);
comment|//	        assertNotNull("Document should not be null", doc);
comment|//	        data = serializer.serialize(doc);
comment|//	        System.out.println(data);
comment|//	        doc.getUpdateLock().release(Lock.READ_LOCK);
comment|//
comment|//	        doc = broker.openDocument(DBBroker.ROOT_COLLECTION +  "/test/new_test2.xml", Lock.READ_LOCK);
comment|//	        assertNull("Document should not exist", doc);
comment|//	    } finally {
comment|//	        pool.release(broker);
comment|//	    }
comment|//	}
comment|//
comment|//	public void testXMLDBStore() throws Exception {
comment|//		BrokerPool.FORCE_CORRUPTION = false;
comment|//	    BrokerPool pool = startDB();
comment|//
comment|//	    org.xmldb.api.base.Collection root = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION + , "admin", "");
comment|//	    CollectionManagementServiceImpl mgr = (CollectionManagementServiceImpl)
comment|//	    	root.getService("CollectionManagementService", "1.0");
comment|//	    org.xmldb.api.base.Collection test = root.getChildCollection("test");
comment|//	    if (test == null)
comment|//	    	test = mgr.createCollection("test");
comment|//	    org.xmldb.api.base.Collection test2 = test.getChildCollection("test2");
comment|//	    if (test2 == null)
comment|//	    	test2 = mgr.createCollection("test2");
comment|//
comment|//	    File f = new File("samples/shakespeare/r_and_j.xml");
comment|//	    Resource res = test2.createResource("test3.xml", "XMLResource");
comment|//	    res.setContent(f);
comment|//	    test2.storeResource(res);
comment|//
comment|//	    mgr.moveResource(DBBroker.ROOT_COLLECTION +  "/test/test2/test3.xml", DBBroker.ROOT_COLLECTION + "/test", "new_test3.xml");
comment|//	}
comment|//
comment|//	public void testXMLDBRead() throws Exception {
comment|//		BrokerPool.FORCE_CORRUPTION = false;
comment|//	    BrokerPool pool = startDB();
comment|//
comment|//	    org.xmldb.api.base.Collection test = DatabaseManager.getCollection("xmldb:exist://" + DBBroker.ROOT_COLLECTION +  "/test", "admin", "");
comment|//	    Resource res = test.getResource("new_test3.xml");
comment|//	    assertNotNull("Document should not be null", res);
comment|//	    System.out.println(res.getContent());
comment|//	}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|home
decl_stmt|,
name|file
init|=
literal|"conf.xml"
decl_stmt|;
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
if|if
condition|(
name|home
operator|==
literal|null
condition|)
name|home
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|)
expr_stmt|;
try|try
block|{
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
comment|// initialize driver
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
operator|.
name|newInstance
argument_list|()
decl_stmt|;
name|database
operator|.
name|setProperty
argument_list|(
literal|"create-database"
argument_list|,
literal|"true"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|registerDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
return|return
name|BrokerPool
operator|.
name|getInstance
argument_list|()
return|;
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
name|fail
argument_list|(
name|e
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
specifier|protected
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
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

