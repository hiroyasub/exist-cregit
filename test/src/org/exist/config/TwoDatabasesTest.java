begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2010-2011 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|config
package|;
end_package

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|commons
operator|.
name|io
operator|.
name|output
operator|.
name|ByteArrayOutputStream
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
name|BinaryDocument
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
name|DefaultDocumentSet
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
name|MutableDocumentSet
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
name|security
operator|.
name|Subject
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
name|FileUtils
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
name|*
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

begin_comment
comment|/**  * @author alex  */
end_comment

begin_class
specifier|public
class|class
name|TwoDatabasesTest
block|{
specifier|private
specifier|static
name|Path
name|config1File
decl_stmt|;
specifier|private
specifier|static
name|Path
name|dataDir1
decl_stmt|;
specifier|private
specifier|static
name|Path
name|config2File
decl_stmt|;
specifier|private
specifier|static
name|Path
name|dataDir2
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|prepare
parameter_list|()
block|{
specifier|final
name|String
name|log4j
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"log4j.configurationFile"
argument_list|)
decl_stmt|;
if|if
condition|(
name|log4j
operator|==
literal|null
condition|)
block|{
name|Path
name|lf
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"log42j.xml"
argument_list|)
decl_stmt|;
if|if
condition|(
name|Files
operator|.
name|isReadable
argument_list|(
name|lf
argument_list|)
condition|)
block|{
name|System
operator|.
name|setProperty
argument_list|(
literal|"log4j.configurationFile"
argument_list|,
name|lf
operator|.
name|toUri
argument_list|()
operator|.
name|toASCIIString
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|final
name|String
name|packagePath
init|=
name|TwoDatabasesTest
operator|.
name|class
operator|.
name|getPackage
argument_list|()
operator|.
name|getName
argument_list|()
operator|.
name|replace
argument_list|(
literal|'.'
argument_list|,
literal|'/'
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|existHome
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
operator|.
name|orElse
argument_list|(
name|Paths
operator|.
name|get
argument_list|(
literal|"."
argument_list|)
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|testConfigPkg
init|=
name|existHome
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"src"
argument_list|)
operator|.
name|resolve
argument_list|(
name|packagePath
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|tmpTest
init|=
name|existHome
operator|.
name|resolve
argument_list|(
literal|"test"
argument_list|)
operator|.
name|resolve
argument_list|(
literal|"temp"
argument_list|)
operator|.
name|resolve
argument_list|(
name|packagePath
argument_list|)
decl_stmt|;
name|config1File
operator|=
name|testConfigPkg
operator|.
name|resolve
argument_list|(
literal|"conf1.xml"
argument_list|)
expr_stmt|;
name|dataDir1
operator|=
name|tmpTest
operator|.
name|resolve
argument_list|(
literal|"data1"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|mkdirsQuietly
argument_list|(
name|dataDir1
argument_list|)
expr_stmt|;
name|config2File
operator|=
name|testConfigPkg
operator|.
name|resolve
argument_list|(
literal|"conf2.xml"
argument_list|)
expr_stmt|;
name|dataDir2
operator|=
name|tmpTest
operator|.
name|resolve
argument_list|(
literal|"data2"
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|mkdirsQuietly
argument_list|(
name|dataDir2
argument_list|)
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanup
parameter_list|()
block|{
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dataDir2
argument_list|)
expr_stmt|;
name|FileUtils
operator|.
name|deleteQuietly
argument_list|(
name|dataDir1
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Rule
specifier|public
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer1
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|"db1"
argument_list|,
name|config1File
argument_list|)
decl_stmt|;
annotation|@
name|Rule
specifier|public
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer2
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|"db2"
argument_list|,
name|config2File
argument_list|)
decl_stmt|;
specifier|private
name|Subject
name|user1
decl_stmt|;
specifier|private
name|Subject
name|user2
decl_stmt|;
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
name|BrokerPool
name|pool1
init|=
name|existEmbeddedServer1
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|user1
operator|=
name|pool1
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker1
init|=
name|pool1
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user1
argument_list|)
argument_list|)
init|)
block|{
name|Collection
name|top1
init|=
literal|null
decl_stmt|;
try|try
block|{
name|top1
operator|=
name|broker1
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|top1
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|top1
operator|!=
literal|null
condition|)
block|{
name|top1
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|BrokerPool
name|pool2
init|=
name|existEmbeddedServer2
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
name|user2
operator|=
name|pool2
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|getSystemSubject
argument_list|()
expr_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker2
init|=
name|pool2
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user2
argument_list|)
argument_list|)
init|)
block|{
name|Collection
name|top2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|top2
operator|=
name|broker2
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///"
argument_list|)
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|top2
operator|!=
literal|null
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|top2
operator|!=
literal|null
condition|)
block|{
name|top2
operator|.
name|getLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|putGet
parameter_list|()
throws|throws
name|LockException
throws|,
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|put
argument_list|()
expr_stmt|;
name|get
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|put
parameter_list|()
throws|throws
name|EXistException
throws|,
name|LockException
throws|,
name|TriggerException
throws|,
name|PermissionDeniedException
throws|,
name|IOException
block|{
specifier|final
name|BrokerPool
name|pool1
init|=
name|existEmbeddedServer1
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker1
init|=
name|pool1
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user1
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction1
init|=
name|pool1
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|top1
init|=
literal|null
decl_stmt|;
try|try
block|{
name|top1
operator|=
name|storeBin
argument_list|(
name|broker1
argument_list|,
name|transaction1
argument_list|,
literal|"1"
argument_list|)
expr_stmt|;
name|pool1
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|commit
argument_list|(
name|transaction1
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|top1
operator|!=
literal|null
condition|)
block|{
name|top1
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|final
name|BrokerPool
name|pool2
init|=
name|existEmbeddedServer2
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker2
init|=
name|pool2
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user1
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction2
init|=
name|pool2
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|Collection
name|top2
init|=
literal|null
decl_stmt|;
try|try
block|{
name|top2
operator|=
name|storeBin
argument_list|(
name|broker2
argument_list|,
name|transaction2
argument_list|,
literal|"2"
argument_list|)
expr_stmt|;
name|pool2
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|commit
argument_list|(
name|transaction2
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
if|if
condition|(
name|top2
operator|!=
literal|null
condition|)
block|{
name|top2
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|get
parameter_list|()
throws|throws
name|EXistException
throws|,
name|IOException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
block|{
specifier|final
name|BrokerPool
name|pool1
init|=
name|existEmbeddedServer1
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker1
init|=
name|pool1
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user1
argument_list|)
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
name|getBin
argument_list|(
name|broker1
argument_list|,
literal|"1"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|final
name|BrokerPool
name|pool2
init|=
name|existEmbeddedServer2
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
try|try
init|(
specifier|final
name|DBBroker
name|broker2
init|=
name|pool2
operator|.
name|get
argument_list|(
name|Optional
operator|.
name|of
argument_list|(
name|user2
argument_list|)
argument_list|)
init|)
block|{
name|assertTrue
argument_list|(
name|getBin
argument_list|(
name|broker2
argument_list|,
literal|"2"
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|String
name|bin
init|=
literal|"ABCDEFG"
decl_stmt|;
specifier|private
name|Collection
name|storeBin
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|Txn
name|txn
parameter_list|,
name|String
name|suffix
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|TriggerException
throws|,
name|EXistException
throws|,
name|IOException
block|{
name|String
name|data
init|=
name|bin
operator|+
name|suffix
decl_stmt|;
name|Collection
name|top
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///"
argument_list|)
argument_list|)
decl_stmt|;
name|top
operator|.
name|addBinaryResource
argument_list|(
name|txn
argument_list|,
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///bin"
argument_list|)
argument_list|,
name|data
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
return|return
name|top
return|;
block|}
specifier|private
name|boolean
name|getBin
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|String
name|suffix
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|LockException
block|{
name|BinaryDocument
name|binDoc
init|=
literal|null
decl_stmt|;
try|try
block|{
name|Collection
name|top
init|=
name|broker
operator|.
name|getCollection
argument_list|(
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///"
argument_list|)
argument_list|)
decl_stmt|;
name|int
name|count
init|=
name|top
operator|.
name|getDocumentCount
argument_list|(
name|broker
argument_list|)
decl_stmt|;
name|MutableDocumentSet
name|docs
init|=
operator|new
name|DefaultDocumentSet
argument_list|()
decl_stmt|;
name|top
operator|.
name|getDocuments
argument_list|(
name|broker
argument_list|,
name|docs
argument_list|)
expr_stmt|;
name|XmldbURI
index|[]
name|uris
init|=
name|docs
operator|.
name|getNames
argument_list|()
decl_stmt|;
comment|//binDoc = (BinaryDocument)broker.getXMLResource(XmldbURI.create("xmldb:exist:///bin"),LockMode.READ_LOCK);
name|binDoc
operator|=
operator|(
name|BinaryDocument
operator|)
name|top
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"xmldb:exist:///bin"
argument_list|)
argument_list|)
expr_stmt|;
name|top
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
name|assertTrue
argument_list|(
name|binDoc
operator|!=
literal|null
argument_list|)
expr_stmt|;
name|ByteArrayOutputStream
name|os
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|broker
operator|.
name|readBinaryResource
argument_list|(
name|binDoc
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|String
name|comp
init|=
name|os
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|?
operator|new
name|String
argument_list|(
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
else|:
literal|""
decl_stmt|;
return|return
name|comp
operator|.
name|equals
argument_list|(
name|bin
operator|+
name|suffix
argument_list|)
return|;
block|}
finally|finally
block|{
if|if
condition|(
name|binDoc
operator|!=
literal|null
condition|)
block|{
name|binDoc
operator|.
name|getUpdateLock
argument_list|()
operator|.
name|release
argument_list|(
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

