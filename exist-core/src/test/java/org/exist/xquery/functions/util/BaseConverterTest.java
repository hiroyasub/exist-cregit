begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * eXist Open Source Native XML Database  * Copyright (C) 2009 The eXist Project  * http://exist-db.org  *  * This program is free software; you can redistribute it and/or  * modify it under the terms of the GNU Lesser General Public License  * as published by the Free Software Foundation; either version 2  * of the License, or (at your option) any later version.  *  * This program is distributed in the hope that it will be useful,  * but WITHOUT ANY WARRANTY; without even the implied warranty of  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  * GNU Lesser General Public License for more details.  *  * You should have received a copy of the GNU Lesser General Public License  * along with this program; if not, write to the Free Software Foundation  * Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.  *  *  $Id$  */
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
name|util
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|test
operator|.
name|ExistXmldbEmbeddedServer
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|After
import|;
end_import

begin_import
import|import
name|org
operator|.
name|junit
operator|.
name|Before
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
name|exist
operator|.
name|xquery
operator|.
name|XPathException
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
name|modules
operator|.
name|XPathQueryService
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
comment|/**  * @author ljo  */
end_comment

begin_class
specifier|public
class|class
name|BaseConverterTest
block|{
annotation|@
name|ClassRule
specifier|public
specifier|static
name|ExistXmldbEmbeddedServer
name|existXmldbEmbeddedServer
init|=
operator|new
name|ExistXmldbEmbeddedServer
argument_list|(
literal|false
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|testBaseConverterOctalToInt
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"util:base-to-integer(0755, 8)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existXmldbEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"493"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBaseConverterIntToHex
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"util:integer-to-base(10, 16)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existXmldbEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"a"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|testBaseConverterIntToBinary
parameter_list|()
throws|throws
name|XMLDBException
block|{
specifier|final
name|String
name|query
init|=
literal|"util:integer-to-base(4, 2)"
decl_stmt|;
specifier|final
name|ResourceSet
name|result
init|=
name|existXmldbEmbeddedServer
operator|.
name|executeQuery
argument_list|(
name|query
argument_list|)
decl_stmt|;
specifier|final
name|String
name|r
init|=
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
decl_stmt|;
name|assertEquals
argument_list|(
literal|"100"
argument_list|,
name|r
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

