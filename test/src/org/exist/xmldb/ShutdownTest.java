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
name|java
operator|.
name|io
operator|.
name|File
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
name|TestCase
import|;
end_import

begin_comment
comment|/**  * Check if database shutdownDB/restart works properly. The test opens  * the database, stores a few files and queries them, then shuts down the  * db.  *    * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ShutdownTest
extends|extends
name|TestCase
block|{
specifier|private
specifier|final
specifier|static
name|String
name|URI
init|=
name|XmldbURI
operator|.
name|LOCAL_DB
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
literal|"<bank-account>7466356</bank-account>"
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
literal|"<bank-account>364553</bank-account>"
operator|+
literal|"</user>"
operator|+
literal|"</config>"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY1
init|=
literal|"//user[@id = 'george']/phone[contains(., '69')]/text()"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY2
init|=
literal|"//user[@id = 'sam']/customer-id[. = '993834']"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY3
init|=
literal|"//user[email = 'sam@email.com']"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|TEST_QUERY4
init|=
literal|"/ROOT-ELEMENT/ELEMENT/ELEMENT-1"
decl_stmt|;
specifier|private
name|String
index|[]
name|wordList
decl_stmt|;
specifier|public
name|ShutdownTest
parameter_list|(
name|String
name|name
parameter_list|)
block|{
name|super
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
specifier|public
name|void
name|testShutdown
parameter_list|()
block|{
try|try
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
literal|50
condition|;
name|i
operator|++
control|)
block|{
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Starting the database ..."
argument_list|)
expr_stmt|;
name|Collection
name|rootCol
init|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|URI
argument_list|)
decl_stmt|;
comment|// after restarting the db, we first try a bunch of queries
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
name|Assert
operator|.
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
name|Assert
operator|.
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
name|Assert
operator|.
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
name|Assert
operator|.
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
name|TEST_QUERY4
argument_list|)
expr_stmt|;
name|Assert
operator|.
name|assertEquals
argument_list|(
literal|5000
argument_list|,
name|result
operator|.
name|getSize
argument_list|()
argument_list|)
expr_stmt|;
comment|// now replace the data files
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
literal|"\" count=\""
operator|+
name|i
operator|+
literal|"\">"
operator|+
name|XML
operator|+
literal|"</data>"
decl_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing resource ..."
argument_list|)
expr_stmt|;
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
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Storing large file ..."
argument_list|)
expr_stmt|;
name|File
name|tempFile
init|=
name|DBUtils
operator|.
name|generateXMLFile
argument_list|(
literal|5000
argument_list|,
literal|7
argument_list|,
name|wordList
argument_list|)
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|testCol
argument_list|,
literal|"R2.xml"
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
name|System
operator|.
name|out
operator|.
name|println
argument_list|(
literal|"Shut down the database ..."
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|URI
argument_list|)
expr_stmt|;
block|}
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
comment|/* (non-Javadoc) 	 * @see junit.framework.TestCase#setUp() 	 */
specifier|protected
name|void
name|setUp
parameter_list|()
block|{
try|try
block|{
name|Collection
name|rootCol
init|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|URI
argument_list|)
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
name|String
name|existHome
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"exist.home"
argument_list|)
decl_stmt|;
name|File
name|existDir
init|=
name|existHome
operator|==
literal|null
condition|?
operator|new
name|File
argument_list|(
literal|"."
argument_list|)
else|:
operator|new
name|File
argument_list|(
name|existHome
argument_list|)
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|rootCol
argument_list|,
literal|"biblio.rdf"
argument_list|,
operator|new
name|File
argument_list|(
name|existDir
argument_list|,
literal|"samples/biblio.rdf"
argument_list|)
argument_list|)
expr_stmt|;
name|wordList
operator|=
name|DBUtils
operator|.
name|wordList
argument_list|(
name|rootCol
argument_list|)
expr_stmt|;
comment|// store the data files
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
name|File
name|tempFile
init|=
name|DBUtils
operator|.
name|generateXMLFile
argument_list|(
literal|5000
argument_list|,
literal|7
argument_list|,
name|wordList
argument_list|)
decl_stmt|;
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|testCol
argument_list|,
literal|"R2.xml"
argument_list|,
name|tempFile
argument_list|)
expr_stmt|;
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|URI
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
comment|/* (non-Javadoc) 	 * @see junit.framework.TestCase#tearDown() 	 */
specifier|protected
name|void
name|tearDown
parameter_list|()
block|{
try|try
block|{
name|Collection
name|rootCol
init|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|URI
argument_list|)
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
name|DBUtils
operator|.
name|shutdownDB
argument_list|(
name|URI
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
specifier|static
name|void
name|main
parameter_list|(
name|String
index|[]
name|args
parameter_list|)
block|{
name|junit
operator|.
name|textui
operator|.
name|TestRunner
operator|.
name|run
argument_list|(
name|ShutdownTest
operator|.
name|class
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

