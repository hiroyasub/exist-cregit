begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 20011 The eXist-db Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: DatabaseInsertResources_NoValidation_Test.java 5986 2007-06-03 15:39:39Z dizzzz $  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
package|;
end_package

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
name|java
operator|.
name|io
operator|.
name|IOException
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
name|XMLReaderObjectFactory
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
name|Test
import|;
end_import

begin_comment
comment|/**  *  Insert documents for validation tests.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseInsertResources_WithValidation_Test
block|{
specifier|private
specifier|static
name|Configuration
name|config
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_COLLECTION
init|=
literal|"testValidationInsert"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ADMIN_UID
init|=
literal|"admin"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|ADMIN_PWD
init|=
literal|""
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|GUEST_UID
init|=
literal|"guest"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|VALIDATION_HOME_COLLECTION_URI
init|=
literal|"/db/"
operator|+
name|TEST_COLLECTION
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME_COLLECTION
decl_stmt|;
comment|/**      * Test for inserting hamlet.xml, while validating using default registered      * DTD set in system catalog.      *      * First the string      *<!--!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd"-->      * needs to be modified into      *<!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd">      */
annotation|@
name|Test
specifier|public
name|void
name|testValidDocumentSystemCatalog
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|hamletWithValid
init|=
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|getHamlet
argument_list|()
argument_list|)
decl_stmt|;
name|hamletWithValid
operator|=
name|hamletWithValid
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \"play.dtd\">"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamletWithValid
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"xmldb:exist://"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
operator|+
literal|"/hamlet_valid.xml"
argument_list|)
expr_stmt|;
block|}
comment|/**      * Test for inserting hamlet.xml, while validating using default registered      * DTD set in system catalog.      *      * First the string      *<!--!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd"-->      * needs to be modified into      *<!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd">      *      * Aditionally all "TITLE" elements are renamed to "INVALIDTITLE"      */
annotation|@
name|Test
specifier|public
name|void
name|invalidDocumentSystemCatalog
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|hamletWithInvalid
init|=
operator|new
name|String
argument_list|(
name|TestTools
operator|.
name|getHamlet
argument_list|()
argument_list|)
decl_stmt|;
name|hamletWithInvalid
operator|=
name|hamletWithInvalid
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \"play.dtd\">"
argument_list|)
expr_stmt|;
name|hamletWithInvalid
operator|=
name|hamletWithInvalid
operator|.
name|replaceAll
argument_list|(
literal|"TITLE"
argument_list|,
literal|"INVALIDTITLE"
argument_list|)
expr_stmt|;
try|try
block|{
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamletWithInvalid
operator|.
name|getBytes
argument_list|()
argument_list|,
literal|"xmldb:exist://"
operator|+
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
operator|+
literal|"/hamlet_invalid.xml"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|ioe
parameter_list|)
block|{
comment|//TODO consider how to get better error handling than matching on exception strings!
if|if
condition|(
operator|!
name|ioe
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
operator|.
name|matches
argument_list|(
literal|".*Element type \"INVALIDTITLE\" must be declared.*"
argument_list|)
condition|)
block|{
throw|throw
name|ioe
throw|;
block|}
block|}
block|}
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
throws|throws
name|Exception
block|{
name|config
operator|=
operator|new
name|Configuration
argument_list|()
expr_stmt|;
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"auto"
argument_list|)
expr_stmt|;
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
name|createTestCollections
argument_list|()
expr_stmt|;
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
name|removeTestCollections
argument_list|()
expr_stmt|;
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|createTestCollections
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
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
name|Subject
name|admin
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
comment|/** create nessecary collections if they dont exist */
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
argument_list|)
argument_list|)
decl_stmt|;
name|testCollection
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|testCollection
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
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
operator|+
literal|"/"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP_COLLECTION
argument_list|)
argument_list|)
decl_stmt|;
name|col
operator|.
name|getPermissions
argument_list|()
operator|.
name|setOwner
argument_list|(
name|GUEST_UID
argument_list|)
expr_stmt|;
name|broker
operator|.
name|saveCollection
argument_list|(
name|txn
argument_list|,
name|col
argument_list|)
expr_stmt|;
name|transact
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
if|if
condition|(
name|transact
operator|!=
literal|null
operator|&&
name|txn
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
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
block|}
specifier|private
specifier|static
name|void
name|removeTestCollections
parameter_list|()
throws|throws
name|Exception
block|{
name|BrokerPool
name|pool
init|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
decl_stmt|;
name|DBBroker
name|broker
init|=
literal|null
decl_stmt|;
name|TransactionManager
name|transact
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
name|Subject
name|admin
init|=
name|pool
operator|.
name|getSecurityManager
argument_list|()
operator|.
name|authenticate
argument_list|(
name|ADMIN_UID
argument_list|,
name|ADMIN_PWD
argument_list|)
decl_stmt|;
name|broker
operator|=
name|pool
operator|.
name|get
argument_list|(
name|admin
argument_list|)
expr_stmt|;
name|transact
operator|=
name|pool
operator|.
name|getTransactionManager
argument_list|()
expr_stmt|;
name|txn
operator|=
name|transact
operator|.
name|beginTransaction
argument_list|()
expr_stmt|;
name|Collection
name|testCollection
init|=
name|broker
operator|.
name|getOrCreateCollection
argument_list|(
name|txn
argument_list|,
name|XmldbURI
operator|.
name|create
argument_list|(
name|VALIDATION_HOME_COLLECTION_URI
argument_list|)
argument_list|)
decl_stmt|;
name|broker
operator|.
name|removeCollection
argument_list|(
name|txn
argument_list|,
name|testCollection
argument_list|)
expr_stmt|;
name|transact
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
if|if
condition|(
name|transact
operator|!=
literal|null
operator|&&
name|txn
operator|!=
literal|null
condition|)
block|{
name|transact
operator|.
name|abort
argument_list|(
name|txn
argument_list|)
expr_stmt|;
block|}
throw|throw
name|e
throw|;
block|}
finally|finally
block|{
if|if
condition|(
name|broker
operator|!=
literal|null
condition|)
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
block|}
block|}
end_class

end_unit

