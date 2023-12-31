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
name|java
operator|.
name|io
operator|.
name|*
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|URL
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
name|XMLReaderObjectFactory
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
name|InputStreamUtil
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
name|TestUtils
operator|.
name|*
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|util
operator|.
name|PropertiesBuilder
operator|.
name|propertiesBuilder
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|exist
operator|.
name|samples
operator|.
name|Samples
operator|.
name|SAMPLES
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
name|validDocumentSystemCatalog
parameter_list|()
throws|throws
name|IOException
block|{
name|String
name|hamletWithValid
init|=
name|getHamletXml
argument_list|()
decl_stmt|;
name|hamletWithValid
operator|=
name|hamletWithValid
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \""
operator|+
name|getPlayDtdUrl
argument_list|()
operator|+
literal|"\">"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
operator|new
name|FastByteArrayInputStream
argument_list|(
name|hamletWithValid
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
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
specifier|private
name|String
name|getHamletXml
parameter_list|()
throws|throws
name|IOException
block|{
try|try
init|(
specifier|final
name|InputStream
name|is
init|=
name|SAMPLES
operator|.
name|getHamletSample
argument_list|()
init|)
block|{
return|return
name|InputStreamUtil
operator|.
name|readString
argument_list|(
name|is
argument_list|,
name|UTF_8
argument_list|)
return|;
block|}
block|}
specifier|private
name|URL
name|getPlayDtdUrl
parameter_list|()
block|{
return|return
name|SAMPLES
operator|.
name|getSampleUrl
argument_list|(
literal|"shakespeare/play.dtd"
argument_list|)
return|;
block|}
comment|/**      * Test for inserting hamlet.xml, while validating using default registered      * DTD set in system catalog.      *      * First the string      *<!--!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd"-->      * needs to be modified into      *<!DOCTYPE PLAY PUBLIC "-//PLAY//EN" "play.dtd">      *      * Additionally all "TITLE" elements are renamed to "INVALIDTITLE"      */
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
name|getHamletXml
argument_list|()
decl_stmt|;
name|hamletWithInvalid
operator|=
name|hamletWithInvalid
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \""
operator|+
name|getPlayDtdUrl
argument_list|()
operator|+
literal|"\">"
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
operator|new
name|FastByteArrayInputStream
argument_list|(
name|hamletWithInvalid
operator|.
name|getBytes
argument_list|(
name|UTF_8
argument_list|)
argument_list|)
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
specifier|final
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
name|propertiesBuilder
argument_list|()
operator|.
name|set
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"auto"
argument_list|)
operator|.
name|build
argument_list|()
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
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
block|}
specifier|private
specifier|static
name|void
name|createTestCollections
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
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
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
name|authenticate
argument_list|(
name|ADMIN_DB_USER
argument_list|,
name|ADMIN_DB_PWD
argument_list|)
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
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
name|GUEST_DB_USER
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
name|GUEST_DB_USER
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
block|}
specifier|private
specifier|static
name|void
name|removeTestCollections
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
specifier|final
name|TransactionManager
name|transact
init|=
name|pool
operator|.
name|getTransactionManager
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
name|authenticate
argument_list|(
name|ADMIN_DB_USER
argument_list|,
name|ADMIN_DB_PWD
argument_list|)
argument_list|)
argument_list|)
init|;
specifier|final
name|Txn
name|txn
init|=
name|transact
operator|.
name|beginTransaction
argument_list|()
init|)
block|{
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
block|}
block|}
end_class

end_unit

