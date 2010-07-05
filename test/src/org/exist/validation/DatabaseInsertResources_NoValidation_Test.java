begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id: DatabaseInsertResources_NoValidation_Test.java 5986 2007-06-03 15:39:39Z dizzzz $  */
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
name|apache
operator|.
name|log4j
operator|.
name|Logger
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
name|io
operator|.
name|ExistIOException
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

begin_import
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|fail
import|;
end_import

begin_comment
comment|/**  *  Insert documents for validation tests.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseInsertResources_NoValidation_Test
block|{
specifier|private
specifier|final
specifier|static
name|Logger
name|logger
init|=
name|Logger
operator|.
name|getLogger
argument_list|(
name|DatabaseInsertResources_NoValidation_Test
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
name|String
name|eXistHome
init|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
decl_stmt|;
specifier|private
specifier|static
name|BrokerPool
name|pool
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Configuration
name|config
init|=
literal|null
decl_stmt|;
comment|// ---------------------------------------------------
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
block|{
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
name|pool
operator|=
name|BrokerPool
operator|.
name|getInstance
argument_list|()
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
name|getGuestAccount
argument_list|()
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
name|TestTools
operator|.
name|VALIDATION_DTD
argument_list|)
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
name|col
operator|=
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
name|TestTools
operator|.
name|VALIDATION_XSD
argument_list|)
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
name|col
operator|=
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
name|TestTools
operator|.
name|VALIDATION_TMP
argument_list|)
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
name|transact
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
name|broker
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
comment|/**      * Insert all documents into database, switch of validation.      */
annotation|@
name|Test
specifier|public
name|void
name|insertValidationResources
parameter_list|()
block|{
try|try
block|{
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"no"
argument_list|)
expr_stmt|;
name|String
name|addressbook
init|=
name|eXistHome
operator|+
literal|"/samples/validation/addressbook"
decl_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook.xsd"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD
operator|+
literal|"/addressbook.xsd"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/catalog.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_XSD
operator|+
literal|"/catalog.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook_valid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/addressbook_valid.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|addressbook
operator|+
literal|"/addressbook_invalid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/addressbook_invalid.xml"
argument_list|)
expr_stmt|;
comment|// ----------------------
name|String
name|hamlet
init|=
name|eXistHome
operator|+
literal|"/samples/validation/dtd"
decl_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/hamlet.dtd"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_DTD
operator|+
literal|"/hamlet.dtd"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/catalog.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_DTD
operator|+
literal|"/catalog.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/hamlet_valid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_valid.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/hamlet_invalid.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_invalid.xml"
argument_list|)
expr_stmt|;
comment|// ----------------------
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/hamlet_nodoctype.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_nodoctype.xml"
argument_list|)
expr_stmt|;
name|TestTools
operator|.
name|insertDocumentToURL
argument_list|(
name|hamlet
operator|+
literal|"/hamlet_wrongdoctype.xml"
argument_list|,
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_wrongdoctype.xml"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|ExistIOException
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getCause
argument_list|()
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|ex
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
name|logger
operator|.
name|error
argument_list|(
name|ex
argument_list|)
expr_stmt|;
name|fail
argument_list|(
name|ex
operator|.
name|getMessage
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|AfterClass
specifier|public
specifier|static
name|void
name|shutdown
parameter_list|()
block|{
name|BrokerPool
operator|.
name|stopAll
argument_list|(
literal|true
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

