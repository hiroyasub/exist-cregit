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
name|junit
operator|.
name|framework
operator|.
name|Test
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
name|framework
operator|.
name|TestSuite
import|;
end_import

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

begin_comment
comment|/**  *  Insert documents for validation tests.  *  * @author Dannes Wessels (dizzzz@exist-db.org)  */
end_comment

begin_class
specifier|public
class|class
name|DatabaseInsertResources_NoValidation_Test
extends|extends
name|TestCase
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
name|BrokerPool
name|pool
decl_stmt|;
specifier|private
specifier|static
name|String
name|eXistHome
decl_stmt|;
specifier|private
specifier|static
name|Configuration
name|config
decl_stmt|;
specifier|public
name|DatabaseInsertResources_NoValidation_Test
parameter_list|(
name|String
name|testName
parameter_list|)
block|{
name|super
argument_list|(
name|testName
argument_list|)
expr_stmt|;
block|}
specifier|public
specifier|static
name|Test
name|suite
parameter_list|()
block|{
name|TestSuite
name|suite
init|=
operator|new
name|TestSuite
argument_list|(
name|DatabaseInsertResources_NoValidation_Test
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|suite
return|;
block|}
specifier|protected
name|BrokerPool
name|startDB
parameter_list|()
block|{
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
name|PROPERTY_VALIDATION
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
comment|// ---------------------------------------------------
specifier|public
name|void
name|testStart
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
name|eXistHome
operator|=
name|ConfigurationHelper
operator|.
name|getExistHome
argument_list|()
operator|.
name|getAbsolutePath
argument_list|()
expr_stmt|;
name|pool
operator|=
name|startDB
argument_list|()
expr_stmt|;
block|}
comment|/**      * Insert all documents into database, switch of validation.      */
specifier|public
name|void
name|testInsertValidationResources
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
name|config
operator|.
name|setProperty
argument_list|(
name|XMLReaderObjectFactory
operator|.
name|PROPERTY_VALIDATION
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
specifier|public
name|void
name|testShutdown
parameter_list|()
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
name|this
operator|.
name|getName
argument_list|()
argument_list|)
expr_stmt|;
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

