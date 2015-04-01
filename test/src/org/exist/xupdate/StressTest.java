begin_unit|revision:1.0.0;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  *  eXist Open Source Native XML Database  *  Copyright (C) 2001-04 The eXist Project  *  http://exist-db.org  *    *  This program is free software; you can redistribute it and/or  *  modify it under the terms of the GNU Lesser General Public License  *  as published by the Free Software Foundation; either version 2  *  of the License, or (at your option) any later version.  *    *  This program is distributed in the hope that it will be useful,  *  but WITHOUT ANY WARRANTY; without even the implied warranty of  *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the  *  GNU Lesser General Public License for more details.  *    *  You should have received a copy of the GNU Lesser General Public License  *  along with this program; if not, write to the Free Software  *  Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.  *    *  $Id$  */
end_comment

begin_package
package|package
name|org
operator|.
name|exist
operator|.
name|xupdate
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Random
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
name|textui
operator|.
name|TestRunner
import|;
end_import

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
name|After
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
name|Test
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
name|CollectionManagementService
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
name|XMLResource
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
name|modules
operator|.
name|XUpdateQueryService
import|;
end_import

begin_comment
comment|/**  * @author wolf  *  */
end_comment

begin_class
specifier|public
class|class
name|StressTest
block|{
specifier|private
specifier|final
specifier|static
name|String
name|XML
init|=
literal|"<root><a/><b/><c/></root>"
decl_stmt|;
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
specifier|private
specifier|final
specifier|static
name|int
name|RUNS
init|=
literal|1000
decl_stmt|;
specifier|private
name|Collection
name|rootCol
decl_stmt|;
specifier|private
name|Collection
name|testCol
decl_stmt|;
specifier|private
specifier|final
name|Random
name|rand
init|=
operator|new
name|Random
argument_list|()
decl_stmt|;
specifier|private
name|String
index|[]
name|tags
decl_stmt|;
annotation|@
name|Test
specifier|public
name|void
name|stressTest
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|insertTags
argument_list|()
expr_stmt|;
name|removeTags
argument_list|()
expr_stmt|;
name|fetchDb
argument_list|()
expr_stmt|;
block|}
specifier|private
name|void
name|insertTags
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|XPathQueryService
name|xquery
init|=
operator|(
name|XPathQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|String
index|[]
name|tagsWritten
init|=
operator|new
name|String
index|[
name|RUNS
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|RUNS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|tag
init|=
name|tags
index|[
name|i
index|]
decl_stmt|;
name|String
name|parent
decl_stmt|;
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|rand
operator|.
name|nextInt
argument_list|(
literal|100
argument_list|)
operator|<
literal|70
condition|)
block|{
name|parent
operator|=
literal|"//"
operator|+
name|tagsWritten
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|i
argument_list|)
operator|/
literal|2
index|]
expr_stmt|;
block|}
else|else
name|parent
operator|=
literal|"/root"
expr_stmt|;
name|String
name|xupdate
init|=
literal|"<xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xupdate:append select=\""
operator|+
name|parent
operator|+
literal|"\">"
operator|+
literal|"<xupdate:element name=\""
operator|+
name|tag
operator|+
literal|"\"/>"
operator|+
literal|"</xupdate:append>"
operator|+
literal|"</xupdate:modifications>"
decl_stmt|;
name|long
name|mods
init|=
name|service
operator|.
name|updateResource
argument_list|(
literal|"test.xml"
argument_list|,
name|xupdate
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|mods
argument_list|,
literal|1
argument_list|)
expr_stmt|;
name|tagsWritten
index|[
name|i
index|]
operator|=
name|tag
expr_stmt|;
name|String
name|query
init|=
literal|"//"
operator|+
name|tagsWritten
index|[
name|rand
operator|.
name|nextInt
argument_list|(
name|i
operator|+
literal|1
argument_list|)
index|]
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xquery
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
argument_list|)
expr_stmt|;
block|}
name|XMLResource
name|res
init|=
operator|(
name|XMLResource
operator|)
name|testCol
operator|.
name|getResource
argument_list|(
literal|"test.xml"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|res
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|removeTags
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XUpdateQueryService
name|service
init|=
operator|(
name|XUpdateQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XUpdateQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|int
name|start
init|=
name|rand
operator|.
name|nextInt
argument_list|(
name|RUNS
operator|/
literal|4
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|start
init|;
name|i
operator|<
name|RUNS
condition|;
name|i
operator|++
control|)
block|{
name|String
name|xupdate
init|=
literal|"<xupdate:modifications version=\"1.0\" xmlns:xupdate=\"http://www.xmldb.org/xupdate\">"
operator|+
literal|"<xupdate:remove select=\"//"
operator|+
name|tags
index|[
name|i
index|]
operator|+
literal|"\"/>"
operator|+
literal|"</xupdate:modifications>"
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unused"
argument_list|)
name|long
name|mods
init|=
name|service
operator|.
name|updateResource
argument_list|(
literal|"test.xml"
argument_list|,
name|xupdate
argument_list|)
decl_stmt|;
name|i
operator|+=
name|rand
operator|.
name|nextInt
argument_list|(
literal|3
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|void
name|fetchDb
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|XPathQueryService
name|xquery
init|=
operator|(
name|XPathQueryService
operator|)
name|testCol
operator|.
name|getService
argument_list|(
literal|"XPathQueryService"
argument_list|,
literal|"1.0"
argument_list|)
decl_stmt|;
name|ResourceSet
name|result
init|=
name|xquery
operator|.
name|query
argument_list|(
literal|"for $n in collection('"
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test')//* return local-name($n)"
argument_list|)
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|result
operator|.
name|getSize
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Resource
name|r
init|=
name|result
operator|.
name|getResource
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|tag
init|=
name|r
operator|.
name|getContent
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
name|ResourceSet
name|result2
init|=
name|xquery
operator|.
name|query
argument_list|(
literal|"//"
operator|+
name|tag
argument_list|)
decl_stmt|;
name|assertEquals
argument_list|(
name|result2
operator|.
name|getSize
argument_list|()
argument_list|,
literal|1
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
name|rootCol
operator|=
name|DBUtils
operator|.
name|setupDB
argument_list|(
name|URI
argument_list|)
expr_stmt|;
name|testCol
operator|=
name|rootCol
operator|.
name|getChildCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
if|if
condition|(
name|testCol
operator|!=
literal|null
condition|)
block|{
name|CollectionManagementService
name|mgr
init|=
name|DBUtils
operator|.
name|getCollectionManagementService
argument_list|(
name|rootCol
argument_list|)
decl_stmt|;
name|mgr
operator|.
name|removeCollection
argument_list|(
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"/test"
argument_list|)
expr_stmt|;
block|}
name|testCol
operator|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|rootCol
argument_list|,
literal|"test"
argument_list|)
expr_stmt|;
name|assertNotNull
argument_list|(
name|testCol
argument_list|)
expr_stmt|;
name|tags
operator|=
operator|new
name|String
index|[
name|RUNS
index|]
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|RUNS
condition|;
name|i
operator|++
control|)
block|{
name|tags
index|[
name|i
index|]
operator|=
literal|"TAG"
operator|+
name|i
expr_stmt|;
block|}
name|DBUtils
operator|.
name|addXMLResource
argument_list|(
name|testCol
argument_list|,
literal|"test.xml"
argument_list|,
name|XML
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
name|XMLDBException
block|{
name|TestUtils
operator|.
name|cleanupDB
argument_list|()
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
end_class

end_unit

