begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2009 The eXist Project  *  http://exist-db.org  *  *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *  *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *  *  You should have received a copy of the GNU Lesser General Public  *  License along with this library; if not, write to the Free Software  *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA  *  * $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xquery
operator|.
name|functions
operator|.
name|validate
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
name|java
operator|.
name|io
operator|.
name|FilenameFilter
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
import|import static
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|XMLAssert
operator|.
name|assertXpathEvaluatesTo
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
name|EmbeddedExistTester
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
name|ResourceSet
import|;
end_import

begin_comment
comment|/**  * Tests for the validation:jaxv() function with XSDs.  *  * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|JaxvTest
extends|extends
name|EmbeddedExistTester
block|{
annotation|@
name|BeforeClass
specifier|public
specifier|static
name|void
name|prepareResources
parameter_list|()
throws|throws
name|Exception
block|{
name|String
name|noValidation
init|=
literal|"<?xml version='1.0'?>"
operator|+
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0"
operator|+
literal|"\">"
operator|+
literal|"<validation mode=\"no\"/>"
operator|+
literal|"</collection>"
decl_stmt|;
name|Collection
name|conf
init|=
name|createCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"system/config/db/personal"
argument_list|)
decl_stmt|;
name|storeResource
argument_list|(
name|conf
argument_list|,
literal|"collection.xconf"
argument_list|,
name|noValidation
operator|.
name|getBytes
argument_list|()
argument_list|)
expr_stmt|;
name|Collection
name|collection
init|=
name|createCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"personal"
argument_list|)
decl_stmt|;
name|File
name|directory
init|=
operator|new
name|File
argument_list|(
literal|"samples/validation/personal"
argument_list|)
decl_stmt|;
name|FilenameFilter
name|filter
init|=
operator|new
name|FilenameFilter
argument_list|()
block|{
specifier|public
name|boolean
name|accept
parameter_list|(
name|File
name|dir
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
operator|(
name|name
operator|.
name|startsWith
argument_list|(
literal|"personal"
argument_list|)
operator|)
return|;
block|}
block|}
decl_stmt|;
for|for
control|(
name|File
name|file
range|:
name|directory
operator|.
name|listFiles
argument_list|(
name|filter
argument_list|)
control|)
block|{
name|LOG
operator|.
name|info
argument_list|(
literal|"Storing "
operator|+
name|file
operator|.
name|getAbsolutePath
argument_list|()
argument_list|)
expr_stmt|;
name|byte
index|[]
name|data
init|=
name|readFile
argument_list|(
name|directory
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|storeResource
argument_list|(
name|collection
argument_list|,
name|file
operator|.
name|getName
argument_list|()
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Test
specifier|public
name|void
name|xsd_stored_valid
parameter_list|()
block|{
name|String
name|query
init|=
literal|"validation:jaxv( "
operator|+
literal|"doc('/db/personal/personal-valid.xml'), "
operator|+
literal|"doc('/db/personal/personal.xsd') )"
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
name|query
argument_list|,
literal|"true"
argument_list|,
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
operator|.
name|toString
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
name|LOG
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
name|Test
specifier|public
name|void
name|xsd_stored_report_valid
parameter_list|()
block|{
name|String
name|query
init|=
literal|"validation:jaxv-report( "
operator|+
literal|"doc('/db/personal/personal-valid.xml'), "
operator|+
literal|"doc('/db/personal/personal.xsd') )"
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"valid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
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
name|Test
specifier|public
name|void
name|xsd_stored_invalid
parameter_list|()
block|{
name|String
name|query
init|=
literal|"validation:jaxv-report( "
operator|+
literal|"doc('/db/personal/personal-invalid.xml'), "
operator|+
literal|"doc('/db/personal/personal.xsd') )"
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"invalid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
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
name|Test
specifier|public
name|void
name|xsd_anyuri_valid
parameter_list|()
block|{
name|String
name|query
init|=
literal|"validation:jaxv-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/personal/personal-valid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/personal/personal.xsd') )"
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"valid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
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
name|Test
specifier|public
name|void
name|xsd_anyuri_invalid
parameter_list|()
block|{
name|String
name|query
init|=
literal|"validation:jaxv-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/personal/personal-invalid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/personal/personal.xsd') )"
decl_stmt|;
try|try
block|{
name|ResourceSet
name|results
init|=
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|results
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|String
name|r
init|=
operator|(
name|String
operator|)
name|results
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
decl_stmt|;
name|assertXpathEvaluatesTo
argument_list|(
literal|"invalid"
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|ex
parameter_list|)
block|{
name|LOG
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
block|}
end_class

end_unit

