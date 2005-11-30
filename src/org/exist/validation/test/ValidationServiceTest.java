begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *  *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|validation
operator|.
name|test
package|;
end_package

begin_import
import|import
name|junit
operator|.
name|framework
operator|.
name|Assert
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
name|validation
operator|.
name|service
operator|.
name|ValidationService
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
name|XMLDBException
import|;
end_import

begin_comment
comment|/**  *  jUnit test for testing the Validation Service.  *  * @author dizzzz  */
end_comment

begin_class
specifier|public
class|class
name|ValidationServiceTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
literal|"xmldb:exist://"
operator|+
name|DBBroker
operator|.
name|ROOT_COLLECTION
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|DRIVER
init|=
literal|"org.exist.xmldb.DatabaseImpl"
decl_stmt|;
specifier|private
name|Collection
name|rootCollection
init|=
literal|null
decl_stmt|;
specifier|private
name|ValidationService
name|service
init|=
literal|null
decl_stmt|;
specifier|private
name|String
name|eXistHome
init|=
literal|null
decl_stmt|;
specifier|public
name|ValidationServiceTest
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
name|ValidationServiceTest
operator|.
name|class
argument_list|)
decl_stmt|;
return|return
name|suite
return|;
block|}
specifier|public
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|">>> setUp"
argument_list|)
expr_stmt|;
name|eXistHome
operator|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
expr_stmt|;
name|Class
name|cl
init|=
name|Class
operator|.
name|forName
argument_list|(
name|DRIVER
argument_list|)
decl_stmt|;
name|Database
name|database
init|=
operator|(
name|Database
operator|)
name|cl
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
name|rootCollection
operator|=
name|DatabaseManager
operator|.
name|getCollection
argument_list|(
name|URI
argument_list|,
literal|"admin"
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
literal|"Could not connect to database."
argument_list|)
expr_stmt|;
name|service
operator|=
name|getValidationService
argument_list|()
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"<<<\n"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|private
name|ValidationService
name|getValidationService
parameter_list|()
block|{
try|try
block|{
return|return
operator|(
name|ValidationService
operator|)
name|rootCollection
operator|.
name|getService
argument_list|(
literal|"ValidationService"
argument_list|,
literal|"1.0"
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testGetName
parameter_list|()
block|{
try|try
block|{
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|"ValidationService check"
argument_list|,
name|service
operator|.
name|getName
argument_list|()
argument_list|,
literal|"ValidationService"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testGetVersion
parameter_list|()
block|{
try|try
block|{
name|assertEquals
argument_list|(
literal|"ValidationService check"
argument_list|,
name|service
operator|.
name|getVersion
argument_list|()
argument_list|,
literal|"1.0"
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testXsdValidDocument
parameter_list|()
block|{
try|try
block|{
name|assertTrue
argument_list|(
name|service
operator|.
name|validateResource
argument_list|(
literal|"/db/grammar/addressbook_valid.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testXsdInvalidDocument
parameter_list|()
block|{
try|try
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|validateResource
argument_list|(
literal|"/db/grammar/addressbook_invalid.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testNonexistingDocument
parameter_list|()
block|{
try|try
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|validateResource
argument_list|(
name|DBBroker
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/foobar.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testDtdValidDocument
parameter_list|()
block|{
try|try
block|{
name|assertTrue
argument_list|(
name|service
operator|.
name|validateResource
argument_list|(
literal|"/db/grammar/hamlet_valid.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
specifier|public
name|void
name|testDtdInvalidDocument
parameter_list|()
block|{
try|try
block|{
name|assertFalse
argument_list|(
name|service
operator|.
name|validateResource
argument_list|(
literal|"/db/grammar/hamlet_invalid.xml"
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
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
block|}
end_class

end_unit

