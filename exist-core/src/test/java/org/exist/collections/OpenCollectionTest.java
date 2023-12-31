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
name|collections
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
name|net
operator|.
name|URISyntaxException
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
name|junit
operator|.
name|framework
operator|.
name|TestCase
operator|.
name|assertNotNull
import|;
end_import

begin_class
specifier|public
class|class
name|OpenCollectionTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
name|ExistEmbeddedServer
name|existEmbeddedServer
init|=
operator|new
name|ExistEmbeddedServer
argument_list|(
literal|true
argument_list|,
literal|false
argument_list|)
decl_stmt|;
specifier|private
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
literal|"testCollection"
argument_list|)
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|init
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
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
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
try|try
init|(
specifier|final
name|Collection
name|col
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
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
block|}
block|}
block|}
comment|/**      * Test opening a collection using a full XmldbURI including scheme.      */
annotation|@
name|Test
specifier|public
name|void
name|loadFullXmldbURI
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|EXistException
throws|,
name|URISyntaxException
throws|,
name|DatabaseConfigurationException
block|{
name|loadCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
literal|"xmldb:exist:///db/testCollection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|loadRelativeXmldbURI
parameter_list|()
throws|throws
name|PermissionDeniedException
throws|,
name|IOException
throws|,
name|EXistException
throws|,
name|URISyntaxException
throws|,
name|DatabaseConfigurationException
block|{
name|loadCollection
argument_list|(
name|XmldbURI
operator|.
name|xmldbUriFor
argument_list|(
literal|"testCollection"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|loadCollection
parameter_list|(
name|XmldbURI
name|uri
parameter_list|)
throws|throws
name|DatabaseConfigurationException
throws|,
name|IOException
throws|,
name|EXistException
throws|,
name|PermissionDeniedException
block|{
comment|// Restart database, otherwise collection would be read from cache
name|existEmbeddedServer
operator|.
name|restart
argument_list|()
expr_stmt|;
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
try|try
init|(
specifier|final
name|Collection
name|col
init|=
name|broker
operator|.
name|openCollection
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
name|assertNotNull
argument_list|(
name|col
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

