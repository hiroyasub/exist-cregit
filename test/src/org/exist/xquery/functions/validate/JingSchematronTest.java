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
name|org
operator|.
name|custommonkey
operator|.
name|xmlunit
operator|.
name|exceptions
operator|.
name|XpathException
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
name|function
operator|.
name|Predicate
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
comment|/**  * Tests for the validation:jing() function with SCHs.  *   * @author dizzzz@exist-db.org  */
end_comment

begin_class
specifier|public
class|class
name|JingSchematronTest
extends|extends
name|EmbeddedExistTester
block|{
specifier|private
specifier|static
specifier|final
name|String
name|noValidation
init|=
literal|"<?xml version='1.0'?>"
operator|+
literal|"<collection xmlns=\"http://exist-db.org/collection-config/1.0\">"
operator|+
literal|"<validation mode=\"no\"/>"
operator|+
literal|"</collection>"
decl_stmt|;
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
comment|// Switch off validation
specifier|final
name|Collection
name|conf
init|=
name|createCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"system/config/db/tournament"
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
comment|// Create filter
specifier|final
name|Predicate
argument_list|<
name|Path
argument_list|>
name|filter
init|=
name|path
lambda|->
block|{
specifier|final
name|String
name|fileName
init|=
name|FileUtils
operator|.
name|fileName
argument_list|(
name|path
argument_list|)
decl_stmt|;
return|return
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"Tournament"
argument_list|)
operator|||
name|fileName
operator|.
name|startsWith
argument_list|(
literal|"tournament"
argument_list|)
return|;
block|}
decl_stmt|;
comment|// Store schematron 1.5 test files
specifier|final
name|Collection
name|col15
init|=
name|createCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"tournament/1.5"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|sch15
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"samples/validation/tournament/1.5"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|file
range|:
name|FileUtils
operator|.
name|list
argument_list|(
name|sch15
argument_list|,
name|filter
argument_list|)
control|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|readFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|storeResource
argument_list|(
name|col15
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
argument_list|,
name|data
argument_list|)
expr_stmt|;
block|}
comment|// Store schematron iso testfiles
specifier|final
name|Collection
name|colISO
init|=
name|createCollection
argument_list|(
name|rootCollection
argument_list|,
literal|"tournament/iso"
argument_list|)
decl_stmt|;
specifier|final
name|Path
name|schISO
init|=
name|Paths
operator|.
name|get
argument_list|(
literal|"samples/validation/tournament/iso"
argument_list|)
decl_stmt|;
for|for
control|(
specifier|final
name|Path
name|file
range|:
name|FileUtils
operator|.
name|list
argument_list|(
name|schISO
argument_list|,
name|filter
argument_list|)
control|)
block|{
specifier|final
name|byte
index|[]
name|data
init|=
name|readFile
argument_list|(
name|file
argument_list|)
decl_stmt|;
name|storeResource
argument_list|(
name|colISO
argument_list|,
name|FileUtils
operator|.
name|fileName
argument_list|(
name|file
argument_list|)
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
name|sch_15_stored_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"doc('/db/tournament/1.5/Tournament-valid.xml'), "
operator|+
literal|"doc('/db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"valid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sch_15_stored_valid_boolean
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|String
name|query
init|=
literal|"validation:jing( "
operator|+
literal|"doc('/db/tournament/1.5/Tournament-valid.xml'), "
operator|+
literal|"doc('/db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
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
name|assertEquals
argument_list|(
literal|"true"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sch_15_stored_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"doc('/db/tournament/1.5/Tournament-invalid.xml'), "
operator|+
literal|"doc('/db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sch_15_anyuri_valid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/Tournament-valid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"valid"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|sch_15_anyuri_invalid
parameter_list|()
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|XpathException
throws|,
name|IOException
block|{
name|String
name|query
init|=
literal|"validation:jing-report( "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/Tournament-invalid.xml'), "
operator|+
literal|"xs:anyURI('xmldb:exist:///db/tournament/1.5/tournament-schema.sch') )"
decl_stmt|;
name|executeAndEvaluate
argument_list|(
name|query
argument_list|,
literal|"invalid"
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|executeAndEvaluate
parameter_list|(
name|String
name|query
parameter_list|,
name|String
name|expectedValue
parameter_list|)
throws|throws
name|XMLDBException
throws|,
name|SAXException
throws|,
name|IOException
throws|,
name|XpathException
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
name|expectedValue
argument_list|,
literal|"//status/text()"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

