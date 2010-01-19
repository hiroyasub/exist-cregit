begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-07 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
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
name|ByteArrayInputStream
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
name|io
operator|.
name|FileInputStream
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
name|io
operator|.
name|OutputStream
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
name|net
operator|.
name|URLConnection
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
name|*
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|log4j
operator|.
name|BasicConfigurator
import|;
end_import

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
name|external
operator|.
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
name|DatabaseInstanceManager
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
name|Collection
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
name|ResourceSet
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
name|XPathQueryService
import|;
end_import

begin_comment
comment|/**  *  Set of Tests for validation:validate($a) and validation:validate($a, $b)  * regaring validatin using DTD's.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|ValidationFunctions_DTD_Test
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
name|ValidationFunctions_DTD_Test
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
specifier|private
specifier|static
name|XPathQueryService
name|service
decl_stmt|;
specifier|private
specifier|static
name|Collection
name|root
init|=
literal|null
decl_stmt|;
specifier|private
specifier|static
name|Database
name|database
init|=
literal|null
decl_stmt|;
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|startup
parameter_list|()
block|{
name|BasicConfigurator
operator|.
name|configure
argument_list|()
expr_stmt|;
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
name|SecurityManager
operator|.
name|GUEST
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
name|org
operator|.
name|exist
operator|.
name|collections
operator|.
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
annotation|@
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"setUp"
argument_list|)
expr_stmt|;
name|Class
argument_list|<
name|?
argument_list|>
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
literal|"org.exist.xmldb.DatabaseImpl"
argument_list|)
decl_stmt|;
name|database
operator|=
operator|(
name|Database
operator|)
name|cl
operator|.
name|newInstance
argument_list|()
expr_stmt|;
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
name|root
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|service
operator|=
operator|(
name|XPathQueryService
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"XQueryService"
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
try|try
block|{
name|File
name|file
init|=
operator|new
name|File
argument_list|(
name|eXistHome
argument_list|,
literal|"samples/shakespeare/hamlet.xml"
argument_list|)
decl_stmt|;
name|InputStream
name|fis
init|=
operator|new
name|FileInputStream
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|ByteArrayOutputStream
name|baos
init|=
operator|new
name|ByteArrayOutputStream
argument_list|()
decl_stmt|;
name|TestTools
operator|.
name|copyStream
argument_list|(
name|fis
argument_list|,
name|baos
argument_list|)
expr_stmt|;
name|fis
operator|.
name|close
argument_list|()
expr_stmt|;
name|String
name|sb
init|=
operator|new
name|String
argument_list|(
name|baos
operator|.
name|toByteArray
argument_list|()
argument_list|)
decl_stmt|;
name|sb
operator|=
name|sb
operator|.
name|replaceAll
argument_list|(
literal|"\\Q<!\\E.*DOCTYPE.*\\Q-->\\E"
argument_list|,
literal|"<!DOCTYPE PLAY PUBLIC \"-//PLAY//EN\" \"play.dtd\">"
argument_list|)
expr_stmt|;
name|InputStream
name|is
init|=
operator|new
name|ByteArrayInputStream
argument_list|(
name|sb
operator|.
name|getBytes
argument_list|()
argument_list|)
decl_stmt|;
comment|// -----
name|URL
name|url
init|=
operator|new
name|URL
argument_list|(
literal|"xmldb:exist://"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP
operator|+
literal|"/hamlet_valid.xml"
argument_list|)
decl_stmt|;
name|URLConnection
name|connection
init|=
name|url
operator|.
name|openConnection
argument_list|()
decl_stmt|;
name|OutputStream
name|os
init|=
name|connection
operator|.
name|getOutputStream
argument_list|()
decl_stmt|;
name|TestTools
operator|.
name|copyStream
argument_list|(
name|is
argument_list|,
name|os
argument_list|)
expr_stmt|;
name|is
operator|.
name|close
argument_list|()
expr_stmt|;
name|os
operator|.
name|close
argument_list|()
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
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION_MODE
argument_list|,
literal|"yes"
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
comment|// ===========================================================
specifier|private
name|void
name|clearGrammarCache
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Clearing grammar cache"
argument_list|)
expr_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
try|try
block|{
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:clear-grammar-cache()"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
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
block|}
comment|// ===========================================================
annotation|@
name|Test
specifier|public
name|void
name|validateUsingSystemCatalog
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"validateUsingSystemCatalog"
argument_list|)
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
comment|// DTD for hamlet_valid.xml is registered in system catalog.
comment|// result should be "document is valid"
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|TestTools
operator|.
name|VALIDATION_TMP
operator|+
literal|"/hamlet_valid.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"hamlet_valid.xml in systemcatalog"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedCatalog
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"specifiedCatalog"
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('"
operator|+
name|TestTools
operator|.
name|VALIDATION_HOME
operator|+
literal|"/hamlet_valid.xml') ,"
operator|+
literal|" xs:anyURI('/db/validation/dtd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml') ,"
operator|+
literal|" xs:anyURI('/db/validation/dtd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test3"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong catalog"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test4"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/catalog.xml') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"wrong catalog, invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|specifiedGrammar
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"specifiedGrammar"
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/hamlet.dtd') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/hamlet.dtd') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
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
block|}
annotation|@
name|Test
specifier|public
name|void
name|searchedGrammar
parameter_list|()
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"searchedGrammar"
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|ResourceSet
name|result
init|=
literal|null
decl_stmt|;
name|String
name|r
init|=
literal|null
decl_stmt|;
try|try
block|{
name|logger
operator|.
name|info
argument_list|(
literal|"Test1"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/dtd/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test2"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/validation/xsd/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document, not found"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test3"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_valid.xml'), "
operator|+
literal|" xs:anyURI('/db/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"valid document"
argument_list|,
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
name|logger
operator|.
name|info
argument_list|(
literal|"Test4"
argument_list|)
expr_stmt|;
name|result
operator|=
name|service
operator|.
name|query
argument_list|(
literal|"validation:validate( xs:anyURI('/db/validation/hamlet_invalid.xml'), "
operator|+
literal|" xs:anyURI('/db/') )"
argument_list|)
expr_stmt|;
name|r
operator|=
operator|(
name|String
operator|)
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
expr_stmt|;
name|assertEquals
argument_list|(
literal|"invalid document"
argument_list|,
literal|"false"
argument_list|,
name|r
argument_list|)
expr_stmt|;
name|clearGrammarCache
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|logger
operator|.
name|error
argument_list|(
name|e
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
name|logger
operator|.
name|info
argument_list|(
literal|"shutdown"
argument_list|)
expr_stmt|;
name|DatabaseManager
operator|.
name|deregisterDatabase
argument_list|(
name|database
argument_list|)
expr_stmt|;
name|DatabaseInstanceManager
name|dim
init|=
operator|(
name|DatabaseInstanceManager
operator|)
name|root
operator|.
name|getService
argument_list|(
literal|"DatabaseInstanceManager"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|dim
operator|.
name|shutdown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

