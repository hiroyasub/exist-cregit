begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 Wolfgang M. Meier  *  wolfgang@exist-db.org  *  http://exist.sourceforge.net  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xmldb
package|;
end_package

begin_import
import|import
name|org
operator|.
name|exist
operator|.
name|TestUtils
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
name|ExistXmldbEmbeddedServer
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
name|concurrent
operator|.
name|DBUtils
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
name|Resource
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
import|import static
name|org
operator|.
name|junit
operator|.
name|Assert
operator|.
name|assertEquals
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
name|assertNotNull
import|;
end_import

begin_comment
comment|/**  * Check if database shutdownDB/restart works properly. The test opens  * the database, stores a few files and queries them, then shuts down the  * db.  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ShutdownTest
block|{
specifier|private
specifier|final
specifier|static
name|int
name|ITERATIONS
init|=
literal|50
decl_stmt|;
specifier|protected
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<config>"
operator|+
literal|"<user id=\"george\">"
operator|+
literal|"<phone>+49 69 888478</phone>"
operator|+
literal|"<email>george@email.com</email>"
operator|+
literal|"<customer-id>64534233</customer-id>"
operator|+
literal|"<reference>7466356</reference>"
operator|+
literal|"</user>"
operator|+
literal|"<user id=\"sam\">"
operator|+
literal|"<phone>+49 69 774345</phone>"
operator|+
literal|"<email>sam@email.com</email>"
operator|+
literal|"<customer-id>993834</customer-id>"
operator|+
literal|"<reference>364553</reference>"
operator|+
literal|"</user>"
operator|+
literal|"</config>"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUERY1
init|=
literal|"//user[@id = 'george']/phone[contains(., '69')]/text()"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUERY2
init|=
literal|"//user[@id = 'sam']/customer-id[. = '993834']"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|TEST_QUERY3
init|=
literal|"//user[email = 'sam@email.com']"
decl_stmt|;
annotation|@
name|ClassRule
specifier|public
specifier|static
specifier|final
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
name|Before
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
specifier|final
name|Collection
name|rootCol
init|=
name|existXmldbEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|Collection
name|testCol
init|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
literal|"C1"
argument_list|)
decl_stmt|;
if|if
condition|(
name|testCol
operator|==
literal|null
condition|)
block|{
name|testCol
operator|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|rootCol
argument_list|,
literal|"C1"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
block|}
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|rootCol
argument_list|,
literal|"biblio.rdf"
argument_list|,
name|TestUtils
operator|.
name|resolveSample
argument_list|(
literal|"biblio.rdf"
argument_list|)
argument_list|)
expr_stmt|;
comment|// store the data files
specifier|final
name|String
name|xml
init|=
literal|"<data now=\""
operator|+
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
literal|"\" count=\"1\">"
operator|+
name|XML
operator|+
literal|"</data>"
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|testCol
argument_list|,
literal|"R1.xml"
argument_list|,
name|xml
argument_list|)
expr_stmt|;
block|}
annotation|@
name|After
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|Exception
block|{
name|Collection
name|rootCol
init|=
name|existXmldbEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|DBUtils
operator|.
name|removeCollection
argument_list|(
name|rootCol
argument_list|,
literal|"C1"
argument_list|)
expr_stmt|;
name|Resource
name|res
init|=
name|rootCol
operator|.
name|getResource
argument_list|(
literal|"biblio.rdf"
argument_list|)
decl_stmt|;
name|rootCol
operator|.
name|removeResource
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Test
specifier|public
name|void
name|shutdown
parameter_list|()
throws|throws
name|Exception
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|ITERATIONS
condition|;
name|i
operator|++
control|)
block|{
name|existXmldbEmbeddedServer
operator|.
name|restart
argument_list|()
expr_stmt|;
specifier|final
name|Collection
name|rootCol
init|=
name|existXmldbEmbeddedServer
operator|.
name|getRoot
argument_list|()
decl_stmt|;
comment|// after restarting the db, we first try a bunch of queries
specifier|final
name|Collection
name|testCol
init|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
literal|"C1"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|DBUtils
operator|.
name|query
argument_list|(
name|testCol
argument_list|,
name|TEST_QUERY1
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|"+49 69 888478"
argument_list|,
name|result
operator|.
name|getResource
argument_list|(
literal|0
argument_list|)
operator|.
name|getContent
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|DBUtils
operator|.
name|query
argument_list|(
name|testCol
argument_list|,
name|TEST_QUERY2
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
name|result
operator|=
name|DBUtils
operator|.
name|query
argument_list|(
name|testCol
argument_list|,
name|TEST_QUERY3
argument_list|)
expr_stmt|;
name|assertEquals
argument_list|(
literal|1
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
end_class

end_unit

