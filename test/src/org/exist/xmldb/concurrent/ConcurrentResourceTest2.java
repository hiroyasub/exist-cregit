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
name|xmldb
operator|.
name|concurrent
package|;
end_package

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
name|action
operator|.
name|MultiResourcesAction
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
name|action
operator|.
name|XQueryAction
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
name|XMLDBException
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
comment|/**  * @author wolf  */
end_comment

begin_class
specifier|public
class|class
name|ConcurrentResourceTest2
extends|extends
name|ConcurrentTestBase
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
specifier|private
specifier|final
specifier|static
name|String
name|QUERY0
init|=
literal|"declare default element namespace 'http://www.loc.gov/mods/v3';"
operator|+
literal|"collection(\""
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"\")//mods[titleInfo/title&= 'germany']"
decl_stmt|;
specifier|private
specifier|final
specifier|static
name|String
name|QUERY1
init|=
literal|"declare default element namespace 'http://www.loc.gov/mods/v3';"
operator|+
literal|"<result>{for $t in distinct-values(\""
operator|+
name|XmldbURI
operator|.
name|ROOT_COLLECTION
operator|+
literal|"\")//mods/subject/topic) order by $t return<topic>{$t}</topic>}</result>"
decl_stmt|;
specifier|public
name|ConcurrentResourceTest2
parameter_list|()
block|{
name|super
argument_list|(
name|URI
argument_list|,
literal|"C1"
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Before
annotation|@
name|Override
specifier|public
name|void
name|setUp
parameter_list|()
throws|throws
name|Exception
block|{
name|super
operator|.
name|setUp
argument_list|()
expr_stmt|;
name|Collection
name|c1
init|=
name|DBUtils
operator|.
name|addCollection
argument_list|(
name|getTestCollection
argument_list|()
argument_list|,
literal|"C1-C2"
argument_list|)
decl_stmt|;
name|assertNotNull
argument_list|(
name|c1
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|MultiResourcesAction
argument_list|(
literal|"samples/mods"
argument_list|,
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|0
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|MultiResourcesAction
argument_list|(
literal|"samples/mods"
argument_list|,
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|0
argument_list|,
literal|300
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|,
literal|"R1.xml"
argument_list|,
name|QUERY0
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|200
argument_list|,
literal|500
argument_list|)
expr_stmt|;
name|addAction
argument_list|(
operator|new
name|XQueryAction
argument_list|(
name|URI
operator|+
literal|"/C1/C1-C2"
argument_list|,
literal|"R1.xml"
argument_list|,
name|QUERY1
argument_list|)
argument_list|,
literal|200
argument_list|,
literal|300
argument_list|,
literal|500
argument_list|)
expr_stmt|;
comment|//addAction(new XQueryAction(URI + "/C1/C1-C2", "R1.xml", QUERY0), 200, 400, 500);
comment|//addAction(new XQueryAction(URI + "/C1/C1-C2", "R1.xml", QUERY1), 200, 500, 500);
block|}
annotation|@
name|After
annotation|@
name|Override
specifier|public
name|void
name|tearDown
parameter_list|()
throws|throws
name|XMLDBException
block|{
name|super
operator|.
name|tearDown
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

