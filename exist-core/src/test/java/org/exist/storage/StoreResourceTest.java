begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2001-2019 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public  * License along with this library; if not, write to the Free Software  * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  */
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
name|*
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
name|security
operator|.
name|internal
operator|.
name|aider
operator|.
name|GroupAider
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
name|internal
operator|.
name|aider
operator|.
name|UserAider
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
name|io
operator|.
name|FastByteArrayInputStream
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
name|io
operator|.
name|FastByteArrayOutputStream
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
name|hamcrest
operator|.
name|Matcher
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
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|DiffBuilder
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|builder
operator|.
name|Input
import|;
end_import

begin_import
import|import
name|org
operator|.
name|xmlunit
operator|.
name|diff
operator|.
name|Diff
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
name|InputStream
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
name|exist
operator|.
name|storage
operator|.
name|DBBroker
operator|.
name|PreserveType
operator|.
name|NO_PRESERVE
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|TestConstants
operator|.
name|TEST_COLLECTION_URI
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|hamcrest
operator|.
name|Matchers
operator|.
name|not
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
name|*
import|;
end_import

begin_class
specifier|public
class|class
name|StoreResourceTest
block|{
specifier|private
specifier|static
specifier|final
name|String
name|USER1_NAME
init|=
literal|"user1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER1_PWD
init|=
name|USER1_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER2_NAME
init|=
literal|"user2"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|USER2_PWD
init|=
name|USER2_NAME
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|GROUP1_NAME
init|=
literal|"group1"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|USER1_DOC1
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"u1d1.xml"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|XmldbURI
name|USER1_BIN_DOC1
init|=
name|XmldbURI
operator|.
name|create
argument_list|(
literal|"u1d1.bin"
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|USER1_DOC1_MODE
init|=
literal|0664
decl_stmt|;
comment|// rw-rw--r--
specifier|private
specifier|static
specifier|final
name|int
name|USER1_BIN_DOC1_MODE
init|=
literal|0664
decl_stmt|;
comment|// rw-rw--r--
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existWebServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
comment|/**      * As group member replace {@link #USER1_DOC1} from {@link TestConstants#TEST_COLLECTION_URI}      */
annotation|@
name|Test
specifier|public
name|void
name|replaceXmlAsOwner
parameter_list|()
throws|throws
name|AuthenticationException
throws|,
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|Subject
name|user2
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|USER2_NAME
argument_list|,
name|USER2_PWD
argument_list|)
decl_stmt|;
specifier|final
name|long
name|originalDoc1LastModified
init|=
name|getLastModified
argument_list|(
name|USER1_DOC1
argument_list|)
decl_stmt|;
name|replaceXmlDoc
argument_list|(
name|user2
argument_list|,
name|NO_PRESERVE
argument_list|,
name|USER1_DOC1
argument_list|,
literal|"<something>else</something>"
argument_list|)
expr_stmt|;
name|checkAttributes
argument_list|(
name|USER1_DOC1
argument_list|,
name|USER1_NAME
argument_list|,
name|GROUP1_NAME
argument_list|,
name|USER1_DOC1_MODE
argument_list|,
name|equalTo
argument_list|(
name|getCreated
argument_list|(
name|USER1_DOC1
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|originalDoc1LastModified
argument_list|)
argument_list|)
expr_stmt|;
block|}
comment|/**      * As group member replace {@link #USER1_BIN_DOC1} from {@link TestConstants#TEST_COLLECTION_URI}      */
annotation|@
name|Test
specifier|public
name|void
name|replaceBinaryAsGroupMember
parameter_list|()
throws|throws
name|AuthenticationException
throws|,
name|LockException
throws|,
name|PermissionDeniedException
throws|,
name|EXistException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|Subject
name|user2
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|USER2_NAME
argument_list|,
name|USER2_PWD
argument_list|)
decl_stmt|;
specifier|final
name|long
name|originalDoc1LastModified
init|=
name|getLastModified
argument_list|(
name|USER1_BIN_DOC1
argument_list|)
decl_stmt|;
name|replaceBinDoc
argument_list|(
name|user2
argument_list|,
name|NO_PRESERVE
argument_list|,
name|USER1_BIN_DOC1
argument_list|,
literal|"something else"
argument_list|)
expr_stmt|;
name|checkAttributes
argument_list|(
name|USER1_BIN_DOC1
argument_list|,
name|USER1_NAME
argument_list|,
name|GROUP1_NAME
argument_list|,
name|USER1_BIN_DOC1_MODE
argument_list|,
name|equalTo
argument_list|(
name|getCreated
argument_list|(
name|USER1_BIN_DOC1
argument_list|)
argument_list|)
argument_list|,
name|not
argument_list|(
name|originalDoc1LastModified
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|replaceXmlDoc
parameter_list|(
specifier|final
name|Subject
name|execAsUser
parameter_list|,
specifier|final
name|DBBroker
operator|.
name|PreserveType
name|preserve
parameter_list|,
specifier|final
name|XmldbURI
name|docName
parameter_list|,
specifier|final
name|String
name|content
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|SAXException
block|{
specifier|final
name|XmldbURI
name|uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|docName
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
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
name|execAsUser
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|;
specifier|final
name|Collection
name|col
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
operator|.
name|removeLastSegment
argument_list|()
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
name|IndexInfo
name|indexInfo
init|=
name|col
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|content
argument_list|)
decl_stmt|;
name|col
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|indexInfo
argument_list|,
name|content
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check the replaced document is correct
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
name|execAsUser
argument_list|)
argument_list|)
init|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
specifier|final
name|String
name|docXml
init|=
name|broker
operator|.
name|getSerializer
argument_list|()
operator|.
name|serialize
argument_list|(
name|lockedDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
decl_stmt|;
specifier|final
name|Diff
name|diff
init|=
name|DiffBuilder
operator|.
name|compare
argument_list|(
name|Input
operator|.
name|fromString
argument_list|(
name|content
argument_list|)
argument_list|)
operator|.
name|withTest
argument_list|(
name|Input
operator|.
name|fromString
argument_list|(
name|docXml
argument_list|)
argument_list|)
operator|.
name|build
argument_list|()
decl_stmt|;
name|assertFalse
argument_list|(
name|diff
operator|.
name|toString
argument_list|()
argument_list|,
name|diff
operator|.
name|hasDifferences
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|replaceBinDoc
parameter_list|(
specifier|final
name|Subject
name|execAsUser
parameter_list|,
specifier|final
name|DBBroker
operator|.
name|PreserveType
name|preserve
parameter_list|,
specifier|final
name|XmldbURI
name|docName
parameter_list|,
specifier|final
name|String
name|content
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
specifier|final
name|XmldbURI
name|uri
init|=
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|docName
argument_list|)
decl_stmt|;
specifier|final
name|byte
index|[]
name|data
init|=
name|content
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
decl_stmt|;
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
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
name|execAsUser
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|;
specifier|final
name|Collection
name|col
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|uri
operator|.
name|removeLastSegment
argument_list|()
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
try|try
init|(
specifier|final
name|FastByteArrayInputStream
name|is
init|=
operator|new
name|FastByteArrayInputStream
argument_list|(
name|data
argument_list|)
init|)
block|{
name|col
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|uri
operator|.
name|lastSegment
argument_list|()
argument_list|,
name|is
argument_list|,
literal|"application/octet-stream"
argument_list|,
name|data
operator|.
name|length
argument_list|)
expr_stmt|;
block|}
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
comment|// check the replaced document is correct
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
name|execAsUser
argument_list|)
argument_list|)
init|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|uri
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|;
specifier|final
name|InputStream
name|is
init|=
name|broker
operator|.
name|getBinaryResource
argument_list|(
operator|(
name|BinaryDocument
operator|)
name|lockedDoc
operator|.
name|getDocument
argument_list|()
argument_list|)
init|;
specifier|final
name|FastByteArrayOutputStream
name|os
init|=
operator|new
name|FastByteArrayOutputStream
argument_list|()
init|)
block|{
name|os
operator|.
name|write
argument_list|(
name|is
argument_list|)
expr_stmt|;
name|assertArrayEquals
argument_list|(
name|data
argument_list|,
name|os
operator|.
name|toByteArray
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|long
name|getCreated
parameter_list|(
specifier|final
name|XmldbURI
name|docName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
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
init|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|docName
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
return|return
name|lockedDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|getCreated
argument_list|()
return|;
block|}
block|}
specifier|private
name|long
name|getLastModified
parameter_list|(
specifier|final
name|XmldbURI
name|docName
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
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
init|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|docName
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
return|return
name|lockedDoc
operator|.
name|getDocument
argument_list|()
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
return|;
block|}
block|}
specifier|private
name|void
name|checkAttributes
parameter_list|(
specifier|final
name|XmldbURI
name|docName
parameter_list|,
specifier|final
name|String
name|expectedOwner
parameter_list|,
specifier|final
name|String
name|expectedGroup
parameter_list|,
specifier|final
name|int
name|expectedMode
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|Long
argument_list|>
name|expectedCreated
parameter_list|,
specifier|final
name|Matcher
argument_list|<
name|Long
argument_list|>
name|expectedLastModified
parameter_list|)
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
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
init|;
specifier|final
name|LockedDocument
name|lockedDoc
init|=
name|broker
operator|.
name|getXMLResource
argument_list|(
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|docName
argument_list|)
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|READ_LOCK
argument_list|)
init|)
block|{
specifier|final
name|DocumentImpl
name|doc
init|=
name|lockedDoc
operator|.
name|getDocument
argument_list|()
decl_stmt|;
specifier|final
name|Permission
name|permission
init|=
name|doc
operator|.
name|getPermissions
argument_list|()
decl_stmt|;
name|assertEquals
argument_list|(
literal|"Owner value was not expected"
argument_list|,
name|expectedOwner
argument_list|,
name|permission
operator|.
name|getOwner
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Group value was not expected"
argument_list|,
name|expectedGroup
argument_list|,
name|permission
operator|.
name|getGroup
argument_list|()
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"Mode value was not expected"
argument_list|,
name|expectedMode
argument_list|,
name|permission
operator|.
name|getMode
argument_list|()
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"Created value is not correct"
argument_list|,
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getCreated
argument_list|()
argument_list|,
name|expectedCreated
argument_list|)
expr_stmt|;
name|assertThat
argument_list|(
literal|"LastModified value is not correct"
argument_list|,
name|doc
operator|.
name|getMetadata
argument_list|()
operator|.
name|getLastModified
argument_list|()
argument_list|,
name|expectedLastModified
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|prepareDb
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
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|pool
operator|.
name|getSecurityManager
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
name|sm
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
name|pool
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
name|collection
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
name|chmod
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|collection
operator|.
name|getURI
argument_list|()
argument_list|,
literal|511
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|createGroup
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER1_NAME
argument_list|,
name|USER1_PWD
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
name|createUser
argument_list|(
name|broker
argument_list|,
name|sm
argument_list|,
name|USER2_NAME
argument_list|,
name|USER2_PWD
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|Before
specifier|public
name|void
name|setup
parameter_list|()
throws|throws
name|EXistException
throws|,
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|AuthenticationException
block|{
specifier|final
name|BrokerPool
name|pool
init|=
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
comment|// create user1 resources
specifier|final
name|Subject
name|user1
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|USER1_NAME
argument_list|,
name|USER1_PWD
argument_list|)
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
name|user1
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|;
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|TEST_COLLECTION_URI
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
name|u1d3xml
init|=
literal|"<empty3/>"
decl_stmt|;
specifier|final
name|IndexInfo
name|u1d3ii
init|=
name|collection
operator|.
name|validateXMLResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|USER1_DOC1
argument_list|,
name|u1d3xml
argument_list|)
decl_stmt|;
name|collection
operator|.
name|store
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|u1d3ii
argument_list|,
name|u1d3xml
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_DOC1
argument_list|)
argument_list|,
name|USER1_DOC1_MODE
argument_list|)
expr_stmt|;
name|chgrp
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_DOC1
argument_list|)
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
specifier|final
name|String
name|u1d3bin
init|=
literal|"bin3"
decl_stmt|;
name|collection
operator|.
name|addBinaryResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|USER1_BIN_DOC1
argument_list|,
name|u1d3bin
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|,
literal|"text/plain"
argument_list|)
expr_stmt|;
name|chmod
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_BIN_DOC1
argument_list|)
argument_list|,
name|USER1_BIN_DOC1_MODE
argument_list|)
expr_stmt|;
name|chgrp
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_BIN_DOC1
argument_list|)
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
name|transaction
operator|.
name|commit
argument_list|()
expr_stmt|;
block|}
block|}
annotation|@
name|After
specifier|public
name|void
name|teardown
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
name|pool
init|=
name|existWebServer
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
init|;
specifier|final
name|Txn
name|transaction
init|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|removeDocument
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_DOC1
argument_list|)
argument_list|)
expr_stmt|;
name|removeDocument
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
operator|.
name|append
argument_list|(
name|USER1_BIN_DOC1
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
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|cleanupDb
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
name|existWebServer
operator|.
name|getBrokerPool
argument_list|()
decl_stmt|;
specifier|final
name|SecurityManager
name|sm
init|=
name|pool
operator|.
name|getSecurityManager
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
name|sm
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
name|pool
operator|.
name|getTransactionManager
argument_list|()
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
name|removeUser
argument_list|(
name|sm
argument_list|,
name|USER2_NAME
argument_list|)
expr_stmt|;
name|removeUser
argument_list|(
name|sm
argument_list|,
name|USER1_NAME
argument_list|)
expr_stmt|;
name|removeGroup
argument_list|(
name|sm
argument_list|,
name|GROUP1_NAME
argument_list|)
expr_stmt|;
name|removeCollection
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|TEST_COLLECTION_URI
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
name|void
name|createUser
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|username
parameter_list|,
specifier|final
name|String
name|password
parameter_list|,
specifier|final
name|String
modifier|...
name|supplementalGroups
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|Group
name|userGroup
init|=
operator|new
name|GroupAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|sm
operator|.
name|addGroup
argument_list|(
name|broker
argument_list|,
name|userGroup
argument_list|)
expr_stmt|;
specifier|final
name|Account
name|user
init|=
operator|new
name|UserAider
argument_list|(
name|username
argument_list|)
decl_stmt|;
name|user
operator|.
name|setPassword
argument_list|(
name|password
argument_list|)
expr_stmt|;
name|user
operator|.
name|setPrimaryGroup
argument_list|(
name|userGroup
argument_list|)
expr_stmt|;
name|sm
operator|.
name|addAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
name|userGroup
operator|=
name|sm
operator|.
name|getGroup
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|userGroup
operator|.
name|addManager
argument_list|(
name|sm
operator|.
name|getAccount
argument_list|(
name|username
argument_list|)
argument_list|)
expr_stmt|;
name|sm
operator|.
name|updateGroup
argument_list|(
name|userGroup
argument_list|)
expr_stmt|;
for|for
control|(
specifier|final
name|String
name|supplementalGroup
range|:
name|supplementalGroups
control|)
block|{
name|userGroup
operator|=
name|sm
operator|.
name|getGroup
argument_list|(
name|supplementalGroup
argument_list|)
expr_stmt|;
name|user
operator|.
name|addGroup
argument_list|(
name|userGroup
argument_list|)
expr_stmt|;
block|}
name|sm
operator|.
name|updateAccount
argument_list|(
name|user
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|createGroup
parameter_list|(
specifier|final
name|DBBroker
name|broker
parameter_list|,
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|Group
name|userGroup
init|=
operator|new
name|GroupAider
argument_list|(
name|group
argument_list|)
decl_stmt|;
name|sm
operator|.
name|addGroup
argument_list|(
name|broker
argument_list|,
name|userGroup
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|chmod
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
name|pathUri
parameter_list|,
specifier|final
name|int
name|mode
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|PermissionFactory
operator|.
name|chmod
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|pathUri
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|mode
argument_list|)
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|chgrp
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
name|pathUri
parameter_list|,
specifier|final
name|String
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
block|{
name|PermissionFactory
operator|.
name|chown
argument_list|(
name|broker
argument_list|,
name|transaction
argument_list|,
name|pathUri
argument_list|,
name|Optional
operator|.
name|empty
argument_list|()
argument_list|,
name|Optional
operator|.
name|of
argument_list|(
name|group
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeUser
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|username
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|sm
operator|.
name|deleteAccount
argument_list|(
name|username
argument_list|)
expr_stmt|;
name|sm
operator|.
name|deleteGroup
argument_list|(
name|username
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeGroup
parameter_list|(
specifier|final
name|SecurityManager
name|sm
parameter_list|,
specifier|final
name|String
name|group
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|EXistException
block|{
name|sm
operator|.
name|deleteGroup
argument_list|(
name|group
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|removeDocument
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
name|documentUri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|LockException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|documentUri
operator|.
name|removeLastSegment
argument_list|()
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
name|DocumentImpl
name|doc
init|=
name|collection
operator|.
name|getDocument
argument_list|(
name|broker
argument_list|,
name|documentUri
operator|.
name|lastSegment
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|collection
operator|.
name|removeResource
argument_list|(
name|transaction
argument_list|,
name|broker
argument_list|,
name|doc
argument_list|)
expr_stmt|;
block|}
name|broker
operator|.
name|saveCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|removeCollection
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
name|collectionUri
parameter_list|)
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|TriggerException
block|{
try|try
init|(
specifier|final
name|Collection
name|collection
init|=
name|broker
operator|.
name|openCollection
argument_list|(
name|collectionUri
argument_list|,
name|Lock
operator|.
name|LockMode
operator|.
name|WRITE_LOCK
argument_list|)
init|)
block|{
if|if
condition|(
name|collection
operator|!=
literal|null
condition|)
block|{
name|broker
operator|.
name|removeCollection
argument_list|(
name|transaction
argument_list|,
name|collection
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

